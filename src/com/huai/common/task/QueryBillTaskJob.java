package com.huai.common.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import com.huai.common.service.PrintBillService;
import com.huai.common.util.GetBean;
import com.huai.common.util.HttpPostUtils;
import com.huai.common.util.ut;

public class QueryBillTaskJob  implements Runnable {
    
	private static final Logger log = Logger.getLogger(QueryBillTaskJob.class);
	
    int count = 0;
	public void run() {
		log.info(" QueryBillTaskJob  任务进行中。。。  ");  
		try{
			ParamObject po = (ParamObject)GetBean.getBean("PO");
			PrintBillService printBillService = (PrintBillService)GetBean.getBean("printBillService");
			if(po.isRun()){
//				log.info("  查询  打印记录  count = "+count++);
				//1. 查询待打印记录
				Map param = new HashMap();
				param.put("appid", po.getAppid());
				log.info(" QueryBillTaskJob po.getQrybillinfo_url():"+po.getQrybill_url());
				String result = HttpPostUtils.doPost(po.getQrybill_url(),param);
				JSONObject obj = JSONObject.fromObject(result);
		        List bills = (List)obj.get("bills");
		        printBillService.saveBills(bills);
			}else{
//				log.info("  isRun = false ，  暂不执行任务  "+count++);
			}
		}catch(Exception e){
			//e.printStackTrace();
		}
	}
	
}
