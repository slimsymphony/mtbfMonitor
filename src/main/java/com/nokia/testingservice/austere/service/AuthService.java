package com.nokia.testingservice.austere.service;

import com.nokia.testingservice.austere.exception.AuthException;
import com.nokia.testingservice.austere.model.User;

public interface AuthService {

	public boolean authUser( String user, String password ) throws AuthException;
	
	public User getUserInfo( String user );
	
	public void logOff( String user ) throws AuthException;

	void updateCacheUser( User user );
	
}
