package com.wadpam.open.web;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class CommonController extends AbstractRestController {
    private static final Logger LOG = LoggerFactory.getLogger(CommonController.class);

    /**
     * Get a cross platform unique id.
     * @return a UUID type 4
     */
    @RestReturn(value=Map.class, entity=Map.class, code={
            @RestCode(code=200, message="OK", description="UUID generated"),
    })
    @RequestMapping(value="uuid", method= RequestMethod.GET)
    @ResponseBody
    public Map<String, String> generateUUID4(HttpServletRequest request) {
        LOG.debug("Generate UUID");

        // Generate a type 4 UUID
        UUID uuid = UUID.randomUUID();

        Map<String, String> response = new HashMap<String, String>();
        response.put("uuid", uuid.toString());
        return response;
    }
}
