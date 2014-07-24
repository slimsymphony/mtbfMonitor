package com.nokia.testingservice.austere.exception;

import com.nokia.testingservice.austere.util.LogUtils;
/**
 * Exception for auth related error.
 *
 * @author Frank Wang
 * @since Jun 8, 2012
 */
public class AuthException extends Exception {

	private static final long serialVersionUID = 5807574591849408432L;

	public AuthException() {
		super();
	}

	public AuthException( Throwable t ) {
		super( t );
	}

	public AuthException( String msg, Throwable t ) {
		super( msg, t );
	}

	public AuthException( String msg ) {
		super( msg );
	}

	@Override
	public void printStackTrace() {
		LogUtils.getServiceLog().error( "AuthException", this );
	}
}
