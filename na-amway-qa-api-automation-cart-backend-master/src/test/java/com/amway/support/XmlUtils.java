package com.amway.support;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amway.support.MapUtils;

/**
 * XML Utils to manipulate xml tags in xml file.
 */
@SuppressWarnings("unused")
public class XmlUtils {

	/**
	 * To Get all the Tags from xml document that matches given tag name
	 * 
	 * @param xmlLocation
	 *            - XML File Location
	 * @param originNode
	 *            - Tag Name
	 * @return - List of Tags with given Tag Name as NodeList
	 * @throws Exception
	 *             -
	 */
	static public NodeList getNodeListFromXML(String xmlLocation,
			String originNode) throws Exception {
		File fXmlFile = new File(xmlLocation);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		NodeList nList = doc.getElementsByTagName(originNode);
		return nList;
	}
	
	static public NodeList getNodeListFromXML1(String xmlLocation,
			String originNode) throws Exception {
		File fXmlFile = new File(xmlLocation);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		Document nList1 = (Document) doc.getElementsByTagName(originNode);
		NodeList nList = nList1.getElementsByTagName("shipping-address");
		return nList;
	}

	/**
	 * To get all child nodes from given parent node
	 * 
	 * @param node
	 *            - Parent Node
	 * @return - NodeList of child nodes
	 * @throws Exception
	 *             -
	 */
	static public NodeList getNodeListFromNode(Node node) throws Exception {
		NodeList nList = node.getChildNodes();
		return nList;
	}

	/**
	 * To get all content from given xml with matching tagName as Key & tag
	 * content as value
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param tagName
	 *            - Tag Name
	 * @return - LinkedHashMap<String, Object> - Object can be String /
	 *         LinkedHashMap<String, Object/String>
	 * @throws Exception
	 *             -
	 */
	static public LinkedHashMap<String, Object> getDataFromXML(
			String xmlLocation, String tagName, int i) throws Exception {
		LinkedHashMap<String, Object> dataToReturn = new LinkedHashMap<String, Object>();
		NodeList nList = getNodeListFromXML(xmlLocation, tagName);
		for (int index = 0; index < nList.getLength(); index++) {

			dataToReturn.put(getTagNameFromNode(nList.item(i)) + "-"
					+ (index + 1), getNodeData(nList.item(i)));
		}

		return dataToReturn;
	}

	/**
	 * To get all content from given xml with matching tagName as Key & tag
	 * content as value
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param tagName
	 *            - Tag Name
	 * @return - LinkedHashMap<String, Object> - Object can be String /
	 *         LinkedHashMap<String, Object/String>
	 * @throws Exception
	 *             -
	 */
	static public String getDateFromXML(String xmlLocation, String tagName,
			int i) throws Exception {
		String dataToReturn = null;
		NodeList nList = getNodeListFromXML(xmlLocation, tagName);
		Node node = nList.item(i);
		NodeList nList1 = node.getChildNodes();
		for (int index = 0; index < nList1.getLength(); index++) {

			dataToReturn = getTagContentFromNode(node);
		}

		return dataToReturn;
	}

	/**
	 * To get all content from given xml with matching tagName as Key & tag
	 * content as value
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param tagName
	 *            - Tag Name
	 * @return - LinkedHashMap<String, Object> - Object can be String /
	 *         LinkedHashMap<String, Object/String>
	 * @throws Exception
	 *             -
	 */
	static public LinkedHashMap<String, String> getDataFromXMLAsString(
			String xmlLocation, String tagName, int orderLocation)
			throws Exception {
		LinkedHashMap<String, String> dataToReturn = new LinkedHashMap<String, String>();
		NodeList nList = getNodeListFromXML(xmlLocation, "order");
		Node node = nList.item(orderLocation);
		Element parent = (Element) nList.item(orderLocation);
		Node child = (Node) parent.getElementsByTagName(tagName).item(0);
		NodeList nList1 = child.getChildNodes();
		if (child.getChildNodes().getLength() > 1) {
			for (int index = 0; index < nList1.getLength(); index++) {
				if (!nList1.item(index).toString().contains("text"))
					dataToReturn = MapUtils.mergeHashMaps(
							getNodeData(nList1.item(index)), dataToReturn);
			}
		} else {
			if (!node.toString().contains("text"))
				dataToReturn.put(getTagNameFromNode(node),
						getTagContentFromNode(node));
		}

		return dataToReturn;
	}

