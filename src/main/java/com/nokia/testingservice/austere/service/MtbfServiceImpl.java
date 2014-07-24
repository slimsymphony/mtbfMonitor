package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.ReportMeta;
import com.nokia.testingservice.austere.model.MTBFDetail;
import com.nokia.testingservice.austere.model.MtbfData;
import com.nokia.testingservice.austere.model.Result;
import com.nokia.testingservice.austere.model.ReportMeta.Status;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LogUtils;

public class MtbfServiceImpl implements MtbfService {

	@Override
	public Collection<MTBFDetail> getMtbfDetails( String siteName, String product ) {
		return getMtbfDetails( CommonUtils.getCurrentDay(), siteName, product );
	}

	private MTBFDetail encap( ResultSet rs ) throws SQLException {
		MTBFDetail md = new MTBFDetail();
		md.setSeq( rs.getInt( "Seq" ) );
		md.setStationID( rs.getInt( "StationID" ) );
		md.setReportDate( rs.getInt( "ReportDate" ) );
		md.setType( rs.getString( "Event" ) );
		md.setAmount( rs.getInt( "Amount" ) );
		md.setErrorID( rs.getString( "ErrorID" ) );
		md.setDesc( rs.getString( "Desciption" ) );
		md.setStatus( rs.getString( "Status" ) );
		md.setIsKnown( rs.getInt( "IsKnown" ) );
		return md;
	}

