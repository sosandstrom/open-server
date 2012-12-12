package com.wadpam.open.analytics.google;

/**
 * Callback class mapping domain to an unique tracking code.
 */
public interface TrackingCodeMapper {

    /**
     * Create a unique tracking code based on domain
     * @param domain the domain name
     * @return unique tracking code
     */
    String mapToTrackingCode(String domain);

}
