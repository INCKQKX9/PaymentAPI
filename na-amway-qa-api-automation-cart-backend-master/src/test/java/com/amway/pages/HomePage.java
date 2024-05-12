package com.amway.pages;

import java.util.List;

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

public class HomePage extends LoadableComponent<HomePage> {

	private String appURL;
	private WebDriver driver;
	private boolean isPageLoaded;
	public ElementLayer elementLayer;
	private static TestDataPropertiesReader dAta = TestDataPropertiesReader.getInstance("data");
	
	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Starts **************************
	 **********************************************************************************************/

	@FindBy(css="img[src*='Logo']")
	WebElement amwayLogo;
	
	@FindBy(css="div[data-testid='home-banner-testid']")
	List<WebElement> homeCarousel;
	
	@FindBy(xpath="//*[@data-icon='cart-shopping']")
	public static WebElement cartIcon;
	
	@FindBy(xpath="//*[@class='_user-name-label_1o2ld_32']")
	public static WebElement loggedinSuccessfully;
	
	@FindBy(xpath="//*[text()='Add To Cart']")
	public static List<WebElement> AddToCart;
	
	
	
	

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
	public HomePage(WebDriver driver, String url) {
		appURL = url;
		this.driver = driver;
		ElementLocatorFactory finder = new AjaxElementLocatorFactory(driver, Utils.maxElementWait);
		PageFactory.initElements(finder, this);
	}// HomePage

	/**
	 * 
	 * @param driver
	 *            : webdriver
	 */
	public HomePage(WebDriver driver) {
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
		driver.get(appURL);
		Utils.waitForPageLoad(driver);
	}// load

	public boolean ValidateUserName()
	{
		boolean flag =  loggedinSuccessfully.isDisplayed();
		return flag;
	}
	
	



}// HomePage
