package com.wadpam.open.security;

import com.google.appengine.api.NamespaceManager;
import com.wadpam.open.dao.DAppDao;
import com.wadpam.open.domain.DApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements an UserDetailsService used by Spring security when authenticating
 * applications accessing the REST interface.
 * @author mattiaslevin
 */
public class ApiUserDetailsService implements UserDetailsService {

    static final Logger LOG = LoggerFactory.getLogger(ApiUserDetailsService.class);

    private DAppDao appDao;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOG.debug("Get UserDetails for user:{}", username);

        // Get the domain through the namespace
        final String domain = NamespaceManager.get();
        LOG.debug("Domain:{}", domain);

        DApp dApp = appDao.findByPrimaryKey(domain);
        if (null != dApp) {

            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>(1);
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_APP"));

            UserDetails userDetails = new User(dApp.getApiUser(), dApp.getApiPassword(), grantedAuthorities);

            return userDetails;
        } else {
            LOG.info("Trying to authenticate towards a domain that does not exist");
            return null;
        }
    }


    // Setters and getters
    public void setAppDao(DAppDao appDao) {
        this.appDao = appDao;
    }
}
