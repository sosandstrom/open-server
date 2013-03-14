package com.wadpam.open.analytics.google;

/**
 * Represent a tracked application
 * @author mattiaslevin*
 */
public class Application {

    /**
     *  Application name.
     *  Added in universal analytics
     */
    private String name;

    /**
     * Application version
     *  Added in universal analytics
     */
    private String version;


    // Constructor
    public Application(String applicationName, String version) {
        this.name = applicationName;
        this.version = version;
    }


    // Setters and getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
