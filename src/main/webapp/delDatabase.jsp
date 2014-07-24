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
	String siteName = request.getParameter("siteName");
	String url = request.getParameter("url");
	String driverClass = request.getParameter("driverClass");
	String user = request.getParameter("user");
	String password = request.getParameter("password");
	DbMeta dm = new DbMeta();
	dm.setSiteName(siteName);
	dm.setUrl(url);
	dm.setUser(user);
	dm.setPassword(password);
	dm.setDriverClass(driverClass);
	SiteService ss = SiteServiceFactory.getInstance();
	try{
		ss.delDatabase(dm);
		OperationRecordUtils.record( userName, OperationRecord.OperationType.DeleteDatabase, "User["+userName+"] delete database:"+ dm );
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
%>