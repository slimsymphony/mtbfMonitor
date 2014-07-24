package com.nokia.testingservice.austere;

import java.util.Date;

import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.nokia.testingservice.austere.exception.ScheduleException;
import com.nokia.testingservice.austere.model.AustereJob;
import com.nokia.testingservice.austere.util.ScheduleUtils;

public class QuartzTest {
	public static void main( String[] args ) {
		final  QuartzTest qz = new QuartzTest();
		Thread t = new Thread() {
			public void run() {
				System.out.println("Current Thread is:"+this.getId());
				qz.exec();
			}
		};
		t.start();
	}

	public synchronized void exec() {
		try {
			// Grab the Scheduler instance from the Factory
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			// and start it off
			scheduler.start();
			// define the job and tie it to our HelloJob class
			JobDetail job = JobBuilder.newJob( HelloJob.class ).withIdentity( "schedule1", "group1" ).build();
			// Trigger the job to run now, and then repeat every 40 seconds
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity( "schedule1", "group1" ).startNow()
					.withSchedule( SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds( 40 ).repeatForever() ).build();
			// Tell quartz to schedule the job using our trigger
			scheduler.scheduleJob( job, trigger );
			try {
					Thread.sleep( 3000 );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			scheduler.shutdown();
		} catch ( SchedulerException se ) {
			se.printStackTrace();
		}
	}
	
	public static class HelloJob implements AustereJob {
		@Override
		public void execute( JobExecutionContext context ) throws JobExecutionException {
			Thread.currentThread().notifyAll();
			System.out.println( "Hello, world!" );
		}
	}

	public void testScheduleUtils() {
		try {
			ScheduleUtils.scheduleIntervalJob( "schedule", HelloJob.class, IntervalUnit.DAY, 1, new Date() );
		} catch ( ScheduleException e ) {
			e.printStackTrace();
		} finally {

		}
	}
}
