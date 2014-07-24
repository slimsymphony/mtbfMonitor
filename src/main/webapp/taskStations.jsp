<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	String site = "";
	if(request.getParameter("site")!=null){
		site = request.getParameter("site");
		session.setAttribute("site",site);
	}else{
		if(session.getAttribute("site")!=null){
			site = (String) session.getAttribute("site");
		}
	}
	int taskId = CommonUtils.parseInt( request.getParameter( "taskId" ),0);
	int count = CommonUtils.parseInt( request.getParameter( "count" ),0);
	TaskService ts = TaskServiceFactory.getInstance();
	List<Integer> sids = ts.getTaskStationIds(taskId);
	Task task = ts.getTaskByID(taskId);
	StationService ss = StationServiceFactory.getInstance();
	List<Station> sts = ss.getAllStationsBySite(site);
%>
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="pragma" content="no-cache" />
<title>TaskView</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<style type="text/css">
body {
	text-align: center;
	font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif;
}

table {
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

div {
	text-align: left;
}

label,input {
	display: block;
}

</style>
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript" src="js/date.js"></script>
<script type="text/javascript">
$(function(){
	var count = <%=count%>;
	$('#sync').click(function(){
		var str = '';
		var n = 0;
		$('#form1 input').each(function(){
			if(this.checked){
				if(str!='')
					str +=",";
				str += this.value;
				n++;
			}
		});
		if(n>count){
			if(!window.confirm("Selected Stations more than planed account(Plan:"+count+",current:"+n+"),are you sure to continue?")){
				return false;
			}
		}else if(n<count){
			if(!window.confirm("Selected Stations less than planed account(Plan:"+count+",current:"+n+"),are you sure to continue?")){
				return false;
			}
		}
		$.ajax({
			  type: 'POST',
			  url: "setTaskStation.jsp",
			  data: {taskId:'<%=taskId%>',stationIds:str},
			  success: function(data){
					if(data=='true'){
						alert("success!");
						window.opener.location.reload();
						window.close();
					}else{
						alert("Failure:"+data);
					}
				},
			  dataType: "text",
			  async:false
			});
		event.preventDefault();
	});
});

function setback(id){
	var chk = $('#'+id);
	if( chk.attr('checked')){
		chk.parent().parent().css("background-color","grey");
	}else{
		chk.parent().parent().css("background-color","");
	}
}
</script>
</head>
<body>
<h2>Set Relationship between Task and Station!</h2>
<form id="form1">
<table>
<tr>
	<th>Station</th>
	<th>Operation</th>
</tr>
<%for(Station st : sts) {%>
<tr <%if(sids.contains(st.getId())){%>style="background-color:grey;"<%}%>>
	<td><span><img></img><%=st.getPcName() %></span></td>
	<td><input onclick="setback('<%=st.getPcName() %>')" type="checkbox" value="<%=st.getId() %>" id="<%=st.getPcName() %>" <%if(sids.contains(st.getId())){out.print(" checked ");} %>/><label style="cursor:pointer" for="<%=st.getPcName() %>">Select</label></td>
</tr>
<%} %>
<tr>
	<th colspan="2" style="text-align:center"><button id="sync">Sync</button> &nbsp;&nbsp; <button onclick="window.close()">Close</button></th>
</tr>
</table>
</form>
</body>
</html>