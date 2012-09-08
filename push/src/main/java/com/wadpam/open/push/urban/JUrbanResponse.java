package com.wadpam.open.push.urban;

import java.util.*;

public class JUrbanResponse implements java.io.Serializable {

    private List<String> scheduledNotifications;

    public List<String> getScheduledNotifications() {
        return scheduledNotifications;
    }

    public void setScheduledNotifications(List<String> scheduledNotifications) {
        this.scheduledNotifications = scheduledNotifications;
    }

    public boolean hasScheduledNotifications() {
        return (null != scheduledNotifications && !scheduledNotifications.isEmpty());
    }
}
