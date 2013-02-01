package com.wadpam.open.web;

import com.google.appengine.api.datastore.Email;
import com.wadpam.open.domain.DAppDomain;
import com.wadpam.open.json.JAppDomain;
import com.wadpam.open.mvc.CrudController;
import com.wadpam.open.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

/**
 * Controller for managing domains.
 * @author mattiaslevin
 */
@Controller
@RequestMapping("{domain}/_admin/domain")
public class DomainController extends CrudController<JAppDomain, DAppDomain, String, DomainService> {

    public static final int ERR_BASE_DOMAIN = DomainService.ERR_BASE_DOMAIN;
    public static final int ERR_DOMAIN_NOT_FOUND = ERR_BASE_DOMAIN + 101;


    // Setters and getters
    
    public DomainController() {
        super(JAppDomain.class);
    }

    @Override
    public void convertDomain(DAppDomain from, JAppDomain to) {
        convertStringEntity(from, to);
        to.setAnalyticsTrackingCode(from.getAnalyticsTrackingCode());
        to.setAppArg1(from.getAppArg1());
        to.setAppArg2(from.getAppArg2());
        to.setDescription(from.getDescription());
        to.setEmail(null != from.getEmail() ? from.getEmail().getEmail() : null);
        to.setPassword(from.getPassword());
        to.setUsername(from.getUsername());
    }

    @Override
    public void convertJson(JAppDomain from, DAppDomain to) {
        convertJString(from, to);
        to.setAnalyticsTrackingCode(from.getAnalyticsTrackingCode());
        to.setAppArg1(from.getAppArg1());
        to.setAppArg2(from.getAppArg2());
        to.setDescription(from.getDescription());
        to.setEmail(null != from.getEmail() ? new Email(from.getEmail()) : null);
        to.setPassword(from.getPassword());
        to.setUsername(from.getUsername());
    }
    
    @Autowired
    public void setDomainService(DomainService domainService) {
        this.service = domainService;
    }
}
