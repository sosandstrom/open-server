package com.wadpam.open;


import com.wadpam.open.analytics.OpenAnalyticsTracker;
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
 * Unit tests.
 * @author mattiaslevin
 */
public class AnalyticsTest {

    static final Logger LOG = LoggerFactory.getLogger(AnalyticsTest.class);

    private Profile profile;
    private Device deviceData = new Device();
    private Visitor visitorData;

    private GAOpenAnalyticsTracker tracker;

    public AnalyticsTest() {

        // Create profile
        this.profile = new Profile("test-profile", "UA-35889513-2");

        // Set visitor
        this.visitorData = Visitor.visitorWithNewSession(999, now() - 50000, now() - 4000, 10);

        // Set device data
        this.deviceData.setEncoding("UTF-8");
        this.deviceData.setFlashVersion("11");
        this.deviceData.setScreenResolution("800x600");
        this.deviceData.setUserLanguage("sv");
        this.deviceData.setColorDepth("24-bit");
        this.deviceData.setUserAgent("Mozilla/5.0(iPad; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B314 Safari/531.21.10");
    }


    @Before
    public void setup() {
        this.visitorData.startNewSession();
        this.tracker = this.profile.getTracker(this.visitorData, this.deviceData);
        this.tracker.setDebug(true);
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
    public void eventWithLabel() {
        LOG.info("Event with label and value");

        tracker.trackEvent("category 1", "action 1", "A label", 2);

        assertTrue(true);
    }

    @Test
    public void eventWithCustomVar() {
        LOG.info("Event with custom variable");

        List<CustomVariable> customVars = new ArrayList<CustomVariable>();
        customVars.add(new CustomVariable(1, "name 1", "value 1"));
        customVars.add(new CustomVariable(2, "name 2", "value 2"));

        tracker.trackEvent("category 1", "action 1", "A label", 2, customVars);

        assertTrue(true);
    }


    @Test
    public void basicPageView() {
        LOG.info("Test basic page view");

        tracker.trackPageView("/page/url", "A Title", "legend-passbook.appspot.com");

        assertTrue(true);
    }

    @Test
    public void pageViewWithReferrer() {
        LOG.info("Page view with referrer");

        tracker.trackPageView("/page/url", "A Title", "www.testhost.com", "/referrer/page", "www.referrerhost.com", null);

        assertTrue(true);
    }

    @Test
    public void pageViewWithCustomVars() {
        LOG.info("Page view with referrer");

        List<CustomVariable> customVars = new ArrayList<CustomVariable>();
        customVars.add(new CustomVariable(1, "name 1", "value 1"));
        customVars.add(new CustomVariable(2, "name 2", "value 2"));

        tracker.trackPageView("/page/url", "A Title", "www.testhostname.com", "/referrer/page", "www.referrerhost.com", customVars);

        assertTrue(true);
    }

    // Return now
    private static long now() {
        return System.currentTimeMillis() / 1000L;
    }
}
