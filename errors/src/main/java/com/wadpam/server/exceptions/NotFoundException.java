package com.wadpam.server.exceptions;

/**
 *
 * @author os
 */
public class NotFoundException extends RestError {

    public NotFoundException(int code, String message) {
        super(code, message);
    }

}
