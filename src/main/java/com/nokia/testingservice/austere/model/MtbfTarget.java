package com.nokia.testingservice.austere.model;

import com.nokia.testingservice.austere.util.CommonUtils;

public class MtbfTarget extends AustereModel<MtbfTarget> {
	private String productName;
	private float mtbfTarget;
	private int isValid;
	
	public String getProductName() {
		return productName;
	}
	public void setProductName( String productName ) {
		this.productName = productName;
	}
	public float getMtbfTarget() {
		return mtbfTarget;
	}
	public void setMtbfTarget( float mtbfTarget ) {
		this.mtbfTarget = mtbfTarget;
	}
	public int getIsValid() {
		return isValid;
	}
	public void setIsValid( int isValid ) {
		this.isValid = isValid;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
