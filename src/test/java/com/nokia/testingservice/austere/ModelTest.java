package com.nokia.testingservice.austere;

import com.nokia.testingservice.austere.model.Station;

public class ModelTest {

	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		Station st = new Station();
		st.setPcName( "evan pc" );
		st.setId( 1001 );
		st.setUsed( 1 );
		System.out.println(st.toString());
		Station st2 = st.clone();
		st2.setId( 1002 );
		System.out.println(st2);
	}

}
