package kr.co.cs.business.xtest;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataTypes;

import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.common.publicutil.FileSystemUtil;
import kr.co.cs.common.publicutil.MailUtil;

import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.persistence.dao.CommonDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.security.DomainCombiner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      //factory.setIgnoringElementContentWhitespace(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(new StringReader(respXml)));
      Element root = doc.getDocumentElement();
  	
      return root;
  }
	
	public void XtestTran(XcommonDto dto) throws Exception {

	}

}



















 
