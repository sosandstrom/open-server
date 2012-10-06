package com.wadpam.open.transaction;

import com.google.appengine.api.NamespaceManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

/**
 * Class for implementing setting a fixed GAE name space.
 * Can be useful when making datastore queries accross domais.
 * @author mattiaslevin
 */
public class GaeFixedNamespaceExecutor implements Ordered {

    static final Logger LOG = LoggerFactory.getLogger(GaeFixedNamespaceExecutor.class);

    static final String DEFAULT_FIXED_NAMESPACE = "defaultFixedNamespace";

    private int order = 30;

    private String namespace = DEFAULT_FIXED_NAMESPACE;


    // Run a method in a fixed namespace
    public Object doFixedNamespace(ProceedingJoinPoint pjp, FixedNamespace annotatedNamespace) throws Throwable {

        String newNamespace = annotatedNamespace.value();
        if (null == annotatedNamespace || annotatedNamespace.value().isEmpty())
            newNamespace = this.namespace;

        LOG.debug("Use a fixed namespace:{}", newNamespace);

        // Preserve current namespace:
        final String currentNamespace = NamespaceManager.get();
        NamespaceManager.set(newNamespace);

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
