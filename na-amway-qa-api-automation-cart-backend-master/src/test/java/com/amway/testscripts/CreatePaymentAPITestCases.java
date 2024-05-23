package com.amway.testscripts;

import java.util.ArrayList;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amway.api.utilities.CreatePaymentUtility;
import com.amway.pages.BankTransferPage;
import com.amway.pages.CreditCardPage;
import com.amway.pojo.response.CreatePaymentExternalResponseDTO;
import com.amway.pojo.response.CreatePaymentResponseDTO;
import com.amway.support.DataProviderUtils;
import com.amway.support.EmailReport;
import com.amway.support.Log;
import com.amway.support.RestAssuredAPI;
import com.amway.support.Utils;
import com.amway.support.WebDriverFactory;
import com.amway.tdata.TData.Payment;

import io.restassured.response.Response;

@Listeners(EmailReport.class)
public class CreatePaymentAPITestCases extends RestAssuredAPI {

	@Test(enabled = true, description = "Testcase to verify the create payment api for payment for credit card", dataProviderClass = DataProviderUtils.class, dataProvider = "APIData")

	public void tcAmwayAPICreditCardCreatePayment(String refType, String currency, String amount,
			String paymentMethodProviderId, String paymentMethodCode, String entryType, String accountId,
			String profileId) throws Exception {
		boolean result = false;

		try {

			RestAssuredAPI rp = new RestAssuredAPI();
			Log.message("<b>==========================================================</b>");
			Log.message("<i><b>   Validation for create_payment_API using credit card Started" + "</b></i>");
			Log.message("<b>==========================================================</b>");
			String refId = String.valueOf(Utils.getRandom(1000, 100000));
			CreatePaymentUtility createPaymentUtilityObj = new CreatePaymentUtility();
			Response response = createPaymentUtilityObj.performCreatePayment(refId, refType, currency,
					Integer.parseInt(amount), paymentMethodProviderId, paymentMethodCode, entryType, accountId,
					profileId);

			int actualResultStatusCode = response.statusCode();
			Log.assertThat(actualResultStatusCode == 200, "Status code is return as 200.",
					"Status Code is return as not 200, Actual result : " + actualResultStatusCode);
			if (actualResultStatusCode == 200) {
				io.restassured.path.json.JsonPath js = createPaymentUtilityObj.getJsonPathObject(response);
				String paymentId = js.get("paymentId");
				CreatePaymentExternalResponseDTO paymentResponeDto = response
						.as(CreatePaymentExternalResponseDTO.class);
				String websiteUrl = paymentResponeDto.getTransactions().get(0).getEntries().get(0)
						.getPayload().webPaymentUrl;

				final WebDriver driver = WebDriverFactory.get(browser);

				CreditCardPage cardPage = new CreditCardPage(driver, websiteUrl).get();
				cardPage.enterCreditCardDetails();
				cardPage.enterOtp();

				driver.close();

				// once otp done check status is CAPTUERED OR NOT
				Log.message("<i><b>   Validation for Get Payment API usin payment ID" + "</b></i>");
				Thread.sleep(2000);
				Response resPaymentData = createPaymentUtilityObj.performGetPaymentByPaymentId(paymentId);
				io.restassured.path.json.JsonPath paymentJs = createPaymentUtilityObj.getJsonPathObject(resPaymentData);
				Log.assertThat(resPaymentData.statusCode() == 200, "Status code is return as 200.",
						"Status Code is return as not 200, Actual result : " + resPaymentData.statusCode());
				if (resPaymentData.statusCode() == 200) {
					String status = paymentJs.get("status");

					Log.assertThat(status.equals("CAPTURED"), "Status code is return as CAPTURED.",
							"Status Code is return as not CAPTURED, Actual result : " + status);
				}

			}

		}

		catch (Exception e) {
			Log.exception(e);
		} // catch
		finally {
			Log.endTestCase();
		} // finally
	}
	
	
	
	@Test(enabled = true, description = "Testcase to verify the create payment api for Online Bank Transfer", dataProviderClass = DataProviderUtils.class, dataProvider = "APIData")