	/**
	 * To get all content from given xml with matching tagName as Key & tag
	 * content as value
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param tagName
	 *            - Tag Name
	 * @return - LinkedHashMap<String, Object> - Object can be String /
	 *         LinkedHashMap<String, Object/String>
	 * @throws Exception
	 *             -
	 */
	static public LinkedList<LinkedHashMap<String, String>> getDataFromXMLForProductsDetails(
			String xmlLocation, String tagName, int orderLocation)
			throws Exception {
		LinkedList<LinkedHashMap<String, String>> dataToReturn = new LinkedList<LinkedHashMap<String, String>>();

		Node child = null;
		NodeList nList = getNodeListFromXML(xmlLocation, "order");
		Node node = nList.item(orderLocation);
		Element parent = (Element) nList.item(orderLocation);
		int productTotalCount = parent.getElementsByTagName(tagName)
				.getLength();
		for (int productCount = 0; productCount < productTotalCount; productCount++) {
			LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
			child = (Node) parent.getElementsByTagName(tagName).item(
					productCount);
			NodeList nList1 = child.getChildNodes();
			if (child.getChildNodes().getLength() > 1) {
				for (int index = 0; index < nList1.getLength(); index++) {
					if (!nList1.item(index).toString().contains("text")) {
						if (hashMap.containsKey(getTagNameFromNode(nList1
								.item(index)))) {
							hashMap.put(getTagNameFromNode(nList1.item(index))
									+ "-1",
									getTagContentFromNode(nList1.item(index)));
						} else {
							hashMap.put(getTagNameFromNode(nList1.item(index)),
									getTagContentFromNode(nList1.item(index)));
						}
					}
				}
			} else {
				if (!node.toString().contains("text"))
					hashMap.put(getTagNameFromNode(node),
							getTagContentFromNode(node));

			}
			dataToReturn.add(hashMap);
		}

		return dataToReturn;
	}
	
	

	static public Double getproductDetails(String xmlLocation,
			String parentNode, String childNode, int i) throws Exception {
		Double itemCount = 0.0;
		Node child = null;
		NodeList nList = getNodeListFromXML(xmlLocation, "order");
		Node node = nList.item(i);
		Element parent = (Element) nList.item(i);
		int productTotalCount = parent.getElementsByTagName(parentNode)
				.getLength();
		for (int productCount = 0; productCount < productTotalCount; productCount++) {
			child = (Node) parent.getElementsByTagName(parentNode).item(
					productCount);
			NodeList nList1 = child.getChildNodes();
			for (int index = 0; index < nList1.getLength(); index++) {
				if (!nList1.item(index).toString().contains("text")) {
					if (getTagNameFromNode(nList1.item(index)).contains(
							childNode)) {
						itemCount = itemCount
								+ Double.parseDouble(getTagContentFromNode(nList1
										.item(index)));
					}
				}
			}
		}
		return itemCount;
	}

	static public LinkedList<LinkedHashMap<String, Double>> getDataFromXMLForOrderSummary(
			String xmlLocation, String tagName, int orderLocation)
			throws Exception {
		LinkedList<LinkedHashMap<String, Double>> dataToReturn = new LinkedList<LinkedHashMap<String, Double>>();
		LinkedHashMap<String, Double> hashMap = new LinkedHashMap<String, Double>();
		NodeList nList = getNodeListFromXML(xmlLocation, "order");
		Node node = nList.item(orderLocation);
		Element parent = (Element) nList.item(orderLocation);
		Node child = (Node) parent.getElementsByTagName(tagName).item(0);
		NodeList nList1 = child.getChildNodes();
		if (child.getChildNodes().getLength() > 1) {
			for (int index = 0; index < nList1.getLength(); index++) {
				if (!nList1.item(index).toString().contains("text")) {
					if (hashMap.containsKey(getTagNameFromNode(nList1
							.item(index)))) {
						hashMap.put(getTagNameFromNode(nList1.item(index))
								+ "-1", Double
								.parseDouble(getTagContentFromNode(nList1
										.item(index))));
					} else {
						hashMap.put(getTagNameFromNode(nList1.item(index)),
								Double.parseDouble(getTagContentFromNode(nList1
										.item(index))));
					}
				}
			}
		} else {
			if (!node.toString().contains("text"))
				hashMap.put(getTagNameFromNode(node),
						Double.parseDouble(getTagContentFromNode(node)));

		}
		dataToReturn.add(hashMap);

		return dataToReturn;
	}
	

	/**
	 * To get all content from given xml with matching tagName as Key & tag
	 * content as value
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param tagName
	 *            - Tag Name
	 * @return - LinkedHashMap<String, Object> - Object can be String /
	 *         LinkedHashMap<String, Object/String>
	 * @throws Exception
	 *             -
	 */

