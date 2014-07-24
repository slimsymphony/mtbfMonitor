<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%><%
	int taskId = Integer.parseInt(request.getParameter("taskId"));
	TaskService ts = TaskServiceFactory.getInstance();
	ts.deleteTask(taskId);
	
	String userName = "";
	if ( session.getAttribute( "userInfo" ) != null ) {
		User userInfo = ( User ) session.getAttribute( "userInfo" );
		userName = userInfo.getUserID();
	}
	OperationRecordUtils.record( userName, OperationRecord.OperationType.DeleteTask, "User["+userName+"] delete task:"+taskId );
	out.println("true");
%>