package com.wadpam.open.json;

import java.io.Serializable;

/**
 * A JLocation contains the latitude and longitude coordinates.
 * @author sosandstrom
 */
public class JLocation implements Serializable {

    private Float latitude;
    private Float longitude;

    public JLocation() {
    }

    public JLocation(Float latitude, Float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("[%s,%s]", getLatitude(), getLongitude());
    }

}
