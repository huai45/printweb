package com.huai.common.util;

import java.util.List;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import javax.sql.DataSource;

public class ut {

	public static DateFormat df_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static DateFormat df_day = new SimpleDateFormat("yyyy-MM-dd");
	public static DecimalFormat df = new DecimalFormat("0.00"); 
	public static Pattern int_pattern = Pattern.compile("[0-9]*");
	
	public static void p(Object o){
		System.out.println(o);
	};
	
	public static void log(Object o){
		System.out.println(o);
	};
	
	public static String getDoubleString(double d){
		return df.format(d); 
	};
	
	public static double doubled(String d){
		return Double.parseDouble(d); 
	};
	
	public static double formatDouble(double d){
		String temp = getDoubleString(d);
		return Double.parseDouble(temp); 
	};
	
	public static int passMin(String time){
		int min = 0;
		try {
			Date date = df_time.parse(time);
			long diff = new Date().getTime() - date.getTime() ;
		    long m = diff / (1000 * 60);
			min = (int)m;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return min;
		}//转换成功的Date对象
		return min;
	};
	
	public static int passMin(String time1,String time2){
		int min = 0;
		try {
			Date date1 = df_time.parse(time1);
			Date date2 = df_time.parse(time2);
			long diff = date2.getTime() - date1.getTime() ;
		    long m = diff / (1000 * 60);
			min = (int)m;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return min;
		}//转换成功的Date对象
		return min;
	};
	
