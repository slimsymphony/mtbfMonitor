package com.nokia.testingservice.austere.model;

import java.sql.Timestamp;

import com.nokia.testingservice.austere.util.CommonUtils;

public class Result extends AustereModel<Result> {
	private int resultID;
	private int conditionID;
	private int result;
	private String resultAsText;
	private Timestamp timeStamp;
	private String stepID;

	public int getResultID() {
		return resultID;
	}

	public void setResultID( int resultID ) {
		this.resultID = resultID;
	}

	public int getConditionID() {
		return conditionID;
	}

	public void setConditionID( int conditionID ) {
		this.conditionID = conditionID;
	}

	public int getResult() {
		return result;
	}

	public void setResult( int result ) {
		this.result = result;
	}

	public String getResultAsText() {
		return resultAsText;
	}

	public void setResultAsText( String resultAsText ) {
		this.resultAsText = resultAsText;
	}

	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp( Timestamp timeStamp ) {
		this.timeStamp = timeStamp;
	}

	public String getStepID() {
		return stepID;
	}

	public void setStepID( String stepID ) {
		this.stepID = stepID;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
