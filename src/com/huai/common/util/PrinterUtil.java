package com.huai.common.util;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import netscape.javascript.JSObject;
import com.huai.common.domain.IData;
import com.huai.socket.WebSocketServer;

public class PrinterUtil {

	private static final Logger log = Logger.getLogger(PrinterUtil.class);
	
	/**
	 *  ������߾�
	 */
    public static void setLeftSpace(PrintWriter pw , int size){
    	pw.write(0x1d); 
    	pw.write(0x4c); 
    	pw.write(5); 
    	pw.write(0); 
	}
	
    /**
	 *  �����м��
	 */
    public static void setLineSpace(PrintWriter pw , int size){
    	pw.write(0x1b); 
    	pw.write(0x33); 
    	pw.write(size); 
	}
    
	/**
	 *  ���ô�ӡ�����С
	 */
    public static void setFont(PrintWriter pw , int size){
    	pw.write(0x1D); 
    	pw.write('!'); 
    	pw.write(size);
	}
	
    /**
	 *  ��ӡ����Զ���ֽ
	 */
	public static void pushPaper(PrintWriter pw){
		setFont( pw , 1 );
		for(int i=0;i<3;i++){ 
			pw.println(" ");
		}
	}
	
	/**
	 *  ��ӡ����Զ���ֽ
	 */
    public static void cutPaper(PrintWriter pw){
    	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	pw.write(0x1b);
    	pw.write('m');
	}

	public static String printTodayMoney(IData data) {
		List recv_data = (List)data.get("recv_data");
		log.info(" recv_data.size() : "+recv_data.size());
		List floor_data = (List)data.get("floor_data");
		log.info(" floor_data.size() : "+floor_data.size());
		Map item_data = (Map)((List)data.get("item_data")).get(0);
		log.info(" item_data : "+item_data);
		Map bill_count_data = (Map)((List)data.get("bill_count_data")).get(0);
		log.info(" bill_count_data : "+bill_count_data);
		
		log.info(" ***************  printTodayMoney *************** ");
		String  printer = data.getString("printer");
		Socket client = new java.net.Socket();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			//������߾�
			setLeftSpace( socketWriter , 0 );
			//�����м��
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //��������
			socketWriter.println(" ***   ������ϸ   ***");
			setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			//�����м��
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  ��ӡʱ��  �� "+ut.currentTime());
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    ���˷�ʽ           ���");
			double total_money = 0;
			int recv_total = 0;
			for(int i=0;i< recv_data.size();i++){
	            Map item = (Map)recv_data.get(i);
	            log.info(" item : "+item);
	            recv_total = recv_total + Integer.parseInt(item.get("recv_fee").toString());
	            socketWriter.println("    "+item.get("mode_name")+"              ��"+item.get("recv_fee"));
	        }
			socketWriter.println("    "+"�ϼ�"+"              ��"+recv_total);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    ¥    ��           ���");
			for(int i=0;i< floor_data.size();i++){
	            Map item = (Map)floor_data.get(i);
	            log.info(" item : "+item);
	            socketWriter.println("    "+item.get("floor")+"              ��"+item.get("money"));
	        }
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ʵ�պϼ�             ��"+recv_total);
	        socketWriter.println("    Ĩ����             ��"+item_data.get("moling_money").toString());
	        socketWriter.println("    �������             ��"+item_data.get("lose_money").toString());
	        socketWriter.println("    ���۽��             ��"+item_data.get("discount_money").toString());
	        socketWriter.println("    ���˵���             "+bill_count_data.get("close_count").toString());
	        socketWriter.println("    �Ͳ�����             "+item_data.get("total_person").toString()+"��");
//	        socketWriter.println("    �˾�����             ��"+item_data.get("average_money").toString());
	        socketWriter.println(" ");
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			client.close();
			log.info("************  ��ӡ�ս��㵥���  ************");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return "";
	}

