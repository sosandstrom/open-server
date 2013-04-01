/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.domain;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.wadpam.open.json.JAppDomain;
import java.util.Map;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author sosandstrom
 */
public class DomainHelper {
    public static final String DOMAIN_ITEST = "itest";
    static final String BASE_PATH = "/api/" + DOMAIN_ITEST;
    static final String ADMIN_DEFAULT = "/api/default/_admin";
    static final String DOMAIN_PATH = ADMIN_DEFAULT + "/domain/v10";
    public static final String BASIC_ITEST_VALUE = "aXRlc3Q6aXRlc3Q=";
    public static final String J_BASIC_ITEST = "Basic " + BASIC_ITEST_VALUE;

    public static String getEndpoints(final String BASE_URL) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> endpoints = restTemplate.getForObject(BASE_URL + "/api/default/endpoints", 
                Map.class);
        return endpoints.get("apiUrl");
    }
    
    public static JAppDomain upsertITestDomain(final String BASE_URL) {
        final LocalServiceTestHelper userServiceHelper = 
            new LocalServiceTestHelper(new LocalUserServiceTestConfig());
        userServiceHelper.setUp();
        userServiceHelper.setEnvIsAdmin(true).setEnvIsLoggedIn(true);

        RestTemplate restTemplate = new RestTemplate();

        JAppDomain itest = null;
        try {
            itest = restTemplate.getForObject(BASE_URL + DOMAIN_PATH + "/{id}", 
                JAppDomain.class, DOMAIN_ITEST);
        }
        catch (HttpClientErrorException notFound) {
            // create the AppDomain
            itest = new JAppDomain();
            itest.setId(DOMAIN_ITEST);
            itest.setDescription("domain for itests");
            itest.setUsername(DOMAIN_ITEST);
            itest.setPassword(DOMAIN_ITEST);
            restTemplate.postForLocation(BASE_URL + DOMAIN_PATH, itest);
        }
        
        userServiceHelper.setEnvIsLoggedIn(false);
        
        return itest;
    }
    
}
