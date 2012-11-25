package com.wadpam.open.analytics.google;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Contains information about a device.
 * @author mattiaslevin
 */
public class Device {

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

    /** The host */
    private String host;

    /** Remote address */
    private String remoteAddress;

    /** User agent */
    private String userAgent;


    // Create a default iPhone5 with basic settings
    public static Device defaultiPhoneDevice() {
        Device device = new Device();
        device.screenResolution = "1136x640";
        device.viewPortResolution = "1136x640";
        device.colorDepth = "24-bit";
        device.userLanguage = Locale.getDefault().getLanguage();
        device.userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
        return device;
    }

    // Create a default iPad with basic settings
    public static Device defaultiPadDevice() {
        Device device = new Device();
        device.screenResolution = "2048x1536";
        device.viewPortResolution = "2048x1536";
        device.colorDepth = "24-bit";
        device.userLanguage = Locale.getDefault().getLanguage();
        device.userAgent = "Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
        return device;
    }

    // Create a default profile from http request
    public static Device defaultDevice(HttpServletRequest request) {
        Device device = new Device();
        device.screenResolution = "1136x640";
        device.colorDepth = "24-bit";
        device.userLanguage = request.getLocale().getLanguage();
        if (null == device.userLanguage || device.userLanguage.isEmpty())
            device.userLanguage = Locale.getDefault().getLanguage();
        device.host = request.getHeader("Host");
        device.remoteAddress = request.getRemoteAddr();
        device.userAgent = request.getHeader("User-Agent");
        return device;
    }

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
