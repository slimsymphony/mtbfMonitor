package com.nokia.testingservice.austere.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
	
	public static Logger getLog( String name ) {
		return LoggerFactory.getLogger( name );
	}
	
	public static Logger getServiceLog( ) {
		return LoggerFactory.getLogger( "service" );
	}
	
	public static Logger getWebLog( ) {
		return LoggerFactory.getLogger( "web" );
	}
	
	public static Logger getDbLog( ) {
		return LoggerFactory.getLogger( "db" );
	}
	
	public static Logger getScheduleLog( ) {
		return LoggerFactory.getLogger( "schedule" );
	}
}
