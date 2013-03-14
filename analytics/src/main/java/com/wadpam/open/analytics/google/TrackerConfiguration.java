package com.wadpam.open.analytics.google;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Configuration related data.
 * @author mattiaslevin
 */
public class TrackerConfiguration {

    /** The tracking Id */
    private String trackingId;

    /** The name of the tracker */
    private String name;


    // Constructor
    public TrackerConfiguration(String name, String trackingId) {
        this.trackingId = trackingId;
        this.name = name;
    }


    // Setters and Getters
    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
