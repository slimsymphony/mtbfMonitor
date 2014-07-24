package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Constants;
import com.nokia.testingservice.austere.model.Station;
import com.nokia.testingservice.austere.model.Task;
import com.nokia.testingservice.austere.model.Workload;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.DbUtils.DBType;
import com.nokia.testingservice.austere.util.LogUtils;

/**
 * implementation of book service.
 * 
 * @author Frank Wang
 * @since May 30, 2012
 */
public class BookServiceImpl implements BookService {

	@Override
	public void bookForTask( Connection conn, Task task ) throws ServiceException {
		DBType dbType = null;
		try{
			dbType = DbUtils.getDBType( conn );
		}catch( Exception e ) {
			throw new ServiceException( "Check database type failed", e );
		}
		String sql = "";
		switch( dbType ) {
			case oracle:
				sql = "select StationID,PcName from (select StationID,PcName from Stations where StationID not in( select StationID from Task_Station_Status where Wk between ? and ? ) order by StationID) where rownum<=" + task.getStationCount();
				break;
			case mysql:
				sql = "select StationID,PcName from Stations where StationID not in( select StationID from Task_Station_Status where Wk between ? and ? ) order by StationID limit "+task.getStationCount();
				break;
			default:
				sql = "select top " + task.getStationCount() + " StationID,PcName from Stations where StationID not in( select StationID from Task_Station_Status where Wk between ? and ? ) order by StationID";
		}
		
		LogUtils.getDbLog().info( "DBTYPE:" + dbType + ", sql=" + sql );
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			Map<Integer, String> stations = new HashMap<Integer, String>();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, task.getStartWk() );
			ps.setInt( 2, task.getEndWk() );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				stations.put( rs.getInt( 1 ), rs.getString( 2 ) );
			}
			if ( stations.size() < task.getStationCount() ) {
				throw new ServiceException( "Can't allocate enough Austere Stations for task:" + task );
			}
			sql = "insert into Task_Station_Status(StationID,PcName,TaskID,Wk,Status) values(?,?,?,?,?)";
			ps2 = conn.prepareStatement( sql );
			for ( Entry<Integer, String> k : stations.entrySet() ) {
				for ( int s = task.getStartWk(); s <= task.getEndWk(); s++ ) {
					ps2.setInt( 1, k.getKey() );
					ps2.setString( 2, k.getValue() );
					ps2.setInt( 3, task.getTaskID() );
					ps2.setInt( 4, s );
					ps2.setInt( 5, Constants.STATION_STATUS_BOOK );
					ps2.executeUpdate();
					ps2.clearParameters();
				}
			}
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				e1.printStackTrace();
			}
			if( e instanceof ServiceException )
				throw (ServiceException) e;
			else
				throw new ServiceException( "Book Stations For Task faild:" + task, e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( ps2 != null )
				CommonUtils.closeQuitely( ps2 );
		}
	}

	private Workload encap( ResultSet rs ) throws SQLException {
		Workload w = new Workload();
		w.setPcName( rs.getString( "PcName" ) );
		w.setStationID( rs.getInt( "StationID" ) );
		w.setTaskID( rs.getInt( "TaskID" ) );
		w.setStatus( rs.getInt( "Status" ) );
		w.setWk( rs.getInt( "Wk" ) );
		return w;
	}

	@Override
	public List<Workload> getWorkloadByStation( int stationID ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Workload> ws = new ArrayList<Workload>();
		try {
			String sql = "select * from Task_Station_Status where StationID=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, stationID );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				ws.add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get Workload by Station error, stationID:" + stationID, e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return ws;
	}

	/**
	 * Just use a straight-forward way
	 * <ol>
	 * <li>If this task haven't already started, delete exist workloads and re-book.</li>
	 * <li>If this task have already started, then user can only modify the end point or modify the capacity.This part
	 * should be verified by view logic.</li>
	 * </ol>
	 * 
	 * @see com.nokia.testingservice.austere.service.BookService#updateBook(com.nokia.testingservice.austere.model.Task,
	 *      com.nokia.testingservice.austere.model.Task)
	 */
	@Override
	public void updateBook( Connection conn, Task oldTask, Task newTask ) throws ServiceException {
		if ( oldTask.getStartWk() < CommonUtils.getCurrentWk() ) { // already started
			int ewi = newTask.getEndWk() - oldTask.getEndWk();
			int cc = newTask.getStationCount() - oldTask.getStationCount();
			if ( cc > 0 ) {// increase station
				if ( ewi > 0 ) { // delay
					Task inTaskW = newTask.clone();
					inTaskW.setStartWk( oldTask.getEndWk() + 1 );
					inTaskW.setStatus( Constants.TASK_STATUS_NOT_START );
					Task inTaskC = newTask.clone();
					inTaskC.setStatus( Constants.TASK_STATUS_NOT_START );
					inTaskC.setStationCount( cc );
					inTaskC.setStartWk( CommonUtils.getCurrentWk() + 1 );
					inTaskC.setEndWk( oldTask.getEndWk() );
					bookForTask( conn, inTaskW );
					bookForTask( conn, inTaskC );
				} else if ( ewi < 0 ) { // in advance
					// delete wk
					deleteBookByTaskandInterval( conn, newTask.getTaskID(), newTask.getEndWk() + 1, oldTask.getEndWk() );
					// book new station
					Task inTask = newTask.clone();
					inTask.setStartWk( CommonUtils.getCurrentWk() + 1 );
					inTask.setStationCount( cc );
					bookForTask( conn, inTask );
				} else {
					Task inTask = newTask.clone();
					inTask.setStationCount( cc );
					inTask.setStartWk( CommonUtils.getCurrentWk() + 1 );
					bookForTask( conn, inTask );
				}
			} else if ( cc < 0 ) {// decrease station
				if ( ewi > 0 ) { // delay
					// delete decreased book
					deleteBookByTaskandInterval( conn, newTask.getTaskID(), CommonUtils.getCurrentWk() + 1, oldTask.getEndWk() );
					// add new book
					Task inTask = newTask.clone();
					inTask.setStartWk( CommonUtils.getCurrentWk() + 1 );
					bookForTask( conn, inTask );
				} else if ( ewi < 0 ) { // in advance
					// delete book
					deleteBookByTaskandInterval( conn, newTask.getTaskID(), newTask.getEndWk() + 1, oldTask.getEndWk() );
					/*
					 * for ( int stationID : getLimitedBookedStationsByTaskIDandInterval( Math.abs( cc ),
					 * newTask.getTaskID(), CommonUtils.getCurrentWk() + 1, newTask.getEndWk() ) ) {
					 * deleteBookByStationAndInterval( stationID, newTask.getTaskID(), CommonUtils.getCurrentWk() + 1,
					 * newTask.getEndWk() ); }
					 */
					deleteLimitBookByWkandTask( Math.abs( cc ), newTask.getTaskID(), CommonUtils.getCurrentWk() + 1, newTask.getEndWk() );
				} else {
					// just delete
					/*
					 * for ( int stationID : getLimitedBookedStationsByTaskIDandInterval( Math.abs( cc ),
					 * newTask.getTaskID(), CommonUtils.getCurrentWk() + 1, newTask.getEndWk() ) ) {
					 * deleteBookByStationAndInterval( stationID, newTask.getTaskID(), CommonUtils.getCurrentWk() + 1,
					 * newTask.getEndWk() ); }
					 */
					deleteLimitBookByWkandTask( Math.abs( cc ), newTask.getTaskID(), CommonUtils.getCurrentWk() + 1, newTask.getEndWk() );
				}
			} else { // station count not changed.
				if ( ewi > 0 ) { // delay
					Task inTask = newTask.clone();
					inTask.setStartWk( oldTask.getEndWk() + 1 );
					bookForTask( conn, inTask );
				} else if ( ewi < 0 ) { // in advance
					deleteBookByTaskandInterval( conn, newTask.getTaskID(), newTask.getEndWk() + 1, oldTask.getEndWk() );
				} else { // only update status
					if ( newTask.getEndWk() == CommonUtils.getCurrentWk() ) {
						if ( oldTask.getStatus() == Constants.TASK_STATUS_STARTED && newTask.getStatus() == Constants.TASK_STATUS_END )
							updateWorkloadStatusByTask( conn, newTask.getTaskID(), newTask.getStatus(), CommonUtils.getCurrentWk() );
					}
				}
			}
		} else {
			if ( oldTask.getStartWk() == newTask.getStartWk() && oldTask.getEndWk() == newTask.getEndWk() && oldTask.getStationCount() == newTask.getStationCount() ) { // only update status
				if ( newTask.getStartWk() == CommonUtils.getCurrentWk() ) {
					if ( oldTask.getStatus() == Constants.TASK_STATUS_NOT_START && newTask.getStatus() == Constants.TASK_STATUS_STARTED )
						updateWorkloadStatusByTask( conn, newTask.getTaskID(), newTask.getStatus(), CommonUtils.getCurrentWk() );
				} 
			} else { // for future task changes.
				// delete first
				deleteBookByTask( conn, oldTask.getTaskID() );
				// re-book
				bookForTask( conn, newTask );
			}
		}
	}

	private void deleteLimitBookByWkandTask( int limit, int taskID, int startWk, int endWk ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DbUtils.getCentralConnection();
			conn.setAutoCommit( false );
			DBType dbType = null;
			try{
				dbType = DbUtils.getDBType( conn );
			}catch( Exception e ) {
				throw new ServiceException( "Check database type failed", e );
			}
			String sql = "";
			switch( dbType ) {
				case oracle:
					sql = "delete from Task_Station_Status where taskID=? and Wk=? and StationID in ( select stationid from (select t.stationid from (select distinct(stationid) from Task_Station_Status where taskId=? and wk=? ) t order by stationId desc) where rownum<=" + limit + ")";
					break;
				case mysql:
					sql = "delete from Task_Station_Status where taskID=? and Wk=? and StationID in (select t.stationid from (select distinct(stationid) from Task_Station_Status where taskId=? and wk=? ) t order by stationId desc limit " + limit + " )";
					break;
				default:
					sql = "delete from Task_Station_Status where taskID=? and Wk=? and StationID in (select top " + limit + " t.stationid from (select distinct(stationid) from Task_Station_Status where taskId=? and wk=? ) t order by stationId desc)";
			}
			
			LogUtils.getDbLog().info( "DBTYPE:" + dbType + ", sql=" + sql );
			ps = conn.prepareStatement( sql );
			for ( int i = endWk; i >= startWk; i-- ) {
				ps.setInt( 1, taskID );
				ps.setInt( 2, i );
				ps.setInt( 3, taskID );
				ps.setInt( 4, i );
				ps.executeUpdate();
				ps.clearParameters();
			}
			conn.commit();
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				LogUtils.getDbLog().error( "Delete limit book by wk and task rollback failed", e1 );
			}
			throw new ServiceException( "Delete limit book by wk and task rollback failed,limit=" + limit + ",taskID=" + taskID + ",startWk=" + startWk + ",endWk=" + endWk, e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null ) {
				try {
					conn.setAutoCommit( true );
				} catch ( SQLException e ) {
					e.printStackTrace();
				}
				CommonUtils.closeQuitely( conn );
			}
		}

	}

	@Override
	public void deleteBookByTask( Connection conn, int taskID ) throws ServiceException {
		PreparedStatement ps = null;
		try {
			String sql = "delete from Task_Station_Status where TaskID=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, taskID );
			ps.executeUpdate();
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				LogUtils.getServiceLog().error( "Delete book by task rollback failed.", e1 );
			}
			throw new ServiceException( "Delete Book By TaskId failed, taskID=" + taskID, e );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
		}

	}

	@Override
	public void deleteBookByTaskandInterval( Connection conn, int taskID, int startWk, int endWk ) throws ServiceException {
		PreparedStatement ps = null;
		try {
			String sql = "delete from Task_Station_Status where TaskID=? and Wk between ? and ?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, taskID );
			ps.setInt( 2, startWk );
			ps.setInt( 3, endWk );
			ps.executeUpdate();
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				LogUtils.getServiceLog().error( "Delete book by task and interval roll back failed.", e1 );
			}
			throw new ServiceException( "Delete Book By Task and Interval failed, taskID=" + taskID + ",startWk=" + startWk + ",endWk=" + endWk, e );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
		}
	}

	@Override
	public List<Integer> getLimitedBookedStationsByTaskIDandInterval( int limit, int taskID, int startWk, int endWk ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Integer> stationIDs = new ArrayList<Integer>();
		try {
			String sql = "";
			conn = DbUtils.getCentralConnection();
			DBType dbType = null;
			try{
				dbType = DbUtils.getDBType( conn );
			}catch( Exception e ) {
				throw new ServiceException( "Check database type failed", e );
			}
			switch( dbType ) {
				case oracle:
					sql = "select StationID from ( select StationID from (select  distinct(StationID)  from Task_Station_Status where TaskID=? and Wk between ? and ? ) t order by t.StationID desc ) where rownum<=" + limit;
					break;
				case mysql:
					sql = "select StationID from (select  distinct(StationID)  from Task_Station_Status where TaskID=? and Wk between ? and ? ) t order by t.StationID desc limit " + limit ;
					break;
				default:
					sql = "select top " + limit + " StationID from (select  distinct(StationID)  from Task_Station_Status where TaskID=? and Wk between ? and ? ) t order by t.StationID desc";
			}
			
			LogUtils.getDbLog().info( "DBTYPE:" + dbType + ", sql=" + sql );
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, taskID );
			ps.setInt( 2, startWk );
			ps.setInt( 3, endWk );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				stationIDs.add( rs.getInt( 1 ) );
			}
			if ( stationIDs.size() < limit )
				throw new ServiceException( "[GetLimitedBookedStationsByTaskIDandInterval] found " + stationIDs.size() + " stationID, not equals to limit:" + limit );
			return stationIDs;
		} catch ( Exception e ) {
			throw new ServiceException( "Get Book By Task and Interval failed, taskID=" + taskID + ",startWk=" + startWk + ",endWk=" + endWk, e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
	}

	@Override
	public List<Workload> getWorkloadsByTaskAndWk( int taskID, int wk ) throws ServiceException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Workload> ws = new ArrayList<Workload>();
		try {
			String sql = "select * from Task_Station_Status where taskId=? and  Wk=? order by PcName";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, taskID );
			ps.setInt( 2, CommonUtils.getCurrentWk() );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				ws.add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get current Workload error", e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return ws;
	}
	
	@Override
	public void deleteBookByStationAndInterval( Connection conn, int stationID, int taskID, int startWk, int endWk ) throws ServiceException {
		PreparedStatement ps = null;
		try {
			String sql = "delete from Task_Station_Status where StationID=? and TaskID=? and Wk between ? and ?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, stationID );
			ps.setInt( 2, taskID );
			ps.setInt( 3, startWk );
			ps.setInt( 4, endWk );
			ps.executeUpdate();
		} catch ( Exception e ) {
			try {
				conn.rollback();
			} catch ( SQLException e1 ) {
				LogUtils.getServiceLog().error( "Delete book by station and interval roll back failed", e1 );
			}
			throw new ServiceException( "Delete Book By stationID and Interval failed, stationID=" + stationID + ",startWk=" + startWk + ",endWk=" + endWk, e );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
		}
	}

	@Override
	public List<Workload> getWorkloadsByTask( int taskID ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Workload> ws = new ArrayList<Workload>();
		try {
			String sql = "select * from Task_Station_Status where TaskID=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, taskID );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				ws.add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get Workload by taskID error, taskID:" + taskID, e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return ws;
	}

	@Override
	public List<Workload> getCurrentWorkloads() throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Workload> ws = new ArrayList<Workload>();
		try {
			String sql = "select * from Task_Station_Status where Wk=? order by taskID,PcName";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, CommonUtils.getCurrentWk() );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				ws.add( encap( rs ) );
			}
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get current Workload error", e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return ws;
	}

	@Override
	public Map<String, List<Workload>> getWorkloadsByTaskID( int taskID ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, List<Workload>> ws = new HashMap<String, List<Workload>>();
		StationService ss = StationServiceFactory.getInstance();
		try {
			String sql = "select * from Task_Station_Status where TaskID=? order by WK,StationID";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, taskID );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				int stationID = rs.getInt( "StationID" );
				Workload wl = encap( rs );
				Station station = ss.getStationById( stationID );
				String pcName = station.getPcName();
				if ( ws.get( pcName ) == null ) {
					List<Workload> ls = new ArrayList<Workload>();
					ls.add( wl );
					ws.put( pcName, ls );
				} else {
					ws.get( pcName ).add( wl );
				}
			}
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Get Workload by taskID error, taskID:" + taskID, e );
		} finally {
			if ( rs != null )
				CommonUtils.closeQuitely( rs );
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null )
				CommonUtils.closeQuitely( conn );
		}
		return ws;
	}

	public void updateWorkloadStatusByTask( Connection conn, int taskID, int status, int wk ) throws ServiceException {
		PreparedStatement ps = null;
		try {
			String sql = "update Task_Station_Status set Status=?  where TaskId=? and Wk=?";
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, status );
			ps.setInt( 2, taskID );
			ps.setInt( 3, wk );
			ps.executeUpdate();
		} catch ( Exception e ) {
			LogUtils.getServiceLog().error( "Update Workload Status error, taskID:" + taskID + ", status:" + status + ", wk:" + wk, e );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
		}
	}
}
