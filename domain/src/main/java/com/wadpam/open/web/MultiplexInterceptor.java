package com.wadpam.open.web;

import com.wadpam.open.domain.DAppDomain;
import com.wadpam.open.mvc.MultiplexCrudService;
import com.wadpam.open.security.SecurityInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author sosandstrom
 */
public class MultiplexInterceptor extends HandlerInterceptorAdapter {
    
    static final Logger LOG = LoggerFactory.getLogger(MultiplexInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object principal = request.getAttribute(SecurityInterceptor.ATTR_NAME_PRINCIPAL);
        if (null != principal && principal instanceof DAppDomain) {
            final DAppDomain domain = (DAppDomain) principal;
            final String mux = domain.getAppArg1();
            LOG.debug("Setting MUX to {}", mux);
            MultiplexCrudService.setMultiplexKey(mux);
        }
        return true;
    }

    
    
}
