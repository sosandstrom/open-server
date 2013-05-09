/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.security;

/**
 *
 * @author sosandstrom
 */
public interface SecurityConfig {
    
    String DELETE = "DELETE";
    String GET = "GET";
    String OPTIONS = "OPTIONS";
    String PATCH = "PATCH";
    String POST = "POST";
    String PUT = "PUT";
    
    /** 
     * Role given if method:path is whitelisted.
     */
    String ANONYMOUS = "ROLE_ANONYMOUS";
    
    /** 
     * Role given if application is authenticated, 
     * usually via Basic Authentication.
     */
    String APPLICATION = "ROLE_APPLICATION";
    
    /** 
     * Role given if user is authenticated, 
     * usually via Basic Authentication or OAuth2.
     */
    String USER = "ROLE_USER";
    

    WhitelistBuilder WHITELIST_BUILDER = new WhitelistBuilder();
}
