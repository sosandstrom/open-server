package com.wadpam.open.service;

import com.google.appengine.api.datastore.Email;
import com.wadpam.open.exceptions.ConflictException;
import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.open.transaction.Idempotent;
import com.wadpam.open.dao.DAppDomainDao;
import com.wadpam.open.domain.DAppDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing domains.
 * @author mattiaslevin
 */
public class DomainService {
    static final Logger LOG = LoggerFactory.getLogger(DomainService.class);

    public static final int ERR_BASE_DOMAIN = 10000;
    public static final int ERR_DOMAIN_ALREADY_EXISTS = ERR_BASE_DOMAIN + 1;
    public static final int ERR_DOMAIN_NOT_FOUND = ERR_BASE_DOMAIN + 2;

    private DAppDomainDao domainDao;


    // Init method
    public void init() {
        // Do nothing
    }

    // Create a new domain
    @Idempotent
    @Transactional
    public DAppDomain createDomain(String domain, String username, String password,
                                   String description, String toEmail, String trackingCode) {

        // Check if already exists
        DAppDomain dAppDomain = domainDao.findByPrimaryKey(domain);
        if (null != dAppDomain) {
            // Not allowed
            throw new ConflictException(ERR_DOMAIN_ALREADY_EXISTS,
                    String.format("Not possible to create domain:%s, domain already exists", domain));
        }

        // Create
        dAppDomain = new DAppDomain();
        dAppDomain.setAppDomain(domain);
        dAppDomain.setUsername(username);
        dAppDomain.setPassword(password);
        dAppDomain.setDescription(description);
        if (null != toEmail) {
            dAppDomain.setEmail(new Email(toEmail));
        }
        dAppDomain.setAnalyticsTrackingCode(trackingCode);

        // persist
        domainDao.persist(dAppDomain);

        return dAppDomain;
    }

    // Update existing domain
    @Idempotent
    @Transactional
    public DAppDomain updateDomain(String domain, String username, String password,
                                   String description, String toEmail, String trackingCode) {

        // Check that the domain exists
        DAppDomain dAppDomain = domainDao.findByPrimaryKey(domain);
        if (null == dAppDomain) {
            throw new NotFoundException(ERR_DOMAIN_NOT_FOUND,
                    String.format("Domain:%s, not found", domain));
        }

        // Upadte
        dAppDomain.setUsername(username);
        dAppDomain.setPassword(password);
        dAppDomain.setDescription(description);
        if (null != toEmail) {
            dAppDomain.setEmail(new Email(toEmail));
        } else {
            dAppDomain.setEmail(null);
        }
        dAppDomain.setAnalyticsTrackingCode(trackingCode);

        // persist
        domainDao.persist(dAppDomain);

        return dAppDomain;
    }

    // Get domain details
    public DAppDomain getDomain(String domain) {

        DAppDomain dAppDomain = domainDao.findByPrimaryKey(domain);
        return dAppDomain;
    }

    // Delete domain
    @Idempotent
    @Transactional
    public DAppDomain deleteDomain(String domain) {

        // Check that the domain exists
        DAppDomain dAppDomain = domainDao.findByPrimaryKey(domain);
        if (null == dAppDomain) {
            throw new NotFoundException(ERR_DOMAIN_NOT_FOUND,
                    String.format("Domain:%s, not found", domain));
        }

        // Delete
        domainDao.delete(dAppDomain);

        return dAppDomain;
    }


    // Setters and getters
    public void setDomainDao(DAppDomainDao domainDao) {
        this.domainDao = domainDao;
    }
}
