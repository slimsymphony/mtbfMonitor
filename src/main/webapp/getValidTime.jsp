<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	int week = CommonUtils.parseInt(request.getParameter("week"),0);
	String site = request.getParameter("site");
	if(site == null)
		site = (String)session.getAttribute("site");
	Map<Integer,Float> map = new LinkedHashMap<Integer,Float>();
	Map<Integer,Integer> map2 = new LinkedHashMap<Integer,Integer>();
	String product = request.getParameter("product");
	int taskId = CommonUtils.parseInt(request.getParameter("task"),0);
	StationService ss = StationServiceFactory.getInstance();
	int criterion =  CommonUtils.parseInt(request.getParameter("criterion"),0);
	TaskService ts = TaskServiceFactory.getInstance();
	Task tsk = ts.getTaskByID(taskId);
	int sc = tsk.getStationCount();
	criterion = sc*criterion;
	int max = 0;
	int base = 0;
	/*if(product==null||product.isEmpty()){
		if(week > 1000){
			map.put(week,ss.getValidRunningTime( site, week ));
		}else{
			int currwk = CommonUtils.getWk(new Date());
			max = currwk%100;
			base = (currwk/100)*100;
			for( int i=1; i<=max; i++ ){
				map.put(base+i,ss.getValidRunningTime( site, base+i ));
				map2.put(base+i,ss.getStationValidTimeByWeek( site, base+i ).size());
			}
		}
	}else{
		if(week > 1000){
			map.put(week,ss.getValidRunningTimeForProduct( site, week, product ));
		}else{
			int currwk = CommonUtils.getWk(new Date());
			max = currwk%100;
			base = (currwk/100)*100;
			for( int i=1; i<=max; i++ ){
				map.put(base+i,ss.getValidRunningTimeForProduct( site, base+i, product ));
				map2.put(base+i,ss.getStationValidTimeByWeekForProduct( site, base+i, product ).size());
			}
		}
	}*/
	if(taskId>0){
		if(week > 1000){
			map.put(week,ss.getValidRunningTimeForTask( site, week, taskId ));
		}else{
			int currwk = CommonUtils.getWk(new Date());
			max = currwk%100;
			base = (currwk/100)*100;
			for( int i=1; i<=max; i++ ){
				float f = ss.getValidRunningTimeForTask( site, base+i, taskId );
				map.put(base+i,f);
				if(f>0)
					map2.put(base+i,ss.getStationValidTimeByWeekForTask( site, base+i, taskId ).size());
				else
					map2.put(base+i,0);
			}
		}
	}else{
		if(week > 1000){
			map.put(week,ss.getValidRunningTime( site, week ));
		}else{
			int currwk = CommonUtils.getWk(new Date());
			max = currwk%100;
			base = (currwk/100)*100;
			for( int i=1; i<=max; i++ ){
				float f = ss.getValidRunningTime( site, base+i );
				map.put(base+i,f);
				if(f>0)
					map2.put(base+i,ss.getStationValidTimeByWeek( site, base+i ).size());
				else
					map2.put(base+i,0);
			}
		}
	}
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Valid Running Time Charts</title>
<%if(week < 1000){ %>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/modules/exporting.js"></script>
<script type="text/javascript" >
$(function(){
	var chart;
	 $(document).ready(function() {
		chart = new Highcharts.Chart({
			chart: {
				renderTo: 'container'
				,type: 'column'
			},
			title: {
				text: 'Validated Running Time'
			},
			subtitle: {
				text: ''
			},
			xAxis: {
				labels: {  
                    formatter: function () {  
                        return 'WK'+this.value;
                    }  
                }  
			},
			
			yAxis: [{
				title: {
					text: 'Running Hours',
					color: '#AA4643'
				},
				stackLabels: {
                     enabled: true,
                     style: {
                         fontWeight: 'bold',
                         color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                     }
             	},
				min: 0,
				max: <%=criterion%>
                
			},{
				title: {
					text: 'Running Stations',
					color: '#BB4643'
				},
				min: 0,
				max: <%=sc%>,
				opposite: true
			}
			],
			tooltip: {
				enabled: true,
				formatter: function() {
					if(this.series.name=='Criterion Stations'){
						return '<b>WK'+ this.x +'</b><br/>'+this.series.name+':<%=sc%>';
					}else if(this.series.name=='Criterion Hours'){
						return '<b>WK'+ this.x +'</b><br/>'+this.series.name+':<%=criterion%>';
					}else{
						return '<b>WK'+ this.x +'</b><br/>'+this.series.name+':'+ this.y;
					}
				}
			},
			plotOptions: {
				column: {
					stacking: 'normal',
					dataLabels: {
						enabled: true
					},
					enableMouseTracking: true,
					events:{
						click:function(event){ 
							window.open("showStationUsage.jsp?siteName=<%=site%>&task=<%=taskId%>&week="+event.point.x);
						}
					}
				}
			},
			series: [
				{
					name:'Criterion Hours',
					type: 'column',
					stack: "Hours",
					yAxis: 0,
					data:[<%
					for(Map.Entry<Integer,Float> entry : map.entrySet()){%>
				      {x:<%=entry.getKey()%>,y:<%if(Math.round(entry.getValue())>0){%><%=criterion-Math.round(entry.getValue())%><%}else{out.print(0);}%>},
					<%}%>]
				},
				{
					name: 'Running Hours',
					type: 'column',
					stack: "Hours",
					yAxis: 0,
					data: [
						<%
							for(Map.Entry<Integer,Float> entry : map.entrySet()){%>
					      {x:<%=entry.getKey()%>,y:<%=Math.round(entry.getValue())%>},
						<%}%>
	
					]
				},
				{
					name:'Criterion Stations',
					type: 'column',
					stack: "Stations",
					yAxis: 1,
					data:[<%
					for(Map.Entry<Integer,Integer> entry : map2.entrySet()){%>
				      {x:<%=entry.getKey()%>,y:<%if(entry.getValue()>0){%><%=sc-entry.getValue()%><%}else{out.print(0);}%>},
					<%}%>]
				},
				{
					name: 'Running Stations',
					type: 'column',
					stack: "Stations",
					yAxis: 1,
					data: [
						<%
							for(Map.Entry<Integer,Integer> entry : map2.entrySet()){%>
					      {x:<%=entry.getKey()%>,y:<%=entry.getValue()%>},
						<%}%>
	
					]
				}
			]
		});
	 });	
});
</script>
<%} %>
</head>
<body>
<h3>Validated  Running Time 
<% if(week==0){%> from WK<%=base+1%> to WK<%=base+max%><%}else{%> for WK<%=week%><%}%>
<% if(product!=null&&!product.isEmpty()) {%> for Product <%=product%><%} %>
</h3>
<table border="1" align="center" width="60%">
<tr>
	<th>WEEK</th>
	<th>Valid Running Hours</th>
	<th>Running Stations</th>
</tr>
<%for(Map.Entry<Integer,Float> entry : map.entrySet()) {%>
<tr>
	<td style="text-align:center"><%=entry.getKey() %></td>
	<td style="text-align:center"><%=entry.getValue() %></td>
	<td style="text-align:center"><%=map2.get(entry.getKey()) %></td>
</tr>
<%} %>
<%if(week < 1000){ %>
<div id="container" style="min-width: 500px; height: 700px; margin: 0 auto">
    	
</div>
<%} %>
</table>
</body>
</html>