	/**
	 *  获取当前系统时间
	 *  字符串的格式�? �? yyyy-MM-dd HH:mm:ss
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String currentTime(){
		return df_time.format(new Date());
	};
	
	/**
	 *  获取当前系统日期
	 *  字符串的格式�? �? yyyy-MM-dd
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String currentDate(){
		return df_day.format(new Date());
	};
	
	/**
	 *  获取当前系统日期
	 *  字符串的格式�? �? yyyy-MM-dd
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String currentYear(){
		return df_day.format(new Date()).substring(0,4);
	};
	
	/**
	 *  获取与当前日期相差i天的日期
	 * @param   i   相差天数
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String currentYear(int i){
		Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
		//设置代表的日期为1�?
        c.set(Calendar.YEAR,year+i);
		return df_day.format( c.getTime() ).substring(0, 4);
	};
	
	/**
	 *  获取当前系统日期
	 *  字符串的格式�? �? yyyy-MM-dd
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String currentMonth(){
		return df_day.format(new Date()).substring(5,7);//2011-12-07
	};
	
	public static String currentMonth(int i){
		Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
		//设置代表的日期为1�?
        c.set(Calendar.MONTH,month+i);
		return df_day.format( c.getTime() ).substring(5, 7);
	};
	
	/**
	 *  获取当前系统日期
	 *  字符串的格式�? �? yyyy-MM-dd
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String currentDay(){
		return currentDate().substring(8,10);
	};
	
	public static String currentDay(int i){
		return currentDate(i).substring(8,10);
	};
	
	/**
	 *  获取指定返回格式的当前系统日�?
	 * @param   str   日期格式字符�?
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String currentDate(String str){
		DateFormat _df = new SimpleDateFormat( str );
		return _df.format(new Date());
	};
	
	/**
	 *  获取与当前日期相差i天的日期
	 * @param   i   相差天数
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String currentDate(int i){
		Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
		//设置代表的日期为1�?
        c.set(Calendar.DATE,day+i);
		return df_day.format( c.getTime() );
	};
	
	/**
	 *  获取与当前日期相差i天的日期
	 * @param   i   相差天数
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String currentDate(String date , int i){
		Date day;
		Calendar c = Calendar.getInstance();
		try {
			day = df_day.parse(date);
			c.setTime(day);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int n = c.get(Calendar.DATE);
		//设置代表的日期为1�?
        c.set(Calendar.DATE,n+i);
		return df_day.format( c.getTime() );
	};
	
	/**
	 *  获取与当前日期相差i天的日期
	 * @param   i   相差天数
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String getTimeAfterMimute(String time , int i){
		Date day;
		Calendar c = Calendar.getInstance();
		try {
			day = df_time.parse(time);
			c.setTime(day);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int n = c.get(Calendar.MINUTE);
        c.set(Calendar.MINUTE, n+i);
		return df_time.format( c.getTime() );
	};
	
	/**
	 *  获取本月的第�?�?
	 * @param
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String firstDayOfMonth(){
		Calendar c = Calendar.getInstance();
        //设置代表的日期为1�?
        c.set(Calendar.DATE,1);
		return df_day.format(c.getTime());
	};
	
	/**
	 *  获取本月的最后一�?
	 * @param
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static int daysOfMonth(String day){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			Date date = sdf.parse(day);
			c.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		//获得当前月的�?大日期数
		int maxDay = c.getActualMaximum(Calendar.DATE); 
		return maxDay;
	};
	
	/**
	 *  获取本月的最后一�?
	 * @param
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static int daysOfWeek(String day){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			Date date = sdf.parse(day);
			c.setTime(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		//获得当前月的�?大日期数
		int dayofweek = c.get(Calendar.DAY_OF_WEEK)-1; 
		if(dayofweek==0){
			dayofweek = 7;
		};
		return dayofweek;
	};
	
	
	/**
	 *  获取本月的最后一�?
	 * @param
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String lastDayOfMonth(){
		Calendar c = Calendar.getInstance();
		//获得当前月的�?大日期数
		int maxDay = c.getActualMaximum(Calendar.DATE); 
        c.set(Calendar.DATE,maxDay);
		return df_day.format(c.getTime());
	};
	
	/**
	 *  获取�?�?7年的字符串数�?
	 * @param
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String[] lastSevenYears(){
		String[] years = new String[7];
		Calendar c = Calendar.getInstance();
		//获得当前月的�?大日期数
		int curr_year = c.get(Calendar.YEAR); 
		years[0] = String.valueOf(curr_year-3);
		years[1] = String.valueOf(curr_year-2);
		years[2] = String.valueOf(curr_year-1);
		years[3] = String.valueOf(curr_year);
		years[4] = String.valueOf(curr_year+1);
		years[5] = String.valueOf(curr_year+2);
		years[6] = String.valueOf(curr_year+3);
		return years;
	};
	
	
	/**
	 *  获取当前系统日期
	 *  字符串的格式�? �? yyyy-MM-dd
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String DateToBan(String date){
		String ban = null;
		try{
			ban = date.substring(0,4)+date.substring(5,7)+date.substring(8,10);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ban;
	};
	
	/**
	 *  判断字符串是否为�?
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static boolean isEmpty(String image) {
		// TODO Auto-generated method stub
		if(image==null||"".equals(image.trim())||"-".equals(image.trim())){
			return true;
		}
		return false;
	}
	
	/**
	 *  返回前台的成功信息的json字符�?
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String suc(String str) {
		// TODO Auto-generated method stub
		String s = "";
		if(str!=null){
			s = str;
		}
		return "{\"success\":\"true\",\"msg\":\""+s+"\"}";
	}
	
	/**
	 *  返回前台的成功信息的json字符�?
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String suc(String str,String key,String value) {
		// TODO Auto-generated method stub
		String s = "";
		if(str!=null){
			s = str;
		}
		return "{\"success\":\"true\",\"msg\":\""+s+"\",\""+key+"\":\""+value+"\"}";
	}
	
	/**
	 *  返回前台的成功信息的json字符�?
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String suc(String str,String k1,String v1,String k2,String v2) {
		// TODO Auto-generated method stub
		String s = "";
		if(str!=null){
			s = str;
		}
		return "{\"success\":\"true\",\"msg\":\""+s+"\",\""+k1+"\":\""+v1+"\",\""+k2+"\":\""+v2+"\"}";
	}
	
	/**
	 *  返回前台的成功信息的json字符�?
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String suc(String str,String k1,String v1,String k2,String v2,String k3,String v3,String k4,String v4) {
		// TODO Auto-generated method stub
		String s = "";
		if(str!=null){
			s = str;
		}
		return "{\"success\":\"true\",\"msg\":\""+s+"\",\""+k1+"\":\""+v1+"\",\""+k2+"\":\""+v2+"\",\""+k3+"\":\""+v3+"\",\""+k4+"\":\""+v4+"\"}";
	}
	
	/**
	 *  返回前台的成功信息的json字符�?
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String suc(String str,String k1,String v1,String k2,String v2,String k3,String v3,String k4,String v4,String k5,String v5) {
		// TODO Auto-generated method stub
		String s = "";
		if(str!=null){
			s = str;
		}
		return "{\"success\":\"true\",\"msg\":\""+s+"\",\""+k1+"\":\""+v1+"\",\""+k2+"\":\""+v2+"\",\""+k3+"\":\""+v3+"\",\""+k4+"\":\""+v4+"\",\""+k5+"\":\""+v5+"\"}";
	}
	
	/**
	 *  返回前台的失败信息的json字符�?
	 * @param 
	 * @return 
	 *     add time �?2011-10-07
	 *  modify time �?2011-10-07
	 */
	public static String err(String str) {
		// TODO Auto-generated method stub
		String s = "";
		if(str!=null){
			s = str;
		}
		return "{\"success\":\"false\",\"msg\":\""+s+"\"}";
	}
	
	public static String err(String str,String key,String value) {
		// TODO Auto-generated method stub
		String s = "";
		if(str!=null){
			s = str;
		}
		return "{\"success\":\"false\",\"msg\":\""+s+"\",\""+key+"\":\""+value+"\"}";
	}
	
