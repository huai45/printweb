package com.huai.common.service;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import com.huai.common.dao.BaseDao;
import com.huai.common.dao.PrintDao;
import com.huai.common.domain.IData;
import com.huai.common.task.ParamObject;
import com.huai.common.util.GetBean;
import com.huai.common.util.PrinterUtil;
import com.huai.common.util.ut;

@Component("printBillService")
public class PrintBillServiceImpl implements PrintBillService {

	private static final Logger log = Logger.getLogger(PrintBillServiceImpl.class);
	
	@Resource(name="baseDao")
	public BaseDao baseDao;
	
	@Resource(name="printDao")
	public PrintDao printDao;
    	
	public String printBill(IData bill) {
//		log.info(" ���ڴ�ӡ�˵� bill = "+bill.getString("BILL_ID"));
		log.info(" ���ڴ�ӡ�˵� bill = "+bill);
		if(bill.getString("PAY_TYPE").equals("1")){
			this.printFinishBill(bill);
		}else{
			this.printQueryBill(bill);
		}
		return bill.getString("STATE");
	}
	
	public String printOneBill() {
		//1. ��ѯһ����ӡ��¼
		IData param = new IData();
        param.put("count", "1");
        
        ParamObject po = (ParamObject)GetBean.getBean("PO");
        List bills = po.getBills();
//        log.info(" printOneBill  bills.size = "+bills.size());
        if(bills.size()==0){
        	return "";
        }
//        List data = printDao.queryPrintBill(param);
//        if(data.size()==0){
//        	return "";
//        }
//        IData bill = new IData((Map)data.get(0));
        IData bill = (IData)bills.get(0);
        String print_id = bill.getString("PRINT_ID");
        IData remote_bill = printDao.queryBillById(bill);
        if(remote_bill==null){
        	log.info("  remote_bill = null PRINT_ID = "+bill.getString("PRINT_ID"));
        	bill.put("STATE", "9");
    		bill.put("PRINT_TIME", ut.currentTime());
    		bill.put("REMARK", "error");
//            printDao.submitPrintBill(bill);
            bills.remove(bill);
    		return "";
        }
        //2. ��ӡ�˵�
        String state = printBill(remote_bill);
        //3. ��¼��ӡ���
//        bill.put("STATE", state);
//		bill.put("PRINT_TIME", ut.currentTime());
//		bill.put("REMARK", "success");
//        printDao.submitPrintBill(bill);
        bills.remove(bill);
		return "";
	}

	public String saveBills(List bills) {
		
		ParamObject po = (ParamObject)GetBean.getBean("PO");
		for(int i=0;i<bills.size();i++){
        	IData bill = new IData((Map)bills.get(i));
        	po.getBills().add(bill);
        	String reslut = printDao.saveBill(bill);
		}
		return null;
	}

