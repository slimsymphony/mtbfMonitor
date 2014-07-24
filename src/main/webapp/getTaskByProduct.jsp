<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.testingservice.austere.service.*"%>
<%@page import="com.nokia.testingservice.austere.model.*"%>
<%@page import="com.nokia.testingservice.austere.util.*"%>
<%@page import="java.util.*"%><%
String product = request.getParameter("product");
String siteName = "";
if(request.getParameter("site")!=null){
	siteName = request.getParameter("site");
	session.setAttribute("site",siteName);
}else{
	if(session.getAttribute("site")!=null){
		siteName = (String) session.getAttribute("site");
	}
}
TaskService ts = TaskServiceFactory.getInstance();
List<Task> tasks = null;
if(product==null||product.isEmpty())
	tasks = ts.getAllValidTasks(siteName);
else
	tasks=ts.getTasksByProduct(siteName,product);
String json = CommonUtils.toJson(tasks);
out.print(json);
%>