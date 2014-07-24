<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%>
<%
	String site = "";
	if(request.getParameter("site")!=null){
		site = request.getParameter("site");
		session.setAttribute("site",site);
	}else{
		if(session.getAttribute("site")!=null){
			site = (String) session.getAttribute("site");
		}
	}
	int amount = CommonUtils.parseInt(request.getParameter("amount"),0);
	int seq = CommonUtils.parseInt(request.getParameter("seq"),0);
	String type= request.getParameter("type");
	String errorID = request.getParameter("errorID");
	String desc = request.getParameter("desc");
	String status = request.getParameter("status");
	int reportDay = CommonUtils.parseInt(request.getParameter("reportDay"),0);
	int isKnown = CommonUtils.parseInt(request.getParameter("isKnown"), Constants.MTBF_STATUS_UNKNOWN);
	String pcName = request.getParameter("pcName");
	MTBFDetail md = new MTBFDetail();
	md.setSeq(seq);
	md.setAmount(amount);
	md.setType(type);
	md.setErrorID(errorID);
	md.setDesc(desc);
	md.setStatus(status);
	md.setIsKnown(isKnown);
	md.setReportDate(reportDay);
	StationService ss = StationServiceFactory.getInstance();
	md.setStationID(ss.getStationByPcName(site,pcName).getId());
	MtbfService ms = MtbfServiceFactory.getInstance();
	try{
		ms.updateMTBFDetail(md);
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
%>