	static public LinkedList<LinkedHashMap<String, String>> getDataFromXMLLL(
			String xmlLocation, String tagName, int orderPosition)
			throws Exception {
		LinkedList<LinkedHashMap<String, String>> dataToReturn = new LinkedList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		String[] addresslabel = null;
		String addressValue = "";
		NodeList nList = getNodeListFromXML(xmlLocation, tagName);
		NodeList validateProperNode = getNodeListFromXML(xmlLocation,
				"shipping-method");
		int checker = 0;
		boolean addressEntered = false;
		String Address = null;
		String postalCode = "";
		String PhoneNumber = "";
		try {
			Node node = nList.item(orderPosition);
			if (node.getChildNodes().getLength() > 1) {
				NodeList nList1 = node.getChildNodes();
				for (int index = 0; index < nList1.getLength(); index++) {
					if (!nList1.item(index).toString().contains("text")) {
						addresslabel = getNodeData(nList1.item(index))
								.toString().split("\\=");
						if ((addresslabel[0].trim().contains("{address1"))
								|| (addresslabel[0].trim().contains("{city"))
								|| (addresslabel[0].trim()
										.contains("{state-code"))
								|| (addresslabel[0].trim()
										.contains("{postal-code"))) {
							if (addresslabel[0].trim().contains("{postal-code")) {
								postalCode = addresslabel[1];
							} else {
								addressValue = addressValue + addresslabel[1];
							}
							checker++;

						}
						hashMap = MapUtils.mergeHashMaps(
								getNodeData(nList1.item(index)), hashMap);
						if ((checker == 4) && (addressEntered == false)) {
							Address = (addressValue + postalCode).replaceAll(
									"}", "").toLowerCase();
							hashMap.put("address", Address);
							addressEntered = true;
						}

					}
				}
			} else {
				if (!node.toString().contains("text"))
					hashMap.put(getTagNameFromNode(node),
							getTagContentFromNode(node));

			}
			dataToReturn.add(hashMap);
		} catch (Exception e) {
			Log.message("Hashmap is not properly Added");
		}
		return dataToReturn;
	}
	
	/**
	 * To get all content from given xml with matching tagName as Key & tag
	 * content as value
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param tagName
	 *            - Tag Name
	 * @return - LinkedHashMap<String, Object> - Object can be String /
	 *         LinkedHashMap<String, Object/String>
	 * @throws Exception
	 *             -
	 */
	static public LinkedList<LinkedHashMap<String, String>> getShippingDetailsFromXmlShip(
			String xmlLocation, String tagName, int orderPosition)
			throws Exception {
		LinkedList<LinkedHashMap<String, String>> dataToReturn = new LinkedList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		int orderAdd = 0;
		String[] addresslabel = null;
		String addressValue = "";
		NodeList List = getNodeListFromXML(xmlLocation, "shipments");
		Element parent = (Element) List.item(orderPosition);
		if(parent.getTextContent().contains("EGC")){
		NodeList nList = getNodeListFromXML(xmlLocation, tagName);
		NodeList validateProperNode = getNodeListFromXML(xmlLocation,
				"shipping-method");
		int checker = 0;
		boolean addressEntered = false;
		String Address = null;
		String postalCode = "";
		String PhoneNumber = "";
		try {
			Node node = parent;
			if (node.getChildNodes().getLength() > 1) {
				NodeList nList1 = node.getChildNodes();
				for (int index = 0; index < nList1.getLength(); index++) {
					if (!nList1.item(index).toString().contains("text")) {
						addresslabel = getNodeData(nList1.item(index))
								.toString().split("\\=");
						if ((addresslabel[0].trim().contains("{address1"))
								|| (addresslabel[0].trim().contains("{city"))
								|| (addresslabel[0].trim()
										.contains("{state-code"))
								|| (addresslabel[0].trim()
										.contains("{postal-code"))) {
							if (addresslabel[0].trim().contains("{postal-code")) {
								postalCode = addresslabel[1];
							} else {
								addressValue = addressValue + addresslabel[1];
							}
							checker++;
						}
						hashMap = MapUtils.mergeHashMaps(
								getNodeData(nList1.item(index)), hashMap);
						break;
					}
				}
			} else {
				if (!node.toString().contains("text"))
					hashMap.put(getTagNameFromNode(node),
							getTagContentFromNode(node));

			}
		
			dataToReturn.add(hashMap);
		} catch (Exception e) {
			Log.message("Fields are not captured properly");
		}
		} else{
			int checker = 0;
			boolean addressEntered = false;
			String Address = null;
			String postalCode = "";
			String PhoneNumber = "";
			try {
				Node node = (Node) parent.getChildNodes();
				if (node.getChildNodes().getLength() > 1) {
					NodeList nList1 = node.getChildNodes();
					for (int index = 0; index < nList1.getLength(); index++) {
						if (!nList1.item(index).toString().contains("text")) {
							addresslabel = getNodeData(nList1.item(index))
									.toString().split("\\=");
							if ((addresslabel[0].trim().contains("{address1"))
									|| (addresslabel[0].trim().contains("{city"))
									|| (addresslabel[0].trim()
											.contains("{state-code"))
									|| (addresslabel[0].trim()
											.contains("{postal-code"))) {
								if (addresslabel[0].trim().contains("{postal-code")) {
									postalCode = addresslabel[1];
								} else {
									addressValue = addressValue + addresslabel[1];
								}
								checker++;
							}
							hashMap = MapUtils.mergeHashMaps(
									getNodeData(nList1.item(index)), hashMap);
							if ((checker == 4) && (addressEntered == false)) {
								Address = (addressValue + postalCode).replaceAll(
										"}", "").toLowerCase();
								hashMap.put("address", Address);
								addressEntered = true;
							}
						}
					}
				} else {
					if (!node.toString().contains("text"))
						hashMap.put(getTagNameFromNode(node),
								getTagContentFromNode(node));

				}
			
				dataToReturn.add(hashMap);
			} catch (Exception e) {
				Log.message("Fields are not captured properly");
			}
			
		}
		return dataToReturn;
	}


