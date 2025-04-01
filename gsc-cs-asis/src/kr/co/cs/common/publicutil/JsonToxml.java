package kr.co.cs.common.publicutil;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



public class JsonToxml {
	private Map<String, Object> dataSet = null;

	 
	private final static Logger logger = LogManager.getLogger("process.if");

	public String jsonToXml(String resp) throws Exception {
	 try {
	
		// StringBuffer jsonData = new StringBuffer();
	 /*
	  jsonData.append("{");
	  jsonData.append(" \"blog\": \"vip125.blog.me\",");
	  jsonData.append(" \"info\": [");
	  jsonData.append("  {");
	  jsonData.append("   \"name\": \"vip125\",");
	  jsonData.append("   \"lv\": \"관리자\",");
	  jsonData.append("   \"key\": \"test\"");
	  jsonData.append("  },");
	  jsonData.append("  {");
	  jsonData.append("   \"name\": \"vip125\",");
	  jsonData.append("   \"lv\": \"관리자\",");
	  jsonData.append("   \"key\": \"test\"");
	  jsonData.append("  }");
	  jsonData.append(" ]");
	  jsonData.append("}");
	  */
	
	  
	  String jsonData = resp;
	
			  
	  
	  
	  if (!jsonData.equals("")) {
	   dataSet = new HashMap<String, Object>();
	   JSONParser parser = new JSONParser();
	   JSONObject jsonobject = (JSONObject) parser.parse(jsonData.toString());
	  
	   for (Object key : jsonobject.keySet()) {
		    Object jsongroup = jsonobject.get(key);
		    List<Object> jsonList = new ArrayList<Object>();
		    Map<String, Object> jsonMap = new HashMap<String, Object>();
		   
		    
		    // JSONObject Type 일경우
		    if (jsongroup instanceof JSONObject) {
			    	
			    	jsonMap = subJsonObject(key, (JSONObject) jsongroup);
			    	jsonList.add(new HashMap<String, Object> (jsonMap));
			    	dataSet.put(key.toString(), new ArrayList<Object>(jsonList));
			    	
			    	jsonMap.clear();
			    	jsonList.clear();
		    	
		    } else if (jsongroup instanceof ArrayList) { // JSONArray Type 일경우
		    		dataSet.put(key.toString(), new ArrayList<Object>(subJsonArray(key, (JSONArray) jsongroup)));
		    } else {  // 일반 데이터 일경우
		    		dataSet.put(key.toString(), jsongroup.toString());
	    }
	   }
	  }
	  System.out.println("=========================initXmlSet()==================================");
	 resp = initXmlSet();
	  
	 } catch (ClassCastException ce) {
		 logger.debug("ClassCastException ::" + ce.getMessage());
	 } catch (Exception e) {
		 logger.debug("EXception ::" + e.getMessage());
	 } finally {
	 }
	return resp;
	}

	 


	// JSONObject 를 처리해 주는 함수 (재귀)

	private Map<String, Object> subJsonObject(Object key, JSONObject jsonobject) {
		
		 List<Object> jsonList = new ArrayList<Object>();
		 Map<String, Object> jsonMap = new HashMap<String, Object>();
	 
		 for (Object nodename : jsonobject.keySet()) {
			 if (jsonobject.get(nodename) instanceof ArrayList) { // JSONArray Type 일경우
				 
				 jsonList = subJsonArray(nodename, (JSONArray) jsonobject.get(nodename));
				 jsonMap.put(nodename.toString(), new ArrayList<Object> (jsonList));
				 jsonList.clear();
				 
			 } else {// 일반 데이터 일경우
				 jsonMap.put(nodename.toString(), getB(jsonobject.get(nodename)));
			 }
		 }
	 	return jsonMap;
	}

	// JSONArray 를 처리해 주는 함수 (재귀)
	private List<Object> subJsonArray(Object key, JSONArray jsonarray) {
		 List<Object> jsonList = new ArrayList<Object>();
		 Map<String, Object> jsonMap = new HashMap<String, Object>();

		 for (Object list : jsonarray) {
			  jsonMap = subJsonObject(key, (JSONObject) list);
			  jsonList.add(new HashMap<String, Object> (jsonMap));
			  jsonMap.clear();
		 }
		 
	 return jsonList;
	}

	 


	// XML을 만들어주는 함수

