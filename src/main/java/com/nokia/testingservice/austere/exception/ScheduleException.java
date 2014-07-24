package com.nokia.testingservice.austere.exception;

import com.nokia.testingservice.austere.util.LogUtils;

/**
 * Schedule exception for describing schedule jobs.
 *
 * @author Frank Wang
 * @since Jun 11, 2012
 */
public class ScheduleException extends Exception {

	private static final long serialVersionUID = -2125696109447369183L;

	public ScheduleException() {
		super();
	}

	public ScheduleException( Throwable t ) {
		super( t );
	}

	public ScheduleException( String msg, Throwable t ) {
		super( msg, t );
	}

	public ScheduleException( String msg ) {
		super( msg );
	}

	@Override
	public void printStackTrace() {
		LogUtils.getServiceLog().error( "ScheduleException", this );
	}
}
