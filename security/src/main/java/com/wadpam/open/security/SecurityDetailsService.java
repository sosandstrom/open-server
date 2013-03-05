/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sosandstrom
 */
public interface SecurityDetailsService {

    Object loadUserDetailsByUsername(HttpServletRequest request, 
            HttpServletResponse response, 
            String uri, 
            String authValue, 
            String username);
    
}
