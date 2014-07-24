package com.nokia.testingservice.austere.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;

import com.google.gson.Gson;

public final class CommonUtils {

	private static SimpleDateFormat wkFmt = new SimpleDateFormat( "yyww" );
	private static SimpleDateFormat dayFmt = new SimpleDateFormat( "yywwFF" );

	public static String getWebRootPath( HttpServletRequest request, String path ) {
		return request.getSession().getServletContext().getRealPath( path );
	}

	public static String getErrorStack( Throwable t ) {
		StringWriter sw = new StringWriter();
		t.printStackTrace( new PrintWriter( sw ) );
		return sw.toString();
	}

	public static void rollback( Connection conn ) {
		try {
			conn.rollback();
		} catch ( SQLException e ) {
			LogUtils.getDbLog().error( "Connection Roll back failed.", e );
		}
	}

	public static void setCommit( Connection conn, boolean val ) {
		try {
			conn.setAutoCommit( val );
		} catch ( SQLException e ) {
			LogUtils.getDbLog().error( "Connection Set autocommit option failed. value=" + val, e );
		}
	}

	public static void closeQuitely( Connection conn ) {
		if ( conn != null ) {
			try {
				conn.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( ResultSet rs ) {
		if ( rs != null ) {
			try {
				rs.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( PreparedStatement ps ) {
		if ( ps != null ) {
			try {
				ps.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( InputStream in ) {
		if ( in != null ) {
			try {
				in.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( OutputStream out ) {
		if ( out != null ) {
			try {
				out.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( Reader reader ) {
		if ( reader != null ) {
			try {
				reader.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void closeQuitely( Writer writer ) {
		if ( writer != null ) {
			try {
				writer.close();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static String getProperties( String propName, String propFile ) throws Exception {
		Properties props = new Properties();
		props.load( CommonUtils.class.getClassLoader().getResourceAsStream( "db.properties" ) );
		return ( String ) props.get( propName );
	}

	public static String toJson( Object o ) {
		Gson json = new Gson();
		return json.toJson( o );
	}

	public static <T> T fromJson( String jsonStr, Class<T> t ) {
		Gson json = new Gson();
		return json.fromJson( jsonStr, t );
	}
	
	public static <T> T fromJson( String jsonStr, Type type ) {
		Gson json = new Gson();
		return json.fromJson( jsonStr, type );
	}

	public static int getDay( Date date ) {
		/*
		 * Calendar cal = Calendar.getInstance(); cal.setTime( date ); return Integer.parseInt( String.valueOf( cal.get(
		 * Calendar.YEAR ) ).substring( 2 ) + String.valueOf( cal.get( Calendar.WEEK_OF_YEAR ) ) + String.valueOf(
		 * cal.get( Calendar.DAY_OF_WEEK ) ) );
		 */
		return parseInt( dayFmt.format( date ), 0 );
	}

	public static int getDay2( Date date ) {
		Calendar cal = Calendar.getInstance();
		cal.setTime( date );
		return Integer.parseInt( String.valueOf( cal.get( Calendar.YEAR ) ).substring( 2 ) + String.valueOf( cal.get( Calendar.WEEK_OF_YEAR ) )
				+ String.valueOf( cal.get( Calendar.DAY_OF_WEEK ) ) );

	}

	public static int getWk( Date date ) {
		/*
		 * Calendar cal = Calendar.getInstance(); cal.setTime( date ); return Integer.parseInt( String.valueOf( cal.get(
		 * Calendar.YEAR ) ).substring( 2 ) + String.valueOf( cal.get( Calendar.WEEK_OF_YEAR ) ) );
		 */
		return parseInt( wkFmt.format( date ), 0 );
	}

	public static int getCurrentDay() {
		return getDay( new Date() );
	}

	public static int getCurrentWk() {
		int year = Calendar.getInstance().get( Calendar.YEAR );
		int wks = Calendar.getInstance().get( Calendar.WEEK_OF_YEAR );
		return Integer.parseInt( String.valueOf( year ).substring( 2 ) + String.valueOf( wks ) );
	}

	public static int parseInt( String str, int defaultValue ) {
		try {
			return Integer.parseInt( str );
		} catch ( Exception e ) {
			return defaultValue;
		}
	}
	
	public static boolean parseBool( String str ) {
		return Boolean.parseBoolean( str );
	}

	public static float parseFloat( String str, float defaultValue ) {
		try {
			return Float.parseFloat( str );
		} catch ( Exception e ) {
			return defaultValue;
		}
	}

	public static String notNull( String str, boolean forWeb ) {
		if ( str == null )
			return forWeb ? "&nbsp;" : "";
		return str;
	}

	public static long parseLong( String str, long defaultValue ) {
		try {
			return Long.parseLong( str );
		} catch ( Exception e ) {
			return defaultValue;
		}
	}

	private static TimeZone gmtZone = TimeZone.getTimeZone( "GMT" );

	public static Timestamp local2gmt( Timestamp date, TimeZone localZone ) {
		long gmtMillis = date.getTime() - localZone.getOffset( date.getTime() );
		long targetmillis = gmtMillis + gmtZone.getOffset( System.currentTimeMillis() );
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date( targetmillis ) );
		return new Timestamp( cal.getTimeInMillis() );
	}

	public static Timestamp gmt2local( Timestamp date, TimeZone localZone ) {
		long gmtMillis = date.getTime() - gmtZone.getOffset( date.getTime() );
		long targetmillis = gmtMillis + localZone.getOffset( System.currentTimeMillis() );
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date( targetmillis ) );
		return new Timestamp( cal.getTimeInMillis() );
	}

	public static Date local2gmt( Date date, TimeZone localZone ) {
		long gmtMillis = date.getTime() - localZone.getOffset( date.getTime() );
		long targetmillis = gmtMillis + gmtZone.getOffset( System.currentTimeMillis() );
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date( targetmillis ) );
		return cal.getTime();
	}

	public static Date gmt2local( Date date, TimeZone localZone ) {
		long gmtMillis = date.getTime() - gmtZone.getOffset( date.getTime() );
		long targetmillis = gmtMillis + localZone.getOffset( System.currentTimeMillis() );
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date( targetmillis ) );
		return cal.getTime();
	}
	
	public static String getDateStr( Date date, String pattern ) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format( date );
	}
	
	public static Date[] getIntervalFromWeek( int week ) {
		Calendar cal = Calendar.getInstance();
		int year = 2000 + Integer.parseInt( String.valueOf( week ).substring( 0, 2 ) );
		int wk = Integer.parseInt( String.valueOf( week ).substring( 2 ) );
		cal.set( Calendar.YEAR, year );
		cal.set( Calendar.WEEK_OF_YEAR, wk );
		cal.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		Date[] interval = new Date[2];
		interval[0] = cal.getTime();
		cal.set( Calendar.HOUR_OF_DAY, 23 );
		cal.set( Calendar.MINUTE, 59 );
		cal.set( Calendar.SECOND, 59 );
		cal.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
		interval[1] = cal.getTime();
		return interval;
	}
}
