/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.json;

import com.wadpam.open.user.json.JOpenUser;

/**
 *
 * @author sosandstrom
 */
public class JProfile extends JOpenUser {

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
}
