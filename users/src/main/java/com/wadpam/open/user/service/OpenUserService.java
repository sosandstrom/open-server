package com.wadpam.open.user.service;

import com.wadpam.open.mvc.MardaoCrudService;
import com.wadpam.open.user.dao.DOpenUserDao;
import com.wadpam.open.user.domain.DOpenUser;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author os
 */
public class OpenUserService extends MardaoCrudService<DOpenUser, Long, DOpenUserDao> {

    @Autowired
    public void setDOpenUserDao(DOpenUserDao dOpenUserDao) {
        this.dao = dOpenUserDao;
    }
}
