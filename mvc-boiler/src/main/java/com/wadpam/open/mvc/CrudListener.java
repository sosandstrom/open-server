/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.open.mvc;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;

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
    public static final int DELETE_BATCH = 14;
    
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
