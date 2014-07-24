package com.nokia.testingservice.austere.model;

import java.util.List;

import com.nokia.testingservice.austere.util.CommonUtils;

public class Site extends AustereModel<Site> {
	private String siteName;
	private List<DbMeta> databases;
	
	public String getSiteName() {
		return siteName;
	}

	public void setSiteName( String siteName ) {
		this.siteName = siteName;
	}

	public List<DbMeta> getDatabases() {
		return databases;
	}

	public void setDatabases( List<DbMeta> databases ) {
		this.databases = databases;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
