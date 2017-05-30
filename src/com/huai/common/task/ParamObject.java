package com.huai.common.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component("PO")
public class ParamObject {
    
	private boolean run = false;
	private String qryfood_url="";
	private String qrybill_url="";
	private String qrybillinfo_url="";
	private String back_url="";
	private String appid="";
	private long time_food = 0;
	private long time_bill = 0;
	
	private List bills = new ArrayList();
	
	private QueryTaskJob queryTaskJob;
	private QueryBillTaskJob queryBillTaskJob;
	
	public String toString(){
		System.out.println(" runtag : "+run);
		System.out.println(" qryfood_url : "+qryfood_url);
		System.out.println(" qrybill_url : "+qrybill_url);
		System.out.println(" qrybillinfo_url : "+qrybillinfo_url);
		System.out.println(" back_url : "+back_url);
		System.out.println(" appid : "+appid);
		System.out.println(" time_food : "+time_food);
		System.out.println(" time_bill : "+time_bill);
		return "po";
	}


	public boolean isRun() {
		return run;
	}


	public void setRun(boolean run) {
		this.run = run;
	}



	public String getQryfood_url() {
		return qryfood_url;
	}


	public void setQryfood_url(String qryfood_url) {
		this.qryfood_url = qryfood_url;
	}


	public String getQrybill_url() {
		return qrybill_url;
	}


	public void setQrybill_url(String qrybill_url) {
		this.qrybill_url = qrybill_url;
	}


	public String getBack_url() {
		return back_url;
	}

	public void setBack_url(String back_url) {
		this.back_url = back_url;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}



	public long getTime_food() {
		return time_food;
	}


	public void setTime_food(long time_food) {
		this.time_food = time_food;
	}


	public long getTime_bill() {
		return time_bill;
	}


	public void setTime_bill(long time_bill) {
		this.time_bill = time_bill;
	}


	public QueryTaskJob getQueryTaskJob() {
		return queryTaskJob;
	}


	public void setQueryTaskJob(QueryTaskJob queryTaskJob) {
		this.queryTaskJob = queryTaskJob;
	}


	public String getQrybillinfo_url() {
		return qrybillinfo_url;
	}


	public void setQrybillinfo_url(String qrybillinfo_url) {
		this.qrybillinfo_url = qrybillinfo_url;
	}


	public QueryBillTaskJob getQueryBillTaskJob() {
		return queryBillTaskJob;
	}


	public void setQueryBillTaskJob(QueryBillTaskJob queryBillTaskJob) {
		this.queryBillTaskJob = queryBillTaskJob;
	}


	public List getBills() {
		return bills;
	}


	public void setBills(List bills) {
		this.bills = bills;
	}

	
}
