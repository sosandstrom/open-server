package com.wadpam.open.analytics.google;

import java.net.URI;

/**
 * Interface for different dispatch strategies to Google Analytics, e.g. synchronous, asynchronous, GAE task.
 * @author mattiaslevin
 */
public interface EventDispatcher {



    /**
     * Dispatch a request to GA to track a pageview or event.
     * @param uri the url to use
     * @return true if the dispatch was successful (in case of asynchronous request it will always return true)
     */
    public abstract boolean dispatch(Device device, URI uri);

}
