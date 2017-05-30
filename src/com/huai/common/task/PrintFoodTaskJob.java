package com.huai.common.task;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.huai.common.service.PrintFoodService;

@Component("printFoodTaskJob")
public class PrintFoodTaskJob {
    
	private static final Logger log = Logger.getLogger(PrintFoodTaskJob.class);
	
	private int food_count = 0;
	
	@Resource(name="printFoodService")
	public PrintFoodService printFoodService;
	
    @Scheduled(fixedDelay = 1000)
    public void printFood() {
        log.info(" ¥Ú”°  ≤À∆∑°£°£°£  food_count = "+food_count++);
        printFoodService.printOneFood();
    }
    
//    @Scheduled(fixedRate = 5000)  
//    void doSomethingWithRate(){  
//        log.info("I'm doing with rate now!");  
//    }  
      
//    @Scheduled(cron = "0/5 * * * * *")  
//    void doSomethingWith(){  
//        log.info("I'm doing with cron now!");  
//    }  
    
}
