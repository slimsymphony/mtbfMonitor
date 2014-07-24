package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;

import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LogUtils;

public class UsageAnalyzer {
	
	public static void main(String[] args) {
		DateTime s = new DateTime( 2013, 3, 28, 0, 0 );
		DateTime e = new DateTime( 2013, 4, 28, 0, 0 );
		Map<String, Integer> map  = getStationUsageByProduct("922","Aqua",s,e);
		for(Entry<String,Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		System.out.println("----------------------------------------------");
		map  = getStationUsage("922",s,e);
		for(Entry<String,Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
		System.out.println("----------------------------------------------");
		map  = getExecutionOnProduct("922",s,e);
		for(Entry<String,Integer> entry : map.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
	}
	
	public static Map<String, Integer> getStationUsage( String siteName, DateTime s, DateTime e ){
		DateTime curr = new DateTime(s);
		DateTime next = s.plusDays( 1 );
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getMonitorConnection( siteName );
			String sql = "select count( distinct stationid) from (select * from executionStates union select * from executionStates_history) as A where starttime<? and lastupdate>?";
			ps = conn.prepareStatement( sql );
			while( curr.isBefore( e ) || curr.equals( e )) {
				Timestamp start = new Timestamp(curr.getMillis());
				Timestamp end = new Timestamp(next.getMillis());
				ps.setTimestamp( 1, end );
				ps.setTimestamp( 2, start );
				rs = ps.executeQuery();
				rs.next();
				map.put(curr.toString( "yyyy-MM-dd" ),rs.getInt( 1 ));
				curr = curr.plusDays( 1 );
				next = next.plusDays( 1 );
			}
		}catch(Exception ex) {
			LogUtils.getServiceLog().error( "Get Station Usage stat failed. siteName="+siteName+",start:"+s+",end:"+e, ex );
		}finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}
	
	public static Map<String, Integer> getStationUsageByProduct( String siteName, String product, DateTime s, DateTime e ){
		DateTime curr = new DateTime(s);
		DateTime next = s.plusDays( 1 );
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getMonitorConnection( siteName );
			String sql = "select count( distinct stationid) from executionStates where product=? and starttime<? and lastupdate>?";
			ps = conn.prepareStatement( sql );
			while( curr.isBefore( e ) || curr.equals( e )) {
				Timestamp start = new Timestamp(curr.getMillis());
				Timestamp end = new Timestamp(next.getMillis());
				ps.setString( 1, product );
				ps.setTimestamp( 2, end );
				ps.setTimestamp( 3, start );
				rs = ps.executeQuery();
				rs.next();
				map.put(curr.toString( "yyyy-MM-dd" ),rs.getInt( 1 ));
				curr = curr.plusDays( 1 );
				next = next.plusDays( 1 );
			}
		}catch(Exception ex) {
			LogUtils.getServiceLog().error( "Get Station Usage stat failed. siteName="+siteName+",start:"+s+",end:"+e, ex );
		}finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}
	
	public static Map<String, Integer> getStationUsageByTask( String siteName, int taskId, DateTime s, DateTime e ){
		DateTime curr = new DateTime(s);
		DateTime next = s.plusDays( 1 );
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		Connection conn = null;
		Connection conn2 = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			conn2 = DbUtils.getCentralConnection();
			String sql2 = "select distinct pcname from stations where sitename=?";
			if(taskId>0)
				sql2+=" and stationId in (select stationid from task_stations where taskId=?)";
			ps2 = conn2.prepareStatement(sql2);
			ps2.setString(1, siteName);
			if(taskId>0)
				ps2.setInt(2, taskId);
			rs2 = ps2.executeQuery();
			StringBuffer allPcs = new StringBuffer(200); 
			while(rs2.next()) {
				if(allPcs.length()>0)
					allPcs.append(",");
				allPcs.append("'").append(rs2.getString(1)).append("'");
			}
			conn = DbUtils.getMonitorConnection( siteName );
			String sql = "select count( distinct stationid) from (select * from executionStates union select * from executionStates_history) as A where stationId in ( select id from stationInfos where pcname in ("+allPcs.toString()+")) and starttime<? and lastupdate>?";
			ps = conn.prepareStatement( sql );
			while( curr.isBefore( e ) || curr.equals( e )) {
				Timestamp start = new Timestamp(curr.getMillis());
				Timestamp end = new Timestamp(next.getMillis());
				ps.setTimestamp( 1, end );
				ps.setTimestamp( 2, start );
				rs = ps.executeQuery();
				rs.next();
				map.put(curr.toString( "yyyy-MM-dd" ),rs.getInt( 1 ));
				curr = curr.plusDays( 1 );
				next = next.plusDays( 1 );
				ps.clearParameters();
			}
		}catch(Exception ex) {
			LogUtils.getServiceLog().error( "Get Station Usage stat by Task failed. siteName="+siteName+",start:"+s+",end:"+e+",taskId:"+taskId, ex );
		}finally {
			CommonUtils.closeQuitely(rs2);
			CommonUtils.closeQuitely(ps2);
			CommonUtils.closeQuitely(conn2);
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}
	
	public static Map<String,Integer> getExecutionOnProduct( String siteName, DateTime s, DateTime e ){
		DateTime start = new DateTime(s);
		DateTime end = new DateTime(e).plusDays( 1 ); 
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getMonitorConnection( siteName );
			String sql = "select product,count(product) from (select product,stationid from executionStates where starttime<? and lastupdate>? group by product,stationid) as tb group by product";
			ps = conn.prepareStatement( sql );
			ps.setTimestamp( 2, new Timestamp(start.getMillis()) );
			ps.setTimestamp( 1, new Timestamp(end.getMillis()) );
			rs = ps.executeQuery();
			while( rs.next()) {
				map.put( rs.getString( 1 ), rs.getInt( 2 ) );
			}
		}catch(Exception ex) {
			LogUtils.getServiceLog().error( "Get Execution on product failed. siteName="+siteName+",start:"+s+",end:"+e, ex );
		}finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}
	
