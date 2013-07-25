package com.wadpam.open.mvc;

import com.wadpam.open.exceptions.RestException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.Filter;
import net.sf.mardao.core.dao.Dao;
import net.sf.mardao.core.dao.DaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 *
 * @author os
 */
public abstract class MardaoCrudService<
        T extends Object,
        ID extends Serializable,
        D extends Dao<T, ID>> implements CrudService<T, ID> {
    
    protected static final Logger LOG = LoggerFactory.getLogger(MardaoCrudService.class);
    
    protected D dao;
    
    protected AbstractPlatformTransactionManager transactionManager = null;
    protected final TransactionDefinition TRANSACTION_DEFINITION = new DefaultTransactionDefinition();

    @Override
    public T createDomain() {
        try {
            return (T) ((DaoImpl) dao).createDomain();
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        }
        return null;
    }
    
    protected void preDao() {
    }
    
    protected void postDao() {
    }
    
    protected TransactionStatus getTransaction() {
        if (null == transactionManager) {
            return null;
        }
        
        
        final TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
        return status;
    }
    
    protected void commitTransaction(TransactionStatus status) {
        if (null != transactionManager && null != status) {
            transactionManager.commit(status);
        }
    }
    
    protected void rollbackTransaction(TransactionStatus status) {
        if (null != transactionManager && null != status && !status.isCompleted()) {
            LOG.warn("Transaction Rollback");
            transactionManager.rollback(status);
        }
    }
    
    
    @Override
    public ID create(T domain) {
        if (null == domain) {
            return null;
        }
        
        final TransactionStatus transactionStatus = getTransaction();
        preDao();
        prePersist(domain);
        try {
            final ID id = dao.persist(domain);

            LOG.debug("Created {}", domain);

            commitTransaction(transactionStatus);
            return id;
        }
        finally {
            postDao();
            rollbackTransaction(transactionStatus);
        }
    }
    
    @Override
    public void delete(String parentKeyString, ID id) {
        final TransactionStatus transactionStatus = getTransaction();
        preDao();
        try {
            Object parentKey = dao.getPrimaryKey(parentKeyString);
            dao.delete(parentKey, id);
            commitTransaction(transactionStatus);
        }
        finally {
            postDao();
            rollbackTransaction(transactionStatus);
        }
    }

    @Override
    public void delete(String parentKeyString, Iterable<ID> ids) {
        final TransactionStatus transactionStatus = getTransaction();
        preDao();
        try {
            Object parentKey = dao.getPrimaryKey(parentKeyString);
            dao.delete(parentKey, ids);
            commitTransaction(transactionStatus);
        }
        finally {
            postDao();
            rollbackTransaction(transactionStatus);
        }
    }

    @Override
    public void exportCsv(OutputStream out, Long startDate, Long endDate) {
        preDao();
        try {
            // TODO: filter on dates
            dao.writeAsCsv(out, getExportColumns(), (DaoImpl) dao, null, null, false, null, false);
        }
        finally {
            postDao();
        }
    }
    
    @Override
    public T get(String parentKeyString, ID id) {
        if (null == id || "".equals(id)) {
            return null;
        }
        
        preDao();
        try {
            // TODO: parentKeyString must be decoded by parent dao!
            Object parentKey = dao.getPrimaryKey(parentKeyString);
            T domain = dao.findByPrimaryKey(parentKey, id);
            LOG.debug("GET {}/{}/{} returns {}", new Object[] {
                dao.getTableName(), parentKey, id, domain});

            return domain;
        }
        catch (RuntimeException toLog) {
            LOG.warn("in GET", toLog);
            throw toLog;
        }
        finally {
            postDao();
        }
    }
    
    @Override
    public Iterable<T> getByPrimaryKeys(Iterable<ID> ids) {
        preDao();
        try {
            final Iterable<T> entities = dao.queryByPrimaryKeys(null, ids);

            return entities;
        }
        finally {
            postDao();
        }
    }
    
    protected String[] getExportColumns() {
        return new String[] {
            dao.getPrimaryKeyColumnName(),
            dao.getCreatedDateColumnName(),
            dao.getCreatedByColumnName(),
            dao.getUpdatedDateColumnName(),
            dao.getUpdatedByColumnName()
        };
    }
    
    public String getKeyString(Object key) {
        return dao.getKeyString(key);
    }

    @Override
    public CursorPage<T> getPage(int pageSize, String cursorKey) {
        preDao();
        try {
            return dao.queryPage(pageSize, cursorKey);
        }
        finally {
            postDao();
        }
    }
    
    @Override
    public String getParentKeyString(T domain) {
        final Object parentKey = dao.getParentKey(domain);
        return dao.getKeyString(parentKey);
    }
    
    public Object getPrimaryKey(T domain) {
        return dao.getPrimaryKey(domain);
    }

    @Override
    public String getPrimaryKeyColumnName() {
        return dao.getPrimaryKeyColumnName();
    }

    @Override
    public Class getPrimaryKeyColumnClass() {
        return dao.getColumnClass(dao.getPrimaryKeyColumnName());
    }

    @Override
    public ID getSimpleKey(T domain) {
        return dao.getSimpleKey(domain);
    }

    @Override
    public String getTableName() {
        return dao.getTableName();
    }

    @Override
    public Map<String, Class> getTypeMap() {
        final TreeMap<String, Class> map = new TreeMap<String, Class>();
        
        for (String col : dao.getColumnNames()) {
            map.put(col, dao.getColumnClass(col));
        }
        
        return map;
    }
    
    /** Override to implement pre-persist validation */
    protected void prePersist(T domain) throws RestException {
    }
    
    @Autowired
    public void setDao(D dao) {
        this.dao = dao;
    }

    public D getDao() {
        return dao;
    }
    
    @Override
    public ID update(T domain) {
        final TransactionStatus transactionStatus = getTransaction();
        preDao();
        try {
            LOG.debug("Update {}", domain);
            final ID id = dao.getSimpleKey(domain);
            if (null == domain || null == id) {
                throw new IllegalArgumentException("ID cannot be null updating " + dao.getTableName());
            }

            dao.update(domain);

            commitTransaction(transactionStatus);
            return id;
        }
        finally {
            postDao();
            rollbackTransaction(transactionStatus);
        }
    }
    
    @Override
    public List<ID> upsert(Iterable<T> dEntities) {
        final TransactionStatus transactionStatus = getTransaction();
        
        // group entities by create or update:
        final ArrayList<T> toCreate = new ArrayList<T>();
        final ArrayList<T> toUpdate = new ArrayList<T>();
        ID id;
        for (T d : dEntities) {
            id = dao.getSimpleKey(d);
            if (null == id) {
                toCreate.add(d);
            }
            else {
                toUpdate.add(d);
            }
        }
        LOG.debug("Creating {}, Updating {}", toCreate.size(), toUpdate.size());
        LOG.debug("Creating {}, Updating {}", toCreate, toUpdate);
        
        try {
            // create new entities using async API
            Future<List<?>> createFuture = null;
            if (!toCreate.isEmpty()) {
                createFuture = dao.persistForFuture(toCreate);
            }

            // update in parallel
            if (!toUpdate.isEmpty()) {
                dao.update(toUpdate);
            }

            // join future
            if (null != createFuture) {
                Collection<ID> ids = dao.getSimpleKeys(createFuture);
    //            Iterator<ID> i = ids.iterator();
    //            for (T t : toCreate) {
    //                dao.setSimpleKey(t, i.next());
    //            }
            }

            // collect the IDs
            final ArrayList<ID> body = new ArrayList<ID>();
            for (T d : dEntities) {
                body.add(getSimpleKey(d));
            }

            commitTransaction(transactionStatus);
            return body;
        }
        finally {
            rollbackTransaction(transactionStatus);
        }
    }
    
    @Override
    public CursorPage<ID> whatsChanged(Date since, String createdBy, String updatedBy, 
            int pageSize, String cursorKey) {
        
        // are createdBy and/or updatedBy specified?
        int fs = (null != createdBy ? 1 : 0) + (null != updatedBy ? 1 : 0);
        Filter filters[] = new Filter[fs];
        int i = 0;
        if (null != createdBy) {
            filters[i++] = dao.createEqualsFilter(dao.getCreatedByColumnName(), createdBy);
        }
        if (null != updatedBy) {
            filters[i++] = dao.createEqualsFilter(dao.getUpdatedByColumnName(), updatedBy);
        }
        
        return whatsChanged(null, since, pageSize, cursorKey, filters);
    }
    
    public CursorPage<ID> whatsChanged(Object parentKey, Date since, 
            int pageSize, String cursorKey, Filter... filters) {
        preDao();
        try {
            return dao.whatsChanged(parentKey, since, pageSize, cursorKey, filters);
        }
        finally {
            postDao();
        }
    }

    public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
}
