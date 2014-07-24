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
<!-- script type="text/javascript" src="js/jquery.jplayer.min.js"></script-->
<style type="text/css">
body{
	/*text-align: center;*/
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

#emRoller{
	border-width:1px;
}
</style>
<script type="text/javascript">
$(function(){
	$("#accordion").accordion({ 
		header: "h3",
		collapsible: true,
		autoHeight: false,
		navigation: true });
	
	//historyEm();
});


function refresh(){
	$('#source').html("[Realtime]");
	$("#accordion").html("");
	$.post( "getEmergency.jsp", 
		   	{}, 
			function(data, textStatus){
		   		if($.trim(data)!=''){
		   			//data.replace("\n", "<br/>");
		   			var ems = jQuery.parseJSON(data);
		   			var size = ems.length;
					for(var i=0;i<size;i++){
						//var ndiv = 
						$("#accordion").append($("<h3><a href='#'>"+ems[i].source+" @"+ems[i].emergencyTime+"</a></h3><div><p>"+ems[i].detail+"</p></div>")).accordion('destroy').accordion();
						//ndiv.append($("<h3></h3>")).text(ems[i].source).toggleClass("demoHeaders");
						//ndiv.append($("<div></div>")).text(ems[i].detail);
						//$('#accordion').append($('<div><h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-state-active ui-corner-top" role="tab" aria-expanded="true" aria-selected="true" tabindex="0"><span class="ui-icon ui-icon-triangle-1-s"></span><a href="#">'+ems[i].source+"@"+ems[i].emergencyTime+'</a></h3><div class="ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active" role="tabpanel">'+ems[i].detail+'</div></div>'))
					}
		   		}
	  		},
	  		'text'
	);
}

function historyEm(){
	$('#source').html("[History]");
	$("#accordion").html("");
	$.post( "getEmergency.jsp?history=true", 
		   	{}, 
			function(data, textStatus){
		   		if($.trim(data)!=''){
		   			//data.replace("\n", "<br/>");
		   			var ems = jQuery.parseJSON(data);
		   			var size = ems.length;
					for(var i=0;i<size;i++){
						//var ndiv = 
						$("#accordion").append($("<h3><a href='#'>"+ems[i].source+" @"+ems[i].emergencyTime+"</a></h3><div><p>"+ems[i].detail+"</p></div>")).accordion('destroy').accordion();
						//ndiv.append($("<h3></h3>")).text(ems[i].source).toggleClass("demoHeaders");
						//ndiv.append($("<div></div>")).text(ems[i].detail);
						//$('#accordion').append($('<div><h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-state-active ui-corner-top" role="tab" aria-expanded="true" aria-selected="true" tabindex="0"><span class="ui-icon ui-icon-triangle-1-s"></span><a href="#">'+ems[i].source+"@"+ems[i].emergencyTime+'</a></h3><div class="ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom ui-accordion-content-active" role="tabpanel">'+ems[i].detail+'</div></div>'))
					}
		   		}
	  		},
	  		'text'
	);
}
window.setTimeout(refresh, 600000);
</script>
</head>
<body>
<div id="emRoller">
	<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" role="button" value="RealTime" onclick="refresh()" />&nbsp;
	<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" role="button" value="History" onclick="historyEm()" />
	<!-- Accordion -->
		<h2 class="demoHeaders">Emergency<span id="source"></span></h2>
		<div id="accordion">
		</div>
</div>
</body>
</html>