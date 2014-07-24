package com.nokia.testingservice.austere.service;

import java.util.List;

import com.nokia.testingservice.austere.model.TestStation;

public interface StatusService {
	TestStation[] getCurrentStationStatus( String siteName, String productFilter, String taskFilter );
	String[] getProducts();
	TestStation[] getCurrentStationStatus( String siteName );
	List<String> getProducts( String siteName );
	String getRecentSteps( String siteName, String product, String executionId, int limit );
}
