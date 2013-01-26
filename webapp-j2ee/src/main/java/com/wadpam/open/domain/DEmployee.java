package com.wadpam.open.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 *
 * @author os
 */
@Entity
public class DEmployee extends AbstractLongEntity {
    
    @Basic
    private String name;
    
    @ManyToOne
    private DEmployee manager;
    
    @ManyToOne
    private DOrganization organization;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DEmployee getManager() {
        return manager;
    }

    public void setManager(DEmployee manager) {
        this.manager = manager;
    }

    public DOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(DOrganization organization) {
        this.organization = organization;
    }
    
}
