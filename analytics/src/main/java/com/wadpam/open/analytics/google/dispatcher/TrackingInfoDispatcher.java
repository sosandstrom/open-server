package com.wadpam.open.analytics.google.dispatcher;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Interface for different dispatch strategies to Google Analytics, e.g. synchronous, asynchronous,
 * GAE task, GET and POST.
 *
 * @author mattiaslevin
 */
public interface TrackingInfoDispatcher {

    /**
     * Dispatch tracking info to GA.
     * @param baseUrl the base utl to GA
     * @param params the params to use either as query parameters or in the request body
     * @param userAgent the original senders user agent
     * @param remoteAddress the original senders remote address
     * @return true if the dispatch was successful (in case of asynchronous request it will always return true)
     */
    public abstract boolean dispatch(String baseUrl, String params, String userAgent, String remoteAddress) throws URISyntaxException;

}
