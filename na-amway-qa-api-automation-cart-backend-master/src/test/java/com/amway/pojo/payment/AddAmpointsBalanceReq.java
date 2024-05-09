package com.amway.pojo.payment;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddAmpointsBalanceReq {

	 public String entryType;
	    public int points;
	    public String referenceId;
	    public String referenceType;
	    public String description;
	    public Payload payload;
	   
	    
	    @Data
	    @Builder
	    public static class Payload{
	    }	
}
