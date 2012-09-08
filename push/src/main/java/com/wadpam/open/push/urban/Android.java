
package com.wadpam.open.push.urban;

public class Android implements java.io.Serializable
{
	private String alert;
	private String extra;
	
	public String getAlert()
	{
		return alert;
	}
	
	public void setAlert(String alert)
	{
		this.alert = alert;
	}
	
	public String getExtra()
	{
		return extra;
	}
	
	public void setExtra(String extra)
	{
		this.extra = extra;
	}
	
	public String toString()
	{
		return this.getAlert();
	}
	
	
}
