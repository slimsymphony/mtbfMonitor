<%@include file="header.jsp"%><%@page contentType="text/json;charset=UTF-8"%><%@page import="com.nokia.testingservice.austere.service.*"%><%@page import="com.nokia.testingservice.austere.model.*"%><%@page import="com.nokia.testingservice.austere.util.*"%><%@page import="java.sql.*"%><%
	String site = "TEST";
	if(request.getParameter("site")!=null){
		site = request.getParameter("site");
		session.setAttribute("site",site);
	}else{
		if(session.getAttribute("site")!=null){
			site = (String) session.getAttribute("site");
		}
	}
	String id = request.getParameter("id");
	String pcname = request.getParameter("pcname");
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	try{
		conn = DbUtils.getMonitorConnection(site);
		ps = conn.prepareStatement("select details from stationInfos where pcname=?");
		ps.setString(1,pcname);
		rs = ps.executeQuery();
		if(rs.next()){
			Station s = new Station();
			out.print(rs.getString("details"));
		}
	}catch(Exception ex){
		ex.printStackTrace();
	}finally{
		CommonUtils.closeQuitely(rs);
		CommonUtils.closeQuitely(ps);
		CommonUtils.closeQuitely(conn);
	}
%>