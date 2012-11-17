package com.wadpam.open.analytics;

/**
 * Builds a GA url based on request data.
 * @author mattiaslevin
 */
public abstract class AbstractURLBuilder {

    protected ConfigurationData configurationData;
    protected VisitorData visitorData;
    protected DeviceData deviceData;

    public AbstractURLBuilder(ConfigurationData configurationData, VisitorData visitorData, DeviceData deviceData) {
        this.configurationData = configurationData;
        this.visitorData = visitorData;
        this.deviceData = deviceData;
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
    abstract String buildURL(PageData data);

}
