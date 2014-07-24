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
	String dateStr = request.getParameter( "date" );
	if(dateStr==null)
		dateStr = "";
	
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Austere Daily Report</title>
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
#ifm{
	width:100%;
	height:800px;
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
			$('#ifm').attr('src','dailyReport.jsp?date=<%=dateStr%>&site=<%=siteName%>&product='+$('#product').val());
		});
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
<iframe id="ifm" src="" ></iframe>
MTBF Index calculation = MIN (120, (Total run time of all runs – total UI freeze time – total key lock - total power down time) / MAX (1, (total number of resets + total number of freezes)))
</body>
</html>