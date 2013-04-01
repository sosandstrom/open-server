package com.wadpam.open.analytics.piwik;

import com.wadpam.open.analytics.Tracker;
import com.wadpam.open.analytics.google.trackinginfo.CustomVariable;

import java.util.List;

/**
 * Implement a Piwik analytics tracker.
 *
 * @author mattiaslevin
 */
public class PiwikAnalyticsTracker implements Tracker {

    @Override
    public void trackPageView(String hostName, String path, String title, String referrerPage, String referrerSite, List<CustomVariable> customVariables) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void trackEvent(String category, String action, String label, Integer value, List<CustomVariable> customVariables) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
