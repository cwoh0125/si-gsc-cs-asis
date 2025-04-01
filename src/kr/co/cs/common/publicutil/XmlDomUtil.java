
package kr.co.cs.common.publicutil;

/**
 * xml dom parsing
 * author : one
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Stack;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * DOM 처리 기능 모음 Class.

 */
public final class XmlDomUtil {
    private static DocumentBuilderFactory dbf = getDOMFactory();
    private static Stack<DocumentBuilder> documentBuilders = new Stack<DocumentBuilder>();
	
    public XmlDomUtil() {}

    private static DocumentBuilderFactory getDOMFactory() {
        DocumentBuilderFactory dbf;
        try {
            dbf = DocumentBuilderFactory.newInstance();
          //XXE 방지 추가
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    		
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            dbf.setNamespaceAware(true);
        } catch (ClassCastException e) {
            dbf = null;
        } catch (Exception e) {
            dbf = null;
        }
        
        return dbf;
    }

    public static Document newDocument() throws ParserConfigurationException {
        return dbf.newDocumentBuilder().newDocument();
    }

    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
    	DocumentBuilder documentBuilder = null;
    	
    	if(documentBuilders.empty()) {
    		synchronized(dbf) {
    			documentBuilder = dbf.newDocumentBuilder();
    		}
    		
    		documentBuilders.push(documentBuilder);
    	} else {
    		documentBuilder = (DocumentBuilder) documentBuilders.pop();
    	}
    	
    	return documentBuilder;
    }

    public static void releaseDocumentBuilder(javax.xml.parsers.DocumentBuilder db) {
        synchronized (documentBuilders) {
            documentBuilders.push(db);
        }
    }
    
    public static Document makeDocument(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
    	DocumentBuilder documentBuilder = getDocumentBuilder();
    	Document document = documentBuilder.parse(inputStream);
    	releaseDocumentBuilder(documentBuilder);
    	
    	return document;
    }

    public static Document makeDocument(byte bytes[]) throws ParserConfigurationException, SAXException, IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        
        return makeDocument(byteArrayInputStream);
    }
 
    public static Document makeDocument(InputSource inputSource) throws ParserConfigurationException, SAXException, IOException {
    	DocumentBuilder documentBuilder = getDocumentBuilder();
    	Document document = documentBuilder.parse(inputSource);
    	releaseDocumentBuilder(documentBuilder);
    	
    	return document;
    }
    
    public static Document makeDocument(String string) throws ParserConfigurationException, SAXException, IOException {
    	InputSource inputSource = new InputSource(new StringReader(string));
    	
    	return makeDocument(inputSource);
    }
}
