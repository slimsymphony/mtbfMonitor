<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%
	User userInfo = null; 
	if ( session.getAttribute( "userInfo" ) != null ) {
		userInfo = ( User ) session.getAttribute( "userInfo" );
	}
	Set<String> sites = DbUtils.databases.keySet();
	String siteName = "";
	if(request.getParameter("site")!=null){
		siteName = request.getParameter("site");
		session.setAttribute("site",siteName);
	}else{
		if(session.getAttribute("site")!=null){
			siteName = (String) session.getAttribute("site");
		}else{
			response.sendRedirect("selectSite.jsp");
		}
	}
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="pragma" content="no-cache"> 
<meta http-equiv="cache-control" content="no-cache"> 
<meta http-equiv="expires" content="0">
<title>Welcome to Austere</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="css/shadowbox.css">
<link rel="stylesheet" type="text/css" href="css/iealert/style.css" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript" src="js/jQueryRotate.2.2.js"></script>
<script type="text/javascript" src="js/shadowbox.js"></script>
<script type="text/javascript" src="js/iealert.min.js"></script>
<style type="text/css">
body{
	text-align: left;
	margin-top: 50px;
	margin-left: 10%;
	margin-right: 10%;
	/* background-image: url('images/austere.gif');
	background-repeat: no-repeat;
	background-position: left top; */
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

iframe{
	width:100%;
	height:500px;
	border:1px;
}

.header{
	font-size: 40pt;
	/*font-family:Impact;*/
	font-weight: bold;
	color: darkblue;
	text-align: center;
	/* margin-top: 160px; */
	margin-bottom: 30px;
	/*border-style:solid;
	border-color:brown;*/
	vertical-align:middle;
	/* position: relative; */
	/* overflow: hidden; */
}

.content{
	text-align: center;
}

.links{
	text-align: center;
	border-style:solid;
	border-color:yellow;
}

.main{
	text-align: center;
	/*border-style:solid;
	border-color:red;*/
}

.fm{
	text-align: center;
	width:80%;
	height:80%;
	border-style:solid;
	border-color:brown;
}

.footer{
	margin-top: 50px;
	text-align: center;
	/*border-style:solid;
	border-color:green;*/
}

.tabs{
	margin-top: 50px;
}
</style>

<script type="text/javascript">
$(function(){
	$('#tabs').tabs();
	var rotation = function (){
		   $("#logo").rotate({
		      angle:0, 
		      animateTo:360,
		      duration:2500,
		      callback: rotation
		   });
		}
	rotation();
	
	$('#site').val('<%=siteName%>');
	
	$('#site').change(function(){
		location.href='index.jsp?site='+$('#site').val();
	});
	
	function onLogin(){
		$('#login-form').css("display", "none"); 
		var buttons = $('.selector').dialog('option', 'buttons');
		buttons.attr("disabled",true);
		$.post( "login.do", 
				{user:$("#user").val(),pass:$("#password").val()}, 
				function(data,status,jqXHR){
					if($.trim(data)=='true'){
						//alert("Login success!");
						//parent.parent.location.reload();
						parent.parent.location.href=window.location.href;
					}else{
						alert("Login failed:"+data);
						$('#login-form').css("display",'block');
					}
				},
				'text'
		);
		//this.attr("disabled",true);
	}
	
	$( "#login-form" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 250,
		width: 450,
		modal: true,
		buttons: {
			"Login": onLogin,
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		},
		open: function(event, ui) { 
			var buttons = $('.selector').dialog('option', 'buttons');
			buttons.attr("disabled",false);
		},
		close: function() {
		}
	});
	$("#password").keypress(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 13) { //Enter keycode
			onLogin();
		}
	});
	$('#login').click(function(e){
		e.preventDefault();
		$( "#login-form" ).dialog( "open" );
	});
	
	$('#restLink').click(function(e){
		e.preventDefault();
		//if($.browser.webkit || $.browser.mozilla){
			//$( "#restC" ).click();
		//} else {
			$( "#restA" ).click();
		//}
	});
	
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
	$('#logout').click(function(e){
		e.preventDefault();
		if(window.confirm("Are you sure to logout?"))
			logoff();
	});
	$("#password").keypress(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 13) { //Enter keycode
			onLogin();
		}
	});
	
	Shadowbox.init({ 
		handleOversize: "drag",
	    modal: true
	});
	
	/*if($.browser.msie){
		if(window.confirm("Your browser is not best compatible for this application,We suggest you to install a Google Chrome, which can bring best experience. Do you want to install Chrome right now? "))
			window.open("http://www.google.com/chrome");
	}*/
	$("body").iealert();
});


function hidIcon(){
	var iconDiv = document.getElementById("icon");
	iconDiv.style.display = "none";
}

