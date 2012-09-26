package com.wadpam.server.exceptions;

/**
 * This exception indicates bad request.
 * @author sosandstrom
 */
public class BadRequestException extends RestException {

    public BadRequestException(int code, String message) {
        super(code, message);
    }

}
