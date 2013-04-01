/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sosandstrom
 */
public class InMemorySecurityDetailsService implements SecurityDetailsService {
    
    public final HashMap<String, String> DETAILS_MAP = new HashMap<String, String>();

    @Override
    public Object loadUserDetailsByUsername(HttpServletRequest request, 
            HttpServletResponse response, 
            String uri, 
            String authValue, 
            String username) {
        if (DETAILS_MAP.containsKey(username)) {
            return username;
        }
        return null;
    }

    @Override
    public Collection<String> getRolesFromUserDetails(Object details) {
        return null != details ? Arrays.asList(ROLE_USER) : Collections.EMPTY_LIST;
    }

}
