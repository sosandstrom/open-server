package com.wadpam.open.mvc;

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.open.json.JBaseObject;
import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.json.JLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;
import net.sf.mardao.core.domain.AbstractLongEntity;
import net.sf.mardao.core.domain.AbstractStringEntity;
import net.sf.mardao.core.geo.DLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author os
 */
public abstract class CrudController<
        J extends Object, 
        T extends Object, 
        ID extends Serializable,
        S extends CrudService<T, ID>> {
    
    public static final String NAME_X_REQUESTED_WITH = "X-Requested-With";
    public static final String VALUE_X_REQUESTED_WITH_AJAX = "XMLHttpRequest";
    
    public static final int ERR_CRUD_BASE = 99000;
    public static final int ERR_DELETE_NOT_FOUND = ERR_CRUD_BASE + 1;
    public static final int ERR_GET_NOT_FOUND = ERR_CRUD_BASE + 2;
    
    protected static final Logger LOG = LoggerFactory.getLogger(CrudController.class);
    
    protected final Class jsonClass;
    protected S service;
    
    protected final ArrayList<CrudListener> listeners = new ArrayList<CrudListener>();
    
    protected CrudController(Class jsonClazz) {
        this.jsonClass = jsonClazz;
    }
    
    /**
     * Creates an Entity from the form-encoded body, 
     * and redirects to the created Entity.
     * @param request
     * @param response
     * @param model
     * @param jEntity The form-encoded body will be bound to this parameter
     * @return a redirect to the {@link get()} method
     */
    @RestReturn(value=URI.class, code={
        @RestCode(code=302, description="Entity created", message="Redirect")
    })
    @RequestMapping(value="v10", method=RequestMethod.POST, 
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView createFromForm(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @ModelAttribute J jEntity) {
        
        final String path = createForLocation(request, domain, model, jEntity);
        return new RedirectView(path, true);
    }
    
    /**
     * Creates an Entity from the request parameters.
     * Responds with a 201 Created (no content) and the Location header.
     * @param request
     * @param response
     * @param model
     * @param jEntity The request parameters will be bound to this object
     * @return a 201 Created with Location header to the {@link get()} method
     */
    @RestReturn(value=URI.class, code={
        @RestCode(code=201, description="Entity created", message="Created")
    })
    @RequestMapping(value="v10", method=RequestMethod.GET, 
            params={"_method=POST"},
            headers={"X-Requested-With=XMLHttpRequest"},
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity createFromJsonp(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @ModelAttribute J jEntity) {
        final String path = createForLocation(request, domain, model, jEntity);
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Location", path);
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }
    
    /**
     * Creates an Entity from the JSON body, 
     * and redirects to the created Entity.
     * @param request
     * @param response
     * @param model
     * @param jEntity The JSON body will be bound to this parameter
     * @return a redirect to the {@link get()} method
     */
    @RestReturn(value=URI.class, code={
        @RestCode(code=302, description="Entity created", message="Redirect")
    })
    @RequestMapping(value="v10", method=RequestMethod.POST, 
            consumes=MediaType.APPLICATION_JSON_VALUE)
    public RedirectView createFromJson(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @RequestBody J jEntity) {
        final String path = createForLocation(request, domain, model, jEntity);
        return new RedirectView(path, true);
    }
    
    /**
     * Creates an Entity from the form-encoded body, 
     * and redirects to the created Entity.
     * @param domain the path-variable domain
     * @param _expects must be set to 200
     * @param jEntity The form-encoded body will be bound to this object
     * @return 200 and the created entity
     */
    @RestReturn(value=Object.class, code={
        @RestCode(code=200, description="Entity created", message="OK")
    })
    @RequestMapping(value="v10", method=RequestMethod.POST, 
            params={"_expects=200"},
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public J createFromFormWithContent(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @RequestParam(value="_expects") Integer _expects,
            Model model,
            @ModelAttribute J jEntity) {
        return createForObject(request, response, domain, model, jEntity);
    }
    
    @RequestMapping(value="v10", method=RequestMethod.POST, 
            params={"_expects=200"},
            consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public J createFromJsonWithContent(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @RequestBody J jEntity) {
        return createForObject(request, response, domain, model, jEntity);
    }
    
    /**
     * If you want to populate the request body, override populateRequestBody() method.
     * @param body the request body to create
     * @return 
     */
    protected T create(HttpServletRequest request, String domain, J body) {
        LOG.debug(body.toString());
        
        T d = convertJson(body);
        preService(request, domain, CrudListener.CREATE, body, d, null);
        ID id = service.create(d);
        postService(request, domain, CrudListener.CREATE, body, id, d);
        return d;
    }
    
    protected J createForObject(HttpServletRequest request,
            HttpServletResponse response,
            String domain,
            Model model, 
            J body) {
        J amendedBody = populateRequestBody(request, model, body);
        T d = create(request, domain, amendedBody);
        
        // default for GET is to include inner objects if any
        // should be included for createForObject too
        if (null != getInnerParameterNames()) {
            for (String name : getInnerParameterNames()) {
                request.setAttribute(name, Boolean.TRUE);
            }
        }
        
        return convertWithInner(request, response, domain, model, d);
    }
    
    protected String createForLocation(HttpServletRequest request, 
            String domain,
            Model model, J body) {
        J amendedBody = populateRequestBody(request, model, body);
        T d = create(request, domain, amendedBody);
        
        final StringBuffer path = new StringBuffer(request.getRequestURI());
        path.append('/');
        path.append(service.getSimpleKey(d));
        final String parentKeyString = service.getParentKeyString(d);
        if (null != parentKeyString) {
            path.append("?parentKeyString=");
            path.append(parentKeyString);
        }
        LOG.debug("Location for created entity is {}", path.toString());
        return path.toString();
    }
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.DELETE)
    public ResponseEntity delete(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestParam(required=false) String parentKeyString
            ) {
        LOG.debug("DELETE {}/{}", parentKeyString, id);
        
        preService(request, domain, CrudListener.DELETE, null, null, id);
        service.delete(parentKeyString, id);
        postService(request, domain, CrudListener.DELETE, null, id, null);
        
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value="v10/{id}", method=RequestMethod.GET,
            params={"_method=DELETE"})
    public ResponseEntity deleteFromJsonp(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestParam(required=false) String parentKeyString
            ) {
        return delete(request, response, domain, id, parentKeyString);
    }

    /**
     * Writes the entities as CSV on response body
     * @param response
     * @param startDate
     * @param endDate
     * @throws IOException 
     */
    @RestReturn(value=String.class, code={
        @RestCode(code=200, description="A CSV stream with Tasks", message="OK")})
    @RequestMapping(value="v10", method= RequestMethod.GET, params={"startDate"})
    public void exportCsv(
            HttpServletResponse response,
            @RequestParam Long startDate,
            @RequestParam(required=false) Long endDate
    ) throws IOException 
    {
        response.setContentType("text/csv");
        final String contentDisposition = String.format("attachment;filename=%ss.csv",
                service.getTableName());
        response.setHeader("Content-Disposition", contentDisposition);
        final OutputStream out = response.getOutputStream();
        
        service.exportCsv(out, startDate, endDate);
    }
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.GET)
    @ResponseBody
    public J get(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestParam(required=false) String parentKeyString,
            Model model
            ) {
        LOG.debug("GET {}/{}", parentKeyString, id);
        
        preService(request, domain, CrudListener.GET, null, null, id);
        T d = service.get(parentKeyString, id);
        if (null == d) {
            throw new NotFoundException(ERR_GET_NOT_FOUND, request.getQueryString());
        }
        
        // default for GET is to include inner objects if any
        if (null != getInnerParameterNames()) {
            for (String name : getInnerParameterNames()) {
                request.setAttribute(name, Boolean.TRUE);
            }
        }
        J body = convertWithInner(request,response, domain, model, d);
        postService(request, domain, CrudListener.GET, body, id, d);
        
        return body;
    }
    
    @RequestMapping(value="v10/manager.html", method={RequestMethod.GET, RequestMethod.POST})
    public String getBootstrap() {
        return "bootstrap-schema.html";
    }
    
    /**
     * Queries for non-deleted entities. If not found or soft-deleted, it will be excluded from the response.
     * @param id array of ids to retrieve
     * @return a Collection of non-deleted J objects
     */
    @RestReturn(value=List.class, code={
        @RestCode(code=200, description="A CursorPage with JSON entities", message="OK")})
    @RequestMapping(value="v10", method={RequestMethod.GET, RequestMethod.POST}, params={"id"})
    @ResponseBody
    public Collection<J> getExisting(
            @RequestParam ID[] id
            ) {
        final Iterable<T> page = service.getByPrimaryKeys(Arrays.asList(id));
        
        final Collection<J> body = convert(page);
        return body;
    }

    /**
     * Queries for a (next) page of entities
     * @param pageSize default is 10
     * @param cursorKey null to get first page
     * @return a page of entities
     */
    @RestReturn(value=JCursorPage.class, code={
        @RestCode(code=200, description="A CursorPage with JSON entities", message="OK")})
    @RequestMapping(value="v10", method= RequestMethod.GET)
    @ResponseBody
    public JCursorPage<J> getPage(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) Serializable cursorKey) {
        
        preService(request, domain, CrudListener.GET_PAGE, null, null, cursorKey);
        final CursorPage<T, ID> page = service.getPage(pageSize, cursorKey);
        final JCursorPage body = convertPage(page);
        postService(request, domain, CrudListener.GET_PAGE, null, cursorKey, body);

        return body;
    }
    
    @RequestMapping(value="v10/schema", method= RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSchema() {
        final TreeMap<String, Object> body = new TreeMap<String, Object>();
        body.put("tableName", service.getTableName());
        body.put("primaryKeyName", service.getPrimaryKeyColumnName());
        body.put("primaryKeyType", getType(service.getPrimaryKeyColumnName(), service.getPrimaryKeyColumnClass()));
        final TreeMap<String, String> columns = new TreeMap<String, String>();
        body.put("columns", columns);
        
        Class value;
        for (Entry<String, Class> entry : service.getTypeMap().entrySet()) {
            columns.put(entry.getKey(), getType(entry.getKey(), entry.getValue()));
        }
        
        return body;
    }
    
    public static String getType(String key, Class value) {
        if (Long.class.equals(value) ||
                Integer.class.equals(value) ||
                Short.class.equals(value) ||
                Byte.class.equals(value)) {
            return "number";
        }
        else if (String.class.equals(value)) {
            return "email".equals(key) ? "email" : "text";
        }
        else if (Boolean.class.equals(value)) {
            return "boolean";
        }
        else if (Date.class.equals(value)) {
            return "date";
        }
        return value.getSimpleName();
    }
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView updateFromForm(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            Model model,
            @ModelAttribute J jEntity) {
        return updateForLocation(request, response, 
                domain, id, 
                xRequestedWith, 
                model, 
                jEntity);
    }
    
    protected RedirectView updateForLocation(HttpServletRequest request,
            HttpServletResponse response,
            String domain,
            ID id,
            String xRequestedWith,
            Model model,
            J jEntity) {
        LOG.debug(jEntity.toString());

        J amendedBody = populateRequestBody(request, model, jEntity);
        T d = convertJson(amendedBody);
        preService(request, domain, CrudListener.UPDATE, jEntity, d, id);
        service.update(d);
        postService(request, domain, CrudListener.UPDATE, jEntity, id, d);
        
        final StringBuffer path = new StringBuffer("v10/");
        path.append(service.getSimpleKey(d));
        final String parentKeyString = service.getParentKeyString(d);
        if (null != parentKeyString) {
            path.append("?parentKeyString=");
            path.append(parentKeyString);
        }
        return new RedirectView(path.toString(), true);
    }
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.GET, 
            params={"_method=POST"},
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView updateFromJsonp(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            Model model,
            @ModelAttribute J jEntity) {
        return updateForLocation(request, response, 
                domain, id, 
                xRequestedWith, 
                model, jEntity);
    }
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    public RedirectView updateFromJson(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            Model model,
            @RequestBody J jEntity) {
        return updateForLocation(request, response, domain, id, xRequestedWith, 
                model, jEntity);
    }
    
    /**
     * Queries for a (next) page of ids which has changed since last update.
     * Header If-Modified-Since is required, timestamp of last update; use "Sat, 29 Oct 1994 19:43:31 GMT" if none
     * @param pageSize default is 10
     * @param cursorKey null to get first page
     * @return a page of ids that has updatedDate >= lastModified, or 304 Not Modified
     */
    @RestReturn(value=JCursorPage.class, entity=Long.class, code={
        @RestCode(code=200, description="A CursorPage with ids", message="OK"),
        @RestCode(code=304, description="Nothing changed since", message="Not Modified")})
    @RequestMapping(value="v10", method= RequestMethod.GET, headers={"If-Modified-Since"})
    @ResponseBody
    public CursorPage<ID, ID> whatsChanged(
            HttpServletRequest request,
            WebRequest webRequest,
            @PathVariable String domain,
            @RequestHeader(value="If-Modified-Since") Date since,
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) Serializable cursorKey) throws ParseException {
        final long currentMillis = System.currentTimeMillis();
        preService(request, domain, CrudListener.WHAT_CHANGED, null, null, cursorKey);
        final CursorPage<ID, ID> page = service.whatsChanged(since, pageSize, cursorKey);
        long lastModified = page.getItems().isEmpty() ? 0L : currentMillis;
        
        if (webRequest.checkNotModified(lastModified)) {
            // shortcut exit - no further processing necessary
            return null;
        }
        
        postService(request, domain, CrudListener.WHAT_CHANGED, null, cursorKey, page);
        
        return page;
    }
    
    // --------------- @ModelAttribute methods --------------------------
    
    /**
     * Override to populate body with values from request and model.
     * This implementation does nothing.
     * @param request the servlet request
     * @param model the model with @ModelAttribute objects
     * @param body the request body to populate
     * @return the amended request body
     */
    protected J populateRequestBody(HttpServletRequest request,
            Model model,
            J body) {
        return body;
    }
    
    // --------------- Converter methods --------------------------
    
    /** This implementation does nothing, please override */
    public J addInnerObjects(HttpServletRequest request, 
            HttpServletResponse response,
            String domain,
            Model model,
            J jEntity) {
        // do nothing
        return jEntity;
    }

    public J convertDomain(T from) {
        if (null == from) {
            return null;
        }
        J to = createJson();
        convertDomain(from, to);
        return to;
    }
    
    public T convertJson(J from) {
        if (null == from) {
            return null;
        }
        T to = createDomain();
        convertJson(from, to);
        return to;
    }

    public abstract void convertDomain(T from, J to);
    public abstract void convertJson(J from, T to);
    
    public J createJson() {
        try {
            return (J) jsonClass.newInstance();
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        }
        return null;
    }
    
    public T createDomain() {
        return service.createDomain();
    }
    
    public static void convertCreatedUpdatedEntity(AbstractCreatedUpdatedEntity from, JBaseObject to) {
        if (null == from || null == to) {
            return;
        }

        to.setCreatedBy(from.getCreatedBy());
        to.setCreatedDate(toLong(from.getCreatedDate()));
        to.setUpdatedBy(from.getUpdatedBy());
        to.setUpdatedDate(toLong(from.getUpdatedDate()));
    }
    
    protected J convertWithInner(HttpServletRequest request, HttpServletResponse response,
            String domain, Model model, T from) {
        final J to = convertDomain(from);
        return addInnerObjects(request, response, domain, model, to);
    }
    
    public static void convertLongEntity(AbstractLongEntity from, JBaseObject to) {
        if (null == from || null == to) {
            return;
        }

        convertCreatedUpdatedEntity((AbstractCreatedUpdatedEntity) from, to);
        
        to.setId(toString(from.getId()));
    }

    public static void convertStringEntity(AbstractStringEntity from, JBaseObject to) {
        if (null == from || null == to) {
            return;
        }

        convertCreatedUpdatedEntity((AbstractCreatedUpdatedEntity) from, to);
        
        to.setId(from.getId());
    }

    public static void convertJCreatedUpdated(JBaseObject from, AbstractCreatedUpdatedEntity to) {
        if (null == from || null == to) {
            return;
        }

        to.setCreatedBy(from.getCreatedBy());
        to.setCreatedDate(toDate(from.getCreatedDate()));
        to.setUpdatedBy(from.getUpdatedBy());
        to.setUpdatedDate(toDate(from.getUpdatedDate()));
    }
    
    public static void convertJLong(JBaseObject from, AbstractLongEntity to) {
        if (null == from || null == to) {
            return;
        }
        convertJCreatedUpdated(from, to);

        to.setId(toLong(from.getId()));
    }

    public static void convertJString(JBaseObject from, AbstractStringEntity to) {
        if (null == from || null == to) {
            return;
        }
        convertJCreatedUpdated(from, to);

        to.setId(from.getId());
    }

    // Convert iterable
    public Collection<J> convert(Iterable<T> from) {
        if (null == from)
            return new ArrayList<J>();

        final Collection<J> returnValue = new ArrayList<J>();

        J to;
        for (T o : from) {
            to = convertDomain(o);
            returnValue.add(to);
        }
        
        return returnValue;
    }

    // Convert Mardao DLocation
    public static JLocation convert(DLocation from) {
        if (null == from) {
            return null;
        }

        return new JLocation(from.getLatitude(), from.getLongitude());
    }
    
    public JCursorPage<J> convertPage(CursorPage<T, ID> from) {
        final JCursorPage<J> to = new JCursorPage<J>();
        
        to.setPageSize(from.getItems().size());
        to.setCursorKey(from.getCursorKey());
        to.setItems(convert(from.getItems()));
        
        return to;
    }

    protected Collection<String> getInnerParameterNames() {
        return Collections.EMPTY_LIST;
    }

    public static Long toLong(Date from) {
        if (null == from) {
            return null;
        }
        return from.getTime();
    }

    public static Long toLong(String from) {
        if (null == from) {
            return null;
        }
        return Long.parseLong(from);
    }

    public static Date toDate(Long from) {
        if (null == from) {
            return null;
        }
        return new Date(from);
    }

    public static String toString(Long from) {
        if (null == from) {
            return null;
        }
        return Long.toString(from);
    }

    public static Collection<Long> toLongs(Collection<String> from) {
        if (null == from) {
            return null;
        }

        final Collection<Long> to = new ArrayList<Long>();

        for(String s : from) {
            try {
                to.add(Long.parseLong(s));
            }
            catch (NumberFormatException sometimes) {
                to.add(null);
            }
        }

        return to;
    }

    public static Collection<String> toString(Collection<Long> from) {
        if (null == from) {
            return null;
        }

        final Collection<String> to = new ArrayList<String>(from.size());

        for(Long l : from) {
            to.add(l.toString());
        }

        return to;
    }
    
    public void addListener(CrudListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(CrudListener listener) {
        listeners.remove(listener);
    }
    
    protected void preService(HttpServletRequest request, String namespace,
            int operation, Object json, Object domain, Serializable id) {
        for (CrudListener l : listeners) {
            l.preService(this, service, request, namespace, 
                    operation, json, domain, id);
        }
    }
    
    protected void postService(HttpServletRequest request, String namespace,
            int operation, Object json, Serializable id, Object serviceResponse) {
        for (CrudListener l : listeners) {
            l.postService(this, service, request, namespace, 
                    operation, json, id, serviceResponse);
        }
    }

}
