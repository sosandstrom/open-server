package com.wadpam.open.analytics;

/**
 * Implement the universal analytics format.
 * @author mattiaslevin
 */
public class URLBuilderVUniversalAnalytics extends AbstractURLBuilder {

    public URLBuilderVUniversalAnalytics(ConfigurationData configurationData, VisitorData visitorData, DeviceData deviceData) {
        super(configurationData, visitorData, deviceData);
    }

    @Override
    String getFormatVersion() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String buildURL(PageData data) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
