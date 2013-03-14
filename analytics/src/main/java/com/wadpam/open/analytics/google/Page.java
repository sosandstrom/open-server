package com.wadpam.open.analytics.google;

import java.util.List;

/**
 * Base class for GA requests containing common parameters.
 * @author mattiaslevin
 */
public class Page {

    // Page views

    /** Page title */
    private String documentTitle = null;

    /**
     * The full url of the page on which content reside
     * Setting the host name and page path will override this value
     * Added in universal analytics.
     */
    private String documentUrl = null;

    /** Host name */
    private String hostName = null;

    /** Page URL */
    private String documentPath = null;

    /**
     * Content description
     * Added in universal analytics.
     */
    private String contentDescription = null;


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
     *  utm_source query parameter See the Marketing Campaign Tracking
     *  section for more information about query parameters.
     */
    private String campaignSource = "(direct)";

    /**
     * utmccn
     * Stores the campaign name or value in the utm_campaign query parameter.
     */
    private String campaignName = "(direct)";

    /**
     * utmctr
     * Identifies the keywords used in an organic search or the value in the utm_term query parameter.
     */
    private String campaignKeyword = null;

    /**
     * utmcmd
     * A campaign medium or value of utm_medium query parameter.
     */
    private String campaignMedium = "(none)";

    /**
     * utmcct
     * Campaign content or the content of a particular ad (used for A/B testing)
     */
    private String campaignContent = null;

    /**
     * Campaign id.
     * Added in universal analytics.
     */
    private String campaignId = null;


    /**
     * Referral, complete url
     */
    private String referrer = null;


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
        campaignMedium = "referral";
        campaignContent = page;
        campaignName = "(referral)";
        campaignSource = site;
        campaignKeyword = null;
    }

    /** Set search referrer */
    public void setSearchReferrer(String searchSource, String searchKeywords){
        campaignSource = searchSource;
        campaignKeyword = searchKeywords;
        campaignMedium = "organic";
        campaignName = "(organic)";
        campaignContent = null;
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

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignContent() {
        return campaignContent;
    }

    public void setCampaignContent(String campaignContent) {
        this.campaignContent = campaignContent;
    }

    public String getCampaignMedium() {
        return campaignMedium;
    }

    public void setCampaignMedium(String campaignMedium) {
        this.campaignMedium = campaignMedium;
    }

    public String getCampaignSource() {
        return campaignSource;
    }

    public void setCampaignSource(String campaignSource) {
        this.campaignSource = campaignSource;
    }

    public String getCampaignKeyword() {
        return campaignKeyword;
    }

    public void setCampaignKeyword(String campaignKeyword) {
        this.campaignKeyword = campaignKeyword;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public List<CustomVariable> getCustomVariables() {
        return customVariables;
    }

    public void setCustomVariables(List<CustomVariable> customVariables) {
        this.customVariables = customVariables;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }
}
