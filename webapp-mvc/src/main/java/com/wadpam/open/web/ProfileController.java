/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.web;

import com.wadpam.open.domain.DProfile;
import com.wadpam.open.json.JProfile;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.mvc.CrudService;
import com.wadpam.open.service.ProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author sosandstrom
 */
@Controller
@RequestMapping(value="{domain}/profile")
public class ProfileController extends CrudController<JProfile, DProfile, Long, CrudService<DProfile, Long>> {

    public ProfileController() {
        super(JProfile.class);
        this.service = new ProfileService();
    }

    @Override
    public void convertDomain(DProfile from, JProfile to) {
        convertLongEntity(from, to);
        to.setPhoneNumber(from.getPhoneNumber());
    }

    @Override
    public void convertJson(JProfile from, DProfile to) {
        convertJLong(from, to);
        to.setPhoneNumber(from.getPhoneNumber());
    }

}
