package com.nokia.testingservice.austere.service.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nokia.testingservice.austere.exception.ScheduleException;
import com.nokia.testingservice.austere.model.AustereJob;
import com.nokia.testingservice.austere.service.ScheduleService;
import com.nokia.testingservice.austere.service.ScheduleServiceFactory;
import com.nokia.testingservice.austere.util.LogUtils;

public class StationCheckJob implements AustereJob {

	@Override
	public void execute( JobExecutionContext context ) throws JobExecutionException {
		ScheduleService ss = ScheduleServiceFactory.getInstance();
		try {
			ss.handleStation();
		} catch ( ScheduleException e ) {
			LogUtils.getScheduleLog().error( "Execute Task check job met problem.", e );
			//EmergencyService es = EmergencyServiceFactory.getInstance();
			//es.throwScheduleEmergency( "[Station Status Checking]", CommonUtils.getErrorStack( e ) );
			JobExecutionException jee =  new JobExecutionException("Execute Task check job met problem.", e);
			jee.setRefireImmediately( true );
		}

	}

}
