package com.wadpam.open.analytics;

import com.wadpam.open.analytics.google.trackinginfo.CustomVariable;

import java.util.List;

/**
 * Interface for analytics trackers, e.g Google Analytics and Piwik.
 * @author mattiaslevin
 */
public interface Tracker {


    /**
     * Track a page view referrer and custom variables.
     * @param hostName the host name
     * @param path the page url, e.g. /page/. Must be provided
     * @param title the title of the page
     * @param referrerSite optional. The referrer site
     * @param referrerPage optional. The referrer page
     * @param customVariables optional. List of custom variables
     */
    public void trackPageView(String hostName,
                              String path,
                              String title,
                              String referrerSite,
                              String referrerPage,
                              List<CustomVariable> customVariables);

    /**
     * Track an event.
     * @param category the category
     * @param action the action
     * @param label the label
     * @param value optional. Event value
     * @param customVariables optional. Custom variables
     */
    public void trackEvent(String category,
                           String action,
                           String label,
                           Integer value,
                           List<CustomVariable> customVariables);

}
