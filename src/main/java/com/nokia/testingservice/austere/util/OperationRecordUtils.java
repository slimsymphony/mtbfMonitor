package com.nokia.testingservice.austere.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.nokia.testingservice.austere.model.OperationRecord;
import com.nokia.testingservice.austere.model.OperationRecord.OperationType;

public class OperationRecordUtils {

	public static void record( String userID, OperationType type, String detail ) {
		OperationRecord or = new OperationRecord();
		or.setUserID( userID );
		or.setType( type );
		or.setDetails( detail );
		record( or );
	}
	
	public static void record( OperationRecord record ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "insert into OperationRecords( OperationType, UserID, Details, OperationTime ) values(?,?,?,?)" );
			ps.setString( 1, record.getType().name() );
			ps.setString( 2, record.getUserID() );
			ps.setString( 3, record.getDetails() );
			ps.setTimestamp( 4, CommonUtils.local2gmt( new Timestamp(System.currentTimeMillis()), TimeZone.getDefault() ) );
			ps.executeUpdate();
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "Record operationRecord failed, record:" + record );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	private static OperationRecord encap( ResultSet rs ) throws SQLException {
		OperationRecord or = new OperationRecord();
		or.setType( OperationType.parse( rs.getString( "OperationType" ) ) );
		or.setOperationTime( CommonUtils.gmt2local( rs.getTimestamp( "OperationTime" ), TimeZone.getDefault() ) );
		or.setUserID( rs.getString( "UserID" ) );
		or.setDetails( rs.getString( "Details" ) );
		return or;
	}

	public static List<OperationRecord> getAllRecords() {
		List<OperationRecord> records = new ArrayList<OperationRecord>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from OperationRecords order by OperationTime desc" );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				records.add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "Get all operationRecord failed." );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}

		return records;
	}

	public static List<OperationRecord> getRecords( Timestamp start, Timestamp end ) {
		List<OperationRecord> records = new ArrayList<OperationRecord>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from OperationRecords where OperationTime between ? and ? order by OperationTime desc" );
			ps.setTimestamp( 1, start );
			ps.setTimestamp( 2, end );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				records.add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "Get all operationRecord failed." );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}

		return records;
	}

	public static List<OperationRecord> getRecords( String userID ) {
		List<OperationRecord> records = new ArrayList<OperationRecord>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from OperationRecords where UserID=? order by OperationTime desc" );
			ps.setString( 1, userID );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				records.add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "Get all operationRecord failed." );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return records;
	}

	public static List<OperationRecord> getRecords( OperationType type ) {
		List<OperationRecord> records = new ArrayList<OperationRecord>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from OperationRecords where OperationType=? order by OperationTime desc" );
			ps.setString( 1, type.name() );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				records.add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getDbLog().error( "Get all operationRecord failed." );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return records;
	}
}
