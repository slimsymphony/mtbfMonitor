package com.nokia.testingservice.austere.service;

import java.util.List;
import java.util.Map;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Station;

/**
 * Service for Station models.
 * 
 * @author Frank Wang
 * @since May 29, 2012
 */
public interface StationService {
	
	public void createStation( Station station ) throws ServiceException;

	public void updateStation( Station station ) throws ServiceException;

	public void deleteStationByID( int stationID ) throws ServiceException;

	public void deleteStationByPcName( String site, String pcName )throws ServiceException;
	
	public List<Station> getAllStations();
	
	public List<Station> getAllStationsBySite( String site );
	
	public List<Station> getStationsByStatus( String site, int status );
	
	public Station getStationByPcName( String site, String pcName );
	
	public Station getStationById( int stationID );
	
	public Station getStation( String siteName, int stationId );
	
	public float getValidRunningTime( String siteName, int week );
	
	public float getValidRunningTimeForProduct( String siteName, int week, String product );
	
	public float getValidRunningTimeForTask( String siteName, int week, int taskId );
	
	public Map<Integer,Map<String,Integer>> getIowCardsChanges( String siteName, int start, int end);
	
	public Map<Integer,Map<String,Integer>> getIowCardsChangesForProduct( String siteName, int start, int end, String product);
	
	public Map<Integer,Map<String,Integer>> getIowCardsChangesForTask( String siteName, int start, int end, int taskId);
	
	public Map<Station, Float> getStationValidTimeByWeek( String siteName, int week );
	
	public Map<Station, Float> getStationValidTimeByWeekForProduct( String siteName, int week, String product );

	public Map<Station, Float> getStationValidTimeByWeekForTask( String siteName, int week, int taskId );
	
	public Map<Station, String> syncStationStatus(String site, int taskId);
	
}
