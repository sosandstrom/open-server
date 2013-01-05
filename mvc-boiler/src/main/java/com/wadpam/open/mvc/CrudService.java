package com.wadpam.open.mvc;

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
    
    @Autowired
    protected Dao<T, ID> dao;
    
    public ID create(T domain) {
        if (null == domain) {
            return null;
        }
        
        prePersist(domain);
        
        final ID id = dao.persist(domain);
        
        LOG.debug("Created {} with ID {}", dao.getTableName(), id);
        
        return id;
    }
    
    public ID getSimpleKey(T domain) {
        return dao.getSimpleKey(domain);
    }

    /** Override to implement pre-persist validation */
    protected void prePersist(T domain) throws RestException {
    }
}
