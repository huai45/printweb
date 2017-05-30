package com.huai.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.huai.common.util.HttpPostUtils;

public class HttpPostTest {

	public static void main(String[] args) { 
		Map param = new HashMap();
		param.put("appid", "bjkl");
		param.put("name", "大海都是水");
		param.put("time", "123");
		long t1 = System.currentTimeMillis();
		String y = HttpPostUtils.doPost("http://localhost:9999/localprint/queryfoodprint.html",param);
		long t2 = System.currentTimeMillis();
        System.out.println(y);
        JSONObject obj = JSONObject.fromObject(y);
        long t3 = System.currentTimeMillis();
        List foods = (List)obj.get("data");
        
        System.out.println(foods.size());
        System.out.println(" t1 = "+(t1));
        System.out.println(" t2 = "+(t2));
        System.out.println(" t3 = "+(t3));
        System.out.println(" t2-t1 = "+(t2-t1));
        for(int i=0;i<foods.size();i++){
        	Map food = (Map)foods.get(i);
        	System.out.println(food);
        }
    } 
	
	
}
