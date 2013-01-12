package com.wadpam.open.service;

import com.google.appengine.api.NamespaceManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

/**
 * Class for implementing setting a fixed GAE name space.
 * Can be useful when making datastore queries across domains.
 * @author mattiaslevin
 */
public class GaeFixedNamespaceExecutor implements Ordered {
    static final Logger LOG = LoggerFactory.getLogger(GaeFixedNamespaceExecutor.class);

    static final String DEFAULT_FIXED_NAMESPACE = "defaultNamespace";

    private int order = 30;
    private String namespace = DEFAULT_FIXED_NAMESPACE;


    // Run a method in a fixed namespace
    public Object doFixedNamespaceOperation(ProceedingJoinPoint pjp) throws Throwable {
        LOG.debug("Use a fixed namespace:{}", namespace);

        // Preserve current namespace:
        final String currentNamespace = NamespaceManager.get();
        NamespaceManager.set(namespace);

        Object result;
        try {
            result = pjp.proceed();
        }
        finally {
            // Revert namespace
            NamespaceManager.set(currentNamespace);
        }
        return result;

    }


    // Getters and setters
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