	@Override
	public Collection<MTBFDetail> getMtbfDetails( int date, String siteName, String product ) {
		Collection<MTBFDetail> mds = new ArrayList<MTBFDetail>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from MtbfDailyStatus where ReportDate=? order by StationID" );
			ps.setInt( 1, date );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				mds.add( encap( rs ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get MTBF details failed.", ex );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return mds;
	}

	@Override
	public Collection<MTBFDetail> getWeekMtbfDetails( int week, String siteName, String product ){
		Collection<MTBFDetail> mds = new ArrayList<MTBFDetail>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select * from MtbfDailyStatus where ReportDate between ? and ? order by StationID" );
			ps.setInt( 1, week*100 );
			ps.setInt( 2, week*100 + 7 );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				mds.add( encap( rs ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get MTBF details failed.", ex );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return mds;
	}
	
	@Override
	public int addMTBFDetail( MTBFDetail md ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int seq = 0;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "insert into MtbfDailyStatus(StationID,ReportDate,Event,Amount,ErrorID,Desciption,Status,IsKnown) values(?,?,?,?,?,?,?,?)" );
			ps.setInt( 1, md.getStationID() );
			ps.setInt( 2, md.getReportDate() );
			ps.setString( 3, md.getType() );
			ps.setInt( 4, md.getAmount() );
			ps.setString( 5, md.getErrorID() );
			ps.setString( 6, md.getDesc() );
			ps.setString( 7, md.getStatus() );
			ps.setInt( 8, md.getIsKnown() );
			ps.executeUpdate();
			ps.close();
			ps = conn.prepareStatement( "select DISTINCT @@IDENTITY as iden from MtbfDailyStatus" );
			rs = ps.executeQuery();
			rs.next();
			seq = rs.getInt( 1 );
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Add MTBF failed.MTBFDetail=" + md, ex );
			throw new ServiceException( "Add MTBF failed.MTBFDetail=" + md, ex );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return seq;
	}

	@Override
	public void updateMTBFDetail( MTBFDetail md ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "update MtbfDailyStatus set StationID=?,ReportDate=?,Event=?,Amount=?,ErrorID=?,Desciption=?,Status=?,IsKnown=? where Seq=?" );
			ps.setInt( 1, md.getStationID() );
			ps.setInt( 2, md.getReportDate() );
			ps.setString( 3, md.getType() );
			ps.setInt( 4, md.getAmount() );
			ps.setString( 5, md.getErrorID() );
			ps.setString( 6, md.getDesc() );
			ps.setString( 7, md.getStatus() );
			ps.setInt( 8, md.getIsKnown() );
			ps.setInt( 9, md.getSeq() );
			ps.executeUpdate();
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Update MTBF failed.MTBFDetail=" + md, ex );
			throw new ServiceException( "Update MTBF failed.MTBFDetail=" + md, ex );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}

	}

	@Override
	public void deleteMTBFDetail( int seq ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "delete from MtbfDailyStatus where Seq=?" );
			ps.setInt( 1, seq );
			ps.executeUpdate();
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Delete MTBF failed. Seq=" + seq, ex );
			throw new ServiceException( "Delete MTBF failed. Seq=" + seq, ex );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	public List<ReportMeta> getDailyReportMetasByProduct( String siteName, String product, Date date ) {
		if ( date == null )
			date = new Date();
		// date = CommonUtils.local2gmt( date, TimeZone.getDefault() );
		List<ReportMeta> rms = new ArrayList<ReportMeta>();
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps5 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		try {
			conn = DbUtils.getConnectionByProduct( siteName, product );
			String sql = "select distinct(ConditionID) from Result where  (TimeStamp between ? and ?) and ConditionID in (select ConditionID from TestCondition where deviceID in (select deviceID from device where DUTTYPE like ?))";
			String sql2 = "select tc.languageID,d.SerialNo,d.SWVer,d.HWver,s.TestSystem,s.PCHostname, a.ProductName, tc.StatusIRP"
					+ " from TestCondition tc,Device d, System s, tArea a"
					+ " where tc.ConditionID=? and d.DeviceID=tc.DeviceID and s.SystemID=d.SystemID and a.AreaNo = tc.AreaNo";
			String sql3 = "select max(Timestamp),min(Timestamp) from Result where conditionID=?";
			String sql4 = "select count(1) from Result where conditionID=? and stepid in (select stepid from TStep where caseid in (select caseID from TCase where GroupID in (select GroupID from TGroup where groupno=99) and caseno=2))  and (Timestamp between ? and ?) and resultAsText=?";
			String sql5 = "select Reboot,freeze from executionStates where pcname=? and starttime<?";
			LogUtils.getDbLog().info( sql );
			LogUtils.getDbLog().info( sql2 );
			LogUtils.getDbLog().info( sql3 );
			LogUtils.getDbLog().info( sql4 );
			Calendar cal = Calendar.getInstance();
			ps = conn.prepareStatement( sql );
			cal.setTime( date );
			cal.set( Calendar.AM_PM, Calendar.AM );
			cal.set( Calendar.HOUR, 0 );
			cal.set( Calendar.MINUTE, 0 );
			cal.set( Calendar.SECOND, 0 );
			cal.set( Calendar.MILLISECOND, 0 );
			Timestamp start = new Timestamp( cal.getTime().getTime() );
			ps.setTimestamp( 1, start );
			cal.set( Calendar.AM_PM, Calendar.PM );
			cal.set( Calendar.HOUR, 11 );
			cal.set( Calendar.MINUTE, 59 );
			cal.set( Calendar.SECOND, 59 );
			cal.set( Calendar.MILLISECOND, 999 );
			Timestamp end = new Timestamp( cal.getTime().getTime() );
			ps.setTimestamp( 2, end );
			ps.setString( 3, product );
			ps2 = conn.prepareStatement( sql2 );
			ps3 = conn.prepareStatement( sql3 );
			ps4 = conn.prepareStatement( sql4 );
			ps5 = conn.prepareStatement( sql5 );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				int conditionID = rs.getInt( 1 );
				// get meta
				ReportMeta drm = new ReportMeta();
				ps2.setInt( 1, conditionID );
				rs2 = ps2.executeQuery();
				rs2.next();
				drm.setDate( CommonUtils.getDay( date ) );
				drm.setLanPackage( transLan( rs2.getInt( 1 ) ) );
				drm.setIMEI( rs2.getString( 2 ) );
				drm.setSwVersion( rs2.getString( 3 ) );
				drm.setHwVersion( rs2.getString( 4 ) );
				drm.setTestSystem( rs2.getString( 6 ) );
				drm.setProductName( rs2.getString( 7 ) );
				drm.setStatus( Status.parse( rs2.getString( 8 ) ) );
				rs2.close();
				ps2.clearParameters();
				// get time info
				ps3.setInt( 1, conditionID );
				ps3.executeQuery();
				rs3 = ps3.executeQuery();
				rs3.next();
				Timestamp max = CommonUtils.gmt2local( rs3.getTimestamp( 1 ), TimeZone.getDefault() );
				Timestamp min = CommonUtils.gmt2local( rs3.getTimestamp( 2 ), TimeZone.getDefault() );
				float interval = ( float ) ( ( float ) ( max.getTime() - min.getTime() ) / ( float ) ( 1000L * 60L * 60L ) );
				String inter = String.valueOf( interval );
				if ( inter.length() > 3 )
					inter = inter.substring( 0, 4 );
				drm.setTotalRuntime( inter + "  (" + min + "~" + max + ")" );
				drm.setResets( getResetCount( ps5, start, end, drm.getTestSystem() ) );
				// TODO: this part need to be update after invalid time calculate function clear.
				drm.setInvalidTime( getInvalidTime() );
				drm.setMtbfIdx( getMtbf( interval, drm.getInvalidTime(), drm.getResets(), drm.getFreezes() ) );
				rs3.close();
				ps3.clearParameters();
				// get reset/freeze
				ps4.setInt( 1, conditionID );
				ps4.setTimestamp( 2, start );
				ps4.setTimestamp( 3, end );
				rs4 = ps4.executeQuery();
				Collection<Result> results = new ArrayList<Result>();
				int freezeCount = 0;
				if ( rs4.next() )
					freezeCount = rs4.getInt( 1 );
				/*
				 * while ( rs4.next() ) { Result result = new Result(); result.setConditionID( rs4.getInt( "ConditionID"
				 * ) ); result.setResult( rs4.getInt( "Result" ) ); result.setResultAsText( rs4.getString(
				 * "ResultAsText" ) ); result.setResultID( rs4.getInt( "ResultID" ) ); result.setStepID( rs4.getString(
				 * "StepID" ) ); result.setTimeStamp( rs4.getTimestamp( "TimeStamp" ) ); results.add( result ); if (
				 * rs4.getString( "ResultAsText" ).equalsIgnoreCase( "DEAD" ) ) { freezeCount++; } }
				 */
				rs4.close();
				ps4.clearParameters();
				drm.setFreezes( freezeCount );
				// calculate MTBF index

				// end
				rms.add( drm );
			}

		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get Daily Report Metadata failed, date=" + date, e );
		} finally {
			if ( rs4 != null )
				CommonUtils.closeQuitely( rs4 );
			if ( rs3 != null )
				CommonUtils.closeQuitely( rs3 );
			if ( rs2 != null )
				CommonUtils.closeQuitely( rs2 );
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps5 != null )
				CommonUtils.closeQuitely( ps5 );
			if ( ps4 != null )
				CommonUtils.closeQuitely( ps4 );
			if ( ps3 != null )
				CommonUtils.closeQuitely( ps3 );
			if ( ps2 != null )
				CommonUtils.closeQuitely( ps2 );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return rms;
	}

