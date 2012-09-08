package com.wadpam.open.push.urban;

/**
 * 
 * APS: Apple Push notification Service
 * 
 */
public class APS implements java.io.Serializable {
	private Object badge;
	private Object alert;
	private String sound;

	public boolean isAutoBadge() {
		return (this.badge instanceof String);
	}

	public Integer getBadgeAsInteger() {
		if (this.badge instanceof Integer) {
			return (Integer) badge;
		} else {
			throw new IllegalStateException("badge is not an integer");
		}
	}

	public String getBadgeAsString() {
		if (this.badge instanceof String) {
			return (String) badge;
		} else {
			throw new IllegalStateException("badge is not a string");
		}
	}

	/**
	 * 
	 * use this method for autobadge
	 * 
	 * @param strAutoBadge
	 *            "auto" or "+1" or "-1"
	 * 
	 * @see #setBadge(Integer)
	 * 
	 */
	public void setAutoBadge(String strAutoBadge) {
		if ((strAutoBadge != null) && (!strAutoBadge.equals("auto"))
				&& (!strAutoBadge.startsWith("+"))
				&& (!strAutoBadge.startsWith("-"))) {
			throw new IllegalArgumentException("strAutoBadge parameter: "
					+ strAutoBadge);
		}

		this.badge = strAutoBadge;
	}

	/**
	 * 
	 * @param iBadge
	 * 
	 * @see #setAutoBadge(String)
	 * 
	 */
	public void setBadge(Integer iBadge) {
		this.badge = iBadge;
	}

	public String getAlertAsString() {
		return (String) alert;
	}

	public Alert getAlert() {
		return (Alert) alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public void setAlert(Alert a) {
		this.alert = a;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	/**
	 * 
	 * Apple-specific class
	 * 
	 * see Apple's APNS docs for more information
	 * 
	 */
	public static class Alert extends java.util.HashMap<String, Object> {
		public void setBody(String value) {
			this.put("body", value);
		}

		public void setActionLocKey(String value) {
			this.put("action-loc-key", value);
		}

		public void setLocKey(String value) {
			this.put("loc-key", value);
		}

		public void setLocArgs(String... args) {
			this.put("loc-args", args);
		}
	}
}
