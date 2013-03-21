package com.wadpam.open.web;

import com.wadpam.open.AbstractRestTempleIntegrationTest;
import com.wadpam.open.analytics.google.*;
import com.wadpam.open.analytics.google.config.Device;
import com.wadpam.open.analytics.google.config.Property;
import com.wadpam.open.analytics.google.config.Visitor;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration tests for Analytics.
 * @author mattiaslevin
 */
public class AnalyticsITest {
    //extends AbstractRestTempleIntegrationTest {
    static final Logger LOG = LoggerFactory.getLogger(AnalyticsITest.class);

    private Property trackerConfig;
    private Device deviceData = new Device();
    private Visitor visitorData;
    private GoogleAnalyticsTracker tracker;

    public AnalyticsITest() {

        // Create profile
        trackerConfig = new Property("test-profile", "UA-35889513-2");

        // Set visitor
        visitorData = new Visitor("999", now() - 50000, now() - 4000, 10);

        // Set device data
        deviceData.setEncoding("UTF-8");
        deviceData.setFlashVersion("11");
        deviceData.setScreenResolution("800x600");
        deviceData.setUserLanguage("sv");
        deviceData.setColorDepth("24-bit");
        deviceData.setUserAgent("Mozilla/5.0(iPad; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B314 Safari/531.21.10");
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        visitorData.startNewSession();
        tracker = new GoogleAnalyticsTrackerBuilder()
                .trackerConfiguration(trackerConfig)
                .visitor(visitorData)
                .device(deviceData)
                .build();
        tracker.setDebug(true);
    }

    @After
    public void tearDown() {
    }

//    @Override
    protected String getBaseUrl() {
        return "http://localhost:8234/domain/itest/";
    }

    @Test
    public void testSynchAnalytics() {
    }

    @Test
    public void testAsynchAnalytics() {
    }

    // Return now
    private static long now() {
        return System.currentTimeMillis() / 1000L;
    }
}
