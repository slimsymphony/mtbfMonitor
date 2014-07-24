<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
UserService us = UserServiceFactory.getInstance();
List<User> users = us.getAllUsers();
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
	$('#addUser').click(function(){
		$( "#new-form" ).dialog( "open" );
	});
	
	$( "#update-form" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 150,
		width: 350,
		modal: true,
		buttons: {
 			"Update": function() {
 					$( this ).dialog( "close" );
					$.post( "updateUser.jsp", 
					{userID:$('#userID').val(),role:$("#Role").val()}, 
					function(data,status){
						if($.trim(data)=='true'){
							location.reload();
						}else if(data.responseText && $.trim(data.responseText)=='true'){
							location.reload();
						}
						else{
							alert("Update User failed:"+data);
							$( this ).dialog( "open" );
						}
					} 
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
	
	$( "#new-form" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 300,
		width: 450,
		modal: true,
		buttons: {
 			"Add": function() {
	 				if( $.trim($('#newUserID').val())=='' && $.trim($('#newMail').val())=='' ){
						alert('You have to enter either NOE or mail of user to add');
						return false;
					}
 					$( this ).dialog( "close" );
					$.post( "addUser.jsp", 
					{userID:$('#newUserID').val(),role:$("#newRole").val(),mail:$('#newMail').val()}, 
					function(data,status){
						if($.trim(data)=='true'){
							location.reload();
						}else if(data.responseText && $.trim(data.responseText)=='true'){
							location.reload();
						}
						else{
							alert("Add User failed:"+data);
							$( this ).dialog( "open" );
						}
					} 
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

function update(userID,currentRole){
	$( '#userID' ).val(userID);
	$( '#Role' ).val(currentRole);
	$( "#update-form" ).dialog( "open" );
}

function del(userID){
	if(!window.confirm("Are you sure to delete this user?")){
		return;
	}
	$.get( "delUser.jsp", 
		   	{userID:userID}, 
			function(data, textStatus){
		   		if($.trim(data)!='true'){
		   			location.reload();
		   		}else{
		   			alert(data);
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
		<input type="button" id="addUser" value="Add User" class="ui-widget"/>
		<input type="button" class="ui-widget" name="back" value="back" onclick="location.href='maintain.jsp'"/>
	</div>
	<br/>
		<table class="ui-widget">
			<thead class="ui-widget-header">
				<tr>
					<th>NOE</th>
					<th>mail</th>
					<th>RegisterTime</th>
					<th>Role</th>
					<th>Operation</th>
				</tr>			
			</thead>
			<%for(User user:users) {%>
			<tr>
				<td><%=user.getUserID( ) %></td>
				<td><%=user.getMail(  ) %></td>
				<td><%=user.getCreateTime(  ) %></td>
				<td><%=user.getRole( ) %></td>
				<td>
					<input type="button" name="update" onclick="update('<%=user.getUserID( ) %>','<%=user.getRole( ) %>')" value="update"/>
					<input type="button" name="delete" onclick="del('<%=user.getUserID( ) %>')" value="delete"/>
				</td>
			</tr>
			<%} %>
		</table>
	</div>
	<div id="new-form" title="Add User">
		<form>
			<fieldset>
				<span>You can input either NOE or mail of a User to add</span><br/>
				<label for="newUserID">NOE</label>
				<input type="text" id="newUserID" value=""/><br/>
				<label for="newMail">Mail</label>
				<input type="text" id="newMail" value=""/><br/>
				<label for="newRole">Role</label>
				<select name="newRole" id="newRole">
				<%for(User.Role role : User.Role.values(  )){ %>
				<option value="<%=role%>"><%=role%></option>
				<%} %>
				</select>
			</fieldset>
		</form>
	</div>
	<div id="update-form" title="Update User">
		<form>
			<fieldset>
				<label for="Role">Role</label>
				<select name="Role" id="Role">
				<%for(User.Role role : User.Role.values(  )){ %>
				<option value="<%=role%>"><%=role%></option>
				<%} %>
				</select>
				<input type="hidden" id="userID" value=""/>
			</fieldset>
		</form>
	</div>
</body>
</html>