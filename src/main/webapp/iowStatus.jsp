<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	int start = CommonUtils.parseInt(request.getParameter("start"),0);
	int end = CommonUtils.parseInt(request.getParameter("end"),0);
	int chartType = CommonUtils.parseInt(request.getParameter("type"),1);
	String site = request.getParameter("site");
	if(site == null)
		site = (String)session.getAttribute("site");
	Map<Integer,Map<String,Integer>> map = null;
	String product = request.getParameter("product");
	int taskId = CommonUtils.parseInt(request.getParameter("task"),0);
	TaskService ts = TaskServiceFactory.getInstance();
	Task task = ts.getTaskByID(taskId);
	StationService ss = StationServiceFactory.getInstance();
	/*if(product==null||product.isEmpty()||product.equalsIgnoreCase("null")){
		map = ss.getIowCardsChanges( site, start, end ); 
	}else{
		map = ss.getIowCardsChangesForProduct( site, start, end, product );
	}*/
	if(taskId>0){
		map = ss.getIowCardsChangesForTask( site, start, end, taskId );
	}else{
		map = ss.getIowCardsChanges( site, start, end );
	}
	List<String> names = new ArrayList<String>();
	for(Map<String,Integer> m : map.values()){
		for(String str : m.keySet(  )){
			if(!names.contains( str ))
				names.add(str);
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
$(function () {
	Highcharts.setOptions({
		global: {
			useUTC: false
		}
	});
	var labels = [];
	<%for(int i=0;i<names.size(  );i++){%>
	labels[<%=i%>] = '<%=names.get(i)%>';
	<%}%>
    var chart;
    $(document).ready(function() {
    	$('#switch').val(<%=chartType%>);
    	$('#switch').change(function(){
    		window.location.href='iowStatus.jsp?start=<%=start%>&end=<%=end%>&siteName=<%=site%>&product=<%=product%>&type='+$(this).val()+"&task=<%=taskId%>";
    	});
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'container',
                type: '<%if(chartType==1){out.print("line");}else{out.print("column");}%>',
                marginRight: 130,
                marginBottom: 25
            },
            title: {
                text: 'IowCard Usage Diagram',
                x: -20 //center
            },
            xAxis: {
                labels: {  
                    formatter: function () {  
                        return 'WK'+this.value;
                    }  
                },
                tickInterval: 1
            },
            yAxis: {
                title: {
                    text: 'IowCard Number'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }],
                <%if(chartType!=1){%>
                stackLabels: {
                        enabled: true,
                        style: {
                            fontWeight: 'bold',
                            color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                        }
                },
                <%}%>
                formatter: function () {  
                    return this.value;
                },
                min: 0
            },
            tooltip: {
                formatter: function() {
					var tip =   '<b>WK'+ this.x +'</b><br/>'+ this.series.name +': '+ this.y +'<br/>';
					<%if(chartType!=1){%> tip += 'Total: '+ this.point.stackTotal;<%}%>
					return tip;
                }
            },
            plotOptions: {
                column: {
                    stacking: 'normal',
                    dataLabels: {
                        enabled: true,
                        color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
                    }
                }
            },	
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 100,
                borderWidth: 1
            },
            series: [
            <%for(String na : names){%>
    			{
	                name: '<%=na%>',
	                data: [
	                <%for(Map.Entry<Integer,Map<String,Integer>> entry : map.entrySet( )){
	                	int num = 0;
	                	for( Map.Entry<String,Integer> en2 : entry.getValue().entrySet( )){
	                		if(na.equals(en2.getKey())){
	                			num = en2.getValue(); 
	                			break;
	                		}
	                	}
	                %>
	                      {y:<%=num%>,x:<%=entry.getKey()%>},
	                <%}%>
	                ]
	            },
	         <%}%>
	         /*{
	                name: 'All LowCards',
	                data: [
	                <%for(Map.Entry<Integer,Map<String,Integer>> entry : map.entrySet( )){
	                	int num = 0;
	                	for( Map.Entry<String,Integer> en2 : entry.getValue().entrySet( )){
	                			num += en2.getValue(); 
	                	}
	                %>
	                      {y:<%=num%>,x:<%=entry.getKey()%>},
	                <%}%>
	                ]
	            }*/
             ]
        });
    });
    
});
</script>
</head>
<body>
<h3>IowCard usage Status from WK<%=start%> to WK<%=end%> <%if(task!=null){%> for <%=task.getProduct() %>_<%=task.getMilestone() %><%} %>
</h3>
<select id="switch"><option value="1">Line Chart</option><option value="2">Column Chart</option></select>
<table width="80%" border=1>
<tr>
	<th>WEEK</th>
	<th>Details</th>
</tr>
<%for(Map.Entry<Integer,Map<String,Integer>> entry : map.entrySet()) {%>
<tr>
	<td><%=entry.getKey() %></td>
	<td><ul>
	<%int num = 0;for(Map.Entry<String,Integer> en3 : entry.getValue().entrySet( ) ) {num +=en3.getValue();%>
	<li><%=en3.getKey() %>:&nbsp;&nbsp;&nbsp;<%=en3.getValue() %></li>
	<%} %>
	<li>Total: &nbsp;&nbsp;&nbsp;<%=num%></li></ul>
	</td>
</tr>
<%} %>
<div id="container" style="min-width: 500px; height: 700px; margin: 0 auto"></div>
</table>
</body>
</html>