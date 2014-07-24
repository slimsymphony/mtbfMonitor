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
	String userID = request.getParameter("userID");
	UserService us = UserServiceFactory.getInstance();
	try{
		us.delUser(userID);
		out.write("true");
		OperationRecordUtils.record( userName, OperationRecord.OperationType.DeleteUser, "User["+userName+"] delete user:"+userID );
	}catch(Exception e){
		LogUtils.getServiceLog().error("Delete User failed, userId="+userID, e);
		out.write("false");
	}
%>