/***/
package com.wadpam.open.push.service;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.wadpam.open.push.service.android.AndroidNotification;

/**
 * @author sophea <a href='mailto:sm@goldengekko.com'> sophea </a>
 * @version $id$ - $Revision$
 * @date 2013
 */
public class GoogleAndroidPushNotificationService implements PushNotificationService {

    private static final Logger LOG                     = LoggerFactory.getLogger(GoogleAndroidPushNotificationService.class);

    private AndroidNotification notification;

    public GoogleAndroidPushNotificationService(String appId,String secretKey) {
        notification = new AndroidNotification();
        notification.setAndroidConnector(new Sender(secretKey));
        notification.setAppId(appId);
    }
    /* (non-Javadoc)
     * @see com.wadpam.open.push.service.PushNotificationService#push(java.lang.String, java.lang.String[])
     */
    @Override
    public void push(String payload, String... identifiers) throws IOException {
        // TODO Auto-generated method stub

        LOG.debug("Starting to push notification for Android devices {}", payload);
        
        try {
            Message.Builder messageBuilder = notification.getMessageBuilder();
            messageBuilder.delayWhileIdle(true);
            messageBuilder.collapseKey(payload);
            messageBuilder.timeToLive(600);
            
            messageBuilder.addData(notification.getAppId(), payload);
            
            MulticastResult multicastResult = notification.getAndroidConnector().send(messageBuilder.build(),
                    Arrays.asList(identifiers), 5) ; //retry 5 times

        }
        catch (Exception e) {
            LOG.error("Failed to push message - {}", e.getMessage());
        }
        
        
    }

    /* (non-Javadoc)
     * @see com.wadpam.open.push.service.PushNotificationService#register(java.lang.String, java.lang.String[])
     */
    @Override
    public void register(String identifier, String... tags) throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.wadpam.open.push.service.PushNotificationService#unregister(java.lang.String)
     */
    @Override
    public void unregister(String identifier) throws IOException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.wadpam.open.push.service.PushNotificationService#pushTags(java.lang.String, java.lang.String[])
     */
    @Override
    public void pushTags(String message, String... tags) throws IOException {
        // TODO Auto-generated method stub

    }

}
