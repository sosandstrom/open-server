package com.wadpam.open.analytics.google.trackinginfo;

/**
 * An Exception being tracked.
 *
 * @author mattiaslevin
 */
public class Exception {

    /**
     * Crete exception, non fatal.
     * @param description the description
     */
    public Exception(String description) {
        this.description = description;
        fatal = false;
    }

    /**
     * Create exception
     * @param description the description
     * @param fatal is fatal
     */
    public Exception(String description, boolean fatal) {
        this.description = description;
        this.fatal = fatal;
    }

    /**
     * Exception description.
     */
    private String description;

    /**
     * Is this a fatal exception or not.
     */
    private boolean fatal;

    // Setters and getters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description.getBytes().length > 150) {
            // Max 150 bytes
            description = new String(description.getBytes(), 0, 150);
        }
        this.description = description;
    }

    public boolean isFatal() {
        return fatal;
    }

    public void setFatal(boolean fatal) {
        this.fatal = fatal;
    }
}
