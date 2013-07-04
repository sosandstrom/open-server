package com.wadpam.open.push.service;

import java.io.IOException;

/**
 *
 * @author os
 */
public interface PushNotificationService {
    void push(String message,String... identifiers) throws IOException;
    void register(String identifier,String... tags) throws IOException;
    void unregister(String identifier) throws IOException;
    void pushTags(String message,String... tags) throws IOException;
}
