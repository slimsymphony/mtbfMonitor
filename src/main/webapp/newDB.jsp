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
	String siteName = request.getParameter("sitename");
	String url = request.getParameter("url");
	String driverClass = request.getParameter("driverClass");
	String user = request.getParameter("user");
	String password = request.getParameter("password");
	SiteService ss = SiteServiceFactory.getInstance();
	try{
		DbMeta dm = new DbMeta();
		dm.setSiteName(siteName);
		dm.setUrl(url);
		dm.setDriverClass(driverClass);
		dm.setUser(user);
		dm.setPassword(password);
		ss.addDatabase(dm);
		OperationRecordUtils.record( userName, OperationRecord.OperationType.AddDatabase, "User["+userName+"] add database " + dm );
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
%>