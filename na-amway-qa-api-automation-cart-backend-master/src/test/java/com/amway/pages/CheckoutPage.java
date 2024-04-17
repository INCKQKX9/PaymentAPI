package com.amway.pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.amway.support.Log;
import com.amway.support.Utils;

public class CheckoutPage extends LoadableComponent<CheckoutPage> {

	private WebDriver driver;
	private boolean isPageLoaded;
	public ElementLayer elementLayer;
	
	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Starts **************************
	 **********************************************************************************************/

	@FindBy(css="img[src*='Logo']")
	WebElement amwayLogo;
	
	@FindBy(xpath="//*[text()='Add To Cart']")
	public static List<WebElement> AddToCart;	
	
	@FindBy(id="proceed-to-checkout-btn-overview")
	public static WebElement CheckOut;
	
	@FindBy(xpath="//*[text()='Continue as a Guest']")
	public static WebElement ContinueasaGuest;
	
	@FindBy(id="dropdown-content-recipientNumber")
	public static WebElement CountryDropDown;
	
	@FindBy(name="recipientNumber")
	public static WebElement RecipientsNumber;
	
	
	@FindBy(xpath="//*[text()='Verify Mobile Number']")
	public static WebElement VerifyMobileNumber;
	
	
	@FindBy(xpath="//*[contains(@name,'input')]")
	public static WebElement VerifyCode;
	
	@FindBy(xpath="//*[text()='Verified']")
	public static WebElement SuccessfullyVerify;
	
	
	
	

	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Ends ****************************
	 **********************************************************************************************/

	/**
	 * constructor of the class
	 * 
	 * @param driver
	 *            : Webdriver
	 * 
	 * @param url
	 *            : UAT URL
	 */
	public CheckoutPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		elementLayer = new ElementLayer(driver);
	}// CheckoutPage

	/**
	 * 
	 * @param driver
	 *            : webdriver
	 */

	@Override
	protected void isLoaded() {
		if (!isPageLoaded) {
			Assert.fail();
		}
		driver.manage().deleteAllCookies();
		
		if (isPageLoaded && !(Utils.waitForElement(driver, amwayLogo))) {
			Log.fail("Home Page did not open up. Site might be down.", driver);
		}else{
			Log.pass("Home Page is Loaded as Expected",driver);
		}
		
		elementLayer = new ElementLayer(driver);

	}// isLoaded

	@Override
	protected void load() {
		isPageLoaded = true;
		Utils.waitForPageLoad(driver);
	}// load

	//Initialize the page objects
	public CheckoutPage()
	{
		PageFactory.initElements(driver, this);
	}
		
		 
	public void AddItemsToCart()
	 {
		 AddToCart.get(1).click();
	 }
	 
	public void GuestUserSelection()
	 {
		 CheckOut.click();
		 ContinueasaGuest.click();
	 }
	 
	 
	public void VerifyMobileNumber(String code, String Mobilenum)
	 {
		 if(CountryDropDown.isDisplayed())
			{
		     Select countryCodeDropdown=new Select(CountryDropDown);
		     countryCodeDropdown.selectByVisibleText(code);
			}
		 
		 RecipientsNumber.sendKeys(Mobilenum);
		 VerifyMobileNumber.click();
		 
		 List<WebElement> allInputElements = (List<WebElement>) VerifyCode;
			
		   if(allInputElements.size() != 0) 
		   {
							
			   for(WebElement inputElement : allInputElements) 
			   {
				  inputElement.sendKeys("0");
			   }
		   }  		 
		 
	 }
	
	public void GuestUserValidateCheckout() 
	{
//		VerifyMobileNumber(prop.getProperty("CountryCode"), prop.getProperty("RecipientsNumber"));
		
	}


}// CheckoutPage
