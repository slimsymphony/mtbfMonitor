package com.nokia.testingservice.austere.service;

/**
 * Factory for status service.
 * 
 * @author Frank Wang
 * @since May 29, 2012
 */
public class StatusServiceFactory {

	private static StatusService instance;

	public synchronized static StatusService getInstance() {
		if ( instance == null )
			instance = new StatusServiceImpl();
		return instance;
	}
}
