package com.wadpam.open.web;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.analytics.OpenAnalyticsTracker;
import com.wadpam.open.analytics.google.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * A Controller that can be used by app to indicate that they are alive/started/running.
 * These events will be forwarded to Google Analytics for tracking.
 * @author mattiaslevin
 */
@Controller
@RequestMapping(value="{domain}/isalive")
public class IsAliveController extends AbstractRestController {
    private static final Logger LOG = LoggerFactory.getLogger(IsAliveController.class);

    /**
     *  Event details, should be configured through spring context if the the
     *  default values of not ok.
     */
    /** Category name */
    private String category = "Monitoring";
    /** Action name */
    private String action = "IsAlive";
    /** Custom variable index */
    private int customVariableIndex = 0;
    /** Custom variable name */
    private String customVariableName = "Hour";

    /**
     * Set a tracking code if you like all is alive events to
     * be handled by same analytic account.
     */
    private String trackingCode;


    @ModelAttribute
    public String addTrackingCode(HttpServletRequest request) {
        final String trackingCode = (String) request.getAttribute("trackingCode");
        //LOG.debug("Add tracking code {}", trackingCode);
        return trackingCode;
    }


    /**
     * Indicate the the app is alive and running.
     * @param trackingCode The tracking code that will be used
     *                     The recommendation is to configuring an interceptor that finds
     *                     and configure the correct tracking code.
     * @param id unique id of the app or possibly the location of the app.
     *           If this value is provided it will be used as label in the tracking.
     * @param hour the hour the event was sent, e.g. 00, 01, 02 .. 11, 12, 13 .. 22, 23.
     *             If this parameter if provider a customer variable will be included in
     *             the tracking.
     * @return http 200 always unless server error
     */
    @RestReturn(value=Void.class, entity=Void.class, code={
            @RestCode(code=200, message="OK", description="IsAlive was logged")
    })
    @RequestMapping(method= RequestMethod.POST)
    @ResponseBody
    public void isAlive(HttpServletRequest request,
                        @PathVariable String domain,
                        @ModelAttribute String trackingCode,
                        @RequestParam(required = false) String id,
                        @RequestParam(required = false) String hour) {
        LOG.debug("Log is alive message from:{} with tacking code:{}", id, trackingCode);

        // Create device
        Device device = Device.defaultDevice(request);
        // Create visitor
        long now = new Date().getTime() / 1000;
        Visitor visitor = Visitor.visitorWithNewSession(id.hashCode(), now, now, 1);

        // Create profile
        Profile profile = null;
        if (null != trackingCode) {
            profile = Profile.getInstance("IsAliveTracker", trackingCode);
        } else if (null != this.trackingCode) {
            profile = Profile.getInstance("IsAliveTracker", this.trackingCode);
        }  else {
            LOG.info("No tracker configured, is alive message will not be sent to any tracker");
            return;
        }

        // Create tracker
        OpenAnalyticsTracker tracker = profile.getTracker(visitor, device);

        // Custom variables
        List<CustomVariable> customVariables = null;
        if (null != hour) {
            customVariables = new ArrayList<CustomVariable>(1);
            customVariables.add(new CustomVariable(customVariableIndex, customVariableName, hour));
        }

        // Send events to Google Analytics
        tracker.trackEvent(category, action, id, 1, customVariables);
    }


    // Setters and getters
    public void setAction(String action) {
        this.action = action;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCustomVariableIndex(int customVariableIndex) {
        this.customVariableIndex = customVariableIndex;
    }

    public void setCustomVariableName(String customVariableName) {
        this.customVariableName = customVariableName;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

}
