package com.amway.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.testng.Assert;
import com.amway.support.Log;
import com.amway.support.TestDataPropertiesReader;
import com.amway.support.Utils;

public class BankTransferPage extends LoadableComponent<BankTransferPage> {

	private String appURL;
	private WebDriver driver;
	private boolean isPageLoaded;
	public ElementLayer elementLayer;
	private static TestDataPropertiesReader dAta = TestDataPropertiesReader.getInstance("data");
	

	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Starts **************************
	 **********************************************************************************************/

	@FindBy(xpath = "//*[@class='amway-logo']/*[@class='icon']")
	WebElement amwayLogo;

	@FindBy(xpath = "//*[@name='cardNumber']")
	WebElement cardNumber;
	
	@FindBy(xpath = "//*[@name='expyear']")
	WebElement expiryYear;
	
	@FindBy(xpath = "//*[@name='cvv']")
	WebElement cardCvv;
	
	@FindBy(xpath = "//*[@name='name']")
	WebElement cardName;

	@FindBy(xpath = "//button[@type='submit']")
	WebElement payNowButton;
	
	@FindBy(xpath = "//*[@name='challengeDataEntry']")
	WebElement creditCardOtp;
	
	@FindBy(xpath = "//*[@class='acs-challenge-form-actions']/*[@class='acs-challenge-btn proceed']")
	WebElement submitOtpButton;
	
	@FindBy(xpath = "//*[text()='CAPTURED']")
	WebElement capturedStatus;
	
	@FindBy(xpath = "//iframe[@id='pgw-ui-container-dialog-iframe']")
	WebElement paymentGatewayIframe;
	
	/*
	 * @FindBy(xpath = "//button[@class='btn btn-primary']") WebElement
	 * confirmPayment;
	 */
	
	@FindBy(xpath = "//button[text()='Pay Now']") 
	WebElement toPay;
	
	@FindBy(xpath = "//div[contains(@class,'select__dropdown-indicator')]")
	//List<WebElement> selectBank;
	WebElement selectBank;
	
	@FindBy(xpath = "//*[contains(text(),'The Siam Commercial Bank')]")
	WebElement selectSiamBank;
	
	@FindBy(xpath = "//input[@type='submit']")
	WebElement paymentSubmit;
	
	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Ends ****************************
	 **********************************************************************************************/

	/**
	 * constructor of the class
	 * 
	 * @param driver : Webdriver
	 * 
	 * @param url    : UAT URL
	 */
	public BankTransferPage(WebDriver driver, String url) {
		appURL = url;
		this.driver = driver;
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, Utils.maxElementWait);
		PageFactory.initElements(finder, this);
	}// HomePage

	/**
	 * 
	 * @param driver : webdriver
	 */
	public BankTransferPage(WebDriver driver) {
		this.driver = driver;
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, Utils.maxElementWait);
		PageFactory.initElements(finder, this);
		elementLayer = new ElementLayer(driver);
		appURL = driver.getCurrentUrl();
	}

	@Override
	protected void isLoaded() {
		if (!isPageLoaded) {
			Assert.fail();
		}
		driver.manage().deleteAllCookies();
//		closeHeathSalesPopUp();
		if (isPageLoaded && !(Utils.waitForElement(driver, toPay))) {
			Log.fail("Home Page did not open up. Site might be down.", driver);
		} else {
			Log.pass("Home Page is Loaded as Expected", driver);
		}

		elementLayer = new ElementLayer(driver);

	}// isLoaded

	@Override
	protected void load() {
		isPageLoaded = true;
		driver.get(appURL);
		Utils.waitForPageLoad(driver);
	}// load

	
	public void enterCreditCardDetails() throws Exception
	{
		if(cardNumber.isDisplayed())
		{
			Log.message("Card details are displayed and entering the card details");
			cardNumber.sendKeys("4111111111111111");
			Thread.sleep(1500);
			expiryYear.sendKeys("12/24");
			//cardCvv.sendKeys("123");
			cardName.sendKeys("Abdul");
			if(payNowButton.isEnabled())
			{
				Thread.sleep(1500);
				Log.message("Card details are Entered and clicking the Pay Now button");
				payNowButton.click();
				Thread.sleep(1500);
				
			}
			
			
		}		
	}

	public void enterOtp() throws Exception
	{
		Thread.sleep(5000);
		if(creditCardOtp.isDisplayed())
		{
			creditCardOtp.sendKeys("123456");
			submitOtpButton.click();
			
		}
		Utils.waitForElement(driver, capturedStatus, 100);
		if(capturedStatus.isDisplayed())
		{
			Log.message("Captured status is displayed");
		}
		
		
	}
	
	public void bankTranser() {
		//Utils.waitForElement(driver, paymentGatewayIframe, 20);
		int size = driver.findElements(By.tagName("iframe")).size();
		System.out.println("total no. of Frames : " + size);
		//driver.switchTo().frame("pgw-ui-container-dialog-iframe");
		Utils.waitForElement(driver, toPay, 10);
		selectBank.click();
		selectSiamBank.click();
		toPay.click();
		Utils.waitForElement(driver, paymentSubmit, 50);
		paymentSubmit.click();
		
	}
	
	
//
//	private boolean isGCOToggleDisplayed() {
//		try {
//			return gcoToggle.isDisplayed();
//		} catch (NoSuchElementException e) {
//			Log.message("====================== GCO Toggle is not displayed.");
//			return false;
//		}
//	}



	/**********************************************************************************************
	 ****************************** WebElements calls of Home Page - Ends *************************
	 **********************************************************************************************/

	
}// HomePage