package com.wadpam.open.analytics.google;

import com.wadpam.open.analytics.OpenAnalyticsTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Server side Google Analytics tracker.
 * @author mattiaslevin
 */
public class GAOpenAnalyticsTracker extends OpenAnalyticsTracker {
    static final Logger LOG = LoggerFactory.getLogger(GAOpenAnalyticsTracker.class);

    /** The dispatcher strategy for sending events to GA */
    private EventDispatcher eventDispatcher;

    /** Tracker configuration related data */
    private TrackerConfiguration trackerConfiguration;

    /** Describes the visitor */
    private Visitor visitor;

    /** Describes the device */
    private Device device;

    //** The url builder to use */
    private AbstractURLBuilder urlBuilder;

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
     */
    public GAOpenAnalyticsTracker(TrackerConfiguration trackerConfig, Visitor visitor, Device device) {

        // Check input params
        if (null == trackerConfig || null == visitor || null == device) {
            LOG.error("Configuration, visitor and device must be provided");
            throw new IllegalArgumentException();
        }

        // Set configuration data
        this.trackerConfiguration = trackerConfig;
        this.visitor = visitor;
        this.device = device;

        // In the future we can support different URL builders as Google evolve their protocol
        // A new universal analytic protocol is in the pipe but not yet publicly available.
        this.urlBuilder = new URLBuilderV5_3_8(trackerConfig, visitor, device);

        // Set the default event dispatcher strategy
        this.eventDispatcher = new SynchronousEventDispatcher(device.getUserAgent());
    }


    // Start a new session
    public void startNewSession() {
        this.visitor.startNewSession();
    }

    // Track a page view
    @Override
    public void trackPageView(String pageURL, String pageTitle, String hostName,
                              String referrerPage, String referrerSite,
                              List<CustomVariable> customVariables) {
        LOG.debug("Track page view for url:{} and tile:{}", pageURL, pageTitle);

        // Populate the request data
        Page data = new Page();
        data.setPageURL(pageURL);
        data.setPageTitle(pageTitle);
        data.setHostName(hostName);
        if (null != referrerSite && null != referrerPage) {
            data.setReferrer(referrerSite, referrerPage);
        }
        data.setCustomVariables(customVariables);

        this.sendRequest(data);
    }


    // Track event
    @Override
    public void trackEvent(String category, String action, String label, Integer value,
                           List<CustomVariable> customVariables) {
        LOG.debug("Track event for category:{} and action:{}", category, action);

        // Populate the request data
        Page data = new Page();
        data.setEventCategory(category);
        data.setEventAction(action);
        data.setEventLabel(label);
        data.setEventValue(value);
        data.setCustomVariables(customVariables);

        this.sendRequest(data);
    }


    // Generate the request URL and make the request
    private void sendRequest(Page page) {
        LOG.debug("Send request to GA:{}", page);

        // Build request URL
        final String url = this.urlBuilder.buildURL(page);

        // Check if in debug mode
        if (this.debug) {
            // Only print to console
            LOG.info("{}", url);
            return;
        }

        try {
            // Make the request
            LOG.debug("Make request with url:{}", url);
            this.eventDispatcher.dispatch(new URI(url));
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

    public AbstractURLBuilder getUrlBuilder() {
        return urlBuilder;
    }

    public void setUrlBuilder(AbstractURLBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }
}
