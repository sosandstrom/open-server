package com.wadpam.open.json;

/**
 *
 * @author os
 */
public class JRestError {
    private int code;
    private String developerMessage;
    private String message;
    private String moreInfo;
    private String stackInfo;
    private int status;

    @Override
    public String toString() {
        return String.format("HTTP %d {code:%d, developerMessage:%s, message:%s, stackInfo:%s}", 
                status, code, developerMessage, message, stackInfo);
    }
    
    public JRestError() {
    }

    public JRestError(int status) {
        this.status = status;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getStackInfo() {
        return stackInfo;
    }

    public void setStackInfo(String stackInfo) {
        this.stackInfo = stackInfo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    
}
