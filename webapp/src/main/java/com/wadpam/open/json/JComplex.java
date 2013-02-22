package com.wadpam.open.json;

/**
 *
 * @author os
 */
public class JComplex extends JBaseObject {
    private String name;
    private Long organizationKey;
    private Long manager;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationKey() {
        return organizationKey;
    }

    public void setOrganizationKey(Long organizationKey) {
        this.organizationKey = organizationKey;
    }

    public Long getManager() {
        return manager;
    }

    public void setManager(Long manager) {
        this.manager = manager;
    }

}
