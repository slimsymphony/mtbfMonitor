package com.nokia.testingservice.austere.exception;

import com.nokia.testingservice.austere.util.LogUtils;

public class AustereException extends Exception {

	private static final long serialVersionUID = 9186358833739012888L;

	public AustereException() {
		super();
	}

	public AustereException( Throwable t ) {
		super( t );
	}

	public AustereException( String msg, Throwable t ) {
		super( msg, t );
	}

	public AustereException( String msg ) {
		super( msg );
	}

	@Override
	public void printStackTrace() {
		LogUtils.getServiceLog().error( "AustereException", this );
	}
}
