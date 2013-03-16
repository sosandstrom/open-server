package com.wadpam.open.analytics.google.protocol;

import com.wadpam.open.analytics.google.trackinginfo.*;
import com.wadpam.open.analytics.google.config.Property;
import com.wadpam.open.analytics.google.config.Visitor;
import com.wadpam.open.analytics.google.config.Application;
import com.wadpam.open.analytics.google.config.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A Google Analytics protocol compatible with the legacy 5.3.7 format.
 * This is a legacy protocol format and has since been replaced with the
 * new Measurement protocol.
 *
 * @author mattiaslevin
 */
public class LegacyProtocol_v5_3_8 extends GoogleAnalyticsProtocol {
    private static final Logger LOG = LoggerFactory.getLogger(LegacyProtocol_v5_3_8.class);

    private Random random = new Random();

    private static final String BASE_URL = "http://www.google-analytics.com/__utm.gif";


    // Get version
    @Override
    public String getFormatVersion() {
        return "5.3.8";
    }

    @Override
    public String baseUrl() {
        return BASE_URL;
    }


    // Build URL
    @Override
    public String buildParams(Property property, Visitor visitor, Device device, Application app, TrackingInfo trackingInfo) {
        LOG.debug("Build url for version:{}", this.getFormatVersion());

        ContentInfo contentInfo = trackingInfo.getContentInfo();
        TrafficSource trafficSource = trackingInfo.getTrafficSource();
        Event event = trackingInfo.getEvent();


        Map<String, String> params = new LinkedHashMap<String, String>();

        // Version of the format
        params.put("utmwv", this.getFormatVersion());

        // utms
        // TODO Not sure what this is
        params.put("utms", "3");

        // Random int to avoid caching
        params.put("utmn", Integer.toString(random.nextInt(Integer.MAX_VALUE)));

        // Hostname
        if(null != contentInfo && null != contentInfo.getHostName()) {
            params.put("utmhn", urlEncode(contentInfo.getHostName()));
        }

        // Events
        StringBuilder eventSB = new StringBuilder();
        if (null != event && null != event.getCategory() && null != event.getAction()) {
            params.put("utmt", "event");

            // Format 5(category*action*label*value)

            // Category and action
            eventSB.append("5(").append(urlEncode(event.getCategory())).
                    append("*").
                    append(urlEncode(event.getAction()));

            // Label
            if(null != event.getLabel()){
                eventSB.append("*").append(urlEncode(event.getLabel()));
            }

            // Value
            if(null != event.getValue()) {
                eventSB.append("*").append(event.getValue());
            }

            eventSB.append(")");
        }
        else if (null != event && (null != event.getAction() || null != event.getCategory())) {
            // Both category and action must be provided
            throw new IllegalArgumentException("Event tracking must have both a category and an action");
        }

        // Custom vars
        if (null != trackingInfo.getCustomVariables() && trackingInfo.getCustomVariables().size() > 0) {

            StringBuilder names = new StringBuilder();
            StringBuilder values = new StringBuilder();
            StringBuilder scope = new StringBuilder();

            Iterator<CustomVariable> iterator = trackingInfo.getCustomVariables().iterator();
            while (iterator.hasNext()) {
                CustomVariable customVariable = iterator.next();

                // <index>!<name>*<index>!<name>...
                names.append(customVariable.getIndex()).append("!").append(urlEncode(customVariable.getName()));
                // <index>!<value>*<index>!<value>...
                values.append(customVariable.getIndex()).append("!").append(urlEncode(customVariable.getValue()));
                // <index>!<scope>*<index>!<scope>..
                scope.append(customVariable.getIndex()).append("!").append(customVariable.getScope().getValue());

                if (iterator.hasNext()) {
                    names.append("*");
                    values.append("*");
                    scope.append("*");
                }
            }

            // Build the full string
            // 8(<index>!<name>*<index>!<name>)9(<index>!<value>*<index>!<value>)11(<index>!<scope>*<index>!<scope>)
            String customVars = String.format("8(%s)9(%s)11(%s)", names.toString(), values.toString(), scope.toString());

            eventSB.append(customVars);
        }

        if (eventSB.length() > 0) {
            params.put("utme", eventSB.toString());
        }

        // Encoding
        if (null != device.getEncoding()) {
            params.put("utmcs", device.getEncoding());
        } else {
            params.put("utmcs", "-");
        }

        // Screen resolution
        if (null != device.getScreenResolution()) {
            params.put("utmsr", device.getScreenResolution());
        }

        // View port
        if (null != device.getViewPortSize()) {
            params.put("utmvp", device.getViewPortSize());
        }

        // color depth
        if (null != device.getColorDepth()) {
            params.put("utmsc", device.getColorDepth());
        }

        // Language
        if (null != device.getUserLanguage()) {
            params.put("utmul", device.getUserLanguage());
        }

        // Java enabled (always set to true)
        params.put("utmje", "1");

        // Flash version
        if(null != device.getFlashVersion()) {
            params.put("utmfl", urlEncode(device.getFlashVersion()));
        }

        // Page views
        // Do not need to set utmt since default i "page"
        // Page title
        if (null != contentInfo && null != contentInfo.getTitle()) {
            params.put("utmdt", urlEncode(contentInfo.getTitle()));
        }

        // Random number for AdSense
        params.put("utmhid", Integer.toString(random.nextInt(Integer.MAX_VALUE)));


        // Referrer
        if (null != trafficSource && null != trafficSource.getDocumentReferrer()) {
            params.put("utmr", urlEncode(trafficSource.getDocumentReferrer()));
        } else {
            params.put("utmr", "-");
        }

        // Page url
        if (null != contentInfo && null != contentInfo.getPath()) {
            params.put("utmp", urlEncode(contentInfo.getPath()));
        }

        // Tracking id
        params.put("utmac", property.getTrackingId());

        // Cookie
        int hostnameHash = hostnameHash("DummyHostName");
        if (null != contentInfo && null != contentInfo.getHostName()) {
            hostnameHash = hostnameHash(contentInfo.getHostName());
        }
        int visitorId = visitor.getVisitorId().hashCode();
        long timestampFirst = visitor.getFirstVisitTimestamp();
        long timestampPrevious = visitor.getPreviousVisitTimestamp();
        int visits = visitor.getNumberOfVisits();
        long sessionStart = visitor.getSession().getStartTimestamp();

        // utmccn=(organic)|utmcsr=google|utmctr=snotwuh|utmcmd=organic
        StringBuilder cookieSB = new StringBuilder();
        cookieSB.append("__utma%3D")
                .append(hostnameHash)
                .append(".").append(visitorId)
                .append(".").append(timestampFirst)
                .append(".").append(timestampPrevious)
                .append(".").append(sessionStart)
                .append(".").append(visits)
                .append("%3B%2B")
                .append("__utmz%3D")
                .append(hostnameHash)
                .append(".").append(timestampFirst)
                .append(".1")
                .append(".1")
                .append(".utmcsr%3D").append(trafficSource != null && trafficSource.getCampaignSource() != null ? trafficSource.getCampaignSource() : "")
                .append("%7Cutmccn%3D").append(trafficSource != null && trafficSource.getCampaignName() != null ? trafficSource.getCampaignName() : "")
                .append("%7Cutmcmd%3D").append(trafficSource != null && trafficSource.getCampaignMedium() != null ? trafficSource.getCampaignMedium() : "")
                .append(trafficSource != null && trafficSource.getCampaignKeyword() != null ? "%7Cutmctr%3D" + trafficSource.getCampaignKeyword() : "")
                .append(trafficSource != null && trafficSource.getCampaignContent() != null ? "%7Cutmcct%3D" + trafficSource.getCampaignContent() : "")
                .append("%3B");

        params.put("utmcc", cookieSB.toString());

        // utmu
        // TODO: Do not know what this is or if it is needed
        params.put("utmu", "qQ~");

        // Build the query string
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        // Remove last &
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    /* page view url:
    * http://www.google-analytics.com/__utm.gif
    * ?utmwv=4.7.2
    * &utmn=631966530
    * &utmhn=www.dmurph.com
    * &utmcs=ISO-8859-1
    * &utmsr=1280x800
    * &utmsc=24-bit
    * &utmul=en-us
    * &utmje=1
    * &utmfl=10.1%20r53
    * &utmdt=Hello
    * &utmhid=2043994175
    * &utmr=0
    * &utmp=%2Ftest%2Ftest.php
    * &utmac=UA-17109202-5
    * &utmcc=__utma%3D143101472.2118079581.1279863622.1279863622.1279863622.1%3B%2B__utmz%3D143101472.1279863622.1.1.utmcsr%3D(direct)%7Cutmccn%3D(direct)%7Cutmcmd%3D(none)%3B&gaq=1
    */

    /* event url:
    * http://www.google-analytics.com/__utm.gif
    * ?utmwv=4.7.2
    * &utmn=480124034
    * &utmhn=www.dmurph.com
    * &utmt=event
    * &utme=5(Videos*Play)
    * &utmcs=ISO-8859-1
    * &utmsr=1280x800
    * &utmsc=24-bit
    * &utmul=en-us
    * &utmje=1
    * &utmfl=10.1%20r53
    * &utmdt=Hello
    * &utmhid=166062212
    * &utmr=0
    * &utmp=%2Ftest%2Ftest.php
    * &utmac=UA-17109202-5
    * &utmcc=__utma%3D143101472.2118079581.1279863622.1279863622.1279863622.1%3B%2B__utmz%3D143101472.1279863622.1.1.utmcsr%3D(direct)%7Cutmccn%3D(direct)%7Cutmcmd%3D(none)%3B&gaq=1
    */

    // Host name hash
    private int hostnameHash(String hostname){
        return hostname.hashCode();
    }

}