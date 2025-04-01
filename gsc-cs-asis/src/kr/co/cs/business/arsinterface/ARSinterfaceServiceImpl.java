package kr.co.cs.business.arsinterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.digester.substitution.VariableAttributes;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataTypes;
import com.tobesoft.xplatform.data.Variable;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.data.datatype.DataType;

import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.persistence.dao.CommonDao;

public class ARSinterfaceServiceImpl implements ARSinterfaceService {
	
	private final static Logger logger = LogManager.getLogger("process.if");
	private final static Logger extlogger = LogManager.getLogger("process.ext");
	
	 
	private CommonDao commonDao = null;

	private String INTERFACE_URL = "XinterfaceAction.do?method=CommonInterface";
/*	
	private String TEST_WAS_END_POINT = "http://203.245.82.121:8088/gscwas/was/";
	private String TEST_SAP_END_POINT = "http://203.245.65.176:48899/WSCSC.asmx";
	public static final String SAP_END_POINT = "http://203.245.89.15:48899/WSCSC.asmx";  //SAP
	public static final String WAS_END_POINT = "http://203.245.82.207:8030/gscwas/was/"; //CAPTIVE
*/	 
//commit  TEST		
	
	private String WAS = "WAS";
	private String SAP = "SAP";
	private String FAX = "FAX";
	private String SELECT = "SELECT";
	private String INSERT = "INSERT";
	private String UPDATE = "UPDATE";
	private String DELETE = "DELETE";
	private String PROCEDURE = "PROCEDURE";
	private String PROCEDURE_OUT = "PROCEDURE_OUT";
	
	private String INTERFACE_ENV = "INTERFACE_ENV";
	private String INTERFACE_DATA = "INTERFACE_DATA";
	
	private String sqlid_select = "CallMethod";
	private String sqlid_insert = "CallMethod";
	private String sqlid_update = "CallMethod";
	private String sqlid_delete = "CallMethod";
	
	private String IF001 = "IF001";										//주유소조회
																		//IF002 : 유종조회		
	private String IF003 = "IF003";										//인도처조회
	private String IF003_SAP_ID = "CallCSC_SAPSHPDPT_RNI";
	private String IF003_SAP_RESULT = "CallCSC_SAPSHPDPT_RNIResult";
																		//IF004 : 저유소조회		
	private String IF005 = "IF005";										//차량번호조회
	private String IF005_SAP_ID = "CallCSC_SAPVEHINF_RNI";
	private String IF005_SAP_RESULT = "CallCSC_SAPVEHINF_RNIResult";
	
	private String IF006 = "IF006";										//주문요청
	private String IF006_SAP_ID = "CallCSC_SAPORDREQ_RNE";
	private String IF006_SAP_RESULT = "CallCSC_SAPORDREQ_RNEResult";
	
	private String IF007 = "IF007";										//입금정보조회
	private String IF007_SAP_ID = "CallCSC_SAPRCTINF_RNI";
	private String IF007_SAP_RESULT = "CallCSC_SAPRCTINF_RNIResult";
	
	private String IF008 = "IF008";										//주문내역조회,배차배송조회	
	private String IF008_SAP_ID = "CallCSC_SAPARSRMK_RNI";																	
	private String IF008_SAP_RESULT = "CallCSC_SAPARSRMK_RNIResult";
	
	private String IF009 = "IF009";										//주문내역조회,배차배송조회 팩스
	private String IF009_SAP_ID = "CallCSC_SAPARSRMK_RNI";
	private String IF009_SAP_RESULT = "CallCSC_SAPARSRMK_RNIResult";
	
	private String IF010 = "IF010";										//입금예상금액안내
	private String IF010_SAP_ID = "CallCSC_SAPRCTRMK_RNI";
	private String IF010_SAP_RESULT = "CallCSC_SAPRCTRMK_RNIResult";
																		//IF011 : 고객담당SR조회
	private String IF012 = "IF012";										//주문변경
	private String IF012_SAP_ID = "CallCSC_SAPARSORDCHG_RNI";
	private String IF012_SAP_RESULT = "CallCSC_SAPARSORDCHG_RNIResult";
	
	private String IF013 = "IF013";										//고객포인트조회
	private String IF013_URL = "arsPtCustptSelC02.do";
																		//IF014 : 성명재녹음
	private String IF015 = "IF015";										//주유내역,행사포인트사용내역 팩스발송
																		//IF016 : SMS발송요청
	private String IF017 = "IF017";										//비밀번호 인증/변경
	private String IF017_URL = "arsCrCrdpwUpdC01.do";
	
	private String IF018 = "IF018";										//약관팩스발송
	
	private String IF019 = "IF019";			
	private String IF019_URL = "arsCmCustSelC02.do";					//고객조회
	
	private String IF021 = "IF021";
	private String IF021_URL = "arsCrCrdregChkC02.do";					//카드등록가능여부체크
		
	private String IF022 = "IF022";										//통합고객동의
	// private String IF022_URL = "arsCmCustInsC04.do";					//주민번호->사업자번호 조회
	private String IF022_URL = "arsCmCustInsC05.do";					//ARS 카드재발급>합고객동의
	
	private String IF026 = "IF026";										//약관동의정보등록수정
	private String IF026_URL = "cmInfoagrInsC01.do";	
	
	private String IF031 = "IF031";										//본인인증여부등록
	private String IF031_URL = "cmCautIudC03.do";	
	
	private String IF033 = "IF033";										//고객조회(휴대전화번호,생년월일 2개의 값으로 조회)
	private String IF033_URL = "arsCmCustSelC04.do";

	private String IF034 = "IF034";										//제휴카드등록가능여부조회-2013-01-29 이광호 추가
	private String IF034_URL = "crTregcrdSelC03.do";
	
	//test

	private String IF035 = "IF035";										//제휴카드사용등록-2013-01-29 이광호 추가
	private String IF035_URL = "crTregcrdInsC01.do";
	
	private String IF037 = "IF037";										//ARS 휴대폰제약조건 체크 2015-01-13 추가
	private String IF037_URL = "arsCmCuikTphnChkC01.do";

	private String IF_OIL = "IF_OIL";									//고객주유내역조회
	private String IF_OIL_URL = "arsPtCustptSelC23.do";	
	
	private String IF_PNT = "IF_PNT";									//고객행사포인트내역조회
	private String IF_PNT_URL = "arsPtCustptSelC24.do";	
	
	private String IF_KIND = "IF_KIND";
	
	private String ErrorMsg = "ErrorMsg";
	private String ErrorCode = "ErrorCode";
	
	private String DS_COMMON = "DS_COMMON";
	private String IF_ID = "IF_ID";
	