	public static String printCategory(IData data) {
		List category_data = (List)data.get("category_data");
		log.info(" category_data.size() : "+category_data.size());
		log.info(" ***************  printCategory *************** ");
		String  printer = data.getString("printer");
		log.info(" printer : "+printer);
		Socket client = new java.net.Socket();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			setLeftSpace( socketWriter , 0 );    //������߾�
			setLineSpace( socketWriter, 10 );    //�����м��
			setFont( socketWriter , 17);         //��������
			socketWriter.println(" ***   ������ϸ   ***");
			setFont( socketWriter , 0);          //��������
			socketWriter.println(" ");
			setLineSpace( socketWriter , 64);    //�����м��
			socketWriter.println(" ");
			socketWriter.println("  ��ӡʱ��  �� "+ut.currentTime());
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    ������         Ӧ��        ʵ��  ");
			//double total_money = 0;
	        for(int i=0;i< category_data.size();i++){
	        	Map item = (Map)category_data.get(i);
	            log.info(" item : "+item);
	            socketWriter.println("     "+item.get("category")+"     ��"+item.get("money")+"    ��"+item.get("real_money"));
	        }
	        socketWriter.println(" ");
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// ��Ա������ƾ֤��ӡ
	public static String printVipBill(IData info) {
		log.info(" *********  printVipCostMoney   ��ʼ  *********    ");
		String  printer = info.getString("printer");
//		Map info = data.get("info");
		Socket client = new java.net.Socket();
		String now = ut.currentTime();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			//������߾�
			setLeftSpace( socketWriter , 0 );
			//�����м��
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //��������
			socketWriter.println("**��Ա������ƾ֤ ��**");
			setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			//�����м��
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  ����ʱ��  �� "+now);
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ����                 "+info.get("CARD_NO"));
	        socketWriter.println("    ���ѵص�             "+info.get("RESTNAME"));
	        socketWriter.println("    ����ǰ���           �� "+info.get("OLD_MONEY"));
	        socketWriter.println("    ���ѽ��             �� "+info.get("PAYFEE"));
	        socketWriter.println("    ���Ѻ����           �� "+info.get("NEW_MONEY"));
	        socketWriter.println("    ����Ա               "+info.get("STAFF_NAME"));
	        socketWriter.println("    ��ע                 "+info.get("REMARK"));
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ���˶��������п���ȷ������");
	        socketWriter.println("    ǩ�֣�");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+info.get("ADDRESS"));
			socketWriter.println("  �绰 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			setFont( socketWriter , 17);   //��������
			socketWriter.println("**��Ա������ƾ֤ ��**");
			setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			//�����м��
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  ����ʱ��  �� "+now);
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ����                 "+info.get("CARD_NO"));
	        socketWriter.println("    ���ѵص�             "+info.get("RESTNAME"));
	        socketWriter.println("    ����ǰ���           �� "+info.get("OLD_MONEY"));
	        socketWriter.println("    ���ѽ��             �� "+info.get("PAYFEE"));
	        socketWriter.println("    ���Ѻ����           �� "+info.get("NEW_MONEY"));
	        socketWriter.println("    ����Ա               "+info.get("STAFF_NAME"));
	        socketWriter.println("    ��ע                 "+info.get("REMARK"));
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ���˶��������п���ȷ������");
	        socketWriter.println("    ǩ�֣�");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+info.get("ADDRESS"));
			socketWriter.println("  �绰 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("************  ��ӡ��Ա������ƾ֤���  ************");
    	return "";
	}

	// ��Ա����ֵƾ֤��ӡ
	public static String printVipPay(IData info) {
		log.info(" *********  printVipAddmoney   ��ʼ  *********    ");
		String  printer = info.getString("printer");
		Socket client = new java.net.Socket();
		String now = ut.currentTime();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			//������߾�
			setLeftSpace( socketWriter , 0 );
			//�����м��
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //��������
			socketWriter.println(" ***  ��ֵƾ֤ �� ***");
			setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			//�����м��
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  ��ֵʱ��  �� "+now);
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ����                 "+info.get("CARD_NO"));
	        socketWriter.println("    ��ֵ�ص�             "+info.get("RESTNAME"));
	        socketWriter.println("    ��ֵǰ���           �� "+info.get("OLD_MONEY"));
	        socketWriter.println("    ��ֵ���             �� "+info.get("PAYFEE"));
	        socketWriter.println("    ��ֵ�����           �� "+info.get("NEW_MONEY"));
	        socketWriter.println("    ����Ա               "+info.get("STAFF_NAME"));
	        socketWriter.println("    ��ע                 "+"��Ա����ֵ  ��Ʊδ����");
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ���˶��������п���ȷ������");
	        socketWriter.println("    ǩ�֣�");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+info.get("ADDRESS"));
			socketWriter.println("  �绰 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			
			setFont( socketWriter , 17);   //��������
			socketWriter.println(" ***  ��ֵƾ֤ �� ***");
			setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			//�����м��
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  ��ֵʱ��  �� "+now);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    ����                 "+info.get("CARD_NO"));
	        socketWriter.println("    ��ֵ�ص�             "+info.get("RESTNAME"));
	        socketWriter.println("    ��ֵǰ���           �� "+info.get("OLD_MONEY"));
	        socketWriter.println("    ��ֵ���             �� "+info.get("PAYFEE"));
	        socketWriter.println("    ��ֵ�����           �� "+info.get("NEW_MONEY"));
	        socketWriter.println("    ����Ա               "+info.get("STAFF_NAME"));
	        socketWriter.println("    ��ע                 "+"��Ա����ֵ  ��Ʊδ����");
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ���˶��������п���ȷ������");
	        socketWriter.println("    ǩ�֣�");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+info.get("ADDRESS"));
			socketWriter.println("  �绰 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("************  ��ӡ��Ա����ֵƾ֤���  ************");
    	return "";
	}

