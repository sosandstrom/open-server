package com.wadpam.open.analytics.google;

import com.wadpam.open.analytics.OpenAnalyticsTracker;

/**
 * Represent a profile.
 * @author mattiaslevin
 */
public class Profile {

    private TrackerConfiguration configurationData;

    /**
     * Create a Google Analytics profile with a specific tracking id.
     * @param trackingId the tracking id for this profile
     * @param name The name of the profile
     */
    public Profile(String name, String trackingId) {
        this.configurationData = new TrackerConfiguration(name, trackingId);
    }

    /**
     * Create a tracker for a specific user and device.
     * @param visitor visitor specific data
     * @param device device data
     * @return a tracker
     */
    public OpenAnalyticsTracker getTracker(Visitor visitor, Device device) {
        return new GAOpenAnalyticsTracker(this.configurationData, visitor, device);
    }

}
