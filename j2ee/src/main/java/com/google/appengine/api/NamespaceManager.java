package com.google.appengine.api;

/**
 *
 * @author os
 */
public class NamespaceManager {
    private static final ThreadLocal<String> NAMESPACE = new ThreadLocal<String>();
    
    public static String get() {
        return NAMESPACE.get();
    }
    
    public static void set(String namespace) {
        NAMESPACE.set(namespace);
    }
}
