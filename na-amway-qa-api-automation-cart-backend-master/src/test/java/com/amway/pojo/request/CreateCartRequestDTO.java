package com.amway.pojo.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCartRequestDTO {
    public Boolean isGroupCart;
    public ArrayList<Entry> entries;
    public String warehouseId;
    
    @Data
    @Builder
	public static class BundleSlot{
	    public String id;
	    public ArrayList<Entry> entries;
	}

    @Data
    @Builder
	public  static class Entry{
	    public Product product;
	    public int quantity;
	    public ArrayList<BundleSlot> bundleSlots;
	}

    @Data
    @Builder
	public static class Product{
	    public String id;
	}

	

	

}
