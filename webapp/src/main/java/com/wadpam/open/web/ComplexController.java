package com.wadpam.open.web;

import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.dao.DSampleDao;
import com.wadpam.open.domain.DComplex;
import com.wadpam.open.json.JComplex;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.service.ComplexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author os
 */
@RestReturn(value=JComplex.class)
@Controller
@RequestMapping(value="{domain}/complex")
public class ComplexController extends CrudController<JComplex,
        DComplex, Long, ComplexService>{
    
    @Autowired
    private DSampleDao sampleDao;

    public ComplexController() {
        super(JComplex.class);
    }

    @Override
    public void convertDomain(DComplex from, JComplex to) {
        convertLongEntity(from, to);
        
        to.setName(from.getName());
        to.setManager(null != from.getManager() ? from.getManager().getId() : null);
        to.setOrganizationKey(sampleDao.getSimpleKeyByPrimaryKey(from.getOrganizationKey()));
    }

    @Override
    public void convertJson(JComplex from, DComplex to) {
        convertJLong(from, to);
        
        to.setName(from.getName());
        if (null != from.getManager()) {
            DComplex manager = new DComplex();
            manager.setId(from.getManager());
            to.setManager(manager);
        }
        to.setOrganizationKey(sampleDao.getPrimaryKey(null, from.getOrganizationKey()));
    }
    
    @Autowired
    public void setComplexService(ComplexService complexService) {
        this.service = complexService;
    }
}
