package com.wadpam.open.analytics;

/**
 * Contains information about a device.
 * @author mattiaslevin
 */
public class DeviceData {

    /** The encoding */
    private String encoding = "UTF-8";

    /** Screen resolution */
    private String screenResolution;

    /** View port size */
    private String viewPortResolution;

    /** Color depth */
    private String colorDepth;

    /** User language */
    private String userLanguage;

    /** Flash version */
    private String flashVersion;

    /** User agent */
    private String userAgent;


    @Override
    public String toString() {
        return String.format("User-agent:%s", this.userAgent);
    }


    // Setters and Getters
    public String getColorDepth() {
        return colorDepth;
    }

    public void setColorDepth(String colorDepth) {
        this.colorDepth = colorDepth;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFlashVersion() {
        return flashVersion;
    }

    public void setFlashVersion(String flashVersion) {
        this.flashVersion = flashVersion;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
    }

    public String getViewPortResolution() {
        return viewPortResolution;
    }

    public void setViewPortResolution(String viewPortResolution) {
        this.viewPortResolution = viewPortResolution;
    }
}
