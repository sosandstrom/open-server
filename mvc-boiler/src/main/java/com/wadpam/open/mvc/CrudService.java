package com.wadpam.open.mvc;

import com.wadpam.open.transaction.Idempotent;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.sf.mardao.core.CursorPage;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author os
 */
@Transactional(readOnly = true)
public interface CrudService<
        T extends Object, 
        ID extends Serializable> {
    
    /**
     * Factory method that creates a domain-model side object.
     * @return 
     */
    T createDomain();
    
    /**
     * Persists an entity in the underlying database.
     * @param domain
     * @return the generated ID of the entity.
     */
    @Transactional(readOnly = false)
    @Idempotent
    ID create(T domain);
    
    /**
     * Deletes an entity from the underlying database persistence.
     * @param parentKeyString
     * @param id 
     */
    @Transactional(readOnly = false)
    @Idempotent
    void delete(String parentKeyString, ID id);

    /**
     * Deletes specified entities from the underlying database persistence.
     * @param parentKeyString
     * @param ids an array of IDs
     */
    @Transactional(readOnly = false)
    @Idempotent
    void delete(String parentKeyString, Iterable<ID> ids);

    void exportCsv(OutputStream out, Long startDate, Long endDate);
    
    /**
     * Reads the specified entity from underlying database persistence.
     * @param parentKeyString
     * @param id
     * @return null if not found, otherwise the read entity.
     */
    @Idempotent
    T get(String parentKeyString, ID id);
    
    /**
     * Batch-reads specified entities from underlying database persistence.
     * @param ids
     * @return 
     */
    @Idempotent
    Iterable<T> getByPrimaryKeys(Iterable<ID> ids);

    /**
     * Reads a page of entities from the underlying database persistence.
     * @param pageSize
     * @param cursorKey null for first page, otherwise same value as returned in previous CursorPage
     * @return 
     */
    @Idempotent
    CursorPage<T> getPage(int pageSize, String cursorKey);
    
    /**
     * Extractor method to get the simple key from specified domain entity object.
     * @param domain
     * @return 
     */
    ID getSimpleKey(T domain);
    
    /**
     * Extractor method to get the parent key string from specified domain entity object.
     * @param domain
     * @return 
     */
    String getParentKeyString(T domain);

    /**
     * Returns the name of the primary key property / column.
     * @return 
     */
    String getPrimaryKeyColumnName();
    
    /**
     * @return the class of the primary key, e.g. Long or String
     */
    Class getPrimaryKeyColumnClass();
    
    /**
     * @return the table name or entity kind
     */
    String getTableName();
    
    Map<String, Class> getTypeMap();
    
    /**
     * Updates an entity in the underlying datastore persistence.
     * @param domain
     * @return the ID of the updated entity
     */
    @Transactional(readOnly = false)
    @Idempotent
    ID update(T domain);
    
    /**
     * Updates or Inserts specified entities in the underlying datastore persistence.
     * @param domains
     * @return the IDs of the upserted entities
     */
    @Transactional(readOnly = false)
    @Idempotent
    List<ID> upsert(Iterable<T> domains);
    
    /**
     * Returns a page of IDs, for those entities that has changed or been deleted since.
     * @param since include entities that has changed or been deleted after this value
     * @param pageSize
     * @param cursorKey null for first page, otherwise same value as returned in previous CursorPage
     * @return the IDs of the upserted entities
     */
    @Idempotent
    CursorPage<ID> whatsChanged(Date since, int pageSize, String cursorKey);

}
