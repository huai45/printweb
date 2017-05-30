package com.huai.common.service;

import java.util.List;

import com.huai.common.domain.IData;

public interface PrintBillService {

	public String printBill(IData bill);
	
    public String printQueryBill(IData bill);
	
	public String printFinishBill(IData bill);
	
	public String printOneBill();

	public String saveBills(List bills);

	
}
