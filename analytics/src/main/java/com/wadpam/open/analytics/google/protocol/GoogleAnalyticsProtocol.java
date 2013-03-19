package com.wadpam.open.analytics.google.protocol;

import com.wadpam.open.analytics.google.trackinginfo.TrackingInfo;
import com.wadpam.open.analytics.google.config.Property;
import com.wadpam.open.analytics.google.config.Visitor;
import com.wadpam.open.analytics.google.config.Application;
import com.wadpam.open.analytics.google.config.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;

/**
 * Builds Google Analytics protocol parameters based on property configuration and tracking info.
 * The parameters can either be used as query parameters or request body.
 *
 * @author mattiaslevin
 */
public abstract class GoogleAnalyticsProtocol {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsProtocol.class);

    /**
     * The version of the implemented protocol.
     * @return the version
     */
    public abstract String getFormatVersion();

    /**
     * The base Google Analytics url to use.
     */
    public abstract String baseUrl();

    /**
     * Build the url request from the trackingInfo.
     * @param property the property configuration
     * @param visitor the visitor
     * @param device the device
     * @param trackingInfo The tracking trackingInfo
     * @return Encoded params that can either be used as query parameters or request body.
     */
    public abstract String buildParams(Property property, Visitor visitor,
                                       Device device, Application app, TrackingInfo trackingInfo);


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
