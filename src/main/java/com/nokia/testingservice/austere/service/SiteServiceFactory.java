package com.nokia.testingservice.austere.service;

public class SiteServiceFactory {

	private static SiteService instance;

	public synchronized static SiteService getInstance() {
		if ( instance == null )
			instance = new SiteServiceImpl();
		return instance;
	}
}
