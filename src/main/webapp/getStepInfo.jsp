<%@include file="header.jsp"%>
<%@page contentType="text/json;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	String siteName = "TEST";
	if(request.getParameter("site")!=null){
		siteName = request.getParameter("site");
		session.setAttribute("site",siteName);
	}else{
		if(session.getAttribute("site")!=null){
			siteName = (String) session.getAttribute("site");
		}
	}
	String id = request.getParameter("id");
	String product = request.getParameter("product");
	if(id == null || product== null){
		out.print("{error:'Invalid Id or Product'}");
		return;
	}
	StatusService ss = StatusServiceFactory.getInstance();
	String json = ss.getRecentSteps( siteName, product, id, 10 );
	out.print(json);
%>