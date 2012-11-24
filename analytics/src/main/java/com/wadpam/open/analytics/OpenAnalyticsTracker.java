package com.wadpam.open.analytics;

import com.wadpam.open.analytics.google.CustomVariable;

import java.util.List;

/**
 * Interface the different tracker implementations should implement.
 * @author mattiaslevin
 */
public abstract class OpenAnalyticsTracker {


    /** Track a page view without setting referrer */
    public void trackPageView(String pageURL, String pageTitle, String hostName) {
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
    public abstract void trackPageView(String pageURL, String pageTitle, String hostName,
                                       String referrerPage, String referrerSite,
                                       List<CustomVariable> customVariables);


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
    public abstract void trackEvent(String category, String action, String label,
                                    Integer value, List<CustomVariable> customVariables);

}
