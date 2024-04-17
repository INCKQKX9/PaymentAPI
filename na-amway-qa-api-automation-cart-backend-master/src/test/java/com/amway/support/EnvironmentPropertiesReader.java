package com.amway.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.testng.log4testng.Logger;

/**
 * EnvironmentPropertiesReader is to set the environment variable declaration
 * mapping for config properties in the UI test
 */
public class EnvironmentPropertiesReader {

	private static final Logger log = Logger.getLogger(EnvironmentPropertiesReader.class);
	private static EnvironmentPropertiesReader envProperties;
	private String accTransusername;
	private String accMgrusername;
	private String password;
	private String url;
	private String browser;
	private String dbURL;
	private String dbUsername;
	private String sauceLab;
	private String dbpassword;
	private String dbDriver;
	private static Properties property = new Properties();
	private String filePath = "./src/main/resources/data.properties";
	//private String jenkinsPath = "/src/main/resources/jenkinsconfig";
	private String testlinkURL;
	private String testLinkDevKey;
	private String testProject;
	private String testPlan;
	private String testSuiteID;
	//private String mobileTestSuiteID;
	private String testBuildId;
	private String ecomUsername;
	private String ecomPassword;
	private Properties properties;

	public EnvironmentPropertiesReader() {
		try {
			FileInputStream fileInputStream = new FileInputStream(filePath);
			property.load(fileInputStream);
			this.setTransMgrUsername(property.getProperty("transmgrusername"));
			this.setAccMgrUsername(property.getProperty("accountmgrusername"));
			this.setPassword(property.getProperty("password"));
			this.setBrowser(property.getProperty("browser"));
			this.setURL(property.getProperty("URL"));
			this.setDBurl(property.getProperty("DBurl"));
			this.setDBpassword(property.getProperty("DBpassword"));
			this.setDBusername(property.getProperty("DBusername"));
			this.setDbDriver(property.getProperty("DBdriver"));
			this.setTestlinkURL(property.getProperty("testlinkUrl"));
			this.setTestLinkDevKey(property.getProperty("testlinkKey"));
			this.setTestProject(property.getProperty("testProject"));
			this.setTestPlan(property.getProperty("testPlan"));
			this.setTestSuiteID(property.getProperty("testSuiteID"));
			this.setTestBuildId(property.getProperty("testBuildId"));
			this.setSauceLab(property.getProperty("sauceLab"));
			
			
			fileInputStream.close();
			properties = loadProperties();

		} catch (FileNotFoundException e) {
			e.getMessage();
		} catch (IOException e) {
			e.getMessage();
		}

	}

	/*
	 * public EnvironmentPropertiesReader(String fileName) { try { FileInputStream
	 * fileInputStream = new FileInputStream( new
	 * java.io.File(".").getCanonicalPath() + jenkinsPath + File.separatorChar +
	 * fileName); property.load(fileInputStream);
	 * this.setTransMgrUsername(property.getProperty("transmgrusername"));
	 * this.setAccMgrUsername(property.getProperty("accountmgrusername"));
	 * this.setPassword(property.getProperty("password"));
	 * this.setBrowser(property.getProperty("browser"));
	 * this.setURL(property.getProperty("URL"));
	 * this.setDBurl(property.getProperty("DBurl"));
	 * this.setDBpassword(property.getProperty("DBpassword"));
	 * this.setDBusername(property.getProperty("DBusername"));
	 * this.setDbDriver(property.getProperty("DBdriver")); fileInputStream.close();
	 * 
	 * } catch (FileNotFoundException e) { e.getMessage(); } catch (IOException e) {
	 * e.getMessage(); } }
	 */

	public EnvironmentPropertiesReader(String fileName) {
		try {
			FileInputStream fileInputStream = new FileInputStream("./src/main/resources/" + fileName);
			property.load(fileInputStream);
			this.setTestlinkURL(property.getProperty("testlinkUrl"));
			this.setTestLinkDevKey(property.getProperty("testlinkKey"));
			this.setTestProject(property.getProperty("testProject"));
			this.setTestPlan(property.getProperty("testPlan"));
			this.setTestSuiteID(property.getProperty("testSuiteID"));
			this.setTestBuildId(property.getProperty("testBuildId"));
			this.setEcomUsername(property.getProperty("ecomUsername"));
			this.setEcomPassword(property.getProperty("ecomPassword"));
			fileInputStream.close();
			
		} catch (FileNotFoundException e) {
			e.getMessage();
		} catch (IOException e) {
			e.getMessage();
		}
	}

	private Properties loadProperties() {
		File file = new File("./src/main/resources/config.properties");
		FileInputStream fileInput = null;
		Properties props = new Properties();

		try {
			fileInput = new FileInputStream(file);
			props.load(fileInput);
			fileInput.close();
		} catch (FileNotFoundException e) {
			log.error("config.properties is missing or corrupt : " + e.getMessage());
		} catch (IOException e) {
			log.error("read failed due to: " + e.getMessage());
		}

		return props;
	}
	
	@SuppressWarnings("unused")
	public static Properties loadProperties(String fileName) {
		File file = new File("./src/main/resources/" + fileName + ".properties");
		FileInputStream fileInput = null;
		Properties props = new Properties();

		try {
			fileInput = new FileInputStream(file);
			props.load(fileInput);
			fileInput.close();
		} catch (FileNotFoundException e) {
			log.error("config.properties is missing or corrupt : " + e.getMessage());
		} catch (IOException e) {
			log.error("read failed due to: " + e.getMessage());
		}

		return props;
	}

