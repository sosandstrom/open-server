/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 *
 * @author sosandstrom
 */
@Entity
public class DProfile extends AbstractLongEntity {

    @Basic
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    
}
