<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	String userName = "";
	if ( session.getAttribute( "userInfo" ) != null ) {
		User userInfo = ( User ) session.getAttribute( "userInfo" );
		userName = userInfo.getUserID();
	}
	String instanceName = request.getParameter("instanceName");
	try{
		DbUtils.addInstanceName(instanceName);	
		OperationRecordUtils.record( userName, OperationRecord.OperationType.AddInstance, "User["+userName+"] add instance " + instanceName );
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
%>