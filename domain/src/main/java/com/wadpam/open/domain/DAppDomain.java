package com.wadpam.open.domain;

import com.google.appengine.api.datastore.Email;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import net.sf.mardao.core.domain.AbstractStringEntity;

/**
 * Domain object containing domain/app properties.
 * @author mattiaslevin
 */
@Entity
public class DAppDomain extends AbstractStringEntity {

    /** Short description of the domain */
    @Basic
    private String description;

    /** The user name used in basic authentication */
    @Basic
    private String username;

    /** The password used in basic authentication */
    @Basic
    private String password;

    /** Analytics tracking code, e.g. Google Analytics */
    @Basic
    private String analyticsTrackingCode;

    /** Contact email that can be used for email send from GAE */
    @Basic
    private Email email;

    /** Context specific app property */
    @Basic
    private String appArg1;

    /** Context specific app property */
    @Basic
    private String appArg2;


    @Override
    public String toString() {
        return String.format("Domain:%s, description:%s, tracking code:%s",
                getId(), description, analyticsTrackingCode);
    }


    // Setters and getters
    public String getAnalyticsTrackingCode() {
        return analyticsTrackingCode;
    }

    public void setAnalyticsTrackingCode(String analyticsTrackingCode) {
        this.analyticsTrackingCode = analyticsTrackingCode;
    }

    public String getAppArg1() {
        return appArg1;
    }

    public void setAppArg1(String appArg1) {
        this.appArg1 = appArg1;
    }

    public String getAppArg2() {
        return appArg2;
    }

    public void setAppArg2(String appArg2) {
        this.appArg2 = appArg2;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