	/**
	 * To get all content from given xml with matching tagName as Key & tag
	 * content as value
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param tagName
	 *            - Tag Name
	 * @return - LinkedHashMap<String, Object> - Object can be String /
	 *         LinkedHashMap<String, Object/String>
	 * @throws Exception
	 *             -
	 */
	static public LinkedList<LinkedHashMap<String, String>> getShippingDetailsFromXml(
			String xmlLocation, String tagName, int orderPosition)
			throws Exception {
		LinkedList<LinkedHashMap<String, String>> dataToReturn = new LinkedList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		int orderAdd = 0;
		String[] addresslabel = null;
		String addressValue = "";
		NodeList List = getNodeListFromXML(xmlLocation, tagName);
		Element parent = (Element) List.item(orderPosition);
		if(parent.getTextContent().contains("EGC")){
		NodeList nList = getNodeListFromXML(xmlLocation, tagName);
		NodeList validateProperNode = getNodeListFromXML(xmlLocation,
				"shipping-method");
		int checker = 0;
		boolean addressEntered = false;
		String Address = null;
		String postalCode = "";
		String PhoneNumber = "";
		try {
			Node node = parent;
			if (node.getChildNodes().getLength() > 1) {
				NodeList nList1 = node.getChildNodes();
				for (int index = 0; index < nList1.getLength(); index++) {
					if (!nList1.item(index).toString().contains("text")) {
						addresslabel = getNodeData(nList1.item(index))
								.toString().split("\\=");
						if ((addresslabel[0].trim().contains("{address1"))
								|| (addresslabel[0].trim().contains("{city"))
								|| (addresslabel[0].trim()
										.contains("{state-code"))
								|| (addresslabel[0].trim()
										.contains("{postal-code"))) {
							if (addresslabel[0].trim().contains("{postal-code")) {
								postalCode = addresslabel[1];
							} else {
								addressValue = addressValue + addresslabel[1];
							}
							checker++;
						}
						hashMap = MapUtils.mergeHashMaps(
								getNodeData(nList1.item(index)), hashMap);
						break;
					}
				}
			} else {
				if (!node.toString().contains("text"))
					hashMap.put(getTagNameFromNode(node),
							getTagContentFromNode(node));

			}
		
			dataToReturn.add(hashMap);
		} catch (Exception e) {
			Log.message("EWREWR");
		}
		} else{
			int checker = 0;
			boolean addressEntered = false;
			String Address = null;
			String postalCode = "";
			String PhoneNumber = "";
			try {
				Node node = (Node) parent.getChildNodes();
				if (node.getChildNodes().getLength() > 1) {
					NodeList nList1 = node.getChildNodes();
					for (int index = 0; index < nList1.getLength(); index++) {
						if (!nList1.item(index).toString().contains("text")) {
							addresslabel = getNodeData(nList1.item(index))
									.toString().split("\\=");
							if ((addresslabel[0].trim().contains("{address1"))
									|| (addresslabel[0].trim().contains("{city"))
									|| (addresslabel[0].trim()
											.contains("{state-code"))
									|| (addresslabel[0].trim()
											.contains("{postal-code"))) {
								if (addresslabel[0].trim().contains("{postal-code")) {
									postalCode = addresslabel[1];
								} else {
									addressValue = addressValue + addresslabel[1];
								}
								checker++;
							}
							hashMap = MapUtils.mergeHashMaps(
									getNodeData(nList1.item(index)), hashMap);
							if ((checker == 4) && (addressEntered == false)) {
								Address = (addressValue + postalCode).replaceAll(
										"}", "").toLowerCase();
								hashMap.put("address", Address);
								addressEntered = true;
							}
						}
					}
				} else {
					if (!node.toString().contains("text"))
						hashMap.put(getTagNameFromNode(node),
								getTagContentFromNode(node));

				}
			
				dataToReturn.add(hashMap);
			} catch (Exception e) {
				Log.message("EWREWR");
			}
			
		}
		return dataToReturn;
	}

	
	/**
	 * To get Tag name from Node
	 * 
	 * @param node
	 *            - Node
	 * @return - String - Tag Name
	 * @throws Exception
	 *             -
	 */
	static public String getTagNameFromNode(Node node) throws Exception {
		Element element = (Element) node;
		return element.getTagName();
	}

