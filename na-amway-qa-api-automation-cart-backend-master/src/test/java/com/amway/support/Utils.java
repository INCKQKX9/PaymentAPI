package com.amway.support;

import java.math.BigDecimal;
import java.text.Collator;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 * Util class consists wait for page load,page load with user defined max time
 * and is used globally in all classes and methods
 * 
 */
public class Utils {
	private static EnvironmentPropertiesReader configProperty = EnvironmentPropertiesReader.getInstance();

	public static int maxElementWait = 10;

	/**
	 * waitForPageLoad waits for the page load with default page load wait time
	 * 
	 * @param driver
	 *            : Webdriver
	 */
	public static void waitForPageLoad(final WebDriver driver) {
		waitForPageLoad(driver, WebDriverFactory.maxPageLoadWait);
	}

	/**
	 * waitForPageLoad waits for the page load with custom page load wait time
	 * 
	 * @param driver
	 *            : Webdriver
	 * @param maxWait
	 *            : Max wait duration
	 */
	public static void waitForPageLoad(final WebDriver driver, int maxWait) {
		long startTime = StopWatch.startTime();
		FluentWait<WebDriver> wait = new WebDriverWait(driver,Duration.ofSeconds(maxWait)).pollingEvery(Duration.ofSeconds(500))
				.ignoring(StaleElementReferenceException.class).withMessage("Page Load Timed Out");
		try {

			if (configProperty.getProperty("documentLoad").equalsIgnoreCase("true"))
				wait.until(WebDriverFactory.documentLoad);

			if (configProperty.getProperty("imageLoad").equalsIgnoreCase("true"))
				wait.until(WebDriverFactory.imagesLoad);

			if (configProperty.getProperty("framesLoad").equalsIgnoreCase("true"))
				wait.until(WebDriverFactory.framesLoad);

			String title = driver.getTitle().toLowerCase();
			String url = driver.getCurrentUrl().toLowerCase();
			Log.event("Page URL:: " + url);

			if ("the page cannot be found".equalsIgnoreCase(title) || title.contains("is not available")
					|| url.contains("/error/") || url.toLowerCase().contains("/errorpage/")) {
				Assert.fail("Site is down. [Title: " + title + ", URL:" + url + "]");
			}
		} catch (TimeoutException e) {
			driver.navigate().refresh();
			wait.until(WebDriverFactory.documentLoad);
			wait.until(WebDriverFactory.imagesLoad);
			wait.until(WebDriverFactory.framesLoad);
		}
		Log.event("Page Load Wait: (Sync)", StopWatch.elapsedTime(startTime));

	} // waitForPageLoad

