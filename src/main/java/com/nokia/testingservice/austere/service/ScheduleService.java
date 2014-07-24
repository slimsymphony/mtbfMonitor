package com.nokia.testingservice.austere.service;

import com.nokia.testingservice.austere.exception.ScheduleException;

/**
 * Interface for maintaining scheduled jobs, including task/station status maintain, data collect/clean, etc.
 * 
 * @author Frank Wang
 * @since Jun 11, 2012
 */
public interface ScheduleService {
	/**
	 * In this method, we have to handle the task status for all the task.
	 * <ul>
	 * <li>check all the task status </li>
	 * <li>Update not start task to started status </li>
	 * <li>Update started task to end status </li>
	 * <li>Archive? TBD</li>
	 * </ul>
	 * @throws ScheduleException
	 */
	void handleTask() throws ScheduleException;

	/**
	 * In this method, we should compare running station status with planned station status. Find out all the differences. 
	 * @throws ScheduleException
	 */
	void handleStation() throws ScheduleException;

	void handleDataGather() throws ScheduleException;
}
