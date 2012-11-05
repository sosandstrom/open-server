package com.wadpam.open.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluate if an principle is allowed to access a domain object.
 * @author mattiaslevin
 */
public class DomainAccessPermissionEvaluator implements PermissionEvaluator {

    static final Logger LOG = LoggerFactory.getLogger(DomainAccessPermissionEvaluator.class);

    private Map<String, Permission> permissionNameToPermission = new HashMap<String, Permission>();

    // Constructor
    public DomainAccessPermissionEvaluator(Map<String, Permission> permissionNameToPermissionMap) {
        this.permissionNameToPermission = permissionNameToPermissionMap;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        boolean hasPermission = false;
        if (null != targetDomainObject && null != authentication && permission instanceof String) {
            hasPermission = checkPermission(authentication, targetDomainObject, (String) permission);
        }
        return hasPermission;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new AuthorizationServiceException("Id and Class permissions are not supported by");
    }

    // Find the correct permission implementation to run
    private boolean checkPermission(Authentication authentication, Object targetDomainObject, String permissionKey) {
        Permission permission = permissionNameToPermission.get(permissionKey);

        // Check a permission implementation exists
        if (null == permission) {
            LOG.error("Checking permission of a type that is not supported:{}", permissionKey);
            throw new AuthorizationServiceException(String.format("Checking permission of a type that is not supported:%s", permissionKey));
        }

        return permission.isAllowed(authentication, targetDomainObject);
    }

}
