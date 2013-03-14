package com.wadpam.open.analytics.google;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Server side Google Analytics tracker.
 * @author mattiaslevin
 */
public class GoogleAnalyticsTracker extends AbstractTracker {
    static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsTracker.class);

    /** The dispatcher strategy for sending events to GA */
    private EventDispatcher eventDispatcher;

    /** Tracker configuration related data */
    private TrackerConfiguration trackerConfiguration;

    /** Describes the visitor */
    private Visitor visitor;

    /** Describes the device */
    private Device device;

    /** Described the application */
    private Application app;

    //** The url builder to use */
    private URLBuilder urlBuilder;

    /** If debug is enabled events will only be printed to the console and not sent to GA */
    private boolean debug = false;


    /**
     * Create a tracker.
     * Each tracker will have a trackingId taken from GA, a visitor object describing the visitor
     * and a device object describing the device.
     * Depending on the application the tracker my be reused for the same user with device for
     * subsequent requests. The visitor and device object can be persisted for future tracking.
     * @param trackerConfig configuration data
     * @param visitor visitor data
     * @param device device data
     * @param app application data
     */
    public GoogleAnalyticsTracker(TrackerConfiguration trackerConfig, Visitor visitor, Device device, Application app) {
        this(trackerConfig, visitor, device, app, new URLBuilderV5_3_8(), new SynchronousEventDispatcher());
    }


    /**
     * Create a tracker.
     * @param trackerConfig configuration data
     * @param visitor visitor data
     * @param device device data
     * @param app application data. Optional
     * @param urlBuilder the url builder used to create the absolute url to Google Analytics
     * @param eventDispatcher the event dispatcher
     */
    public GoogleAnalyticsTracker(TrackerConfiguration trackerConfig, Visitor visitor, Device device, Application app,
                                  URLBuilder urlBuilder, EventDispatcher eventDispatcher) {

        // Check input params
        if (null == trackerConfig || null == visitor || null == device ||
                null == urlBuilder || null == eventDispatcher) {
            LOG.error("Configuration, visitor, device and url builder must be provided");
            throw new IllegalArgumentException();
        }

        // Set configuration data
        this.trackerConfiguration = trackerConfig;
        this.visitor = visitor;
        this.device = device;
        this.app = app;

        // In the future we can support different URL builders as Google evolve their protocol
        // A new universal analytic protocol is in the pipe but not yet publicly available.
        this.urlBuilder = urlBuilder;

        // Set the default event dispatcher strategy
        this.eventDispatcher = eventDispatcher;
    }



        // Start a new session
    public void startNewSession() {
        visitor.startNewSession();
    }

    // Track a page view
    @Override
    public void trackPageView(String pathUrl,
                              String pageTitle,
                              String hostName,
                              String referrerPage,
                              String referrerSite,
                              List<CustomVariable> customVariables) {
        LOG.debug("Track page view for url:{} and tile:{}", pathUrl, pageTitle);

        // Populate the request data
        Page data = new Page();
        data.setDocumentPath(pathUrl);
        data.setDocumentTitle(pageTitle);
        data.setHostName(hostName);
        if (null != referrerSite && null != referrerPage) {
            data.setReferrer(referrerSite, referrerPage);
        }
        data.setCustomVariables(customVariables);

        sendRequest(data);
    }


    // Track event
    @Override
    public void trackEvent(String category,
                           String action,
                           String label,
                           Integer value,
                           List<CustomVariable> customVariables) {
        LOG.debug("Track event for category:{} and action:{}", category, action);

        // Populate the request data
        Page data = new Page();
        data.setEventCategory(category);
        data.setEventAction(action);
        data.setEventLabel(label);
        data.setEventValue(value);
        data.setCustomVariables(customVariables);

        sendRequest(data);
    }


    // Generate the request URL and make the request
    private void sendRequest(Page page) {
        LOG.debug("Send request to GA:{}", page);

        // Build request URL
        final String url = urlBuilder.buildURL(trackerConfiguration, visitor, device, app, page);

        // Check if in debug mode
        if (debug) {
            // Only print to console
            LOG.info("{}", url);
            return;
        }

        try {
            // Make the request
            LOG.debug("Make request with url:{}", url);
            eventDispatcher.dispatch(new URI(url), device.getUserAgent(), device.getRemoteAddress());
        } catch (URISyntaxException e) {
            LOG.error("Not possible to create URI object from URL with reason:{}", e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }


    // Setters and getters
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public TrackerConfiguration getTrackerConfiguration() {
        return trackerConfiguration;
    }

    public void setTrackerConfiguration(TrackerConfiguration trackerConfiguration) {
        this.trackerConfiguration = trackerConfiguration;
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

    public URLBuilder getUrlBuilder() {
        return urlBuilder;
    }

    public void setUrlBuilder(URLBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    public Application getApp() {
        return app;
    }

    public void setApp(Application app) {
        this.app = app;
    }
}
