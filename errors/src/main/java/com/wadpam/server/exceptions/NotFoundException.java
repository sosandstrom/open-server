package com.wadpam.server.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This exception indicates a specific resource not found.
 * @author sosandstrom
 */
public class NotFoundException extends RestException {

    public NotFoundException(int code, String message) {
        super(code, HttpStatus.NOT_FOUND.value(), message);
    }

}
