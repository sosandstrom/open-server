package com.wadpam.open.json;

import java.util.Collection;

/**
 * Json object for app settings.
 * @author mattiaslevin
 */
public class JApp extends JBaseObject {

    /** The unique domain name for the app */
    private String      domain;

    /** The email addressed ofr the admins for this app */
    private Collection<String> appAdmins;

    /** An unique app user generated when the app is created. Most be provided in the request from the apps */
    private String      apiUser;

    /** A generated app password. Must be provided in the requests from the apps */
    private String      apiPassword;

    /** A short description of the app */
    private String      description;


    @Override
    public String subString() {
        return String.format("{domain:%s, apiUser:%s, apiPassword:%s}",
                getDomain(), getApiUser(), getApiPassword());
    }

    // Setters and Getters
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Collection<String> getAppAdmins() {
        return appAdmins;
    }

    public void setAppAdmins(Collection<String> appAdmins) {
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
