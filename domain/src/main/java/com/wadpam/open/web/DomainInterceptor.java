package com.wadpam.open.web;

import com.wadpam.open.domain.DAppDomain;
import com.wadpam.open.exceptions.AuthenticationFailedException;
import com.wadpam.open.security.SecurityDetailsService;
import com.wadpam.open.security.SecurityInterceptor;
import com.wadpam.open.service.DomainService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A domain interceptor responsible for performing authentication
 * and configuring the domain tracking code and to email address.
 * Supports Basic Authentication.
 * @author mattiaslevin
 * @author sosandstrom
 */
public abstract class DomainInterceptor extends SecurityInterceptor 
        implements SecurityDetailsService {

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
