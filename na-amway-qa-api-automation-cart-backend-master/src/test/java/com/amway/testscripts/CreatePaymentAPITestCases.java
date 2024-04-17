package com.amway.testscripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amway.support.TestDataExtractor;
import com.amway.api.utilities.CreatePaymentUtility;
import com.amway.pages.HomePage;
import com.amway.pojo.payment.AmpurseRes;
import com.amway.pojo.response.CreateCartResponseDTO;
import com.amway.pojo.response.CreatePaymentResponseDTO;
import com.amway.support.BrowserActions;
import com.amway.support.CommonUtils;
import com.amway.support.DataProviderUtils;
import com.amway.support.EmailReport;
import com.amway.support.EnvironmentPropertiesReader;
import com.amway.support.Log;
import com.amway.support.RestAssuredAPI;
import com.amway.support.WebDriverFactory;
import com.amway.tdata.TData.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amway.support.Utils;
import com.jayway.jsonpath.JsonPath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.minidev.json.JSONArray;
import io.restassured.http.ContentType;

@Listeners(EmailReport.class)
public class CreatePaymentAPITestCases extends RestAssuredAPI {

	@Test(enabled = true, description = "Testcase to verify the create payment api for payment for credit card", dataProviderClass = DataProviderUtils.class, dataProvider = "APIData")
	public void tcAmwayAPICreatePayment(String refId, String refType, String currency, String amount,
			String paymentMethodProviderId, String paymentMethodCode, String entryType) throws Exception {
		boolean result = false;

		try {

			RestAssuredAPI rp = new RestAssuredAPI();
			Log.message("<b>==========================================================</b>");
			Log.message("<i><b>   Validation for create_payment_API  Started" + "</b></i>");
			Log.message("<b>==========================================================</b>");

			CreatePaymentUtility createPaymentUtilityObj = new CreatePaymentUtility();
			Response response = createPaymentUtilityObj.performCreatePayment(refId, refType, currency,
					Integer.parseInt(amount), paymentMethodProviderId, paymentMethodCode, entryType);

			int actualResultStatusCode = response.statusCode();
			Log.assertThat(actualResultStatusCode == 200, "Status code is return as 200.",
					"Status Code is return as not 200, Actual result : " + actualResultStatusCode);

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
				ArrayList<Integer> ab = js.get("availableBalance");
				Integer balance = (Integer) ab.get(0);

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

					CreatePaymentResponseDTO createPaymentResponseDTO = response.as(CreatePaymentResponseDTO.class);
					int actualResultStatusCode = response.statusCode();
					Log.assertThat(actualResultStatusCode == 200,
							"Create Payment for Ampurse executed successfully as code is return as 200.",
							"Status Code is return as not 200 for create payment, Actual result : "
									+ actualResultStatusCode);
					String transStatus = createPaymentResponseDTO.getTransactions().get(0).getStatus();

					Log.assertThat(transStatus.equals("AUTHORIZED"),
							"transaction Status is AUTHORIZED. for create payment.",
							"Status is not AUTHORIZED, Actual result : " + transStatus);

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
					CreatePaymentResponseDTO createPaymentResponseDTO = response.as(CreatePaymentResponseDTO.class);
					int actualResultStatusCode = response.statusCode();
					Log.assertThat(actualResultStatusCode == 200,
							"Create Payment for Credit voucher executed successfully as code is return as 200.",
							"Status Code is return as not 200 for create payment, Actual result : "
									+ actualResultStatusCode);
					String transStatus = createPaymentResponseDTO.getTransactions().get(0).getStatus();

					Log.assertThat(transStatus.equals("AUTHORIZED"),
							"transaction Status is AUTHORIZED. for create payment credit voucher",
							"Status is not AUTHORIZED for credit voucher, Actual result : " + transStatus);

					Log.assertThat(
							createPaymentResponseDTO.paymentId != null && !createPaymentResponseDTO.paymentId.isEmpty(),
							"Payment Id created successfully  for credit voucher", "Payment id has not generated for credit voucher.");

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

}
