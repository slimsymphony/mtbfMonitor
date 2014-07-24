<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.util.concurrent.*"%>
<%
	EmergencyService es = EmergencyServiceFactory.getInstance();
	Collection<Emergency> list = new ArrayList<Emergency>();
	int size = 0;
	if ( request.getParameter( "history" ) == null ) {
		BlockingQueue<Emergency> queue = es.getEmergencyQueue();
		for(Emergency em : queue){
			em.setEmergencyTime( CommonUtils.gmt2local( em.getEmergencyTime(), TimeZone.getDefault() ) );
		}
		size = queue.drainTo( list );
	} else {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		list = es.getEmergencysFromDB( null, cal.getTime(), null );
		size = list.size(); 
	}
	if ( size > 0 ) {
		out.write( CommonUtils.toJson( list ).replaceAll("\\*","<br/>\\*") );
	} else {
		out.write( "" );
	}
%>