<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
	StationService ss = StationServiceFactory.getInstance();
	List<Station> stations = ss.getAllStations();
	int i=0;
%>
<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<title>Stations Status</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<style type="text/css">
body{
	background-color: white;
}
.style1{
	background-color: blue;
	color: white;
}
.style-1{
	background-color: red;
}
.style0{
	background-color: yellow;
	color: black;
}
</style>
</head>
<body>

<table width="100%" height="100%"  class="ui-widget">
	<thead class="ui-widget-header">
		<tr>
		<th>Legend</th>
		<th class="style1">Normal</th>
		<th class="style-1">Fault</th>
		<th class="style0">Missing</th>
		</tr>
	</thead>
	<tr>
	<%for(Station station:stations){ 
		if(i%5==0){%>
	</tr>
	<tr>			
	<%}%>
		<td class="style<%=station.getUsed()%>"><img height="40px" src="images/station.png"/><%=station.getPcName()%></td>
	<%
		i++;
		}
	%>
	</tr>
</table>

</body>
</html>