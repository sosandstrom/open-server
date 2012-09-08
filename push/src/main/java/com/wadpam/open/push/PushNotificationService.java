package com.wadpam.open.push;

import java.io.IOException;

/**
 *
 * @author os
 */
public interface PushNotificationService {
    void push(String... identifiers) throws IOException;
    void register(String identifier) throws IOException;
    void unregister(String identifier) throws IOException;
}
