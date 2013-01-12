package com.wadpam.open.exceptions;

import org.springframework.http.HttpStatus;

/**
 * This exception indicates an internal open errror.
 * @author sosandstrom
 */
public class ServerErrorException extends RestException {

    /**
     * Internal temporary open error exception.
     * @param code unique error code that can be used for localization in the app
     * @param message error message
     */
    public ServerErrorException(int code, String message) {
        this(code, null, null, message);
    }


    /**
     * Internal temporary open error exception.
     * @param code unique error code that can be used for localization in the app
     * @param developerMessage developer message
     * @param moreInfo typically a url where more info can be found
     * @param message error message
     */
    public ServerErrorException(int code,
                                String developerMessage,
                                String moreInfo,
                                String message) {
        super(code, developerMessage, moreInfo, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
