package com.wadpam.open.service;

import com.wadpam.open.dao.GeneratedDComplexDao;
import com.wadpam.open.domain.DComplex;
import com.wadpam.open.json.JComplex;
import com.wadpam.open.mvc.CrudService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author os
 */
public class ComplexService extends CrudService<JComplex, DComplex, Long> {
    
    @Autowired
    public void setDComplexDao(GeneratedDComplexDao dComplexDao) {
        this.dao = dComplexDao;
    }
}