	public void tcAmwayAPIOnlineBankTransferCreatePayment(String refType, String currency, String amount,
			String paymentMethodProviderId, String paymentMethodCode, String entryType, String accountId,
			String profileId) throws Exception {
		boolean result = false;

		try {

			RestAssuredAPI rp = new RestAssuredAPI();
			Log.message("<b>==========================================================</b>");
			Log.message("<i><b>   Validation for create_payment_API using Online Bank Transfer" + "</b></i>");
			Log.message("<b>==========================================================</b>");
			String refId = String.valueOf(Utils.getRandom(1000, 100000));
			CreatePaymentUtility createPaymentUtilityObj = new CreatePaymentUtility();
			Response response = createPaymentUtilityObj.performCreatePayment(refId, refType, currency,
					Integer.parseInt(amount), paymentMethodProviderId, paymentMethodCode, entryType, accountId,
					profileId);

			int actualResultStatusCode = response.statusCode();
			Log.assertThat(actualResultStatusCode == 200, "Status code is return as 200.",
					"Status Code is return as not 200, Actual result : " + actualResultStatusCode);
			if (actualResultStatusCode == 200) {
				io.restassured.path.json.JsonPath js = createPaymentUtilityObj.getJsonPathObject(response);
				String paymentId = js.get("paymentId");
				CreatePaymentExternalResponseDTO paymentResponeDto = response
						.as(CreatePaymentExternalResponseDTO.class);
				String websiteUrl = paymentResponeDto.getTransactions().get(0).getEntries().get(0)
						.getPayload().webPaymentUrl;

				final WebDriver driver = WebDriverFactory.get(browser);

				 BankTransferPage btransfer = new BankTransferPage(driver);
				 driver.get(websiteUrl);
				 btransfer.bankTranser();
				

				driver.close();

				// once otp done check status is CAPTUERED OR NOT
				Log.message("<i><b>   Validation for Get Payment API usin payment ID" + "</b></i>");
				Thread.sleep(2000);
				Response resPaymentData = createPaymentUtilityObj.performGetPaymentByPaymentId(paymentId);
				io.restassured.path.json.JsonPath paymentJs = createPaymentUtilityObj.getJsonPathObject(resPaymentData);
				Log.assertThat(resPaymentData.statusCode() == 200, "Status code is return as 200.",
						"Status Code is return as not 200, Actual result : " + resPaymentData.statusCode());
				if (resPaymentData.statusCode() == 200) {
					String status = paymentJs.get("status");

					Log.assertThat(status.equals("CAPTURED"), "Status code is return as CAPTURED.",
							"Status Code is return as not CAPTURED, Actual result : " + status);
				}

			}

		}

		catch (Exception e) {
			Log.exception(e);
		} // catch
		finally {
			Log.endTestCase();
		} // finally
	}
	

	@Test(enabled = true, description = "Testcase to verify the create payment using split method", dataProviderClass = DataProviderUtils.class, dataProvider = "APIData")

	public void tcAmwayAPISplitAmpurseOBTCreatePayment(String refType, String currency, String totalAmount,String accountId,
			String profileId,String obtPaymentMethodProviderId, String obtPaymentMethodCode, String obtEntryType,String obtAmount,String ampursePaymentMethodProviderId, String ampursePaymentMethodCode, String ampurseEntryType,String ampurseAmount ) throws Exception {
		boolean result = false;

		try {

 			RestAssuredAPI rp = new RestAssuredAPI();
			Log.message("<b>==========================================================</b>");
			Log.message("<i><b>   Validation for create_payment_API using split payment" + "</b></i>");
			Log.message("<b>==========================================================</b>");
			String refId = String.valueOf(Utils.getRandom(1000, 100000));
			CreatePaymentUtility createPaymentUtilityObj = new CreatePaymentUtility();
			Response response = createPaymentUtilityObj.performSplitCreatePayment(refId, refType, currency,
					Integer.parseInt(totalAmount), obtPaymentMethodProviderId, obtPaymentMethodCode, obtEntryType,ampursePaymentMethodProviderId, ampursePaymentMethodCode, ampurseEntryType, accountId,
					profileId,Integer.parseInt(obtAmount),Integer.parseInt(ampurseAmount));

			int actualResultStatusCode = response.statusCode();
			Log.assertThat(actualResultStatusCode == 200, "Status code is return as 200.",
					"Status Code is return as not 200, Actual result : " + actualResultStatusCode);
			if (actualResultStatusCode == 200) {
				io.restassured.path.json.JsonPath js = createPaymentUtilityObj.getJsonPathObject(response);
				String paymentId = js.get("paymentId");
				CreatePaymentExternalResponseDTO paymentResponeDto = response
						.as(CreatePaymentExternalResponseDTO.class);
				String websiteUrl = paymentResponeDto.getTransactions().get(1).getEntries().get(0)
						.getPayload().webPaymentUrl;

				final WebDriver driver = WebDriverFactory.get(browser);

				 BankTransferPage btransfer = new BankTransferPage(driver);
				 driver.get(websiteUrl);
				 btransfer.bankTranser();
				

				driver.close();

				// once otp done check status is CAPTUERED OR NOT
				Log.message("<i><b>   Validation for Get Payment status using for split payment(Ampurse,OBT) " + "</b></i>");
				Thread.sleep(2000);
				Response resPaymentData = createPaymentUtilityObj.performGetPaymentByPaymentId(paymentId);
				io.restassured.path.json.JsonPath paymentJs = createPaymentUtilityObj.getJsonPathObject(resPaymentData);
				Log.assertThat(resPaymentData.statusCode() == 200, "Status code is return as 200.",
						"Status Code is return as not 200, Actual result : " + resPaymentData.statusCode());
				if (resPaymentData.statusCode() == 200) {
					String status = paymentJs.get("status");

					Log.assertThat(status.equals("CAPTURED"), "Status code is return as CAPTURED.",
							"Status Code is return as not CAPTURED, Actual result : " + status);
				}

			}

		}

		catch (Exception e) {
			Log.exception(e);
		} // catch
		finally {
			Log.endTestCase();
		} // finally
	}


