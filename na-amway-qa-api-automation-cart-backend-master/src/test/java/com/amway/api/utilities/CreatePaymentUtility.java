
package com.amway.api.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amway.pojo.payment.AddAmpointsBalanceReq;
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
			String paymentMethodProviderId, String paymentMethodCode, String entryType, String accountId,
			String profileId) throws Exception {
		String uri = getURI("createPayment");
		Response response = null;
		CreatePaymentRequest createPaymentDto = CreatePaymentRequest.builder().refId(refId).refType(refType)
				.amount(Amount.builder().currency(currency).value(value).build())
				.userInfo(UserInfo.builder().accountId(accountId).profileId(profileId).build())
				.transactions(createTransaction(paymentMethodProviderId, paymentMethodCode, entryType, value))
				.build();

		RequestSpecificationDTO requestSpecificationDTO;
		try {
			requestSpecificationDTO = createRequestSpecificationObject(uri, "POST", setPaymentHeaders(false),
					createPaymentDto);
			response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			String profileId) throws Exception {
		String uri = getURI("createPayment");

		CreatePaymentRequest createPaymentDto = CreatePaymentRequest.builder().refId(refId).refType(refType)
				.amount(Amount.builder().currency(currency).value(value).build())
				.userInfo(UserInfo.builder().accountId(acoountId).profileId(profileId).build())
				.transactions(createTransaction(paymentMethodProviderId, paymentMethodCode, entryType, value))
				.build();

		RequestSpecificationDTO requestSpecificationDTO;
		Response response = null;
		try {
			requestSpecificationDTO = createRequestSpecificationObject(uri, "POST", setPaymentHeaders(false),
					createPaymentDto);
			response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	public Response performGetBal(String accountId, String accountType) throws Exception {
		String uri = getURI("checkBal");
		HashMap<String, String> qparam = new HashMap<String, String>();
		qparam.put("accountId", accountId);
		qparam.put("accountType", accountType);

		RestAssuredAPI res = new RestAssuredAPI();
		RequestSpecificationDTO requestSpecificationDTO;
		Response response = null;
		try {
			requestSpecificationDTO = createRequestSpecificationObject(uri, "GET", setPaymentHeaders(false), qparam);
			response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	public Response performGetBalAmpoints(String accountId) throws Exception {
		String uri = getURI("checkBalAmpoints");
		HashMap<String, String> qparam = new HashMap<String, String>();
		qparam.put("accountId", accountId);

		RestAssuredAPI res = new RestAssuredAPI();
		RequestSpecificationDTO requestSpecificationDTO;

		Response response = null;
		try {
			requestSpecificationDTO = createRequestSpecificationObject(uri, "GET", setPaymentHeaders(false), qparam);
			response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	public Response performAddBalance(String accountId, String accountType, String currency, String entryType,
			Integer amount, String rfId) throws Exception {
		String uri = getURI("addBal");

		AddBalanceReq addBalanceDto = AddBalanceReq.builder().accountId(accountId).accountType(accountType)
				.currency(currency).entryType(entryType).amountValue(amount).availableBalance(amount)
				.source(Source.builder().system(Payment.SYSTEM).refId(rfId).build()).build();

		JSONArray jsonArrayData = new JSONArray();
		jsonArrayData.add(addBalanceDto);
		RequestSpecificationDTO requestSpecificationDTO;
		Response response = null;
		try {
			requestSpecificationDTO = createRequestSpecificationObjectArray(uri, "POST", setPaymentHeaders(false),
					jsonArrayData);
			response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		RequestSpecificationArrayDTO requestSpecificationArrayDTO = createRequestSpecificationObjectArray(uri, "POST",
//				setPaymentHeaders(false), jsonArrayData);
//		Response response = RestAssuredAPI.callAPI(requestSpecificationArrayDTO, null);

		return response;
	}

	public Response performAddBalanceAmpoints(String accountId, String entryType, Integer points, String rfId)
			throws Exception {
		String uri = getURI("addBalAmpoints");
		HashMap<String, String> qparam = new HashMap<String, String>();
		qparam.put("accountId", accountId);

		AddAmpointsBalanceReq addAmpointsBalanceDto = AddAmpointsBalanceReq.builder().entryType(entryType)
				.points(points).referenceId(rfId).build();

		RequestSpecificationDTO requestSpecificationDTO;
		Response response = null;
		try {
			requestSpecificationDTO = createRequestSpecificationObject(uri, "POST", setPaymentHeaders(false), qparam);
			response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		RequestSpecificationArrayDTO requestSpecificationArrayDTO = createRequestSpecificationObjectArray(uri, "POST",
//				setPaymentHeaders(false), jsonArrayData);
//		Response response = RestAssuredAPI.callAPI(requestSpecificationArrayDTO, null);

		return response;
	}

	public Response performGetPaymentByPaymentId(String paymentId) throws Exception {
		String uri = getURI("getPaymentById") + "/" + paymentId;

		HashMap<String, String> qparam = new HashMap<String, String>();

		qparam.put("projection", "DETAILED");

		RestAssuredAPI res = new RestAssuredAPI();
		RequestSpecificationDTO requestSpecificationDTO;
		Response response = null;
		try {
			requestSpecificationDTO = createRequestSpecificationObject(uri, "GET", setPaymentHeaders(false), qparam);
			response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	public JsonPath getJsonPathObject(Response res) {

		String resString = res.asString();
		io.restassured.path.json.JsonPath js = new io.restassured.path.json.JsonPath(resString);
		return js;
	}
	//refId, refType, currency,
	//Integer.parseInt(totalAmount), obtPaymentMethodProviderId, obtPaymentMethodCode, obtEntryType,ampursePaymentMethodProviderId, amoursePaymentMethodCode, ampurseEntryType, accountId,
	//profileId,Integer.parseInt(obtAmount),Integer.parseInt(ampurseAmount)
	public Response performSplitCreatePayment(String refId, String refType, String currency, Integer totalAmount,
			String obtPaymentMethodProviderId, String obtPaymentMethodCode, String obtEntryType,String ampursePaymentMethodProviderId, String ampursePaymentMethodCode, String ampurseEntryType, String accountId,
			String profileId,Integer obtAmount,Integer ampurseAmount) throws Exception {
		String uri = getURI("createPayment");
		Response response = null;
		CreatePaymentRequest createPaymentDto = CreatePaymentRequest.builder().refId(refId).refType(refType)
				.amount(Amount.builder().currency(currency).value(totalAmount).build())
				.userInfo(UserInfo.builder().accountId(accountId).profileId(profileId).build())
				.transactions(createTransactionForSplit(ampursePaymentMethodProviderId, ampursePaymentMethodCode, ampurseEntryType,obtPaymentMethodProviderId, obtPaymentMethodCode, obtEntryType,obtAmount,ampurseAmount))
				.build();

		RequestSpecificationDTO requestSpecificationDTO;
		try {
			requestSpecificationDTO = createRequestSpecificationObject(uri, "POST", setPaymentHeaders(false),
					createPaymentDto);
			response = RestAssuredAPI.callAPI(requestSpecificationDTO, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}
	public ArrayList<Transaction> createTransactionForSplit(String ampursePaymentMethodProviderId, String ampursePaymentMethodCode,
			String ampurseEntryType,String obtPaymentMethodProviderId, String obtPaymentMethodCode,
			String obtEntryType, Integer obtAmount,Integer ampurseAmount) {

		ArrayList<Transaction> transactions = new ArrayList<>();
		transactions.add(Transaction.builder().paymentMethodProviderId(ampursePaymentMethodProviderId)
				.paymentMethodCode(ampursePaymentMethodCode).paymentLocation("").terminalId("").description("string")
				.amount(Amount.builder().currency("THB").value(ampurseAmount).build())
				.entry(Entry.builder().entryType(ampurseEntryType).payload(Payload.builder().tokenize(true).build()).build())

				.build()

		);
		transactions.add(Transaction.builder().paymentMethodProviderId(obtPaymentMethodProviderId)
				.paymentMethodCode(obtPaymentMethodCode).paymentLocation("").terminalId("").description("string")
				.amount(Amount.builder().currency("THB").value(obtAmount).build())
				.entry(Entry.builder().entryType(obtEntryType).payload(Payload.builder().tokenize(true).build()).build())

				.build()

		);
		return transactions;
}
}