	/**
	 * To get content from Node
	 * 
	 * @param node
	 *            - Node
	 * @return - String - Content inside tag
	 * @throws Exception
	 *             -
	 */
	static public String getTagContentFromNode(Node node) throws Exception {
		Element element = (Element) node;
		return element.getTextContent();
	}

	static public String getTagContentFromAttr(Attr node) throws Exception {
		Element element = (Element) node;
		return element.getTextContent();
	}

	/**
	 * A recursive method to get tag content as LinkedHashMap<String, String>
	 * with all parent & child nodes
	 * 
	 * @param node
	 *            - Node
	 * @return - LinkedHashMap<String, String> of all nodes
	 * @throws Exception
	 *             -
	 */
	static public LinkedHashMap<String, String> getNodeData(Node node)
			throws Exception {
		LinkedHashMap<String, String> dataToReturn = new LinkedHashMap<String, String>();

		if (node.getChildNodes().getLength() > 1) {
			NodeList nList = node.getChildNodes();
			for (int index = 0; index < nList.getLength(); index++) {
				if (!nList.item(index).toString().contains("text"))
					dataToReturn = MapUtils.mergeHashMaps(
							getNodeData(nList.item(index)), dataToReturn);
			}
		} else {
			if (!node.toString().contains("text"))
				dataToReturn.put(getTagNameFromNode(node),
						getTagContentFromNode(node));
		}

		return dataToReturn;
	}

	/**
	 * To get tag content from childNode of 1st occurance in parentNode
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param parentNode
	 *            - Parent Tag
	 * @param childNode
	 *            - Child Tag
	 * @return - LinkedHashMap<String, String> of child tag content
	 * @throws Exception
	 *             -
	 */
	static public LinkedHashMap<String, String> getNodeData(String xmlLocation,
			String parentNode, String childNode, int i) throws Exception {
		LinkedHashMap<String, String> dataToReturn = new LinkedHashMap<String, String>();
		NodeList nList = getNodeListFromXML(xmlLocation, parentNode);
		Element parent = (Element) nList.item(i);
		String OrderNumber = parent.getAttributeNode(childNode).getNodeValue();
		Node child = (Node) parent.getElementsByTagName(childNode).item(1);
		dataToReturn.put(getTagContentFromNode(child),
				getTagContentFromNode(child));
		return dataToReturn;
	}

	/**
	 * To get tag content from childNode of 1st occurance in parentNode
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param parentNode
	 *            - Parent Tag
	 * @param childNode
	 *            - Child Tag
	 * @return - LinkedHashMap<String, String> of child tag content
	 * @throws Exception
	 *             -
	 */
	static public int getorderLocation(String xmlLocation, String parentNode,
			String childNode, String orderNumber) throws Exception {
		int dataToReturn = 0;
		int i;
		boolean flag = false;
		NodeList nList = getNodeListFromXML(xmlLocation, parentNode);
		for (i = 0; i < nList.getLength(); i++) {
			Element parent = (Element) nList.item(i);
			String OrderNumber = parent.getAttributeNode(childNode)
					.getNodeValue();
			if (OrderNumber.equals(orderNumber)) {
				dataToReturn = i;
				flag = true;
				break;
			}
		}
		if (flag == false) {
			Log.fail("Order number fetched from the storefront is not in Xml");
		}
		return dataToReturn;
	}

	/**
	 * To get tag content from childNode of 1st occurance in parentNode
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param parentNode
	 *            - Parent Tag
	 * @param childNode
	 *            - Child Tag
	 * @return - LinkedHashMap<String, String> of child tag content
	 * @throws Exception
	 *             -
	 */
	static public LinkedList<LinkedHashMap<String, String>> getNodeDataAsLL(
			String xmlLocation, String parentNode, String childNode, int i)
			throws Exception {
		LinkedList<LinkedHashMap<String, String>> dataToReturn = new LinkedList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();

		NodeList nList = getNodeListFromXML(xmlLocation, parentNode);

		Element parent = (Element) nList.item(i);
		Node child = (Node) parent.getElementsByTagName(childNode).item(0);
		hashMap.put(getTagNameFromNode(child), getTagContentFromNode(child));
		dataToReturn.add(hashMap);

		return dataToReturn;
	}

