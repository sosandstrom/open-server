/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.service;

import com.wadpam.open.domain.DAppDomain;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sosandstrom
 */
public class ItestService {
    
    @Autowired
    private DomainService domainService;
    
    public void init() {
        DAppDomain itest = new DAppDomain();
        itest.setId("itest");
        itest.setUsername("itest");
        itest.setPassword("itest");
        domainService.create(itest);
    }
            
}
