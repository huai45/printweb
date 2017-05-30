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
	 *  设置左边距
	 */
    public static void setLeftSpace(PrintWriter pw , int size){
    	pw.write(0x1d); 
    	pw.write(0x4c); 
    	pw.write(5); 
    	pw.write(0); 
	}
	
    /**
	 *  设置行间距
	 */
    public static void setLineSpace(PrintWriter pw , int size){
    	pw.write(0x1b); 
    	pw.write(0x33); 
    	pw.write(size); 
	}
    
	/**
	 *  设置打印字体大小
	 */
    public static void setFont(PrintWriter pw , int size){
    	pw.write(0x1D); 
    	pw.write('!'); 
    	pw.write(size);
	}
	
    /**
	 *  打印完毕自动走纸
	 */
	public static void pushPaper(PrintWriter pw){
		setFont( pw , 1 );
		for(int i=0;i<3;i++){ 
			pw.println(" ");
		}
	}
	
	/**
	 *  打印完毕自动切纸
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
			//设置左边距
			setLeftSpace( socketWriter , 0 );
			//设置行间距
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //设置字体
			socketWriter.println(" ***   今日明细   ***");
			setFont( socketWriter , 0);   //设置字体
			socketWriter.println(" ");
			//设置行间距
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  打印时间  ： "+ut.currentTime());
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    结账方式           金额");
			double total_money = 0;
			int recv_total = 0;
			for(int i=0;i< recv_data.size();i++){
	            Map item = (Map)recv_data.get(i);
	            log.info(" item : "+item);
	            recv_total = recv_total + Integer.parseInt(item.get("recv_fee").toString());
	            socketWriter.println("    "+item.get("mode_name")+"              ￥"+item.get("recv_fee"));
	        }
			socketWriter.println("    "+"合计"+"              ￥"+recv_total);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    楼    层           金额");
			for(int i=0;i< floor_data.size();i++){
	            Map item = (Map)floor_data.get(i);
	            log.info(" item : "+item);
	            socketWriter.println("    "+item.get("floor")+"              ￥"+item.get("money"));
	        }
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    实收合计             ￥"+recv_total);
	        socketWriter.println("    抹零金额             ￥"+item_data.get("moling_money").toString());
	        socketWriter.println("    舍弃零角             ￥"+item_data.get("lose_money").toString());
	        socketWriter.println("    打折金额             ￥"+item_data.get("discount_money").toString());
	        socketWriter.println("    总账单数             "+bill_count_data.get("close_count").toString());
	        socketWriter.println("    就餐人数             "+item_data.get("total_person").toString()+"人");
//	        socketWriter.println("    人均消费             ￥"+item_data.get("average_money").toString());
	        socketWriter.println(" ");
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			client.close();
			log.info("************  打印日结算单完毕  ************");
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
			setLeftSpace( socketWriter , 0 );    //设置左边距
			setLineSpace( socketWriter, 10 );    //设置行间距
			setFont( socketWriter , 17);         //设置字体
			socketWriter.println(" ***   档口明细   ***");
			setFont( socketWriter , 0);          //设置字体
			socketWriter.println(" ");
			setLineSpace( socketWriter , 64);    //设置行间距
			socketWriter.println(" ");
			socketWriter.println("  打印时间  ： "+ut.currentTime());
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    档口名         应收        实收  ");
			//double total_money = 0;
	        for(int i=0;i< category_data.size();i++){
	        	Map item = (Map)category_data.get(i);
	            log.info(" item : "+item);
	            socketWriter.println("     "+item.get("category")+"     ￥"+item.get("money")+"    ￥"+item.get("real_money"));
	        }
	        socketWriter.println(" ");
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// 会员卡消费凭证打印
	public static String printVipBill(IData info) {
		log.info(" *********  printVipCostMoney   开始  *********    ");
		String  printer = info.getString("printer");
//		Map info = data.get("info");
		Socket client = new java.net.Socket();
		String now = ut.currentTime();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			//设置左边距
			setLeftSpace( socketWriter , 0 );
			//设置行间距
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //设置字体
			socketWriter.println("**会员卡消费凭证 ①**");
			setFont( socketWriter , 0);   //设置字体
			socketWriter.println(" ");
			//设置行间距
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  结算时间  ： "+now);
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    卡号                 "+info.get("CARD_NO"));
	        socketWriter.println("    消费地点             "+info.get("RESTNAME"));
	        socketWriter.println("    消费前余额           ￥ "+info.get("OLD_MONEY"));
	        socketWriter.println("    消费金额             ￥ "+info.get("PAYFEE"));
	        socketWriter.println("    消费后余额           ￥ "+info.get("NEW_MONEY"));
	        socketWriter.println("    操作员               "+info.get("STAFF_NAME"));
	        socketWriter.println("    备注                 "+info.get("REMARK"));
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    本人对上述所列款项确认无误！");
	        socketWriter.println("    签字：");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // 打印饭店信息
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"欢迎您的光临");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  地址 :  "+info.get("ADDRESS"));
			socketWriter.println("  电话 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			setFont( socketWriter , 17);   //设置字体
			socketWriter.println("**会员卡消费凭证 ②**");
			setFont( socketWriter , 0);   //设置字体
			socketWriter.println(" ");
			//设置行间距
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  结算时间  ： "+now);
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    卡号                 "+info.get("CARD_NO"));
	        socketWriter.println("    消费地点             "+info.get("RESTNAME"));
	        socketWriter.println("    消费前余额           ￥ "+info.get("OLD_MONEY"));
	        socketWriter.println("    消费金额             ￥ "+info.get("PAYFEE"));
	        socketWriter.println("    消费后余额           ￥ "+info.get("NEW_MONEY"));
	        socketWriter.println("    操作员               "+info.get("STAFF_NAME"));
	        socketWriter.println("    备注                 "+info.get("REMARK"));
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    本人对上述所列款项确认无误！");
	        socketWriter.println("    签字：");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // 打印饭店信息
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"欢迎您的光临");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  地址 :  "+info.get("ADDRESS"));
			socketWriter.println("  电话 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("************  打印会员卡消费凭证完毕  ************");
    	return "";
	}

	// 会员卡充值凭证打印
	public static String printVipPay(IData info) {
		log.info(" *********  printVipAddmoney   开始  *********    ");
		String  printer = info.getString("printer");
		Socket client = new java.net.Socket();
		String now = ut.currentTime();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			//设置左边距
			setLeftSpace( socketWriter , 0 );
			//设置行间距
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //设置字体
			socketWriter.println(" ***  充值凭证 ① ***");
			setFont( socketWriter , 0);   //设置字体
			socketWriter.println(" ");
			//设置行间距
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  充值时间  ： "+now);
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    卡号                 "+info.get("CARD_NO"));
	        socketWriter.println("    充值地点             "+info.get("RESTNAME"));
	        socketWriter.println("    充值前余额           ￥ "+info.get("OLD_MONEY"));
	        socketWriter.println("    充值金额             ￥ "+info.get("PAYFEE"));
	        socketWriter.println("    充值后余额           ￥ "+info.get("NEW_MONEY"));
	        socketWriter.println("    操作员               "+info.get("STAFF_NAME"));
	        socketWriter.println("    备注                 "+"会员卡充值  发票未开！");
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    本人对上述所列款项确认无误！");
	        socketWriter.println("    签字：");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // 打印饭店信息
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"欢迎您的光临");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  地址 :  "+info.get("ADDRESS"));
			socketWriter.println("  电话 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			
			setFont( socketWriter , 17);   //设置字体
			socketWriter.println(" ***  充值凭证 ② ***");
			setFont( socketWriter , 0);   //设置字体
			socketWriter.println(" ");
			//设置行间距
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  充值时间  ： "+now);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    卡号                 "+info.get("CARD_NO"));
	        socketWriter.println("    充值地点             "+info.get("RESTNAME"));
	        socketWriter.println("    充值前余额           ￥ "+info.get("OLD_MONEY"));
	        socketWriter.println("    充值金额             ￥ "+info.get("PAYFEE"));
	        socketWriter.println("    充值后余额           ￥ "+info.get("NEW_MONEY"));
	        socketWriter.println("    操作员               "+info.get("STAFF_NAME"));
	        socketWriter.println("    备注                 "+"会员卡充值  发票未开！");
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    本人对上述所列款项确认无误！");
	        socketWriter.println("    签字：");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // 打印饭店信息
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"欢迎您的光临");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  地址 :  "+info.get("ADDRESS"));
			socketWriter.println("  电话 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("************  打印会员卡充值凭证完毕  ************");
    	return "";
	}

	// 会挂账用户消费凭证打印
	public static String printCreditBill(IData info) {
		log.info(" *********  printCreditCostMoney   开始  *********    ");
		String  printer = info.getString("printer");
		Socket client = new java.net.Socket();
		String now = ut.currentTime();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			//设置左边距
			setLeftSpace( socketWriter , 0 );
			//设置行间距
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //设置字体
			socketWriter.println("**挂账消费凭证 ①**");
			setFont( socketWriter , 0);   //设置字体
			socketWriter.println(" ");
			//设置行间距
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  结算时间  ： "+now);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    客户                 "+info.get("CUSTNAME"));
	        socketWriter.println("    消费地点             "+info.get("RESTNAME"));
	        socketWriter.println("    消费前欠费           ￥ "+info.get("OLD_MONEY"));
	        socketWriter.println("    消费金额             ￥ "+info.get("PAYFEE"));
	        socketWriter.println("    消费后欠费           ￥ "+info.get("NEW_MONEY"));
	        socketWriter.println("    操作员               "+info.get("STAFF_NAME"));
	        socketWriter.println("    备注                 "+info.get("REMARK"));
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    本人对上述所列款项确认无误！");
	        socketWriter.println("    签字：");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // 打印饭店信息
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"欢迎您的光临");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  地址 :  "+info.get("ADDRESS"));
			socketWriter.println("  电话 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			
			setFont( socketWriter , 17);   //设置字体
			socketWriter.println(" ** 挂账消费凭证 ②**");
			setFont( socketWriter , 0);   //设置字体
			socketWriter.println(" ");
			//设置行间距
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  结算时间  ： "+now);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    客户                 "+info.get("CUSTNAME"));
	        socketWriter.println("    消费地点             "+info.get("RESTNAME"));
	        socketWriter.println("    消费前欠费           ￥ "+info.get("OLD_MONEY"));
	        socketWriter.println("    消费金额             ￥ "+info.get("PAYFEE"));
	        socketWriter.println("    消费后欠费           ￥ "+info.get("NEW_MONEY"));
	        socketWriter.println("    操作员               "+info.get("STAFF_NAME"));
	        socketWriter.println("    备注                 "+info.get("REMARK"));
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    本人对上述所列款项确认无误！");
	        socketWriter.println("    签字：");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // 打印饭店信息
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"欢迎您的光临");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  地址 :  "+info.get("ADDRESS"));
			socketWriter.println("  电话 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("************  打印挂账消费凭证完毕  ************");
    	return "";
	}

	// 挂账用户冲正凭证打印
	public static String printCreditPay(IData info) {
		log.info(" *********  printCreditAddmoney   开始  *********    ");
		String  printer = info.getString("printer");
		Socket client = new java.net.Socket();
		String now = ut.currentTime();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			//设置左边距
			setLeftSpace( socketWriter , 0 );
			//设置行间距
			setLineSpace( socketWriter, 10 );    
			setFont( socketWriter , 17);   //设置字体
			socketWriter.println("**挂账缴费凭证 ① **");
			setFont( socketWriter , 0);   //设置字体
			socketWriter.println(" ");
			//设置行间距
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  缴费时间  ： "+now);
			socketWriter.println(" --------------------------------------");
	        socketWriter.println("    客户                 "+info.get("CUSTNAME"));
	        socketWriter.println("    缴费地点             "+info.get("RESTNAME"));
	        socketWriter.println("    缴费前欠费           ￥ "+info.get("OLD_MONEY"));
	        socketWriter.println("    缴费金额             ￥ "+info.get("PAYFEE"));
	        socketWriter.println("    缴费后欠费           ￥ "+info.get("NEW_MONEY"));
	        socketWriter.println("    操作员               "+info.get("STAFF_NAME"));
	        socketWriter.println("    备注                 "+"挂账缴费");
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    本人对上述所列款项确认无误！");
	        socketWriter.println("    签字：");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // 打印饭店信息
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"欢迎您的光临");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  地址 :  "+info.get("ADDRESS"));
			socketWriter.println("  电话 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			
			setFont( socketWriter , 17);   //设置字体
			socketWriter.println(" ** 挂账缴费凭证 ② **");
			setFont( socketWriter , 0);   //设置字体
			socketWriter.println(" ");
			//设置行间距
			setLineSpace( socketWriter , 64);
			socketWriter.println(" ");
			socketWriter.println("  缴费时间  ： "+now);
			socketWriter.println(" --------------------------------------");
			socketWriter.println("    客户                 "+info.get("CUSTNAME"));
	        socketWriter.println("    缴费地点             "+info.get("RESTNAME"));
	        socketWriter.println("    缴费前欠费           ￥ "+info.get("OLD_MONEY"));
	        socketWriter.println("    缴费金额             ￥ "+info.get("PAYFEE"));
	        socketWriter.println("    缴费后欠费           ￥ "+info.get("NEW_MONEY"));
	        socketWriter.println("    操作员               "+info.get("STAFF_NAME"));
	        socketWriter.println("    备注                 "+"挂账缴费");
	        socketWriter.println(" --------------------------------------");
	        socketWriter.println("    本人对上述所列款项确认无误！");
	        socketWriter.println("    签字：");
	        socketWriter.println(" ");
	        socketWriter.println(" ");
	        // 打印饭店信息
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" --------------------------------------");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+info.get("RESTNAME") +"欢迎您的光临");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  地址 :  "+info.get("ADDRESS"));
			socketWriter.println("  电话 :  "+info.get("TELEPHONE"));
			socketWriter.println(" --------------------------------------");
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("************  打印挂账缴费凭证完毕  ************");
    	return "";
	}
	
	
	
    
    
}