	public static EnvironmentPropertiesReader getInstance(String fileName) {
		if (envProperties == null) {
			envProperties = new EnvironmentPropertiesReader(fileName);
		}
		return envProperties;
	}

	public static EnvironmentPropertiesReader getInstance() {
		if (envProperties == null) {
			envProperties = new EnvironmentPropertiesReader();
		}
		return envProperties;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public boolean hasProperty(String key) {
		return StringUtils.isNotBlank(properties.getProperty(key));
	}

	/**
	 * Set Account Transactional user name
	 * 
	 * @param accTransusername : Account Transactional user name
	 */
	public void setTransMgrUsername(String accTransusername) {
		this.accTransusername = accTransusername;
	}

	/**
	 * Get user name
	 * 
	 * @return userName
	 */
	public String getTransMgrUsername() {
		return accTransusername;
	}

	/**
	 * Set Account Manager User Name
	 * 
	 * @param accMgrusername : Account Manager User Name
	 */
	public void setAccMgrUsername(String accMgrusername) {
		this.accMgrusername = accMgrusername;
	}

	/**
	 * Get user name
	 * 
	 * @return userName
	 */
	public String getAccMgrUsername() {
		return accMgrusername;
	}

	/**
	 * set Password
	 * 
	 * @param password : Password String
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get password
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * set URL
	 * 
	 * @param url
	 */

	/**
	 * set URL1
	 * 
	 * @param url : Site URL
	 */
	public void setURL(String url) {
		this.url = url;
	}

	/**
	 * Get URL
	 * 
	 * @return url
	 */
	public String getURL() {
		return url;
	}

	/**
	 * set browser
	 * 
	 * @param browser : Browser Type
	 */
	public void setBrowser(String browser) {
		this.browser = browser;
	}

	/**
	 * get browser
	 * 
	 * @return browser
	 */
	public String getBrowser() {
		return browser;
	}

	/**
	 * set DBusername
	 * 
	 * @param dBusername : DB User Name
	 */
	public void setDBusername(String dBusername) {
		dbUsername = dBusername;
	}

	/**
	 * get DBusername
	 * 
	 * @return dBusername
	 */
	public String getDBusername() {
		return dbUsername;
	}

	/**
	 * Set SauceLab execution flag value
	 * 
	 * @param sauceLab : Sauce Lab Execution Flag Value
	 */
	public void setSauceLab(String sauceLab) {
		this.sauceLab = sauceLab;
	}

	/**
	 * get SauceLab execution flag value
	 * 
	 * @return SauceLab flag value
	 */
	public String getSauceLab() {
		System.out.println("sauce value" + sauceLab);
		return sauceLab;
	}

	/**
	 * set dBpassword
	 * 
	 * @param dBpassword : DB Password
	 */
	public void setDBpassword(String dBpassword) {
		dbpassword = dBpassword;
	}

	/**
	 * get dBpassword
	 * 
	 * @return dBpassword
	 */
	public String getDBpassword() {
		return dbpassword;
	}

	/**
	 * Set DB URL
	 * 
	 * @param dBurl : DB URL
	 */
	public void setDBurl(String dBurl) {
		dbURL = dBurl;
	}

	/**
	 * get dbURL
	 * 
	 * @return dbURL
	 */
	public String getDBurl() {
		return dbURL;
	}

	/**
	 * set dbDriver
	 * 
	 * @param dbDriver : DB Driver
	 */
	public void setDbDriver(String dbDriver) {
		this.dbDriver = dbDriver;
	}

	/**
	 * get dbDriver
	 * 
	 * @return dbDriver
	 */
	public String getDbDriver() {
		return dbDriver;
	}

	public void setTestlinkURL(String testlinkURL) {
		this.testlinkURL = testlinkURL;
	}

	public String getTestlinkURL() {
		return testlinkURL;
	}

	public void setTestLinkDevKey(String testLinkDevKey) {
		this.testLinkDevKey = testLinkDevKey;
	}

	public String getTestLinkDevKey() {
		return testLinkDevKey;
	}

	public void setTestProject(String testProject) {
		this.testProject = testProject;
	}

	public String getTestProject() {
		return testProject;
	}

	public void setTestPlan(String testPlan) {
		this.testPlan = testPlan;
	}

	public String getTestPlan() {
		return testPlan;
	}

	public void setEcomUsername(String ecomUsername) {
		this.ecomUsername = ecomUsername;
	}

	public String getEcomUsername() {
		return ecomUsername;
	}

	public void setEcomPassword(String ecomPassword) {
		this.ecomPassword = ecomPassword;
	}

	public String getEcomPassword() {
		return ecomPassword;
	}

	public void setTestBuildId(String buildId) {
		this.testBuildId = buildId;

	}

	public String getTestBuildId() {
		return testBuildId;
	}

	public void setTestSuiteID(String suiteID) {
		this.testSuiteID = suiteID;
	}

	public String getTestSuiteID() {
		return testSuiteID;
	}

	/**
	 * Reading Data From env.properties File
	 */
	public static Map<String, String> readPropertiesFile() {
		Map<String, String> propertiesMap = new HashMap<>();
		FileInputStream fis = null;
		Properties properties = new Properties();

		try {
			fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\env.properties");
			properties.load(fis);

			// Iterate over the properties and store them in the map
			for (String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				propertiesMap.put(key, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return propertiesMap;
	}
}
