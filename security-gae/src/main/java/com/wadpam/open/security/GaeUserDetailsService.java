package com.wadpam.open.security;

import com.google.appengine.api.users.User;
import com.wadpam.open.dao.DAppAdminDao;
import com.wadpam.open.domain.DAppAdmin;
import com.wadpam.open.service.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements an AuthenticationUserDetailsService used by Spring security when authenticating
 * backoffice requests logged in using Google.
 * @author mattiaslevin
 */
public class GaeUserDetailsService implements AuthenticationUserDetailsService {

    static final Logger LOG = LoggerFactory.getLogger(GaeUserDetailsService.class);

    private DAppAdminDao appAdminDao;


    @Override
    public UserDetails loadUserDetails(Authentication authentication) throws UsernameNotFoundException {

        User user = (User)authentication.getPrincipal();
        if (null == user) {
            // Should not happen
            LOG.error("Google user was not possible to obtain from the authentication principle when loading user details");
            throw new UsernameNotFoundException("Not possible to get the Google user from principle");
        }

        LOG.debug("Creating user details for Google user with email:{} and user id:{}", user.getEmail(), user.getUserId());


        // Check the role
        String credentials = (String)authentication.getCredentials();

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(1);
        if (credentials.equalsIgnoreCase("ROLE_ADMIN")) {
            // If the user is a registered as admin for the GAE application set role to admin
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {

            // Check the account status in the datastore to decide role
            DAppAdmin dAppAdmin = appAdminDao.findByPrimaryKey(user.getUserId());

            if (null != dAppAdmin && dAppAdmin.getAccountStatus().equalsIgnoreCase(AppService.ACCOUNT_ACTIVE)) {
                // Normal backoffice user
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            } else {
                // Give all other types of user pending role
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_PENDING"));
            }

        }

        LOG.debug("Credentials:{}", grantedAuthorities);

        UserDetails userDetails = new GaeUserDetails(user, grantedAuthorities);

        return userDetails;
    }


    // Setters and getters
    public void setAppAdminDao(DAppAdminDao appAdminDao) {
        this.appAdminDao = appAdminDao;
    }
}