	public static String err(String str,String k1,String v1,String k2,String v2) {
		// TODO Auto-generated method stub
		String s = "";
		if(str!=null){
			s = str;
		}
		return "{\"success\":\"false\",\"msg\":\""+s+"\",\""+k1+"\":\""+v1+"\",\""+k2+"\":\""+v2+"\"}";
	}
	/**
	 *  获取班次的中文显示字符串
	 */
	public static String getShowBan(Object str) {
		if(str==null){
			return "-";
		}
		String ban = str.toString();
		String no = ban.substring(8, 10);
		if(no.equals("01")){
			//no = "第一班";
			no = "";
		}else if(no.equals("02")){
			no = "第二班";
		}else if(no.equals("03")){
			no = "第三班";
		}else if(no.equals("04")){
			no = "第四班";
		}else if(no.equals("05")){
			no = "第五班";
		}else if(no.equals("06")){
			no = "第六班";
		}else{
			return "错误日期";
		}
		ban = ban.substring(0, 4)+"-"+ban.substring(4, 6)+"-"+ban.substring(6, 8)+" "+no;
		return ban;
	}
	
	/**
	 *  获取班次的中文显示字符串
	 */
	public static String getShowBanForKL(String ban) {
		String no = ban.substring(8, 10);
		if(no.equals("01")){
			no = "午班";
		}else if(no.equals("02")){
			no = "晚班";
		}
		ban = no;
		return ban;
	}

	public static String lpad(String str , int length ) {
		return lpad( str ,  length ,  ' ' );
	}
	
	public static String rpad(String str , int length ) {
		return rpad( str ,  length ,  ' ' );
	}
	
	
	public static String lpad(String str , int length , char dot ) {
		// TODO Auto-generated method stub
		int size = str.getBytes().length;
		if(size == length)  return str;
		if(size > length){
			return str;
		}
		int n = length - size;
		char[] dots = new char[n];
		for(int i=0;i<n;i++){
			dots[i] = dot;
		}
		return String.valueOf(dots)+str;
	}
	
	public static String rpad(String str , int length , char dot ) {
		// TODO Auto-generated method stub
		int size = str.getBytes().length;
		if(size == length)  return str;
		if(size > length){
			return str;
		}
		int n = length - size;
		char[] dots = new char[n];
		for(int i=0;i<n;i++){
			dots[i] = dot;
		}
		return str+String.valueOf(dots);
	}
	
	public static String billType(String bill_type) {
		if(bill_type==null) return "无";
		if(bill_type.equals("in")) return "采购";
		if(bill_type.equals("out")) return "领取";
		if(bill_type.equals("return")) return "退领";
		if(bill_type.equals("backout")) return "退货";
		if(bill_type.equals("check")) return "盘点";
		return "无";
	}
	
	public static String callType(Object obj) {
		if(obj==null) return "未知";
		String call_type = obj.toString();
		if(call_type.equals("1")) return "即起";
		if(call_type.equals("0")) return "叫起";
		return "未知";
	}

	public static String getBillFee(List packages ,List items){
		double total = 0;
		for(int i=0;i<packages.size();i++){
			Map item = (Map)packages.get(i);
			double price = Double.parseDouble(item.get("PACKAGE_PRICE").toString());
			total = total + price;
		}
		for(int i=0;i<items.size();i++){
			Map item = (Map)items.get(i);
			if(!item.get("PACKAGE_ID").toString().trim().equals("")){
				continue;
			}
			double price = Double.parseDouble(item.get("PRICE").toString());
			double count = Double.parseDouble(item.get("COUNT").toString());
			double back_count = Double.parseDouble(item.get("BACK_COUNT").toString());
			double free_count = Double.parseDouble(item.get("FREE_COUNT").toString());
			double rate = Double.parseDouble(item.get("PAY_RATE").toString());
			log("rate="+rate);
			total = total + price*(count-back_count-free_count)*(rate)/100;
		}
		log("total="+total);
		return df.format(total);
	}
	
	public static String getRateFee(List items){
		double total = 0;
		for(int i=0;i<items.size();i++){
			Map item = (Map)items.get(i);
			if(!item.get("PACKAGE_ID").toString().trim().equals("")){
				continue;
			}
			double price = Double.parseDouble(item.get("PRICE").toString());
			double count = Double.parseDouble(item.get("COUNT").toString());
			double back_count = Double.parseDouble(item.get("BACK_COUNT").toString());
			double free_count = Double.parseDouble(item.get("FREE_COUNT").toString());
			double rate = Double.parseDouble(item.get("PAY_RATE").toString());
			log("rate="+rate);
			total = total + price*(count-back_count-free_count)*(100-rate)/100;
		}
		log("total="+total);
		return df.format(total);
	}
	
	public static Object MapToJson(Map bill) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
