package com.wadpam.open.transaction;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;


/**
 * An Spring transaction manager implementing the transaction strategy for GAE.
 * @author mattiaslevin
 */
public class GaeTransactionManager extends AbstractPlatformTransactionManager {
    static final Logger LOG = LoggerFactory.getLogger(GaeTransactionManager.class);


    @Override
    protected Object doGetTransaction() throws TransactionException {
        LOG.debug("Get transaction");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction txn = datastore.beginTransaction(options);

        return txn;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition transactionDefinition) throws TransactionException {
        LOG.debug("Begin transaction");

        Transaction txn = (Transaction)transaction;
        if (null == txn)
            throw new NoTransactionException("Transaction object not available when about to start transaction");
        else if (txn.isActive() == false)
            throw new IllegalTransactionStateException("Transaction not active when about to start the transaction");

        // Everything looks ok, do nothing
    }

    @Override
    protected void doCommit(DefaultTransactionStatus defaultTransactionStatus) throws TransactionException {
        LOG.debug("Commit transaction");

        Transaction txn = (Transaction)defaultTransactionStatus.getTransaction();
        if (null != txn)
            // Commit
            txn.commit();
        else
            throw new NoTransactionException("Transaction object not available when about to commit");
    }

    @Override
    protected void doRollback(DefaultTransactionStatus defaultTransactionStatus) throws TransactionException {
        LOG.debug("Rollback transaction");

        Transaction txn = (Transaction)defaultTransactionStatus.getTransaction();
        if (null != txn) {
            // Rollback
            txn.isActive();
            txn.rollback();
        }
        else
            throw new NoTransactionException("Transaction object not available when about to rollback transaction");
    }
}
