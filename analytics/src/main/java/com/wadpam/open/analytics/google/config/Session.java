package com.wadpam.open.analytics.google.config;

/**
 * Information about a single session.
 *
 * @author mattiaslevin
 */
public class Session {

    /**
     * Session start time stamp, unix timestamp (seconds since 1970)
     */
    private long startTimestamp;


    /**
     * Indicates if this is a new session.
     * This is needed to track session start and stop in the new Measurement
     * Protocol.
     */
    private boolean trackStartSession;


    /**
     * Create a new session.
     * Sets the trackStartSession property to indicate that this is a new session
     * in the next tracking information sent to Google Analytics.
     */
    public Session() {
        startTimestamp = now();
        trackStartSession = true;
    }

    /**
     * Restart the current session (not the same as creating a new session).
     */
    public long restartSession() {
        startTimestamp = now();
        return startTimestamp;
    }

    /**
     * Stop the session.
     */
    public void stopSession() {
        startTimestamp = -1;
        trackStartSession = false;
    }

    // Return now
    private static long now() {
        return System.currentTimeMillis() / 1000L;
    }


    // Setters and getters
    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public boolean isTrackStartSession() {
        return trackStartSession;
    }

    public void setTrackStartSession(boolean trackStartSession) {
        this.trackStartSession = trackStartSession;
    }
}
