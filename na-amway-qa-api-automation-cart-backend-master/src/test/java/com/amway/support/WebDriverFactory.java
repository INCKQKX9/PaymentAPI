package com.amway.support;

import java.io.UnsupportedEncodingException;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.xml.XmlTest;

//import com.saucelabs.selenium.client.factory.SeleniumFactory;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;

/**
 * WebdriverFactory class used to get a web driver instance, depends on the user
 * requirement as driverHost, driverPort and browserName we adding the
 * desiredCapabilities and other static action initialized here and some methods
 * used to retrieve the Hub and node information. It also consists page wait
 * load for images/frames/document
 */

public class WebDriverFactory {

    private static Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);
    private static EnvironmentPropertiesReader configProperty = EnvironmentPropertiesReader.getInstance();
    private static MobileEmulationUserAgentConfiguration mobEmuUA = new MobileEmulationUserAgentConfiguration();

    static String driverHost;
    static String driverPort;
    static String browserName;
    static String deviceName;
    static URL hubURL;
    static Proxy zapProxy = new Proxy();

    static DesiredCapabilities ieCapabilities = new DesiredCapabilities();
    static DesiredCapabilities firefoxCapabilities = new DesiredCapabilities();
    static DesiredCapabilities chromeCapabilities = new DesiredCapabilities();
    static DesiredCapabilities safariCapabilities =  new DesiredCapabilities();
    static DesiredCapabilities edgeCapabilities =  new DesiredCapabilities();
    static DesiredCapabilities iOS_SimulatorCapabilities = new DesiredCapabilities();
    static DesiredCapabilities androidSimulatorCapabilities = new DesiredCapabilities();
    static DesiredCapabilities iOSDeviceCapabilities = new DesiredCapabilities();
    static ChromeOptions opt = new ChromeOptions();
    static FirefoxOptions option1 = new FirefoxOptions();
    static FirefoxProfile fp = new FirefoxProfile();
    public static ExpectedCondition<Boolean> documentLoad;
    public static ExpectedCondition<Boolean> framesLoad;
    public static ExpectedCondition<Boolean> imagesLoad;
    public static int maxPageLoadWait = 90;
    public static int maxWindowWait = 90;

    static {
        try {
            documentLoad = new ExpectedCondition<Boolean>() {
                public final Boolean apply(final WebDriver driver) {
                    final JavascriptExecutor js = (JavascriptExecutor) driver;
                    boolean docReadyState = false;
                    try {
                        docReadyState = (Boolean) js.executeScript(
                                "return (function() { if (document.readyState != 'complete') {  return false; } if (window.jQuery != null && window.jQuery != undefined && window.jQuery.active) { return false;} if (window.jQuery != null && window.jQuery != undefined && window.jQuery.ajax != null && window.jQuery.ajax != undefined && window.jQuery.ajax.active) {return false;}  if (window.angular != null && angular.element(document).injector() != null && angular.element(document).injector().get('$http').pendingRequests.length) return false; return true;})();");
                    } catch (WebDriverException e) {
                        docReadyState = true;
                    }
                    return docReadyState;

                }
            };

            imagesLoad = new ExpectedCondition<Boolean>() {
                public final Boolean apply(final WebDriver driver) {
                    boolean docReadyState = true;
                    try {
                        JavascriptExecutor js;
                        List<WebElement> images = driver.findElements(By.cssSelector("img[src]"));
                        for (int i = 0; i < images.size(); i++) {
                            try {
                                js = (JavascriptExecutor) driver;
                                docReadyState = docReadyState && (Boolean) js.executeScript(
                                        "return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0",
                                        images.get(i));
                                if (!docReadyState) {
                                    break;
                                }
                            } catch (StaleElementReferenceException e) {
                                images = driver.findElements(By.cssSelector("img[src]"));
                                i--;
                                continue;
                            } catch (WebDriverException e) {

                                // setting the true value if any exception arise
                                // Ex:: inside frame or switching to new windows
                                // or
                                // switching to new frames
                                docReadyState = true;
                            }
                        }
                    } catch (WebDriverException e) {
                        docReadyState = true;
                    }
                    return docReadyState;
                }
            };

            framesLoad = new ExpectedCondition<Boolean>() {
                public final Boolean apply(final WebDriver driver) {
                    boolean docReadyState = true;
                    try {
                        JavascriptExecutor js;
                        List<WebElement> frames = driver.findElements(By.cssSelector("iframe[style*='hidden']"));
                        for (WebElement frame : frames) {
                            try {
                                driver.switchTo().defaultContent();
                                driver.switchTo().frame(frame);
                                js = (JavascriptExecutor) driver;
                                docReadyState = docReadyState
                                        && (Boolean) js.executeScript("return (document.readyState==\"complete\")");
                                driver.switchTo().defaultContent();
                                if (!docReadyState) {
                                    break;
                                }
                            } catch (WebDriverException e) {
                                docReadyState = true;
                            }
                        }
                    } catch (WebDriverException e) {
                        docReadyState = true;
                    } finally {
                        driver.switchTo().defaultContent();
                    }
                    return docReadyState;
                }
            };

            XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
            driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost")
                    : test.getParameter("deviceHost");
            driverPort = System.getProperty("hubPort") != null ? System.getProperty("hubPort")
                    : test.getParameter("devicePort");

            maxPageLoadWait = configProperty.getProperty("maxPageLoadWait") != null
                    ? Integer.valueOf(configProperty.getProperty("maxPageLoadWait"))
                    : maxPageLoadWait;
            WebDriverManager.chromedriver().setup();
            WebDriverManager.firefoxdriver().setup();
            WebDriverManager.iedriver().setup();
            WebDriverManager.safaridriver().setup();
            opt.addArguments("--ignore-certificate-errors");
            opt.addArguments("incognito");
            opt.addArguments("--disable-bundled-ppapi-flash");
            opt.addArguments("--disable-extensions");
            opt.addArguments("--disable-web-security");
            opt.addArguments("--always-authorize-plugins");
            opt.addArguments("--allow-running-insecure-content");
            opt.addArguments("--test-type");
            opt.addArguments("--enable-npapi");
            //opt.setCapability(CapabilityType.TAKES_SCREENSHOT, true);

            try {
                hubURL = new URL("http://" + driverHost + ":" + driverPort + "/wd/hub");
            } catch (MalformedURLException e) {
                // e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method to get instance of web driver using default parameters
     * 
     * @return browserName - Browser name
     * @throws MalformedURLException
     */
    public static WebDriver get() throws MalformedURLException {
        browserName = System.getProperty("browserName") != null ? System.getProperty("browserName")
                : Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("browserName")
                        .toLowerCase();
        return get(browserName, null);
    }

    /**
     * Method to get instance of web driver using browser details
     * 
     * @return driver - WebDriver
     * @throws MalformedURLException
     */
    public static WebDriver get(String browserSetup) throws MalformedURLException {
        // if(configProperty.getProperty("runMobileApp").equals("true"))
        // return AppiumDriverFactory.get();
        // else
    	
        return get(browserSetup, null);
    }

    /**
     * Webdriver to get the web driver with browser name and platform and setting
     * the desired capabilities for browsers
     * 
     * @param browserWithPlatform
     *            - Browser With Platform
     * @param proxy
     *            - Proxy
     * @return driver - WebDriver Instance
     * @throws MalformedURLException
     */
    @SuppressWarnings("deprecation")
	public static WebDriver get(String browserWithPlatform, Proxy proxy) throws MalformedURLException {
        String browser = null;
        String platform = null;
        String browserVersion = null;
        String appiumVersion = null;
        String deviceOrientation = null;
        String sauceUserName = null;
        String sauceAuthKey = null;
        WebDriver driver = null;
        String userAgent = null;
        String sauceLink = null;
        long startTime = StopWatch.startTime();

        // Get invoking test name to pass on to Jenkins
        String callerMethodName = new Exception().getStackTrace()[2].getMethodName();
        Log.event("TestCaseID:: " + callerMethodName);

        String driverInitializeInfo[] = null;
        // Handling System property variable overridden on parallel execution till web
        // driver initialization part
        synchronized (System.class) {
            // From local to sauce lab for browser test
            if (configProperty.hasProperty("runSauceLabFromLocal")
                    && configProperty.getProperty("runSauceLabFromLocal").trim().equalsIgnoreCase("true")) {

                sauceUserName = configProperty.hasProperty("sauceUserName")
                        ? configProperty.getProperty("sauceUserName")
                        : null;
                sauceAuthKey = configProperty.hasProperty("sauceAuthKey") ? configProperty.getProperty("sauceAuthKey")
                        : null;

                // Desktop
                if (configProperty.hasProperty("runDesktop")
                        && configProperty.getProperty("runDesktop").equalsIgnoreCase("true")) {

                    if (browserWithPlatform.contains("&")) {
                        driverInitializeInfo = browserWithPlatform.split("&");
                        browser = driverInitializeInfo[0];

                        // BrowserType enum available in selenium remote class, so here pointing our
                        // framework BrowserType
                        browser = com.amway.support.BrowserType.fromConfiguration(browser).getConfiguration();
                        browserVersion = driverInitializeInfo[1];
                        platform = driverInitializeInfo[2];
                    }

                    System.setProperty("SELENIUM_DRIVER",
                            "sauce-ondemand:?os=" + platform + "&browser=" + browser + "&browser-version="
                                    + browserVersion + "&username=" + sauceUserName + "&access-key=" + sauceAuthKey);
                    System.setProperty("SAUCE_USER_NAME", sauceUserName);
                    System.setProperty("SAUCE_API_KEY", sauceAuthKey);

                    // From local to sauce lab for device test
                    if (configProperty.hasProperty("runUserAgentDeviceTest")
                            && configProperty.getProperty("runUserAgentDeviceTest").trim().equalsIgnoreCase("true")) {
                        deviceName = driverInitializeInfo[3];
                        System.setProperty("runUserAgentDeviceTest", "true");
                        System.setProperty("deviceName", deviceName);
                    }
                }
                // CSS
                if (configProperty.hasProperty("runCSS")
                        && configProperty.getProperty("runCSS").equalsIgnoreCase("true")) {

                    if (browserWithPlatform.contains("&")) {
                        driverInitializeInfo = browserWithPlatform.split("&");
                        browser = driverInitializeInfo[0];

                        // BrowserType enum available in selenium remote class, so here pointing our
                        // framework BrowserType
                        browser = com.amway.support.BrowserType.fromConfiguration(browser).getConfiguration();
                        browserVersion = driverInitializeInfo[1];
                        platform = driverInitializeInfo[2];
                    }

                    System.setProperty("SELENIUM_DRIVER",
                            "sauce-ondemand:?os=" + platform + "&browser=" + browser + "&browser-version="
                                    + browserVersion + "&username=" + sauceUserName + "&access-key=" + sauceAuthKey);
                    System.setProperty("SAUCE_USER_NAME", sauceUserName);
                    System.setProperty("SAUCE_API_KEY", sauceAuthKey);

                    // From local to sauce lab for device test
                    if (configProperty.hasProperty("runUserAgentDeviceTest")
                            && configProperty.getProperty("runUserAgentDeviceTest").trim().equalsIgnoreCase("true")) {
                        deviceName = driverInitializeInfo[3];
                        System.setProperty("runUserAgentDeviceTest", "true");
                        System.setProperty("deviceName", deviceName);
                    }
                }
                // Mobile
                if (configProperty.hasProperty("runMobile")
                        && configProperty.getProperty("runMobile").equalsIgnoreCase("true")) {

                    browser = configProperty.getProperty("mobileBrowserName");
                    platform = configProperty.getProperty("mobilePlatformName");
                    deviceName = configProperty.getProperty("mobileDeviceName");
                    browserVersion = configProperty.getProperty("mobilePlatformVersion");
                    appiumVersion = configProperty.getProperty("appiumVersion");
                    deviceOrientation = configProperty.getProperty("deviceOrientation");

                    // adding method name into sauce lab build
                    iOSDeviceCapabilities.setCapability("name", callerMethodName);

                    iOSDeviceCapabilities.setCapability("browserName", browser);
                    iOSDeviceCapabilities.setCapability("platformVersion", browserVersion);
                    iOSDeviceCapabilities.setCapability("platformName", platform);
                    iOSDeviceCapabilities.setCapability("deviceName", deviceName);
                    iOSDeviceCapabilities.setCapability("deviceOrientation", deviceOrientation);
                    iOSDeviceCapabilities.setCapability("appiumVersion", appiumVersion);

                    driver = new RemoteWebDriver(new URL(
                            "http://" + sauceUserName + ":" + sauceAuthKey + "@ondemand.saucelabs.com:80/wd/hub"),
                            iOSDeviceCapabilities);

                    String saucelabsSessionId = (((RemoteWebDriver) driver).getSessionId()).toString();
                    sauceLink = "http://saucelabs.com/jobs/" + saucelabsSessionId + "?auth="
                            + newHMACMD5Digest(sauceUserName + ":" + sauceAuthKey, saucelabsSessionId);
                    logger.debug("Saucelab link for " + callerMethodName + ":: " + sauceLink);
                    //Log.addSauceJobUrlToReport(driver, sauceLink);
                   // Log.addTestRunMachineInfo(driver, sauceLink);
                    return driver;
                }
            }
            // check for Jenkins override from Jenkins to sauce lab
           /* if (System.getProperty("SELENIUM_DRIVER") != null || System.getenv("SELENIUM_DRIVER") != null) {
                return newWebDriverInstanceFromEnvironment(callerMethodName);
            }*/
        }
        // To support local to local execution by grid configuration
        if (browserWithPlatform.contains("_")) {
            browser = browserWithPlatform.split("_")[0].toLowerCase().trim();
            platform = browserWithPlatform.split("_")[1].toUpperCase().trim();
            System.out.println("browser--->"+browser);
            System.out.println("platform--->"+platform);
        } else {
            platform = "ANY";
        }

        try {
            if ("chrome".equalsIgnoreCase(browser)) {

                if (configProperty.hasProperty("runUserAgentDeviceTest")
                        && configProperty.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true")) {
                    deviceName = configProperty.hasProperty("deviceName") ? configProperty.getProperty("deviceName")
                            : null;
                    userAgent = mobEmuUA.getUserAgent(deviceName);
                    if (userAgent != null && deviceName != null) {
                        driver = new RemoteWebDriver(hubURL, setChromeUserAgent(deviceName, userAgent));
                    } else {
                        logger.error(
                                "Given user agent configuration not yet implemented (or) check the parameters(deviceName) value in config.properties: "
                                        + deviceName);
                    }
                } else {
                    opt.addArguments("--start-maximized");
                    opt.addArguments("--disable-web-security");
                    Map<String, Object> prefs = new HashMap<String, Object>();
                    prefs.put("credentials_enable_service", false);
                    prefs.put("profile.password_manager_enabled", false);
                    opt.setExperimentalOption("prefs", prefs);
                    if(platform != "windows")
                    {
                    	System.out.print("Entering into the block");
                    	opt.addArguments("--no-sandbox");
                    	//opt.addArguments("--remote-debugging-port=9222");
                    	//opt.addArguments("--headless=new");                    	
                    	//opt.addArguments("--disable-gpu");
                    	//opt.addArguments("start-maximized"); // open Browser in maximized mode
                    	//opt.addArguments("disable-infobars"); // disabling infobars
                    	//opt.addArguments("--disable-extensions"); // disabling extensions
                    	//opt.addArguments("--disable-gpu"); // applicable to windows os only
                    	opt.addArguments("--disable-dev-shm-usage"); 
                    	// overcome limited resource problems
                    	opt.addArguments("window-size=1920,1080");
                    	opt.addArguments("start-maximized");
                    	opt.addArguments("--proxy-server='direct://'");
                    	opt.addArguments("--proxy-bypass-list=*");
                    	opt.addArguments("--remote-allow-origins=*");
                    	opt.addArguments("--disable-web-security");
         
                    }
					chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
                    chromeCapabilities.setPlatform(Platform.fromString(platform));
                    
                    System.out.print("Platform Cabalities"+ chromeCapabilities.getPlatformName());
                   
                    if (proxy != null)
                        chromeCapabilities.setCapability(CapabilityType.PROXY, proxy);
                    System.setProperty("webdriver.http.factory", "jdk-http-client");
                    driver = new ChromeDriver(opt);
                }
            } else if ("iexplorer".equalsIgnoreCase(browser)) {
                ieCapabilities.setCapability("enablePersistentHover", false);
                ieCapabilities.setCapability("ignoreZoomSetting", true);
                ieCapabilities.setCapability("nativeEvents", false);
                ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
                        true);
                ieCapabilities.setPlatform(Platform.fromString(platform));

                if (proxy != null)
                    ieCapabilities.setCapability(CapabilityType.PROXY, proxy);
                 driver = new InternetExplorerDriver();
            } else if ("edge".equalsIgnoreCase(browser)) {
                edgeCapabilities.setPlatform(Platform.fromString(platform));
                driver = new RemoteWebDriver(hubURL, edgeCapabilities);
            } else if ("safari".contains(browser.split("\\&")[0])) {
                if (configProperty.hasProperty("runUserAgentDeviceTest")
                        && configProperty.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true")) {
                    deviceName = configProperty.hasProperty("deviceName") ? configProperty.getProperty("deviceName")
                            : null;
                    userAgent = mobEmuUA.getUserAgent(deviceName);
                    if (userAgent != null && deviceName != null) {
                        driver = new RemoteWebDriver(hubURL, setChromeUserAgent(deviceName, userAgent));
                    } else {
                        logger.error(
                                "Given user agent configuration not yet implemented (or) check the parameters(deviceName) value in config.properties: "
                                        + deviceName);
                    }
                } else {
                    safariCapabilities.setCapability("prerun",
                            "https://gist.githubusercontent.com/saucyallison/3a73a4e0736e556c990d/raw/d26b0195d48b404628fc12342cb97f1fc5ff58ec/disable_fraud.sh");
                    driver = new RemoteWebDriver(hubURL, safariCapabilities);
                }
                // To run a ZAP TC's use Browser opt as zap
            } else if ("zap".equalsIgnoreCase(browser)) {
                Proxy zapChromeProxy = new Proxy();
                zapChromeProxy.setHttpProxy("localhost:8080");
                zapChromeProxy.setFtpProxy("localhost:8080");
                zapChromeProxy.setSslProxy("localhost:8080");
                chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
                chromeCapabilities.setCapability(CapabilityType.PROXY, zapChromeProxy);
                chromeCapabilities.setPlatform(Platform.fromString(platform));
                driver = new RemoteWebDriver(hubURL, chromeCapabilities);
            } else {
                synchronized (WebDriverFactory.class) {
                    firefoxCapabilities.setCapability("unexpectedAlertBehaviour", "ignore");
                    firefoxCapabilities.setPlatform(Platform.fromString(platform));
                    driver = new FirefoxDriver();
                }
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(maxPageLoadWait));
            }
            Assert.assertNotNull(driver,
                    "Driver did not intialize...\n Please check if hub is running / configuration settings are corect.");

            if (!"ANDROID".equalsIgnoreCase(platform)) {
                driver.manage().window().maximize();
            }
        } catch (UnreachableBrowserException e) {
            e.printStackTrace();
            throw new SkipException("Hub is not started or down.");
        } catch (WebDriverException e) {

            try {
                if (driver != null) {
                    driver.quit();
                }
            } catch (Exception e1) {
                e.printStackTrace();
            }

            if (e.getMessage().toLowerCase().contains("error forwarding the new session empty pool of vm for setup")) {
                throw new SkipException("Node is not started or down.");
            } else if (e.getMessage().toLowerCase()
                    .contains("error forwarding the new session empty pool of vm for setup")
                    || e.getMessage().toLowerCase().contains("cannot get automation extension")
                    || e.getMessage().toLowerCase().contains("chrome not reachable")) {
                Log.message("&emsp;<b> --- Re-tried as browser crashed </b>");
                try {
                    driver.quit();
                } catch (WebDriverException e1) {
                    e.printStackTrace();
                }
                driver = get();
            } else {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception encountered in getDriver Method." + e.getMessage().toString());
        } finally {
            // ************************************************************************************************************
            // * Update start time of the tests once free slot is assigned by
            // RemoteWebDriver - Just for reporting purpose
            // *************************************************************************************************************/
            try {
                Field f = Reporter.getCurrentTestResult().getClass().getDeclaredField("m_startMillis");
                f.setAccessible(true);
                f.setLong(Reporter.getCurrentTestResult(), Calendar.getInstance().getTime().getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        Log.event("Driver::initialize::Get", StopWatch.elapsedTime(startTime));
        return driver;
    }

    /**
     * newWebDriverInstanceFromEnvironment is primarily for use with Jenkins.
     * Currently the only environment it will setup correctly is saucelabs. It grabs
     * the browser, os, screen res, etc. from the environment so you can run your
     * tests with a configuration matrix.
     * 
     * required environment variables:
     * 
     * SELENIUM_DRIVER, SAUCE_USER_NAME, SAUCE_API_KEY
     * 
     * 
     * @param testName
     *            - name of the test to pass to the grid
     * @return remote WebDriver instance
     * @throws MalformedURLException 
     */
    @SuppressWarnings("unused")
  /* public static WebDriver newWebDriverInstanceFromEnvironment(String testName) throws MalformedURLException {
        // right now we only support sauce labs
        if (System.getenv("SAUCE_USER_NAME") != null || System.getProperty("SAUCE_USER_NAME") != null) {
            SauceLabsCapabilitiesConfiguration caps = new SauceLabsCapabilitiesConfiguration(testName,
                    System.getenv("BUILD_ID"));
            String screenResolution = configProperty.hasProperty("screenResolution")
                    ? configProperty.getProperty("screenResolution")
                    : null;
            String seleniumVersion = configProperty.hasProperty("seleniumVersion")
                    ? configProperty.getProperty("seleniumVersion")
                    : null;
            String iedriverVersion = configProperty.hasProperty("iedriverVersion")
                    ? configProperty.getProperty("iedriverVersion")
                    : null;
            String chromedriverVersion = configProperty.hasProperty("chromedriverVersion")
                    ? configProperty.getProperty("chromedriverVersion")
                    : null;
            String safaridriverVersion = configProperty.hasProperty("chromedriverVersion")
                    ? configProperty.getProperty("chromedriverVersion")
                    : null;
            String maxTestDuration = configProperty.hasProperty("maxTestDuration")
                    ? configProperty.getProperty("maxTestDuration")
                    : null;
            String commandTimeout = configProperty.hasProperty("commandTimeout")
                    ? configProperty.getProperty("commandTimeout")
                    : null;
            String idleTimeout = configProperty.hasProperty("idleTimeout") ? configProperty.getProperty("idleTimeout")
                    : null;

            caps.setRecordSnapshot(false);

            // To setting the screen resolution
            if (screenResolution != null) {               
                // To handling selenium version for safari execution
                if ((System.getProperty("SELENIUM_DRIVER") != null
                        && !System.getProperty("SELENIUM_DRIVER").contains("safari")) || (System.getenv("SELENIUM_DRIVER") != null
                                && !System.getenv("SELENIUM_DRIVER").contains("safari"))) {
                	caps.setScreenResolution(screenResolution);
                } else {
                	caps.setScreenResolution("1920x1440");
                }
            }

            // To setting the selenium, ie and chrome driver version capabilities
            if (seleniumVersion != null) {
                // To handling selenium version for safari execution
                if ((System.getProperty("SELENIUM_DRIVER") != null
                        && !System.getProperty("SELENIUM_DRIVER").contains("safari")))
                    caps.setSeleniumVersion(seleniumVersion);
                if (System.getenv("SELENIUM_DRIVER") != null && !System.getenv("SELENIUM_DRIVER").contains("safari"))
                    caps.setSeleniumVersion(seleniumVersion);
            }

            if (iedriverVersion != null)
                caps.setIeDriverVersion(iedriverVersion);
            if (chromedriverVersion != null)
                caps.setChromeDriverVersion(chromedriverVersion);

            // Timeout capabilities
            if (maxTestDuration != null)
                caps.setMaxTestDuration(maxTestDuration);
            if (commandTimeout != null)
                caps.setCommandTimeout(commandTimeout);
            if (idleTimeout != null)
                caps.setIdleTimeout(idleTimeout);

            WebDriver driver = null;
            String userAgent = null;
            // User agent capabilities
            if (System.getProperty("runUserAgentDeviceTest") != null) {
                if (System.getProperty("runUserAgentDeviceTest").equalsIgnoreCase("true")) {
                    deviceName = System.getProperty("deviceName") != null ? System.getProperty("deviceName") : null;
                    userAgent = mobEmuUA.getUserAgent(deviceName) != null ? mobEmuUA.getUserAgent(deviceName) : null;

                    if (deviceName != null && userAgent != null) {
                 //       driver = SeleniumFactory.createWebDriver(caps.getUserAgentDesiredCapabilities(caps, deviceName, userAgent));
                    } else {
                        logger.error("Invalid mobile emulation configuration, check the parameters(deviceName) value: "
                                + deviceName);
                    }
                } else {
                    logger.error("runUserAgentDeviceTest value has been set as false");
                }
            } else {
     //           driver = SeleniumFactory.createWebDriver(caps.getDesiredCapabilities());
           }
            String saucelabsSessionId = (((RemoteWebDriver) driver).getSessionId()).toString();
            String sauceLink = "http://saucelabs.com/jobs/" + saucelabsSessionId + "?auth=" + newHMACMD5Digest(
                    System.getenv("SAUCE_USER_NAME") + ":" + System.getenv("SAUCE_API_KEY"), saucelabsSessionId);
            logger.debug("Saucelab link for " + testName + ":: " + sauceLink);
            //Log.addSauceJobUrlToReport(driver, sauceLink);
            
            return driver;
        } else {
            return null;
        }
    }
*/
    /**
     * To generates an md5 HMAC digest based on the provided key and message.
     * 
     * @param keyString
     *            - Secret key
     * @param msg
     *            - The message to be authenticated
     * @return sEncodedString - the digest
     */
    public static String newHMACMD5Digest(String keyString, String msg) {
        String sEncodedString = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();

            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            sEncodedString = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return sEncodedString;
    }

    /**
     * Get the test session Node IP address,port when executing through Grid
     * 
     * @param driver
     *            : WebDriver
     * @return nodeIP - Session ID
     * @throws Exception
     */
    public static final String getTestSessionNodeIP(final WebDriver driver) throws Exception {
        XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
        driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost")
                : test.getParameter("deviceHost");
        driverPort = test.getParameter("devicePort");
        HttpHost host = new HttpHost(driverHost, Integer.parseInt(driverPort));
        HttpClient client = HttpClientBuilder.create().build();
        URL testSessionApi = new URL("http://" + driverHost + ":" + driverPort + "/grid/api/testsession?session="
                + ((RemoteWebDriver) driver).getSessionId());
        BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST",
                testSessionApi.toExternalForm());
        HttpResponse response = client.execute(host, r);
        JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
        String nodeIP = object.getString("proxyId").toLowerCase();
        nodeIP = nodeIP.replace("http://", "");
        nodeIP = nodeIP.replaceAll(":[0-9]{1,5}", "").trim();
        return nodeIP;
    }

    /**
     * Get the test session Hub IP address, port when executing through Grid
     * 
     * @param driver
     *            - WebDriver
     * @return nodeIP - Session ID
     * @throws Exception
     */
    public static final String getHubSession(final WebDriver driver) throws Exception {
        XmlTest test = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest();
        driverHost = System.getProperty("hubHost") != null ? System.getProperty("hubHost")
                : test.getParameter("deviceHost");
        driverPort = test.getParameter("devicePort");
        HttpHost host = new HttpHost(driverHost, Integer.parseInt(driverPort));
        HttpClient client = HttpClientBuilder.create().build();
        URL testSessionApi = new URL("http://" + driverHost + ":" + driverPort + "/grid/api/testsession?session="
                + ((RemoteWebDriver) driver).getSessionId());
        BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST",
                testSessionApi.toExternalForm());
        HttpResponse response = client.execute(host, r);
        JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
        String nodeIP = object.getString("proxyId").toLowerCase();
        nodeIP = nodeIP.replace("http://", "");
        nodeIP = nodeIP.replaceAll(":[0-9]{1,5}", "").trim();
        return nodeIP;
    }

    /**
     * To storing chrome mobile emulation configurations(width, height, pixelRatio)
     * and returning the capabilities
     * 
     * <p>
     * if required feasible result then set andriodWidth, androidHeight,
     * androidPixelRatio, iosWidth,androidHeight and iosPixelRatio values in the
     * config.propeties
     *
     * @param deviceName
     *            - device name
     * @param userAgent
     *            - User agent
     * @return chromeCapabilities
     */
    public static DesiredCapabilities setChromeUserAgent(String deviceName, String userAgent) {
        Map<String, Object> deviceMetrics = new HashMap<String, Object>();
        Map<String, Object> mobileEmulation = new HashMap<String, Object>();

        int width = 0;
        int height = 0;
        Double pixRatio = null;

        width = Integer.valueOf(mobEmuUA.getDeviceWidth(deviceName));
        height = Integer.valueOf(mobEmuUA.getDeviceHeight(deviceName));
        pixRatio = Double.valueOf(mobEmuUA.getDevicePixelRatio(deviceName));

        deviceMetrics.put("width", width);
        deviceMetrics.put("height", height);
        deviceMetrics.put("pixelRatio", pixRatio);
        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent", userAgent);
        Log.event("mobileEmulation settings::==> " + mobileEmulation);
        opt.setExperimentalOption("mobileEmulation", mobileEmulation);
        chromeCapabilities.setCapability(ChromeOptions.CAPABILITY, opt);
        return chromeCapabilities;
    }

    /**
     * To print the Har Summary details
     * 
     * @param har
     */
    public static void printHarData(Har har) {
        HarLog log = har.getLog();
        List<HarEntry> harEntries = log.getEntries();
        Long totalSize = 0L;
        int callCount = 0;
        long totalTime = 0;
        for (HarEntry entry : harEntries) {
            callCount++;
            if (entry.getResponse() == null) {
                continue;
            }
            totalSize += entry.getResponse().getBodySize();
            totalTime += entry.getTime(TimeUnit.MILLISECONDS);
        }
        HarSummary summary = new HarSummary((double) totalSize / 1024, callCount, totalTime);
        Log.message("#################<b>PERF DATA</b>###################");
        Log.message("<br>");
        Log.message("Call count : " + summary.getCallCount());
        Log.message("Size : " + summary.getTotalPayloadSize() / 1024 + " MB");
        Log.message("Total load time : " + summary.getTotalLoadTime() / 1000 + " seconds");
    }

} // WebDriverFactory