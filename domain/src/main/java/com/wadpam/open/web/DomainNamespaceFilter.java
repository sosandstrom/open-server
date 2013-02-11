/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.open.web;

import com.google.appengine.api.NamespaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A filter responsible for setting a GAE namespace based on the url pattern.
 * @author os
 * @author mattiaslevin
 */
public class DomainNamespaceFilter implements Filter {
    static final Logger LOG = LoggerFactory.getLogger(DomainNamespaceFilter.class);

    private static final String DEFAULT_NAME_SPACE = null;
    protected static final String DEFAULT_NAME_SPACE_PATH = "default";

    private String defaultNameSpace = DEFAULT_NAME_SPACE;
    private String defaultNameSpacePath = DEFAULT_NAME_SPACE_PATH;


    // Thread locals
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<String>();
    private static final ThreadLocal<String> DOMAIN = new ThreadLocal<String>();


    @Override
    public void init(FilterConfig fc) throws ServletException {
        // Do nothing
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final String uri = request.getRequestURI();
        
        CONTEXT.remove();
        DOMAIN.remove();

        // preserve current namespace:
        final String currentNamespace = NamespaceManager.get();
        
        String context = null;
        String domain = null;

        // Default namespace
        // api/default
        Pattern defaultPattern = Pattern.compile("\\A/([^/]+)/" + defaultNameSpacePath);
        Matcher m = defaultPattern.matcher(uri);
        if (m.find()) {
            context = m.group(1);
            domain = defaultNameSpace;
        }

        // Domain name space
        // api/{domain}
        Pattern domainPattern = Pattern.compile("\\A/([^/]+)/([^/]+)");
        m = domainPattern.matcher(uri);
        if (null == context && m.find()) {
            context = m.group(1);
            domain = m.group(2);
        }

        // Set the name space
        try {
            LOG.debug("======= Switching namespace to {}, context is /{}/ ========", domain, context);
                
            if (null != domain) {
                CONTEXT.set(context);
                DOMAIN.set(domain);
                
                NamespaceManager.set(domain);
            }

            chain.doFilter(req, res);

        }
        finally {
            // restore initial namespace:
            NamespaceManager.set(currentNamespace);
            DOMAIN.remove();
        }
    }
    
    @Override
    public void destroy() {
    }

    public static void doInNamespace(String namespace, Runnable runnable) {
        final String current = NamespaceManager.get();
        try {
            NamespaceManager.set(namespace);
            LOG.debug("------ doInNamespace({}) current {} ------", namespace, current);
            runnable.run();
        }
        finally {
            NamespaceManager.set(current);
            LOG.debug("------ doInNamespace({}) restored to {} ------", namespace, current);
        }
    }

    // Setters and getters
    public static final String getContext() {
        return CONTEXT.get();
    }
    
    public static final String getDomain() {
        return DOMAIN.get();
    }

    public String getDefaultNameSpace() {
        return defaultNameSpace;
    }

    public void setDefaultNameSpace(String defaultNameSpace) {
        this.defaultNameSpace = defaultNameSpace;
    }

    public String getDefaultNameSpacePath() {
        return defaultNameSpacePath;
    }

    public void setDefaultNameSpacePath(String defaultNameSpacePath) {
        this.defaultNameSpacePath = defaultNameSpacePath;
    }
}