	@Test(enabled = true, description = "Testcase to verify the create payement api for ampurse", dataProviderClass = DataProviderUtils.class, dataProvider = "APIData")
	public void tcAmwayAPIAmpurseCreatePayment(String refType, String currency, String amount,
			String paymentMethodProviderId, String paymentMethodCode, String entryType, String accountId,
			String profileId) throws Exception {
		boolean result = false;
		Integer callStatus = 0;

		try {

			RestAssuredAPI rp = new RestAssuredAPI();

			Log.message("<b>==========================================================</b>");
			Log.message("<i><b>   Validation for create Ampurse_payment_API  Started" + "</b></i>");
			Log.message("<b>==========================================================</b>");

			CreatePaymentUtility createPaymentUtilityObj = new CreatePaymentUtility();
			Response getResponse = createPaymentUtilityObj.performGetBal(accountId, Payment.AMPURESE_ACCOUNT_TYPE);

			// String res = getResponse.asString();
			// io.restassured.path.json.JsonPath js = new
			// io.restassured.path.json.JsonPath(res);

			io.restassured.path.json.JsonPath js = createPaymentUtilityObj.getJsonPathObject(getResponse);
			callStatus = getResponse.statusCode();
			Log.assertThat(callStatus == 200, "Get Balance Executed Successfully as status code return as 200.",
					"Status Code is return as not 200, Actual result : " + callStatus);
			if (callStatus == 200) {
				String  ab = js.get("availableBalance").toString().replaceAll("\\[|\\]", "");
				Float balance = Float.parseFloat(ab);
//				String balance = ab.get(0).toString();
//				Double balance = Double.parseDouble(s);

//				Double balance =  ab.get(0);

				System.out.println("-------------------Current Available Balance--------------------" + balance);

				if (balance == 0 || balance < Integer.parseInt(amount)) {

					System.out.println("--------------------------NO/Low Balance Available---------------------------");
					Log.message("<i><b>   call Api to add balance  Started" + "</b></i>");

					String rfid = String.valueOf(Utils.getRandom(1000, 100000));
					Response addBalresponse = createPaymentUtilityObj.performAddBalance(accountId,
							Payment.AMPURESE_ACCOUNT_TYPE, currency, Payment.ENTRY_TYPE_CREDIT_BALANCE,
							Payment.STORE_BLANCE, rfid);
					callStatus = addBalresponse.statusCode();

					Log.assertThat(callStatus == 200, "Balance added successfully  as status code is return as 200.",
							"Status Code is return as not 200, Actual result : " + callStatus);

				}
				// create payment

				if (callStatus == 200) {
					String rfidCreate = String.valueOf(Utils.getRandom(1000, 100000));
					Response response = createPaymentUtilityObj.performWalletCreatePayment(rfidCreate, refType,
							currency, Integer.parseInt(amount), paymentMethodProviderId, paymentMethodCode, entryType,
							accountId, profileId);

					CreatePaymentExternalResponseDTO createPaymentResponseDTO = response.as(CreatePaymentExternalResponseDTO.class);
					int actualResultStatusCode = response.statusCode();
					Log.assertThat(actualResultStatusCode == 200,
							"Create Payment for Ampurse executed successfully as code is return as 200.",
							"Status Code is return as not 200 for create payment, Actual result : "
									+ actualResultStatusCode);
					String transStatus = createPaymentResponseDTO.getTransactions().get(0).getStatus();

					Log.assertThat(transStatus.equals("CAPTURED"),
							"transaction Status is CAPTURED. for create payment.",
							"Status is not CAPTURED, Actual result : " + transStatus);

					Log.assertThat(
							createPaymentResponseDTO.paymentId != null && !createPaymentResponseDTO.paymentId.isEmpty(),
							"Payment Id created successfully", "Payment id has not generated.");

				}

			}

		}

		catch (Exception e) {
			Log.exception(e);
		} // catch
		finally {
			Log.endTestCase();
		} // finally
	}

