package com.huai.common.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

import com.huai.common.action.SchedulerAction;
import com.huai.common.service.PrintFoodService;
import com.huai.common.util.GetBean;
import com.huai.common.util.HttpPostUtils;
import com.huai.common.util.ut;

public class QueryTaskJob  implements Runnable {
    
	private static final Logger log = Logger.getLogger(QueryTaskJob.class);
	
    int count = 0;
	public void run() {
		log.info(" QueryTaskJob  任务进行中。。。  ");  
		try{
			ParamObject po = (ParamObject)GetBean.getBean("PO");
			PrintFoodService printFoodService = (PrintFoodService)GetBean.getBean("printFoodService");
			if(po.isRun()){
				log.info("  查询  打印记录  count = "+count++);
				//1. 查询待打印记录
				Map param = new HashMap();
				param.put("appid", po.getAppid());
				String result = HttpPostUtils.doPost(po.getQryfood_url(),param);
				JSONObject obj = JSONObject.fromObject(result);
		        List foods = (List)obj.get("foods");
		        printFoodService.saveFoods(foods);
		        
		        printFoodService.deleteHistory();
		        
//		        jdbcTemplate.batchUpdate( print_sql , 
//	    			new BatchPreparedStatementSetter() {
//	    				public int getBatchSize() {
//	    				        return foods.size();
//	    				   }
//	    				public void setValues(PreparedStatement pstmt, int i)
//	    						throws SQLException {
//	    					IData item = new IData((Map)foods.get(i));
//	    					pstmt.setString(1, item.getString("PRINT_ID"));
//	    					pstmt.setString(2, item.getString("REST_ID"));
//	    				    pstmt.setString(3, item.getString("BARCODE"));
//	    				    pstmt.setString(4, item.getString("PRINTER"));
//	    				    pstmt.setString(5, item.getString("PRINT_COUNT"));
//	    				    pstmt.setString(6, item.getString("CALL_TYPE"));
//	    				    pstmt.setString(7, "0");
//	    				    pstmt.setString(8, item.getString("TABLE_ID"));
//	    				    pstmt.setString(9, item.getString("TABLE_ID"));
//	    				    pstmt.setString(10, item.getString("BILL_ID"));
//	    				    pstmt.setString(11, item.getString("NOP"));
//	    				    pstmt.setString(12, item.getString("ITEM_ID"));
//	    				    pstmt.setString(13, item.getString("FOOD_ID"));
//	    				    pstmt.setString(14, item.getString("FOOD_NAME"));
//	    				    pstmt.setString(15, item.getString("PRICE"));
//	    				    pstmt.setString(16, item.getString("COUNT"));
//	    				    pstmt.setString(17, item.getString("UNIT"));
//	    				    pstmt.setString(18, item.getString("NOTE"));
//	    				    pstmt.setString(19, item.getString("OPER_TIME"));
//	    				    pstmt.setString(20, item.getString("OPER_STAFF_ID"));
//	    				    pstmt.setString(21, item.getString("OPER_STAFF_NAME"));
//	    				}
//	    		});
			}else{
				log.info("  isRun = false ，  暂不执行任务  "+count++);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
    
}
