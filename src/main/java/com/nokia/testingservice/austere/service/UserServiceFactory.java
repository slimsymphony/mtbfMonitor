package com.nokia.testingservice.austere.service;

/**
 * User service Factory.
 *
 * @author Frank Wang
 * @since Jun 14, 2012
 */
public class UserServiceFactory {

	private static UserService instance;

	public static synchronized UserService getInstance() {
		if ( instance == null )
			instance = new UserServiceImpl();
		return instance;
	}
}
