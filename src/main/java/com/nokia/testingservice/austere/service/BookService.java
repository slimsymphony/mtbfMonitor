package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Task;
import com.nokia.testingservice.austere.model.Workload;

/**
 * Service interface for station booking for tasks.
 * 
 * @author Frank Wang
 * @since May 30, 2012
 */
public interface BookService {

	public void bookForTask( Connection conn, Task task ) throws ServiceException;

	public List<Workload> getWorkloadByStation( int StationID ) throws ServiceException;

	public List<Workload> getWorkloadsByTask( int taskID ) throws ServiceException;

	public List<Workload> getWorkloadsByTaskAndWk( int taskID, int wk ) throws ServiceException;

	public Map<String, List<Workload>> getWorkloadsByTaskID( int taskID ) throws ServiceException;

	public List<Workload> getCurrentWorkloads() throws ServiceException;

	public List<Integer> getLimitedBookedStationsByTaskIDandInterval( int limited, int taskID, int startWk, int endWk ) throws ServiceException;;

	public void updateBook( Connection conn, Task oldTask, Task newTask ) throws ServiceException;

	public void deleteBookByTaskandInterval( Connection conn, int taskID, int startWk, int endWk ) throws ServiceException;

	public void deleteBookByTask( Connection conn, int taskID ) throws ServiceException;

	public void deleteBookByStationAndInterval( Connection conn, int stationID, int taskID, int startWk, int endWk ) throws ServiceException;

	public void updateWorkloadStatusByTask( Connection conn, int taskID, int status, int wk ) throws ServiceException;
}
