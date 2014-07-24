package com.nokia.testingservice.austere.service;

public class EmergencyServiceFactory {
	private static EmergencyService instance;

	public synchronized static EmergencyService getInstance() {
		if ( instance == null )
			instance = new EmergencyServiceImpl();
		return instance;
	}
}
