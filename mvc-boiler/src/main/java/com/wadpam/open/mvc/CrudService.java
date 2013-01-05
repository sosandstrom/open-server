package com.wadpam.open.mvc;

import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.open.exceptions.RestException;
import com.wadpam.open.json.JBaseObject;
import java.io.Serializable;
import net.sf.mardao.core.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    
    
}
