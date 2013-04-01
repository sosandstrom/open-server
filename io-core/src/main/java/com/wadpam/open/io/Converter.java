package com.wadpam.open.io;

import java.io.OutputStream;
import java.util.Map;

/**
 *
 * @author os
 */
public interface Converter<D> {
    
    Object initPreDao(OutputStream out, Object arg);

    Object preExport(OutputStream out, Object arg, Object preExport, D[] daos);

    Object postExport(OutputStream out, Object arg, Object preExport, Object postExport, 
            D[] daos);

    Object preDao(OutputStream out, Object arg, Object preExport, Object preDao, 
            String tableName, Iterable<String> columns, Map<String, String> headers, 
            int daoIndex, D dao);

    Object postDao(OutputStream out, Object arg, Object preExport, Object preDao, Object postDao, D dao);

    Object writeValues(OutputStream out, Object arg, Object preExport, Object preDao, 
            Iterable<String> columns, int daoIndex, D dao, int entityIndex, Object entity, Map<String, Object> values);
    
}
