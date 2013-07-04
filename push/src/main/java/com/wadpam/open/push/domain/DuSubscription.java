package com.wadpam.open.push.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;

import net.sf.mardao.core.domain.AbstractLongEntity;
@Entity
public class DuSubscription extends AbstractLongEntity {

    @Basic
    private String deviceId;
    
    /**provided by each device token*/
    @Basic
    private String deviceToken;

    /**e.g. for Nokadi brandId, for Track180 trackId, for TwoForOne 'all', for BAA airport:fligth:date*/
    @Basic
    private String key;

    @Basic
    private Long   userId;

    @Basic
    private Date   startDate;

    @Basic
    private Date   endDate;

    /**it can be Urban email, sms junit xmpp*/
    @Basic
    private String pushType;

    @Basic
    private String tag;
    
    @Basic
    private Long deviceType;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Long deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
