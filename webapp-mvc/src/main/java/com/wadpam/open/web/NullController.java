/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.web;

import com.wadpam.open.domain.DProfile;
import com.wadpam.open.json.JProfile;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.mvc.CrudService;
import java.util.Collections;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author sosandstrom
 */
@Controller
@RequestMapping(value="{domain}/null")
public class NullController extends CrudController<JProfile, DProfile, Long, CrudService<DProfile, Long>> {

    public NullController() {
        super(JProfile.class);
    }

    @Override
    public Map<String, Object> getSchema() {
        return Collections.EMPTY_MAP;
    }
    
    @Override
    public void convertDomain(DProfile from, JProfile to) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void convertJson(JProfile from, DProfile to) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
