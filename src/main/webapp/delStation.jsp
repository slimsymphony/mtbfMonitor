<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
	String userName = "";
	if ( session.getAttribute( "userInfo" ) != null ) {
		User userInfo = ( User ) session.getAttribute( "userInfo" );
		userName = userInfo.getUserID();
	}
	String id = request.getParameter("id");
	
	StationService ss = StationServiceFactory.getInstance();
	try{
		ss.deleteStationByID( CommonUtils.parseInt( id, 0 ) );
		OperationRecordUtils.record( userName, OperationRecord.OperationType.DeleteStation, "User["+userName+"] delete Station:"+id );
		out.write("true");
	}catch(Exception e){
		LogUtils.getServiceLog().error("Delete Station failed, StationID="+id, e);
		out.write("false");
	}
%>