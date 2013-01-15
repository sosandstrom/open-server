package com.wadpam.open.mvc;

import com.sun.net.httpserver.Headers;
import com.wadpam.docrest.domain.RestCode;
import com.wadpam.docrest.domain.RestReturn;
import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.open.json.JBaseObject;
import com.wadpam.open.json.JCursorPage;
import com.wadpam.open.json.JLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.mardao.core.CursorPage;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;
import net.sf.mardao.core.domain.AbstractLongEntity;
import net.sf.mardao.core.geo.DLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        J extends JBaseObject, 
        T extends Object, 
        ID extends Serializable,
        S extends CrudService<T, ID>> {
    
    public static final String NAME_X_REQUESTED_WITH = "X-Requested-With";
    public static final String VALUE_X_REQUESTED_WITH_AJAX = "XMLHttpRequest";
    
    public static final int ERR_CRUD_BASE = 99000;
    public static final int ERR_DELETE_NOT_FOUND = ERR_CRUD_BASE + 1;
    public static final int ERR_GET_NOT_FOUND = ERR_CRUD_BASE + 2;
    
    protected static final Logger LOG = LoggerFactory.getLogger(CrudController.class);
    
    protected S service;
    
    @RequestMapping(value="v10", method=RequestMethod.POST, 
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView createFromForm(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @ModelAttribute J jEntity) {
        
        final String path = createForLocation(request, jEntity);
        return new RedirectView(path, true);
    }
    
    @RequestMapping(value="v10", method=RequestMethod.GET, 
            params={"_method=POST"},
            headers={"X-Requested-With=XMLHttpRequest"},
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity createFromJsonp(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @ModelAttribute J jEntity) {
        final String path = createForLocation(request, jEntity);
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Location", path);
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }
    
    @RequestMapping(value="v10", method=RequestMethod.POST, 
            consumes=MediaType.APPLICATION_JSON_VALUE)
    public RedirectView createFromJson(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @RequestBody J jEntity) {
        final String path = createForLocation(request, jEntity);
        return new RedirectView(path, true);
    }
    
    @RequestMapping(value="v10", method=RequestMethod.POST, 
            params={"_expects=200"},
            consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public J createFromFormWithContent(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @ModelAttribute J jEntity) {
        return createForObject(request, jEntity);
    }
    
    @RequestMapping(value="v10", method=RequestMethod.POST, 
            params={"_expects=200"},
            consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public J createFromJsonWithContent(HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @RequestBody J jEntity) {
        return createForObject(request, jEntity);
    }
    
    protected T create(J body) {
        LOG.debug(body.toString());
        
        T d = convertJson(body);
        service.create(d);
        return d;
    }
    
    protected J createForObject(HttpServletRequest request, J body) {
        T d = create(body);
        return convertWithInner(request, d);
    }
    
    protected String createForLocation(HttpServletRequest request, J body) {
        T d = create(body);
        
        final StringBuffer path = new StringBuffer(request.getRequestURI());
        path.append('/');
        path.append(service.getSimpleKey(d));
        final String parentKeyString = service.getParentKeyString(d);
        if (null != parentKeyString) {
            path.append("?parentKeyString=");
            path.append(parentKeyString);
        }
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
        
        service.delete(parentKeyString, id);
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
            @RequestParam(required=false) String parentKeyString
            ) {
        LOG.debug("GET {}/{}", parentKeyString, id);
        
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
        J body = convertWithInner(request, d);
        
        return body;
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
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) Serializable cursorKey) {
        final CursorPage<T, ID> page = service.getPage(pageSize, cursorKey);
        final JCursorPage body = convertPage(page);

        return body;
    }
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView updateFromForm(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            @ModelAttribute J jEntity) {
        LOG.debug(jEntity.toString());

        // patch id in body?
        if (null == jEntity.getId()) {
            jEntity.setId(id.toString());
        }
        
        T d = convertJson(jEntity);
        service.update(d);
        
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
            @ModelAttribute J jEntity) {
        return updateFromForm(request, response, domain, id, xRequestedWith, jEntity);
    }
    
    @RequestMapping(value="v10/{id}", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    public RedirectView updateFromJson(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String domain,
            @PathVariable ID id,
            @RequestHeader(value=NAME_X_REQUESTED_WITH, required=false) String xRequestedWith,
            @RequestBody J jEntity) {
        return updateFromForm(request, response, domain, id, xRequestedWith, jEntity);
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
            WebRequest request,
            @RequestHeader(value="If-Modified-Since") Date since,
            @RequestParam(defaultValue="10") int pageSize, 
            @RequestParam(required=false) Serializable cursorKey) throws ParseException {
        final long currentMillis = System.currentTimeMillis();
        final CursorPage<ID, ID> page = service.whatsChanged(since, pageSize, cursorKey);
        long lastModified = page.getItems().isEmpty() ? 0L : currentMillis;
        
        if (request.checkNotModified(lastModified)) {
            // shortcut exit - no further processing necessary
            return null;
        }
        
        return page;
    }
    
    // --------------- Converter methods --------------------------
    
    /** This implementation does nothing, please override */
    public J addInnerObjects(HttpServletRequest request, J jEntity) {
        // do nothing
        return jEntity;
    }

    public abstract J convertDomain(T from);
    public abstract T convertJson(J from);
    
    public static void convertCreatedUpdatedEntity(AbstractCreatedUpdatedEntity from, JBaseObject to) {
        if (null == from || null == to) {
            return;
        }

        to.setCreatedBy(from.getCreatedBy());
        to.setCreatedDate(toLong(from.getCreatedDate()));
        to.setUpdatedBy(from.getUpdatedBy());
        to.setUpdatedDate(toLong(from.getUpdatedDate()));
    }
    
    protected J convertWithInner(HttpServletRequest request, T from) {
        final J to = convertDomain(from);
        return addInnerObjects(request, to);
    }
    
    public static void convertLongEntity(AbstractLongEntity from, JBaseObject to) {
        if (null == from || null == to) {
            return;
        }

        convertCreatedUpdatedEntity((AbstractCreatedUpdatedEntity) from, to);
        
        to.setId(toString(from.getId()));
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
    
    
}
