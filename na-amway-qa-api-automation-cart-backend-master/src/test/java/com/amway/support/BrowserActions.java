package com.amway.support;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;

/**
 * Wrapper for Selenium WebDriver actions which will be performed on browser
 * 
 * Wrappers are provided with exception handling which throws Skip Exception on
 * occurrence of NoSuchElementException
 * 
 */
public class BrowserActions {

	public static String MOUSE_HOVER_JS = "var evObj = document.createEvent('MouseEvents');"
			+ "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
			+ "arguments[0].dispatchEvent(evObj);";

	/**
	 * Wrapper to type a text in browser text field
	 * 
	 * @param txt
	 *            : WebElement of the Text Field
	 * @param txtToType
	 *            : Text to type [String]
	 * @param driver
	 *            : WebDriver Instances
	 * @param elementDescription
	 *            : Description about the WebElement
	 * @throws Exception
	 */
	public static void typeOnTextField(WebElement txt, String txtToType,
			WebDriver driver, String elementDescription) throws Exception {

		if (!Utils.waitForElement(driver, txt))
			throw new Exception(elementDescription
					+ " field not found in page!!");

		try {
			txt.clear();
			// txt.click();
			txt.sendKeys(txtToType);
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription
					+ " field not found in page!!");

		}

	}// typeOnTextField

	/**
	 * 
	 * @param driver
	 * @param element
	 * 
	 */
	public static void mouseHover(WebDriver driver, WebElement element) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).clickAndHold(element).build().perform();
	}

	/**
	 * Wrapper to type a text in browser text field
	 * 
	 * @param txt
	 *            : String Input (CSS Locator)
	 * @param txtToType
	 *            : Text to type [String]
	 * @param driver
	 *            : WebDriver Instances
	 * @param elementDescription
	 *            : Description about the WebElement
	 * @throws Exception
	 */
	public static void typeOnTextField(String txt, String txtToType,
			WebDriver driver, String elementDescription) throws Exception {

		WebElement element = checkLocator(driver, txt);
		if (!Utils.waitForElement(driver, element, 1))
			throw new Exception(elementDescription
					+ " field not found in page!!");

		try {
			element.clear();
			element.click();
			element.sendKeys(txtToType);
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription
					+ " field not found in page!!");

		}

	}// typeOnTextField

	public static void scrollToViewElement(WebElement element, WebDriver driver)
			throws InterruptedException {
		final long startTime = StopWatch.startTime();
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView(true);", element);
		Thread.sleep(1000);
		Log.event("Scrolls the element", StopWatch.elapsedTime(startTime));
	}

	public static void scrollToViewElementMiddle(WebElement element, WebDriver driver) {
		try {
			String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
					+ "var elementTop = arguments[0].getBoundingClientRect().top;"
					+ "window.scrollBy(0, elementTop-(viewPortHeight/2));";
			((JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, element);
			BrowserActions.nap(2);
		} catch (Exception ex) {
			Log.event("Moved to element..");
		}
	}
	public static void nap(long time) {
		try {
			TimeUnit.SECONDS.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Wrapper to click on button/text/radio/checkbox in browser
	 * 
	 * @param btn
	 *            : WebElement of the Button Field
	 * @param driver
	 *            : WebDriver Instances
	 * @param elementDescription
	 *            : Description about the WebElement
	 */

	public static int countElements(String xpath, WebDriver driver) {
		return driver.findElements(By.xpath(xpath)).size();
	}

	public static void clickOnElement(WebElement btn, WebDriver driver,
			String elementDescription) throws Exception {
		if (!Utils.waitForElement(driver, btn, 15))
			throw new Exception(elementDescription + " not found in page!!");

		try {
			btn.click();
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}

	}// clickOnButton

	/**
	 * Double click method
	 */

	public static void doubleClickByJS(WebDriver driver, WebElement element) {

		String jScript =

				"var targLink = arguments[0];"
						+ "var clickEvent  = document.createEvent ('MouseEvents');"

				+ "clickEvent.initEvent ('dblclick', true, true);"
				+ "targLink.dispatchEvent (clickEvent);";

		((JavascriptExecutor) driver).executeScript(jScript, element);

	}

	/**
	 * Wrapper to click on button/text/radio/checkbox in browser
	 * 
	 * @param btn
	 *            : String Input (CSS Locator) [of the Button Field]
	 * @param driver
	 *            : WebDriver Instances
	 * @param elementDescription
	 *            : Description about the WebElement
	 * @throws Exception
	 */
	public static void clickOnElement(String btn, WebDriver driver,
			String elementDescription) throws Exception {

		WebElement element = checkLocator(driver, btn);
		if (!Utils.waitForElement(driver, element, 1))
			throw new Exception(elementDescription + " not found in page!!");

		try {
			element.click();
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}

	}// clickOnButton

	public static void actionClick(WebElement element, WebDriver driver,
			String elementDescription) throws Exception {
		if (!Utils.waitForElement(driver, element, 5))
			throw new Exception(elementDescription + " not found in page!!");

		try {
			Actions actions = new Actions(driver);
			actions.moveToElement(element).click(element).build().perform();
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}
	}

	public static void javascriptClick(WebElement element, WebDriver driver,
			String elementDescription) throws Exception {
		if (!Utils.waitForElement(driver, element, 5))
			throw new Exception(elementDescription + " not found in page!!");

		try {
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}
	}

	/**
	 * Wrapper to get a text from the provided WebElement
	 * 
	 * @param driver
	 *            : WebDriver Instance
	 * @param fromWhichTxtShldExtract
	 *            : WebElement from which text to be extract in String format
	 * @return: String - text from web element
	 * @param elementDescription
	 *            : Description about the WebElement
	 * @throws Exception
	 */
	public static String getText(WebDriver driver,
			WebElement fromWhichTxtShldExtract, String elementDescription)
					throws Exception {

		String textFromHTMLAttribute = "";

		try {
			textFromHTMLAttribute = fromWhichTxtShldExtract.getText();

			if (textFromHTMLAttribute.isEmpty())
				textFromHTMLAttribute = fromWhichTxtShldExtract
				.getAttribute("textContent");

		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}

		return textFromHTMLAttribute;

	}// getText

	/**
	 * Wrapper to get a text from the provided WebElement
	 * 
	 * @param driver
	 *            : WebDriver Instance
	 * @param fromWhichTxtShldExtract
	 *            : String Input (CSS Locator) [from which text to be extract in
	 *            String format]
	 * @return: String - text from web element
	 * @param elementDescription
	 *            : Description about the WebElement
	 * @throws Exception
	 */
	public static String getText(WebDriver driver,
			String fromWhichTxtShldExtract, String elementDescription)
					throws Exception {

		String textFromHTMLAttribute = "";

		WebElement element = checkLocator(driver, fromWhichTxtShldExtract);

		try {
			textFromHTMLAttribute = element.getText();

			if (textFromHTMLAttribute.isEmpty())
				textFromHTMLAttribute = element.getAttribute("textContent");

		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}

		return textFromHTMLAttribute;

	}// getText

	/**
	 * Wrapper to get a text from the provided WebElement's Attribute
	 * 
	 * @param driver
	 *            : WebDriver Instance
	 * @param fromWhichTxtShldExtract
	 *            : WebElement from which text to be extract in String format
	 * @param attributeName
	 *            : Attribute Name from which text should be extracted like
	 *            "style, class, value,..."
	 * @return: String - text from web element
	 * @param elementDescription
	 *            : Description about the WebElement
	 * @throws Exception
	 */
	public static String getTextFromAttribute(WebDriver driver,
			WebElement fromWhichTxtShldExtract, String attributeName,
			String elementDescription) throws Exception {

		String textFromHTMLAttribute = "";

		try {
			textFromHTMLAttribute = fromWhichTxtShldExtract
					.getAttribute(attributeName);
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}

		return textFromHTMLAttribute;

	}// getTextFromAttribute

	/**
	 * Wrapper to get a text from the provided WebElement's Attribute
	 * 
	 * @param driver
	 *            : WebDriver Instance
	 * @param fromWhichTxtShldExtract
	 *            : String Input (CSS Locator) [from which text to be extract in
	 *            String format]
	 * @param attributeName
	 *            : Attribute Name from which text should be extracted like
	 *            "style, class, value,..."
	 * @return: String - text from web element
	 * @param elementDescription
	 *            : Description about the WebElement
	 * @throws Exception
	 */
	public static String getTextFromAttribute(WebDriver driver,
			String fromWhichTxtShldExtract, String attributeName,
			String elementDescription) throws Exception {

		String textFromHTMLAttribute = "";
		WebElement element = checkLocator(driver, fromWhichTxtShldExtract);

		try {
			textFromHTMLAttribute = element.getAttribute(attributeName);
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}

		return textFromHTMLAttribute;

	}// getTextFromAttribute

	/**
	 * Wrapper to select option from combobox in browser and doesn't wait for
	 * spinner to disappear
	 * 
	 * @param btn
	 *            : String Input (CSS Locator) [of the ComboBox Field]
	 * 
	 * @param optToSelect
	 *            : option to select from combobox
	 * 
	 * @param driver
	 *            : WebDriver Instances
	 * @param elementDescription
	 *            : Description about the WebElement
	 * @throws Exception
	 */
	public static void selectFromComboBox(String btn, String optToSelect,
			WebDriver driver, String elementDescription) throws Exception {

		WebElement element = checkLocator(driver, btn);
		if (!Utils.waitForElement(driver, element, 1))
			throw new Exception(elementDescription + " not found in page!!");

		try {
			Select selectBox = new Select(element);
			selectBox.selectByValue(optToSelect);
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}

	}// selectFromComboBox

	/**
	 * Wrapper to select option from combobox in browser
	 * 
	 * @param btn
	 *            : WebElement of the combobox Field
	 * 
	 * @param optToSelect
	 *            : option to select from combobox
	 * 
	 * @param driver
	 *            : WebDriver Instances
	 * @param elementDescription
	 *            : Description about the WebElement
	 */
	public static void selectFromComboBox(WebElement btn, String optToSelect,
			WebDriver driver, String elementDescription) {

		if (!Utils.waitForElement(driver, btn, 1))
			throw new SkipException(elementDescription + " not found in page!!");

		try {
			Select selectBox = new Select(btn);
			selectBox.selectByValue(optToSelect);
		} catch (NoSuchElementException e) {
			throw new SkipException(elementDescription + " not found in page!!");
		}

	}// selectFromComboBox

	/**
	 * Select drop down value and doesn't wait for spinner.
	 *
	 * @param elementLocator
	 *            the element locator
	 * @param valueToBeSelected
	 *            the value to be selected
	 */
	public static void selectDropDownValue(WebDriver driver,
			WebElement dropDown, String valueToBeSelected) {
		Select select = new Select(dropDown);
		try{
			select.selectByVisibleText(valueToBeSelected);
		}catch(Exception e1)
		{
			select.selectByValue(valueToBeSelected);
		}
	}

	/**
	 * Select drop down value and doesn't wait for spinner.
	 *
	 * @param elementLocator
	 *            the element locator
	 * @param valueToBeSelected
	 *            the value to be selected
	 */
	public static void selectDropDownIndex(WebDriver driver,
			WebElement dropDown, String valueToBeSelected) {
		dropDown.click();
		Select dropdown = new Select(dropDown);
		int closuretype = Integer.parseInt(valueToBeSelected);
		dropdown.selectByIndex(closuretype);
	}

	public static void openMenu(WebDriver driver, WebElement element) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).clickAndHold(element).build().perform();
	}

	public static void selectRadioOrCheckbox(WebElement element,
			String enableOrDisable) {
		if ("YES".equalsIgnoreCase(enableOrDisable)) {
			if (!(isRadioOrCheckBoxSelected(element))) {
				element.click();
			}
		}
		if ("NO".equalsIgnoreCase(enableOrDisable)) {
			if (isRadioOrCheckBoxSelected(element)) {
				element.click();
			}
		}
	}

	public static boolean isRadioOrCheckBoxSelected(WebElement element) {
		if (element.getAttribute("class").contains("active")) {
			return true;
		}

		if (null != element.getAttribute("checked")) {
			return true;
		}

		for (WebElement childElement : element.findElements(By.xpath(".//*"))) {
			if (childElement.getAttribute("class").contains("active")) {
				return true;
			}
		}

		return false;
	}

	public static String getRadioOrCheckboxChecked(WebElement element) {
		if (element.getAttribute("class").contains("active")) {
			return "Yes";
		}

		if (null != element.getAttribute("checked")) {
			return "Yes";
		}

		for (WebElement childElement : element.findElements(By.xpath(".//*"))) {
			if (childElement.getAttribute("class").contains("active")) {
				return "Yes";
			}
		}

		return "No";
	}

	/**
	 * To check whether locator string is xpath or css
	 * 
	 * @param driver
	 * @param locator
	 * @return elements
	 */
	public static List<WebElement> checkLocators(WebDriver driver,
			String locator) {
		List<WebElement> elements = null;
		if (locator.startsWith("//")) {
			elements = (new WebDriverWait(driver, 10).pollingEvery(500,
					TimeUnit.MILLISECONDS).ignoring(
							NoSuchElementException.class,
							StaleElementReferenceException.class)
							.withMessage("Couldn't find " + locator))
							.until(ExpectedConditions
									.visibilityOfAllElementsLocatedBy(By.xpath(locator)));
		} else {
			elements = (new WebDriverWait(driver, 10).pollingEvery(500,
					TimeUnit.MILLISECONDS).ignoring(
							NoSuchElementException.class,
							StaleElementReferenceException.class)
							.withMessage("Couldn't find " + locator))
							.until(ExpectedConditions
									.visibilityOfAllElementsLocatedBy(By
											.cssSelector(locator)));
		}
		return elements;
	}

	/**
	 * To check whether locator string is xpath or css
	 * 
	 * @param driver
	 * @param locator
	 * @return element
	 */
	public static WebElement checkLocator(WebDriver driver, String locator) {

		WebElement element = null;
		if (locator.startsWith("//")) {
			element = (new WebDriverWait(driver, 10).pollingEvery(500,
					TimeUnit.MILLISECONDS).ignoring(
							NoSuchElementException.class,
							StaleElementReferenceException.class)
							.withMessage("Couldn't find " + locator))
							.until(ExpectedConditions.visibilityOfElementLocated(By
									.xpath(locator)));
		} else {
			element = (new WebDriverWait(driver, 10).pollingEvery(500,
					TimeUnit.MILLISECONDS).ignoring(
							NoSuchElementException.class,
							StaleElementReferenceException.class)
							.withMessage("Couldn't find " + locator))
							.until(ExpectedConditions.visibilityOfElementLocated(By
									.cssSelector(locator)));
		}
		return element;
	}

	/**
	 * To perform mouse hover on an element using javascript
	 * 
	 * @param driver
	 * @param element
	 */
	public static void moveToElementJS(WebDriver driver, WebElement element) {
		((JavascriptExecutor) driver).executeScript(MOUSE_HOVER_JS, element);
	} 
	
	public static void moveToElementOnMobileJS(WebDriver driver, WebElement element)
	{
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
	}

	public static void ScrollToViewTop(WebDriver driver)
	{
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,250)", "");
	}

	/**
	 * To get matching text element from List of web elements
	 * 
	 * @param elements
	 *            -
	 * @param contenttext
	 *            - text to match
	 * @return elementToBeReturned as WebElement
	 * @throws Exception
	 *             -
	 */
	public static WebElement getMachingTextElementFromList(
			List<WebElement> elements, String contenttext, String condition)
					throws Exception {
		WebElement elementToBeReturned = null;
		boolean found = false;
		if (elements.size() > 0) {
			for (WebElement element : elements) {
				if (condition.toLowerCase().equals("equals")
						&& element.getText().trim().replaceAll("\\s+", " ")
						.equalsIgnoreCase(contenttext)) {
					elementToBeReturned = element;
					found = true;
					break;
				}
				BrowserActions.nap(1);

				if (condition.toLowerCase().equals("contains")
						&& element.getText().trim().replaceAll("\\s+", " ")
						.contains(contenttext)) {
					elementToBeReturned = element;
					found = true;
					break;
				}
			}
			if (!found) {
				throw new Exception("Didn't find the correct text("
						+ contenttext + ")..! on the page");
			}
		} else {
			throw new Exception("Unable to find list element...!");
		}
		return elementToBeReturned;
	}

	/**
	 * Open a new tab on the browser
	 * 
	 * @param driver
	 */
	public static void openNewTab(WebDriver driver) {
		driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL + "t");
	}

	/**
	 * To Verify the element is present
	 * 
	 */
	public static boolean elementPresent(WebElement element, WebDriver driver,
			String elementDescription) throws Exception {
		if (!Utils.waitForElement(driver, element, 5))
			throw new Exception(elementDescription + " not found in page!!");
		try {
			if (element.isDisplayed()) {
				return true;
			}
		} catch (NoSuchElementException e) {
			throw new Exception(elementDescription + " not found in page!!");
		}
		return false;
	}

	/**
	 * To trim number from String variable
	 * 
	 * @param txtTrim
	 * @return
	 */
	public static String trimNumber(String txtTrim) {
		final String input = txtTrim;
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			final char c = input.charAt(i);
			if (c > 47 && c < 58) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/*
	 * double click
	 */
	public static void doubleClick(WebElement element) throws Exception {
		for (int count = 0; count < 2; count++) {
			element.click();
		}
	}

	@SuppressWarnings("rawtypes")
	final public static void fillFormDetails(LinkedHashMap <String, WebElement> instructions, WebDriver driver) throws Exception {

		//final long startTime = StopWatch.startTime();
		try {
			Set billingDetailsSet = instructions.entrySet();
			Iterator billingDetailsIterator = billingDetailsSet.iterator();

			while (billingDetailsIterator.hasNext()) {

				Map.Entry mapEntry = (Map.Entry) billingDetailsIterator.next();
				String[] keyWithElementTypeAndDescriptionAndTextToType = mapEntry.getKey().toString().split("_");
				WebElement locator = (WebElement) mapEntry.getValue();

				switch (keyWithElementTypeAndDescriptionAndTextToType[0].toLowerCase()) {

				case "type":{
					BrowserActions.typeOnTextField(locator, keyWithElementTypeAndDescriptionAndTextToType[2], driver, keyWithElementTypeAndDescriptionAndTextToType[1]);
					break;
				}
				case "click":{ 
					
					if(Utils.getBrowserName(driver).equalsIgnoreCase("firefox"))
					{
						BrowserActions.nap(5);  
						
						JavascriptExecutor js = (JavascriptExecutor) driver; 
						js.executeScript("window.scrollBy(0,500)"); 
						BrowserActions.nap(2); 
						/*Header header = new Header(driver); 
						header.scrollCompletelyToBottomOfTheWebPage(); */
						
					}
					
					BrowserActions.javascriptClick(locator, driver, keyWithElementTypeAndDescriptionAndTextToType[1]);  
					
					if(Utils.getBrowserName(driver).equalsIgnoreCase("firefox"))
						BrowserActions.nap(8);
					
					break;
				}
				case "select": {
					//WebElement element = driver.findElement(By.cssSelector(locator));
					Select select = new Select(locator);
					try{
						if(Utils.getBrowserName(driver).equalsIgnoreCase("firefox")) 
						{
							BrowserActions.nap(5); 
							JavascriptExecutor js = (JavascriptExecutor) driver; 
							js.executeScript("window.scrollBy(0,500)"); 
							BrowserActions.nap(2); 
							/*Header header = new Header(driver); 
							header.scrollCompletelyToBottomOfTheWebPage(); */ 
						}
						select.selectByVisibleText(keyWithElementTypeAndDescriptionAndTextToType[2]);
					}catch(Exception e1)
					{
						if(Utils.getBrowserName(driver).equalsIgnoreCase("firefox"))
						{
							BrowserActions.nap(5); 
							
					
							
							
							JavascriptExecutor js = (JavascriptExecutor) driver; 
							js.executeScript("window.scrollBy(0,500)"); 
							BrowserActions.nap(2); 
							/*Header header = new Header(driver); 
							header.scrollCompletelyToBottomOfTheWebPage(); */
						}
						select.selectByValue(keyWithElementTypeAndDescriptionAndTextToType[2]);
					}
					Log.event("Selected(" + keyWithElementTypeAndDescriptionAndTextToType[2] + ") from " + keyWithElementTypeAndDescriptionAndTextToType[1]);
					break;
				}
				case "check": {
					BrowserActions.selectRadioOrCheckbox(locator, keyWithElementTypeAndDescriptionAndTextToType[2]);
					break;
				}

				}// Switch

				Utils.waitForPageLoad(driver);

			}// While
		} catch (Exception e) {
			Log.failsoft("No such Element");
		}

	}
	
	public static void ScrollToViewBottom(WebDriver driver)	{
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(0,-250)", "");
	} 
}// BrowserActions