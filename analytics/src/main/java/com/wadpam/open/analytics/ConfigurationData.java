package com.wadpam.open.analytics;

/**
 * Configuration related data.
 * @author mattiaslevin
 */
public class ConfigurationData {

    // Constructor
    public ConfigurationData(String trackingID) {
        this.trackingID = trackingID;
    }

    /** The tracking ID */
    private String trackingID;

    // Setters and Getters
    public String getTrackingID() {
        return trackingID;
    }

    public void setTrackingID(String trackingID) {
        this.trackingID = trackingID;
    }
}
