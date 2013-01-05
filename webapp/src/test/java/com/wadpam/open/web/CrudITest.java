package com.wadpam.open.web;

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
public class CrudITest {

    static final String                  BASE_URL       = "http://localhost:8234/domain/itest/";

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
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateSample() {
        JSample jSample = new JSample();
        jSample.setName("mySampleName");
        URI uri = template.postForLocation(BASE_URL + "sample/v10", jSample);
        System.out.println("Sample URI = " + uri);
        assertNotNull("Sample location", uri);
        assertTrue("Relative Sample URI", uri.getPath().startsWith("/domain/itest/sample/v10/"));
    }

}
