/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 *
 * @author sosandstrom
 */
public class CrudServlet extends HttpServlet {
    /** use in web.xml as init-param-name for resource name */
    public static final String PARAM_NAME_RESOURCE = "com.wadpam.open.CrudResource";
    
    public static final Pattern PATTERN_NO_ID = Pattern.compile("\\A/([^/]+)/([^/]+)/([^/]+)/v10[/]?\\z");
    public static final Pattern PATTERN_WITH_ID = Pattern.compile("\\A/([^/]+)/([^/]+)/([^/]+)/v10/([^/]+)[/]?\\z");
    
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }
    
    private CrudController ctrl;
    private Map<String, Object> schema;
    private String primaryKeyType;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        // resolve the resource name, by convention:
        String resourceName = config.getInitParameter(PARAM_NAME_RESOURCE);
        // by parameter?
        if (null == resourceName) {
            int endIndex = config.getServletName().lastIndexOf("Servlet");
            resourceName = config.getServletName().substring(0, endIndex);
        }
        try {
            // instantiate the Controller
            Class ctrlClass = getClass().getClassLoader().loadClass(
                    String.format("%sController", resourceName));
            ctrl = (CrudController) ctrlClass.newInstance();
            
            schema = ctrl.getSchema();
            primaryKeyType = (String) schema.get("primaryKeyType");
            
        } catch (ClassNotFoundException ex) {
            throw new ServletException("Cannot load controller class", ex);
        } catch (InstantiationException ex) {
            throw new ServletException("Cannot instantiate controller class", ex);
        } catch (IllegalAccessException ex) {
            throw new ServletException("Cannot access controller class", ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        process(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        process(request, response);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        final String uri = request.getRequestURI();
        final String method = request.getMethod();
        final String parentKeyString = request.getParameter("parentKeyString");
        
        // With or without /{id} ?
        String id = null;
        Serializable primaryKey = null;
        Matcher matcher = PATTERN_WITH_ID.matcher(uri);
        if (matcher.find()) {
            
        }
        else {
            matcher = PATTERN_NO_ID.matcher(uri);
            if (!matcher.find()) {
                response.setStatus(400);
                return;
            }
            id = matcher.group(4);
            // ID is Long or String
            if ("number".equals(primaryKeyType)) {
                primaryKey = Long.parseLong(id);
            }
            else {
                primaryKey = id;
            }
        }
        final String context = matcher.group(1);
        final String domain = matcher.group(2);
        final String resource = matcher.group(3);
        Object responseBody = null;
        final Model model = new ExtendedModelMap();
        
        if ("GET".equalsIgnoreCase(method)) {
            
            // GET Page or GET Details?
            if (null == id) {
                final String pageSizeString = request.getParameter("pageSize");
                final int pageSize = Integer.parseInt(null != pageSizeString ? pageSizeString : "10");
                final String cursorKey = request.getParameter("cursorKey");
                responseBody = ctrl.getPage(request, response, domain, model, pageSize, cursorKey);
            }
            else {
                responseBody = ctrl.get(request, response, domain, primaryKey, parentKeyString, model);
            }
            
            response.setStatus(200);
        }
        else if ("POST".equalsIgnoreCase(method)) {
            // bind JSON to Object
            final InputStream in = request.getInputStream();
            Object requestBody = MAPPER.readValue(in, ctrl.jsonClass);
            
            // Create or Update resource?
            if (null == id) {
                responseBody = ctrl.createFromJsonWithContent(request, response, domain, 200, model, requestBody);
                // HTTP 201 Created
                response.setStatus(201);
            }
            else {
                // ignore response body
                ctrl.updateFromJson(request, response, domain, primaryKey, null, model, requestBody);
                // HTTP 204 No Content
                response.setStatus(204);
            }
        }
        else if ("DELETE".equalsIgnoreCase(method) && null != id) {
            ctrl.delete(request, response, domain, primaryKey, parentKeyString);
            response.setStatus(200);
        }
        else {
            // method not supported
            response.setStatus(405);
        }

        // serialize body to JSON, using Jackson
        if (null != responseBody) {
            final OutputStream out = response.getOutputStream();
            MAPPER.writeValue(out, responseBody);
            response.setContentType("application/json");
        }
    }
}
