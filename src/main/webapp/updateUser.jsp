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
	String role = request.getParameter("role");
	UserService us = UserServiceFactory.getInstance();
	try{
		us.updateUserRole(userID,User.Role.getRole(role));
		OperationRecordUtils.record( userName, OperationRecord.OperationType.UpdateUser, "User["+userName+"] update user:"+userID+",role:"+role );
		out.write("true");
	}catch(Exception e){
		LogUtils.getServiceLog().error("Delete User failed, userId="+userID, e);
		out.write("false");
	}
%>