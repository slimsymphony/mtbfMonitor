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
	String valid = request.getParameter("isvalid");
	String instanceName = request.getParameter("instanceName");
	ProductService ps = ProductServiceFactory.getInstance();
	try{
		if(valid!=null && valid.equals( "checked" ))
			ps.updateProduct( name, true, instanceName );
		else
			ps.updateProduct( name, false, instanceName );
		OperationRecordUtils.record( userName, OperationRecord.OperationType.UpdateProduct, "User["+userName+"] update Product:"+name+",valid:"+valid );
		out.write("true");
	}catch(Exception e){
		LogUtils.getServiceLog().error("Update Product failed, Product="+name+",valid="+valid, e);
		out.write("false");
	}
%>