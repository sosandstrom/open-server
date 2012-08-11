package com.wadpam.server.exceptions;

/**
 * Base class for all REST exceptions
 * @author sosandstrom
 */
public class RestError extends Error {
    private int code;
    private String developerMessage;
    private int status;

    public RestError(int code, String message) {
        super(message);
        this.code = code;
    }

    
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
}
