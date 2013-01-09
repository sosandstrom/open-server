package com.wadpam.open.io;

import java.io.OutputStream;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author os
 */
public class Exporter<D> {
    
    static final Logger LOG = LoggerFactory.getLogger(Exporter.class);
    
    private Converter<D> converter;
    private Extractor<D> extractor;
    
    public void export(OutputStream out, Object arg, D... daos) {
        
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
    }
    
    protected void exportDao(OutputStream out, Object arg, Object preExport, int daoIndex, D dao) {
        // prepare converter for dao
        Object preDao = extractor.preDao(arg, preExport, dao);
        String tableName = extractor.getTableName(arg, dao);
        Iterable<String> columns = extractor.getColumns(arg, dao);
        Object logPreDao = converter.preDao(out, arg, preExport, preDao, tableName, 
                columns, daoIndex, dao);
        if (null != logPreDao) {
            LOG.debug("{} {}", dao, logPreDao);
        }

        int entityIndex = 0;
        Iterable entities = extractor.queryIterable(arg, dao);
        Object log;
        for (Object entity : entities) {
            log = exportEntity(out, arg, preExport, preDao, columns, daoIndex, dao, 
                    entityIndex, entity);
            if (null != log) {
                entityIndex++;
            }
        }
            
        // close converter for dao
        Object postDao = extractor.postDao(arg, preExport, preDao, dao);
        Object logPostDao = converter.postDao(out, arg, preExport, preDao, postDao, dao);
        if (null != logPostDao) {
            LOG.debug("{} {}", dao, logPostDao);
        }
    }
    
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
