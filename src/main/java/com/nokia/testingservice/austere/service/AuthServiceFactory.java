package com.nokia.testingservice.austere.service;

/**
 * Factory for authService.
 *
 * @author Frank Wang
 * @since May 29, 2012
 */
public class AuthServiceFactory {
	private static AuthService instance;

	public static synchronized AuthService getInstance() {
		if ( instance == null )
			instance = new AuthServiceImpl();
		return instance;
	}
}
