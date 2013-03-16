package com.wadpam.open.analytics.google.protocol;

import com.wadpam.open.analytics.google.trackinginfo.*;
import com.wadpam.open.analytics.google.config.Property;
import com.wadpam.open.analytics.google.config.Visitor;
import com.wadpam.open.analytics.google.config.Application;
import com.wadpam.open.analytics.google.config.Device;
import com.wadpam.open.analytics.google.trackinginfo.Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implementation of the Measurement protocol.
 *
 * @author mattiaslevin
 */
public class MeasurementProtocol_v1 extends GoogleAnalyticsProtocol {
    private static final Logger LOG = LoggerFactory.getLogger(MeasurementProtocol_v1.class);

    // The type of views being tracker, page view (web) or app screen (app)
    // The same tracker instance can not handle both types.
    public enum ViewTrackingType {
        APPVIEW, PAGEVIEW
    }

    private Random random = new Random();

    private static final String BASE_URL = "http://www.google-analytics.com/collect";
    private static final String BASE_URL_SECURE = "https://ssl.google-analytics.com/collect";

    /**
     * Default tracking is app screen views.
     * Change this value is you like to track pageviews instead.
     * The Property must be configured with the same type in the Google Analytics
     * web admin page.
     */
    private ViewTrackingType viewTrackingType = ViewTrackingType.APPVIEW;


    @Override
    public String getFormatVersion() {
        return "1";
    }

    @Override
    public String baseUrl() {
        return BASE_URL;
    }

    @Override
    public String buildParams(Property property, Visitor visitor, Device device, Application app, TrackingInfo trackingInfo) {
        LOG.debug("Build url for version:{}", this.getFormatVersion());

        Map<String, String> params = new LinkedHashMap<String, String>();

        ContentInfo contentInfo = trackingInfo.getContentInfo();
        TrafficSource trafficSource = trackingInfo.getTrafficSource();
        Event event = trackingInfo.getEvent();

        // Version of the protocol
        params.put("v", this.getFormatVersion());

        // Tracking id
        params.put("tid", property.getTrackingId());

        // Anonymize API
        //params.put("api", ?);

        // Queue time
        if (null != trackingInfo.getQueueTime()) {
            params.put("qt", Integer.toString(trackingInfo.getQueueTime()));
        }

        // Client id
        params.put("cid", visitor.getVisitorId());

        // Session control - start, stop
        if (visitor.getSession().isTrackStartSession()) {
            params.put("sc", "start");
            visitor.getSession().setTrackStartSession(false);
        } if (visitor.getSession().getStartTimestamp() == -1) {
            params.put("sc", "end");
        }

        // Traffic source
        if (null != trafficSource) {
            // Document referrer
            if (null != trafficSource.getDocumentReferrer()) {
                params.put("dr", trafficSource.getDocumentReferrer());
            }

            // Campaign name
            if (null != trafficSource.getCampaignName()) {
                params.put("cn", trafficSource.getCampaignName());
            }

            // Campaign source
            if (null != trafficSource.getCampaignSource()) {
                params.put("cs", trafficSource.getCampaignSource());
            }

            // Campaign medium
            if (null != trafficSource.getCampaignMedium()) {
                params.put("cm", trafficSource.getCampaignMedium());
            }

            // Campaign keyword
            if (null != trafficSource.getCampaignKeyword()) {
                params.put("ck", trafficSource.getCampaignKeyword());
            }

            // Campaign content
            if (null != trafficSource.getCampaignContent()) {
                params.put("cc", trafficSource.getCampaignContent());
            }

            // Campaign ID
            if (null != trafficSource.getCampaignId()) {
                params.put("ci", trafficSource.getCampaignId());
            }
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

        // Non-iteractive hit
        // Unsure about the use of this parameter
        //params.put("ni", ?);

        // Hit type
        // Must be one of 'pageview', 'appview', 'event', 'transaction', 'item', 'social', 'exception', 'timing'.
        // Only pageview, appview and event is supported right now
        String hitType = "pageview";

        // Content info
        if (null != contentInfo) {
            if (viewTrackingType == ViewTrackingType.APPVIEW) {
                hitType = "appview";
            }

            // Document location URL
            if (null != contentInfo.getAbsoluteUrl()) {
                params.put("dl", contentInfo.getAbsoluteUrl());
            } else if (null != contentInfo.getHostName() && null != contentInfo.getPath()) {
                params.put("dl", String.format("http://%s/%s", contentInfo.getHostName(), contentInfo.getPath()));
            }

            // Document host name
            if (null != contentInfo.getHostName()) {
                params.put("dh", contentInfo.getHostName());
            }

            // Document path
            if (null != contentInfo.getPath()) {
                params.put("dp", contentInfo.getPath());
            }

            // Document title
            if (null != contentInfo.getTitle()) {
                params.put("dt", contentInfo.getTitle());
            }

            // ContentInfo description
            if (null != contentInfo.getContentDescription()) {
                params.put("cd", contentInfo.getContentDescription());
            } else {
                // Set the absolute url as default
                params.put("cd", params.get("dl"));
            }

            // TODO Screen views does still now show, only in real-time view

        }

        // Application type
        if (null != app && null != app.getName()) {
            // Application name
            params.put("an", app.getName());

            // Application version
            if (null != app.getVersion()) {
                params.put("av", app.getVersion());
            }
        }

        // Event type
        if (null != event) {
            // This is an event hit type
            hitType = "event";

            // Category
            if (null != event.getCategory()) {
                params.put("ec", event.getCategory());
            }

            // Action
            if (null != event.getAction()) {
                params.put("ea", event.getAction());
            }

            // Label
            if (null != event.getLabel()) {
                params.put("el", event.getLabel());
            }

            // Value
            if (null != event.getValue()) {
                params.put("ev", Integer.toString(event.getValue()));
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
        // TODO Must implement

        // Timing
        // TODO


        // Exceptions
        if (null != trackingInfo.getException()) {
            Exception exception = trackingInfo.getException();

            // Description
            params.put("exd", exception.getDescription());

            // Fatal
            if (exception.isFatal()) {
                params.put("exf", "0");
            }  else {
                params.put("exf", "1");
            }
        }


        // Custom Dimensions / Metrics
        if (null != trackingInfo.getCustomDimension()) {
            for (Map.Entry<Integer, String> dimension : trackingInfo.getCustomDimension().entrySet()) {
                params.put(String.format("cd[%d]", dimension.getKey()), dimension.getValue());
            }
        }

        // Custom Metrics
        if (null != trackingInfo.getCustomMetric()) {
            for (Map.Entry<Integer, Integer> metric : trackingInfo.getCustomMetric().entrySet()) {
                params.put(String.format("cm[%d]", metric.getKey()), Integer.toString(metric.getValue()));
            }
        }

        // Set the tracking info type
        // Must be one of 'pageview', 'appview', 'event', 'transaction', 'item', 'social', 'exception', 'timing'.
        params.put("t", hitType);

        // Cache buster, only needed in GET operations
        params.put("z", Integer.toString(random.nextInt(Integer.MAX_VALUE)));

        // Build the query string
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(urlEncode(entry.getValue())).append("&");
        }

        // Remove last &
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }


    // Setters and getters
    public ViewTrackingType getViewTrackingType() {
        return viewTrackingType;
    }

    public void setViewTrackingType(ViewTrackingType viewTrackingType) {
        this.viewTrackingType = viewTrackingType;
    }
}
