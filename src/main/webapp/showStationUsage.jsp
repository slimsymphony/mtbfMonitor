<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%!
Map<Station,Float> sort(Map<Station,Float> map) {
    ValueComparator bvc =  new ValueComparator(map);
    TreeMap<Station,Float> sorted_map = new TreeMap<Station,Float>(bvc);
    sorted_map.putAll(map);
    return sorted_map;
}
class ValueComparator implements Comparator<Station> {
    Map<Station,Float> base;
    public ValueComparator(Map<Station,Float> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(Station a, Station b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
%>
<%
	String site = request.getParameter("site");
	if(site == null)
		site = (String)session.getAttribute("site");
	int week = CommonUtils.parseInt(request.getParameter("week"),0);
	String product = request.getParameter("product");
	int taskId = CommonUtils.parseInt(request.getParameter("task"),0);
	TaskService ts = TaskServiceFactory.getInstance();
	Task task = ts.getTaskByID(taskId);
	StationService ss = StationServiceFactory.getInstance();
	Map<Station,Float> map = null; 
	float totalTime = 0f;
	/*if(product==null||product.isEmpty()||product.equalsIgnoreCase("null")){
		map = sort(ss.getStationValidTimeByWeek( site, week ));
	}else{
		map = sort(ss.getStationValidTimeByWeekForProduct( site, week, product ));
	}*/
	if(taskId>0){
		map = sort(ss.getStationValidTimeByWeekForTask( site, week, taskId ));
	}else{
		map = sort(ss.getStationValidTimeByWeek( site, week ));
	}
	if(map!=null){
		for(float v:map.values()){
			totalTime += v;
		}
	}
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>Valid Running Time Charts</title>
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
				renderTo: 'container',
				type: 'pie',
				plotBackgroundColor: null,
	            plotBorderWidth: null,
	            plotShadow: false
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
                        return this.value;
                    }  
                }  
			},
			yAxis: {
				title: {
					text: 'Running Hours',
					color: '#AA4643'
				},
				min: 0
                
			},
			tooltip: {
				enabled: true,
				pointFormat: 'Running Hours:<b>{point.y}</b> <br/>Percentage:<b>{point.percentage}%</b>',
            	percentageDecimals: 1
				/*formatter: function() {
					return '<b>{series.name}</b><br/>Hours:'+ this.y;
				}*/
			},
			plotOptions: {
				pie: {
					allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        connectorColor: '#000000',
                        formatter: function() {
                            return '<b>'+ this.point.name +'</b>: '+ this.point.y +' Hours';
                        }
                    }
				}
			},
			series: [
				{
					name: 'Usage Hours',
					type: 'pie',
					data: [
						<%
							for(Map.Entry<Station,Float> entry : map.entrySet()){%>
					      ['<%=entry.getKey().getPcName()%>',<%=entry.getValue()%>],
						<%}%>
	
					]
				},
			]
		});
	 });	
});
function trans( msg ){
	var xmlDoc = $(jQuery.parseXML(msg));
	var content = '';
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
	document.write(content);
}
</script>
</head>
<body>
<h3>Validated Running Time in Week <%=week %><% if(product!=null&&!product.isEmpty()) {%> for Product <%=product%><%} %>, total Stations:<%=map.size() %>, total valid time:<%=totalTime %> Hours</h3>
<%if(task!=null) {%><h4>TASK : <%=task.getProduct() %>_<%=task.getMilestone() %></h4><%} %>
<table border="1">
<tr>
	<th>Station</th>
	<th>Valid Running Hours</th>
</tr>
<%for(Map.Entry<Station,Float> entry : map.entrySet()) {Station stat = entry.getKey();%>
<tr>
	<td>
		<ul>
			<li><b>PCNAME:</b><%=stat.getPcName() %></li>
			<li><b>IP:</b><%=stat.getIp() %></li>
			<li><b>MAC:</b><%=stat.getMac() %></li>
			<li><b>DETAILS:</b><script>trans('<%=stat.getDetails() %>');</script></li>
			<li><b>STATUS:</b><%if(stat.getUsed()==Station.USED){out.print("Used");}else{out.print("Free");}%></li>
		</ul>
	</td>
	<td><%=entry.getValue() %></td>
</tr>
<%} %>
<div id="container" style="min-width: 500px; height: 700px; margin: 0 auto"></div>
</table>
</body>
</html>