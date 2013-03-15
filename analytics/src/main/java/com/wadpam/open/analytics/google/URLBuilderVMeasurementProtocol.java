package com.wadpam.open.analytics.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implement the universal analytics format.
 * @author mattiaslevin
 */
public class URLBuilderVMeasurementProtocol extends URLBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(URLBuilderVMeasurementProtocol.class);

    private Random random = new Random();

    private static final String BASE_URL = "http://www.google-analytics.com/collect";
    private static final String BASE_URL_SECURE = "https://www.google-analytics.com/collect";

    @Override
    public String getFormatVersion() {
        return "1";
    }

    @Override
    public String buildURL(TrackerConfiguration trackerConfig, Visitor visitor, Device device, Application app, Page data) {
        LOG.debug("Build url for version:{}", this.getFormatVersion());

        Map<String, String> params = new LinkedHashMap<String, String>();

        // Version of the format
        params.put("v", this.getFormatVersion());

        // Tracking id
        params.put("tid", trackerConfig.getTrackingId());

        // Anonymize API
        //params.put("api", ?);

        // Queue time
        //params.put("qt", ?);

        // Client id
        params.put("cid", visitor.getVisitorId());

        // Session control, start, stop
        //params.put("sc", ?);

        // Document referrer
        if (null != data.getReferrer()) {
            params.put("dr", data.getReferrer());
        }

        // Campaign name
        if (null != data.getCampaignName()) {
            params.put("cn", data.getCampaignName());
        }

        // Campaign source
        if (null != data.getCampaignSource()) {
            params.put("cs", data.getCampaignSource());
        }

        // Campaign medium
        if (null != data.getCampaignMedium()) {
            params.put("cm", data.getCampaignMedium());
        }

        // Campaign keyword
        if (null != data.getCampaignKeyword()) {
            params.put("ck", data.getCampaignKeyword());
        }

        // Campaign content
        if (null != data.getCampaignContent()) {
            params.put("cc", data.getCampaignContent());
        }

        // Campaign ID
        if (null != data.getCampaignId()) {
            params.put("ci", data.getCampaignId());
        }

//        // Google AdWords ID
//        if (null != data.getGoogleAdWordsId()) {
//            params.put("gclid", ?);
//        }

//        // Google Display Ads ID
//        if (null != data.getGoogleDisplayAdsId()) {
//            params.put("dclid", ?);
//        }

        // Screen resolution
        if (null != device.getScreenResolution()) {
            params.put("sr", device.getScreenResolution());
        }

        // View port
        if (null != device.getViewPortSize()) {
            params.put("vp", device.getViewPortSize());
        }

        // Encoding
        if (null != device.getEncoding()) {
            params.put("de", device.getEncoding());
        }

        // Color depth
        if (null != device.getColorDepth()) {
            params.put("sd", device.getColorDepth());
        }

        // Language
        if (null != device.getUserLanguage()) {
            params.put("ul", device.getUserLanguage());
        }

        // Java enabled (always set to true)
        params.put("je", "1");

        // Flash version
        if(null != device.getFlashVersion()) {
            params.put("fl", device.getFlashVersion());
        }

        // Hit type
        // Must be one of 'pageview', 'appview', 'event', 'transaction', 'item', 'social', 'exception', 'timing'.
        // Only pageview, appview and event is supported right now'
        String hitType = "pageview"; // TODO Do we need to support pageview

        // Non-iteractive hit
        // Unsure about the use of this parameter
        //params.put("ni", ?);

        // Document location URL
        if (null != data.getDocumentUrl()) {
            params.put("dl", data.getDocumentUrl());
        }

        // Document host name
        if (null != data.getHostName()) {
            params.put("dh", data.getHostName());
        }

        // Document path
        if (null != data.getDocumentPath()) {
            params.put("dp", data.getDocumentPath());
        }

        // Document title
        if (null != data.getDocumentTitle()) {
            params.put("dt", data.getDocumentTitle());
        }

        // Content description
        if (null != data.getContentDescription()) {
            params.put("cd", data.getContentDescription());
        }

        // Application type
        if (null != app && null != app.getName()) {
            hitType = "appview";

            // Application name
            params.put("an", app.getName());

            // Application version
            if (null != app.getVersion()) {
                params.put("av", app.getVersion());
            }
        }

        // Event type
        if (null !=data.getEventAction()) {
            hitType = "event";

            // Category
            if (null != data.getEventCategory()) {
                params.put("ec", data.getEventCategory());
            }

            // Action
            if (null != data.getEventAction()) {
                params.put("ea", data.getEventAction());
            }

            // Label
            if (null != data.getEventLabel()) {
                params.put("el", data.getEventLabel());
            }

            // Value
            if (null != data.getEventValue()) {
                params.put("ev", Integer.toString(data.getEventValue()));
            }
        }

//        // E-Commerce, not supported right now
//        if (null != transaction.getTransactionId) {
//            hitType = "transaction";
//
//            // Transaction id
//            params.put("ti", ?);
//
//            // Transaction affiliation
//            params.put("ta", ?);
//
//            // Transaction revenue
//            params.put("tr", ?);
//
//            // Transaction shipping
//            params.put("ts", ?);
//
//            // Transaction tax
//            params.put("tt", ?);
//
//            // Currency code
//            params.put("cu", ?);
//        }

//        if (null != items.name) {
//            hitType = "item";
//
//            // Item name
//             params.put("in", ?);
//
//            // Item price
//            params.put("ip", ?);
//
//            // Item quantity
//            params.put("iq", ?);
//
//            // Item code
//            params.put("ic", ?);
//
//            // Item category
//            params.put("iv", ?);
//
//            // Currency code
//            params.put("cu", ?);
//        }

        // Social interaction
        // TODO

        // Timing
        // TODO

        // Exceptions
        // TODO

        // Custom Dimensions / Metrics
        // TODO

        // Set the tracking info type
        // Must be one of 'pageview', 'appview', 'event', 'transaction', 'item', 'social', 'exception', 'timing'.
        params.put("t", hitType);

        // Cache buster
        params.put("z", Integer.toString(random.nextInt(Integer.MAX_VALUE)));

        StringBuilder sb = new StringBuilder();
        // Set base url
        sb.append(BASE_URL).append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(urlEncode(entry.getValue())).append("&");
        }

        // Remove last &
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }
}
