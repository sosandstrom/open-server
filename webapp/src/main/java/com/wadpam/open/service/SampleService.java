package com.wadpam.open.service;

import com.wadpam.open.dao.GeneratedDSampleDao;
import com.wadpam.open.domain.DSample;
import com.wadpam.open.json.JSample;
import com.wadpam.open.mvc.CrudService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author os
 */
public class SampleService extends CrudService<JSample, DSample, Long> {
    
    @Autowired
    public void setDSampleDao(GeneratedDSampleDao dSampleDao) {
        this.dao = dSampleDao;
    }
    
}
