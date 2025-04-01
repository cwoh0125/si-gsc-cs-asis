package kr.co.cs.business.xtest;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import kr.co.cs.common.publicutil.FileSystemUtil;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.persistence.dao.CommonDao;

public class XtestServiceImpl implements XtestService {

	private CommonDao commonDao = null;

	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;
	}

	public void XtestTran2(XcommonDto dto) throws Exception {

		String abc = FileSystemUtil.getByteFileContents("C:/work_project/GS/xml/sample.xml");

		Element root = makeDom(abc);

		//System.out.println("11111 ---->" + root.getElementsByTagName("IN_CARD_NO").item(0).getNodeName());
		//System.out.println("22222 ---->" + root.getElementsByTagName("IN_CARD_NO").item(0).getNodeType());
		//System.out.println("33333 ---->" + root.getElementsByTagName("IN_CARD_NO").item(0).getNodeValue());
		//System.out.println("44444 ---->" + root.getElementsByTagName("IN_CARD_NO").item(0).getTextContent());

		System.out.println("55555 1---->" + root.getElementsByTagName("OT_RESULT").item(0).getChildNodes().item(1).getNodeType());

		System.out.println("55555 2---->" + root.getElementsByTagName("data").item(1).getChildNodes().item(1).getNodeType());
		//	System.out.println("11111 ---->" + root.getElementsByTagName("data").item(1).getNodeType());
		// System.out.println("22222 ---->" + root.getElementsByTagName("data").item(1).getNodeType());

		//System.out.println("dddddddddddd ====> " + root.getElementsByTagName("CHNL_SUB_CO_CODE").item(0).getTextContent());
	}

	private Element makeDom(String respXml) throws ParserConfigurationException, SAXException, IOException {

		//XML 파싱 준비
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		
		//위에서 구성한 URL을 통해 XML 파싱 시작
		Document doc = null;
				
		//XML 파싱 준비
		factory = DocumentBuilderFactory.newInstance();

		//XXE 방지 추가
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		
		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);
		
		//factory.setIgnoringElementContentWhitespace(true);
		builder = factory.newDocumentBuilder();
		doc = builder.parse(new InputSource(new StringReader(respXml)));
		Element root = doc.getDocumentElement();

		return root;
	}

	public void XtestTran(XcommonDto dto) throws Exception {

	}

}
