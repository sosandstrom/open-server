package com.wadpam.open.io;

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
        
        // first, initialize converter
        Object preExport = extractor.preExport(arg, daos);
        Object logPre = converter.preExport(out, arg, preExport, daos);
        if (null != logPre) {
            LOG.debug("{}", logPre);
        }
        
        int daoIndex = 0;
        for (D dao : daos) {
            exportDao(out, arg, preExport, daoIndex, dao);
            daoIndex++;
        }
        
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
    protected void exportDao(OutputStream out, Object arg, Object preExport, int daoIndex, D dao) {
        exportDaoImpl(out, arg, preExport, daoIndex, dao, 0, -1);
    }
    
    /**
     * The implementation of the export of a Dao.
     */
    protected void exportDaoImpl(OutputStream out, Object arg, Object preExport, int daoIndex, D dao, int offset, int limit) {
        // prepare converter for dao
        Object preDao = extractor.preDao(arg, preExport, dao);
        String tableName = extractor.getTableName(arg, dao);
        Iterable<String> columns = extractor.getColumns(arg, dao);
        LOG.debug("{} has columns {}", tableName, columns);
        Map<String, String> headers = extractor.getHeaderNames(arg, dao);
        Object logPreDao = converter.preDao(out, arg, preExport, preDao, tableName, 
                columns, headers, daoIndex, dao);
        if (null != logPreDao) {
            LOG.debug("{} {}", dao, logPreDao);
        }

        int entityIndex = 0;
        Object log;
        int pageSize;
        int returned = 0;
        int actualSize;
        do {
            pageSize = Math.min(50, limit-returned);
            actualSize = 0;
            Iterable entities = extractor.queryIterable(arg, dao, offset+returned, pageSize);
            for (Object entity : entities) {
                log = exportEntity(out, arg, preExport, preDao, columns, daoIndex, dao, 
                        entityIndex, entity);
                if (null != log) {
                    entityIndex++;
                }
                returned++;
                actualSize++;
            }
        } while (actualSize == pageSize && returned < limit);
            
        // close converter for dao
        Object postDao = extractor.postDao(arg, preExport, preDao, dao);
        Object logPostDao = converter.postDao(out, arg, preExport, preDao, postDao, dao);
        if (null != logPostDao) {
            LOG.debug("{} {}", dao, logPostDao);
        }
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
    
}
