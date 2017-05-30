package com.huai.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.huai.common.domain.IData;
import com.huai.common.task.ParamObject;
import com.huai.common.task.PrintBillTaskJob;
import com.huai.common.util.GetBean;
import com.huai.common.util.HttpPostUtils;
import com.huai.common.util.ut;

@Component("printDao")
public class PrintDaoImpl extends BaseDao implements PrintDao {

	private static final Logger log = Logger.getLogger(PrintDaoImpl.class);
	
	@Resource(name="PO")
	ParamObject po;
	
	public List queryPrintFood(IData param) {
		String date = ut.currentDate();
		List foods = jdbcTemplate.queryForList(" select * from tf_print_log where state in ('0','9') and oper_time like '"+date+"%' order by state asc,print_id asc limit 0 , ? " , 
	        new Object[]{ param.getString("count") });
		return foods;
	}

	public String submitPrintFood(IData food) {
		jdbcTemplate.update(" update tf_print_log set state = ?, print_time = ? ,remark = ? where print_id = ?  ",
	        new Object[]{ food.get("STATE"),food.get("PRINT_TIME"),food.get("REMARK"),food.get("PRINT_ID") });
		return null;
	}

	public List queryPrintBill(IData param) {
		List bills = jdbcTemplate.queryForList(" select * from tf_print_bill_log where state in ('0') order by state asc , print_id asc limit 0 , ? " , 
	        new Object[]{ param.getString("count") });
		return bills;
	}

	public String submitPrintBill(IData bill) {
		ut.p(" submitPrintBill  bill:"+bill);
		jdbcTemplate.update(" update tf_print_bill_log set state = ?, print_time = ? ,remark = ? where print_id = ?  ",
	        new Object[]{ bill.get("STATE"),bill.get("PRINT_TIME"),bill.get("REMARK"),bill.get("PRINT_ID") });
		return null;
	}

	public IData queryBillById(IData bill) {
		IData new_bill = null;
		if(ut.isEmpty(po.getQrybillinfo_url())){
			return new_bill;
		}
		Map param = new HashMap();
		param.put("appid", po.getAppid());
		param.put("bill_id", bill.getString("BILL_ID"));
		String result = HttpPostUtils.doPost(po.getQrybillinfo_url(),param);
		JSONObject obj = JSONObject.fromObject(result);
		if(obj.getString("success").equals("true")){
			new_bill = new IData((Map)obj.get("bill"));
		}
		return new_bill;
	}

	public String saveFood(IData food) {
		String print_sql = "insert into tf_print_log ( PRINT_ID,REST_ID,BARCODE,PRINTER,PRINT_COUNT,CALL_TYPE,STATE,PRINT_TIME, " +
			" TABLE_ID,TABLE_NAME,BILL_ID,NOP,ITEM_ID, " +
			" FOOD_ID,FOOD_NAME,PRICE,COUNT,BACK_COUNT,UNIT,NOTE,OPER_TIME,OPER_STAFF_ID,OPER_STAFF_NAME,PRINTER_START,PRINTER_HURRY,PRINTER_BACK,PRINTER_SEC,REMARK ) values " +
			" ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ) ";
		try{
    		jdbcTemplate.update(print_sql,new Object[]{
    			food.getString("PRINT_ID"),food.getString("REST_ID"),
    			food.getString("BARCODE"),food.getString("PRINTER"),
    			food.getString("PRINT_COUNT"),food.getString("CALL_TYPE"),
    			"0","",food.getString("TABLE_ID"),
    			food.getString("TABLE_ID"),food.getString("BILL_ID"),
    			food.getString("NOP"),food.getString("ITEM_ID"),
    			food.getString("FOOD_ID"),food.getString("FOOD_NAME"),
    			food.getString("PRICE"),food.getString("COUNT"),food.getString("BACK_COUNT"),
    			food.getString("UNIT"),food.getString("NOTE"),
    			food.getString("OPER_TIME"),food.getString("OPER_STAFF_ID"),
    			food.getString("OPER_STAFF_NAME"),
    			food.getString("PRINTER_START"),
    			food.getString("PRINTER_HURRY"),
    			food.getString("PRINTER_BACK"),
    			food.getString("PRINTER_SEC"),
    			food.getString("REMARK")});
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		ut.p("Ö÷¼ü³åÍ» ptint_id = "+food.getString("PRINT_ID"));
    		return "error";
		}
		return "success";
	}
	
	
	public String saveBill(IData bill) {
		 String print_sql = "insert into tf_print_bill_log ( PRINT_ID,REST_ID,STATE,PRINTER,BILL_ID,OPER_TIME,OPER_STAFF_ID,OPER_STAFF_NAME,PRINT_TIME,REMARK ) values " +
			" ( ?,?,?,?,?,?,?,?,?,? ) ";
		 try{
			 jdbcTemplate.update(print_sql,new Object[]{
					bill.getString("PRINT_ID"),bill.getString("REST_ID"),
					bill.getString("STATE"),bill.getString("PRINTER"),
					bill.getString("BILL_ID"),bill.getString("OPER_TIME"),
					bill.getString("OPER_STAFF_ID"),bill.getString("OPER_STAFF_NAME"),
					"","" });
		}catch(Exception e){
        	ut.p("Ö÷¼ü³åÍ» ptint_id = "+bill.getString("PRINT_ID"));
        	return "error";
    	}
		return "success";
	}

	public String deleteHistory() {
		String date = ut.currentDate(-2);
		List foods = jdbcTemplate.queryForList(" select print_id from tf_print_log where oper_time <= '"+date+" 00:00:00' " , 
		        new Object[]{ });
//		log.info(" deleteHistory , foods = "+foods.size());
		if(foods.size()>0){
			int m = jdbcTemplate.update(" delete from tf_print_log where oper_time <= '"+date+" 00:00:00'  ");
			log.info(" deleteHistory , delete foods = "+m);
			int n = jdbcTemplate.update(" delete from tf_print_bill_log where oper_time <= '"+date+" 00:00:00'  ");
			log.info(" deleteHistory , delete bills = "+n);
		}
		return null;
	}

}
