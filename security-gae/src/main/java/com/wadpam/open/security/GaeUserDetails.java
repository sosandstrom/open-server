package com.wadpam.open.security;

import com.google.appengine.api.users.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * A UserDetails object that also hold a GAE user.
 * This will allow downstream code (service) to using Spring security only to access user details.
 */
public class GaeUserDetails extends org.springframework.security.core.userdetails.User {

    private User user;


    public GaeUserDetails(User gaeUser, Collection<? extends GrantedAuthority> authorities) {
        this(gaeUser.getUserId(), "canBeAnything", authorities);
        user = gaeUser;
    }

    public GaeUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }


    // Setter and Getter
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

}
