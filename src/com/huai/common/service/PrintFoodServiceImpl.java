package com.huai.common.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;

import netscape.javascript.JSObject;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.huai.common.dao.BaseDao;
import com.huai.common.dao.PrintDao;
import com.huai.common.domain.IData;
import com.huai.common.task.PrintBillTaskJob;
import com.huai.common.util.GetBean;
import com.huai.common.util.ut;

@Component("printFoodService")
public class PrintFoodServiceImpl implements PrintFoodService {

	private static final Logger log = Logger.getLogger(PrintFoodServiceImpl.class);
	
	@Resource(name="baseDao")
	public BaseDao baseDao;
	
	@Resource(name="printDao")
	public PrintDao printDao;
	
	
	public String printFood(IData food) throws Exception {
		ut.p(" 正在打印菜单 food = "+food);
		String print_id = food.getString("PRINT_ID");
		String barcode = food.getString("BARCODE");
		String printer = food.getString("PRINTER");
		String table_id = food.getString("TABLE_ID");
		String food_name = food.getString("FOOD_NAME");
		String nop = food.getString("NOP");
		String call_type = food.getString("CALL_TYPE");
		String call_type_show = getCallTypeShow(call_type);
		String count = food.getString("COUNT");
		String back_count = food.getString("BACK_COUNT");
		String price = food.getString("PRICE");
		String unit = food.getString("UNIT");
		String note = food.getString("NOTE");
		String oper_time = food.getString("OPER_TIME");
		String staff_name = food.getString("OPER_STAFF_NAME");
		String remark = food.getString("REMARK");
		int printCount = Integer.parseInt(food.getString("PRINT_COUNT"));
		// call_type --  0 ： 叫起    1 ： 即起   2：  起菜   3：  催菜   4:   退菜
		if(call_type.equals("2")){
	    	printCount = 1;
	    	printer = food.getString("PRINTER_START");
		}
		if(call_type.equals("3")){
	    	printCount = 1;
	    	printer = food.getString("PRINTER_HURRY");
		}
		if(call_type.equals("4")){
	    	printCount = 1;
	    	printer = food.getString("PRINTER_BACK");
		}
//		printer = "192.168.1.58";
		log.info(" printer = "+printer);
		if(printer.endsWith(".257")||printer.endsWith(".258")||printer.endsWith(".289")){
			food.put("STATE", "2");
			food.put("PRINT_TIME", ut.currentTime());
			food.put("REMARK", "success");
			return food.getString("STATE");
		}
		Socket client = new java.net.Socket();
		client.connect(new InetSocketAddress( printer , 9100),10*1000);
		PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
		//设置左边距
		setLeftSpace( socketWriter , 0 );
		//设置行间距
		setLineSpace( socketWriter, 10 );
		for(int i=0;i<printCount;i++){
			log.info(" printFood , Food_name =  "+food_name+" ,第 "+i+" 次打印  开始 ！！！！ printCount = "+printCount);
			//  打印备注信息   如 :  打印机故障!请转送:烧腊
			if(!remark.trim().equals("")){
				setFont( socketWriter , 17);    //设置字体
				socketWriter.println(remark.trim());
				setFont( socketWriter , 0);    //设置字体
				socketWriter.println(" ");
			}
			setFont( socketWriter , 1);    //设置字体
			if(call_type.equals("0")||call_type.equals("1")){
				if(i == 0){
					socketWriter.print("  正  式");
				}else{
					socketWriter.print(" @ 备份"+i);
				}
			}else{
			    socketWriter.print("       ");
			    //i = 9999; //  当起菜或者催菜时  只打印一张出来  所以把i设成一个比较大的数值，下次循环就不会执行， printCount(打印张数)也就失效了  
			}
			setFont( socketWriter , 18);    //设置字体
			socketWriter.println("       "+call_type_show);
			setFont( socketWriter , 0);    //设置字体
			socketWriter.println(" ");
			
			setFont( socketWriter , 17);    //设置字体
			socketWriter.println(" "+table_id+" 台    [ "+nop+" 人 ]");
			
			setFont( socketWriter , 0);    //设置字体
			socketWriter.println(" ");
			socketWriter.print(""+oper_time);
			socketWriter.println("     点菜员:"+staff_name);
			socketWriter.println(" ");
			
			setFont( socketWriter , 17);    //设置字体
			socketWriter.println(food_name);
			
			setFont( socketWriter , 0);    //设置字体
			socketWriter.println(" ");
			
			setFont( socketWriter , 17);    //设置字体
			if(call_type.equals("4")){
				socketWriter.println("点菜数量 ： "+count+" "+unit);
				socketWriter.println("总退菜量 ： "+back_count+" "+unit);
				double left_count = Double.parseDouble(count)-Double.parseDouble(back_count);
				socketWriter.println("剩余数量 ： "+ut.getDoubleString(left_count)+" "+unit);
			}else{
				if(Double.parseDouble(back_count)>0){
					socketWriter.println("点菜数量 ： "+count+" "+unit);
					socketWriter.println("退菜数量 ： "+back_count+" "+unit);
					double left_count = Double.parseDouble(count)-Double.parseDouble(back_count);
					socketWriter.println("制作数量 ： "+ut.getDoubleString(left_count)+" "+unit);
				}else{
					socketWriter.println("数量 ：    "+count+" "+unit);
				}
			}
			setFont( socketWriter , 0);    //设置字体
			socketWriter.println(" ");
			setFont( socketWriter , 1);    //设置字体
			if(!call_type.equals("4")){
				socketWriter.println("单价 ："+price+" 元        合计 ："+ut.getDoubleString(ut.doubled(price)*ut.doubled(count))+" 元");
			}
			if(note!=null && !note.trim().equals("") && !note.trim().equals("-")){
				setFont( socketWriter , 0);    //设置字体
				socketWriter.println(" ");
				setFont( socketWriter , 17);    //设置字体
				socketWriter.println("备注："+note.trim());
			}
			setFont( socketWriter , 1);    //设置字体
			socketWriter.println( "-----------------------------------------" );
			log.info(" call_type = "+call_type);
			// 只有即起和叫起的菜单打印条码，   起菜、催菜和退菜的都不打印条码,只打编号
			log.info(" barcode = "+barcode);
			socketWriter.println("No. "+ barcode ); 
			// call_type --  0 ： 叫起    1 ： 即起   2：  起菜   3：  催菜   4:   退菜
			if(call_type.equals("0")||call_type.equals("1")){ 
				
				//设置条码编码方式  
				socketWriter.write(0x1d); 
				socketWriter.write('k'); 
				socketWriter.write(70);  //设置条码编码方式为69  CODE39 
				socketWriter.write(12);   //设置条码位数为6位 
				char[] chars = barcode.toCharArray();
				for(int n = 0;n<chars.length;n++){
//						log.info(chars[n]+" : "+(int)chars[n]);
					socketWriter.write((int)chars[n]);
				}
				log.info(" end chars ....  ");
				socketWriter.println( barcode );
			}else{
				socketWriter.println(" ");
			}
			// 打印完毕自动走纸
			pushPaper(socketWriter);
			// 打印完毕自动切纸
			cutPaper(socketWriter);
			// 强制打印缓冲区
			socketWriter.flush();
			log.info(" printFood , Food_name =  "+food_name+" ,第 "+i+" 次打印 结束 ！！！！ printCount = "+printCount);
		}
		client.close();
		log.info(" -----  打印任务  end    ----  ");
		food.put("STATE", "2");
		food.put("PRINT_TIME", ut.currentTime());
		food.put("REMARK", "success");
		return food.getString("STATE");
	}

