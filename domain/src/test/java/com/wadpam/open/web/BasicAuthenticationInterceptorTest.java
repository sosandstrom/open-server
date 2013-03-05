/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.web;

import com.wadpam.open.domain.DAppDomain;
import com.wadpam.open.security.SecurityDetailsService;
import com.wadpam.open.security.SecurityInterceptor;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 *
 * @author sosandstrom
 */
public class BasicAuthenticationInterceptorTest extends TestCase {
    static final Logger LOG = LoggerFactory.getLogger(BasicAuthenticationInterceptorTest.class);

    static final String URI = "/api/domain/resource/v10";
    BasicAuthenticationInterceptor instance;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        instance = new BasicAuthenticationInterceptor();
        instance.setSecurityDetailsService(new SecurityDetailsService() {
            @Override
            public Object loadUserDetailsByUsername(HttpServletRequest request, 
                    HttpServletResponse response, 
                    String uri, String authValue, String username) {
                if (URI.equals(uri)) {
                    DAppDomain appDomain = new DAppDomain();
                    appDomain.setId("domain");
                    appDomain.setUsername("username");
                    appDomain.setPassword("password");

                    return appDomain;
                }
                return null;
            }
        });
        LOG.info("-----           setUp() {}           -----", getName());
    }

    @Override
    protected void tearDown() throws Exception {
        LOG.info("+++++           tearDown() {}        +++++", getName());
        super.tearDown();
    }

    public void testIsAuthenticatedNoCredentials() {
        final String authValue = null;
        String actual = instance.isAuthenticated(null, null, null, 
                URI, "GET", authValue);
        assertNull(actual);
    }
    
    public void testIsWhitelisted() {
        Entry<String, Collection<String>> forUri = new AbstractMap.SimpleImmutableEntry(
                String.format("\\A%s\\z", URI),
                Arrays.asList("GET"));
        Collection<Entry<String, Collection<String>>> whitelistedMethods = Arrays.asList(forUri);
        instance.setWhitelistedMethods(whitelistedMethods);
        final String authValue = null;
        MockHttpServletRequest request = new MockHttpServletRequest();
        String actual = instance.isAuthenticated(request, null, null, 
                URI, "GET", authValue);
        assertEquals("whitelisted", SecurityInterceptor.USERNAME_ANONYMOUS, actual);
        assertNull(request.getAttribute(SecurityInterceptor.ATTR_NAME_USERNAME));
    }
    
    public void testIsAuthenticatedNoSuchUser() {
        final String authValue = "test:test";
        String actual = instance.isAuthenticated(null, null, null, 
                URI, "GET", authValue);
        assertNull("Wrong username", actual);
    }
    
    public void testIsAuthenticatedWrongPassword() {
        final String authValue = "username:test";
        String actual = instance.isAuthenticated(null, null, null, 
                URI, "GET", authValue);
        assertNull("Wrong password", actual);
    }
    
    public void testIsAuthenticated() {
        final String authValue = "username:password";
        MockHttpServletRequest request = new MockHttpServletRequest();
        String actual = instance.isAuthenticated(request, null, null, 
                URI, "GET", authValue);
        assertEquals("OK", "username", actual);
        assertEquals("ATTR", "username", request.getAttribute(SecurityInterceptor.ATTR_NAME_USERNAME));
    }
    
}
