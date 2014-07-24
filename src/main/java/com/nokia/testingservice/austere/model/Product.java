package com.nokia.testingservice.austere.model;

import java.util.Date;

import com.nokia.testingservice.austere.util.CommonUtils;

/**
 * Define product model.
 *
 * @author Frank Wang
 * @since Jun 5, 2012
 */
public class Product extends AustereModel<Product> {

	private String productName;
	private int invalid;
	private Date createTime;
	private String instanceName;

	public String getProductName() {
		return productName;
	}

	public void setProductName( String productName ) {
		this.productName = productName;
	}

	public int getInvalid() {
		return invalid;
	}

	public void setInvalid( int invalid ) {
		this.invalid = invalid;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime( Date createTime ) {
		this.createTime = createTime;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName( String instanceName ) {
		this.instanceName = instanceName;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
