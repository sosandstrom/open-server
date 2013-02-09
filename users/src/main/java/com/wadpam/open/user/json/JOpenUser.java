/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.user.json;

import com.wadpam.open.json.JBaseObject;

/**
 *
 * @author sosandstrom
 */
public class JOpenUser extends JBaseObject {

    private String displayName;
    
    private String email;
    
    private String username;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
