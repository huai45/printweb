package com.huai.common.service;

import java.util.List;
import com.huai.common.domain.IData;

public interface PrintFoodService {

	public String printFood(IData food) throws Exception;

	public String printOneFood();

	public String saveFoods(List foods);

	public String deleteHistory();
	
	
}
