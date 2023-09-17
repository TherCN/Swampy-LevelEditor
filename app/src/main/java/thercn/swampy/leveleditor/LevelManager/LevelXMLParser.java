package thercn.swampy.leveleditor.LevelManager;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import thercn.swampy.leveleditor.AppUtils.AppLog;
import thercn.swampy.leveleditor.AppUtils.AppTools;

public class LevelXMLParser {
	private File xmlFile;
	private NodeList objectNodes;
	private Document document;
	/*public static void main(String[] args) {
	 LevelXMLParser a = new LevelXMLParser("/sdcard/SLE/Levels/test/test.xml");
	 a.setPropertyValue(0, "Type", "99999");
	 a.printPreview();
	 a.saveModifyToFile();
	 }*/
	public LevelXMLParser(String file) {
		xmlFile = new File(file);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(xmlFile);
			Element root = document.getDocumentElement();
			objectNodes = root.getElementsByTagName("Object");
		} catch (ParserConfigurationException | IOException | SAXException e) {
			AppLog.WriteExceptionLog(e);
		}
	}
	// 添加属性
	public boolean addProperty(int itemId, String name, String value) {
		try {
			Node propertiesNode =
				document.getElementsByTagName("Properties").item(itemId);
			Element newNode = document.createElement("Property");
			newNode.setAttribute("name", name);
			newNode.setAttribute("value", value);
			propertiesNode.appendChild(newNode);
			return true;
		} catch (TransformerFactoryConfigurationError e) {
			AppLog.WriteLog(e.getMessage());
			return false;
		} catch (DOMException e) {
			AppLog.WriteLog(e.getMessage());
			return false;
		}
	}
	// 删除属性
	public boolean removeProperty(int itemId, String propertyName) {
		try {
			Node propertiesNode =
				document.getElementsByTagName("Properties").item(itemId);
			NodeList propertyNodes = propertiesNode.getChildNodes();

			for (int i = 0; i < propertyNodes.getLength(); i++) {
				Node node = propertyNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					if (element.getAttribute("name").equals(propertyName)) {
						propertiesNode.removeChild(node);
						return true;
					}
				}
			}

			return false;
		} catch (Exception e) {
			AppLog.WriteLog(e.getMessage());
			return false;
		}
	}
	// 添加物体
	public boolean addObject(String name, String filePath) {
		try {
			// 获取Objects元素
			Node rootNode = document.getDocumentElement();
			// 创建新Object元素
			Element objectElement = document.createElement("Object");
			objectElement.setAttribute("name", name);
			rootNode.appendChild(objectElement);
			// 创建AbsoluteLocation子元素并设置值
			Element absoluteLocationElement =
				document.createElement("AbsoluteLocation");
			absoluteLocationElement.setAttribute("value", "0.0 0.0");
			objectElement.appendChild(absoluteLocationElement);
			// 创建Properties子元素（此处为空）
			Element propertiesElement = document.createElement("Properties");
			objectElement.appendChild(propertiesElement);
			addProperty(getObjectItemId(name), "Angle", "0");
			addProperty(getObjectItemId(name), "Filename", filePath);
			return true;
		} catch (Exception e) {
			AppLog.WriteExceptionLog(e);
			return false;
		}
	}
	// 添加默认属性
	public void addType(String name) {
		String[][] Object = getObjectProperties(getObjectItemId(name));
		String[][] DefaultProperties = getDefaultProperties(Object[1][2]);
		addProperty(getObjectItemId(name), DefaultProperties[0][0],
					DefaultProperties[0][1]);
	}
	// 从物体名获取索引值
	public int getObjectItemId(String name) {
		try {
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("Object");
			int index = -1; // 初始化索引值为-1
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					if (element.getAttribute("name").equals(name)) {
						index = i;
						break;
					}
				}
			}

			if (index != -1) {
				return index;
			}
		} catch (Exception e) {
			AppLog.WriteLog(e.getMessage());
		}
		return -1;
	}

	// 获取默认属性
	public String[][] getDefaultProperties(String fileName) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File("/sdcard/SLE" + fileName));
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("DefaultProperties");
			if (nodeList.getLength() == 0) {
				return null;
			}
			Element element = (Element)nodeList.item(0);
			NodeList propertyList = element.getElementsByTagName("Property");
			String[][] properties = new String[propertyList.getLength()][2];
			for (int n = 0; n < propertyList.getLength(); n++) {
				properties[n][0] = propertyList.item(n)
					.getAttributes()
					.getNamedItem("name")
					.getNodeValue();
				properties[n][1] = propertyList.item(n)
					.getAttributes()
					.getNamedItem("value")
					.getNodeValue();

			}
			return properties;
		} catch (Exception e) {
			AppLog.WriteExceptionLog(e);
			return null;
		}
	}
	// 删除物体
	public boolean removeObject(String name) {
		try {
			NodeList childNodes = document.getDocumentElement().getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE &&
					node.getNodeName().equals("Object")) {
					Element element = (Element)node;
					if (element.getAttribute("name").equals(name)) {
						document.getDocumentElement().removeChild(node);
						return true;
					}
				}
			}

			return false;
		} catch (DOMException e) {
			AppLog.WriteLog(e.getMessage());
			return false;
		}
	}
	// 获取所有物体
	public String[] getObjects() {
		String[] ObjectList = new String[objectNodes.getLength()];
		if (objectNodes.getLength() != 0) {
			for (int i = 0; i < objectNodes.getLength(); i++) {
				Node objectNode = objectNodes.item(i);
				if (objectNode.getNodeType() == Node.ELEMENT_NODE) {
					Element objectElement = (Element)objectNode;
					String objectName = objectElement.getAttribute("name");
					ObjectList[i] = objectName;
				}
			}
		}
		return ObjectList;
	}
	// 获取物体属性
	public String[][] getObjectProperties(Object item) {
		if (item instanceof String) {
			String name = String.valueOf(item);
			item = getObjectItemId(name);
		}
		Node objectName = objectNodes.item((int)item);
		String[] objectPropertyName;
		String[] objectPropertyValue;

		if (objectName.getNodeType() == Node.ELEMENT_NODE) {
			Element objectElement = (Element)objectName;
			NodeList locationNodes =
				objectElement.getElementsByTagName("AbsoluteLocation");
			objectPropertyValue = new String[locationNodes.getLength()];
			objectPropertyName = new String[locationNodes.getLength()];

			for (int j = 0; j < locationNodes.getLength(); j++) {
				Node locationNode = locationNodes.item(j);
				if (locationNode.getNodeType() == Node.ELEMENT_NODE) {
					Element locationElement = (Element)locationNode;
					String locationValue = locationElement.getAttribute("value");
					objectPropertyName[j] = "AbsoluteLocation";
					objectPropertyValue[j] = locationValue;
				}
			}

			NodeList propertyNodes = objectElement.getElementsByTagName("Property");
			objectPropertyName =
				Arrays.copyOf(objectPropertyName,
							  objectPropertyName.length + propertyNodes.getLength());
			objectPropertyValue =
				Arrays.copyOf(objectPropertyValue,
							  objectPropertyValue.length + propertyNodes.getLength());

			for (int j = 0; j < propertyNodes.getLength(); j++) {
				Node propertyNode = propertyNodes.item(j);
				if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
					Element propertyElement = (Element)propertyNode;
					String propertyName = propertyElement.getAttribute("name");
					String propertyValue = propertyElement.getAttribute("value");
					objectPropertyName[j + locationNodes.getLength()] = propertyName;
					objectPropertyValue[j + locationNodes.getLength()] = propertyValue;
				}
			}
		} else {
			objectPropertyName = null;
			objectPropertyValue = null;
		}

		String[][] Property = {objectPropertyName, objectPropertyValue};
		return Property;
	}
	// 设置属性值
	public void setPropertyValue(int item, String name, String value) {
		Element object = (Element)objectNodes.item(item);
		NodeList locationNodes =
			object.getElementsByTagName("AbsoluteLocation");

		for (int j = 0; j < locationNodes.getLength(); j++) {
			Node locationNode = locationNodes.item(j);
			if (locationNode.getNodeType() == Node.ELEMENT_NODE) {
				Element locationElement = (Element)locationNode;
				locationElement.setAttribute("value", value);
				return;
			}
		}
		Node propertiesNode = object.getElementsByTagName("Properties").item(0);
		NodeList propertyNodes = propertiesNode.getChildNodes();
		for (int i = 0; i < propertyNodes.getLength(); i++) {
			Node node = propertyNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element propertyElement = (Element)node;
				if (propertyElement.getAttribute("name").equals(name) && !propertyElement.getAttribute("name").equals("AbsoluteLocation")) {
					try {
						propertyElement.setAttribute("value", value);
						return;
					} catch (DOMException e) {
						AppLog.WriteExceptionLog(e);
					}
				}
			}
		}
	}
	
	// 打印预览
	public boolean printPreview(Context context) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult();
			Writer result_text = new StringWriter();
			result = new StreamResult(result_text);
			transformer.transform(source, result);
			String res_text = String.valueOf(result_text);
			AppTools.printText(context, res_text);
			return true;
		} catch (TransformerException e) {
			AppLog.WriteLog(e.getMessage());
			return false;
		} catch (TransformerFactoryConfigurationError e) {
			AppLog.WriteLog(e.getMessage());
			return false;
		}
	}
	// 仅限Linux系统
	public boolean fixObjects() {
		try {
			Runtime.getRuntime().exec("sed -i \"1s|<Objects>|\n<Objects>\n|g\" " + xmlFile.toString());
			return true;
		} catch (IOException e) {
			AppLog.WriteExceptionLog(e);
			return false;
		}
	}
	// 保存
	public boolean saveModifyToFile() {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);
			//fixObjects();
			return true;
		} catch (TransformerException e) {
			AppLog.WriteLog(e.getMessage());
			return false;
		} catch (TransformerFactoryConfigurationError e) {
			AppLog.WriteLog(e.getMessage());
			return false;
		}
	}
}
