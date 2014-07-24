package com.nokia.testingservice.austere.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.mail.EmailException;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nokia.testingservice.austere.exception.ScheduleException;
import com.nokia.testingservice.austere.model.AustereJob;
import com.nokia.testingservice.austere.model.Task;
import com.nokia.testingservice.austere.service.TaskService;
import com.nokia.testingservice.austere.service.TaskServiceFactory;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.LogUtils;
import com.nokia.testingservice.austere.util.MailUtils;
import com.nokia.testingservice.austere.util.ScheduleUtils;

/**
 * Auto Start Scheduled Services.
 * 
 * @author Frank Wang
 * @since Jun 12, 2012
 */
public class ScheduleServiceAutoController implements ServletContextListener {

	static class TaskDeadLineChecker implements AustereJob{
		@SuppressWarnings("serial")
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			TaskService ts = TaskServiceFactory.getInstance();
			List<Task> tasks = ts.getAllTasks("VanceInfo");
			StringBuffer sb = new StringBuffer("");
			int currwk = CommonUtils.getCurrentWk();
			for(Task task:tasks) {
				if(task.getEndWk()==currwk) {
					sb.append("Task:<").append(task.getProduct()).append("-").append(task.getMilestone()).append("-Station:").append(task.getStationCount()).append(">\n");
				}
			}
			if(sb.length()>0) {
				try {
					MailUtils.sendMail(new ArrayList<String>() {{this.add("di.6.yang@nokia.com");}}, null, "Finishing Task Notification List", sb.toString());
				} catch (EmailException e) {
					LogUtils.getScheduleLog().error( "Send Mail for task finish notification failed!", e );
				}
			}
		}
	}
	@Override
	public void contextInitialized( ServletContextEvent sce ) {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.HOUR, 9 );
		cal.set( Calendar.MINUTE, 00 );
		cal.set( Calendar.SECOND, 00 );
		try {
			ScheduleUtils.scheduleIntervalJob( "TaskDeadLineChecker", TaskDeadLineChecker.class, IntervalUnit.DAY, 1, cal.getTime() );
		} catch ( ScheduleException e ) {
			LogUtils.getScheduleLog().error( "Start Station monitor Scheduler failed!", e );
			try {
				MailUtils.sendMail(MailUtils.austereOwnerList, null, "Start Station monitor Scheduler failed!", CommonUtils.getErrorStack(e));
			} catch (EmailException e1) {
				LogUtils.getScheduleLog().error( "Send Mail failed!", e1 );
			}
		} 
		// EmergencyService es = EmergencyServiceFactory.getInstance();
		/*try {
			ScheduleUtils.scheduleIntervalJob( "TaskChecker", TaskCheckJob.class, IntervalUnit.DAY, 1, cal.getTime() );
		} catch ( ScheduleException e ) {
			LogUtils.getScheduleLog().error( "Start Task monitor Scheduler failed!", e );
			es.throwScheduleEmergency( "[Task Status Checking Job initial failed]", CommonUtils.getErrorStack( e ) );
		}
		try {
			ScheduleUtils.scheduleIntervalJob( "StationChecker", StationCheckJob.class, IntervalUnit.HOUR, 1, Calendar.getInstance().getTime() );
		} catch ( ScheduleException e ) {
			LogUtils.getScheduleLog().error( "Start Station monitor Scheduler failed!", e );
			es.throwScheduleEmergency( "[Station Status Checking Job initial failed]", CommonUtils.getErrorStack( e ) );
		}*/
	}

	@Override
	public void contextDestroyed( ServletContextEvent sce ) {
		try {
			ScheduleUtils.endScheduler();
		} catch ( Exception e ) {
			LogUtils.getScheduleLog().error( "Shutdown Scheduler met problem.", e );
		}
	}
}
