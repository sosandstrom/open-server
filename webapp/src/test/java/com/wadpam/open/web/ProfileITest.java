package com.wadpam.open.web;

import com.wadpam.open.json.JProfile;
import com.wadpam.open.json.JSample;
import java.net.URI;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import static org.junit.Assert.*;

/**
 * Integration test for the CRUD controllers.
 * @author sosandtrom
 */
public class ProfileITest {

    static final String                  BASE_URL       = "http://localhost:8234/domain/itest/";

    RestTemplate                         template;
    public ProfileITest() {
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
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateProfile() {
        final String NAME = "My DisplayName";
        final String USERNAME = "myUsername";
        final String PHONENUMBER = "+46762066731";
        JProfile request = new JProfile();
        request.setUsername(USERNAME);
        request.setDisplayName(NAME);
        request.setPhoneNumber(PHONENUMBER);
        URI uri = template.postForLocation(BASE_URL + "user/v10", request);
        
        JProfile actual = template.getForObject(uri, JProfile.class);
        assertNotNull("Assigned ID", actual.getId());
        assertEquals("Display Name", NAME, actual.getDisplayName());
        assertEquals("username", USERNAME, actual.getUsername());
        assertEquals("Phone", PHONENUMBER, actual.getPhoneNumber());
    }
    
}
