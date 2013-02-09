/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.domain;

import com.wadpam.open.user.domain.DOpenUser;
import javax.persistence.Basic;
import javax.persistence.Entity;

/**
 *
 * @author sosandstrom
 */
public class DProfile extends DOpenUser {

    @Basic
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    
}
