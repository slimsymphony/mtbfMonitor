package com.nokia.testingservice.austere.model;

import com.nokia.testingservice.austere.util.CommonUtils;

/**
 * Model object for represent Daily Report meta data.
 *
 * @author Frank Wang
 * @since Jun 20, 2012
 */
public class ReportMeta extends AustereModel<ReportMeta> {
	
	public static enum Status{
		F,P,I;
		public static Status parse(String str) {
			for(Status s: Status.values()) {
				if(s.name().equals( str ))
					return s;
			}
			return P;
		}
	}
	
	private String productName;
	private String swVersion;
	private String hwVersion;
	private String lanPackage;
	private String IMEI;
	private String simCardInfo;
	private String trace;
	private String location;
	private String testSystem;
	private String totalRuntime;
	private float invalidTime;
	private int resets;
	private int freezes;
	private String mtbfIdx;
	private Status status;
	private int date;

	
	
	public String getProductName() {
		return productName;
	}

	public void setProductName( String productName ) {
		this.productName = productName;
	}

	public String getSwVersion() {
		return swVersion;
	}

	public void setSwVersion( String swVersion ) {
		this.swVersion = swVersion;
	}

	public String getHwVersion() {
		return hwVersion;
	}

	public void setHwVersion( String hwVersion ) {
		this.hwVersion = hwVersion;
	}

	public String getLanPackage() {
		return lanPackage;
	}

	public void setLanPackage( String lanPackage ) {
		this.lanPackage = lanPackage;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI( String iMEI ) {
		IMEI = iMEI;
	}

	public String getSimCardInfo() {
		return simCardInfo;
	}

	public void setSimCardInfo( String simCardInfo ) {
		this.simCardInfo = simCardInfo;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace( String trace ) {
		this.trace = trace;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation( String location ) {
		this.location = location;
	}

	public String getTestSystem() {
		return testSystem;
	}

	public void setTestSystem( String testSystem ) {
		this.testSystem = testSystem;
	}

	public String getTotalRuntime() {
		return totalRuntime;
	}

	public void setTotalRuntime( String totalRuntime ) {
		this.totalRuntime = totalRuntime;
	}

	public float getInvalidTime() {
		return invalidTime;
	}

	public void setInvalidTime( float invalidTime ) {
		this.invalidTime = invalidTime;
	}

	public int getResets() {
		return resets;
	}

	public void setResets( int resets ) {
		this.resets = resets;
	}

	public int getFreezes() {
		return freezes;
	}

	public void setFreezes( int freezes ) {
		this.freezes = freezes;
	}

	public String getMtbfIdx() {
		return mtbfIdx;
	}

	public void setMtbfIdx( String mtbfIdx ) {
		this.mtbfIdx = mtbfIdx;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus( Status status ) {
		this.status = status;
	}

	public int getDate() {
		return date;
	}

	public void setDate( int date ) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
	
}
