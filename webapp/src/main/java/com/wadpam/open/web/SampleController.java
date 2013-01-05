package com.wadpam.open.web;

import com.wadpam.open.domain.DSample;
import com.wadpam.open.json.JSample;
import com.wadpam.open.mvc.CrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author os
 */
@Controller
@RequestMapping(value="{domain}/sample")
public class SampleController extends CrudController<JSample,
        DSample, Long>{

    @Override
    public JSample convertDomain(DSample from) {
        JSample to = new JSample();
        convert(from, to);
        
        to.setName(from.getName());
        
        return to;
    }

    @Override
    public DSample convertJson(JSample from) {
        DSample to = new DSample();
        convert(from, to);
        
        to.setName(from.getName());
        
        return to;
    }
    
}