	/**
	 * To get tag content from childNode of 1st occurance in parentNode
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param parentNode
	 *            - Parent Tag
	 * @param childNode
	 *            - Child Tag
	 * @return - LinkedHashMap<String, String> of child tag content
	 * @throws Exception
	 *             -
	 */
	static public LinkedList<LinkedHashMap<String, String>> getNodeHashForGC(
			String xmlLocation, String parentNode, String childNode, int i)
			throws Exception {
		LinkedList<LinkedHashMap<String, String>> dataToReturn = new LinkedList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();

		NodeList nList = getNodeListFromXML(xmlLocation, parentNode);
		Node node = nList.item(i);
		NodeList nList1 = node.getChildNodes();
		for (int n = 0; n < nList1.getLength(); n++) {
			Element parent = (Element) nList.item(i);
			Node child = (Node) parent.getElementsByTagName(childNode).item(n);
			if (!nList1.item(n).toString().contains("text")) {
				if (hashMap.containsKey(getTagNameFromNode(child))) {
					hashMap.put(getTagNameFromNode(child) + "-" + (i + 1),
							getTagContentFromNode(child));

				} else {
					hashMap.put(getTagNameFromNode(child),
							getTagContentFromNode(child));
				}
			}

		}

		dataToReturn.add(hashMap);
		return dataToReturn;
	}

	static public LinkedList<LinkedHashMap<String, Double>> getNodeHashForGCAmount(
			String xmlLocation, String parentNode, String childNode, int i)
			throws Exception {
		LinkedList<LinkedHashMap<String, Double>> dataToReturn = new LinkedList<LinkedHashMap<String, Double>>();
		LinkedHashMap<String, Double> hashMap = new LinkedHashMap<String, Double>();
int m=1;
		NodeList nList = getNodeListFromXML(xmlLocation, parentNode);
		Node node = nList.item(i);
		NodeList nList1 = node.getChildNodes();
		for (int n = 0; n < nList1.getLength(); n++) {
			if (hashMap.size() == 3) {
				break;
			}
			if (!nList1.item(n).toString().contains("text")&&(!nList1.item(n).getTextContent().contains("PayPal"))) {
				Element parent = (Element) nList1.item(n);
				if (!parent.getTextContent().contains("cardholder_name")) {
					Node child = (Node) parent.getElementsByTagName(childNode)
							.item(0);

					if (hashMap.containsKey(getTagNameFromNode(child))) {
						hashMap.put(getTagNameFromNode(child) + m++, Double
								.parseDouble(getTagContentFromNode(child)));

					} else {
						hashMap.put(getTagNameFromNode(child), Double
								.parseDouble(getTagContentFromNode(child)));
					}
				}
			}

		}

		dataToReturn.add(hashMap);
		return dataToReturn;
	}

	static public LinkedList<LinkedHashMap<String, Double>>	getNodeHashForPaypalAmount(
			String xmlLocation, String parentNode, String childNode, int i)
			throws Exception {
		LinkedList<LinkedHashMap<String, Double>> dataToReturn = new LinkedList<LinkedHashMap<String, Double>>();
		LinkedHashMap<String, Double> hashMap = new LinkedHashMap<String, Double>();

		NodeList nList = getNodeListFromXML(xmlLocation, parentNode);
		Node node = nList.item(i);
		NodeList nList1 = node.getChildNodes();
		for (int n = 0; n < nList1.getLength(); n++) {
			if (hashMap.size() == 1) {
				break;
			}
			if (!nList1.item(n).toString().contains("text")&&nList1.item(n).getTextContent().contains("PayPal")) {
				Element parent = (Element) nList1.item(n);
				
				
					Node child = (Node) parent.getElementsByTagName(childNode)
							.item(0);
child.getChildNodes();
					if (hashMap.containsKey(getTagNameFromNode(child))) {
						hashMap.put(getTagNameFromNode(child) + "-1", Double
								.parseDouble(getTagContentFromNode(child)));

					} else {
						hashMap.put(getTagNameFromNode(child), Double
								.parseDouble(getTagContentFromNode(child)));
					}
				
			}

		}

		dataToReturn.add(hashMap);
		return dataToReturn;
	}
	static public LinkedHashMap<String, String> getDataFromXMLForCC(
			String xmlLocation, String parentNode, String childNode,
			int orderLocation) throws Exception {
		LinkedHashMap<String, String> dataToReturn = new LinkedHashMap<String, String>();
		NodeList nList = getNodeListFromXML(xmlLocation, parentNode);
		Node node = nList.item(orderLocation);
		NodeList nList1 = node.getChildNodes();
		for (int n = 0; n < nList1.getLength(); n++) {
			if (!nList1.item(n).toString().contains("text")) {
				Element parent = (Element) nList1.item(n);
				Node child = (Node) parent.getElementsByTagName(childNode)
						.item(n);

				if (dataToReturn.containsKey(getTagNameFromNode(child))) {
					dataToReturn.put(getTagNameFromNode(child) + "-1",
							getTagContentFromNode(child));

				} else {
					dataToReturn.put(getTagNameFromNode(child),
							getTagContentFromNode(child));
				}
			}

		}

		return dataToReturn;
	}

