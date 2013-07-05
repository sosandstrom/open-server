/***/
package com.wadpam.open.push.json;

import com.wadpam.open.json.JBaseObject;

/**
 * @author sophea <a href='mailto:sm@goldengekko.com'> sophea </a>
 * @version $id$ - $Revision$
 * @date 2013
 */
public class JSubscription extends JBaseObject {

    private String deviceId;
    
    /**provided by each device*/
    private String deviceToken;

    /**end date*/
    private Long   endDate;

    /**e.g. for Nokadi brandId, for Track180 trackId, for TwoForOne 'all', for BAA airport:fligth:date*/
    private String key;

    /**it can be Urban email, sms junit xmpp*/
    private String pushType;

    /**start date*/
    private Long   startDate;

    /**userId*/
    private Long   userId;

    /**tag*/
    private String tag;
    
    
    /** deviceType */
    private Long deviceType;

    /**
     * @return the deviceToken
     */
    public String getDeviceToken() {
        return deviceToken;
    }

    /**
     * @param deviceToken the deviceToken to set
     */
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the startDate
     */
    public Long getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Long getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the pushType
     */
    public String getPushType() {
        return pushType;
    }

    /**
     * @param pushType the pushType to set
     */
    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return the deviceType
     */
    public Long getDeviceType() {
        return deviceType;
    }

    /**
     * @param deviceType the deviceType to set
     */
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
