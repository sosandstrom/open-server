package com.wadpam.open.analytics.google;

import java.net.URI;

/**
 * Interface for different dispatch strategies to Google Analytics, e.g. synchronous, asynchronous, GAE task.
 * @author mattiaslevin
 */
public abstract class EventDispatcher {

    // The name of the host that made the original request
    private String host;

    // The ip address of the agent that made the original request
    private String remoteAddress;

    // The user agent of the device that made the original request
    private String userAgent;


    // Constructor
    public EventDispatcher(String host, String remoteAddress, String userAgent) {
        this.host = host;
        this.remoteAddress = remoteAddress;
        this.userAgent = userAgent;
    }


    /**
     * Dispatch a request to GA to track a pageview or event.
     * @param uri the url to use
     * @return true if the dispatch was successful (in case of asynchronous request it will always return true)
     */
    public abstract boolean dispatch(URI uri);


    // Setters and getters
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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
