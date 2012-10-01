package com.wadpam.server.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This exception indicates bad request.
 * @author sosandstrom
 */
public class BadRequestException extends RestException {

    public BadRequestException(int code, String message) {
        super(code, HttpStatus.BAD_REQUEST.value(), message);
    }

}
