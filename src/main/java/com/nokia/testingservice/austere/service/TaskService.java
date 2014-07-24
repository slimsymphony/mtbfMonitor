package com.nokia.testingservice.austere.service;

import java.util.List;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Task;

/**
 * Task related service interface.
 *
 * @author Frank Wang
 * @since May 29, 2012
 */
public interface TaskService {
	
	public void createTask( Task task ) throws ServiceException;
	
	public void updateTask( Task task, boolean statusChange ) throws ServiceException;
	
	public void deleteTask( int taskId ) throws ServiceException;
	
	public void deleteTasksByOwner( String site, String owner ) throws ServiceException;
	
	public List<Task> getTasksByOwner( String site, String owner );
	
	public List<Task> getTasksByOwner( String site, String owner, int status );
	
	public List<Task> getAllTasks( String site );
	
	public List<Task> getAllValidTasks( String site );
	
	public List<Task> getAllTasks( String site, int status );
	
	public List<Task> getTasksByProduct( String site, String product );
	
	public Task getTaskByID( int taskID );
	
	public void setTaskStations( int taskId, List<Integer> stationIds ) throws ServiceException ;

	public List<Integer> getTaskStationIds( int taskId );
	
	public void clearTaskStations( int taskId );
}
