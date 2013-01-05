package com.wadpam.open.json;

/**
 *
 * @author os
 */
public class JComplex extends JBaseObject {
    private String name;
    private Long organizationId;
    private Long managerId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
    
    
}
