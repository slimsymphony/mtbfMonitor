<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%
	MtbfService ms = MtbfServiceFactory.getInstance();
	String siteName = "TEST";
	if(request.getParameter("site")!=null){
		siteName = request.getParameter("site");
		session.setAttribute("site",siteName);
	}else{
		if(session.getAttribute("site")!=null){
	siteName = (String) session.getAttribute("site");
		}
	}
	String dateStr = request.getParameter( "date" );
	Date date = new Date();
	if(dateStr!=null&&!dateStr.trim().equals("")){
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		date = sdf.parse( dateStr );
	}
	String product = request.getParameter("product");
	// take 12/2/29 as a example.
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	//cal.set(Calendar.MONTH,Calendar.FEBRUARY);
	//cal.set(Calendar.DAY_OF_MONTH,29);
	cal.set( Calendar.HOUR, 0 );
	cal.set( Calendar.AM, Calendar.AM );
	cal.set( Calendar.MINUTE, 0 );
	cal.set( Calendar.SECOND, 0 );
	List<ReportMeta> list = ms.getDailyReportMetasByProduct( siteName, product, cal.getTime());
	StationService ss = StationServiceFactory.getInstance();
 	Map<String,Collection<MTBFDetail>> mds = new HashMap<String,Collection<MTBFDetail>>();
 	int reportDay = CommonUtils.getDay(cal.getTime());
 	Collection<MTBFDetail> ts = ms.getMtbfDetails(reportDay, siteName, product);
	for(MTBFDetail md : ts){
		String pcName = ss.getStationById(md.getStationID()).getPcName();
		if(mds.get(pcName)==null){
			ts = new ArrayList<MTBFDetail>();
			mds.put(pcName,ts);
		}
		
		mds.get(pcName).add(md);
	}
	for(ReportMeta drm : list){
		if( mds.get(drm.getTestSystem())==null){
			mds.put(drm.getTestSystem(),new ArrayList<MTBFDetail>());
		}
	}
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
Th{
	text-align:center;
}

.styleTable TD { font-weight: normal !important; padding: .4em; border-top-width: 0px !important; }
.styleTable TH { text-align: center; padding: .8em .4em; }
.styleTable TD.first, .styleTable TH.first { border-left-width: 0px !important; }
</style>
<style  type="text/css" media="screen">
    #printableButNotVisible { display:none }
</style>
<style type="text/css" media="print">
    #accordion h3, #vcol, div.loadingui, div.ui-tabs-hide, ul.ui-tabs-nav li, td.HeaderRight, .n4p { display:none }
    #printableButNotVisible { display:block }
