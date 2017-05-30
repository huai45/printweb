package com.huai.common.task;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.huai.common.service.PrintBillService;

@Component("printBillTaskJob")
public class PrintBillTaskJob {
    
	private static final Logger log = Logger.getLogger(PrintBillTaskJob.class);
	
	private int bill_count = 0;
	
	@Resource(name="printBillService")
	public PrintBillService printBillService;
	
    @Scheduled(fixedDelay = 1000)  
    public void printBill() {
//    	log.info(" ¥Ú”°  ’Àµ•°£°£°£  bill_count = "+bill_count++);
        printBillService.printOneBill();
    }
    
}





