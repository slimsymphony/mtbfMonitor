<%
	String siteName = "";
	if(request.getParameter("site")!=null){
		siteName = request.getParameter("site");
		session.setAttribute("site",siteName);
	}else{
		if(session.getAttribute("site")!=null){
			siteName = (String) session.getAttribute("site");
		}else{
			response.sendRedirect("selectSite.jsp");
		}
	}
	com.nokia.testingservice.austere.service.StatusService ss = new com.nokia.testingservice.austere.service.StatusServiceImpl();
	com.nokia.testingservice.austere.model.TestStation[] tss = ss.getCurrentStationStatus( siteName );
	out.println(com.nokia.testingservice.austere.util.CommonUtils.toJson(tss));
%>