package com.wadpam.open.security;

import org.springframework.security.core.Authentication;

/**
 * Interface implemented by various domain access permission evaluators.
 * @author mattiaslevin
 */
public interface Permission {

    /**
     * Check if the principle has permission to access the domain object.
     * @param authentication Spring authentication object
     * @param targetDomainObject domain object being accessed
     * @return true if the principle is allowed to access the domain object
     */
    boolean isAllowed(Authentication authentication, Object targetDomainObject);

}