	/* 운영시 수정해야함 */
	private String TEST_FAX_DB_ACCOUNT 	= "sa";
	private String TEST_FAX_DB_PASSWORD = "1234";
	private String REAL_FAX_DB_ACCOUNT 	= "db2admin";
	private String REAL_FAX_DB_PASSWORD = "db2admin";
	private String TEST_FAX_DB_URL = "jdbc:sqlserver://192.168.16.208:1433;DatabaseName=WISEImage4";
	private String REAL_FAX_DB_URL = "jdbc:sqlserver://192.168.16.113:1433;DatabaseName=WISEImage4";
	
	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;  
	}  
 

	public void ARSTransaction(XcommonDto dto) throws SQLException,
			Exception {
	}

	public void ARSUserTransaction(XcommonDto dto) throws SQLException,	Exception {
		String[] ret;
		DataSet Ds = null;	
		Ds = dto.getDataSet("INTERFACE_ENV");	//환경설정 데이터셋 

		logger.debug("===================REQUEST=================");
		//System.out.println("===================REQUEST=================");
		for(int i = 0 ; i < dto.getVlistCount() ; i++){
			logger.debug("Parameter : " + dto.getVlist().get(i).getName() + " \t");
			logger.debug("P Value : " + dto.getVariableValue(i));
			//System.out.println("P Value : " + dto.getVariableValue(i));
		}
		for(int i = 0 ; i < dto.getDslist().size() ; i++){
			logger.debug("Dataset : " + dto.getDslist().get(i).getName());
			//System.out.println("Dataset : " + dto.getDslist().get(i).getName());			
			for(int  j = 0 ; j < dto.getDslist().get(i).getColumnCount() ; j++){
				logger.debug("Column ID : " + dto.getDslist().get(i).getColumn(j).getName() + "\t");
				//System.out.println("Column ID : " + dto.getDslist().get(i).getColumn(j).getName() + "\t");
				for(int k = 0 ; k < dto.getDslist().get(i).getRowCount() ; k++){					
					logger.debug("Value : " + dto.getDslist().get(i).getString(k, j));
					//System.out.println("Value : " + dto.getDslist().get(i).getString(k, j));
				}	
			}
		}
		logger.debug("===========================================");
		//각 인터페이스  ID에 따른 처리방법 분기
		/*
		 * WAS : 칼텍스 WAS
		 * SAP : 칼텍스 SAP
		 * FAX : SAP이나 WAS를 통하지않는 FAX(IF018)
		 * 통합CRM
		 * 1. SELECT
		 * 2. INSERT
		 * 3. UPDATE
		 * 4. DELETE
		 */	 
		String intfId = Ds.getString(0, IF_KIND); //인터페이스 ID
		//통합CRM DB 조회
		if(SELECT.equals(intfId)) 
		{
			Ds = inputSet(dto.getDataSet(INTERFACE_DATA), intfId); //조회별 특수상황 세팅
			logger.debug(dto.getRowMap(Ds, 0));
			String sqlid = dto.getVariableValue(sqlid_select);
			List list = commonDao.selectList(sqlid, dto.getRowMap(Ds, 0));
			logger.debug("List size : " + list.size());
			if(list.size()>0) {					
				dto.getOutdslist().add(dto.setDataSet(list, INTERFACE_DATA));
			}
			//고객번호로 조회시 데이터가 n개일때 처리
			if(IF001.equals(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID))){
				if(list.size() > 1){
					dto.getOutvlist().set(ErrorMsg, "데이터 다건조회.");				
					dto.getOutvlist().set(ErrorCode, "10");
				}			
			}
			
			//2013.12.23 악성고객 코드조회 추가 START
			if("IF036".equals(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID))){
				/*
				 *	조회된 row 없으면 A - 기존 시나리오
				 *  조회된 row 1건이면 B,C - 악성고객 멘트 후 등급에 따라 상담사 또는 불만담당자 연결
				 *	조회된 row 2이상이면  무조건C - 악성고객 멘트 후 불만담당자연결
				*/ 
				if(list.size() == 0){
					
					HashMap<String,Object> hashMap = new HashMap<String,Object>();
					hashMap.put("BL_CD", "A");
					
					List<HashMap<String,Object>> tmpList = new ArrayList<HashMap<String,Object>>();
					tmpList.add(hashMap);
					
					dto.getOutdslist().add(dto.setDataSet(tmpList, INTERFACE_DATA));
					
				/*}else if(list.size() > 1){

					dto.getOutdslist().clear();
					
					HashMap<String,Object> hashMap = new HashMap<String,Object>();
					hashMap.put("BL_CD", "C");
					
					List<HashMap<String,Object>> tmpList = new ArrayList<HashMap<String,Object>>();
					tmpList.add(hashMap);

					dto.getOutdslist().add(dto.setDataSet(tmpList, INTERFACE_DATA));
				*/	
				}else{
					
					String blCdData = dto.getOutdslist().get(0).getString(0, dto.getOutdslist().get(0).getColumn(0).getName().toString()); 
					
					if("1".equals(blCdData)||"2".equals(blCdData)){
						dto.getOutdslist().get(INTERFACE_DATA).set(0, "BL_CD", "B");
					}else if("3".equals(blCdData)){
						dto.getOutdslist().get(INTERFACE_DATA).set(0, "BL_CD", "C");
					}
				}
			}
			//2013.12.23 악성고객 코드조회 추가 END
		}
		else if(INSERT.equals(intfId))
		{
			Ds = inputSet(dto.getDataSet(INTERFACE_DATA), intfId); //조회별 특수상황 세팅
			String sqlid = dto.getVariableValue(sqlid_insert);
			//extlogger.debug("sqlid:::"+sqlid);
			//System.out.println("==========>"+sqlid);
			int k = 0;
			
			if (Ds != null) {
				for(k = 0 ; k < Ds.getRowCount(); k++) {
					//logger.debug(sqlid+"//////////"+dto.getRowMap(Ds, k));
					//System.out.println(dto.getRowMap(Ds, k));
					extlogger.debug("dto.getRowMap:::"+dto.getRowMap(Ds, k));
					//logger.debug("시작시간 [" + new Date() +"]");
					commonDao.insert(sqlid, dto.getRowMap(Ds, k));
					//logger.debug("종료시간 [" + new Date() +"]");
				}
			}
		}
		else if(UPDATE.equals(intfId))
		{
			Ds = inputSet(dto.getDataSet(INTERFACE_DATA), intfId); //조회별 특수상황 세팅
			String sqlid = dto.getVariableValue(sqlid_update);
			if(Ds != null) {
				for(int k=0; k < Ds.getRowCount(); k++) {
					logger.debug(dto.getRowMap(Ds, k));
					commonDao.update(sqlid, dto.getRowMap(Ds, k));
				}
			}
		}
		else if(DELETE.equals(intfId))
		{
			Ds = inputSet(dto.getDataSet(INTERFACE_DATA), intfId); //조회별 특수상황 세팅
			String sqlid = dto.getVariableValue(sqlid_delete);
			
			if(Ds != null) {
				for(int k=0; k < Ds.getRowCount(); k++) {		
					logger.debug(dto.getRowMap(Ds, k));
					commonDao.delete(sqlid, dto.getRowMap(Ds, k));
				}	
			}
		}			

		//칼텍스 SAP
		else if(SAP.equals(intfId))
		{
			//ERP 재구축 관련 시작
			Ds = dto.getDataSet("INTERFACE_DATA");
			String gubun = "";
			
			if("IF007".equals(Ds.getString(0, "IF_ID"))){
				
				Integer.parseInt(Ds.getString(0, "BUDAT"));
				if(Integer.parseInt(Ds.getString(0, "BUDAT")) < Integer.parseInt(Const.SAP_ERP_ESTABLISH_DATE)){
					gubun = "46C";
				}
			}else if("IF008".equals(Ds.getString(0, "IF_ID"))){
				
				/*
				 * IF008(CallCSC_SAPARSRMK_RNI)서비스는 주문진행정보/배차배송조회 두가지 기능을 하는 서비스.
				 * 처음에는 TCODE에 데이터를 TR009(주문진행정보)/TR010(배차배송정보) 로 구분을 해서 사용을 하려 했던것으로 보임.
				 * 4.6C 서버에서는 TCODE를 TR009/TR010 둘다 뒤의 두자리를 자르고 TR0 으로 받고 있었음. 
				 * ERP 재구축 프로젝트로  개발계 SAP 6C 서버에 TCODE 를 5자 보내면 Error 확인됨
				 * 4.6C 에서는 SAP Adaptor을 사용하고 6C서버는 WCF Adaptor을 사용하여 6C서버에서는 5자리 보낸것을 3자리로 자르는 작업 불가능
				 * SAP 에 확인결과 TCODE는 RFC 실행에 아무런 영향을 주지 않는다는 답변확인.
				 * TTYPE 01/02/03 을 기준으로 구분되어 실행한다고 답변확인.
				 * 따라서 아래는 6C 서버에서 에러 나지 않도록 TCODE를 3자리로 자르는 작업을 함.
				 * 하지만 아래 TCODE의 데이터는 SAP에서는 전혀 사용하지 않는 의미 없는 데이터임. 
				 */
				if(Ds.getString(0, "TCODE").length()!=0){
					Ds.set(0, "TCODE", Ds.getString(0, "TCODE").substring(2, 5));
				}
				
				//Ds.set(0, "TCODE", Ds.getString(0, "TCODE").substring(2, 5));

				if(!"".equals(Ds.getString(0,"ERDAT"))){
					if(Integer.parseInt(Ds.getString(0,"ERDAT"))< Integer.parseInt(Const.SAP_ERP_ESTABLISH_DATE)) {
						gubun = "46C";
					}
				}
			}
			
			if("46C".equals(gubun)){
				if(ComUtil.isProd()){
					ret = this.SAPString(dto, Const.SAP_46C_END_POINT);
				}else{
					ret = this.SAPString(dto, Const.TEST_SAP_46C_END_POINT);
				}
			}else{
				if(ComUtil.isProd()){
					ret = this.SAPString(dto, Const.SAP_END_POINT);
				}else{
					ret = this.SAPString(dto, Const.TEST_SAP_END_POINT);
				}
				
				//주문번호로 주문내역 조회시 6C서버에 주문내역이 없으면 46C로 재조회하기 위해 만듬
				if(IF008.equals(Ds.getString(0, IF_ID)) && "200".equals(ret[0])){
					Element root = makeDom(ret[2]);
					String headers = getHeader(Ds.getString(0, IF_ID));	
					String records = getRecord(Ds.getString(0, IF_ID)); //인터페이스 ID로 키 값 가져옴
					
					DataSet dataSet = new DataSet();
					Element element = (Element)root.getElementsByTagName(headers).item(0);  //원본
					dataSet = xmlToDataSet(element, DS_COMMON, records);
					
					if("ES004".equals(dataSet.getString(0, "RET_CODE"))){
						if(ComUtil.isProd()){
							ret = this.SAPString(dto, Const.SAP_46C_END_POINT);
						}else{
							ret = this.SAPString(dto, Const.TEST_SAP_46C_END_POINT);
						}
					}
				}
			}
			
			//ERP 재구축 관련  종료
			
			//운영-개발 분기
			/*
			if(ComUtil.isProd()){
				ret = this.SAPString(dto, Const.SAP_END_POINT);
			}else{
				ret = this.SAPString(dto, Const.TEST_SAP_END_POINT);
			}
			*/
			if("200".equals(ret[0])) {
				setOutputDataset(dto, ret);
			} else {		
				setErrorData(dto, ret);
			}
		}		
		//칼텍스 WAS 
		else if(WAS.equals(intfId))
		{			
			DataSet ds_ifdata = dto.getDataSet(INTERFACE_DATA);
						//String endPoint = Const.WAS_END_POINT + getURL(ds_ifdata.getString(0, IF_ID));
			//개발,운영계 여부에 따라 GSC와 I/F하는 서버 변경 START		2012.11.14
			String endPoint ="";
			if(ComUtil.isProd()){
				endPoint = Const.WAS_END_POINT + getURL(ds_ifdata.getString(0, IF_ID));
			}else{				
				endPoint = Const.TEST_WAS_END_POINT + getURL(ds_ifdata.getString(0, IF_ID));					
			}
			
			logger.debug("WAS_END_POINT : " + endPoint);
			//개발,운영계 여부에 따라 GSC와 I/F하는 서버 변경 END		2012.11.14
			
			//여기서 https분기 처리 해줘야 한다.
			
			
			ret = wasCall(dto, endPoint, 0, HTTP.UTF_8, "euc-kr");
			logger.debug(" ret[0] : " + ret[0]);
			logger.debug(" ret[1] : "  + ret[1]);
			logger.debug(" ret[2] : "  + ret[2]);
			if("200".equals(ret[0])) {
				setOutputDataset(dto, ret);
			} else {		
				setErrorData(dto, ret);
			}     
		}
		else if(FAX.equals(intfId)){
			logger.debug("======FAX In======");
			DataSet ds_ifdata = dto.getDataSet(INTERFACE_DATA);
			if(IF015.equals(ds_ifdata.getString(0, IF_ID))){			//주유내역,행사포인트사용내역일때				
				dto = setFaxDto(dto);									//내부적으로 두번돌게 처리				
			}else if(IF018.equals(ds_ifdata.getString(0, IF_ID))){	//약관, 상품권일때	
				sendFax(dto, ds_ifdata.getString(0, IF_ID));			//바로 팩스로 보냄
			}else if(IF009.equals(ds_ifdata.getString(0, IF_ID))){	//주문조회, 배차배송조회일때
				
				
				//SAP ERP재구축에 의해 추가 START
				if(ds_ifdata.getString(0, "TCODE").length()!=0){
					ds_ifdata.set(0, "TCODE", ds_ifdata.getString(0, "TCODE").substring(2, 5));
					System.out.println(ds_ifdata.getString(0, "TCODE"));
				}
				//SAP ERP재구축에 의해 추가 END
				
				if(ComUtil.isProd()){
					ret = this.SAPString(dto, Const.SAP_END_POINT);
				}else{
					ret = this.SAPString(dto, Const.TEST_SAP_END_POINT);
				}
				
				if("200".equals(ret[0])) {
					setOutputDataset(dto, ret);
				} else {		
					setErrorData(dto, ret);
				}
			}
		}

	}

	public void ARSNonTransaction(XcommonDto dto) throws SQLException,
			Exception {


	}
	
	private XcommonDto setFaxDto(XcommonDto dto) throws Exception{	//IF015 세팅을 위해 사용
		//주유내역 조회후 행사포인트사용내역 조회
		DataSet ds_data = dto.getDataSet(INTERFACE_DATA); //최초입력받은 값이 들어있는 데이터셋 가져옴
		
		//1. 새로운 DTO를 생성하여  행사포인트사용내역을 조회함
		//2. 새로운 DTO를 생성하여  주유내역을 조회함
		XcommonDto oilDto = new XcommonDto(); //새로운 DTO생성
		XcommonDto pntDto = new XcommonDto(); //새로운 DTO생성
		
		//1. 행사포인트사용내역조회
		Variable v = Variable.createVariable("CallMethod", "FAXCall");
		pntDto.setAddExternalVariable(v);
				
		//INTERFACE_ENV 데이터셋 생성
		DataSet ifEnv = new DataSet("INTERFACE_ENV");
		ifEnv.addColumn(IF_KIND, DataTypes.STRING, 2000);
		ifEnv.addColumn("REQ_SERVICE_METHOD", DataTypes.STRING, 2000);
		ifEnv.addColumn("REQ_SERVICE_ID", DataTypes.STRING, 2000);
		ifEnv.addColumn("RES_HEADER_SECTION", DataTypes.STRING, 2000);
		ifEnv.addColumn("RES_COLUMN_SECTION", DataTypes.STRING, 2000);
		ifEnv.addColumn("RES_RECORD_SECTION", DataTypes.STRING, 2000);
		ifEnv.newRow(0);
		ifEnv.set(0, IF_KIND, "FAX");
		
		//INTERFACE DATA 데이터셋 생성
		DataSet ifData = new DataSet(INTERFACE_DATA);
		ifData.addColumn(IF_ID, DataTypes.STRING, 2000);
		ifData.addColumn("req_chnl_code", DataTypes.STRING, 2000);
		ifData.addColumn("input_user_id", DataTypes.STRING, 2000);
		ifData.addColumn("input_user_nm", DataTypes.STRING, 2000);
		ifData.addColumn("input_user_ip", DataTypes.STRING, 2000);
		ifData.addColumn("in_cust_no", DataTypes.STRING, 2000);
		ifData.addColumn("in_strt_dt", DataTypes.STRING, 2000);
		ifData.addColumn("in_end_dt", DataTypes.STRING, 2000);
		ifData.newRow(0);
		ifData.set(0, IF_ID, "IF_PNT");
		ifData.set(0, "req_chnl_code", "610060");
		ifData.set(0, "input_user_id", "online");
		ifData.set(0, "input_user_nm", "ARS");
		ifData.set(0, "input_user_ip", "192.168.9.208");
		ifData.set(0, "in_cust_no", ds_data.getString(0, "in_cust_no"));
		ifData.set(0, "in_strt_dt", ds_data.getString(0, "in_strt_dt"));
		ifData.set(0, "in_end_dt", ds_data.getString(0, "in_end_dt"));
		
		//데이터셋을 DTO에 add
		pntDto.setAddExternalDataset(ifEnv);
		pntDto.setAddExternalDataset(ifData);
		
		
		String[] ret;
		
        if(ComUtil.isProd()){
            ret = wasCall(pntDto, Const.WAS_END_POINT + IF_PNT_URL , 0, HTTP.UTF_8, "euc-kr");
        }else{
            ret = wasCall(pntDto, Const.TEST_WAS_END_POINT + IF_PNT_URL , 0, HTTP.UTF_8, "euc-kr");
        }		
		
		
		//이 상태에서 행사포인트내역 조회되었음 ret에 가지고있음 
		logger.debug("Point History : " + ret[2] );
		if("200".equals(ret[0])){ //정상송신이면
			//행사포인트사용내역 outdataset 만들고
			setOutputDataset(pntDto, ret);
			
			//주유내역조회
			logger.debug("======OIL History Start=====");
			//새로운 DTO생성
			oilDto.setAddExternalVariable(v);
			
			//ifenv는 위에 생성된것과 같으므로 기존거 사용, 데이터 부분만 생성
			DataSet ifOilData = new DataSet(INTERFACE_DATA);
			
			ifOilData.addColumn(IF_ID, DataTypes.STRING, 2000);
			ifOilData.addColumn("req_chnl_code", DataTypes.STRING, 2000);
			ifOilData.addColumn("input_user_id", DataTypes.STRING, 2000);
			ifOilData.addColumn("input_user_nm", DataTypes.STRING, 2000);
			ifOilData.addColumn("input_user_ip", DataTypes.STRING, 2000);
			ifOilData.addColumn("in_cust_no", DataTypes.STRING, 2000);
			ifOilData.addColumn("in_strt_dt", DataTypes.STRING, 2000);
			ifOilData.addColumn("in_end_dt", DataTypes.STRING, 2000);
			ifOilData.addColumn("in_search_key", DataTypes.STRING, 2000);
			ifOilData.addColumn("in_crd_no", DataTypes.STRING, 2000);
			ifOilData.newRow(0);
			ifOilData.set(0, IF_ID, "IF_OIL");
			ifOilData.set(0, "req_chnl_code", "610060");
			ifOilData.set(0, "input_user_id", "online");
			ifOilData.set(0, "input_user_nm", "ARS");
			ifOilData.set(0, "input_user_ip", "192.168.9.208");
			ifOilData.set(0, "in_cust_no", ds_data.getString(0, "in_cust_no"));
			ifOilData.set(0, "in_strt_dt", ds_data.getString(0, "in_strt_dt"));
			ifOilData.set(0, "in_end_dt", ds_data.getString(0, "in_end_dt"));
			ifOilData.set(0, "in_search_key", ds_data.getString(0, "in_search_key"));								
			ifOilData.set(0, "in_crd_no", ds_data.getString(0, "in_crd_no"));									
			
			//데이터셋을 DTO에 add
			oilDto.setAddExternalDataset(ifEnv);
			oilDto.setAddExternalDataset(ifOilData);
			
			//주유내역조회를 위한 컬럼세팅완료
			
			String[] ret2 = null;
	        if(ComUtil.isProd()){
	            ret2 = wasCall(oilDto, Const.WAS_END_POINT + IF_OIL_URL , 0, HTTP.UTF_8, "euc-kr");
	        }else{
	            ret2 = wasCall(oilDto, Const.TEST_WAS_END_POINT + IF_OIL_URL , 0, HTTP.UTF_8, "euc-kr");	            
	        }       
	        			
			
			logger.debug("OIL HISTORY : " + ret2[2]);
			if("200".equals(ret2[0])){//정상송신이면
				setOutputDataset(oilDto, ret2);				
			}
		}
		//여기까지 행사내역+주유내역 전부 조회완료된 상태(pntDto,oilDto의 INTERFACE_DATA_RESULT에 담겨있음)
		//두 dto를 합쳐 하나의 dto를 생성(sendFax 메소드에 보내기위해)
		XcommonDto faxDto = new XcommonDto();
		faxDto.setAddExternalVariable(v);
		
		DataSet pntOut = pntDto.getDataSet("INTERFACE_DATA_RESULT");
		DataSet oilOut = oilDto.getDataSet("INTERFACE_DATA_RESULT");
		
		//합쳐질 데이터를 넣을 데이터셋 생성
		DataSet sumData = new DataSet(INTERFACE_DATA);
		//컬럼생성
		sumData.addColumn(IF_ID, DataTypes.STRING, 2000);					//인터페이스ID
		sumData.addColumn("FAX_NO", DataTypes.STRING, 2000);				//FAX NO
		sumData.addColumn("CUST_NM", DataTypes.STRING, 2000);				//고객명명
		sumData.addColumn("HEAD_RS_BZ_NO", DataTypes.STRING, 2000);			//앞주민등록번호
		sumData.addColumn("TAIL_RS_BZ_NO", DataTypes.STRING, 2000);			//뒤주민등록번호
		sumData.addColumn("SALE_DTIME", DataTypes.STRING, 2000);			//주유일시
		sumData.addColumn("CRD_NO", DataTypes.STRING, 2000);				//카드번호
		sumData.addColumn("FRCH_NM", DataTypes.STRING, 2000);				//주유소명
		sumData.addColumn("SPCL_PT_NM", DataTypes.STRING, 2000);			//행사명
		sumData.addColumn("TR_OCUR_RSN_NM", DataTypes.STRING, 2000);		//사유
		sumData.addColumn("AMT", DataTypes.STRING, 2000);					//주유금액
		sumData.addColumn("PNT", DataTypes.STRING, 2000);					//적립포인트
		sumData.addColumn("TOTAL_PNT", DataTypes.STRING, 2000);					//적립포인트
		for(int i = 0 ; i < pntOut.getRowCount() ; i++){	//행사포인트내역부터 insert
			sumData.newRow(i);
			sumData.set(i, IF_ID, IF015);
			sumData.set(i, "FAX_NO", ds_data.getString(0, "in_fax_no"));
			sumData.set(i, "CUST_NM", pntOut.getString(i, "CUST_NM"));
			sumData.set(i, "HEAD_RS_BZ_NO", pntOut.getString(i, "HEAD_RS_BZ_NO"));
			sumData.set(i, "TAIL_RS_BZ_NO", pntOut.getString(i, "TAIL_RS_BZ_NO"));
			sumData.set(i, "SALE_DTIME", pntOut.getString(i, "SALE_DTIME"));
			sumData.set(i, "CRD_NO", pntOut.getString(i, "CRD_NO"));
			sumData.set(i, "FRCH_NM", "");
			sumData.set(i, "SPCL_PT_NM", pntOut.getString(i, "SPCL_PT_NM"));
			sumData.set(i, "TR_OCUR_RSN_NM", pntOut.getString(i, "TR_OCUR_RSN_NM"));
			if(pntOut.getString(i, "AMT") == null){
				sumData.set(i, "AMT", "");
			}else{
				sumData.set(i, "AMT", pntOut.getString(i, "AMT"));
			}
			sumData.set(i, "PNT", pntOut.getString(i, "SPCL_PT"));
			sumData.set(i, "TOTAL_PNT", ds_data.getString(0, "in_usable_pt"));
		}
		for(int j = 0 ; j < oilOut.getRowCount() ; j++){
			sumData.newRow(pntOut.getRowCount()+j);
			sumData.set(pntOut.getRowCount()+j, IF_ID, IF015);
			sumData.set(pntOut.getRowCount()+j, "FAX_NO", ds_data.getString(0, "in_fax_no"));
			sumData.set(pntOut.getRowCount()+j, "CUST_NM", oilOut.getString(j, "CUST_NM"));
			sumData.set(pntOut.getRowCount()+j, "HEAD_RS_BZ_NO", oilOut.getString(j, "HEAD_RS_BZ_NO"));
			sumData.set(pntOut.getRowCount()+j, "TAIL_RS_BZ_NO", oilOut.getString(j, "TAIL_RS_BZ_NO"));
			sumData.set(pntOut.getRowCount()+j, "SALE_DTIME", oilOut.getString(j, "SALE_DTIME"));
			sumData.set(pntOut.getRowCount()+j, "CRD_NO", oilOut.getString(j, "CRD_NO"));
			sumData.set(pntOut.getRowCount()+j, "FRCH_NM", oilOut.getString(j, "FRCH_NM"));
			sumData.set(pntOut.getRowCount()+j, "SPCL_PT_NM", "");
			sumData.set(pntOut.getRowCount()+j, "TR_OCUR_RSN_NM", oilOut.getString(j, "PAYM_TP_NM"));
			sumData.set(pntOut.getRowCount()+j, "AMT", oilOut.getString(j, "AMT"));
			sumData.set(pntOut.getRowCount()+j, "PNT", oilOut.getString(j, "GNSP_SPCL_PT"));
			sumData.set(pntOut.getRowCount()+j, "TOTAL_PNT", ds_data.getString(0, "in_usable_pt"));
		}//값 세팅완료

		if(sumData.getRowCount() == 0){									//sumData 가 0이면  fax로 보내지않고 종료
			dto.getOutvlist().set(ErrorMsg, "출력할 데이터가 없습니다.");				
			dto.getOutvlist().set(ErrorCode, "-1");
		}else{
			faxDto.setAddExternalDataset(sumData); //dto에 dataset setting
			sendFax(faxDto, faxDto.getDataSet(INTERFACE_DATA).getString(0, IF_ID));	//IF_ID = IF015
		}
		return dto;
	}
	
	private String[] wasCall(XcommonDto dto, String sendURL, int dRow, String inCharSet, String outCharSet) throws Exception 
	{	
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		DataSet ds_ifdata = dto.getDataSet(INTERFACE_DATA);
		List<BasicNameValuePair> sdata = new ArrayList<BasicNameValuePair>();
		for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
			if(!IF_ID.equals(ds_ifdata.getColumn(i).getName()))
			{
				sdata.add(new BasicNameValuePair(ds_ifdata.getColumn(i).getName(), ds_ifdata.getString(dRow, i)==null?"":ds_ifdata.getString(dRow, i)));
			}
			
		}
		logger.debug("send url ============================ " + sendURL);
		logger.debug("send data ============================ " + sdata.toString());		
	
		String[] ret = httpCall(sendURL, sdata, inCharSet, outCharSet);
