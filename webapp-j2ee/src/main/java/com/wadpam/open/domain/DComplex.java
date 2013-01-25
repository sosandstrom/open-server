package com.wadpam.open.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import net.sf.mardao.core.Parent;
import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 *
 * @author os
 */
@Entity
public class DComplex extends AbstractLongEntity {
    
    @Basic
    private String name;
    
    @Parent(kind="DSample")
    private Object organizationKey;
    
    @ManyToOne
    private DComplex manager;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getOrganizationKey() {
        return organizationKey;
    }

    public void setOrganizationKey(Object organizationKey) {
        this.organizationKey = organizationKey;
    }

    public DComplex getManager() {
        return manager;
    }

    public void setManager(DComplex manager) {
        this.manager = manager;
    }
}
