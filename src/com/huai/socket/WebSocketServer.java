package com.huai.socket;

import java.net.InetSocketAddress;  
import java.util.concurrent.Executors;  
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;  
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;  

public class WebSocketServer {
	
	private static final Logger log = Logger.getLogger(WebSocketServer.class);

	private final int port; 
	
    public WebSocketServer(int port) { 
        this.port = port; 
    } 
    
    public void run() { 
        // ���� Socket channel factory  
        ServerBootstrap bootstrap = new ServerBootstrap( 
                new NioServerSocketChannelFactory( 
                        Executors.newCachedThreadPool(), 
                        Executors.newCachedThreadPool())); 
        // ���� Socket pipeline factory  
        bootstrap.setPipelineFactory(new WebSocketServerPipelineFactory()); 
        // �������񣬿�ʼ����  
        bootstrap.bind(new InetSocketAddress(port)); 
        // ��ӡ��ʾ��Ϣ  
        log.info("Web socket server started at port " + port + '.'); 
        log.info("Open your browser and navigate to ws://localhost:" + port + "/websocket"); 
    } 
    
    
    
}
