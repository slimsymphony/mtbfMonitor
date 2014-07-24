<%@include file="header.jsp"%><%@page contentType="text/html;charset=UTF-8"%><%@page import="com.nokia.testingservice.austere.service.*"%><%@page import="com.nokia.testingservice.austere.model.*"%><%@page import="com.nokia.testingservice.austere.util.*"%><%@page import="java.util.*"%><%
int taskId = CommonUtils.parseInt(request.getParameter("taskId"),0);
String stationIds = request.getParameter("stationIds");
if(taskId<=0){
	out.print("Invalid Task ID:"+taskId);
}
List<Integer> sids = new ArrayList<Integer>();
if(stationIds!=null && !stationIds.isEmpty()){
	for(String idr : stationIds.split(",")){
		int id = CommonUtils.parseInt(idr,0);
		if(!sids.contains(id))
			sids.add(id);
	}
}
TaskService ts = TaskServiceFactory.getInstance();
try{
	ts.setTaskStations(taskId, sids);
	out.print("true");
}catch(Exception e){
	out.print(e.getMessage());
}
%>