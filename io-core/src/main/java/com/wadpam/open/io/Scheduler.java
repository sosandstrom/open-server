/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import java.io.OutputStream;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sosandstrom
 */
public class Scheduler<D> {
    
    protected static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);
    
    static final String KEY_PRE_EXPORT = "Exporter.Scheduler.preExport";
    static final String KEY_PRE_DAO = "Exporter.Scheduler.preDao";
    static final Integer STATE_PENDING = 0;
    static final Integer STATE_RUNNING = 1;
    static final Integer STATE_DONE = 2;
    
    protected final HashMap<Object, Object> CACHE = new HashMap<Object, Object>();

    protected static Exporter exporter = null;
    
    /**
     * Override to do processing in different thread.
     * This implementation simply calls {@link Exporter#exportDao(java.lang.Object, int, int, int) }.
     * @param preExport
     * @param daoIndex
     * @param offset
     * @param limit 
     */
    public void scheduleExportDao(OutputStream out, int daoIndex, int offset, int limit) {
        LOG.debug("scheduling for dao #{}, {}/{}", new Object[] {
            daoIndex, offset, limit
        });
        exporter.exportDao(out, daoIndex, offset, limit);
    }
    
    /**
     * Override to do postExport in different thread.
     */
    protected void schedulePostExport() {
        LOG.debug("scheduling for postExport");
        Object preExport = getCached(KEY_PRE_EXPORT);
        exporter.postExport(null, null, preExport);
    }
    
    public Object getCached(Object key) {
        return CACHE.get(key);
    }

    public void putCached(Object key, Object value) {
        CACHE.put(key, value);
    }
    
    public static String getDaoKey(int daoIndex) {
        return String.format("Exporter.Scheduler.daoKey.%d", daoIndex);        
    }

    /**
     * @param daoIndex
     * @return 201 Created when all done, 204 No Content 
     */
    public int onDone(int daoIndex) {
        putCached(getDaoKey(daoIndex), STATE_DONE);
        
        int i = 0;
        Object state = null;
        String cacheKey;
        
        // are all Daos exported?
        do {
            cacheKey = getDaoKey(i);
            state = getCached(cacheKey);
            LOG.debug("onDone({}) #{} has state {}", new Object[] 
                {daoIndex, i, state});
            i++;
        } while (STATE_DONE.equals(state));
        
        // done?
        if (null == state) {
            schedulePostExport();
        }
        
        // Created when all done, No Content 
        return null == state ? 201 : 204;
    }
    
    public void preExport(Object arg) {
        
    }

    public void setExporter(Exporter<D> exporter) {
        Scheduler.exporter = exporter;
    }
    
}
