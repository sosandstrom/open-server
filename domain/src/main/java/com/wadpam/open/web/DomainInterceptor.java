package com.wadpam.open.web;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.wadpam.open.security.SecurityInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A domain interceptor responsible for performing authentication
 * and configuring the domain tracking code and to email address.
 * Supports Basic Authentication.
 * @author mattiaslevin
 * @author sosandstrom
 */
public abstract class DomainInterceptor extends SecurityInterceptor {

    // Name and attribute injected in the request
    public static final String ATTR_NAME_DOMAIN = "_domain";
    public static final String ATTR_NAME_TRACKING_CODE = "_trackingCode";
    public static final String ATTR_NAME_EMAIL = "_email";
    
    // Paths
    private static final String PATH_AH = "/_ah/";
    private static final Pattern PATH_MONITOR = Pattern.compile("\\A/api/([^/]+)/monitor");

    private static final Pattern PATH_ADMIN = Pattern.compile("\\A/api/(_admin|_worker)");
    private static final Pattern PATH_DOMAIN_ADMIN = Pattern.compile("\\A/api/([^/]+)/(_admin|_worker)");

    private static final String PATH_DOMAIN_DEFAULT = "/api/default/";
    
    private static UserService userService = null;
    
    static {
        try {
            userService = UserServiceFactory.getUserService();
        }
        catch (Throwable t) {
            LOG.warn("Problem getting UserService: {}", t.getMessage());
        }
    }

    /**
     * Override to populate request with GAE UserService Admin User
     */
    @Override
    public String isAuthenticated(HttpServletRequest request, HttpServletResponse response, Object handler, String uri, String method, String authValue) {
        if (null != userService && userService.isUserLoggedIn() && userService.isUserAdmin()) {
            request.setAttribute(ATTR_NAME_CONTAINER_ADMIN_NAME, userService.getCurrentUser().getEmail());
            request.setAttribute(ATTR_NAME_CONTAINER_ADMIN_PRINCIPAL, userService.getCurrentUser());
        }
        return super.isAuthenticated(request, response, handler, uri, method, authValue);
    }
    
    @Override
    protected boolean skipEnvironmentPaths(HttpServletRequest request, HttpServletResponse response, String uri) {
        // Skip auth for admin and worker paths
        // Security is handled in web xml and by GAE
        Matcher m = PATH_ADMIN.matcher(uri);
        if (m.find()) {
            // Do nothing, not domain namespace of security
            return true;
        }

        // Skip auth for monitor request, no security
        m = PATH_MONITOR.matcher(uri);
        if (m.find()) {
            // Do nothing, not domain namespace of security
            return true;
        }

        // Skip auth for domain admin and worker paths
        // Security is handled in web xml and by GAE
        m = PATH_DOMAIN_ADMIN.matcher(uri);
        if (m.find()) {
            return true;
        }
        
        // No authentication for default domain (unless _admin above)
        if (uri.startsWith(PATH_DOMAIN_DEFAULT)) {
            return true;
        }

        return super.skipEnvironmentPaths(request, response, uri);
    }

}
