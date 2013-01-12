package com.wadpam.open.jsonp;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 *
 * @author os
 */
public class AccessControlAllowFilter implements Filter {
    static final Logger LOG = LoggerFactory.getLogger(AccessControlAllowFilter.class);

    @Override
    public void init(FilterConfig fc) throws ServletException {
        LOG.debug("init()");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // cast to HTTP
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        
        // populate response 
        final String accessControlRequestMethod = request.getHeader("Access-Control-Request-Method");
        if (null != accessControlRequestMethod) {
            response.setHeader("Access-Control-Allow-Methods", accessControlRequestMethod);
            LOG.debug("Access-Control-Allow-Methods: {}", accessControlRequestMethod);
        }
        
        final String accessControlRequestHeaders = request.getHeader("Access-Control-Request-Headers");
        if (null != accessControlRequestHeaders) {
            response.setHeader("Access-Control-Allow-Headers", accessControlRequestHeaders);
            LOG.debug("Access-Control-Allow-Headers: {}", accessControlRequestHeaders);
        }
        
        final String accessControlRequestOrigin = request.getHeader("Origin");
        if (null != accessControlRequestOrigin) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            LOG.debug("Access-Control-Allow-Origin: * for {}", accessControlRequestOrigin);
        }
        
        // OK for HTTP OPTIONS?
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpStatus.OK.value());
        }
        else {
            // service the GET, POST, DELETE, PUT request
            chain.doFilter(req, res);
        }
        
    }

    @Override
    public void destroy() {
        LOG.debug("destroy()");
    }
    
}
