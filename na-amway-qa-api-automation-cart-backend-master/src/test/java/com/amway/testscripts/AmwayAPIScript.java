package com.amway.testscripts;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amway.support.TestDataExtractor;
import com.amway.pages.HomePage;
import com.amway.support.BrowserActions;
import com.amway.support.DataProviderUtils;
import com.amway.support.EmailReport;
import com.amway.support.EnvironmentPropertiesReader;
import com.amway.support.Log;
import com.amway.support.RestAssuredAPI;
import com.amway.support.WebDriverFactory;
import com.amway.support.Utils;
import com.jayway.jsonpath.JsonPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONArray;
import io.restassured.http.ContentType;


@Listeners(EmailReport.class)
public class AmwayAPIScript extends TestDataExtractor {

private static EnvironmentPropertiesReader configProperty = EnvironmentPropertiesReader.getInstance();
		
	String webSite;
		String magentoWebsite;
		private String workbookName = "testdata\\data\\amway.xls";
		private String sheetName = "DemoCase_Data";
		
        String sessionId = "TCWOA0pTXdDd2ULnySHNA3Kf1ZVGsvXRJjOJhObKVOM";
        String aboSessionId = "baSLJmHPB7OgE5eXJ689d4z_JQ7wSAc2g1nDVzb0JIs";
        String guestSessionId = "333c304b-d69a-4206-9dfe-f2b6ccefa3c8";

