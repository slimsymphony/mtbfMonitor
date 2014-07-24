package com.nokia.testingservice.austere.service;

/**
 * Facotry of station service.
 *
 * @author Frank Wang
 * @since May 30, 2012
 */
public class StationServiceFactory {

	private static StationService instance;

	public synchronized static StationService getInstance() {
		if ( instance == null )
			instance = new StationServiceImpl();
		return instance;
	}
}
