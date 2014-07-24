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
	String name = request.getParameter("name");
	String site = request.getParameter("siteName");
	int status = CommonUtils.parseInt(request.getParameter("status"),0);
	Station stat = new Station();
	stat.setPcName( name );
	//stat.setUsed( status );
	stat.setSiteName(site);
	StationService ss = StationServiceFactory.getInstance();
	try{
		ss.createStation( stat );
		OperationRecordUtils.record( userName, OperationRecord.OperationType.AddStation, "User["+userName+"] Add Station:"+stat );
		out.write("true");
	}catch(Exception e){
		LogUtils.getServiceLog().error("Add Station failed, Station="+stat, e);
		out.write("false");
	}
%>