	static public List<String> getcardNumberXml(String xmlLocation,
			String parentNode, String childNode, int i) throws Exception {
		List<String> cardNumbers = new ArrayList<String>();
		NodeList nList = getNodeListFromXML(xmlLocation, parentNode);
		Node node = nList.item(i);

		NodeList nList1 = node.getChildNodes();
		for (int n = 0; n < nList1.getLength(); n++) {
			Element parent = (Element) nList.item(i);
			try {
				Node child = (Node) parent.getElementsByTagName(childNode)
						.item(n);
				if (!nList1.item(n).toString().contains("text")) {
					cardNumbers.add(getTagContentFromNode(child));
				}
			} catch (Exception e) {
				Log.failsoft("Card number is not generated/ Generated number is not in state to retrieve");
				break;
			}
		}
		return cardNumbers;
	}

	static public Double getShippingValue(String xmlLocation,
			String parentNode, String childNode, int i) throws Exception {

		Double xmlShippingValue = 0.00;
		double xmlValue = 0;
	
		/*Node child4 = null;
		NodeList nList3 = null;
		NodeList nList4 = null;
		Node child2 = null;
		String empty = null;
		boolean flag=false;
		NodeList nList = getNodeListFromXML(xmlLocation, "order");
		Node node = nList.item(i);
		Element parent = (Element) nList.item(i);
		Node childsum = (Node) parent.getElementsByTagName("shipping-lineitem")
				.item(0);
		NodeList nList2 = childsum.getChildNodes();

		for (int s = 0; s < nList2.getLength(); s++) {
			if (!nList2.item(s).toString().contains("text")) {
				if (getTagNameFromNode(nList2.item(s)).contains(
						"price-adjustments")) {
					child2 = childsum.getChildNodes().item(s);
					nList3 = child2.getChildNodes();
					flag = true;	
					 break;
					}
			}
					 }
		if(flag==true){
		
		
					for (int f = 0; f < nList3.getLength(); f++) {
						if (!nList3.item(f).toString().contains("text")) {
							if (getTagNameFromNode(nList3.item(f)).contains(
									"price-adjustment")) {

								child4 = childsum.getChildNodes().item(1);
								xmlValue = Double
										.parseDouble(getTagContentFromNode(child4));
								String xmlVal = String.valueOf(xmlValue);
								if (xmlVal.contains("-")) {
									String convert = xmlVal.replace("-", "")
											.trim();
									finalXmlValue = Double.parseDouble(convert);
								} else {
									finalXmlValue = xmlValue;
								}
								break;
							}
						}
					}
		}else{
			finalXmlValue = xmlValue;
		}*/
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        String valDiscountPrice = null;
        String valueDiscountPrice = null;
        int productCount = 0;
        String prodId = null;
        String valueXpath = null;
    	
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            
        }
        try {
            document = builder.parse(xmlLocation);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        int count  = i+1;
        valueXpath = "//order["+count+"]//shipping-lineitems//shipping-lineitem//net-price/text()";
        String shipPrice = (String) xpath.compile(valueXpath).evaluate(
 				document, XPathConstants.STRING);
        Double discountShipPrice = Double.parseDouble(shipPrice);
        valueXpath = "count(//order["+count+"]//shipping-lineitems//shipping-lineitem//price-adjustments)";
        int ShipDiscount= Integer.parseInt((String) xpath.compile(valueXpath).evaluate(
 				document, XPathConstants.STRING));
        if(ShipDiscount==1){
        	 valueXpath = "//order["+count+"]//shipping-lineitems//shipping-lineitem//price-adjustments//net-price/text()";
        	String disShipPrice = (String) xpath.compile(valueXpath).evaluate(
     				document, XPathConstants.STRING);
        	Double finalXmlValue = Double.parseDouble(disShipPrice);
        	xmlShippingValue = finalXmlValue +discountShipPrice;
        	xmlShippingValue =Double.parseDouble(new DecimalFormat("##.####").format(xmlShippingValue));
        	
        }else{
        	xmlShippingValue =discountShipPrice;
        }
		return xmlShippingValue;
	}

	static public Double getItemCount(String xmlLocation, String parentNode,
			String childNode, int i) throws Exception {
		Double itemCount = 0.0;
		Node child = null;
		NodeList nList = getNodeListFromXML(xmlLocation, "order");
		Node node = nList.item(i);
		Element parent = (Element) nList.item(i);
		int productTotalCount = parent.getElementsByTagName(parentNode)
				.getLength();
		for (int productCount = 0; productCount < productTotalCount; productCount++) {
			child = (Node) parent.getElementsByTagName(parentNode).item(
					productCount);
			NodeList nList1 = child.getChildNodes();
			for (int index = 0; index < nList1.getLength(); index++) {
				if (!nList1.item(index).toString().contains("text")) {
					if (getTagNameFromNode(nList1.item(index)).contains(
							childNode)) {
						itemCount = itemCount
								+ Double.parseDouble(getTagContentFromNode(nList1
										.item(index)));
					}
				}
			}
		}
		return itemCount;
	}

