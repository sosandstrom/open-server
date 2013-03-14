package com.wadpam.open.analytics.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;

/**
 * Builds a Google Analytics url based on request data.
 * @author mattiaslevin
 */
public abstract class URLBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(URLBuilder.class);


    /**
     * Gets the version of the create GA url created by this builder.
     * @return the version
     */
    public abstract String getFormatVersion();

    /**
     * Build the url request from the data.
     * @param trackerConfig the tracker configuration
     * @param visitor the visitor
     * @param device the device
     * @param data The request data
     * @return A a url that can be used for tracking
     */
    public abstract String buildURL(TrackerConfiguration trackerConfig, Visitor visitor,
                                    Device device, Application app, Page data);

    // URL encode a string
    protected static String urlEncode(String string) {
        if(null == string){
            return null;
        }
        try {
            // Google Analytics expect %20 encoding to space
            //return URLEncoder.encode(string, "UTF-8");        // Will encode space to +
            return UriUtils.encodeQueryParam(string, "UTF-8");  // Will encode space to %20
        } catch (UnsupportedEncodingException e) {
            LOG.error("URL encodes does not support character format");
            throw new IllegalArgumentException();
        }
    }
}
