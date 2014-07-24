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
	int id = CommonUtils.parseInt(request.getParameter("id"),0);
	int status = CommonUtils.parseInt(request.getParameter("status"),1);
	Station stat = new Station();
	stat.setPcName( name );
	stat.setId( id );
	stat.setUsed( status );
	StationService ss = StationServiceFactory.getInstance();
	try{
		ss.updateStation( stat );
		OperationRecordUtils.record( userName, OperationRecord.OperationType.UpdateStation, "User["+userName+"] update Station:"+stat );
		out.write("true");
	}catch(Exception e){
		LogUtils.getServiceLog().error("Update Station failed, stat="+stat, e);
		out.write("false");
	}
%>