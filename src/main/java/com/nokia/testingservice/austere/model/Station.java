package com.nokia.testingservice.austere.model;

import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.HashCodeBuilder;

/**
 * Describe Austere station
 * 
 * @author f78wang
 * 
 */
public class Station extends AustereModel<Station> {
	public final static int USED = 1;
	public final static int FREE = 0;
	public final static int UNKNOWN = -1;
	
	private int id;
	private String pcName;
	private int used = UNKNOWN;
	private String siteName;
	private String ip;
	private String mac;
	private String details;

	public int getId() {
		return id;
	}

	public void setId( int stationID ) {
		this.id = stationID;
	}

	public String getPcName() {
		return pcName;
	}

	public void setPcName( String pcName ) {
		this.pcName = pcName;
	}

	public int getUsed() {
		return used;
	}

	public void setUsed( int status ) {
		this.used = status;
	}
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName( String siteName ) {
		this.siteName = siteName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp( String ip ) {
		this.ip = ip;
	}

	public String getMac() {
		return mac;
	}

	public void setMac( String mac ) {
		this.mac = mac;
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails( String details ) {
		this.details = details;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( obj == null || !( obj instanceof Station ) ) {
			return false;
		}
		Station s = ( Station ) obj;
		if ( this.pcName.equalsIgnoreCase( s.getPcName() ) && this.id == s.getId() ) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		String pn = pcName==null?"":pcName;
		return new HashCodeBuilder(17, 31).append(id).append(pn.toLowerCase()).toHashCode();
	}
	
	@Override 
	public String toString() {
		return CommonUtils.toJson( this );
	}

}
