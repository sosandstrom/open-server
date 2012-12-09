package com.google.appengine.api.utils;

/**
 *
 * @author os
 */
public class SystemProperty {
    public static final String KEY_APPLICATION_ID = "com.google.appengine.application.id";
    public static final String KEY_APPLICATION_VERSION = "com.google.appengine.application.version";
    
    public static final SystemProperty applicationId = new SystemProperty(KEY_APPLICATION_ID, null);
    public static final SystemProperty applicationVersion = new SystemProperty(KEY_APPLICATION_VERSION, null);
    
    private final String key;
    private String value;
    
    protected SystemProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public String get() {
        return value;
    }
    
    public String key() {
        return key;
    }
    
    public void set(String value) {
        this.value = value;
    }
}
