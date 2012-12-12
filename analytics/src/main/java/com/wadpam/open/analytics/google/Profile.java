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
    private Profile(String name, String trackingId) {
        this.configurationData = new TrackerConfiguration(name, trackingId);
    }


    /**
     * Create a tracking profile based in a unique static tracking id.
     * @param name the name of the profile
     * @param trackingId the unique tracking id
     * @return a tracking profile
     */
    public static Profile getInstance(String name, String trackingId) {
        return new Profile(name, trackingId);
    }


    /**
     * Create a tracking provide based in the provided tracking code mapper.
     * @param name the name of the profile
     * @param domain the domain name
     * @param trackingCodeMapper an instance of a tracking code mapper
     * @return a tracking profile
     */
    public static Profile getInstance(String name, String domain, TrackingCodeMapper trackingCodeMapper) {
        return new Profile(name, trackingCodeMapper.mapToTrackingCode(domain));
    }


    /**
     * Create a tracker for a specific user and device.
     * @param visitor visitor specific data
     * @param device device data
     * @return a tracker
     */
    public GAOpenAnalyticsTracker getTracker(Visitor visitor, Device device) {
        return new GAOpenAnalyticsTracker(this.configurationData, visitor, device);
    }

}
