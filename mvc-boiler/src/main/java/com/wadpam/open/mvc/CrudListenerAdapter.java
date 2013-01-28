/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.mvc;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author sosandstrom
 */
public class CrudListenerAdapter implements CrudListener {

    @Override
    public void preService(CrudController controller, CrudService service, HttpServletRequest request, String namespace, int operation, Object json, Object domain, Serializable id) {
        switch (operation) {
            case CREATE:
                preCreate(controller, service, request, namespace, json, domain);
                break;
            case GET:
                preGet(controller, service, request, namespace, id);
                break;
            case UPDATE:
                preUpdate(controller, service, request, namespace, json, domain, id);
                break;
            case DELETE:
                preDelete(controller, service, request, namespace, id);
                break;
        }
    }

    @Override
    public void postService(CrudController controller, CrudService service, HttpServletRequest request, String namespace, int operation, Object json, Serializable id, Object serviceResponse) {
        switch (operation) {
            case CREATE:
                postCreate(controller, service, request, namespace, json, id, serviceResponse);
                break;
            case GET:
                postGet(controller, service, request, namespace, id, serviceResponse, json);
                break;
            case UPDATE:
                postUpdate(controller, service, request, namespace, json, id, serviceResponse);
                break;
            case DELETE:
                postDelete(controller, service, request, namespace, id);
        }
    }

    /**
     * Override to implement the pre-service create callback
     * @param controller
     * @param service
     * @param request
     * @param namespace
     * @param json
     * @param domain
     */
    protected void preCreate(CrudController controller, CrudService service, HttpServletRequest request, String namespace, Object json, Object domain) {
    }

    protected void preGet(CrudController controller, CrudService service, HttpServletRequest request, String namespace, Serializable id) {
    }

    protected void preUpdate(CrudController controller, CrudService service, HttpServletRequest request, String namespace, Object json, Object domain, Serializable id) {
    }

    protected void preDelete(CrudController controller, CrudService service, HttpServletRequest request, String namespace, Serializable id) {
    }
    
    /**
     * Override to implement the post-service create callback
     * @param controller
     * @param service
     * @param request
     * @param domain
     * @param json
     * @param id
     * @param serviceResponse 
     */
    protected void postCreate(CrudController controller, CrudService service, HttpServletRequest request, String domain, Object json, Serializable id, Object serviceResponse) {
    }

    /**
     * 
     * @param controller
     * @param service
     * @param request
     * @param namespace
     * @param id
     * @param serviceResponse
     * @param jsonResponse 
     */
    protected void postGet(CrudController controller, CrudService service, HttpServletRequest request, String namespace, Serializable id, Object serviceResponse, Object jsonResponse) {
    }

    protected void postUpdate(CrudController controller, CrudService service, HttpServletRequest request, String namespace, Object json, Serializable id, Object serviceResponse) {
    }

    protected void postDelete(CrudController controller, CrudService service, HttpServletRequest request, String namespace, Serializable id) {
    }

}
