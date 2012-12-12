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
    private String category = "Tools";
    /** Action name */
    private String action = "IsAlive";
    /** Custom variable index */
    private int customVariableIndex = 0;
    /** Custom variable name */
    private String customVariableName = "Hour";

    /**
     * Set a tracking code if you like all is alive envents to
     * be handled by same analytic account.
     */
    private String trackingCode;

    /**
     * Set a tracking code mapper callback class that will be used
     * to decide with unique tracker id to used based on domain
     * and/or id provided in the request
     */
    private TrackingCodeMapper trackingCodeMapper;


    /**
     * Indicate the the app is alive and running.
     * @param id unique id of the app or possibly the location of the app
     * @return http 200 always
     */
    @RestReturn(value=Void.class, entity=Void.class, code={
            @RestCode(code=200, message="OK", description="IsAlive was logged")
    })
    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public void isAlive(HttpServletRequest request,
                        @PathVariable String domain,
                        @RequestParam(required = false) String id) {
        LOG.debug("Log is alive message from:{}", id);

        // Get the current hour - 01, 02, 03 .. 23 etc
        Calendar calendar = Calendar.getInstance();
        String hour = String.format("%d", calendar.get(Calendar.HOUR_OF_DAY));
        LOG.debug("Hour:{}", hour);

        long now = calendar.getTimeInMillis() / 1000;

        // Create device
        Device device = Device.defaultDevice(request);
        // Create visitor
        Visitor visitor = Visitor.visitorWithNewSession(id.hashCode(), now, now, 1);

        // Create profile
        Profile profile = null;
        if (null != trackingCode) {
            profile = Profile.getInstance("IsAliveTracker", trackingCode);
        } else if (null != trackingCodeMapper) {
            profile = Profile.getInstance("IsAliveTracker", domain, trackingCodeMapper);
        }  else {
            LOG.info("No tracker configured, is alive message will not be sent to any tracker");
            return;
        }

        // Create tracker
        OpenAnalyticsTracker tracker = profile.getTracker(visitor, device);

        // Send events to Google Analytics
        List<CustomVariable> customVariables = new ArrayList<CustomVariable>(1);
        customVariables.add(new CustomVariable(customVariableIndex, customVariableName, hour));
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

    public void setTrackingCodeMapper(TrackingCodeMapper trackingCodeMapper) {
        this.trackingCodeMapper = trackingCodeMapper;
    }
}
