package com.amway.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import org.openqa.selenium.WebDriver;

import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.NetworkMode;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.amway.pojo.request.RequestSpecificationDTO;
import com.google.gson.Gson;
import io.restassured.*;
import com.amway.support.Log;

public class BaseTest {
	protected static ExtentReports extent;
	protected static String webSite;
	public static WebDriver driver;
	public static Properties prop;
	public static ExtentReports report;
	public static String Desc = "";
	public static String Author = "";
	public static String Category = "";
	public static String environment = "QA";
	public static String browser;

	@BeforeMethod(alwaysRun = true)
	public void beforeMethod() {

	}

	@AfterMethod(alwaysRun = true)
	public void afterMethod(ITestContext ctx) {

	}

	@BeforeTest(alwaysRun = true)
	public void init(ITestContext context) {
		System.out.println("In Before test");
		webSite = (System.getProperty("webSite") != null ? System.getProperty("webSite")
				: context.getCurrentXmlTest().getParameter("webSite"));
		prop = (System.getProperty("configFile") != null
				? EnvironmentPropertiesReader.loadProperties(System.getProperty("configFile"))
				: EnvironmentPropertiesReader.loadProperties(context.getCurrentXmlTest().getParameter("configFile")));
		webSite = (System.getProperty("environment") != null ? System.getProperty("environment")
				: context.getCurrentXmlTest().getParameter("environment"));
		browser=context.getCurrentXmlTest().getParameter("browserName");
	}

	@BeforeSuite(alwaysRun = true)
	public void beforeSuite() {
		extent = new ExtentReports("./test-output/TestAutomationExtentReport.html", true, DisplayOrder.NEWEST_FIRST,
				NetworkMode.ONLINE);
	}

	/*
	 * After suite will be responsible to close the report properly at the end You
	 * an have another afterSuite as well in the derived class and this one will be
	 * called in the end making it the last method to be called in test exe
	 */
	@AfterSuite
	public void afterSuite() {
		extent.flush();
	}

	/**
	 * Inits the test data.
	 *
	 * @param workbook  the workbook
	 * @param sheetName the sheet name
	 */
	public HashMap<String, String> initTestData(String workbook, String sheetName) {
		/** Loading the test data from excel using the test case id */
		TestDataExtractor testData = new TestDataExtractor();
		testData.setWorkBookName(workbook);
		testData.setWorkSheet(sheetName);
		testData.setFilePathMapping(true);

		Throwable t = new Throwable();
		String testCaseId = t.getStackTrace()[1].getMethodName();
		testData.setTestCaseId(testCaseId);
		return testData.readData();
	}

	/**
	 * This is for calling the actual api's from the code using switch-case
	 * 
	 * @param <T>
	 * @param requestSpecificationDTO
	 * @param tclass
	 * @return
	 */
	public static <T> T callAPI(RequestSpecificationDTO requestSpecificationDTO, Class<T> tclass) {
		Response response = null;
		Gson gson = new Gson();

		RequestSpecification requestSpecification = requestSpecificationCreation(requestSpecificationDTO);
		switch (requestSpecificationDTO.getMethod()) {
		case "GET":
			response = requestSpecification.get();
			break;
		case "POST":
			response = requestSpecification.post();
			break;
		case "PUT":
			response = requestSpecification.put();
			break;
		case "DELETE":
			response = requestSpecification.delete();
			break;
		case "PATCH":
			response = requestSpecification.patch();
			break;
		} // switch case ends here
		System.out.println("--------------------------Request---------------------------");
		System.out.println("--------------------------Headers---------------------------");
		System.out.println(requestSpecificationDTO.getHeaders());
		System.out.println("--------------------------Request body----------------------");
		if (requestSpecificationDTO.getRequestArrayBody() != null) {
			System.out.println(requestSpecificationDTO.getRequestArrayBody());
		} else if (requestSpecificationDTO.getRequestBody() != null) {
			System.out.println(requestSpecificationDTO.getRequestBody());
		}
		
		System.out.println("--------------------------Request Uri-----------------------");
		System.out.println(requestSpecificationDTO.getUri());
		System.out.println("--------------------------Request Query params-----------------------");
		System.out.println(requestSpecificationDTO.getQueryParam());
		System.out.println("--------------------------Response Body---------------------------");
		System.out.println(response.getBody().asString().replace("\n", "").replace("\t", ""));
		if (tclass != null)
			return gson.fromJson(response.getBody().asString(), tclass);
		else
			return (T) response;
	} // function callAPI() ends here

	public static RequestSpecification requestSpecificationCreation(RequestSpecificationDTO requestSpecificationDTO) {
		RequestSpecification requestSpecification = RestAssured.given();
		try {
			if (requestSpecificationDTO.getHeaders() != null)
				requestSpecification.headers(requestSpecificationDTO.getHeaders());
			if (requestSpecificationDTO.getUri() != null)
				requestSpecification.baseUri(requestSpecificationDTO.getUri());
			if (requestSpecificationDTO.getContentType() != null)
				requestSpecification.contentType(requestSpecificationDTO.getContentType());

			// when we need to post data as array of object then fill requestArrayBody like addBalance
			// when we need to post data as object then fill requestBody like createPayment
			if (requestSpecificationDTO.getRequestArrayBody() != null) {
				requestSpecification.body(requestSpecificationDTO.getRequestArrayBody().toJSONString());
			} else if (requestSpecificationDTO.getRequestBody() != null) {
				requestSpecification.body(requestSpecificationDTO.getRequestBody().toJSONString());
			}

			if (requestSpecificationDTO.getQueryParam() != null)
				requestSpecification.queryParams(requestSpecificationDTO.getQueryParam());
		} catch (Exception e) {
			Log.message(Desc);
		}

		requestSpecification.relaxedHTTPSValidation();
		return requestSpecification;
	}

}
