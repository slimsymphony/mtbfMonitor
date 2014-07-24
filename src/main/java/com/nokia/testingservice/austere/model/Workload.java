package com.nokia.testingservice.austere.model;

import com.nokia.testingservice.austere.util.CommonUtils;

/**
 * Define the workload on station related with task.
 *
 * @author Frank Wang
 * May 29, 2012
 */
public class Workload extends AustereModel<Workload> {
	private int stationID;
	private String pcName;
	private int wk;
	private int taskID;
	private int status;
	
	public int getStationID() {
		return stationID;
	}
	public void setStationID( int stationID ) {
		this.stationID = stationID;
	}
	public String getPcName() {
		return pcName;
	}
	public void setPcName( String pcName ) {
		this.pcName = pcName;
	}
	public int getWk() {
		return wk;
	}
	public void setWk( int wk ) {
		this.wk = wk;
	}
	public int getTaskID() {
		return taskID;
	}
	public void setTaskID( int taskID ) {
		this.taskID = taskID;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus( int status ) {
		this.status = status;
	}
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
