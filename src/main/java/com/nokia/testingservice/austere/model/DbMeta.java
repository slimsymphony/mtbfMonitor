package com.nokia.testingservice.austere.model;

import com.nokia.testingservice.austere.util.CommonUtils;

public class DbMeta extends AustereModel<DbMeta> {
	private String siteName;
	private String url;
	private String user;
	private String password;
	private String driverClass;

	public String getUrl() {
		return url;
	}

	public void setUrl( String url ) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser( String user ) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName( String siteName ) {
		this.siteName = siteName;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass( String driverClass ) {
		this.driverClass = driverClass;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
