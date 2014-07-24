<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.util.LogReader.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%
	LogType type = LogType.parse(request.getParameter("logSelect"));
	DateType date = DateType.parse(CommonUtils.parseInt(request.getParameter("logDate"), 0 ));
	out.println(LogReader.getLog(type,date,CommonUtils.getWebRootPath(request,"/")));
%>