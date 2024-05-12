package com.amway.pojo.response;

import java.util.ArrayList;
import java.util.Date;

import com.amway.pojo.response.CreatePaymentResponseDTO.Amount;
import com.amway.pojo.response.CreatePaymentResponseDTO.Transaction;
import com.amway.pojo.response.CreatePaymentResponseDTO.UserInfo;

import lombok.Data;

@Data
public class CreatePaymentExternalResponseDTO {
	public String paymentId;
	public String refId;
	public boolean initiated;
	public String refType;
	public Amount amount;
	public UserInfo userInfo;
	public int authorizedAmount;
	public String status;
	public ArrayList<Transaction> transactions;

	@Data
	public static class Transaction {
		public String transactionId;
		public String paymentMethodProviderId;
		public String refundMethodProviderId;
		public String paymentMethodCode;
		public String paymentProviderCode;
		public String userAccountId;
		public String refundReason;
		public String paymentLocation;
		public String terminalId;
		public String refundedAmount;
		public Amount amount;
		public String status;
		public ArrayList<Entry> entries;
		public Date createdAt;
		public Date lastUpdatedAt;
	}

	@Data
	public static class UserInfo {
		public String accountId;
		public String profileId;
	}

	@Data
	public  static class Amount {
		public String currency;
		public int value;
	}

	@Data
	public static class Entry {
		public String entryType;
		public Amount amount;
		public String description;
		public String status;
		public Payload payload;
		public UserParams userParams;
		public String entryId;
		public Date createdAt;
	}

	@Data
	public static class Payload {
		public String webPaymentUrl;
		public String redirectUrl;
		public String merchantID;
		public String accountId;
		public String transactionReference;
		public String tokenize;
	}

	@Data
	public static class UserParams {
	}

}
