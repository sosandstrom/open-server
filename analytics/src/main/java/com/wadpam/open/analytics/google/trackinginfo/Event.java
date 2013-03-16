package com.wadpam.open.analytics.google.trackinginfo;

/**
 * An Event being tracked.
 *
 * @author mattiaslevin
 */
public class Event {

    /**
     * Create an event for tracking.
     * @param category category name
     * @param action action name
     */
    public Event(String category, String action) {
        this(category, action, null, null);
    }

    /**
     * Create an event for tracking.
     * @param category category name
     * @param action action name
     * @param label label
     */
    public Event(String category, String action, String label) {
        this(category, action, label, null);
    }

    /**
     * Create an event for tracking.
     * @param category category name
     * @param action action name
     * @param label label
     * @param value event value
     */
    public Event(String category, String action, String label, Integer value) {
        this.action = action;
        this.category = category;
        this.label = label;
        this.value = value;
    }

    /**
     * Event category
     */
    private String category;

    /**
     * Event action
     */
    private String action;

    /**
     * Event label
     */
    private String label;

    /**
     * Event value
     */
    private Integer value;


    // Setters and getters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
