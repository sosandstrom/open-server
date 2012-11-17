package com.wadpam.open.analytics;

import java.security.SecureRandom;

/**
 * Contains information about a visitor.
 * @author mattiaslevin
 */
public class VisitorData {

    /** Unique visitor id */
    private int visitorId;

    /** First time user visited */
    private long timestampFirst;

    /** Previous time the user visited */
    private long timestampPrevious;

    /** Number of visits */
    private int visits;

    /** Current session from the visitor */
    private SessionData session;


    /**
     * Create a first time visitor.
     * Generate a visitor id and and set correct time stamps.
     * @return a new visitor
     */
    public static VisitorData createFirstTimeVisitor() {
        int visitorId = (new SecureRandom().nextInt() & 0x7FFFFFFF);
        long now = now();
        return new VisitorData(visitorId, now, now, 1, new SessionData());
    }


    /**
     * Create a visitor with a new session.
     * Session timestamp will be set to current system time
     * @param visitorId existing visitor id
     * @param timestampFirst first time the user visited
     * @param timestampPrevious the previous time the user visited
     * @param visits the number of visits
     * @return a new visitor
     */
    public static VisitorData createVisitorWithNewSession(int visitorId, long timestampFirst, long timestampPrevious, int visits) {
        return new VisitorData(visitorId, timestampFirst, timestampPrevious, visits, new SessionData());
    }


    /**
     * Constructor
     * @param visitorId unique visitor id
     * @param timestampFirst first time the user visited
     * @param timestampPrevious the previous time the user visited
     * @param visits number of visits
     * @param session current session for the user
     */
    public VisitorData(int visitorId, long timestampFirst, long timestampPrevious, int visits, SessionData session) {
        this.visitorId = visitorId;
        this.timestampFirst = timestampFirst;
        this.timestampPrevious = timestampPrevious;
        this.session = session;
        this.visits = visits;
    }


    // Start new session for the visitor
    // Set previous time stamp to current and current to now
    public void startNewSession() {
        this.timestampPrevious = this.session.getSessionStart();
        this.visits++;
        this.session = new SessionData();
    }


    // Restart the current session
    public void resetCurrentSession() {
        this.session.resetCurrentSession();
    }

    // Return now
    private static long now() {
        return System.currentTimeMillis() / 1000L;
    }


    // Setters and Getters
    public long getTimestampFirst() {
        return timestampFirst;
    }

    public void setTimestampFirst(long timestampFirst) {
        this.timestampFirst = timestampFirst;
    }

    public long getTimestampPrevious() {
        return timestampPrevious;
    }

    public void setTimestampPrevious(long timestampPrevious) {
        this.timestampPrevious = timestampPrevious;
    }

    public int getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(int visitorId) {
        this.visitorId = visitorId;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public SessionData getSession() {
        return session;
    }

    public void setSession(SessionData session) {
        this.session = session;
    }
}
