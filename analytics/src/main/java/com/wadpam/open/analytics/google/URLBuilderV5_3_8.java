package com.wadpam.open.analytics.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Build a GA url compatible with the version 5.3.7 format.
 * @author mattiaslevin
 */
public class URLBuilderV5_3_8 implements URLBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(URLBuilderV5_3_8.class);

    private Random random = new Random();

    private static final String BASE_URL = "http://www.google-analytics.com/__utm.gif";


    // Get version
    @Override
    public String getFormatVersion() {
        return "5.3.8";
    }


    // Build URL
    @Override
    public String buildURL(TrackerConfiguration trackerConfig, Visitor visitor, Device device, Page data) {
        LOG.debug("Build url for version:{}", this.getFormatVersion());

        Map<String, String> params = new LinkedHashMap<String, String>();

        // Version of the format
        params.put("utmwv", this.getFormatVersion());

        // utms
        // TODO Not sure what this is
        params.put("utms", "3");

        // Random int to avoid caching
        params.put("utmn", Integer.toString(random.nextInt(Integer.MAX_VALUE)));

        // Hostname
        if(null != data.getHostName()) {
            params.put("utmhn", urlEncode(data.getHostName()));
        }

        // Events
        StringBuilder eventSB = new StringBuilder();
        if (null != data.getEventAction() && null != data.getEventCategory()) {
            params.put("utmt", "event");

            // Format 5(category*action*label*value)

            // Category and action
            eventSB.append("5(").append(urlEncode(data.getEventCategory())).
                    append("*").
                    append(urlEncode(data.getEventAction()));

            // Label
            if(data.getEventLabel() != null){
                eventSB.append("*").append(urlEncode(data.getEventLabel()));
            }

            // Value
            if(null != data.getEventValue()) {
                eventSB.append("*").append(data.getEventValue());
            }

            eventSB.append(")");
        }
        else if (data.getEventAction() != null || data.getEventCategory() != null) {
            // Both category and action must be provided
            throw new IllegalArgumentException("Event tracking must have both a category and an action");
        }

        // Custom vars
        if (null != data.getCustomVariables() && data.getCustomVariables().size() > 0) {

            StringBuilder names = new StringBuilder();
            StringBuilder values = new StringBuilder();
            StringBuilder scope = new StringBuilder();

            Iterator<CustomVariable> iterator = data.getCustomVariables().iterator();
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
        if (null != device.getViewPortResolution()) {
            params.put("utmvp", device.getViewPortResolution());
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
        if (null != data.getPageTitle()) {
            params.put("utmdt", urlEncode(data.getPageTitle()));
        }

        // Random number for AdSense
        params.put("utmhid", Integer.toString(random.nextInt(Integer.MAX_VALUE)));


        // Referral
        if (null != data.getUtmr()) {
            params.put("utmr", urlEncode(data.getUtmr()));
        } else {
            params.put("utmr", "-");
        }

        // Page url
        if (null != data.getPageURL()) {
            params.put("utmp", urlEncode(data.getPageURL()));
        }

        // Tracking id
        params.put("utmac", trackerConfig.getTrackingId());

        // Cookie
        int hostnameHash = hostnameHash(data.getHostName());
        int visitorId = visitor.getVisitorId();
        long timestampFirst = visitor.getTimestampFirst();
        long timestampPrevious = visitor.getTimestampPrevious();
        int visits = visitor.getVisits();
        long sessionStart = visitor.getSession().getSessionStart();

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
                .append(".utmcsr%3D").append(data.getUtmcsr())
                .append("%7Cutmccn%3D").append(data.getUtmccn())
                .append("%7Cutmcmd%3D").append(data.getUtmcmd())
                .append(data.getUtmctr() != null ? "%7Cutmctr%3D" + data.getUtmcsr() : "")
                .append(data.getUtmcct() != null ? "%7Cutmcct%3D" + data.getUtmcct() : "")
                .append("%3B");

        params.put("utmcc", cookieSB.toString());

        // utmu
        // TODO: Do not know what this is or if it is needed
        params.put("utmu", "qQ~");

        StringBuilder sb = new StringBuilder();
        // Set base url
        sb.append(BASE_URL).append("?");
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


    // URL encode a string
    private String urlEncode(String string) {
        if(null == string){
            return null;
        }
        try {
            // Google Analytics expect %20 encoding to space
            //return URLEncoder.encode(string, "UTF-8");        // Will encode space to +
            return UriUtils.encodeQueryParam(string, "UTF-8");  // Will encode space to %20
        } catch (UnsupportedEncodingException e) {
            LOG.error("URL encodes does not support character format");
            throw new IllegalArgumentException();
        }
    }


    // Host name hash
    private int hostnameHash(String hostname){
        return 999;
    }

}