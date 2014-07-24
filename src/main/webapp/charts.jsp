<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="java.sql.*"%>
<%@page import="org.joda.time.DateTime"%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Statistics Charts</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/modules/exporting.js"></script>
<script type="text/javascript" >
<%
String start = request.getParameter("start");
String end = request.getParameter("end");
String type = request.getParameter("type");
String product = request.getParameter("product");
int taskId = CommonUtils.parseInt(request.getParameter("task"),0);
TaskService ts = TaskServiceFactory.getInstance();
Task task = ts.getTaskByID(taskId);
SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
String site = request.getParameter("site");
DateTime s = null;
DateTime e = null;
if(start!=null&&!start.trim().isEmpty()){
	s = new DateTime(sdf.parse(start).getTime());
}
if(end!=null&&!end.trim().isEmpty()){
	e = new DateTime(sdf.parse(end).getTime()+(long)1000*60*60*24);
}
Map<String, Integer> map = null;
String title = "";
if(type!=null){
	if("us".equals(type)){// station use
		map = UsageAnalyzer.getStationUsage(site,s,e);
		title = "Usage for Stations";
	}else if("up".equals(type)){//Product use
		//map = UsageAnalyzer.getStationUsageByProduct(siteName,product,s,e);
		map  = UsageAnalyzer.getStationUsageByTask(site,taskId,s,e);
		title = "Usage for "+task.getProduct()+"_"+task.getMilestone();
	}else if("eop".equals(type)){//execute on product
		map  = UsageAnalyzer.getExecutionOnTask(site,s,e);
		title = "Executions on Tasks";
	}

%>
var labels = [];
var vals = [];
<% int i =0; if(map!=null)
for(Map.Entry<String,Integer> entry : map.entrySet()){%>
labels[<%=i%>] = '<%=entry.getKey()%>';
vals[<%=i++%>] = <%=entry.getValue()%>;
<%}%>
$(function(){
	var chart;
	 $(document).ready(function() {
		<%if("eop".equals(type)){%>
				// Radialize the colors
					Highcharts.getOptions().colors = $.map(Highcharts.getOptions().colors, function(color) {
					    return {
					        radialGradient: { cx: 0.5, cy: 0.3, r: 0.7 },
					        stops: [
					            [0, color],
					            [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] // darken
					        ]
					    };
					});
		<%}%>
		chart = new Highcharts.Chart({
			chart: {
				renderTo: 'container',
				<%if("eop".equals(type)){%>type:'pie'<%}else{%>type: 'line'<%}%>
			},
			title: {
				text: '<%=title%>'
			},
			subtitle: {
				text: ''
			},
			xAxis: {
				labels: {
					rotation: -45,
					align: 'right',
                    formatter: function () {  
                        return labels[this.value];
                    }  
                }  
			},
			yAxis: {
				title: {
					text: 'Usage',
					color: '#AA4643'
				},
				min: 0
                
			},
			legend: {
				itemStyle: {
					   cursor: 'pointer',
					   color: '#274b6d',
					   fontSize: '12px'
					}
			},
			tooltip: {
				<%if(type.equals("eop")){%>
				pointFormat: '<b>{point.percentage}%</b>',
			    percentageDecimals: 1
				<%}else{%>
				enabled: true,
				formatter: function() {
					return '<b>'+ labels[this.x] +'</b><br/>Usage:'+ this.y;
				}
				<%}%>
			},
			plotOptions: {
				<%if("eop".equals(type)){%>
				pie: {
			            allowPointSelect: true,
			            cursor: 'pointer',
			            dataLabels: {
			                enabled: true,
			                color: '#000000',
			                style:{fontSize: '16px'},
			                connectorColor: '#000000',
			                formatter: function() {
			                    return '<b>'+ this.point.name +'</b>: '+this.point.y;
			                }
			            }
			        }
			        
				<%}else{%>
				line: {
					dataLabels: {
						enabled: true
					},
					enableMouseTracking: true
				}
				<%}%>
			},
			series: [
				<%if("eop".equals(type)){%>
				{
		                name: 'Tasks',
		                data: [
						<%int n=0;
						for(Map.Entry<String,Integer> entry : map.entrySet()){%>
							['<%=entry.getKey()%>',<%=entry.getValue()%>],
							<%}%>]
		            }
				<%}else{%>
				{
				name: 'Usage',
				data: [
					<%	int n=0;
						for(Map.Entry<String,Integer> entry : map.entrySet()){%>
				      {x:<%=n++%>,y:<%=entry.getValue()%>},
					<%}%>

				]
			}<%}%>
			]
		});
	 });	
});
<%}%>
</script>
</head>
<body>
<div id="container" style="min-width: 500px; height: 700px; margin: 0 auto">
    	
</div>
</body>
</html>