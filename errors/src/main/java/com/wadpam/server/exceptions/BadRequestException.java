package com.wadpam.server.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This exception indicates bad request.
 * @author sosandstrom
 */
public class BadRequestException extends RestException {

    /**
     * Create a bad request exception.
     * @param code unique error code that can be used for localization in the app
     * @param message error message
     */
    public BadRequestException(int code, String message) {
        this(code, null, null, message);
    }


    /**
     * Create a bad request exception.
     * @param code unique error code that can be used for localization in the app
     * @param developerMessage developer message
     * @param moreInfo typically a url where more info can be found
     * @param message error message
     */
    public BadRequestException(int code,
                               String developerMessage,
                               String moreInfo,
                               String message) {
        super(code, developerMessage, moreInfo, HttpStatus.BAD_REQUEST.value(), message);
    }

}
