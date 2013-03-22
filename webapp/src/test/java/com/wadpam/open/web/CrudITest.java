package com.wadpam.open.web;

import com.wadpam.open.json.JComplex;
import com.wadpam.open.json.JSample;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Integration test for the CRUD controllers.
 * @author sosandtrom
 */
public class CrudITest {
    static final Logger LOG = LoggerFactory.getLogger(CrudITest.class);

    protected static final String                  BASE_URL       = MonitorITest.BASE_URL + "itest/";

    RestTemplate                         template;
    public CrudITest() {
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
    public void testCreateSample() {
        LOG.info("+ testCreateSample():");
        final String NAME = "mySampleName";
        JSample actual = createSample(NAME);
        assertNotNull("Assigned Sample ID", actual.getId());
        assertEquals("Created name", NAME, actual.getName());
    }
    
    protected JSample createSample(String name) {
        JSample request = new JSample();
        request.setName(name);
        URI uri = template.postForLocation(BASE_URL + "sample/v10", request);
        
        LOG.info("GET {}", uri);
        JSample actual = template.getForObject(uri, JSample.class);
//        JSample actual = template.postForObject(BASE_URL + "sample/v10", request, JSample.class);
        return actual;
    }

    @Test
    public void testCreateComplex() {
        LOG.info("+ testCreateComplex():");
        final String ORG_NAME = "myOrganizationName";
        JSample org = createSample(ORG_NAME);
        
        final String NAME = "myComplexName";
        JComplex request = new JComplex();
        request.setName(NAME);
        request.setManager(null);
        request.setOrganizationKey(Long.parseLong(org.getId()));
        URI uri = template.postForLocation(BASE_URL + "complex/v10", request);
        
        JComplex actual = template.getForObject(uri, JComplex.class);
        assertNotNull("Complex", actual);
        assertEquals("Complex organizationId", (Long) Long.parseLong(org.getId()), actual.getOrganizationKey());
    }
    
    @Test
    public void testDeleteSample() {
        final String NAME = "mySampleName";
        JSample request = new JSample();
        request.setName(NAME);
        URI uri = template.postForLocation(BASE_URL + "sample/v10", request);
        
        template.delete(uri);
        
        try {
            JSample actual = template.getForObject(uri, JSample.class);
            fail("Expected NOT_FOUND");
        }
        catch (HttpClientErrorException expected) {
            assertEquals("Delete status", "404 Not Found", expected.getMessage());
        }
    }
    
    @Test
    public void testUpdateSample() {
        final String NAME = "mySampleName";
        JSample request = new JSample();
        request.setName("initialName");
        URI uri = template.postForLocation(BASE_URL + "sample/v10", request);
        request = template.getForObject(uri, JSample.class);
        
        // update
        request.setName(NAME);
        ResponseEntity entity = template.postForEntity(uri, request, JSample.class);
        assertEquals(HttpStatus.NO_CONTENT, entity.getStatusCode());
        
        JSample actual = template.getForObject(uri, JSample.class);
        assertEquals("Updated name", NAME, actual.getName());
        assertTrue("UpdatedDate", actual.getCreatedDate() < actual.getUpdatedDate());
    }
    
    @Test
    public void testUpdateSampleForLocation() {
        final String NAME = "mySampleName";
        JSample request = new JSample();
        request.setName("initialName");
        URI uri = template.postForLocation(BASE_URL + "sample/v10", request);
        request = template.getForObject(uri, JSample.class);
        
        // update
        request.setName(NAME);
        URI updatedUri = template.postForLocation(uri.toString() + "?_expects=302", request);
        assertEquals(uri, updatedUri);
        
        JSample actual = template.getForObject(uri, JSample.class);
        assertEquals("Updated name", NAME, actual.getName());
        assertTrue("UpdatedDate", actual.getCreatedDate() < actual.getUpdatedDate());
    }
    
}
