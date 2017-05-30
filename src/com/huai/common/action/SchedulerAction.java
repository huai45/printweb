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
import com.huai.common.task.QueryBillTaskJob;
import com.huai.common.task.QueryTaskJob;
import com.huai.common.util.GetBean;
import com.huai.common.util.ut;

@Controller
@RequestMapping("/task")
public class SchedulerAction {

	private static final Logger log = Logger.getLogger(SchedulerAction.class);
	
	@RequestMapping(value = "/start")
	@ResponseBody
    public Object start(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)  {
//		log.info(" SchedulerAction   start  appid = "+request.getParameter("appid"));
        ThreadPoolTaskScheduler pqScheduler = (ThreadPoolTaskScheduler)GetBean.getBean("pqScheduler");
        Map map = new HashMap();
        map.put("succes", false);
        ParamObject po = (ParamObject)GetBean.getBean("PO");
        po.setAppid(request.getParameter("appid"));
		po.setRun(true);
		po.setQryfood_url(request.getParameter("queryfoodurl"));
		po.setQrybill_url(request.getParameter("querybillurl"));
		po.setQrybillinfo_url(request.getParameter("querybillinfourl"));
		po.setBack_url(request.getParameter("backurl"));
		po.setTime_food(Long.parseLong(request.getParameter("ftime")));
		po.setTime_bill(Long.parseLong(request.getParameter("btime")));
        if(po.getQueryTaskJob()==null){
    		if(po.getTime_food()>0){
    			QueryTaskJob job = new QueryTaskJob();
	        	pqScheduler.scheduleWithFixedDelay(job, po.getTime_food());
	        	po.setQueryTaskJob(job);
	        	map.put("succes", true);
	        }
        }
        if(po.getQueryBillTaskJob()==null){
    		if(po.getTime_bill()>0){
    			QueryBillTaskJob queryBillTaskJob = new QueryBillTaskJob();
	        	pqScheduler.scheduleWithFixedDelay(queryBillTaskJob, po.getTime_bill());
	        	po.setQueryBillTaskJob(queryBillTaskJob);
	        	map.put("succes", true);
	        }
        }
        map.put("msg", pqScheduler.getThreadNamePrefix());
		return map;
	}
	
	@RequestMapping(value = "/setpo")
	@ResponseBody
    public Object setPO(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)  {
		log.info(" setpo ");
		ParamObject po = (ParamObject)GetBean.getBean("PO");
		po.setAppid(request.getParameter("appid"));
		po.setRun(true);
		po.setQryfood_url(request.getParameter("queryfoodurl"));
		po.setQrybill_url(request.getParameter("querybillurl"));
		po.setQrybillinfo_url(request.getParameter("qrybillinfourl"));
		po.setBack_url(request.getParameter("backurl"));
		po.setTime_food(Long.parseLong(request.getParameter("ftime")));
		po.setTime_bill(Long.parseLong(request.getParameter("btime")));
		Map map = new HashMap();
        map.put("succes", true);
		return map;
	}
	
	@RequestMapping(value = "/po")
	@ResponseBody
    public Object showPO(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)  {
		log.info(" po  ");
		ParamObject po = (ParamObject)GetBean.getBean("PO");
		po.toString();
		Map map = new HashMap();
        map.put("succes", true);
        map.put("po", po.toString());
		return map;
	}
	
	@RequestMapping(value = "/startQuery")
	@ResponseBody
    public Object startQuery(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)  {
		log.info(" startQuery ");
		ParamObject po = (ParamObject)GetBean.getBean("PO");
		po.setRun(true);
		Map map = new HashMap();
        map.put("succes", true);
		return map;
	}
	
	@RequestMapping(value = "/stopQuery")
	@ResponseBody
    public Object stopQuery(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)  {
		log.info(" stopQuery ");
		ParamObject po = (ParamObject)GetBean.getBean("PO");
		po.setRun(false);
		Map map = new HashMap();
        map.put("succes", true);
		return map;
	}
	
}
