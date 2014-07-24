package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.nokia.testingservice.austere.model.TestStation;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LogUtils;

public class StatusServiceImpl implements StatusService {

	private TestStation encap( ResultSet rs ) throws SQLException {
		TestStation ts = new TestStation();
		ts.setId( rs.getString( "id" ) );
		ts.setProduct( rs.getString( "product" ) );
		ts.setSw( rs.getString( "sw" ) );
		ts.setRemark( rs.getString( "remark" ) );
		ts.setFreezeCount( rs.getInt( "freeze" ) );
		ts.setResetCount( rs.getInt( "reboot" ) );
		ts.setStartTime( CommonUtils.gmt2local( rs.getTimestamp( "starttime" ), TimeZone.getDefault() ) );
		ts.setLastUpdate( CommonUtils.gmt2local( rs.getTimestamp( "lastupdate" ), TimeZone.getDefault() ) );
		ts.setExecutionId( rs.getString( "executionId" ));
		ts.setStationId( rs.getInt( "stationId" ) );
		ts.setStatus( rs.getString( "status" ) );
		ts.setSystemType( rs.getString( "systemType" ) );
		ts.setPassed( rs.getInt( "passed" ) );
		ts.setFailed( rs.getInt( "failed" ) );
		return ts;
	}

	@Override
	public TestStation[] getCurrentStationStatus( String siteName ) {
		List<TestStation> list = new ArrayList<TestStation>();
		Connection conn = null;
		Map<String, Connection> conns = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if ( siteName == null || siteName.trim().isEmpty() ) {
				conns = DbUtils.getMonitorConnections();
				for ( Connection con : conns.values() ) {
					try {
						ps = con.prepareStatement( "select id,executionId,stationId,status,systemType,product,sw,passed,failed,remark,freeze,reboot,starttime,lastupdate from executionStates order by lastupdate desc" );
						rs = ps.executeQuery();
						while ( rs.next() ) {
							TestStation ts = encap( rs );
							ts.setSiteName( siteName );
							list.add( ts );
						}
					} catch ( Exception ex ) {
						LogUtils.getServiceLog().error( "Get Current Station Status got error.", ex );
					} finally {
						CommonUtils.closeQuitely( rs );
						CommonUtils.closeQuitely( ps );
						CommonUtils.closeQuitely( con );
					}
				}
			} else {
				conn = DbUtils.getMonitorConnection( siteName );
				ps = conn.prepareStatement( "select id,executionId,stationId,status,systemType,product,sw,passed,failed,remark,freeze,reboot,starttime,lastupdate from executionStates order by lastupdate desc" );
				rs = ps.executeQuery();
				while ( rs.next() ) {
					TestStation ts = encap( rs );
					ts.setSiteName( siteName );
					list.add( ts );
				}
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return list.toArray( new TestStation[0] );
	}

	@Override
	public TestStation[] getCurrentStationStatus( String siteName, String productFilter, String taskFilter ) {
		String sql = "select id,executionId,stationId,status,systemType,product,sw,passed,failed,remark,freeze,reboot,starttime,lastupdate from executionStates where product=? ";
		String sql2 = "select distinct pcname from stations where stationId in (select stationid from task_stations where taskId=?)";
		String ext = " order by lastupdate desc";
		if ( productFilter == null || productFilter.trim().equals( "" ) ) {
			if ( taskFilter == null || taskFilter.trim().equals( "" ) ) 
					return getCurrentStationStatus( siteName );
		} else if ( taskFilter != null && !taskFilter.trim().equals( "" ) ) {
			sql += "and PcName in (select distinct(PcName) from Task_Stations where TaskID=" + taskFilter + " )";
		}
		List<TestStation> list = new ArrayList<TestStation>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Connection> cons = null;
		try {
			if ( siteName == null || siteName.trim().equals( "" ) ) {
				cons = DbUtils.getMonitorConnections();
				for ( String sn : cons.keySet() ) {
					try {
						conn = cons.get( sn );
						ps = conn.prepareStatement( "select id,executionId,stationId,status,systemType,product,sw,passed,failed,remark,freeze,reboot,starttime,lastupdate from executionStates order by lastupdate desc" );
						rs = ps.executeQuery();
						while ( rs.next() ) {
							TestStation ts = encap( rs );
							ts.setSiteName( siteName );
							list.add( ts );
						}
					} catch ( Exception ex ) {
						LogUtils.getServiceLog().error( "Get Current Station Status got error.", ex );
					} finally {
						CommonUtils.closeQuitely( rs );
						CommonUtils.closeQuitely( ps );
						CommonUtils.closeQuitely( conn );
					}
				}
			} else if( taskFilter != null && !taskFilter.trim().equals( "" ) ) {
				conn = DbUtils.getCentralConnection();
				ps = conn.prepareStatement(sql2);
				ps.setInt(1, CommonUtils.parseInt(taskFilter, 0));
				rs = ps.executeQuery();
				StringBuffer allPcs = new StringBuffer();
				while(rs.next()) {
					if(allPcs.length()>0)
						allPcs.append(",");
					allPcs.append("'").append(rs.getString(1)).append("'");
				}
				Connection conn2 = DbUtils.getMonitorConnection( siteName );
				PreparedStatement ps2 = conn2.prepareStatement("select id,executionId,stationId,status,systemType,product,sw,passed,failed,remark,freeze,reboot,starttime,lastupdate from executionStates where stationId in (select id from stationInfos where pcname in ("+allPcs.toString()+"))  order by lastupdate desc");
				ResultSet rs2= null;
				rs2 = ps2.executeQuery();
				while(rs2.next()) {
					TestStation ts = encap( rs2 );
					ts.setSiteName( siteName );
					list.add( ts );
				}
				rs2.close();
				ps2.close();
				conn2.close();
			} else {
				conn = DbUtils.getMonitorConnection( siteName );
				ps = conn.prepareStatement( sql + ext );
				ps.setString( 1, productFilter );
				rs = ps.executeQuery();
				while ( rs.next() ) {
					TestStation ts = encap( rs );
					ts.setSiteName( siteName );
					list.add( ts );
				}
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return list.toArray( new TestStation[0] );
	}

	public String[] getProducts() {
		List<String> list = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( "select ProductName from Products where Invalid=0 order by ProductName" );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				list.add( rs.getString( 1 ) );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return list.toArray( new String[0] );
	}
	
	public List<String> getProducts( String siteName ){
		List<String> products = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getMonitorConnection( siteName );
			String sql = "select * from productGroup order by groupName,product";
			ps = conn.prepareStatement( sql );
			rs = ps.executeQuery();
			while(rs.next()) {
				products.add( rs.getString( "product" ) );
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return products;
	}

	@Override
	public String getRecentSteps( String siteName, String product, String executionId, int limit ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Map<String,String>> steps = new ArrayList<Map<String,String>>();
		try{
			conn = DbUtils.getConnectionByProduct(siteName, product);
			String sql = "select top "+ limit +" r.TimeStamp as FinishedTime,g.GroupNo,c.CaseNo,s.StepNo,c.CaseDesc,s.StepDesc,r.ResultAsText as StepResult from Result r with (nolock) INNER JOIN TStep s with (nolock) on r.StepID=s.StepID INNER JOIN TCase c with (nolock) on c.CaseID=s.CaseID INNER JOIN TGroup g with (nolock) ON c.GroupID = g.GroupID INNER JOIN TestCondition t with (nolock) on t.ConditionID=r.ConditionID  WHERE t.ConditionID=? order by r.TimeStamp desc";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, CommonUtils.parseInt(executionId, 0));
			rs = ps.executeQuery();
			while(rs.next()){
				Map<String,String> step = new HashMap<String,String>();
				step.put("FinishedTime", CommonUtils.gmt2local( rs.getTimestamp("FinishedTime"), TimeZone.getDefault() ).toString() );
				step.put("GroupNo", rs.getString("GroupNo") );
				step.put("CaseNo", rs.getString("CaseNo") );
				step.put("StepNo", rs.getString("StepNo") );
				step.put("CaseDesc", rs.getString("CaseDesc") );
				step.put("StepDesc", rs.getString("StepDesc") );
				step.put("StepResult", rs.getString("StepResult") );
				steps.add(step);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			CommonUtils.closeQuitely( rs );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
		return CommonUtils.toJson(steps);
	}
}
