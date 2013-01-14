package com.wadpam.open.service;

import com.wadpam.open.dao.GeneratedDSampleDao;
import com.wadpam.open.domain.DSample;
import com.wadpam.open.mvc.MardaoCrudService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author os
 */
public class SampleService extends MardaoCrudService<DSample, Long, GeneratedDSampleDao> {
    
    @Autowired
    public void setDSampleDao(GeneratedDSampleDao dSampleDao) {
        this.dao = dSampleDao;
    }
    
}
