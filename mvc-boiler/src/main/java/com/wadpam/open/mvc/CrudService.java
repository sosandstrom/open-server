package com.wadpam.open.mvc;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import net.sf.mardao.core.CursorPage;

/**
 *
 * @author os
 */
public interface CrudService<
        T extends Object, 
        ID extends Serializable> {
    
    ID create(T domain);
    
    void delete(String parentKeyString, ID id);

    void exportCsv(OutputStream out, Long startDate, Long endDate);
    
    T get(String parentKeyString, ID id);
    
    Iterable<T> getByPrimaryKeys(Collection<ID> ids);

    CursorPage<T, ID> getPage(int pageSize, Serializable cursorKey);
    
    ID getSimpleKey(T domain);
    
    String getParentKeyString(T domain);

    String getTableName();
    
    ID update(T domain);
    
    CursorPage<ID, ID> whatsChanged(Date since, int pageSize, Serializable cursorKey);

}
