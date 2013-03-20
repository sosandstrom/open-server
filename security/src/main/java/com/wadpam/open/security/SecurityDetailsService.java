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
