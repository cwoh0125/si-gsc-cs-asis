package kr.co.cs.business.xinterface;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//쿠폰 암복호화 추가 2021.04.13
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.tmatesoft.svn.core.internal.io.dav.http.HTTPHeader;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.penta.scpdb.*;
import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataTypes;

import kr.co.cs.business.xcommon.XcommonService;
import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.common.publicutil.HttpsClient;
import kr.co.cs.common.publicutil.JsonToxml;
import kr.co.cs.common.publicutil.tmJsonToxml;
//import kr.co.cs.common.publicutil.XmlDomUtil;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.persistence.dao.CommonDao;


public class XinterfaceServiceImpl implements XinterfaceService {
	
	
	//쿠폰 암복호화 
    static String   PK = ""; 
    static String   IV = "";
    static String Alg = Const.Alg;
    
    
	
		
    
	private final static Logger logger = LogManager.getLogger("process.if");
	
	private CommonDao commonDao = null; 
	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;
	}
	
	private final static String DAMO_INI_PATH = Const.DAMO_URL;
  
    private Element makeDom(String respXml) throws ParserConfigurationException, SAXException, IOException {
      //Document doc = XmlDomUtil.makeDocument(respXml);
      //Element root = doc.getDocumentElement();
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      //factory.setIgnoringElementContentWhitespace(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(new StringReader(respXml)));
      Element root = doc.getDocumentElement();
      
      System.out.println("makedome:::>>"+respXml);
      return root;
  }
    
    
    
    private Map<String, Object> dataSet = null;
    
	public void CommonInterface(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		System.out.println("#################### CommonInterface start!!!!!!!!!!!!!!!!"); 
		/* DS NAME : INTERFACE_ENV */		
		/* COLUMN NAME
		 * 1: IF_KIND : 
		 * 2: REQ_SERVICE_METHOD : EX) pointInfoTmCustPointSearch.do
		 * 3: REQ_SERVICE_ID :     EX) CallCSC_SAPCRDDET_RNI
		 * 4: RES_HEADER_SECTION : EX) CallCSC_SAPCRDDET_RNIResult
		 * 5: RES_COLUMN_SECTION : EX) STP_PT_VANI_DEAL_SEL_Rsp
		 * 6: RES_RECORD_SECTION : EX) OT_RESULT 
		 * 7: RES_ROOT_SECTION   : EX) Y, N (only column만 됩니다.) 
		 * 
		 * 10: UPDATE_TIME1 : //시간이 들어가는 컬럼들정보를 업데이트 해준다.
		 * */
		
		/* DS NAME : INTERFACE_DATA */		
		/* COLUMN NAME : USER defined
		 * */
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		String smethod = dto.dsToString(ds_env.getObject(0, "REQ_SERVICE_METHOD"));
		
		/* 키관리 솔루션 damo 변경 | 2024.09.20 np847	*/
		//쿠폰에서 사용하는 암복호화 키값 운영/개발 분기 처리 
		if(ComUtil.isProd()) {// 운영
			   PK = Const.PK;  			 
			   IV = PK.substring(0,16); // 16byte			
		}else{ //개발 
			    PK = Const.PK_TEST;
			    IV = PK.substring(0,16); // 16byte			
		}

		/*d'amo ScpDbAgent Object 
		ScpDbAgent damo = new ScpDbAgent();
		String damoKey = "";
		try {
			if(ComUtil.isProd()) {// 운영
				//Key관리 String to Hex
				damoKey = damo.ScpExportKey(DAMO_INI_PATH, "ICCSCSC_ENC",  " ");
				
				//key관리 Hex to String 
				byte[] decodeHexByte = DatatypeConverter.parseHexBinary(damoKey);
				PK = new String(decodeHexByte);
				
				IV = PK.substring(0,16); // 16byte	
			}else{ //개발 
				//Key관리 String to Hex
				damoKey = damo.ScpExportKey(DAMO_INI_PATH, "ICCSCSC_DEV_ENC",  " ");
				
				//key관리 Hex to String 
				byte[] decodeHexByte = DatatypeConverter.parseHexBinary(damoKey);
				PK = new String(decodeHexByte);
				
				IV = PK.substring(0,16); // 16byte	
			}
		} catch (ScpDbAgentException se) {
			logger.debug("D'AMO Agent Exception ::" + se.getMessage());
		}
		
		logger.debug("D'Amo KEY :::: " + PK);
		logger.debug("D'Amo IV :::: " + IV);
		*/
		
		//시간 업데이트
		DataSet ds_if = dto.getDataSet("INTERFACE_DATA");
		String currentTime = ComUtil.getCurDateTime("yyyyMMddHHmmss");
		if(!"".equals(dto.dsToString(ds_env.getObject(0, "UPDATE_TIME1")))) {
			//if(ds_if.getObject(0, ds_env.getString(0, "UPDATE_TIME1"))!=null) {
			if(ds_if.getColumnDataType(ds_env.getString(0, "UPDATE_TIME1"))>0) {
				ds_if.set(0, ds_env.getString(0, "UPDATE_TIME1"), currentTime);
			}
		}

		
		//Project7 임시사용
		//사용자의 아이디를 가져와서 비교후 프로젝트팀만 다른경로의 인터페이스를 태우기위해 생성
		DataSet ds_gdsUser = dto.getDataSet("GDS_USER");
		String gblUserId = dto.dsToString(ds_gdsUser.getObject(0, "GBL_USR_ID"));
		
		//호출..		
		String kind = dto.dsToString(ds_env.getObject(0, "IF_KIND"));
		
		String[] ret = new String[3] ;
		if("WAS".equals(kind)){
			if(ComUtil.isProd())
				ret = WAS(dto, Const.WAS_END_POINT, 0, HTTP.UTF_8, "KSC5601");
			else{
				/*if("NP337".equals(gblUserId) ||
				   "NP339".equals(gblUserId) ||
				   "NP340".equals(gblUserId) ||
				   "DEV07".equals(gblUserId) ||
				  "70201".equals(gblUserId) ){
					ret = WAS(dto, Const.TEST_P7_WAS_END_POINT, 0, HTTP.UTF_8, "KSC5601");
				}else{
					ret = WAS(dto, Const.TEST_WAS_END_POINT, 0, HTTP.UTF_8, "KSC5601");
				}*/
				System.out.println("TEST SERVER::::::::"+Const.TEST_WAS_END_POINT);
				//ret = WASS(dto, Const.TEST_WAS_END_POINT, 0, HTTP.UTF_8, "KSC5601");
				
				ret = WAS(dto, Const.TEST_WAS_END_POINT, 0, HTTP.UTF_8, "KSC5601");
			}
		} else if("GSWAS".equals(kind)){
			if(ComUtil.isProd())
				ret = WAS(dto, Const.GSWAS_END_POINT, 0, HTTP.UTF_8, "KSC5601");
			else
				ret = WAS(dto, Const.TEST_GSWAS_END_POINT, 0, HTTP.UTF_8, "KSC5601");
		} else if("GSSMS".equals(kind)){
			if(ComUtil.isProd()) 
				ret = WAS(dto, Const.GSSMS_END_POINT, 0, HTTP.UTF_8, "KSC5601");
			else
				ret = WAS(dto, Const.TEST_GSSMS_END_POINT, 0, HTTP.UTF_8, "KSC5601");
		} else if("KIXX".equals(kind)){
			if(ComUtil.isProd())
				ret = WAS(dto, Const.KIXX_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			else
				ret = WAS(dto, Const.TEST_KIXX_END_POINT, 0, HTTP.UTF_8, "UTF-8");
		} else if("ERMS".equals(kind)){
			if(ComUtil.isProd())
				ret = WAS(dto, Const.ERMS_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			else
				ret = WAS(dto, Const.TEST_ERMS_END_POINT, 0, HTTP.UTF_8, "UTF-8");
		} else if("SAP".equals(kind)){
			if(ComUtil.isProd())
				ret = this.SAPString(dto, Const.SAP_END_POINT);
			else
				ret = this.SAPString(dto, Const.TEST_SAP_END_POINT);
		} else if("MPP".equals(kind)){
			if(ComUtil.isProd())
				ret = WAS(dto, Const.MPP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			else
				ret = WAS(dto, Const.TEST_MPP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
		} else if("OILCP".equals(kind)){
			if(ComUtil.isProd())
				ret = WAS(dto, Const.OILCP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			else
				ret = WAS(dto, Const.TEST_OILCP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
		} else if("MPP-SMS".equals(kind)){
			if(ComUtil.isProd())
				ret = this.MPPString(dto, Const.GSN_MPP_SMS_END_POINT3);
			else
				ret = this.MPPString(dto, Const.TEST_GSN_MPP_SMS_END_POINT3);
		} else if("AO".equals(kind)){
			if(ComUtil.isProd())
				ret = this.AOString(dto, Const.AO_END_POINT);
			else
				ret = this.AOString(dto, Const.TEST_AO_END_POINT);
		}else if("ORDCNCL".equals(kind)){
			if(ComUtil.isProd())
				ret = CANCEL_WAS(dto, Const.ORDER_CANCEL_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			else
				ret = CANCEL_WAS(dto, Const.TEST_ORDER_CANCEL_END_POINT_1, 0, HTTP.UTF_8, "UTF-8");
		}else if("SAP_46C".equals(kind)){
			if(ComUtil.isProd())
				ret = this.SAPString(dto, Const.SAP_46C_END_POINT);
			else
				ret = this.SAPString(dto, Const.TEST_SAP_46C_END_POINT);
		}else if("GSCP".equals(kind)){
			if(ComUtil.isProd())
				ret = WAS(dto, Const.GSCP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			else
				ret = WAS(dto, Const.TEST_GSCP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
		} else if("GSC-APP".equals(kind)){ 
			if(ComUtil.isProd()){
				ret = WASGET(dto, Const.GSCAPP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}else{				
				ret = WASGET(dto, Const.TEST_GSCAPP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}
		}else if("GSCP-APP".equals(kind) || ("GSCPDTL-APP").equals(kind)){			
				if(ComUtil.isProd()) {
					ret = WASA(dto, Const.GSCP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
				}else {
					ret = WASA(dto, Const.TEST_GSCP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}
		}else if("WAS-APP".equals(kind)){
			if(ComUtil.isProd()) {
				ret = WAS(dto, Const.WASAPP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}else {
				ret = WAS(dto, Const.WASAPPTEST_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}
		}else if("GSCPAPPDTL-APP".equals(kind)) {
			if(ComUtil.isProd()) {
				ret = WASA(dto, Const.GSCP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}else {
				ret = WASA(dto, Const.TEST_GSCP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}
		}else if("WAS-EVAPP".equals(kind)) {
			if(ComUtil.isProd()) {
				ret = WAS(dto, Const.WAS_EVAPP_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}else {
				ret = WAS(dto, Const.WAS_EVAPPTEST_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}
		}else if("WAS-TM".equals(kind)) {
			if(ComUtil.isProd()) {
				ret = TM_Rest(dto, Const.WAS_TM_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}else {
				ret = TM_Rest(dto, Const.WAS_TMTEST_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}
		}else if("WAS-TMPAYMENT".equals(kind)) {
			if(ComUtil.isProd()) {
				ret = TM_Rest(dto, Const.WAS_TM_PAYMENT_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}else {
				ret = TM_Rest(dto, Const.WAS_TMTEST_PAYMENT_END_POINT, 0, HTTP.UTF_8, "UTF-8");
			}
		}else if("GSC-VOC".equals(kind)) {
			ret = VOC_Rest(dto, Const.AWS_GSC_VOC_END_POINT, 0, HTTP.UTF_8, "UTF-8"); 
		}
			
		
		if("200".equals(ret[0])) {			
			setOutputDataset(dto, ret);
		}else{
			setErrorData(dto, ret);
		}
		 
		//디비작업(저장 조회) 
		//xcommonservice.XcommonUserTransaction(dto); 
	}
	
	//여러개의 레코드 인터페이스(was 만, 결과가 멀티로우[조회] 일때는 쓰지마라..)
	//아웃풋 데이터셋은 SUCESS_RESULT, FAIL_RESULT 에 내려보넴
	public void MultiRowTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		/* DS NAME : INTERFACE_ENV */		
		/* COLUMN NAME
		 * 1: IF_KIND : WAS, EAI, SAP
		 * 2: REQ_SERVICE_METHOD : EX) /gswas/was/pointInfoTmCustPointSearch.do 
		 * 3: REQ_SERVICE_ID :     EX) CallCSC_SAPCRDDET_RNI
		 * 4: RES_DATA_SECTION   : EX) 컬럼섹션...
		 * */
		
		/* DS NAME : INTERFACE_DATA */		
		/* COLUMN NAME : A1, A2, A3, A4, .......
		 * */
 
		/* OUTPUT DS NAME : INTERFACE_RESULT 결과를 업데이트 할 정보...*/
		/* COLUMN NAME : A1, A2, A3, A4, RET_CD, RET_MSG, .......
		 * */
		
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
		
		String smethod = dto.dsToString(ds_env.getObject(0, "REQ_SERVICE_METHOD"));
		
		String WAS_END_POINT;
		String MPP_END_POINT;
		
		if(ComUtil.isProd()) {
			WAS_END_POINT = Const.WAS_END_POINT;
			MPP_END_POINT = Const.MPP_END_POINT;
		} else {
			WAS_END_POINT = Const.TEST_WAS_END_POINT;
			MPP_END_POINT = Const.TEST_MPP_END_POINT;
		}
		
		String kind = dto.dsToString(ds_env.getObject(0, "IF_KIND"));
		
		if("WAS".equals(kind) || "MPP".equals(kind)){
			String[] ret;
			DataSet ds_out;
			List<HashMap<String, Object>> slist = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> smap = new HashMap<String, Object>();
			List<HashMap<String, Object>> flist = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> fmap = new HashMap<String, Object>();
			
			/* TM I/F 배포 후 Sparrow 배포 시 반영 필요*/
			if(ds_ifdata != null) {
				for(int i=0; i<ds_ifdata.getRowCount(); i++) {
					
					if("WAS".equals(kind)){
						ret = WAS(dto, WAS_END_POINT, i, HTTP.UTF_8, "KSC5601");
						
					} else if("MPP".equals(kind)){
						ret = WAS(dto, MPP_END_POINT, i, HTTP.UTF_8, "UTF-8");
						
					} else { 
						return;
					}
					
					if("200".equals(ret[0])) {
						ds_out = getTranResult(dto, ret[2]);
						smap = dto.getDefaultRowMap(ds_ifdata, i);
						smap.putAll(dto.getDefaultRowMap(ds_out, 0));
						smap.put("HTTP_RET_CODE", "0");
						smap.put("HTTP_RET_MSG", "통신성공 ");				
						slist.add(i, smap);
					} else {				
						fmap = dto.getDefaultRowMap(ds_ifdata, i);
						fmap.put("HTTP_RET_CODE", "-1");
						fmap.put("HTTP_RET_MSG", "요청하신 거래는 다음에러로 인해 통신실패를 하였습니다.\n보다 정확한 원인을 알고 싶으시면 아래메세지를 관리자에게 보여주세요 \n\n [" + ret[0] +" : "+ ret[1] +"]");				
						flist.add(i, fmap);
					}
				}
			}
			
			/* sparrow 변경으로 인한 주석처리
			for(int i=0; i<ds_ifdata.getRowCount(); i++) {
				
				if("WAS".equals(kind)){
					ret = WAS(dto, WAS_END_POINT, i, HTTP.UTF_8, "KSC5601");
					
				} else if("MPP".equals(kind)){
					ret = WAS(dto, MPP_END_POINT, i, HTTP.UTF_8, "UTF-8");
					
				} else { 
					return;
				}
				
				if("200".equals(ret[0])) {
					ds_out = getTranResult(dto, ret[2]);
					smap = dto.getDefaultRowMap(ds_ifdata, i);
					smap.putAll(dto.getDefaultRowMap(ds_out, 0));
					smap.put("HTTP_RET_CODE", "0");
					smap.put("HTTP_RET_MSG", "통신성공 ");				
					slist.add(i, smap);
				} else {				
					fmap = dto.getDefaultRowMap(ds_ifdata, i);
					fmap.put("HTTP_RET_CODE", "-1");
					fmap.put("HTTP_RET_MSG", "요청하신 거래는 다음에러로 인해 통신실패를 하였습니다.\n보다 정확한 원인을 알고 싶으시면 아래메세지를 관리자에게 보여주세요 \n\n [" + ret[0] +" : "+ ret[1] +"]");				
					flist.add(i, fmap);
				}
			}
			*/
			
			dto.getOutdslist().add(dto.setDataSet(slist, "SUCESS_RESULT"));
			dto.getOutdslist().add(dto.setDataSet(flist, "FAIL_RESULT"));
			
			if(!ComUtil.isProd()) {
				DataSet retxml = new DataSet("DS_RETXML");
				retxml.addColumn("SEND_URL", DataTypes.STRING, 2000);
				retxml.addColumn("SEND_DATA", DataTypes.STRING, 2000);
				retxml.addColumn("RET_DATA", DataTypes.STRING, 2000);
				retxml.newRow(0);
				retxml.set(0, "SEND_URL", dto.getUserTempMap().get("sendUrl"));
				retxml.set(0, "SEND_DATA", dto.getUserTempMap().get("sendData"));
				retxml.set(0, "RET_DATA", dto.getUserTempMap().get("resultData"));
				dto.getOutdslist().add(retxml);
			}
		}
		 
		//디비작업(저장 조회)
		//xcommonservice.XcommonUserTransaction(dto);
	}
 
	private String[] WAS(XcommonDto dto, String endpoint, int dRow, String inCharSet, String outCharSet) throws Exception {
		
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
		System.out.println("endpoint!>>>"+endpoint);
		
		String sendURL = endpoint + ds_env.getString(0, "REQ_SERVICE_METHOD");
		
		
		
		
		String kind = dto.dsToString(ds_env.getObject(0, "IF_KIND"));
		
		System.out.println("kind::"+kind);
	   System.out.println("URL::"+">>>>>>>>>>>>>>>>>>>>>>>>>>"+ sendURL);   	    
		
		List<BasicNameValuePair> sdata = new ArrayList<BasicNameValuePair>();				
		
		for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
			sdata.add(new BasicNameValuePair(ds_ifdata.getColumn(i).getName(), ds_ifdata.getString(dRow, i)==null?"":ds_ifdata.getString(dRow, i)));		
		}
		
		String[] ret = httpCall(sendURL, sdata, inCharSet, outCharSet);
	
		
		//운영이 아니면
		if(!ComUtil.isProd()) {
			dto.getUserTempMap().put("sendUrl", sendURL);
			dto.getUserTempMap().put("sendData", sdata.toString());
			dto.getUserTempMap().put("resultData", ret[2]);
		}
		
		return ret;
	}
	
//쿠폰 모바일 암호화
private String[] WASA(XcommonDto dto, String endpoint, int dRow, String inCharSet, String outCharSet) throws Exception {
	
	DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
	DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
	
	String sendURL = endpoint + ds_env.getString(0, "REQ_SERVICE_METHOD");
	
    //System.out.println("GSCP-APP     sendURL"+">>>>>>>>>>>>>>>>>>>>>>>>>>"+ sendURL);	

    //URL param 값 셋팅
	String sdata = "";
	
	//System.out.println("count"+ds_ifdata.getColumnCount());
		
	for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
		
		if(i == ds_ifdata.getColumnCount()-1){
		
			sdata = sdata+ds_ifdata.getColumn(i).getName() + "="+ds_ifdata.getString(dRow, i);			
		}else{ 
			sdata = sdata+ds_ifdata.getColumn(i).getName() + "="+ds_ifdata.getString(dRow, i)+"&";				
		}			
	}	
	//암호화
	sdata = encrypt(sdata);
	
	//System.out.println("encrypt>>>>>>>>>>>>>>>>>>>>>>"+sdata.toString());
	
	String[] ret = httpCallgetA(sendURL, sdata, inCharSet, outCharSet);
	//운영이 아니면
	if(!ComUtil.isProd()) {
		dto.getUserTempMap().put("sendUrl", sendURL);
		dto.getUserTempMap().put("sendData", sdata.toString());
		dto.getUserTempMap().put("resultData", ret[2]);
	}
	
	return ret;
}


	
	
	private String[] WASS(XcommonDto dto, String endpoint, int dRow, String inCharSet, String outCharSet) throws Exception {
		
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
		
		String sendURL = endpoint + ds_env.getString(0, "REQ_SERVICE_METHOD");
		
	   // System.out.println("endPoint11"+">>>>>>>>>>>>>>>>>>>>>>>>>>"+ endpoint);
		
		List<BasicNameValuePair> sdata = new ArrayList<BasicNameValuePair>();
		for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
			sdata.add(new BasicNameValuePair(ds_ifdata.getColumn(i).getName(), ds_ifdata.getString(dRow, i)==null?"":ds_ifdata.getString(dRow, i)));
		}
		
		String[] ret = httpsCall(sendURL, sdata, inCharSet, outCharSet);
		//운영이 아니면
		if(!ComUtil.isProd()) {
			dto.getUserTempMap().put("sendUrl", sendURL);
			dto.getUserTempMap().put("sendData", sdata.toString());
			dto.getUserTempMap().put("resultData", ret[2]);
		}
		
		return ret;
	}
	

	private String[] WASGET(XcommonDto dto, String endpoint, int dRow, String inCharSet, String outCharSet) throws Exception {
		
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
		
		String sendURL = endpoint + ds_env.getString(0, "REQ_SERVICE_METHOD");
		
	    //System.out.println("sendURL"+">>>>>>>>>>>>>>>>>>>>>>>>>>"+ sendURL);
		
		//List<BasicNameValuePair> sdata = new ArrayList<BasicNameValuePair>();
		/*
		 for(int i=0; i<ds_ifdata.getColumnCount(); i++) {		 
			sdata.add(new BasicNameValuePair(ds_ifdata.getColumn(i).getName(), ds_ifdata.getString(dRow, i)==null?"":ds_ifdata.getString(dRow, i)));
			System.out.println("sdata setting>>>>>>>>["+i+"]"+sdata.toString());
		}
		*/
	    //URL param 값 셋팅
		String sdata = "";
		//System.out.println("count"+ds_ifdata.getColumnCount());
		
		
		
		for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
			
			if(i == ds_ifdata.getColumnCount()-1){
				//System.out.println(i+"==============="+ds_ifdata.getColumnCount());
				sdata = sdata+ds_ifdata.getColumn(i).getName() + "="+ds_ifdata.getString(dRow, i);			
			}else{ 
				sdata = sdata+ds_ifdata.getColumn(i).getName() + "="+ds_ifdata.getString(dRow, i)+"&";				
			}			
		}
		
		//System.out.println("sdataa>>>>>>>>>>>>>>>>>>>>>>"+sdata.toString());
		
		String[] ret = httpCallget(sendURL, sdata, inCharSet, outCharSet);
		//운영이 아니면
		if(!ComUtil.isProd()) {
			dto.getUserTempMap().put("sendUrl", sendURL);
			dto.getUserTempMap().put("sendData", sdata.toString());
			dto.getUserTempMap().put("resultData", ret[2]);
		}
		
		return ret;
	}
	
//	private String[] SAPDOM(XcommonDto dto, String endpoint) throws Exception {
//		
//		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
//		DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
//		
//		XmlBuilderUtil xml = new XmlBuilderUtil("soap12:Envelope");
//		xml.setAttribute("xmlns:soap12", "http://www.w3.org/2003/05/soap-envelope");
//		xml.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
//		xml.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//		xml.addChild("soap12:Body");
//		xml.setInnerElement();
//		xml.addChild(dto.dsToString(ds_env.getObject(0, "REQ_SERVICE_ID"))); //service ID
//		xml.setAttribute("xmlns", "http://eai.gsc.com/CSC");
//		xml.setInnerElement();
//		xml.addChild("Req");
//		xml.setInnerElement();		
//		
//		//data 시작
//		for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
//			xml.addChild(ds_ifdata.getColumn(i).getName(), dto.dsToString(ds_ifdata.getObject(0, i)));
//		}
//		String sendData = xml.domToString();
//		
//       	String[] ret = soapCall(sendData, endpoint, "");
//
//       	dto.getUserTempMap().put("sendUrl", endpoint);
//		dto.getUserTempMap().put("sendData", sendData);
//       	dto.getUserTempMap().put("resultData", ret[2]);
//       	
//       	return ret;
//	}
	
	
	private String[] SAPString(XcommonDto dto, String endpoint) throws Exception {
		
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
		
		StringBuffer xml = new StringBuffer(100);
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		xml.append("<soap12:Envelope");
		xml.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		xml.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
		xml.append(" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n");
		xml.append("<soap12:Body>\n");
		xml.append("<"); xml.append(dto.dsToString(ds_env.getObject(0, "REQ_SERVICE_ID"))); //service ID
		xml.append(" xmlns=\"http://eai.gsc.com/CSC\">\n");
		xml.append("<Req>\n");
		
		//data 시작
		for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
			xml.append("<"); xml.append(ds_ifdata.getColumn(i).getName()); xml.append(">"); 
			xml.append(dto.dsToString(ds_ifdata.getObject(0, i)));
			xml.append("</"); xml.append(ds_ifdata.getColumn(i).getName()); xml.append(">\n"); 
		}
				
		xml.append("</Req>\n");
		xml.append("</"); xml.append(dto.dsToString(ds_env.getObject(0, "REQ_SERVICE_ID"))); xml.append(">\n"); //service ID
	    xml.append("</soap12:Body>\n");
	    xml.append("</soap12:Envelope>\n");
		
		String sendData = xml.toString();	
		
       	String[] ret = soapCall(sendData, endpoint, "");

       	if(!ComUtil.isProd()) {
	       	dto.getUserTempMap().put("sendUrl", endpoint);
			dto.getUserTempMap().put("sendData", sendData);
	       	dto.getUserTempMap().put("resultData", ret[2]);
       	}
       	
       	return ret;
	}
	
    private DataSet xmlToDataSet(Element element) {    	
        return xmlToDataSet(element, "temp", "");
    }	
	
    private DataSet xmlToDataSet(Element element, String dsName, String excludePart) {
    	
    	DataSet dataset = new DataSet(dsName);
    	StringBuffer sb = new StringBuffer(100);    	
    	
    	
        if(element != null) {
        	
        	String ext = excludePart==null?"":excludePart;
        	
        	for(Node column = element.getFirstChild(); column != null; column = column.getNextSibling()) {
	            switch(column.getNodeType()) {
		            case Node.ELEMENT_NODE:
		            	if(!column.getNodeName().toUpperCase().equals(ext)) 
		            	{
		            		//2014.07.10 jh
		            		if(dataset.getColumn(column.getNodeName().toUpperCase())== null)
		            		{
		            			dataset.addColumn(column.getNodeName().toUpperCase(), DataTypes.STRING, 2000);
		            		}
		            	}
		                break;
		            default :
		            	break;
	            }        		
        	}           

			dataset.newRow();
        	for(Node column = element.getFirstChild(); column != null; column = column.getNextSibling()) {
	            switch(column.getNodeType()) {
		            case Node.ELEMENT_NODE:
		            	if(!column.getNodeName().toUpperCase().equals(ext)) {		            
		            			dataset.set(0, column.getNodeName().toUpperCase(), column.getTextContent());		            	
		            			sb.append(column.getTextContent());
		            	}
		                break;
		            default :
		            	break;
	            }        		
        	}
        }
        if(sb.length()==0) 
        	dataset.clearData();
        
        return dataset;
    }
    
private DataSet GscpDtlLxmlToDataSet(Element element, String dsName, String excludePart) {
    	
    	DataSet dataset = new DataSet(dsName);
    	StringBuffer sb = new StringBuffer(100);    	
    	
    	
        if(element != null) {
        	
        	String ext = excludePart==null?"":excludePart;
        	
        	for(Node column = element.getFirstChild(); column != null; column = column.getNextSibling()) {
	            switch(column.getNodeType()) {
		            case Node.ELEMENT_NODE:
		            	if(!column.getNodeName().toUpperCase().equals(ext)) 
		            	{
		            		//2014.07.10 jh
		            		if(dataset.getColumn(column.getNodeName().toUpperCase())== null)
		            		{
		            			dataset.addColumn(column.getNodeName().toUpperCase(), DataTypes.STRING, 2000);
		            		}
		            	}
		                break;
		            default :
		            	break;
	            }        		
        	}           

			dataset.newRow();
        	for(Node column = element.getFirstChild(); column != null; column = column.getNextSibling()) {
	            switch(column.getNodeType()) {
		            case Node.ELEMENT_NODE:
		            	if(!column.getNodeName().toUpperCase().equals(ext)) {
		            		if(column.getNodeName().toUpperCase().equals("ENCOUT")) {
		            			if(column.getTextContent() != null) {
			            			try {
										dataset.set(0, column.getNodeName().toUpperCase(),decrypt( column.getTextContent()));
									} catch (DOMException e) {
										logger.debug("GscpDtlLxmlToDataSet DOMException :: " + e.getMessage());
									} catch (Exception e) {
										logger.debug("GscpDtlLxmlToDataSet Exception :: " + e.getMessage());
									}
		            			}
		            		}else {
		            			dataset.set(0, column.getNodeName().toUpperCase(), column.getTextContent());
		            		}
		            		sb.append(column.getTextContent());
		            	}
		                break;
		            default :
		            	break;
	            }        		
        	}
        }
        if(sb.length()==0) 
        	dataset.clearData();
        
        return dataset;
    }   
    
  //사용하는 함수 쿠폰상세
/*
private DataSet GscpDtlLxmlToDataSet(NodeList nlist, String dsName, String excludePart) throws DOMException, Exception {    	
	
	System.out.println("============GSCPDTLxmlToDataSet========="+excludePart.toString());
	DataSet dataset = new DataSet(dsName);		
	StringBuffer sb = new StringBuffer(100);		

	
    Element item;
    
    if(nlist != null && nlist.getLength() > 0) {

    	item = (Element)nlist.item(0);
    	
    	//컬럼만들기
    	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {
            switch(column.getNodeType()) {
            //case Node.TEXT_NODE:
            //case Node.CDATA_SECTION_NODE:
            //    break;	                
            case Node.ELEMENT_NODE:            	
        		dataset.addColumn(column.getNodeName().toUpperCase(), DataTypes.STRING, 2000);
                break;
            default :
            	break;
            }
        }    	
        for(int row=0; row<nlist.getLength(); row++) {
        	item = (Element)nlist.item(row);        	
        	
        	dataset.newRow();
        	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {        		
        		
	            switch(column.getNodeType()) {	            
	            case Node.ELEMENT_NODE:
	            		dataset.set(row, column.getNodeName().toUpperCase(),column.getTextContent());
	                break;
	            default :
	            	break;
	            }
	        }
        }        
        //여기서 encOut복호화 처리 해줌.
        System.out.println("================decrypt encOut start====================");
        for(int row=0; row<nlist.getLength(); row++) {
        	item = (Element)nlist.item(row);
        	Node rootNode = item.getElementsByTagName("encOut").item(row);
    		dataset.set(row,rootNode.getNodeName().toUpperCase(),decrypt(rootNode.getTextContent()));
        }
        //data 있는지 첫번째 로우만  체크..
        for(int row=0; row<1; row++) {
        	item = (Element)nlist.item(row);        	
        	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {
	            switch(column.getNodeType()) {
	            case Node.ELEMENT_NODE:
	            	//System.out.println(">>>column.getTextContent()"+column.getTextContent());
        			sb.append(column.getTextContent());
	                break;
	            default :
	            	break;
	            }
	        }
        }
    }
    if(sb.length()==0) 
    	System.out.println(">>>>>sb.length():"+sb.length());
    	dataset.clearData();
    
    return dataset;
}
*/
	private DataSet xmlRecordToDataSet(NodeList nlist, String dsName) {
		
		System.out.println("===========================xmlRecordToDataSet====================");

		DataSet dataset = new DataSet(dsName);		
		StringBuffer sb = new StringBuffer(100); 
		
		
		//System.out.println("nlist:"+nlist.toString());
		//System.out.println("dsName:"+dsName.toString());
		
        Element item;
	    
	    if(nlist != null && nlist.getLength() > 0) {
	
        	item = (Element)nlist.item(0);
        	
        	//컬럼만들기
        	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {
	            switch(column.getNodeType()) {
	            //case Node.TEXT_NODE:
	            //case Node.CDATA_SECTION_NODE:
	            //    break;	                
	            case Node.ELEMENT_NODE:
            		dataset.addColumn(column.getNodeName().toUpperCase(), DataTypes.STRING, 2000);
	                break;
	            default :
	            	break;
	            }
	        }

	        for(int row=0; row<nlist.getLength(); row++) {
	        	item = (Element)nlist.item(row);
	        	
	        	dataset.newRow();
	        	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {
		            switch(column.getNodeType()) {
		            case Node.ELEMENT_NODE:		            	
	            		dataset.set(row, column.getNodeName().toUpperCase(), column.getTextContent());	            		
		                break;
		            default :
		            	break;
		            }
		        }
	        }

	        //data 있는지 첫번째 로우만  체크..
	        for(int row=0; row<1; row++) {
	        	item = (Element)nlist.item(row);
	        	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {
		            switch(column.getNodeType()) {
		            case Node.ELEMENT_NODE:
            			sb.append(column.getTextContent());
		                break;
		            default :
		            	break;
		            }
		        }
	        }
	    }
        if(sb.length()==0) 
        	dataset.clearData();
        
        return dataset;
	}
	
	private DataSet GscpxmlRecordToDataSet(NodeList nlist, String dsName) throws DOMException, Exception {
		System.out.println("======================GSCP-APP=====================");
		DataSet dataset = new DataSet(dsName);		
		StringBuffer sb = new StringBuffer(100);	
		
        Element item;
	    
	    if(nlist != null && nlist.getLength() > 0) {
	
        	item = (Element)nlist.item(0);
        	
        	//컬럼만들기
        	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {
	            switch(column.getNodeType()) {
	            //case Node.TEXT_NODE:
	            //case Node.CDATA_SECTION_NODE:
	            //    break;	                
	            case Node.ELEMENT_NODE:	            	
            		dataset.addColumn(column.getNodeName().toUpperCase(), DataTypes.STRING, 2000);            	
	                break;
	            default :
	            	break;
	            }
	        }

	        for(int row=0; row<nlist.getLength(); row++) {
	        	item = (Element)nlist.item(row);
	        	
	        	dataset.newRow();
	        	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {
		            switch(column.getNodeType()) {
		            case Node.ELEMENT_NODE:
	            		try {	            		
							dataset.set(row, column.getNodeName().toUpperCase(), decrypt(column.getTextContent()));
						} catch (Exception e) {
							logger.debug("GscpxmlRecordToDataSet Exception ::" + e.getMessage());
						}
	            		
		                break;
		            default :
		            	break;
		            }
		        }
	        }

	        //data 있는지 첫번째 로우만  체크..
	        for(int row=0; row<1; row++) {
	        	item = (Element)nlist.item(row);
	        	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {
		            switch(column.getNodeType()) {
		            case Node.ELEMENT_NODE:
            			sb.append(column.getTextContent());            	
		                break;
		            default :
		            	break;
		            }
		        }
	        }
	    }
	    
	    
	    
        if(sb.length()==0) 
        	dataset.clearData();
        
        return dataset;
	}
	
	/*
	 * 일반 http post전송
	 * */
	private String[] httpCall(String sendurl, List<BasicNameValuePair> sdata, String inCharSet, String outCharSet) throws IOException {
 
		String startTime = ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss");

		System.out.println("================sendurl::::"+sendurl);
			
		HttpClient httpclient = new DefaultHttpClient();
        UrlEncodedFormEntity reqEntity = null;
        HttpEntity   resEntity = null;
        String[] ret = new String[3];
        
        InputStream iis = null;
        ByteArrayOutputStream bos = null;
        
        try {
        	HttpParams params = httpclient.getParams();
        	HttpConnectionParams.setConnectionTimeout(params, 30000);
        	HttpConnectionParams.setSoTimeout(params, 30000);
        	
            HttpPost httppost = new HttpPost(sendurl);
            
            reqEntity = new UrlEncodedFormEntity(sdata, inCharSet);            
            
            httppost.setEntity(reqEntity);
        	//httppost.setHeader("HTTP1.1", "");    
            HttpResponse response = httpclient.execute(httppost);   
          
            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
            ret[1] = response.getStatusLine().getReasonPhrase();            
            
            if("200".equals(ret[0])) {
                int totlen = 0;
                int len = 0;
	            resEntity = response.getEntity();
//	            ret[2] = EntityUtils.toString(resEntity); ==> 이게 되면 좋은데... 이상하게 exception 떨어진다.. 짜증....
	            iis = resEntity.getContent();
	            bos = new ByteArrayOutputStream();
	            byte[] packet = new byte[2048];
	            
	            if(iis!=null) {
		            while (true) {	            
		            	len = iis.read(packet, 0, packet.length);
		                if(len > 0) {
		                	totlen = totlen + len;
		                	bos.write(packet,0,len);
		                }
		                if(len <= 0) {
		                	break;
		                }
		            }
		            logger.debug("[outCharSet]["+outCharSet+"]");		            
		            ret[2] = new String(bos.toByteArray(), outCharSet);
		            logger.debug("[outCharSet_1]["+outCharSet+"]");
	            } else {
	                ret[0] = "201";
	                ret[1] = "응답받은 데이터가 없습니다.";
	            	ret[2] = "No return data";
	            }

            } else {
	            ret[2] = "No return data";
            }
            logger.debug("[resEntity]["+resEntity+"]");
            logger.debug("[httpcall]["+ret[2]+"]");            
        } catch (Exception e) {
        	logger.debug("httpCall Exception ::" + e.getMessage());
        }
        finally {
        	
        	try {if(iis!=null) iis.close();}catch(Exception e){logger.debug("httpCall iisException ::" + e.getMessage());}
        	try {if(bos!=null) bos.close();}catch(Exception e){logger.debug("httpCall bosException ::" + e.getMessage());}
            httpclient.getConnectionManager().shutdown();
            try {
            	StringBuffer sb = new StringBuffer(100);
	            sb.append("[URL][");
	            sb.append(sendurl);
	            sb.append("][SENDTIME][");
	            sb.append(startTime);
	            sb.append("][RECVTIME][");
	            sb.append(ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss"));
	            sb.append("][SENDDATA][");
	            sb.append(sdata.toString());
	            sb.append("][RECVDATA][");
	            sb.append(ret[0]);
	            sb.append(" : ");
	            sb.append(ret[2]);
	            sb.append("]");
	    		logger.info(sb.toString());
	    		System.out.println("======>"+sb.toString());
            } catch(Exception le) {logger.debug("XinterfaceServiceImpl httpCall Exception ::" + le.getMessage());}

        }
        return ret;
    }
	
	
	/*
	 * 일반 http post전송
	 * */
	private String[] httpsCall(String sendurl, List<BasicNameValuePair> sdata, String inCharSet, String outCharSet) throws IOException {
 
		String startTime = ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss");

		System.out.println("NONONONO================https::::"+sendurl);
			
		
		HttpsClient httpsclient = new HttpsClient();				
		HttpClient httpclient = httpsclient.getNewHttpClient();
		
		
		
        UrlEncodedFormEntity reqEntity = null;
        HttpEntity   resEntity = null;
        String[] ret = new String[3];
        
        InputStream iis = null;
        ByteArrayOutputStream bos = null;
        
        try {
        	HttpParams params = httpclient.getParams();
        	HttpConnectionParams.setConnectionTimeout(params, 30000);
        	HttpConnectionParams.setSoTimeout(params, 30000);
        	
            HttpPost httppost = new HttpPost(sendurl);
            
            reqEntity = new UrlEncodedFormEntity(sdata, inCharSet);
            httppost.setEntity(reqEntity);
        	//httppost.setHeader("HTTP1.1", "");    
            HttpResponse response = httpclient.execute(httppost);   
            System.out.println("sendurl::"+sendurl+"httppost:::"+httppost);
            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
            ret[1] = response.getStatusLine().getReasonPhrase();            
            
            if("200".equals(ret[0])) {
                int totlen = 0;
                int len = 0;
	            resEntity = response.getEntity();
//	            ret[2] = EntityUtils.toString(resEntity); ==> 이게 되면 좋은데... 이상하게 exception 떨어진다.. 짜증....
	            iis = resEntity.getContent();
	            bos = new ByteArrayOutputStream();
	            byte[] packet = new byte[2048];
	            
	            if(iis!=null) {
		            while (true) {	            
		            	len = iis.read(packet, 0, packet.length);
		                if(len > 0) {
		                	totlen = totlen + len;
		                	bos.write(packet,0,len);
		                }
		                if(len <= 0) {
		                	break;
		                }
		            }
		            logger.debug("[outCharSet]["+outCharSet+"]");		            
		            ret[2] = new String(bos.toByteArray(), outCharSet);
		            logger.debug("[outCharSet_1]["+outCharSet+"]");
	            } else {
	                ret[0] = "201";
	                ret[1] = "응답받은 데이터가 없습니다.";
	            	ret[2] = "No return data";
	            }

            } else {
	            ret[2] = "No return data";
            }
            logger.debug("[resEntity]["+resEntity+"]");
            logger.debug("[httpcall]["+ret[2]+"]");            
        } catch (IOException ex) {
            throw ex;
        }
        finally {
        	
        	try {if(iis!=null) iis.close();}catch(Exception e){logger.debug("XinterfaceServiceImpl iisException ::" + e.getMessage());}
        	try {if(bos!=null) bos.close();}catch(Exception e){logger.debug("XinterfaceServiceImpl bosException ::" + e.getMessage());}
            httpclient.getConnectionManager().shutdown();
            try {
            	StringBuffer sb = new StringBuffer(100);
	            sb.append("[URL][");
	            sb.append(sendurl);
	            sb.append("][SENDTIME][");
	            sb.append(startTime);
	            sb.append("][RECVTIME][");
	            sb.append(ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss"));
	            sb.append("][SENDDATA][");
	            sb.append(sdata.toString());
	            sb.append("][RECVDATA][");
	            sb.append(ret[0]);
	            sb.append(" : ");
	            sb.append(ret[2]);
	            sb.append("]");
	    		logger.info(sb.toString());
	    		System.out.println("======>"+sb.toString());
            } catch(Exception le) {logger.debug("XinterfaceServiceImpl httpsCall Exception ::" + le.getMessage());}

        }
        return ret;
    }
	
	
	/*
	 * 일반 http get전송 josn to xml
	 * */
	private String[] httpCallget(String sendurl,String sdata, String inCharSet, String outCharSet) throws Exception {
 
		String startTime = ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss");
		
		
		//URL url = new URL(sendurl);
		
		//HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
	
		
		HttpClient httpclient = new DefaultHttpClient();
       UrlEncodedFormEntity reqEntity = null;
       
        HttpEntity   resEntity = null;
        String[] ret = new String[3];
        
        InputStream iis = null;
        ByteArrayOutputStream bos = null;
        
        try {
        	
        	HttpParams params = httpclient.getParams();
        	HttpConnectionParams.setConnectionTimeout(params, 30000);
        	HttpConnectionParams.setSoTimeout(params, 30000);
        	
        	//HttpsConnect httpsConnect = new HttpsConnect();
        	
            //HttpPost httppost = new HttpPost(sendurl);
        	sendurl = sendurl + "?"+sdata;
        	System.out.println("sendURL>>>>>>>>>>>>"+sendurl);
           HttpGet httpget = new HttpGet(sendurl);
            
           
            //System.out.println("sdata.toString()===>"+sdata.toString());
            
            //json to xml
            HttpResponse response = httpclient.execute(httpget);  
            
            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
            ret[1] = response.getStatusLine().getReasonPhrase();            
           // System.out.println("ret[0]==============>"+ret[0]);
            //System.out.println("ret[1]==============>"+ret[1]);
            if("200".equals(ret[0])) {           	
            	
                int totlen = 0;
                int len = 0;
	            resEntity = response.getEntity();
//	            ret[2] = EntityUtils.toString(resEntity); ==> 이게 되면 좋은데... 이상하게 exception 떨어진다.. 짜증....
	            iis = resEntity.getContent();
	            bos = new ByteArrayOutputStream();
	            byte[] packet = new byte[2048];
	            
	            if(iis!=null) {
		            while (true) {	            
		            	len = iis.read(packet, 0, packet.length);
		                if(len > 0) {
		                	totlen = totlen + len;
		                	bos.write(packet,0,len);
		                }
		                if(len <= 0) {
		                	break;
		                }
		            }
		            logger.debug("[outCharSet]["+outCharSet+"]");		            
		            ret[2] = new String(bos.toByteArray(), outCharSet);
		            
		           // System.out.println("ret[2]============>"+ret[2]);
		            
		            logger.debug("[outCharSet_1]["+outCharSet+"]");
	            } else {
	                ret[0] = "201";
	                ret[1] = "응답받은 데이터가 없습니다.";
	            	ret[2] = "No return data";
	            }

            } else {
	            ret[2] = "No return data";
            }
            logger.debug("[resEntity]["+resEntity+"]");
            logger.debug("[httpcallget]["+ret[2]+"]");            
        } catch (IOException ex) {
            throw ex;
        }
        finally {
        	
        	try {if(iis!=null) iis.close();}catch(Exception e){logger.debug("XinterfaceServiceImpl httpCallget iisException ::" + e.getMessage());}
        	try {if(bos!=null) bos.close();}catch(Exception e){logger.debug("XinterfaceServiceImpl httpCallget bosException ::" + e.getMessage());}
            httpclient.getConnectionManager().shutdown();
            try {
            	StringBuffer sb = new StringBuffer(100);
	            sb.append("[URL][");
	            sb.append(sendurl);
	            sb.append("][SENDTIME][");
	            sb.append(startTime);
	            sb.append("][RECVTIME][");
	            sb.append(ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss"));
	            sb.append("][SENDDATA][");
	            sb.append(sdata.toString());
	            sb.append("][RECVDATA][");
	            sb.append(ret[0]);
	            sb.append(" : ");
	            sb.append(ret[2]);
	            sb.append("]");
	    		logger.info(sb.toString());
	    		System.out.println("======>"+sb.toString());
            } catch(Exception le) {logger.debug("XinterfaceServiceImpl httpCallget Exception ::" + le.getMessage());}

        }
        return ret;
    }
	

	/*
	 * 일반 http get전송 josn to xml
	 * */
	private String[] httpCallgetA(String sendurl,String sdata, String inCharSet, String outCharSet) throws Exception {
 
		String startTime = ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss");
		
		

		HttpClient httpclient = new DefaultHttpClient();
       UrlEncodedFormEntity reqEntity = null;
       
        HttpEntity   resEntity = null;
        String[] ret = new String[3];
        
        InputStream iis = null;
        ByteArrayOutputStream bos = null;        
        
        try {
        	
        	HttpParams params = httpclient.getParams();
        	HttpConnectionParams.setConnectionTimeout(params, 30000);
        	HttpConnectionParams.setSoTimeout(params, 30000);        	

        	sendurl = sendurl + "?Clico_Cd=0003&EncStr="+sdata;        
           HttpGet httpget = new HttpGet(sendurl);
            
           System.out.println("1)sendUrl>>>>"+sendurl);
            //System.out.println("sdata.toString()===>"+sdata.toString());
            
            //json to xml
            HttpResponse response = httpclient.execute(httpget);  
            
            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
            ret[1] = response.getStatusLine().getReasonPhrase();            
           System.out.println("ret[0]==============>"+ret[0]);
           System.out.println("ret[1]==============>"+ret[1]);
            if("200".equals(ret[0])) {           	            	
                int totlen = 0;
                int len = 0;
	            resEntity = response.getEntity();
//	            ret[2] = EntityUtils.toString(resEntity); ==> 이게 되면 좋은데... 이상하게 exception 떨어진다.. 짜증....
	            iis = resEntity.getContent();        
	            
	            bos = new ByteArrayOutputStream();
	            byte[] packet = new byte[2048];
	            
	            if(iis!=null) {
		            while (true) {	            
		            	len = iis.read(packet, 0, packet.length);
		                if(len > 0) {
		                	totlen = totlen + len;
		                	bos.write(packet,0,len);
		                }
		                if(len <= 0) {
		                	break;
		                }
		            }
		            logger.debug("[outCharSet]["+outCharSet+"]");		          
	
		            	ret[2] = new String(bos.toByteArray(), outCharSet);
		
		          //  System.out.println("ret[2]===============>"+ret[2]);			            
		            //System.out.println(">>>>>>>>>>>>ret[2]============>"+decrypt(ret[2]));
		            
		            logger.debug("[outCharSet_1]["+outCharSet+"]");
	            } else {
	                ret[0] = "201";
	                ret[1] = "응답받은 데이터가 없습니다.";
	            	ret[2] = "No return data";
	            }

            } else {
	            ret[2] = "No return data";
            }
            logger.debug("[resEntity]["+resEntity+"]");
            logger.debug("[httpcallget]["+ret[2]+"]");            
        } catch (IOException ex) {
            throw ex;
        }
        finally {
        	
        	try {if(iis!=null) iis.close();}catch(Exception e){logger.debug("XinterfaceServiceImpl httpCallgetA iisException ::" + e.getMessage());}
        	try {if(bos!=null) bos.close();}catch(Exception e){logger.debug("XinterfaceServiceImpl httpCallgetA bosException ::" + e.getMessage());}
            httpclient.getConnectionManager().shutdown();
            try {
            	StringBuffer sb = new StringBuffer(100);
	            sb.append("[URL][");
	            sb.append(sendurl);
	            sb.append("][SENDTIME][");
	            sb.append(startTime);
	            sb.append("][RECVTIME][");
	            sb.append(ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss"));
	            sb.append("][SENDDATA][");
	            sb.append(sdata.toString());
	            sb.append("][RECVDATA][");
	            sb.append(ret[0]);
	            sb.append(" : ");
	            sb.append(ret[2]);
	            sb.append("]");
	    		logger.info(sb.toString());
	    		System.out.println("ret[2]======>"+sb.toString());
            } catch(Exception le) {logger.debug("XinterfaceServiceImpl httpCallgetA iisException ::" + le.getMessage());}

        }
        return ret;
    }
				
	/*
	 * soap 전송
	 * reqSoapXml : xml string
	 * endpoint : URL
	 * sSoapAction : SOAPAction
	 * */
	private String[] soapCall(String reqSoapXml, String endpoint, String sSoapAction) throws IOException {
		
		String startTime = ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss");
		
        HttpClient httpclient = new DefaultHttpClient();
        StringEntity reqEntity = null;
        HttpEntity   resEntity = null;
        String[] ret = new String[3];
        
        try {
        	
        	HttpParams params = httpclient.getParams();
        	HttpConnectionParams.setConnectionTimeout(params, 30000);
        	HttpConnectionParams.setSoTimeout(params, 30000);
        	
            HttpPost httppost = new HttpPost(endpoint);
            httppost.setHeader("SOAPAction", sSoapAction);
    
            //params.setParameter(CookieSpecPNames.DATE_PATTERNS, Arrays.asList("EEE, dd MMM-yyyy-HH:mm:ss z", "EEE, dd MMMyyyy HH:mm:ss z"));
            //httppost.setParams(params);
            System.out.println("reqSoapXml :"+reqSoapXml);
            reqEntity = new StringEntity(reqSoapXml, "UTF-8");
            reqEntity.setContentType("application/soap+xml");
            httppost.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(httppost);
            
            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
            ret[1] = response.getStatusLine().getReasonPhrase();
            
            if("200".equals(ret[0])) {
                resEntity = response.getEntity();
                if(resEntity!=null) {
                	ret[2] = EntityUtils.toString(resEntity);
                } else {
	                ret[0] = "201";
	                ret[1] = "응답받은 데이터가 없습니다.";
	            	ret[2] = "No return data";
                }
            } else {
	            ret[2] = "No return data";
            }
        } catch (IOException ex) {
            throw ex;
        }
        finally {
            if (resEntity != null) {
                resEntity.consumeContent();
            }
            httpclient.getConnectionManager().shutdown();
            
            try {
	            StringBuffer sb = new StringBuffer(100);
	            sb.append("[URL][");
	            sb.append(endpoint);
	            sb.append("][SENDTIME][");
	            sb.append(startTime);
	            sb.append("][RECVTIME][");
	            sb.append(ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss"));
	            sb.append("][SENDDATA][");
	            sb.append(reqSoapXml);
	            sb.append("][RECVDATA][");
	            sb.append(ret[0]);
	            sb.append(" : ");
	            sb.append(ret[2]);
	            sb.append("]");
	    		logger.info(sb.toString());
            } catch(Exception le) {logger.debug("XinterfaceServiceImpl soapCall Exception ::" + le.getMessage());}
        }
        return ret;
    }
	
	
	/*
	 * 넘겨받은 xml로 output dataset을 만듬.
	 * */
	private void setOutputDataset(XcommonDto dto, String[] resp) throws Exception {

		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		
		System.out.println("ds_env"+ds_env.getName());
		System.out.println("ds_env"+ds_env.toString());
		//통신성공
		DataSet dcommon = new DataSet("DS_COMMON");
		dcommon.addColumn("HTTP_RET_CODE", DataTypes.STRING, 2000);
		dcommon.addColumn("HTTP_RET_MSG", DataTypes.STRING, 2000);
		dcommon.newRow(0);	
		dcommon.set(0, "HTTP_RET_CODE", "0");
		dcommon.set(0, "HTTP_RET_MSG", "통신성공 ");
		dto.getOutdslist().add(dcommon);
		
		//돔에 집어넣기...
		try {
			String kind = dto.dsToString(ds_env.getObject(0, "IF_KIND"));
			Element root;

			//jsontoxml
			 if("GSC-APP".equals(kind) || "GSC-VOC".equals(kind)){				
			 	 //System.out.println("resp[2] = "+ resp[2]);
				 logger.debug("resp[2] = "+ resp[2]);
	             JsonToxml jdata = new JsonToxml();	            
	             resp[2] = jdata.jsonToXml(resp[2]);
			 } else if("WAS-TM".equals(kind) || "WAS-TMPAYMENT".equals(kind)) {
				 String jsonStr = "";
				 jsonStr = decrypt(resp[2]);
				 				
				 tmJsonToxml jdata = new tmJsonToxml();	            
	             resp[2] = jdata.jsonToXml(jsonStr);
			 }
			
			//2014.07.10 JH -수정 
			//20210413 에너지 플러스 복호화 추가			
			//if("GSCP".equals(kind) || "ERMS".equals(kind)){
			if("GSCP".equals(kind) || "GSCP-APP".equals(kind) || "GSCPDTL-APP".equals(kind) || "WAS-TM".equals(kind) ||"WAS-TMPAYMENT".equals(kind) || "GSC-VOC".equals(kind)){				
				root = makeDom(resp[2].replaceAll("\\&lt\\;\\!\\[CDATA\\[","\\<\\!\\[CDATA\\[").replaceAll("\\]\\]\\&gt\\;","\\]\\]\\>"));
			}else {//
				root = makeDom(resp[2]);
			}
			
			NodeList nodelist;
			Element element;
			
			String headers = dto.dsToString(ds_env.getObject(0, "RES_HEADER_SECTION"));
			String columns = dto.dsToString(ds_env.getObject(0, "RES_COLUMN_SECTION"));
			String records = dto.dsToString(ds_env.getObject(0, "RES_RECORD_SECTION"));
			String root_columns = dto.dsToString(ds_env.getObject(0, "RES_ROOT_SECTION"));	
			
			
			System.out.println("headers :: "+headers);
			System.out.println("columns :: "+columns);
			System.out.println("records :: "+records);
			System.out.println("root_columns :: "+root_columns);
			System.out.println("kind::"+kind);
			
					
			if(!"".equals(headers)) {				
				element = (Element)root.getElementsByTagName(headers).item(0);		
				nodelist = root.getElementsByTagName(headers);				
				if(element!=null) {
					if("GSCPDTL-APP".equals(kind)){
						dto.getOutdslist().add(GscpDtlLxmlToDataSet(element, "DS_HEADER", records));
					}else {
						dto.getOutdslist().add(xmlToDataSet(element, "DS_HEADER", records));
					}						
					}				
			}
			
			/*
			if(!"".equals(headers)) {				
				element = (Element)root.getElementsByTagName(headers).item(0);		
				nodelist = root.getElementsByTagName(headers);				
				if(element!=null) {
					if("GSCPDTL-APP".equals(kind)){
						if(nodelist!=null) {							
							dto.getOutdslist().add(GscpDtlLxmlToDataSet(nodelist, "DS_HEADER", records));
						}
					}else {
						dto.getOutdslist().add(xmlToDataSet(element, "DS_HEADER", records));
					}
				}
			}
			*/
			
			if(!"".equals(columns)) {			
				System.out.println("===========================columns============================");
				element = (Element)root.getElementsByTagName(columns).item(0);				
				if(element!=null)
					dto.getOutdslist().add(xmlToDataSet(element, "DS_COLUMN", records));
			}

			if(!"".equals(records)) {
				nodelist = root.getElementsByTagName(records);				
				System.out.println("===========================records============================");
				if(nodelist!=null)
					{
						if("GSCP-APP".equals(kind)){
								System.out.println("kind>>>>>>>>>>>>>>>>>>>>>>"+kind);
							dto.getOutdslist().add(GscpxmlRecordToDataSet(nodelist, "DS_RECORD"));  
						}else {
							dto.getOutdslist().add(xmlRecordToDataSet(nodelist, "DS_RECORD"));
						}
					}			
			}			
			
			if("Y".equals(root_columns)) {
				dto.getOutdslist().add(xmlToDataSet(root, "DS_ROOT", records));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			
		} finally {
			if(!ComUtil.isProd()) {
				System.out.println("===================================");
				DataSet retxml = new DataSet("DS_RETXML");
				retxml.addColumn("SEND_URL", DataTypes.STRING, 2000);
				retxml.addColumn("SEND_DATA", DataTypes.STRING, 2000);
				retxml.addColumn("RET_DATA", DataTypes.STRING, 2000);
				retxml.newRow(0);
				retxml.set(0, "SEND_URL", dto.getUserTempMap().get("sendUrl"));
				retxml.set(0, "SEND_DATA", dto.getUserTempMap().get("sendData"));
				retxml.set(0, "RET_DATA", dto.getUserTempMap().get("resultData"));
				dto.getOutdslist().add(retxml);
				System.out.println("retxml>>>>"+retxml.toString());
			}
		}
	}
	
	/*
	 * exception 떨어진 애들...
	 * */
	private void setErrorData(XcommonDto dto, String[] ret) {

		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		
		DataSet dcommon = new DataSet("DS_COMMON");
		dcommon.addColumn("HTTP_RET_CODE", DataTypes.STRING, 2000);
		dcommon.addColumn("HTTP_RET_MSG", DataTypes.STRING, 2000);
		dcommon.newRow(0);	
		dcommon.set(0, "HTTP_RET_CODE", "-1");
		dcommon.set(0, "HTTP_RET_MSG", "요청하신 거래는 다음에러로 인해 통신실패를 하였습니다.\n보다 정확한 원인을 알고 싶으시면 아래메세지를 관리자에게 보여주세요 \n\n  [" + ret[0] + " : " + ret[1] + "]");

		dto.getOutdslist().add(dcommon);


		
		if(!ComUtil.isProd()) {
			DataSet retxml = new DataSet("DS_RETXML");
			retxml.addColumn("SEND_URL", DataTypes.STRING, 2000);
			retxml.addColumn("SEND_DATA", DataTypes.STRING, 2000);
			retxml.addColumn("RET_DATA", DataTypes.STRING, 2000);
			retxml.newRow(0);
			retxml.set(0, "SEND_URL", dto.getUserTempMap().get("sendUrl"));
			retxml.set(0, "SEND_DATA", dto.getUserTempMap().get("sendData"));
			retxml.set(0, "RET_DATA", dto.getUserTempMap().get("resultData"));
			dto.getOutdslist().add(retxml);
		}
	}
	
	/*
	 * 멀티 트랜잭션에서 응답 코드 담기.
	 * */
	private DataSet getTranResult(XcommonDto dto, String respxml) throws Exception {
		
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		
		Element root = makeDom(respxml);
		Element element;
		DataSet ds = new DataSet();
		
		if(!"".equals(dto.dsToString(ds_env.getObject(0, "RES_DATA_SECTION")))) {	
			element = (Element)root.getElementsByTagName(dto.dsToString(ds_env.getObject(0, "RES_DATA_SECTION"))).item(0);
			ds = xmlToDataSet(element);
		}
		return ds;
	}
	
	//	MPP-SMS 발송 조회 I/F 위하여 만듬
	private String[] MPPString(XcommonDto dto, String endpoint) throws Exception {
		
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
		
		StringBuffer xml = new StringBuffer(100);
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		xml.append("<soap12:Envelope");
		xml.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		xml.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
		xml.append(" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n");
		xml.append("<soap12:Body>\n");
		xml.append("<"); xml.append(dto.dsToString(ds_env.getObject(0, "REQ_SERVICE_ID"))); //service ID
		xml.append(" xmlns=\"http://192.168.7.21/NexWebServices/SMSService.asmx\">\n");
		
		//data 시작
		for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
			xml.append("<"); xml.append(ds_ifdata.getColumn(i).getName()); xml.append(">"); 
			xml.append(dto.dsToString(ds_ifdata.getObject(0, i)));
			xml.append("</"); xml.append(ds_ifdata.getColumn(i).getName()); xml.append(">\n"); 
		}
				
		xml.append("</"); xml.append(dto.dsToString(ds_env.getObject(0, "REQ_SERVICE_ID"))); xml.append(">\n"); //service ID
	    xml.append("</soap12:Body>\n");
	    xml.append("</soap12:Envelope>\n");
		
		String sendData = xml.toString();
		System.out.println("xml : "+xml);
		System.out.println("endpoint : "+endpoint);
       	String[] ret = soapCall(sendData, endpoint, "");

       	if(!ComUtil.isProd()) {
	       	dto.getUserTempMap().put("sendUrl", endpoint);
			dto.getUserTempMap().put("sendData", sendData);
	       	dto.getUserTempMap().put("resultData", ret[2]);
       	}
       	
       	return ret;
	}
	
	
//	AO 조회 I/F 위하여 만듬
	private String[] AOString(XcommonDto dto, String endpoint) throws Exception {
		
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
		
		StringBuffer xml = new StringBuffer(100);
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		xml.append("<soap12:Envelope");
		xml.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		xml.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
		xml.append(" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n");
		xml.append("<soap12:Body>\n");
		xml.append("<"); xml.append(dto.dsToString(ds_env.getObject(0, "REQ_SERVICE_ID"))); //service ID
		xml.append(" xmlns=\"http://tempuri.org/\">\n");
		
		//data 시작
		for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
			xml.append("<"); xml.append(ds_ifdata.getColumn(i).getName()); xml.append(">"); 
			xml.append(dto.dsToString(ds_ifdata.getObject(0, i)));
			xml.append("</"); xml.append(ds_ifdata.getColumn(i).getName()); xml.append(">\n"); 
		}
				
		xml.append("</"); xml.append(dto.dsToString(ds_env.getObject(0, "REQ_SERVICE_ID"))); xml.append(">\n"); //service ID
	    xml.append("</soap12:Body>\n");
	    xml.append("</soap12:Envelope>\n");
		
		String sendData = xml.toString();
		//System.out.println("xml : "+xml);
		//System.out.println("endpoint : "+endpoint);
       	String[] ret = soapCall(sendData, endpoint, "");


       	String temp=null;
       	
       	temp = AOtranXML(ret[2]);

       	ret[2]=null;
       	ret[2]=temp;
       	
       	if(!ComUtil.isProd()) {
	       	dto.getUserTempMap().put("sendUrl", endpoint);
			dto.getUserTempMap().put("sendData", sendData);
	       	dto.getUserTempMap().put("resultData", ret[2]);
       	}
       	
       	return ret;
	}
	
	
	private String AOtranXML(String ret) throws Exception {
		String temp = ret;
		
		try {
			
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			sb.append("<TRANSFORMATION>");//없으면 리턴값을 못가지고옴 명칭은 맘대로
			sb.append("<RSV_RSLT>");		
			sb.append("<RESULT_CODE>0</RESULT_CODE>");		
			sb.append("<RESULT_MSG>정상</RESULT_MSG>");
			sb.append("<LIST>");		
						
			DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			Document doc = docBuild.parse(new InputSource(new StringReader(temp.toString())));
			doc.getDocumentElement().normalize();
			
			NodeList list = doc.getElementsByTagName("DataModel");
			
			// <LIST>로 row 구분하기 위해 List에 담음   2013.09.11
			List<String> tmpList = new ArrayList<String>();
			
			for (int i=0; i< list.getLength(); i++){
				Node DataModel = list.item(i);
				Element DataModelElmnt = (Element) DataModel;
				
				NodeList keyList = DataModelElmnt.getElementsByTagName("Key");
				Element keyElmnt  =(Element) keyList.item(0);
				Node key = keyElmnt.getFirstChild();
				
				NodeList valueList= DataModelElmnt.getElementsByTagName("Value");
				Element valueElmnt = (Element) valueList.item(0);
				Node value = valueElmnt.getFirstChild();

				//tmpList에 이미 add되어 있으면, </LIST>로 닫고 새 <LIST>생성  2013.09.11
				if(tmpList.contains(key.getNodeValue()) != true){
					
					tmpList.add(key.getNodeValue());
					
					if(value == null ){
						sb.append("<"+key.getNodeValue()+">"+""+"</"+key.getNodeValue()+">");
					}else{
						sb.append("<"+key.getNodeValue()+">"+value.getNodeValue()+"</"+key.getNodeValue()+">");
					}
					
				}else{

					tmpList.clear();
					
					if(value == null ){
						sb.append("</LIST><LIST>" + "<"+key.getNodeValue()+">"+""+"</"+key.getNodeValue()+">");
					}else{
						sb.append("</LIST><LIST>" + "<"+key.getNodeValue()+">"+value.getNodeValue()+"</"+key.getNodeValue()+">");
					}
				}
				
				// 2013.09.11 끝

				/*save my code
				if(value == null ){
					sb.append("<"+key.getNodeValue()+">"+""+"</"+key.getNodeValue()+">");
				}else{
					sb.append("<"+key.getNodeValue()+">"+value.getNodeValue()+"</"+key.getNodeValue()+">");
				}
				*/
			}
			sb.append("</LIST>");
			sb.append("</RSV_RSLT>");
			sb.append("</TRANSFORMATION>");
			
			
			//System.out.println("------******----XMLTrans:" + sb.toString());
			
			temp = sb.toString();
		}catch(Exception e){
			logger.debug("XinterfaceServiceImpl soapCall Exception ::" + e.getMessage());
		}
		
		return temp;
		
	}
	
	
	private String[] CANCEL_WAS(XcommonDto dto, String endpoint, int dRow, String inCharSet, String outCharSet) throws Exception {
		
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
		
		String sendURL = endpoint + ds_env.getString(0, "REQ_SERVICE_METHOD");
		
		List<BasicNameValuePair> sdata = new ArrayList<BasicNameValuePair>();
		for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
			sdata.add(new BasicNameValuePair(ds_ifdata.getColumn(i).getName(), ds_ifdata.getString(dRow, i)==null?"":ds_ifdata.getString(dRow, i)));
		}
		
		String[] ret = httpCall(sendURL, sdata, inCharSet, outCharSet);
		String temp="";
		temp = trans_cancelXML(ret[2]);
		
		ret[2]=null;
		ret[2]=temp;
		System.out.println(ret[2].toString());
		
		if(!ComUtil.isProd()) {
			dto.getUserTempMap().put("sendUrl", sendURL);
			dto.getUserTempMap().put("sendData", sdata.toString());
			dto.getUserTempMap().put("resultData", ret[2]);
		}
		
		return ret;
	}
	
	private String trans_cancelXML(String ret) throws Exception {
	
		//System.out.println("**************res.trim():"+ret.trim()+"****************");
		String temp = ret.trim();
		temp.substring(0,3);
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<TRANSFORMATION>");
		sb.append("<RSV_RSLT>");		
		sb.append("<RESULT_CODE>"+temp.substring(0,3)+"</RESULT_CODE>");		
		sb.append("<RESULT_MSG>"+temp.substring(3)+"</RESULT_MSG>");
		sb.append("</RSV_RSLT>");
		sb.append("</TRANSFORMATION>");
		
		return sb.toString();
	}
	
	
		//20210415 GSCP 암복호화 
	   // public static String Alg = "AES/CBC/PKCS5Padding";	    
	    //public static  String  PK = "000320131128ABCDEFGHIJKLMNOPQRST"; // 운영
	    //public static String   PK = "1234567890ABCDEFGHIJKLMNOPQRSTUV"; // 개발
	   // public static String IV = PK.substring(0,16); // 16byte

	    public static String encrypt(String sdata) throws Exception {   	
	        Cipher cipher = Cipher.getInstance(Alg);
	        SecretKeySpec keySpec = new SecretKeySpec(PK.getBytes(), "AES");
	        IvParameterSpec ivParamSpec = new IvParameterSpec(IV.getBytes());
	        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);	        

	        byte[] encrypted = cipher.doFinal(sdata.getBytes("UTF-8"));
	        String encodeBytes = new String(Base64.encodeBase64(encrypted));        
	        
	        return encodeBytes;
	    }

	    

	    public static String decrypt(String cipherText) throws Exception {	    	
	    	
	    	if(cipherText == null) {
	    		return "";
	    	}

	        Cipher cipher = Cipher.getInstance(Alg);
	        SecretKeySpec keySpec = new SecretKeySpec(PK.getBytes(), "AES");
	        IvParameterSpec ivParamSpec = new IvParameterSpec(IV.getBytes());
	        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);	       
	        
	        byte[] decodeBytes = Base64.decodeBase64(cipherText.getBytes());
	        return new String(cipher.doFinal(decodeBytes), "UTF-8");              
	    }			
	    
	    /**
	     *  TM I/F Rest-post 암호화 전달
	     *  2022.11.17 NP847
	     * @param dto
	     * @param endpoint
	     * @param dRow
	     * @param inCharSet
	     * @param outCharSet
	     * @return
	     * @throws Exception
	     */
	    private String[] TM_Rest(XcommonDto dto, String endpoint, int dRow, String inCharSet, String outCharSet) throws Exception {
	    	System.out.println("#################### TM_Rest start!!!!!!!!!!!!!!!!");
	    	
	    	/*키관리 솔루션 damo 변경 | 2024.09.20 np847 */
	    	//TM에서 사용하는 암복호화 키값 운영/개발 분기 처리 
			if(ComUtil.isProd()) {// 운영
				   PK = Const.TM_PK;  			 
				   IV = PK.substring(0,16); // 16byte			
			}else{ //개발 
				    PK = Const.TM_PK_TEST;
				    IV = PK.substring(0,16); // 16byte			
			}
			
	    	/*d'amo ScpDbAgent Object 
			ScpDbAgent damo = new ScpDbAgent();
			String damoKey = "";
			
			try {
				if(ComUtil.isProd()) {// 운영
					//Key관리 String to Hex
					damoKey = damo.ScpExportKey(DAMO_INI_PATH, "ICCSCSC_ENC",  " ");
					
					//key관리 Hex to String 
					byte[] decodeHexByte = DatatypeConverter.parseHexBinary(damoKey);
					PK = new String(decodeHexByte);
					
					IV = PK.substring(0,16); // 16byte	
				}else{ //개발 
					//Key관리 String to Hex
					damoKey = damo.ScpExportKey(DAMO_INI_PATH, "ICCSCSC_DEV_ENC",  " ");
					
					//key관리 Hex to String 
					byte[] decodeHexByte = DatatypeConverter.parseHexBinary(damoKey);
					PK = new String(decodeHexByte);
					
					IV = PK.substring(0,16); // 16byte	
				}
			} catch (ScpDbAgentException se) {
				logger.debug("D'AMO Agent Exception ::" + se.getMessage());
			}
			
			logger.debug("D'Amo KEY :: " + PK);
			logger.debug("D'Amo IV :: " + IV);
			*/
	    	


	    	DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
	    	DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
	    	
	    	String sendURL = endpoint + ds_env.getString(0, "REQ_SERVICE_METHOD");
	    	
	    	//tm-if json 방식
	    	JSONObject jsonObj = new JSONObject();
	    	
	    	String sdataKey = "";		// json key값
	    	String sdataVal = "";		// json value값
	    	String jsonStr = "";			// json to string 전송
	    	
	    	if(ds_ifdata != null) {
				for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
					/* 데이터 확인용
					logger.debug("data Name : " +ds_ifdata.getColumn(i).getName() );
					logger.debug("data Value : " +ds_ifdata.getString(dRow, i) );
					 */
					
					sdataKey = ds_ifdata.getColumn(i).getName();
					sdataVal = ds_ifdata.getString(dRow, i);
					
					jsonObj.put(sdataKey, sdataVal);
				}
	    	}

	    	//parameter  암호화
	    	jsonStr = encrypt(jsonObj.toString());
	    	//logger.debug("jsonStr :" + jsonStr);
	    	String[] ret = TmPostCall(sendURL, jsonStr, inCharSet, outCharSet);
	    	
	    	//운영이 아니면
	    	if(!ComUtil.isProd()) {
	    		dto.getUserTempMap().put("sendUrl", sendURL);
	    		dto.getUserTempMap().put("sendData", jsonStr);
	    		dto.getUserTempMap().put("resultData", ret[2]);
	    	}
	    	
	    	
	    	return ret;
	    }
	    
	    /**
		 *  TM I/F post전송
		 *   2022.11.17 NP847
		 *  I/F Type : REST API 
		 *  Data Type : Json
		 *  REQ,RES Type : POST
		 * */
		private String[] TmPostCall(String sendurl, String jsonStr,  String inCharSet, String outCharSet ) throws IOException {
			String startTime = ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss");
			
			logger.debug("######## TM Interface Call Start Time ========> " + startTime);
			
			HttpsClient httpsclient = new HttpsClient();				
			HttpClient httpclient = httpsclient.getNewHttpClient();
			StringEntity strParm = null;
	        HttpEntity   resEntity = null;
	        String[] ret = new String[3];
	        
	        InputStream iis = null;
	        ByteArrayOutputStream bos = null;
	        
	        try {
	        	HttpParams params = httpclient.getParams();
	        	HttpConnectionParams.setConnectionTimeout(params, 30000);
	        	HttpConnectionParams.setSoTimeout(params, 30000);

	        	//entity json set
	        	strParm = new StringEntity(jsonStr);
	            strParm.setContentEncoding(inCharSet);
	            strParm.setContentType("application/json");
	            
	        	//post 메소드 URL 생성
	            HttpPost httppost = new HttpPost(sendurl);
	            httppost.setEntity(strParm);
	            HttpResponse response = httpclient.execute(httppost);   
	            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
	            ret[1] = response.getStatusLine().getReasonPhrase();            

	            if("200".equals(ret[0])) {
	                int totlen = 0;
	                int len = 0;
		            resEntity = response.getEntity();
//		            ret[2] = EntityUtils.toString(resEntity); ==> 이게 되면 좋은데... 이상하게 exception 떨어진다.. 짜증....
		            iis = resEntity.getContent();
		            bos = new ByteArrayOutputStream();
		            byte[] packet = new byte[2048];
		            
		            if(iis!=null) {
			            while (true) {
			            	len = iis.read(packet, 0, packet.length);
			                if(len > 0) {
			                	totlen = totlen + len;
			                	bos.write(packet,0,len);
			                }
			                if(len <= 0) {
			                	break;
			                }
			            }
			            logger.debug("[outCharSet]["+outCharSet+"]");		            
			            ret[2] = new String(bos.toByteArray(), outCharSet);
			            logger.debug("[outCharSet_1]["+outCharSet+"]");
		            } else {
		                ret[0] = "201";
		                ret[1] = "응답받은 데이터가 없습니다.";
		            	ret[2] = "No return data";
		            }

	            } else {
		            ret[2] = "No return data";
	            }
	            logger.debug("[resEntity]["+resEntity+"]");
	            logger.debug("[httpcall]["+ret[2]+"]");            
	        } catch (IOException ex) {
	            throw ex;
	        } catch (Exception e) {
	        	logger.debug("XinterfaceServiceImpl TmPostCall Exception ::" + e.getMessage());
			}
	        finally {
	        	
	        	try {if(iis!=null) iis.close();}catch(Exception e){logger.debug("XinterfaceServiceImpl TmPostCall iisException ::" + e.getMessage());}
	        	try {if(bos!=null) bos.close();}catch(Exception e){logger.debug("XinterfaceServiceImpl TmPostCall bosException ::" + e.getMessage());}
	            httpclient.getConnectionManager().shutdown();

	        }
				
	        return ret;
	    }
	    
	    /**
		 *  TM 불만처리결과 Update
		 *  @param : json
		 *   2022.11.17 NP847
		 */
	    public String TmDsftAcpnUpdate(HttpServletRequest request) throws Exception {
	    	logger.debug("## DsftAcpn Update Start  ##");
	    	
	    	//TM에서 사용하는 암복호화 키값 운영/개발 분기 처리 
			if(ComUtil.isProd()) {// 운영
				   PK = Const.TM_PK;  			 
				   IV = PK.substring(0,16); // 16byte			
			}else{ //개발 
				    PK = Const.TM_PK_TEST;
				    IV = PK.substring(0,16); // 16byte			
			}
	    	
	    	//return set
	    	String resultRtn = null;
	    	JSONObject setJsObj = new JSONObject();
	    	
			BufferedReader br = null;
			InputStream is = null;
			StringBuffer sb = new StringBuffer();
			
			//요청값을 jsonObject parsing
			JSONParser jp = new JSONParser();
			JSONObject parmJsObj = new JSONObject();
			
			//json body를 한줄씩 담을 변수
			String bodyLine = "";
			//복호화 변수
			String decStr = "";
			try {
				//body내용 -> inputstream에 담는다.
				is = request.getInputStream();
				
				if(is != null) {
					br = new BufferedReader(new InputStreamReader(is));
					while((bodyLine=br.readLine()) != null) {
						sb.append(bodyLine);
					}
					
					// parameter 복호화
					decStr = decrypt(sb.toString());
					parmJsObj =  (JSONObject) jp.parse(decStr);
					
					try {
						//json to hashmap 매핑
						HashMap<String, String> map = new HashMap<String, String>();
						Iterator it = parmJsObj.entrySet().iterator();
						
						while (it.hasNext()) {
							Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
							//logger.debug("key : " + entry.getKey());
							//logger.debug("val : " + entry.getValue());
							map.put(entry.getKey(), entry.getValue());
						}
						
						String sqlmapid = "CMP180.Set_TM_DSFT_ACPN_PROC_UPDATE";
						commonDao.update(sqlmapid, map);
						
					} catch (Exception e) {
						logger.debug("XinterfaceServiceImpl TmDsftAcpnUpdate Exception ::" + e.getMessage());
					}
					
					// return 값 set
					setJsObj.put("RESULT", "정상 처리 되었습니다.");
					setJsObj.put("RESULT_CODE", "00");
				} else {
					logger.debug("Parameter is Null!!");
					
					// return 값 set
					setJsObj.put("RESULT", "필수 입력값이 없습니다.");
					setJsObj.put("RESULT_CODE", "10");
				}
				

				
			} catch (Exception e) {
				logger.debug("XinterfaceServiceImpl TmDsftAcpnUpdate Exception ::" + e.getMessage());
				setJsObj.put("RESULT", "시스템 오류입니다. 관리자에게 문의주세요.");
				setJsObj.put("RESULT_CODE", "99");
			} finally {
				if(is != null) {is.close();}
				if(br != null) {br.close();}

				//return 암호화
				resultRtn = encrypt(setJsObj.toString());
			}
			return resultRtn;
	    }
	    
	    /**
	     *  GSC VOC I/F Rest-post 암호화 전달
	     *  2024.04.25 NP847
	     * @param dto
	     * @param endpoint
	     * @param dRow
	     * @param inCharSet
	     * @param outCharSet
	     * @return
	     * @throws Exception
	     */
	    private String[] VOC_Rest(XcommonDto dto, String endpoint, int dRow, String inCharSet, String outCharSet) throws Exception {
	    	logger.debug("## CUST VOC REST API START !! ##");
	    	
	    	DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
	    	DataSet ds_ifdata = dto.getDataSet("INTERFACE_DATA");
	    	
	    	String sendURL = endpoint + ds_env.getString(0, "REQ_SERVICE_METHOD");
	    	
	    	//voc-if json 방식
	    	JSONObject jsonObj = new JSONObject();
	    	JSONArray jsonArr = new JSONArray();
	    	JSONObject reqJsonObj = new JSONObject();
	    	
	    	
	    	String sdataKey = "";		// json key값
	    	String sdataVal = "";		// json value값
	    	String jsonStr = "";			// json to string 전송
	    	
	    	try {
	    		if(ds_ifdata != null) {
					for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
						/* 데이터 확인용 
						logger.debug("data Name : " +ds_ifdata.getColumn(i).getName() );
						logger.debug("data Value : " +ds_ifdata.getString(dRow, i) );
						 */
						
						sdataKey = ds_ifdata.getColumn(i).getName();
						sdataVal = ds_ifdata.getString(dRow, i);
						
						jsonObj.put(sdataKey, sdataVal);
						
					}
					jsonArr.add(jsonObj);
					reqJsonObj.put("voc_list", jsonArr);
		    	}
			} catch (Exception e) {
				logger.debug("data setting Exception ::" + e.getMessage());
			}
	    	
	    	jsonStr = reqJsonObj.toJSONString();
	    	//logger.debug("jsonStr :" + jsonStr);
	    	String[] ret = VocPostCall(sendURL, jsonStr, inCharSet, outCharSet);
	    	
	    	//운영이 아니면
	    	if(!ComUtil.isProd()) {
	    		dto.getUserTempMap().put("sendUrl", sendURL);
	    		dto.getUserTempMap().put("sendData", jsonStr);
	    		dto.getUserTempMap().put("resultData", ret[2]);
	    	}

	    	
	    	return ret;
	    	
	    }
	    
	    /**
		 *  VOC I/F post전송
		 *   2024.04.29 NP847
		 *  I/F Type : REST API 
		 *  Data Type : Json
		 *  REQ,RES Type : POST
		 * */
		private String[] VocPostCall(String sendurl, String jsonStr,  String inCharSet, String outCharSet ) throws IOException {
			String startTime = ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss");
			logger.debug("######## VOC Interface Call Start Time ========> " + startTime);
			
			HttpURLConnection conn = null;
			BufferedReader in = null;
			OutputStream os = null;
			String inputLine = "";
			StringBuffer outResult = new StringBuffer();
			String[] ret = new String[3];
			
			try {
				//url setting
				URL url = new URL(sendurl);
				//api connection setting 
				conn = (HttpURLConnection) url.openConnection();
				
				//API HTTP Connection header, method 등 setting
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json; " + inCharSet);
				conn.setRequestProperty("Authorization", "Bearer " + Const.GSC_VOC_ACCESS_TOKEN);
				conn.addRequestProperty("Accept", "application/json");
				conn.setDoOutput(true);
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(30000);
				
				//connection outstream setting 및 통신
				os = conn.getOutputStream();
				os.write(jsonStr.getBytes("UTF-8"));
				os.flush();
				
				//return 결과 읽기
				in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				
				logger.debug("HTTP Connection Status :: " + conn.getResponseCode());
				
				//responseCode :: 201 or 200
				if(conn.getResponseCode() == HttpURLConnection.HTTP_CREATED || conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					//result data stringbuffer setting
					while((inputLine = in.readLine()) != null) {
						outResult.append(inputLine);
					}
					
					//POST 통신은 200이 아니라 201로 와서 200으로 셋팅
					if(outResult.length() > 0) {
						ret[0] = "200";
						ret[1] = "HTTP_OK";
						ret[2] = outResult.toString();
					} else {
						ret[2] = "No return data";
					}
					
					logger.debug("[VOC httpCall]" +"["+ret[2]+"]");
				}
				
				conn.disconnect();
				in.close();
				os.close();
				
			} catch (ProtocolException pe) {
				logger.debug("ProtocolException !! :: " + pe.getMessage());
				ret[2] = "No return data[Protocol Exception]";
			} catch(IOException ie) {
				logger.debug("IOException !! :: " + ie.getMessage());
				ret[2] = "No return data[IOException]";
			} catch(Exception e) {
				logger.debug("Exception !! :: " + e.getMessage());
				ret[2] = "No return data[Exception]";
			} finally {
				if(conn != null) {try {conn.disconnect();} catch (Exception e2) {logger.debug("connection Exception :: " + e2.getMessage());}}
				if(in != null) {try {in.close();} catch (Exception e2) {logger.debug("BufferReader Exception ::" + e2.getMessage());}}
				if(os != null) {try {os.close();} catch (Exception e2) {logger.debug("outputStream Exception ::" + e2.getMessage());}}
				if(outResult != null) {try {outResult.delete(0, outResult.length());} catch (Exception e2) {logger.debug("StringBuffer Exception ::" + e2.getMessage());}}
			}
			
			
			
			/*
			HttpsClient httpsclient = new HttpsClient();				
			HttpClient httpclient = httpsclient.getNewHttpClient();
			StringEntity strParm = null;
			HttpEntity reqEntity = null;
	        HttpEntity   resEntity = null;
	        String[] ret = new String[3];
	        
	        InputStream iis = null;
	        ByteArrayOutputStream bos = null;
	        
	        try {
	        	HttpParams params = httpclient.getParams();
	        	HttpConnectionParams.setConnectionTimeout(params, 30000);
	        	HttpConnectionParams.setSoTimeout(params, 30000);

	        	//entity json set
	        	reqEntity = new ByteArrayEntity(jsonStr.getBytes());
	        	strParm = new StringEntity(jsonStr);
	            strParm.setContentEncoding(inCharSet);
	            
	        	//post 메소드 URL 생성
	            HttpPost httppost = new HttpPost(sendurl);
	            httppost.setEntity(reqEntity);
	            httppost.addHeader("Authorization", "Bearer " + Const.GSC_VOC_ACCESS_TOKEN);
	            httppost.addHeader("Content-Type", "application/json");
	            
	            HttpResponse response = httpclient.execute(httppost);   
	            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
	            ret[1] = response.getStatusLine().getReasonPhrase();            
	            
	            logger.debug("Http Response0 ::" + ret[0]);
	            logger.debug("Http Response1 ::" + ret[1]);
	            

	            if("200".equals(ret[0]) || "201".equals(ret[0])) {
	                int totlen = 0;
	                int len = 0;
		            resEntity = response.getEntity();
//		            ret[2] = EntityUtils.toString(resEntity); ==> 이게 되면 좋은데... 이상하게 exception 떨어진다.. 짜증....
		            iis = resEntity.getContent();
		            bos = new ByteArrayOutputStream();
		            byte[] packet = new byte[2048];
		            
		            if(iis!=null) {
			            while (true) {
			            	len = iis.read(packet, 0, packet.length);
			                if(len > 0) {
			                	totlen = totlen + len;
			                	bos.write(packet,0,len);
			                }
			                if(len <= 0) {
			                	break;
			                }
			            }
			            logger.debug("[outCharSet]["+outCharSet+"]");		            
			            ret[2] = new String(bos.toByteArray(), outCharSet);
			            logger.debug("[outCharSet_1]["+outCharSet+"]");
		            } else {
		                ret[0] = "201";
		                ret[1] = "응답받은 데이터가 없습니다.";
		            	ret[2] = "No return data";
		            }

	            } else {
		            ret[2] = "No return data";
	            }
	            logger.debug("[resEntity]["+resEntity+"]");
	            logger.debug("[httpcall]["+ret[2]+"]");            
	        } catch (IOException ex) {
	            throw ex;
	        } catch (Exception e) {
				e.printStackTrace();
			}
	        finally {
	        	
	        	try {if(iis!=null) iis.close();}catch(Exception e){e.printStackTrace();}
	        	try {if(bos!=null) bos.close();}catch(Exception e){e.printStackTrace();}
	            httpclient.getConnectionManager().shutdown();

	        }
	        */
				
	        return ret;
	    }
	    
}
