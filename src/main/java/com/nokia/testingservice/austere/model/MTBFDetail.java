package com.nokia.testingservice.austere.model;

import com.nokia.testingservice.austere.util.CommonUtils;

public class MTBFDetail extends AustereModel<MTBFDetail> {
	private int seq;
	private String type;
	private String errorID;
	private String desc;
	private String status;
	private int isKnown;
	private int stationID;
	private int reportDate;
	private int amount;

	
	
	public int getAmount() {
		return amount;
	}

	public void setAmount( int amount ) {
		this.amount = amount;
	}

	public int getReportDate() {
		return reportDate;
	}

	public void setReportDate( int reportDate ) {
		this.reportDate = reportDate;
	}

	public int getStationID() {
		return stationID;
	}

	public void setStationID( int stationID ) {
		this.stationID = stationID;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq( int seq ) {
		this.seq = seq;
	}

	public int getIsKnown() {
		return isKnown;
	}

	public void setIsKnown( int isKnown ) {
		this.isKnown = isKnown;
	}

	public String getType() {
		return type;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public String getErrorID() {
		return errorID;
	}

	public void setErrorID( String errorID ) {
		this.errorID = errorID;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc( String desc ) {
		this.desc = desc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus( String status ) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
