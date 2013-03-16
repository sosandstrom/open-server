package com.wadpam.open;

import com.wadpam.open.analytics.google.*;
import com.wadpam.open.analytics.google.config.Device;
import com.wadpam.open.analytics.google.config.Property;
import com.wadpam.open.analytics.google.config.Visitor;
import com.wadpam.open.analytics.google.protocol.GoogleAnalyticsProtocol;
import com.wadpam.open.analytics.google.protocol.MeasurementProtocol_v1;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

/**
 * Unit test of the new Google Analytics measurement protocol.
 * @mattiaslevin
 */
public class AnalyticsMeasurementProtocolTest {
    static final Logger LOG = LoggerFactory.getLogger(AnalyticsMeasurementProtocolTest.class);

    private Property trackerConfig;
    private Device deviceData = new Device();
    private Visitor visitorData;

    private GoogleAnalyticsTracker tracker;

    public AnalyticsMeasurementProtocolTest() {

        // Create profile
        trackerConfig = new Property("test-profile", "UA-38370095-2");

        // Set visitor
        visitorData = new Visitor("9998", now() - 50000, now() - 4000, 10);

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
                .trackerConfiguration(trackerConfig)
                .visitor(visitorData)
                .device(deviceData)
                .app("Test app", "1.2")
                .measurementProtocol()
                .build();
        //tracker.setDebug(true);
    }

    @After
    public void tearDown() {
        // Do nothing right now
    }


    @Test
    public void basicEvent() {
        LOG.info("Test basic event");

        tracker.trackEvent("Category 2", "Action 2");

        assertTrue(true);
    }


    @Test
    public void basicPageView() {
        LOG.info("Test basic page view");

        tracker.trackPageView("hostname2", "/path2", "title2");

        assertTrue(true);
    }

    // Return now
    private static long now() {
        return System.currentTimeMillis() / 1000L;
    }

}
