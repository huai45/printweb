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
public class CommonAction {

	private static final Logger log = Logger.getLogger(CommonAction.class);
	
	@RequestMapping(value = "/index.html")   
    public ModelAndView testindexpage(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)  {
		log.info(" index.html  ");
		return new ModelAndView("/index", modelMap);
	}
	
	
	
	
}
