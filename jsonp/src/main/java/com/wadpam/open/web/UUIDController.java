package com.wadpam.open.web;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
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
import java.util.UUID;

/**
 * This controller implements common features usable my most services.
 * @author mattiaslevin
 */
@Controller
@RequestMapping(value="{domain}/common")
public class UUIDController extends AbstractRestController {
    private static final Logger LOG = LoggerFactory.getLogger(UUIDController.class);


    private UUIDCreatedListener uuidCallback;

    /**
     * Get a cross platform unique id.
     * @param appArg1 an option parameter the device can set that will be included in
     *                the callback to any registered callback listener.
     * @param appArg2 an option parameter the device can set that will be included in
     *                the callback to any registered callback listener.
     * @return a UUID type 4
     */
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="UUID generated")
    })
    @RequestMapping(value="uuid", method= RequestMethod.GET)
    @ResponseBody
    public Map<String, String> generateUUID4(HttpServletRequest request,
                                             @RequestParam String appArg1,
                                             @RequestParam String appArg2) {
        LOG.debug("Generate UUID");

        // Generate a type 4 UUID
        UUID uuid = UUID.randomUUID();

        // If we have a callback method set, call it
        if (null != uuidCallback) {
            uuidCallback.uuidCreated(uuid.toString(), appArg1, appArg2);
        }

        Map<String, String> response = new HashMap<String, String>();
        response.put("uuid", uuid.toString());
        return response;
    }


    // Setters and getters
    public UUIDCreatedListener getUuidCallback() {
        return uuidCallback;
    }

    public void setUuidCallback(UUIDCreatedListener uuidCallback) {
        this.uuidCallback = uuidCallback;
    }
}
