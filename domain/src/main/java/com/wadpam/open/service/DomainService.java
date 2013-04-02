package com.wadpam.open.service;

import com.google.appengine.api.NamespaceManager;
import com.wadpam.open.dao.DAppDomainDao;
import com.wadpam.open.domain.DAppDomain;
import com.wadpam.open.mvc.MardaoCrudService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service for managing domains.
 * @author mattiaslevin
 */
public class DomainService extends MardaoCrudService<DAppDomain, String, DAppDomainDao>{

    public static final int ERR_BASE_DOMAIN = 10000;
    public static final int ERR_DOMAIN_ALREADY_EXISTS = ERR_BASE_DOMAIN + 1;
    public static final int ERR_DOMAIN_NOT_FOUND = ERR_BASE_DOMAIN + 2;
    
    private static final ThreadLocal<String> CURRENT_NAMESPACE = new ThreadLocal<String>();

    /**
     * Store current namespace and set to Empty
     */
    @Override
    protected void preDao() {
        CURRENT_NAMESPACE.set(NamespaceManager.get());
        NamespaceManager.set(null);
    }

    /**
     * Restore the current namespace
     */
    @Override
    protected void postDao() {
        NamespaceManager.set(CURRENT_NAMESPACE.get());
        CURRENT_NAMESPACE.remove();
    }

    // Setters and getters
    @Autowired
    public void setDomainDao(DAppDomainDao domainDao) {
        this.dao = domainDao;
    }
}
