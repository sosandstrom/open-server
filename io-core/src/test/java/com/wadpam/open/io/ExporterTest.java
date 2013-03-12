/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.open.io;

import static com.wadpam.open.io.Scheduler.KEY_PRE_EXPORT;
import static com.wadpam.open.io.Scheduler.LOG;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author os
 */
public class ExporterTest extends Scheduler<ExporterTest> 
        implements Converter<ExporterTest>, Extractor<ExporterTest> {
    
    final static String PRE_EXPORT = "preExport";
    
    final static ExporterTest DAO0 = new ExporterTest("Dao0");
    final static ExporterTest DAO1 = new ExporterTest("Dao1");
    final static ExporterTest DAO2 = new ExporterTest("Dao2");
    final static ExporterTest DAO3 = new ExporterTest("Dao3");
    final static ExporterTest DAO4 = new ExporterTest("Dao4");
    final static ExporterTest DAO5 = new ExporterTest("Dao5");
    
    final static ExporterTest[] DAOS = {DAO0, DAO1, DAO2, DAO3, DAO4, DAO5};
    final String name;
    
    private HashMap<Integer, FutureTask<Integer>> FUTURES = new HashMap<Integer, FutureTask<Integer>>();
    
    public ExporterTest() {
        this("Test");
        setExporter(new Exporter<ExporterTest>(DAOS));
    }

    protected ExporterTest(String name) {
        this.name = name;
    }
    
    @Before
    public void setUp() {
        CACHE.clear();
        exporter.setScheduler(this);
        exporter.setConverter(this);
        exporter.setExtractor(this);
        LOG.info("-------------- setUp() --------------------");
    }
    
    @After
    public void tearDown() {
        LOG.info("-------------- tearDown() --------------------");
    }

     @Test
     public void testExport() throws InterruptedException, ExecutionException {
         Object preExport = exporter.export(null, null);
         assertEquals(PRE_EXPORT, preExport);
         Object actual = getCached(KEY_PRE_EXPORT);
         assertEquals(PRE_EXPORT, actual);
         
         Object state;
         String key;
         for (int i = 0; i < 6; i++) {
             key = getDaoKey(i);
             state = getCached(key);
             assertEquals(STATE_PENDING, state);
         }
         
         // join all futures to complete
         for (Entry<Integer, FutureTask<Integer>> entry : FUTURES.entrySet()) {
             state = entry.getValue().get();
             assertEquals(STATE_DONE, state);
         }
     }
     
     // ----------------  Scheduler methods ------------------------------

    @Override
    public void scheduleExportDao(final OutputStream out, 
            final int daoIndex, final int offset, final int limit) {
        LOG.info("Scheduler.exportDao #{}, {}/{}", new Object[] {
            daoIndex, offset, limit
        });
        
        // called when scheduling
        final String keyDao = getDaoKey(daoIndex);
        if (0 == offset) {
            assertNull(getCached(keyDao));
            putCached(keyDao, STATE_PENDING);
        }
        else {
            assertEquals(STATE_RUNNING, getCached(keyDao));
        }
        
        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                Thread.sleep(4000 + Math.round(Math.random()*4000));
                exporter.exportDao(out, daoIndex, offset, limit);
//                if (250 == offset) {
                    putCached(keyDao, STATE_DONE);
                    
                    return STATE_DONE;
//                }
//                else {
//                    scheduleExportDao(preExport, daoIndex, 
//                            offset+limit, limit);
//                    return STATE_RUNNING;
//                }
            }
        });
        FUTURES.put(daoIndex, task);
        Thread taskThread = new Thread(task);
        taskThread.start();
    }

   // ----------------- Converter methods --------------------------------

    @Override
    public Object preExport(OutputStream out, Object arg, Object preExport, ExporterTest[] daos) {
        return null;
    }

    @Override
    public Object postExport(OutputStream out, Object arg, Object preExport, Object postExport, ExporterTest[] daos) {
        LOG.info("========== Converter.postExport");
        return null;
    }

    @Override
    public Object preDao(OutputStream out, Object arg, Object preExport, Object preDao, String tableName, Iterable<String> columns, Map<String, String> headers, int daoIndex, ExporterTest dao) {
        LOG.info("preDao for {}", dao.name);
        return null;
    }

    @Override
    public Object postDao(OutputStream out, Object arg, Object preExport, Object preDao, Object postDao, ExporterTest dao) {
        LOG.info("=== Contverter.postDao for {}", dao.name);
        return null;
    }

    @Override
    public Object writeValues(OutputStream out, Object arg, Object preExport, Object preDao, Iterable<String> columns, int daoIndex, ExporterTest dao, int entityIndex, Object entity, Map<String, Object> values) {
//        LOG.debug("writeValues {} has {}", dao.name, values);
        return null;
    }
    
    // -----------------             Extractor methods     ---------------------

    @Override
    public Iterable<String> getColumns(Object arg, ExporterTest dao) {
        return Arrays.asList("Id", dao.name);
    }

    @Override
    public Map<String, String> getHeaderNames(Object arg, ExporterTest dao) {
        return null;
    }

    @Override
    public Map<String, Object> getValues(Object arg, ExporterTest dao, Object entity) {
        Integer[] cols = (Integer[]) entity;
        HashMap val = new HashMap();
        val.put("Id", cols[0]);
        val.put(dao.name, cols[1]);
        return val;
    }

    @Override
    public String getTableName(Object arg, ExporterTest dao) {
        return dao.name;
    }

    @Override
    public Object postDao(Object arg, Object preExport, Object preDao, ExporterTest dao) {
        LOG.info("=== Extractor.postDao for {}", dao.name);
        return null;
    }

    @Override
    public Object postExport(Object arg, Object preExport, ExporterTest[] daos) {
        LOG.info("========== Extractor.postExport");
        return null;
    }

    @Override
    public Object preDao(Object arg, Object preExport, ExporterTest dao) {
        return String.format("pre%s", dao.name);
    }

    @Override
    public Object preExport(Object arg, ExporterTest[] daos) {
        assertEquals(DAOS.length, daos.length);
        return PRE_EXPORT;
    }

    @Override
    public Iterable queryIterable(Object arg, ExporterTest dao, int offset, int limit) {
        ArrayList list = new ArrayList();
        if (limit < 1) {
            limit = 500;
        }
        for (int i = offset; i < offset+limit && i < 317; i++) {
            list.add(new Integer[] {i, 1000+i});
        }
        return list;
    }
}