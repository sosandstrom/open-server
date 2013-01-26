package com.wadpam.open.web;

import com.wadpam.open.domain.DSample;
import com.wadpam.open.json.JSample;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author os
 */
@Controller
@RequestMapping(value="{domain}/sample")
public class SampleController extends CrudController<JSample,
        DSample, Long, SampleService>{

    @Override
    public JSample convertDomain(DSample from) {
        if (null == from) {
            return null;
        }

        JSample to = new JSample();
        convertLongEntity(from, to);
        
        to.setName(from.getName());
        
        return to;
    }

    @Override
    public DSample convertJson(JSample from) {
        if (null == from) {
            return null;
        }

        DSample to = new DSample();
        convertJLong(from, to);
        
        to.setName(from.getName());
        
        return to;
    }
    
    @Autowired
    public void setSampleService(SampleService sampleService) {
        this.service = sampleService;
    }
}
