package com.wadpam.server.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 *
 * @author os
 */
public class RestJsonExceptionResolver extends AbstractHandlerExceptionResolver {
    
    public static final String KEY_ERROR_OBJECT = "error";
    
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
                ModelAndView mav = new ModelAndView();
                mav.setViewName("MappingJacksonJsonView");
                mav.addObject(KEY_ERROR_OBJECT, exception.getMessage() );
                return mav;
    }

//    @Override
//    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, 
//            HttpServletResponse response, 
//            HandlerMethod handlerMethod, 
//            Exception exception) {
//                ModelAndView mav = new ModelAndView();
//                mav.setViewName("MappingJacksonJsonView");
//                mav.addObject("error", exception.getMessage() );
//                return mav;
//    }
    
}
