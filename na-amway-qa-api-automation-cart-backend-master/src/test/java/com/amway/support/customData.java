package com.amway.support;

import java.util.LinkedHashMap;
import java.util.LinkedList;
public class customData {
	// global variables of the class
	
	public LinkedList<LinkedHashMap<String, String>> ProductDetails;
	public LinkedHashMap<String, String> shippingDetails;
	public LinkedHashMap<String, String> billingDetails;
	public LinkedHashMap<String, String> paymentDetails;
	public LinkedHashMap<String, String> orderSummaryDetails;
	public LinkedList<LinkedHashMap<String, String>> productPriceFromReviewPageList;
	public LinkedHashMap<String, String> OrderInfoFromReceiptPage;

	// constructor has type of data that is required
	public void setData(LinkedList<LinkedHashMap<String, String>> ProductDetails, 
			LinkedHashMap<String, String> shippingDetails,
			LinkedHashMap<String, String> billingDetails,
			LinkedHashMap<String, String> paymentDetails,
			LinkedHashMap<String, String> orderSummaryDetails,
			LinkedList<LinkedHashMap<String, String>> productPriceFromReviewPageList,
			LinkedHashMap<String, String> OrderInfoFromReceiptPage) {
		
		// initialize the input variable from main
		// function to the global variable of the class
		this.ProductDetails = ProductDetails;
		this.shippingDetails = shippingDetails;
		this.billingDetails = billingDetails;
		this.paymentDetails = paymentDetails;
		this.orderSummaryDetails = orderSummaryDetails;
		this.productPriceFromReviewPageList = productPriceFromReviewPageList;
		this.OrderInfoFromReceiptPage = OrderInfoFromReceiptPage;
	}
	
	public customData() {
		
		// initialize the input variable from main
		// function to the global variable of the class
		this.ProductDetails = null;
		this.shippingDetails = null;
		this.billingDetails = null;
		this.paymentDetails = null;
		this.orderSummaryDetails = null;
		this.productPriceFromReviewPageList = null;
		this.OrderInfoFromReceiptPage = null;
	}
}
