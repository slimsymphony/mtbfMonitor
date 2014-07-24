<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%
	StatusService ss = StatusServiceFactory.getInstance();
	String siteName = "";
	if(request.getParameter("site")!=null){
		siteName = request.getParameter("site");
		session.setAttribute("site",siteName);
	}else{
		if(session.getAttribute("site")!=null){
			siteName = (String) session.getAttribute("site");
		}
	}
	String productFilter = request.getParameter("product");
	String taskFilter = request.getParameter("task");
	//TestStation[] tss = ss.getCurrentStationStatus(siteName,productFilter,taskFilter);
	String[] products = ss.getProducts();
%>
<!DOCTYPE HTML>
<html>
<head>
<title>Austere Monitoring</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="refresh" content="150" />
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="css/shadowbox.css">
<style type="text/css">
body {
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

.main {
	margin: auto;
	position: relative;
	width: 100%;
}

.urgent {
	color: red;
	font-size: 200%;
	font-weight: bolder;
}

.content {
	width: 100%;
	border-width: 2px;
	border-style: solid;
	border-color: black;
	vertical-align: bottom;
}

td {
	text-align: center;
	border-width: 1px;
	border-style: dashed;
	border-color: black;
}
</style>
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript" src="js/shadowbox.js"></script>

<script type="text/javascript">
$(function() {
	$.post( "getTaskByProduct.jsp", 
			{ site:'<%=siteName%>'}, 
			function(data,status){
				var tasks = jQuery.parseJSON(data);
				var size = tasks.length;
				$("#task").find('option').remove().end();
				$("#task").append($("<option></option>").attr("value","").text("ALL"));
				for(var i=0;i<size;i++){
					$("#task").append($("<option></option>").attr("value",tasks[i].taskID).text(tasks[i].product+"_"+tasks[i].milestone)); 
				}
				$("#task").val('<%=taskFilter%>');
				$('#statusFilter').val("P");
				$('#statusFilter').change();
			},
			'text'
	);
	$('#product').change(
		function(){
			$("#ifm").attr("src","mtbfContent.jsp?site=<%=siteName%>&product="+$('#product').val()+"&status="+$('#statusFilter').val());
			$.post( "getTaskByProduct.jsp", 
					{ site:'<%=siteName%>',product:$("#product").val() }, 
					function(data,status){
						var tasks = jQuery.parseJSON(data);
						var size = tasks.length;
						$("#task").find('option').remove().end();
						$("#task").append($("<option></option>").attr("value","").text("ALL"));
						for(var i=0;i<size;i++){
							$("#task").append($("<option></option>").attr("value",tasks[i].taskID).text(tasks[i].milestone)); 
						}
						$("#task").val('<%=taskFilter%>');
					},
					'text'
			);
			return;
		}
	)
	$('#task').change(
		function(){
			//document.form1.submit();
			$("#ifm").attr("src","mtbfContent.jsp?site=<%=siteName%>&task="+$('#task').val()+"&status="+$('#statusFilter').val());
			return;
		}
	)
	$('#statusFilter').change(function(){
		$("#ifm").attr("src","mtbfContent.jsp?site=<%=siteName%>&status="+$('#statusFilter').val()+"&task="+$('#task').val());
		return;
	});
	
	/*Shadowbox.init({ 
		handleOversize: "drag",
	    modal: true
	});
	
	$('#stations').click(function(e){
		
	});*/
	
	//$('#task').change();
	//$("#product").change();
});
	
</script>

</head>
<body>
	<div class="main">
		<div>
			<form name="form1" method="post" action="mtbf.jsp">
				<fieldset>
					<label style="display:none" for="product">product:</label> 
					<select style="display:none" id="product" name="product" class="text ui-widget-content ui-corner-all">
						<option value="">ALL</option>
						<%
							for ( String product : products ) {
						%>
						<option value="<%=product%>" <% if(product.equalsIgnoreCase( productFilter )){%>selected<%} %> ><%=product%></option>
						<%
							}
						%>
					</select>
					
					<label for="task">task:</label> 
					<select id="task" name="task" class="text ui-widget-content ui-corner-all"></select>

					<!-- <a rel="shadowbox[Mixed]" href="stationStatus.jsp">Stations Status</a> --> 
					<label for="statusFilter">Status</label>
					<select id="statusFilter">
						<option value="">ALL</option>
						<option value="P">Running</option>
						<option value="F">Finished</option>
						<option value="A">ABNORMAL</option>
					</select>
				</fieldset>
			</form>
		</div>
		<iframe id="ifm" style="width:100%;height:420px;border:0px" id="monitor" src=""></iframe>
	</div>
</body>
</html>