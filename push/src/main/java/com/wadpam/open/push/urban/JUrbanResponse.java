package com.wadpam.open.push.urban;

import java.util.*;

public class JUrbanResponse implements java.io.Serializable {
    
    private String push_id;
    
    

//    private List<String> scheduledNotifications;
//
//    public List<String> getScheduledNotifications() {
//        return scheduledNotifications;
//    }
//
//    public void setScheduledNotifications(List<String> scheduledNotifications) {
//        this.scheduledNotifications = scheduledNotifications;
//    }
//
//    public boolean hasScheduledNotifications() {
//        return (null != scheduledNotifications && !scheduledNotifications.isEmpty());
//    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }
}
