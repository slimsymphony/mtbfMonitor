package com.nokia.testingservice.austere.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.nokia.testingservice.austere.exception.ScheduleException;
import com.nokia.testingservice.austere.model.AustereJob;

public class ScheduleUtils {
	public static String SCHEDULE_GROUP_NAME = "Austere_schedule_group";
	private static Scheduler scheduler;

	/**
	 * Get all JobDetail objects in Quartz.
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public static List<JobDetail> getAllJobs() throws SchedulerException {
		List<JobDetail> jlist = new ArrayList<JobDetail>();
		if ( scheduler != null ) {
			for ( JobKey key : scheduler.getJobKeys( GroupMatcher.jobGroupEquals( SCHEDULE_GROUP_NAME ) ) ) {
				jlist.add( scheduler.getJobDetail( key ) );
			}
		}
		return jlist;
	}

	public static JobDetail getJob( String jobName ) throws SchedulerException {
		if ( scheduler != null ) {
			return scheduler.getJobDetail( new JobKey( jobName, SCHEDULE_GROUP_NAME ) );
		}
		return null;
	}

	public static synchronized void stopJob( String jobName ) throws SchedulerException {
		if ( scheduler != null ) {
			JobDetail job = scheduler.getJobDetail( new JobKey( jobName, SCHEDULE_GROUP_NAME ) );
			Trigger trigger = scheduler.getTrigger( new TriggerKey(jobName, SCHEDULE_GROUP_NAME ) ); 
			if( job == null || trigger == null )
				throw new SchedulerException( "Stop Job/trigger [" + jobName + "] failed, Scheduler not initialized!" );
			else {
				scheduler.unscheduleJob( new TriggerKey(jobName, SCHEDULE_GROUP_NAME ) );
			}
		} else {
			throw new SchedulerException( "Stop Job [" + jobName + "] failed, Scheduler not initialized!" );
		}
	}
	
	public static synchronized void endScheduler() throws SchedulerException {
		if ( scheduler != null ) {
			scheduler.shutdown();
		}
	}

	/**
	 * Schedule a interval job.
	 * 
	 * @param scheduleName Both jobname and triggername use this schedulename.
	 * @param jobClass
	 * @param unit Current support MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK. MONTH and YEAR are not supported.
	 * @param intervalCnt If interval unit is day, than intervalCnt will be ignored.
	 * @param triggerStartTime Year,month and day will be ignored.
	 * @throws ScheduleException
	 */
	public static synchronized void scheduleIntervalJob( String scheduleName, Class<? extends AustereJob> jobClass, IntervalUnit unit, int intervalCnt,
			Date triggerStartTime ) throws ScheduleException {
		try {
			if ( scheduler != null ) {
				if ( !scheduler.isStarted() )
					scheduler.start();
			} else {
				scheduler = StdSchedulerFactory.getDefaultScheduler();
				scheduler.start();
			}
			JobDetail job = JobBuilder.newJob( jobClass ).withIdentity( scheduleName, SCHEDULE_GROUP_NAME ).build();
			Trigger trigger = null;
			if ( triggerStartTime == null )// if no start time provided, start now
				triggerStartTime = new Date();
			TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger().withIdentity( scheduleName, SCHEDULE_GROUP_NAME ).startAt( triggerStartTime );// .startNow();
			ScheduleBuilder<? extends Trigger> schedBuilder = null;
			switch ( unit ) {
			// MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
				case MILLISECOND:
					schedBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds( intervalCnt ).repeatForever();
					break;
				case SECOND:
					schedBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds( intervalCnt ).repeatForever();
					break;
				case MINUTE:
					schedBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes( intervalCnt ).repeatForever();
					break;
				case HOUR:
					schedBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours( intervalCnt ).repeatForever();
					break;
				case DAY:
					schedBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().onEveryDay().withIntervalInHours( 12 );
					break;
				case WEEK:
					Calendar cal = Calendar.getInstance();
					cal.setTime( triggerStartTime );
					schedBuilder = CronScheduleBuilder.weeklyOnDayAndHourAndMinute( cal.get( Calendar.DAY_OF_WEEK ), cal.get( Calendar.HOUR_OF_DAY ), cal.get( Calendar.MINUTE ) );
					break;
				default:
					throw new ScheduleException( "The Interval UNit is not support :" + unit );
			}
			trigger = tb.withSchedule( schedBuilder ).build();
			scheduler.scheduleJob( job, trigger );
		} catch ( SchedulerException e ) {
			LogUtils.getScheduleLog().error( "Schedule a new job failed, scheduleName=" + scheduleName, e );
			throw new ScheduleException( "Schedule a new job failed, scheduleName=" + scheduleName, e );
		}
	}

}
