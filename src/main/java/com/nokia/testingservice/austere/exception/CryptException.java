package com.nokia.testingservice.austere.exception;

import com.nokia.testingservice.austere.util.LogUtils;

/**
 * Exception for Encrypt and Decrypt.
 *
 * @author Frank Wang
 * @since Jun 29, 2012
 */
public class CryptException extends AustereException {

	private static final long serialVersionUID = -2559257271182646166L;

	public CryptException() {
		super();
	}

	public CryptException( Throwable t ) {
		super( t );
	}

	public CryptException( String msg, Throwable t ) {
		super( msg, t );
	}

	public CryptException( String msg ) {
		super( msg );
	}

	@Override
	public void printStackTrace() {
		LogUtils.getServiceLog().error( "CryptException", this );
	}
}
