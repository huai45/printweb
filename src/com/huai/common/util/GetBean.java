package com.huai.common.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class GetBean extends HttpServlet {

    private static WebApplicationContext context;

    public void init() throws ServletException {
        context = WebApplicationContextUtils.getWebApplicationContext(this
                .getServletContext());
        ut.log(" ******   GetBean ************** context: "+context);
    }

    public static Object getBean(String id) {
    	Object bean = null;
    	try{
    		bean = context.getBean(id);
    	} catch (Exception e) {
			e.printStackTrace();
		}
        return bean;
        //return context.getBean(id);
    }
    
	public static WebApplicationContext getContext() {
		return context;
	}
    
}
