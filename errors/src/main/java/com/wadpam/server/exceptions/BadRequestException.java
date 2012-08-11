package com.wadpam.server.exceptions;

/**
 *
 * @author os
 */
public class BadRequestException extends RestError {

    public BadRequestException(int code, String message) {
        super(code, message);
    }

}
