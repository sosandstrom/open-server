package com.wadpam.open.mvc;

import java.util.HashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author os
 */
public class MultiplexCrudServiceTest {
    
    final MultiplexCrudService alpha = new MultiplexCrudService("alpha");
    final MultiplexCrudService beta = new MultiplexCrudService("beta");
    final MultiplexCrudService delta = new MultiplexCrudService("delta");
    
    MultiplexCrudService mux;
    
    public MultiplexCrudServiceTest() {
    }
    
    @Before
    public void setUp() {
        mux = new MultiplexCrudService("mux");
        HashMap map = new HashMap();
        mux.setMultiplexMap(map);
        map.put("alpha", alpha);
        map.put("beta", beta);
        
        mux.setDefaultService(delta);
        
        MultiplexCrudService.setMultiplexKey(null);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testKeyNull() {
        assertNull(mux.getMultiplexKey());
        assertEquals(delta, mux.getDelegate());
    }
    
    @Test
    public void testFlatKey() {
        MultiplexCrudService.setMultiplexKey("alpha");
        assertEquals("alpha", mux.getMultiplexKey());
        assertEquals(alpha, mux.getDelegate());
    }
    
    @Test
    public void testFlatNotFound() {
        MultiplexCrudService.setMultiplexKey("notFound");
        assertEquals("notFound", mux.getMultiplexKey());
        assertEquals(delta, mux.getDelegate());
    }
    
    @Test
    public void testMappedKey() {
        MultiplexCrudService.setMultiplexKey("{\"mux\":\"alpha\",\"mix\":\"beta\",\"max\":\"max\"}");
        assertEquals("alpha", mux.getMultiplexKey());
        assertEquals(alpha, mux.getDelegate());
    }
    
    @Test
    public void testMapped2Key() {
        MultiplexCrudService.setMultiplexKey("{\"mux\":\"beta\",\"mix\":\"alpha\",\"max\":\"max\"}");
        assertEquals("beta", mux.getMultiplexKey());
        assertEquals(beta, mux.getDelegate());
    }
    
    @Test
    public void testKeyNotMapped() {
        MultiplexCrudService.setMultiplexKey("{\"mux\":\"notFound\",\"mix\":\"beta\",\"max\":\"max\"}");
        assertEquals("notFound", mux.getMultiplexKey());
        assertEquals(delta, mux.getDelegate());
    }
    
    @Test
    public void testServiceKeyNotFound() {
        MultiplexCrudService.setMultiplexKey("{\"mix\":\"beta\",\"max\":\"max\"}");
        assertNull(mux.getMultiplexKey());
        assertEquals(delta, mux.getDelegate());
    }
    
}