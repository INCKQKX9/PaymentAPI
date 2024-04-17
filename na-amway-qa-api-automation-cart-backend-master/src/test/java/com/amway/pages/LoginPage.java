package com.amway.pages;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.amway.support.Log;
import com.amway.support.Utils;

public class LoginPage extends LoadableComponent<LoginPage> {

	private WebDriver driver;
	private boolean isPageLoaded;
	public ElementLayer elementLayer;
	
	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Starts **************************
	 **********************************************************************************************/

	@FindBy(css="img[src*='Logo']")
	WebElement amwayLogo;
	
	@FindBy(id="password-form:username-pass")
	public static WebElement userName; 
	
	
	@FindBy(id="password-form:password")
	public static WebElement Password; 	
	
	
	@FindBy(id="password-form:submit-btn-password")
	public static WebElement Submit; 
	
	
	@FindBy(id="password-form:country-code")
	public static WebElement CountryCode; 
	
	
	@FindBy(xpath="//*[@class='_user-name-label_1o2ld_32']")
	public static WebElement loggedinSuccessfully;
	
	
	@FindBy(xpath="//*[@data-icon='circle-user']")
	//*[@class='_iconbutton-container_g1lrl_35 _theme-white_g1lrl_68']
	public static WebElement Account;
	
	
	@FindBy(xpath="//span[text()='Sign In']")
	public static WebElement SignIn;
	
	
	
	

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
	public LoginPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		elementLayer = new ElementLayer(driver);
	}// LoginPage
	

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

	public static void select_list(String status)
	{
		if(CountryCode.isDisplayed())
		{
	     Select countryCodeDropdown=new Select(CountryCode);
	     countryCodeDropdown.selectByVisibleText(status);
		}
	}
	

	//Initialize the page objects
	public LoginPage()
	{
		PageFactory.initElements(driver, this);
	}
	
	
	//Perform actions on web-element
	public boolean validateLogin()
	{
		return loggedinSuccessfully.isDisplayed();
	}
	
	public HomePage loginTest(String emailID, String Pwd)
	{
		Account.click();
		SignIn.click();
		userName.sendKeys(emailID);
		Password.sendKeys(Pwd);
		Submit.click();
		return new HomePage(driver).get();		
			
	}


}// LoginPage
