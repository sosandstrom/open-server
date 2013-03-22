/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author sosandstrom
 */
@Controller
@RequestMapping("{domain}")
public class SecurityController {

    @RequestMapping(value = "security")
    @ResponseBody
    public String getHello() {
        return "Hello Security";
    }
}