		@BeforeTest(alwaysRun = true)
		public void init(ITestContext context) throws Exception {
			webSite = (System.getProperty("Website") != null ? System
					.getProperty("Website") : context.getCurrentXmlTest()
					.getParameter("Website"));
			
		}
		
		
		@Test(enabled = true, description = "Testcase to verify the Complete journey of login customer with single product API and verify the Status Code and response", dataProviderClass = DataProviderUtils.class, dataProvider = "parallelTestDataProvider")
		public void TC_Amway_API_E2E_001(String browser) throws Exception {
			// ** Loading the test data from excel using the test case id */
			HashMap<String, String> testData = TestDataExtractor.initTestData(
					workbookName, sheetName);
			HashMap<String,String> headerMap = new HashMap<String,String>();
			HashMap<String,String> qparam = new HashMap<String,String>();
			qparam.put("projections", "DETAILED");
			Utils utils = new Utils();
			String rand1 = String.valueOf(utils.getRandom(1000, 100000));
			System.out.print("Random Value"+rand1);
			headerMap.put("X-Idempotency-Key",rand1);
			headerMap.put("Cookie", "session=kbuS8l__IzEyW-lrMeG__lzsyOwjWL22BA-Mzgwgv-M");
			headerMap.put("Accept-Language", "en-us");
			
			headerMap.put("X-Amway-Tenant", "200");
			headerMap.put("X-Amway-Vertical", "DEFAULT");
			
			headerMap.put("X-Amway-Country", "th");
			headerMap.put("X-Amway-Channel", "WEB");
			headerMap.put("Content-type", "application/json");
			String body1 = testData.get("jsonBody");
			String url1 = "comcartas/v1/carts";
			// Get the web driver instance
			int i = 1;
			try {
				
			   RestAssuredAPI rp = new RestAssuredAPI();
			   Log.message("<b>==========================================================</b>");
			   Log.message("<i><b>   Validation for create_cart_API  Started   </b></i>");
			   Log.message("<b>==========================================================</b>");
			   Log.message(i++ +".<b> Caputured the create cart id without body API Response</b>");
			   
			  Response response = rp.postRequest(headerMap,qparam,url1,body1);
			  			  
			  Log.assertThat(response.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  @SuppressWarnings("unchecked")
			
			  HashMap<String, Object> map = new Gson().fromJson(response.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map.size());
			  System.out.println(response.getBody().prettyPrint());
			  
			  String tableBody =
					    map.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent = "<table>" + tableBody + "</table>";
					    System.out.println("tableContent = "+tableContent);
					  Log.message(tableContent);
			  
			  String cartId = map.get("id").toString().trim();
			  System.out.println("Cart Id--->"+cartId);
			  
			  Log.message(i++ +".<b> Caputured the CartID--><b>" +  map.get("id").toString());
			  Log.assertThat(map.get("id").toString()!= null, "Cart Id is successfully Created", "Cart Id is not created Successfully");
			
			  String url2 = "comcartas/v1/carts/"+cartId+"/subCarts/"+cartId;
			  System.out.println("Get Url" + url2);
			  System.out.println(response.getBody().prettyPrint());
		
			  System.out.println("Cart Id1--->"+response);
			  
			  io.restassured.path.json.JsonPath jsonPath = response.jsonPath();
				 
			 String produdctId = jsonPath.getString("subCarts.entries.product.id").toString();
			 
			 produdctId = produdctId.replace("[", "").replace("]", "");
			 
			 System.out.println("Product Id1--->"+produdctId);
			 
			 Log.assertThat(produdctId.equals("117580TH"), "Product id is verified and valid Product Id is Passed in Cart API request and response.", "Cart Id is not created Successfully");
			 
			 
			  Log.message("<b>==========================================================</b>");
			  Log.message("<i><b>   Validation for get cart by ID API Started     </b></i>");
			  Log.message("<b>==========================================================</b>");
			  Log.message(i++ +".<b> Caputured the get cart id by id API Response</b>");
			  Response response1 = rp.getRequest(headerMap,url2);
			  System.out.println("Get Status Code" + response1.statusCode());
			  Log.assertThat(response1.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  
			  
			  HashMap<String, Object> map1 = new Gson().fromJson(response1.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map1.size());
			  System.out.println(response1.getBody().prettyPrint());
			  
			  String tableBody1 =
					    map1.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map1.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent1 = "<table>" + tableBody1 + "</table>";
					    System.out.println("tableContent = "+tableContent1);
					  Log.message(tableContent1);
					  System.out.println("Cart Id 1"+map.get("id").toString().trim());
					  System.out.println("Cart Id 2"+map1.get("id").toString().trim());
					  Log.assertThat(map.get("id").toString().trim().equals(map1.get("id").toString().trim()),"Get Cart API is displayed the same details of created cart via create cart API!", "Get Cart API is not displayed the same details of created cart via create cart API!");
					  
					  

					  Log.message("<b>==========================================================</b>");
					  Log.message("<i><b>   Validation for Create checkout API by ID API Started     </b></i>");
					  Log.message("<b>==========================================================</b>");
					  Log.message(i++ +".<b> Caputured the Create checkout API Response</b>");
					  
					  String url3 = "comcheckoutas/v1/checkouts";
					  
					  String body2 = testData.get("jsonBody1").replace("+cartId", cartId);
					  
					  System.out.println("Checkout Body = "+body2);
					  
					  System.out.println("Property URL = "+url3);
					  
					  Response response2 = rp.postRequest(headerMap,url3,body2);
					  
					  System.out.println("Checkout response = "+response2);
					  
					  Log.assertThat(response2.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
					  
					  HashMap<String, Object> map2 = new Gson().fromJson(response2.asString(), HashMap.class);
					  System.out.println("JSon via Size"+map2.size());
					  System.out.println(response2.getBody().prettyPrint());
					  
					  String tableBody2 =
							    map2.keySet()
							       .stream()
							       .map(item -> new StringBuilder("<tr><td>")
							                                     .append(item)
							                                     .append("</td><td>")
							                                     .append(String.valueOf(map2.get(item)))
							                                     .append("</td></tr>")
							       )
							       .collect(Collectors.joining());
							    
							    String tableContent2 = "<table>" + tableBody2 + "</table>";
							    System.out.println("tableContent = "+tableContent2);
							  Log.message(tableContent2);
					
						Log.assertThat(map.get("id").toString().trim().equals(map2.get("cartId").toString().trim()),"Checkout Cart API is displayed the same details of created cart via create cart API!", "Checkout Cart API is not displayed the same details of created cart via create cart API!");
						
						Log.assertThat(map2.get("checkoutId").toString()!= null, "Checkout Id is successfully Created", "Checkout Id is not created Successfully");
			
						String checkoutId= map2.get("checkoutId").toString();
						
						System.out.println("CheckoutId--->" +checkoutId);
						
						 io.restassured.path.json.JsonPath jsonPath1 = response2.jsonPath();
						 
						 String produdctIdcheckout = jsonPath1.getString("entries.product.id").toString();
						 
						 produdctIdcheckout = produdctIdcheckout.replace("[", "").replace("]", "");
						 
						 System.out.println("produdctIdcheckout--->" +produdctIdcheckout);
						
						 System.out.println("Product Id1--->"+produdctId);
						 
						 Log.assertThat(produdctId.equals(produdctIdcheckout) , "Product id is verified and valid Product Id is Passed in checkout API and Cart API response.", "Product id is not matched and valid Product Id is Passed in checkout API and Cart API response.");
						 
						
						 
						
						 Log.message("<b>==========================================================</b>");
						 Log.message("<i><b>   Validation for Create Payment API by ID API Started     </b></i>");
						 Log.message("<b>==========================================================</b>");
						 Log.message(i++ +".<b> Caputured the Create Payment API Response</b>");
						
						String url4 = "comcheckoutas/v1/checkouts/"+checkoutId+"/payments";
						String body3 = testData.get("jsonBody2");
						
						
						  System.out.println("Checkout Body = "+body3);
						  
						  System.out.println("Property URL = "+url4);
						  
						  Response response3 = rp.postRequest(headerMap,url4,body3);
						  
						  System.out.println("Checkout response = "+response3);
						  
						  Log.assertThat(response2.statusCode()==200, "Payment Status code is return as 200.", "Payment Status Code is return as not 200");
						  
						  HashMap<String, Object> map3 = new Gson().fromJson(response3.asString(), HashMap.class);
						  System.out.println("JSon via Size"+map3.size());
						  System.out.println(response2.getBody().prettyPrint());
						  
						  String tableBody3 =
								    map3.keySet()
								       .stream()
								       .map(item -> new StringBuilder("<tr><td>")
								                                     .append(item)
								                                     .append("</td><td>")
								                                     .append(String.valueOf(map3.get(item)))
								                                     .append("</td></tr>")
								       )
								       .collect(Collectors.joining());
								    
								    String tableContent3 = "<table>" + tableBody3 + "</table>";
								    System.out.println("tableContent = "+tableContent3);
								  Log.message(tableContent3);
								  
							Log.message("<b>==========================================================</b>");
							Log.message("<i><b>   Validation for Create Order API by ID API Started     </b></i>");
							Log.message("<b>==========================================================</b>");
							Log.message(i++ +".<b> Caputured the Create Order API Response</b>");
								  
						   String url5 = "comorderas/v1/orders";
						   String body4 = testData.get("jsonBody3").replace("+checkoutID",checkoutId);
						   
						   Response response4 = rp.postRequest(headerMap,url5,body4);
						   
						   System.out.println("Checkout response = "+response4);
							  
							  Log.assertThat(response4.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
							  
							  HashMap<String, Object> map4 = new Gson().fromJson(response4.asString(), HashMap.class);
							  System.out.println("JSon via Size"+map4.size());
							  System.out.println(response2.getBody().prettyPrint());
							  
							  String tableBody4 =
									    map4.keySet()
									       .stream()
									       .map(item -> new StringBuilder("<tr><td>")
									                                     .append(item)
									                                     .append("</td><td>")
									                                     .append(String.valueOf(map4.get(item)))
									                                     .append("</td></tr>")
									       )
									       .collect(Collectors.joining());
									    
									    String tableContent4 = "<table>" + tableBody4 + "</table>";
									    System.out.println("tableContent = "+tableContent4);
									    Log.message(tableContent4);
								
							    
						
			}
				
			catch (Exception e) {
				Log.exception(e);
			} // catch
			finally {
				Log.endTestCase();
			} // finally
		}
		
		@Test(enabled = false, description = "Testcase to verify the Complete journey of ABO customer using single product  and verify the Status Code and response", dataProviderClass = DataProviderUtils.class, dataProvider = "parallelTestDataProvider")
		public void TC_Amway_API_E2E_002(String browser) throws Exception {
			// ** Loading the test data from excel using the test case id */
			HashMap<String, String> testData = TestDataExtractor.initTestData(
					workbookName, sheetName);
			HashMap<String,String> headerMap = new HashMap<String,String>();
			HashMap<String,String> qparam = new HashMap<String,String>();
			qparam.put("projections", "DETAILED");
			Utils utils = new Utils();
			String rand1 = String.valueOf(utils.getRandom(1000, 100000));
			System.out.print("Random Value"+rand1);
			headerMap.put("X-Idempotency-Key",rand1);
			headerMap.put("Cookie", "session=xOjNmec1lwznXV_of-uZrvj8aaLnPFry_4xD4a6p-9c");
			headerMap.put("Accept-Language", "en-us");
			
			headerMap.put("X-Amway-Tenant", "200");
			headerMap.put("X-Amway-Vertical", "DEFAULT");
			
			headerMap.put("X-Amway-Country", "th");
			headerMap.put("X-Amway-Channel", "WEB");
			headerMap.put("Content-type", "application/json");
			String body1 = testData.get("jsonBody");
			String url1 = "comcartas/v1/carts";
			// Get the web driver instance
			int i = 1;
			try {
				
			   RestAssuredAPI rp = new RestAssuredAPI();
			   Log.message("<b>==========================================================</b>");
			   Log.message("<i><b>   Validation for create_cart_API  Started   </b></i>");
			   Log.message("<b>==========================================================</b>");
			   Log.message(i++ +".<b> Caputured the create cart id without body API Response</b>");
			   
			  Response response = rp.postRequest(headerMap,qparam,url1,body1);
			  			  
			  Log.assertThat(response.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  @SuppressWarnings("unchecked")
			
			  HashMap<String, Object> map = new Gson().fromJson(response.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map.size());
			  System.out.println(response.getBody().prettyPrint());
			  
			  String tableBody =
					    map.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent = "<table>" + tableBody + "</table>";
					    System.out.println("tableContent = "+tableContent);
					  Log.message(tableContent);
			  
			  String cartId = map.get("id").toString().trim();
			  System.out.println("Cart Id--->"+cartId);
			  
			  Log.message(i++ +".<b> Caputured the CartID--><b>" +  map.get("id").toString());
			  Log.assertThat(map.get("id").toString()!= null, "Cart Id is successfully Created", "Cart Id is not created Successfully");
			
			  String url2 = "comcartas/v1/carts/"+cartId+"/subCarts/"+cartId;
			  System.out.println("Get Url" + url2);
		
			  Log.message("<b>==========================================================</b>");
			  Log.message("<i><b>   Validation for get cart by ID API Started     </b></i>");
			  Log.message("<b>==========================================================</b>");
			  Log.message(i++ +".<b> Caputured the get cart id by id API Response</b>");
			  Response response1 = rp.getRequest(headerMap,url2);
			  System.out.println("Get Status Code" + response1.statusCode());
			  Log.assertThat(response1.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  
			  
			  HashMap<String, Object> map1 = new Gson().fromJson(response1.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map1.size());
			  System.out.println(response1.getBody().prettyPrint());
			  
			  String tableBody1 =
					    map1.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map1.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent1 = "<table>" + tableBody1 + "</table>";
					    System.out.println("tableContent = "+tableContent1);
					  Log.message(tableContent1);
					  System.out.println("Cart Id 1"+map.get("id").toString().trim());
					  System.out.println("Cart Id 2"+map1.get("id").toString().trim());
					  Log.assertThat(map.get("id").toString().trim().equals(map1.get("id").toString().trim()),"Get Cart API is displayed the same details of created cart via create cart API!", "Get Cart API is not displayed the same details of created cart via create cart API!");
					  
					  

					  Log.message("<b>==========================================================</b>");
					  Log.message("<i><b>   Validation for Create checkout API by ID API Started     </b></i>");
					  Log.message("<b>==========================================================</b>");
					  Log.message(i++ +".<b> Caputured the Create checkout API Response</b>");
					  
					  String url3 = "comcheckoutas/v1/checkouts";
					  
					  String body2 = testData.get("jsonBody1").replace("+cartId", cartId);
					  
					  System.out.println("Checkout Body = "+body2);
					  
					  System.out.println("Property URL = "+url3);
					  
					  Response response2 = rp.postRequest(headerMap,url3,body2);
					  
					  System.out.println("Checkout response = "+response2);
					  
					  Log.assertThat(response2.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
					  
					  HashMap<String, Object> map2 = new Gson().fromJson(response2.asString(), HashMap.class);
					  System.out.println("JSon via Size"+map2.size());
					  System.out.println(response2.getBody().prettyPrint());
					  
					  String tableBody2 =
							    map2.keySet()
							       .stream()
							       .map(item -> new StringBuilder("<tr><td>")
							                                     .append(item)
							                                     .append("</td><td>")
							                                     .append(String.valueOf(map2.get(item)))
							                                     .append("</td></tr>")
							       )
							       .collect(Collectors.joining());
							    
							    String tableContent2 = "<table>" + tableBody2 + "</table>";
							    System.out.println("tableContent = "+tableContent2);
							  Log.message(tableContent2);
					
						Log.assertThat(map.get("id").toString().trim().equals(map2.get("cartId").toString().trim()),"Checkout Cart API is displayed the same details of created cart via create cart API!", "Checkout Cart API is not displayed the same details of created cart via create cart API!");
						
						Log.assertThat(map2.get("checkoutId").toString()!= null, "Checkout Id is successfully Created", "Checkout Id is not created Successfully");
			
						String checkoutId= map2.get("checkoutId").toString();
						
						System.out.println("CheckoutId--->" +checkoutId);
						
						 Log.message("<b>==========================================================</b>");
						 Log.message("<i><b>   Validation for Create Payment API by ID API Started     </b></i>");
						 Log.message("<b>==========================================================</b>");
						 Log.message(i++ +".<b> Caputured the Create Payment API Response</b>");
						
						String url4 = "comcheckoutas/v1/checkouts/"+checkoutId+"/payments";
						String body3 = testData.get("jsonBody2");
						
						  System.out.println("Checkout Body = "+body3);
						  
						  System.out.println("Property URL = "+url4);
						  
						  Response response3 = rp.postRequest(headerMap,url4,body3);
						  
						  System.out.println("Checkout response = "+response3);
						  
						  Log.assertThat(response2.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
						  
						  HashMap<String, Object> map3 = new Gson().fromJson(response3.asString(), HashMap.class);
						  System.out.println("JSon via Size"+map3.size());
						  System.out.println(response2.getBody().prettyPrint());
						  
						  String tableBody3 =
								    map3.keySet()
								       .stream()
								       .map(item -> new StringBuilder("<tr><td>")
								                                     .append(item)
								                                     .append("</td><td>")
								                                     .append(String.valueOf(map3.get(item)))
								                                     .append("</td></tr>")
								       )
								       .collect(Collectors.joining());
								    
								    String tableContent3 = "<table>" + tableBody3 + "</table>";
								    System.out.println("tableContent = "+tableContent3);
								  Log.message(tableContent3);
						
			}
				
			catch (Exception e) {
				Log.exception(e);
			} // catch
			finally {
				Log.endTestCase();
			} // finally
		}
		
		@Test(enabled = false, description = "Testcase to verify the Complete journey of login customer with single product API and pickup at store and verify the Status Code and response", dataProviderClass = DataProviderUtils.class, dataProvider = "parallelTestDataProvider")
		public void TC_Amway_API_E2E_003(String browser) throws Exception {
			// ** Loading the test data from excel using the test case id */
			HashMap<String, String> testData = TestDataExtractor.initTestData(
					workbookName, sheetName);
			HashMap<String,String> headerMap = new HashMap<String,String>();
			HashMap<String,String> qparam = new HashMap<String,String>();
			qparam.put("projections", "DETAILED");
			Utils utils = new Utils();
			String rand1 = String.valueOf(utils.getRandom(1000, 100000));
			System.out.print("Random Value"+rand1);
			headerMap.put("X-Idempotency-Key",rand1);
			headerMap.put("Cookie", "session=xOjNmec1lwznXV_of-uZrvj8aaLnPFry_4xD4a6p-9c");
			headerMap.put("Accept-Language", "en-us");
			
			headerMap.put("X-Amway-Tenant", "200");
			headerMap.put("X-Amway-Vertical", "DEFAULT");
			
			headerMap.put("X-Amway-Country", "th");
			headerMap.put("X-Amway-Channel", "WEB");
			headerMap.put("Content-type", "application/json");
			String body1 = testData.get("jsonBody");
			String url1 = "comcartas/v1/carts";
			// Get the web driver instance
			int i = 1;
			try {
				
			   RestAssuredAPI rp = new RestAssuredAPI();
			   Log.message("<b>==========================================================</b>");
			   Log.message("<i><b>   Validation for create_cart_API  Started   </b></i>");
			   Log.message("<b>==========================================================</b>");
			   Log.message(i++ +".<b> Caputured the create cart id without body API Response</b>");
			   
			  Response response = rp.postRequest(headerMap,qparam,url1,body1);
			  			  
			  Log.assertThat(response.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  @SuppressWarnings("unchecked")
			
			  HashMap<String, Object> map = new Gson().fromJson(response.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map.size());
			  System.out.println(response.getBody().prettyPrint());
			  
			  String tableBody =
					    map.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent = "<table>" + tableBody + "</table>";
					    System.out.println("tableContent = "+tableContent);
					  Log.message(tableContent);
			  
			  String cartId = map.get("id").toString().trim();
			  System.out.println("Cart Id--->"+cartId);
			  
			  Log.message(i++ +".<b> Caputured the CartID--><b>" +  map.get("id").toString());
			  Log.assertThat(map.get("id").toString()!= null, "Cart Id is successfully Created", "Cart Id is not created Successfully");
			
			  String url2 = "comcartas/v1/carts/"+cartId+"/subCarts/"+cartId;
			  System.out.println("Get Url" + url2);
		
			  Log.message("<b>==========================================================</b>");
			  Log.message("<i><b>   Validation for get cart by ID API Started     </b></i>");
			  Log.message("<b>==========================================================</b>");
			  Log.message(i++ +".<b> Caputured the get cart id by id API Response</b>");
			  Response response1 = rp.getRequest(headerMap,url2);
			  System.out.println("Get Status Code" + response1.statusCode());
			  Log.assertThat(response1.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  
			  
			  HashMap<String, Object> map1 = new Gson().fromJson(response1.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map1.size());
			  System.out.println(response1.getBody().prettyPrint());
			  
			  String tableBody1 =
					    map1.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map1.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent1 = "<table>" + tableBody1 + "</table>";
					    System.out.println("tableContent = "+tableContent1);
					  Log.message(tableContent1);
					  System.out.println("Cart Id 1"+map.get("id").toString().trim());
					  System.out.println("Cart Id 2"+map1.get("id").toString().trim());
					  Log.assertThat(map.get("id").toString().trim().equals(map1.get("id").toString().trim()),"Get Cart API is displayed the same details of created cart via create cart API!", "Get Cart API is not displayed the same details of created cart via create cart API!");
					  
					  

					  Log.message("<b>==========================================================</b>");
					  Log.message("<i><b>   Validation for Create checkout API by ID API Started     </b></i>");
					  Log.message("<b>==========================================================</b>");
					  Log.message(i++ +".<b> Caputured the Create checkout API Response</b>");
					  
					  String url3 = "comcheckoutas/v1/checkouts";
					  
					  String body2 = testData.get("jsonBody1").replace("+cartId", cartId);
					  
					  System.out.println("Checkout Body = "+body2);
					  
					  System.out.println("Property URL = "+url3);
					  
					  Response response2 = rp.postRequest(headerMap,url3,body2);
					  
					  System.out.println("Checkout response = "+response2);
					  
					  Log.assertThat(response2.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
					  
					  HashMap<String, Object> map2 = new Gson().fromJson(response2.asString(), HashMap.class);
					  System.out.println("JSon via Size"+map2.size());
					  System.out.println(response2.getBody().prettyPrint());
					  
					  String tableBody2 =
							    map2.keySet()
							       .stream()
							       .map(item -> new StringBuilder("<tr><td>")
							                                     .append(item)
							                                     .append("</td><td>")
							                                     .append(String.valueOf(map2.get(item)))
							                                     .append("</td></tr>")
							       )
							       .collect(Collectors.joining());
							    
							    String tableContent2 = "<table>" + tableBody2 + "</table>";
							    System.out.println("tableContent = "+tableContent2);
							  Log.message(tableContent2);
					
						Log.assertThat(map.get("id").toString().trim().equals(map2.get("cartId").toString().trim()),"Checkout Cart API is displayed the same details of created cart via create cart API!", "Checkout Cart API is not displayed the same details of created cart via create cart API!");
						
						Log.assertThat(map2.get("checkoutId").toString()!= null, "Checkout Id is successfully Created", "Checkout Id is not created Successfully");
			
						String checkoutId= map2.get("checkoutId").toString();
						
						System.out.println("CheckoutId--->" +checkoutId);
						
						 Log.message("<b>==========================================================</b>");
						 Log.message("<i><b>   Validation for Create Payment API by ID API Started     </b></i>");
						 Log.message("<b>==========================================================</b>");
						 Log.message(i++ +".<b> Caputured the Create Payment API Response</b>");
						
						String url4 = "comcheckoutas/v1/checkouts/"+checkoutId+"/payments";
						String body3 = testData.get("jsonBody2");
						
						
						  System.out.println("Checkout Body = "+body3);
						  
						  System.out.println("Property URL = "+url4);
						  
						  Response response3 = rp.postRequest(headerMap,url4,body3);
						  
						  System.out.println("Checkout response = "+response3);
						  
						  Log.assertThat(response2.statusCode()==200, "Payment Status code is return as 200.", "Payment Status Code is return as not 200");
						  
						  HashMap<String, Object> map3 = new Gson().fromJson(response3.asString(), HashMap.class);
						  System.out.println("JSon via Size"+map3.size());
						  System.out.println(response2.getBody().prettyPrint());
						  
						  String tableBody3 =
								    map3.keySet()
								       .stream()
								       .map(item -> new StringBuilder("<tr><td>")
								                                     .append(item)
								                                     .append("</td><td>")
								                                     .append(String.valueOf(map3.get(item)))
								                                     .append("</td></tr>")
								       )
								       .collect(Collectors.joining());
								    
								    String tableContent3 = "<table>" + tableBody3 + "</table>";
								    System.out.println("tableContent = "+tableContent3);
								  Log.message(tableContent3);
								  
							Log.message("<b>==========================================================</b>");
							Log.message("<i><b>   Validation for Create Order API by ID API Started     </b></i>");
							Log.message("<b>==========================================================</b>");
							Log.message(i++ +".<b> Caputured the Create Order API Response</b>");
								  
						   String url5 = "comorderas/v1/orders";
						   String body4 = testData.get("jsonBody3").replace("+checkoutID",checkoutId);
						   
						   Response response4 = rp.postRequest(headerMap,url5,body4);
						   
						   System.out.println("Checkout response = "+response4);
							  
							  Log.assertThat(response4.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
							  
							  HashMap<String, Object> map4 = new Gson().fromJson(response4.asString(), HashMap.class);
							  System.out.println("JSon via Size"+map4.size());
							  System.out.println(response2.getBody().prettyPrint());
							  
							  String tableBody4 =
									    map4.keySet()
									       .stream()
									       .map(item -> new StringBuilder("<tr><td>")
									                                     .append(item)
									                                     .append("</td><td>")
									                                     .append(String.valueOf(map4.get(item)))
									                                     .append("</td></tr>")
									       )
									       .collect(Collectors.joining());
									    
									    String tableContent4 = "<table>" + tableBody4 + "</table>";
									    System.out.println("tableContent = "+tableContent4);
									    Log.message(tableContent4);
								
									    
						
			}
				
			catch (Exception e) {
				Log.exception(e);
			} // catch
			finally {
				Log.endTestCase();
			} // finally
		}

		
		@Test(enabled = false, description = "Testcase to verify the Complete journey of ABO customer with single product API and pickup at store and verify the Status Code and response", dataProviderClass = DataProviderUtils.class, dataProvider = "parallelTestDataProvider")
		public void TC_Amway_API_E2E_004(String browser) throws Exception {
			// ** Loading the test data from excel using the test case id */
			HashMap<String, String> testData = TestDataExtractor.initTestData(
					workbookName, sheetName);
			HashMap<String,String> headerMap = new HashMap<String,String>();
			HashMap<String,String> qparam = new HashMap<String,String>();
			qparam.put("projections", "DETAILED");
			Utils utils = new Utils();
			String rand1 = String.valueOf(utils.getRandom(1000, 100000));
			System.out.print("Random Value"+rand1);
			headerMap.put("X-Idempotency-Key",rand1);
			headerMap.put("Cookie", "session=xOjNmec1lwznXV_of-uZrvj8aaLnPFry_4xD4a6p-9c");
			headerMap.put("Accept-Language", "en-us");
			
			headerMap.put("X-Amway-Tenant", "200");
			headerMap.put("X-Amway-Vertical", "DEFAULT");
			
			headerMap.put("X-Amway-Country", "th");
			headerMap.put("X-Amway-Channel", "WEB");
			headerMap.put("Content-type", "application/json");
			String body1 = testData.get("jsonBody");
			String url1 = "comcartas/v1/carts";
			// Get the web driver instance
			int i = 1;
			try {
				
			   RestAssuredAPI rp = new RestAssuredAPI();
			   Log.message("<b>==========================================================</b>");
			   Log.message("<i><b>   Validation for create_cart_API  Started   </b></i>");
			   Log.message("<b>==========================================================</b>");
			   Log.message(i++ +".<b> Caputured the create cart id without body API Response</b>");
			   
			  Response response = rp.postRequest(headerMap,qparam,url1,body1);
			  			  
			  Log.assertThat(response.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  @SuppressWarnings("unchecked")
			
			  HashMap<String, Object> map = new Gson().fromJson(response.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map.size());
			  System.out.println(response.getBody().prettyPrint());
			  
			  String tableBody =
					    map.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent = "<table>" + tableBody + "</table>";
					    System.out.println("tableContent = "+tableContent);
					  Log.message(tableContent);
			  
			  String cartId = map.get("id").toString().trim();
			  System.out.println("Cart Id--->"+cartId);
			  
			  Log.message(i++ +".<b> Caputured the CartID--><b>" +  map.get("id").toString());
			  Log.assertThat(map.get("id").toString()!= null, "Cart Id is successfully Created", "Cart Id is not created Successfully");
			
			  String url2 = "comcartas/v1/carts/"+cartId+"/subCarts/"+cartId;
			  System.out.println("Get Url" + url2);
		
			  Log.message("<b>==========================================================</b>");
			  Log.message("<i><b>   Validation for get cart by ID API Started     </b></i>");
			  Log.message("<b>==========================================================</b>");
			  Log.message(i++ +".<b> Caputured the get cart id by id API Response</b>");
			  Response response1 = rp.getRequest(headerMap,url2);
			  System.out.println("Get Status Code" + response1.statusCode());
			  Log.assertThat(response1.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  
			  
			  HashMap<String, Object> map1 = new Gson().fromJson(response1.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map1.size());
			  System.out.println(response1.getBody().prettyPrint());
			  
			  String tableBody1 =
					    map1.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map1.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent1 = "<table>" + tableBody1 + "</table>";
					    System.out.println("tableContent = "+tableContent1);
					  Log.message(tableContent1);
					  System.out.println("Cart Id 1"+map.get("id").toString().trim());
					  System.out.println("Cart Id 2"+map1.get("id").toString().trim());
					  Log.assertThat(map.get("id").toString().trim().equals(map1.get("id").toString().trim()),"Get Cart API is displayed the same details of created cart via create cart API!", "Get Cart API is not displayed the same details of created cart via create cart API!");
					  
					  

					  Log.message("<b>==========================================================</b>");
					  Log.message("<i><b>   Validation for Create checkout API by ID API Started     </b></i>");
					  Log.message("<b>==========================================================</b>");
					  Log.message(i++ +".<b> Caputured the Create checkout API Response</b>");
					  
					  String url3 = "comcheckoutas/v1/checkouts";
					  
					  String body2 = testData.get("jsonBody1").replace("+cartId", cartId);
					  
					  System.out.println("Checkout Body = "+body2);
					  
					  System.out.println("Property URL = "+url3);
					  
					  Response response2 = rp.postRequest(headerMap,url3,body2);
					  
					  System.out.println("Checkout response = "+response2);
					  
					  Log.assertThat(response2.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
					  
					  HashMap<String, Object> map2 = new Gson().fromJson(response2.asString(), HashMap.class);
					  System.out.println("JSon via Size"+map2.size());
					  System.out.println(response2.getBody().prettyPrint());
					  
					  String tableBody2 =
							    map2.keySet()
							       .stream()
							       .map(item -> new StringBuilder("<tr><td>")
							                                     .append(item)
							                                     .append("</td><td>")
							                                     .append(String.valueOf(map2.get(item)))
							                                     .append("</td></tr>")
							       )
							       .collect(Collectors.joining());
							    
							    String tableContent2 = "<table>" + tableBody2 + "</table>";
							    System.out.println("tableContent = "+tableContent2);
							  Log.message(tableContent2);
					
						Log.assertThat(map.get("id").toString().trim().equals(map2.get("cartId").toString().trim()),"Checkout Cart API is displayed the same details of created cart via create cart API!", "Checkout Cart API is not displayed the same details of created cart via create cart API!");
						
						Log.assertThat(map2.get("checkoutId").toString()!= null, "Checkout Id is successfully Created", "Checkout Id is not created Successfully");
			
						String checkoutId= map2.get("checkoutId").toString();
						
						System.out.println("CheckoutId--->" +checkoutId);
						
						 Log.message("<b>==========================================================</b>");
						 Log.message("<i><b>   Validation for Create Payment API by ID API Started     </b></i>");
						 Log.message("<b>==========================================================</b>");
						 Log.message(i++ +".<b> Caputured the Create Payment API Response</b>");
						
						String url4 = "comcheckoutas/v1/checkouts/"+checkoutId+"/payments";
						String body3 = testData.get("jsonBody2");
						
						
						  System.out.println("Checkout Body = "+body3);
						  
						  System.out.println("Property URL = "+url4);
						  
						  Response response3 = rp.postRequest(headerMap,url4,body3);
						  
						  System.out.println("Checkout response = "+response3);
						  
						  Log.assertThat(response2.statusCode()==200, "Payment Status code is return as 200.", "Payment Status Code is return as not 200");
						  
						  HashMap<String, Object> map3 = new Gson().fromJson(response3.asString(), HashMap.class);
						  System.out.println("JSon via Size"+map3.size());
						  System.out.println(response2.getBody().prettyPrint());
						  
						  String tableBody3 =
								    map3.keySet()
								       .stream()
								       .map(item -> new StringBuilder("<tr><td>")
								                                     .append(item)
								                                     .append("</td><td>")
								                                     .append(String.valueOf(map3.get(item)))
								                                     .append("</td></tr>")
								       )
								       .collect(Collectors.joining());
								    
								    String tableContent3 = "<table>" + tableBody3 + "</table>";
								    System.out.println("tableContent = "+tableContent3);
								  Log.message(tableContent3);
								  
							Log.message("<b>==========================================================</b>");
							Log.message("<i><b>   Validation for Create Order API by ID API Started     </b></i>");
							Log.message("<b>==========================================================</b>");
							Log.message(i++ +".<b> Caputured the Create Order API Response</b>");
								  
						   String url5 = "comorderas/v1/orders";
						   String body4 = testData.get("jsonBody3").replace("+checkoutID",checkoutId);
						   
						   Response response4 = rp.postRequest(headerMap,url5,body4);
						   
						   System.out.println("Checkout response = "+response4);
							  
							  Log.assertThat(response4.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
							  
							  HashMap<String, Object> map4 = new Gson().fromJson(response4.asString(), HashMap.class);
							  System.out.println("JSon via Size"+map4.size());
							  System.out.println(response2.getBody().prettyPrint());
							  
							  String tableBody4 =
									    map4.keySet()
									       .stream()
									       .map(item -> new StringBuilder("<tr><td>")
									                                     .append(item)
									                                     .append("</td><td>")
									                                     .append(String.valueOf(map4.get(item)))
									                                     .append("</td></tr>")
									       )
									       .collect(Collectors.joining());
									    
									    String tableContent4 = "<table>" + tableBody4 + "</table>";
									    System.out.println("tableContent = "+tableContent4);
									    Log.message(tableContent4);
								
									    
						
			}
				
			catch (Exception e) {
				Log.exception(e);
			} // catch
			finally {
				Log.endTestCase();
			} // finally
		}

		@Test(enabled = false, description = "Testcase to verify the Complete journey of ABO customer with single product API and pickup at store and verify the Status Code and response", dataProviderClass = DataProviderUtils.class, dataProvider = "parallelTestDataProvider")
		public void TC_Amway_API_E2E_005(String browser) throws Exception {
			// ** Loading the test data from excel using the test case id */
			HashMap<String, String> testData = TestDataExtractor.initTestData(
					workbookName, sheetName);
			HashMap<String,String> headerMap = new HashMap<String,String>();
			HashMap<String,String> qparam = new HashMap<String,String>();
			qparam.put("projections", "DETAILED");
			Utils utils = new Utils();
			String rand1 = String.valueOf(utils.getRandom(1000, 100000));
			System.out.print("Random Value"+rand1);
			headerMap.put("X-Idempotency-Key",rand1);
			headerMap.put("Cookie", "session=xOjNmec1lwznXV_of-uZrvj8aaLnPFry_4xD4a6p-9c");
			headerMap.put("Accept-Language", "en-us");
			
			headerMap.put("X-Amway-Tenant", "200");
			headerMap.put("X-Amway-Vertical", "DEFAULT");
			
			headerMap.put("X-Amway-Country", "th");
			headerMap.put("X-Amway-Channel", "WEB");
			headerMap.put("Content-type", "application/json");
			String body1 = testData.get("jsonBody");
			String url1 = "comcartas/v1/carts";
			// Get the web driver instance
			int i = 1;
			try {
				
			   RestAssuredAPI rp = new RestAssuredAPI();
			   Log.message("<b>==========================================================</b>");
			   Log.message("<i><b>   Validation for create_cart_API  Started   </b></i>");
			   Log.message("<b>==========================================================</b>");
			   Log.message(i++ +".<b> Caputured the create cart id without body API Response</b>");
			   
			  Response response = rp.postRequest(headerMap,qparam,url1,body1);
			  			  
			  Log.assertThat(response.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  @SuppressWarnings("unchecked")
			
			  HashMap<String, Object> map = new Gson().fromJson(response.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map.size());
			  System.out.println(response.getBody().prettyPrint());
			  
			  String tableBody =
					    map.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent = "<table>" + tableBody + "</table>";
					    System.out.println("tableContent = "+tableContent);
					  Log.message(tableContent);
			  
			  String cartId = map.get("id").toString().trim();
			  System.out.println("Cart Id--->"+cartId);
			  
			  Log.message(i++ +".<b> Caputured the CartID--><b>" +  map.get("id").toString());
			  Log.assertThat(map.get("id").toString()!= null, "Cart Id is successfully Created", "Cart Id is not created Successfully");
			
			  String url2 = "comcartas/v1/carts/"+cartId+"/subCarts/"+cartId;
			  System.out.println("Get Url" + url2);
		
			  Log.message("<b>==========================================================</b>");
			  Log.message("<i><b>   Validation for get cart by ID API Started     </b></i>");
			  Log.message("<b>==========================================================</b>");
			  Log.message(i++ +".<b> Caputured the get cart id by id API Response</b>");
			  Response response1 = rp.getRequest(headerMap,url2);
			  System.out.println("Get Status Code" + response1.statusCode());
			  Log.assertThat(response1.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  
			  
			  HashMap<String, Object> map1 = new Gson().fromJson(response1.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map1.size());
			  System.out.println(response1.getBody().prettyPrint());
			  
			  String tableBody1 =
					    map1.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map1.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent1 = "<table>" + tableBody1 + "</table>";
					    System.out.println("tableContent = "+tableContent1);
					  Log.message(tableContent1);
					  System.out.println("Cart Id 1"+map.get("id").toString().trim());
					  System.out.println("Cart Id 2"+map1.get("id").toString().trim());
					  Log.assertThat(map.get("id").toString().trim().equals(map1.get("id").toString().trim()),"Get Cart API is displayed the same details of created cart via create cart API!", "Get Cart API is not displayed the same details of created cart via create cart API!");
					  
					  

					  Log.message("<b>==========================================================</b>");
					  Log.message("<i><b>   Validation for Create checkout API by ID API Started     </b></i>");
					  Log.message("<b>==========================================================</b>");
					  Log.message(i++ +".<b> Caputured the Create checkout API Response</b>");
					  
					  String url3 = "comcheckoutas/v1/checkouts";
					  
					  String body2 = testData.get("jsonBody1").replace("+cartId", cartId);
					  
					  System.out.println("Checkout Body = "+body2);
					  
					  System.out.println("Property URL = "+url3);
					  
					  Response response2 = rp.postRequest(headerMap,url3,body2);
					  
					  System.out.println("Checkout response = "+response2);
					  
					  Log.assertThat(response2.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
					  
					  HashMap<String, Object> map2 = new Gson().fromJson(response2.asString(), HashMap.class);
					  System.out.println("JSon via Size"+map2.size());
					  System.out.println(response2.getBody().prettyPrint());
					  
					  String tableBody2 =
							    map2.keySet()
							       .stream()
							       .map(item -> new StringBuilder("<tr><td>")
							                                     .append(item)
							                                     .append("</td><td>")
							                                     .append(String.valueOf(map2.get(item)))
							                                     .append("</td></tr>")
							       )
							       .collect(Collectors.joining());
							    
							    String tableContent2 = "<table>" + tableBody2 + "</table>";
							    System.out.println("tableContent = "+tableContent2);
							  Log.message(tableContent2);
					
						Log.assertThat(map.get("id").toString().trim().equals(map2.get("cartId").toString().trim()),"Checkout Cart API is displayed the same details of created cart via create cart API!", "Checkout Cart API is not displayed the same details of created cart via create cart API!");
						
						Log.assertThat(map2.get("checkoutId").toString()!= null, "Checkout Id is successfully Created", "Checkout Id is not created Successfully");
			
						String checkoutId= map2.get("checkoutId").toString();
						
						System.out.println("CheckoutId--->" +checkoutId);
						
						 Log.message("<b>==========================================================</b>");
						 Log.message("<i><b>   Validation for Create Payment API by ID API Started     </b></i>");
						 Log.message("<b>==========================================================</b>");
						 Log.message(i++ +".<b> Caputured the Create Payment API Response</b>");
						
						String url4 = "comcheckoutas/v1/checkouts/"+checkoutId+"/payments";
						String body3 = testData.get("jsonBody2");
						
						
						  System.out.println("Checkout Body = "+body3);
						  
						  System.out.println("Property URL = "+url4);
						  
						  Response response3 = rp.postRequest(headerMap,url4,body3);
						  
						  System.out.println("Checkout response = "+response3);
						  
						  Log.assertThat(response2.statusCode()==200, "Payment Status code is return as 200.", "Payment Status Code is return as not 200");
						  
						  HashMap<String, Object> map3 = new Gson().fromJson(response3.asString(), HashMap.class);
						  System.out.println("JSon via Size"+map3.size());
						  System.out.println(response2.getBody().prettyPrint());
						  
						  String tableBody3 =
								    map3.keySet()
								       .stream()
								       .map(item -> new StringBuilder("<tr><td>")
								                                     .append(item)
								                                     .append("</td><td>")
								                                     .append(String.valueOf(map3.get(item)))
								                                     .append("</td></tr>")
								       )
								       .collect(Collectors.joining());
								    
								    String tableContent3 = "<table>" + tableBody3 + "</table>";
								    System.out.println("tableContent = "+tableContent3);
								  Log.message(tableContent3);
								  
							Log.message("<b>==========================================================</b>");
							Log.message("<i><b>   Validation for Create Order API by ID API Started     </b></i>");
							Log.message("<b>==========================================================</b>");
							Log.message(i++ +".<b> Caputured the Create Order API Response</b>");
								  
						   String url5 = "comorderas/v1/orders";
						   String body4 = testData.get("jsonBody3").replace("+checkoutID",checkoutId);
						   
						   Response response4 = rp.postRequest(headerMap,url5,body4);
						   
						   System.out.println("Checkout response = "+response4);
							  
							  Log.assertThat(response4.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
							  
							  HashMap<String, Object> map4 = new Gson().fromJson(response4.asString(), HashMap.class);
							  System.out.println("JSon via Size"+map4.size());
							  System.out.println(response2.getBody().prettyPrint());
							  
							  String tableBody4 =
									    map4.keySet()
									       .stream()
									       .map(item -> new StringBuilder("<tr><td>")
									                                     .append(item)
									                                     .append("</td><td>")
									                                     .append(String.valueOf(map4.get(item)))
									                                     .append("</td></tr>")
									       )
									       .collect(Collectors.joining());
									    
									    String tableContent4 = "<table>" + tableBody4 + "</table>";
									    System.out.println("tableContent = "+tableContent4);
									    Log.message(tableContent4);
								
									    
						
			}
				
			catch (Exception e) {
				Log.exception(e);
			} // catch
			finally {
				Log.endTestCase();
			} // finally
		}

		@Test(enabled = false, description = "Testcase to verify the Complete journey of ABO customer with single product API and pickup at store and verify the Status Code and response", dataProviderClass = DataProviderUtils.class, dataProvider = "parallelTestDataProvider")
		public void TC_Amway_API_E2E_006(String browser) throws Exception {
			// ** Loading the test data from excel using the test case id */
			HashMap<String, String> testData = TestDataExtractor.initTestData(
					workbookName, sheetName);
			HashMap<String,String> headerMap = new HashMap<String,String>();
			HashMap<String,String> qparam = new HashMap<String,String>();
			qparam.put("projections", "DETAILED");
			Utils utils = new Utils();
			String rand1 = String.valueOf(utils.getRandom(1000, 100000));
			System.out.print("Random Value"+rand1);
			headerMap.put("X-Idempotency-Key",rand1);
			headerMap.put("Cookie", "session=xOjNmec1lwznXV_of-uZrvj8aaLnPFry_4xD4a6p-9c");
			headerMap.put("Accept-Language", "en-us");
			
			headerMap.put("X-Amway-Tenant", "200");
			headerMap.put("X-Amway-Vertical", "DEFAULT");
			
			headerMap.put("X-Amway-Country", "th");
			headerMap.put("X-Amway-Channel", "WEB");
			headerMap.put("Content-type", "application/json");
			String body1 = testData.get("jsonBody");
			String url1 = "comcartas/v1/carts";
			// Get the web driver instance
			int i = 1;
			try {
				
			   RestAssuredAPI rp = new RestAssuredAPI();
			   Log.message("<b>==========================================================</b>");
			   Log.message("<i><b>   Validation for create_cart_API  Started   </b></i>");
			   Log.message("<b>==========================================================</b>");
			   Log.message(i++ +".<b> Caputured the create cart id without body API Response</b>");
			   
			  Response response = rp.postRequest(headerMap,qparam,url1,body1);
			  			  
			  Log.assertThat(response.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  @SuppressWarnings("unchecked")
			
			  HashMap<String, Object> map = new Gson().fromJson(response.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map.size());
			  System.out.println(response.getBody().prettyPrint());
			  
			  String tableBody =
					    map.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent = "<table>" + tableBody + "</table>";
					    System.out.println("tableContent = "+tableContent);
					  Log.message(tableContent);
			  
			  String cartId = map.get("id").toString().trim();
			  System.out.println("Cart Id--->"+cartId);
			  
			  Log.message(i++ +".<b> Caputured the CartID--><b>" +  map.get("id").toString());
			  Log.assertThat(map.get("id").toString()!= null, "Cart Id is successfully Created", "Cart Id is not created Successfully");
			
			  String url2 = "comcartas/v1/carts/"+cartId+"/subCarts/"+cartId;
			  System.out.println("Get Url" + url2);
		
			  Log.message("<b>==========================================================</b>");
			  Log.message("<i><b>   Validation for get cart by ID API Started     </b></i>");
			  Log.message("<b>==========================================================</b>");
			  Log.message(i++ +".<b> Caputured the get cart id by id API Response</b>");
			  Response response1 = rp.getRequest(headerMap,url2);
			  System.out.println("Get Status Code" + response1.statusCode());
			  Log.assertThat(response1.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  
			  
			  HashMap<String, Object> map1 = new Gson().fromJson(response1.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map1.size());
			  System.out.println(response1.getBody().prettyPrint());
			  
			  String tableBody1 =
					    map1.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map1.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent1 = "<table>" + tableBody1 + "</table>";
					    System.out.println("tableContent = "+tableContent1);
					  Log.message(tableContent1);
					  System.out.println("Cart Id 1"+map.get("id").toString().trim());
					  System.out.println("Cart Id 2"+map1.get("id").toString().trim());
					  Log.assertThat(map.get("id").toString().trim().equals(map1.get("id").toString().trim()),"Get Cart API is displayed the same details of created cart via create cart API!", "Get Cart API is not displayed the same details of created cart via create cart API!");
					  
					  

					  Log.message("<b>==========================================================</b>");
					  Log.message("<i><b>   Validation for Create checkout API by ID API Started     </b></i>");
					  Log.message("<b>==========================================================</b>");
					  Log.message(i++ +".<b> Caputured the Create checkout API Response</b>");
					  
					  String url3 = "comcheckoutas/v1/checkouts";
					  
					  String body2 = testData.get("jsonBody1").replace("+cartId", cartId);
					  
					  System.out.println("Checkout Body = "+body2);
					  
					  System.out.println("Property URL = "+url3);
					  
					  Response response2 = rp.postRequest(headerMap,url3,body2);
					  
					  System.out.println("Checkout response = "+response2);
					  
					  Log.assertThat(response2.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
					  
					  HashMap<String, Object> map2 = new Gson().fromJson(response2.asString(), HashMap.class);
					  System.out.println("JSon via Size"+map2.size());
					  System.out.println(response2.getBody().prettyPrint());
					  
					  String tableBody2 =
							    map2.keySet()
							       .stream()
							       .map(item -> new StringBuilder("<tr><td>")
							                                     .append(item)
							                                     .append("</td><td>")
							                                     .append(String.valueOf(map2.get(item)))
							                                     .append("</td></tr>")
							       )
							       .collect(Collectors.joining());
							    
							    String tableContent2 = "<table>" + tableBody2 + "</table>";
							    System.out.println("tableContent = "+tableContent2);
							  Log.message(tableContent2);
					
						Log.assertThat(map.get("id").toString().trim().equals(map2.get("cartId").toString().trim()),"Checkout Cart API is displayed the same details of created cart via create cart API!", "Checkout Cart API is not displayed the same details of created cart via create cart API!");
						
						Log.assertThat(map2.get("checkoutId").toString()!= null, "Checkout Id is successfully Created", "Checkout Id is not created Successfully");
			
						String checkoutId= map2.get("checkoutId").toString();
						
						System.out.println("CheckoutId--->" +checkoutId);
						
						 Log.message("<b>==========================================================</b>");
						 Log.message("<i><b>   Validation for Create Payment API by ID API Started     </b></i>");
						 Log.message("<b>==========================================================</b>");
						 Log.message(i++ +".<b> Caputured the Create Payment API Response</b>");
						
						String url4 = "comcheckoutas/v1/checkouts/"+checkoutId+"/payments";
						String body3 = testData.get("jsonBody2");
						
						
						  System.out.println("Checkout Body = "+body3);
						  
						  System.out.println("Property URL = "+url4);
						  
						  Response response3 = rp.postRequest(headerMap,url4,body3);
						  
						  System.out.println("Checkout response = "+response3);
						  
						  Log.assertThat(response2.statusCode()==200, "Payment Status code is return as 200.", "Payment Status Code is return as not 200");
						  
						  HashMap<String, Object> map3 = new Gson().fromJson(response3.asString(), HashMap.class);
						  System.out.println("JSon via Size"+map3.size());
						  System.out.println(response2.getBody().prettyPrint());
						  
						  String tableBody3 =
								    map3.keySet()
								       .stream()
								       .map(item -> new StringBuilder("<tr><td>")
								                                     .append(item)
								                                     .append("</td><td>")
								                                     .append(String.valueOf(map3.get(item)))
								                                     .append("</td></tr>")
								       )
								       .collect(Collectors.joining());
								    
								    String tableContent3 = "<table>" + tableBody3 + "</table>";
								    System.out.println("tableContent = "+tableContent3);
								  Log.message(tableContent3);
								  
							Log.message("<b>==========================================================</b>");
							Log.message("<i><b>   Validation for Create Order API by ID API Started     </b></i>");
							Log.message("<b>==========================================================</b>");
							Log.message(i++ +".<b> Caputured the Create Order API Response</b>");
								  
						   String url5 = "comorderas/v1/orders";
						   String body4 = testData.get("jsonBody3").replace("+checkoutID",checkoutId);
						   
						   Response response4 = rp.postRequest(headerMap,url5,body4);
						   
						   System.out.println("Checkout response = "+response4);
							  
							  Log.assertThat(response4.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
							  
							  HashMap<String, Object> map4 = new Gson().fromJson(response4.asString(), HashMap.class);
							  System.out.println("JSon via Size"+map4.size());
							  System.out.println(response2.getBody().prettyPrint());
							  
							  String tableBody4 =
									    map4.keySet()
									       .stream()
									       .map(item -> new StringBuilder("<tr><td>")
									                                     .append(item)
									                                     .append("</td><td>")
									                                     .append(String.valueOf(map4.get(item)))
									                                     .append("</td></tr>")
									       )
									       .collect(Collectors.joining());
									    
									    String tableContent4 = "<table>" + tableBody4 + "</table>";
									    System.out.println("tableContent = "+tableContent4);
									    Log.message(tableContent4);
								
									    
						
			}
				
			catch (Exception e) {
				Log.exception(e);
			} // catch
			finally {
				Log.endTestCase();
			} // finally
		}

		@Test(enabled = false, description = "Testcase to verify the Complete journey of ABO customer with single product API and pickup at store and verify the Status Code and response", dataProviderClass = DataProviderUtils.class, dataProvider = "parallelTestDataProvider")
		public void TC_Amway_API_E2E_007(String browser) throws Exception {
			// ** Loading the test data from excel using the test case id */
			HashMap<String, String> testData = TestDataExtractor.initTestData(
					workbookName, sheetName);
			HashMap<String,String> headerMap = new HashMap<String,String>();
			HashMap<String,String> qparam = new HashMap<String,String>();
			qparam.put("projections", "DETAILED");
			Utils utils = new Utils();
			String rand1 = String.valueOf(utils.getRandom(1000, 100000));
			System.out.print("Random Value"+rand1);
			headerMap.put("X-Idempotency-Key",rand1);
			headerMap.put("Cookie", "session=xOjNmec1lwznXV_of-uZrvj8aaLnPFry_4xD4a6p-9c");
			headerMap.put("Accept-Language", "en-us");
			
			headerMap.put("X-Amway-Tenant", "200");
			headerMap.put("X-Amway-Vertical", "DEFAULT");
			
			headerMap.put("X-Amway-Country", "th");
			headerMap.put("X-Amway-Channel", "WEB");
			headerMap.put("Content-type", "application/json");
			String body1 = testData.get("jsonBody");
			String url1 = "comcartas/v1/carts";
			// Get the web driver instance
			int i = 1;
			try {
				
			   RestAssuredAPI rp = new RestAssuredAPI();
			   Log.message("<b>==========================================================</b>");
			   Log.message("<i><b>   Validation for create_cart_API  Started   </b></i>");
			   Log.message("<b>==========================================================</b>");
			   Log.message(i++ +".<b> Caputured the create cart id without body API Response</b>");
			   
			  Response response = rp.postRequest(headerMap,qparam,url1,body1);
			  			  
			  Log.assertThat(response.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  @SuppressWarnings("unchecked")
			
			  HashMap<String, Object> map = new Gson().fromJson(response.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map.size());
			  System.out.println(response.getBody().prettyPrint());
			  
			  String tableBody =
					    map.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent = "<table>" + tableBody + "</table>";
					    System.out.println("tableContent = "+tableContent);
					  Log.message(tableContent);
			  
			  String cartId = map.get("id").toString().trim();
			  System.out.println("Cart Id--->"+cartId);
			  
			  Log.message(i++ +".<b> Caputured the CartID--><b>" +  map.get("id").toString());
			  Log.assertThat(map.get("id").toString()!= null, "Cart Id is successfully Created", "Cart Id is not created Successfully");
			
			  String url2 = "comcartas/v1/carts/"+cartId+"/subCarts/"+cartId;
			  System.out.println("Get Url" + url2);
		
			  Log.message("<b>==========================================================</b>");
			  Log.message("<i><b>   Validation for get cart by ID API Started     </b></i>");
			  Log.message("<b>==========================================================</b>");
			  Log.message(i++ +".<b> Caputured the get cart id by id API Response</b>");
			  Response response1 = rp.getRequest(headerMap,url2);
			  System.out.println("Get Status Code" + response1.statusCode());
			  Log.assertThat(response1.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  
			  
			  HashMap<String, Object> map1 = new Gson().fromJson(response1.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map1.size());
			  System.out.println(response1.getBody().prettyPrint());
			  
			  String tableBody1 =
					    map1.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map1.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent1 = "<table>" + tableBody1 + "</table>";
					    System.out.println("tableContent = "+tableContent1);
					  Log.message(tableContent1);
					  System.out.println("Cart Id 1"+map.get("id").toString().trim());
					  System.out.println("Cart Id 2"+map1.get("id").toString().trim());
					  Log.assertThat(map.get("id").toString().trim().equals(map1.get("id").toString().trim()),"Get Cart API is displayed the same details of created cart via create cart API!", "Get Cart API is not displayed the same details of created cart via create cart API!");
					  
					  

					  Log.message("<b>==========================================================</b>");
					  Log.message("<i><b>   Validation for Create checkout API by ID API Started     </b></i>");
					  Log.message("<b>==========================================================</b>");
					  Log.message(i++ +".<b> Caputured the Create checkout API Response</b>");
					  
					  String url3 = "comcheckoutas/v1/checkouts";
					  
					  String body2 = testData.get("jsonBody1").replace("+cartId", cartId);
					  
					  System.out.println("Checkout Body = "+body2);
					  
					  System.out.println("Property URL = "+url3);
					  
					  Response response2 = rp.postRequest(headerMap,url3,body2);
					  
					  System.out.println("Checkout response = "+response2);
					  
					  Log.assertThat(response2.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
					  
					  HashMap<String, Object> map2 = new Gson().fromJson(response2.asString(), HashMap.class);
					  System.out.println("JSon via Size"+map2.size());
					  System.out.println(response2.getBody().prettyPrint());
					  
					  String tableBody2 =
							    map2.keySet()
							       .stream()
							       .map(item -> new StringBuilder("<tr><td>")
							                                     .append(item)
							                                     .append("</td><td>")
							                                     .append(String.valueOf(map2.get(item)))
							                                     .append("</td></tr>")
							       )
							       .collect(Collectors.joining());
							    
							    String tableContent2 = "<table>" + tableBody2 + "</table>";
							    System.out.println("tableContent = "+tableContent2);
							  Log.message(tableContent2);
					
						Log.assertThat(map.get("id").toString().trim().equals(map2.get("cartId").toString().trim()),"Checkout Cart API is displayed the same details of created cart via create cart API!", "Checkout Cart API is not displayed the same details of created cart via create cart API!");
						
						Log.assertThat(map2.get("checkoutId").toString()!= null, "Checkout Id is successfully Created", "Checkout Id is not created Successfully");
			
						String checkoutId= map2.get("checkoutId").toString();
						
						System.out.println("CheckoutId--->" +checkoutId);
						
						 Log.message("<b>==========================================================</b>");
						 Log.message("<i><b>   Validation for Create Payment API by ID API Started     </b></i>");
						 Log.message("<b>==========================================================</b>");
						 Log.message(i++ +".<b> Caputured the Create Payment API Response</b>");
						
						String url4 = "comcheckoutas/v1/checkouts/"+checkoutId+"/payments";
						String body3 = testData.get("jsonBody2");
						
						
						  System.out.println("Checkout Body = "+body3);
						  
						  System.out.println("Property URL = "+url4);
						  
						  Response response3 = rp.postRequest(headerMap,url4,body3);
						  
						  System.out.println("Checkout response = "+response3);
						  
						  Log.assertThat(response2.statusCode()==200, "Payment Status code is return as 200.", "Payment Status Code is return as not 200");
						  
						  HashMap<String, Object> map3 = new Gson().fromJson(response3.asString(), HashMap.class);
						  System.out.println("JSon via Size"+map3.size());
						  System.out.println(response2.getBody().prettyPrint());
						  
						  String tableBody3 =
								    map3.keySet()
								       .stream()
								       .map(item -> new StringBuilder("<tr><td>")
								                                     .append(item)
								                                     .append("</td><td>")
								                                     .append(String.valueOf(map3.get(item)))
								                                     .append("</td></tr>")
								       )
								       .collect(Collectors.joining());
								    
								    String tableContent3 = "<table>" + tableBody3 + "</table>";
								    System.out.println("tableContent = "+tableContent3);
								  Log.message(tableContent3);
								  
							Log.message("<b>==========================================================</b>");
							Log.message("<i><b>   Validation for Create Order API by ID API Started     </b></i>");
							Log.message("<b>==========================================================</b>");
							Log.message(i++ +".<b> Caputured the Create Order API Response</b>");
								  
						   String url5 = "comorderas/v1/orders";
						   String body4 = testData.get("jsonBody3").replace("+checkoutID",checkoutId);
						   
						   Response response4 = rp.postRequest(headerMap,url5,body4);
						   
						   System.out.println("Checkout response = "+response4);
							  
							  Log.assertThat(response4.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
							  
							  HashMap<String, Object> map4 = new Gson().fromJson(response4.asString(), HashMap.class);
							  System.out.println("JSon via Size"+map4.size());
							  System.out.println(response2.getBody().prettyPrint());
							  
							  String tableBody4 =
									    map4.keySet()
									       .stream()
									       .map(item -> new StringBuilder("<tr><td>")
									                                     .append(item)
									                                     .append("</td><td>")
									                                     .append(String.valueOf(map4.get(item)))
									                                     .append("</td></tr>")
									       )
									       .collect(Collectors.joining());
									    
									    String tableContent4 = "<table>" + tableBody4 + "</table>";
									    System.out.println("tableContent = "+tableContent4);
									    Log.message(tableContent4);
								
									    
						
			}
				
			catch (Exception e) {
				Log.exception(e);
			} // catch
			finally {
				Log.endTestCase();
			} // finally
		}

		@Test(enabled = false, description = "Testcase to verify the Complete journey of ABO customer with single product API and pickup at store and verify the Status Code and response", dataProviderClass = DataProviderUtils.class, dataProvider = "parallelTestDataProvider")
		public void TC_Amway_API_E2E_008(String browser) throws Exception {
			// ** Loading the test data from excel using the test case id */
			HashMap<String, String> testData = TestDataExtractor.initTestData(
					workbookName, sheetName);
			HashMap<String,String> headerMap = new HashMap<String,String>();
			HashMap<String,String> qparam = new HashMap<String,String>();
			qparam.put("projections", "DETAILED");
			Utils utils = new Utils();
			String rand1 = String.valueOf(utils.getRandom(1000, 100000));
			System.out.print("Random Value"+rand1);
			headerMap.put("X-Idempotency-Key",rand1);
			headerMap.put("Cookie", "session=xOjNmec1lwznXV_of-uZrvj8aaLnPFry_4xD4a6p-9c");
			headerMap.put("Accept-Language", "en-us");
			
			headerMap.put("X-Amway-Tenant", "200");
			headerMap.put("X-Amway-Vertical", "DEFAULT");
			
			headerMap.put("X-Amway-Country", "th");
			headerMap.put("X-Amway-Channel", "WEB");
			headerMap.put("Content-type", "application/json");
			String body1 = testData.get("jsonBody");
			String url1 = "comcartas/v1/carts";
			// Get the web driver instance
			int i = 1;
			try {
				
			   RestAssuredAPI rp = new RestAssuredAPI();
			   Log.message("<b>==========================================================</b>");
			   Log.message("<i><b>   Validation for create_cart_API  Started   </b></i>");
			   Log.message("<b>==========================================================</b>");
			   Log.message(i++ +".<b> Caputured the create cart id without body API Response</b>");
			   
			  Response response = rp.postRequest(headerMap,qparam,url1,body1);
			  			  
			  Log.assertThat(response.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  @SuppressWarnings("unchecked")
			
			  HashMap<String, Object> map = new Gson().fromJson(response.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map.size());
			  System.out.println(response.getBody().prettyPrint());
			  
			  String tableBody =
					    map.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent = "<table>" + tableBody + "</table>";
					    System.out.println("tableContent = "+tableContent);
					  Log.message(tableContent);
			  
			  String cartId = map.get("id").toString().trim();
			  System.out.println("Cart Id--->"+cartId);
			  
			  Log.message(i++ +".<b> Caputured the CartID--><b>" +  map.get("id").toString());
			  Log.assertThat(map.get("id").toString()!= null, "Cart Id is successfully Created", "Cart Id is not created Successfully");
			
			  String url2 = "comcartas/v1/carts/"+cartId+"/subCarts/"+cartId;
			  System.out.println("Get Url" + url2);
		
			  Log.message("<b>==========================================================</b>");
			  Log.message("<i><b>   Validation for get cart by ID API Started     </b></i>");
			  Log.message("<b>==========================================================</b>");
			  Log.message(i++ +".<b> Caputured the get cart id by id API Response</b>");
			  Response response1 = rp.getRequest(headerMap,url2);
			  System.out.println("Get Status Code" + response1.statusCode());
			  Log.assertThat(response1.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
			  
			  
			  HashMap<String, Object> map1 = new Gson().fromJson(response1.asString(), HashMap.class);
			  System.out.println("JSon via Size"+map1.size());
			  System.out.println(response1.getBody().prettyPrint());
			  
			  String tableBody1 =
					    map1.keySet()
					       .stream()
					       .map(item -> new StringBuilder("<tr><td>")
					                                     .append(item)
					                                     .append("</td><td>")
					                                     .append(String.valueOf(map1.get(item)))
					                                     .append("</td></tr>")
					       )
					       .collect(Collectors.joining());
					    
					    String tableContent1 = "<table>" + tableBody1 + "</table>";
					    System.out.println("tableContent = "+tableContent1);
					  Log.message(tableContent1);
					  System.out.println("Cart Id 1"+map.get("id").toString().trim());
					  System.out.println("Cart Id 2"+map1.get("id").toString().trim());
					  Log.assertThat(map.get("id").toString().trim().equals(map1.get("id").toString().trim()),"Get Cart API is displayed the same details of created cart via create cart API!", "Get Cart API is not displayed the same details of created cart via create cart API!");
					  
					  

					  Log.message("<b>==========================================================</b>");
					  Log.message("<i><b>   Validation for Create checkout API by ID API Started     </b></i>");
					  Log.message("<b>==========================================================</b>");
					  Log.message(i++ +".<b> Caputured the Create checkout API Response</b>");
					  
					  String url3 = "comcheckoutas/v1/checkouts";
					  
					  String body2 = testData.get("jsonBody1").replace("+cartId", cartId);
					  
					  System.out.println("Checkout Body = "+body2);
					  
					  System.out.println("Property URL = "+url3);
					  
					  Response response2 = rp.postRequest(headerMap,url3,body2);
					  
					  System.out.println("Checkout response = "+response2);
					  
					  Log.assertThat(response2.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
					  
					  HashMap<String, Object> map2 = new Gson().fromJson(response2.asString(), HashMap.class);
					  System.out.println("JSon via Size"+map2.size());
					  System.out.println(response2.getBody().prettyPrint());
					  
					  String tableBody2 =
							    map2.keySet()
							       .stream()
							       .map(item -> new StringBuilder("<tr><td>")
							                                     .append(item)
							                                     .append("</td><td>")
							                                     .append(String.valueOf(map2.get(item)))
							                                     .append("</td></tr>")
							       )
							       .collect(Collectors.joining());
							    
							    String tableContent2 = "<table>" + tableBody2 + "</table>";
							    System.out.println("tableContent = "+tableContent2);
							  Log.message(tableContent2);
					
						Log.assertThat(map.get("id").toString().trim().equals(map2.get("cartId").toString().trim()),"Checkout Cart API is displayed the same details of created cart via create cart API!", "Checkout Cart API is not displayed the same details of created cart via create cart API!");
						
						Log.assertThat(map2.get("checkoutId").toString()!= null, "Checkout Id is successfully Created", "Checkout Id is not created Successfully");
			
						String checkoutId= map2.get("checkoutId").toString();
						
						System.out.println("CheckoutId--->" +checkoutId);
						
						 Log.message("<b>==========================================================</b>");
						 Log.message("<i><b>   Validation for Create Payment API by ID API Started     </b></i>");
						 Log.message("<b>==========================================================</b>");
						 Log.message(i++ +".<b> Caputured the Create Payment API Response</b>");
						
						String url4 = "comcheckoutas/v1/checkouts/"+checkoutId+"/payments";
						String body3 = testData.get("jsonBody2");
						
						
						  System.out.println("Checkout Body = "+body3);
						  
						  System.out.println("Property URL = "+url4);
						  
						  Response response3 = rp.postRequest(headerMap,url4,body3);
						  
						  System.out.println("Checkout response = "+response3);
						  
						  Log.assertThat(response2.statusCode()==200, "Payment Status code is return as 200.", "Payment Status Code is return as not 200");
						  
						  HashMap<String, Object> map3 = new Gson().fromJson(response3.asString(), HashMap.class);
						  System.out.println("JSon via Size"+map3.size());
						  System.out.println(response2.getBody().prettyPrint());
						  
						  String tableBody3 =
								    map3.keySet()
								       .stream()
								       .map(item -> new StringBuilder("<tr><td>")
								                                     .append(item)
								                                     .append("</td><td>")
								                                     .append(String.valueOf(map3.get(item)))
								                                     .append("</td></tr>")
								       )
								       .collect(Collectors.joining());
								    
								    String tableContent3 = "<table>" + tableBody3 + "</table>";
								    System.out.println("tableContent = "+tableContent3);
								  Log.message(tableContent3);
								  
							Log.message("<b>==========================================================</b>");
							Log.message("<i><b>   Validation for Create Order API by ID API Started     </b></i>");
							Log.message("<b>==========================================================</b>");
							Log.message(i++ +".<b> Caputured the Create Order API Response</b>");
								  
						   String url5 = "comorderas/v1/orders";
						   String body4 = testData.get("jsonBody3").replace("+checkoutID",checkoutId);
						   
						   Response response4 = rp.postRequest(headerMap,url5,body4);
						   
						   System.out.println("Checkout response = "+response4);
							  
							  Log.assertThat(response4.statusCode()==200, "Status code is return as 200.", "Status Code is return as not 200");
							  
							  HashMap<String, Object> map4 = new Gson().fromJson(response4.asString(), HashMap.class);
							  System.out.println("JSon via Size"+map4.size());
							  System.out.println(response2.getBody().prettyPrint());
							  
							  String tableBody4 =
									    map4.keySet()
									       .stream()
									       .map(item -> new StringBuilder("<tr><td>")
									                                     .append(item)
									                                     .append("</td><td>")
									                                     .append(String.valueOf(map4.get(item)))
									                                     .append("</td></tr>")
									       )
									       .collect(Collectors.joining());
									    
									    String tableContent4 = "<table>" + tableBody4 + "</table>";
									    System.out.println("tableContent = "+tableContent4);
									    Log.message(tableContent4);
								
									    
						
			}
				
			catch (Exception e) {
				Log.exception(e);
			} // catch
			finally {
				Log.endTestCase();
			} // finally
		}



}
	
