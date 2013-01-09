package com.wadpam.open.io;

import java.util.Map;

/**
 *
 * @author os
 */
public interface Extractor<D> {
    
    Iterable<String> getColumns(Object arg, D dao);

    Map<String, Object> getValues(Object arg, D dao, Object entity);
    
    String getTableName(Object arg, D dao);

    Object postDao(Object arg, Object preExport, Object preDao, D dao);

    Object postExport(Object arg, Object preExport, D[] daos);

    Object preDao(Object arg, Object preExport, D dao);

    Object preExport(Object arg, D[] daos);

    Iterable queryIterable(Object arg, D dao);

}
