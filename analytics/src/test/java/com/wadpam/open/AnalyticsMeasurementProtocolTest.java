package com.wadpam.open;

import com.wadpam.open.analytics.google.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test of the new Google Analytics measurement protocol.
 * @mattiaslevin
 */
public class AnalyticsMeasurementProtocolTest {
    static final Logger LOG = LoggerFactory.getLogger(AnalyticsMeasurementProtocolTest.class);

    private TrackerConfiguration trackerConfig;
    private Device deviceData = new Device();
    private Visitor visitorData;
    private URLBuilder urlBuilder;

    private GoogleAnalyticsTracker tracker;

    public AnalyticsMeasurementProtocolTest() {

        // Create profile
        trackerConfig = new TrackerConfiguration("test-profile", "UA-38370095-2");

        // Set visitor
        visitorData = new Visitor("999", now() - 50000, now() - 4000, 10);

        // The new Measurement url builder
        urlBuilder = new URLBuilderVMeasurementProtocol();

        // Set device data
        deviceData.setEncoding("UTF-8");
        deviceData.setFlashVersion("11");
        deviceData.setScreenResolution("800x600");
        deviceData.setUserLanguage("sv");
        deviceData.setColorDepth("24-bit");
        deviceData.setUserAgent("Mozilla/5.0(iPad; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B314 Safari/531.21.10");
    }


    @Before
    public void setup() {
        visitorData.startNewSession();
        tracker = new GoogleAnalyticsTrackerBuilder()
                .withTrackingConfiguration(trackerConfig)
                .withVisitor(visitorData)
                .withDevice(deviceData)
                .withApp("Test app", "1.1")
                .withURLBuilder(urlBuilder)
                .build();
        tracker.setDebug(true);
    }

    @After
    public void tearDown() {
        // Do nothing right now
    }


    @Test
    public void basicEvent() {
        LOG.info("Test basic event");

        tracker.trackEvent("category 1", "action 1");

        assertTrue(true);
    }


    @Test
    public void basicPageView() {
        LOG.info("Test basic page view");

        tracker.trackPageView("url", "A Title", null);

        assertTrue(true);
    }

    // Return now
    private static long now() {
        return System.currentTimeMillis() / 1000L;
    }

}
