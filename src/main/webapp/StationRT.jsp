<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Comparation of Stations between plan and Realtime</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/modules/exporting.js"></script>
<style type="text/css">
body{
	/*text-align: center;*/
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}
</style>
<script type="text/javascript">
$(function(){
	
	var chart1; // globally available
	$(document).ready(function() {
	      chart1 = new Highcharts.Chart({
	         chart: {
	            renderTo: 'container',
	            type: 'column'
	         },
	         title: {
	            text: 'Comparation of running Stations between plan and Realtime'
	         },
	         xAxis: {
	            categories: ['1225.1', '1225.2', '1225.3', '1225.4', '1225.5']
	         },
	         yAxis: {
	            title: {
	               text: 'Station Count'
	            }
	         },
	         series: [{
	        	type: 'spline',
	            name: 'Plan',
	            data: [5, 5, 5, 5, 5,5, 5, 5, 5, 5,5, 5, 5, 5, 5,5, 5, 5, 5, 5,5, 5, 5, 5, 5]
	         }, {
	            type: 'column',
	            name: 'Real-Situation',
	            data: [5, 2, 3, 4, 4,5, 2, 3, 4, 4,5, 2, 3, 4, 4,5, 2, 3, 4, 4,5, 2, 3, 4, 4]
	         }]
	      });
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
	<div id="container" style="width: 100%; height: 400px"></div>
</body>
</html>