package com.wadpam.open.web;

import com.wadpam.open.domain.DomainHelper;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Integration test for the monitor controller.
 * @author sosandtrom
 */
public class SecurityITest {
    static final Logger LOG = LoggerFactory.getLogger(SecurityITest.class);

    static final String                  BASE_URL       = CrudITest.BASE_URL;

    RestTemplate                         template;
    public SecurityITest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        template = new RestTemplate();
        LOG.info("---------------------- setUp() ------------------------------");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSecurityNoCredentials() {
        LOG.info("+ testSecurityNoCredentials");
        try {
            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", DomainHelper.J_BASIC_ITEST);
            HttpEntity requestEntity = new HttpEntity(headers);
            ResponseEntity<String> entity = template.exchange(BASE_URL + "security", 
                    HttpMethod.GET,
                    requestEntity,
                    String.class);
            fail("Expected 401 Unauthorized");
        }
        catch (HttpClientErrorException expected) {
            assertEquals("401 Unauthorized", expected.getMessage());
        }
    }

    @Test
    public void testSecurityForbidden() {
        LOG.info("+ testSecurityForbidden");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", DomainHelper.J_BASIC_ITEST);
            HttpEntity requestEntity = new HttpEntity(headers);
            ResponseEntity<String> entity = template.exchange(BASE_URL + "security", 
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            fail("Expected 403 Forbidden");
        }
        catch (HttpClientErrorException expected) {
            assertEquals("403 Forbidden", expected.getMessage());
        }
    }

    @Test
    public void testSecurityApplication() {
        LOG.info("+ testSecurityApplication");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", DomainHelper.J_BASIC_ITEST);
        HttpEntity requestEntity = new HttpEntity(headers);
        ResponseEntity<String> entity = template.exchange(BASE_URL + "security", 
                HttpMethod.GET,
                requestEntity,
                String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testSecurityFullyAuthenticated() {
        LOG.info("+ testSecurityFullyAuthenticated");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", DomainHelper.J_BASIC_ITEST);
        HttpEntity requestEntity = new HttpEntity(headers);
        ResponseEntity<String> entity = template.exchange(BASE_URL + "security?access_token={access_token}", 
                HttpMethod.POST,
                requestEntity,
                String.class,
                "itest");
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

}
