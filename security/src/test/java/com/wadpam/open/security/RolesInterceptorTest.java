/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.open.security;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author os
 */
public class RolesInterceptorTest extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(RolesInterceptorTest.class);
    
    final Map.Entry<String, Collection<String>> updateUser = new AbstractMap.SimpleImmutableEntry(
                "\\APOST:/api/[^/]+/user/v10/\\d+\\z", Arrays.asList("ROLE_USER"));
    final Map.Entry<String, Collection<String>> getUser = new AbstractMap.SimpleImmutableEntry(
                "\\A[^:]+:/api/[^/]+/user/v10/\\d+\\z", Arrays.asList("ROLE_ANONYMOUS", "ROLE_USER"));
    final Map.Entry<String, Collection<String>> getCountries = new AbstractMap.SimpleImmutableEntry(
                "\\AGET:/api/[^/]+/country/v10\\z", Arrays.asList("ROLE_ANONYMOUS", "ROLE_USER"));
    final Map.Entry<String, Collection<String>> catchAll = new AbstractMap.SimpleImmutableEntry(
                "\\A[^:]+:/api/[^/]+/", Arrays.asList("ROLE_CATCH_ALL"));
    final Collection<Map.Entry<String, Collection<String>>> ruledMethods = Arrays.asList(
            updateUser, getUser, getCountries, catchAll);
    
    final ArrayList<Map.Entry<Pattern, Set<String>>> RULED_METHODS = 
            new ArrayList<Map.Entry<Pattern, Set<String>>>();
    
    public RolesInterceptorTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        SecurityInterceptor.setListedMethods(ruledMethods, RULED_METHODS);
        LOG.info("--------------- setUp() {} ---------------------", getName());
    }
    
    @Override
    protected void tearDown() throws Exception {
        LOG.info("--------------- tearDown() {} ---------------------", getName());
    }

    public void testSpecificMethod() {
        boolean update = RolesInterceptor.isAuthorized(RULED_METHODS, "POST", 
                "/api/test/user/v10/123", Arrays.asList("ROLE_USER", "ROLE_BACKOFFICE"));
        assertTrue("UpdateUser", update);
        boolean get = RolesInterceptor.isAuthorized(RULED_METHODS, "GET", 
                "/api/test/user/v10/123", Arrays.asList("ROLE_USER", "ROLE_BACKOFFICE"));
        assertTrue("GetUser", get);
        boolean getAnonymous = RolesInterceptor.isAuthorized(RULED_METHODS, "GET", 
                "/api/test/user/v10/123", Arrays.asList("ROLE_ANONYMOUS"));
        assertTrue("GetUserAnonymous", getAnonymous);
    }
    
    public void testGetUserForbidden() {
        boolean forbidden = RolesInterceptor.isAuthorized(RULED_METHODS, "GET", 
                "/api/test/user/v10/123", Collections.EMPTY_LIST);
        assertFalse("GetUserForbidden", forbidden);
    }
}
