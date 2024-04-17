package com.amway.pojo.payment;



import java.util.ArrayList;

import com.amway.pojo.request.CreateCartRequestDTO;
import com.amway.pojo.request.CreateCartRequestDTO.Entry;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;



@Data
@Builder
public class CreatePaymentRequest {

		@NonNull
	    public String refId;
		@NonNull
	    public String refType;
	    public Amount amount;
	    public UserInfo userInfo;
	    public ArrayList<Transaction> transactions;
	
	    @Data
	    @Builder
	    public static class Amount{
	    	@NonNull
	        public String currency;
	    	@NonNull
	        public int value;
	    }
	    @Data
	    @Builder
	    public static class Entry{
	    	@NonNull
	        public String entryType;
	        public Payload payload;
	    }
	    @Data
	    @Builder
	    public static class Payload{
	        public boolean tokenize;
	    }
	    @Data
	    @Builder
	    public static class UserInfo{
	        public String accountId;
	        public String profileId;
	    }


	    @Data
	    @Builder
	    public static class Transaction{
	    	@NonNull
	        public String paymentMethodProviderId;
	    	@NonNull
	        public String paymentMethodCode;
	        public String paymentLocation;
	        public String terminalId;
	        @NonNull
	        public String description;
	        public Amount amount;
	        public Entry entry;
	       
	    }



}

