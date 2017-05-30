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
		ut.p(" ���ڴ�ӡ�˵� food = "+food);
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
		// call_type --  0 �� ����    1 �� ����   2��  ���   3��  �߲�   4:   �˲�
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
		//������߾�
		setLeftSpace( socketWriter , 0 );
		//�����м��
		setLineSpace( socketWriter, 10 );
		for(int i=0;i<printCount;i++){
			log.info(" printFood , Food_name =  "+food_name+" ,�� "+i+" �δ�ӡ  ��ʼ �������� printCount = "+printCount);
			//  ��ӡ��ע��Ϣ   �� :  ��ӡ������!��ת��:����
			if(!remark.trim().equals("")){
				setFont( socketWriter , 17);    //��������
				socketWriter.println(remark.trim());
				setFont( socketWriter , 0);    //��������
				socketWriter.println(" ");
			}
			setFont( socketWriter , 1);    //��������
			if(call_type.equals("0")||call_type.equals("1")){
				if(i == 0){
					socketWriter.print("  ��  ʽ");
				}else{
					socketWriter.print(" @ ����"+i);
				}
			}else{
			    socketWriter.print("       ");
			    //i = 9999; //  ����˻��ߴ߲�ʱ  ֻ��ӡһ�ų���  ���԰�i���һ���Ƚϴ����ֵ���´�ѭ���Ͳ���ִ�У� printCount(��ӡ����)Ҳ��ʧЧ��  
			}
			setFont( socketWriter , 18);    //��������
			socketWriter.println("       "+call_type_show);
			setFont( socketWriter , 0);    //��������
			socketWriter.println(" ");
			
			setFont( socketWriter , 17);    //��������
			socketWriter.println(" "+table_id+" ̨    [ "+nop+" �� ]");
			
			setFont( socketWriter , 0);    //��������
			socketWriter.println(" ");
			socketWriter.print(""+oper_time);
			socketWriter.println("     ���Ա:"+staff_name);
			socketWriter.println(" ");
			
			setFont( socketWriter , 17);    //��������
			socketWriter.println(food_name);
			
			setFont( socketWriter , 0);    //��������
			socketWriter.println(" ");
			
			setFont( socketWriter , 17);    //��������
			if(call_type.equals("4")){
				socketWriter.println("������� �� "+count+" "+unit);
				socketWriter.println("���˲��� �� "+back_count+" "+unit);
				double left_count = Double.parseDouble(count)-Double.parseDouble(back_count);
				socketWriter.println("ʣ������ �� "+ut.getDoubleString(left_count)+" "+unit);
			}else{
				if(Double.parseDouble(back_count)>0){
					socketWriter.println("������� �� "+count+" "+unit);
					socketWriter.println("�˲����� �� "+back_count+" "+unit);
					double left_count = Double.parseDouble(count)-Double.parseDouble(back_count);
					socketWriter.println("�������� �� "+ut.getDoubleString(left_count)+" "+unit);
				}else{
					socketWriter.println("���� ��    "+count+" "+unit);
				}
			}
			setFont( socketWriter , 0);    //��������
			socketWriter.println(" ");
			setFont( socketWriter , 1);    //��������
			if(!call_type.equals("4")){
				socketWriter.println("���� ��"+price+" Ԫ        �ϼ� ��"+ut.getDoubleString(ut.doubled(price)*ut.doubled(count))+" Ԫ");
			}
			if(note!=null && !note.trim().equals("") && !note.trim().equals("-")){
				setFont( socketWriter , 0);    //��������
				socketWriter.println(" ");
				setFont( socketWriter , 17);    //��������
				socketWriter.println("��ע��"+note.trim());
			}
			setFont( socketWriter , 1);    //��������
			socketWriter.println( "-----------------------------------------" );
			log.info(" call_type = "+call_type);
			// ֻ�м���ͽ���Ĳ˵���ӡ���룬   ��ˡ��߲˺��˲˵Ķ�����ӡ����,ֻ����
			log.info(" barcode = "+barcode);
			socketWriter.println("No. "+ barcode ); 
			// call_type --  0 �� ����    1 �� ����   2��  ���   3��  �߲�   4:   �˲�
			if(call_type.equals("0")||call_type.equals("1")){ 
				
				//����������뷽ʽ  
				socketWriter.write(0x1d); 
				socketWriter.write('k'); 
				socketWriter.write(70);  //����������뷽ʽΪ69  CODE39 
				socketWriter.write(12);   //��������λ��Ϊ6λ 
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
			// ��ӡ����Զ���ֽ
			pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			cutPaper(socketWriter);
			// ǿ�ƴ�ӡ������
			socketWriter.flush();
			log.info(" printFood , Food_name =  "+food_name+" ,�� "+i+" �δ�ӡ ���� �������� printCount = "+printCount);
		}
		client.close();
		log.info(" -----  ��ӡ����  end    ----  ");
		food.put("STATE", "2");
		food.put("PRINT_TIME", ut.currentTime());
		food.put("REMARK", "success");
		return food.getString("STATE");
	}

	public String printOneFood() {
		//1. ��ѯһ����ӡ��¼ 
        IData param = new IData();
        param.put("count", "1");
        List data = printDao.queryPrintFood(param);
        if(data.size()==0){
        	return "";
        }
        IData food = new IData((Map)data.get(0));
        food.put("REMARK", "");
        //2. ��ӡ��Ʒ
        try {
			String state = printFood(food);
			//3. ��¼��ӡ���
	        printDao.submitPrintFood(food);
		} catch (Exception e) {
			e.printStackTrace();
			// call_type --  0 �� ����    1 �� ����   2��  ���   3��  �߲�   4:   �˲�
			String call_type = food.getString("CALL_TYPE");
			if(call_type.equals("0")){
				food.put("REMARK", "IP:"+food.getString("PRINTER")+"��ӡ����!��ת��");
			}
			if(call_type.equals("1")){
				food.put("REMARK", "IP:"+food.getString("PRINTER")+"��ӡ����!��ת��");
			}
			if(call_type.equals("2")){
				food.put("REMARK", "IP:"+food.getString("PRINTER_START")+"��ӡ����!��ת��");
			}
			if(call_type.equals("3")){
				food.put("REMARK", "IP:"+food.getString("PRINTER_HURRY")+"��ӡ����!��ת��");
			}
			if(call_type.equals("4")){
				food.put("REMARK", "IP:"+food.getString("PRINTER_BACK")+"��ӡ����!��ת��");
			}
			food.put("REMARK", food.getString("REMARK"));
			food.put("PRINTER", food.getString("PRINTER_SEC"));
			food.put("PRINTER_BACK", food.getString("PRINTER_SEC"));
			food.put("PRINTER_START", food.getString("PRINTER_SEC"));
			food.put("PRINTER_HURRY", food.getString("PRINTER_SEC"));
			try {
				String state = printFood(food);
				//3. ��¼��ӡ���
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
	 *  ��ӡ������Ϣ
	 */
    private void printPaperFoot(PrintWriter pw,JSObject window){
		setFont( pw , 0);
    	pw.println(" ----------------------------------------��--");
    	setFont( pw , 1);
//		pw.println("          ������®���ʲ�����ӭ���Ĺ���");
    	pw.println("          "+window.getMember("rest_name").toString() +"��ӭ���Ĺ���");
		setFont( pw , 0);
		pw.println(" ");
		pw.println("  ��ַ :  "+window.getMember("address").toString());
		pw.println("  �绰 :  "+window.getMember("phone").toString());
		pw.println(" ----------------------------------------��--");
	}
    
    /**
	 *  ��ӡ��˾��Ϣ
	 */
    private void printCompanyInfo(PrintWriter pw){
    	setFont( pw , 0);
    	pw.println(" ----------------------------------------��--");
    	setFont( pw , 1);
    	pw.println("          "+"" +"��ӭʹ�÷�Ħ����������ϵͳ");
		setFont( pw , 0);
		pw.println(" ");
		pw.println("  ���綩�����¼��http://www.fanmole.com ");
		//pw.println("       �ͷ��绰 :  18622102888");
		pw.println(" ----------------------------------------��--");
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
    
    
    // call_type --  0 �� ����    1 �� ����   2��  ���   3��  �߲�   4:   �˲�
    public String getCallTypeShow(String type){
    	String str="";
    	if(type.equals("0")){
    		str="����";
    	}else if(type.equals("1")){
    		str="����";
    	}else if(type.equals("2")){
    		str="���";
    	}else if(type.equals("3")){
    		str="�߲�";
    	}else if(type.equals("4")){
    		str="�˲�";
    	}
    	return str;
	}

	public String deleteHistory() {
		printDao.deleteHistory();
		return null;
	}
    

}
