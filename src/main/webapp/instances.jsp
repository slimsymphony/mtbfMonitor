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
<title>InstanceName Maintain</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<style type="text/css">
body{
	/*text-align: center;*/
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

table{
	width: 99%;
}
</style>
<script type="text/javascript">
$(function(){
	
	$('#backbtn').click(function(){
		location.href='maintain.jsp';
	});
	
	$('#newInstanceBtn').click(function(){
		var instanceName = window.prompt('Please input InstanceName');
		if(instanceName==null)
			return;
		if($.trim(instanceName)==''){
			alert('Instance Name must not null');
			return;
		}
		$.get( "newInstance.jsp", 
			   	{instanceName:instanceName}, 
				function(data, textStatus){
			   		if($.trim(data)=='true'){
			   			location.reload();
			   		}else{
			   			alert("Add new Instance failed:"+$.trim(data));
			   		}
		  		},
		  		'text'
		);
	});
});


function updateInstance(instanceName){
	var newInstanceName = window.prompt('Please input New instance Name', instanceName);
	if(newInstanceName==null)
		return;
	if(newInstanceName==''){
		alert('Instance name must not null');
		return;
	}
	$.post( "updateInstanceName.jsp", 
		   	{oldName:instanceName,newName:newInstanceName}, 
			function(data, textStatus){
		   		if($.trim(data)=='true'){
		   			location.reload();
		   		}else{
		   			alert("Update instanceName failed:"+$.trim(data));
		   		}
	  		},
	  		'text'
	);
}

function deleteInstance(instanceName){
	if(!window.confirm('Are you sure to delete this InstanceName?'))
		return;
	var cascade = window.confirm('Do you want to Cascade Delete all the product with this InstanceName?');
	$.post( "deleteInstanceName.jsp", 
		   	{instanceName:instanceName,cascade:cascade}, 
			function(data, textStatus){
		   		if($.trim(data)=='true'){
		   			location.reload();
		   		}else{
		   			alert("delete instanceName failed:"+$.trim(data));
		   		}
	  		},
	  		'text'
	);
}


</script>
</head>
<body>
	<div id="content">
		<table class="ui-widget" >
		<thead class="ui-widget-header">
			<tr>
			<td style="text-align:left;" colspan="6"><input id="newInstanceBtn" type="button" value="Add Instance" /></td>
			</tr>
			<tr>
				<th>Instance Name</th>
				<th>Operation</th>
			</tr>
		</thead>
		<% for(String instance : DbUtils.instances) {%>
			<tr>
				<td><%= instance %></td>
				<td>
					<input type="button" value="Update" onclick="updateInstance('<%=instance%>')"/>
					<input type="button" value="Delete" onclick="deleteInstance('<%=instance%>')"/>
				</td>
			</tr>
		<% } %>
		<tfoot class="ui-widget-header">
			<tr>
				<th colspan="2"><hr/></th>
			</tr>
		</tfoot>
		</table>
		<button id="backbtn" class="ui-widget">Back</button>
	</div>
</body>
</html>