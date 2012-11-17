package com.wadpam.open.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Build a GA url compatible with the version 5.3.7 format.
 * @author mattiaslevin
 */
public class URLBuilderV5_3_7 extends AbstractURLBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(URLBuilderV5_3_7.class);

    private Random random = new Random();

    private static final String BASE_URL = "http://www.google-analytics.com/__utm.gif";


    // Constructor
    public URLBuilderV5_3_7(ConfigurationData configurationData, VisitorData visitorData, DeviceData deviceData) {
        super(configurationData, visitorData, deviceData);
    }

    // Get version
    @Override
    public String getFormatVersion() {
        return "5.3.8";
    }


    // Build URL
    @Override
    public String buildURL(PageData data) {
        LOG.debug("Build url for version:{}", this.getFormatVersion());

        Map<String, String> params = new LinkedHashMap<String, String>();

        // Tracking id
        params.put("utmac", configurationData.getTrackingID());

        // Version of the format
        params.put("utmwv", this.getFormatVersion());

        // utms
        // TODO Not sure what this is
        params.put("utms", "2");

        // Random int to avoid caching
        params.put("utmn", Integer.toString(random.nextInt(Integer.MAX_VALUE)));

        // Random number for AdSense
        params.put("utmhid", Integer.toString(random.nextInt(Integer.MAX_VALUE)));

        // Hostname
        if(null != data.getHostName()) {
            params.put("utmhn", urlEncode(data.getHostName()));
        }

        // Page views
        // Do not need to set utmt since default i "page"

        // Page title
        if (null != data.getPageTitle()) {
            params.put("utmdt", urlEncode(data.getPageTitle()));
        }

        // Page url
        if (null != data.getPageURL()) {
            params.put("utmp", urlEncode(data.getPageURL()));
        }


        // Events
        StringBuilder eventSB = new StringBuilder();
        if (null != data.getEventAction() && null != data.getEventCategory()) {
            params.put("utmt", "event");
            //sb.append("&utmt=event");

            // Format 5(category*action*label*value)

            // Category and action
            eventSB.append("5(").append(data.getEventCategory()).append("*").append(data.getEventAction());

            // Label
            if(data.getEventLabel() != null){
                eventSB.append("*").append(data.getEventLabel());
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
                names.append(customVariable.getIndex()).append("!").append(customVariable.getName());
                // <index>!<value>*<index>!<value>...
                values.append(customVariable.getIndex()).append("!").append(customVariable.getValue());
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

        // Referral
        if (null != data.getUtmr()) {
            params.put("utmr", urlEncode(data.getUtmr()));
        } else {
            params.put("utmr", "-");
        }

        // Device related

        // Encoding
        if (null != this.deviceData.getEncoding()) {
            params.put("utmcs", this.deviceData.getEncoding());
        } else {
            params.put("utmcs", "-");
            //sb.append("&utmcs=-");
        }

        // Screen resolution
        if (null != this.deviceData.getScreenResolution()) {
            params.put("utmsr", this.deviceData.getScreenResolution());
        }

        // View port
        if (null != this.deviceData.getViewPortResolution()) {
            params.put("utmvp", this.deviceData.getViewPortResolution());
        }

        // color depth
        if (null != this.deviceData.getColorDepth()) {
            params.put("utmsc", this.deviceData.getColorDepth());
        }

        // Language
        if (null != this.deviceData.getUserLanguage()) {
            params.put("utmul", this.deviceData.getUserLanguage());
        }

        // Java enabled (always set to true)
        params.put("utmje", "1");

        // Flash version
        if(null != this.deviceData.getFlashVersion()) {
            params.put("utmfl", this.deviceData.getFlashVersion());
        }

        // utmu
        // TODO: Do not know what this is or if it is needed
        params.put("utmu", "qQ~");

        // Cookie
        int hostnameHash = hostnameHash(data.getHostName());
        int visitorId = this.visitorData.getVisitorId();
        long timestampFirst = this.visitorData.getTimestampFirst();
        long timestampPrevious = this.visitorData.getTimestampPrevious();
        int visits = this.visitorData.getVisits();
        long sessionStart = this.visitorData.getSession().getSessionStart();

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
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("URL encodes does not support character format");
            throw new IllegalArgumentException();
        }
    }


    // Host name hash
    private int hostnameHash(String hostname){
        return 999;
    }



//    public String buildURL(PageData data) {
//        LOG.debug("Build url for version:{}", this.getFormatVersion());
//
//        StringBuilder sb = new StringBuilder();
//
//        // Set base url
//        sb.append(BASE_URL);
//
//        // Tracking id
//        sb.append("?utmac=").append(urlEncode(configurationData.getTrackingID()));
//
//        // Version of the format
//        sb.append("&utmwv=").append(urlEncode(this.getFormatVersion()));
//
//        // Random int to avoid caching
//        sb.append("&utmn=").append(random.nextInt(Integer.MAX_VALUE));
//
//        // Random number for AdSense
//        sb.append("&utmhid=").append(random.nextInt(Integer.MAX_VALUE));
//
//        // Hostname
//        if(null != data.getHostName()) {
//            sb.append("&utmhn=").append(urlEncode(data.getHostName()));
//        }
//
//        // Page views
//
//        // Page title
//        if (null != data.getPageTitle()) {
//            sb.append("&utmdt=").append(urlEncode(data.getPageTitle()));
//        }
//
//        // Page url
//        if (null != data.getPageURL()) {
//            sb.append("&utmp=").append(urlEncode(data.getPageURL()));
//        }
//
//
//        // Events
//        if (null != data.getEventAction() && null != data.getEventCategory()) {
//            sb.append("&utmt=event");
//
//            // Format 5(category*action*label*value)
//
//            // Category and action
//            sb.append("&utme=5(").append(urlEncode(data.getEventCategory())).append("*").append(urlEncode(data.getEventAction()));
//
//            // Label
//            if(data.getEventLabel() != null){
//                sb.append("*").append(urlEncode(data.getEventLabel()));
//            }
//
//            // Value
//            if(null != data.getEventValue()) {
//                sb.append("*").append(data.getEventValue());
//            }
//
//            sb.append(")");
//        }
//        else if (data.getEventAction() != null || data.getEventCategory() != null) {
//            // Both category and action must be provided
//            throw new IllegalArgumentException("Event tracking must have both a category and an action");
//        }
//
//        // Custom vars
//        if (null != data.getCustomVariables() && data.getCustomVariables().size() > 0) {
//
//            StringBuilder names = new StringBuilder();
//            StringBuilder values = new StringBuilder();
//            StringBuilder scope = new StringBuilder();
//
//            Iterator<CustomVariable> iterator = data.getCustomVariables().iterator();
//            while (iterator.hasNext()) {
//                CustomVariable customVariable = iterator.next();
//
//                // <index>!<name>*<index>!<name>...
//                names.append(customVariable.getIndex()).append("!").append(customVariable.getName());
//                // <index>!<value>*<index>!<value>...
//                values.append(customVariable.getIndex()).append("!").append(customVariable.getValue());
//                // <index>!<scope>*<index>!<scope>..
//                scope.append(customVariable.getIndex()).append("!").append(customVariable.getScope().getValue());
//
//                if (iterator.hasNext()) {
//                    names.append("*");
//                    values.append("*");
//                    scope.append("*");
//                }
//            }
//
//            // Build the full string
//            // 8(<index>!<name>*<index>!<name>)9(<index>!<value>*<index>!<value>)11(<index>!<scope>*<index>!<scope>)
//            String customVars = urlEncode(String.format("8(%s)9(%s)11(%s)", names.toString(), values.toString(), scope.toString()));
//
//            sb.append(customVars);
//        }
//
//        // Referral
//        if (null != data.getUtmr()) {
//            sb.append("&utmr=").append(urlEncode(data.getUtmr()));
//        } else {
//            sb.append("&utmr=-");
//        }
//
//        // Device related
//
//        // Encoding
//        if (null != this.deviceData.getEncoding()) {
//            sb.append("&utmcs=").append(urlEncode(this.deviceData.getEncoding()));
//        } else {
//            sb.append("&utmcs=-");
//        }
//
//        // Screen resolution
//        if (null != this.deviceData.getScreenResolution()) {
//            sb.append("&utmsr=").append(urlEncode(this.deviceData.getScreenResolution()));
//        }
//
//        // View port
//        if (null != this.deviceData.getViewPortResolution()) {
//            sb.append("&utmvp=").append(urlEncode(this.deviceData.getViewPortResolution()));
//        }
//
//        // color depth
//        if (null != this.deviceData.getColorDepth()) {
//            sb.append("&utmsc=").append(urlEncode(this.deviceData.getColorDepth()));
//        }
//
//        // Language
//        if (null != this.deviceData.getUserLanguage()) {
//            sb.append("&utmul=").append(urlEncode(this.deviceData.getUserLanguage()));
//        }
//
//        // Java enabled (always set to true)
//        sb.append("&utmje=1");
//
//        // Flash version
//        if(null != this.deviceData.getFlashVersion()) {
//            sb.append("&utmfl=").append(urlEncode(this.deviceData.getFlashVersion()));
//        }
//
//        // Cookie
//        int hostnameHash = hostnameHash(data.getHostName());
//        int visitorId = this.visitorData.getVisitorId();
//        long timestampFirst = this.visitorData.getTimestampFirst();
//        long timestampPrevious = this.visitorData.getTimestampPrevious();
//        int visits = this.visitorData.getVisits();
//        long sessionStart = this.visitorData.getSession().getSessionStart();
//
//        // utmccn=(organic)|utmcsr=google|utmctr=snotwuh|utmcmd=organic
//        sb.append("&utmcc=__utma=").append(hostnameHash)
//                .append(".").append(visitorId)
//                .append(".").append(timestampFirst)
//                .append(".").append(timestampPrevious)
//                .append(".").append(sessionStart)
//                .append(".").append(visits)
//                .append(";+__utmz=").append(hostnameHash)
//                .append(".").append(timestampFirst)
//                .append(".1.1.utmcsr=").append(data.getUtmcsr())
//                .append("|utmccn=").append(data.getUtmccn())
//                .append("|utmcmd=").append(data.getUtmcmd())
//                .append(data.getUtmctr() != null ? "|utmctr=" + data.getUtmcsr() : "")
//                .append(data.getUtmcct() != null ? "|utmcct=" + data.getUtmcct() : "")
//                .append(";&");
//
//        return sb.toString();
//    }

}
