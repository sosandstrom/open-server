package com.wadpam.open.service;

import com.google.appengine.api.NamespaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * Help class containing utility functions for working with GAE namespace.
 */
public class NamespaceUtilities {
    static final Logger LOG = LoggerFactory.getLogger(NamespaceUtilities.class);


    /**
     * Execute the runnable class in a fixed namespace.
     * @param namespace namespace to run within
     * @param runnable the runnable class
     */
    public static void runInNamespace(String namespace, Runnable runnable) {
        LOG.info("Run in fixed namespace:{}", namespace);

        // Preserve current namespace:
        final String currentNamespace = NamespaceManager.get();
        NamespaceManager.set(namespace);

        try {
            // Run code
           runnable.run();
        }
        finally {
            // Revert namespace
            NamespaceManager.set(currentNamespace);
        }

    }

    /**
     * Execute the callable class in a fixed namespace and return the result.
     * It also allows the callable to throw exceptions.
     * @param namespace name to run within
     * @param callable the callable class
     * @return the return value from the callable
     * @throws Exception
     */
    public static <T> T runInNamespace(String namespace, Callable<T> callable) throws Exception {
        LOG.info("Run in fixed namespace:{}", namespace);

        // Preserve current namespace:
        final String currentNamespace = NamespaceManager.get();
        NamespaceManager.set(namespace);

        T result;
        try {
            // Run code
            result = callable.call();
        }
        finally {
            // Revert namespace
            NamespaceManager.set(currentNamespace);
        }

        return result;
    }

}
