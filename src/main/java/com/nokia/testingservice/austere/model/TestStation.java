package com.nokia.testingservice.austere.model;

import java.sql.Timestamp;

import com.nokia.testingservice.austere.util.CommonUtils;

/**
 * Define a test Station should be included properties.
 * 
 * @author f78wang
 *
 */
public class TestStation extends AustereModel<TestStation> {
	String id;
	int stationId;
	String executionId;
	String status;
	String testType;
	String sw;
	String product;
	String remark;
	int resetCount;
	int freezeCount;
	String systemType;
	Timestamp startTime;
	Timestamp lastUpdate;
	int passed;
	int failed;
	String siteName;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	public String getSw() {
		return sw;
	}

	public void setSw(String sw) {
		this.sw = sw;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getResetCount() {
		return resetCount;
	}

	public void setResetCount(int resetCount) {
		this.resetCount = resetCount;
	}

	public int getFreezeCount() {
		return freezeCount;
	}

	public void setFreezeCount(int freezeCount) {
		this.freezeCount = freezeCount;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public int getStationId() {
		return stationId;
	}

	public void setStationId( int stationId ) {
		this.stationId = stationId;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId( String executionId ) {
		this.executionId = executionId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus( String status ) {
		this.status = status;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType( String systemType ) {
		this.systemType = systemType;
	}

	public int getPassed() {
		return passed;
	}

	public void setPassed( int passed ) {
		this.passed = passed;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed( int failed ) {
		this.failed = failed;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName( String siteName ) {
		this.siteName = siteName;
	}

	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
