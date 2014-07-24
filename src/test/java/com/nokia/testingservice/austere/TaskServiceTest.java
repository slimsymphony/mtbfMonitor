package com.nokia.testingservice.austere;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Constants;
import com.nokia.testingservice.austere.model.Task;
import com.nokia.testingservice.austere.service.TaskService;
import com.nokia.testingservice.austere.service.TaskServiceFactory;

public class TaskServiceTest {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		TaskService ts = TaskServiceFactory.getInstance();
		Task task = new Task();
		//task.setTaskID( 1 );
		task.setStartWk( 1207 );
		task.setEndWk( 1212 );
		task.setMilestone( "PD1" );
		task.setProduct( "Hawaii" );
		task.setOwner( "Jeffery" );
		task.setStationCount( 3 );
		task.setIsUpdated( Constants.TASK_NOT_UPDATED );
		try {
			ts.createTask( task );
		} catch ( ServiceException e ) {
			e.printStackTrace();
		}
	}

}
