package com.amway.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.testng.log4testng.Logger;

/**
 * EnvironmentPropertiesReader is to set the environment variable declaration
 * mapping for data properties in the UI test
 */
public class TestDataPropertiesReader {

	private static final Logger log = Logger
			.getLogger(TestDataPropertiesReader.class);
	private static TestDataPropertiesReader envProperties;
	private static Properties property = new Properties();
	private String filePath = "./src/main/resources/data.properties";
	private String visaCardNumber;
	private String amexCardNumber;
	private String discoverCardNumber;
	private String masterCardNumber;
	private String productName;
	
	

	private Properties properties;

	public TestDataPropertiesReader() {
		try {
			FileInputStream fileInputStream = new FileInputStream(filePath);
			property.load(fileInputStream);
			this.setVisaCardNumber(property.getProperty("visaCardNumber"));
			this.setAmexCardNumber(property.getProperty("amexCardNumber"));
			this.setDiscoverCardNumber(property.getProperty("discoverCardNumber"));
			this.setMasterCardNumber(property.getProperty("masterCardNumber"));
			this.setProductName(property.getProperty("productName"));

			fileInputStream.close();
			properties = loadProperties();

		} catch (FileNotFoundException e) {
			e.getMessage();
		} catch (IOException e) {
			e.getMessage();
		}

	}
	
	/**
	 * Parameterized constructor to load given property file
	 * @param propertyFile - Property file name without extension
	 */
	private TestDataPropertiesReader(String propertyFile) {
		properties = loadProperties(propertyFile);
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
			log.error("config.properties is missing or corrupt : "
					+ e.getMessage());
		} catch (IOException e) {
			log.error("read failed due to: " + e.getMessage());
		}

		return props;
	}
	
	/**
	 * To load properties from given property file name without extension
	 * @param propertyFile - Property File Name without Extension
	 * @return Properties - All properties available in property file
	 */
	private Properties loadProperties(String propertyFile) {
		File file = new File("./src/main/resources/"+propertyFile+".properties");
		FileInputStream fileInput = null;
		Properties props = new Properties();

		try {
			fileInput = new FileInputStream(file);
			props.load(fileInput);
			fileInput.close();
		} catch (FileNotFoundException e) {
			log.error(""+propertyFile+".properties is missing or corrupt : " + e.getMessage());
		} catch (IOException e) {
			log.error("read failed due to: " + e.getMessage());
		}

		return props;
	}
	

	public static TestDataPropertiesReader getInstance() {
		if (envProperties == null) {
			envProperties = new TestDataPropertiesReader();
		}
		return envProperties;
	}
	
	/**
	 * To get given property file instance of EnvirionmentPropertyReader
	 * @param propertyFile - Property File name without
	 * @return EnvironmentPropertiesReader - Property file instance
	 */
	public static TestDataPropertiesReader getInstance(String propertyFile) {
		TestDataPropertiesReader envProperties=null;
		if (envProperties == null) {
			envProperties = new TestDataPropertiesReader(propertyFile);
		}
		return envProperties;
	}
	
	public Set<Object> Keyset() {
		return properties.keySet();
	}
	
	public int getsize() {
		return properties.size();
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	public boolean hasProperty(String key) {
		return StringUtils.isNotBlank(properties.getProperty(key));
	}

	public void setVisaCardNumber(String visaCardNumber) {
		this.visaCardNumber = visaCardNumber;
	}

	public String getVisaCardNumber() {
		return visaCardNumber;
	}
	
	public void setAmexCardNumber(String amexCardNumber) {
		this.amexCardNumber = amexCardNumber;
	}

	public String getAmexCardNumber() {
		return amexCardNumber;
	}
	
	public void setDiscoverCardNumber(String discoverCardNumber) {
		this.discoverCardNumber = discoverCardNumber;
	}

	public String getDiscoverCardNumber() {
		return discoverCardNumber;
	}
	
	public void setMasterCardNumber(String masterCardNumber) {
		this.masterCardNumber = masterCardNumber;
	}
	public String getMasterCardNumber() {
		return masterCardNumber;
	}	
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	
	/**
	 * Special method used to get only data.properties based on param passed.
	 * @param key - Product Definition Syntax
	 * @return - Product ID
	 */
	public String get(String key) {
		String value = properties.getProperty(key);
		if(value.isEmpty() || value.equals(null) || value.equals("")) 
			Log.fail("Product ID missing in property file / Product definition(" + key + ") syntax is not valid.");

		return value.trim();
	}
}
