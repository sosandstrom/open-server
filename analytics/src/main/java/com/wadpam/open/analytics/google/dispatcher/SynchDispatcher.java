package com.wadpam.open.analytics.google.dispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implement a synchronous Google Analytics dispatcher.
 * @author mattiaslevin
 */
public class SynchDispatcher implements TrackingInfoDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(SynchDispatcher.class);

    /**
     * Set the http method to use.
     * GET and POST is supported. GET is default.
     */
    private HttpMethod httpMethod = HttpMethod.GET;

    private RestTemplate restTemplate;

    // Constructor
    public SynchDispatcher() {

        // Configure the rest template
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5 * 1000); // 10s timeout
        factory.setReadTimeout(5 * 1000); // 10s read timeout
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Crete new dispatcher and decide the http method to use.
     */
    public SynchDispatcher(HttpMethod httpMethod) {
        this();
        this.httpMethod = httpMethod;
    }

    // Dispatch an event
    @Override
    public boolean dispatch(String baseUrl, String params, final String userAgent, final String remoteAddress) throws URISyntaxException {

        // Http headers
        HttpHeaders httpHeaders = new HttpHeaders();
        if (null != userAgent)
            httpHeaders.set("User-Agent",userAgent);

        if (null != remoteAddress) {
            httpHeaders.set("X-Forwarded-For", remoteAddress);
            httpHeaders.set("X-Real-IP", remoteAddress);
        }

        // URI and Http entity
        URI analyticsUri = null;
        HttpEntity<String> httpEntity = null;
        if (httpMethod == HttpMethod.GET) {
            // Add parameters as query parameters since we are doing a GET request
            analyticsUri = new URI(String.format("%s?%s", baseUrl, params));
            httpEntity = new HttpEntity<String>(httpHeaders);
        } else if (httpMethod == HttpMethod.POST) {
            // Parameters will be added to the request body
            analyticsUri = new URI(baseUrl);
            httpEntity = new HttpEntity<String>(params, httpHeaders);
        } else {
            throw new IllegalArgumentException(String.format("Operation %s is not supported", httpMethod));
        }

        // Make the request using rest template
        ResponseEntity<byte[]> response = restTemplate.exchange(analyticsUri, httpMethod, httpEntity, byte[].class);
        if (response.getStatusCode() == HttpStatus.OK) {
            //ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
            //byte[] bytes = converter.readInternal(byte[].class, response);
            //LOG.debug("Received number of bytes:{}", bytes.length);
            return true;
        } else {
            LOG.warn("Request to Google Analytics failed");
            return false;
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }
}
