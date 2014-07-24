package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Constants;
import com.nokia.testingservice.austere.model.Task;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LogUtils;

/**
 * Implementation of TaskService.
 * 
 * @author Frank Wang
 * @since May 29, 2012
 */
public class TaskServiceImpl implements TaskService {

	@Override
	public synchronized void createTask( Task task ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		int taskID = 0;
		try {
			String sql = "insert into Tasks(Product,StartWk,EndWk,Milestone,Station_count,Owner,IsUpdated,Status,site) values(?,?,?,?,?,?,0,0,?)";
			conn = DbUtils.getCentralConnection();
			conn.setAutoCommit( false );
			ps = conn.prepareStatement( sql );
			ps.setString( 1, task.getProduct() );
			ps.setInt( 2, task.getStartWk() );
			ps.setInt( 3, task.getEndWk() );
			ps.setString( 4, task.getMilestone() );
			ps.setInt( 5, task.getStationCount() );
			ps.setString( 6, task.getOwner() );
			ps.setString(7, task.getSite());
			ps.executeUpdate();
			sql = "select DISTINCT @@IDENTITY as iden from Tasks";
			ps2 = conn.prepareStatement( sql );
			rs = ps2.executeQuery();
			rs.next();
			taskID = rs.getInt( 1 );
			task.setTaskID( taskID );
			//BookService bs = BookServiceFactory.getInstance();
			//bs.bookForTask( conn, task );
			conn.commit();
		} catch ( Exception ex ) {
			if ( conn != null ) {
				try {
					conn.rollback();
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}
			if( ex instanceof ServiceException )
				throw (ServiceException) ex;
			else
				throw new ServiceException( "Create Task failed:" + task, ex );
		} finally {
				CommonUtils.closeQuitely( rs );
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( ps2 );
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
	public void updateTask( Task task, boolean statusChange ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "update Tasks set Product=?,StartWk=?,EndWk=?,Milestone=?,Station_count=?,Owner=?,IsUpdated=?,Status=? where TaskID=?";
			Task oldTask = this.getTaskByID( task.getTaskID() );
			conn = DbUtils.getCentralConnection();
			conn.setAutoCommit( false );
			ps = conn.prepareStatement( sql );
			ps.setString( 1, task.getProduct() );
			ps.setInt( 2, task.getStartWk() );
			ps.setInt( 3, task.getEndWk() );
			ps.setString( 4, task.getMilestone() );
			ps.setInt( 5, task.getStationCount() );
			ps.setString( 6, task.getOwner() );
			if( statusChange )
				ps.setInt( 7, Constants.TASK_NOT_UPDATED );
			else
				ps.setInt( 7, Constants.TASK_UPDATED );	
			ps.setInt( 8, task.getStatus() );
			ps.setInt( 9, task.getTaskID() );
			ps.executeUpdate();
			//BookService bs = BookServiceFactory.getInstance();
			//bs.updateBook( conn, oldTask, task );
			conn.commit();
		} catch ( Exception ex ) {
			try {
				conn.rollback();
			} catch ( SQLException e ) {
				LogUtils.getDbLog().error( "UpdateTask failed, task:" + task, e );
			}
			throw new ServiceException( "Update Task failed:" + task, ex );
		} finally {
			if ( ps != null )
				CommonUtils.closeQuitely( ps );
			if ( conn != null ) {
				try {
					conn.setAutoCommit( true );
				} catch ( SQLException e ) {
					LogUtils.getDbLog().error( "set commit mode failed", e );
				}
				CommonUtils.closeQuitely( conn );
			}
		}
	}

	@Override
	public void deleteTask( int taskId ) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		try {
			String sql = "insert into tasks_history select * from Tasks where TaskID=?";
			String sql2 = "delete from Tasks where TaskID=?";
			conn = DbUtils.getCentralConnection();
			conn.setAutoCommit( false );
			ps = conn.prepareStatement( sql );
			ps2 = conn.prepareStatement( sql2 );
			ps.setInt( 1, taskId );
			ps2.setInt( 1, taskId );
			ps.executeUpdate();
			ps2.executeUpdate();
			conn.commit();
			conn.setAutoCommit( true );
			this.clearTaskStations(taskId);
		} catch ( Exception ex ) {
			try {
				conn.rollback();
			} catch ( Exception e ) {
				LogUtils.getDbLog().error( "DeleteTask rollback failed", e );
			}
			throw new ServiceException( "Delete Task failed:" + taskId, ex );
		} finally {
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( ps2 );
			CommonUtils.closeQuitely( conn );
		}
	}

	@Override
	public void deleteTasksByOwner( String site, String owner ) throws ServiceException {
		try {
			for( Task t : getTasksByOwner(site, owner))
			{
				deleteTask( t.getTaskID() );
			}
		} catch ( Exception ex ) {
			throw new ServiceException( "Delete Task By Owner failed:" + owner, ex );
		} 
	}

	private Task encap( ResultSet rs ) throws Exception {
		Task task = new Task();
		task.setTaskID( rs.getInt( "TaskID" ) );
		task.setOwner( rs.getString( "Owner" ) );
		task.setProduct( rs.getString( "Product" ) );
		task.setStationCount( rs.getInt( "Station_count" ) );
		task.setStartWk( rs.getInt( "StartWk" ) );
		task.setEndWk( rs.getInt( "EndWk" ) );
		task.setMilestone( rs.getString( "Milestone" ) );
		task.setIsUpdated( rs.getInt( "IsUpdated" ) );
		task.setStatus( rs.getInt( "Status" ) );
		task.setSite( rs.getString("site") );
		return task;
	}

	@Override
	public List<Task> getTasksByOwner( String site, String owner ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Task> tasks = new ArrayList<Task>();
		try {
			String sql = "select * from Tasks where site=? and Owner=? order by StartWk,Product,Milestone";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, site );
			ps.setString( 2, owner );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				tasks.add( encap( rs ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get Task By Owner failed:site="+site+",owner=" + owner, ex );
		} finally {
				CommonUtils.closeQuitely( rs );
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( conn );
		}
		return tasks;
	}

	@Override
	public List<Task> getTasksByOwner( String site, String owner, int status ) {
		if ( status == Constants.TASK_STATUS_ALL )
			return getTasksByOwner( site, owner );
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Task> tasks = new ArrayList<Task>();
		try {
			String sql = "select * from Tasks where site=? and Owner=? and status=? order by StartWk,Product,Milestone";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, site );
			ps.setString( 2, owner );
			ps.setInt( 3, status );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				tasks.add( encap( rs ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get Task By Owner/status failed:site="+site+",owner=" + owner +",status="+status, ex );
		} finally {
				CommonUtils.closeQuitely( rs );
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( conn );
		}
		return tasks;
	}

	@Override
	public List<Task> getAllTasks( String site ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Task> tasks = new ArrayList<Task>();
		try {
			String sql = "select * from Tasks where site=? order by StartWk,Product,Milestone";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString(1, site);
			rs = ps.executeQuery();
			while ( rs.next() ) {
				tasks.add( encap( rs ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get All Tasks failed,site="+site, ex );
		} finally {
				CommonUtils.closeQuitely( rs );
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( conn );
		}
		return tasks;
	}

	@Override
	public List<Task> getAllTasks( String site, int status ) {
		if ( status == Constants.TASK_STATUS_ALL )
			return getAllTasks(site);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Task> tasks = new ArrayList<Task>();
		try {
			String sql = "select * from Tasks where site=? and Status=? order by StartWk,Product,Milestone";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString(1, site);
			ps.setInt( 2, status );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				tasks.add( encap( rs ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get All Tasks by status failed, site="+site+", status=" + status, ex );
		} finally {
				CommonUtils.closeQuitely( rs );
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( conn );
		}
		return tasks;
	}

	@Override
	public List<Task> getAllValidTasks(String site) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Task> tasks = new ArrayList<Task>();
		try {
			String sql = "select * from Tasks where site=? and Status!=? order by StartWk,Product,Milestone";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString(1, site);
			ps.setInt( 2, Constants.TASK_STATUS_END );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				tasks.add( encap( rs ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get All Valid Tasks by status failed,site="+site, ex );
		} finally {
				CommonUtils.closeQuitely( rs );
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( conn );
		}
		return tasks;
	}

	@Override
	public List<Task> getTasksByProduct( String site, String product ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Task> tasks = new ArrayList<Task>();
		try {
			String sql = "select * from Tasks where site=? and  Product=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setString( 1, site );
			ps.setString( 2, product );
			rs = ps.executeQuery();
			while ( rs.next() ) {
				tasks.add( encap( rs ) );
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get Tasks By product failed:site="+ site +",product=" + product, ex );
		} finally {
				CommonUtils.closeQuitely( rs );
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( conn );
		}
		return tasks;
	}

	@Override
	public Task getTaskByID( int taskID ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from Tasks where TaskID=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, taskID );
			rs = ps.executeQuery();
			if ( rs.next() )
				return encap( rs );
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Get Tasks By TaskID failed:" + taskID, ex );
		} finally {
				CommonUtils.closeQuitely( rs );
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( conn );
		}
		return null;
	}
	
	@Override
	public void setTaskStations( int taskId, List<Integer> stationIds ) throws ServiceException {
		Connection conn = null;
		PreparedStatement psPre = null;
		PreparedStatement ps = null;
		try {
			String sql = "insert into task_stations(taskId,stationId) values(?,?)";
			conn = DbUtils.getCentralConnection();
			conn.setAutoCommit(false);
			psPre = conn.prepareStatement( "delete from task_stations where taskId=?" );
			psPre.setInt(1, taskId);
			psPre.executeUpdate();
			ps = conn.prepareStatement( sql );
			for( int i : stationIds ) {
				ps.setInt( 1, taskId );
				ps.setInt( 2 , i );
				ps.executeUpdate();
				ps.clearParameters();
			}
			conn.setAutoCommit(true);
		} catch ( Exception ex ) {
			try {
				if(conn!=null)
					conn.rollback();
			}catch(Exception e) {
				e.printStackTrace();	
			}
			LogUtils.getServiceLog().error( "Set Task stations failed:" + taskId+",stationIds="+stationIds, ex );
			throw new ServiceException( "Set Task stations failed:" + taskId+",stationIds="+stationIds, ex);
		} finally {
			CommonUtils.closeQuitely( psPre );
			CommonUtils.closeQuitely( ps );
			CommonUtils.closeQuitely( conn );
		}
	}
	
	@Override
	public List<Integer> getTaskStationIds( int taskId ) {
		List<Integer> stationIds = new ArrayList<Integer>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select stationId from task_stations where taskId=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, taskId );
			rs = ps.executeQuery();
			while( rs.next() ) {
				stationIds.add(rs.getInt(1));
			}
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "get Task stations failed:" + taskId, ex );
		} finally {
				CommonUtils.closeQuitely( rs );
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( conn );
		}
		return stationIds;
	}

	@Override
	public void clearTaskStations( int taskId ) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "delete from task_stations where taskId=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement( sql );
			ps.setInt( 1, taskId );
		} catch ( Exception ex ) {
			LogUtils.getServiceLog().error( "Clear Task stations failed:" + taskId, ex );
		} finally {
				CommonUtils.closeQuitely( ps );
				CommonUtils.closeQuitely( conn );
		}
	}
}
