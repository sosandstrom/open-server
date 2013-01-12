package com.wadpam.open.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This exception indicates an authentication failure.
 * @author sosandstrom
 */
public class AuthenticationFailedException extends RestException {

    /**
     * Create a authentication failed exception.
     * @param code unique error code that can be used for localization in the app
     * @param message error message
     */
    public AuthenticationFailedException(int code, String message) {
        this(code, null,null, message);
    }

    /**
     * Create a authentication failed exception.
     * @param code unique error code that can be used for localization in the app
     * @param developerMessage developer message
     * @param moreInfo typically a url where more info can be found
     * @param message error message
     */
    public AuthenticationFailedException(int code,
                                         String developerMessage,
                                         String moreInfo,
                                         String message) {
        super(code, developerMessage, moreInfo, HttpStatus.UNAUTHORIZED, message);
    }

}
