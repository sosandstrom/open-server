package com.wadpam.open.analytics.google.trackinginfo;

/**
 * The source of the tracking information - referrer, campaign, Google Ad Words, Google Display Ad.
 *
 * @author mattiaslevin
 */
public class TrafficSource {


    public TrafficSource() {
    }

    /**
     * Traffic source is a referrer
     * @param documentReferrer the referral absolute url
     */
    public TrafficSource(String documentReferrer) {
        this.documentReferrer = documentReferrer;
    }

    /**
     * Traffic source is a campaign
     * @param campaignId campaign id
     * @param campaignName campaign name
     * @param campaignSource campaign source
     * @param campaignMedium campaign medium
     */
    public TrafficSource(String campaignId, String campaignName, String campaignSource, String campaignMedium) {
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.campaignSource = campaignSource;
        this.campaignMedium = campaignMedium;
    }

    /**
     * The referral source that brought traffic to a web site.
     * The full url, http://foo.com/home
     */
    private String documentReferrer;

    /**
     * The name of the campaign.
     */
    private String campaignName;

    /**
     * The source of the campaign.
     */
    private String campaignSource;

    /**
     * The campaign medium used, e.g. email
     */
    private String campaignMedium;

    /**
     * The campaign keyword.
     */
    private String campaignKeyword;

    /**
     * The campaign content.
     */
    private String campaignContent;

    /**
     * Campaign id.
     *
     * Added in the Measurement protocol.
     */
    private String campaignId;

    /**
     * Specifies the Google AdWords Id.
     *
     * Added in the Measurement protocol.
     */
    private String googleAdWordsId;

    /**
     * Specifies the Google Display Ads Id
     *
     * Added in the Measurement protocol.
     */
    private String googleDisplayAdsId;


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

    /**
     * Set referrer.
     * Legacy protocol.
     */
    @Deprecated
    public void setReferrer(String site, String page){
        campaignSource = site;
        campaignName = "(referral)";
        campaignMedium = "referral";
        campaignContent = page;
        campaignKeyword = null;
    }

    /**
     * Set search referrer
     * Legacy protocol.
     */
    @Deprecated
    public void setSearchReferrer(String searchSource, String searchKeywords){
        campaignSource = searchSource;
        campaignName = "(organic)";
        campaignMedium = "organic";
        campaignContent = null;
        campaignKeyword = searchKeywords;
    }


    // Setters and getters
    public String getCampaignContent() {
        return campaignContent;
    }

    public void setCampaignContent(String campaignContent) {
        this.campaignContent = campaignContent;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignKeyword() {
        return campaignKeyword;
    }

    public void setCampaignKeyword(String campaignKeyword) {
        this.campaignKeyword = campaignKeyword;
    }

    public String getCampaignMedium() {
        return campaignMedium;
    }

    public void setCampaignMedium(String campaignMedium) {
        this.campaignMedium = campaignMedium;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignSource() {
        return campaignSource;
    }

    public void setCampaignSource(String campaignSource) {
        this.campaignSource = campaignSource;
    }

    public String getDocumentReferrer() {
        return documentReferrer;
    }

    public void setDocumentReferrer(String documentReferrer) {
        this.documentReferrer = documentReferrer;
    }

    public String getGoogleAdWordsId() {
        return googleAdWordsId;
    }

    public void setGoogleAdWordsId(String googleAdWordsId) {
        this.googleAdWordsId = googleAdWordsId;
    }

    public String getGoogleDisplayAdsId() {
        return googleDisplayAdsId;
    }

    public void setGoogleDisplayAdsId(String googleDisplayAdsId) {
        this.googleDisplayAdsId = googleDisplayAdsId;
    }
}
