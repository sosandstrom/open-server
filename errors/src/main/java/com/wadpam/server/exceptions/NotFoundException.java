package com.wadpam.server.exceptions;

/**
 * This exception indicates a specific resource not found.
 * @author sosandstrom
 */
public class NotFoundException extends RestException {

    public NotFoundException(int code, String message) {
        super(code, message);
    }

}
