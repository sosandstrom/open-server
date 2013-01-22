MVC-boiler
===========

The MVC Boiler module contains boilerplate CRUD Controller and CRUD Service. 
The purpose is to implement RESTful CRUD functionality with inheritance and generics.

CrudController
--------------
The CrudController implements the following REST methods:
* Create 
  POST {basePath}/v10
* Read
  GET {basePath}/v10/{id}
* Update 
  POST {basePath}/v10/{id}
* Delete
  DELETE {basePath}/v10/{id}
and some convenient, generic methods:
* Get Page of items
  GET {basePath}/v10[?pageSize=10][&cursorKey={cursorKey}]
* Get specified, existing items
  GET {basePath}/v10?id={id0}[&id={idN}]
* Get ids for changed items (whatsChanged)
  GET {basePath}/v10[?pageSize=10][&cursorKey={cursorKey}]
  If-Modified-Since: {timestampString}

CrudService
-----------
The CrudService interface backs the CrudController with service methods.

`
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
`

MardaoCrudService
-----------------
The MardaoCrudService implements the CrudService interface by using a Mardao Dao
object. You can implement a full REST service with the following few lines:
`
public class CategoryService extends MardaoCrudService<
        DmCategory, 
        Long, 
        DmCategoryDao> {

}
`
where the DmCategoryDao is injected using @Autowired.