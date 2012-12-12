package com.wadpam.open.analytics.google;

/**
 * Contains information about a visitor.
 * @author mattiaslevin
 */
public class Visitor {

    /** Unique visitor id */
    private int visitorId;

    /** First time user visited */
    private long timestampFirst;

    /** Previous time the user visited */
    private long timestampPrevious;

    /** Number of visits */
    private int visits;

    /** Current session for the visitor */
    private Session session;


    /**
     * Create a visitor with a new session.
     * Session timestamp will be set to current system time
     * @param visitorId existing visitor id
     * @param timestampFirst first time the user visited, unix timestamp (seconds since 1970)
     * @param timestampPrevious the previous time the user visited, unix timestamp (seconds since 1970)
     * @param visits the number of visits
     * @return a new visitor
     */
    public static Visitor visitorWithNewSession(int visitorId, long timestampFirst, long timestampPrevious, int visits) {
        return new Visitor(visitorId, timestampFirst, timestampPrevious, visits, new Session());
    }


    /**
     * Constructor
     * @param visitorId unique visitor id
     * @param timestampFirst first time the user visited, unix timestamp (seconds since 1970)
     * @param timestampPrevious the previous time the user visited, unix timestamp (seconds since 1970)
     * @param visits number of visits
     * @param session current session for the user
     */
    public Visitor(int visitorId, long timestampFirst, long timestampPrevious, int visits, Session session) {
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
        this.session = new Session();
    }


    // Restart the current session
    public void resetCurrentSession() {
        this.session.resetCurrentSession();
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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
