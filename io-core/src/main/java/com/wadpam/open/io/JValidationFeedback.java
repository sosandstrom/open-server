package com.wadpam.open.io;

import java.io.Serializable;

/**
 *
 * @author os
 */
public class JValidationFeedback {
    public static final int CODE_NOT_A_NUMBER = 1;
    public static final int CODE_NOT_A_DATE = 2;
    public static final int CODE_NOT_PERCENTAGE = 3;
    public static final int CODE_NOT_LOCATION = 4;
    public static final int CODE_REQUIRED = 11;
    public static final int CODE_SOFT_REQUIRED = 12;
    
    
    private Serializable id;
    private String columnName;
    private int errorCode;
    private String message;

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
