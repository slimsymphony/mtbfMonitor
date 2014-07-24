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
	String siteName2 = request.getParameter("sitename2");
	String url2 = request.getParameter("url2");
	String driverClass2 = request.getParameter("driverClass2");
	String user2 = request.getParameter("user2");
	String password2 = request.getParameter("password2");
	SiteService ss = SiteServiceFactory.getInstance();
	try{
		DbMeta dm = new DbMeta();
		dm.setSiteName(siteName);
		dm.setUrl(url);
		dm.setDriverClass(driverClass);
		dm.setUser(user);
		dm.setPassword(password);
		DbMeta odm = new DbMeta();
		odm.setSiteName(siteName2);
		odm.setUrl(url2);
		odm.setDriverClass(driverClass2);
		odm.setUser(user2);
		odm.setPassword(password2);
		ss.updateDatabase(odm,dm);
		OperationRecordUtils.record( userName, OperationRecord.OperationType.UpdateDatabase, "User["+userName+"] update database from "+ odm +" to " + dm );
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
%>