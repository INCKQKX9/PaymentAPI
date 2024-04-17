
package com.amway.api.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.amway.pojo.payment.AddBalanceReq;
import com.amway.pojo.payment.AddBalanceReq.Source;
import com.amway.pojo.payment.CreatePaymentRequest;
import com.amway.pojo.payment.CreatePaymentRequest.Amount;
import com.amway.pojo.payment.CreatePaymentRequest.Entry;
import com.amway.pojo.payment.CreatePaymentRequest.Payload;
import com.amway.pojo.payment.CreatePaymentRequest.Transaction;
import com.amway.pojo.payment.CreatePaymentRequest.UserInfo;
import com.amway.pojo.request.RequestSpecificationDTO;

import com.amway.support.RestAssuredAPI;
import com.amway.tdata.TData.Payment;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CreatePaymentUtility extends RestAssuredAPI {
	Response response = null;

	public Response performCreatePayment(String refId, String refType, String currency, Integer value,
			String paymentMethodProviderId, String paymentMethodCode, String entryType) {
		String uri = getURI("createPayment");

		CreatePaymentRequest createPaymentDto = CreatePaymentRequest.builder().refId(refId).refType(refType)
				.amount(Amount.builder().currency(currency).value(value).build())
				.transactions(createTransaction(paymentMethodProviderId, paymentMethodCode, entryType, value)).build();
		System.out.println(createPaymentDto.getTransactions());
		RequestSpecificationDTO requestSpecificationDTO = createRequestSpecificationObject(uri, "POST",
				setPaymentHeaders(false), createPaymentDto);
		Response response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);

		return response;
	}

	public ArrayList<Transaction> createTransaction(String paymentMethodProviderId, String paymentMethodCode,
			String entryType, Integer value) {

		ArrayList<Transaction> transactions = new ArrayList<>();
		transactions.add(Transaction.builder().paymentMethodProviderId(paymentMethodProviderId)
				.paymentMethodCode(paymentMethodCode).paymentLocation("").terminalId("").description("string")
				.amount(Amount.builder().currency("THB").value(value).build())
				.entry(Entry.builder().entryType(entryType).payload(Payload.builder().tokenize(true).build()).build())

				.build()

		);

		return transactions;

	}

	public Response performWalletCreatePayment(String refId, String refType, String currency, Integer value,
			String paymentMethodProviderId, String paymentMethodCode, String entryType, String acoountId,
			String profileId) {
		String uri = getURI("createPayment");

		CreatePaymentRequest createPaymentDto = CreatePaymentRequest.builder().refId(refId).refType(refType)
				.amount(Amount.builder().currency(currency).value(value).build())
				.userInfo(UserInfo.builder().accountId(acoountId).profileId(profileId).build())
				.transactions(createTransaction(paymentMethodProviderId, paymentMethodCode, entryType, value)).build();

		RequestSpecificationDTO requestSpecificationDTO = createRequestSpecificationObject(uri, "POST",
				setPaymentHeaders(false), createPaymentDto);
		Response response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);

		return response;
	}

	public Response performGetBal(String accountId, String accountType) {
		String uri = getURI("checkBal");
		HashMap<String, String> qparam = new HashMap<String, String>();
		qparam.put("accountId", accountId);
		qparam.put("accountType", accountType);

		RestAssuredAPI res = new RestAssuredAPI();
		RequestSpecificationDTO requestSpecificationDTO = createRequestSpecificationObject(uri, "GET",
				setPaymentHeaders(false), qparam);
		Response response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);

		return response;
	}

	public Response performAddBalance(String accountId, String accountType, String currency, String entryType,
			Integer amount, String rfId) throws ParseException {
		String uri = getURI("addBal");

		AddBalanceReq addBalanceDto = AddBalanceReq.builder().accountId(accountId).accountType(accountType)
				.currency(currency).entryType(entryType).amountValue(amount).availableBalance(amount)
				.source(Source.builder().system(Payment.SYSTEM).refId(rfId).build()).build();

		JSONArray jsonArrayData = new JSONArray();
		jsonArrayData.add(addBalanceDto);
		RequestSpecificationDTO requestSpecificationDTO = createRequestSpecificationObjectArray(uri, "POST",
				setPaymentHeaders(false), jsonArrayData);
		Response response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);
		
//		RequestSpecificationArrayDTO requestSpecificationArrayDTO = createRequestSpecificationObjectArray(uri, "POST",
//				setPaymentHeaders(false), jsonArrayData);
//		Response response = RestAssuredAPI.callAPI(requestSpecificationArrayDTO, null);

		return response;
	}
	

	public JsonPath getJsonPathObject(Response res) {
		
		String resString=res.asString();
		io.restassured.path.json.JsonPath js = new io.restassured.path.json.JsonPath(resString);
		return js;
	}

}
