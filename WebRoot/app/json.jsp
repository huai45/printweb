<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%
String callback = request.getParameter("callback");
String msg = request.getParameter("msg");
System.out.println("callback:"+callback);
System.out.println("msg.length():"+msg.length());
System.out.println("msg:"+msg);
String jspn = "{\"success\":\"true\",\"msg\":\"haha\"}";
String result = callback+"("+jspn+")";
System.out.println("result:"+result);
out.print(result);%>