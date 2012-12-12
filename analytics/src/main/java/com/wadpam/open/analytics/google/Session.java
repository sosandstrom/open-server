package com.wadpam.open.analytics.google;

/**
 * information about a single session.
 * @author mattiaslevin
 */
public class Session {

    /** Session start time stamp, unix timestamp (seconds since 1970) */
    private long sessionStart;

    public Session() {
        this.sessionStart = now();
    }

    // Restart the current session
    public long resetCurrentSession() {
        this.sessionStart = now();
        return this.sessionStart;
    }

    // Return now
    private static long now() {
        return System.currentTimeMillis() / 1000L;
    }

    // Setters and getters
    public long getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(long sessionStart) {
        this.sessionStart = sessionStart;
    }
}
