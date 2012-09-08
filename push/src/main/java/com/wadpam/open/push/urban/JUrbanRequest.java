package com.wadpam.open.push.urban;

import java.util.*;

/**
 * 
 * 
 * Push notification message
 * 
 */
public class JUrbanRequest implements java.io.Serializable {
	private List<String> apids;
	private List<String> deviceTokens;
	private List<String> aliases;
	private List<String> tags;
	private List<Calendar> scheduleFor;
	private List<String> excludeTokens;
	private APS aps;
	private Android android;
	private String url;
	private Map<String, Object> payload = new HashMap<String, Object>();

	public void addPayloadValue(String key, Object value) {
		this.payload.put(key, value);
	}

	public List<String> getDeviceTokens() {
		return deviceTokens;
	}

	public void setDeviceTokens(List<String> deviceTokens) {
		this.deviceTokens = deviceTokens;
	}

	public void addDeviceToken(String token) {
		if (this.deviceTokens == null) {
			this.deviceTokens = new ArrayList<String>();
		}
		this.getDeviceTokens().add(token);
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}

	public void addAlias(String alias) {
		this.getAliases().add(alias);
	}

	public List<String> getTags() {
		if (tags == null) {
			tags = new ArrayList<String>();
		}
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public void addTag(String tag) {
		this.getTags().add(tag);
	}

	public List<Calendar> getScheduleFor() {
		return scheduleFor;
	}

	public void addScheduleFor(Calendar c) {
		if (this.scheduleFor == null) {
			this.scheduleFor = new ArrayList<Calendar>();
		}
		this.scheduleFor.add(c);
	}

	public void setScheduleFor(List<Calendar> scheduleFor) {
		this.scheduleFor = scheduleFor;
	}

	public List<String> getExcludeTokens() {
		return excludeTokens;
	}

	public void setExcludeTokens(List<String> excludeTokens) {
		this.excludeTokens = excludeTokens;
	}

	public APS getAps() {
		return aps;
	}

	public void setAps(APS aps) {
		this.aps = aps;
	}

	public List<String> getApids() {
		return apids;
	}

	public void setApids(List<String> apids) {
		this.apids = apids;
	}
	
	public void addApid(String apid) {
		if (this.apids == null) {
			this.apids = new ArrayList<String>();
		}
		this.getApids().add(apid);
	}
	
	public Android getAndroid() {
		return android;
	}

	public void setAndroid(Android android) {
		this.android = android;
	}

	public void setUrl(String u) {
		this.url = u;
	}

	public String getUrl() {
		return this.url;
	}

	public void setMessage(String msg) {
		if (msg == null) {
			clearMessage();
		}

		this.android = new Android();
		this.android.setAlert(msg);
		this.aps = new APS();
		this.aps.setBadge(2);
		this.aps.setAlert(msg);

	}

	public void clearMessage() {
		this.android = null;
		this.aps = null;
	}
}
