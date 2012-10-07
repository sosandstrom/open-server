package com.wadpam.open.integrationtest;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * A error handler for Spring RestTemplate that ignores all http error codes.
 * Useful during integration tests.
 * @author mattiaslevin
 */
public class DoNothingResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    protected boolean hasError(HttpStatus statusCode) {
        return false;
    }
}
