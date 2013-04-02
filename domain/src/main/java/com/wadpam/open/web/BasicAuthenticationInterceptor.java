/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.web;

import com.wadpam.open.domain.DAppDomain;
import com.wadpam.open.exceptions.AuthenticationFailedException;
import com.wadpam.open.mvc.CrudService;
import com.wadpam.open.security.SecurityDetailsService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author sosandstrom
 */
public class BasicAuthenticationInterceptor extends DomainInterceptor
        implements SecurityDetailsService{
    
    private static final Pattern PATH_DOMAIN = Pattern.compile("\\A/api/([^/]+)");

    private CrudService<DAppDomain, String> domainService;
    
    /**
     * Default constructor, sets the SecurityDetailsService to this.
     */
    public BasicAuthenticationInterceptor() {
        super();
        setSecurityDetailsService(this);
    }

    @Override
    protected String getRealmPassword(Object details) {
        final DAppDomain appDomain = (DAppDomain) details;
        final String password = appDomain.getPassword();
        return password;
    }

    @Override
    protected String getRealmUsername(String clientUsername, Object details) {
        final DAppDomain appDomain = (DAppDomain) details;
        final String realmUsername = appDomain.getUsername();
        
        // check that specified username matches the DAppDomain.username
        if (!clientUsername.equals(realmUsername)) {
            return null;
        }
        
        return realmUsername;
    }

    /**
     * Returns the ROLE_APPLICATION if specified details are not null.
     * @param details
     * @return the ROLE_APPLICATION if specified details are not null.
     */
    @Override
    public Collection<String> getRolesFromUserDetails(Object details) {
        return getRolesFromUserDetailsImpl(details);
    }

    /**
     * Returns the ROLE_APPLICATION if specified details are not null.
     * @param details
     * @return the ROLE_APPLICATION if specified details are not null.
     */
    public static Collection<String> getRolesFromUserDetailsImpl(Object details) {
        return null != details ? Arrays.asList(ROLE_APPLICATION) : Collections.EMPTY_LIST;
    }

    /**
     * To load the DAppDomain by domain path variable
     * @param request
     * @param response
     * @param uri
     * @param authValue
     * @param clientUsername
     * @return 
     */
    @Override
    public Object loadUserDetailsByUsername(HttpServletRequest request, 
            HttpServletResponse response, 
            String uri, 
            String authValue, 
            String clientUsername) {
        DAppDomain appDomain = null;
        Matcher m = PATH_DOMAIN.matcher(uri);
        if (m.find()) {
            final String domain = m.group(1);
            appDomain = domainService.get(null, domain);
            if (null == appDomain) {
                throw new AuthenticationFailedException(-1, domain);
            }
            request.setAttribute(ATTR_NAME_DOMAIN, appDomain);
        }
        return appDomain;
    }

    // ---------------------- Setters and getters ------------------------------

    @Autowired
    public void setDomainService(CrudService domainService) {
        this.domainService = domainService;
    }

}
