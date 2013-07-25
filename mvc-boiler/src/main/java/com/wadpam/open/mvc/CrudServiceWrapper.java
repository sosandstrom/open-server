/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.mvc;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.sf.mardao.core.CursorPage;

/**
 *
 * @author sosandstrom
 */
public class CrudServiceWrapper<T extends Object, ID extends Serializable, E extends T> implements CrudService<E, ID> {
    
    protected CrudService<T, ID> _delegate;

    public CrudServiceWrapper(CrudService<T, ID> delegate) {
        this._delegate = delegate;
    }

    public CrudServiceWrapper() {
    }

    public CrudService<T, ID> getDelegate() {
        return _delegate;
    }
    
    public void setDelegate(CrudService<T, ID> delegate) {
        this._delegate = delegate;
    }

    @Override
    public E createDomain() {
        return (E) getDelegate().createDomain();
    }

    @Override
    public ID create(E domain) {
        return getDelegate().create( domain);
    }

    @Override
    public void delete(String parentKeyString, ID id) {
        getDelegate().delete(parentKeyString, id);
    }

    @Override
    public void delete(String parentKeyString, Iterable<ID> ids) {
        getDelegate().delete(parentKeyString, ids);
    }

    @Override
    public void exportCsv(OutputStream out, Long startDate, Long endDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E get(String parentKeyString, ID id) {
        return (E) getDelegate().get(parentKeyString, id);
    }

    @Override
    public Iterable<E> getByPrimaryKeys(Iterable<ID> ids) {
        return (Iterable<E>) getDelegate().getByPrimaryKeys(ids);
    }

    @Override
    public CursorPage<E> getPage(int pageSize, String cursorKey) {
        return (CursorPage<E>) getDelegate().getPage(pageSize, cursorKey);
    }

    @Override
    public ID getSimpleKey(E domain) {
        return getDelegate().getSimpleKey(domain);
    }

    @Override
    public String getParentKeyString(E domain) {
        return getDelegate().getParentKeyString(domain);
    }

    @Override
    public String getTableName() {
        return getDelegate().getTableName();
    }

    @Override
    public Map<String, Class> getTypeMap() {
        return getDelegate().getTypeMap();
    }
    
    @Override
    public ID update(E domain) {
        return getDelegate().update(domain);
    }

    @Override
    public List<ID> upsert(Iterable<E> domains) {
        return getDelegate().upsert((Iterable<T>) domains);
    }

    @Override
    public CursorPage<ID> whatsChanged(Date since, String createdBy, String updatedBy, 
            int pageSize, String cursorKey) {
        return getDelegate().whatsChanged(since, createdBy, updatedBy, 
                pageSize, cursorKey);
    }

    @Override
    public String getPrimaryKeyColumnName() {
        return getDelegate().getPrimaryKeyColumnName();
    }

    @Override
    public Class getPrimaryKeyColumnClass() {
        return getDelegate().getPrimaryKeyColumnClass();
    }

}
