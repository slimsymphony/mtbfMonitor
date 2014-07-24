<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
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
	int currWk = CommonUtils.getCurrentWk();
	int startWk =(currWk/100)*100+1;
	ProductService ps = ProductServiceFactory.getInstance();
	List<String> products = ps.getCurrentProducts((String)session.getAttribute("site"));
	TaskService ts = TaskServiceFactory.getInstance();
	List<Task> tasks = ts.getAllValidTasks(site);
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
a{
	font-size: 22px;
}
</style>
<script type="text/javascript">
$(function(){
	function fisc(date) {
	    var checkDate = new Date(date.getFullYear(),0,1);
	    var week = (Math.floor(Math.round((date.getTime() - checkDate.getTime()) / 86400000) / 7) + 2);
	    if (week < 1) {
	        week = 52 + week;
	    }
	    return week;
	}
	
	$( "#datepicker" ).datepicker({showWeek: true,calculateWeek: fisc });
	$( "#startD" ).datepicker({showWeek: true,calculateWeek: fisc });
	$( "#endD" ).datepicker({showWeek: true,calculateWeek: fisc });
	$('#us').click(function(){
		if($( "#startD" ).val()==''||$( "#endD" ).val()==''){
			alert('You have to select time interval first!');
			return;
		}else{
			window.open('charts.jsp?type=us&start='+$( "#startD" ).val()+"&end="+$( "#endD" ).val()+"&site=<%=session.getAttribute("site")%>");
		}
	});
	$('#up').click(function(){
		if($( "#startD" ).val()==''||$( "#endD" ).val()==''){
			alert('You have to select time interval first!');
			return;
		}else{
			window.open('charts.jsp?type=up&start='+$( "#startD" ).val()+"&end="+$( "#endD" ).val()+"&site=<%=session.getAttribute("site")%>&task="+$('#task').val());
		}
	});
	$('#eop').click(function(){
		if($( "#startD" ).val()==''||$( "#endD" ).val()==''){
			alert('You have to select time interval first!');
			return;
		}else{
			window.open('charts.jsp?type=eop&start='+$( "#startD" ).val()+"&end="+$( "#endD" ).val()+"&site=<%=session.getAttribute("site")%>");
		}
	});
	$('#dr').click(function(e){
		e.preventDefault();
		var date = $( "#datepicker" ).val();
		window.open("dailyReportFrame.jsp?date="+date);
	});
	
	$('#wr').click(function(e){
		e.preventDefault();
		var defaultValue = <%=currWk%>;
		var week = $( "#weekpicker" ).val();
		if(week=='')
			week = defaultValue;
		window.open("weeklyReportFrame.jsp?week="+week);
	});
	
	$('#da').click(function(e){
		e.preventDefault();
		window.alert("Not implemented now, but Please expect!");
	});
	
	$('#vrtBtn').click(function(){
		if($('#weekV').val()=='0')
			window.open("getValidTime.jsp?week="+$('#weekV').val()+"&task="+$('#taskV').val()+"&site=<%=session.getAttribute("site")%>&criterion="+$('#criterion').val());
		else
			window.open("showStationUsage.jsp?site=<%=session.getAttribute("site")%>&task="+$('#taskV').val()+"&week="+$('#weekV').val());
	});
	
	$('#iusBtn').click(function(){
		window.open("iowStatus.jsp?start="+$('#weekB').val()+"&end="+$('#weekE').val()+"&task="+$('#taskI').val())+"&site=<%=session.getAttribute("site")%>";
	});
	
});

