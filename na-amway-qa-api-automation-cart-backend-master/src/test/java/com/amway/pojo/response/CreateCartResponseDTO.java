package com.amway.pojo.response;

import java.util.ArrayList;
import java.util.Date;

import lombok.Data;

@Data
public class CreateCartResponseDTO {
	
	
	    public String id;
	    public boolean isGroupCart;
	    public AmwayUser amwayUser;
	    public ArrayList<SubCart> subCarts;
	    public SubTotal subTotal;
	    public TotalDiscount totalDiscount;
	    public int totalEntries;
	    public TotalPrice totalPrice;
	    public TotalPriceWithoutDiscount totalPriceWithoutDiscount;
	    public Fulfillment fulfillment;
	    public ArrayList<Error> errors;
	    public int version;
	    
	    @Data
	    public class AmwayUser{
	        public String amwayUserId;
	    }
	    @Data
	    public class AmwayValue{
	        public int businessVolume;
	        public int pointValue;
	    }
	    @Data
	    public class ApplicableCoupon{
	        public Date couponStartDate;
	        public Date couponEndDate;
	        public int promotionId;
	        public int priority;
	        public ArrayList<Message> message;
	        public ArrayList<String> coupons;
	        public Date startDate;
	        public Date endDate;
	        public ArrayList<Title> title;
	    }
	    @Data
	    public class BasePrice{
	        public double value;
	        public String currency;
	        public AmwayValue amwayValue;
	    }
	    @Data
	    public class CouponResults{
	        public ArrayList<ApplicableCoupon> applicableCoupons;
	    }
	    @Data
	    public class Entry{
	        public PromotionResults promotionResults;
	        public RationingResult rationingResult;
	        public boolean isPersistent;
	        public boolean isFreeItem;
	        public ArrayList<Error> errors;
	        public int entryId;
	        public Product product;
	        public int quantity;
	        public RetailPrice retailPrice;
	        public TotalPrice totalPrice;
	        public TotalPriceWithoutDiscount totalPriceWithoutDiscount;
	    }
	    @Data
	    public class Error{
	        public String title;
	        public String detail;
	        public String severity;
	        public String code;
	    }
	    @Data
	    public class Flags{
	        public boolean isDonation;
	        public boolean restrictFromAddToCart;
	        public boolean restrictFromNonAbo;
	        public ArrayList<String> restrictFromSpecifiedUsers;
	        public ArrayList<String> restrictFromAccountTypes;
	        public boolean restrictFromAnonymous;
	        public ArrayList<String> restrictToAwardLevel;
	        public boolean isPickUpAvailable;
	        public boolean isNonBusinessVolumeItem;
	        public boolean isDeliveryAvailable;
	        public boolean isKit;
	        public boolean isInstallmentPlanEligible;
	        public boolean restrictFromCollectionCenter;
	    }
	    @Data
	    public class Fulfillment{
	        public String freeDeliveryAmount;
	        public String deliveryCharge;
	        public String serviceLevel;
	        public boolean freeShipping;
	        public String currency;
	    }
	    @Data
	    public class Message{
	        public String language;
	        public String text;
	    }
	    @Data
	    public class PotentialPromotion{
	        public Promotion promotion;
	    }
	    @Data
	    public class Product{
	        public String id;
	        public String name;
	        public String cartIconPublicUrl;
	        public String cartIconAltText;
	        public String copyServingSize;
	        public String derivedAvailabilityStatus;
	        public String itemDisposition;
	        public boolean isSellable;
	        public String brand;
	        public ArrayList<String> categories;
	        public BasePrice basePrice;
	        public Flags flags;
	    }
	    @Data
	    public class Promotion{
	        public String code;
	        public String name;
	        public String message;
	        public boolean isApplied;
	        public Date startDate;
	        public Date endDate;
	    }
	    @Data
	    public class PromotionResults{
	        public ArrayList<Object> firedPromotions;
	        public ArrayList<Object> potentialPromotions;
	    }
	    @Data
	    public class RationingResult{
	        public int availableQuantity;
	        public String frequency;
	    }
	    @Data
	    public class RetailPrice{
	        public double value;
	        public String currency;
	        public AmwayValue amwayValue;
	    }


	    @Data
	    public class SubCart{
	        public String subCartId;
	        public AmwayUser amwayUser;
	        public ArrayList<Entry> entries;
	        public PromotionResults promotionResults;
	        public CouponResults couponResults;
	        public ArrayList<Object> promotionalItemOptions;
	        public SubTotal subTotal;
	        public TotalDiscount totalDiscount;
	        public TotalUpliftAmwayValue totalUpliftAmwayValue;
	        public TotalItemLevelDiscount totalItemLevelDiscount;
	        public int totalEntries;
	        public TotalPrice totalPrice;
	        public TotalPriceWithoutDiscount totalPriceWithoutDiscount;
	        public TotalPriceWithTax totalPriceWithTax;
	        public TotalTax totalTax;
	        public boolean isRationingApplied;
	        public ArrayList<Error> errors;
	    }
	    @Data
	    public class SubTotal{
	        public double value;
	        public String currency;
	        public AmwayValue amwayValue;
	    }
	    @Data
	    public class Title{
	        public String language;
	        public String text;
	    }
	    @Data
	    public class TotalDiscount{
	        public double value;
	        public String currency;
	        public AmwayValue amwayValue;
	    }
	    @Data
	    public class TotalItemLevelDiscount{
	        public double value;
	        public String currency;
	        public AmwayValue amwayValue;
	    }
	    @Data
	    public class TotalPrice{
	        public double value;
	        public String currency;
	        public AmwayValue amwayValue;
	    }
	    @Data
	    public class TotalPriceWithoutDiscount{
	        public double value;
	        public String currency;
	        public AmwayValue amwayValue;
	    }
	    @Data
	    public class TotalPriceWithTax{
	        public double value;
	        public String currency;
	        public AmwayValue amwayValue;
	    }
	    @Data
	    public class TotalTax{
	        public double value;
	    }
	    @Data
	    public class TotalUpliftAmwayValue{
	        public int businessVolume;
	        public int pointValue;
	    }

}
