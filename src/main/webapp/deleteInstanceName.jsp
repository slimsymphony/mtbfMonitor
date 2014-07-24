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
	String cascade = request.getParameter("cascade");
	try{
		DbUtils.removeInstanceName( instanceName, CommonUtils.parseBool(cascade) );
		OperationRecordUtils.record( userName, OperationRecord.OperationType.DeleteInstance, "User["+userName+"] delete instance:"+instanceName+",cascade="+cascade );
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
%>