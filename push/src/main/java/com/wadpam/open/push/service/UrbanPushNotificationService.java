package com.wadpam.open.push.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.wadpam.open.json.SkipNullObjectMapper;
import com.wadpam.open.push.urban.APS;
import com.wadpam.open.push.urban.JUrbanRequest;
import com.wadpam.open.push.urban.JUrbanResponse;

/**
 *
 * @author os
 */
public class UrbanPushNotificationService implements PushNotificationService {

    static final Logger LOG = LoggerFactory.getLogger(UrbanPushNotificationService.class);
    private static final String BASE_URL = "https://go.urbanairship.com";
    protected static final ObjectMapper MAPPER = new SkipNullObjectMapper();
    
    private final String clientKey;
    private final String clientSecret;
    
    private final SimpleClientHttpRequestFactory REQUEST_FACTORY = new SimpleClientHttpRequestFactory() {
        @Override
        protected void prepareConnection(HttpURLConnection connection,
                String httpMethod) throws IOException {
            super.prepareConnection(connection, httpMethod);

            // Basic Authentication for Police API
            final String basic = buildAuthorization(clientKey, clientSecret);
            LOG.debug("Authorization: {}", basic);
            connection.setRequestProperty("Authorization", basic);
        }
    };
    private final RestTemplate TEMPLATE = new RestTemplate(REQUEST_FACTORY);

    public UrbanPushNotificationService(String clientKey, String clientSecret) {
        this.clientKey = clientKey;
        this.clientSecret = clientSecret;
        
        MappingJacksonHttpMessageConverter jsonMessageConverter = new MappingJacksonHttpMessageConverter();
        jsonMessageConverter.setObjectMapper(MAPPER);
        final List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(jsonMessageConverter);
        
        TEMPLATE.setMessageConverters(messageConverters);
        MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }
    
    protected JUrbanRequest createRequest(List<String> identifiers,List<String> tags,String message) {
        final JUrbanRequest request = new JUrbanRequest();
        
        
        if (null != identifiers && !identifiers.isEmpty()) {
            request.setDevice_tokens(identifiers);
        }
        
        if (null != tags && !tags.isEmpty()) {
            request.setTags(tags);
        }
        
        APS aps = new APS();
        aps.setAlert(message);
        request.setAps(aps);
        
        return request;
    }

    /**
     *
     * @param identifiers the deviceTokens
     * @throws IOException exception
     */
    @Override
    public void push(String message, String... identifiers) throws IOException {
        if (0 == identifiers.length) {
            return;
        } 
        
        final String path = String.format("%s/api/push/", BASE_URL);
        final JUrbanRequest request = createRequest(Arrays.asList(identifiers),null,message);
        final String json = MAPPER.writeValueAsString(request);
        
        LOG.debug("push {} on {}", json, path);
        JUrbanResponse response = TEMPLATE.postForObject(path, request, JUrbanResponse.class);
        LOG.debug("pushed {}, got {}", json, response);
    }

    @Override
    public void register(String identifier,String... tags) throws IOException {
        final String path = String.format("%s/api/device_tokens/{identifier}/", BASE_URL);
        
        LOG.debug("register for {} where identifier='{}'", path, identifier);
        
        //call remove first
        unregister(identifier);
        
        if (tags != null && tags.length >0) {
            final JUrbanRequest request = new JUrbanRequest();
            request.setTags(Arrays.asList(tags));
            final String json = MAPPER.writeValueAsString(request);
            TEMPLATE.postForObject(path, request,String.class);
        } else {
                TEMPLATE.put(path, null, identifier);
        }
    }

    @Override
    public void unregister(String identifier) throws IOException {
        final String path = String.format("%s/api/device_tokens/{identifier}/", BASE_URL);
        LOG.debug("unregister for {} where identifier='{}'", path, identifier);
        TEMPLATE.delete(path, identifier);
    }

    /**
     * Builds the basic authentication header value
     *
     * @param username username
     * @param password password
     * @return Authorization header value
     */
    private String buildAuthorization(String username, String password) {
        final String plain = String.format("%s:%s", username, password);
        return String.format("Basic %s", Base64.encodeBase64String(plain.getBytes()));
    }

    
    @Override
    public void pushTags(String message,String... tags) throws IOException {
        if (0 == tags.length) {
            return;
        } 
        
        final String path = String.format("%s/api/push/", BASE_URL);
        final JUrbanRequest request = createRequest(null,Arrays.asList(tags),message);
        final String json = MAPPER.writeValueAsString(request);
        
        LOG.debug("push {} on {}", json, path);
        JUrbanResponse response = TEMPLATE.postForObject(path, request, JUrbanResponse.class);
        LOG.debug("pushed {}, got {}", json, response);
        
    }

}
