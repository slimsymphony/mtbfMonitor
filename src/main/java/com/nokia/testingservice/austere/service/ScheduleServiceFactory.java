package com.nokia.testingservice.austere.service;

public class ScheduleServiceFactory {
	private static ScheduleService instance;

	public synchronized static ScheduleService getInstance() {
		if ( instance == null )
			instance = new ScheduleServiceImpl();
		return instance;
	}
}
