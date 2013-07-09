package com.wadpam.open.push.urban;

/**
 * 
 * APS: Apple Push notification Service
 * 
 */
public class APS implements java.io.Serializable {
    /**auto, increment, and  decrement: values to send ( "auto" ,"+1","-1" ) */
	private String badge ="auto";
	private String alert;
	
//	private String sound;

//	public Integer getBadge() {
//            return (Integer) badge;
//	}
//
//	/**
//	 * 
//	 * @param iBadge
//	 * 
//	 * @see #setAutoBadge(String)
//	 * 
//	 */
//	public void setBadge(Integer iBadge) {
//		this.badge = iBadge;
//	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

//	public void setAlert(Alert a) {
//		this.alert = a;
//	}
//
//	public String getSound() {
//		return sound;
//	}
//
//	public void setSound(String sound) {
//		this.sound = sound;
//	}
//
//	/**
//	 * 
//	 * Apple-specific class
//	 * 
//	 * see Apple's APNS docs for more information
//	 * 
//	 */
//	public static class Alert extends java.util.HashMap<String, Object> {
//		public void setBody(String value) {
//			this.put("body", value);
//		}
//
//		public void setActionLocKey(String value) {
//			this.put("action-loc-key", value);
//		}
//
//		public void setLocKey(String value) {
//			this.put("loc-key", value);
//		}
//
//		public void setLocArgs(String... args) {
//			this.put("loc-args", args);
//		}
//	}
}
