<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
	List<OperationRecord> ors = OperationRecordUtils.getAllRecords();
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Welcome to Austere</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<style type="text/css">
body{
	/*text-align: center;*/
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}
body .divMain{
	overflow:auto;
    }
.divMain #floatDiv {
	position:absolute;
}
</style>
<script type="text/javascript">
$(function(){
	$(window).scroll(function () {
		var pos = $(document).scrollTop()+"px";
		$('#floatDiv').animate({top:pos},{duration:500,queue:false});
	});
});

function refresh(){
	$.get( "getEmergency.jsp", 
		   	{}, 
			function(data, textStatus){
		   		if($.trim(data)!=''){
		   			
		   		}
	  		},
	  		'text'
	);
}
</script>
</head>
<body>
	<div class="divMain">
		<div id="floatDiv">
			<input class="ui-widget" type="button" value="back" onclick="location.href='maintain.jsp';"/>
		</div>
		<br/>
		<table class="ui-widget" style="width:80%">
			<thead class="ui-widget-header">
				<tr>
					<th>Type</th>
					<th>User</th>
					<th>Time</th>
				</tr>
			</thead>
				<%for( OperationRecord p : ors) {%>
				<tr title="<%=p.getDetails()%>">
					<td><%=p.getType()%></td>
					<td><%=p.getUserID()%></td>
					<td><%=p.getOperationTime() %></td>
					
				</tr>
				<%} %>
			<tfoot class="ui-widget-header">
				<tr>
					<th colspan="3"><hr/></th>
				</tr>
			</tfoot>
		</table>
	</div>
</body>
</html>