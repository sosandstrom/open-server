MVC-boiler
===========

The MVC Boiler module contains boilerplate CRUD Controller and CRUD Service. 
The purpose is to implement RESTful CRUD functionality with inheritance and generics.

CrudController
--------------
The CrudController implements the following REST methods:
* Create - `POST {basePath}/v10`
* Read - `GET {basePath}/v10/{id}`
* Update - `POST {basePath}/v10/{id}`
* Delete - `DELETE {basePath}/v10/{id}`

and some convenient, generic methods:

* Get Page of items - `GET {basePath}/v10[?pageSize=10][&cursorKey={cursorKey}]`
* Get specified, existing items - `GET {basePath}/v10?id={id0}[&id={idN}]`
* Get ids for changed items - `GET {basePath}/v10[?pageSize=10][&cursorKey={cursorKey}]` with a `If-Modified-Since: {timestampString}` HTTP header

Example
    
    @Controller
    @RequestMapping("{domain}/category")
    public class CategoryController extends CrudController< 
            JCategory, 
            DmCategory, 
            Long, 
            CategoryService> {
        
        @Override
        public JCategory convertDomain(DmCategory from) {
            if (from == null) {
                return null;
            }
            JCategory to = new JCategory();
            Converter.convertLong(from, to);

            to.setDescription(from.getDescription());
            to.setName(from.getName());
            to.setType(from.getType());
            return to;
        }

        @Override
        public DmCategory convertJson(JCategory from) {
            if (from == null) {
                return null;
            }
            DmCategory to = new DmCategory();
            Converter.convertLong(from, to);

            to.setDescription(from.getDescription());
            to.setName(from.getName());
            to.setType(from.getType());
            return to;
        }
        
    }

where the ContactService is injected using @Autowired.

CrudService
-----------
The CrudService interface backs the CrudController with service methods.

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

MardaoCrudService
-----------------
The MardaoCrudService implements the CrudService interface by using a Mardao Dao
object. You can implement a full REST service with the following few lines

    public class CategoryService extends MardaoCrudService< 
            DmCategory, 
            Long, 
            DmCategoryDao> { 
            
    }
where the DmCategoryDao is injected using @Autowired.