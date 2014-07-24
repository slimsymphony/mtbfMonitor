package com.nokia.testingservice.austere.service;

/**
 * Factory for task service.
 * 
 * @author Frank Wang
 * @since May 29, 2012
 */
public class TaskServiceFactory {

	private static TaskService instance;

	public synchronized static TaskService getInstance() {
		if ( instance == null )
			instance = new TaskServiceImpl();
		return instance;
	}
}
