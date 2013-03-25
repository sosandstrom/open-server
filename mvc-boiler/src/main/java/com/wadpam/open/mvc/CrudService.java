package com.wadpam.open.mvc;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.sf.mardao.core.CursorPage;

/**
 *
 * @author os
 */
public interface CrudService<
        T extends Object, 
        ID extends Serializable> {
    
    T createDomain();
    
    ID create(T domain);
    
    void delete(String parentKeyString, ID id);

    void delete(String parentKeyString, ID[] id);

    void exportCsv(OutputStream out, Long startDate, Long endDate);
    
    T get(String parentKeyString, ID id);
    
    Iterable<T> getByPrimaryKeys(Collection<ID> ids);

    CursorPage<T, ID> getPage(int pageSize, String cursorKey);
    
    ID getSimpleKey(T domain);
    
    String getParentKeyString(T domain);

    String getPrimaryKeyColumnName();
    
    Class getPrimaryKeyColumnClass();
    
    String getTableName();
    
    Map<String, Class> getTypeMap();
    
    ID update(T domain);
    
    List<ID> upsert(Iterable<T> domains);
    
    CursorPage<ID, ID> whatsChanged(Date since, int pageSize, String cursorKey);

}
