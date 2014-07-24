<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	String userName = "";
	boolean isAuth = false;
	if ( session.getAttribute( "userInfo" ) != null ) {
		User userInfo = ( User ) session.getAttribute( "userInfo" );
		userName = userInfo.getUserID();
		isAuth = true; 
	}
	String taskScope = request.getParameter( "taskScope" );
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="pragma" content="no-cache" />
<!-- <meta http-equiv="refresh" content="30" />  -->
<title>TaskView</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript">
$(function() {
	$('#refresh').button().click(
		function(){
			document.getElementById("ifm").contentDocument.location.reload(true);
		}
	);
});
function updateScope(){
	document.form1.submit();
}

function logoff(){
	$.get( "logout.do", {}, function(data, textStatus){
		if($.trim(data)=='true'){
			//alert("Successful logout!");
			parent.location.reload();
		}else if(data.responseText && $.trim(data.responseText)=='true'){
			//alert("Successful logout!");
			parent.location.reload();
		}else{
			alert(data);
		}
	  },'text'
	);
}
</script>
<style type="text/css">
body{
	text-align: center;
}
iframe{
	width:100%;
	height:500px;
	border: 0px;
}
div{
text-align:left;
}
</style>
</head>
<body>
	<div>
		<form name="form1" method="post" action="">
		<img id="refresh" title="refresh" src="images/refresh.png" />
		<%
			if ( isAuth ) {
		%>
			<select name="taskScope" id="taskScope" onChange="updateScope()" class="text ui-widget-content ui-corner-all">
				<option <%if ( taskScope == null || taskScope.equals( "private" ) ) {%>
					selected <%}%> value="private">Private</option>
				<option <%if ( taskScope != null && taskScope.equals( "all" ) ) {%> selected
					<%}%> value="all">All</option>
			</select>
		<%
			}
		%>
		</form>
		<iframe id="ifm" src="taskList.jsp?taskScope=<%=taskScope%>"></iframe>
	</div>
</body>
</html>