function refresh(){
	$.get( "getEmergency.jsp", 
		   	{}, 
			function(data, textStatus){
		   		if($.trim(data)!=''){
		   			
		   		}
	  		},
	  		'text'
	);
}
</script>
</head>
<body>
	<div>	
	<ul>
		<li>
			<fieldset>
			<label>Start</label><input type="text" id="startD" style="cursor:hand;"/><label>End</label><input type="text" id="endD" style="cursor:hand;"/>
			<!-- <select id="products">
			<%for ( String product : products ) {%>
				<option value="<%=product%>"><%=product%></option>
			<%}%>
			</select>
			 -->
			<ul>
				<li><a id="us" href="javascript:void(0)">Usage Ratio(Station)</a></li>
				<li><a id="up" href="javascript:void(0)">Usage Ratio(Task)</a><label for="task">Task</label>
			<select id="task" name="task" class="text ui-widget-content ui-corner-all">
			<%for(Task t : tasks){ %><option value="<%=t.getTaskID()%>"><%=t.getProduct() %>_<%=t.getMilestone() %></option><%} %>
			</select></li>
				<li><a id="eop" href="javascript:void(0)">Execution on Task</a></li>
			</ul>
			</fieldset>
		</li>
		<li>
			<fieldset>
				<label>Valid Running Time</label>
				<select id="weekV"><option value="0">This Year</option>
				<%for(int i=startWk;i<=currWk;i++) {%><option value="<%=i%>"><%=i%></option><%}%>
				</select>
				<!-- <label for="productV"></label>
				<select id="productV"><option value="">ALL</option>
			<%for ( String product : products ) {%><option value="<%=product%>"><%=product%></option><%}%>
				</select>
				-->
				<label for="taskV">Task</label>
				<select id="taskV" name="taskV" class="text ui-widget-content ui-corner-all">
				<%for(Task t : tasks){ %><option value="<%=t.getTaskID()%>"><%=t.getProduct() %>_<%=t.getMilestone() %></option><%} %>
				</select>
				<label>Criterion Hours</label> <input id="criterion" type="text" value="120" /> 
				<br/>
				<button id="vrtBtn">Go</button>
			</fieldset>
		</li>
		<li>
			<fieldset>
				<label>IowCard usage statistics</label><br/>
				<label>Begin</label>
				<select id="weekB">
				<%for(int i=startWk;i<=currWk;i++) {%><option value="<%=i%>"><%=i%></option><%}%>
				</select>
				<label>End</label>
				<select id="weekE">
				<%for(int i=startWk;i<=currWk;i++) {%><option value="<%=i%>" <%if(currWk==i){out.print("selected");}%>><%=i%></option><%}%>
				</select>
				<label for="taskI">Task</label>
				<select id="taskI" name="taskI" class="text ui-widget-content ui-corner-all">
				<%for(Task t : tasks){ %><option value="<%=t.getTaskID()%>"><%=t.getProduct() %>_<%=t.getMilestone() %></option><%} %>
				</select>
				<!-- <label for="productI"></label>
				<select id="productI"><option value="">ALL</option>
			<%for ( String product : products ) {%><option value="<%=product%>"><%=product%></option><%}%>
				</select>
				-->
				<button id="iusBtn">Go</button>
			</fieldset>
		</li>
		<li style="display:none">
			<fieldset>
			<label for="datepicker">Select Date (Empty means today)</label>
			<input type="text" id="datepicker" title="click to select date" style="cursor:hand;"/><br/>
			<a id="dr" href="#">MTBF Daily Report</a>
			</fieldset>
		</li>
		<li style="display:none">
			<fieldset>
				<label for="weekpicker">Select Week (Empty means this week)</label>
				<select id="weekpicker">
					<option value="">&nbsp;</option>
				<%for(int i=startWk;i<=currWk;i++) {%>
					<option value="<%=i%>" <%if(currWk==i){out.print("selected");} %>><%=i%></option>
				<%}%>
				</select>
				<br/><a id="wr" href="#">MTBF Weekly Report</a>
			</fieldset>
		</li>
		<li style="display:none">
			<fieldset>
				<a id="da" href="#">Data Analyze</a>
			</fieldset>
		</li>
	</ul>
	</div>
</body>
</html>