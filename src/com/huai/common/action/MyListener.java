package com.huai.common.action;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.huai.socket.WebSocketServer;

public class MyListener  implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		new WebSocketServer(8070).run();
		
	}

}
