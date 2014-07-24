<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.reflect.Type"%>
<%@page import="com.google.gson.reflect.TypeToken"%>
<%
	String jsonStr = "";
	int cnt = 0;
	while(cnt<10){
		jsonStr = (String)session.getAttribute( "mtbfData" );
		if( jsonStr==null || jsonStr.length()==0){
			cnt ++;
			try{
				Thread.sleep( 1000 );
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			break;
		}
	}
	List<MtbfData> mds = CommonUtils.fromJson( jsonStr, new TypeToken<List<MtbfData>>(){}.getType() );
	session.removeAttribute( "mtbfData" );
	if(mds==null) mds = new ArrayList<MtbfData>(); 
	int size = mds.size();
%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>MTBF report Chart</title>
<link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.20.custom.min.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/modules/exporting.js"></script>
<script type="text/javascript" >
var chart;
function iframeAutoFit()
{
    try
    {
        if(window!=parent)
        {
            var a = parent.document.getElementsByTagName("IFRAME");
            for(var i=0; i<a.length; i++) //author:meizz
            {
                if(a[i].contentWindow==window)
                {
                    var h1=0, h2=0;
                    a[i].parentNode.style.height = a[i].offsetHeight +"px";
                    a[i].style.height = "10px";
                    if(document.documentElement&&document.documentElement.scrollHeight)
                    {
                        h1=document.documentElement.scrollHeight;
                    }
                    if(document.body) h2=document.body.scrollHeight;
                    var h=Math.max(h1, h2);
                    if(document.all) {h += 4;}
                    if(window.opera) {h += 1;}
                    a[i].style.height = a[i].parentNode.style.height = h +"px";
                }
            }
        }
    }
    catch (ex){}
}
if(window.attachEvent)
{
    window.attachEvent("onload",  iframeAutoFit);
    //window.attachEvent("onresize",  iframeAutoFit);
}
else if(window.addEventListener)
{
    window.addEventListener('load',  iframeAutoFit,  false);
    //window.addEventListener('resize',  iframeAutoFit,  false);
}
$(document).ready(function() {
	chart = new Highcharts.Chart({
		chart: {
			renderTo: 'container',
			type: 'line'
		},
		title: {
			text: 'MTBF Report'
		},
		subtitle: {
			text: 'Weekly MTBF Analyzing'
		},
		xAxis: {
			categories: [
			 <%
				 for(MtbfData md : mds) { size--;%>
				 '<%=md.getSoftVersion()%>'
				 <% if(size>0){%>,<%}%>
				 <% }%>
			]
		},
		yAxis: [
		    {
				title: {
					text: 'MTBF Value',
					color: '#AA4643'
				}
			},
			{
				title: {
					text: 'Resets/Freeze',
					style: {
						color: '#BAC6B3'
					}
				},
				opposite: true
			}
		],
		tooltip: {
			enabled: true,
			formatter: function() {
				return '<b>'+ this.series.name +'</b><br/>'+
					this.x +': '+ this.y ;
			}
		},
		plotOptions: {
			line: {
				dataLabels: {
					enabled: true
				},
				enableMouseTracking: true
			}
		},
		series: [{
			name: 'Mtbf Index',
			yAxis: 0,
			data: [
 			<% size = mds.size();
			for(MtbfData md : mds) { size --;%>
			<%=md.getMtbfIdx()%>
			<% if(size>0){%>,<%}%>
			<% }%>
			]
		}, {
			name: 'Target',
			yAxis: 0,
			data: [
			<% size = mds.size();
			for(MtbfData md : mds) { size--;%>
			<%=md.getTargetMtbf()%>
			<% if(size>0){%>,<%}%>
			<% }%>
			]
		}, {
			name: 'Resets/Freeze',
			yAxis: 1,
			data: [
			<% size = mds.size();
			for(MtbfData md : mds) {size--;%>
			<%=md.getResetAndFreeze()%>
			<% if(size>0){%>,<%}%>
			<% }%>
			]
		}]
	});
	
	iframeAutoFit();
	
});
</script>
</head>
<body>
	<div id="container" style="width: 100%; height: 400px"></div>
</body>
</html>