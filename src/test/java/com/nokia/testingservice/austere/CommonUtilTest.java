package com.nokia.testingservice.austere;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Assert;

import org.junit.Test;

import com.nokia.testingservice.austere.util.CommonUtils;

public class CommonUtilTest {
	
	@Test
	public void testWkDay() {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.YEAR, 2012 );
		cal.set( Calendar.MONTH, 5 );
		cal.set( Calendar.DAY_OF_MONTH, 26 );
		Assert.assertEquals( 1226, CommonUtils.getWk( cal.getTime() ) );
		Assert.assertEquals( 122604, CommonUtils.getDay( cal.getTime() ) );
	}
	
	public static void testLocal() {
		Date date = new Date();
		System.out.println(date);
		Date date2 = CommonUtils.local2gmt( date, TimeZone.getDefault() );
		System.out.println(date2);
		System.out.println( CommonUtils.gmt2local(date2, TimeZone.getDefault()) );
	}
	
	public static void testInterval() {
		Date[] s = CommonUtils.getIntervalFromWeek( CommonUtils.getCurrentWk() );
		System.out.println(s[0]);
		System.out.println(s[1]);
	}
	
	public static void main(String[] args) {
		//testLocal();
		testInterval();
	}
}