	private String initXmlSet() throws Exception {
	 StringBuffer xmlStream = new StringBuffer();
	 String[] setNode = { "\t<@NODE>\r\n", "\t</@NODE>\r\n" };
	 String mainNode = "\t<@NODE><![CDATA[@DATA]]></@NODE>\r\n";
	 xmlStream.append("<?xml version=\"1.0\" encoding=\"euc-kr\"?>\r\n");
	 xmlStream.append("<ns0:STP_USER_CTGRAGREE_SEL_C01_Response xmlns:ns0=\"STP_USER_CTGRAGREE_SEL_C01_Rsp\">\r\n");
	 xmlStream.append("<STP_USER_CTGRAGREE_SEL_C01_Rsp>\r\n");
	 if (dataSet != null) {
	  try {
		   for (Map.Entry<String, Object> dataMap : dataSet.entrySet()) {
			    if (dataMap.getValue() instanceof List) {
				     xmlStream.append(setNode[0].replaceAll("@NODE", getB(dataMap.getKey())));
					     for (Object dataList : (List<Object>) dataMap.getValue()) {
						      xmlStream.append("\t\t<OT_OUT_CURSOR>\r\n");
						      xmlStream.append(subXmlRows(dataList, ""));
						      xmlStream.append("\t\t</OT_OUT_CURSOR>\r\n");
					     }
			     xmlStream.append(setNode[1].replaceAll("@NODE", getB(dataMap.getKey())));
			    } else {
	
				     String dataValue = getB(dataMap.getValue()).replaceAll("&", "%26");
				     String mainNode1 = mainNode.replaceAll("@NODE", getB(dataMap.getKey()));
				     xmlStream.append(mainNode1.replaceAll("@DATA", dataValue));
			    }
		   }
	  } catch (ClassCastException ce) { //ClassCastException
		  logger.debug("=================================ce::::"+ce.toString());
	  } catch (Exception e) { //ClassCastException
		  logger.debug("=================================e::::"+e.toString());
	  } finally {
	  }
	 }
	 xmlStream.append("</STP_USER_CTGRAGREE_SEL_C01_Rsp>\r\n");
	 xmlStream.append("</ns0:STP_USER_CTGRAGREE_SEL_C01_Response>");
	 
	 System.out.println(">>>>>>>>>>>>>>>>>xml::"+xmlStream.toString());
	 //logger.debug("=================================xml::"+xmlStream.toString());
	 return xmlStream.toString();
	}

	 


	// XML sublist 를 만들어주는 함수 (재귀)
	private String subXmlRows(Object rowmap, String depth) {
	 String[] subNode = {depth + "\t\t\t<@NODE>\r\n", depth + "\t\t\t</@NODE>\r\n" };
	 String[] xmlNode = {depth + "\t\t\t<@NODE><![CDATA[@DATA]]></@NODE>\r\n", depth + "\t\t\t\t\t<@NODE><![CDATA[@DATA]]></@NODE>\r\n"};
	 StringBuffer xmlStream = new StringBuffer();
	
	 
	 try{
		 for (Map.Entry<String, Object> dataMap : ((Map<String, Object>) rowmap).entrySet()) {
			  if (dataMap.getValue() instanceof List) {
				  xmlStream.append(subNode[0].replaceAll("@NODE", getB(dataMap.getKey())));
				  for (Object dataList : (List<Object>) dataMap.getValue()) {
				    xmlStream.append(depth).append("\t\t\t\t<row>\r\n");
				    xmlStream.append(subXmlRows(dataList, depth + "\t\t"));
				    xmlStream.append(depth).append("\t\t\t\t</row>\r\n");
				  }
				  xmlStream.append(subNode[1].replaceAll("@NODE", getB(dataMap.getKey())));
			  } else {
				  String dataValue = getB(dataMap.getValue()).replaceAll("&", "%26");
		
				   String xmlNode1 = xmlNode[0].replaceAll("@NODE", getB(dataMap.getKey()));
				   xmlStream.append(xmlNode1.replaceAll("@DATA",  dataValue));
			  }
		 }
	 }catch(ClassCastException ce){
		 logger.debug("===================xml sublist ce:::"+ce.toString());
	 }catch(Exception e){
		 logger.debug("===================xml sublist e:::"+e.toString());
	 }
	 

	 return xmlStream.toString();
	}



	// null 처리해주는 함수

	private String getB(Object str) {
	 if (str == null) {
	  return "";
	 }
	 return str.toString();
	}


}
