package com.wadpam.server.web;

import com.wadpam.server.exceptions.BadRequestException;
import com.wadpam.server.exceptions.NotFoundException;
import com.wadpam.server.exceptions.RestError;
import com.wadpam.server.json.JRestError;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author os
 */
@Controller
public abstract class AbstractRestController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestController.class);
    public static final ObjectMapper MAPPER = new ObjectMapper();
    
    protected void doHandle(HttpServletResponse response, Throwable e, HttpStatus status) {
        LOG.warn("Handling error for {} {}", status, e.getLocalizedMessage());
        LOG.warn("Stack track {}", e.getStackTrace());
        final JRestError body = new JRestError(status.value());
        
        // for all
        body.setDeveloperMessage(e.getLocalizedMessage());
        
        final Class clazz = e.getClass();
        if (RestError.class.isAssignableFrom(clazz)) {
            
            final RestError restError = (RestError) e;
            body.setCode(restError.getCode());
        }
        try {
            response.setContentType("application/json");
            final PrintWriter writer = response.getWriter();
            final String json = MAPPER.writeValueAsString(body);
            writer.print(json);
            writer.flush();
            writer.close();
            response.setStatus(status.value());
        } catch (IOException ex) {
            LOG.error("Unexpected", ex);
        }
    }
    
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public void handleBadRequest(HttpServletResponse response, BadRequestException e) {
        
        doHandle(response, e, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public void handleNotFound(HttpServletResponse response, NotFoundException e) {
        
        doHandle(response, e, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(RestError.class)
    @ResponseBody
    public void handleRestError(HttpServletResponse response, RestError e) {
        
        doHandle(response, e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public void handleDefault(HttpServletResponse response, Throwable t) {
        
        doHandle(response, t, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
