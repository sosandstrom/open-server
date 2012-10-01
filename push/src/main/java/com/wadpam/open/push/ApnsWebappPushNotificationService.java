package com.wadpam.open.push;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author os
 */
public class ApnsWebappPushNotificationService implements PushNotificationService {
    static final Logger LOG = LoggerFactory.getLogger(ApnsWebappPushNotificationService.class);
    private final String BASE_URL;
    private final RestTemplate template;
    
    public ApnsWebappPushNotificationService(String baseUrl) {
        this.BASE_URL = baseUrl;
        final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(15000);
        this.template = new RestTemplate(factory);
    }

    @Override
    public void push(String... identifiers) throws IOException {
        StringBuffer url = new StringBuffer(BASE_URL);
        url.append("ApnsServlet?alert=finally");
        for (int i = 0; i < identifiers.length; i++) {
            url.append('&');
            url.append("pushToken=");
            url.append(URLEncoder.encode(identifiers[i], "UTF-8"));
        }
        
        LOG.debug("pushing on {}", url.toString());
        Map response = template.getForObject(url.toString(), Map.class);
        LOG.info("push response is {}", response);
    }

    @Override
    public void register(String identifier) throws IOException {
        LOG.info("register {} not required", identifier);
    }

    @Override
    public void unregister(String identifier) throws IOException {
        LOG.info("unregister {} not required", identifier);
    }
    
}
