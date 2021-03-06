package com.wadpam.open.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This exception indicates a conflict.
 * @author sosandstrom
 */
public class ConflictException extends RestException {

    /**
     * Create a conflict exception.
     * @param code unique error code that can be used for localization in the app
     * @param message error message
     */
    public ConflictException(int code, String message) {
        this(code, null,null, message);
    }

    /**
     * Create a conflict exception.
     * @param code unique error code that can be used for localization in the app
     * @param developerMessage developer message
     * @param moreInfo typically a url where more info can be found
     * @param message error message
     */
    public ConflictException(int code, 
            String developerMessage,
            String moreInfo,
            String message) {
        super(code, developerMessage, moreInfo, HttpStatus.CONFLICT, message);
    }

}
