package com.wadpam.server.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This exception indicates a specific resource not found.
 * @author sosandstrom
 */
public class NotFoundException extends RestException {

    public NotFoundException(int code, 
            String developerMessage,
            String moreInfo,
            String message) {
        super(code, developerMessage, moreInfo, HttpStatus.NOT_FOUND.value(), message);
    }

}
