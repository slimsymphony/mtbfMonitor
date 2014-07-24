package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nokia.testingservice.austere.exception.ScheduleException;
import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Constants;
import com.nokia.testingservice.austere.model.DbMeta;
import com.nokia.testingservice.austere.model.Emergency;
import com.nokia.testingservice.austere.model.Emergency.EmergencyLevel;
import com.nokia.testingservice.austere.model.Emergency.EmergencyType;
import com.nokia.testingservice.austere.model.Task;
import com.nokia.testingservice.austere.model.TestStation;
import com.nokia.testingservice.austere.model.Workload;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LogUtils;

public class ScheduleServiceImpl implements ScheduleService {

	/*
	 * Daily run, actually weekly run is acceptable.
	 * @see com.nokia.testingservice.austere.service.ScheduleService#handleTask()
	 */
	@Override
	public void handleTask() throws ScheduleException {
		int cw = CommonUtils.getCurrentWk();
		TaskService ts = TaskServiceFactory.getInstance();
		BookService bs = BookServiceFactory.getInstance();
		for ( Task task : ts.getAllTasks( null ) ) {
			try {
				switch ( task.getStatus() ) {
					case Constants.TASK_STATUS_NOT_START:
						if ( task.getStartWk() <= cw ) {
							task.setStatus( Constants.TASK_STATUS_STARTED );
							ts.updateTask( task, true );
						}
						break;
					case Constants.TASK_STATUS_STARTED:
						if ( task.getEndWk() > cw ) {
							boolean needUpdate = false;
							for ( Workload wl : bs.getWorkloadsByTaskAndWk( task.getTaskID(), cw ) ) {
								if ( wl.getStatus() != Constants.TASK_STATUS_STARTED ) {
									needUpdate = true;
									break;
								}
							}
							if ( needUpdate ) {
								Connection conn = null;
								try {
									conn = DbUtils.getCentralConnection();
									bs.updateWorkloadStatusByTask( conn, task.getTaskID(), Constants.TASK_STATUS_STARTED, cw );
								}finally {
									CommonUtils.closeQuitely( conn );
								}
							}
						} else {
							task.setStatus( Constants.TASK_STATUS_END );
							ts.updateTask( task, true );
						}
						break;
					case Constants.TASK_STATUS_END:
						// nothing to do
						break;
					default:
						LogUtils.getScheduleLog().warn( "Current Task status invalid:" + task );
						break;
				}
			} catch ( ServiceException e ) {
				LogUtils.getScheduleLog().warn( "Handle Task status update failed:" + task, e );
			}
		}
	}

	@Override
	public void handleStation() throws ScheduleException {
		StatusService ss = StatusServiceFactory.getInstance();
		StationService st = StationServiceFactory.getInstance();
		BookService bs = BookServiceFactory.getInstance();
		TaskService tas = TaskServiceFactory.getInstance();
		EmergencyService es = EmergencyServiceFactory.getInstance();
		final long threshold = 1000L * 60L * 60L * 24L;
		Connection conn = null;
		try {
			List<Workload> plans = bs.getCurrentWorkloads();
			for ( DbMeta dm : DbUtils.databases.values() ) {
				for ( String instanceName : DbUtils.instances ) {
					try {
						conn = DbUtils.getConnection( dm.getSiteName(), instanceName );
						if ( conn == null )
							continue;
					} catch ( Exception e ) {
						continue;
					} finally {
						CommonUtils.closeQuitely( conn );
					}
					TestStation[] tss = ss.getCurrentStationStatus( dm.getSiteName() );
					List<Workload> missing = new ArrayList<Workload>();
					List<Workload> fault = new ArrayList<Workload>();
					for ( Workload plan : plans ) {
						int status = Constants.STATION_MISSING;
						plan.setStatus( Constants.STATION_STATUS_PROBLEM );
						for ( TestStation ts : tss ) {
							if ( st.getStation(ts.getSiteName(), ts.getStationId()).getPcName().equals( plan.getPcName() ) ) {
								status = Constants.STATION_NORMAL;
								plan.setStatus( Constants.STATION_STATUS_WORK );
								if ( ( System.currentTimeMillis() - ts.getLastUpdate().getTime() ) >= threshold ) {
									status = Constants.STATION_FAULT;
									plan.setStatus( Constants.STATION_STATUS_PROBLEM );
								}
								break;
							}
						}
						if ( status == Constants.STATION_MISSING )
							missing.add( plan );
						else if ( status == Constants.STATION_FAULT )
							fault.add( plan );
					}
					Emergency em = null;
					Task task = null;
					UserService us = null;
					StringBuilder sb = new StringBuilder( 200 );
					if ( ( missing.size() + fault.size() ) > 0 ) {
						us = UserServiceFactory.getInstance();
						em = new Emergency();
						em.setLevel( EmergencyLevel.Error );
						em.setType( EmergencyType.ScheduleEmergency );
						em.setEmergencyTime( new Date() );
						em.setSource( "[Station Status Schedule Checking failed]" );
						sb.append( "Found Austere Stations execution have some problem.The problem Stations list below:\n" );
						for ( Workload plan : missing ) {
							task = tas.getTaskByID( plan.getTaskID() );
							sb.append( "* AustereStation[" ).append( plan.getPcName() ).append( "] with internal ID:" ).append( plan.getStationID() )
									.append( " not in use. It should be executing Task:" ).append( task.toString() + "\n" );
							em.addStakeholder( us.getUserByID( task.getOwner() ).getMail() );
						}

						for ( Workload plan : fault ) {
							task = tas.getTaskByID( plan.getTaskID() );
							sb.append( "* AustereStation[" ).append( plan.getPcName() ).append( "] with internal ID:" ).append( plan.getStationID() )
									.append( " not Response. Please check the status of that Station\n" );
							em.addStakeholder( us.getUserByID( task.getOwner() ).getMail() );
						}
						em.setDetail( sb.toString() );
					}
					if ( em != null ) {
						es.throwEmergency( em );
					}
				}
			}

		} catch ( ServiceException e ) {
			throw new ScheduleException( "Find error when handle Station status check.", e );
		}
	}

	@Override
	public void handleDataGather() throws ScheduleException {
		// TODO Auto-generated method stub
	}

}
