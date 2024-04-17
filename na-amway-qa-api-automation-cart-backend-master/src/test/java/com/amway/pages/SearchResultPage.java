package com.amway.pages;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.testng.Assert;

import com.amway.support.Log;
import com.amway.support.Utils;

public class SearchResultPage extends LoadableComponent<SearchResultPage> {

	private WebDriver driver;
	private boolean isPageLoaded;
	public ElementLayer elementLayer;
	
	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Starts **************************
	 **********************************************************************************************/

	@FindBy(css="img[src*='Logo']")
	WebElement amwayLogo;
	
	@FindBy(xpath="//*[@placeholder='Search']")
	public static WebElement searchField; 
	
	@FindBy(xpath="//*[text()='Search results for']")
	public static WebElement searchResult; 
	
	
	
	

	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Ends ****************************
	 **********************************************************************************************/

	/**
	 * constructor of the class
	 * 
	 * @param driver
	 *            : webdriver
	 */
	public SearchResultPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		elementLayer = new ElementLayer(driver);
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
		Utils.waitForPageLoad(driver);
	}// load

	//Initialize the page objects
	public SearchResultPage()
	{
		PageFactory.initElements(driver, this);
	}
	
	
	public void searchForProduct(String prdtSrch) throws AWTException
	{
		searchField.sendKeys(prdtSrch);
	    Robot robot = new Robot();
	    robot.keyPress(KeyEvent.VK_ENTER);
	    robot.keyRelease(KeyEvent.VK_ENTER);
	    robot.delay(200);
	}
	
	public boolean verifySearchResult() 
	{
		boolean srchRes = searchResult.isDisplayed();
		return srchRes;
	}
	


}// SearchResultPage
