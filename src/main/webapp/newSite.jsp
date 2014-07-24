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
	SiteService ss = SiteServiceFactory.getInstance();
	try{
		Site site = new Site();
		site.setSiteName(siteName);
		site.setDatabases(new ArrayList<DbMeta>());
		ss.addSite(site);
		OperationRecordUtils.record( userName, OperationRecord.OperationType.AddSite, "User["+userName+"] add new site:"+ siteName );
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
%>