//		dto.getUserTempMap().put("sendUrl", sendURL);
//		dto.getUserTempMap().put("sendData", sdata.toString());
//		dto.getUserTempMap().put("resultData", ret[0] +" : "+ ret[1] +" : "+ ret[2]);
		
		return ret;
	}
	
	/*
	 * 일반 http 전송
	 * */
	private String[] httpCall(String sendurl, List<BasicNameValuePair> sdata, String inCharSet, String outCharSet) throws IOException {

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
        	
        	logger.debug("Interface In time : ");
        	getCurrentTime();
            HttpResponse response = httpclient.execute(httppost);
            logger.debug("Interface Out time : ");
            getCurrentTime();
            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
            ret[1] = response.getStatusLine().getReasonPhrase();
            
            if("200".equals(ret[0])) {
            	 int totlen = 0;
                 int len = 0;
 	            resEntity = response.getEntity();
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
 		            ret[2] = new String(bos.toByteArray(), outCharSet);
 	            } else {
 	                ret[0] = "201";
 	                ret[1] = "응답받은 데이터가 없습니다.";
 	            	ret[2] = "";
 	            }
            } else {
	            ret[2] = "";
            }
            
        } catch (IOException ex) {
            throw ex;
        }
        finally {
        	
        	try {if(iis!=null) iis.close();}catch(Exception e){}
        	try {if(bos!=null) bos.close();}catch(Exception e){}
        	
            httpclient.getConnectionManager().shutdown();
        }
        return ret;
    }

	 private DataSet xmlToDataSet(Element element, String dsName, String excludePart) {
	    	
	    	DataSet dataset = new DataSet(dsName);
	    	StringBuffer sb = new StringBuffer(100);
	    	logger.debug("====xmlToDataSet=====");
	        if(element != null) {
	        	
	        	String ext = excludePart==null?"":excludePart;
	        	for(Node column = element.getFirstChild(); column != null; column = column.getNextSibling()) {
		            switch(column.getNodeType()) {
			            case Node.ELEMENT_NODE:
			            	if(!"OT_OUT_CURSOR".equals(column.getNodeName()) && !"T_".equals(column.getNodeName().substring(0, 2))){
			            		if(!column.getNodeName().toUpperCase().equals(ext)) {
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
			            	if(!"OT_OUT_CURSOR".equals(column.getNodeName()) && !"T_".equals(column.getNodeName().substring(0, 2))){
				            	if(!column.getNodeName().toUpperCase().equals(ext)) {
				            		dataset.set(0, column.getNodeName().toUpperCase(), column.getTextContent());
				            		sb.append(column.getTextContent());
				            	}
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
	 
		
	private DataSet xmlRecordToDataSet(NodeList nlist, String dsName) {
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
	
	/*
	 * 넘겨받은 xml로 output dataset을 만듬.
	 * */
	private void setOutputDataset(XcommonDto dto, String[] resp) throws Exception {

		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		String ifKind = ds_env.getString(0, IF_KIND); //xml종류
		DataSet ds_data = dto.getDataSet(INTERFACE_DATA);
	
		logger.debug("====XML set====");
		
		//돔에 집어넣기...
		Element root = makeDom(resp[2]);
		NodeList nodelist;
		Element element;

		String headers = "";
		String columns = "";
		String records = "";
		if(SAP.equals(ifKind))
		{
			headers = getHeader(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID));	
			records = getRecord(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID)); //인터페이스 ID로 키 값 가져옴
		}else if(WAS.equals(ifKind) || FAX.equals(ifKind)){
//			records = "OT_OUT_CURSOR"; //칼텍스 전체적용
			headers = getHeader(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID));
			records = getRecord(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID));
		}
		
		
		if(!"".equals(headers)) {	
			element = (Element)root.getElementsByTagName(headers).item(0);  //원본
			if(element!=null){
				if(FAX.equals(ifKind) && !IF009.equals(ds_data.getString(0, IF_ID))){
					logger.debug("External");
					dto.setAddExternalDataset(xmlToDataSet(element, DS_COMMON, records)); //Dataset에 담음(outds X)					
				}
				else{
					logger.debug("Normal");
					dto.getOutdslist().add(xmlToDataSet(element, DS_COMMON, records));
				}
				//에러메세지 통일부
				if(SAP.equals(ifKind)){
					dto.getOutvlist().set(ErrorMsg, dto.getOutdslist().get(DS_COMMON).getString(0, "RET_MSG"));
					dto.getOutvlist().set(ErrorCode, dto.getOutdslist().get(DS_COMMON).getString(0, "RET_CODE"));
				}else if(WAS.equals(ifKind)){
					dto.getOutvlist().set(ErrorMsg, dto.getOutdslist().get(DS_COMMON).getString(0, "OT_RES_MSG"));
					dto.getOutvlist().set(ErrorCode, dto.getOutdslist().get(DS_COMMON).getString(0, "OT_RESPON_CODE"));
				}
			}                                  
		}
		if(!"".equals(columns)) {			
			element = (Element)root.getElementsByTagName(columns).item(0);
			if(element!=null)
				if(FAX.equals(ifKind) && !IF009.equals(ds_data.getString(0, IF_ID))){
					dto.setAddExternalDataset(xmlToDataSet(element, "DS_COLUMN", records));
				}else{
					dto.getOutdslist().add(xmlToDataSet(element, "DS_COLUMN", records));
				}
		}
		if(!"".equals(records)) {
			nodelist = root.getElementsByTagName(records);
			if(nodelist!=null)
				if(FAX.equals(ifKind) && !IF009.equals(ds_data.getString(0, IF_ID))){
					dto.setAddExternalDataset(xmlRecordToDataSet(nodelist, "INTERFACE_DATA_RESULT"));
				}else{
					dto.getOutdslist().add(xmlRecordToDataSet(nodelist, INTERFACE_DATA));
				}
		}
		/*
		 * FAX일경우 FAX전송 
		 */
		if(IF009.equals(ds_data.getString(0, IF_ID))){
			if(dto.getOutdslist().get(INTERFACE_DATA).getRowCount() == 0){
				dto.getOutvlist().set(ErrorMsg, "출력할 데이터가 없습니다.");				
				dto.getOutvlist().set(ErrorCode, "-1");
			}
			else{
				this.sendFax(dto, ds_data.getString(0, IF_ID));
			}
		}
		
		/*
		 * 인도처코드의 경우 입력받은 인도처 코드만 out으로 내보냄 
		 */
		if(IF003.equals(ds_data.getString(0, IF_ID))){
			String KUNWE2 = "0000"+ds_data.getString(0, "KUNWE2");											//입력받은 인도처코드
			for(int i = 0 ; i < dto.getOutdslist().get(INTERFACE_DATA).getRowCount() ; i++){		//output의rowcnt
				if(!KUNWE2.equals(dto.getOutdslist().get(INTERFACE_DATA).getString(i, "KUNWE"))){	//같지않으면
					dto.getOutdslist().get(INTERFACE_DATA).removeRow(i);
					i--;
				}
			}
		}
	
		
		
		//SAP쪽 ES로 시작하는 에러코드를 음수형으로 변환
		if(SAP.equals(ifKind)){
			String sapErr = dto.getOutvlist().get(ErrorCode).getString();
			String sapCode = "";
			if(dto.getOutvlist().get(ErrorCode).getString().length() > 2){
				sapCode = sapErr.substring(2);
				sapErr = sapErr.substring(0, 2);
				if("ES".equals(sapErr)){
					dto.getOutvlist().set(ErrorCode, "-" + sapCode);
				}
			}
			// CRM DB저장
			if(IF006.equals(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID))){
				retXmlToDb(dto, dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID));
			}
			//입금예상금액안내의경우 음수값 표현
			if(IF010.equals(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID))){
				this.setMinusExpression(dto.getOutdslist().get(DS_COMMON));
			}
			//SAP 6C 서버에서 주문번호로 주문이 조회 안되었을 경우 46C서버로 한번더 조회 요청
			if(IF008.equals(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID))){
				System.out.println("===============6C NO DATA=============");
				System.out.println(dto.getDataSet(INTERFACE_DATA).getString(0, "VBELN"));
				System.out.println("===============Searching VBELN=============");
			}
		}
		//정상통신일때 메세지처리
		if(!FAX.equals(ifKind)){
			if("".equals(dto.getOutvlist().get(ErrorMsg).getString()))
				dto.getOutvlist().set(ErrorMsg, "통신완료");				
			if("".equals(dto.getOutvlist().get(ErrorCode).getString()))
				dto.getOutvlist().set(ErrorCode, "0");
		}
		
		//고객번호로 조회시 데이터가 n개일때 처리
		if(IF001.equals(ds_data.getString(0, IF_ID))){
			if(dto.getOutdslist().get(INTERFACE_DATA).getRowCount() > 1){
				dto.getOutvlist().set(ErrorMsg, "데이터 다건조회.");				
				dto.getOutvlist().set(ErrorCode, "10");
			}			
		}
		
		//IF034 이후 에르고다음카드의 경우 CVC번호 생성해서 넘겨줌
		/*
		if(IF034.equals(ds_data.getString(0, IF_ID))){
			//String crdNo = dto.getOutvlist().getString("IN_CRD_NO");
			String crdNo = dto.getOutdslist().get(INTERFACE_DATA).getString(0, "IN_CRD_NO");
			System.out.println(crdNo);
			
			//DataSet에 addColumn 하였더니 IllegalStateException이 발생함
			//확인결과 일반적으로 데이터셋을 생성한 다음 데이터를 set한 후에는 addColumn하지 않으므로 X-API에서 위처럼 처리해 놓음
			//따라서 addColumn 하려면 setChangeStructureWithData(true)를 호출한 후에 추가를 해야함
			//2013.09
			dto.getOutdslist().get(INTERFACE_DATA).setChangeStructureWithData(true);
			dto.getOutdslist().get(INTERFACE_DATA).addColumn("OT_CVC", DataTypes.STRING, 3);	
			dto.getOutdslist().get(INTERFACE_DATA).set(0, "OT_CVC", "121");	
			
			System.out.println("==================Ergo Daum Direct============");
		}
		*/

	}
	/*
	 * 
	 *  마이너스 처리
	 */
	private void setMinusExpression(DataSet ds){
		for(int i = 0 ; i < ds.getRowCount() ; i++){
			if(!"".equals(ds.getString(i, "V_CREDIT_AMT"))){
				 int length = ds.getString(i, "V_CREDIT_AMT").length();
				 Character lastChar = ds.getString(i, "V_CREDIT_AMT").charAt(length-1);
				 if("-".equals(lastChar.toString())){
					 ds.set(i, "V_CREDIT_AMT", lastChar+ds.getString(i, "V_CREDIT_AMT").substring(0, length-1).trim());
				 }
			}
			if(!"".equals(ds.getString(i, "V_ORDER_AMT"))){
				int length = ds.getString(i, "V_ORDER_AMT").length();
				Character lastChar = ds.getString(i, "V_ORDER_AMT").charAt(length-1);
				if("-".equals(lastChar.toString())){
					ds.set(i, "V_ORDER_AMT", lastChar+ds.getString(i, "V_ORDER_AMT").substring(0, length-1).trim());
				 }
			}
			if(!"".equals(ds.getString(i, "V_SUM_AMT"))){
				int length = ds.getString(i, "V_SUM_AMT").length();
				Character lastChar = ds.getString(i, "V_SUM_AMT").charAt(length-1);
				if("-".equals(lastChar.toString())){
					ds.set(i, "V_SUM_AMT", lastChar+ds.getString(i, "V_SUM_AMT").substring(0, length-1).trim());
				 }
			}
		}
	}
	
	
	
	/*
	 * exception 처리 - error code
	 * */
	private void setErrorData(XcommonDto dto, String[] ret) {

		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		String ifKind = ds_env.getString(0, IF_KIND); //xml종류
		
		DataSet dcommon = new DataSet(DS_COMMON);
		dcommon.addColumn("HTTP_RET_CODE", DataTypes.STRING, 2000);
		dcommon.addColumn("HTTP_RET_MSG", DataTypes.STRING, 2000);
		dcommon.newRow(0);	
		dcommon.set(0, "HTTP_RET_CODE", "-1");
		dcommon.set(0, "HTTP_RET_MSG", "통신실패 [" + ret[0] + " : " + ret[1] + "]");
		dto.getOutdslist().add(dcommon);
		if(SAP.equals(ifKind)){
			dto.getOutvlist().set(ErrorMsg, dto.getOutdslist().get(DS_COMMON).getString(0, "HTTP_RET_MSG"));
			dto.getOutvlist().set(ErrorCode, dto.getOutdslist().get(DS_COMMON).getString(0, "HTTP_RET_CODE"));
			if("".equals(dto.getOutvlist().get(ErrorMsg).getString())){
				dto.getOutvlist().set(ErrorMsg, "통신완료");				
			}
			if("".equals(dto.getOutvlist().get(ErrorCode).getString())){
				dto.getOutvlist().set(ErrorCode, "0");
			}
			if(IF006.equals(dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID))){
				retXmlToDb(dto, dto.getDataSet(INTERFACE_DATA).getString(0, IF_ID));
			}
		}else if(WAS.equals(ifKind)){
			dto.getOutvlist().set(ErrorMsg, dto.getOutdslist().get(DS_COMMON).getString(0, "HTTP_RET_MSG"));
			dto.getOutvlist().set(ErrorCode, dto.getOutdslist().get(DS_COMMON).getString(0, "HTTP_RET_CODE"));
			if("".equals(dto.getOutvlist().get(ErrorMsg).getString())){
				dto.getOutvlist().set(ErrorMsg, "통신완료");				
			}
			if("".equals(dto.getOutvlist().get(ErrorCode).getString())){
				dto.getOutvlist().set(ErrorCode, "0");
			}
		}	
	}
	
	/*
	 * GSC통신 후 CRM DB저장
	 */
	private void retXmlToDb(XcommonDto dto, String ifId)
	{		
		DataSet ds_xmlToDb = new DataSet("INTERFACE_DATA2");//새로운 데이터셋 생성
		DataSet outData = dto.getOutdslist().get(INTERFACE_DATA);
		logger.debug("======retXmlToDb========");		
		if(IF006.equals(ifId)){																	//주문요청의경우
			dto.getVlist().set("CallMethod", "ARS010.set_ARSOrder_INSERT");
			dto.getDataSet(INTERFACE_ENV).set(0, IF_KIND, "INSERT"); //insert로 변환	
			//새로 dataset 하나 만들어서 컬럼추가
			for(int i = 0 ; i < outData.getColumnCount() ; i++){
				ds_xmlToDb.addColumn(outData.getColumn(i).getName(), DataTypes.STRING, 2000);
			}
			ds_xmlToDb.addColumn("VBELN", DataTypes.STRING, 2000);
			ds_xmlToDb.addColumn("KUNNR", DataTypes.STRING, 2000);
			ds_xmlToDb.addColumn("ERRMSG", DataTypes.STRING, 2000);
			ds_xmlToDb.addColumn("ERRCODE", DataTypes.STRING, 2000);
			ds_xmlToDb.addColumn("ORD_REQ_TIME", DataTypes.STRING, 2000);
			String orderTime = getOrderReqTime();
			//로우에 값 세팅
			for(int j = 0 ; j <  outData.getRowCount() ; j++){
				ds_xmlToDb.newRow(j); //로우 추가
				ds_xmlToDb.set(j, "VBELN", dto.getOutdslist().get(DS_COMMON).getString(0, "VBELN"));
				ds_xmlToDb.set(j, "KUNNR", dto.getDslist().get(INTERFACE_DATA).getString(0, "KUNNR"));
				if("".equals(dto.getOutdslist().get(DS_COMMON).getString(0, "RET_MSG")))
					ds_xmlToDb.set(j, "ERRMSG", "정상");
				else
					ds_xmlToDb.set(j, "ERRMSG", dto.getOutdslist().get(DS_COMMON).getString(0, "RET_MSG"));
				
				if("".equals(dto.getOutdslist().get(DS_COMMON).getString(0, "RET_CODE")))
					ds_xmlToDb.set(j, "ERRCODE", 0);
				else
					ds_xmlToDb.set(j, "ERRCODE", dto.getOutdslist().get(DS_COMMON).getString(0, "RET_CODE"));
				ds_xmlToDb.set(j, "ORD_REQ_TIME", orderTime);
				for(int i = 0 ; i < ds_xmlToDb.getColumnCount() - 5 ; i++){
					ds_xmlToDb.set(j, outData.getColumn(i).getName(), outData.getString(j, i));
				}					
			}
		}
		String sqlid = dto.getVariableValue(sqlid_insert);
		for(int k = 0 ; k < ds_xmlToDb.getRowCount(); k++) {
			try {
				commonDao.insert(sqlid, dto.getRowMap(ds_xmlToDb, k));
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
    private Element makeDom(String respXml) throws ParserConfigurationException, SAXException, IOException {
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(new StringReader(respXml)));
      Element root = doc.getDocumentElement();
  	
      return root;
    }

    private DataSet xmlToDataSet(Element element) {    	
        return xmlToDataSet(element, "temp", "");
    }    
    private DataSet inputSet(DataSet ds, String intfId)
    {
    	if(IF001.equals(intfId)) //고객(주유소)조회
    	{
    		if(!"".equals(ds.getString(0, "cust_id")) && !"".equals(ds.getString(0, "tel_no"))) //둘다 값이 있을경우
    		{
    			ds.set(0, "tel_no", ""); //전화번호 없앰
    		}
    	}  	
    	
    	return ds;
    }
    
	private String[] SAPString(XcommonDto dto, String endpoint) throws Exception {

			DataSet ds_ifdata = dto.getDataSet(INTERFACE_DATA);
			
			StringBuffer xml = new StringBuffer(100);
			xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			xml.append("<soap12:Envelope");
			xml.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			xml.append(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
			xml.append(" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n");
			xml.append("<soap12:Body>\n");
			xml.append("<"); xml.append(getURL(ds_ifdata.getString(0, IF_ID))); //서비스 id
			xml.append(" xmlns=\"http://eai.gsc.com/CSC\">\n");
			xml.append("<Req>\n");
			
			//data 시작
			for(int j = 0 ; j < ds_ifdata.getRowCount() ; j++ )
			{
				for(int i=0; i<ds_ifdata.getColumnCount(); i++) {
					if(!IF_ID.equals(ds_ifdata.getColumn(i).getName())){						
						if( j > 0 && "IF006".equals(ds_ifdata.getString(0, IF_ID)) && i < 5){
							i = 5 ;
						}else if( j > 0 && "IF010".equals(ds_ifdata.getString(0, IF_ID)) && i < 4){
							i = 4;
						}
						xml.append(openTable(ds_ifdata, ds_ifdata.getColumn(i).getName(), ds_ifdata.getString(j, IF_ID), j));
						xml.append("<"); xml.append(ds_ifdata.getColumn(i).getName()); xml.append(">"); 
						xml.append(dto.dsToString(ds_ifdata.getObject(j, i)));
						xml.append("</"); xml.append(ds_ifdata.getColumn(i).getName()); xml.append(">\n");
						xml.append(closeTable(ds_ifdata, ds_ifdata.getColumn(i).getName(), ds_ifdata.getString(j, IF_ID), j));
					}	
				}
			}					
			xml.append("</Req>\n");
			xml.append("</"); xml.append(getURL(ds_ifdata.getString(0, IF_ID))); xml.append(">\n"); //서비스 id
		    xml.append("</soap12:Body>\n");
		    xml.append("</soap12:Envelope>\n");
			
			String sendData = xml.toString();
			
			logger.debug("Connect URL : " + endpoint);
			logger.debug("DATA : " + sendData.toString());		
			
	       	String[] ret = soapCall(sendData, endpoint, "");
	
	       	dto.getUserTempMap().put("sendUrl", endpoint);
			dto.getUserTempMap().put("sendData", sendData);
	       	dto.getUserTempMap().put("resultData", ret[0] +" : "+ ret[1] +" : "+ ret[2]);
	
	       	return ret;
	}
	
	/*
	 * 인풋이 n건일 경우의 테이블형태의 스키마로 만들기 - 여는부분
	 */	
	private String openTable(DataSet ds_ifdata, String value, String ifId, int row){
		String xml = "";
		if(IF010.equals(ifId)){
			if("MATNR".equals(value)){
				if(0 == row){
					xml = "<T_MAT>\n" + "<ZSMAT>\n";
				}else{
					xml = "<ZSMAT>\n";
				}
			}
		}if(IF006.equals(ifId)){
			if("MATNR".equals(value)){
				if(0 == row){
					xml = "<T_MAT>\n" + "<ZSMAT>\n";
				}else{
					xml = "<ZSMAT>\n";
				}
			}
		}
		
		return xml;
	}
	
	/*
	 * 인풋이 n건일 경우의 테이블형태의 스키마로 만들기 - 닫는부분
	 */	
	private String closeTable(DataSet ds_ifdata, String value, String ifId, int row){
		String xml = "";
		if(IF010.equals(ifId)){
			if("WERKS".equals(value)){
				if(ds_ifdata.getRowCount() -1 == row){
					xml = "</ZSMAT>\n" + "</T_MAT>\n";
				}else{
					xml = "</ZSMAT>\n";
				}
			}			
		}if(IF006.equals(ifId)){
			if("VEHICLE".equals(value)){
				if(ds_ifdata.getRowCount() -1 == row){
					xml = "</ZSMAT>\n" + "</T_MAT>\n";
				}else{
					xml = "</ZSMAT>\n";
				}
			}			
		}
		
		return xml;
	}
	
	/*
	 * soap 전송
	 * reqSoapXml : xml string
	 * endpoint : URL
	 * sSoapAction : SOAPAction
	 * */
	private String[] soapCall(String reqSoapXml, String endpoint, String sSoapAction) throws IOException {
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

            reqEntity = new StringEntity(reqSoapXml, "UTF-8");
            reqEntity.setContentType("application/soap+xml");
            httppost.setEntity(reqEntity);

            logger.debug("Interface In time : ");
        	getCurrentTime();
            HttpResponse response = httpclient.execute(httppost);
            logger.debug("Interface Out time : ");
        	getCurrentTime();
            
            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
            ret[1] = response.getStatusLine().getReasonPhrase(); 
            
            if("200".equals(ret[0])) {
                resEntity = response.getEntity();
                if(resEntity!=null) {
                	ret[2] = EntityUtils.toString(resEntity);
                } else {
	                ret[0] = "201";
	                ret[1] = "응답받은 데이터가 없습니다.";
	            	ret[2] = "";
                }
            } else {
	            ret[2] = "";
            }

        } catch (IOException ex) {
            throw ex;
        }
        finally {
            if (resEntity != null) {
                resEntity.consumeContent();
            }
            httpclient.getConnectionManager().shutdown();
        }
        return ret;
    }
	
	private void sendFax(XcommonDto dto, String ifId) throws SQLException, Exception{
		
		logger.debug("======FAX Module Start=====");
		logger.debug("IFID :" + ifId);
		logger.debug(dto.getDslist().size());
		for(int i = 0 ; i < dto.getDslist().size() ; i++){
			logger.debug(dto.getDslist().get(i).getName());
		}
		logger.debug("======Common Code Convert=====");

		
		//fax 시퀀스 생성
		String seq_qid = "Common.Get_SEQ_SELECT"; 
		String contents_qid = "Common.Set_TblFaxlogList_INSERT";
		HashMap map = new HashMap();
		String fax_seq = commonDao.selectString(seq_qid, map);
		String dest_url = "";
		logger.debug("FAX seq : " + fax_seq);
		StringBuffer ifData = new StringBuffer(1024); //실제 데이터를 담을 스트링버퍼
		
		logger.debug("!======Dto Generation=====!");
		//새로운 dto생성
		Variable v = Variable.createVariable("CallMethod", "FAXCall");
		XcommonDto faxDto = new XcommonDto();
		faxDto.setAddExternalVariable(v);
				
		logger.debug("======ds_comm Generation=====");
		//DataSet 생성 후 Fax로 보내기위한 컬럼생성
		DataSet ds_comm = new DataSet(DS_COMMON);
		DataSet faxData = new DataSet("FAXCONTENTS");
		DataSet in_data = dto.getDslist().get(INTERFACE_DATA);
		for(int i = 0 ; i < in_data.getColumnCount() ; i++){
			logger.debug(in_data.getColumn(i).getName());
		}
		
		ds_comm.addColumn(IF_ID, DataTypes.STRING, 2000);
		ds_comm.newRow(0);
		ds_comm.set(0, IF_ID, ifId);
		
		logger.debug("======faxData Generation=====");
		logger.debug("fax_seq : " + fax_seq);
		logger.debug("======Row Generation=====");
		faxData.addColumn("SEQ_NO", DataTypes.STRING, 2000);
		faxData.addColumn("USER_ID", DataTypes.STRING, 2000);
		//faxData.addColumn("INPUT_COVER", DataTypes.STRING, 2000);
		faxData.addColumn("INPUT_DATA", DataTypes.STRING);
		faxData.addColumn("FORM_ID", DataTypes.STRING, 2000);
		faxData.addColumn("FORM_TYPE", DataTypes.STRING, 2000);
		faxData.addColumn("FAX_NUMBER", DataTypes.STRING, 2000);
		faxData.addColumn("RES_FAX_ID", DataTypes.STRING, 2000);
		faxData.addColumn("RES_MSG_CD", DataTypes.STRING, 2000);
		faxData.addColumn("RES_MSG_VALUE", DataTypes.STRING, 2000);
		//user define		
/*		faxData.addColumn("CUST_ID", DataTypes.STRING, 2000);
		faxData.addColumn("CUST_NM", DataTypes.STRING, 2000);
		faxData.addColumn("BIZ_DV_CD", DataTypes.STRING, 2000);
		faxData.addColumn("REQ_CHNL_CD", DataTypes.STRING, 2000);
		faxData.addColumn("CUST_FAX_NO", DataTypes.STRING, 2000);
		faxData.addColumn("SNDG_CNSLR_ID", DataTypes.STRING, 2000);
		faxData.addColumn("REG_ID", DataTypes.STRING, 2000);
		faxData.addColumn("LST_CORC_ID", DataTypes.STRING, 2000);
*/		
		//fax로 내보낼 데이터를 만듬		
		logger.debug("======Data Creating=====");
		faxData.newRow(0);
		faxData.set(0, "SEQ_NO", fax_seq);
		faxData.set(0, "USER_ID", "ARS");		
		faxData.set(0, "RES_FAX_ID", "");
		faxData.set(0, "RES_MSG_CD", "");
		faxData.set(0, "RES_MSG_VALUE", "");
/*		if(IF015.equals(ifId)){
			faxData.set(0, "CUST_ID", "-");
		}else{
			faxData.set(0, "CUST_ID", in_data.getString(0, "CUST_NO"));
		}faxData.set(0, "CUST_NM", in_data.getString(0, "CUST_NM"));
		faxData.set(0, "BIZ_DV_CD", "");
		faxData.set(0, "REQ_CHNL_CD", "610060");
		faxData.set(0, "CUST_FAX_NO", in_data.getString(0, "FAX_NO"));
		faxData.set(0, "SNDG_CNSLR_ID", "ARS");
		faxData.set(0, "REG_ID", "ARS");
		faxData.set(0, "LST_CORC_ID", "ARS");
*/	
		//// xml용 StringBuffer 생성///////////
		StringBuffer sb = new StringBuffer("");
		 
		if(IF015.equals(ifId)){															//포인트 사용내역서 조회 IF015
			logger.debug("=====Come in======");
			logger.debug(in_data.getColumnCount());
			logger.debug(in_data.getRowCount());
			faxData.set(0, "FORM_ID", "7");
			faxData.set(0, "FAX_NUMBER", in_data.getString(0, "FAX_NO"));	
			faxData.set(0, "FORM_TYPE", "2"); // FOD방식
			ifData.append("보너스카드 주유내역서|");
			ifData.append(in_data.getString(0, "CUST_NM") + "#");
			ifData.append(in_data.getString(0, "CUST_NM") + "|");
			ifData.append(in_data.getString(0, "HEAD_RS_BZ_NO") + in_data.getString(0, "TAIL_RS_BZ_NO") + "|");
			ifData.append(in_data.getString(0, "TOTAL_PNT") + "|");			
			//////////////////////////////////////////////////////
			dest_url = Const.bonus_ivr_url;
			sb.append("<result><data><header>");
			sb.append("<cust_nm>"+in_data.getString(0, "CUST_NM")+"</cust_nm>");
			sb.append("<fax_seq>"+fax_seq+"</fax_seq>");
			sb.append("<fax_number>"+in_data.getString(0, "FAX_NO")+"</fax_number>");
			sb.append("<tot_pt>"+in_data.getString(0, "TOTAL_PNT")+"</tot_pt></header><body>");
			//총가용포인트(임시처리)			
			for(int i = 0 ; i < in_data.getRowCount() ; i++){
				if(i != 0 && i%30 == 0 ){											 //페이지처리
					ifData.append("$" + in_data.getString(0, "CUST_NM") + "|");	 //고객명
					ifData.append(in_data.getString(0, "HEAD_RS_BZ_NO") + in_data.getString(0, "TAIL_RS_BZ_NO") + "|");			 //고객번호
					ifData.append(in_data.getString(0, "TOTAL_PNT") + "|");
				}
				String useDate = "";
				if(in_data.getString(i, "SALE_DTIME").length() == 20){
					useDate = in_data.getString(i, "SALE_DTIME").substring(0, 4)+"-"
					+ in_data.getString(i, "SALE_DTIME").substring(4, 6)+"-"
					+ in_data.getString(i, "SALE_DTIME").substring(6, 8)+" "
					+ in_data.getString(i, "SALE_DTIME").substring(8, 10)+":"
					+ in_data.getString(i, "SALE_DTIME").substring(10, 12)+":"
					+ in_data.getString(i, "SALE_DTIME").substring(12, 14);
				}else{							
					useDate = in_data.getString(i, "SALE_DTIME");
				}
				ifData.append(useDate + "|");
				ifData.append(in_data.getString(i, "CRD_NO") + "|");
				ifData.append(in_data.getString(i, "FRCH_NM") + "|");
				ifData.append(in_data.getString(i, "SPCL_PT_NM") + "|");
				ifData.append(in_data.getString(i, "TR_OCUR_RSN_NM") + "|");
				ifData.append(in_data.getString(i, "AMT") + "|");
				ifData.append(in_data.getString(i, "PNT") + "|");	
				////////////////////////////////////////////////
				sb.append("<info><exec_dt>"+useDate+"</exec_dt>");
				sb.append("<card_no>"+in_data.getString(i, "CRD_NO")+"</card_no>");
				sb.append("<branch>"+in_data.getString(i, "FRCH_NM")+"</branch>");
				sb.append("<event_nm>"+in_data.getString(i, "SPCL_PT_NM")+"</event_nm>");
				sb.append("<reason>"+in_data.getString(i, "TR_OCUR_RSN_NM")+"</reason>");
				sb.append("<amount>"+in_data.getString(i, "AMT")+"</amount>");
				sb.append("<point>"+in_data.getString(i, "PNT")+"</point></info>");
			}
			sb.append("</body></data></result>");
			logger.debug("FAX Send Data : " + ifData.toString());
			faxData.set(0, "INPUT_DATA", ifData);
		}
		else if(IF018.equals(ifId)){													//약관, 상품권 IF018
			logger.debug("FLAG : " + in_data.getString(0, "FLAG"));
			String fixed_form_id = "12";
			if("1".equals(in_data.getString(0, "FLAG"))){			//약관
				faxData.set(0, "FORM_ID", "12");
				faxData.set(0, "FAX_NUMBER", in_data.getString(0, "FAX_NO"));
				faxData.set(0, "INPUT_DATA", "약관|고객님");
				faxData.set(0, "FORM_TYPE", "1"); // tif방식
			}else if("2".equals(in_data.getString(0, "FLAG"))){			//상품권
				faxData.set(0, "FORM_ID", "13");
				faxData.set(0, "FAX_NUMBER", in_data.getString(0, "FAX_NO"));
				faxData.set(0, "INPUT_DATA", "상품권|고객님");
				faxData.set(0, "FORM_TYPE", "1"); // tif방식
				fixed_form_id = "13";
			}
			sb.append("<result><data>");
			sb.append("<cust_nm>"+in_data.getString(0, "CUST_NM")+"</cust_nm>");
			sb.append("<fax_seq>"+fax_seq+"</fax_seq>");
			sb.append("<FORM_ID>"+fixed_form_id+"</FORM_ID>");
			sb.append("<fax_number>"+in_data.getString(0, "FAX_NO")+"</fax_number></data></result>");
			dest_url = Const.app_fixed_forms_url;
		}
		else{																			//주문내역, 배차배송
			DataSet outDs_comm = dto.getOutdslist().get(DS_COMMON);
			for(int  i = 0 ; i < outDs_comm.getColumnCount() ; i++){
				logger.debug(outDs_comm.getColumn(i).getName());
				logger.debug(outDs_comm.getString(0, i));
			}
			DataSet ds_data = dto.getOutdslist().get(INTERFACE_DATA);
			//if("TR009".equals(in_data.getString(0, "TCODE"))){							//주문내역용지		
			if("009".equals(in_data.getString(0, "TCODE"))){							//주문내역용지
				faxData.set(0, "FORM_ID", "5");
				faxData.set(0, "FAX_NUMBER", in_data.getString(0, "FAX_NO"));
				faxData.set(0, "FORM_TYPE", "2"); // FOD방식
				//실제 넘길 데이터 생성	
				ifData.append("주문결과조회|");
				ifData.append(outDs_comm.getString(0, "NAME2") + "#");				//표지세팅
				ifData.append(outDs_comm.getString(0, "NAME2") + "|");//고객명
				ifData.append(outDs_comm.getString(0, "KUNNR2") + "|"); //고객번호
				ifData.append(ds_data.getRowCount() + "|");
				//////////////////////////////////////////////////////
				dest_url = Const.order_ivr_url;
				sb.append("<result><data><header>");
				sb.append("<cust_nm>"+outDs_comm.getString(0, "NAME2")+"</cust_nm>");
				sb.append("<cust_cd>"+outDs_comm.getString(0, "KUNNR2")+"</cust_cd>");
				sb.append("<fax_seq>"+fax_seq+"</fax_seq>");
				sb.append("<fax_number>"+in_data.getString(0, "FAX_NO")+"</fax_number>");
				sb.append("<order_cnt>"+ds_data.getRowCount()+"</order_cnt></header><body>");
				for(int i = 0 ; i < ds_data.getRowCount() ; i++){
					if(i != 0 && i%14 == 0 ){											 //페이지처리
						ifData.append("$" + outDs_comm.getString(0, "NAME2") + "|");	 //고객명
						ifData.append(outDs_comm.getString(0, "KUNNR2") + "|");			 //고객번호
						ifData.append(ds_data.getRowCount() + "|");
					}
					String orderDate = "";
					if(ds_data.getString(i, "ERDAT2").length() == 14){
						orderDate = ds_data.getString(i, "ERDAT2").substring(0, 4)+"-"
						+ ds_data.getString(i, "ERDAT2").substring(4, 6)+"-"
						+ ds_data.getString(i, "ERDAT2").substring(6, 8)+" "
						+ ds_data.getString(i, "ERDAT2").substring(8, 10)+":"
						+ ds_data.getString(i, "ERDAT2").substring(10, 12)+":"
						+ ds_data.getString(i, "ERDAT2").substring(12, 14);
					}else{							
						orderDate = ds_data.getString(i, "ERDAT2");
					}
					ifData.append(ds_data.getString(i, "VBELN2") + "|");		//오더번호
					ifData.append(orderDate + "|"); 		//주문일자
					ifData.append(ds_data.getString(i, "WENAME") + "|");		//인도처
					ifData.append(ds_data.getString(i, "MAKTX") + "|"); 		//유종명
					ifData.append(ds_data.getString(i, "KWMENG").trim() + "L|"); 		//주문량
					ifData.append(ds_data.getString(i, "REQ_TIME") + "|"); 		//납품요청일시
					ifData.append(convertCommonCode("OIC_MOT", ds_data.getString(i, "OIC_MOT")) + "|"); 		//운송모드
					ifData.append(ds_data.getString(i, "WNAME") + "|"); 		//출하저유소명
					ifData.append(convertCommonCode("CMGST", ds_data.getString(i, "CMGST")) + "|");			//지사확인여부
					
					///////////////////////////////////////////////////////////////
					sb.append("<info><order_no>"+ds_data.getString(i, "VBELN2")+"</order_no>");
					sb.append("<order_dt>"+orderDate+"</order_dt>");
					sb.append("<receipient>"+ds_data.getString(i, "WENAME")+"</receipient>");
					sb.append("<oil_type>"+ds_data.getString(i, "MAKTX")+"</oil_type>");
					sb.append("<volume>"+ds_data.getString(i, "KWMENG").trim() + "L</volume>");
					sb.append("<expected_dt>"+ds_data.getString(i, "REQ_TIME")+"</expected_dt>");
					sb.append("<tran_type>"+convertCommonCode("OIC_MOT", ds_data.getString(i, "OIC_MOT"))+"</tran_type>");
					sb.append("<sender>"+ds_data.getString(i, "WNAME")+"</sender>");
					sb.append("<confirmation>"+convertCommonCode("CMGST", ds_data.getString(i, "CMGST"))+"</confirmation></info>");
				}
				sb.append("</body></data></result>");
				logger.debug("FAX Send Data : " + ifData.toString());
				faxData.set(0, "INPUT_DATA", ifData);
			//}else if("TR010".equals(in_data.getString(0, "TCODE"))){							// 배차배송내역조회 IF009
			}else if("010".equals(in_data.getString(0, "TCODE"))){							// 배차배송내역조회 IF009
				faxData.set(0, "FORM_ID", "6");
				faxData.set(0, "FAX_NUMBER", in_data.getString(0, "FAX_NO"));
				faxData.set(0, "FORM_TYPE", "2"); // FOD방식
				//실제 넘길 데이터 생성
				ifData.append("배차배송내역조회|");
				ifData.append(outDs_comm.getString(0, "NAME2") + "#");			//표지세팅
				ifData.append(outDs_comm.getString(0, "NAME2") + "|");//고객명
				ifData.append(outDs_comm.getString(0, "KUNNR2") + "|"); //고객번호
				ifData.append(ds_data.getRowCount() + "|");
				//////////////////////////////////////////////////////
				dest_url = Const.tran_ivr_url;
				sb.append("<result><data><header>");
				sb.append("<cust_nm>"+outDs_comm.getString(0, "NAME2")+"</cust_nm>");
				sb.append("<cust_cd>"+outDs_comm.getString(0, "KUNNR2")+"</cust_cd>");
				sb.append("<fax_seq>"+fax_seq+"</fax_seq>");
				sb.append("<fax_number>"+in_data.getString(0, "FAX_NO")+"</fax_number>");
				sb.append("<order_cnt>"+ds_data.getRowCount()+"</order_cnt></header><body>");
				for(int i = 0 ; i < ds_data.getRowCount() ; i++){
					if(i != 0 && i%14 == 0 ){										//페이지처리		
						ifData.append("$" + outDs_comm.getString(0, "NAME2") + "|");//고객명
						ifData.append(outDs_comm.getString(0, "KUNNR2") + "|"); 	//고객번호
						ifData.append(ds_data.getRowCount() + "|");
					}
					String orderDate = "";
					if(ds_data.getString(i, "ERDAT2").length() == 14){
						orderDate = ds_data.getString(i, "ERDAT2").substring(0, 4)+"-"
						+ ds_data.getString(i, "ERDAT2").substring(4, 6)+"-"
						+ ds_data.getString(i, "ERDAT2").substring(6, 8)+" "
						+ ds_data.getString(i, "ERDAT2").substring(8, 10)+":"
						+ ds_data.getString(i, "ERDAT2").substring(10, 12)+":"
						+ ds_data.getString(i, "ERDAT2").substring(12, 14);
					}else{
						orderDate = ds_data.getString(i, "ERDAT2");
					}
					ifData.append(ds_data.getString(i, "VBELN2") + "|");		//오더번호
					ifData.append(orderDate + "|"); 		//주문일자
					ifData.append(ds_data.getString(i, "WENAME") + "|");		//인도처
					ifData.append(ds_data.getString(i, "MAKTX") + "|"); 		//유종
					ifData.append(ds_data.getString(i, "KWMENG").trim() + ds_data.getString(i, "VRKME").trim() + "|"); 		//주문량					
					ifData.append(ds_data.getString(i, "REQ_TIME") + "|");		//납품요청일시
					ifData.append(ds_data.getString(i, "SHIP_TIME") + "|"); 	//배차계획일시
					ifData.append(ds_data.getString(i, "LOAD_TIME") + "|"); 	//적재일시
					if("".equals(ds_data.getString(i, "LFIMG").trim()))
						ifData.append("|");
					else
						ifData.append(ds_data.getString(i, "LFIMG").trim() + ds_data.getString(i, "VRKME").trim() + "|"); 		//적재량
					ifData.append(ds_data.getString(i, "WNAME") + "|"); 		//출하처
					ifData.append(convertCommonCode("CMGST", ds_data.getString(i, "CMGST")) + "|"); 		//지사확인여부
					ifData.append(ds_data.getString(i, "VEH_TEXT") + "|"); 		//차량번호
					ifData.append(ds_data.getString(i, "DRIVERCODE") + "|"); 	//기사이름
					ifData.append(ds_data.getString(i, "TELF1") + "|"); 		//전화번호
					
					///////////////////////////////////////////////////////////////
					sb.append("<info><order_no>"+ds_data.getString(i, "VBELN2")+"</order_no>");
					sb.append("<order_dt>"+orderDate+"</order_dt>");
					sb.append("<receipient>"+ds_data.getString(i, "WENAME")+"</receipient>");
					sb.append("<oil_type>"+ds_data.getString(i, "MAKTX")+"</oil_type>");
					sb.append("<volume>"+ds_data.getString(i, "KWMENG").trim() + "L</volume>");
					sb.append("<expected_dt>"+ds_data.getString(i, "REQ_TIME")+"</expected_dt>");
					sb.append("<tran_expected_dt>"+ds_data.getString(i, "SHIP_TIME")+"</tran_expected_dt>");
					sb.append("<load_expected_dt>"+ds_data.getString(i, "LOAD_TIME")+"</load_expected_dt>");
					if("".equals(ds_data.getString(i, "LFIMG").trim()))
						sb.append("<load_amt></load_amt>");
					else
						sb.append("<load_amt>"+ ds_data.getString(i, "LFIMG").trim() + ds_data.getString(i, "VRKME").trim() +"</load_amt>");
					sb.append("<unload_nm>"+ds_data.getString(i, "WNAME")+"</unload_nm>");
					sb.append("<confirmation>"+convertCommonCode("CMGST", ds_data.getString(i, "CMGST"))+"</confirmation>");
					sb.append("<veh_no>"+ds_data.getString(i, "VEH_TEXT")+"</veh_no>");
					sb.append("<driver_nm>"+ds_data.getString(i, "DRIVERCODE")+"</driver_nm>");
					sb.append("<tel_no>"+ds_data.getString(i, "TELF1")+"</tel_no></info>");

					
				}
				sb.append("</body></data></result>");
				logger.debug("FAX Send Data : " + ifData.toString());
				faxData.set(0, "INPUT_DATA", ifData);
			}
		}	 	

		faxDto.setAddExternalDataset(faxData);
		dto.setAddExternalDataset(faxData);
		logger.debug("======CRM DB Insert=====");
		dataInsert(dto, faxData, contents_qid, fax_seq);
		
//		faxDBSave(faxDto, fax_seq);
		logger.debug("---------"+sb.toString());
		sendXml(sb.toString(), dest_url);
	}
	
	////////////////////////////////일반 xml 전송////////////////////////////
	public void sendXml(String send_data, String dest_url){
		try{
			String xmlString = send_data;
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpRequest = new HttpPost(dest_url);
			httpRequest.setHeader("Content-Type", "application/xml");
			StringEntity xmlEntity = new StringEntity(xmlString,"UTF-8");
			httpRequest.setEntity(xmlEntity);
			HttpResponse httpresponse = httpClient.execute(httpRequest);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private String[] send(String sendurl, List<BasicNameValuePair> sdata, String inCharSet, String outCharSet) throws IOException {

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
        	
        	logger.debug("Interface In time : ");
        	getCurrentTime();
            HttpResponse response = httpclient.execute(httppost);
            logger.debug("Interface Out time : ");
            getCurrentTime();
            ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
            ret[1] = response.getStatusLine().getReasonPhrase();
            
            if("200".equals(ret[0])) {
                int totlen = 0;
                int len = 0;
	            resEntity = response.getEntity();
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
		            ret[2] = new String(bos.toByteArray(), outCharSet);
	            } else {
	                ret[0] = "201";
	                ret[1] = "응답받은 데이터가 없습니다.";
	            	ret[2] = "";
	            }

            } else {
	            ret[2] = "";
            }
            
        } catch (IOException ex) {
            throw ex;
        }
        finally {
        	
        	try {if(iis!=null) iis.close();}catch(Exception e){}
        	try {if(bos!=null) bos.close();}catch(Exception e){}
        	
            httpclient.getConnectionManager().shutdown();
        }
        return ret;
    }
	
	// 데이터 인서트
	private void dataInsert(XcommonDto dto, DataSet Ds, String sqlid, String seq) throws Exception {
		if(Ds != null) {
			for(int k=0; k < Ds.getRowCount(); k++) {
				HashMap map = dto.getDefaultRowMap(Ds, k);
//				HashMap map = new HashMap();
	/*			for(int i = 0 ;  i < Ds.getColumnCount() ; i++){
					map.put(Ds.getColumn(i).getName().toLowerCase(), Ds.getString(k, i));
				}*/
				map.put("seq_no", seq);
				commonDao.insert(sqlid, map);
			}
		}
	}
	
	private void faxDBSave(XcommonDto dto, String fax_seq) throws Exception{
		
		logger.debug("======FAX DB INSERT=====");
		for(int i = 0 ; i <dto.getDslist().size() ; i++){
			logger.debug("name : " + dto.getDslist().get(i).getName());
		}
		DataSet ds_contents = dto.getDataSet("FAXCONTENTS");
		logger.debug("Form type : " + dto.dsToString(ds_contents.getObject(0, "FORM_TYPE")));
		String ft = dto.dsToString(ds_contents.getObject(0, "FORM_TYPE"));
	
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		Connection faxconn = null;
		PreparedStatement ps = null;    
		
		try {			
//			faxconn = DriverManager.getConnection(REAL_FAX_DB_URL, REAL_FAX_DB_ACCOUNT, REAL_FAX_DB_PASSWORD);
			faxconn = DriverManager.getConnection(Const.FAX_DB_URL, Const.FAX_DB_ACCOUNT, Const.FAX_DB_PASSWORD);
			StringBuffer sb = new StringBuffer(100);
			if("1".equals(ft)){
				sb.append(" INSERT INTO REQUEST_DATA (");
				sb.append("  user_id         ");
				sb.append(" ,filepage       ");
				sb.append(" ,filename       ");
				sb.append(" ,faxnumber_count ");
				sb.append(" ,request_date    ");
				sb.append(" ,body      ");
				sb.append(" ,cover_value       ");
				sb.append(" ,flag            ");
				sb.append(" ,priority        ");
				sb.append(" ,form_id         ");
				sb.append(" ,customer_id     ");
				sb.append(" ,fod_id     ");
			    sb.append(" )VALUES(");
			    sb.append(" ? ");
			    sb.append(" ,'1' ");
			    sb.append(" ,? ");
			    sb.append(" ,1 ");
			    sb.append(" ,convert(varchar(8), getdate(), 112)    ");
			    sb.append(" ,? ");
			    sb.append(" ,? ");
			    sb.append(" ,'00'  ");
			    sb.append(" ,0 ");
			    sb.append(" ,? ");
			    sb.append(" ,? ");
			    sb.append(" ,'1' ");
			    sb.append(" )");
			    
			    ps = faxconn.prepareStatement(sb.toString());
			    ps.setString(1, "ARS");
			    ps.setString(2, "");
			    ps.setString(3, dto.dsToString(ds_contents.getObject(0, "FAX_NUMBER"))+"/" + "ARS" + "/1|");
			    ps.setString(4, dto.dsToString(ds_contents.getObject(0, "INPUT_DATA")));
			    ps.setString(5, dto.dsToString(ds_contents.getObject(0, "FORM_ID")));
			    ps.setString(6, "ARS");
			    ps.executeUpdate();
			    ps.close();
				
			}else{
				
				sb.append(" INSERT INTO FOD_ROW_Data (");
				sb.append("  site_code     ");
				sb.append(" ,fax_number    ");
				sb.append(" ,user_id       ");
				sb.append(" ,FOD_ID        ");
				sb.append(" ,customer_id   ");
				sb.append(" ,request_date  ");
				sb.append(" ,request_time  ");
				sb.append(" ,cover_id      ");
				sb.append(" ,process_tag   ");
				sb.append(" ,fax_value     ");
			    sb.append(" )VALUES(");
			    
			    sb.append("  1     ");
			    sb.append(" ,?    "); //fax_number
			    sb.append(" ,?    "); //user_id
			    sb.append(" ,?    "); //fod_id
			    sb.append(" ,?    "); //customer_id
			    sb.append(" ,convert(varchar(8), getdate(), 112)  ");
			    sb.append(" ,replace(convert(varchar(8), getdate(), 108),':','')  ");
			    sb.append(" ,'1'   ");
			    sb.append(" ,0   ");
			    sb.append(" ,?   ");  //fax_value
			    sb.append(" )");
			    
			    ps = faxconn.prepareStatement(sb.toString());
			    ps.setString(1, dto.dsToString(ds_contents.getObject(0, "FAX_NUMBER")));
			    ps.setString(2, dto.dsToString(ds_contents.getObject(0, "USER_ID")));
			    ps.setString(3, dto.dsToString(ds_contents.getObject(0, "FORM_ID")));
			    ps.setString(4, fax_seq);
			    ps.setString(5, dto.dsToString(ds_contents.getObject(0, "INPUT_COVER")) + dto.dsToString(ds_contents.getObject(0, "INPUT_DATA")));		
			    ps.executeUpdate();
			    ps.close();
			}
		} catch (Exception e) {
			//logger.debug("팩스디비 저장중 오류 " + e.toString());
			throw new RuntimeException("팩스디비 저장중 오류  : " + e);
	
		} finally {
			try { if(ps !=null) ps.close(); ps=null; } catch(Exception e){};
			try { if(faxconn !=null) faxconn.close(); faxconn=null; } catch(Exception e){};	
		}
		logger.debug("======ARS INTERFACE FAX Connection END=====");
	}
	
	private String convertCommonCode(String id, String code) throws Exception{
		String name = "";
		if("CMGST".equals(id)){ //지사확인여부
			if("A".equals(code))
				name = "승인필요없음";
			else if("B".equals(code))
				name = "승인대기";
			else if("C".equals(code))
				name = "부분승인";
			else if("D".equals(code))
				name = "승인완료";
			else if(" ".equals(code)||"".equals(code))
				name = "승인필요없음";			
		}else if("OIC_MOT".equals(id)){
			if("00".equals(code))
				name = "기타";
			else if("1C".equals(code))
				name = "Tank Truck_고객";
			else if("1L".equals(code))
				name = "Tank Truck_GS";
			else if("2C".equals(code))
				name = "Stack Truck_고객";
			else if("2L".equals(code))
				name = "Stack Truck_GS";
			else if("3C".equals(code))
				name = "Rail Tank Car_고객";
			else if("3L".equals(code))
				name = "Rail Tank Car_GS";
			else if("4C".equals(code))
				name = "Rail Box Car_고객";
			else if("4L".equals(code))
				name = "Rail Box Car_GS";
			else if("5C".equals(code))
				name = "Vessel_고객";
			else if("5L".equals(code))
				name = "Vessel_GS";
			else if("6C".equals(code))
				name = "Pipe Line_고객";
			else if("6L".equals(code))
				name = "Pipe Line_GS";
			else if("7C".equals(code))
				name = "Barge_고객";
			else if("7L".equals(code))
				name = "Barge_GS";
		}
			
		
		return name;
	}
	
	private String getURL(String wasIfId)
	{ logger.debug("getURL_ID===="+wasIfId);
		String URL = "";
		if(IF003.equals(wasIfId)){
			URL = IF003_SAP_ID;
		}else if(IF005.equals(wasIfId)){
			URL = IF005_SAP_ID;
		}else if(IF006.equals(wasIfId)){
			URL = IF006_SAP_ID;
		}else if(IF007.equals(wasIfId)){
			URL = IF007_SAP_ID;
		}else if(IF008.equals(wasIfId)){
			URL = IF008_SAP_ID;
		}else if(IF009.equals(wasIfId)){
			URL = IF009_SAP_ID;				//fax 주문,배차배송
		}else if(IF010.equals(wasIfId)){
			URL = IF010_SAP_ID;
		}else if(IF012.equals(wasIfId)){
			URL = IF012_SAP_ID;
		}else if(IF013.equals(wasIfId)){
			URL = IF013_URL;
		}else if(IF017.equals(wasIfId)){
			URL = IF017_URL;
		}else if(IF019.equals(wasIfId)){
			URL = IF019_URL;
		}else if(IF021.equals(wasIfId)){
			URL = IF021_URL;
		}else if(IF022.equals(wasIfId)){
			URL = IF022_URL;
		}else if(IF026.equals(wasIfId)){
			URL = IF026_URL;
		}else if(IF031.equals(wasIfId)){
			URL = IF031_URL;
		}else if(IF033.equals(wasIfId)){
			logger.debug("===================SJH_033=================");
			URL = IF033_URL;
			logger.debug("===================SJH_033end=================");
		}else if(IF034.equals(wasIfId)){		// 이광호 추가 2013-01-29
			URL = IF034_URL;
		}else if(IF035.equals(wasIfId)){		// 이광호 추가 2013-01-29
			URL = IF035_URL;
		}else if(IF_OIL.equals(wasIfId)){
			URL = IF_OIL_URL;
		}else if(IF_PNT.equals(wasIfId)){
			URL = IF_PNT_URL;
		}else if(IF037.equals(wasIfId)){       //ARS 휴대폰제약조건 추가 2015-01-13
			logger.debug("===================SJH_URL=================");
			System.out.println("IF037_URL");
			logger.debug("===================SJH_URL=================");
			URL = IF037_URL;			
		}
		
		return URL;
	}

	private String getRecord(String ifId)
	{
		if(IF003.equals(ifId)){
			ifId = "ZSKUNWE";
		}else if(IF005.equals(ifId)){
			ifId = "ZSTRUCK";
		}else if(IF006.equals(ifId)){
			ifId = "ZSMAT";
		}else if(IF007.equals(ifId)){
			ifId = "ZSINC";
		}else if(IF008.equals(ifId)){
			ifId = "ZSARSORD";
		}else if(IF009.equals(ifId)){
			ifId = "ZSARSORD";
		}else if(IF010.equals(ifId)){
			ifId = "ZSMAT";
		}else if(IF012.equals(ifId)){
			ifId = IF012_SAP_RESULT;
		}else if(IF013.equals(ifId)){
			ifId = "OT_OUT_CURSOR";
		}else if(IF017.equals(ifId)){
			ifId = "OT_OUT_CURSOR";	
		}else if(IF019.equals(ifId)){
			ifId = "STP_ARS_CM_CUST_SEL_C02_Rsp";
		}else if(IF021.equals(ifId)){
			ifId = "STP_ARS_CR_CRDREG_CHK_C02_Rsp";
		}else if(IF022.equals(ifId)){
			ifId = "STP_ARS_CM_CUST_INS_C05_Rsp";
		}else if(IF026.equals(ifId)){
			ifId = "STP_CM_INFOAGR_INS_C01_Rsp";
		}else if(IF031.equals(ifId)){
			ifId = "STP_CM_CAUT_IUD_C03_Rsp";
		}else if(IF033.equals(ifId)){
			ifId = "STP_ARS_CM_CUST_SEL_C04_Rsp";
		}else if(IF034.equals(ifId)){						// 2013-01-29 이광호 추가
			ifId = "STP_CR_TREGCRD_SEL_C03_Rsp";
		}else if(IF035.equals(ifId)){						// 2013-01-29 이광호 추가
			ifId = "STP_CR_TREGCRD_INS_C01_Rsp";
		}else if(IF_PNT.equals(ifId)){
			ifId = "OT_OUT_CURSOR";
		}else if(IF_OIL.equals(ifId)){
			ifId = "OT_OUT_CURSOR";
		}else if(IF037.equals(ifId)){
			logger.debug("===================SJH_R=================");
			System.out.println("IF037_R");
			logger.debug("===================SJH_R=================");
			ifId = "STP_ARS_CM_CUIKTPHN_CHK_C01_Rsp";
		}
		
		return ifId;
	}
	private String getHeader(String ifId)
	{
		if(IF003.equals(ifId)){
			ifId = IF003_SAP_RESULT;
		}else if(IF005.equals(ifId)){
			ifId = IF005_SAP_RESULT;
		}else if(IF006.equals(ifId)){
			ifId = IF006_SAP_RESULT;
		}else if(IF007.equals(ifId)){
			ifId = IF007_SAP_RESULT;
		}else if(IF008.equals(ifId)){
			ifId = IF008_SAP_RESULT;
		}else if(IF009.equals(ifId)){
			ifId = IF009_SAP_RESULT;
		}else if(IF010.equals(ifId)){
			ifId = IF010_SAP_RESULT;
		}else if(IF012.equals(ifId)){
			ifId = IF012_SAP_RESULT;
		}else if(IF013.equals(ifId)){
			ifId = "STP_CM_CUST_SEL_C03_Rsp";
		}else if(IF017.equals(ifId)){
			ifId = "STP_ARS_CR_CRDPW_UPD_C01_Rsp";	
		}else if(IF019.equals(ifId)){
			ifId = "STP_ARS_CM_CUST_SEL_C02_Rsp";
		}else if(IF021.equals(ifId)){
			ifId = "STP_ARS_CR_CRDREG_CHK_C02_Rsp";
		}else if(IF022.equals(ifId)){
			ifId = "STP_ARS_CM_CUST_INS_C05_Rsp";
		}else if(IF026.equals(ifId)){
			ifId = "STP_CM_INFOAGR_INS_C01_Rsp";
		}else if(IF031.equals(ifId)){
			ifId = "STP_CM_CAUT_IUD_C03_Rsp";
		}else if(IF033.equals(ifId)){
			ifId = "STP_ARS_CM_CUST_SEL_C04_Rsp";
		}else if(IF034.equals(ifId)){						// 2013-01-29 이광호 추가
			ifId = "STP_CR_TREGCRD_SEL_C03_Rsp";
		}else if(IF035.equals(ifId)){						// 2013-01-29 이광호 추가
			ifId = "STP_CR_TREGCRD_INS_C01_Rsp";
		}else if(IF_PNT.equals(ifId)){
			ifId = "STP_ARS_PT_CUSTPT_SEL_C22_Rsp";
		}else if(IF_OIL.equals(ifId)){
			ifId = "STP_ARS_PT_CUSTPT_SEL_C21_Rsp";
		}else if(IF037.equals(ifId)){						// ARS휴대폰제약조건 체크 추가 2015-01-13
			logger.debug("===================SJH_H=================");
			System.out.println("IF037_H");
			logger.debug("===================SJH_H_E=================");
			ifId = "STP_ARS_CM_CUIKTPHN_CHK_C01_Rsp";
		}
		
		return ifId;
	}
	
	private void getCurrentTime(){
		String time = "";
		Calendar cal = Calendar.getInstance(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		time = sdf.format(cal.getTime());		
		logger.debug(time);		
	}
	
	private String getOrderReqTime(){
		Calendar cal = Calendar.getInstance(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");		
		return sdf.format(cal.getTime());
	}
}