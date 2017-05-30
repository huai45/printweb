<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%
String callback = request.getParameter("callback");
String info = (String)request.getAttribute("info");
String jspn = "{\"success\":\"true\",\"msg\":\""+info+"\"}";
String result = callback+"("+info+")";
System.out.println("result : "+result);
out.print(result);%>
