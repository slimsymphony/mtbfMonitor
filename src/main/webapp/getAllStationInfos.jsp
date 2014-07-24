<%@include file="header.jsp"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.*"%>
<%
Connection conn = null;
PreparedStatement ps = null;
ResultSet rs = null;
List<String[]> ss = new ArrayList<String[]>();
try {
	Class.forName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
	conn = DriverManager.getConnection( "jdbc:sqlserver://10.57.14.200:1433;databaseName=MTBF_MONITOR", "austere", "austere2" );
	ps = conn.prepareStatement( "select pcname,ip,mac,details from stationInfos order by pcname" );
	rs = ps.executeQuery();
	String pcName = null;
	String ip = null;
	String mac = null;
	String details = null;
	while( rs.next() ) {
		pcName = rs.getString(1);
		ip = rs.getString(2);
		mac = rs.getString(3);
		details = rs.getString(4);
		String[] arr = new String[]{pcName,ip,mac,details};
		ss.add(arr);
		/*System.out.println("pcName:"+pcName+",ip:"+ip+",mac:"+mac+",details:"+details);
		if( details!=null && !details.isEmpty() ) {
			String PCSystemName = details.substring(details.indexOf("<PCSystemName>")+13, details.indexOf("</PCSystemName>"));
			String CPUName = details.substring(details.indexOf("<CPUName>")+9, details.indexOf("</CPUName>"));
			String Memory = details.substring(details.indexOf("<Memory>")+9, details.indexOf("</Memory>"));
			String AustereVerison = details.substring(details.indexOf("<AustereVersion>")+16, details.indexOf("</AustereVersion>"));
			String IowCard = details.substring(details.indexOf("<IowCard>")+9, details.indexOf("</IowCard>"));
			System.out.println(",PcSystemName:"+PCSystemName+",CPUName:"+CPUName+",Memory:"+Memory+",AustereVerison:"+AustereVerison+",IowCard:"+IowCard);
		}*/
	}
} catch ( Exception e ) {
	e.printStackTrace();
	System.err.println( "Get Data Connection failed, " + e.getMessage() );
}finally {
	rs.close();
	ps.close();
	conn.close();
}
%>
<html>
<head>
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript">
function getdetail(msg){
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
	return content;
}
</script>
</head>
<body>
<table align="center" border="1" width="90%">
<tr>
	<th>PcName</th>
	<th>IP</th>
	<th>MAC</th>
	<th>Details</th>
</tr>
<%for(String[] arr : ss) {%>
<tr>
<td><%=arr[0] %></td>
<td><%=arr[1] %></td>
<td><%=arr[2] %></td>
<td><script>document.write(getdetail('<%=arr[3]%>'));</script></td>
</tr>
<%} %>
</table>
</body>
</html>