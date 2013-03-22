package com.wadpam.open.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

/**
 * Integration test for the CRUD controllers.
 * @author sosandtrom
 */
public class ExportITest {

    static final String                  BASE_URL       = CrudITest.BASE_URL;

    RestTemplate                         template;
    public ExportITest() {
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
    public void testExportEmployees() {
        String actual = template.getForObject(BASE_URL + "export/employees", String.class);
        System.out.println(actual);
    }
    
    @Test
    public void testExportAllAsJSON() {
        String actual = template.getForObject(BASE_URL + "export/all.json", String.class);
        System.out.println(actual);
    }
    
    @Test
    public void testExportAllAsXLS() throws IOException {
        byte[] actual = template.getForObject(BASE_URL + "export/all.xls", byte[].class);
        File f = File.createTempFile("OpenServer", ".xls");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(actual);
        fos.close();
        System.out.println(f.getAbsolutePath());
    }
    
}
