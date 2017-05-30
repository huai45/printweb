package com.huai.common.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IData extends HashMap {

	public IData() {
	}
	
	public IData(Map map) {
	    this.putAll(map);
	}

	public String getString(Object key){
		if(this.containsKey(key)){
			return this.get(key).toString();
		}
		return null;
	}
	
	public boolean has(Object key){
		if(this.containsKey(key)){
			return true;
		}
		return false;
	}

}
