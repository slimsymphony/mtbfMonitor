package com.nokia.testingservice.austere.service;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import com.nokia.testingservice.austere.model.Emergency;
import com.nokia.testingservice.austere.model.Emergency.EmergencyType;

/**
 * Interface for emergency related works.
 *
 * @author Frank Wang
 * @since Jun 12, 2012
 */
public interface EmergencyService {
	
	void throwEmergency( Emergency e );
	boolean haveEmergency();
	BlockingQueue<Emergency> getEmergencyQueue();
	void throwScheduleEmergency( String source, String detail );
	void throwServiceEmergency( String source, String detail );
	void throwDatabaseEmergency( String source, String detail );
	
	Collection<Emergency> getEmergencysFromDB( EmergencyType type, Date start, Date end );
}
