package com.wadpam.open.analytics;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.web.AbstractRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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


    /**
     * A GAE task for sending and event to Google Analytics.
     */
    @RequestMapping(value="task", method= RequestMethod.GET)
    @ResponseBody
    public void sendEventTask(HttpServletRequest request) {
        LOG.debug("GAE sent event task");

        // TODO Missing implementation

    }


    /**
     * Forward a page view on the behalf of an app.
     * @param pageUrl The url or name of the page
     * @param title The title of the page
     */
    @RequestMapping(value="pageview", method= RequestMethod.GET)
    @ResponseBody
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="Page view forwarded"),
    })
    public void forwardPageView(HttpServletRequest request,
                                @RequestParam String pageUrl,
                                @RequestParam String title) {
        LOG.debug("Forward page view");

        // TODO Missing implementation

    }

    /**
     * Forward an event on the behalf of an app.
     * @param category the event category
     * @param action the action name
     * @param label a label
     * @param value a value
     */
    @RequestMapping(value="event", method= RequestMethod.GET)
    @ResponseBody
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="Event forwarded"),
    })
    public void forwardEvent(HttpServletRequest request,
                             @RequestParam String category,
                             @RequestParam String action,
                             @RequestParam(required = false) String label,
                             @RequestParam(required = false) int value) {
        LOG.debug("Forward event");

        // TODO Missing implementation

    }

}
