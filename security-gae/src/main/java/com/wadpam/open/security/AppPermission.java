package com.wadpam.open.security;

import com.google.appengine.api.datastore.Email;
import com.wadpam.open.dao.DAppDao;
import com.wadpam.open.domain.DApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Evaluate if and principle is allowed to access app information.
 * @author mattiaslevin
 */
public class AppPermission implements Permission {

    static final Logger LOG = LoggerFactory.getLogger(AppPermission.class);

    private DAppDao appDao;


    @Override
    public boolean isAllowed(Authentication authentication, Object domainName) {

        GaeUserDetails userDetails = (GaeUserDetails)authentication.getPrincipal();
        LOG.debug("Check if user:{} is a allowed to access app:{}", userDetails.getEmail(), domainName);

        // Check if GAE admin then always true
        if (null != userDetails && userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return true;

        // Get the app details for the domain
        DApp dApp = appDao.findByPrimaryKey((String)domainName);

        // Check if the user is admin of the app
        if (null == dApp || dApp.getAppAdmins().contains(new Email(userDetails.getEmail())))
            return true;
        else
            return false;
    }


    // Setters
    public void setAppDao(DAppDao appDao) {
        this.appDao = appDao;
    }
}
