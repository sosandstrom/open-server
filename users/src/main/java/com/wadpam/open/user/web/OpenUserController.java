/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.user.web;

import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.user.domain.DOpenUser;
import com.wadpam.open.user.json.JOpenUser;
import com.wadpam.open.user.service.OpenUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author sosandstrom
 */
@Controller
@RequestMapping("{domain}/user")
public class OpenUserController extends CrudController<JOpenUser, DOpenUser, Long, OpenUserService> {

    public OpenUserController() {
        super(JOpenUser.class);
    }

    @Override
    public void convertDomain(DOpenUser from, JOpenUser to) {
        convertLongEntity(from, to);
        to.setDisplayName(from.getDisplayName());
        to.setEmail(from.getEmail());
        to.setUsername(from.getUsername());
    }

    @Override
    public void convertJson(JOpenUser from, DOpenUser to) {
        convertJLong(from, to);
        to.setDisplayName(from.getDisplayName());
        to.setEmail(from.getEmail());
        to.setUsername(from.getUsername());
    }

}
