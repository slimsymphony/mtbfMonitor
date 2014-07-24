package com.nokia.testingservice.austere;

import java.util.Map;
import java.util.Map.Entry;

import com.nokia.testingservice.austere.service.StationService;
import com.nokia.testingservice.austere.service.StationServiceFactory;

public class StatTest {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		StationService ss = StationServiceFactory.getInstance();
		float hours = ss.getValidRunningTime( "922", 1320 );
		float hours2 = ss.getValidRunningTimeForProduct( "922", 1320 , "Aqua");
		System.out.println( Math.round( hours ) );
		System.out.println(hours2);
		Map<Integer,Map<String,Integer>> map = ss.getIowCardsChanges( "922", 1310, 1320 );
		for(Entry<Integer,Map<String,Integer>> entry:map.entrySet()) {
			System.out.println(entry.getKey()+":");
			for(Entry<String,Integer> en : entry.getValue().entrySet()) {
				System.out.println(en.getKey()+":"+en.getValue());
			}
		}
		
		map = ss.getIowCardsChangesForProduct( "922", 1310, 1320, "Aqua" );
		for(Entry<Integer,Map<String,Integer>> entry:map.entrySet()) {
			System.out.println(entry.getKey()+":");
			for(Entry<String,Integer> en : entry.getValue().entrySet()) {
				System.out.println(en.getKey()+":"+en.getValue());
			}
		}
	}

}
