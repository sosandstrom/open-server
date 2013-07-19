package com.wadpam.open.mvc;

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

import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.open.json.JBaseObject;
import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.json.JLocation;
import java.text.SimpleDateFormat;

/**
 *
 * @author os
 */
public abstract class CrudController<
        J extends Object, 
        T extends Object, 
        ID extends Serializable,
        S extends CrudService<T, ID>> implements CrudObservable {
    
    public static final String NAME_X_REQUESTED_WITH = "X-Requested-With";
    public static final String VALUE_X_REQUESTED_WITH_AJAX = "XMLHttpRequest";
    
    public static final int ERR_CRUD_BASE = 99000;
    public static final int ERR_DELETE_NOT_FOUND = ERR_CRUD_BASE + 1;
    public static final int ERR_GET_NOT_FOUND = ERR_CRUD_BASE + 2;
    
    /** Sat, 29 Oct 1994 19:43:31 GMT */
    public static final SimpleDateFormat LAST_MODIFIED_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    
    protected static final Logger LOG = LoggerFactory.getLogger(CrudController.class);
    
    protected final Class jsonClass;
    protected S service;
    protected final Class idClass;
    protected final ArrayList<CrudListener> listeners = new ArrayList<CrudListener>();
    
    protected CrudController(Class<J> jsonClazz, Class idClass) {
        this.jsonClass = jsonClazz;
        this.idClass = idClass;
    }
    
    protected CrudController(Class<J> jsonClazz) {
        this.jsonClass = jsonClazz;
        this.idClass = Long.class;
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
        
        final String id = createForId(request, domain, model, jEntity);
        final String httpUrl = request.getRequestURL().toString();
        final String absoluteUrl = String.format("%s/%s", httpUrl, id);
        return new RedirectView(absoluteUrl);
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
            params={"_method=POST"})
    public ResponseEntity createFromJsonp(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @ModelAttribute J jEntity) {
        final String id = createForId(request, domain, model, jEntity);
        final String httpUrl = request.getRequestURL().toString();
        final String absoluteUrl = String.format("%s/%s", httpUrl, id);
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Location", absoluteUrl);
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
        final String id = createForId(request, domain, model, jEntity);
        final String httpUrl = request.getRequestURL().toString();
        final String absoluteUrl = String.format("%s/%s", httpUrl, id);
        return new RedirectView(absoluteUrl);
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
    
  /**
     * Creates an Entity from the json-object-encoded body by MediaType.APPLICATION_JSON_VALUE, 
     * and redirects to the created Entity.
     * @param domain the path-variable domain
     * @param _expects must be set to 200
     * @param jEntity The Request body will be bound to this object
     * @return 200 and the created entity
     */
    @RestReturn(value=Object.class, code={
        @RestCode(code=200, description="Entity created", message="OK")
    })
    @RequestMapping(value="v10", method=RequestMethod.POST, 
            params={"_expects=200"},
            consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public J createFromJsonWithContent(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @RequestParam(value="_expects") Integer _expects,
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
    
    protected String createForId(HttpServletRequest request, 
            String domain,
            Model model, J body) {
        J amendedBody = populateRequestBody(request, model, body);
        T d = create(request, domain, amendedBody);
        
        final StringBuffer path = new StringBuffer();
        path.append(service.getSimpleKey(d));
        final String parentKeyString = service.getParentKeyString(d);
        if (null != parentKeyString) {
            path.append("?parentKeyString=");
            path.append(parentKeyString);
        }
        LOG.debug("Path for created entity is {}", path.toString());
        return path.toString();
    }
    
	 /**
	     *  delete an Entity and return HttpStatus.NO_CONTENT .
	     * @param domain the path-variable domain
	     * @param id the path-variable id	 	     
	     * @return  HttpStatus.NO_CONTENT
	     */
    @RestReturn(value=ResponseEntity.class, code={
        @RestCode(code=204, description="Entity deleted", message="OK")
    })		 
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

	 /**
	     * deleteFromJsonp  for cross-domain - delete an Entity and return HttpStatus.NO_CONTENT .
	     * @param domain the path-variable domain
	     * @param id the path-variable id
	     * @param _method value must be  "DELETE"	 	 
	     * @return  HttpStatus.NO_CONTENT
	     */	
    @RestReturn(value=ResponseEntity.class, code={
        @RestCode(code=204, description="Entity deleted", message="OK")
    })		 
    @RequestMapping(value="v10/{id}", method=RequestMethod.GET,
            params={"_method=DELETE"})
    public ResponseEntity deleteFromJsonp(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestParam(required=false) String parentKeyString,
            @RequestParam("_method") String _method
            ) {
        return delete(request, response, domain, id, parentKeyString);
    }

    /**
    * deleteFromJsonp  for cross-domain - delete an Entity and return HttpStatus.NO_CONTENT . 
    * use param id the ids as an array
    * @param domain the path-variable domain
    * @param id the ids as an array
    * @param _method value must be  "DELETE"	 	 
    * @return  HttpStatus.NO_CONTENT
    */	
    @RestReturn(value=ResponseEntity.class, code={
        @RestCode(code=204, description="Entities deleted", message="OK")
    })		 
    @RequestMapping(value="v10", method=RequestMethod.DELETE)
    public ResponseEntity deleteBatch(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @RequestParam String[] id,
            @RequestParam(required=false) String parentKeyString
            ) {
        LOG.debug("DELETE {}/{}", parentKeyString, id);
        
        Collection simpleKeys =convertDomainPrimaryKeys(id);
        
        preService(request, domain, CrudListener.DELETE_BATCH, null, null, id);
        service.delete(parentKeyString, simpleKeys);

        postService(request, domain, CrudListener.DELETE_BATCH, null, id, null);
        
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
    * deleteFromJsonp  for cross-domain - delete an Entity and return HttpStatus.NO_CONTENT .
    * @param domain the path-variable domain
    * @param id the ids as an array
    * @param _method value must be  "DELETE"	 	 
    * @return  HttpStatus.NO_CONTENT
    */	
    @RestReturn(value=ResponseEntity.class, code={
        @RestCode(code=204, description="Entities deleted", message="OK")
    })		 
    @RequestMapping(value="v10", method=RequestMethod.GET,
            params={"_method=DELETE"})
    public ResponseEntity deleteBatchFromJsonp(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @RequestParam String[] id,
            @RequestParam(required=false) String parentKeyString,
            @RequestParam("_method") String _method
            ) {
        return deleteBatch(request, response, domain, id, parentKeyString);
    }

    /**
     * get Entity details
     * @param domain the domain namespace
     * @param id the Entity's primary key or ID
     * @param parentKeyString the Entity's parent key as String if any
     * @return Entity as json object, 404 if not found
     */
    @RestReturn(value=Object.class, code={
        @RestCode(code=200, description="get Entity successfully", message="OK"),
        @RestCode(code=404, description="Entity not found", message="Not Found")
    },
    supportsClassParams=true)
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
     * The param i array of ids to retrieve.
     * @param i array of ids to retrieve
     * @return a Collection of non-deleted J objects
     */
    @RestReturn(value=List.class, code={
        @RestCode(code=200, description="A CursorPage with JSON entities", message="OK")})
    @RequestMapping(value="v10", method={RequestMethod.GET, RequestMethod.POST}, params={"i"})
    @ResponseBody
    public Collection<J> getExisting(HttpServletRequest request,
            @RequestParam String[] i) {
        
        Collection simpleKeys =convertDomainPrimaryKeys(i);
        final Iterable<T> page = service.getByPrimaryKeys(simpleKeys);
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
        @RestCode(code=200, description="A CursorPage with JSON entities", message="OK")},
    supportsClassParams=true)
    @RequestMapping(value="v10", method= RequestMethod.GET)
    @ResponseBody
    public JCursorPage<J> getPage(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) String cursorKey) {
        
        preService(request, domain, CrudListener.GET_PAGE, null, null, cursorKey);
        final CursorPage<T> page = service.getPage(pageSize, cursorKey);
        final JCursorPage body = convertPageWithInner(request, response, domain, 
                model, page);
        postService(request, domain, CrudListener.GET_PAGE, body, cursorKey, page);

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
            return "datetime";
        }
        return value.getSimpleName();
    }
    
    protected T update(HttpServletRequest request,
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
        
        return d;
    }
	  /**
	     * update an Entity from the form-encoded body, 
	     * and return HttpStatus.NO_CONTENT.
	     * @param domain the path-variable domain
	     * @param id the path-variable id
	     * @param jEntity The form-encoded body will be bound to this object
	     * @return HttpStatus.NO_CONTENT.
	     */
    @RestReturn(value=ResponseEntity.class, code={
        @RestCode(code=204, description="Entity updated", message="OK")
    })
    @RequestMapping(value="v10/{id}", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity updateFromForm(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            Model model,
            @ModelAttribute J jEntity) {
        
        update(request, response, 
                domain, id, 
                xRequestedWith, 
                model, 
                jEntity);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    
	 /**  updateFromJsonp update entity with cross-domain jsonp	     
	     * and return HttpStatus.NO_CONTENT.
	     * @param domain the path-variable domain
	     * @param id the path-variable id
              * @param  _method  value must be "POST"
	     * @param jEntity The form-encoded body will be bound to this object
	     * @return HttpStatus.NO_CONTENT.
	     */
	@RestReturn(value=ResponseEntity.class, code={
        @RestCode(code=204, description="Entity updated", message="OK")
    })	 
    @RequestMapping(value="v10/{id}", method=RequestMethod.GET, 
            params={"_method=POST"},
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity updateFromJsonp(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            Model model,
            @ModelAttribute J jEntity) {

        update(request, response, 
                domain, id, 
                xRequestedWith, 
                model, 
                jEntity);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    
	 /**  updateFromJson update entity with MediaType.APPLICATION_JSON_VALUE header
	     * and return HttpStatus.NO_CONTENT.
	     * @param domain the path-variable domain
	     * @param id the path-variable id        
	     * @param jEntity  RequestBody
	     * @return HttpStatus.NO_CONTENT.
	     */
	@RestReturn(value=ResponseEntity.class, code={
        @RestCode(code=204, description="Entity updated", message="OK")
    })
    @RequestMapping(value="v10/{id}", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateFromJson(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            Model model,
            @RequestBody J jEntity) {
        
        update(request, response, 
                domain, id, 
                xRequestedWith, 
                model, 
                jEntity);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    
    protected RedirectView updateForLocation(HttpServletRequest request,
            HttpServletResponse response,
            String domain,
            ID id,
            String xRequestedWith,
            Model model,
            J jEntity) {

        T d = update(request, response, domain, id, xRequestedWith, model, jEntity);
        
        final StringBuffer path = new StringBuffer();
        final String parentKeyString = service.getParentKeyString(d);
        if (null != parentKeyString) {
            path.append("?parentKeyString=");
            path.append(parentKeyString);
        }
        final String httpUrl = request.getRequestURL().toString();
        final String absoluteUrl = String.format("%s%s", httpUrl, path.toString());
        
        return new RedirectView(absoluteUrl);
    }
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.POST, 
            params={"_expects=302"},
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView updateFromFormForLocation(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestParam(value="_expects") Integer _expects,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            Model model,
            @ModelAttribute J jEntity) {
        return updateForLocation(request, response, 
                domain, id, 
                xRequestedWith, 
                model, 
                jEntity);
    }
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.GET, 
            params={"_method=POST", "_expects=302"},
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView updateFromJsonpForLocation(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestParam(value="_expects") Integer _expects,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            Model model,
            @ModelAttribute J jEntity) {
        return updateForLocation(request, response, 
                domain, id, 
                xRequestedWith, 
                model, jEntity);
    }

    
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.POST, 
            params={"_expects=302"},
            consumes=MediaType.APPLICATION_JSON_VALUE)
    public RedirectView updateFromJsonForLocation(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestParam(value="_expects") Integer _expects,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            Model model,
            @RequestBody J jEntity) {
        return updateForLocation(request, response, domain, id, xRequestedWith, 
                model, jEntity);
    }
    
  /**
     * Upserts a batch from the json-object-encoded array body by MediaType.APPLICATION_JSON_VALUE, 
     * and responds with the upserted IDs.
     * @param domain the path-variable domain
     * @param jEntities The Request body will be bound to this object array
     * @return 200 and the upserted Ids
     */
    @RestReturn(value=Object.class, code={
        @RestCode(code=200, description="Batch of Entities upserted", message="OK")
    })
    @RequestMapping(value="v10/_batch", method=RequestMethod.POST, 
            consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ID> upsertBatchFromJsonWithContent(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            Model model,
            @RequestBody ArrayList<Map<String,Object>> jEntities) {
        LOG.debug("upserting {} entities", jEntities.size());

        ArrayList<T> dEntities = new ArrayList<T>();
        for (Map<String,Object> map : jEntities) {
            J jEntity = convertMap(map);
            J amendedBody = populateRequestBody(request, model, jEntity);
            T d = convertJson(amendedBody);
            dEntities.add(d);
        }
        
        preService(request, domain, CrudListener.UPSERT_BATCH, jEntities, dEntities, null);
        final List<ID> body = service.upsert(dEntities);
        postService(request, domain, CrudListener.UPSERT_BATCH, jEntities, null, body);

        return body;
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
    public CursorPage<ID> whatsChanged(
            HttpServletRequest request,
            WebRequest webRequest,
            @PathVariable String domain,
            @RequestHeader(value="If-Modified-Since") String since,
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) String cursorKey) throws ParseException {
        final long currentMillis = System.currentTimeMillis();
        preService(request, domain, CrudListener.WHAT_CHANGED, null, null, cursorKey);
        final Date sinceDate = LAST_MODIFIED_DATE_FORMAT.parse(since);
        final CursorPage<ID> page = service.whatsChanged(sinceDate, pageSize, cursorKey);
        long lastModified = page.getItems().isEmpty() ? 1000L : currentMillis;
        LOG.debug("page contains {} IDs", page.getItems().size());
        
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
    
    public void addInnerObjects(HttpServletRequest request, 
            HttpServletResponse response,
            String domain,
            Model model,
            J jEntity) {
        addInnerObjects(request, response, domain, 
                model, Arrays.asList(jEntity));
    }

    /** This implementation does nothing, please override */
    public void addInnerObjects(HttpServletRequest request, 
            HttpServletResponse response,
            String domain,
            Model model,
            Iterable<J> jEntity) {
        // do nothing
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

    public J convertMap(Map<String,Object> from) {
        if (null == from) {
            return null;
        }
        J to = createJson();
        convertJMap(from, to);
        return to;
    }

    public abstract void convertDomain(T from, J to);
    public abstract void convertJson(J from, T to);
    
    /**
     * Override to get upsertBatch to work!
     * @param from is a LinkedHashMap created by Jackson
     */
    public void convertJMap(Map<String,Object> from, J to) {
        throw new UnsupportedOperationException("Override in subclass!"); 
    }
    
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
        addInnerObjects(request, response, domain, model, to);
        return to;
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
    
    public static void convertJCreatedUpdated(Map<String, Object> from, JBaseObject to) {
        if (null == from || null == to) {
            return;
        }

        to.setCreatedBy((String)from.get("createdBy"));
        to.setCreatedDate((Long)from.get("createdDate"));
        to.setUpdatedBy((String)from.get("updatedBy"));
        to.setUpdatedDate((Long)from.get("updatedDate"));
    }
    
    public static void convertJLong(JBaseObject from, AbstractLongEntity to) {
        if (null == from || null == to) {
            return;
        }
        convertJCreatedUpdated(from, to);

        to.setId(toLong(from.getId()));
    }
    
    public static void convertJLong(Map<String, Object> from, JBaseObject to) {
        if (null == from || null == to) {
            return;
        }
        convertJCreatedUpdated(from, to);

        Object id = from.get("id");
        to.setId(null != id ? id.toString() : null);
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

    // Convert iterable
    public Collection<J> convertWithInner(HttpServletRequest request, HttpServletResponse response,
            String domain, Model model, Iterable<T> from) {
        if (null == from)
            return new ArrayList<J>();

        // basic conversion first
        final Collection<J> returnValue = convert(from);

        // then add inner objects batch-style
        addInnerObjects(request, response, domain, model, returnValue);
        
        return returnValue;
    }

    // Convert Mardao DLocation
    public static JLocation convert(DLocation from) {
        if (null == from) {
            return null;
        }

        return new JLocation(from.getLatitude(), from.getLongitude());
    }
    
    public JCursorPage<J> convertPage(CursorPage<T> from) {
        final JCursorPage<J> to = new JCursorPage<J>();
        
        to.setPageSize(from.getItems().size());
        to.setCursorKey(from.getCursorKey());
        to.setTotalSize(from.getTotalSize());
        to.setItems(convert(from.getItems()));
        
        return to;
    }

    public JCursorPage<J> convertPageWithInner(HttpServletRequest request, HttpServletResponse response,
            String domain, Model model, CursorPage<T> from) {
        final JCursorPage<J> to = new JCursorPage<J>();
        
        to.setPageSize(from.getItems().size());
        to.setCursorKey(from.getCursorKey());
        to.setTotalSize(from.getTotalSize());
        to.setItems(convertWithInner(request, response, domain, model, 
                from.getItems()));
        
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
    
    @Override
    public void addListener(CrudListener listener) {
        listeners.add(listener);
    }
    
    @Override
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
    
    protected Collection convertDomainPrimaryKeys(String[] id) {
        LOG.debug("convertDomainPrimaryKeys : {}",  id);
        Collection simpleKeys =null;
        if (Long.class.equals(idClass)){
            simpleKeys =CrudController.toLongs(Arrays.asList(id));
        } else {
            simpleKeys=Arrays.asList(id);
        }
        return simpleKeys;
    }
    // -----------------  getters and setters    -------------------------------

    public S getService() {
        return service;
    }

    public void setService(S service) {
        this.service = service;
    }
    
  
}

