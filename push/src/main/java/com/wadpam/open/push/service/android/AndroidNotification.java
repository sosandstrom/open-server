package com.wadpam.open.push.service.android;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

public class AndroidNotification {

    private Message.Builder message;

    private Sender          androidConnector;

    private String appId;
    /**
     * Get the gcm message
     * 
     * @return
     */
    public Message.Builder getMessageBuilder() {
        if (null == this.message) {
            this.message = new Message.Builder();
        }
        return message;
    }

    /**
     * @return the androidConnector
     */
    public Sender getAndroidConnector() {

        return androidConnector;
    }

    /**
     * @param androidConnector
     *            the androidConnector to set
     */
    public void setAndroidConnector(Sender androidConnector) {
        this.androidConnector = androidConnector;
    }

    public Message.Builder getMessage() {
        return message;
    }

    public void setMessage(Message.Builder message) {
        this.message = message;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

}
