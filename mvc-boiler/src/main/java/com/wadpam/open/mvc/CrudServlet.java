/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.mvc;

import com.wadpam.open.json.JBaseObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author sosandstrom
 */
public class CrudServlet extends HttpServlet {
    /** use in web.xml as init-param-name for controller classes */
    public static final String PARAM_NAME_CONTROLLER_CLASSES = "com.wadpam.open.CrudControllerClasses";
    
    public static final Pattern PATTERN_NO_ID = Pattern.compile("\\A/([^/]+)/([^/]+)/([^/]+)/v10[/]?\\z");
    public static final Pattern PATTERN_WITH_ID = Pattern.compile("\\A/([^/]+)/([^/]+)/([^/]+)/v10/([^/]+)[/]?\\z");
    
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }
    
    static final Logger LOG = LoggerFactory.getLogger(CrudServlet.class);
    
    private final Map<String,CrudController> ctrlMap = new HashMap<String, CrudController>();
    private final Map<String,String> primaryKeyTypeMap = new HashMap<String, String>();
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        // resolve the resource name, by convention:
        String classNames[] = config.getInitParameter(PARAM_NAME_CONTROLLER_CLASSES)
                .split(",\\s");
        for (String className : classNames) {
            try {
                // instantiate the Controller
                Class ctrlClass = getClass().getClassLoader().loadClass(className.trim());
                RequestMapping requestMapping = (RequestMapping) ctrlClass.getAnnotation(RequestMapping.class);
                if (null != requestMapping && 0 < requestMapping.value().length) {
                    CrudController ctrl = (CrudController) ctrlClass.newInstance();
                    Map schema = ctrl.getSchema();
                    String primaryKeyType = (String) schema.get("primaryKeyType");
                    for (String path : requestMapping.value()) {
                        int beginIndex = path.lastIndexOf('/')+1;
                        final String name = path.substring(beginIndex);
                        ctrlMap.put(name, ctrl);
                        primaryKeyTypeMap.put(name, primaryKeyType);
                        LOG.info("Initialized CrudServlet for {} with primaryKeyType {}", 
                                className.trim(), primaryKeyType);
                    }
                }
            } catch (ClassNotFoundException ex) {
                LOG.warn("Cannot load controller class", ex);
            } catch (InstantiationException ex) {
                LOG.warn("Cannot instantiate controller class", ex);
            } catch (IllegalAccessException ex) {
                LOG.warn("Cannot access controller class", ex);
            }
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
        LOG.debug("Invoking CrudServlet for {} {}", method, uri);
        
        // With or without /{id} ?
        String id = null;
        Serializable primaryKey = null;
        Matcher matcher = PATTERN_WITH_ID.matcher(uri);
        if (matcher.find()) {
            final String resource = matcher.group(3);
            id = matcher.group(4);
            // ID is Long or String
            if ("number".equals(primaryKeyTypeMap.get(resource))) {
                primaryKey = Long.parseLong(id);
            }
            else {
                primaryKey = id;
            }
        }
        else {
            matcher = PATTERN_NO_ID.matcher(uri);
            if (!matcher.find()) {
                response.setStatus(400);
                return;
            }
        }
        final String context = matcher.group(1);
        final String domain = matcher.group(2);
        final String resource = matcher.group(3);
        Object responseBody = null;
        final Model model = new ExtendedModelMap();
        final CrudController ctrl = ctrlMap.get(resource);
        if (null == ctrl) {
            response.setStatus(404);
            return;
        }
        
        LOG.debug("Invoking Controller for {} with ID {}", resource, primaryKey);
        
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
                final String _expects = request.getParameter("_expects");
                responseBody = ctrl.createFromJsonWithContent(request, response, domain, 200, model, requestBody);
                if ("200".equals(_expects)) {
                    // HTTP 200 Created
                    response.setStatus(200);
                }
                else {
                    // HTTP 302 Redirect
                    StringBuffer sb = request.getRequestURL();
                    if (sb.lastIndexOf("/")+1 < sb.length()) {
                        sb.append("/");
                    }
                    sb.append(((JBaseObject)responseBody).getId());
                    responseBody = null;
                    response.sendRedirect(sb.toString());
                }
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
        LOG.debug("Writing response for body {}", responseBody);
        if (null != responseBody) {
            response.setContentType("application/json");
            final OutputStream out = response.getOutputStream();
            MAPPER.writeValue(out, responseBody);
            out.flush();
            out.close();
        }
        LOG.info("Done processing request {} {}", method, uri);
    }
}
