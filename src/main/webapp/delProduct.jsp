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
	
	ProductService ps = ProductServiceFactory.getInstance();
	try{
		ps.delProduct( name );
		OperationRecordUtils.record( userName, OperationRecord.OperationType.DeleteProduct, "User["+userName+"] delete Product:"+name );
		out.write("true");
	}catch(Exception e){
		LogUtils.getServiceLog().error("Delete Product failed, Product="+name, e);
		out.write("false");
	}
%>