<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.nokia.testingservice.austere.service.*"%>
<%@ page import="com.nokia.testingservice.austere.util.*"%>
<%@ page import="com.nokia.testingservice.austere.model.*"%>
<%
	org.slf4j.Logger log = LogUtils.getWebLog();
	String backto = request.getParameter("from");
	if(backto==null||backto.equals("")||backto.equalsIgnoreCase("null"))
		backto = "/index.jsp";
	String loginFailMsg = "";
	if( request.getParameter("isSubmit")!=null ){
		String username = request.getParameter("user");
		String password = request.getParameter("pass");
		AuthService as = AuthServiceFactory.getInstance();
		if ( as.authUser( username, password ) ){
			session.setAttribute( "userInfo", as.getUserInfo( username ));
			response.sendRedirect(backto);
			return;
		}else{
			loginFailMsg = "LoginFailed";
		}
	}
%>
<!DOCTYPE HTML>
<html>
<head>
<style type="text/css">
body{
font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

div{
	font-size: 22px;
	font-weight: bold;
	text-align:center;
}
.fail{
	font-size: 18px;
	font-weight: bold;
	text-align:center;
	color:red;
}
</style>
</head>
<body>
<iframe name="ifm" id="ifm" style="display:none"></iframe>
	<div>Please enter your NOE and password</div>
	<div class="fail"><%=loginFailMsg%></div>
	<form action="login.jsp" method="POST">
		<table align="center">
			<tr>
				<td>NOE:</td>
				<td><input type="text" name="user" /></td>
			</tr>
			<tr>
				<td>PASSWORD:</td>
				<td><input type="password" name="pass" /></td>
			</tr>
			<tr>
				<td colspan="2" align="center"><input type="submit" /></td>
			</tr>
		</table>
		<input type="hidden" name="isSubmit" value="1"/>
		<input type="hidden" name="from" value="<%=request.getParameter("from")%>"/>
	</form>
</body>
</html>