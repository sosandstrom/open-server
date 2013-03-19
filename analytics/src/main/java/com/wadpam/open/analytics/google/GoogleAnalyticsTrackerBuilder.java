package com.wadpam.open.analytics.google;

import com.wadpam.open.analytics.google.config.Application;
import com.wadpam.open.analytics.google.config.Device;
import com.wadpam.open.analytics.google.config.Property;
import com.wadpam.open.analytics.google.config.Visitor;
import com.wadpam.open.analytics.google.dispatcher.SynchDispatcher;
import com.wadpam.open.analytics.google.dispatcher.TrackingInfoDispatcher;
import com.wadpam.open.analytics.google.protocol.GoogleAnalyticsProtocol;
import com.wadpam.open.analytics.google.protocol.LegacyProtocol_v5_3_8;
import com.wadpam.open.analytics.google.protocol.MeasurementProtocol_v1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * A builder class that help build a Google Analytics tracker using a fluent builder API and
 * default values when possible.
 *
 * @author mattiaslevin
 */
public class GoogleAnalyticsTrackerBuilder {
    static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsTrackerBuilder.class);

    private Property property;
    private Device device;
    private Application app;
    private Visitor visitor;
    private GoogleAnalyticsProtocol protocol;
    private TrackingInfoDispatcher trackerDispatcher;


    // Default constructor
    public GoogleAnalyticsTrackerBuilder() {
    }


    /**
     * Create a tracker with as little input variables as possible.
     * The device will be created from the request, measurement protocol and POST will be used
     * @param trackingCode the tracking code
     * @param visitorId visitor id
     * @param request request
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder defaultTracker(String trackingCode, String visitorId, HttpServletRequest request) {
        trackerConfiguration(trackingCode).visitor(visitorId).deviceFromRequest(request).measurementProtocol();
        return this;
    }


    /**
     * Set tracker configuration.
     * @param property tracking configuration
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder trackerConfiguration(Property property) {
        this.property = property;
        return this;
    }

    /**
     * Create a tracker configuration with a name and tracking code.
     * @param name name
     * @param trackingCode tracking code
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder trackerConfiguration(String name, String trackingCode) {
        property = new Property(name, trackingCode);
        return this;
    }

    /**
     * Create a tracker configuration from a tracking code.
     * @param trackingCode tracking code
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder trackerConfiguration(String trackingCode) {
        property = new Property(trackingCode, trackingCode);
        return this;
    }

    /**
     * Set device.
     * @param device device
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder device(Device device) {
        this.device = device;
        return this;
    }

    /**
     * Create a device with iPhone (iOS5) characteristics.
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder iPhoneDevice() {
        device = Device.defaultiPhoneDevice();
        return this;
    }

    /**
     * Create a device with iPad (iOS5) characteristics.
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder iPadDevice() {
        device = Device.defaultiPadDevice();
        return this;
    }

    /**
     * Create a device from the incoming request.
     * @param request incoming request
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder deviceFromRequest(HttpServletRequest request) {
        device = Device.defaultDevice(request);
        return this;
    }

    /**
     * Set visitor.
     * @param visitor visitor
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder visitor(Visitor visitor) {
        this.visitor = visitor;
        return this;
    }

    /**
     * Create a visitor with first and previous visit set to now and number of visits set to 1.
     * @param visitorId visitor id
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder visitor(String visitorId) {
        long now = System.currentTimeMillis() / 1000L;
        visitor = new Visitor(visitorId, now, now, 1);
        return this;
    }

    /**
     * Create app information.
     * @param name app name
     * @param version app version
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder app(String name, String version) {
        app = new Application(name, version);
        return this;
    }

    /**
     * Create a url protocol based on the measurement protocol.
     * The dispatcher will be set to synch and POST.
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder measurementProtocol() {
        protocol = new MeasurementProtocol_v1();
        trackerDispatcher = new SynchDispatcher(HttpMethod.POST);
        return this;
    }

    /**
     * Create a protocol based on the legacy protocol.
     * The dispatcher will be set to synch and GET.
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder legacyProtocol() {
        protocol = new LegacyProtocol_v5_3_8();
        trackerDispatcher = new SynchDispatcher(HttpMethod.GET);
        return this;
    }

    /**
     * Sets the protocol.
     * If the URL builder is not set explicitly Google Analytics v5.3.8 legacy protocol will be used.
     * @param protocol the protocol
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder protocol(GoogleAnalyticsProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * Set the tracking info dispatcher.
     * If the dispatcher is not set explicitly a synchronous dispatcher based on
     * Spring RestTemplate will be used.
     * @param dispatcher the tracking info dispatcher
     * @return tracker builder
     */
    public GoogleAnalyticsTrackerBuilder dispatcher(TrackingInfoDispatcher dispatcher) {
        this.trackerDispatcher = dispatcher;
        return this;
    }

    /**
     * Build the tracker.
     * @return tracker
     */
    public GoogleAnalyticsTracker build() {
        // Check mandatory data
        // Check input params
        if (null == property || null == visitor || null == device) {
            LOG.error("Configuration, visitor, device must be provided");
            throw new IllegalArgumentException();
        }

        // Set default values
        if (null == protocol) {
            protocol = new LegacyProtocol_v5_3_8();
        }

        if (null == trackerDispatcher) {
            trackerDispatcher = new SynchDispatcher(HttpMethod.GET);
        }

        // Create the tracker
        return new GoogleAnalyticsTracker(property, visitor, device, app, protocol, trackerDispatcher);
    }
}
