package com.wadpam.open.web;

import com.wadpam.open.exceptions.BadRequestException;
import com.wadpam.open.exceptions.NotFoundException;
import com.wadpam.open.exceptions.RestException;
import com.wadpam.open.json.JRestError;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Base REST controller implementing common behavior.
 * -Handing common exceptions
 * -Return a json when an exception happens
 * @author sosandstrom
 */
@Controller
public abstract class AbstractRestController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRestController.class);

    public static final ObjectMapper MAPPER = new ObjectMapper();
    
    public AbstractRestController() {
        MAPPER.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }


    // Handle the exception and also print a full stack trace
    protected void doHandleWithFullStackTrace(HttpServletResponse response, Exception e, HttpStatus status) {

        // Make sure we print out the stack track to the console and log
        LOG.error(String.format("Handling error for HTTP %s %s", status, e.getLocalizedMessage()), e);

        doHandle(response, e, status);
    }

    // Return a json structure explaining the error
    protected void doHandle(HttpServletResponse response, Exception e, HttpStatus status) {

        final JRestError body = new JRestError(status.value());
        
        // for all
        body.setDeveloperMessage(e.getLocalizedMessage());
        body.setMessage(e.getLocalizedMessage());

        StackTraceElement topFrame = e.getStackTrace()[0];
        StringBuilder moreInfo = new StringBuilder();
        moreInfo.append("exception:").append(e.getClass().getName())
                .append(" class:").append(topFrame.getClassName())
                .append(" method:").append(topFrame.getMethodName())
                .append(" line:").append(topFrame.getLineNumber());
        body.setMoreInfo(moreInfo.toString());
        
        final Class clazz = e.getClass();
        if (RestException.class.isAssignableFrom(clazz)) {
            
            final RestException restError = (RestException) e;
            body.setCode(restError.getCode());
            if (null != restError.getDeveloperMessage()) {
                body.setDeveloperMessage(restError.getDeveloperMessage());
            }
        }
        try {
            response.setStatus(status.value());
            response.setContentType("application/json");
            final PrintWriter writer = response.getWriter();
            final String json = MAPPER.writeValueAsString(body);
            writer.print(json);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            LOG.error("Unexpected", ex);
        }
    }


    // Dedicated exception handlers

    @ExceptionHandler(RestException.class)
    @ResponseBody
    public void handleRestRequest(HttpServletResponse response, RestException e) {
        doHandle(response, e, e.getStatus());
    }


    // All other exceptions
    // The exception must be a subclass of Exception or Spring will not handle it here
    
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public void handleDefault(HttpServletResponse response, Exception t) {
        doHandleWithFullStackTrace(response, t, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