	/**
	 * Wait until element disappears in the page
	 * 
	 * @param driver
	 *            - driver instance
	 * @param element
	 *            - webelement to wait to have disaapear
	 * @return true if element is not appearing in the page
	 */
	public static boolean waitUntilElementDisappear(WebDriver driver, final WebElement element) {
		final boolean isNotDisplayed;
		WebDriverWait wait = (WebDriverWait) new WebDriverWait(driver, Duration.ofSeconds(WebDriverFactory.maxPageLoadWait));
		isNotDisplayed = wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				boolean isPresent = false;
				try {
					if (element.isDisplayed()) {
						isPresent = false;
						Log.event("Element " + element.toString() + ", is still visible in page");
					}
				} catch (Exception ex) {
					isPresent = true;
					Log.event("Element " + element.toString() + ", is not displayed in page ");
					return isPresent;
				}
				return isPresent;
			}
		});
		return isNotDisplayed;
	}

	/**
	 * To get the test orientation
	 * 
	 * <p>
	 * if test run on sauce lab device return landscape or portrait or valid
	 * message, otherwise check local device execution and return landscape or
	 * portrait or valid message
	 * 
	 * @return dataToBeReturned - portrait or landscape or valid message
	 */
	public static String getTestOrientation() {
		String dataToBeReturned = null;
		boolean checkExecutionOnSauce = false;
		boolean checkDeviceExecution = false;
		checkExecutionOnSauce = (System.getProperty("SELENIUM_DRIVER") != null
				|| System.getenv("SELENIUM_DRIVER") != null) ? true : false;

		if (checkExecutionOnSauce) {
			checkDeviceExecution = ((System.getProperty("runUserAgentDeviceTest") != null)
					&& (System.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true"))) ? true : false;
			if (checkDeviceExecution) {
				dataToBeReturned = (System.getProperty("deviceOrientation") != null)
						? System.getProperty("deviceOrientation") : "no sauce run system variable: deviceOrientation ";
			} else {
				dataToBeReturned = "sauce browser test: no orientation";
			}
		} else {
			checkDeviceExecution = (configProperty.hasProperty("runUserAgentDeviceTest")
					&& (configProperty.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true"))) ? true : false;
			if (checkDeviceExecution) {
				dataToBeReturned = configProperty.hasProperty("deviceOrientation")
						? configProperty.getProperty("deviceOrientation")
						: "no local run config variable: deviceOrientation ";
			} else {
				dataToBeReturned = "local browser test: no orientation";
			}
		}
		return dataToBeReturned;
	}

	/**
	 * To wait for the specific element on the page
	 * 
	 * @param driver
	 *            : Webdriver
	 * @param element
	 *            : Webelement to wait for
	 * @return boolean - return true if element is present else return false
	 */
	public static boolean waitForElement(WebDriver driver, WebElement element) {
		return waitForElement(driver, element, maxElementWait);
	}

	/**
	 * To wait for the specific element on the page
	 * 
	 * @param driver
	 *            : Webdriver
	 * @param element
	 *            : Webelement to wait for
	 * @param maxWait
	 *            : Max wait duration
	 * @return boolean - return true if element is present else return false
	 */
	public static boolean waitForElement(WebDriver driver, WebElement element, int maxWait) {
		boolean statusOfElementToBeReturned = false;
		long startTime = StopWatch.startTime();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(maxWait));
		try {
			WebElement waitElement = wait.until(ExpectedConditions.visibilityOf(element));
			if (waitElement.isDisplayed() && waitElement.isEnabled()) {
				statusOfElementToBeReturned = true;
				Log.event("Element is displayed:: " + element.toString());
			}
		} catch (Exception e) {
			statusOfElementToBeReturned = false;
			Log.event("Unable to find a element after " + StopWatch.elapsedTime(startTime) + " sec ==> "
					+ element.toString());
		}
		return statusOfElementToBeReturned;
	}

	public static WebDriver switchWindows(WebDriver driver, String windowToSwitch, String opt,
			String closeCurrentDriver) throws Exception {

		WebDriver currentWebDriver = driver;
		WebDriver assingedWebDriver = driver;
		boolean windowFound = false;
		ArrayList<String> multipleWindows = new ArrayList<String>(assingedWebDriver.getWindowHandles());

		for (int i = 0; i < multipleWindows.size(); i++) {

			assingedWebDriver.switchTo().window(multipleWindows.get(i));

			if (opt.equals("title")) {
				if (assingedWebDriver.getTitle().trim().equals(windowToSwitch)) {
					windowFound = true;
					break;
				}
			} else if (opt.equals("url")) {
				if (assingedWebDriver.getCurrentUrl().contains(windowToSwitch)) {
					windowFound = true;
					break;
				}
			} // if

		} // for

		if (!windowFound)
			throw new Exception("Window: " + windowToSwitch + ", not found!!");
		else {
			if (closeCurrentDriver.equals("true"))
				currentWebDriver.close();
		}

		return assingedWebDriver;

	}// switchWindows

	/**
	 * Switching between tabs or windows in a browser
	 * 
	 * @param driver
	 *            -
	 */
	public static void switchToNewWindow(WebDriver driver) {
		String winHandle = driver.getWindowHandle();
		for (String index : driver.getWindowHandles()) {
			if (!index.equals(winHandle)) {
				driver.switchTo().window(index);
				break;
			}
		}
		if (!((RemoteWebDriver) driver).getCapabilities().getBrowserName().matches(".*safari.*")) {
			((JavascriptExecutor) driver).executeScript(
					"if(window.screen){window.moveTo(0, 0); window.resizeTo(window.screen.availWidth, window.screen.availHeight);};");
		}
	}

	/**
	 * To compare two HashMap values,then print unique list value and print
	 * missed list value
	 * 
	 * @param expectedList
	 *            - expected element list
	 * @param actualList
	 *            - actual element list
	 * @return statusToBeReturned - returns true if both the lists are equal,
	 *         else returns false
	 */
	public static boolean compareTwoHashMap(Map<String, String> expectedList, Map<String, String> actualList) {
		List<String> missedkey = new ArrayList<String>();
		HashMap<String, String> missedvalue = new HashMap<String, String>();
		try {
			for (String k : expectedList.keySet()) {
				if (!(actualList.get(k).toLowerCase().replaceAll("-", "").trim()
						.equalsIgnoreCase(expectedList.get(k).toLowerCase().replaceAll("-", "").trim()))) {
					missedvalue.put(k, actualList.get(k));
					Log.event("Missed Values:: " + missedvalue);
					return false;
				}
			}
			for (String y : actualList.keySet()) {
				if (!expectedList.containsKey(y)) {
					missedkey.add(y);
					Log.event("Missed keys:: " + missedkey);
					return false;
				}
			}
		} catch (NullPointerException np) {
			return false;
		}
		return true;
	}

	/**
	 * To compare two HashMap values,then print unique list value and print
	 * missed list value
	 * 
	 * @param expectedList
	 *            - expected element list
	 * @param actualList
	 *            - actual element list
	 * @return statusToBeReturned - returns true if both the lists are equal,
	 *         else returns false
	 */
	public static boolean productcompareTwoHashMap(Map<String, String> expectedList, Map<String, String> actualList) {
		new ArrayList<String>();
		HashMap<String, String> missedvalue = new HashMap<String, String>();
		try {
			for (String k : expectedList.keySet()) {
				if (!(actualList.get(k).toLowerCase().replaceAll("-", "").trim()
						.equalsIgnoreCase(expectedList.get(k).toLowerCase().replaceAll("-", "").trim()))) {
					missedvalue.put(k, actualList.get(k));
					Log.event("Missed Values:: " + missedvalue);
					return false;
				}
			}
		} catch (NullPointerException np) {
			return false;
		}
		return true;
	}

	/**
	 * To compare two array list values,then print unique list value and print
	 * missed list value
	 * 
	 * @param expectedElements
	 *            - expected element list
	 * @param actualElements
	 *            - actual element list
	 * @return statusToBeReturned - returns true if both the lists are equal,
	 *         else returns false
	 */
	public static boolean compareTwoList(List<String> expectedElements, List<String> actualElements) {
		boolean statusToBeReturned = false;
		List<String> uniqueList = new ArrayList<String>();
		List<String> missedList = new ArrayList<String>();
		for (String item : expectedElements) {
			if (actualElements.contains(item)) {
				uniqueList.add(item);
			} else {
				missedList.add(item);
			}
		}
		Collections.sort(expectedElements);
		Collections.sort(actualElements);
		if (expectedElements.equals(actualElements)) {
			Log.event("All elements checked on this page:: " + uniqueList);
			statusToBeReturned = true;
		} else {
			Log.failsoft("Missing element on this page:: " + missedList);
			statusToBeReturned = false;
		}
		return statusToBeReturned;
	}

	/**
	 * Verify the css property for an element
	 * 
	 * @param element
	 *            - WebElement for which to verify the css property
	 * @param cssProperty
	 *            - the css property name to verify
	 * @param actualValue
	 *            - the actual css value of the element
	 * @return boolean
	 */
	public static boolean verifyCssPropertyForElement(WebElement element, String cssProperty, String actualValue) {
		boolean result = false;

		String actualClassProperty = element.getCssValue(cssProperty);

		if (actualClassProperty.contains(actualValue)) {
			result = true;
		}
		return result;
	}

	/**
	 * To get the value of an input field.
	 * 
	 * @param element
	 *            - the input field you need the value/text of
	 * @param driver
	 *            -
	 * @return text of the input's value
	 */
	public static String getValueOfInputField(WebElement element, WebDriver driver) {
		String sDataToBeReturned = null;
		if (Utils.waitForElement(driver, element)) {
			sDataToBeReturned = element.getAttribute("value");
		}
		return sDataToBeReturned;
	}

	/**
	 * To wait for the specific element which is in disabled state on the page
	 * 
	 * @param driver
	 *            - current driver object
	 * @param element
	 *            - disabled webelement
	 * @param maxWait
	 *            - duration of wait in seconds
	 * @return boolean - return true if disabled element is present else return
	 *         false
	 */
	public static boolean waitForDisabledElement(WebDriver driver, WebElement element, int maxWait) {
		boolean statusOfElementToBeReturned = false;
		long startTime = StopWatch.startTime();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(maxWait));
		try {
			WebElement waitElement = wait.until(ExpectedConditions.visibilityOf(element));
			if (!waitElement.isEnabled()) {
				statusOfElementToBeReturned = true;
				Log.event("Element is displayed and disabled:: " + element.toString());
			}
		} catch (Exception ex) {
			statusOfElementToBeReturned = false;
			Log.event("Unable to find disabled element after " + StopWatch.elapsedTime(startTime) + " sec ==> "
					+ element.toString());
		}
		return statusOfElementToBeReturned;
	}

	/**
	 * To know which environment should run against the script
	 * 
	 * @return String - return prod site/Qa site value
	 */
	public static String getExecutionApp() {
		String dataToBeReturned = null;
		if (configProperty.getProperty("ExecuteOnProductionServer").equalsIgnoreCase("true")) {
			dataToBeReturned = "ProdSite";
		} else {
			dataToBeReturned = "QAsites";
		}
		Log.event("Running platform type:: " + dataToBeReturned);
		return dataToBeReturned;
	}// getRunPlatForm

	/**
	 * To get run platform from the config.Property files
	 * 
	 * @return String - return mobile/desktop value
	 */
	public static String getRunPlatForm() {
		// configProperty.hasProperty("testlinkUrl");

		if (System.getProperties().contains("runUserAgentDeviceTest")
				&& System.getProperty("runUserAgentDeviceTest").equals("true")) {
			if (configProperty.getProperty("deviceName").contains("phone"))
				return "mobile";
			else if (configProperty.getProperty("deviceName").contains("pad"))
				return "tablet";
			else
				return "N/A";
		}
		String dataToBeReturned = null;
		if ((configProperty.hasProperty("runMobile")
				&& configProperty.getProperty("runMobile").equalsIgnoreCase("true"))
				|| (configProperty.hasProperty("runUserAgentDeviceTest")
						&& configProperty.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true"))) {
			dataToBeReturned = "mobile";
		} else {
			dataToBeReturned = "desktop";
		}
		Log.event("Running platform type:: " + dataToBeReturned);
		return dataToBeReturned;
	}// getRunPlatForm
		// //// deepak

	/**
	 * To compare two Linked list HashMap values,then print unique list value
	 * and print missed list value
	 * 
	 * @param expectedList
	 *            - expected element list
	 * @param actualList
	 *            - actual element list
	 * @return returns true if both the lists are equal, else returns false
	 */

	@SuppressWarnings("unlikely-arg-type")
	public static boolean compareTwoLinkedListHashMap(LinkedList<LinkedHashMap<String, String>> expectedList,
			LinkedList<LinkedHashMap<String, String>> actualList, String[]... noNeed) {
		int size = expectedList.size();
		if (noNeed.length > 0) {
			for (int i = 0; i < noNeed.length; i++) {
				System.out.println("Removed Key from Expected:: " + noNeed[i][i]);
				expectedList.remove(noNeed[i][i]);
				System.out.println("Removed Key from Actual  :: " + noNeed[i][i]);
				actualList.remove(noNeed[i][i]);
			}
		}
		boolean flag = true;
		try {
			for (int i = 0; i < size; i++) {
				if (!Utils.compareTwoHashMap(expectedList.get(i), actualList.get(i)))
					flag = false;
			}
		} catch (NullPointerException np) {
			return false;
		}
		return flag;
	}

	@SuppressWarnings("unlikely-arg-type")
	public static boolean compareTwoLinkedDoubleListHashMap(LinkedList<LinkedHashMap<String, Double>> expectedList,
			LinkedList<LinkedHashMap<String, Double>> actualList, String[]... noNeed) {
		int size = expectedList.size();
		if (noNeed.length > 0) {
			for (int i = 0; i < noNeed.length; i++) {
				System.out.println("Removed Key from Expected:: " + noNeed[i][i]);
				expectedList.remove(noNeed[i][i]);
				System.out.println("Removed Key from Actual  :: " + noNeed[i][i]);
				actualList.remove(noNeed[i][i]);
			}
		}
		boolean flag = true;
		try {
			for (int i = 0; i < size; i++) {
				if (!Utils.compareTwoDoubleHashMap(expectedList.get(i), actualList.get(i)))
					flag = false;
			}
		} catch (NullPointerException np) {
			return false;
		}
		return flag;
	}

	public static boolean compareTwoList1(List<String> expectedElements, List<String> actualElements) {
		boolean statusToBeReturned = false;
		List<String> missedList = new ArrayList<String>();
		for (int i = 0; i < expectedElements.size(); i++) {
			if (!(expectedElements.get(i).equals(actualElements.get(i)))) {
				statusToBeReturned = false;
				missedList.add(expectedElements.get(i));
			}
		}
		Log.failsoft("Missing element on this page:: " + missedList);
		return statusToBeReturned;
	}

	/**
	 * To sort LinkedList of Product
	 * 
	 * @param actualList
	 * @return
	 * @throws Exception
	 */
	public static LinkedList<LinkedHashMap<String, String>> sortLinkedListProduct(LinkedList<LinkedHashMap<String, String>> actualList) throws Exception {
		LinkedList<LinkedHashMap<String, String>> listToReturn = new LinkedList<LinkedHashMap<String, String>>();

		// actualList = makeUnique(actualList);

		LinkedList<String> list = new LinkedList<String>();

		LinkedList<String> listSize = new LinkedList<String>();
		LinkedList<String> listColor = new LinkedList<String>();
		int size = actualList.size();
		for (int x = 0; x < size; x++) {
			if (!actualList.get(x).get("ProductName").contains("E-Gift Card")) {
				list.add(actualList.get(x).get("ProductName") + "_" + actualList.get(x).get("Size") + "_"
						+ actualList.get(x).get("Color"));
			} else {
				list.add(actualList.get(x).get("ProductName") + "_NA_NA");
			}
			// listSize.add(actualList.get(x).get("Size"));

		}
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return Collator.getInstance().compare(o1, o2);
			}
		});
		for (int i = 0; i < size; i++) {

			listSize.add(list.get(i).split("_")[1]);
			listColor.add(list.get(i).split("_")[2]);
		}

		for (int i = 0; i < size && listToReturn.size() < size; i++) {
			for (int j = 0; j < size && listToReturn.size() < size; j++) {
				System.out.println();
				if (list.get(i).split("_")[0].equals(actualList.get(j).get("ProductName"))) {
					if (listSize.get(i).equals(actualList.get(j).get("Size"))
							&& listColor.get(i).equals(actualList.get(j).get("Color"))) {
						listToReturn.add(actualList.get(j));
						break;
					}

				}
			}
		}

		// //printing sorted list
		/*
		 * System.out.println("-----------------------------------------");
		 * for(int y = 0 ;y < size; y++) System.out.println(list.get(y));
		 * System.out.println("-----------------------------------------");
		 */
		return listToReturn;
	}

	/**
	 * To sort LinkedList of address
	 * 
	 * @param actualList
	 * @return
	 * @throws Exception
	 */
	public static LinkedList<LinkedHashMap<String, String>> sortLinkedListAddress(
			LinkedList<LinkedHashMap<String, String>> actualList) throws Exception {
		LinkedList<LinkedHashMap<String, String>> listToReturn = new LinkedList<LinkedHashMap<String, String>>();
		actualList = makeUniqueAddress(actualList);
		LinkedList<String> list = new LinkedList<String>();
		int size = actualList.size();
		for (int x = 0; x < size; x++)
			list.add(actualList.get(x).get("FirstName"));

		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return Collator.getInstance().compare(o1, o2);
			}
		});

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (actualList.size() == 1) {
					LinkedHashMap<String, String> listToAdd = new LinkedHashMap<String, String>(actualList.get(i));
					listToReturn.add(listToAdd);
					continue;
				} else if (list.get(i).equals(actualList.get(j).get("FirstName"))) {
					LinkedHashMap<String, String> listToAdd = new LinkedHashMap<String, String>(actualList.get(i));
					listToReturn.add(listToAdd);
					continue;
				}
			}
		}

		/*
		 * //printing sorted list
		 * System.out.println("-----------------------------------------");
		 * for(int y = 0 ;y < size; y++) System.out.println(list.get(y));
		 * System.out.println("-----------------------------------------");
		 */
		return listToReturn;
	}

	/**
	 * To make the linked list of linkedHash map unique
	 * 
	 * @param hashMap
	 * @return
	 * @throws Exception
	 * 
	 *             created by Dhanapal.K
	 */
	public static LinkedList<LinkedHashMap<String, String>> makeUnique(
			LinkedList<LinkedHashMap<String, String>> hashMap) throws Exception {
		int nosProduct = hashMap.size();
		for (int i = 0; i < nosProduct; i++) {
			for (int j = i + 1; j < nosProduct; j++) {
				if (hashMap.get(i).get("ProductName").equals(hashMap.get(j).get("ProductName")))
					if (hashMap.get(i).get("Color").equals(hashMap.get(j).get("Color")))
						if (hashMap.get(i).get("Size").equals(hashMap.get(j).get("Size"))) {
							int qty = Integer.parseInt(hashMap.get(i).get("Quantity"))
									+ Integer.parseInt(hashMap.get(j).get("Quantity"));
							hashMap.get(i).put("Quantity", Integer.toString(qty));
							hashMap.remove(j);
							nosProduct = hashMap.size();
							j--;
						}
			}
		}

		return hashMap;
	}

	public static LinkedList<LinkedHashMap<String, String>> makeUniqueAddress(
			LinkedList<LinkedHashMap<String, String>> hashMap) throws Exception {
		int nosProduct = hashMap.size();
		for (int i = 0; i < nosProduct; i++) {
			for (int j = i + 1; j < nosProduct; j++) {
				System.out.print("i value " + hashMap.get(i).get("FirstName"));
				System.out.print("j value " + hashMap.get(j).get("FirstName"));
				if (hashMap.toString().contains("FirstName")) {
					if (hashMap.get(i).get("FirstName").equals(hashMap.get(j).get("FirstName")))
						if (hashMap.get(i).get("Address").equals(hashMap.get(j).get("Address"))) {
							hashMap.remove(j);
							nosProduct = hashMap.size();
							j--;

						}
				} else {
					hashMap.remove(j);
					nosProduct = hashMap.size();
					j--;
				}
			}
		}
		return hashMap;
	}

	public static LinkedList<LinkedHashMap<String, String>> makeUniqueAddress1(
			LinkedList<LinkedHashMap<String, String>> hashMap) throws Exception {
		int nosProduct = hashMap.size();
		for (int i = 0; i < nosProduct; i++) {
			for (int j = i + 1; j < nosProduct; j++) {
				System.out.print("i value " + hashMap.get(i).get("FirstName"));
				System.out.print("j value " + hashMap.get(j).get("FirstName"));
				if (hashMap.toString().contains("FirstName")) {
					if (hashMap.get(i).get("FirstName").equals(hashMap.get(j).get("FirstName")))
						if (hashMap.get(i).get("Address").equals(hashMap.get(j).get("Address"))) {
							hashMap.remove(j);
							nosProduct = hashMap.size();
							j--;

						}
				} else {
					hashMap.remove(j);
					nosProduct = hashMap.size();
					j--;
				}
			}
		}
		return hashMap;
	}

	/**
	 * Wait until element disappears in the page
	 * 
	 * @param driver
	 *            - driver instance
	 * @param element
	 *            - webelement to wait to have disaapear
	 * @return true if element is not appearing in the page
	 */
	public static boolean waitUntilElementDisappear(WebDriver driver, final WebElement element, int maxWait) {
		final boolean isNotDisplayed;

		WebDriverWait wait = (WebDriverWait) new WebDriverWait(driver, Duration.ofSeconds(maxWait));
		isNotDisplayed = wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver webDriver) {
				boolean isPresent = false;
				try {
					if (element.isDisplayed()) {
						isPresent = false;
						Log.event("Element " + element.toString() + ", is still visible in page");
					}
				} catch (Exception ex) {
					isPresent = true;
					Log.event("Element " + element.toString() + ", is not displayed in page ");
					return isPresent;
				}
				return isPresent;
			}
		});
		return isNotDisplayed;
	}

	/**
	 * To generate random number from '0 to Maximum Value' or 'Minimum Value
	 * ---- Maximum Value'
	 * 
	 * @param max
	 *            - maximum bound
	 * @param min
	 *            - origin bound
	 * @return - random number between 'min to max' or '0 to max'
	 * @throws Exception
	 */
	public static int getRandom(int min, int max) throws Exception {
		Random random = new Random();
		int rand;
		if (min == 0)
			rand = random.nextInt(max);
		else
			rand = ThreadLocalRandom.current().nextInt(min, max);

		return rand;
	}

	/**
	 * To verify the page url contains the given word
	 * 
	 * @param driver
	 * @param stringContains
	 * @return boolean
	 */
	public static boolean verifyPageURLContains(final WebDriver driver, String hostURL, String stringContains) {
		boolean status = false;
		String url = null;
		try {
			url = driver.getCurrentUrl();
			if (url == null) {
				url = ((JavascriptExecutor) driver).executeScript("return document.URL;").toString();
			}
		} catch (Exception e) {
			url = ((JavascriptExecutor) driver).executeScript("return document.URL;").toString();
			// TODO: handle exception
		}
		if (url.contains("production")) {
			if (url.contains(hostURL.split("https://storefront:dcp-preview@")[1]) && url.contains(stringContains)) {
				status = true;
			}
		} else if (url.contains("wow")) {
			if (url.contains(hostURL.split("https://storefront:almostthere@")[1]) && url.contains(stringContains)) {
				status = true;
			}
		} else if (hostURL.contains("https://")) {
			if (url.contains(hostURL.split("https://")[1]) && url.contains(stringContains)) {
				status = true;
			}
		} else if (hostURL.contains("http://")) {
			if (url.contains(hostURL.split("http://")[1]) && url.contains(stringContains)) {
				status = true;
			}
		}

		return status;
	}

	/**
	 * Round to certain number of decimals
	 * 
	 * @param d
	 * @param decimalPlace
	 *            the numbers of decimals
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static float round(double d, int decimalPlace) {
		return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	/**
	 * checkPriceWithDollar
	 * 
	 * @param price
	 * @return
	 */
	public static String checkPriceWithDollar(String price) {

		if (!price.startsWith("$")) {
			Log.failsoft(price + " does not contains '$' value");
		}

		return price;
	}

	/**
	 * copyHashMap
	 * 
	 * @param actual
	 * @param ignore
	 * @return
	 * @throws Exception
	 */
	public static LinkedHashMap<String, String> copyHashMap(LinkedHashMap<String, String> actual, String ignore)
			throws Exception {
		List<String> indexes = new ArrayList<String>(actual.keySet());
		LinkedHashMap<String, String> expected = new LinkedHashMap<String, String>();

		for (int i = 0; i < indexes.size(); i++) {
			if (!indexes.get(i).equals(ignore))
				expected.put(indexes.get(i), actual.get(indexes.get(i)));
		}

		return expected;
	}

	/**
	 * copyLinkedListHashMap
	 * 
	 * @param actual
	 * @param ignore
	 * @return
	 * @throws Exception
	 */
	public static LinkedList<LinkedHashMap<String, String>> copyLinkedListHashMap(
			LinkedList<LinkedHashMap<String, String>> actual, String ignore) throws Exception {
		int size = actual.size();
		LinkedList<LinkedHashMap<String, String>> expected = new LinkedList<LinkedHashMap<String, String>>();
		for (int j = 0; j < size; j++) {
			List<String> indexes = new ArrayList<String>(actual.get(j).keySet());
			LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
			for (int i = 0; i < indexes.size(); i++) {
				if (!indexes.get(i).equals(ignore))
					hashMap.put(indexes.get(i), actual.get(j).get(indexes.get(i)));
			}
			expected.add(hashMap);
		}
		return expected;
	}

	public static boolean sortCompartPrintPaymentDetails(
			LinkedHashMap<String, LinkedHashMap<String, String>> paymentDetails1,
			LinkedHashMap<String, LinkedHashMap<String, String>> paymentDetails2, String[]... ignore) throws Exception {
		boolean flag = true;
		List<String> outterIndexOfFirst = new ArrayList<String>(paymentDetails1.keySet());
		List<String> outterIndexOfSecond = new ArrayList<String>(paymentDetails2.keySet());

		if (outterIndexOfFirst.toString().contains("GiftCard")) {
			LinkedList<LinkedHashMap<String, String>> giftCardDetailsInFirst = new LinkedList<LinkedHashMap<String, String>>();
			LinkedList<LinkedHashMap<String, String>> giftCardDetailsInSecond = new LinkedList<LinkedHashMap<String, String>>();

			for (int i = 0; i < outterIndexOfFirst.size(); i++) {
				if (outterIndexOfFirst.get(i).contains("GiftCard")) {
					giftCardDetailsInFirst.add(paymentDetails1.get(outterIndexOfFirst.get(i)));
				}

				if (outterIndexOfSecond.get(i).contains("GiftCard")) {
					giftCardDetailsInSecond.add(paymentDetails2.get(outterIndexOfSecond.get(i)));
				}
			}

			flag = compareTwoLinkedListHashMap(giftCardDetailsInFirst, giftCardDetailsInSecond, ignore);

		}

		if (outterIndexOfFirst.toString().contains("BRD")) {
			LinkedList<LinkedHashMap<String, String>> giftCardDetailsInFirst = new LinkedList<LinkedHashMap<String, String>>();
			LinkedList<LinkedHashMap<String, String>> giftCardDetailsInSecond = new LinkedList<LinkedHashMap<String, String>>();

			for (int i = 0; i < outterIndexOfFirst.size(); i++) {
				if (outterIndexOfFirst.get(i).contains("BRD")) {
					giftCardDetailsInFirst.add(paymentDetails1.get(outterIndexOfFirst.get(i)));
				}

				if (outterIndexOfSecond.get(i).contains("BRD")) {
					giftCardDetailsInSecond.add(paymentDetails2.get(outterIndexOfSecond.get(i)));
				}
			}

			flag = compareTwoLinkedListHashMap(giftCardDetailsInFirst, giftCardDetailsInSecond);
		}

		if (outterIndexOfFirst.toString().contains("CreditCard")) {
			LinkedList<LinkedHashMap<String, String>> giftCardDetailsInFirst = new LinkedList<LinkedHashMap<String, String>>();
			LinkedList<LinkedHashMap<String, String>> giftCardDetailsInSecond = new LinkedList<LinkedHashMap<String, String>>();

			for (int i = 0; i < outterIndexOfFirst.size(); i++) {
				if (outterIndexOfFirst.get(i).contains("CreditCard")) {
					giftCardDetailsInFirst.add(paymentDetails1.get(outterIndexOfFirst.get(i)));
				}

				if (outterIndexOfSecond.get(i).contains("CreditCard")) {
					giftCardDetailsInSecond.add(paymentDetails2.get(outterIndexOfSecond.get(i)));
				}
			}

			flag = compareTwoLinkedListHashMap(giftCardDetailsInFirst, giftCardDetailsInSecond);
		}

		return flag;
	}

	/**
	 * To get browser name
	 * 
	 * @param driver
	 *            : Webdriver
	 * @return
	 * @throws Exception
	 */
	public static String getBrowserName(final WebDriver driver) throws Exception {
		String browserName = ((RemoteWebDriver) driver).getCapabilities().getBrowserName();
		return browserName;
	}

	public static boolean compareAndPrintTableLinkedListHashMap(String title, String col1Head, String col2Head,
			LinkedList<LinkedHashMap<String, String>> hashMap1, LinkedList<LinkedHashMap<String, String>> hashMap2,
			String[]... noNeed) throws Exception {
		// int iteration1 = hashMap1.size();
		int iteration2 = hashMap2.size();
		boolean flag = true;
		boolean flag1 = true;
		for (int j = 0; j < iteration2; j++) {
			if (noNeed.length > 0) {
				for (int i = 0; i < noNeed[0].length; i++) {
					System.out.println("Removed Key from Expected:: " + noNeed[0][i]);
					hashMap1.get(j).remove(noNeed[0][i]);
					System.out.println("Removed Key from Actual  :: " + noNeed[0][i]);
					hashMap2.get(j).remove(noNeed[0][i]);
				}
			}

			Log.message("<br>");
			Log.message1("<table><tr bgcolor='#BBBBBB' align='center'><td colspan=3><b><font color='black'>" + title
					+ "(" + (j + 1) + ")</font></b></td></tr>");
			Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>" + col1Head
					+ "</td><td>" + col2Head + "</td></tr>");
			List<String> indexes1 = new ArrayList<String>(hashMap1.get(j).keySet());
			List<String> indexes2 = new ArrayList<String>(hashMap2.get(j).keySet());
			List<String> maxIndex = indexes1.size() > indexes2.size() ? indexes1 : indexes2;
			for (int i = 0; i < maxIndex.size(); i++) {
				String value1 = hashMap1.get(j).containsKey(maxIndex.get(i))
						? hashMap1.get(j).get(maxIndex.get(i)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				String value2 = hashMap2.get(j).containsKey(maxIndex.get(i))
						? hashMap2.get(j).get(maxIndex.get(i)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				if (value1 != null || value2 != null) {

					if (value1.equals(value2))
						Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i) + "</td><td>"
								+ value1 + "</td><td>" + value2 + "</td></tr>");
					else
						Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i)
								+ "</td><td bgcolor='red'>" + value1 + "</td><td>" + value2 + "</td></tr>");
				}
			}
			Log.message1("</table>");

			flag = Utils.compareTwoHashMap(hashMap1.get(j), hashMap2.get(j));
			if (!flag) {
				flag1 = flag;
			}
		}

		return flag1;
	}

	public static boolean compareAndPrintTableLinkedListHashMapDoubleDiscount(String title, String col1Head,
			String col2Head, LinkedList<LinkedHashMap<String, String>> hashMap1,
			LinkedList<LinkedHashMap<String, String>> hashMap2, String[]... noNeed) throws Exception {
		// int iteration1 = hashMap1.size();
		int iteration2 = hashMap2.size();
		boolean flag = true;
		boolean flag1 = true;
		int count = 0;
		for (int j = 0; j < iteration2; j++) {
			if (noNeed.length > 0) {
				for (int i = 0; i < noNeed[0].length; i++) {
					System.out.println("Removed Key from Expected:: " + noNeed[0][i]);
					hashMap1.get(j).remove(noNeed[0][i]);
					System.out.println("Removed Key from Actual  :: " + noNeed[0][i]);
					hashMap2.get(j).remove(noNeed[0][i]);
				}
			}

			Log.message("<br>");
			Log.message1("<table><tr bgcolor='#BBBBBB' align='center'><td colspan=3><b><font color='black'>" + title
					+ "(" + (j + 1) + ")</font></b></td></tr>");
			Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>" + col1Head
					+ "</td><td>" + col2Head + "</td></tr>");
			List<String> indexes1 = new ArrayList<String>(hashMap1.get(j).keySet());
			List<String> indexes2 = new ArrayList<String>(hashMap2.get(j).keySet());
			count = 0;
			List<String> maxIndex = indexes1.size() > indexes2.size() ? indexes1 : indexes2;
			for (int i = 0; i < maxIndex.size(); i++) {

				String value1 = hashMap1.get(j).containsKey(maxIndex.get(i))
						? hashMap1.get(j).get(maxIndex.get(i)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				String value2 = hashMap2.get(j).containsKey(maxIndex.get(i))
						? hashMap2.get(j).get(maxIndex.get(i)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				if (value1 != null || value2 != null) {
					Float diffrence = (float) 0;

					if (!(value1.contains("%") || value1.contains("%") || value1.toLowerCase().equals("na"))) {
						Float value1Float = Float.parseFloat(value1);
						Float value2Float = Float.parseFloat(value2);

						if (value1Float > value2Float)
							diffrence = value1Float - value2Float;
						else
							diffrence = value2Float - value2Float;
					}

					if (value1.equals(value2) || (diffrence < 0.02)) {
						Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i) + "</td><td>"
								+ value1 + "</td><td>" + value2 + "</td></tr>");
						count++;
					} else
						Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i)
								+ "</td><td bgcolor='red'>" + value1 + "</td><td>" + value2 + "</td></tr>");
				}
			}
			Log.message1("</table>");

			if (count == maxIndex.size())
				flag = true;
			else
				flag = Utils.compareTwoHashMap(hashMap1.get(j), hashMap2.get(j));
			if (!flag) {
				flag1 = flag;
			}
		}

		return flag1;
	}

	public static boolean containsShipHashAndPrintTableLinkedListHashMap(String title, String col1Head, String col2Head,
			LinkedList<LinkedHashMap<String, String>> hashMap1, LinkedList<LinkedHashMap<String, String>> hashMap2,
			String[]... noNeed) throws Exception {
		hashMap2.size();
		int check;
		int x;
		int y = 0;
		boolean flag = false;
		int j = 0;
		int count = 0;
		if (noNeed.length > 0) {
			for (int i = 0; i < noNeed[0].length; i++) {
				System.out.println("Removed Key from Expected:: " + noNeed[0][i]);
				hashMap1.get(j).remove(noNeed[0][i]);
				System.out.println("Removed Key from Actual  :: " + noNeed[0][i]);
				hashMap2.get(j).remove(noNeed[0][i]);
			}
		}

		List<String> indexes1 = new ArrayList<String>(hashMap1.get(j).keySet());
		List<String> indexes2 = new ArrayList<String>(hashMap2.get(j).keySet());
		for (check = 0; check < indexes2.size(); check++) {
			if (indexes2.get(check).contains("last-name")) {
				break;
			}
		}
		List<String> maxIndex = indexes1.size() > indexes2.size() ? indexes1 : indexes2;

		int MaxSize = hashMap1.size() > hashMap2.size() ? hashMap1.size() : hashMap2.size();
		int MinSize = hashMap1.size() < hashMap2.size() ? hashMap1.size() : hashMap2.size();

		for (x = 0; x < MaxSize; x++) {
			for (y = 0; y < MinSize; y++) {
				String value1 = hashMap1.get(y).containsKey(maxIndex.get(1))
						? hashMap1.get(y).get(maxIndex.get(check)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				String value2 = hashMap2.get(x).containsKey(maxIndex.get(1))
						? hashMap2.get(x).get(maxIndex.get(check)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				if (value1 != null || value2 != null) {
					if (value1.equals(value2)) {
						count++;
						Log.message("<br>");
						Log.message1("<table><tr bgcolor='#BBBBBB' align='center'><td colspan=3><b><font color='black'>"
								+ title + "(" + (j + 1) + ")</font></b></td></tr>");
						Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>"
								+ col1Head + "</td><td>" + col2Head + "</td></tr>");
						for (int n = 0; n < maxIndex.size(); n++) {
							String exactvalue1 = hashMap1.get(y).containsKey(maxIndex.get(n))
									? hashMap1.get(y).get(maxIndex.get(n)).replaceAll("-", "").toLowerCase().trim()
									: "<font color='red'>No Value</font>";
							String exactvalue2 = hashMap2.get(x).containsKey(maxIndex.get(n))
									? hashMap2.get(x).get(maxIndex.get(n)).replaceAll("-", "").toLowerCase().trim()
									: "<font color='red'>No Value</font>";
							if (exactvalue1 != null || exactvalue2 != null) {
								if (exactvalue1.equals(exactvalue2))
									Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(n)
											+ "</td><td>" + exactvalue1 + "</td><td>" + exactvalue2 + "</td></tr>");
								else {
									Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(n)
											+ "</td><td bgcolor='red'>" + exactvalue1 + "</td><td>" + exactvalue2
											+ "</td></tr>");
								}

							}
						}
						flag = Utils.productcompareTwoHashMap(hashMap2.get(x), hashMap1.get(y));
						Log.message1("</table>");

					}

				}

			}
			if (count == MaxSize) {
				break;
			}

		}
		return flag;
	}

	public static boolean CheckHashcontainsAndPrintTableLinkedListHashMap(String title, String col1Head,
			String col2Head, LinkedList<LinkedHashMap<String, String>> hashMap1,
			LinkedList<LinkedHashMap<String, String>> hashMap2, String[]... noNeed) throws Exception {
		hashMap2.size();
		int check;
		int x;
		int y = 0;
		boolean flag = true;
		int j = 0;
		int count = 0;
		if (noNeed.length > 0) {
			for (int i = 0; i < noNeed[0].length; i++) {
				System.out.println("Removed Key from Expected:: " + noNeed[0][i]);
				hashMap1.get(j).remove(noNeed[0][i]);
				System.out.println("Removed Key from Actual  :: " + noNeed[0][i]);
				hashMap2.get(j).remove(noNeed[0][i]);
			}
		}

		List<String> indexes1 = new ArrayList<String>(hashMap1.get(j).keySet());
		List<String> indexes2 = new ArrayList<String>(hashMap2.get(j).keySet());
		for (check = 0; check < indexes2.size(); check++) {
			if (indexes2.get(check).contains("product-id")) {
				break;
			}
		}
		List<String> maxIndex = indexes1.size() > indexes2.size() ? indexes1 : indexes2;

		int MaxSize = hashMap1.size() > hashMap2.size() ? hashMap1.size() : hashMap2.size();
		int MinSize = hashMap1.size() < hashMap2.size() ? hashMap1.size() : hashMap2.size();

		for (x = 0; x < MaxSize; x++) {
			for (y = 0; y < MinSize; y++) {
				String value1 = hashMap1.get(y).containsKey(maxIndex.get(1))
						? hashMap1.get(y).get(maxIndex.get(check)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				String value2 = hashMap2.get(x).containsKey(maxIndex.get(1))
						? hashMap2.get(x).get(maxIndex.get(check)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				if (value1 != null || value2 != null) {
					if (value1.equals(value2)) {
						count++;
						Log.message("<br>");
						Log.message1("<table><tr bgcolor='#BBBBBB' align='center'><td colspan=3><b><font color='black'>"
								+ title + "(" + (j + 1) + ")</font></b></td></tr>");
						Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>"
								+ col1Head + "</td><td>" + col2Head + "</td></tr>");
						for (int n = 0; n < maxIndex.size(); n++) {
							String exactvalue1 = hashMap1.get(y).containsKey(maxIndex.get(n))
									? hashMap1.get(y).get(maxIndex.get(n)).replaceAll("-", "").toLowerCase().trim()
									: "<font color='red'>No Value</font>";
							String exactvalue2 = hashMap2.get(x).containsKey(maxIndex.get(n))
									? hashMap2.get(x).get(maxIndex.get(n)).replaceAll("-", "").toLowerCase().trim()
									: "<font color='red'>No Value</font>";
							if (exactvalue1 != null || exactvalue2 != null) {
								if (exactvalue1.equals(exactvalue2))
									Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(n)
											+ "</td><td>" + exactvalue1 + "</td><td>" + exactvalue2 + "</td></tr>");
								else {
									Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(n)
											+ "</td><td bgcolor='red'>" + exactvalue1 + "</td><td>" + exactvalue2
											+ "</td></tr>");
								}

							}
						}
						if (flag) {
							flag = Utils.productcompareTwoHashMap(hashMap2.get(x), hashMap1.get(y));
						}
						Log.message1("</table>");

					}

				}

			}
			if (count == MaxSize) {
				break;
			}

		}
		return flag;
	}

	public static boolean checkHashcontainsAndPrintTableLinkedListHashMapDoubleDiscount(String title, String col1Head,
			String col2Head, LinkedList<LinkedHashMap<String, String>> hashMap1,
			LinkedList<LinkedHashMap<String, String>> hashMap2, String[]... noNeed) throws Exception {
		hashMap2.size();
		int check;
		int x;
		int y = 0;
		boolean flag = true;
		int j = 0;
		int count = 0;
		if (noNeed.length > 0) {
			for (int i = 0; i < noNeed[0].length; i++) {
				System.out.println("Removed Key from Expected:: " + noNeed[0][i]);
				hashMap1.get(j).remove(noNeed[0][i]);
				System.out.println("Removed Key from Actual  :: " + noNeed[0][i]);
				hashMap2.get(j).remove(noNeed[0][i]);
			}
		}

		List<String> indexes1 = new ArrayList<String>(hashMap1.get(j).keySet());
		List<String> indexes2 = new ArrayList<String>(hashMap2.get(j).keySet());
		for (check = 0; check < indexes2.size(); check++) {
			if (indexes2.get(check).contains("product-id")) {
				break;
			}
		}
		List<String> maxIndex = indexes1.size() > indexes2.size() ? indexes1 : indexes2;

		int MaxSize = hashMap1.size() > hashMap2.size() ? hashMap1.size() : hashMap2.size();
		int MinSize = hashMap1.size() < hashMap2.size() ? hashMap1.size() : hashMap2.size();

		for (x = 0; x < MaxSize; x++) {
			for (y = 0; y < MinSize; y++) {
				String value1 = hashMap1.get(y).containsKey(maxIndex.get(1))
						? hashMap1.get(y).get(maxIndex.get(check)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				String value2 = hashMap2.get(x).containsKey(maxIndex.get(1))
						? hashMap2.get(x).get(maxIndex.get(check)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";
				if (value1 != null || value2 != null) {
					if (value1.equals(value2)) {
						count++;
						Log.message("<br>");
						Log.message1("<table><tr bgcolor='#BBBBBB' align='center'><td colspan=3><b><font color='black'>"
								+ title + "(" + (j + 1) + ")</font></b></td></tr>");
						Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>"
								+ col1Head + "</td><td>" + col2Head + "</td></tr>");
						int count2 = 0;
						for (int n = 0; n < maxIndex.size(); n++) {
							String exactvalue1 = hashMap1.get(y).containsKey(maxIndex.get(n))
									? hashMap1.get(y).get(maxIndex.get(n)).replaceAll("-", "").toLowerCase().trim()
									: "<font color='red'>No Value</font>";
							String exactvalue2 = hashMap2.get(x).containsKey(maxIndex.get(n))
									? hashMap2.get(x).get(maxIndex.get(n)).replaceAll("-", "").toLowerCase().trim()
									: "<font color='red'>No Value</font>";

							if (exactvalue1 != null || exactvalue2 != null) {
								float extvalue1 = Float.parseFloat(exactvalue1);
								float extvalue2 = Float.parseFloat(exactvalue2);
								float diffrence = 0;
								if (extvalue1 > extvalue2)
									diffrence = extvalue1 - extvalue2;
								else
									diffrence = extvalue2 - extvalue1;

								if (exactvalue1.equals(exactvalue2) || diffrence <= 0.01) {
									Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(n)
											+ "</td><td>" + exactvalue1 + "</td><td>" + exactvalue2 + "</td></tr>");
									count2++;
								} else {
									Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(n)
											+ "</td><td bgcolor='red'>" + exactvalue1 + "</td><td>" + exactvalue2
											+ "</td></tr>");
								}

							}
						}
						if (flag) {
							if (count2 == maxIndex.size())
								flag = true;
							else
								flag = Utils.productcompareTwoHashMap(hashMap2.get(x), hashMap1.get(y));
						}
						Log.message1("</table>");

					}

				}

			}
			if (count == MaxSize) {
				break;
			}

		}
		return flag;
	}

	public static boolean compareAndPrintTableLinkedDoubleListHashMap(String title, String col1Head, String col2Head,
			LinkedList<LinkedHashMap<String, Double>> hashMap1, LinkedList<LinkedHashMap<String, Double>> hashMap2,
			String[]... noNeed) throws Exception {
		// int iteration1 = hashMap1.size();
		int iteration2 = hashMap2.size();
		boolean flag = true;
		boolean flag1 = true;
		for (int j = 0; j < iteration2; j++) {
			if (noNeed.length > 0) {
				for (int i = 0; i < noNeed[0].length; i++) {
					System.out.println("Removed Key from Expected:: " + noNeed[0][i]);
					hashMap1.get(j).remove(noNeed[0][i]);
					System.out.println("Removed Key from Actual  :: " + noNeed[0][i]);
					hashMap2.get(j).remove(noNeed[0][i]);
				}
			}

			Log.message("<br>");
			Log.message1("<table><tr bgcolor='#BBBBBB' align='center'><td colspan=3><b><font color='black'>" + title
					+ "(" + (j + 1) + ")</font></b></td></tr>");
			Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>" + col1Head
					+ "</td><td>" + col2Head + "</td></tr>");
			List<String> indexes1 = new ArrayList<String>(hashMap1.get(j).keySet());
			List<String> indexes2 = new ArrayList<String>(hashMap2.get(j).keySet());
			List<String> maxIndex = indexes1.size() > indexes2.size() ? indexes1 : indexes2;
			for (int i = 0; i < maxIndex.size(); i++) {
				Double value1 = (Double) (hashMap1.get(j).containsKey(maxIndex.get(i))
						? hashMap1.get(j).get(maxIndex.get(i)) : "<font color='red'>No Value</font>");
				Double value2 = (Double) (hashMap2.get(j).containsKey(maxIndex.get(i))
						? hashMap2.get(j).get(maxIndex.get(i)) : "<font color='red'>No Value</font>");
				if (value1 != null || value2 != null) {
					if (value1.equals(value2))
						Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i) + "</td><td>"
								+ value1 + "</td><td>" + value2 + "</td></tr>");
					else
						Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i)
								+ "</td><td bgcolor='red'>" + value1 + "</td><td>" + value2 + "</td></tr>");
				}
			}
			Log.message1("</table>");

			flag = Utils.compareTwoDoubleHashMap(hashMap1.get(j), hashMap2.get(j));
			if (!flag) {
				flag1 = flag;
			}
		}

		return flag1;
	}

	public static boolean compareAndPrintTableLinkedDoubleListHashMapDoubleDiscount(String title, String col1Head,
			String col2Head, LinkedList<LinkedHashMap<String, Double>> hashMap1,
			LinkedList<LinkedHashMap<String, Double>> hashMap2, String[]... noNeed) throws Exception {
		// int iteration1 = hashMap1.size();
		int iteration2 = hashMap2.size();
		boolean flag = true;
		boolean flag1 = true;
		for (int j = 0; j < iteration2; j++) {
			if (noNeed.length > 0) {
				for (int i = 0; i < noNeed[0].length; i++) {
					System.out.println("Removed Key from Expected:: " + noNeed[0][i]);
					hashMap1.get(j).remove(noNeed[0][i]);
					System.out.println("Removed Key from Actual  :: " + noNeed[0][i]);
					hashMap2.get(j).remove(noNeed[0][i]);
				}
			}

			Log.message("<br>");
			Log.message1("<table><tr bgcolor='#BBBBBB' align='center'><td colspan=3><b><font color='black'>" + title
					+ "(" + (j + 1) + ")</font></b></td></tr>");
			Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>" + col1Head
					+ "</td><td>" + col2Head + "</td></tr>");
			List<String> indexes1 = new ArrayList<String>(hashMap1.get(j).keySet());
			List<String> indexes2 = new ArrayList<String>(hashMap2.get(j).keySet());
			List<String> maxIndex = indexes1.size() > indexes2.size() ? indexes1 : indexes2;
			int count = 0;
			for (int i = 0; i < maxIndex.size(); i++) {
				// count=0;
				Double value1 = (Double) (hashMap1.get(j).containsKey(maxIndex.get(i))
						? hashMap1.get(j).get(maxIndex.get(i)) : "<font color='red'>No Value</font>");
				Double value2 = (Double) (hashMap2.get(j).containsKey(maxIndex.get(i))
						? hashMap2.get(j).get(maxIndex.get(i)) : "<font color='red'>No Value</font>");
				// boolean flag2 = false;
				if (value1 != null || value2 != null) {
					Double diffrence = (double) 0;
					if (value1 > value2)
						diffrence = value1 - value2;
					else
						diffrence = value2 - value1;

					if (value1.equals(value2) || (diffrence < 0.02)) {
						Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i) + "</td><td>"
								+ value1 + "</td><td>" + value2 + "</td></tr>");
						count++;
					} else
						Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i)
								+ "</td><td bgcolor='red'>" + value1 + "</td><td>" + value2 + "</td></tr>");
				}
			}
			Log.message1("</table>");

			flag = Utils.compareTwoDoubleHashMap(hashMap1.get(j), hashMap2.get(j));
			if (count == maxIndex.size())
				flag1 = true;
			else if (!flag) {
				flag1 = flag;
			}
		}

		return flag1;
	}

	public static boolean compareTwoDoubleHashMap(Map<String, Double> expectedList, Map<String, Double> actualList) {
		List<String> missedkey = new ArrayList<String>();
		HashMap<String, Double> missedvalue = new HashMap<String, Double>();
		try {
			for (String k : expectedList.keySet()) {
				if (!(actualList.get(k).equals(expectedList.get(k)))) {
					missedvalue.put(k, actualList.get(k));
					Log.event("Missed Values:: " + missedvalue);
					return false;
				}
			}
			for (String y : actualList.keySet()) {
				if (!expectedList.containsKey(y)) {
					missedkey.add(y);
					Log.event("Missed keys:: " + missedkey);
					return false;
				}
			}
		} catch (NullPointerException np) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unlikely-arg-type")
	public static boolean compareTwoDoubleLinkedListHashMap(LinkedList<LinkedHashMap<String, String>> expectedList,
			LinkedList<LinkedHashMap<String, String>> actualList, String[]... noNeed) {
		int size = expectedList.size();
		if (noNeed.length > 0) {
			for (int i = 0; i < noNeed.length; i++) {
				System.out.println("Removed Key from Expected:: " + noNeed[i][i]);
				expectedList.remove(noNeed[i][i]);
				System.out.println("Removed Key from Actual  :: " + noNeed[i][i]);
				actualList.remove(noNeed[i][i]);
			}
		}
		boolean flag = true;
		try {
			for (int i = 0; i < size; i++) {
				if (!Utils.compareTwoHashMap(expectedList.get(i), actualList.get(i)))
					flag = false;
			}
		} catch (NullPointerException np) {
			return false;
		}
		return flag;
	}

	public static boolean compareAndPrintTableHashMap(String title, String col1Head, String col2Head,
			LinkedHashMap<String, String> hashMap1, LinkedHashMap<String, String> hashMap2, String[]... noNeed)
			throws Exception {

		if (noNeed.length > 0) {
			for (int i = 0; i < noNeed[0].length; i++) {
				System.out.println("Removed Key from <b>Expected:: " + noNeed[0][i]);
				hashMap1.remove(noNeed[0][i]);
				System.out.println("Removed Key from Actual  :: " + noNeed[0][i]);
				hashMap2.remove(noNeed[0][i]);
			}
		}

		Log.message("<br>");
		Log.message1("<table><tr bgcolor='#BBBBBB'  align='center'><td colspan=3><b><font color='black'>" + title
				+ "</font></b></td></tr>");
		Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>" + col1Head
				+ "</td><td>" + col2Head + "</td></tr>");
		List<String> indexes1 = new ArrayList<String>(hashMap1.keySet());
		List<String> indexes2 = new ArrayList<String>(hashMap2.keySet());
		List<String> maxIndex = indexes1.size() > indexes2.size() ? indexes1 : indexes2;
		for (int i = 0; i < maxIndex.size(); i++) {
			String value1 = hashMap1.containsKey(maxIndex.get(i)) ? hashMap1.get(maxIndex.get(i)) : "No Value";

			// Showing null pointer exception
			/*
			 * String value2 = hashMap2.containsKey(maxIndex.get(i)) ?
			 * hashMap2.get(maxIndex.get(i).toLowerCase().trim()) : "No Value";
			 * ======= String value2 = hashMap2.containsKey(maxIndex.get(i)) ?
			 * hashMap2.get(maxIndex.get(i)) : "No Value"; >>>>>>> .r559 if
			 * (value1.toLowerCase().trim().equals(value2.toLowerCase().trim()))
			 * Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" +
			 * maxIndex.get(i) + "</td><td>" + value1 + "</td><td>" + value2 +
			 * "</td></tr>"); else Log.message1(
			 * "<tr><td bgcolor='#BBBBBB' style='color:black;'>" +
			 * maxIndex.get(i) + "</td><td bgcolor='red'>" + value1 +
			 * "</td><td>" + value2 + "</td></tr>");
			 */
			String value2 = hashMap2.containsKey(maxIndex.get(i)) ? hashMap2.get(maxIndex.get(i)) : "No Value";
			if (value1.equals(value2))
				Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i) + "</td><td>" + value1
						+ "</td><td>" + value2 + "</td></tr>");
			else
				Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i)
						+ "</td><td bgcolor='red'>" + value1 + "</td><td>" + value2 + "</td></tr>");
		}
		Log.message1("</table>");

		return Utils.compareTwoHashMap(hashMap1, hashMap2);
	}

	public static void printTableHashMap(String title, LinkedHashMap<String, String> hashMap1) throws Exception {

		Log.message("<br>");
		Log.message1(
				"<table><tr bgcolor='#BBBBBB'><td colspan=2><b><font color='black'>" + title + "</font></b></td></tr>");
		Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>Value</td></tr>");
		List<String> indexes1 = new ArrayList<String>(hashMap1.keySet());
		for (int i = 0; i < indexes1.size(); i++) {
			Log.message1("<tr><td bgcolor='#BBBBBB' style='color:black;'>" + indexes1.get(i) + "</td><td>"
					+ hashMap1.get(indexes1.get(i)) + "</td></tr>");
		}
		Log.message1("</table>");
	}

	/**
	 * Used to validate if a locator is on the page
	 * 
	 * @param driver
	 * @param by
	 *            locator
	 * @return true or false
	 */
	@SuppressWarnings("deprecation")
	public static boolean exists(WebDriver driver, WebElement list) {
		driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
		boolean found = false;
		try {

			if (list.isDisplayed()) {
				found = true;

			} else {
				found = false;
			}

			return found;
		} catch (Exception e) {
			return false;
		} finally {
			driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
		}
	}

	
	public static boolean printTableLinkedListHashMap(String title, String col1Head,
			LinkedList<LinkedHashMap<String, String>> hashMap1, String[]... noNeed) throws Exception {
		// int iteration1 = hashMap1.size();
		int iteration2 = hashMap1.size();
		boolean flag = true;
		boolean flag1 = true;
		for (int j = 0; j < iteration2; j++) {
			if (noNeed.length > 0) {
				for (int i = 0; i < noNeed[0].length; i++) {
					System.out.println("Removed Key from Expected:: " + noNeed[0][i]);
					hashMap1.get(j).remove(noNeed[0][i]);

				}
			}

			Log.message("<br>");
			Log.message1("<table><tr bgcolor='#BBBBBB' align='center'><td colspan=3><b><font color='black'>" + title
					+ "(" + (j + 1) + ")</font></b></td></tr>");
			Log.message1("<tr align='center' bgcolor='#BBBBBB' style='color:black;'><td>Contents</td><td>" + col1Head);
			List<String> indexes1 = new ArrayList<String>(hashMap1.get(j).keySet());

			List<String> maxIndex = indexes1;
			for (int i = 0; i < maxIndex.size(); i++) {
				String value1 = hashMap1.get(j).containsKey(maxIndex.get(i))
						? hashMap1.get(j).get(maxIndex.get(i)).replaceAll("-", "").toLowerCase().trim()
						: "<font color='red'>No Value</font>";

				if (value1 != null) {

					Log.message1(
							"<tr><td bgcolor='#BBBBBB' style='color:black;'>" + maxIndex.get(i) + "</td><td>" + value1);

				}
			}
			Log.message1("</table>");

			// flag = Utils.compareTwoHashMap(hashMap1.get(j), hashMap2.get(j));
			if (!flag) {
				flag1 = flag;
			}
		}

		return flag1;
	}
	
public static boolean elementDisplayedAbove(WebDriver driver, final WebElement element,final WebElement element1) {
		
		Point elementFirst = element.getLocation();
		
		int elementFirstX = elementFirst.x;
		int elementFirstY = elementFirst.y;
		
		Point elementSecond = element1.getLocation();
		
		int elementSecondX = elementSecond.x;
		int elementSecondY = elementSecond.y;
		
		System.out.println("elementFirstX" + elementFirstX);
		System.out.println("elementFirstY" + elementFirstY);
		System.out.println("elementSecondX" + elementSecondX);
		System.out.println("elementSecondY" + elementSecondY);
		
		return true;
	}

	public static List<String> listOfData(String str) {
    String[] dataArray = str.split("\\s*,\\s*");
    return Arrays.asList(dataArray);
	}
	
}