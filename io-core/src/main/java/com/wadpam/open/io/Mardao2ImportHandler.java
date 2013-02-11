/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sf.mardao.core.dao.Dao;
import net.sf.mardao.core.dao.DaoImpl;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;

/**
 *
 * @author sosandstrom
 */
public class Mardao2ImportHandler extends ValidationHandlerAdapter {
    
    private final DaoImpl<AbstractCreatedUpdatedEntity, Serializable,
            Serializable, Object, Object, Serializable> dao;

    public Mardao2ImportHandler(Dao dao) {
        this.dao = (DaoImpl) dao;
        
        final ArrayList<String> columns = new ArrayList<String>(dao.getColumnNames());
        columns.add(0, dao.getPrimaryKeyColumnName());
        if (null != dao.getParentKeyColumnName()) {
            columns.add(0, dao.getParentKeyColumnName());
        }
        setValidColumns(columns);
        
        final HashMap<String, Integer> required = new HashMap<String, Integer>();
        for (String col : columns) {
            required.put(col, JValidationFeedback.CODE_REQUIRED);
        }
        setRequiredColumns(required);
        
        final HashMap<String, String> file2Domain = new HashMap<String, String>();
        for (String col : columns) {
            file2Domain.put(col, col);
        }
        setFileToDomainMap(file2Domain);
        
        setRegexps(Collections.EMPTY_MAP);
    }
    
    @Override
    public AbstractCreatedUpdatedEntity update(int row, Map<String, String> properties, boolean mergeIfExist) {
        AbstractCreatedUpdatedEntity domain = dao.createDomain(properties);
        if (!mergeIfExist) {
            Object primaryKey = dao.getPrimaryKey(domain);
            AbstractCreatedUpdatedEntity existing = dao.findByPrimaryKey(primaryKey);
            if (null != existing) {
                return existing;
            }
        }
        dao.persist(domain);
        return domain;
    }

}
