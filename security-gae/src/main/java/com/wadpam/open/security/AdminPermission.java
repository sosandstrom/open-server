package com.wadpam.open.security;

import com.google.appengine.api.datastore.Email;
import com.wadpam.open.dao.DAppAdminDao;
import com.wadpam.open.domain.DAppAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Evaluate if and principle is allowed to access admin information.
 * @author mattiaslevin
 */
public class AdminPermission implements Permission {

    static final Logger LOG = LoggerFactory.getLogger(AdminPermission.class);

    private DAppAdminDao appAdminDao;


    @Override
    public boolean isAllowed(Authentication authentication, Object adminEmail) {

        GaeUserDetails userDetails = (GaeUserDetails)authentication.getPrincipal();
        LOG.debug("Check if user:{} is a allowed to access admin:{}", userDetails.getEmail(), adminEmail);

        // Check if GAE admin then always true
        if (null != userDetails && userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
            return true;

        // Get the app details for the domain
        DAppAdmin dAppAdmin = appAdminDao.findByEmail(new Email(userDetails.getEmail()));

        // Check if the user is admin of the app
        if (null == dAppAdmin || dAppAdmin.getEmail().getEmail().equalsIgnoreCase((String)adminEmail))
            return true;
        else
            return false;
    }


    // Setters
    public void setAppAdminDao(DAppAdminDao appAdminDao) {
        this.appAdminDao = appAdminDao;
    }
}