function getElementPos(el) {
	 
    var ua = navigator.userAgent.toLowerCase();
    var isOpera = (ua.indexOf('opera') != -1);
    var isIE = (ua.indexOf('msie') != -1 && !isOpera); // not opera spoof
 
    //var el = document.getElementById(elementId);
 
    if(el.parentNode === null || el.style.display == 'none')
    {
        return false;
    }
 
    var parent = null;
    var pos = [];
    var box;
 
    if(el.getBoundingClientRect)    //IE
    {
        box = el.getBoundingClientRect();
        var scrollTop = Math.max(document.documentElement.scrollTop, document.body.scrollTop);
        var scrollLeft = Math.max(document.documentElement.scrollLeft, document.body.scrollLeft);
 
        return {x:box.left + scrollLeft, y:box.top + scrollTop};
    }
    else if(document.getBoxObjectFor)   // gecko
    {
        box = document.getBoxObjectFor(el);
 
        var borderLeft = (el.style.borderLeftWidth)?parseInt(el.style.borderLeftWidth):0;
        var borderTop = (el.style.borderTopWidth)?parseInt(el.style.borderTopWidth):0;
 
        pos = [box.x - borderLeft, box.y - borderTop];
    }
    else    // safari & opera
    {
        pos = [el.offsetLeft, el.offsetTop];
        parent = el.offsetParent;
        if (parent != el) {
            while (parent) {
                pos[0] += parent.offsetLeft;
                pos[1] += parent.offsetTop;
                parent = parent.offsetParent;
            }
        }
        if (ua.indexOf('opera') != -1
            || ( ua.indexOf('safari') != -1 && el.style.position == 'absolute' ))
        {
                pos[0] -= document.body.offsetLeft;
                pos[1] -= document.body.offsetTop;
        }
    }
 
    if (el.parentNode) { parent = el.parentNode; }
    else { parent = null; }
 
    while (parent && parent.tagName != 'BODY' && parent.tagName != 'HTML')
    { // account for any scrolled ancestors
        pos[0] -= parent.scrollLeft;
        pos[1] -= parent.scrollTop;
 
        if (parent.parentNode) { parent = parent.parentNode; }
        else { parent = null; }
    }
    return {x:pos[0], y:pos[1]};
}

</script>

</head>
<body>
	<div class="header" style="text-align:left">
		<img id="logo" height="200px" src="images/Austere_logo.jpg"><span>&nbsp;Austere Monitor & Management Center</span>
		<div style="text-align:right;font-size:12pt">
			<!-- <select id="site">
			<%for(String site:sites){ %>
			<option value="<%=site%>"><%=site%></option>
			<%} %>
			</select>-->
			Current Site: <strong><%=siteName%></strong>
			<button  class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" role="button" onclick="location.href='selectSite.jsp';">Change Site</button>
		</div>
	</div>
	
	<div id="tabs">
		<ul>
			<li><a href="#mtbf">MTBF Monitor</a></li>
			<li><a href="#station">Station Monitor</a></li>
			<li><a href="#manage">Capacity Management</a></li>
			<li><a href="#result">Result Statistic</a></li>
			<%if(userInfo!=null&& userInfo.getRole().equals( User.Role.Admin )){ %>
			<li><a href="#maintain">Maintain Entry</a></li>
			<li><a href="#log">Check Logging </a></li>
			<!-- <li><a href="#emergency">Emergency Alertor</a></li>  -->
			<%} %>
			<li><a href="#contacts">Contact Us</a></li>
			<%if(userInfo==null){ %>
			<li><a id="login" href="#mtbf">Login</a></li>
			<%}else{ %>
			<li><a id="logout" href="#mtbf">Logout</a></li>
			<%} %>
			<li><a id="restLink" href="#rest">Take a Rest:)</a></li>
		</ul>
		<div id="mtbf">
			<iframe id="ifmmtbf" src="mtbf.jsp?site=<%=siteName %>" ></iframe>
		</div>
		<div id="station">
			<iframe id="ifmstation" src="showStationStatus.jsp?site=<%=siteName %>" ></iframe>
		</div>
		<div id="manage">
			<iframe id="ifmmanage" src="manage.jsp?site=<%=siteName %>" ></iframe>
		</div>
		
		<div id="result" style="height:400px">
			<iframe id="ifmreport" style="height:400px" src="reportMain.jsp?site=<%=siteName %>" ></iframe>
		</div>
		<%if(userInfo!=null && userInfo.getRole().equals( User.Role.Admin )){ %>
		<div id="maintain">
			<iframe id="ifmmaintain" style="height:400px" src="maintain.jsp?site=<%=siteName %>" ></iframe>
		</div>
		<div id="log">
			<iframe id="ifmlog" src="logMain.jsp" ></iframe>
		</div>
		<!--<div id="emergency">
			<iframe id="ifmemergency" style="height:500px" src="emergency.jsp" ></iframe>
		</div>-->
		<%} %>
		<div id="contacts">
			<iframe id="ifmcontacts" src="contact.jsp" ></iframe>
		</div>
		<div id="rest">
			<a id="restA" rel="shadowbox[Mixed];width=1200;height=500" href="index2.html">Evan is Your Friend!</a><br/>
			<a id="restB" rel="shadowbox[Mixed];width=1200;height=500" href="rest.html">Only if your are using a HTML5 Supported browser, click here to Rest</a><br/>
			<a id="restC" rel="shadowbox[Mixed];width=1200;height=500" href="fruitninja.html">Only if your are using a HTML5 Supported browser, click here to Have Fun!</a>
		</div>
	</div>
	<!-- <div class="content">
		<div class="links">
			
		</div>
		<div class="main">
			<iframe class="fm" id="content" src=""></iframe>
		</div>
	</div>
	 -->
	<div class="footer"> 
		<!-- <a onMouseOver="showIcon(this,1)" onMouseOut="hidIcon()" href="mailto:jeffery.zhao@nokia.com">Jeffery Zhao</a> or  
		<a onMouseOver="showIcon(this,2)" onMouseOut="hidIcon()" href="mailto:evan.1.chen@nokia.com">Evan Chen</a> <br/>
		&nbsp; -->
		 &copy; Nokia FPD Testing Service Beijing</div>
	<div style="display:none;position:absolute;" id="icon">
		<img id="icon2" src="favicon.ico" width="100px"/>
	</div>
	<div id="login-form" title="Login Form">
		<form>
			<fieldset>
				<label for="user">Your NOE:</label>
				<input type="text" id="user" name="user" /><br/>
				<label for="password">PASSWORD:</label>
				<input type="password" id="password" name="pass" />
			</fieldset>
		</form>
	</div>
</body>
</html>