package com.wadpam.open.analytics.google;

import java.net.URI;

/**
 * Interface for different dispatch strategies to Google Analytics, e.g. synchronous, asynchronous, GAE task.
 * @author mattiaslevin
 */
public interface EventDispatcher {



    /**
     * Dispatch a request to GA to track a pageview or event.
     * @param analyticsUri the url to use
     * @param userAgent the original senders user agent
     * @param remoteAddress the original senders remote address
     * @return true if the dispatch was successful (in case of asynchronous request it will always return true)
     */
    public abstract boolean dispatch(URI analyticsUri, String userAgent, String remoteAddress);

}
