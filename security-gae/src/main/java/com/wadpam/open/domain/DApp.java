package com.wadpam.open.domain;

import com.google.appengine.api.datastore.Email;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Collection;

/**
 * This class contain domain/app configuration data related to API authentication
 * @author mattiaslevin
 */
@Entity
public class DApp extends AbstractCreatedUpdatedEntity implements Serializable {

    private static final long serialVersionUID = -850567951576298916L;


    /** The unique domain name for the app */
    @Id
    private String      domainName; // Can not use the name domain, reserved name in String class?

    /** The email address of the add admins */
    @Basic
    private Collection<Email> appAdmins;

    /** An unique api user generated when the app is created. Most be provided in the request from the apps */
    @Basic
    private String      apiUser;

    /** A generated api password. Must be provided in the requests from the apps */
    @Basic
    private String      apiPassword;

    /** A short description of the app */
    @Basic
    private String      description;


    @Override
    public String toString() {
        return String.format("{domain:%s, appAdmins:%s apiUser:%s, apiPassword:%s}",
                domainName, appAdmins, apiUser, apiPassword);
    }


    // Getters and setters
    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Collection<Email> getAppAdmins() {
        return appAdmins;
    }

    public void setAppAdmins(Collection<Email> appAdmins) {
        this.appAdmins = appAdmins;
    }

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
