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
	String oldName = request.getParameter("oldName");
	String newName = request.getParameter("newName");
	try{
		DbUtils.updateInstanceName(oldName,newName);
		OperationRecordUtils.record( userName, OperationRecord.OperationType.UpdateInstance, "User["+userName+"] update instanceName from:"+ oldName+", to:"+newName );
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
%>