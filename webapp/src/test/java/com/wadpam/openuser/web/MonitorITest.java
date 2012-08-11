package com.wadpam.openuser.web;

import com.wadpam.openuser.json.JMonitor;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author os
 */
public class MonitorITest {

    static final String                  BASE_URL       = "http://localhost:8234/domain/";

    RestTemplate                         template;
    public MonitorITest() {
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
    public void testMonitor() {
        ResponseEntity<JMonitor> entity = template.getForEntity(BASE_URL + "monitor/v10", JMonitor.class);
        assertEquals("getMonitor", HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testMonitorNamespace() {
        ResponseEntity<JMonitor> entity = template.getForEntity(BASE_URL + "monitor/v10", JMonitor.class);
        assertEquals("getMonitor", HttpStatus.OK, entity.getStatusCode());
        assertEquals("getMonitor namespace", "monitor", entity.getBody().getNamespace());
    }

    @Test
    public void testMonitorJsonp() {
        ResponseEntity<String> entity = template.getForEntity(BASE_URL + "monitor/v10?callback=itest", String.class);
        assertEquals("getMonitorJsonp", HttpStatus.OK, entity.getStatusCode());
        assertTrue("getMonitorJsonp callback", entity.getBody().startsWith("itest({"));
    }

}
