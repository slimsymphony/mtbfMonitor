package com.nokia.testingservice.austere.exception;

import com.nokia.testingservice.austere.util.LogUtils;

/**
 * Exception type for representing exceptions thrown in service phase.
 * 
 * @author Frank Wang
 * @since May 29, 2012
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = -1296075362158550060L;

	public ServiceException() {
		super();
	}

	public ServiceException( Throwable t ) {
		super( t );
	}

	public ServiceException( String msg, Throwable t ) {
		super( msg, t );
	}

	public ServiceException( String msg ) {
		super( msg );
	}

	@Override
	public void printStackTrace() {
		LogUtils.getServiceLog().error( "ServiceException", this );
	}
}
