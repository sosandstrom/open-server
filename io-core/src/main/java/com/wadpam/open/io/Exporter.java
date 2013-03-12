package com.wadpam.open.io;

import static com.wadpam.open.io.Scheduler.LOG;
import java.io.OutputStream;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Export "tables" using the specified Dao objects.
 * This Exporter uses an Extractor to pull the data using Daos,
 * and a Converter to serialize the output to different formats.
 * @author os
 * @see Extractor to access the data
 * @see Converter to serialize the output
 */
public class Exporter<D> {
    
    static final Logger LOG = LoggerFactory.getLogger(Exporter.class);
    
    private Converter<D> converter;
    private Extractor<D> extractor;
    
    private Scheduler<D> scheduler = new Scheduler();
    
    private final D[] daos;
    
    public Exporter(D[] daos) {
        this.daos = daos;
        scheduler.setExporter(this);
    }

    public Exporter() {
        this(null);
    }
    
    
    
    public Object export(OutputStream out, Object arg) {
        return export(out, arg, daos);
    }
    
    /**
     * Entry point for export of multiple "tables". Has the following flow:
     * <ol>
     * <li>call the extractor's pre-export callback</li>
     * <li>call the converter's pre-export callback</li>
     * <li>iterate the Daos invoking the {@link exportDao} method</li>
     * <li>call the extractor's post-export callback</li>
     * <li>call the converter's post-export callback</li>
     * </ol>
     * @param out the output stream to write to
     * @param arg user argument, will be passed around
     * @param daos the DAOs to export
     * @see #exportDao
     */
    public Object export(OutputStream out, Object arg, D... daos) {
        LOG.info("exporting for {} daos", daos.length);
        
        // first, initialize converter
        Object preExport = extractor.preExport(arg, daos);
        scheduler.putCached(Scheduler.KEY_PRE_EXPORT, preExport);
        Object logPre = converter.preExport(out, arg, preExport, daos);
        if (null != logPre) {
            LOG.debug("{}", logPre);
        }
        scheduler.preExport(arg);
        
        int daoIndex = 0;
        for (D dao : daos) {
            exportDao(out, arg, preExport, daoIndex, dao);
            daoIndex++;
        }
        return preExport;
    }
        
    /**
     * Called by the Controller.
     * @param out
     * @param arg
     * @param preExport
     * @return 
     */
    protected Object postExport(OutputStream out, Object arg, Object preExport) {
        // close converter
        Object postExport = extractor.postExport(arg, preExport, daos);
        Object logPost = converter.postExport(out, arg, preExport, postExport, daos);
        if (null != logPost) {
            LOG.debug("{}", logPost);
        }
        return logPost;
    }
    
    /**
     * Entry point for export of single "table". Has the following flow:
     * <ol>
     * <li>call the extractor's pre-dao callback</li>
     * <li>call the converter's pre-dao callback</li>
     * <li>iterate the Entities invoking the {@link exportEntity} method</li>
     * <li>call the extractor's post-dao callback</li>
     * <li>call the converter's post-dao callback</li>
     * </ol>
     * @param out the output stream to write to
     * @param arg user argument, will be passed around
     * @param preExport returned value from extractor's pre-export callback
     * @param daoIndex number of output DAOs prior to this one, i.e. starts on 0
     * @param dao the DAO to export
     * @see #exportEntity
     */
    public void exportDao(OutputStream out, Object arg, Object preExport, int daoIndex, D dao) {
        scheduler.scheduleExportDao(out, daoIndex, 0, -1);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Invoked by Controller
     * @param preExport
     * @param daoIndex
     * @param offset
     * @param limit
     * @return true when done
     */
    public Integer exportDao(OutputStream out, int daoIndex, int offset, int limit) {
        return exportDaoImpl(out, null, null, daoIndex, daos[daoIndex], offset, limit);
    }
    
    /**
     * The implementation of the export of a Dao.
     * @return true when done
     */
    public Integer exportDaoImpl(OutputStream out, Object arg, Object preExport, 
            final int daoIndex, D dao, final int offset, final int limit) {
        LOG.info("Exporter.exportDao #{}, {}/{}", new Object[] {
            daoIndex, offset, limit
        });
        
        // prepare converter for dao
        String tableName = extractor.getTableName(arg, dao);
        Iterable<String> columns = extractor.getColumns(arg, dao);
        LOG.debug("{} has columns {}", tableName, columns);
        
        Object preDao = null;
        if (0 == offset) {
            preDao = extractor.preDao(arg, preExport, dao);
            scheduler.putCached(Scheduler.KEY_PRE_DAO, preDao);
            Map<String, String> headers = extractor.getHeaderNames(arg, dao);
            Object logPreDao = converter.preDao(out, arg, preExport, preDao, tableName, 
                    columns, headers, daoIndex, dao);
            if (null != logPreDao) {
                LOG.debug("{} {}", dao, logPreDao);
            }
        }
        else {
            // fetch preDao from MemCache
            preDao = scheduler.getCached(Scheduler.KEY_PRE_DAO);
        }

        int entityIndex = 0;
        Object log;
        int returned = 0;

        LOG.debug("----- query {} items from {} with {} returned.", new Object[] {limit, tableName, returned});
        Iterable entities = extractor.queryIterable(arg, dao, offset, limit);
        for (Object entity : entities) {
            log = exportEntity(out, arg, preExport, preDao, columns, daoIndex, dao, 
                    entityIndex, entity);
            if (null != log) {
                entityIndex++;
            }
            returned++;
        }

        // more?
        if (limit == returned) {
            return offset + limit;
        }
        
        // done, so close converter for dao
        Object postDao = extractor.postDao(arg, preExport, preDao, dao);
        Object logPostDao = converter.postDao(out, arg, preExport, preDao, postDao, dao);
        if (null != logPostDao) {
            LOG.debug("{} {}", dao, logPostDao);
        }
        scheduler.onDone(daoIndex);
        return null;
    }
    
    /**
     * Exports one Entity
     * @param out the output stream to write to
     * @param arg user argument, will be passed around
     * @param preExport returned value from extractor's pre-export callback
     * @param daoIndex number of output DAOs prior to this one, i.e. starts on 0
     * @param dao the DAO to export
     * @param preDao returned value from extractor's pre-dao callback
     * @param columns returned value from {@link Extractor.getColumns}
     * @param entityIndex number of output Entities prior to this one, i.e. starts on 0
     * @param entity the Entity to export
     * @return returned value from {@link Converter.writeValues}
     */
    protected Object exportEntity(OutputStream out, Object arg, Object preExport, 
            Object preDao, Iterable<String> columns, int daoIndex, D dao, int entityIndex, Object entity) {
        Map<String, Object> values = extractor.getValues(arg, dao, entity);
        return converter.writeValues(out, arg, preExport, preDao, columns, 
                daoIndex, dao, entityIndex, entity, values);
    }

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public Extractor getExtractor() {
        return extractor;
    }

    public void setExtractor(Extractor extractor) {
        this.extractor = extractor;
    }

    public void setScheduler(Scheduler<D> scheduler) {
        this.scheduler = scheduler;
    }

}
