package com.wadpam.open.mvc;

import com.wadpam.open.exceptions.RestException;
import com.wadpam.open.json.JBaseObject;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author os
 */
public class CrudService<
        J extends JBaseObject, 
        T extends Object, 
        ID extends Serializable> {
    
    protected static final Logger LOG = LoggerFactory.getLogger(CrudService.class);
    
    protected Dao<T, ID> dao;
    
    public ID create(T domain) {
        if (null == domain) {
            return null;
        }
        
        prePersist(domain);
        
        final ID id = dao.persist(domain);
        
        LOG.debug("Created {}", domain);
        
        return id;
    }
    
    public void delete(String parentKeyString, ID id) {
        Object parentKey = dao.getPrimaryKey(parentKeyString);
        dao.delete(parentKey, id);
    }
    
    public T get(String parentKeyString, ID id) {
        Object parentKey = dao.getPrimaryKey(parentKeyString);
        T domain = dao.findByPrimaryKey(parentKey, id);
        LOG.debug("GET {}/{}/{} returns {}", new Object[] {
            dao.getTableName(), parentKey, id, domain});
        
        return domain;
    }
    
    public Iterable<T> getByPrimaryKeys(Collection<ID> ids) {
        final Iterable<T> entities = dao.queryByPrimaryKeys(null, ids);
        
        return entities;
    }

    public CursorPage<T, ID> getPage(int pageSize, Serializable cursorKey) {
        return dao.queryPage(pageSize, cursorKey);
    }
    
    public ID getSimpleKey(T domain) {
        return dao.getSimpleKey(domain);
    }
    
    public String getParentKeyString(T domain) {
        final Object parentKey = dao.getParentKey(domain);
        return dao.getKeyString(parentKey);
    }

    /** Override to implement pre-persist validation */
    protected void prePersist(T domain) throws RestException {
    }
    
    public ID update(T domain) {
        LOG.debug("Update {}", domain);
        final ID id = dao.getSimpleKey(domain);
        if (null == domain || null == id) {
            throw new IllegalArgumentException("ID cannot be null updating " + dao.getTableName());
        }
        
        dao.update(domain);
        
        return id;
    }
    
    public CursorPage<ID, ID> whatsChanged(Date since, int pageSize, Serializable cursorKey) {
        // TODO: include deletes from Audit table
        return dao.whatsChanged(since, pageSize, cursorKey);
    }
}
