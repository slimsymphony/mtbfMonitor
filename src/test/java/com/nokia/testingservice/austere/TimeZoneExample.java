package com.nokia.testingservice.austere;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeZoneExample {
    public static void main(String[] args) {
        //
        // Create a calendar object and set it time based on the local
        // time zone
        //
        Calendar localTime = Calendar.getInstance();
        localTime.set(Calendar.HOUR, 17);
        localTime.set(Calendar.MINUTE, 15);
        localTime.set(Calendar.SECOND, 20);
 
        int hour = localTime.get(Calendar.HOUR);
        int minute = localTime.get(Calendar.MINUTE);
        int second = localTime.get(Calendar.SECOND);
 
        //
        // Print the local time
        //
        System.out.printf("Local time  : %02d:%02d:%02d\n", hour, minute, second);
        System.out.println(localTime.getTime());
 
        //
        // Create a calendar object for representing a Germany time zone. Then we
        // wet the time of the calendar with the value of the local time
        //
        Calendar germanyTime = new GregorianCalendar(TimeZone.getTimeZone("Germany"));
        germanyTime.setTimeInMillis(localTime.getTimeInMillis());
        hour = germanyTime.get(Calendar.HOUR);
        minute = germanyTime.get(Calendar.MINUTE);
        second = germanyTime.get(Calendar.SECOND);
 
        //
        // Print the local time in Germany time zone
        //
        System.out.printf("Germany time: %02d:%02d:%02d\n", hour, minute, second);
        System.out.println(germanyTime.getTime());
    }
}