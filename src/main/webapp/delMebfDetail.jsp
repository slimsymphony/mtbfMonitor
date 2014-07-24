<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	int seq = CommonUtils.parseInt(request.getParameter("seq"),0);
	MtbfService ms = MtbfServiceFactory.getInstance();
	try{
		ms.deleteMTBFDetail(seq);
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
%>