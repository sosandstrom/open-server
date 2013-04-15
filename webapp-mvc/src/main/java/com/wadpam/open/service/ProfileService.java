/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.service;

import com.wadpam.open.dao.DProfileDaoBean;
import com.wadpam.open.domain.DProfile;
import com.wadpam.open.mvc.MardaoCrudService;
import net.sf.mardao.core.dao.Dao;

/**
 *
 * @author sosandstrom
 */
public class ProfileService extends MardaoCrudService<DProfile, Long, Dao<DProfile, Long>> {

    public ProfileService() {
        this.dao = new DProfileDaoBean();
    }
    
}
