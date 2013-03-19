package com.wadpam.open.analytics.google;

import com.wadpam.open.analytics.Tracker;
import com.wadpam.open.analytics.google.config.Application;
import com.wadpam.open.analytics.google.config.Device;
import com.wadpam.open.analytics.google.config.Property;
import com.wadpam.open.analytics.google.config.Visitor;
import com.wadpam.open.analytics.google.dispatcher.SynchDispatcher;
import com.wadpam.open.analytics.google.dispatcher.TrackingInfoDispatcher;
import com.wadpam.open.analytics.google.protocol.GoogleAnalyticsProtocol;
import com.wadpam.open.analytics.google.protocol.LegacyProtocol_v5_3_8;
import com.wadpam.open.analytics.google.trackinginfo.*;
import com.wadpam.open.analytics.google.trackinginfo.Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Server side Google Analytics tracker.
 *
 * @author mattiaslevin
 */
public class GoogleAnalyticsTracker implements Tracker {
    static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsTracker.class);

    /** The dispatcher strategy for sending events to GA */
    private TrackingInfoDispatcher eventDispatcher;

    /** Tracker configuration related data */
    private Property property;

    /** Describes the visitor */
    private Visitor visitor;

    /** Describes the device */
    private Device device;

    /** Described the application */
    private Application app;

    //** The url builder to use */
    private GoogleAnalyticsProtocol protocol;

    /** If debug is enabled events will only be printed to the console and not sent to GA */
    private boolean debug = false;


    /**
     * Create a tracker.
     * Each tracker will have a trackingId taken from GA, a visitor object describing the visitor
     * and a device object describing the device.
     * Depending on the application the tracker my be reused for the same user with device for
     * subsequent requests. The visitor and device object can be persisted for future tracking.
     * @param property configuration data
     * @param visitor visitor data
     * @param device device data
     * @param app application data
     */
    public GoogleAnalyticsTracker(Property property, Visitor visitor, Device device, Application app) {
        this(property, visitor, device, app, new LegacyProtocol_v5_3_8(), new SynchDispatcher());
    }


    /**
     * Create a tracker.
     * @param property configuration data
     * @param visitor visitor data
     * @param device device data
     * @param app application data. Optional
     * @param protocol the url builder used to create the absolute url to Google Analytics
     * @param dispatcher the event dispatcher
     */
    public GoogleAnalyticsTracker(Property property, Visitor visitor, Device device, Application app,
                                  GoogleAnalyticsProtocol protocol, TrackingInfoDispatcher dispatcher) {

        // Check input params
        if (null == property || null == visitor || null == device ||
                null == protocol || null == dispatcher) {
            LOG.error("Configuration, visitor, device and url builder must be provided");
            throw new IllegalArgumentException();
        }

        // Set configuration data
        this.property = property;
        this.visitor = visitor;
        this.device = device;
        this.app = app;

        // In the future we can support different URL builders as Google evolve their protocols.
        // Currently two different url builder are supported, legacy (v5.3.8) and the new
        // Measurement protocol.
        this.protocol = protocol;

        // Set the default event dispatcher strategy
        this.eventDispatcher = dispatcher;
    }


    /**
     * Start a new session.
     */
    public void startNewSession() {
        visitor.startNewSession();
    }


    // Tracking a page views

    /**
     * Track a page view without setting referrer
     */
    public void trackPageView(String hostName, String path, String title) {
        this.trackPageView(hostName, path, title, null, null, null);
    }

    /**
     * Track page view
     */
    @Override
    public void trackPageView(String hostName,
                              String path,
                              String title,
                              String referrerSite,
                              String referrerPage,
                              List<CustomVariable> customVariables) {
        LOG.debug("Track page view for path:{} and tile:{}", path, title);

        // Populate the request data
        TrackingInfo trackingInfo = new TrackingInfo();

        // Content info
        ContentInfo contentInfo = new ContentInfo(hostName, path, title, null);
        trackingInfo.setContentInfo(contentInfo);

        // Traffic source
        if (null != referrerSite && null != referrerPage) {
            TrafficSource trafficSource = new TrafficSource();
            trafficSource.setReferrer(referrerSite, referrerPage); // TODO
            trackingInfo.setTrafficSource(trafficSource);
        }

        // Custom variables
        trackingInfo.setCustomVariables(customVariables);

        sendRequest(trackingInfo);
    }


    // Track events

    /**
     * Track without label and value
     */
    public void trackEvent(String category, String action) {
        this.trackEvent(category, action, null, null, null);
    }

    /**
     * Track without custom variable
     */
    public void trackEvent(String category, String action, String label, int value) {
        this.trackEvent(category, action, label, value, null);
    }

    /**
     * Track event
     */
    @Override
    public void trackEvent(String category,
                           String action,
                           String label,
                           Integer value,
                           List<CustomVariable> customVariables) {
        LOG.debug("Track event for category:{} and action:{}", category, action);

        // Populate the request data
        TrackingInfo trackingInfo = new TrackingInfo();

        // Create event
        Event event = new Event(category, action, label, value);
        trackingInfo.setEvent(event);

        // Custom variables
        trackingInfo.setCustomVariables(customVariables);

        sendRequest(trackingInfo);
    }


    /**
     * Track screen views in an app.
     * If you are tracking activities in an app (iOS and Android) use this methods
     * over tracking page views.
     *
     * Screen views are only supported in Measurement protocol and the Property must
     * to configured for app tracking in the Google Analytics admin web site.
     * @param screenName the screen name being tracked
     */
    public void screenView(String screenName) {

        // Populate the request data
        TrackingInfo trackingInfo = new TrackingInfo();

        // Content info
        ContentInfo contentInfo = new ContentInfo();
        contentInfo.setContentDescription(screenName);
        trackingInfo.setContentInfo(contentInfo);

        sendRequest(trackingInfo);
    }


    /**
     * Track an exception.
     * @param description The description of the exception.
     *                    Only limited length is supported, make sure important and
     *                    relevant information is first on the description.
     *                    It is probably not a good idea to track a full stack trace.
     * @param isFatal the the exception fatal
     */
    public void exception(String description, boolean isFatal) {

        // Populate the request data
        TrackingInfo trackingInfo = new TrackingInfo();

        Exception exception = new Exception(description, isFatal);
        trackingInfo.setException(exception);

        sendRequest(trackingInfo);
    }


    // Create the request URL and make the request
    private void sendRequest(TrackingInfo trackingInfo) {
        LOG.debug("Send request to GA:{}", trackingInfo);

        // Build request params
        final String params = protocol.buildParams(property, visitor, device, app, trackingInfo);

        // Check if in debug mode
        if (debug) {
            // Print to console
            LOG.info("{}?{}", protocol.baseUrl(), params);
            return;
        }

        try {
            // Make the request
            LOG.debug("Make request with url:{}", params);
            eventDispatcher.dispatch(protocol.baseUrl(), params, device.getUserAgent(), device.getRemoteAddress());
        } catch (URISyntaxException e) {
            LOG.error("Not possible to create URI object from URL with reason:{}", e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }



    // Setters and getters
    public TrackingInfoDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public void setEventDispatcher(TrackingInfoDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public GoogleAnalyticsProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(GoogleAnalyticsProtocol protocol) {
        this.protocol = protocol;
    }

    public Application getApp() {
        return app;
    }

    public void setApp(Application app) {
        this.app = app;
    }
}
