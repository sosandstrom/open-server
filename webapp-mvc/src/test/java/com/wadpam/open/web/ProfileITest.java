package com.wadpam.open.web;

import com.wadpam.open.json.JProfile;
import java.net.URI;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration test for the CRUD controllers.
 * @author sosandtrom
 */
public class ProfileITest {
    static final Logger LOG = LoggerFactory.getLogger(ProfileITest.class);

    protected static final String                  BASE_URL       = "http://localhost:8234/api/itest/";

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
        LOG.info("----------------- setUp() ---------------------------");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateProfile() {
        LOG.info("+ testCreateProfile():");
        final String PHONE = "+85517222165";
        JProfile actual = createProfile(PHONE);
        assertNotNull("Assigned Profile ID", actual.getId());
        assertEquals("Created name", PHONE, actual.getPhoneNumber());
    }
    
    protected JProfile createProfile(String phone) {
        JProfile request = new JProfile();
        request.setPhoneNumber(phone);
        URI uri = template.postForLocation(BASE_URL + "profile/v10", request);
        
        LOG.info("GET {}", uri);
        JProfile actual = template.getForObject(uri, JProfile.class);
        return actual;
    }

    
//    @Test
//    public void testDeleteSample() {
//        final String NAME = "mySampleName";
//        JSample request = new JSample();
//        request.setName(NAME);
//        URI uri = template.postForLocation(BASE_URL + "sample/v10", request);
//        
//        template.delete(uri);
//        
//        try {
//            JSample actual = template.getForObject(uri, JSample.class);
//            fail("Expected NOT_FOUND");
//        }
//        catch (HttpClientErrorException expected) {
//            assertEquals("Delete status", "404 Not Found", expected.getMessage());
//        }
//    }
//    
//    @Test
//    public void testUpdateSample() {
//        final String NAME = "mySampleName";
//        JSample request = new JSample();
//        request.setName("initialName");
//        URI uri = template.postForLocation(BASE_URL + "sample/v10", request);
//        request = template.getForObject(uri, JSample.class);
//        
//        // update
//        request.setName(NAME);
//        ResponseEntity entity = template.postForEntity(uri, request, JSample.class);
//        assertEquals(HttpStatus.NO_CONTENT, entity.getStatusCode());
//        
//        JSample actual = template.getForObject(uri, JSample.class);
//        assertEquals("Updated name", NAME, actual.getName());
//        assertTrue("UpdatedDate", actual.getCreatedDate() < actual.getUpdatedDate());
//    }
//    
//    @Test
//    public void testUpdateSampleForLocation() {
//        final String NAME = "mySampleName";
//        JSample request = new JSample();
//        request.setName("initialName");
//        URI uri = template.postForLocation(BASE_URL + "sample/v10", request);
//        request = template.getForObject(uri, JSample.class);
//        
//        // update
//        request.setName(NAME);
//        URI updatedUri = template.postForLocation(uri.toString() + "?_expects=302", request);
//        assertEquals(uri, updatedUri);
//        
//        JSample actual = template.getForObject(uri, JSample.class);
//        assertEquals("Updated name", NAME, actual.getName());
//        assertTrue("UpdatedDate", actual.getCreatedDate() < actual.getUpdatedDate());
//    }
//    
}
