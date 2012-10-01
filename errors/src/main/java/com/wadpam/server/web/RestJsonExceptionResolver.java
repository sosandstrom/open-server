package com.wadpam.server.web;

import com.wadpam.server.exceptions.RestException;
import com.wadpam.server.json.JRestError;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 *
 * @author os
 */
public class RestJsonExceptionResolver extends AbstractHandlerExceptionResolver 
    implements View {
    
    public static final String KEY_ERROR_OBJECT = "error";
    
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
                ModelAndView mav = new ModelAndView(this);
//                mav.setViewName("MappingJacksonJsonView");
                
                final JRestError error = new JRestError();
                if (null != exception) {
                    error.setDeveloperMessage(exception.getLocalizedMessage());
                    error.setMessage(exception.getLocalizedMessage());
                    
                    if (RestException.class.isAssignableFrom(exception.getClass())) {
                        final RestException re = (RestException) exception;
                        error.setCode(re.getCode());
                        if (null != re.getDeveloperMessage()) {
                            error.setDeveloperMessage(re.getDeveloperMessage());
                        }
                        error.setStatus(re.getStatus());
                    }
                    else {
                        error.setStatus(500);
                    }
                    
                    StackTraceElement topFrame = exception.getStackTrace()[0];
                    StringBuilder moreInfo = new StringBuilder();
                    moreInfo.append("exception:").append(exception.getClass().getName())
                            .append(" class:").append(topFrame.getClassName())
                            .append(" method:").append(topFrame.getMethodName())
                            .append(" line:").append(topFrame.getLineNumber());
                    error.setMoreInfo(moreInfo.toString());
                    
                }
                
                mav.addObject(KEY_ERROR_OBJECT, error);
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
