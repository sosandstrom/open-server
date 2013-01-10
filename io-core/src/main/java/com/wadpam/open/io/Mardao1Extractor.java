package com.wadpam.open.io;

import com.google.appengine.api.datastore.Key;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.mardao.api.dao.Dao;
import net.sf.mardao.api.dao.DaoImpl;

/**
 *
 * @author os
 */
public class Mardao1Extractor implements Extractor<Dao> {

    @Override
    public Iterable<String> getColumns(Object arg, Dao dao) {
        return dao.getColumnNames();
    }

    @Override
    public Map<String, Object> getValues(Object arg, Dao dao, Object entity) {
        net.sf.mardao.api.dao.CsvConverter converter = (net.sf.mardao.api.dao.CsvConverter) dao;
        final ArrayList<String> columns = new ArrayList<String>();
        for (String col : getColumns(arg, dao)) {
            columns.add(col);
        }
        final Map<String, Object> values = converter.getCsvColumnValues((DaoImpl) dao, columns.toArray(new String[columns.size()]), entity);
        
        // replace keys with id / name instead
        Object v;
        for (Entry<String, Object> entry : values.entrySet()) {
            v = entry.getValue();
            if (null != v && "com.google.appengine.api.datastore.Key".equals(v.getClass().getName())) {
                Key key = (Key) v;
                values.put(entry.getKey(), null != key.getName() ? key.getName() : key.getId());
            }
        }
        
        return values;
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
        return dao.xmlFindAll();
    }
    
}
