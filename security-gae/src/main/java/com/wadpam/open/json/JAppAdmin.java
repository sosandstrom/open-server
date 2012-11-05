package com.wadpam.open.json;

/**
 * Json object for officers.
 * @author mattiaslevin
 */
public class JAppAdmin extends JBaseObject {

    /** The Google user unique user id */
    private String      adminId;

    /** The Google users email*/
    private String      email;

    /** The users nickname */
    private String      name;

    /**
     * The status of the user account. Allowed values
     * -pending; Pending approval
     * -active; Account approved and active
     * -suspended; Account is suspended and can not be used
     */
    private String      accountStatus;

    /** The maximum number of apps this user can create */
    private Long        maxNumberOfApps;


    @Override
    public String subString() {
        return String.format("{ adminId:%s, email:%s, account status:%s, max number of apps:%d}",
                getAdminId(), getEmail(), getAccountStatus(), getMaxNumberOfApps());
    }


    // Setters and getters
    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
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
