package com.wadpam.open.analytics;

/**
 * information about a single session.
 * @author mattiaslevin
 */
public class SessionData {


    /** Session start time stamp */
    private long sessionStart;


    public SessionData() {
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
