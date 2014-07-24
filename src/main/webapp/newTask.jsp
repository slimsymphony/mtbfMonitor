<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	String siteName = "";
	if(request.getParameter("site")!=null){
		siteName = request.getParameter("site");
		session.setAttribute("site",siteName);
	}else{
		if(session.getAttribute("site")!=null){
			siteName = (String) session.getAttribute("site");
		}
	}
	String userName = "";
	if ( session.getAttribute( "userInfo" ) != null ) {
		User userInfo = ( User ) session.getAttribute( "userInfo" );
		userName = userInfo.getUserID();
	}
	try {
		String product = request.getParameter( "product" );
		int startWk = CommonUtils.parseInt( request.getParameter("startYear")+request.getParameter("startWk"), 0 );
		int endWk = CommonUtils.parseInt( request.getParameter("endYear")+request.getParameter("endWk"), 0 );
		String milestone = request.getParameter( "milestone" );
		int stationCount = CommonUtils.parseInt( request.getParameter( "stationCount" ), 0 );
		String owner = request.getParameter( "owner" );
		Task task = new Task();
		task.setProduct( product );
		task.setStartWk( startWk );
		task.setEndWk( endWk );
		task.setStationCount( stationCount );
		task.setMilestone( milestone );
		task.setOwner( owner );
		task.setSite(siteName);
		TaskService ts = TaskServiceFactory.getInstance();
		ts.createTask( task );
		OperationRecordUtils.record( userName, OperationRecord.OperationType.CreateTask, "User["+userName+"] create a  task:"+task );
		out.print( "true" );
	} catch ( Throwable e ) {
		out.print( e.getMessage() );
	}
%>