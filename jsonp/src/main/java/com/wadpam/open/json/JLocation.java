package com.wadpam.open.json;

/**
 * A JLocation contains the latitude and longitude coordinates.
 * 
 * @author os
 */
public class JLocation {
    private float latitude;
    private float longitude;

    public JLocation() {
    }

    public JLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("[%s,%s]", getLatitude(), getLongitude());
    }

}
