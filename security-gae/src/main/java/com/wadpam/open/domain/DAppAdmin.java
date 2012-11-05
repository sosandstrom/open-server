package com.wadpam.open.domain;

import com.google.appengine.api.datastore.Email;
import net.sf.mardao.core.domain.AbstractCreatedUpdatedEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Stores all application officers/admins.
 * @author mattiaslevin
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"email"})})
public class DAppAdmin extends AbstractCreatedUpdatedEntity implements Serializable {

    private static final long serialVersionUID = -8906171707894817513L;

    /** The Google user unique user id */
    @Id
    private String      adminId;

    /** The Google users email */
    @Basic
    private Email       email;

    /** The users nickname that will be used as display name */
    @Basic
    private String      name;

    /**
     * The status of the user account. Allowed values
     * -pending; Pending approval
     * -active; Account approved and active
     * -suspended; Account is suspended and can not be used
     */
    @Basic
    private String      accountStatus;

    /** The maximum number of apps this user can create */
    @Basic
    private Long        maxNumberOfApps = 5L;


    @Override
    public String toString() {
        return String.format("{adminId:%s, account status:%s, max number of apps:%d}",
                adminId, accountStatus, maxNumberOfApps);
    }


    // Setters and getters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Long getMaxNumberOfApps() {
        return maxNumberOfApps;
    }

    public void setMaxNumberOfApps(Long maxNumberOfApps) {
        this.maxNumberOfApps = maxNumberOfApps;
    }
}
