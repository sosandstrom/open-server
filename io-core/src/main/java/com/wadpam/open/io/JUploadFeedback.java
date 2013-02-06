package com.wadpam.open.io;

import java.util.Collection;

/**
 *
 * @author os
 */
public class JUploadFeedback {
    private String blobKey;
    
    private boolean valid;
    
    private Collection<JValidationFeedback> errors;
    
    private Exception exception;

    public String getBlobKey() {
        return blobKey;
    }

    public void setBlobKey(String blobKey) {
        this.blobKey = blobKey;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Collection<JValidationFeedback> getErrors() {
        return errors;
    }

    public void setErrors(Collection<JValidationFeedback> errors) {
        this.errors = errors;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
    
    
}