	public static Map<String,Integer> getExecutionOnTask( String siteName, DateTime s, DateTime e ){
		DateTime start = new DateTime(s);
		DateTime end = new DateTime(e).plusDays( 1 ); 
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn2 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs3 = null;
		try {
			conn2 = DbUtils.getCentralConnection();
			String sql2 = "select distinct pcname from stations where stationId in (select stationId from task_stations where taskId=?)";
			String sql3 = "select taskid,product,milestone from tasks where site=?";
			ps3 = conn2.prepareStatement(sql3);
			ps3.setString(1, siteName);
			rs3 = ps3.executeQuery();
			ps2 = conn2.prepareStatement(sql2);
			HashMap<String,String> allPcs = new LinkedHashMap<String,String>();
			while(rs3.next()) {
				ps2.setInt(1, rs3.getInt(1));
				rs2 = ps2.executeQuery();
				StringBuffer pcs = new StringBuffer(200); 
				while(rs2.next()) {
					if(pcs.length()>0)
						pcs.append(",");
					pcs.append("'").append(rs2.getString(1)).append("'");
				}
				allPcs.put(rs3.getString(2)+"_"+rs3.getString(3), pcs.toString());
				CommonUtils.closeQuitely(rs2);
				ps2.clearParameters();
			}
			
			conn = DbUtils.getMonitorConnection( siteName );
			String sql = "select count(distinct stationid) cnt from (select * from executionStates union select * from executionStates_history) as A left join stationInfos on A.stationId=stationInfos.id where pcname in (";
			String ext = ") and starttime<? and lastupdate>? order by cnt";
			for(String key:allPcs.keySet()) {
				try {
					String v = allPcs.get(key);
					if(!v.isEmpty()) {
						ps = conn.prepareStatement( sql+v+ext );
						ps.setTimestamp( 2, new Timestamp(start.getMillis()) );
						ps.setTimestamp( 1, new Timestamp(end.getMillis()) );
						rs = ps.executeQuery();
						if(rs.next()) {
							map.put( key, rs.getInt( 1 ) );
						}
					}
				}catch(Exception ex) {
					LogUtils.getServiceLog().error( "Get Execution on task failed. siteName="+siteName+",start:"+s+",end:"+e+",task:"+key, ex );
				}finally {
					CommonUtils.closeQuitely(rs);
					ps.clearParameters();
				}
			}
		}catch(Exception ex) {
			LogUtils.getServiceLog().error( "Get Execution on task failed. siteName="+siteName+",start:"+s+",end:"+e, ex );
		}finally {
			CommonUtils.closeQuitely(rs3);
			CommonUtils.closeQuitely(ps3);
			CommonUtils.closeQuitely(rs2);
			CommonUtils.closeQuitely(ps2);
			CommonUtils.closeQuitely(conn2);
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}
}
