/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.user.web;

import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.user.domain.DOpenUser;
import com.wadpam.open.user.json.JOpenUser;
import com.wadpam.open.user.service.OpenUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    
    @RequestMapping(value="v10", method= RequestMethod.GET, params="email")
    @ResponseBody
    public JOpenUser getByEmail(@RequestParam String email) {
        DOpenUser dUser = service.getByEmail(email);
        if (null == dUser) {
            throw new NotFoundException(ERR_CRUD_BASE, email);
        }
        return convertDomain(dUser);
    }

    // ----------------------- Converter and setters ---------------------------
    
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