	/*
	 * Currently, invalid time, resets,freezes not available.
	 * @see com.nokia.testingservice.austere.service.MtbfService#getDailyReportMetas(java.util.Date)
	 */
	@Override
	public List<ReportMeta> getDailyReportMetas( String siteName, String instanceName, Date date ) {
		if ( date == null )
			date = new Date();
		// date = CommonUtils.local2gmt( date, TimeZone.getDefault() );
		List<ReportMeta> rms = new ArrayList<ReportMeta>();
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps5 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		try {
			conn = DbUtils.getConnectionByProduct( siteName, instanceName );
			String sql = "select distinct(ConditionID) from Result where TimeStamp between ? and ?";
			String sql2 = "select tc.languageID,d.SerialNo,d.SWVer,d.HWver,s.TestSystem,s.PCHostname, a.ProductName, tc.StatusIRP"
					+ " from TestCondition tc,Device d, System s, tArea a"
					+ " where tc.ConditionID=? and d.DeviceID=tc.DeviceID and s.SystemID=d.SystemID and a.AreaNo = tc.AreaNo";
			String sql3 = "select max(Timestamp),min(Timestamp) from Result where conditionID=?";
			/*
			 * String sql4 = "select ConditionID,Result,ResultAsText,TimeStamp,ResultID,StepID from Result where" +
			 * " conditionID=?" +
			 * " and stepid in (select stepid from TStep where caseid in (select caseID from TCase where GroupID in (select GroupID from TGroup where groupno=99) and caseno=2))"
			 * + " and Timestamp between ? and ? order by TimeStamp";
			 */
			String sql4 = "select count(1) from Result where conditionID=? and stepid in (select stepid from TStep where caseid in (select caseID from TCase where GroupID in (select GroupID from TGroup where groupno=99) and caseno=2))  and (Timestamp between ? and ?) and resultAsText=?";
			String sql5 = "select Reboot,freeze from executionStates where pcname=? and starttime<?";
			LogUtils.getDbLog().info( sql );
			LogUtils.getDbLog().info( sql2 );
			LogUtils.getDbLog().info( sql3 );
			LogUtils.getDbLog().info( sql4 );
			Calendar cal = Calendar.getInstance();
			ps = conn.prepareStatement( sql );
			cal.setTime( date );
			cal.set( Calendar.AM_PM, Calendar.AM );
			cal.set( Calendar.HOUR, 0 );
			cal.set( Calendar.MINUTE, 0 );
			cal.set( Calendar.SECOND, 0 );
			cal.set( Calendar.MILLISECOND, 0 );
			Timestamp start = new Timestamp( cal.getTime().getTime() );
			ps.setTimestamp( 1, start );
			cal.set( Calendar.AM_PM, Calendar.PM );
			cal.set( Calendar.HOUR, 11 );
			cal.set( Calendar.MINUTE, 59 );
			cal.set( Calendar.SECOND, 59 );
			cal.set( Calendar.MILLISECOND, 999 );
			Timestamp end = new Timestamp( cal.getTime().getTime() );
			ps.setTimestamp( 2, end );
			ps2 = conn.prepareStatement( sql2 );
			ps3 = conn.prepareStatement( sql3 );
			ps4 = conn.prepareStatement( sql4 );
			ps5 = conn.prepareStatement( sql5 );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				int conditionID = rs.getInt( 1 );
				// get meta
				ReportMeta drm = new ReportMeta();
				ps2.setInt( 1, conditionID );
				rs2 = ps2.executeQuery();
				rs2.next();
				drm.setLanPackage( transLan( rs2.getInt( 1 ) ) );
				drm.setIMEI( rs2.getString( 2 ) );
				drm.setSwVersion( rs2.getString( 3 ) );
				drm.setHwVersion( rs2.getString( 4 ) );
				drm.setTestSystem( rs2.getString( 6 ) );
				drm.setProductName( rs2.getString( 7 ) );
				drm.setStatus( Status.parse( rs2.getString( 8 ) ) );
				rs2.close();
				ps2.clearParameters();
				// get time info
				ps3.setInt( 1, conditionID );
				ps3.executeQuery();
				rs3 = ps3.executeQuery();
				rs3.next();
				Timestamp max = CommonUtils.gmt2local( rs3.getTimestamp( 1 ), TimeZone.getDefault() );
				Timestamp min = CommonUtils.gmt2local( rs3.getTimestamp( 2 ), TimeZone.getDefault() );
				float interval = ( float ) ( ( float ) ( max.getTime() - min.getTime() ) / ( float ) ( 1000L * 60L * 60L ) );
				String inter = String.valueOf( interval );
				if ( inter.length() > 3 )
					inter = inter.substring( 0, 4 );
				drm.setTotalRuntime( inter + "  (" + min + "~" + max + ")" );
				drm.setResets( getResetCount( ps5, start, end, drm.getTestSystem() ) );
				// TODO: this part need to be update after invalid time calculate function clear.
				drm.setInvalidTime( getInvalidTime() );
				drm.setMtbfIdx( getMtbf( interval, drm.getInvalidTime(), drm.getResets(), drm.getFreezes() ) );
				rs3.close();
				ps3.clearParameters();
				// get reset/freeze
				ps4.setInt( 1, conditionID );
				ps4.setTimestamp( 2, start );
				ps4.setTimestamp( 3, end );
				rs4 = ps4.executeQuery();
				Collection<Result> results = new ArrayList<Result>();
				int freezeCount = 0;
				if ( rs4.next() )
					freezeCount = rs4.getInt( 1 );
				/*
				 * while ( rs4.next() ) { Result result = new Result(); result.setConditionID( rs4.getInt( "ConditionID"
				 * ) ); result.setResult( rs4.getInt( "Result" ) ); result.setResultAsText( rs4.getString(
				 * "ResultAsText" ) ); result.setResultID( rs4.getInt( "ResultID" ) ); result.setStepID( rs4.getString(
				 * "StepID" ) ); result.setTimeStamp( rs4.getTimestamp( "TimeStamp" ) ); results.add( result ); if (
				 * rs4.getString( "ResultAsText" ).equalsIgnoreCase( "DEAD" ) ) { freezeCount++; } }
				 */
				rs4.close();
				ps4.clearParameters();
				drm.setFreezes( freezeCount );
				// calculate MTBF index

				// end
				rms.add( drm );
			}

		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get Daily Report Metadata failed, date=" + date, e );
		} finally {
			if ( rs4 != null )
				CommonUtils.closeQuitely( rs4 );
			if ( rs3 != null )
				CommonUtils.closeQuitely( rs3 );
			if ( rs2 != null )
				CommonUtils.closeQuitely( rs2 );
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps5 != null )
				CommonUtils.closeQuitely( ps5 );
			if ( ps4 != null )
				CommonUtils.closeQuitely( ps4 );
			if ( ps3 != null )
				CommonUtils.closeQuitely( ps3 );
			if ( ps2 != null )
				CommonUtils.closeQuitely( ps2 );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return rms;
	}

	private int getResetCount( PreparedStatement ps, Timestamp start, Timestamp end, String testSystem ) throws SQLException {
		ResultSet rs = null;
		int resetCnt = 0;
		try {
			ps.setString( 1, testSystem );
			ps.setTimestamp( 2, start );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				resetCnt = rs.getInt( 1 );
			}
		} finally {
			CommonUtils.closeQuitely( rs );
			if ( ps != null ) {
				ps.clearParameters();
			}
		}
		return resetCnt;
	}

	private float getInvalidTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * TODO: this function need to be updated after evan update the ExecuteState data structure.
	 * 
	 * @param conn
	 * @param start
	 * @param end
	 * @param testSystem
	 * @return
	 * @throws SQLException
	 */
	private int getResetCount( Connection conn, Timestamp start, Timestamp end, String testSystem ) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		int resetCnt = 0;
		try {
			ps = conn.prepareStatement( "select Reboot,freeze from executionStates where pcname=? and starttime<? " );
			ps.setString( 1, testSystem );
			ps.setTimestamp( 2, start );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				resetCnt = rs.getInt( 1 );
			}
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
		}
		return resetCnt;
	}

	/**
	 * Need these params to calculate.
	 * 
	 * @param totalRuntime
	 * @param invalidTime
	 * @param resets
	 * @param freezes
	 * @return
	 */
	private String getMtbf( float totalRuntime, float invalidTime, int resets, int freezes ) {
		float mtbfIndex = ( totalRuntime - invalidTime ) / Math.max( 1, resets + freezes );
		if ( String.valueOf( mtbfIndex ).length() > 5 )
			return String.valueOf( mtbfIndex ).substring( 0, 5 );
		else
			return String.valueOf( mtbfIndex );
	}

	private String transLan( int lan ) {
		switch ( lan ) {
			case 7:
				return "Chinese";
		}
		return null;
	}

	@Override
	public List<ReportMeta> getWeeklyReportMetas( String siteName, String instanceName, int week ) {
		if ( week < 1200 )
			week = CommonUtils.getCurrentWk();
		Date[] dates = CommonUtils.getIntervalFromWeek( week );
		// date = CommonUtils.local2gmt( date, TimeZone.getDefault() );
		List<ReportMeta> rms = new ArrayList<ReportMeta>();
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps5 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		try {
			conn = DbUtils.getConnection( siteName, instanceName );
			String sql = "select distinct(ConditionID) from Result where TimeStamp between ? and ?";
			String sql2 = "select tc.languageID,d.SerialNo,d.SWVer,d.HWver,s.TestSystem,s.PCHostname, a.ProductName, tc.StatusIRP"
					+ " from TestCondition tc,Device d, System s, tArea a"
					+ " where tc.ConditionID=? and d.DeviceID=tc.DeviceID and s.SystemID=d.SystemID and a.AreaNo = tc.AreaNo";
			String sql3 = "select max(Timestamp),min(Timestamp) from Result where conditionID=?";
			String sql4 = "select ConditionID,Result,ResultAsText,TimeStamp,ResultID,StepID from Result where"
					+ " conditionID=?"
					+ " and stepid in (select stepid from TStep where caseid in (select caseID from TCase where GroupID in (select GroupID from TGroup where groupno=99) and caseno=2))"
					+ " and Timestamp between ? and ? order by TimeStamp";
			String sql5 = "select Reboot,freeze from executionStates where pcname=? and starttime<?";
			LogUtils.getDbLog().info( sql );
			LogUtils.getDbLog().info( sql2 );
			LogUtils.getDbLog().info( sql3 );
			LogUtils.getDbLog().info( sql4 );
			ps = conn.prepareStatement( sql );

			Timestamp start = new Timestamp( dates[0].getTime() );
			ps.setTimestamp( 1, start );
			Timestamp end = new Timestamp( dates[1].getTime() );
			ps.setTimestamp( 2, end );
			ps2 = conn.prepareStatement( sql2 );
			ps3 = conn.prepareStatement( sql3 );
			ps4 = conn.prepareStatement( sql4 );
			ps5 = conn.prepareStatement( sql5 );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				int conditionID = rs.getInt( 1 );
				// get meta
				ReportMeta drm = new ReportMeta();
				ps2.setInt( 1, conditionID );
				rs2 = ps2.executeQuery();
				rs2.next();
				drm.setLanPackage( transLan( rs2.getInt( 1 ) ) );
				drm.setIMEI( rs2.getString( 2 ) );
				drm.setSwVersion( rs2.getString( 3 ) );
				drm.setHwVersion( rs2.getString( 4 ) );
				drm.setTestSystem( rs2.getString( 6 ) );
				drm.setProductName( rs2.getString( 7 ) );
				drm.setStatus( Status.parse( rs2.getString( 8 ) ) );
				rs2.close();
				ps2.clearParameters();
				// get time info
				ps3.setInt( 1, conditionID );
				ps3.executeQuery();
				rs3 = ps3.executeQuery();
				rs3.next();
				Timestamp max = CommonUtils.gmt2local( rs3.getTimestamp( 1 ), TimeZone.getDefault() );
				Timestamp min = CommonUtils.gmt2local( rs3.getTimestamp( 2 ), TimeZone.getDefault() );
				float interval = ( float ) ( ( float ) ( max.getTime() - min.getTime() ) / ( float ) ( 1000L * 60L * 60L ) );
				String inter = String.valueOf( interval );
				if ( inter.length() > 3 )
					inter = inter.substring( 0, 4 );
				drm.setTotalRuntime( inter + "  (" + min + "~" + max + ")" );
				drm.setResets( getResetCount( ps5, start, end, drm.getTestSystem() ) );
				// TODO: this part need to be update after invalid time calculate function clear.
				drm.setInvalidTime( getInvalidTime() );
				drm.setMtbfIdx( getMtbf( interval, drm.getInvalidTime(), drm.getResets(), drm.getFreezes() ) );
				rs3.close();
				ps3.clearParameters();
				// get reset/freeze
				ps4.setInt( 1, conditionID );
				ps4.setTimestamp( 2, start );
				ps4.setTimestamp( 3, end );
				rs4 = ps4.executeQuery();
				Collection<Result> results = new ArrayList<Result>();
				int freezeCount = 0;
				if ( rs4.next() )
					freezeCount = rs4.getInt( 1 );
				/*
				 * while ( rs4.next() ) { Result result = new Result(); result.setConditionID( rs4.getInt( "ConditionID"
				 * ) ); result.setResult( rs4.getInt( "Result" ) ); result.setResultAsText( rs4.getString(
				 * "ResultAsText" ) ); result.setResultID( rs4.getInt( "ResultID" ) ); result.setStepID( rs4.getString(
				 * "StepID" ) ); result.setTimeStamp( rs4.getTimestamp( "TimeStamp" ) ); results.add( result ); if (
				 * rs4.getString( "ResultAsText" ).equalsIgnoreCase( "DEAD" ) ) { freezeCount++; } }
				 */
				rs4.close();
				ps4.clearParameters();
				drm.setFreezes( freezeCount );
				// calculate MTBF index

				// end
				rms.add( drm );
			}

		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get Weekly Report Metadata failed, Week=" + week, e );
		} finally {
			if ( rs4 != null )
				CommonUtils.closeQuitely( rs4 );
			if ( rs3 != null )
				CommonUtils.closeQuitely( rs3 );
			if ( rs2 != null )
				CommonUtils.closeQuitely( rs2 );
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps5 != null )
				CommonUtils.closeQuitely( ps5 );
			if ( ps4 != null )
				CommonUtils.closeQuitely( ps4 );
			if ( ps3 != null )
				CommonUtils.closeQuitely( ps3 );
			if ( ps2 != null )
				CommonUtils.closeQuitely( ps2 );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return rms;
	}

	@Override
	public List<ReportMeta> getWeeklyReportMetasByProduct( String siteName, String product, int week ) {
		if ( week < 1200 )
			week = CommonUtils.getCurrentWk();
		Date[] dates = CommonUtils.getIntervalFromWeek( week );
		// date = CommonUtils.local2gmt( date, TimeZone.getDefault() );
		List<ReportMeta> rms = new ArrayList<ReportMeta>();
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps5 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		ResultSet rs4 = null;
		try {
			conn = DbUtils.getConnectionByProduct( siteName, product );
			String sql = "select distinct(ConditionID) from Result where (TimeStamp between ? and ?) and ConditionID in (select ConditionID from TestCondition where deviceID in (select deviceID from device where DUTTYPE like ?))";
			String sql2 = "select tc.languageID,d.SerialNo,d.SWVer,d.HWver,s.TestSystem,s.PCHostname, a.ProductName, tc.StatusIRP"
					+ " from TestCondition tc,Device d, System s, tArea a"
					+ " where tc.ConditionID=? and d.DeviceID=tc.DeviceID and s.SystemID=d.SystemID and a.AreaNo = tc.AreaNo";
			String sql3 = "select max(Timestamp),min(Timestamp) from Result where conditionID=?";
			String sql4 = "select count(1) from Result where conditionID=? and stepid in (select stepid from TStep where caseid in (select caseID from TCase where GroupID in (select GroupID from TGroup where groupno=99) and caseno=2))  and (Timestamp between ? and ?) and resultAsText=?";
			String sql5 = "select Reboot,freeze from executionStates where pcname=? and starttime<?";
			LogUtils.getDbLog().info( sql );
			LogUtils.getDbLog().info( sql2 );
			LogUtils.getDbLog().info( sql3 );
			LogUtils.getDbLog().info( sql4 );
			ps = conn.prepareStatement( sql );
			Timestamp start = new Timestamp( dates[0].getTime() );
			ps.setTimestamp( 1, start );
			Timestamp end = new Timestamp( dates[1].getTime() );
			ps.setTimestamp( 2, end );
			ps.setString( 3, product );
			ps2 = conn.prepareStatement( sql2 );
			ps3 = conn.prepareStatement( sql3 );
			ps4 = conn.prepareStatement( sql4 );
			ps5 = conn.prepareStatement( sql5 );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				int conditionID = rs.getInt( 1 );
				// get meta
				ReportMeta drm = new ReportMeta();
				ps2.setInt( 1, conditionID );
				rs2 = ps2.executeQuery();
				rs2.next();
				drm.setLanPackage( transLan( rs2.getInt( 1 ) ) );
				drm.setIMEI( rs2.getString( 2 ) );
				drm.setSwVersion( rs2.getString( 3 ) );
				drm.setHwVersion( rs2.getString( 4 ) );
				drm.setTestSystem( rs2.getString( 6 ) );
				drm.setProductName( rs2.getString( 7 ) );
				drm.setStatus( Status.parse( rs2.getString( 8 ) ) );
				rs2.close();
				ps2.clearParameters();
				// get time info
				ps3.setInt( 1, conditionID );
				ps3.executeQuery();
				rs3 = ps3.executeQuery();
				rs3.next();
				Timestamp max = CommonUtils.gmt2local( rs3.getTimestamp( 1 ), TimeZone.getDefault() );
				Timestamp min = CommonUtils.gmt2local( rs3.getTimestamp( 2 ), TimeZone.getDefault() );
				float interval = ( float ) ( ( float ) ( max.getTime() - min.getTime() ) / ( float ) ( 1000L * 60L * 60L ) );
				String inter = String.valueOf( interval );
				if ( inter.length() > 3 )
					inter = inter.substring( 0, 4 );
				drm.setTotalRuntime( inter + "  (" + min + "~" + max + ")" );
				drm.setResets( getResetCount( ps5, start, end, drm.getTestSystem() ) );
				// TODO: this part need to be update after invalid time calculate function clear.
				drm.setInvalidTime( getInvalidTime() );
				drm.setMtbfIdx( getMtbf( interval, drm.getInvalidTime(), drm.getResets(), drm.getFreezes() ) );
				rs3.close();
				ps3.clearParameters();
				// get reset/freeze
				ps4.setInt( 1, conditionID );
				ps4.setTimestamp( 2, start );
				ps4.setTimestamp( 3, end );
				ps4.setString( 4, "DEAD" );
				rs4 = ps4.executeQuery();
				Collection<Result> results = new ArrayList<Result>();
				int freezeCount = 0;
				if ( rs4.next() )
					freezeCount = rs4.getInt( 1 );
				/*
				 * while ( rs4.next() ) { Result result = new Result(); result.setConditionID( rs4.getInt( "ConditionID"
				 * ) ); result.setResult( rs4.getInt( "Result" ) ); result.setResultAsText( rs4.getString(
				 * "ResultAsText" ) ); result.setResultID( rs4.getInt( "ResultID" ) ); result.setStepID( rs4.getString(
				 * "StepID" ) ); result.setTimeStamp( rs4.getTimestamp( "TimeStamp" ) ); results.add( result ); if (
				 * rs4.getString( "ResultAsText" ).equalsIgnoreCase( "DEAD" ) ) { freezeCount++; } }
				 */
				rs4.close();
				ps4.clearParameters();
				drm.setFreezes( freezeCount );
				// calculate MTBF index

				// end
				rms.add( drm );
			}

		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get Weekly Report Metadata failed, Week=" + week, e );
		} finally {
			if ( rs4 != null )
				CommonUtils.closeQuitely( rs4 );
			if ( rs3 != null )
				CommonUtils.closeQuitely( rs3 );
			if ( rs2 != null )
				CommonUtils.closeQuitely( rs2 );
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps5 != null )
				CommonUtils.closeQuitely( ps5 );
			if ( ps4 != null )
				CommonUtils.closeQuitely( ps4 );
			if ( ps3 != null )
				CommonUtils.closeQuitely( ps3 );
			if ( ps2 != null )
				CommonUtils.closeQuitely( ps2 );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return rms;
	}

	@Override
	public List<MtbfData> transfer2MtbfData( List<ReportMeta> rms ) {
		List<MtbfData> mds = new ArrayList<MtbfData>( rms.size() );
		for ( ReportMeta rm : rms ) {
			MtbfData md = new MtbfData();
			md.setMtbfIdx( CommonUtils.parseFloat( rm.getMtbfIdx(), 0.0f ) );
			md.setResetAndFreeze( rm.getFreezes() + rm.getResets() );
			md.setSoftVersion( rm.getSwVersion() );
			md.setTargetMtbf( getMtbfTarget( rm.getProductName(), rm.getDate() ) );
			mds.add( md );
		}
		return mds;
	}

	private float getMtbfTarget( String product, int date ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		float mtbfTarget = 0f;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select target from mtbfTargets where productName=? and setDate<=? order by setDate desc " );
			ps.setString( 1, product );
			ps.setInt( 2, date );
			rs = ps.executeQuery();
			if ( rs.next() ) {
				mtbfTarget = rs.getFloat( 1 );
			} else {
				rs.close();
				ps.close();
				ps = conn.prepareStatement( "select target from mtbfTargets where productName=? order by setDate desc " );
				ps.setString( 1, product );
				rs = ps.executeQuery();
				if ( rs.next() ) {
					mtbfTarget = rs.getFloat( 1 );
				}
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get Mtbf Tartget failed, date=" + date, ex );
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return mtbfTarget;
	}

}
