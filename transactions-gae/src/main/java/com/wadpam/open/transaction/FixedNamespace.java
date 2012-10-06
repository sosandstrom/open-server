package com.wadpam.open.transaction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker interface to annotating methods that should run in a fixed namespace
 * @author mattiaslevin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FixedNamespace {

    // The name of the fixed namespace
    String value();

}
