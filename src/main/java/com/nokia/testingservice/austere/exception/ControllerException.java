package com.nokia.testingservice.austere.exception;

import com.nokia.testingservice.austere.util.LogUtils;

/**
 * Exception type for defining the controller part errors.
 *
 * @author Frank Wang
 * @since May 31, 2012
 */
public class ControllerException extends Exception {
	private static final long serialVersionUID = 3260949465031158721L;

	public ControllerException() {
		super();
	}

	public ControllerException( Throwable t ) {
		super( t );
	}

	public ControllerException( String msg, Throwable t ) {
		super( msg, t );
	}

	public ControllerException( String msg ) {
		super( msg );
	}

	@Override
	public void printStackTrace() {
		LogUtils.getServiceLog().error( "ControllerException", this );
	}
}
