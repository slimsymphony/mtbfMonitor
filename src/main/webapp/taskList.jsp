<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	String siteName = "";
	if(request.getParameter("site")!=null){
		siteName = request.getParameter("site");
		session.setAttribute("site",siteName);
	}else{
		if(session.getAttribute("site")!=null){
			siteName = (String) session.getAttribute("site");
		}
	}
	String userName = "";
	TaskService ts = TaskServiceFactory.getInstance();
	List<Task> tasks = null;
	boolean isAuth = false;
	boolean isAdmin = false;
	User userInfo = null;
	if ( session.getAttribute( "userInfo" ) != null ) {
		userInfo = ( User ) session.getAttribute( "userInfo" );
		isAuth = true;
		userName = userInfo.getUserID();
		if(userInfo.getRole().equals( User.Role.Admin ))
			isAdmin = true;
	}
	
	String taskScope = request.getParameter( "taskScope" );
	int taskStatus = CommonUtils.parseInt( request.getParameter("taskStatus"), Constants.TASK_STATUS_ALL);
	if ( userName != null && !userName.equals( "" ) ) {
		if ( taskScope != null && taskScope.equals( "all" ) ) {
			//tasks = ts.getAllValidTasks();
			tasks = ts.getAllTasks(siteName,taskStatus);
		} else {
			tasks = ts.getTasksByOwner( siteName, userName, taskStatus );
		}
		
	} else {
		tasks = ts.getAllTasks(siteName, taskStatus);
	}
	ProductService ps = ProductServiceFactory.getInstance( );
	String[] productNames = ps.getProductNames( false );
	int currentYear = CommonUtils.getCurrentWk()/100+2000;
	int currentWk = CommonUtils.getCurrentWk()%100;
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
$(function() {
	$( "#dialog:ui-dialog" ).dialog( "destroy" );
	var product = $( "#product" ),
		milestone = $( "#milestone" ),
		milestoneSel = $('#milestoneSel'),
		startYear = $( "#startYear" ),
		startWk = $( "#startWk" ),
		endYear = $( "#endYear" ),
		endWk = $( "#endWk" ),
		stationCount = $( "#stationCount" ),
		owner = $( "#owner" );
		product2 = $( "#product2" ),
		milestone2 = $( "#milestone2" ),
		milestoneSel2 = $('#milestoneSel2'),
		startYear2 = $( "#startYear2" ),
		startWk2 = $( "#startWk2" ),
		endYear2 = $( "#endYear2" ),
		endWk2 = $( "#endWk2" ),
		stationCount2 = $( "#stationCount2" ),
		owner2 = $( "#owner2" );
		allFields = $( [] ).add( product ).add( milestone ).add( startYear ).add( startWk ).add( endYear ).add( endWk ).add( stationCount ),
		allFields2 = $( [] ).add( product2 ).add( milestone2 ).add( startYear2 ).add( startWk2 ).add( endYear2 ).add( endWk2 ).add( stationCount2 ),
		tips = $( ".validateTips" );
	var currentYear = <%=currentYear%>;
	var currentWk = <%=currentWk%>;
	function checkLength( o, n, min, max ) {
		if ( o.val().length > max || o.val().length < min ) {
			o.addClass( "ui-state-error" );
			updateTips( "Length of " + n + " must be between " +
				min + " and " + max + "." );
			return false;
		} else {
			return true;
		}
	}
	
	function checkDate(s,e){
		var startPoint = (7).day().fromNow();
		var start = Date.parse( s.val() );
		var end = Date.parse( e.val() );
		if( startPoint.compareTo( start ) < 0 ){
			alert("You should create task before one week");
			return false;
		}
		if( start.compareTo( end ) > 0 ){
			alert("start point later than end point");
			return false;
		}
		return true;
	}
	
	function checkWk(sy,sw,ey,ew){
		if(sy>ey||(sy==ey&&sw>ew)){
			alert('Start time should not later then End time.');
			return false;
		}
		if( currentYear>sy || (currentYear==sy && (sw-currentWk)<1)  ){
			alert('Task should be booked 1 week prior!');
			return false;
		}
		return true;
	}
	
	function checkWk2(sy,sw,ey,ew){
		if(sy>ey||(sy==ey&&sw>ew)){
			alert('Start time should not later then End time.');
			return false;
		}
		if(currentYear>ey|| (currentYear<=ey&&currentWk>ew ) ){
			alert("End time should later than current time");
			return false;
		}
		return true;
	}
	
	function checkCnt( cnt ){
		if(!$.isNumeric(cnt)){
			alert('Book station count should be numbers!');
			return false;
		}
		if(Number(cnt)<1){
			alert('Book station count should more than 0');
			return false;
		}
			
		/*if(Number(cnt) >15){
			alert('Book station count should less than 15');
			return false;
		}*/
		return true;
	}
	
	$( "#dialog-form" ).dialog({
		autoOpen: false,
		show: "fold",
		hide: "explode",
		height: 450,
		width: 450,
		modal: true,
		open: function(event, ui) {
			var currdate = Date.today();
			startYear.val(currdate.getYear());
			startWk.val(currdate.getWeekOfYear()+2);
			endYear.val(currdate.getYear());
			endWk.val(currdate.getWeekOfYear()+3);
		},
		buttons: {
			"Create Task": function() {
				var bValid = true;
				allFields.removeClass( "ui-state-error" );
				bValid = bValid && checkLength( product, "product", 3, 20 );
				//bValid = bValid && checkDate( startWk, endWk );
				bValid = bValid && checkWk( startYear.val(),startWk.val(), endYear.val(), endWk.val() );
				bValid = bValid && checkCnt(stationCount.val());
				if(bValid){
					$.post( "newTask.jsp", 
							{ product:product.val(),startYear:(startYear.val()%100),startWk:startWk.val(),endYear:(endYear.val()%100),endWk:endWk.val(),milestone:milestone.val(),stationCount:stationCount.val(), owner:owner.val() }, 
							function(data,status){
								if($.trim(data)=='true'){
									alert("Task create success!");
									parent.location.reload();
								}else{
									alert("Task create failure["+$.trim(data)+"],please ask administrator for help");
								}
							},
							'text'
					);
				}
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		},
		close: function() {
		}
	});
	
	$( "#dialog-form2" ).dialog({
		autoOpen: false,
		show: "blind",
		hide: "explode",
		height: 450,
		width: 450,
		modal: true,
		open: function(event, ui) { 
			allFields2.attr("disabled",false);
			var taskId = $("#taskId2").val();
			var taskTr = $("#task_"+taskId);
			var tds = taskTr.children();
			var status;
			if(tds[0].innerText){
				product2.val(tds[0].innerText);
				milestone2.val(tds[1].innerText);
				milestoneSel2.val(tds[1].innerText);
				//var start2 = (tds[2].innerText/100)+2000;
				//var end2 = (tds[3].innerText/100)+2000;
				startYear2.val((tds[2].innerText/100)+2000);
				startWk2.val(tds[2].innerText%100);
				endYear2.val((tds[3].innerText/100)+2000);
				endWk2.val(tds[3].innerText%100);
				stationCount2.val(tds[4].innerText);
				status = tds[7].innerText;
			}else{
				product2.val(tds[0].textContent);
				milestone2.val(tds[1].textContent);
				milestoneSel2.val(tds[1].textContent);
				//var start2 = (tds[2].textContent/100)+2000;
				//var end2 = (tds[3].textContent/100)+2000;
				startYear2.val((tds[2].textContent/100)+2000);
				startWk2.val(tds[2].textContent%100);
				endYear2.val((tds[3].textContent/100)+2000);
				endWk2.val(tds[3].textContent%100);
				stationCount2.val(tds[4].textContent);
				status = tds[7].textContent;
			}
			
			if($.trim(status) =='EXECUTING'){
				startYear2.attr("disabled",true);
				startWk2.attr("disabled",true);
			}else if ($.trim(status) =='FINISHED'){
				startYear2.attr("disabled",true);
				startWk2.attr("disabled",true);
				endYear2.attr("disabled",true);
				endWk2.attr("disabled",true);
			}
		},
		buttons: {
			"Update Task": function() {
				if(!window.confirm("Really? Are you sure to update this task?"))
					return;
				var bValid = true;
				allFields.removeClass( "ui-state-error" );
				bValid = bValid && checkLength( product, "product", 3, 20 );
				//bValid = bValid && checkDate( startWk, endWk );
				bValid = bValid && checkWk2( startYear2.val(),startWk2.val(), endYear2.val(), endWk2.val() );
				bValid = bValid && checkCnt( stationCount2.val() );
				if(bValid){
					$.post( "updateTask.jsp", 
							{ product:product2.val(),startYear:(startYear2.val()%100),startWk:startWk2.val(),endYear:(endYear2.val()%100),endWk:endWk2.val(),milestone:milestone2.val(),stationCount:stationCount2.val(), owner:owner2.val(), taskId:$("#taskId2").val() }, 
							function(data,status){
								if($.trim(data)=='true'){
									alert("Task Update success!");
									location.reload();
								}else{
									alert("Task Update failure["+data+"],please ask administrator for help");
								}
							},
							'text'
					);
				}
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		},
		close: function() {
		}
	});
	
	function onLogin(){
			$('#login-form').css("display", "none"); 
			$.post( "login.do", 
					{user:$("#user").val(),pass:$("#password").val()}, 
					function(data,status,jqXHR){
						if($.trim(data)=='true'){
							//alert("Login success!");
							parent.parent.location.reload();
						}else{
							alert("Login failed:"+data);
							$('#login-form').css("display",'block');
						}
					},
					'text'
			);
			//this.attr("disabled",true);
			 var buttons = $('.selector').dialog('option', 'buttons');
			 buttons.attr("disabled",true);
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
	
	$( "#newTaskBtn" ).button().click(
		function(){
			checkAuth( showCreateForm );
		}
	);
	//$( '#allTask' );
	$('#taskStatus').select().change(function(){
		document.form1.submit();
	});
	$('#taskStatus').val(<%=taskStatus%>);
	$("#password").keypress(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 13) { //Enter keycode
			onLogin();
		}
	});
	$('#milestoneSel').change(function(){
		$('#milestone').val($(this).val());
	});
	$('#milestoneSel').change();
	$('#milestoneSel2').change(function(){
		$('#milestone2').val($(this).val());
	});
	$('#milestoneSel2').change();
});

function showCreateForm(){
	$( "#dialog-form" ).dialog( "open" );
}
function checkAuth( fn, param ){
	$.get( "auth.do?role=User", {}, function(data, textStatus){
		if($.trim(data)=='true'){
			fn(param);
		}else{
			alert($.trim(data));
			$( "#login-form" ).dialog( "open" );
		}
	  },
	  'text'
	);
}

function delTask( taskID ){
	checkAuth(executeDel,taskID);
}

function executeDel(taskID){
	if(!window.confirm("Really? Are you sure to delete this task?"))
		return;
	$.get( "delTask.jsp", {taskId:taskID}, function(){
			parent.location.href="manage.jsp?taskScope=<%=taskScope%>";
	    }
	);				
}

function updateTask( taskID ){
	checkAuth(executeUpdate,taskID);
}

function executeUpdate( taskID ){
	$( "#taskId2").val(taskID);
	$( "#dialog-form2" ).dialog( "open" );	
}
</script>

</head>
<body>
	<div id="func">
		<form name="form1" method="post" action="taskList.jsp">
			<input type="button" id="newTaskBtn" value="Add New Task"/>
			<select style="display:none" name="taskStatus" id="taskStatus" class="text ui-widget-content ui-corner-all">
				<option value="<%=Constants.TASK_STATUS_ALL%>">ALL</option>
				<option value="<%=Constants.TASK_STATUS_NOT_START%>">TASK_STATUS_NOT_START</option>
				<option value="<%=Constants.TASK_STATUS_STARTED%>">TASK_STATUS_STARTED</option>
				<option value="<%=Constants.TASK_STATUS_END%>">TASK_STATUS_END</option>
			</select>
			<input type="hidden" name="taskScope" value="<%=taskScope %>" />
		</form>
	</div>
	<div>
	<table>
		<tr>
			<th>Product</th>
			<th>MileStone</th>
			<th>Start</th>
			<th>End</th>
			<th>Planned</th>
			<th>Deployed</th>
			<!-- <th>Owner</th> -->
			<!-- <th>Updated?</th> -->
			<!-- <th>Status</th> -->
			<th>Operation</th>
		</tr>
		<%
			for ( Task task : tasks ) {
				int realCnt = ts.getTaskStationIds(task.getTaskID()).size();
		%>
		<tr id="task_<%=task.getTaskID()%>" title="TaskID:<%=task.getTaskID()%>">
			<td><%=task.getProduct()%></td>
			<td><%=task.getMilestone()%></td>
			<td><%=task.getStartWk()%></td>
			<td><%=task.getEndWk()%></td>
			<td><%=task.getStationCount()%></td>
			<td><%if(realCnt!=task.getStationCount()){out.print("<font size='8' color='red'>"+realCnt+"</font>");}else{out.print(realCnt);}%></td>
			<!-- <td><%=task.getOwner()%></td> -->
			<!-- <td><%=( task.getIsUpdated() == 0 ) ? "false" : "true"%></td> -->
			<!-- <td>
				<% if( task.getStatus() == Constants.TASK_STATUS_NOT_START ){%>NOT START
				<% }else if ( task.getStatus() == Constants.TASK_STATUS_STARTED ){%>EXECUTING
				<% }else if ( task.getStatus() == Constants.TASK_STATUS_END ){ %>FINISHED<%} %>
			</td> -->
			<td><!-- window.showModalDialog('getBookStatus.jsp?taskId=<%=task.getTaskID()%>&start=<%=task.getStartWk()%>&end=<%=task.getEndWk()%> -->
				<%if((isAuth&&isAdmin)||(isAuth&&task.getOwner().equals( userInfo.getUserID()))){ %>
				<input class="ui-button ui-widget ui-state-default ui-corner-all" type="button" id="detailBtn" value="Stations Mapping" onclick="window.open('taskStations.jsp?count=<%=task.getStationCount()%>&taskId=<%=task.getTaskID()%>')"/>&nbsp;
				<input class="ui-button ui-widget ui-state-default ui-corner-all" type="button" id="delBtn" value="Delete" onclick="delTask(<%=task.getTaskID()%>)"/>&nbsp;
				<% if ( task.getStatus() != Constants.TASK_STATUS_END ){ %>
				<input class="ui-button ui-widget ui-state-default ui-corner-all" type="button" id="updateBtn" value="Update" onclick="updateTask(<%=task.getTaskID()%>)"/>
				<%} 
				}%>
			</td>
		</tr>
		<%
			}
		%>
	</table>
	</div>	
	<div id="dialog-form" title="Create new Task">
		<form>
			<fieldset>
				<label for="product">Product</label> 
				<!-- <input type="text" name="product" id="product" class="text ui-widget-content ui-corner-all" /> -->
				<select name="product" id="product">
					<%
						for ( String pn : productNames ) {
					%>
					<option value="<%=pn%>"><%=pn%></option>
					<%
						}
					%>
				</select> 
				<label for="milestone">Milestone</label>
				<input type="text" name="milestone" id="milestone" />
				<select name="milestoneSel" id="milestoneSel">
					<%
						for ( Milestone m : Milestone.values() ) {
					%>
					<option value="<%=m.name()%>"><%=m.name()%></option>
					<%
						}
					%>
				</select> 
				<label for="StartWk">Start Week</label>
				<select name="startYear" id="startYear" class="text ui-widget-content ui-corner-all">
					<option value="2013">2013</option>
					<option value="2014">2014</option>
					<option value="2015">2015</option>
				</select> 
				<select name="startWk" id="startWk" class="text ui-widget-content ui-corner-all">
					<% for( int i=1;i<=54;i++){ %>
						<option value="<%=i %>"><%=i %></option>
					<% } %>
				</select>
				<!-- <input type="text" name="startWk" id="startWk" value="" class="text ui-widget-content ui-corner-all" /> --> 
				<label for="endWk">End Week</label>
				<select name="endYear" id="endYear" class="text ui-widget-content ui-corner-all">
					<option value="2013">2013</option>
					<option value="2014">2014</option>
					<option value="2015">2015</option>
				</select>
				<select name="endWk" id="endWk" class="text ui-widget-content ui-corner-all">
					<% for( int i=1;i<=54;i++){ %>
						<option value="<%=i %>"><%=i %></option>
					<% } %>
				</select> 
				<!-- <input type="text" name="endWk" id="endWk" value="" class="text ui-widget-content ui-corner-all" /> --> 
				<label for="Station_count">Capacity</label> 
				<input type="text" name="stationCount" id="stationCount" value="" class="text ui-widget-content ui-corner-all" />
				<input type="hidden" name="owner" id="owner" value="<%= userName%>" />
			</fieldset>
		</form>
	</div>
	
	<div id="dialog-form2" title="update Task">
		<form>
			<fieldset>
				<label for="product2">Product</label> 
				<!-- <input type="text" name="product" id="product" class="text ui-widget-content ui-corner-all" /> -->
				<select name="product2" id="product2">
					<%
						for ( String pn : productNames ) {
					%>
					<option value="<%=pn%>"><%=pn%></option>
					<%
						}
					%>
				</select> 
				<label for="milestone2">Milestone</label> 
				<input type="text" name="milestone2" id="milestone2" />
				<select name="milestoneSel2" id="milestoneSel2">
					<%
						for ( Milestone m : Milestone.values() ) {
					%>
					<option value="<%=m.name()%>"><%=m.name()%></option>
					<%
						}
					%>
				</select> 
				<label for="startWk2">Start Week</label> 
				<select name="startYear2" id="startYear2" class="text ui-widget-content ui-corner-all">
					<option value="2013">2013</option>
					<option value="2014">2014</option>
					<option value="2015">2015</option>
				</select> 
				<select name="startWk2" id="startWk2" class="text ui-widget-content ui-corner-all">
					<% for( int i=1;i<=54;i++){ %>
						<option value="<%=i %>"><%=i %></option>
					<% } %>
				</select>
				<!-- <input type="text" name="startWk2" id="startWk2" value="" class="text ui-widget-content ui-corner-all" /> --> 
				<label for="endWk2">End Week</label>
				<select name="endYear2" id="endYear2" class="text ui-widget-content ui-corner-all">
					<option value="2013">2013</option>
					<option value="2014">2014</option>
					<option value="2015">2015</option>
				</select>
				<select name="endWk2" id="endWk2" class="text ui-widget-content ui-corner-all">
					<% for( int i=1;i<=54;i++){ %>
						<option value="<%=i %>"><%=i %></option>
					<% } %>
				</select> 
				<!-- <input type="text" name="endWk2" id="endWk2" value="" class="text ui-widget-content ui-corner-all" /> --> 
				<label for="stationCount2">Capacity</label> 
				<input type="text" name="stationCount2" id="stationCount2" value="" class="text ui-widget-content ui-corner-all" />
				<input type="hidden" name="owner2" id="owner2" value="<%= userName%>" />
				<input type="hidden" name="taskId2" id="taskId2" value="" />
			</fieldset>
		</form>
	</div>
	<div id="login-form" title="Login Form">
		<form>
			<fieldset>
				<label for="user">Your NOE:</label>
				<input type="text" id="user" name="user" />
				<label for="password">PASSWORD:</label>
				<input type="password" id="password" name="pass" />
			</fieldset>
		</form>
	</div>
</body>
</html>