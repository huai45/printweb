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
@RequestMapping("/card")
public class VipCardAction {

	private static final Logger log = Logger.getLogger(VipCardAction.class);
	
	@RequestMapping(value = "/read")
	@ResponseBody
    public Object start(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)  {
		log.info(" VipCardAction   read   ");
        Map map = new HashMap();
        String card_no = "1";
        map.put("succes", true);
        map.put("card_no", card_no);
		return map;
	}
	
	@RequestMapping(value = "/write")
	@ResponseBody
    public Object write(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)  {
        String card_no = request.getParameter("card_no");	
        log.info(" VipCardAction   write  card_no : "+card_no);
        Map map = new HashMap();
        map.put("succes", true);
		return map;
	}
	
	
	
	
}
