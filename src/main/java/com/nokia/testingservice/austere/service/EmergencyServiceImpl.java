package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.mail.EmailException;

import com.nokia.testingservice.austere.model.Emergency;
import com.nokia.testingservice.austere.model.Emergency.EmergencyLevel;
import com.nokia.testingservice.austere.model.Emergency.EmergencyType;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LogUtils;
import com.nokia.testingservice.austere.util.MailUtils;

/**
 * Implementation for emergency service Api.
 * 
 * @author Frank Wang
 * @since Jun 12, 2012
 */
public class EmergencyServiceImpl implements EmergencyService {

	private BlockingDeque<Emergency> emergencyQueue = new LinkedBlockingDeque<Emergency>();

	@Override
	public void throwEmergency( Emergency e ) {
		emergencyQueue.push( e );
		if ( e.getStakeholders().size() > 0 ) {
			try {
				MailUtils.sendMail( e.getStakeholders(), null, e.getSource(), e.getDetail() );
			} catch ( EmailException e1 ) {
				LogUtils.getServiceLog().error( "Send Mail Failed", e1 );
			}
		} else {
			if ( e.getType() == EmergencyType.DatabaseEmergency ) {
				try {
					MailUtils.sendMail( MailUtils.austereOwnerList, null, e.getSource(), e.getDetail() );
				} catch ( EmailException e1 ) {
					LogUtils.getServiceLog().error( "Send Mail to Owner list Failed", e1 );
				}
			}
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			String sql = "insert into EmergencyRecord(type,level,source,detail,time) values(?,?,?,?,?)";
			ps = conn.prepareStatement( sql );
			ps.setString( 1, e.getType().name() );
			ps.setString( 2, e.getLevel().name() );
			ps.setString( 3, e.getSource() );
			ps.setString( 4, e.getDetail() );
			ps.setTimestamp( 5, CommonUtils.local2gmt( new Timestamp( e.getEmergencyTime().getTime() ), TimeZone.getDefault() ) );
			ps.executeUpdate();
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Save emergency event to db failed", ex );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	@Override
	public boolean haveEmergency() {
		return ( emergencyQueue.size() == 0 );
	}

	@Override
	public BlockingQueue<Emergency> getEmergencyQueue() {
		return emergencyQueue;
	}

	@Override
	public void throwScheduleEmergency( String source, String detail ) {
		Emergency em = new Emergency();
		em.setLevel( EmergencyLevel.Error );
		em.setType( EmergencyType.ScheduleEmergency );
		em.setEmergencyTime( CommonUtils.local2gmt( new Date(), TimeZone.getDefault() ) );
		em.setSource( source );
		em.setDetail( detail );
		throwEmergency( em );
	}

	@Override
	public void throwServiceEmergency( String source, String detail ) {
		Emergency em = new Emergency();
		em.setLevel( EmergencyLevel.Error );
		em.setType( EmergencyType.ServiceEmergency );
		em.setEmergencyTime( CommonUtils.local2gmt( new Date(), TimeZone.getDefault() ) );
		em.setSource( source );
		em.setDetail( detail );
		throwEmergency( em );
	}

	@Override
	public void throwDatabaseEmergency( String source, String detail ) {
		Emergency em = new Emergency();
		em.setLevel( EmergencyLevel.Error );
		em.setType( EmergencyType.DatabaseEmergency );
		em.setEmergencyTime( new Date() );
		em.setSource( source );
		em.setDetail( detail );
		throwEmergency( em );
	}

	@Override
	public Collection<Emergency> getEmergencysFromDB( EmergencyType type, Date start, Date end ) {
		Collection<Emergency> ems = new ArrayList<Emergency>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			StringBuilder sql = new StringBuilder( "select * from EmergencyRecord " );// (type,level,source,detail,time)
																						// values(?,?,?,?,?)
			if ( type != null || start != null || end != null ) {
				sql.append( "where " );
				if ( type != null )
					sql.append( "type=?" );
				if ( start != null ) {
					if ( sql.indexOf( "?" ) > 0 )
						sql.append( " and " );
					sql.append( "time>=?" );
				}
				if ( end != null ) {
					if ( sql.indexOf( "?" ) > 0 )
						sql.append( " and " );
					sql.append( "time<=?" );
				}
			}
			sql.append( " order by time desc" );
			ps = conn.prepareStatement( sql.toString() );
			int cnt = 1;
			if ( type != null || start != null || end != null ) {
				if ( type != null ) {
					ps.setString( cnt, type.name() );
					cnt++;
				}
				if ( start != null ) {
					ps.setTimestamp( cnt, CommonUtils.local2gmt( new Timestamp( start.getTime() ), TimeZone.getDefault() ) );
					cnt++;
				}
				if ( end != null ) {
					ps.setTimestamp( cnt, CommonUtils.local2gmt( new Timestamp( end.getTime() ), TimeZone.getDefault() ) );
					cnt++;
				}
			}
			rs = ps.executeQuery();
			while ( rs.next() ) {
				ems.add( encap( rs ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Save emergency event to db failed", ex );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return ems;
	}

	private Emergency encap( ResultSet rs ) throws SQLException {
		Emergency e = new Emergency();
		e.setType( EmergencyType.parse( rs.getString( "type" ) ) );
		e.setLevel( EmergencyLevel.parse( rs.getString( "level" ) ) );
		e.setDetail( rs.getString( "detail" ) );
		e.setSource( rs.getString( "source" ) );
		e.setEmergencyTime( CommonUtils.gmt2local( rs.getTimestamp( "time" ), TimeZone.getDefault() ) );
		return e;
	}

}
