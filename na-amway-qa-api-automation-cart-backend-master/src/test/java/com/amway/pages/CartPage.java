package com.amway.pages;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.testng.Assert;

import com.amway.pages.CartPage;
import com.amway.support.Log;
import com.amway.support.Utils;

public class CartPage extends LoadableComponent<CartPage> {

	private WebDriver driver;
	private boolean isPageLoaded;
	public ElementLayer elementLayer;
	
	
	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Starts **************************
	 **********************************************************************************************/

	@FindBy(css="img[src*='Logo']")
	WebElement amwayLogo;
	
	@FindBy(xpath="//*[@data-icon='cart-shopping']")
	public static WebElement cartIcon; 
	
	@FindBy(xpath="_mini-cart-count_1rqqw_41")
	public static WebElement cartCountIcon; 
	
	@FindBy(xpath="//*[text()='Clear Cart']")
	public static WebElement clearCart; 	
	
	@FindBy(xpath="//*[text()='Empty Cart']")
	public static WebElement emptyCart; 
	
	@FindBy(xpath="//*[text()='Your cart is empty']")
	public static WebElement YourCartIsEmpty; 
	
	@FindBy(xpath="//*[@id='proceed-to-checkout-btn']")
	public static List<WebElement> CheckOut;
	
	@FindBy(xpath="//*[@data-testid='proceed-checkout-btn-overview-desktop']")
	public static WebElement CheckOutBtn; 
	
	@FindBy(xpath="//*[text()='Continue as a Guest']")
	public static WebElement ContinueAsAGuestBtn;
	

	/**********************************************************************************************
	 ********************************* WebElements of Home Page - Ends ****************************
	 **********************************************************************************************/
	
	/**
	 * constructor of the class	
	 * 
	 * @param driver
	 *            : webdriver
	 */
	public CartPage(WebDriver driver) {
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
	public CartPage()
	{
		PageFactory.initElements(driver, this);
	}
		
	public void ClearCart()
	{
		cartIcon.click();

		 try
		 {
			 if(clearCart.isDisplayed())
				{
					clearCart.click();
					emptyCart.click();
					
				}
				 
		 }
		 catch(Exception e)
		 {            
			 e.printStackTrace();
		 }
	}
	
		
	public boolean ValidateEmptyCart()
		{
		     cartIcon.click();
			 boolean emptymycart = YourCartIsEmpty.isDisplayed();
			 return emptymycart;
			 
		}
	
	public void NavigateToCartPage() throws InterruptedException
	{
//		Utils.waitForElement(driver, cartIcon, 10);
		cartIcon.click();
	}
	
	public CheckoutPage CheckOutBtn() 
	{
		CheckOut.get(3).click();
		return new CheckoutPage();
	}
	
	public void CheckOutBtnClick() 
	{
		CheckOutBtn.click();
	}
	
	public void ContinueAsAGuestBtnClick() 
	{
		ContinueAsAGuestBtn.click();
	}
	
	public void GuestUserPOD() throws InterruptedException
	{
		NavigateToCartPage();
		Utils.waitForElement(driver, CheckOutBtn, 10);
		CheckOutBtnClick();
		ContinueAsAGuestBtnClick();		
	}
	

}// CartPageEnd
