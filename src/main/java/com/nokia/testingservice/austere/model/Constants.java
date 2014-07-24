package com.nokia.testingservice.austere.model;

public interface Constants {
	
	static int STATION_STATUS_BOOK = 0;
	static int STATION_STATUS_WORK = 1;
	static int STATION_STATUS_DONE = 2;
	static int STATION_STATUS_PROBLEM = -1;
	
	static int TASK_NOT_UPDATED = 0;
	static int TASK_UPDATED = 1;
	
	static int STATION_NORMAL = 1;
	static int STATION_FAULT = -1;
	static int STATION_MISSING = 0;
	
	static int PRODUCT_INVALID = 1;
	static int PRODUCT_VALID = 0;
	
	static int TASK_STATUS_ALL = -1;
	static int TASK_STATUS_NOT_START = 0;
	static int TASK_STATUS_STARTED = 1;
	static int TASK_STATUS_END = 2;
	
	static String MTBF_FREEZE = "Freeze";
	static String MTBF_STATUS_NOTDETECTED = "DETECTED";
	static String MTBF_STATUS_DETECTED = "NOT_DETECTED";
	
	static int MTBF_STATUS_KNOWN = 0;
	static int MTBF_STATUS_UNKNOWN = 1;
	
	static String MTBF_DAILY = "Daily";
	static String MTBF_WEEKLY = "Weekly";
	
}
