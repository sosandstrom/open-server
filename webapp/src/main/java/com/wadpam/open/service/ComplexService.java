package com.wadpam.open.service;

import com.wadpam.open.dao.GeneratedDComplexDao;
import com.wadpam.open.domain.DComplex;
import com.wadpam.open.mvc.MardaoCrudService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author os
 */
public class ComplexService extends MardaoCrudService<DComplex, Long, GeneratedDComplexDao> {

    public ComplexService() {
        super(DComplex.class);
    }
    
    @Autowired
    public void setDComplexDao(GeneratedDComplexDao dComplexDao) {
        this.dao = dComplexDao;
    }
}
