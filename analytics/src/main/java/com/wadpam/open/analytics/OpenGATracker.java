package com.wadpam.open.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Server side Google Analytics tracker.
 * @author mattiaslevin
 */
public class OpenGATracker {

    static final Logger LOG = LoggerFactory.getLogger(OpenGATracker.class);

    private RestTemplate restTemplate;

    /** Configuration related data */
    private ConfigurationData configurationData;

    /** Describes the visitor */
    private VisitorData visitorData;

    /** Describes the device */
    private DeviceData deviceData;

    //** The url builder to use */
    private AbstractURLBuilder urlBuilder;

    /** If debug is enabled events will only be printed to the console and not sent to GA */
    private boolean debug = false;

    /**
     * Create a tracker.
     * Each tracker will have a trackingID taken from GA, a visitor object describing the visitor
     * and a device object describing the device.
     * Depending on the application the tracker my be reused for the same user with device for
     * subsequent requests. The visitor and device object can be persisted for future tracking.
     * @param trackingID unique tracking ID
     * @param visitorData visitor data
     * @param deviceData device data
     */
    public OpenGATracker(String trackingID, VisitorData visitorData, DeviceData deviceData) {

        // Check input params
        if (null == trackingID || trackingID.isEmpty() || null == visitorData || null == deviceData) {
            LOG.info("Tracking id, visitor and device must be provided");
            throw new IllegalArgumentException();
        }

        // Create configuration data
        this.configurationData = new ConfigurationData(trackingID);
        this.visitorData = visitorData;
        this.deviceData = deviceData;

        this.urlBuilder = new URLBuilderV5_3_7(this.configurationData, visitorData, deviceData);

        // Configure the rest template
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10 * 1000); // 10s timeout
        factory.setReadTimeout(10 * 1000); // 10s read timeout
        this.restTemplate = new RestTemplate(factory);

        // Set the user agent
        ClientHttpRequestInterceptor userAgentHeader =
                new UserAgentHeaderHttpRequestInterceptor(this.deviceData.getUserAgent());
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(userAgentHeader);

        restTemplate.setInterceptors(interceptors);
    }


    /** Track a page view without setting referrer */
    public void trackPageView(String pageURL, String pageTitle, String hostName) {
        // Track without referrer
        this.trackPageView(pageURL, pageTitle, hostName, null, null, null);
    }

    /**
     * Track a page view referrer and custom variables
     * @param pageURL the page url, e.g. /page/. Must be provided
     * @param pageTitle the title of the page
     * @param hostName the host name
     * @param referrerPage optional. The referrer page
     * @param referrerSite optional. The referrer site
     * @param customVariables optional. List of custom variables
     */
    public void trackPageView(String pageURL, String pageTitle, String hostName,
                              String referrerPage, String referrerSite,
                              List<CustomVariable> customVariables) {
        LOG.debug("Track page view for url:{} and tile:{}", pageURL, pageTitle);

        // Populate the request data
        PageData data = new PageData();
        data.setPageURL(pageURL);
        data.setPageTitle(pageTitle);
        data.setHostName(hostName);
        if (null != referrerSite && null != referrerPage) {
            data.setReferrer(referrerSite, referrerPage);
        }
        data.setCustomVariables(customVariables);

        this.sendRequest(data);
    }


    /** Track without label and value */
    public void trackEvent(String category, String action) {
        this.trackEvent(category, action, null, null, null);
    }

    /** Track without value */
    public void trackEvent(String category, String action, String label) {
        this.trackEvent(category, action, label, null, null);
    }

    /**
     * Track an event.
     * @param category the category
     * @param action the action
     * @param label the label
     * @param value optional. The value
     * @param customVariables optional. custom variables
     */
    public void trackEvent(String category, String action, String label, Integer value,
                           List<CustomVariable> customVariables) {
        LOG.debug("Track event for category:{} and action:{}", category, action);

        // Populate the request data
        PageData data = new PageData();
        data.setEventCategory(category);
        data.setEventAction(action);
        data.setEventLabel(label);
        data.setEventValue(value);
        data.setCustomVariables(customVariables);

        this.sendRequest(data);
    }


    // Generate the request URL and make the request
    private void sendRequest(PageData data) {
        LOG.debug("Send request to GA:{}", data);

        // Build request URL
        final String url = this.urlBuilder.buildURL(data);

        // Check if in debug mode
        if (this.debug) {
            // Only print to console
            LOG.info("{}", url);
            return;
        }

        // Make the request
        LOG.info("Make request with url:{}", url);

        Boolean response = restTemplate.execute(url, HttpMethod.GET,
                new RequestCallback() {
                    public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
                        LOG.debug("Url:{}", clientHttpRequest.getURI().toURL().toExternalForm());
                    }
                },
                new ResponseExtractor<Boolean>() {
                    public Boolean extractData(ClientHttpResponse clientHttpResponse) throws IOException {


                        if (clientHttpResponse.getStatusCode() == HttpStatus.OK) {
                            ByteArrayHttpMessageConverter converter = new ByteArrayHttpMessageConverter();
                            byte[] bytes = converter.readInternal(byte[].class, clientHttpResponse);
                            LOG.debug("Received number of bytes:{}", bytes.length);
                            return true;
                        } else {
                            LOG.warn("Logging request to Google Analytics failed");
                            return false;
                        }

                    }
                },
                new HashMap<String, Object>()
        );
    }


    // Set user agent
    private class UserAgentHeaderHttpRequestInterceptor implements ClientHttpRequestInterceptor {
        private final String headerValue;

        public UserAgentHeaderHttpRequestInterceptor(String headerValue) {
            this.headerValue = headerValue;
        }

        public ClientHttpResponse intercept(HttpRequest request,
                                            byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {

            HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
            requestWrapper.getHeaders().set("User-Agent", headerValue);

            return execution.execute(requestWrapper, body);
        }
    }


    // Setters and getters
    public ConfigurationData getConfigurationData() {
        return configurationData;
    }

    public void setConfigurationData(ConfigurationData configurationData) {
        this.configurationData = configurationData;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public DeviceData getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(DeviceData deviceData) {
        this.deviceData = deviceData;
    }

    public AbstractURLBuilder getUrlBuilder() {
        return urlBuilder;
    }

    public void setUrlBuilder(AbstractURLBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    public VisitorData getVisitorData() {
        return visitorData;
    }

    public void setVisitorData(VisitorData visitorData) {
        this.visitorData = visitorData;
    }
}
