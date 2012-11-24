package com.wadpam.open.analytics.google;

/**
 * Builds a GA url based on request data.
 * @author mattiaslevin
 */
public abstract class AbstractURLBuilder {

    protected TrackerConfiguration configuration;
    protected Visitor visitor;
    protected Device device;

    public AbstractURLBuilder(TrackerConfiguration configurationData, Visitor visitorData, Device deviceData) {
        this.configuration = configurationData;
        this.visitor = visitorData;
        this.device = deviceData;
    }

    /**
     * Gets the version of the create GA url created by this builder.
     * @return the version
     */
    abstract String getFormatVersion();

    /**
     * Build the url request from the data.
     * @param data The request data
     * @return A a url that can be used for tracking
     */
    abstract String buildURL(Page data);

}
