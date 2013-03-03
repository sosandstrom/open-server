/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.web;

import com.wadpam.open.domain.DAppDomain;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author sosandstrom
 */
public class BasicAuthenticationInterceptor extends DomainInterceptor {

    // Basic authentication
    private static final String BASIC_AUTH_PARAM_NAME = "jBasic";
    private static final String BASIC_AUTH_PREFIX = "Basic ";

    private static final Base64 B64 = new Base64();

    private String realmName = "Open-Server";
    
    protected String doAuthenticate(HttpServletRequest request, HttpServletResponse response) {
        String username = null;

        // Find authorization in header?
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (null == authorizationHeader) {
            // Alternative way of getting the basic authentication from a parameter
            // Useful with javascript and jquery and some other platforms
            authorizationHeader = String.format("%s%s", BASIC_AUTH_PREFIX, request.getParameter(BASIC_AUTH_PARAM_NAME));
        }
        LOG.debug("Incoming authentication header value:{}", authorizationHeader);

        DAppDomain dAppDomain = (DAppDomain) request.getAttribute(ATTR_NAME_DOMAIN);
        if (null != authorizationHeader && null != dAppDomain) {
            try {
                username = verifyBasicAuthentication(authorizationHeader, dAppDomain);
            } catch (NoSuchAlgorithmException ex) {
                LOG.error("Basic doAuthenticate", ex);
            }
        }
        
        if (null == username) {
            // No match initiate basic authentication with the client
            response.setStatus(401);
            response.setHeader("WWW-Authenticate", 
                    String.format("Basic realm=\"%s\"", realmName));
        }
        
        return username;
    }

    // Authenticate using basic authentication
    protected String verifyBasicAuthentication(String authorizationHeader, DAppDomain dAppDomain)
            throws NoSuchAlgorithmException {

        // Strip away the "Basic " part
        if (authorizationHeader.startsWith(BASIC_AUTH_PREFIX)) {
            authorizationHeader = authorizationHeader.substring(BASIC_AUTH_PREFIX.length());
        } else {
            return null;
        }

        final String expected = encode(dAppDomain.getUsername(), dAppDomain.getPassword());
        // correct username and password?
        LOG.debug("=== checking {} vs {} ===", expected, authorizationHeader);
        return expected.equals(authorizationHeader) ? dAppDomain.getUsername() : null;
    }

    // Build basic authentication string
    private static String encode(String username, String password) {
        final String decoded = String.format("%s:%s", username, password);
        return B64.encodeAsString(decoded.getBytes());
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

}
