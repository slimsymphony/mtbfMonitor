<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
	String siteName = null;
	if(request.getParameter("site")!=null){
		siteName = request.getParameter("site");
		session.setAttribute("site",siteName);
	}else{
		if(session.getAttribute("site")!=null){
			siteName = (String) session.getAttribute("site");
		}
	}
	StationService ss = StationServiceFactory.getInstance();
	List<Station> stations = ss.getAllStationsBySite(siteName);
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Station Maintain</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<style type="text/css">
body{
	/*text-align: center;*/
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

table{
	width: 50%;
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
	$('#backbtn').click(function(){
		//window.history.back();
		location.href='maintain.jsp';
	});
	$('#newStation').click(function(){
		$( "#new-form" ).dialog('open');
	});
	
	$( "#new-form" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 250,
		width: 450,
		modal: true,
		buttons: {
			"Add": function(){
				if($.trim($('#name').val())==''){
					alert("Station Name can't be Null");
					return;
				}
				$.get( "newStation.jsp", 
					   	{name:$('#name').val(),status:$('#status2').val(),siteName:$('#site').val()}, 
						function(data, textStatus){
					   		if($.trim(data)=='true'){
					   			location.reload();
					   		}else{
					   			alert("Add new Station failed:"+data);
					   		}
				  		},
				  		'text'
				);
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		},
		open: function(event, ui) { 
		},
		close: function() {
		}
	});
	
	$( "#update-form" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 250,
		width: 450,
		modal: true,
		buttons: {
			"Update": function(){
				if($.trim($('#name2').val())==''){
					alert("PcName can't be Null");
					return;
				}
				$.get( "updateStation.jsp", 
					   	{id:$('#stationID').val(),name:$('#name2').val(),status:$('#status').val(),siteName:$('#siteName').val()}, 
						function(data, textStatus){
					   		if($.trim(data)=='true'){
					   			location.reload();
					   		}else{
					   			alert("Update Station failed:"+data);
					   		}
				  		},
				  		'text'
				);
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		},
		open: function(event, ui) { 
		},
		close: function() {
		}
	});
});

function updateStation( id, name, status, site ){
	$( '#name2' ).val(name);
	$( '#status' ).val(status);
	$( '#stationID' ).val(id);
	$( '#siteName' ).val(site);
	$( "#update-form" ).dialog('open');
}
function delStation(id){
	if(!window.confirm('Are you sure to delete this Station?'))
		return;
	$.get( "delStation.jsp", 
		   	{id:id}, 
			function(data, textStatus){
		   		if($.trim(data)=='true'){
		   			location.reload();
		   		}else{
		   			alert("Delete Station failed:"+data);
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
		<input id="newStation" type="button" class="ui-widget" value="Add Station" />
		<button id="backbtn" class="ui-widget">Back</button>
	</div>
	<br/>
	<br/>
		<table class="ui-widget">
		<thead class="ui-widget-header">
			<tr>
				<th>PcName</th>
				<th>Status</th>
				<th>Operation</th>
			</tr>
		</thead>
			<%for( Station p : stations) {%>
			<tr>
				<td><%=p.getPcName()%></td>
				<td>
					<%
						if(p.getUsed()==Station.USED){
					%>USED
					<%
						}else if(p.getUsed()==Station.FREE){
					%>FREE
					<%
						}else if(p.getUsed()==Station.UNKNOWN){
					%>UNKNOWN<%
						}
					%>
				</td>
				<td>
					<input onclick="delStation('<%=p.getId()%>')" type="button" value="Delete" />&nbsp;&nbsp;
					<input onclick="updateStation('<%=p.getId()%>','<%=p.getPcName()%>','<%=p.getUsed()%>','<%=p.getSiteName()%>')" type="button" value="Update" />
				</td>
			</tr>
			<%} %>
		<tfoot class="ui-widget-header">
			<tr>
				<th colspan="3"><hr/></th>
			</tr>
		</tfoot>
		</table>
	</div>
	
	<div id="new-form" title="New Station">
		<form>
			<fieldset>
				<label for="name">PC Name:</label>
				<input type="text" id="name" name="name" /><br/>
				<label for="status2">Status :</label>
				<select id="status2" name="status2">
					<option value="1">USED</option>
					<option value="0">FREE</option>
					<option value="-1">UNKNOWN</option>
				</select>
				<input type="hidden" id="site" value="<%=siteName%>" />
			</fieldset>
		</form>
	</div>
	
	<div id="update-form" title="Update Station">
		<form>
			<fieldset>
				<label for="name">PC Name:</label>
				<input type="text" id="name2" name="name2" /><br/>
				<!-- <label for="status">Status :</label>
				<select id="status" name="status">
					<option value="1">NORMAL</option>
					<option value="0">MISSING</option>
					<option value="-1">FAULT</option>
				</select>
				 -->
				<input type="hidden" id="stationID" />
				<input type="hidden" id="siteName" />
			</fieldset>
		</form>
	</div>
</div>
</body>
</html>