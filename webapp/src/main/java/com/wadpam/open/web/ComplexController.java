package com.wadpam.open.web;

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
@Controller
@RequestMapping(value="{domain}/complex")
public class ComplexController extends CrudController<JComplex,
        DComplex, Long>{
    
    @Autowired
    private DSampleDao sampleDao;

    @Override
    public JComplex convertDomain(DComplex from) {
        if (null == from) {
            return null;
        }

        JComplex to = new JComplex();
        convertLongEntity(from, to);
        
        to.setName(from.getName());
        to.setManagerId(null != from.getManager() ? from.getManager().getId() : null);
        to.setOrganizationId(sampleDao.getSimpleKeyByPrimaryKey(from.getOrganizationKey()));
        
        return to;
    }

    @Override
    public DComplex convertJson(JComplex from) {
        if (null == from) {
            return null;
        }

        DComplex to = new DComplex();
        convertJLong(from, to);
        
        to.setName(from.getName());
        if (null != from.getManagerId()) {
            DComplex manager = new DComplex();
            manager.setId(from.getManagerId());
            to.setManager(manager);
        }
        to.setOrganizationKey(sampleDao.getPrimaryKey(null, from.getOrganizationId()));
        
        return to;
    }
    
    @Autowired
    public void setComplexService(ComplexService complexService) {
        this.service = complexService;
    }
}
