package com.wadpam.open.analytics.google.trackinginfo;

import java.util.List;
import java.util.Map;

/**
 * Class for keeping all tracking info associated with a single tracking info event.
 *
 * @author mattiaslevin
 */
public class TrackingInfo {

    /**
     * The delta time in milliseconds since the actual event took place.
     *
     * Added in the Measurement protocol.
     */
    private Integer queueTime;


    /**
     * Content / document information.
     * Mandatory when tracking page and screen views.
     */
    private ContentInfo contentInfo;


    /**
     * Track an event
     */
    private Event event;


    /**
     * The traffic source the brought the hit to this page.
     * Always try to set is applicable and information if available.
     */
    private TrafficSource trafficSource;


    /**
     * Custom variables.
     * Only used in the old legacy protocol. Replaced by Custom Dimension and
     * Custom Metric in Measurement protocol.
     */
    @Deprecated
    private List<CustomVariable> customVariables;


    /**
     * Custom Dimension is a way to segment the tracing info in different dimensions,
     * e.g. gender, age, membership etc.
     * Each dimension have a unique index and must be configured in Google Analytics
     * web admin interface.
     *
     * Added in the Measurement protocol.
     */
    private Map<Integer, String> customDimension;


    /**
     * Custom Metric is a way to measure a metric for a special dimension.
     * Each metric have a unique index and must be configured in Google Analytics
     * web admin interface.
     *
     * Added in the Measurement protocol.
     */
    private Map<Integer, Integer> customMetric;


    /**
     * A e-commerce transaction being tracked.
     * Related to items.
     */
    private Transaction transaction;


    /**
     * A tracked social interaction, e.g. Facebook like
     */
    private SocialInteraction socialInteraction;


    /**
     * A timing being tracked, e.g. download time, server response time etc
     */
    private Timing timing;


    /**
     * An exception being tracked,
     */
    private Exception exception;


    // Setters and getters
    public ContentInfo getContentInfo() {
        return contentInfo;
    }

    public void setContentInfo(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
    }

    public Map<Integer, String> getCustomDimension() {
        return customDimension;
    }

    public void setCustomDimension(Map<Integer, String> customDimension) {
        this.customDimension = customDimension;
    }

    public Map<Integer, Integer> getCustomMetric() {
        return customMetric;
    }

    public void setCustomMetric(Map<Integer, Integer> customMetric) {
        this.customMetric = customMetric;
    }

    public List<CustomVariable> getCustomVariables() {
        return customVariables;
    }

    public void setCustomVariables(List<CustomVariable> customVariables) {
        this.customVariables = customVariables;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Integer getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(Integer queueTime) {
        this.queueTime = queueTime;
    }

    public SocialInteraction getSocialInteraction() {
        return socialInteraction;
    }

    public void setSocialInteraction(SocialInteraction socialInteraction) {
        this.socialInteraction = socialInteraction;
    }

    public Timing getTiming() {
        return timing;
    }

    public void setTiming(Timing timing) {
        this.timing = timing;
    }

    public TrafficSource getTrafficSource() {
        return trafficSource;
    }

    public void setTrafficSource(TrafficSource trafficSource) {
        this.trafficSource = trafficSource;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
