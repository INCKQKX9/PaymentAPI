package com.amway.pojo.payment;

import java.util.ArrayList;

import com.amway.pojo.request.CreateCartRequestDTO;
import com.amway.pojo.request.CreateCartRequestDTO.Entry;

import lombok.Builder;
import lombok.Data;



public class AmpurseRes {
	public String id;
	public String accountId;
	public String accountType;
	public String currency;
	public int availableBalance;
	public int creditLimit;
	public int lockedValue;
	public int balance;
	public int version;
}