	@Test(enabled = true, description = "Testcase to verify the create payement api for Credit Voucher", dataProviderClass = DataProviderUtils.class, dataProvider = "APIData")
	public void tcAmwayAPICreditVoucherCreatePayment(String refType, String currency, String amount,
			String paymentMethodProviderId, String paymentMethodCode, String entryType, String accountId,
			String profileId) throws Exception {
		boolean result = false;
		Integer callStatus = 0;

		try {

			RestAssuredAPI rp = new RestAssuredAPI();

			Log.message("<b>==========================================================</b>");
			Log.message("<i><b>   Validation for create payment from credit Voucher  Started" + "</b></i>");
			Log.message("<b>==========================================================</b>");

			CreatePaymentUtility createPaymentUtilityObj = new CreatePaymentUtility();
			Response getResponse = createPaymentUtilityObj.performGetBal(accountId,
					Payment.CREDIT_VOUCHER_ACCOUNT_TYPE);

			String res = getResponse.asString();
			io.restassured.path.json.JsonPath js = new io.restassured.path.json.JsonPath(res);
			callStatus = getResponse.statusCode();
			Log.assertThat(callStatus == 200, "Status code is return as 200.",
					"Status Code is return as not 200, Actual result : " + callStatus);
			if (callStatus == 200) {
				ArrayList<Integer> ab = js.get("availableBalance");
				Integer balance = (Integer) ab.get(0);

				System.out.println("-------------------Current Available Balance--------------------" + balance);

				if (balance == 0 || balance < Integer.parseInt(amount)) {

					System.out.println("--------------------------NO/Low Balance Available---------------------------");
					Log.message("<i><b>   call Api to add balance  Started" + "</b></i>");

					String rfid = String.valueOf(Utils.getRandom(1000, 100000));
					Response addBalresponse = createPaymentUtilityObj.performAddBalance(accountId,
							Payment.CREDIT_VOUCHER_ACCOUNT_TYPE, currency, Payment.ENTRY_TYPE_CREDIT_BALANCE,
							Payment.STORE_BLANCE, rfid);
					callStatus = addBalresponse.statusCode();

					Log.assertThat(callStatus == 200, "Status code is return as 200.",
							"Status Code is return as not 200, Actual result : " + callStatus);

				}
				// create payment
				if (callStatus == 200) {
					String rfidCreate = String.valueOf(Utils.getRandom(1000, 100000));
					Response response = createPaymentUtilityObj.performWalletCreatePayment(rfidCreate, refType,
							currency, Integer.parseInt(amount), paymentMethodProviderId, paymentMethodCode, entryType,
							accountId, profileId);
					CreatePaymentExternalResponseDTO createPaymentResponseDTO = response.as(CreatePaymentExternalResponseDTO.class);
					int actualResultStatusCode = response.statusCode();
					Log.assertThat(actualResultStatusCode == 200,
							"Create Payment for Credit voucher executed successfully as code is return as 200.",
							"Status Code is return as not 200 for create payment, Actual result : "
									+ actualResultStatusCode);
					String transStatus = createPaymentResponseDTO.getTransactions().get(0).getStatus();

					Log.assertThat(transStatus.equals("CAPTURED"),
							"transaction Status is CAPTURED. for create payment credit voucher",
							"Status is not CAPTURED for credit voucher, Actual result : " + transStatus);

					Log.assertThat(
							createPaymentResponseDTO.paymentId != null && !createPaymentResponseDTO.paymentId.isEmpty(),
							"Payment Id created successfully  for credit voucher",
							"Payment id has not generated for credit voucher.");

				}

			}

		}

		catch (Exception e) {
			Log.exception(e);
		} // catch
		finally {
			Log.endTestCase();
		} // finally
	}

