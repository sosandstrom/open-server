package com.wadpam.open.user.web;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.mardao.core.dao.DaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Fetches the authenticated user's name, and set the Mardao principalName
 * @author os
 */
public class MardaoPrincipalInterceptor extends HandlerInterceptorAdapter {
    
    static final Logger LOG = LoggerFactory.getLogger(MardaoPrincipalInterceptor.class);
    
//    protected static String getSpringSecurityUsername() {
//        String currentUser = null;
//        final SecurityContext ctx = SecurityContextHolder.getContext();
//        if (null != ctx) {
//            final Authentication auth = ctx.getAuthentication();
//            if (null != auth) {
//                currentUser = auth.getName();
//            }
//        }
//        return currentUser;
//    }
    
    protected static String getRequestPrincipalName(HttpServletRequest request) {
        String currentUser = null;
        final Principal principal = request.getUserPrincipal();
        if (null != principal) {
            currentUser = principal.getName();
        }
        return currentUser;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // set the mardao user
        final String currentUser = getRequestPrincipalName(request);
        DaoImpl.setPrincipalName(currentUser);
        LOG.debug("Set mardao principalName to {}", currentUser);
            
        return true;
    }
    
}
