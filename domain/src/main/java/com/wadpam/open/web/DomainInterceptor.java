package com.wadpam.open.web;

import com.wadpam.open.exceptions.AuthenticationFailedException;
import com.wadpam.open.domain.DAppDomain;
import com.wadpam.open.service.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A domain interceptor responsible for performing authentication
 * and configuring the domain tracking code and to email address.
 * @author mattiaslevin
 */
public abstract class DomainInterceptor extends HandlerInterceptorAdapter {
    protected static final Logger LOG = LoggerFactory.getLogger(DomainInterceptor.class);

    protected static final int ERR_DOMAIN_NOT_FOUND = 10000;
    protected static final int ERR_AUTHENTICATION_FAILED = 10001;

    // Name and attribute injected in the request
    protected static final String ATTR_NAME_DOMAIN = "_domain";
    protected static final String ATTR_NAME_TRACKING_CODE = "_trackingCode";
    protected static final String ATTR_NAME_EMAIL = "_email";
    /** must be same as MardaoPrincipalInterceptor value */
    protected static final String ATTR_NAME_USERNAME = "_username";

    public static final String NAME_AUTHORIZATION = "Authorization";
    
    public static final String USERNAME_ANONYMOUS = "[ANONYMOUS]";
    
    // Paths
    private static final String PATH_AH = "/_ah/";
    private static final Pattern PATH_MONITOR = Pattern.compile("\\A/api/([^/]+)/monitor");

    private static final Pattern PATH_ADMIN = Pattern.compile("\\A/api/(_admin|_worker)");
    private static final Pattern PATH_DOMAIN_ADMIN = Pattern.compile("\\A/api/([^/]+)/(_admin|_worker)");

    private static final Pattern PATH_DOMAIN = Pattern.compile("\\A/api/([^/]+)");
    
    private final ArrayList<Entry<Pattern, Set<String>>> WHITELISTED_METHODS = 
            new ArrayList<Entry<Pattern, Set<String>>>();

    @Autowired
    private DomainService domainService;

    protected abstract String doAuthenticate(HttpServletRequest request, HttpServletResponse response);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        final String uri = request.getRequestURI();
        LOG.debug("---- Authenticate using {} for URI {} ----", getClass().getSimpleName(), uri);

        // Skip GAE admin paths
        // Security is handled in web xml and by GAE
        if (uri.startsWith(PATH_AH)) {
            return true;
        }

        String domain = null;
        DAppDomain dAppDomain = null;

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
            // Get the domain from the path, mandatory
            domain = m.group(1);
            if (!DomainNamespaceFilter.DEFAULT_NAME_SPACE_PATH.equals(domain)) {
                
                // appDomain set in previous Interceptor?
                dAppDomain = (DAppDomain) request.getAttribute(ATTR_NAME_DOMAIN);
                if (null == dAppDomain) {
                    dAppDomain = domainService.get(null, domain);
                    request.setAttribute(ATTR_NAME_DOMAIN, dAppDomain);
                }
                
                if (null != dAppDomain) {
                    // Set the tracking code and email, do not like to have the aAppDomain dependency
                    // down in the controller
                    request.setAttribute(ATTR_NAME_TRACKING_CODE, dAppDomain.getAnalyticsTrackingCode());
                    if (null != dAppDomain.getEmail()) {
                        request.setAttribute(ATTR_NAME_EMAIL, dAppDomain.getEmail().getEmail());
                    }
                }
            }
            return true;
        }

        // Authenticate all access to domains
        // Use basic authentication
        m = PATH_DOMAIN.matcher(uri);
        if (m.find()) {
            // Get the domain from the path, mandatory
            domain = m.group(1);
                
            // appDomain set in previous Interceptor?
            dAppDomain = (DAppDomain) request.getAttribute(ATTR_NAME_DOMAIN);
            if (null == dAppDomain) {
                dAppDomain = domainService.get(null, domain);
                request.setAttribute(ATTR_NAME_DOMAIN, dAppDomain);
            }
            
            if (null == dAppDomain) {
                LOG.info("Domain:{} does not exist", domain);
                throw new AuthenticationFailedException(ERR_DOMAIN_NOT_FOUND,
                        String.format("Authentication failed,, domain:%s does not exist", domain));
            }
            request.setAttribute(ATTR_NAME_DOMAIN, dAppDomain);
            // Set the tracking code and email, do not like to have the aAppDomain dependency
            // down in the controller
            request.setAttribute(ATTR_NAME_TRACKING_CODE, dAppDomain.getAnalyticsTrackingCode());
            if (null != dAppDomain.getEmail()) {
                request.setAttribute(ATTR_NAME_EMAIL, dAppDomain.getEmail().getEmail());
            }

            // is this request white-listed?
            boolean whitelisted = isWhitelistedMethod(uri, request.getMethod());
            if (whitelisted) {
                return true;
            }

            // Authenticate:
            String username = doAuthenticate(request, response);
            
            // if not validated, leave as null
            if (!USERNAME_ANONYMOUS.equals(username)) {
                request.setAttribute(ATTR_NAME_USERNAME, username);
            }
            
            return (null != username);
        }

        return false;
    }

    // Setters and getters
    public void setDomainService(DomainService domainService) {
        this.domainService = domainService;
    }

    protected boolean isWhitelistedMethod(String requestURI, String method) {
        Matcher matcher;
        for (Entry<Pattern, Set<String>> entry : WHITELISTED_METHODS) {
            matcher = entry.getKey().matcher(requestURI);
            if (matcher.find()) {
                boolean returnValue = entry.getValue().contains(method);
                LOG.debug("{} whitelisted URI {} {}", new Object[] {
                    returnValue, method, entry.getKey()});
                return returnValue;
            }
        }
        return false;
    }
    
    public void setWhitelistedMethods(Collection<Entry<String, Collection<String>>> whitelistedMethods) {
        WHITELISTED_METHODS.clear();
        SimpleImmutableEntry<Pattern, Set<String>> sie;
        for (Entry<String, Collection<String>> entry : whitelistedMethods) {
            sie = new SimpleImmutableEntry<Pattern, Set<String>>(
                    Pattern.compile(entry.getKey()), new TreeSet<String>(entry.getValue()));
            WHITELISTED_METHODS.add(sie);
        }
    }
}