	public String printOneFood() {
		//1. 查询一条打印记录 
        IData param = new IData();
        param.put("count", "1");
        List data = printDao.queryPrintFood(param);
        if(data.size()==0){
        	return "";
        }
        IData food = new IData((Map)data.get(0));
        food.put("REMARK", "");
        //2. 打印菜品
        try {
			String state = printFood(food);
			//3. 记录打印结果
	        printDao.submitPrintFood(food);
		} catch (Exception e) {
			e.printStackTrace();
			// call_type --  0 ： 叫起    1 ： 即起   2：  起菜   3：  催菜   4:   退菜
			String call_type = food.getString("CALL_TYPE");
			if(call_type.equals("0")){
				food.put("REMARK", "IP:"+food.getString("PRINTER")+"打印故障!请转送");
			}
			if(call_type.equals("1")){
				food.put("REMARK", "IP:"+food.getString("PRINTER")+"打印故障!请转送");
			}
			if(call_type.equals("2")){
				food.put("REMARK", "IP:"+food.getString("PRINTER_START")+"打印故障!请转送");
			}
			if(call_type.equals("3")){
				food.put("REMARK", "IP:"+food.getString("PRINTER_HURRY")+"打印故障!请转送");
			}
			if(call_type.equals("4")){
				food.put("REMARK", "IP:"+food.getString("PRINTER_BACK")+"打印故障!请转送");
			}
			food.put("REMARK", food.getString("REMARK"));
			food.put("PRINTER", food.getString("PRINTER_SEC"));
			food.put("PRINTER_BACK", food.getString("PRINTER_SEC"));
			food.put("PRINTER_START", food.getString("PRINTER_SEC"));
			food.put("PRINTER_HURRY", food.getString("PRINTER_SEC"));
			try {
				String state = printFood(food);
				//3. 记录打印结果
				printDao.submitPrintFood(food);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				food.put("STATE", "9");
				food.put("PRINT_TIME", ut.currentTime());
				food.put("REMARK", food.getString("PRINTER")+":error;msg="+e1.getMessage());
				printDao.submitPrintFood(food);
			}
		}
        return "";
	}

	
	public String saveFoods(List foods) {
		for(int i=0;i<foods.size();i++){
        	IData food = new IData((Map)foods.get(i));
        	String reslut = printDao.saveFood(food);
		}
		return null;
	}
	
	
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
	 *  打印饭店信息
	 */
    private void printPaperFoot(PrintWriter pw,JSObject window){
		setFont( pw , 0);
    	pw.println(" ----------------------------------------—--");
    	setFont( pw , 1);
//		pw.println("          北京恺庐海鲜餐厅欢迎您的光临");
    	pw.println("          "+window.getMember("rest_name").toString() +"欢迎您的光临");
		setFont( pw , 0);
		pw.println(" ");
		pw.println("  地址 :  "+window.getMember("address").toString());
		pw.println("  电话 :  "+window.getMember("phone").toString());
		pw.println(" ----------------------------------------—--");
	}
    
    /**
	 *  打印公司信息
	 */
    private void printCompanyInfo(PrintWriter pw){
    	setFont( pw , 0);
    	pw.println(" ----------------------------------------—--");
    	setFont( pw , 1);
    	pw.println("          "+"" +"欢迎使用饭摩尔餐饮管理系统");
		setFont( pw , 0);
		pw.println(" ");
		pw.println("  网络订餐请登录：http://www.fanmole.com ");
		//pw.println("       客服电话 :  18622102888");
		pw.println(" ----------------------------------------—--");
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
    
    
    // call_type --  0 ： 叫起    1 ： 即起   2：  起菜   3：  催菜   4:   退菜
    public String getCallTypeShow(String type){
    	String str="";
    	if(type.equals("0")){
    		str="叫起";
    	}else if(type.equals("1")){
    		str="即起";
    	}else if(type.equals("2")){
    		str="起菜";
    	}else if(type.equals("3")){
    		str="催菜";
    	}else if(type.equals("4")){
    		str="退菜";
    	}
    	return str;
	}

	public String deleteHistory() {
		printDao.deleteHistory();
		return null;
	}
    

}
