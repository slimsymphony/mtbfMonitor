package com.nokia.testingservice.austere.model;

import com.nokia.testingservice.austere.util.CommonUtils;


/**
 * Define a austere subscribe task.
 *
 * @author Frank Wang
 * May 29, 2012
 */
public class Task extends AustereModel<Task> {
	private int taskID;
	private String product;
	private int startWk;
	private int endWk;
	private String milestone;
	private int stationCount;
	private String owner;
	private int isUpdated;
	private int status;
	private String site;
	
	public int getTaskID() {
		return taskID;
	}
	public void setTaskID( int taskID ) {
		this.taskID = taskID;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct( String product ) {
		this.product = product;
	}
	public int getStartWk() {
		return startWk;
	}
	public void setStartWk( int startWk ) {
		this.startWk = startWk;
	}
	public int getEndWk() {
		return endWk;
	}
	public void setEndWk( int endWk ) {
		this.endWk = endWk;
	}
	public String getMilestone() {
		return milestone;
	}
	public void setMilestone( String milestone ) {
		this.milestone = milestone;
	}
	public int getStationCount() {
		return stationCount;
	}
	public void setStationCount( int stationCount ) {
		this.stationCount = stationCount;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner( String owner ) {
		this.owner = owner;
	}
	public int getIsUpdated() {
		return isUpdated;
	}
	public void setIsUpdated( int isUpdated ) {
		this.isUpdated = isUpdated;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus( int status ) {
		this.status = status;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