	/**
	 *  ��ӡ��̨��ѯ��
	 * @param 
	 * @return 
	 *     add time ��2011-10-07
	 *  modify time ��2011-10-07
	 */
    public String printQueryBill(IData bill) {
    	log.info(" *********  printQueryBill   ��ʼ  *********   bill_Id = "+bill.getString("BILL_ID"));
		String printer = bill.getString("PRINTER");
		log.info(" printQueryBill  printer : "+printer);
		bill.put("STATE", "0");
		Socket client = new java.net.Socket();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			// �����˵����
			calculateBill(bill);
			List items = (List)bill.get("ITEMLIST");
			List recvDetail = (List)bill.get("FEELIST");
			List tuicai = new ArrayList();
			List miancai = new ArrayList();
			List zengsong = new ArrayList();
			//������߾�
			PrinterUtil.setLeftSpace( socketWriter , 0 );
			//�����м��
			PrinterUtil.setLineSpace( socketWriter, 10 );    
			PrinterUtil.setFont( socketWriter , 17);   //��������
			socketWriter.println(" *** ��ѯ�� ***");
			PrinterUtil.setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			PrinterUtil.setFont( socketWriter , 17);   //��������
			String nop = bill.getString("NOP");
			socketWriter.println(" "+bill.getString("TABLE_ID")+" ̨    [ "+(int)Double.parseDouble(nop)+" �� ]");
			//�����м��
			PrinterUtil.setLineSpace( socketWriter, 64 ); 
			PrinterUtil.setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			socketWriter.println("  ��̨ʱ��  �� "+bill.getString("OPEN_TIME"));
			socketWriter.println("  ��̨Ա��  �� "+bill.getString("OPEN_STAFF_NAME"));
			socketWriter.println("  �˵���ˮ  �� "+bill.getString("BILL_ID"));
			socketWriter.println("  ��ӡʱ��  �� "+ut.currentTime());
			socketWriter.println(" ----------------------------------------��--");
			socketWriter.println("    Ʒ��              ���� ��λ ���  ���Ա");
//			log.info(" FOOD_ITEM_STR = "+bill.getString("FOOD_ITEM_STR").toString());
			// һ������2���ո�   һ������ = 2�����ֻ�2���ո�   ÿһ��  1���ո�  18���ո񳤶ȵ�����
			for(int i=0;i<items.size();i++){
				Map item = (Map)items.get(i);
//				log.info(" item = "+item);
				try{
					String name = item.get("FOOD_NAME").toString();
					// state     0 �� δ��   1 �� �Ѵ򵥵�δ�ϲ�   2 �� ���ϲ�
					String state = item.get("STATE").toString();
					if(state.equals("0")){   // δ���
						item.put("FOOD_NAME", "#"+name);
					}
					if(state.equals("2")){  // ���ϲ�
						item.put("FOOD_NAME", "*"+name);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				if(Double.parseDouble(item.get("BACK_COUNT").toString())>0){
					tuicai.add(item);
				}
				if(Double.parseDouble(item.get("FREE_COUNT").toString())>0){
					zengsong.add(item);
				}
				int width = 0;
				String str= "";
				if(Double.parseDouble(item.get("PAY_RATE").toString())<100){
					width = 3;
					str= "("+item.get("PAY_RATE")+"%) ";
				}
	//            log.info(" ************      str="+str);
				item.put("FOOD_NAME",str+item.get("FOOD_NAME"));
				int food_name_length = item.get("FOOD_NAME").toString().length();
				socketWriter.print(" ");
				if(food_name_length>(9+width)){
					socketWriter.println(item.get("FOOD_NAME").toString().trim());
					socketWriter.print("                   ");
					socketWriter.print("   ");
					String count = item.get("COUNT").toString();
					socketWriter.print(count);
					for(int j=count.length();j<4;j++){
						socketWriter.print(" ");
					}
					if(item.get("UNIT").toString().length()==1){
						socketWriter.print(" "+item.get("UNIT")+" ");
					}else{
						socketWriter.print(item.get("UNIT"));
					}
					double total = ut.doubled(item.get("COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
					socketWriter.print("  "+ut.getDoubleString(total/100));
					socketWriter.println("  "+item.get("OPER_STAFF_NAME"));
				}else{
					socketWriter.print(item.get("FOOD_NAME"));
					for(int j=0;j<(9+width-item.get("FOOD_NAME").toString().length());j++){
						socketWriter.print("  ");
					}
					socketWriter.print("   ");
					String count = item.get("COUNT").toString();
					socketWriter.print(count.toString());
					for(int j=count.length();j<4;j++){
						socketWriter.print(" ");
					}
					if(item.get("UNIT").toString().length()==1){
						socketWriter.print(" "+item.get("UNIT").toString()+" ");
					}else{
						socketWriter.print(item.get("UNIT").toString());
					}
					double total = ut.doubled(item.get("COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
					socketWriter.print("  "+ut.getDoubleString(total/100));
					socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
				}
				if(!ut.isEmpty((String)item.get("NOTE"))){
					socketWriter.println("   ������:"+item.get("NOTE").toString().trim());
				}
			}
			socketWriter.println(" ---------------------------------------��---");
			log.info(" tuicai = "+tuicai.size());
			if(tuicai.size()>0){
				socketWriter.println("   ���˲�Ʒ          ���� ��λ ���  ���Ա");
				for(int i=0;i<tuicai.size();i++){
					Map item = (Map)tuicai.get(i);
					int width = 0;
					if(Double.parseDouble(item.get("PAY_RATE").toString())<100){
						width = 3;
					}
					int food_name_length = item.get("FOOD_NAME").toString().length();
					socketWriter.print(" ");
					if(food_name_length>(9+width)){
						socketWriter.println(item.get("FOOD_NAME").toString().trim());
						socketWriter.print("                   ");
						socketWriter.print("   ");
						String back_count = item.get("BACK_COUNT").toString();
						socketWriter.print(back_count);
						for(int j=back_count.length();j<4;j++){
							socketWriter.print(" ");
						}
						if(item.get("UNIT").toString().length()==1){
							socketWriter.print(" "+item.get("UNIT").toString()+" ");
						}else{
							socketWriter.print(item.get("UNIT").toString());
						}
						double total = ut.doubled(item.get("BACK_COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
						socketWriter.print("  "+ut.getDoubleString(total/100));
						socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
					}else{
						socketWriter.print(item.get("FOOD_NAME").toString());
						for(int j=0;j<(9+width-item.get("FOOD_NAME").toString().length());j++){
							socketWriter.print("  ");
						}
						socketWriter.print("   ");
						String count = item.get("BACK_COUNT").toString();
						socketWriter.print(count);
						for(int j=count.length();j<4;j++){
							socketWriter.print(" ");
						}
						if(item.get("UNIT").toString().length()==1){
							socketWriter.print(" "+item.get("UNIT").toString()+" ");
						}else{
							socketWriter.print(item.get("UNIT").toString());
						}
						double total = ut.doubled(item.get("BACK_COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
						socketWriter.print("  "+ut.getDoubleString(total/100));
						socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
					}
					if(!ut.isEmpty((String)item.get("NOTE"))){
						socketWriter.println("   ������:"+item.get("NOTE").toString().trim());
					}
				}
				socketWriter.println(" ---------------------------------------��---");
			}
			log.info(" zengsong = "+zengsong.size());
			if(zengsong.size()>0){
				socketWriter.println("   ���Ͳ�Ʒ          ���� ��λ ���  ���Ա");
				for(int i=0;i<zengsong.size();i++){
					Map item = (Map)zengsong.get(i);
					int width = 0;
					if(Double.parseDouble(item.get("PAY_RATE").toString())<100){
						width = 3;
					}
					int food_name_length = item.get("FOOD_NAME").toString().length();
					socketWriter.print(" ");
					if(food_name_length>(9+width)){
						socketWriter.println(item.get("FOOD_NAME").toString().trim());
						socketWriter.print("                   ");
						socketWriter.print("   ");
						String back_count = item.get("FREE_COUNT").toString();
						socketWriter.print(back_count);
						for(int j=back_count.length();j<4;j++){
							socketWriter.print(" ");
						}
						if(item.get("UNIT").toString().length()==1){
							socketWriter.print(" "+item.get("UNIT").toString()+" ");
						}else{
							socketWriter.print(item.get("UNIT").toString());
						}
						double total = ut.doubled(item.get("FREE_COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
						socketWriter.print("  "+ut.getDoubleString(total/100));
						socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
					}else{
						socketWriter.print(item.get("FOOD_NAME").toString());
						for(int j=0;j<(9+width-item.get("FOOD_NAME").toString().length());j++){
							socketWriter.print("  ");
						}
						socketWriter.print("   ");
						String count = item.get("FREE_COUNT").toString();
						socketWriter.print(count);
						for(int j=count.length();j<4;j++){
							socketWriter.print(" ");
						}
						if(item.get("UNIT").toString().length()==1){
							socketWriter.print(" "+item.get("UNIT").toString()+" ");
						}else{
							socketWriter.print(item.get("UNIT").toString());
						}
						double total = ut.doubled(item.get("FREE_COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
						socketWriter.print("  "+ut.getDoubleString(total/100));
						socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
					}
					if(!ut.isEmpty((String)item.get("NOTE"))){
						socketWriter.println("   ������:"+item.get("NOTE").toString().trim());
					}
				}
				socketWriter.println(" ---------------------------------------��---");
			}
//			if(ut.doubled(bill.getString("DISCOUNT_FEE").toString())>0){
//				socketWriter.println("  ���۽�� :  "+ut.getDoubleString(ut.doubled(bill.getString("DISCOUNT_FEE").toString()))+" Ԫ");
//				socketWriter.println(" ----------------------------------------��--");
//			}
			socketWriter.println("  �˵��ܽ��   :    "+bill.getString("BILL_FEE").toString()+" Ԫ");
			socketWriter.println(" ----------------------------------------��--");
			if(ut.doubled(bill.getString("REDUCE_FEE").toString())>0){
				socketWriter.println("  Ĩȥ��� :  -"+ut.getDoubleString(ut.doubled(bill.getString("REDUCE_FEE").toString()))+" Ԫ");
				socketWriter.println(" ----------------------------------------��--");
			}
			if(recvDetail.size()>0){
				for(int i=0;i<recvDetail.size();i++){
					Map map = (Map)recvDetail.get(i);
					socketWriter.println("   "+map.get("MODE_NAME")+"  :  "+map.get("FEE").toString()+" Ԫ");
				}
				socketWriter.println(" ----------------------------------------��--");
			}
			socketWriter.println(" �� �� �� "+bill.getString("RECV_FEE").toString()+" Ԫ");
			socketWriter.println(" ----------------------------------------��--");
			PrinterUtil.setFont(socketWriter,17);    // ��������
			socketWriter.println(" Ӧ �� �� "+bill.getString("SPAY_FEE").toString()+" Ԫ");
			
			// ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ----------------------------------------��--");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+bill.get("RESTNAME").toString() +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+bill.get("ADDRESS").toString());
			socketWriter.println("  �绰 :  "+bill.get("TELEPHONE").toString());
			socketWriter.println(" ----------------------------------------��--");
			// ��ӡ����Զ���ֽ
			PrinterUtil.pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			PrinterUtil.cutPaper(socketWriter);
			socketWriter.flush();
			client.close();
			log.info("  **************   ��ӡ��ѯ�����  ************** bill_id = "+bill.getString("BILL_ID"));
			bill.put("STATE", "2");
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
    	return bill.getString("STATE");
	}
    
    
    /**
	 *  ��ӡ��̨���˵�
	 * @param 
	 * @return 
	 *     add time ��2011-10-07
	 *  modify time ��2011-10-07
	 */
    public String printFinishBill(IData bill) {
    	log.info(" *********  printQueryBill   ��ʼ  *********    ");
		String printer = bill.getString("PRINTER");
		log.info(" printFinishBill  printer : "+printer);
		bill.put("STATE", "0");
		Socket client = new java.net.Socket();
		try {
			client.connect(new InetSocketAddress( printer , 9100),10*1000);
			PrintWriter socketWriter = new PrintWriter(client.getOutputStream());
			List items = (List)bill.get("ITEMLIST");
			List recvDetail = (List)bill.get("FEELIST");
			List tuicai = new ArrayList();
			List miancai = new ArrayList();
			List zengsong = new ArrayList();
			//������߾�
			PrinterUtil.setLeftSpace( socketWriter , 0 );
			//�����м��
			PrinterUtil.setLineSpace( socketWriter, 10 );    
			PrinterUtil.setFont( socketWriter , 17);   //��������
			socketWriter.println(" *** ���˵� ***");
			PrinterUtil.setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			PrinterUtil.setFont( socketWriter , 17);   //��������
			String nop = bill.getString("NOP");
			socketWriter.println(" "+bill.getString("TABLE_ID")+" ̨    [ "+(int)Double.parseDouble(nop)+" �� ]");
			//�����м��
			PrinterUtil.setLineSpace( socketWriter, 64 ); 
			PrinterUtil.setFont( socketWriter , 0);   //��������
			socketWriter.println(" ");
			socketWriter.println("  ��̨ʱ��  �� "+bill.getString("OPEN_TIME"));
			socketWriter.println("  ��̨Ա��  �� "+bill.getString("OPEN_STAFF_NAME"));
			socketWriter.println("  �˵���ˮ  �� "+bill.getString("BILL_ID"));
			socketWriter.println("  ��ӡʱ��  �� "+ut.currentTime());
			socketWriter.println(" ----------------------------------------��--");
			socketWriter.println("    Ʒ��              ���� ��λ ���  ���Ա");
			// һ������2���ո�   һ������ = 2�����ֻ�2���ո�   ÿһ��  1���ո�  18���ո񳤶ȵ�����
			for(int i=0;i<items.size();i++){
				Map item = (Map)items.get(i);
				try{
					String name = item.get("FOOD_NAME").toString();
					String state = item.get("STATE").toString();
					if(state.equals("0")){   // δ���
						item.put("FOOD_NAME", "#"+name);
					}
					if(state.equals("2")){  // ���ϲ�
						item.put("FOOD_NAME", "*"+name);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				if(Double.parseDouble(item.get("BACK_COUNT").toString())>0){
					tuicai.add(item);
				}
				if(Double.parseDouble(item.get("FREE_COUNT").toString())>0){
					zengsong.add(item);
				}
				int width = 0;
				String str= "";
				if(Double.parseDouble(item.get("PAY_RATE").toString())<100){
					width = 3;
					str= "("+item.get("PAY_RATE")+"%) ";
				}
				item.put("FOOD_NAME",str+item.get("FOOD_NAME"));
				int food_name_length = item.get("FOOD_NAME").toString().length();
				socketWriter.print(" ");
				if(food_name_length>(9+width)){
					socketWriter.println(item.get("FOOD_NAME").toString().trim());
					socketWriter.print("                   ");
					socketWriter.print("   ");
					String count = item.get("COUNT").toString();
					socketWriter.print(count);
					for(int j=count.length();j<4;j++){
						socketWriter.print(" ");
					}
					if(item.get("UNIT").toString().length()==1){
						socketWriter.print(" "+item.get("UNIT")+" ");
					}else{
						socketWriter.print(item.get("UNIT"));
					}
					double total = ut.doubled(item.get("COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
					socketWriter.print("  "+ut.getDoubleString(total/100));
					socketWriter.println("  "+item.get("OPER_STAFF_NAME"));
				}else{
					socketWriter.print(item.get("FOOD_NAME"));
					for(int j=0;j<(9+width-item.get("FOOD_NAME").toString().length());j++){
						socketWriter.print("  ");
					}
					socketWriter.print("   ");
					String count = item.get("COUNT").toString();
					socketWriter.print(count.toString());
					for(int j=count.length();j<4;j++){
						socketWriter.print(" ");
					}
					if(item.get("UNIT").toString().length()==1){
						socketWriter.print(" "+item.get("UNIT").toString()+" ");
					}else{
						socketWriter.print(item.get("UNIT").toString());
					}
					double total = ut.doubled(item.get("COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
					socketWriter.print("  "+ut.getDoubleString(total/100));
					socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
				}
				if(!ut.isEmpty((String)item.get("NOTE"))){
					socketWriter.println("   ������:"+item.get("NOTE").toString().trim());
				}
			}
			socketWriter.println(" ---------------------------------------��---");
			if(tuicai.size()>0){
				socketWriter.println("   ���˲�Ʒ          ���� ��λ ���  ���Ա");
				for(int i=0;i<tuicai.size();i++){
					Map item = (Map)tuicai.get(i);
					int width = 0;
					if(Double.parseDouble(item.get("PAY_RATE").toString())<100){
						width = 3;
					}
					int food_name_length = item.get("FOOD_NAME").toString().length();
					socketWriter.print(" ");
					if(food_name_length>(9+width)){
						socketWriter.println(item.get("FOOD_NAME").toString().trim());
						socketWriter.print("                   ");
						socketWriter.print("   ");
						String back_count = item.get("BACK_COUNT").toString();
						socketWriter.print(back_count);
						for(int j=back_count.length();j<4;j++){
							socketWriter.print(" ");
						}
						if(item.get("UNIT").toString().length()==1){
							socketWriter.print(" "+item.get("UNIT").toString()+" ");
						}else{
							socketWriter.print(item.get("UNIT").toString());
						}
						double total = ut.doubled(item.get("BACK_COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
						socketWriter.print("  "+ut.getDoubleString(total/100));
						socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
					}else{
						socketWriter.print(item.get("FOOD_NAME").toString());
						for(int j=0;j<(9+width-item.get("FOOD_NAME").toString().length());j++){
							socketWriter.print("  ");
						}
						socketWriter.print("   ");
						String count = item.get("BACK_COUNT").toString();
						socketWriter.print(count);
						for(int j=count.length();j<4;j++){
							socketWriter.print(" ");
						}
						if(item.get("UNIT").toString().length()==1){
							socketWriter.print(" "+item.get("UNIT").toString()+" ");
						}else{
							socketWriter.print(item.get("UNIT").toString());
						}
						double total = ut.doubled(item.get("BACK_COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
						socketWriter.print("  "+ut.getDoubleString(total/100));
						socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
					}
					if(!ut.isEmpty((String)item.get("NOTE"))){
						socketWriter.println("   ������:"+item.get("NOTE").toString().trim());
					}
				}
				socketWriter.println(" ---------------------------------------��---");
			}
			if(zengsong.size()>0){
				socketWriter.println("   ���Ͳ�Ʒ          ���� ��λ ���  ���Ա");
				for(int i=0;i<zengsong.size();i++){
					Map item = (Map)zengsong.get(i);
					int width = 0;
					if(Double.parseDouble(item.get("PAY_RATE").toString())<100){
						width = 3;
					}
					int food_name_length = item.get("FOOD_NAME").toString().length();
					socketWriter.print(" ");
					if(food_name_length>(9+width)){
						socketWriter.println(item.get("FOOD_NAME").toString().trim());
						socketWriter.print("                   ");
						socketWriter.print("   ");
						String back_count = item.get("FREE_COUNT").toString();
						socketWriter.print(back_count);
						for(int j=back_count.length();j<4;j++){
							socketWriter.print(" ");
						}
						if(item.get("UNIT").toString().length()==1){
							socketWriter.print(" "+item.get("UNIT").toString()+" ");
						}else{
							socketWriter.print(item.get("UNIT").toString());
						}
						double total = ut.doubled(item.get("FREE_COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
						socketWriter.print("  "+ut.getDoubleString(total/100));
						socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
					}else{
						socketWriter.print(item.get("FOOD_NAME").toString());
						for(int j=0;j<(9+width-item.get("FOOD_NAME").toString().length());j++){
							socketWriter.print("  ");
						}
						socketWriter.print("   ");
						String count = item.get("BACK_COUNT").toString();
						socketWriter.print(count);
						for(int j=count.length();j<4;j++){
							socketWriter.print(" ");
						}
						if(item.get("UNIT").toString().length()==1){
							socketWriter.print(" "+item.get("UNIT").toString()+" ");
						}else{
							socketWriter.print(item.get("UNIT").toString());
						}
						double total = ut.doubled(item.get("FREE_COUNT").toString())*ut.doubled(item.get("PRICE").toString())*ut.doubled(item.get("PAY_RATE").toString());
						socketWriter.print("  "+ut.getDoubleString(total/100));
						socketWriter.println("  "+item.get("OPER_STAFF_NAME").toString().trim());
					}
					if(!ut.isEmpty((String)item.get("NOTE"))){
						socketWriter.println("   ������:"+item.get("NOTE").toString().trim());
					}
				}
				socketWriter.println(" ---------------------------------------��---");
			}
//			if(ut.doubled(PrintBill.getMember("DISCOUNT_FEE").toString())>0){
//				socketWriter.println("  ���۽�� :  "+ut.getDoubleString(ut.doubled(PrintBill.getMember("DISCOUNT_FEE").toString()))+" Ԫ");
//				socketWriter.println(" ----------------------------------------��--");
//			}
			socketWriter.println("  �˵��ܽ��   :    "+bill.getString("BILL_FEE")+" Ԫ");
			socketWriter.println(" ----------------------------------------��--");
			if(ut.doubled(bill.getString("REDUCE_FEE"))>0){
				socketWriter.println("  Ĩȥ��� :  -"+ut.getDoubleString(ut.doubled(bill.getString("REDUCE_FEE")))+" Ԫ");
				socketWriter.println(" ----------------------------------------��--");
			}
			if(recvDetail.size()>0){
				for(int i=0;i<recvDetail.size();i++){
					Map map = (Map)recvDetail.get(i);
					socketWriter.println("   "+map.get("MODE_NAME")+"  :  "+map.get("FEE").toString()+" Ԫ");
				}
				socketWriter.println(" ----------------------------------------��--");
			}
			PrinterUtil.setFont(socketWriter,17);    // ��������
			socketWriter.println(" �� �� �� "+bill.getString("RECV_FEE")+" Ԫ");
			
			// ��ӡ������Ϣ
			PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ----------------------------------------��--");
	    	PrinterUtil.setFont( socketWriter , 1);
	    	socketWriter.println("          "+bill.get("RESTNAME").toString() +"��ӭ���Ĺ���");
	    	PrinterUtil.setFont( socketWriter , 0);
			socketWriter.println(" ");
			socketWriter.println("  ��ַ :  "+bill.get("ADDRESS").toString());
			socketWriter.println("  �绰 :  "+bill.get("TELEPHONE").toString());
			socketWriter.println(" ----------------------------------------��--");
			// ��ӡ����Զ���ֽ
			PrinterUtil.pushPaper(socketWriter);
			// ��ӡ����Զ���ֽ
			PrinterUtil.cutPaper(socketWriter);
			socketWriter.flush();
			client.close();
			log.info("  **************   ��ӡ���  ************** bill = "+bill.getString("BILL_ID"));
			bill.put("STATE", "2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bill.getString("STATE");
	}
    
    public IData calculateBill(IData bill){
    	
    	log.info(" calculateBill  start : ");
    	
    	List packages = (List)bill.get("PACKAGELIST");
		List items = (List)bill.get("ITEMLIST");
		List fees = (List)bill.get("FEELIST");
		List blank_list = new ArrayList();
		if(items==null){
			items = blank_list;
		}
		if(fees==null){
			fees = blank_list;
		}
		if(packages==null){
			packages = blank_list;
		}
		double bill_fee = 0;
		double derate_fee = 0;
		int recv_fee = 0;
		int reduce_fee =  Integer.parseInt(bill.getString("REDUCE_FEE"));
		for(int i=0;i<items.size();i++){
			Map item = (Map)items.get(i);
			if(item.get("PACKAGE_ID")!=null&&!item.get("PACKAGE_ID").toString().equals("")){
				continue;
			}
			int price = Integer.parseInt(item.get("PRICE").toString());
			double count = ut.doubled(item.get("COUNT").toString());
			double back_count = ut.doubled(item.get("BACK_COUNT").toString());
			double free_count = ut.doubled(item.get("FREE_COUNT").toString());
			int payrate = Integer.parseInt(item.get("PAY_RATE").toString());
			double fee = price*(count-back_count-free_count);
			bill_fee = bill_fee + fee;
			derate_fee = derate_fee + fee*(100-payrate)/100;
		}
        for(int i=0;i<fees.size();i++){
        	Map fee = (Map)fees.get(i);
        	int payfee = Integer.parseInt(fee.get("FEE").toString());
        	recv_fee = recv_fee + payfee;
		}
        int spay_fee = (int)(bill_fee- (recv_fee+reduce_fee+derate_fee));
        bill.put("BILL_FEE", ut.getDoubleString(bill_fee));
        bill.put("DERATE_FEE", ut.getDoubleString(derate_fee));
        bill.put("SPAY_FEE", ""+spay_fee);
        bill.put("RECV_FEE", ""+recv_fee);
        if( spay_fee == 0 ){
        	bill.put("CLOSE_FLAG", "1");
        	bill.put("REMARK", "�˵���Ƿ�ѣ����Էⵥ");
        }else{
        	bill.put("CLOSE_FLAG", "0");
        	bill.put("REMARK", "�˵���Ƿ��,���ܷⵥ");
        }
        
        log.info(" calculateBill  end : ");
        
		return bill;
    };
    
    
}
