package com.wadpam.open.analytics.google;

/**
 * Implement the universal analytics format.
 * @author mattiaslevin
 */
public class URLBuilderVUniversalAnalytics extends AbstractURLBuilder {

    public URLBuilderVUniversalAnalytics(TrackerConfiguration configurationData, Visitor visitorData, Device deviceData) {
        super(configurationData, visitorData, deviceData);
    }

    @Override
    String getFormatVersion() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String buildURL(Page data) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
