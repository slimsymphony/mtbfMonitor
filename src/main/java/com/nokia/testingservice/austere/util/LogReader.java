package com.nokia.testingservice.austere.util;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogReader {

	public static enum LogType {
		ROOT, DB, SERVICE, SCHEDULE, WEB;
		public String toString() {
			return this.name().toLowerCase() + ".log";
		}

		public static LogType parse( String name ) {
			if ( DB.name().equalsIgnoreCase( name ) ) {
				return DB;
			} else if ( SERVICE.name().equalsIgnoreCase( name ) ) {
				return SERVICE;
			} else if ( SCHEDULE.name().equalsIgnoreCase( name ) ) {
				return SCHEDULE;
			} else if ( WEB.name().equalsIgnoreCase( name ) ) {
				return WEB;
			} else {
				return ROOT;
			}
		}

	}

	public static enum DateType {
		TODAY, YESTERDAY, TWODAYSBEFORE, THREEDAYSBEFORE;
		public static DateType parse( int value ) {
			if ( value == TODAY.getValue() )
				return TODAY;
			else if ( value == YESTERDAY.getValue() )
				return YESTERDAY;
			else if ( value == TWODAYSBEFORE.getValue() )
				return TWODAYSBEFORE;
			else if ( value == THREEDAYSBEFORE.getValue() )
				return THREEDAYSBEFORE;
			else
				return TODAY;
		}

		public String getPostfix() {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat( ".yy-MM-dd" );
			switch ( this ) {
				case TODAY:
					return "";
				case YESTERDAY:
					cal.add( Calendar.DATE, -1 );
					return sdf.format( cal.getTime() );
				case TWODAYSBEFORE:
					cal.add( Calendar.DATE, -2 );
					return sdf.format( cal.getTime() );
				case THREEDAYSBEFORE:
					cal.add( Calendar.DATE, -3 );
					return sdf.format( cal.getTime() );
				default:
					return "";
			}
		}

		public int getValue() {
			switch ( this ) {
				case TODAY:
					return 0;
				case YESTERDAY:
					return -1;
				case TWODAYSBEFORE:
					return -2;
				case THREEDAYSBEFORE:
					return -3;
			}
			return 0;
		}
	}

	public static void main( String[] args ) {
		System.out.println( LogReader.getLog( com.nokia.testingservice.austere.util.LogReader.LogType.DB, DateType.TODAY,
				"C:\\develop\\eclipse-workspaces\\jee_workspace\\austereWeb" ) );
	}

	public static String getLog( LogType lt, DateType dt, String webRootPath ) {
		String fileName = lt.toString() + dt.getPostfix();
		String realPath = webRootPath + "/../../austereLogs/";
		File file = new File( realPath, fileName );
		if ( !file.exists() ) {
			// try again
			file = new File( webRootPath + "/../../../austereLogs/", fileName );
			if ( !file.exists() )
				return "The log file you assigned:" + file.getAbsolutePath() + " could not found currently.";
			else {
				FileReader fr = null;
				StringBuffer sb = new StringBuffer( 20000 );
				char[] data = new char[1024];
				try {
					fr = new FileReader( file );
					int cnt = 0;
					while ( ( cnt = fr.read( data ) ) > 0 ) {
						sb.append( data, 0, cnt );
					}
				} catch ( Exception e ) {
					LogUtils.getWebLog().error( "Get log met problem LogType:" + lt + ", Datetype:" + dt, e );
				} finally {
					CommonUtils.closeQuitely( fr );
				}
				return sb.toString().replaceAll( "\n", "<br/>" );
			}
		} else {
			FileReader fr = null;
			StringBuffer sb = new StringBuffer( 20000 );
			char[] data = new char[1024];
			try {
				fr = new FileReader( file );
				int cnt = 0;
				while ( ( cnt = fr.read( data ) ) > 0 ) {
					sb.append( data, 0, cnt );
				}
			} catch ( Exception e ) {
				LogUtils.getWebLog().error( "Get log met problem LogType:" + lt + ", Datetype:" + dt, e );
			} finally {
				CommonUtils.closeQuitely( fr );
			}
			return sb.toString().replaceAll( "\n", "<br/>" );
		}
	}
}
