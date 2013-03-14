package com.wadpam.open.analytics.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.*;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Implement a synchronous Google Analytics dispatcher.
 * @author mattiaslevin
 */
public class SynchronousEventDispatcher implements EventDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(SynchronousEventDispatcher.class);

    private RestTemplate restTemplate;

    // Constructor
    public SynchronousEventDispatcher() {

        // Configure the rest template
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5 * 1000); // 10s timeout
        factory.setReadTimeout(5 * 1000); // 10s read timeout
        this.restTemplate = new RestTemplate(factory);
    }


    // Dispatch an event
    @Override
    public boolean dispatch(URI analyticsUri, final String userAgent, final String remoteAddress) {

        boolean response = restTemplate.execute(analyticsUri, HttpMethod.GET,
                new RequestCallback() {
                    public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
                        LOG.debug("Url:{}", clientHttpRequest.getURI().toURL().toString());

                        // Set headers to imitate the sending device as much as possible
                        HttpHeaders headers = clientHttpRequest.getHeaders();

                        //if (null != device.getHost())  // Not allowed to set this value
                        //    headers.set("Host", device.getHost());

                        if (null != userAgent)
                             headers.set("User-Agent",userAgent);

                        if (null != remoteAddress) {
                            headers.set("X-Forwarded-For", remoteAddress);
                            headers.set("X-Real-IP", remoteAddress);
                        }
                    }
                },
                new ResponseExtractor<Boolean>() {
                    public Boolean extractData(ClientHttpResponse clientHttpResponse) throws IOException {
                        if (clientHttpResponse.getStatusCode() == HttpStatus.OK) {
                            //ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
                            //byte[] bytes = converter.readInternal(byte[].class, clientHttpResponse);
                            //LOG.debug("Received number of bytes:{}", bytes.length);
                            return true;
                        } else {
                            LOG.warn("Request to Google Analytics failed");
                            return false;
                        }
                    }
                });

        return response;
    }

}
