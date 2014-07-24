<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%!
	private String trans(String status){
		if(status.equals( "F" )){
			return "images/running.png";
		}else if(status.equals("P")){
			return "images/testing.gif";
		}else if(status.equals("A")){
			return "images/stop.png";
		}else{
			return "images/stop.png";
		}
	}
%>
<%
	StatusService ss = StatusServiceFactory.getInstance();
	StationService st = StationServiceFactory.getInstance();
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
	String status = request.getParameter( "status" );
	if(status==null)
		status = "";
	TestStation[] tss = ss.getCurrentStationStatus(siteName,productFilter,taskFilter);
	//String[] products = ss.getProducts();
%>
<!DOCTYPE HTML>
<html>
	<head>
		<!-- <meta http-equiv="refresh" content="150" /> -->
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
			
			.normal {
				color: green;
				font-size: 150%;
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
			label{
				color: bule;
				font-weight:bolder;
			}
			
		</style>
		<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
		<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
		<script type="text/javascript">
			function supports_local_storage() {
				return !!window.localStorage;
			}
	
			function CookieUtil() {
			};
	
			CookieUtil.prototype.setCookie = function(key, value) {
				//var Days = 1;
				var exp = new Date();
				//exp.setTime(exp.getTime() + Days*24*60*60*1000);
				document.cookie = key + "=" + escape(value);//' + ";expires=" + exp.toGMTString();
			}
	
			CookieUtil.prototype.getCookie = function(key) {
				var arr = document.cookie.match(new RegExp("(^| )" + key
						+ "=([^;]*)(;|$)"));
				if (arr != null)
					return unescape(arr[2]);
				else
					return null;
			}
	
			CookieUtil.prototype.rmCookie = function(key) {
				var exp = new Date();
				exp.setTime(exp.getTime() - 1);
				var cval = getCookie(name);
				if (cval != null)
					document.cookie = name + "=" + cval + ";expires="
							+ exp.toGMTString();
			}
	
			var cookieUtil = new CookieUtil();
	
			function LocalStorageUtil() {};
			
			LocalStorageUtil.prototype.putItem = function(key, value) {
				localStorage[key] = value;
			}
	
			LocalStorageUtil.prototype.getItem = function(key) {
				if (localStorage[key] && !isNaN(parseInt(localStorage[key])))
					return localStorage[key];
				return null;
			}
			
			function showSteps( id, product ){
				var url = "getStepInfo.jsp";
				$.get(
						url,
						{id:id,product:product},
						function(data,status,xhr){
							if(status=='success'){
								var content = '<table>';
								content += '<tr><th>FinishedTime</th><th>GroupNo</th><th>CaseNo</th><th>StepNo</th><th>CaseDesc</th><th>StepDesc</th><th>StepResult</th></tr>';
								for( i in data){
									content +="<tr>";
									content +="<td>"+data[i].FinishedTime+"</td>";
									content +="<td>"+data[i].GroupNo+"</td>";
									content +="<td>"+data[i].CaseNo+"</td>";
									content +="<td>"+data[i].StepNo+"</td>";
									content +="<td>"+data[i].CaseDesc+"</td>";
									content +="<td>"+data[i].StepDesc+"</td>";
									content +="<td>"+data[i].StepResult+"</td>";
									content +="</tr>";
								}
								content += '</table>';
								$('#StepInfo').html(content);
								$( "#StepInfo" ).dialog( "open" );
							}else{
								alert(data);
							}
						},
						'json'
				);
			}
			
			function showDevice(msg){
				if(!msg || msg==''){
					return;
				}
				var content = '';
				var xmlDoc = $(jQuery.parseXML(msg));
				$xml = $( xmlDoc );
			    $pc = $xml.find( "PCSystemName" );
			    if($pc){
			    	content += "<label>Pc System:</label><span>"+$pc.text()+"</span><br/>";
			    }
				$cpu = $xml.find( "CPUName" );
				if($cpu){
					content += "<label>CPU:</label><span>"+$cpu.text()+"</span><br/>";
				}
				$mem = $xml.find( "Memory" );
				if($mem){
					content += "<label>Memory:</label><span>"+$mem.text()+"</span><br/>";
				}
				$av = $xml.find( "AustereVerison" );
				if($av){
					content += "<label>Austere Verison:</label><span>"+$av.text()+"</span><br/>";
				}
				$ics = $xml.find( "IowCard" );
				if($ics){
					content += "<label>IowCards:</label><ul>";
					$ics.each(function(){
						content +="<li>"+$(this).children('ID').text()+" - "+$(this).children('Name').text()+"</li>";
					});
					content += "</ul>";
				}
				$('#deviceInfo').html(content);
				$( "#deviceInfo" ).dialog( "open" );
			}
			
			var localUtil = new LocalStorageUtil();
	
			function findAndCheck(key, value) {
				if (supports_local_storage()) {
					if (localUtil.getItem(key)) {
						var curr = parseInt(localUtil.getItem(key));
						if(parseInt(value)>curr)
							return true;
						else
							return false;
					} else {
						localUtil.putItem(key,value);
						if(parseInt(value)>0)
							return true;
						else
							return false;
					}
				} else {
					if (cookieUtil.getCookie(key)) {
						var curr = parseInt(cookieUtil.getCookie(key));
						if(parseInt(value)>curr)
							return true;
						else
							return false;
					} else {
						cookieUtil.setCookie(key,value);
						if(parseInt(value)>0)
							return true;
						else
							return false;
					}
				}
			}
			
			$(function(){
				$( "#deviceInfo" ).dialog({
					autoOpen: false,
					//show: "fold",
					//hide: "explode",
					height: 350,
					width: 450,
					modal: false,
					open: function(event, ui) { 
					},
				});
				
				$( "#StepInfo" ).dialog({
					autoOpen: false,
					//show: "fold",
					//hide: "explode",
					height: 350,
					width: 950,
					modal: true,
					open: function(event, ui) { 
					},
				});
				$(".box").mouseenter(function(){
					var msg = $(this).attr("detail");
					if(!msg || msg==''){
						return;
					}
					var content = '';
					var xmlDoc = $(jQuery.parseXML(msg));
					$xml = $( xmlDoc );
				    $pc = $xml.find( "PCSystemName" );
				    if($pc){
				    	content += "<label>Pc System:</label><span>"+$pc.text()+"</span><br/>";
				    }
					$cpu = $xml.find( "CPUName" );
					if($cpu){
						content += "<label>CPU:</label><span>"+$cpu.text()+"</span><br/>";
					}
					$mem = $xml.find( "Memory" );
					if($mem){
						content += "<label>Memory:</label><span>"+$mem.text()+"</span><br/>";
					}
					$av = $xml.find( "AustereVerison" );
					if($av){
						content += "<label>Austere Verison:</label><span>"+$av.text()+"</span><br/>";
					}
					$ics = $xml.find( "IowCard" );
					if($ics){
						content += "<label>IowCards:</label><ul>";
						$ics.each(function(){
							content +="<li>"+$(this).children('ID').text()+" - "+$(this).children('Name').text()+"</li>";
						});
						content += "</ul>";
					}
					$('#deviceInfo').html(content);
					$( "#deviceInfo" ).dialog( "open" );
				}).mouseleave(function(){
					$( "#deviceInfo" ).dialog( "close" );
				});
			});
		</script>
	</head>
	<body>
		<table class="content">
			<tr>
				<th>Test Station</th>
				<th>Status</th>
				<th>Test Type</th>
				<th>Product</th>
				<th>Version</th>
				<th>Remark</th>
				<th>Reset</th>
				<th>Freeze</th>
				<th>Operation</th>
			</tr>
			<%
				for ( TestStation ts : tss ) {
					long curr = System.currentTimeMillis();
					if(!status.isEmpty( )){
						if( status.equals("A") && ts.getStatus( ).equals("P")){
							if( (curr-ts.getLastUpdate().getTime())< (2*3600*1000)){
								continue;
							}else{
								ts.setStatus("A");
							}
						}else if( !status.equalsIgnoreCase(ts.getStatus()) || (ts.getStatus( ).equals("P") && ( (curr-ts.getLastUpdate().getTime())>= (2*3600*1000))) ){
							continue;
						}
					}else{
						if( (curr-ts.getLastUpdate().getTime())> (2*3600*1000) && ts.getStatus( ).equals("P") ){
							ts.setStatus("A");
						}
					}
					long interval = ts.getLastUpdate().getTime() - ts.getStartTime().getTime();
					long secs =  interval / 1000;
					long mins = secs / 60;
					long hours = mins / 60;
					long extmins = mins % 60;
					Station station = st.getStation( ts.getSiteName(), ts.getStationId() );
					String pcName = "<span style='color:red'>[Deprecated]</span>";
					String detail = "";
					if(station != null ){
						pcName = station.getPcName();
						detail = station.getDetails();
					}
			%>
			<tr>
				<td><img src="<%=trans(ts.getStatus()) %>" width="20px" /><span class="box" detail="<%=detail%>"><%=pcName%></span></td>
				<td>Starting: <%=ts.getStartTime()%><br />Running: <%=hours%>hours<%=extmins%>mins<br />Last
					Update time:<%=ts.getLastUpdate()%></td>
				<td><strong>MTBF</strong></td>
				<td><%=ts.getProduct()%></td>
				<td><%=ts.getSw()%></td>
				<td><%=ts.getRemark()%></td>
				<td <%if(ts.getResetCount()==0){%>class="normal"<%}else{ %>class="urgent"<%} %> id="<%=ts.getExecutionId()%>_reset"><%=ts.getResetCount()%></td>
				<td <%if(ts.getResetCount()==0){%>class="normal"<%}else{ %>class="urgent"<%} %> id="<%=ts.getExecutionId()%>_freeze"><%=ts.getFreezeCount()%></td>
				<td> <button onclick="showSteps('<%=ts.getExecutionId()%>','<%=ts.getProduct()%>')">Step Info</button></td>
				<script type="text/javascript">
					/*if( findAndCheck("<%=ts.getExecutionId()%>_reset", "<%=ts.getResetCount()%>" ) ){
						document.getElementById("<%=ts.getExecutionId()%>_reset").className = "urgent";
					}
					
					if( findAndCheck("<%=ts.getExecutionId()%>_freeze", "<%=ts.getFreezeCount()%>" ) ){
						document
						.getElementById("<%=ts.getExecutionId()%>_freeze").className = "urgent";
					}*/
				</script>
			</tr>
			<%
				}
			%>
		</table>
	</body>
	<div id="deviceInfo" style="border:2px solid #dddddd" title="Device Info">
		
	</div>
	<div id="StepInfo" title="Step Info">
		
	</div>
</html>