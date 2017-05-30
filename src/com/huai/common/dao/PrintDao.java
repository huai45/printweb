package com.huai.common.dao;

import java.util.List;

import com.huai.common.domain.IData;

public interface PrintDao {

	public List queryPrintFood(IData param);

	public String submitPrintFood(IData food);
	
	public List queryPrintBill(IData param);
	
	public String submitPrintBill(IData bill);

	public IData queryBillById(IData bill);

	public String saveBill(IData bill);

	public String saveFood(IData food);

	public String deleteHistory();

}
