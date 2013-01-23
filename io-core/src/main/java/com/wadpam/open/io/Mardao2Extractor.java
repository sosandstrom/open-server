package com.wadpam.open.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sf.mardao.core.dao.Dao;

/**
 *
 * @author os
 */
public class Mardao2Extractor implements Extractor<Dao> {
    
    Map<String, Map<String, String>> daoHeaders = new HashMap<String, Map<String, String>>();

    @Override
    public Iterable<String> getColumns(Object arg, Dao dao) {
        final ArrayList<String> columns = new ArrayList<String>(dao.getColumnNames());
        
        columns.add(0, dao.getPrimaryKeyColumnName());
        
        if (null != dao.getParentKeyColumnName()) {
            columns.add(0, dao.getParentKeyColumnName());
        }
        
        return columns;
    }
    
    @Override
    public Map<String,String> getHeaderNames(Object arg, Dao dao) {
        final Map<String, String> map = daoHeaders.get(dao.getTableName());
        return null != map ? map : Collections.EMPTY_MAP;
    }
    
    public void setHeaderNames(Map<String, String> headerNames, Dao dao) {
        daoHeaders.put(dao.getTableName(), headerNames);
    }

    @Override
    public Map<String, Object> getValues(Object arg, Dao dao, Object entity) {
        return dao.getDomainProperties(entity);
    }

    @Override
    public String getTableName(Object arg, Dao dao) {
        return dao.getTableName();
    }

    @Override
    public Object postDao(Object arg, Object preExport, Object preDao, Dao dao) {
        return null;
    }

    @Override
    public Object postExport(Object arg, Object preExport, Dao[] daos) {
        return null;
    }

    @Override
    public Object preDao(Object arg, Object preExport, Dao dao) {
        return null;
    }

    @Override
    public Object preExport(Object arg, Dao[] daos) {
        return null;
    }

    @Override
    public Iterable queryIterable(Object arg, Dao dao) {
        return dao.queryAll();
    }
    
}
