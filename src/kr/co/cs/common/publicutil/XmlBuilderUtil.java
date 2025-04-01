package kr.co.cs.common.publicutil;


import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XmlBuilderUtil {

	private final static Logger errlogger = LogManager.getLogger("process.etc");
	
	private Document doc;
	private Element parent;
	private Element element;
	private Element current;
	
	public Document getDocument() {
		return doc;
	}
	
	public Element getRootElement() {
		return doc.getDocumentElement();
	}
	
	public Element getElement() {
		return element;
	}
	
	public XmlBuilderUtil(String root) throws ParserConfigurationException {

		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		doc.setXmlStandalone(true);
		doc.setXmlVersion("1.0");
		element = doc.createElement(root);
		current = element;
		doc.appendChild(element);
	}
	
	public XmlBuilderUtil() throws ParserConfigurationException {
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}
	
	
	/*
	 * 지금위치 element에 자식 element추가
	 * */
	public void addChild(String name) throws Exception {
		addChild(name, "");
	}
	public void addChild(String name, String value) throws Exception {
		Element e = doc.createElement(name);
		e.setTextContent(value);
		element.appendChild(e);
		current = e;
	}
	
	/*
	 * 노드레벨을 변경시킨다.
	 * 
	 * */
	public void setInnerElement() {
		parent = element;
		element = current;
	}

	
	/*
	 * 위치 이동
	 * deep 노드의 깊이
	 * position 한 부모 노드에서의 형제 노드간의 위치
	 * */
	public void moveLevel(int deep, int position) throws Exception {
		moveLevel(doc.getDocumentElement(), deep, position);
	}
	
	/*
	 * 특정 노드로부터의 위치이동
	 * node : 기준노드
 	 * deep 노드의 깊이
	 * position 한 부모 노드에서의 형제 노드간의 위치
	 * */
	public void moveLevel(Node node, int deep, int position) throws Exception {
		Node n = node;
		for(int i=0; i<deep; i++) {
			n = n.getFirstChild();
		}
		if(position > 0 && n.getParentNode()!=null) {
			NodeList nodelist = n.getParentNode().getChildNodes();
			n = nodelist.item(position);
		}
		element = (Element) n;
		if(n.getParentNode()!=null) {
			parent = (Element) n.getParentNode();
		} else {
			parent = element;
		}
	}
	
	/*
	 * 위치 이동
	 * expr 위치 풀경로
	 * */
	public void moveLevel(String expr) throws Exception {
		moveLevel(doc.getDocumentElement(), expr);
	}
	
	public void moveLevel(Node node, String expr) throws Exception {
		//Node n = XPathAPI.selectSingleNode(node, expr);
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node n = (Node) xpath.evaluate(expr, node, XPathConstants.NODE);
		element = (Element) n;
		if(n.getParentNode()!=null) {
			parent = (Element) n.getParentNode();
		}
	}
	
	/*
	 * 마지막 추가한 Element에 속성추가
	 * */
	public void setAttribute(String name, String value) {
		current.setAttribute(name, value);
	}
	
	/*
	 * xml문서를 문자열로 
	 * */
	public String domToString()throws Exception {
		TransformerFactory factory = TransformerFactory.newInstance();
		//sparrow XML외부개체 참조
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
		
		Transformer transformer = factory.newTransformer();
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		Source source = new DOMSource(doc);
		transformer.transform(source, result);
		writer.close();
		return writer.toString();
	}
	
	public void test() {
		try {
			XmlBuilderUtil xml = new XmlBuilderUtil("soap12:Envelope");

			xml.setAttribute("xmlns:soap12", "http://www.w3.org/2003/05/soap-envelope");
			xml.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
			xml.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

			xml.addChild("soap12:Body");
			xml.setInnerElement();
			
			xml.addChild("CallCSC_SAPCRDDET_RNI");  //service id
			xml.setAttribute("xmlns", "http://eai.gsc.com/CSC");
			xml.setInnerElement();
			
			xml.addChild("Req");
			xml.setInnerElement();
					
			xml.addChild("DIRECTORY");
			xml.addChild("FILENAME","gggg");
			xml.addChild("IP");
			
			System.out.println("1  : " + xml.getElement().getNodeName());
			xml.moveLevel(1,0);
			System.out.println("2  : " + xml.getElement().getNodeName());
			
			System.out.println("3  : " + xml.getDocument().getDocumentElement().getNodeName());			
			
			xml.moveLevel(xml.getElement(),1,0);
			System.out.println("4  : " + xml.getElement().getNodeName());
			
			xml.moveLevel(xml.getElement(),1,0);
			System.out.println("5  : " + xml.getElement().getNodeName());

			xml.addChild("dir_1");
			xml.addChild("dir_2");
			
			System.out.println(xml.domToString());
		} catch(ParserConfigurationException ce) {
			errlogger.debug("ClassCastException :: " + ce.getMessage());
		} catch (Exception e) {
			errlogger.debug("Exception :: " + e.getMessage());
		} 
	}
}

















