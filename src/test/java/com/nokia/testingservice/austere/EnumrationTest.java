package com.nokia.testingservice.austere;

import java.util.Hashtable;

public class EnumrationTest {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		Hashtable<Long,String> t = new Hashtable<Long,String>();
		t.put( 1L, "1a" );
		t.put( 2L, "2a" );
		t.put( 3L, "3a" );
		t.put( 4L, "4a" );
		t.put( 5L, "5a" );
		t.put( 6L, "6a" );
		t.put( 7L, "7a" );
		t.put( 8L, "8a" );
		for( long key : t.keySet() ) {
			if( key == 4 ) {
				String v = t.remove( key );
				System.out.println( "remove "+v+" from hashtable" );
			}
		}
	}

}
