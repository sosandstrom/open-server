package com.wadpam.open.web;

/**
 * You can register a concrete class implementing this interface to get callbacks
 * when a device request a UUID.
 * This can be used to save the UUID in the datastore as part of a registration process.
 * @author mattiaslevin
 */
public interface UUIDCreatedListener {

    /** This method is called when the uuid is created */
    void uuidCreated(String uuid, String appArg1, String appArg2);

}
