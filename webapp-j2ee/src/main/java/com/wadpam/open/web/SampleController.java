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

    public SampleController() {
        super(JSample.class);
    }
    
    @Override
    public void convertDomain(DSample from, JSample to) {
        convertLongEntity(from, to);
        
        to.setName(from.getName());
    }

    @Override
    public void convertJson(JSample from, DSample to) {
        convertJLong(from, to);
        
        to.setName(from.getName());
    }
    
    @Autowired
    public void setSampleService(SampleService sampleService) {
        this.service = sampleService;
    }
}
