package com.wadpam.open.analytics.google;

import java.util.List;

/**
 * Base class for GA requests containing common parameters.
 * @author mattiaslevin
 */
public class Page {

    // Page views

    /** Page title */
    private String pageTitle = null;

    /** Host name */
    private String hostName = null;

    /** Page URL */
    private String pageURL = null;


    // Events

    /** Event category */
    private String eventCategory = null;

    /** Event action */
    private String eventAction = null;

    /** Event label */
    private String eventLabel = null;

    /** Event value */
    private Integer eventValue = null;


    /** Custom variables */
    private List<CustomVariable> customVariables;

    /**
     *  utmcsr
     *  Identifies a search engine, newsletter name, or other source specified in the
     *  utm_source query parameter See the �Marketing Campaign Tracking�
     *  section for more information about query parameters.
     */
    private String utmcsr = "(direct)";

    /**
     * utmccn
     * Stores the campaign name or value in the utm_campaign query parameter.
     */
    private String utmccn = "(direct)";

    /**
     * utmctr
     * Identifies the keywords used in an organic search or the value in the utm_term query parameter.
     */
    private String utmctr = null;

    /**
     * utmcmd
     * A campaign medium or value of utm_medium query parameter.
     */
    private String utmcmd = "(none)";

    /**
     * utmcct
     * Campaign content or the content of a particular ad (used for A/B testing)
     */
    private String utmcct = null;


    /**
     * Referral, complete url
     */
    private String utmr = null;


    /**
     * The value from utm_content query parameter.
     *
     * referal:
     * utmcsr=forums.jinx.com|utmcct=/topic.asp|utmcmd=referral
     * utmcsr=rolwheels.com|utmccn=(referral)|utmcmd=referral|utmcct=/rol_dhuez_wheels.php
     * search:
     * utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=rol%20wheels
     * utmcsr%3D(direct)%7Cutmccn%D(direct)%7utmcmd%3D(none)
     */

    /** Set referrer */
    public void setReferrer(String site, String page){
        utmcmd = "referral";
        utmcct = page;
        utmccn = "(referral)";
        utmcsr = site;
        utmctr = null;
    }

    /** Set search referrer */
    public void setSearchReferrer(String searchSource, String searchKeywords){
        utmcsr = searchSource;
        utmctr = searchKeywords;
        utmcmd = "organic";
        utmccn = "(organic)";
        utmcct = null;
    }

    // Setters and getter
    public String getEventAction() {
        return eventAction;
    }

    public void setEventAction(String eventAction) {
        this.eventAction = eventAction;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getEventLabel() {
        return eventLabel;
    }

    public void setEventLabel(String eventLabel) {
        this.eventLabel = eventLabel;
    }

    public Integer getEventValue() {
        return eventValue;
    }

    public void setEventValue(Integer eventValue) {
        this.eventValue = eventValue;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageURL() {
        return pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public String getUtmccn() {
        return utmccn;
    }

    public void setUtmccn(String utmccn) {
        this.utmccn = utmccn;
    }

    public String getUtmcct() {
        return utmcct;
    }

    public void setUtmcct(String utmcct) {
        this.utmcct = utmcct;
    }

    public String getUtmcmd() {
        return utmcmd;
    }

    public void setUtmcmd(String utmcmd) {
        this.utmcmd = utmcmd;
    }

    public String getUtmcsr() {
        return utmcsr;
    }

    public void setUtmcsr(String utmcsr) {
        this.utmcsr = utmcsr;
    }

    public String getUtmctr() {
        return utmctr;
    }

    public void setUtmctr(String utmctr) {
        this.utmctr = utmctr;
    }

    public String getUtmr() {
        return utmr;
    }

    public void setUtmr(String utmr) {
        this.utmr = utmr;
    }

    public List<CustomVariable> getCustomVariables() {
        return customVariables;
    }

    public void setCustomVariables(List<CustomVariable> customVariables) {
        this.customVariables = customVariables;
    }
}
