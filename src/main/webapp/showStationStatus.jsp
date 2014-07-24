<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%!
	private String trans(String status){
		if(!status.equals("NotExist") && !status.equals("NotRunning")){
			return "images/testing.gif";
		}else if(status.equals("NotRunning")){
			return "images/waiting.png";
		}else{
			return "images/stop.png";
		}
	}
%>
<%
	String userName = "";
	if ( session.getAttribute( "userInfo" ) != null ) {
		User userInfo = ( User ) session.getAttribute( "userInfo" );
		userName = userInfo.getUserID();
	}
	String name = request.getParameter("name");
	String site = null;
	if(request.getParameter("site")!=null){
		site = request.getParameter("site");
		session.setAttribute("site",site);
	}else{
		if(session.getAttribute("site")!=null){
			site = (String) session.getAttribute("site");
		}
	}
	int taskId = CommonUtils.parseInt(request.getParameter("taskId"), 0);
	StationService ss = StationServiceFactory.getInstance();
	Map<Station,String> status = ss.syncStationStatus(site,taskId);
	int total=0,running=0,waiting=0,na= 0;
	for(Map.Entry<Station,String> entry:status.entrySet()){
		String s = entry.getValue();
		total ++;
		if(s.equals("NotExist")){
			na ++ ;
		}else if (s.equals("NotRunning")){
			waiting++;
		}else{
			running++;
		}
	}
%>
<html>
<head>
	<title>Stations Status</title>
	<meta http-equiv="refresh" content="150" />
	<meta http-equiv="pragma" content="no-cache"> 
	<meta http-equiv="cache-control" content="no-cache"> 
	<meta http-equiv="expires" content="0">
	<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
	<style type="text/css">
			body {
				font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
			}
			
			.main {
				margin: auto;
				position: relative;
				width: 100%;
			}
			
			.urgent {
				color: red;
				font-size: 200%;
				font-weight: bolder;
			}
			
			.normal {
				color: green;
				font-size: 150%;
				font-weight: bolder;
			}
			
			.content {
				width: 100%;
				border-width: 2px;
				border-style: solid;
				border-color: black;
				vertical-align: bottom;
			}
			
			td {
				text-align: center;
				border-width: 1px;
				border-style: dashed;
				border-color: black;
			}
			label{
				color: bule;
				font-weight:bolder;
			}
			
		</style>
		<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script>
$(function(){
	$.post( "getTaskByProduct.jsp", 
			{ site:'<%=site%>' }, 
			function(data,status){
				var tasks = jQuery.parseJSON(data);
				var size = tasks.length;
				$("#task").find('option').remove().end();
				$("#task").append($("<option></option>").attr("value","0").text("ALL"));
				$("#task").append($("<option></option>").attr("value","-1").text("FREE"));
				for(var i=0;i<size;i++){
					$("#task").append($("<option></option>").attr("value",tasks[i].taskID).text(tasks[i].product+"_"+tasks[i].milestone)); 
				}
				$('#task').val(<%=taskId%>);
			},
			'text'
	);
	$('#task').change(function(){
		var taskId = $('#task').val();
		self.location.href='showStationStatus.jsp?taskId='+taskId;
	});
	
	$( "#deviceInfo" ).dialog({
		autoOpen: false,
		height: 350,
		width: 450,
		modal: false,
		open: function(event, ui) { 
		}
	});
});
function showDetail(pcname){
	$.post( "getStationDetail.jsp", 
			{ site:'<%=site%>',pcname:pcname }, 
			function(msg,status){
					if(!msg || msg==''){
						return;
					}
					var content = '';
					var xmlDoc = $(jQuery.parseXML(msg));
					$xml = $( xmlDoc );
				    $pc = $xml.find( "PCSystemName" );
				    if($pc){
				    	content += "<label>Pc System:</label><span>"+$pc.text()+"</span><br/>";
				    }
					$cpu = $xml.find( "CPUName" );
					if($cpu){
						content += "<label>CPU:</label><span>"+$cpu.text()+"</span><br/>";
					}
					$mem = $xml.find( "Memory" );
					if($mem){
						content += "<label>Memory:</label><span>"+$mem.text()+"</span><br/>";
					}
					$av = $xml.find( "AustereVerison" );
					if($av){
						content += "<label>Austere Verison:</label><span>"+$av.text()+"</span><br/>";
					}
					$ics = $xml.find( "IowCard" );
					if($ics){
						content += "<label>IowCards:</label><ul>";
						$ics.each(function(){
							content +="<li>"+$(this).children('ID').text()+" - "+$(this).children('Name').text()+"</li>";
						});
						content += "</ul>";
					}
					$('#deviceInfo').html(content);
					$( "#deviceInfo" ).dialog( "open" );
			},
			'text'
	);
}
</script>
</head>
<body>
<select id="task"></select> Total Stations <span style="font-size:14pt;font-weight:bold;color:blue"><%=total %></span>, using Stations <span style="font-size:14pt;font-weight:bold;color:green"><%=running %></span>, Idle Stations <span style="font-size:14pt;font-weight:bold;color:brown"><%= waiting%></span>, NonExist Station <span style="font-size:14pt;font-weight:bold;color:red"><%= na %> </span>
<table class="content">
<tr>
<th>Station</th>
<th>Status</th>
</tr>
<%for(Map.Entry<Station,String> entry:status.entrySet()){
	Station st = entry.getKey();
	String s = entry.getValue();
	String product = "";
	if(!s.equals("NotExist") && !s.equals("NotRunning")){
		product = "Using For "+s;
	}else{
		product = s;
	}
%>
<tr>
<td><img src="<%=trans(s) %>" width="20px" /><strong><%=st.getPcName()%></strong><%if(!s.equals("NotExist")){%>&nbsp;<a href='javascript:void(0)' onclick='showDetail("<%=st.getPcName()%>")'>detail</a><%} %></td>
<td><%=product%></td>
</tr>
<%} %>
</table>

<div id="deviceInfo" style="border:2px solid #dddddd" title="Device Info">
		
	</div>
</body>
</html>