package com.amway.support;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amway.pojo.payment.AddBalanceReq;
import com.amway.pojo.request.RequestSpecificationDTO;
import com.amway.support.Log;
import com.amway.tdata.TData.Global;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class RestAssuredAPI extends BaseTest {
	String baseurl = "https://global-qa.amcom-corp-preprod.amway.net/";
	String cookie = "rxVisitor=17111112636896IVSHJE0TSFB0N5N8FV5IFIGSJ3LBQQD; CONSENTMGR=consent:false%7Cts:1711111673584; tasks-completed=false; dtCookie=v_4_srv_11_sn_0CINJE6A0CPL4BRDHPVTRJ5HNNB4OK7R_app-3A32fa1c3c8329f71f_1_app-3A70c281e609d97ade_1_ol_0_perc_100000_mul_1; session=w1mlTcjMCxb2QBnlp7pw2v1uyjNxqYsWMkC-_9sZInA; user-info=id%3D72f98241-eafc-5401-a285-d1afdf46b120%3Btype%3DBUSINESS_OWNER%3BaboNum%3D694806621%3BlastName%3DABO%3BuserName%3DTestuser1%3BpartyId%3Dundefined%3B; dtSa=-; utag_main=v_id:018e662dd4cf00758170c91873a00506f00cf067007e8$_sn:3$_se:9$_ss:0$_st:1711274538925$ses_id:1711272585437%3Bexp-session$_pn:7%3Bexp-session; rxvt=1711274561420|1711272387562; dtPC=11$72693674_364h-vAPQVSSMAETNULPKUHNUVECCMIOLPTNMD-0e0";
	Gson gson = new Gson();

	public Response postRequest(HashMap<String, String> hMap, String url, String body) {

		RestAssured.baseURI = baseurl;
		Response response = RestAssured.given().headers(hMap).when().body(body).post(url).then().extract().response();

		return response;

	}

	public Response postRequest(HashMap<String, String> hMap, HashMap<String, String> qParam, String url, String body) {

		RestAssured.baseURI = baseurl;
		Response response = RestAssured.given().queryParams(qParam).headers(hMap).when().body(body).post(url).then()
				.extract().response();

		return response;

	}

	public Response getRequest(HashMap<String, String> hMap, HashMap<String, String> qParam, String url, String body) {

		RestAssured.baseURI = baseurl;
		Response response = RestAssured.given().queryParams(qParam).headers(hMap).when().body(body).get(url).then()
				.extract().response();

		return response;

	}

	// Added by kuldeep
	/*
	 * public RequestSpecificationDTO createRequestSpecificationObject(String uri,
	 * String method, Map<String, String> headers,Map<String,String> queryParams) {
	 * RequestSpecificationDTO requestSpecificationDTO; requestSpecificationDTO =
	 * RequestSpecificationDTO.builder().headers(headers).uri(uri).queryParam(
	 * queryParams) .method(method).build(); return requestSpecificationDTO; }
	 */

	public Response getRequestWithQueryParam(HashMap<String, String> qParam, String url) {

		RestAssured.baseURI = baseurl;
		Response response = RestAssured.given().queryParams(qParam).when().get(url).then().extract().response();

		return response;

	}

	public Response getRequest(HashMap<String, String> hMap, String url, String body) {

		RestAssured.baseURI = baseurl;
		Response response = RestAssured.given().headers(hMap).when().body(body).get(url).then().extract().response();

		return response;

	}

	public Response getRequest(HashMap<String, String> hMap, String url) {

		RestAssured.baseURI = baseurl;
		Response response = RestAssured.given().headers(hMap).when().get(url).then().extract().response();

		return response;

	}

	public Response deleteRequest(HashMap<String, String> hMap, HashMap<String, String> qParam, String url,
			String body) {

		RestAssured.baseURI = baseurl;
		Response response = RestAssured.given().queryParams(qParam).headers(hMap).when().body(body).delete(url).then()
				.extract().response();

		return response;

	}

	// This function is to return object for the response DTO class object generic
	// to all the response pojo DTO's
	public <T> T getResponseDTO(Response response, Class<T> tclass) {
		return gson.fromJson(response.getBody().asString(), tclass);
	}

	// Function to convert JSON from string JSON
	public JSONObject convertJsonToString(String str) {
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(str);
			return jsonObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// returning null if complier sees any exception in conversion from JSON to
		// string
		return null;

	}

	// Function to convert JSON from string JSON
	public JSONArray convertJsonToStringArray(String str) {
		JSONParser jsonParser = new JSONParser();
		try {
			JSONArray jsonArray = (JSONArray) jsonParser.parse(str);
			return jsonArray;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// returning null if complier sees any exception in conversion from JSON to
		// string
		return null;

	}

	// This function creates DTO for request specification / requestDTO
	public RequestSpecificationDTO createRequestSpecificationObject(String uri, String method,
			Map<String, String> headers, Object tClass) {
		RequestSpecificationDTO requestSpecificationDTO;
		requestSpecificationDTO = RequestSpecificationDTO.builder().headers(headers).uri(uri)
				.requestBody(convertJsonToString(gson.toJson(tClass))).method(method).build();
		return requestSpecificationDTO;
	}

	// This function creates DTO for request specification / requestDTO
	public RequestSpecificationDTO createRequestSpecificationObjectArray(String uri, String method,
			Map<String, String> headers, JSONArray arrayList) {
		RequestSpecificationDTO requestSpecificationDTO;
		requestSpecificationDTO = RequestSpecificationDTO.builder().headers(headers).uri(uri)
				.requestArrayBody(convertJsonToStringArray(gson.toJson(arrayList))).method(method).build();
		return requestSpecificationDTO;
	}

	public Map<String, String> setQueryParams() {
		Map<String, String> queryParams = new HashMap<String, String>();
		// To do add any common
		return queryParams;
	}

	// This function is to generate default/common headers
	public Map<String, String> setHeaders() {
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Accept-Language", "en-us");
		headerMap.put("X-Amway-Tenant", "200");
		headerMap.put("X-Amway-Vertical", "DEFAULT");
		headerMap.put("X-Amway-Country", "th");
		headerMap.put("X-Amway-Channel", "WEB");
		headerMap.put("Content-type", "application/json");
		System.out.println();
		return headerMap;
	}

//	
//	Accept-Language:en-us
//	X-Amway-Channel:<string>
//	X-Amway-Country:<string>
//	X-Amway-Vertical:<string>
//	X-Amway-Tenant:<string>
//	X-Idempotency-Key:kul-006
//	Content-Type:application/json

	// Headers for Anonymous cart
	public Map<String, String> setAnonyomousCartHeaders() throws Exception {
		Map<String, String> headerMap = setHeaders();
		headerMap.put("X-AMW-ANON-ID", prop.getProperty("anonymousAmwId"));
		try {
			headerMap.put("X-Idempotency-Key", String.valueOf(Utils.getRandom(1000, 100000)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return headerMap;
	}

	// Headers for Anonymous cart
	public Map<String, String> setPaymentHeaders(Boolean isUseCookie) throws Exception {
		Map<String, String> headerMap = setHeaders();

		try {
			headerMap.put("X-Idempotency-Key", String.valueOf(Utils.getRandom(1000, 100000)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isUseCookie) {
			headerMap.put("Cookie", cookie);
		}

//		headerMap.put("X-Idempotency-Key", "kul-0003");
		return headerMap;
	}

	public String getURI(String apiName) {
		String baseUrl = environment.equals("QA") ? prop.getProperty("hostQA") : prop.getProperty("hostDev");
		String URI = baseUrl + prop.getProperty(apiName);
		return URI;
	}

	public RequestSpecificationDTO createRequestSpecificationObject(String uri, String method,
			Map<String, String> headers, Map<String, String> queryParams) {
		RequestSpecificationDTO requestSpecificationDTO;
		requestSpecificationDTO = RequestSpecificationDTO.builder().headers(headers).uri(uri).queryParam(queryParams)
				.method(method).build();
		return requestSpecificationDTO;
	}
	

}