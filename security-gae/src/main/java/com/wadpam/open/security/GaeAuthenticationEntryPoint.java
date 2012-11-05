package com.wadpam.open.security;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class will redirect to the Google user login for backoffice login.
 * @author mattiaslevin
 */
public class GaeAuthenticationEntryPoint implements AuthenticationEntryPoint {

    static final Logger LOG = LoggerFactory.getLogger(GaeAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        LOG.debug("Admin trying the access backoffice without being logged in, redirect to Google login");

        UserService userService = UserServiceFactory.getUserService();

        // Ask Google to redirect back to the same url
        String loginUrl = userService.createLoginURL(httpServletRequest.getRequestURI());

        response.sendRedirect(loginUrl);
    }
}
