package com.wadpam.open.transaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.util.ConcurrentModificationException;

/**
 * Class for implementing a finite number of retries when a GAE datastore transaction fail.
 * @author mattiaslevin
 */
public class GaeConcurrentOperationExecutor implements Ordered {

    static final Logger LOG = LoggerFactory.getLogger(GaeConcurrentOperationExecutor.class);

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_WAIT = 100;

    private int maxRetries = DEFAULT_MAX_RETRIES;
    private int order = 1;

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Object doConcurrentOperation(ProceedingJoinPoint pjp) throws Throwable {
        int numberOfAttempts = 0;
        ConcurrentModificationException concurrentModificationException = null;
        do {
            numberOfAttempts++;
            try {
                return pjp.proceed();
            }
            catch(ConcurrentModificationException exception) {
                LOG.info("Datastore transaction failed due to Concurrent Modification Exception. Attempt: " + numberOfAttempts);
                concurrentModificationException = exception;
            }

            // Sleep for a short time
            if (numberOfAttempts <= this.maxRetries)
                Thread.sleep(DEFAULT_WAIT * numberOfAttempts);

        }
        while(numberOfAttempts <= this.maxRetries);

        LOG.warn("Datastore transaction failed max number of tries due to Concurrent Modification Exception, not more retries.");
        throw concurrentModificationException;
    }
}
