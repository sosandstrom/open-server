package com.wadpam.open.analytics;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.analytics.google.*;
import com.wadpam.open.analytics.google.config.Property;
import com.wadpam.open.analytics.google.dispatcher.SynchDispatcher;
import com.wadpam.open.analytics.google.dispatcher.TrackingInfoDispatcher;
import com.wadpam.open.web.AbstractRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling dispatch tasks and also forwarding events to GA
 * on behalf on an app.
 * @author mattiaslevin
 */
@Controller
@RequestMapping(value="{domain}/analytics")
public class AnalyticsController extends AbstractRestController {
    static final Logger LOG = LoggerFactory.getLogger(AnalyticsController.class);

    // The default tracker that will be used if no domain-tracker mapping can be found
    private String defaultTrackerId;

    // Map between domain and a tracker id
    private Map<String, String> domainTrackerIdMapping = new HashMap<String, String>();

    /**
     * A GAE task for sending an event to Google Analytics.
     */
    @RequestMapping(value="task", method= RequestMethod.GET)
    @ResponseBody
    public void sendEventTask(HttpServletRequest request,
                              @RequestParam String baseUrl,
                              @RequestParam String params,
                              @RequestParam String userAgent,
                              @RequestParam String remoteAddress) throws UnsupportedEncodingException {
        LOG.debug("GAE sent event task");

        // Forward
        TrackingInfoDispatcher dispatcher = new SynchDispatcher(HttpMethod.GET);
        try {
            dispatcher.dispatch(URLDecoder.decode(baseUrl, "UTF-8"),
                    URLDecoder.decode(params, "UTF-8"),
                    URLDecoder.decode(userAgent, "UTF-8"),
                    URLDecoder.decode(remoteAddress, "UTF-8"));
        } catch (URISyntaxException e) {
            LOG.error("Not possible to convert url to uri with reason:{}", e.getMessage());
            // No need to throw exception, no one is listening to the result
        }
    }


    /**
     * Forward a page view on the behalf of an app.
     * @param userId a unique user id. Must be consistent through requests
     * @param aboluteUrl the url or name of the page
     * @param title the title of the page
     */
    @RequestMapping(value="pageview", method= RequestMethod.GET)
    @ResponseBody
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="Page view forwarded")
    })
    public void forwardPageView(HttpServletRequest request,
                                @PathVariable String domain,
                                @RequestParam String userId,
                                @RequestParam String aboluteUrl,
                                @RequestParam String title,
                                @RequestParam String appName,
                                @RequestParam String appVersion) {
        LOG.debug("Forward page view");

        String trackerId = this.domainTrackerIdMapping.get(domain);
        String trackerName = domain;
        if (null != trackerId) {
            trackerId = this.defaultTrackerId;
            trackerName = "default";
        }

        Property trackerConfig = new Property(trackerName, trackerId);
        GoogleAnalyticsTracker tracker = new GoogleAnalyticsTrackerBuilder()
                .trackerConfiguration(trackerConfig)
                .visitor(userId)
                .deviceFromRequest(request)
                .app(appName, appVersion)
                .build();

        tracker.trackPageView(request.getRemoteHost(), aboluteUrl, title);
    }

    private static long now() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * Forward an event on the behalf of an app.
     * @param userId a unique user id. Must be consistent through requests
     * @param category the event category
     * @param action the action name
     * @param label a label
     * @param value a value
     */
    @RequestMapping(value="event", method= RequestMethod.GET)
    @ResponseBody
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="Event forwarded")
    })
    public void forwardEvent(HttpServletRequest request,
                             @PathVariable String domain,
                             @RequestParam String userId,
                             @RequestParam String category,
                             @RequestParam String action,
                             @RequestParam(required=false) String label,
                             @RequestParam(required=false) int value,
                             @RequestParam String appName,
                             @RequestParam String appVersion) {
        LOG.debug("Forward event");

        String trackerId = this.domainTrackerIdMapping.get(domain);
        String trackerName = domain;
        if (null != trackerId) {
            trackerId = this.defaultTrackerId;
            trackerName = "default";
        }

        Property trackerConfig = new Property(trackerName, trackerId);
        GoogleAnalyticsTracker tracker = new GoogleAnalyticsTrackerBuilder()
                .trackerConfiguration(trackerConfig)
                .visitor(userId)
                .deviceFromRequest(request)
                .app(appName, appVersion)
                .build();

        tracker.trackEvent(category, action, label, value, null);
    }


    // Setter and getters
    public void setDefaultTrackerId(String defaultTrackerId) {
        this.defaultTrackerId = defaultTrackerId;
    }

    public void setDomainTrackerIdMapping(Map<String, String> domainTrackerIdMapping) {
        this.domainTrackerIdMapping = domainTrackerIdMapping;
    }
}
