package com.huai.common.action;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/operation")
public class CommonDataAction {

	private static final Logger log = Logger.getLogger(CommonDataAction.class);
	
	@RequestMapping(value = "/getInfo")
	@ResponseBody
    public Object getInfo(HttpServletRequest request, HttpServletResponse response,
    		ModelMap modelMap)  {
		log.info(" getInfo  ");
        Map map = new HashMap();
        map.put("succes", true);
        map.put("msg", "hello");
		return map;
	}
}
