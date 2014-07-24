<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>TaskView</title>
<style type="text/css">
body{
font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
font-size: 22px;
}
</style>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript">
$(function(){
	$('#lpm').click(function(e){
		e.preventDefault();
		location.href='products.jsp';
	});
	$('#lum').click(function(e){
		e.preventDefault();
		location.href='users.jsp';
	});
	$('#lsm').click(function(e){
		e.preventDefault();
		location.href='stations.jsp';
	});
	$('#lor').click(function(e){
		e.preventDefault();
		location.href='operationRecord.jsp';
	});
	$('#lst').click(function(e){
		e.preventDefault();
		location.href='sites.jsp';
	});
	$('#lin').click(function(e){
		e.preventDefault();
		location.href='instances.jsp';
	});
});
</script>
</head>
<body>
<div>
	<ul>
		<li><a id="lpm" href="">Product Maintain</a></li>
		<li><a id="lum" href="">User Maintain</a></li>
		<li><a id="lsm" href="">Station Maintain</a></li>
		<li><a id="lor" href="">Operation Record</a></li>
		<li><a id="lst" href="">Site/DB Info Maintain</a></li>
		<li><a id="lin" href="">InstanceName Maintain</a></li>
	</ul>
</div>
</body>
</html>