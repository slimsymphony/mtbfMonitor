<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
	SiteService ss = SiteServiceFactory.getInstance();
	List<Site> sites = ss.getAllSites();
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Site/DB Maintain</title>
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
	
	$('#newSiteBtn').click(function(){
		$( "#newSite" ).dialog('open');
	});
	
	$( "#newSite" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 250,
		width: 450,
		modal: true,
		buttons: {
			"Add": function(){
				$.get( "newSite.jsp", 
					   	{sitename:$('#sitename').val()}, 
						function(data, textStatus){
					   		if($.trim(data)=='true'){
					   			location.reload();
					   		}else{
					   			alert("Add new Site failed:"+$.trim(data));
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
	
	$( "#updateSite" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 250,
		width: 450,
		modal: true,
		buttons: {
			"Update": function(){
				$.get( "updateSiteName.jsp", 
					   	{sitename:$('#sitename2').val()}, 
						function(data, textStatus){
					   		if($.trim(data)=='true'){
					   			location.reload();
					   		}else{
					   			alert("Update SiteName failed:"+$.trim(data));
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
	
	$( "#newDB" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 350,
		width: 500,
		modal: true,
		buttons: {
			"Add": function(){
				$.get( "newDB.jsp", 
					   	{sitename:$('#sname').val(),url:$('#url').val(),driverClass:$('#driverClass').val(),user:$('#user').val(),password:$('#password').val()}, 
						function(data, textStatus){
					   		if($.trim(data)=='true'){
					   			location.reload();
					   		}else{
					   			alert("Add new Database failed:"+$.trim(data));
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
	
	$( "#updateDB" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 350,
		width: 500,
		modal: true,
		buttons: {
			"Update": function(){
				$.get( "updateDB.jsp", 
						{sitename:$('#sname2').val(),url:$('#url2').val(),driverClass:$('#driverClass2').val(),user:$('#user2').val(),password:$('#password2').val(),
						 sitename2:$('#sname3').val(),url2:$('#url3').val(),driverClass2:$('#driverClass3').val(),user2:$('#user3').val(),password2:$('#password3').val()
						}, 
						function(data, textStatus){
					   		if($.trim(data)=='true'){
					   			location.reload();
					   		}else{
					   			alert("Update Database failed:"+$.trim(data));
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


function updateSite(siteName){
	var newSiteName = window.prompt('Please input New SiteName', siteName);
	if(newSiteName==null)
		return;
	if(newSiteName==''){
		alert('Site name must not null');
		return;
	}
	$.post( "updateSiteName.jsp", 
		   	{oldName:siteName,newName:newSiteName}, 
			function(data, textStatus){
		   		if($.trim(data)=='true'){
		   			location.reload();
		   		}else{
		   			alert("Update SiteName failed:"+$.trim(data));
		   		}
	  		},
	  		'text'
	);
}

function deleteSite(siteName){
	if(!window.confirm('Are you sure to delete this Site?'))
		return;
	$.post( "deleteSite.jsp", 
		   	{siteName:siteName}, 
			function(data, textStatus){
		   		if($.trim(data)=='true'){
		   			location.reload();
		   		}else{
		   			alert("Delete SiteName failed:"+$.trim(data));
		   		}
	  		},
	  		'text'
	);
}

function deleteDb(siteName,url,driverClass,user,password){
	if(!window.confirm('Are you sure to delete this Database?'))
		return;
	$.get( "delDatabase.jsp", 
		   	{siteName:siteName,url:url,driverClass:driverClass,user:user,password:password}, 
			function(data, textStatus){
		   		if($.trim(data)=='true'){
		   			location.reload();
		   		}else{
		   			alert("Delete Database failed:"+$.trim(data));
		   		}
	  		},
	  		'text'
	);
}

function updateDb(siteName,url,driverClass,user,password){
	$('#sname2').val(siteName);
	$('#url2').val(url);
	$('#user2').val(user);
	$('#driverClass2').val(driverClass);
	$('#password2').val(password);
	$('#sname3').val(siteName);
	$('#url3').val(url);
	$('#user3').val(user);
	$('#driverClass3').val(driverClass);
	$('#password3').val(password);
	$('#updateDB').dialog("open");
}

function addDB(siteName){
	$('#sname').val(siteName);
	$('#newDB').dialog("open");
}
</script>
</head>
<body>
	<div id="content">
		<table class="ui-widget" >
		<thead class="ui-widget-header">
			<tr>
			<td style="text-align:left;" colspan="6"><input id="newSiteBtn" type="button" value="Add Site Info" /></td>
			</tr>
			<tr>
				<th rowspan="2">SiteName</th>
				<th colspan="4" align="center">Databases</th>
				<th rowspan="2">Site Operation</th>
			</tr>
			<tr>
				<th>URL</th>
				<th>DriverClass</th>
				<th>User</th>
				<th>DB Operation</th>
			</tr>
		</thead>
			<%for( Site s : sites) {%>
			<tr>
				<td rowspan="<%=s.getDatabases().size()%>"><%=s.getSiteName() %></td>
			<% 	int i=0;
				if(s.getDatabases().size()==0){%>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			<% 	}
				for(DbMeta dm : s.getDatabases()){
				if(i>0){%>
			</tr>
			<tr>
				<% }
			%>
				<td><%=dm.getUrl() %></td>
				<td><%=dm.getDriverClass() %></td>
				<td><%=dm.getUser() %></td>
				<td nowrap>
					<input type="button" value="UpdateDB" onclick="updateDb('<%=dm.getSiteName() %>','<%=dm.getUrl() %>','<%=dm.getDriverClass() %>','<%=dm.getUser() %>','<%=dm.getPassword() %>')"/>
					<input type="button" value="DeleteDB" onclick="deleteDb('<%=dm.getSiteName() %>','<%=dm.getUrl() %>','<%=dm.getDriverClass() %>','<%=dm.getUser() %>','<%=dm.getPassword() %>')"/>
				</td>
			<% i++;} %>
				<td rowspan="<%=s.getDatabases().size()%>" nowrap>
					<input type="button" value="Update Site Name" onclick="updateSite('<%=s.getSiteName() %>')"/>
					<input type="button" value="Delete Site" onclick="deleteSite('<%=s.getSiteName() %>')"/>
					<input type="button" value="Add Database" onclick="addDB('<%=s.getSiteName() %>')"/>
				</td>
			</tr>
			<%} %>
		<tfoot class="ui-widget-header">
			<tr>
				<th colspan="6"><hr/></th>
			</tr>
		</tfoot>
		</table>
		<button id="backbtn" class="ui-widget">Back</button>
	</div>
	
	<div id="newSite" title="New Site">
		<form>
			<fieldset>
				<label for="sitename">Site Name:</label>
				<input type="text" id="sitename" name="sitename" /><br/>
			</fieldset>
		</form>
	</div>
	
	<div id="updateSite" title="Update Site">
		<form>
			<fieldset>
				<label for="sitename2">Site Name:</label>
				<input type="text" id="sitename2" name="sitename2" /><br/>
			</fieldset>
		</form>
	</div>
	
	<div id="newDB" title="New Database">
		<form>
			<fieldset>
				<label for="sname">Site Name:</label>
				<input type="text" id="sname" name="sname" readonly /><br/>
				<label for="url">URL:</label>
				<input type="text" id="url" name="url" /><br/>
				<label for="driverClass">DriverClass:</label>
				<input type="text" id="driverClass" name="driverClass" /><br/>
				<label for="user">User:</label>
				<input type="text" id="user" name="user" /><br/>
				<label for="password">Password:</label>
				<input type="password" id="password" name="password" /><br/>
			</fieldset>
		</form>
	</div>
	
	<div id="updateDB" title="Update Database">
		<form>
			<fieldset>
				<label for="sname2">Site Name:</label>
				<input type="text" id="sname2" name="sname2" readonly /><br/>
				<label for="url2">URL:</label>
				<input type="text" id="url2" name="url2" /><br/>
				<label for="driverClass2">DriverClass:</label>
				<input type="text" id="driverClass2" name="driverClass2" /><br/>
				<label for="user2">User:</label>
				<input type="text" id="user2" name="user2" /><br/>
				<label for="password2">Password:</label>
				<input type="password" id="password2" name="password2" /><br/>
				
				<input type="hidden" id="sname3" name="sname2" readonly />
				<input type="hidden" id="url3" name="url2" />
				<input type="hidden" id="driverClass3" name="driverClass2" />
				<input type="hidden" id="user3" name="user2" /><br/>
				<input type="hidden" id="password3" name="password2" />
			</fieldset>
		</form>
	</div>
	
</body>
</html>