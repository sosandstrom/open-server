/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.web;

import com.wadpam.open.domain.DProfile;
import com.wadpam.open.json.JProfile;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.service.ProfileService;
import com.wadpam.open.user.web.OpenUserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author sosandstrom
 */
@Controller
@RequestMapping("{domain}/user")
public class ProfileController extends CrudController<JProfile, DProfile, Long, ProfileService> {
    
    private static final OpenUserController USER_CONTROLLER = new OpenUserController();

    public ProfileController() {
        super(JProfile.class);
    }

    @Override
    public void convertDomain(DProfile from, JProfile to) {
        USER_CONTROLLER.convertDomain(from, to);
        to.setPhoneNumber(from.getPhoneNumber());
    }

    @Override
    public void convertJson(JProfile from, DProfile to) {
        USER_CONTROLLER.convertJson(from, to);
        to.setPhoneNumber(from.getPhoneNumber());
    }

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.service = profileService;
    }
}
