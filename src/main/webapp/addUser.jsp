<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.exception.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
	String userName = "";
	if ( session.getAttribute( "userInfo" ) != null ) {
		User userInfo = ( User ) session.getAttribute( "userInfo" );
		userName = userInfo.getUserID();
	}
	String userID = request.getParameter( "userID" );
	String role = request.getParameter( "role" );
	String mail = request.getParameter( "mail" );
	UserService us = UserServiceFactory.getInstance();
	try {
		if ( userID != null && !userID.trim().equals( "" ) ) {
			us.createUserByID( userID, User.Role.getRole( role ) );
			out.write( "true" );
		} else if ( mail != null && !mail.trim().equals( "" ) ) {
			us.createUserByMail( mail, User.Role.getRole( role ) );
			out.write( "true" );
		} else
			throw new ServiceException( "Either noe and mail is null" );
		OperationRecordUtils.record( userName, OperationRecord.OperationType.AddUser, "User[" + userName + "] add user:" + userID + ", mail=" + mail + ",role:" + role );
	} catch ( Exception e ) {
		LogUtils.getServiceLog().error( "Create User failed, userId=" + userID + ", mail=" + mail + ",role=" + role, e );
		out.write( "Create User failed, userId=" + userID + "," + e.getMessage() );
	}
%>