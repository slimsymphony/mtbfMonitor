<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%
	String siteName = "TEST";
	if(request.getParameter("site")!=null){
		siteName = request.getParameter("site");
		session.setAttribute("site",siteName);
	}else{
		if(session.getAttribute("site")!=null){
			siteName = (String) session.getAttribute("site");
		}
	}
	ProductService ps = ProductServiceFactory.getInstance( );
	String[] products = ps.getProductNames( false );
	String week = request.getParameter( "week" );
	if(week==null)
		week = "";
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Austere Weekly Report</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" media="all" href="css/ui.jqgrid.css" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-en.js" type="text/javascript"></script>
<script type="text/javascript" src="js/jquery.jqGrid.js" type="text/javascript"></script>
<style type="text/css">
body{
	text-align: center;
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}
iframe{
	width:100%;
	border: 0px;
	top:auto;
}
#ifm{
	/*height:800px;*/
}
#ifmChart{
}
</style>
<script type="text/javascript">
$(function(){
		$( "#dialog-modal" ).dialog({
			height: 140,
			modal: true
		});
		$('#subbtn').click(function(){
			$( "#dialog-modal" ).dialog( "close" );
			$('#ifm').attr('src','weeklyReport.jsp?week=<%=week%>&site=<%=siteName%>&product='+$('#product').val());
			$('#ifmChart').attr('src','reportchart.jsp?week=<%=week%>&site=<%=siteName%>&product='+$('#product').val());
		});
		/*$.each($('iframe'),function(){
			alert(this.id);
			alert(this.scrollHeight);
			this.height = $('document').height();
		});*/
		document.getElementById('ifmChart').style.height = document.getElementById('ifmChart').contentWindow.document.scrollHeight;
		
});
</script>
</head>
<body>
<div id="dialog-modal" title="Select Product">
	<select id="product">
		<% for(String product :products) {%>
		<option value="<%=product%>"><%=product%></option>
		<%} %>
	</select>
	<input id="subbtn" type="button" value="submit"/>
</div>
<div>
	<iframe id="ifm" src="" ></iframe>
</div>
<div>
<iframe id="ifmInfo" src="" ></iframe>
</div>
<div>
<iframe id="ifmResults" src="" ></iframe>
</div>
<div>
<iframe id="ifmChart"  scrolling="no" src="" ></iframe>
</div>
<div>
<iframe id="ifmAutosmsLink" src="" ></iframe>
</div>
<div>
<iframe id="ifmErrors" src="" ></iframe>
</div>
<div>
<iframe id="ifmDetails" src="" ></iframe>
</div>
MTBF Index calculation = MIN (120, (Total run time of all runs – total UI freeze time – total key lock - total power down time) / MAX (1, (total number of resets + total number of freezes)))
</body>
</html>