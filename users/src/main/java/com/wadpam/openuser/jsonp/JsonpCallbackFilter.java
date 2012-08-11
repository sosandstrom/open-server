package com.wadpam.openuser.jsonp;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonpCallbackFilter implements Filter {
    public static final String  CALLBACK_METHOD_NAME = "callbackMethodName";

    private static final Logger LOG                  = LoggerFactory.getLogger(JsonpCallbackFilter.class);

    private String              callbackParam        = "callback";

    public void init(FilterConfig fConfig) throws ServletException {
        final String callbackMethodName = fConfig.getInitParameter(CALLBACK_METHOD_NAME);
        if (null != callbackMethodName) {
            callbackParam = callbackMethodName;
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        final String callback = httpRequest.getParameter(callbackParam);
        if (null != callback && callback.length() > 0) {
            LOG.debug("Wrapping response with JSONP callback '" + callback + "'");

            GenericResponseWrapper wrapper = new GenericResponseWrapper(httpResponse);

            chain.doFilter(request, wrapper);
            httpResponse.setContentType("text/javascript;charset=UTF-8");
            final OutputStream out = httpResponse.getOutputStream();
            out.write(callback.getBytes());
            out.write('(');
            final byte data[] = wrapper.getData();
            LOG.debug("Wrapped data size is " + data.length);
            out.write(data);
            out.write(')');
            out.write(';');

            out.close();
        }
        else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
    }
}
