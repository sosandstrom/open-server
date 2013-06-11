/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.security;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sosandstrom
 */
public interface SecurityDetailsService {

    /** 
     * Role given if method:path is whitelisted.
     */
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    
    /** 
     * Role given if application is authenticated, 
     * usually via Basic Authentication.
     */
    public static final String ROLE_APPLICATION = "ROLE_APPLICATION";
    
    /** 
     * Role given if user is authenticated, 
     * usually via Basic Authentication or OAuth2.
     */
    public static final String ROLE_USER = "ROLE_USER";
    
    /** 
     * Role given if user is authenticated via 
     * Container-based security
     */
    public static final String ROLE_CONTAINER_ADMIN = "ROLE_CONTAINER_ADMIN";
    
    /**
     * Loads the user details for the specified username.
     * @param request
     * @param response
     * @param uri
     * @param authValue
     * @param username
     * @return the user details for the specified username.
     */
    Object loadUserDetailsByUsername(HttpServletRequest request, 
            HttpServletResponse response, 
            String uri, 
            String authValue, 
            String username);
    
    /**
     * Extracts the granted roles for the specified user details.
     * @param details the user details containing the roles
     * @return the extracted, granted roles for the specified user details.
     * @since version 19
     */
    Collection<String> getRolesFromUserDetails(Object details);
}