	@Test(enabled = true, description = "Testcase to verify the create payement api for Ampoints", dataProviderClass = DataProviderUtils.class, dataProvider = "APIData")
	public void tcAmwayAPIAmpointsCreatePayment(String refType, String currency, String amount,
			String paymentMethodProviderId, String paymentMethodCode, String entryType, String accountId,
			String profileId) throws Exception {
		boolean result = false;
		Integer callStatus = 0;

		try {

			RestAssuredAPI rp = new RestAssuredAPI();

			Log.message("<b>==========================================================</b>");
			Log.message("<i><b>   Validation for create payment from Ampoints  Started" + "</b></i>");
			Log.message("<b>==========================================================</b>");

			CreatePaymentUtility createPaymentUtilityObj = new CreatePaymentUtility();
			Response getResponse = createPaymentUtilityObj.performGetBalAmpoints(accountId);

			String res = getResponse.asString();
			io.restassured.path.json.JsonPath js = new io.restassured.path.json.JsonPath(res);
			callStatus = getResponse.statusCode();
			Log.assertThat(callStatus == 200, "Status code is return as 200.",
					"Status Code is return as not 200, Actual result : " + callStatus);
			if (callStatus == 200) {

				Float availablePoints = js.get("availablePoints");

				System.out
						.println("-------------------Current Available Balance--------------------" + availablePoints);

				if (availablePoints == 0 || availablePoints < Integer.parseInt(amount)) {

					System.out.println("--------------------------NO/Low Balance Available---------------------------");
					Log.message("<i><b>   call Api to add balance  Started" + "</b></i>");

					String rfid = String.valueOf(Utils.getRandom(1000, 100000));
					Response addBalresponse = createPaymentUtilityObj.performAddBalanceAmpoints(accountId,
							Payment.ENTRY_TYPE_CREDIT_BALANCE, Payment.AMPOINTS_POINTS, rfid);
					callStatus = addBalresponse.statusCode();

					Log.assertThat(callStatus == 200, "Status code is return as 200.",
							"Status Code is return as not 200, Actual result : " + callStatus);

				}

				// create payment

				if (callStatus == 200) {
					String rfidCreate = String.valueOf(Utils.getRandom(1000, 100000));
					Response response = createPaymentUtilityObj.performWalletCreatePayment(rfidCreate, refType,
							currency, Integer.parseInt(amount), paymentMethodProviderId, paymentMethodCode, entryType,
							accountId, profileId);
					CreatePaymentExternalResponseDTO createPaymentResponseDTO = response.as(CreatePaymentExternalResponseDTO.class);
					int actualResultStatusCode = response.statusCode();
					Log.assertThat(actualResultStatusCode == 200,
							"Create Payment for Credit voucher executed successfully as code is return as 200.",
							"Status Code is return as not 200 for create payment, Actual result : "
									+ actualResultStatusCode);
					String transStatus = createPaymentResponseDTO.getTransactions().get(0).getStatus();

					Log.assertThat(transStatus.equals("CAPTURED"),
							"transaction Status is CAPTURED. for create payment credit voucher",
							"Status is not CAPTURED for Ampoints, Actual result : " + transStatus);

					Log.assertThat(
							createPaymentResponseDTO.paymentId != null && !createPaymentResponseDTO.paymentId.isEmpty(),
							"Payment Id created successfully  for credit voucher",
							"Payment id has not generated for credit voucher.");

				}

			}

		}

		catch (Exception e) {
			Log.exception(e);
		} // catch
		finally {
			Log.endTestCase();
		} // finally
	}

	@Test(enabled = true, description = "Testcase to verify the create payment api for payment for Pay at Shop", dataProviderClass = DataProviderUtils.class, dataProvider = "APIData")
	public void tcAmwayAPIPayatShopCreatePayment(String refType, String currency, String amount,
			String paymentMethodProviderId, String paymentMethodCode, String entryType, String accountId,
			String profileId) throws Exception {
		boolean result = false;

		try {

			RestAssuredAPI rp = new RestAssuredAPI();
			Log.message("<b>==========================================================</b>");
			Log.message("<i><b>   Validation for create_payment_API  Started" + "</b></i>");
			Log.message("<b>==========================================================</b>");
			String refId = String.valueOf(Utils.getRandom(1000, 100000));
			CreatePaymentUtility createPaymentUtilityObj = new CreatePaymentUtility();
			Response response = createPaymentUtilityObj.performWalletCreatePayment(refId, refType, currency,
					Integer.parseInt(amount), paymentMethodProviderId, paymentMethodCode, entryType, accountId,
					profileId);

			String res = response.asString();
			io.restassured.path.json.JsonPath js = new io.restassured.path.json.JsonPath(res);
			int actualResultStatusCode = response.statusCode();
			Log.assertThat(actualResultStatusCode == 200, "Status code is return as 200.",
					"Status Code is return as not 200, Actual result : " + actualResultStatusCode);
			if (actualResultStatusCode == 200) {
				String status = js.get("status");
				Log.assertThat(status.equals("AWAITED"), "Status is as AWAITED.",
						"Status is not as AWAITED, Actual result : " + status);

			}

		}

		catch (Exception e) {
			Log.exception(e);
		} // catch
		finally {
			Log.endTestCase();
		} // finally
	}

	

}
