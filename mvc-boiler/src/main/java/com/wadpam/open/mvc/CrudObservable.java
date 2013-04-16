/*
 * INSERT COPYRIGHT HERE
 */

package com.wadpam.open.mvc;

/**
 *
 * @author sosandstrom
 */
public interface CrudObservable {

    void addListener(CrudListener listener);
    
    void removeListener(CrudListener listener);
    
}
