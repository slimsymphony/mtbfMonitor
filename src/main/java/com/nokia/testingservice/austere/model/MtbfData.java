package com.nokia.testingservice.austere.model;

import com.nokia.testingservice.austere.util.CommonUtils;

public class MtbfData extends AustereModel<MtbfData>{
	String softVersion;
	int resetAndFreeze;
	float targetMtbf;
	float mtbfIdx;

	public String getSoftVersion() {
		return softVersion;
	}

	public void setSoftVersion( String softVersion ) {
		this.softVersion = softVersion;
	}

	public int getResetAndFreeze() {
		return resetAndFreeze;
	}

	public void setResetAndFreeze( int resetAndFreeze ) {
		this.resetAndFreeze = resetAndFreeze;
	}

	public float getTargetMtbf() {
		return targetMtbf;
	}

	public void setTargetMtbf( float targetMtbf ) {
		this.targetMtbf = targetMtbf;
	}

	public float getMtbfIdx() {
		return mtbfIdx;
	}

	public void setMtbfIdx( float mtbfIdx ) {
		this.mtbfIdx = mtbfIdx;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
