<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	int taskId = Integer.parseInt( request.getParameter("taskId") );
	int start = Integer.parseInt( request.getParameter("start") );
	int end = Integer.parseInt( request.getParameter("end") );
	BookService bs = BookServiceFactory.getInstance();
	List<Workload> ws = bs.getWorkloadsByTask(taskId);
	for(Workload w: ws){
		if(start>w.getWk())
			start = w.getWk();
		if(end<w.getWk())
			end = w.getWk();
	}
	Map<String,List<Workload>> wls = bs.getWorkloadsByTaskID(taskId);
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>TaskView</title>
<link type="text/css"
	href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript">
	
</script>
<style type="text/css">
body {
	text-align: center;
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

table {
	width: 100%;
	border-width: 2px;
	border-style: solid;
	border-color: black;
	vertical-align: bottom;
}

th {
	text-align: center;
	border-width: 1px;
	border-style: dashed;
	border-color: brown;
	nowrap: true;
}

td {
	text-align: center;
	border-width: 1px;
	border-style: dashed;
	border-color: brown;
	nowrap: true;
}

.book {
	background-color: yellow;
}

.work {
	background-color: green;
}

.done {
	background-color: blue;
}

.problem {
	background-color: red;
}
</style>
</head>
<body>
	<table class="ui-widget">
		<tr>
		<td><label>Legend:</label></td>
		<td class="book">Booked</td>
		<td class="work">Running</td>
		<td class="done">Finished</td>
		<td class="problem">Problem</td>
		</tr>
	</table>
	<table class="ui-widget">
		<thead class="ui-widget-header">
		<tr>
			<th>Status/Station</th>
			<%
				for(int i=0;i<(end-start)+1;i++){
			%>
			<td>Week <%= start+i%></td>
			<%} %>
		</tr>
		</thead>
		<%
		for(String stationName : wls.keySet()){
			List<Workload> ls = wls.get(stationName);
		%>
		<tr>
			<th><%=stationName %></th>
			<% int should = start;
			for( Workload wl : ls){
				int curr = wl.getWk();
				while( curr > should){
					out.println("<td>&nbsp;</td>");
					should++;
			    }%>
			 <td
				class='<%if(wl.getStatus()==Constants.STATION_STATUS_BOOK){%>
						book
						<%}else if(wl.getStatus()==Constants.STATION_STATUS_WORK){ %>
						work
						<%}else if(wl.getStatus()==Constants.STATION_STATUS_DONE){%>
						done
						<%}else if(wl.getStatus()==Constants.STATION_STATUS_PROBLEM){%>
						problem
				<%}%>'>
				<img width="40px" src="images/station.png"/> <%= wl.getWk()%>
			</td>	
			<%should++;}
		}%>
		</tr>
	</table>
</body>
</html>