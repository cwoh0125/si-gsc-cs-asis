package kr.co.cs.daemon;


import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataSetList;
import com.tobesoft.xplatform.data.DataTypes;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.license.A.E;
import com.tobesoft.xplatform.tx.HttpPlatformResponse;
import com.tobesoft.xplatform.tx.PlatformType;

import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.persistence.dao.CommonDao;
import kr.co.cs.presentation.xcommon.XbaseAction;
import sun.text.normalizer.IntTrie;

/**
 * @author Administrator
 * 
 * 
 */
public class SAPApp extends XbaseAction {

	/*
	 * 생성자
	 * was기동시에 시작되게..
	 * */
	public SAPApp() {
		startSap(true);

	}
	
	private final static Logger saplogger = LogManager.getLogger("process.if");
	private final static Logger sapetclogger = LogManager.getLogger("process.etc");
	private final static Logger extlogger = LogManager.getLogger("process.ext");
	
	private String SAP_END_POINT;  //SAP
	
	private DataSet retxml;
	
	private String workTime;
	
	private CommonDao commonDao = null;

	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;  
	}
	
	public void StartSapDaemon(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {		
		startSap();
	}
	
	public void StopSapDaemon(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("val", "N");
		paramMap.put("cd", "DM_SAPIFRUN");
		commonDao.update("Common.Set_DaemonState", paramMap);
	}
		
	public void SapState(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		Iterator<Thread> ir = map.keySet().iterator();

		String alive = "N";
		while(ir.hasNext()) {
			Thread th = ir.next();
			//System.out.println("SAPDaemonThread " + th.getName());
			if(th.getName().indexOf("SAPDaemonThread")>=0) {
				//System.out.println("SAPDaemonThread Alive");
				alive = "Y";
			}
		}
		
		HttpPlatformResponse platformResponse = new HttpPlatformResponse(response, PlatformType.CONTENT_TYPE_XML, "UTF-8");
		VariableList outvlist = new VariableList();
		outvlist.add("sapStatusMsg", alive);		
		PlatformData output = new PlatformData();
		output.setVariableList(outvlist);
		
		platformResponse.setData(output);
		platformResponse.sendData();
	}
		
	private void startSap() throws Exception {
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("val", "Y");
		paramMap.put("cd", "DM_SAPIFRUN");
		commonDao.update("Common.Set_DaemonState", paramMap);
		
		startSap(true);
	}
	
	
	private void startSap(Boolean a) {	
		
		try {
			
			String server_name = InetAddress.getLocalHost().getHostName().trim();
			if(!("___WKH-BT-N".equals(server_name) ||
					"___wkh-bt-n".equals(server_name) ||
			   "CRMDEV".equals(server_name) ||
			   //"CRMDEV".equals(server_name) ||
			   Const.WAS1NAME.equals(server_name) ||
			   Const.WAS2NAME.equals(server_name))) 
			{
				extlogger.debug("[SAP DAEMON STOP ::::: STOPING AS SERVER IS NOT PROD ........]");
				return;
			}
		} catch (UnknownHostException e1) {			
			return;
		}
		
		
		extlogger.debug("[SAP DAEMON GO ::::: GO GO GO........]");

		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		Iterator<Thread> ir = map.keySet().iterator();

		while(ir.hasNext()) {
			Thread th = ir.next();
			if(th.getName().indexOf("SAPDaemonThread")>=0) { 
				//th.interrupt();
				return;
			}
		}
		
		Thread SAPthread = new Thread() {
			public void run() {
				
				HashMap<String, String> runmap = new HashMap<String, String>();
				runmap.put("cd", "DM_SAPIFRUN");
				String bb = null;
				StringBuffer times = new StringBuffer(1);
				
				int tloop = 0;
				int execloop = 0; 
				while(true) {
					
					try {
						Thread.sleep(60000); // 1 minutes
						tloop++;
						execloop++;
						times.delete(0, times.length());
						times.append(new SimpleDateFormat("HHmm").format(new Date())); //시간을 여기다 두는건 혹시나 10분텀의 간격중 0분에서 11분으로 중간작업때문에 넘어갈까봐..
					
					} catch(InterruptedException e) {}

					try {
						if(tloop==2) { //디비체크는 2분에 한번타게끔..
							tloop = 0;
							bb = commonDao.selectString("Common.Get_DaemonRunning", runmap);
							//System.out.println(new Date() + " === CALL ::::: sap CALL ::::: sap CALL ::::: sap " + bb);
							if("N".equals(bb)) {
								execloop = 0; //loop 초기화 (아래 안타게..)
								//System.out.println(new Date() + " : SAP DAEMON STOP ::::: STOP STOP STOP........");
								extlogger.debug("[SAP DAEMON STOP ::::: STOP STOP STOP ........]");
								break;
							}
						}
					} catch(NullPointerException e) {} catch(Exception e) {}

					try {
						if(execloop==10) { //10분에 한번 호출..
							execloop = 0;
							if("225".equals(times.toString().substring(0, 3))) { //오후 11시 분대에 실행
								//System.out.println(new Date() + " === CALL ::::: sap 호출은 하고 있다........");
								SAPProcess();
							}
						}
						
					} catch(NullPointerException e) {}
				}
			}
		};
		
		SAPthread.setName("SAPDaemonThread");		
		SAPthread.setDaemon(true);
		SAPthread.start();
	}
	
	
	/*
	 * 강제로 작업 시킨다.
	 * */
	public void ForceWork(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String sTime = request.getParameter("sTime");
		if(sTime==null || "".equals(sTime) || "null".equals(sTime)) {
			sTime = ComUtil.getCurDateTime("yyyyMMdd");
		}
		
		String SAPB2BINF = request.getParameter("SAPB2BINF");
		String SAPPRDSMST = request.getParameter("SAPPRDSMST");
		String SAPPRDMST = request.getParameter("SAPPRDMST");
		String SAPDPTMST = request.getParameter("SAPDPTMST");
		String SAPTEAMMST = request.getParameter("SAPTEAMMST");
		
		extlogger.debug("ForceWork Start!!");
//		System.out.println("String sTime = request.getParameter(" + sTime );
//		System.out.println("String SAPB2BINF = request.getParameter(" + SAPB2BINF );
//		System.out.println("String SAPPRDSMST = request.getParameter("+SAPPRDSMST );
//		System.out.println("String SAPPRDMST = request.getParameter("+SAPPRDMST );
//		System.out.println("String SAPDPTMST = request.getParameter("+SAPDPTMST );
//		System.out.println("String SAPTEAMMST = request.getParameter("+SAPTEAMMST );
		
		SAPProcess(sTime, SAPB2BINF, SAPPRDSMST, SAPPRDMST, SAPDPTMST, SAPTEAMMST);
		
		HttpPlatformResponse platformResponse = new HttpPlatformResponse(response, PlatformType.CONTENT_TYPE_XML, "UTF-8");

		DataSetList  outdslist = new DataSetList();
		if(!ComUtil.isProd()) {
			outdslist.add(retxml);
		}
		PlatformData output = new PlatformData();
		output.setDataSetList(outdslist);
		
		platformResponse.setData(output);
		platformResponse.sendData();
	}

	
	private void SAPProcess() {
		String st = ComUtil.getCurDateTime("yyyyMMdd");
		SAPProcess(st,"1","1","1","1","1");
	}
	
	private void SAPProcess(String st, String SAPB2BINF, String SAPPRDSMST, String SAPPRDMST, String SAPDPTMST, String SAPTEAMMST) {
		
		workTime = st;
		
		if(ComUtil.isProd()) {
			SAP_END_POINT = Const.SAP_END_POINT;
		} else {
			SAP_END_POINT = Const.TEST_SAP_END_POINT;
		}
		
		if(!ComUtil.isProd()) {
			retxml = new DataSet("DS_RETXML");
			retxml.addColumn("SEND_URL", DataTypes.STRING, 2000);
			retxml.addColumn("SEND_DATA", DataTypes.STRING, 2000);
			retxml.addColumn("RET_DATA", DataTypes.STRING, 2000);
		}
		if("1".equals(SAPB2BINF))
			this.fun_CSC_SAPB2BINF_RNI_N();
		if("1".equals(SAPDPTMST))
			this.fun_CSC_SAPDPTMST_RNI();
		if("1".equals(SAPPRDSMST))
			this.fun_CSC_SAPPRDSMST_RNI();
		if("1".equals(SAPPRDMST))
			this.fun_CSC_SAPPRDMST_RNI();
		if("1".equals(SAPTEAMMST))
			this.fun_CSC_SAPTEAMMST_RNI();
	}
	
//	CallCSC_SAPB2BINF_RNI_N 고객기본정보조회
	private void fun_CSC_SAPB2BINF_RNI_N() {
		HashMap savemap = new HashMap();		
		HashMap<String, String> upmap = new HashMap<String, String>();		
		HashMap<String, String> sendmap = new HashMap<String, String>();
		sendmap.put("TCODE", "");
		sendmap.put("TTYPE", "02");
		sendmap.put("KUNNR", "");
		sendmap.put("SPART", "");
		sendmap.put("REG_DATE", workTime);

		String startTime = ComUtil.getCurDateTime("yyyyMMddHHmmss");

		int eCount = 0;
		List list = new ArrayList();
		try {
			String[] ret = send("CallCSC_SAPB2BINF_RNI_N", sendmap);
			if("200".equals(ret[0])) {
				//System.out.println("111111111111++++++++++++++++++++++++++++++++  sap sap sap :::::::::: " + ret[2]);
				list = xmlRecordToList(makeDom(ret[2]) , "ZSCUST"); //<- 나중에 채워넣으시오...
				for(int i=0; i<list.size(); i++) {
					try {
						upmap = (HashMap) list.get(i);
						//sapetclogger.debug("[SAP DAEMON EXCEPTION] CallCSC_SAPB2BINF_RNI_N :::::::::: "+ upmap.toString());
						//if("".equals(upmap.get("ret_code")))
						commonDao.update("SAP_MASTER.SetCustInfo_UPDATE", upmap);
					} catch (ClassCastException se) {
						eCount++;
						sapetclogger.debug("[SAP DAEMON EXCEPTION] CallCSC_SAPB2BINF_RNI_N :::::::::: " + se + " : " + upmap.toString());
					}
				}
				if(eCount>0) {
					savemap.put("taxn_job_cdnm","정상 : 에러건 있음");
				} else {
					savemap.put("taxn_job_cdnm","정상");	
				}				
			} else {
				savemap.put("taxn_job_cdnm","비정상 : " + ret[0] +" : "+ ret[1]);
			}
		} catch (SQLException e) {
			savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
		} catch (Exception e) {
			savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
		}
		
		savemap.put("job_str_dtm",startTime);
		savemap.put("job_end_dtm",ComUtil.getCurDateTime("yyyyMMddHHmmss"));
		savemap.put("job_dv_cdnm","SAP배치 : 고객기본정보조회");
		savemap.put("scss_ncnt",list.size());
		savemap.put("err_ncnt",eCount);
		try {
			commonDao.insert("SAP_MASTER.SetResult_INSERT", savemap);
		} catch (SQLException e) {
		} catch (Exception e) {
		}
	}

//	CSC_SAPPRDSMST_RNI	제품군 마스터정보 조회
	private void fun_CSC_SAPPRDSMST_RNI() {
		HashMap savemap = new HashMap();		
		HashMap<String, String> upmap = new HashMap<String, String>();		
		HashMap<String, String> sendmap = new HashMap<String, String>();
		sendmap.put("TCODE", "");
		sendmap.put("TTYPE", "");
		
		int eCount = 0;
		List list = new ArrayList();
		String startTime = ComUtil.getCurDateTime("yyyyMMddHHmmss");

		try {
			String[] ret = send("CallCSC_SAPPRDSMST_RNI", sendmap);
			if("200".equals(ret[0])) {
				//System.out.println("222222222222++++++++++++++++++++++++++++++++  sap sap sap :::::::::: " + ret[2]);
				list = xmlRecordToList(makeDom(ret[2]) , "ZSPART"); //<- 나중에 채워넣으시오...
				for(int i=0; i<list.size(); i++) {
					try {
						upmap = (HashMap) list.get(i);
						commonDao.update("SAP_MASTER.SetPrdctGrp_UPDATE", upmap);
					} catch (ClassCastException se) {
						eCount++;
						sapetclogger.debug("[SAP DAEMON EXCEPTION] CallCSC_SAPPRDSMST_RNI exception SAP_MASTER.SetPrdctGrp_UPDATE :::::::::: " + se + " : " + upmap.toString());
					}
				}
				if(eCount>0) {
					savemap.put("taxn_job_cdnm","정상 : 에러건 있음");
				} else {
					savemap.put("taxn_job_cdnm","정상");	
				}				
			} else {
				savemap.put("taxn_job_cdnm","비정상 : " + ret[0] +" : "+ ret[1]);
			}
		} catch (SQLException e) {
			savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
			sapetclogger.debug("[SAP DAEMON EXCEPTION] fun_CSC_SAPPRDSMST_RNI exception :::::::::: " + e);
		} catch (Exception e) {
			savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
			sapetclogger.debug("[SAP DAEMON EXCEPTION] fun_CSC_SAPPRDSMST_RNI exception :::::::::: " + e);
		}

		savemap.put("job_str_dtm",startTime);
		savemap.put("job_end_dtm",ComUtil.getCurDateTime("yyyyMMddHHmmss"));
		savemap.put("job_dv_cdnm","SAP배치 : 제품군 마스터정보 조회");
		savemap.put("scss_ncnt",list.size());
		savemap.put("err_ncnt",eCount);
		try {
			commonDao.insert("SAP_MASTER.SetResult_INSERT", savemap);
		} catch (SQLException e) {	} catch (Exception e) {	}

	}
	
//	CSC_SAPPRDMST_RNI	제품 마스터정보 조회
	private void fun_CSC_SAPPRDMST_RNI() {
		List codelist = new ArrayList();
		try {
			codelist = commonDao.selectList("ORD010.GetPdtNm_SELECT", null);
		} catch (SQLException e) {
		} catch (Exception e) {
		}
		
		HashMap savemap = new HashMap();		
		HashMap<String, String> map = new HashMap<String, String>();
		HashMap<String, String> upmap = new HashMap<String, String>();		
		HashMap<String, String> sendmap = new HashMap<String, String>();
		
		int eCount = 0;
		int clCount = codelist.size();
		for(int i=0; i<clCount; i++) {
			map = (HashMap) codelist.get(i);
			sendmap.put("SPART", map.get("CODE"));
			sendmap.put("TCODE", "");
			sendmap.put("TTYPE", "");
			
			List list = new ArrayList();
			String startTime = ComUtil.getCurDateTime("yyyyMMddHHmmss");

			try {
				String[] ret = send("CallCSC_SAPPRDMST_RNI", sendmap);
				if("200".equals(ret[0])) {
					list = xmlRecordToList(makeDom(ret[2]) , "ZSMATNR"); //<- 나중에 채워넣으시오...
					//System.out.println("++++++++++++++++++++++++++++++++  CallCSC_SAPPRDMST_RNI SIZE :::::::::: " + map.get("CODE") + "  :::  " + list.size());
					for(int k=0; k<list.size(); k++) {
						try {
							upmap = (HashMap) list.get(k);
							commonDao.update("SAP_MASTER.SetPrdct_Dtls_UPDATE", upmap);
						} catch (ClassCastException se) {
							eCount++;
							sapetclogger.debug("[SAP DAEMON EXCEPTION] CallCSC_SAPPRDMST_RNI exception SAP_MASTER.SetPrdct_Dtls_UPDATE :::::::::: " + se + " : " + upmap.toString());
						}
					}
					if(eCount>0) {
						savemap.put("taxn_job_cdnm","정상 : 에러건 있음");
					} else {
						savemap.put("taxn_job_cdnm","정상");	
					}				
				} else {
					savemap.put("taxn_job_cdnm","비정상 : " + ret[0] +" : "+ ret[1]);
				}
			} catch (SQLException e) {
				savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
				sapetclogger.debug("[SAP DAEMON EXCEPTION] CallCSC_SAPPRDMST_RNI DOM exception SAP_MASTER.SetPrdct_Dtls_UPDATE :::::::::: " + e);
			} catch (Exception e) {
				savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
				sapetclogger.debug("[SAP DAEMON EXCEPTION] CallCSC_SAPPRDMST_RNI DOM exception SAP_MASTER.SetPrdct_Dtls_UPDATE :::::::::: " + e);
			}
			savemap.put("job_str_dtm",startTime);
			savemap.put("job_end_dtm",ComUtil.getCurDateTime("yyyyMMddHHmmss"));
			if(i==clCount-1)
				savemap.put("job_dv_cdnm","SAP배치 : 제품 마스터정보 조회(" + map.get("CODE") + "번코드 마지막)");
			else
				savemap.put("job_dv_cdnm","SAP배치 : 제품 마스터정보 조회(" + map.get("CODE") + "번코드)");
			savemap.put("scss_ncnt",list.size());
			savemap.put("err_ncnt",eCount);
			try {
				commonDao.insert("SAP_MASTER.SetResult_INSERT", savemap);
			} catch (SQLException e) {	} catch (Exception e) {}
		}
	}
	
//	CSC_SAPDPTMST_RNI	저유소 마스터정보 조회
	private void fun_CSC_SAPDPTMST_RNI() {
		HashMap savemap = new HashMap();		
		HashMap<String, String> upmap = new HashMap<String, String>();		
		HashMap<String, String> sendmap = new HashMap<String, String>();
		sendmap.put("TCODE", "");
		sendmap.put("TTYPE", "");
		
		int eCount = 0;
		List list = new ArrayList();
		String startTime = ComUtil.getCurDateTime("yyyyMMddHHmmss");

		try {
			String[] ret = send("CallCSC_SAPDPTMST_RNI", sendmap);
			if("200".equals(ret[0])) {
				list = xmlRecordToList(makeDom(ret[2]) , "ZSWERKS_1"); //<- 나중에 채워넣으시오...
				for(int i=0; i<list.size(); i++) {
					try {
						upmap = (HashMap) list.get(i);
						commonDao.update("SAP_MASTER.SetOilrsvInfo_UPDATE", upmap);
					} catch (ClassCastException se) {
						eCount++;
						sapetclogger.debug("[SAP DAEMON EXCEPTION] CallCSC_SAPDPTMST_RNI exception SAP_MASTER.SetOilrsvInfo_UPDATE :::::::::: " + se + " : " + upmap.toString());
					}
				}
				if(eCount>0) {
					savemap.put("taxn_job_cdnm","정상 : 에러건 있음");
				} else {
					savemap.put("taxn_job_cdnm","정상");	
				}				
			} else {
				savemap.put("taxn_job_cdnm","비정상 : " + ret[0] +" : "+ ret[1]);
			}
		} catch (ClassCastException e) {
			savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
			sapetclogger.debug("[SAP DAEMON EXCEPTION] fun_CSC_SAPPRDSMST_RNI exception :::::::::: " + e);
		} catch (Exception e) {
			savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
			sapetclogger.debug("[SAP DAEMON EXCEPTION] fun_CSC_SAPPRDSMST_RNI exception :::::::::: " + e);
		}
		savemap.put("job_str_dtm",startTime);
		savemap.put("job_end_dtm",ComUtil.getCurDateTime("yyyyMMddHHmmss"));
		savemap.put("job_dv_cdnm","SAP배치 : 저유소 마스터정보 조회");
		savemap.put("scss_ncnt",list.size());
		savemap.put("err_ncnt",eCount);
		try {
			commonDao.insert("SAP_MASTER.SetResult_INSERT", savemap);
		} catch (SQLException e) {	} catch (Exception e) {}

	}
	
//	CSC_SAPTEAMMST_RNI	팀코드 마스터정보 조회
	private void fun_CSC_SAPTEAMMST_RNI() {
		HashMap savemap = new HashMap();		
		HashMap<String, String> upmap = new HashMap<String, String>();		
		HashMap<String, String> sendmap = new HashMap<String, String>();
		sendmap.put("TCODE", "");
		sendmap.put("TTYPE", "");
		
		int eCount = 0;
		List list = new ArrayList();
		String startTime = ComUtil.getCurDateTime("yyyyMMddHHmmss");

		try {
			String[] ret = send("CallCSC_SAPTEAMMST_RNI", sendmap);
			if("200".equals(ret[0])) {
				list = xmlRecordToList(makeDom(ret[2]) , "ZSVKBUR1"); //<- 나중에 채워넣으시오...
				for(int i=0; i<list.size(); i++) {
					try {
						upmap = (HashMap) list.get(i);
						commonDao.update("SAP_MASTER.SetEnplcCd_UPDATE", upmap);
					} catch (ClassCastException se) {	
						eCount++;
						sapetclogger.debug("[SAP DAEMON EXCEPTION] CSC_SAPTEAMMST_RNI exception SAP_MASTER.SetEnplcCd_UPDATE :::::::::: " + se + " : " + upmap.toString());
					}
				}
				if(eCount>0) {
					savemap.put("taxn_job_cdnm","정상 : 에러건 있음");
				} else {
					savemap.put("taxn_job_cdnm","정상");	
				}				
			} else {
				savemap.put("taxn_job_cdnm","비정상 : " + ret[0] +" : "+ ret[1]);
			}
		} catch (SQLException e) {
			savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
			sapetclogger.debug("[SAP DAEMON EXCEPTION] fun_CSC_SAPPRDSMST_RNI exception :::::::::: " + e);
		} catch (Exception e) {
			savemap.put("taxn_job_cdnm","비정상 : " + e.toString());
			sapetclogger.debug("[SAP DAEMON EXCEPTION] fun_CSC_SAPPRDSMST_RNI exception :::::::::: " + e);
		}
		savemap.put("job_str_dtm",startTime);
		savemap.put("job_end_dtm",ComUtil.getCurDateTime("yyyyMMddHHmmss"));
		savemap.put("job_dv_cdnm","SAP배치 : 팀코드 마스터정보 조회");
		savemap.put("scss_ncnt",list.size());
		savemap.put("err_ncnt",eCount);
		try {
			commonDao.insert("SAP_MASTER.SetResult_INSERT", savemap);
		} catch (SQLException e) {	} catch (Exception e) {}
	}
	
	
	
	private String[] send(String sid, HashMap<String, String> map) throws Exception {
		
		StringBuffer xml = new StringBuffer(100);
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		xml.append("<soap12:Envelope ");
		xml.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		xml.append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ");
		xml.append("xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n");
		xml.append("<soap12:Body>\n");
		xml.append("<"+ sid +" "); //service ID
		xml.append("xmlns=\"http://eai.gsc.com/CSC\">\n");
		xml.append("<Req>\n");
		
		//data 시작		
		Iterator ir = map.keySet().iterator();
		String key;
		while(ir.hasNext()) {
			key = ir.next().toString();
			xml.append("<"); xml.append(key); xml.append(">"); 
			xml.append(map.get(key));
			xml.append("</"); xml.append(key); xml.append(">\n"); 
		}				
		xml.append("</Req>\n");
	    xml.append("</"+ sid +">\n"); //service ID
	    xml.append("</soap12:Body>\n");
	    xml.append("</soap12:Envelope>\n");
		
       	String[] ret = soapCall(xml.toString());
       	
       	if(!ComUtil.isProd()) {
	       	int row = retxml.newRow();
			retxml.set(row, "SEND_URL", SAP_END_POINT);
			retxml.set(row, "SEND_DATA", xml.toString());
			retxml.set(row, "RET_DATA", ret[2]);
       	}

       	return ret;
	}
	
	
	private String[] soapCall(String reqSoapXml) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        StringEntity reqEntity = null;
        HttpEntity   resEntity = null;
        String[] ret = new String[3];
        
        String startTime = ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss");
        
        try {
        	
        	HttpParams params = httpclient.getParams();
        	HttpConnectionParams.setConnectionTimeout(params, 300000);
        	HttpConnectionParams.setSoTimeout(params, 300000);
        	
            HttpPost httppost = new HttpPost(SAP_END_POINT);
            httppost.setHeader("SOAPAction", "");
    
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
	            	ret[2] = "";
                }
            } else {
	            ret[2] = "";
            }   
            //SAP ERP 재구축에 따른 테스트 
            saplogger.debug(ret[0]+"\n"+ret[1]+"\n"+ret[2]);
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
                sb.append("[SAP DAEMON][URL][");
                sb.append(SAP_END_POINT);
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
                //saplogger.debug(sb.toString());
            } catch(ClassCastException le) {}
        }
        return ret;
    }
	
	private List xmlRecordToList(Element root, String section) {
		
		NodeList nlist = root.getElementsByTagName(section);
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		Element item;
	    
	    if(nlist != null && nlist.getLength() > 0) {
	
	        for(int row=0; row<nlist.getLength(); row++) {
	        	item = (Element)nlist.item(row);
            	map = new HashMap<String, String>();
	        	for(Node column = item.getFirstChild(); column != null; column = column.getNextSibling()) {
		            switch(column.getNodeType()) {
		            case Node.ELEMENT_NODE:
	            		map.put(column.getNodeName().toLowerCase(), column.getTextContent());	            		
		                break;
		            default :
		            	break;
		            }		            
		        }								
	        	list.add(row, map);
	        }
	        
	        //data 있는지 첫번째 로우만  체크..
	        StringBuffer sb = new StringBuffer(100);
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
 
	        if(sb.length()==0)
	        	list = new ArrayList();	    
	    }
	    
	    return list;
	}
	
	
	private Element makeDom(String respXml) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//sparrow XXE 방지 추가
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);
		factory.setNamespaceAware(true);
		//factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(respXml)));
		Element root = doc.getDocumentElement();

		return root;
	}
}


