package com.wadpam.open.json;

import java.io.Serializable;

/**
 * Implement a basic monitor controller that can be used for monitoring uptime of a backend.
 * @author sosandstrom
 */
public class JMonitor implements Serializable {

    private Long currentTimeMillis;
    private String ipAddress;
    private String mavenVersion;
    private String gaeAppId;
    private String gaeVersion;
    private String namespace;


    // Setters and getters
    public Long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(Long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMavenVersion() {
        return mavenVersion;
    }

    public void setMavenVersion(String mavenVersion) {
        this.mavenVersion = mavenVersion;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getGaeAppId() {
        return gaeAppId;
    }

    public void setGaeAppId(String gaeAppId) {
        this.gaeAppId = gaeAppId;
    }

    public String getGaeVersion() {
        return gaeVersion;
    }

    public void setGaeVersion(String gaeVersion) {
        this.gaeVersion = gaeVersion;
    }
}
