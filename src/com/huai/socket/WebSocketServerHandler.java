package com.huai.socket;

import static org.jboss.netty.handler.codec.http.HttpHeaders.*; 
import static org.jboss.netty.handler.codec.http.HttpMethod.*; 
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*; 
import static org.jboss.netty.handler.codec.http.HttpVersion.*; 
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers; 
import org.jboss.netty.channel.ChannelFuture; 
import org.jboss.netty.channel.ChannelFutureListener; 
import org.jboss.netty.channel.ChannelHandlerContext; 
import org.jboss.netty.channel.ExceptionEvent; 
import org.jboss.netty.channel.MessageEvent; 
import org.jboss.netty.channel.SimpleChannelUpstreamHandler; 
import org.jboss.netty.handler.codec.http.DefaultHttpResponse; 
import org.jboss.netty.handler.codec.http.HttpHeaders; 
import org.jboss.netty.handler.codec.http.HttpRequest; 
import org.jboss.netty.handler.codec.http.HttpResponse; 
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame; 
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame; 
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame; 
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame; 
import org.jboss.netty.handler.codec.http.websocketx.WebSocketFrame; 
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker; 
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory; 
import org.jboss.netty.util.CharsetUtil; 
import com.huai.common.domain.IData;
import com.huai.common.service.PrintBillService;
import com.huai.common.util.GetBean;
import com.huai.common.util.PrinterUtil;

/** 
* Handles handshakes and messages 
*/
public class WebSocketServerHandler extends SimpleChannelUpstreamHandler { 

    private static final Logger log = Logger.getLogger(WebSocketServer.class);
    private static final String WEBSOCKET_PATH = "/websocket"; 
    private WebSocketServerHandshaker handshaker; 

    //接收前台传来的message 
    @Override 
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception { 
        Object msg = e.getMessage();
        long t1 = System.currentTimeMillis();
        log.info(" messageReceived   msg = "+msg);
        if (msg instanceof HttpRequest) {  //分辨请求类型并进行相应处理
            handleHttpRequest(ctx, (HttpRequest) msg); 
        } else if (msg instanceof WebSocketFrame) { 
            handleWebSocketFrame(ctx, (WebSocketFrame) msg); 
        } 
        long t2 = System.currentTimeMillis();
        log.info(" messageReceived   use time = "+(t2-t1));
    } 

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception { 
        // Allow only GET methods.    只允许GET方法 
        if (req.getMethod() != GET) { 
            sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN)); 
            return; 
        } 

        // Handshake  socket握手 
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory( 
                getWebSocketLocation(req), null, false); 
        handshaker = wsFactory.newHandshaker(req); 
        if (handshaker == null) { 
            wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel()); 
        } else { 
            handshaker.handshake(ctx.getChannel(), req).addListener(WebSocketServerHandshaker.HANDSHAKE_LISTENER); 
        } 
    } 
    
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) { 
        // Check for closing frame    检查websocket是否关闭 
        if (frame instanceof CloseWebSocketFrame) { 
            handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame); 
            return; 
        } else if (frame instanceof PingWebSocketFrame) { 
            ctx.getChannel().write(new PongWebSocketFrame(frame.getBinaryData())); 
            return; 
        } else if (!(frame instanceof TextWebSocketFrame)) { 
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass() 
                    .getName())); 
        }
        String request = ((TextWebSocketFrame) frame).getText();
        log.info(" handleWebSocketFrame   request = "+request);
        JSONObject obj = JSONObject.fromObject(request);
        //  type :   printbill - 打印账单 ;  
        String type = obj.getString("type");
        if(type.equals("printbill")){
        	IData bill = new IData((Map)obj.get("bill"));
        	PrintBillService printBillService = (PrintBillService)GetBean.getBean("printBillService");
        	printBillService.printBill(bill);
//            List fees = (List)bill.get("FEELIST");
//            List items = (List)bill.get("ITEMLIST");
//            Map info = (Map)bill.get("INFO");
//            log.info(" info = "+info);
//            log.info(" fees.size() = "+fees.size());
//            log.info(" items.size() = "+items.size());
        }
//        if(type.equals("readcard")){
//        	request = CardUtil.readCard();
////        	request = "00000000000000000000000000001002";
//        }
//        if(type.equals("writecard")){
//        	String card_no = obj.get("card_no").toString();
//        	request = CardUtil.writeCard(card_no);
//        }
        if(type.equals("printtoday")){
        	IData data = new IData((Map)obj.get("data"));
        	request = PrinterUtil.printTodayMoney(data);
        }
        if(type.equals("printcategory")){
        	IData data = new IData((Map)obj.get("data"));
        	request = PrinterUtil.printCategory(data);
        }
        if(type.equals("printvipbill")){
        	IData data = new IData((Map)obj.get("data"));
        	request = PrinterUtil.printVipBill(data);
        }
        if(type.equals("printvippay")){
        	IData data = new IData((Map)obj.get("data"));
        	request = PrinterUtil.printVipPay(data);
        }
        if(type.equals("printcreditbill")){
        	IData data = new IData((Map)obj.get("data"));
        	request = PrinterUtil.printCreditBill(data);
        }
        if(type.equals("printcreditpay")){
        	IData data = new IData((Map)obj.get("data"));
        	request = PrinterUtil.printCreditPay(data);
        }
//        if (logger.isDebugEnabled()) { 
//            logger.debug(String.format("Channel %s received %s", ctx.getChannel().getId(), request)); 
//        }
        log.info(" handleWebSocketFrame   result = "+request);
        ctx.getChannel().write(new TextWebSocketFrame(request));
    } 

	private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) { 
        // Generate an error page if response status code is not OK (200). 
        if (res.getStatus().getCode() != 200) { 
            res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8)); 
            setContentLength(res, res.getContent().readableBytes()); 
        }
        // Send the response and close the connection if necessary. 
        ChannelFuture f = ctx.getChannel().write(res); 
        if (!isKeepAlive(req) || res.getStatus().getCode() != 200) { 
            f.addListener(ChannelFutureListener.CLOSE); 
        } 
    } 

    @Override 
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception { 
        e.getCause().printStackTrace(); 
        e.getChannel().close(); 
    } 

    private static String getWebSocketLocation(HttpRequest req) { 
        return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET_PATH; 
    } 
} 