	// ������û�����ƾ֤��ӡ
	public static String printCreditBill(IData info) {
		log.info(" *********  printCreditCostMoney   ��ʼ  *********    ");
		String  printer = info.getString("printer");
		Socket client = new java.net.Socket();
		String now = ut.currentTime();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			//������߾�
			setLeftSpace( socketWriter , 0 );
			//�����м��
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //��������
			socketWriter.println("**��������ƾ֤ ��**");
			setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			//�����м��
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  ����ʱ��  �� "+now);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    �ͻ�                 "+info.get("CUSTNAME"));
	        socketWriter.println("    ���ѵص�             "+info.get("RESTNAME"));
	        socketWriter.println("    ����ǰǷ��           �� "+info.get("OLD_MONEY"));
	        socketWriter.println("    ���ѽ��             �� "+info.get("PAYFEE"));
	        socketWriter.println("    ���Ѻ�Ƿ��           �� "+info.get("NEW_MONEY"));
	        socketWriter.println("    ����Ա               "+info.get("STAFF_NAME"));
	        socketWriter.println("    ��ע                 "+info.get("REMARK"));
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ���˶��������п���ȷ������");
	        socketWriter.println("    ǩ�֣�");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+info.get("ADDRESS"));
			socketWriter.println("  �绰 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			
			setFont( socketWriter , 17);   //��������
			socketWriter.println(" ** ��������ƾ֤ ��**");
			setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			//�����м��
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  ����ʱ��  �� "+now);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    �ͻ�                 "+info.get("CUSTNAME"));
	        socketWriter.println("    ���ѵص�             "+info.get("RESTNAME"));
	        socketWriter.println("    ����ǰǷ��           �� "+info.get("OLD_MONEY"));
	        socketWriter.println("    ���ѽ��             �� "+info.get("PAYFEE"));
	        socketWriter.println("    ���Ѻ�Ƿ��           �� "+info.get("NEW_MONEY"));
	        socketWriter.println("    ����Ա               "+info.get("STAFF_NAME"));
	        socketWriter.println("    ��ע                 "+info.get("REMARK"));
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ���˶��������п���ȷ������");
	        socketWriter.println("    ǩ�֣�");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+info.get("ADDRESS"));
			socketWriter.println("  �绰 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("************  ��ӡ��������ƾ֤���  ************");
    	return "";
	}

	// �����û�����ƾ֤��ӡ
	public static String printCreditPay(IData info) {
		log.info(" *********  printCreditAddmoney   ��ʼ  *********    ");
		String  printer = info.getString("printer");
		Socket client = new java.net.Socket();
		String now = ut.currentTime();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			//������߾�
			setLeftSpace( socketWriter , 0 );
			//�����м��
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //��������
			socketWriter.println("**���˽ɷ�ƾ֤ �� **");
			setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			//�����м��
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  �ɷ�ʱ��  �� "+now);
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    �ͻ�                 "+info.get("CUSTNAME"));
	        socketWriter.println("    �ɷѵص�             "+info.get("RESTNAME"));
	        socketWriter.println("    �ɷ�ǰǷ��           �� "+info.get("OLD_MONEY"));
	        socketWriter.println("    �ɷѽ��             �� "+info.get("PAYFEE"));
	        socketWriter.println("    �ɷѺ�Ƿ��           �� "+info.get("NEW_MONEY"));
	        socketWriter.println("    ����Ա               "+info.get("STAFF_NAME"));
	        socketWriter.println("    ��ע                 "+"���˽ɷ�");
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ���˶��������п���ȷ������");
	        socketWriter.println("    ǩ�֣�");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+info.get("ADDRESS"));
			socketWriter.println("  �绰 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			
			setFont( socketWriter , 17);   //��������
			socketWriter.println(" ** ���˽ɷ�ƾ֤ �� **");
			setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			//�����м��
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  �ɷ�ʱ��  �� "+now);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    �ͻ�                 "+info.get("CUSTNAME"));
	        socketWriter.println("    �ɷѵص�             "+info.get("RESTNAME"));
	        socketWriter.println("    �ɷ�ǰǷ��           �� "+info.get("OLD_MONEY"));
	        socketWriter.println("    �ɷѽ��             �� "+info.get("PAYFEE"));
	        socketWriter.println("    �ɷѺ�Ƿ��           �� "+info.get("NEW_MONEY"));
	        socketWriter.println("    ����Ա               "+info.get("STAFF_NAME"));
	        socketWriter.println("    ��ע                 "+"���˽ɷ�");
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    ���˶��������п���ȷ������");
	        socketWriter.println("    ǩ�֣�");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+info.get("ADDRESS"));
			socketWriter.println("  �绰 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("************  ��ӡ���˽ɷ�ƾ֤���  ************");
    	return "";
	}
	
	
	
    
    
}
