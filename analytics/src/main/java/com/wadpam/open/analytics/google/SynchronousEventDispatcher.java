package com.wadpam.open.analytics.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SynchronousEventDispatcher extends EventDispatcher {
    static final Logger LOG = LoggerFactory.getLogger(SynchronousEventDispatcher.class);

    private RestTemplate restTemplate;

    // Constructor
    public SynchronousEventDispatcher(String host, String remoteAddress, String userAgent) {
        super(host, remoteAddress, userAgent);

        // Configure the rest template
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10 * 1000); // 10s timeout
        factory.setReadTimeout(10 * 1000); // 10s read timeout
        this.restTemplate = new RestTemplate(factory);

        // Set header values
        ClientHttpRequestInterceptor headerInterceptor = new HeaderRequestInterceptor();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(headerInterceptor);
        restTemplate.setInterceptors(interceptors);
    }


    // Dispatch an event
    public boolean dispatch(URI uri) {

        boolean response = restTemplate.execute(uri, HttpMethod.GET,
                new RequestCallback() {
                    public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
                        LOG.debug("Url:{}", clientHttpRequest.getURI().toURL().toString());
                    }
                },
                new ResponseExtractor<Boolean>() {
                    public Boolean extractData(ClientHttpResponse clientHttpResponse) throws IOException {
                        if (clientHttpResponse.getStatusCode() == HttpStatus.OK) {
                            ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
                            byte[] bytes = converter.readInternal(byte[].class, clientHttpResponse);
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

    // Set headers
    private class HeaderRequestInterceptor implements ClientHttpRequestInterceptor {

        public ClientHttpResponse intercept(HttpRequest request,
                                            byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {

            HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
//            if (null != getHost())
//                requestWrapper.getHeaders().set("Host", getHost());
            if (null != getUserAgent())
                requestWrapper.getHeaders().set("User-Agent", getUserAgent());
            if (null != getRemoteAddress()) {
                requestWrapper.getHeaders().set("X-Forwarded-For", getRemoteAddress());                requestWrapper.getHeaders().set("X-Real-IP", getRemoteAddress());
            }

            return execution.execute(requestWrapper, body);
        }
    }

}