</style>
<script type="text/javascript">
$(function(){
	/*  $.fn.styleTable = function (options) {
         var defaults = {
             css: 'styleTable'
         };
         options = $.extend(defaults, options);

         return this.each(function () {

             input = $(this);
             input.addClass(options.css);

             input.find("tr").live('mouseover mouseout', function (event) {
                 if (event.type == 'mouseover') {
                     $(this).children("td").addClass("ui-state-hover");
                 } else {
                     $(this).children("td").removeClass("ui-state-hover");
                 }
             });

             input.find("th").addClass("ui-state-default");
             input.find("td").addClass("ui-widget-content");

             input.find("tr").each(function () {
                 $(this).children("td:not(:first)").addClass("first");
                 $(this).children("th:not(:first)").addClass("first");
             });
         });
     };
     
     $("#Table1").styleTable(); */
     
	function getPageSize() { 
		var winW, winH; 
		if(window.innerHeight) {// all except IE 
			winW = window.innerWidth; 
			winH = window.innerHeight; 
		} else if (document.documentElement && document.documentElement.clientHeight) {// IE 6 Strict Mode 
			winW = document.documentElement.clientWidth; 
			winH = document.documentElement.clientHeight; 
		} else if (document.body) { // other 
			winW = document.body.clientWidth; 
			winH = document.body.clientHeight; 
		}  // for small pages with total size less then the viewport  
		return {WinW:winW, WinH:winH}; 
	} 
	 /* $("#list").jqGrid({
	    url:'getDailyMeta.jsp',
	    datatype: 'json',
	    mtype: 'GET',
	    colNames:['SW Version','HW Version', 'Language Package','IMEI','SIM card info','Trace Info','Location','Test system','Total runtime (h)','Invalid time (h)','Resets','Freezes','MTBF Index'],
	    colModel :[ 
	      {name:'swVersion', index:'swVersion', width:80, sortable:false}, 
	      {name:'hwVersion', index:'hwVersion', width:80, sortable:false}, 
	      {name:'lanPackage', index:'lanPackage', width:90, align:'right', sortable:false}, 
	      {name:'IMEI', index:'IMEI', width:80, align:'right', sortable:false}, 
	      {name:'simCardInfo', index:'simCardInfo', width:110, align:'right', sortable:false}, 
	      {name:'trace', index:'trace', width:110, sortable:false},
	      {name:'location', index:'location', width:80, sortable:false}, 
	      {name:'testSystem', index:'testSystem', width:90, sortable:false}, 
	      {name:'totalRuntime', index:'totalRuntime', width:110, align:'right', sortable:false}, 
	      {name:'invalidTime', index:'invalidTime', width:90, align:'right', sortable:false}, 
	      {name:'resets', index:'resets', width:80, align:'right', sortable:false}, 
	      {name:'freezes', index:'freezes', width:80, sortable:false},
	      {name:'mtbfIdx', index:'mtbfIdx', width:110, sortable:false}
	    ],
	    rowNum:10,
	    rowList:[10,20,30],
	    sortname: 'IMEI',
	    sortorder: 'desc',
	    viewrecords: true,
	    gridview: true,
	    caption: 'Daily Summary'
	  });
	  var ss = getPageSize(); 
	  $('#list').jqGrid('setGridWidth', ss.WinW-200);//.jqGrid('setGridHeight', ss.WinH-300);//.setGridWidth(700,true);
	  */
	  
	  $("#mtbfList").jqGrid({
		    url:'getDailyMtbf.jsp',
		    datatype: 'json',
		    mtype: 'GET',
		    colNames:['SW Version','HW Version', 'Language Package','IMEI','SIM card info','Trace Info','Location','Test system','Total runtime (h)','Invalid time (h)','Resets','Freezes','MTBF Index'],
		    colModel :[ 
		      {name:'swVersion', index:'swVersion', width:80, sortable:false}, 
		      {name:'hwVersion', index:'hwVersion', width:80, sortable:false}, 
		      {name:'lanPackage', index:'lanPackage', width:90, align:'right', sortable:false}, 
		      {name:'IMEI', index:'IMEI', width:80, align:'right', sortable:false}, 
		      {name:'simCardInfo', index:'simCardInfo', width:110, align:'right', sortable:false}, 
		      {name:'trace', index:'trace', width:110, sortable:false},
		      {name:'location', index:'location', width:80, sortable:false}, 
		      {name:'testSystem', index:'testSystem', width:90, sortable:false}, 
		      {name:'totalRuntime', index:'totalRuntime', width:110, align:'right', sortable:false}, 
		      {name:'invalidTime', index:'invalidTime', width:90, align:'right', sortable:false}, 
		      {name:'resets', index:'resets', width:80, align:'right', sortable:false}, 
		      {name:'freezes', index:'freezes', width:80, sortable:false},
		      {name:'mtbfIdx', index:'mtbfIdx', width:110, sortable:false}
		    ],
		    rowNum:10,
		    rowList:[10,20,30],
		    sortname: 'IMEI',
		    sortorder: 'desc',
		    viewrecords: true,
		    gridview: true,
		    caption: 'Daily Summary'
		  });
	  
	  function onLogin(){
			$('#login-form').css("display", "none"); 
			$.post( "login.do", 
					{user:$("#user").val(),pass:$("#password").val()}, 
					function(data,status,jqXHR){
						if($.trim(data)=='true'){
							//alert("Login success!");
							location.reload();
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
	$("#password").keypress(function(e) {
		var code = (e.keyCode ? e.keyCode : e.which);
		if(code == 13) { //Enter keycode
			onLogin();
		}
	});
	
	  $( "#newMD" ).dialog({
			autoOpen: false,
			show: "fold",
			hide: "explode",
			height: 450,
			width: 650,
			modal: true,
			buttons: {
				"Add": addNewMD,
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
	  
	  $( "#updateMD" ).dialog({
			autoOpen: false,
			show: "fold",
			hide: "explode",
			height: 450,
			width: 650,
			modal: true,
			buttons: {
				"Update": onUpdate,
				Cancel: function() {
					$( this ).dialog( "close" );
				}
			},
			open: function(event, ui) { 
				var buttons = $('.selector').dialog('option', 'buttons');
				buttons.attr("disabled",false);
			},
			close: function() {
				var buttons = $('.selector').dialog('option', 'buttons');
				buttons.attr("disabled",true);
			}
		});
	}); 
	
function checkAuth( fn, params ){
	$.get( "auth.do?role=DataMaintainer", {}, function(data, textStatus){
		if($.trim(data)=='true'){
			fn(params);
		}else{
			alert($.trim(data));
			$( "#login-form" ).dialog( "open" );
		}
	  },
	  'text'
	);
}

function addData( tableId ){
	checkAuth(onAdd, tableId);
}

function delData( seq ){
	checkAuth(onDel,seq);
}

function onDel(seq){
	$.get( "delMebfDetail.jsp", {seq:seq}, function(data, textStatus){
		if($.trim(data)=='true'){
			location.reload();
		}else{
			alert("Fail to delete this data,"+data);
		}
	  },
	  'text'
	);
}

function updateData( pcname, seq ){
	var params = new Array();
	params[0] = pcname;
	params[1] = seq;
	checkAuth(openUpdateForm, params);
}

function openUpdateForm(params){
	$('#pcName2').val(params[0]);
	$('#seq').val(params[1]);
	var tds = $('#tr'+params[1]).children();
	if(tds[0].innerText){
		$('#amount2').val(tds[0].innerText);
		$('#type2').val(tds[1].innerText);
		$('#errorID2').val(tds[2].innerText);
		$('#desc2').val(tds[3].innerText);
		$('#status2').val(tds[4].innerText);
		if(tds[5].innerText=='Known'){
			$('#isKnown2').val(0);
		}else{
			$('#isKnown2').val(1);
		}
	}else{
		$('#amount2').val(tds[0].textContent);
		$('#type2').val(tds[1].textContent);
		$('#errorID2').val(tds[2].textContent);
		$('#desc2').val(tds[3].textContent);
		$('#status2').val(tds[4].textContent);
		if(tds[5].textContent=='Known'){
			$('#isKnown2').val(0);
		}else{
			$('#isKnown2').val(1);
		}
	}
	
		
	
	$( "#updateMD" ).dialog( "open" );
}

function onUpdate(){
	if(!$.isNumeric($('#amount2').val())){
		alert("Amount should be number!");
		return;
	}
	$.get( "updateMebfDetail.jsp", 
			{amount:$('#amount2').val(), type:$('#type2').val(),errorID:$('#errorID2').val(),desc:$('#desc2').val(),status:$('#status2').val(),isKnown:$('#isKnown2').val(),pcName:$('#pcName2').val(),reportDay:$('#reportDay2').val(),seq:$('#seq').val()}, 
			function(data, textStatus){
				if($.trim(data)=='true'){
					location.reload();
				}else{
					alert("Fail to update this data,"+data);
					$( "#updateMD" ).dialog( "close" );
				}
		  },
	  'text'
	);
}

function addNewMD(){
	if(!$.isNumeric($('#amount').val())){
		alert("Amount should be number!");
		return;
	}
	$.post( "newMtbfDetail.jsp", 
		{amount:$('#amount').val(), type:$('#type').val(),errorID:$('#errorID').val(),desc:$('#desc').val(),status:$('#status').val(),isKnown:$('#isKnown').val(),pcName:$('#pcName').val(),reportDay:$('#reportDay').val()}, 
		function(data, textStatus){
			if($.isNumeric($.trim(data))){
					location.reload();
			}else{
				alert("Add new MTBF detail information failed,"+data);
				$( "#newMD" ).dialog( "close" );
			}
	  	},
	  	'text'
	);
}

function onAdd(tableId){
	$('#pcName').val(tableId);
	$( "#newMD" ).dialog( "open" );
}
</script>
</head>
<body>
	 <!-- <div id="jQGrid" align="center">
		<table id="list" style="width:2000px"><tr><td/></tr></table> 
	</div>
	 -->
	 <h2>Austere Daily Report {Date:<%=CommonUtils.getDateStr(cal.getTime(),"yyyy-MM-dd")%>}</h2>
	<div>
		<table id="Table1" class="ui-widget">
			<thead class="ui-widget-header">
			<tr>
				<th colspan="13">
				<%
					if(list.size()>0){
				%> 
					<%=list.get(0).getProductName()%>
				<%
					}
				%>
				</th>
			</tr>
			<tr>
				<th>SW Version</th>
				<th>HW Version</th>
				<th>Language Package</th>
				<th>IMEI</th>
				<th>SIM card info</th>
				<th>Trace Info</th>
				<th>Location</th>
				<th>Test system</th>
				<th>Total runtime (h)</th>
				<th>Invalid time (h)</th>
				<th>Resets</th>
				<th>Freezes</th>
				<th>MTBF Index</th>
			</tr>
			</thead>
			<%
				for(ReportMeta drm : list){
					//if(drm.getIMEI().length()!=15)
					//	continue;
			%>
			<tr>
				<td><%=CommonUtils.notNull(drm.getSwVersion(),true) %></td>
				<td><%=CommonUtils.notNull(drm.getHwVersion(),true) %></td>
				<td><%=CommonUtils.notNull(drm.getLanPackage(),true) %></td>
				<td><%=CommonUtils.notNull(drm.getIMEI(),true) %></td>
				<td><%=CommonUtils.notNull(drm.getSimCardInfo(),true) %></td>
				<td><%=CommonUtils.notNull(drm.getTrace(),true) %></td>
				<td><%=CommonUtils.notNull(drm.getLocation(),true) %></td>
				<td><%=CommonUtils.notNull(drm.getTestSystem(),true) %></td>
				<td><%=CommonUtils.notNull(drm.getTotalRuntime(),true) %></td>
				<td><%=drm.getInvalidTime() %></td>
				<td><%=drm.getResets() %></td>
				<td><%=drm.getFreezes() %></td>
				<td><%=CommonUtils.notNull(drm.getMtbfIdx(),true) %></td>
			</tr>
			<%} %>
			<thead class="ui-widget-header">
			<tr><td colspan="14"><hr/></td></tr>
			</thead>
		</table>
	</div>
	 <h3>Error Information Maintain</h3>
	<% 
	for(String pcName : mds.keySet()){
		Collection<MTBFDetail> mtds = mds.get(pcName);
	%>
	
	<br/>
	<div>
		<table border="1" id="<%=pcName%>" class="ui-widget">
			<thead class="ui-widget-header">
			<tr><td colspan="7"><input type="button" class="n4p" value="Add Data" onclick="addData('<%=pcName%>')"/><%=pcName %></td></tr>
			<tr>
				<th>Amount</th>	
				<th>ResetType or Freeze</th>	
				<th>Error ID</th>
				<th>Description</th>	
				<th>Status</th>	
				<th>New or Known Error</th>
				<th>Operation</th>
			</tr>
			</thead>
			<!-- <tr><td colspan="7"><hr/></td></tr> -->
			<% for(MTBFDetail md : mtds){ %>
			<tr id="tr<%=md.getSeq()%>">
				<td><%=md.getAmount() %></td>
				<td><%=md.getType() %></td>
				<td><%=md.getErrorID() %></td>
				<td><%=md.getDesc() %></td>
				<td><%=md.getStatus() %></td>
				<td><%=(md.getIsKnown()==0)?"Known":"New" %></td>
				<td>
					<input type="button" class="n4p" value="update" onclick="updateData('<%=pcName %>','<%=md.getSeq()%>')"/>&nbsp;
					<input type="button" class="n4p" value="delete" onclick="delData('<%=md.getSeq()%>')"/>
				</td>
			</tr>
			<%} %>
		</table>
	</div>
	<%} %>
	<div id="newMD" title="Add Data">
		<form>
			<fieldset>
			<table>
				<tr><td>
				<label for="amount">Amount</label></td><td>
				<input type="text" id="amount" name="amount" />
				</td></tr>
				<tr><td>
				<label for="type">ResetType or Freeze</label></td><td>
				<input type="text" id="type" name="type" />
				</td></tr>
				<tr><td>
				<label for="errorID">Error ID</label></td><td>
				<input type="text" id="errorID" name="errorID" />
				</td></tr>
				<tr><td>
				<label for="desc">Description</label></td><td>
				<textarea  id="desc" name="desc" ></textarea>
				</td></tr>
				<tr><td>
				<label for="status">Status</label></td><td>
				<select id="status" name="status">
					<option value="DETECTED">DETECTED</option>
					<option value="NOT_DETECTED">NOT_DETECTED</option>
				</select>
				</td></tr>
				<tr><td>
				<label for="isKnown">New or Known Error</label></td><td>
				<select id="isKnown" name="isKnown">
					<option value="0">Known</option>
					<option value="1">New</option>
				</select>
				</td></tr>
			</table>
			<input id="reportDay" type="hidden" value="<%=reportDay %>" />
			<input id="pcName" name="pcName" type="hidden" value="" />
			</fieldset>
		</form>
	</div>
	
	<div id="updateMD" title="Update Data">
		<form>
			<fieldset>
			<table>
				<tr><td>
				<label for="amount2">Amount</label></td><td>
				<input type="text" id="amount2" name="amount" />
				</td></tr>
				<tr><td>
				<label for="type2">ResetType or Freeze</label></td><td>
				<input type="text" id="type2" name="type" />
				</td></tr>
				<tr><td>
				<label for="errorID2">Error ID</label></td><td>
				<input type="text" id="errorID2" name="errorID" />
				</td></tr>
				<tr><td>
				<label for="desc2">Description</label></td><td>
				<textarea  id="desc2" name="desc2" ></textarea>
				</td></tr>
				<tr><td>
				<label for="status2">Status</label></td><td>
				<select id="status2">
					<option value="DETECTED">DETECTED</option>
					<option value="NOT_DETECTED">NOT_DETECTED</option>
				</select>
				</td></tr>
				<tr><td>
				<label for="isKnown2">New or Known Error</label></td><td>
				<select id="isKnown2">
					<option value="0">Known</option>
					<option value="1">New</option>
				</select>
				</td></tr>
			</table>
			</fieldset>
			<input id="reportDay2" type="hidden" value="<%=reportDay %>" />
			<input id="pcName2" name="pcName2" type="hidden" value="" />
			<input id="seq" name="seq" type="hidden" value="" />
		</form>
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