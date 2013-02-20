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
    public static final String DEFAULT_NAME_SPACE_PATH = "default";

    public static final Pattern domainPattern = Pattern.compile("\\A/([^/]+)/([^/]+)");
    public static Pattern defaultPattern = Pattern.compile("\\A/([^/]+)/" + DEFAULT_NAME_SPACE_PATH);

    private static String defaultNameSpace = DEFAULT_NAME_SPACE;
    private static String defaultNameSpacePath = DEFAULT_NAME_SPACE_PATH;

    // Thread locals
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<String>();
    private static final ThreadLocal<String> DOMAIN = new ThreadLocal<String>();


    @Override
    public void init(FilterConfig fc) throws ServletException {
        // Do nothing
    }


    @Override
    public void doFilter(ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        CONTEXT.remove();
        DOMAIN.remove();
        final HttpServletRequest request = (HttpServletRequest) req;

        try {
            processRequestInNamespace(request, new Runnable() {
                @Override
                public void run() {
                    try {
                        chain.doFilter(request, res);
                    } catch (IOException ex) {
                        throw new RuntimeException("processing filter chain", ex);
                    } catch (ServletException ex) {
                        throw new RuntimeException("processing filter chain", ex);
                    }
                }
            });
        }
        finally {
            DOMAIN.remove();
        }
      }
    
    @Override
    public void destroy() {
    }

    public static void doInNamespace(String namespace, Runnable runnable) throws IOException, ServletException {
        final String current = NamespaceManager.get();
        try {
            NamespaceManager.set(namespace);
            DOMAIN.set(namespace);
//            LOG.debug("------ doInNamespace({}) current {} ------", namespace, current);
            runnable.run();
        }
        catch (RuntimeException re) {
            
            // unwrap runtime exception from runnable.run()
            if (null != re.getCause()) {
                if (IOException.class.isAssignableFrom(re.getCause().getClass())) {
                    throw (IOException) re.getCause();
                }
                if (ServletException.class.isAssignableFrom(re.getCause().getClass())) {
                    throw (ServletException) re.getCause();
                }
            }
        }
        finally {
            DOMAIN.set(current);
            NamespaceManager.set(current);
//            LOG.debug("------ doInNamespace({}) restored to {} ------", namespace, current);
        }
    }
    
    public static String getDomainNamespace(final String uri) {
        String context = null;
        String domain = null;

        // Default namespace
        // api/default
        Matcher m = defaultPattern.matcher(uri);
        if (m.find()) {
            context = m.group(1);
            CONTEXT.set(context);
            domain = defaultNameSpace;
        }

        // Domain name space
        // api/{domain}
        m = domainPattern.matcher(uri);
        if (null == context && m.find()) {
            context = m.group(1);
            CONTEXT.set(context);
            domain = m.group(2);
        }
        
        return domain;
    }
    
    public static void processRequestInNamespace(HttpServletRequest request, Runnable runnable) throws IOException, ServletException {
        final String uri = request.getRequestURI();
        final String domainNamespace = getDomainNamespace(uri);
        LOG.debug("======= Switching namespace to {}, {} {} ========", 
                new Object[] {domainNamespace, request.getMethod(), uri});
        
        doInNamespace(domainNamespace, runnable);
    }

    // ----- Setters and getters ----------
    
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
        defaultPattern = Pattern.compile("\\A/([^/]+)/" + defaultNameSpacePath);
    }
}
