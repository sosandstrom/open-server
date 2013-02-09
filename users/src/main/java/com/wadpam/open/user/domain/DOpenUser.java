/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.user.domain;

import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import net.sf.mardao.core.domain.AbstractLongEntity;

/**
 *
 * @author sosandstrom
 */
@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"username"}),
    @UniqueConstraint(columnNames={"email"})})
public class DOpenUser extends AbstractLongEntity {

    @Basic
    private String displayName;
    
    @Basic
    private String email;
    
    @Basic
    private String profileLink;
    
    @Basic
    private Collection<String> roles;

    @Basic
    private String thumbnailUrl;
    
    @Basic
    private String username;
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    
}
