package com.wadpam.open.analytics.google.config;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Configure a Google Analytics Property.
 * A corresponding Property must be configured in Google Analytics web admin interface.
 *
 * @author mattiaslevin
 */
public class Property {

    /** The tracking Id */
    private String trackingId;

    /** The name of the tracker */
    private String name;


    // Constructor
    public Property(String name, String trackingId) {
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
