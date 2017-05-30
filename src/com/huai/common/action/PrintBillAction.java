package com.huai.common.action;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.huai.common.task.ParamObject;
import com.huai.common.task.QueryTaskJob;
import com.huai.common.util.GetBean;
import com.huai.common.util.ut;

@Controller
@RequestMapping("/print")
public class PrintBillAction {

	private static final Logger log = Logger.getLogger(PrintBillAction.class);
	
	@RequestMapping(value = "/printBill")
    public ModelAndView printBill(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)  {
		String callback = request.getParameter("callback");
		String str = request.getParameter("str");
		log.info(" PrintBillAction   printBill   callback = "+callback);
		log.info(" str = "+str);
        log.info(" err = "+ut.err("联系电话为空!"));
        modelMap.put("info", ut.err("联系电话为空!"));
		return new ModelAndView("/jqueryajax", modelMap);
	}
	
}
