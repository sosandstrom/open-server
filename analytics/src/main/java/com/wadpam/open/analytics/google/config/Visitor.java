package com.wadpam.open.analytics.google.config;

/**
 * Contains information about a visitor.
 * @author mattiaslevin
 */
public class Visitor {

    /**
     * Unique visitor id.
     */
    private String visitorId;

    /**
     * First time user visited.
     * Only needed in the old legacy protocol when cookies was used for tracking sessions.
     */
    private long firstVisitTimestamp;

    /**
     * Previous time the user visited.
     * Only needed in the old legacy protocol when cookies was used for tracking sessions.
     */
    private long previousVisitTimestamp;

    /**
     * Number of numberOfVisits.
     * Only needed in the old legacy protocol when cookies was used for tracking sessions.
     */
    private int numberOfVisits;

    /**
     * Current session for the visitor
     */
    private Session session;


    /**
     * Create a visitor with a new session.
     * Session timestamp will be set to current system time
     * @param visitorId existing visitor id
     * @param firstVisitTimestamp first time the user visited, unix timestamp (seconds since 1970)
     * @param previousVisitTimestamp the previous time the user visited, unix timestamp (seconds since 1970)
     * @param numberOfVisits the number of numberOfVisits
     * @return a new visitor
     */
    public Visitor(String visitorId, long firstVisitTimestamp, long previousVisitTimestamp, int numberOfVisits) {
        this(visitorId, firstVisitTimestamp, previousVisitTimestamp, numberOfVisits, new Session());
    }


    /**
     * Constructor
     * @param visitorId unique visitor id
     * @param firstVisitTimestamp first time the user visited, unix timestamp (seconds since 1970)
     * @param previousVisitTimestamp the previous time the user visited, unix timestamp (seconds since 1970)
     * @param numberOfVisits number of numberOfVisits
     * @param session current session for the user
     */
    public Visitor(String visitorId, long firstVisitTimestamp, long previousVisitTimestamp, int numberOfVisits, Session session) {
        this.visitorId = visitorId;
        this.firstVisitTimestamp = firstVisitTimestamp;
        this.previousVisitTimestamp = previousVisitTimestamp;
        this.session = session;
        this.numberOfVisits = numberOfVisits;
    }


    /**
     * Start a new session for the visitor.
     */
    public void startNewSession() {
        // Set previous time stamp to current and current to now
        previousVisitTimestamp = session.getStartTimestamp();
        numberOfVisits++;
        session = new Session();
    }


    /**
     * Restart the current session.
     */
    public void resetCurrentSession() {
        session.restartSession();
    }


    // Setters and Getters
    public long getFirstVisitTimestamp() {
        return firstVisitTimestamp;
    }

    public void setFirstVisitTimestamp(long firstVisitTimestamp) {
        this.firstVisitTimestamp = firstVisitTimestamp;
    }

    public long getPreviousVisitTimestamp() {
        return previousVisitTimestamp;
    }

    public void setPreviousVisitTimestamp(long previousVisitTimestamp) {
        this.previousVisitTimestamp = previousVisitTimestamp;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public int getNumberOfVisits() {
        return numberOfVisits;
    }

    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
