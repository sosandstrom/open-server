/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.open.io;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author os
 */
public class ImporterTest {
    
    static final Collection<String> COLUMNS = Arrays.asList("Name", "Email", "Phone");
    
    Importer importer;
    ValidationHandler chunkinHandler;
    
    public ImporterTest() {
    }
    
    @Before
    public void setUp() {
        importer = new Importer();
        Map<String, ValidationHandler> handlerMap = new HashMap<String, ValidationHandler>();
        ValidationHandlerAdapter userHandler = new ValidationHandlerAdapter();
        userHandler.setValidColumns(COLUMNS);
        handlerMap.put("com.wadpam.open.dao.DUserDaoBean", userHandler);
        
        chunkinHandler = new ClasspathChunkingHandler(handlerMap, 5);
    }
    
    @After
    public void tearDown() {
    }

     @Test
     public void testImportMaster() throws Exception {
         InputStream in = getClass().getResourceAsStream("/master.csv");
        Map<String, ValidationHandler> handlerMap = new HashMap<String, ValidationHandler>();
         
         importer.importMaster(in, handlerMap, chunkinHandler);
         assertNotNull(importer);
     }
}
