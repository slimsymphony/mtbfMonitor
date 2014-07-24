<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Welcome to Austere</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript">
$(function(){
	$( "#site-form" ).dialog({
		autoOpen: true,
		show: "fold",
		hide: "explode",
		height: 250,
		width: 450,
		modal: true,
		buttons: {
			"Select": function(){
				location.href='index.jsp?site='+$('#site').val();
			}
		},
		close: function() {
			location.reload();
		}
	});
});
</script>
</head>
<body>
	<div id="site-form" title="Select Site">
		<form>
			<fieldset>
				<label for="site">Select Site:</label>
				<select id="site">
				<% for(String site: DbUtils.databases.keySet()) {%>
					<option value="<%=site %>"><%=site %></option>
				<%} %>
				</select>
			</fieldset>
		</form>
	</div>
</body>
</html>