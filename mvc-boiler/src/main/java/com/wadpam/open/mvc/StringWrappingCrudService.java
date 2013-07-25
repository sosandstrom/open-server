package com.wadpam.open.mvc;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.dao.Dao;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;
import net.sf.mardao.core.domain.AbstractStringEntity;

/**
 *
 * @author sosandstrom
 */
public abstract class StringWrappingCrudService<S extends AbstractStringEntity, L extends LongEntity> 
        implements CrudService<S, String> {

    private final CrudService<L, Long> delegate;

    public StringWrappingCrudService(MardaoCrudService<L, Long, Dao<L, Long>> delegate) {
        this.delegate = delegate;
    }

    public static final Long convertId(String id) {
        return null == id ? null : Long.parseLong(id);
    }
    
    public static final String convertId(Long id) {
        return null == id ? null : Long.toString(id);
    }
    
    public static final ArrayList<Long> convertStringIds(Iterable<String> ids) {
        ArrayList<Long> to = new ArrayList<Long>();
        for (String id : ids) {
            to.add(convertId(id));
        }
        return to;
    }
    
    public static final ArrayList<String> convertLongIds(Iterable<Long> ids) {
        ArrayList<String> to = new ArrayList<String>();
        for (Long id : ids) {
            to.add(convertId(id));
        }
        return to;
    }
    
    public abstract S convertDomain(L from);
    public abstract L convertDomain(S from);
    public abstract void convertDomain(L from, S to);
    public abstract void convertDomain(S from, L to);
    
    public ArrayList<S> convertLongDomains(Iterable<L> from) {
        ArrayList<S> to = new ArrayList<S>();
        for (L l : from) {
            to.add(convertDomain(l));
        }
        return to;
    }
    
    public ArrayList<L> convertStringDomains(Iterable<S> from) {
        ArrayList<L> to = new ArrayList<L>();
        for (S s : from) {
            to.add(convertDomain(s));
        }
        return to;
    }
    
    public static void copyCreatedUpdatedEntity(AbstractCreatedUpdatedEntity from, 
            AbstractCreatedUpdatedEntity to) {
        
        to.setCreatedBy(from.getCreatedBy());
        to.setCreatedDate(from.getCreatedDate());
        to.setUpdatedBy(from.getUpdatedBy());
        to.setUpdatedDate(from.getUpdatedDate());
    }
    
    public static void convertLongEntity(LongEntity from, AbstractStringEntity to) {
        AbstractCreatedUpdatedEntity acue = (AbstractCreatedUpdatedEntity) from;
        copyCreatedUpdatedEntity(acue, to);
        
        to.setId(convertId(from.getLongId()));
    }
    
    public static void convertStringEntity(AbstractStringEntity from, LongEntity to) {
        AbstractCreatedUpdatedEntity acue = (AbstractCreatedUpdatedEntity) to;
        copyCreatedUpdatedEntity(from, acue);
        
        to.setLongId(convertId(from.getId()));
    }
    
    protected CrudService<L, Long> getDelegate() {
        return delegate;
    }
    
    @Override
    public S createDomain() {
        return convertDomain(getDelegate().createDomain());
    }

    @Override
    public String create(S domain) {
        L l = convertDomain(domain);
        Long id = getDelegate().create(l);
        convertDomain(l, domain);
        return convertId(id);
    }

    @Override
    public void delete(String parentKeyString, String id) {
        getDelegate().delete(parentKeyString, convertId(id));
    }

    @Override
    public void delete(String parentKeyString, Iterable<String> ids) {
        getDelegate().delete(parentKeyString, convertStringIds(ids));
    }

    @Override
    public void exportCsv(OutputStream out, Long startDate, Long endDate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public S get(String parentKeyString, String id) {
        final Long lid = convertId(id);
        final L d = getDelegate().get(parentKeyString, lid);
        return convertDomain(d);
    }

    @Override
    public Iterable<S> getByPrimaryKeys(Iterable<String> ids) {
        Iterable<L> l = getDelegate().getByPrimaryKeys(convertStringIds(ids));
        return convertLongDomains(l);
    }

    @Override
    public CursorPage<S> getPage(int pageSize, String cursorKey) {
        CursorPage page = getDelegate().getPage(pageSize, cursorKey);
        
        // in-page conversion
        page.setItems(convertLongDomains(page.getItems()));
        return page;
    }

    @Override
    public String getSimpleKey(S domain) {
        L l = convertDomain(domain);
        Long id = getDelegate().getSimpleKey(l);
        return convertId(id);
    }

    @Override
    public String getParentKeyString(S domain) {
        L l = convertDomain(domain);
        return getDelegate().getParentKeyString(l);
    }

    @Override
    public String getPrimaryKeyColumnName() {
        return getDelegate().getPrimaryKeyColumnName();
    }

    @Override
    public Class getPrimaryKeyColumnClass() {
        return String.class;
    }

    @Override
    public String getTableName() {
        return getDelegate().getTableName();
    }

    @Override
    public Map<String, Class> getTypeMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void setDao(Dao<L, Long> dao) {
        ((MardaoCrudService)delegate).setDao(dao);
    }
    
    @Override
    public String update(S domain) {
        L l = convertDomain(domain);
        Long id = getDelegate().update(l);
        convertDomain(l, domain);
        return convertId(id);
    }

    @Override
    public List<String> upsert(Iterable<S> domains) {
        ArrayList<L> l = convertStringDomains(domains);
        Iterable<Long> ids = getDelegate().upsert(l);
        return convertLongIds(ids);
    }

    @Override
    public CursorPage<String> whatsChanged(Date since, String createdBy, String updatedBy, 
            int pageSize, String cursorKey) {
        CursorPage page = getDelegate().whatsChanged(since, createdBy, updatedBy, 
                pageSize, cursorKey);
        // in-page conversion
        page.setItems(convertLongDomains(page.getItems()));
        return page;
    }
    
}
