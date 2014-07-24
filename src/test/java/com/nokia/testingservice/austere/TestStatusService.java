package com.nokia.testingservice.austere;

import java.util.ArrayList;
import java.util.List;

import com.nokia.testingservice.austere.model.TestStation;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;

public class TestStatusService {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		com.nokia.testingservice.austere.service.StatusService ss = new com.nokia.testingservice.austere.service.StatusServiceImpl();
		com.nokia.testingservice.austere.model.TestStation[] tss = ss.getCurrentStationStatus("922");
		List<TestStation> l = new ArrayList<TestStation>();
		for(com.nokia.testingservice.austere.model.TestStation ts : tss){
			l.add( ts );
			System.out.println(ts);
		}
		System.out.println(CommonUtils.toJson( l ));
	}

}
