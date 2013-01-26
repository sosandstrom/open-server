package com.wadpam.open.analytics.google;

/**
 * Builds a Google Analytics url based on request data.
 * @author mattiaslevin
 */
public interface URLBuilder {

    /**
     * Gets the version of the create GA url created by this builder.
     * @return the version
     */
    public String getFormatVersion();

    /**
     * Build the url request from the data.
     * @param trackerConfig the tracker configuration
     * @param visitor the visitor
     * @param device the device
     * @param data The request data
     * @return A a url that can be used for tracking
     */
    public String buildURL(TrackerConfiguration trackerConfig, Visitor visitor, Device device, Page data);
}