	static public LinkedList<LinkedHashMap<String, String>> shipMethod(
			String xmlLocation, String parentNode, String childNode, int i)
			throws Exception {
		LinkedList<LinkedHashMap<String, String>> shippingMethod = new LinkedList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> shipsMethod = new LinkedHashMap<String, String>();
		String shipMethod = null;
		Node child = null;
		NodeList nList = getNodeListFromXML(xmlLocation, "order");
		Node node = nList.item(i);
		Element parent = (Element) nList.item(i);
		int productTotalCount = parent.getElementsByTagName(parentNode)
				.getLength();
		for (int productCount = 0; productCount < productTotalCount; productCount++) {
			child = (Node) parent.getElementsByTagName(parentNode).item(
					productCount);
			NodeList nList1 = child.getChildNodes();
			for (int index = 0; index < nList1.getLength(); index++) {
				if (!nList1.item(index).toString().contains("text")) {
					if (getTagNameFromNode(nList1.item(index)).contains(
							childNode)
							&& getTagNameFromNode(nList1.item(index)).contains(
									"EGC")) {
						shipMethod = getTagContentFromNode(nList1.item(index));
						shipsMethod.put("shipping-method", shipMethod);
					}
				}
			}
		}
		shippingMethod.add(shipsMethod);
		return shippingMethod;
	}

	/**
	 * To get tag content from childNode of 1st occurance in parentNode
	 * 
	 * @param xmlLocation
	 *            - XML File Path
	 * @param parentNode
	 *            - Parent Tag
	 * @param childNode
	 *            - Child Tag
	 * @return - LinkedHashMap<String, String> of child tag content
	 * @throws Exception
	 *             -
	 */
	static public LinkedHashMap<String, String> getValueOfGC(
			String xmlLocation, String parentNode, String childNode, int i)
			throws Exception {
		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		NodeList nList = getNodeListFromXML(xmlLocation, parentNode);
		Node node = nList.item(i);
		NodeList nList1 = node.getChildNodes();
		for (int n = 0; n < nList1.getLength(); n++) {
			Element parent = (Element) nList.item(i);
			Node child = (Node) parent.getElementsByTagName(childNode).item(n);
			if (!nList1.item(n).toString().contains("text")) {
				if (hashMap.containsKey(getTagNameFromNode(child))) {
					hashMap.put(getTagNameFromNode(child) + "-" + (i + 1),
							getTagContentFromNode(child));

				} else {
					hashMap.put(getTagNameFromNode(child),
							getTagContentFromNode(child));
				}
			}

		}
		return hashMap;
	}

	/*
	 * Node node = nList.item(orderPosition);
	 * if(node.getChildNodes().getLength() > 1) { NodeList nList1=
	 * node.getChildNodes(); boolean flag = false; NodeList nList =
	 * getNodeListFromXML(xmlLocation, parentNode);
	 * for(i=0;i<nList.getLength();i++){ Element parent = (Element)
	 * nList.item(i); String OrderNumber =
	 * parent.getAttributeNode(childNode).getNodeValue();
	 * if(OrderNumber.equals(orderNumber)){ dataToReturn = i; flag=true; break;
	 * } }
	 */
	/**
	 * To sort LinkedList of Product
	 * 
	 * @param actualList
	 * @return
	 * @throws Exception
	 */
	public static LinkedList<LinkedHashMap<String, String>> sortLinkedListProduct(
			LinkedList<LinkedHashMap<String, String>> actualList) throws Exception {
		LinkedList<LinkedHashMap<String, String>> listToReturn = new LinkedList<LinkedHashMap<String, String>>();

		// actualList = makeUnique(actualList);

		try {
			LinkedList<String> list = new LinkedList<String>();

			LinkedList<String> listSize = new LinkedList<String>();
			LinkedList<String> listColor = new LinkedList<String>();
			int size = actualList.size();
			for (int x = 0; x < size; x++) {
				if (!actualList.get(x).get("product-id").contains("E-Gift Card")) {
					list.add(actualList.get(x).get("product-id"));
				} else {
					list.add(actualList.get(x).get("ProductName") + "_NA_NA");
				}
				// listSize.add(actualList.get(x).get("Size"));

			}
			Collections.sort(list, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return Collator.getInstance().compare(o1, o2);
				}
			});
			/*for (int i = 0; i < size; i++) {

				listSize.add(list.get(i).split("_")[1]);
				listColor.add(list.get(i).split("_")[2]);
			}*/

			for (int i = 0; i < size && listToReturn.size() < size; i++) {
				for (int j = 0; j < size && listToReturn.size() < size; j++) {
					System.out.println();
					if (list.get(i).split("_")[0].equals(actualList.get(j).get("product-id"))) {
						 {
							listToReturn.add(actualList.get(j));
							break;
						}

					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// //printing sorted list
		/*
		 * System.out.println("-----------------------------------------");
		 * for(int y = 0 ;y < size; y++) System.out.println(list.get(y));
		 * System.out.println("-----------------------------------------");
		 */
		return listToReturn;
	}

}
