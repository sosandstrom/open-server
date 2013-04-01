package com.wadpam.open.analytics.google.config;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Contains information about the device being tracked.
 *
 * @author mattiaslevin
 */
public class Device {

    /** The encoding */
    private String encoding = "UTF-8";

    /** Screen resolution */
    private String screenResolution;

    /** View port size */
    private String viewPortSize;

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


    /**
     * Create a default iPhone5 device running iOS6.
     */
    public static Device defaultiPhoneDevice() {
        Device device = new Device();
        device.screenResolution = "1136x640";
        device.viewPortSize = "1136x640";
        device.colorDepth = "24-bit";
        device.userLanguage = Locale.getDefault().getLanguage();
        device.userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
        return device;
    }

    /**
     * Create a default iPad device running iOS6.
     */
    public static Device defaultiPadDevice() {
        Device device = new Device();
        device.screenResolution = "2048x1536";
        device.viewPortSize = "2048x1536";
        device.colorDepth = "24-bit";
        device.userLanguage = Locale.getDefault().getLanguage();
        device.userAgent = "Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25";
        return device;
    }

    /**
     * Create a default Samsung Galaxy 3 device running Jelly Beans.
     */
    public static Device defaultGalaxy3() {
        Device device = new Device();
        device.screenResolution = "1280x720";
        device.viewPortSize = "1280x720";
        device.colorDepth = "24-bit";
        device.userLanguage = Locale.getDefault().getLanguage();
        device.userAgent = "Mozilla/5.0 (Linux; U; Android 4.1.2; en-gb; GT-I9300 Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
        return device;
    }

    /**
     * Create a device from the http request.
     * Language, host, remote address and user agent will be set, other properties will be null.
     */
    public static Device defaultDevice(HttpServletRequest request) {
        Device device = new Device();
        //device.screenResolution = "1136x640";
        //device.colorDepth = "24-bit";
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

    public String getViewPortSize() {
        return viewPortSize;
    }

    public void setViewPortSize(String viewPortSize) {
        this.viewPortSize = viewPortSize;
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
