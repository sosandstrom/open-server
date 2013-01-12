package com.wadpam.open.web;

import com.wadpam.open.exceptions.RestException;
import com.wadpam.open.json.JRestError;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 *
 * @author os
 */
public class RestJsonExceptionResolver extends AbstractHandlerExceptionResolver 
    implements View {
    static final Logger LOG = LoggerFactory.getLogger(RestJsonExceptionResolver.class);
    
    public static final String KEY_ERROR_OBJECT = "error";
    
    public RestJsonExceptionResolver() {
        AbstractRestController.MAPPER.getSerializationConfig().setSerializationInclusion(
                JsonSerialize.Inclusion.NON_NULL);
    }
    
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        // ignore Spring Security AuthenticationExceptions:
        try {
            Class clazz = getClass().getClassLoader().loadClass("org.springframework.security.core.AuthenticationException");
            if (clazz.isAssignableFrom(exception.getClass())) {
                return null;
            }
        }
        catch (ClassNotFoundException processIfNotOnClasspath) {
        }
        
        ModelAndView mav = new ModelAndView(this);
//                mav.setViewName("MappingJacksonJsonView");

        final JRestError error = new JRestError();
        if (null != exception) {
            error.setMessage(exception.getLocalizedMessage());

            StackTraceElement topFrame = exception.getStackTrace()[0];
            StringBuilder stackTraceMessage = new StringBuilder();
            stackTraceMessage.append("exception:").append(exception.getClass().getName())
                    .append(" class:").append(topFrame.getClassName())
                    .append(" method:").append(topFrame.getMethodName())
                    .append(" line:").append(topFrame.getLineNumber());
            error.setStackInfo(stackTraceMessage.toString());

            if (RestException.class.isAssignableFrom(exception.getClass())) {
                final RestException re = (RestException) exception;
                error.setCode(re.getCode());

                if (null != re.getDeveloperMessage()) {
                    error.setDeveloperMessage(re.getDeveloperMessage());
                }
                error.setStatus(re.getStatus().value());
                error.setMoreInfo(re.getMoreInfo());
            }
            else {
                error.setStatus(500);
            }

        }

        mav.addObject(KEY_ERROR_OBJECT, error);
        LOG.debug("Resolved exception is", exception);
        LOG.debug("Returning error object {}", error);
        return mav;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JRestError e = (JRestError) model.get(KEY_ERROR_OBJECT);

        response.setContentType(getContentType());
        response.setStatus(e.getStatus());
        
        final PrintWriter writer = response.getWriter();
        AbstractRestController.MAPPER.writeValue(writer, e);
        writer.close();
    }
    
}
