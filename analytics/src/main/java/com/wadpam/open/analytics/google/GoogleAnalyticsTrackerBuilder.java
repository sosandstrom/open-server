package com.wadpam.open.analytics.google;

import com.wadpam.open.analytics.Tracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * A builder class that help build a Google Analytics tracker using a fluent builder API and
 * default values when possible.
 * @author mattiaslevin
 */
public class GoogleAnalyticsTrackerBuilder {
    static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsTrackerBuilder.class);

    private TrackerConfiguration trackerConfiguration;
    private Device device;
    private Visitor visitor;
    private URLBuilder urlBuilder;
    private EventDispatcher eventDispatcher;


    // Default constructor
    public GoogleAnalyticsTrackerBuilder() {
    }


    /**
     * Set tracker configuration.
     * @param trackerConfiguration tracking configuration
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withTrackingConfiguration(TrackerConfiguration trackerConfiguration) {
        this.trackerConfiguration = trackerConfiguration;
        return this;
    }

    /**
     * Create a tracker configuration from a tracking code.
     * @param trackingCode tracking code
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withTrackingCode(String trackingCode) {
        trackerConfiguration = new TrackerConfiguration(trackingCode, trackingCode);
        return this;
    }

    /**
     * Set device.
     * @param device device
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withDevice(Device device) {
        this.device = device;
        return this;
    }

    /**
     * Create a device with iPhone (iOS5) characteristics.
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withDefaultiPhoneDevice() {
        this.device = Device.defaultiPhoneDevice();
        return this;
    }

    /**
     * Create a device with iPad (iOS5) characteristics.
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withDefaultiPadDevice() {
        this.device = Device.defaultiPadDevice();
        return this;
    }

    /**
     * Create a device from the incoming request.
     * @param request incoming request
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withRequest(HttpServletRequest request) {
        this.device = Device.defaultDevice(request);
        return this;
    }

    /**
     * Set visitor.
     * @param visitor visitor
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withVisitor(Visitor visitor) {
        this.visitor = visitor;
        return this;
    }

    /**
     * Create a visitor with first and previous visit set to now and number of visits set to 1.
     * @param visitorId visitor id
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withVisitor(int visitorId) {
        long now = System.currentTimeMillis() / 1000L;
        this.visitor = new Visitor(visitorId, now, now, 1);
        return this;
    }

    /**
     * Sets the URL builder.
     * If the URL builder is not set explicitly Google Analytics v5.3.8 builder will be used.
     * @param urlBuilder builder
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withURLBuilder(URLBuilder urlBuilder) {
        this.urlBuilder = urlBuilder;
        return this;
    }

    /**
     * Set the event dispatcher.
     * If the event dispatcher is not set explicitly a synchronous dispatcher based on
     * Spring RestTemplate will be used.
     * @param eventDispatcher event dispatcher
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder withEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        return this;
    }

    /**
     * Build the tracker.
     * @return tracker
     */
    public GoogleAnalyticsTracker build() {
        // Check mandatory data
        // Check input params
        if (null == trackerConfiguration || null == visitor || null == device) {
            LOG.error("Configuration, visitor, device must be provided");
            throw new IllegalArgumentException();
        }

        // Set default values
        if (null == urlBuilder) {
            urlBuilder = new URLBuilderV5_3_8();
        }

        if (null == eventDispatcher) {
            eventDispatcher = new SynchronousEventDispatcher();
        }

        // Create the tracker
        return new GoogleAnalyticsTracker(trackerConfiguration, visitor, device, urlBuilder, eventDispatcher);
    }
}
