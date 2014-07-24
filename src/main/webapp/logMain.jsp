<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
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
	text-align: center;
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

#options{
	text-align: left;
}
#logRoller{
	border-width:1px;
	text-align: left;
}
</style>
<script type="text/javascript">
$(function() {
	$('#logSelect').change(
		function(){
			if($('#logSelect').val()=='')
				return;
			$.get( "logReader.jsp", {logSelect:$('#logSelect').val(),logDate:$('#logDate').val()}, function(data, textStatus){
				$("#logRoller").html(data);
			  },'text' 
			);
		}
	);
	$('#logDate').change(
			function(){
				$.get( "logReader.jsp", {logSelect:$('#logSelect').val(),logDate:$('#logDate').val()}, function(data, textStatus){
					$("#logRoller").html(data);
				  },'text' 
				);
			}
		);
});
</script>
</head>
<body>
<div id="options">
	<label for="logSelect">Log Type</label>
	<select id="logSelect" name="logSelect">
		<option value="">&nbsp;</option>
		<option value="db">db log</option>
		<option value="service">service log</option>
		<option value="schedule">schedule log</option>
		<option value="web">web log</option>
		<option value="root">Root log</option>
	</select>
	<label for="logDate">Log Date</label>
	<select id="logDate" name="logDate">
		<option value="0">today</option>
		<option value="-1">yesterday</option>
		<option value="-2">the day before yesterday</option>
		<option value="-3">three days ago</option>
	</select>
</div>
<div id="logRoller">
</div>
</body>
</html>