package com.wadpam.open.transaction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker interface to annotating methods that are transactional and idempotent.
 * @author mattiaslevin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    // marker annotation
}
