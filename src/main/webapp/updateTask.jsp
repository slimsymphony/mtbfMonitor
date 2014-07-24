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
	try {
		String product = request.getParameter( "product" );
		int taskID = CommonUtils.parseInt( request.getParameter( "taskId" ), 0 );
		int startWk = CommonUtils.parseInt( request.getParameter("startYear")+request.getParameter("startWk"), 0 );
		int endWk = CommonUtils.parseInt( request.getParameter("endYear")+request.getParameter("endWk"), 0 );
		String milestone = request.getParameter( "milestone" );
		int stationCount = CommonUtils.parseInt( request.getParameter( "stationCount" ), 0 );
		String owner = request.getParameter( "owner" );
		Task task = TaskServiceFactory.getInstance().getTaskByID(taskID);
		//task.setTaskID(taskID);
		task.setProduct( product );
		task.setStartWk( startWk );
		task.setEndWk( endWk );
		task.setStationCount( stationCount );
		task.setMilestone( milestone );
		task.setOwner( owner );
		task.setIsUpdated( 1 );
		TaskService ts = TaskServiceFactory.getInstance();
		ts.updateTask( task, false );
		OperationRecordUtils.record( userName, OperationRecord.OperationType.UpdateTask, "User["+userName+"] update a  task:"+task );
		out.print( "true" );
	} catch ( Exception e ) {
		out.print( e.getMessage() );
	}
%>