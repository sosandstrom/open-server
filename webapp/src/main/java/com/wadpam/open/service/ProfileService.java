/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.service;

import com.wadpam.open.dao.DProfileDaoBean;
import com.wadpam.open.domain.DProfile;
import com.wadpam.open.mvc.CrudServiceWrapper;
import com.wadpam.open.user.domain.DOpenUser;
import com.wadpam.open.user.service.OpenUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sosandstrom
 */
public class ProfileService extends CrudServiceWrapper<DOpenUser, Long, DProfile> {

    protected final OpenUserService openUserService;
    
    public ProfileService() {
        super(new OpenUserService(DProfile.class));
        this.openUserService = (OpenUserService) delegate;
    }

    @Autowired
    public void setDOpenUserDao(DProfileDaoBean dao) {
        openUserService.setDao(dao);
    }
}
