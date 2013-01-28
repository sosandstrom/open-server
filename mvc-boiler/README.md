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

        /** Inject class so CrudController can create JSON objects */
        public CategoryController() {
            super(JCategory.class);
        }

        @Override
        public void convertDomain(DmCategory from, JCategory to) {
            convertLongEntity(from, to);

            to.setDescription(from.getDescription());
            to.setName(from.getName());
            to.setType(from.getType());
        }

        @Override
        public void convertJson(JCategory from, DmCategory to) {
            convertJLong(from, to);

            to.setDescription(from.getDescription());
            to.setName(from.getName());
            to.setType(from.getType());
        }

        @Autowired
        public void setCategoryService(CategoryService categoryService) {
            this.service = categoryService;
        }
    }

where the CategoryService is injected using @Autowired.

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

        @Autowired
        public void setDmCategoryDao(DmCategoryDao dmCategoryDao) {
            this.dao = dmCategoryDao;
        }
    }
where the DmCategoryDao is injected using @Autowired.

CrudListener
------------
The CrudListener interface allows your application service to listen to CrudController
events.

    /**
     * Listens to CrudController methods, and is notified before and after the service.
     * On multiple Controller listeners, the order of notification cannot be guaranteed.
     * @author os
     */
    public interface CrudListener {
        public static final int CREATE = 1;
        public static final int GET = 2;
        public static final int UPDATE = 3;
        public static final int DELETE = 4;
        public static final int GET_PAGE = 11;
        public static final int WHAT_CHANGED = 12;

        /**
         * Triggered by the CrudController before the CrudService is invoked.
         * @param controller the CrudController that triggered this notification
         * @param service the service that is invoked
         * @param request the request object
         * @param namespace the @PathVariable namespace (domain)
         * @param operation one of CREATE, GET, UPDATE, ...
         * @param json The JSON / request entity object for this operation
         * @param domain The domain / service entity object for this operation
         * @param id defined for GET, UPDATE, DELETE
         */
        public void preService(CrudController controller, CrudService service,
                HttpServletRequest request, String namespace,
                int operation, Object json, Object domain, Serializable id);

        /**
         * Triggered by the CrudController after the CrudService was invoked.
         * @param controller the CrudController that triggered this notification
         * @param service the service that was invoked
         * @param request the request object
         * @param domain the @PathVariable domain
         * @param operation one of CREATE, GET, UPDATE, ...
         * @param json The JSON / request entity object for this operation
         * @param id defined for GET, UPDATE, DELETE
         * @param serviceResponse 
         */
        public void postService(CrudController controller, CrudService service,
                HttpServletRequest request, String domain,
                int operation, Object json, Serializable id, Object serviceResponse);
    }

There is also an Adapter class with empty implementations.

CrudListenerAdapter
-------------------
With the CrudListenerAdapter you can cherry-pick the Controller events you want
to listen to, by overriding specific method.

    @Override
    protected void postGet(CrudController controller, CrudService service, HttpServletRequest request, String namespace, Serializable id, Object serviceResponse, Object jsonResponse) {
        // TODO: do stuff here before returning the jsonResponse to client
    }

