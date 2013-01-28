/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.mvc;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import net.sf.mardao.core.CursorPage;

/**
 *
 * @author sosandstrom
 */
public class CrudServiceWrapper<T extends Object, ID extends Serializable, E extends T> implements CrudService<E, ID> {
    
    protected final CrudService<T, ID> delegate;

    public CrudServiceWrapper(CrudService<T, ID> delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public E createDomain() {
        return (E) delegate.createDomain();
    }

    @Override
    public ID create(E domain) {
        return delegate.create( domain);
    }

    @Override
    public void delete(String parentKeyString, ID id) {
        delegate.delete(parentKeyString, id);
    }

    @Override
    public void exportCsv(OutputStream out, Long startDate, Long endDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public E get(String parentKeyString, ID id) {
        return (E) delegate.get(parentKeyString, id);
    }

    @Override
    public Iterable<E> getByPrimaryKeys(Collection<ID> ids) {
        return (Iterable<E>) delegate.getByPrimaryKeys(ids);
    }

    @Override
    public CursorPage<E, ID> getPage(int pageSize, Serializable cursorKey) {
        return (CursorPage<E, ID>) delegate.getPage(pageSize, cursorKey);
    }

    @Override
    public ID getSimpleKey(E domain) {
        return delegate.getSimpleKey(domain);
    }

    @Override
    public String getParentKeyString(E domain) {
        return delegate.getParentKeyString(domain);
    }

    @Override
    public String getTableName() {
        return delegate.getTableName();
    }

    @Override
    public ID update(E domain) {
        return delegate.update(domain);
    }

    @Override
    public CursorPage<ID, ID> whatsChanged(Date since, int pageSize, Serializable cursorKey) {
        return delegate.whatsChanged(since, pageSize, cursorKey);
    }

}
