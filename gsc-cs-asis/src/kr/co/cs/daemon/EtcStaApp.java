package kr.co.cs.daemon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.sql.STRUCT;

import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.FileUploadIOException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.parsing.ParseState;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.jcraft.jsch.JSchException;
import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataSetList;
import com.tobesoft.xplatform.data.DataTypes;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.tx.HttpPlatformResponse;
import com.tobesoft.xplatform.tx.PlatformType;

import kr.co.cs.common.publicutil.FtpUtil;
import kr.co.cs.common.publicutil.SFTPUtil;
//import kr.co.cs.common.publicutil.SFTPUtil;
import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.persistence.dao.CommonDao;
import kr.co.cs.presentation.xcommon.XbaseAction;

/**
 * @author Administrator
 * 
 *         기타 통계배치 데몬 - 접촉이력 일별집계 - 상담사인원 일별집계
 * 
 */
public class EtcStaApp extends XbaseAction {

	private String strDlmtCrLf = "\n";
	// parsing 구분 및 계행	
	private String strWithDLmt = "|^";
	private String strWithCrLf = "|#$";
	private String pWorkDay = ComUtil.getCurDateTime("yyyyMMdd");
	private String pWorkD = "";
	private int resultCnt = 0;

	/*
	 * 생성자 was기동시에 시작되게..
	 */
	public EtcStaApp() {
		startEtcSta(true);
	}

	private final static Logger extlogger = LogManager.getLogger("process.ext");

	private CommonDao commonDao = null;

	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;
	}

	public void StartEtcStaDaemon(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("StartEtcStaDaemon()::called!");
		startEtcSta();
	}

	public void StopEtcStaDaemon(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("val", "N");
		paramMap.put("cd", "DM_ETCSTARUN");
		commonDao.update("Common.Set_DaemonState", paramMap);
	}

	public void EtcStaState(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		Iterator<Thread> ir = map.keySet().iterator();

		String alive = "N";
		while (ir.hasNext()) {
			Thread th = ir.next();
			if (th.getName().indexOf("EtcStaDaemonThread") >= 0) {
				System.out.println("Alive");
				alive = "Y";
			}
		}

		HttpPlatformResponse platformResponse = new HttpPlatformResponse(response, PlatformType.CONTENT_TYPE_XML, "UTF-8");
		VariableList outvlist = new VariableList();
		outvlist.add("EtcStaStatusMsg", alive);
		PlatformData output = new PlatformData();
		output.setVariableList(outvlist);
		System.out.println("EtcStaDaemonThread alive state=[" + alive + "]");
		platformResponse.setData(output);
		platformResponse.sendData();
	}

	private void startEtcSta() throws Exception {
		System.out.println("-->startEtcSta()::called!");

		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("val", "Y");
		paramMap.put("cd", "DM_ETCSTARUN");
		commonDao.update("Common.Set_DaemonState", paramMap);

		startEtcSta(true);
	}

	private void startEtcSta(Boolean a) {
		System.out.println("--->startEtcSta(true)::called!");

		try {
			String server_name = InetAddress.getLocalHost().getHostName().trim();
			if (!("___WKH-BT-N".equals(server_name) || "___wkh-bt-n".equals(server_name) || "CRMDEV".equals(server_name) || //개발계에는 요거 입력 하면 배치 확인됨.
					"co7a-2f4ace09f0".equals(server_name) || //개발자PC명 입력하면됨
					Const.WAS1NAME.equals(server_name) || Const.WAS2NAME.equals(server_name))) {
				System.out.println(new Date() + " : ETCSTA DAEMON STOP ::::: STOPING AS SERVER IS NOT PROD ........");
				extlogger.debug("[ETCSTA DAEMON STOP ::::: STOPING AS SERVER IS NOT PROD ........]");
				System.out.println("[ETCSTA DAEMON STOP ::::: STOPING AS SERVER IS NOT PROD ........]");
				return;
			}
		} catch (UnknownHostException e1) {
			return;
		}

		System.out.println(new Date() + " : ==========ETCSTA DAEMON GO ::::: GO GO GO NEW........");
		extlogger.debug("====================[ETCSTA DAEMON GO ::::: GO GO GO ........]");

		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		Iterator<Thread> ir = map.keySet().iterator();

		while (ir.hasNext()) {
			Thread th = ir.next();
			if (th.getName().indexOf("EtcStaDaemonThread") >= 0) {
				//th.interrupt();
				return;
			}
		}

		Thread etcStaThread = new Thread() {
			public void run() {
				HashMap<String, String> runmap = new HashMap<String, String>();
				runmap.put("cd", "DM_ETCSTARUN");
				String bb = null;

				SimpleDateFormat sdfDtm = new SimpleDateFormat("HHmm");
				Calendar calDay = Calendar.getInstance();
				String strCurTime = sdfDtm.format(calDay.getTime());

				int tloop = 0;
				int execloop = 0;

				while (true) {
					try {
						Thread.sleep(60000); // 1 분					
						tloop++;
						execloop++;

						//현재시각체크
						calDay = Calendar.getInstance();
						strCurTime = sdfDtm.format(calDay.getTime());

					} catch (InterruptedException e) {
					}
					try {
						if (tloop == 2) { //디비체크는2분에 한번타게끔..
							tloop = 0;
							bb = commonDao.selectString("Common.Get_DaemonRunning", runmap);
							//	extlogger.debug("[ETC STA DAEMON STOP ::::: START........]");					
							if ("N".equals(bb)) {
								extlogger.debug("[ETC STA DAEMON STOP ::::: STOP STOP STOP........]");
								execloop = 0; //loop 초기화 (아래 안타게..)
								break;
							}
						}
					} catch (InterruptedException e) {
						extlogger.debug("loop Exception ::" + e.getMessage());
					} catch (Exception e) {
						extlogger.debug("loop Exception ::" + e.getMessage());
					}
					
					try {
						if (execloop == 10) { //10분에 한번 호출..							
							execloop = 0;

							//분리보관 -1Day 체크						
							Calendar cal = new GregorianCalendar();
							cal.add(Calendar.DATE, -1);
							Date date = cal.getTime();

							SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
							pWorkD = formatter.format(date);

							//전날짜 처리
							extlogger.debug("pWorkD::" + pWorkD);

							if ("1820".equals(strCurTime.substring(0, 3))) { //일별배치는 18시20분대에 최초실행하고 23시30분에 다시한번 실행					
								extlogger.debug("EtcStaByDay:" + strCurTime.substring(0, 3));
								EtcStaByDay("", "1", "1");
							}

							if ("202".equals(strCurTime.substring(0, 3))) { //8시 20분경 콜백 통계 배치
								extlogger.debug("EtcStaByDay:" + strCurTime.substring(0, 3));
								EtcStaByDay("", "2", "2");
							}

							//장기 미접속 사용 계정 
							if ("212".equals(strCurTime.substring(0, 3))) {
								extlogger.debug("EtcStaByDay:" + strCurTime.substring(0, 3));
								EtcStaByDay("", "3", "3");
							}

							if ("233".equals(strCurTime.substring(0, 3))) { //23시30분대에 실행
								extlogger.debug("EtcStaByDay+EtcStaByMonth" + strCurTime.substring(0, 3));
								EtcStaByDay("", "1", "1");
								EtcStaByMonth("", "1", "1");
							}

							if ("232".equals(strCurTime.substring(0, 3))) { // 23시 20분대에 실행 파트 이력정보 매월말
								extlogger.debug("PartHistByMonth:" + strCurTime.substring(0, 3) + "<========>" + pWorkD);
								PartHistByMonth("", "1");
							}

							if ("003".equals(strCurTime.substring(0, 3))) { // 00시 30분대에 실행 매주 월요일날 실행
								extlogger.debug("LpMasterMigInsert===>Start!!!!" + strCurTime.substring(0, 3));
								LpMasterMigInsert("");
								LpMasterInfoUpdate("");
							}

							if ("010".equals(strCurTime.substring(0, 3))) { // 01시 00분대에 실행 접촉이력 상세 코드 매월 말
								extlogger.debug("BizDvCdMig===>Start!!!!" + strCurTime.substring(0, 3));
								BizDvCdMig("");
							}

							//월말 개인정보 삭제 배치
							if ("012".equals(strCurTime.substring(0, 3))) { //01시 20분로 수정 개인정보 분리보관 끝나고 배치 실행			
								extlogger.debug("EtcStaByMonthLast===>Start!!!!" + strCurTime.substring(0, 3));
								EtcStaByMonthLast(pWorkD, "2", ""); //개인정보 삭제 월말 배치 추가 20170331								
							}

							//분리 보관 배치 전날짜로 처리 해야 함.							
							//1. 대상자 수신 02시 10분에 실행
							if ("021".equals(strCurTime.substring(0, 3))) {
								extlogger.debug("1::" + pWorkDay + strCurTime.substring(0, 3) + "<========>" + pWorkD);
								Div_CustDw(pWorkD);
							}

							//2. 대상자 파일 다운로드 02시 30분에 실행
							if ("023".equals(strCurTime.substring(0, 3))) {
								extlogger.debug("2::" + pWorkDay + strCurTime.substring(0, 3) + "<========>" + pWorkD);
								Div_CustFileDw(pWorkD);
							}

							//3.  대상자 파일 전송 02시 50분에 실행 
							if ("025".equals(strCurTime.substring(0, 3))) {
								extlogger.debug("3::" + pWorkDay + strCurTime.substring(0, 3) + "<========>" + pWorkD);
								Div_CustFileSend(pWorkD);
							}

							//4. 분리보관 데이터 다운로드 및 전송/삭제    03시  10분에 실행
							if ("031".equals(strCurTime.substring(0, 3))) {
								extlogger.debug("4::" + pWorkDay + strCurTime.substring(0, 3) + "<========>" + pWorkD);
								Div_CustDataSend(pWorkD);

							}
						}

					} catch (NullPointerException e) {
					} catch (IOException e) {
					}
				}
			}
		};

		etcStaThread.setName("EtcStaDaemonThread");
		etcStaThread.setDaemon(true);
		etcStaThread.start();
	}

	/*
	 * 강제로 통계작업 시킨다.
	 */
	public void ForceWork(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//System.out.println("ForceWork::called");

		String sTime = request.getParameter("sTime");
		if (sTime == null || "".equals(sTime) || "null".equals(sTime)) {
			sTime = ComUtil.getCurDateTime("yyyyMMdd");
		}

		String sChkContHist = request.getParameter("CONTHIST"); //실행여부
		String sChkYnUsrHist = request.getParameter("USRHIST"); //실행여부
		String sChkUsrSttc = request.getParameter("USRSTTC"); //실행여부
		String sChkGrpSttc = request.getParameter("GRPSTTC"); //실행여부

		//일별작업
		EtcStaByDay(sTime, sChkContHist, sChkYnUsrHist);

		//월별작업
		EtcStaByMonth(sTime, sChkUsrSttc, sChkGrpSttc);

		//LP MASTER 통계 테이블 강제작업 2013.02
		//LpMasterMigInsert("");
		//LP MASTER 통계 테이블 강제작업 2013.02
		//LpMasterInfoUpdate("");

		//BizDvCdMig("");
	}

	//일별작업
	private void EtcStaByDay(String pWorkDay, String pChkContHist, String pChkUsrHist) {

		//System.out.println("EtcStaByDay::called");

		if (pWorkDay == null || "".equals(pWorkDay) || "null".equals(pWorkDay)) {
			pWorkDay = ComUtil.getCurDateTime("yyyyMMdd");
		}

		if ("1".equals(pChkContHist))
			ContactHistDay(pWorkDay); //접촉이력 일별집계

		if ("1".equals(pChkUsrHist))
			UserHistDay(pWorkDay); //상담사인원 일별집계

		if ("2".equals(pChkUsrHist))
			CbHistDay(pWorkDay); //콜백처리 일별집계

		if ("3".equals(pChkUsrHist))
			System.out.println("pWorkDay::" + pWorkDay);
			USRYnDay(pWorkDay); //장기 미사용자

	}

	//월별작업
	private void EtcStaByMonth(String pWorkDay, String pChkUsrSttc, String pChkGrpSttc) {
		extlogger.debug("EtcStaByMonth::called::" + pWorkDay);

		if (pWorkDay == null || "".equals(pWorkDay) || "null".equals(pWorkDay)) {
			//월의 마지막일자 계산
			Calendar calDay = Calendar.getInstance();
			int iLastDay = calDay.getActualMaximum(calDay.DAY_OF_MONTH);

			//현재 날짜를 셋팅
			pWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

			//월의 마지막이 아니면 실행 하지 않는다.
			String sMonthLastDay = pWorkDay.substring(0, 6) + Integer.toString(iLastDay);

			//월의 1일 계산
			int iFirstDay = calDay.getActualMinimum(calDay.DAY_OF_MONTH);
			String sMonthFistDay = pWorkDay.substring(0, 6) + "01"; //+Integer.toString(iFirstDay);

			//월의 마지막일이 아니면 실행안함
			extlogger.debug("-->sMonthFistDay::" + sMonthFistDay);
			extlogger.debug("2300 Start-->EtcStaByMonth::" + pWorkDay);

			//매월 마지막 날에만 배치 실행 
			if (!pWorkDay.equals(sMonthLastDay)) {
				extlogger.debug("0400 Start-->pWorkDay::" + pWorkDay + "//sMonthLastDay::" + sMonthLastDay);
				return;
			}

		} //END IF

		//강제작업요청시 또는 월의 마지막일자일 경우 실행		
		if ("1".equals(pChkUsrSttc)) {
			AsesUserSttcMM(pWorkDay); //월평가 상담원 월별집계
		}
		if ("1".equals(pChkGrpSttc)) {
			AsesGrpSttcMM(pWorkDay); //월평가 그룹 월별집계
		}
	}

	//매월 1일 개인정보 삭제 배치 실행(실행 일자는 전달 마지막 일자로) 1:20
	private void EtcStaByMonthLast(String pWorkD, String pChkUsrSttc, String pChkGrpSttc) {

		extlogger.debug("EtcStaByMonth::called::" + pWorkDay);

		pWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

		//월의 마지막일자 계산
		Calendar calDay = Calendar.getInstance();

		//월의 1일 계산
		int iFirstDay = calDay.getActualMinimum(calDay.DAY_OF_MONTH);
		String sMonthFistDay = pWorkDay.substring(0, 6) + "01"; //+Integer.toString(iFirstDay);

		//월의 마지막일이 아니면 실행안함			
		extlogger.debug("-->pDwork::" + pWorkD);
		extlogger.debug("-->sMonthFistDay::" + sMonthFistDay);

		//매월 초에 전월 마지막 날짜로 실행 한다.				

		if (!pWorkDay.equals(sMonthFistDay)) {
			extlogger.debug("NOT sMonthFistDay!!!!" + pWorkDay + "::" + sMonthFistDay);
			// System.out.println("NOT iFirstDay!!!!" +pWorkDay+"::"+sMonthFistDay );
			return;
		}

		if ("2".equals(pChkUsrSttc)) {
			extlogger.debug("-->PrivacyMtrDel START!!::" + pChkUsrSttc);
			PrivacyMtrDel(pWorkD); //전달 마직말 일자로 개인정보 삭제 해준다.
		}
	}

	//SP_BT_PRIV_EXEC    20160408 개인정보 삭제 

	private void PrivacyMtrDel(String pWorkDay) {

		try {
			extlogger.debug("Prc_BT_PRIVACY_MTR_DEL::  START !!" + pWorkDay);
			String sqlmapid = "ETC_STA.Prc_BT_PRIVACY_MTR_DEL";

			HashMap map = new HashMap();
			map.put("I_WORK_DT", pWorkDay);
			;
			map.put("I_USER_ID", "SYSTEM");
			commonDao.selectString(sqlmapid, map);

			String listresult = (String) map.get("O_RESULT2");

			extlogger.debug("listresult :: " + listresult);

		} catch (SQLException sqe) {
			extlogger.debug("sqe.getMessage :: " + sqe.getMessage());
		} catch (Exception e) {
			extlogger.debug("e.getMessage :: " + e.getMessage());
		} finally {
			// 배치작업종료로그출력
			extlogger.debug("Prc_BT_PRIVACY_MTR_DEL  END");
		}
	}

	/*
	 * 월말삭제 배치 SP분리 버전 SP오류 내용 확인 후 다시 원복 작업 20200410 원복 작업 20200507 //public
	 * void procBatch01(ActionMapping mapping, ActionForm form,
	 * HttpServletRequest request, HttpServletResponse response) throws
	 * Exception { private void PrivacyMtrDel(String pWorkDay) { try {
	 * 
	 * //pWorkDay = request.getParameter("pWorkDay"); if(pWorkDay==null ||
	 * "".equals(pWorkDay) || "null".equals(pWorkDay)) { pWorkDay =
	 * ComUtil.getCurDateTime("yyyyMMdd"); }
	 * 
	 * 
	 * 
	 * //조회
	 * 
	 * System.out.println("ETC_STA.Prc_BT_PRIVACY_MTR_SEL START!!"); String
	 * sqlmapid = "ETC_STA.Prc_BT_PRIVACY_MTR_SEL"; HashMap map = new HashMap();
	 * map.put("I_WORK_DT", pWorkDay);; map.put("I_USER_ID", "SYSTEM");
	 * map.put("I_JOB_CD", "1"); commonDao.selectString(sqlmapid, map);
	 * 
	 * String listresult = (String) map.get("O_RESULT2");
	 * 
	 * System.out.println("listresult :: "+listresult);
	 * extlogger.debug("listresult :: "+listresult);
	 * System.out.println("ETC_STA.Prc_BT_PRIVACY_MTR_SEL END!!");
	 * 
	 * //삭제 System.out.println("ETC_STA.Prc_BT_PRIVACY_MTR_DEL START!!"); String
	 * sqlmapid_del = "ETC_STA.Prc_BT_PRIVACY_MTR_DEL_1"; HashMap map_del = new
	 * HashMap(); map_del.put("I_WORK_DT", pWorkDay);; map_del.put("I_USER_ID",
	 * "SYSTEM"); map_del.put("I_JOB_CD", "2");
	 * commonDao.selectString(sqlmapid_del, map_del);
	 * 
	 * String listresult_del = (String) map_del.get("O_RESULT2");
	 * 
	 * System.out.println("listresult_del :: "+listresult_del);
	 * extlogger.debug("listresult_del :: "+listresult_del);
	 * System.out.println("ETC_STA.Prc_BT_PRIVACY_MTR_DEL END!!");
	 * 
	 * 
	 * 
	 * //증가 System.out.println("ETC_STA.Prc_BT_PRIVACY_MTR_INS START!!"); String
	 * sqlmapid_ins = "ETC_STA.Prc_BT_PRIVACY_MTR_INS"; HashMap map_ins = new
	 * HashMap(); map_ins.put("I_WORK_DT", pWorkDay);; map_ins.put("I_USER_ID",
	 * "SYSTEM"); map_ins.put("I_JOB_CD", "3");
	 * commonDao.selectString(sqlmapid_ins, map_ins);
	 * 
	 * String listresult_ins = (String) map_ins.get("O_RESULT2");
	 * 
	 * System.out.println("listresult_ins :: "+listresult_ins);
	 * extlogger.debug("listresult_ins :: "+listresult_ins);
	 * System.out.println("ETC_STA.Prc_BT_PRIVACY_MTR_INS END!!");
	 * 
	 * //누계 System.out.println("ETC_STA.Prc_BT_PRIVACY_MTR_AF START!!"); String
	 * sqlmapid_af = "ETC_STA.Prc_BT_PRIVACY_MTR_AF"; HashMap map_af = new
	 * HashMap(); map_af.put("I_WORK_DT", pWorkDay);; map_af.put("I_USER_ID",
	 * "SYSTEM"); map_af.put("I_JOB_CD", "4");
	 * commonDao.selectString(sqlmapid_af, map_af);
	 * 
	 * String listresult_af = (String) map_af.get("O_RESULT2");
	 * 
	 * System.out.println("map_af :: "+listresult_af);
	 * extlogger.debug("map_af :: "+listresult_af);
	 * System.out.println("ETC_STA.Prc_BT_PRIVACY_MTR_AF END!!");
	 * 
	 * }catch(SQLException sqe){
	 * System.out.println("sqe.getMessage :: "+sqe.getMessage());
	 * extlogger.debug("sqe.getMessage :: "+sqe.getMessage()); } catch
	 * (Exception e) { extlogger.debug("e.getMessage :: "+e.getMessage());
	 * System.out.println("e.getMessage :: "+e.getMessage()); }finally { //
	 * 배치작업종료로그출력 System.out.println("Prc_BT_PRIVACY_MTR_DEL  END");
	 * extlogger.debug("Prc_BT_PRIVACY_MTR_DEL  END"); } }
	 */
	//파트이력월별등록
	private void PartHistByMonth(String pWorkDay, String pChkCnslrPartIns) {

		//System.out.println("EtcStaByMonth::called");

		if (pWorkDay == null || "".equals(pWorkDay) || "null".equals(pWorkDay)) {
			//자동실행일경우에만 들어옴
			pWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

			//월의 마지막일자 계산
			Calendar calDay = Calendar.getInstance();
			int iLastDay = calDay.getActualMaximum(calDay.DAY_OF_MONTH);
			String sMonthLastDay = pWorkDay.substring(0, 6) + Integer.toString(iLastDay);
			//월의 마지막일이 아니면 실행안함
			//System.out.println("-->pWorkDay::" + pWorkDay);
			//System.out.println("-->sMonthLastDay::" + sMonthLastDay);
			if (!pWorkDay.equals(sMonthLastDay))
				return;
		}

		if ("1".equals(pChkCnslrPartIns))
			PartHistInsrt();
	}

	//콜백 일별 집계생성
	private void CbHistDay(String pWorkDay) {
		try {
			//System.out.println("-->ContactHistDay::start");
			extlogger.debug("[ETCSTA CbHistDay pWorkDay :::::]" + pWorkDay);
			String sqlmapid = "ETC_STA.Prc_CB_HIST_DAY_SELECT";
			HashMap map = new HashMap();
			map.put("I_WORK_DAY", pWorkDay); //작업일자
			commonDao.selectString(sqlmapid, map);

			//System.out.println("ContactHistDay::end");
		} catch (SQLException se) {
			extlogger.debug("CbHistDay SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("CbHistDay IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("CbHistDay EXCEPTION :: " + e.getMessage());
		}
	}

	//접촉이력 일별집계생성
	private void ContactHistDay(String pWorkDay) {
		try {
			extlogger.debug("===>ContactHistDay::start");
			String sqlmapid = "ETC_STA.Prc_CONTACT_HIST_DAY_SELECT";
			HashMap map = new HashMap();
			map.put("I_WORK_DAY", pWorkDay); //작업일자
			commonDao.selectString(sqlmapid, map);

		} catch (SQLException se) {
			extlogger.debug("ContactHistDay SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("ContactHistDay IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("ContactHistDay EXCEPTION :: " + e.getMessage());
		}
	}

	//상담사인원 일별집계생성
	private void UserHistDay(String pWorkDay) {
		try {
			extlogger.debug("===>UserHistDay::start");
			String sqlmapid = "ETC_STA.Prc_USER_HIST_DAY_SELECT";
			HashMap map = new HashMap();
			map.put("I_WORK_DAY", pWorkDay); //작업일자
			commonDao.selectString(sqlmapid, map);

		} catch (SQLException se) {
			extlogger.debug("UserHistDay SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("UserHistDay IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("UserHistDay EXCEPTION :: " + e.getMessage());
		}
	}

	//장기 미접속 계정 20201116
	private void USRYnDay(String pWorkDay) {
		try {
			extlogger.debug("===>USRYnDay::start");
			String sqlmapid = "ETC_STA.Prc_BT_USR_YN_UPD";
			HashMap map = new HashMap();
			map.put("I_WORK_DT", pWorkDay); //작업일자
			map.put("I_USER_ID", "Batch"); //작업일자			
			commonDao.selectString(sqlmapid, map);
			extlogger.debug("===>USRYnDay::end");
		} catch (SQLException se) {
			extlogger.debug("USRYnDay SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("USRYnDay IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("USRYnDay EXCEPTION :: " + e.getMessage());
		}
	}

	//월평가 상담원 월별집계생성
	private void AsesUserSttcMM(String pWorkDay) {
		try {
			extlogger.debug("===>AsesUserSttcMM::start");
			String sWorkYm = pWorkDay.substring(0, 6); //작업년월

			String sqlmapid = "ETC_STA.Prc_CNSLR_STTC_MM_SELECT";
			HashMap map = new HashMap();
			map.put("I_WORK_YM", sWorkYm); //작업년월
			commonDao.selectString(sqlmapid, map);

		} catch (SQLException se) {
			extlogger.debug("AsesUserSttcMM SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("AsesUserSttcMM IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("AsesUserSttcMM EXCEPTION :: " + e.getMessage());
		}
	}

	//월평가 그룹 월별집계생성
	private void AsesGrpSttcMM(String pWorkDay) {
		try {
			extlogger.debug("AsesGrpSttcMM::start");
			String sWorkYm = pWorkDay.substring(0, 6); //작업년월

			String sqlmapid = "ETC_STA.Prc_GRP_STTC_MM_SELECT";
			HashMap map = new HashMap();
			map.put("I_WORK_YM", sWorkYm); //작업년월
			commonDao.selectString(sqlmapid, map);

		} catch (SQLException se) {
			extlogger.debug("AsesGrpSttcMM SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("AsesGrpSttcMM IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("AsesGrpSttcMM EXCEPTION :: " + e.getMessage());
		}
	}

	// 파트이력등록(QA통계에서 사용)
	private void PartHistInsrt() {
		try {
			extlogger.debug("-->ContactHistDay::start");
			Calendar calDay = Calendar.getInstance();

			// 월말에 다음월의 파트정보를 등록한다.
			calDay.add(calDay.MONTH, 1);
			int YY = calDay.get(calDay.YEAR);
			int MM = calDay.get(calDay.MONTH) + 1;
			String NextMon = Integer.toString(MM);

			if (NextMon.length() == 1) {
				NextMon = "0" + NextMon;
			}

			String pRegYm = Integer.toString(YY) + NextMon;
			String sqlmapid = "ETC_STA.Prc_PART_HIST_MONTH_INSERT";
			HashMap map = new HashMap();
			map.put("I_WORK_YM", pRegYm); //작업일자
			commonDao.insert(sqlmapid, map);

		} catch (SQLException se) {
			extlogger.debug("PartHistInsrt SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("PartHistInsrt IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("PartHistInsrt EXCEPTION :: " + e.getMessage());
		}
	}

	//LP MASTER 통계자료 2013.02 ARS ->MIG테이블로 일괄 입력
	private void LpMasterMigInsert(String pWorkDay) {

		if (pWorkDay == null || "".equals(pWorkDay) || "null".equals(pWorkDay)) {

			pWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

			String year = pWorkDay.substring(0, 4);
			String month = pWorkDay.substring(4, 6);
			String day = pWorkDay.substring(6, 8);
			if ("01".equals(day)) {
				month = Integer.toString(Integer.parseInt(month) - 1);
				if (month.length() == 1) {
					month = "0" + month;
				}
				if ("00".equals(month)) {
					year = Integer.toString(Integer.parseInt(year) - 1);
					month = "12";
				}
				if ("01".equals(month) || "03".equals(month) || "05".equals(month) || "07".equals(month) || "08".equals(month) || "10".equals(month) || "12".equals(month)) {
					day = "31";
				} else if ("02".equals(month)) {
					if (IsLeapYear() == "true") {
						day = "29";
					} else {
						day = "28";
					}
				} else {
					day = "30";
				}
			} else {
				day = Integer.toString(Integer.parseInt(day) - 1);
				if (day.length() == 1) {
					day = "0" + day;
				}
			}

			pWorkDay = year + month + day;
		}

		try {
			extlogger.debug("pWorkDay" + pWorkDay);
			String sqlmapid = "ETC_STA.Prc_LP_MASTER_MIG_SELECT";
			HashMap map = new HashMap();
			map.put("I_WORK_DAY", pWorkDay);
			commonDao.selectString(sqlmapid, map);
		} catch (SQLException se) {
			extlogger.debug("LP_MASTER SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("LP_MASTER IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("LP_MASTER EXCEPTION :: " + e.getMessage());
		}

	}

	//LP MASTER 등록완료 데이터 삭제 2013.02
	//매주 월요일 삭제
	private void LpMasterInfoUpdate(String pWorkDay) {

		extlogger.debug("LpMasterInfoUpdate::" + pWorkDay);
		if (pWorkDay == null || "".equals(pWorkDay) || "null".equals(pWorkDay)) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Calendar calDay = Calendar.getInstance();

			int weekDay = calDay.get(Calendar.DAY_OF_WEEK);
			int monthDay = calDay.get(Calendar.DAY_OF_MONTH);

			calDay.set(Calendar.DAY_OF_MONTH, monthDay - 7);
			pWorkDay = formatter.format(calDay.getTime());
			if (weekDay != 2) {
				extlogger.debug("not monday!!");
				return;
			}
		}

		try {
			extlogger.debug("==============>LpMasterInfoUpdate::start     pWorkDay::" + pWorkDay);
			String sqlmapid = "ETC_STA.Prc_LP_MASTER_DATA_DEL";
			HashMap map = new HashMap();
			map.put("I_WORK_DAY", pWorkDay);
			commonDao.selectString(sqlmapid, map);
		} catch (SQLException se) {
			extlogger.debug("LpMasterInfoUpdate SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("LpMasterInfoUpdate IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("LpMasterInfoUpdate EXCEPTION :: " + e.getMessage());
		}
	}

	//윤년여부 구하기 2013.08
	public String IsLeapYear() {
		Date date = new Date();
		int year = date.getYear();
		if (year % 4 == 0) {
			if ((year % 100) != 0 || (year % 400) == 0) {
				return "true";
			} else {
				return "fasle";
			}
		} else {
			return "false";
		}
	}

	//접촉이력 업무상세코드 통계데이터 MIG 2013.12
	private void BizDvCdMig(String pWorkDay) {

		if (pWorkDay == null || "".equals(pWorkDay) || "null".equals(pWorkDay)) {
			pWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

			String year = pWorkDay.substring(0, 4);
			String month = pWorkDay.substring(4, 6);
			String day = pWorkDay.substring(6, 8);
			if ("01".equals(day)) {
				month = Integer.toString(Integer.parseInt(month) - 1);
				if (month.length() == 1) {
					month = "0" + month;
				}
				if ("00".equals(month)) {
					year = Integer.toString(Integer.parseInt(year) - 1);
					month = "12";
				}
				if ("01".equals(month) || "03".equals(month) || "05".equals(month) || "07".equals(month) || "08".equals(month) || "10".equals(month) || "12".equals(month)) {
					day = "31";
				} else if ("02".equals(month)) {
					if (IsLeapYear() == "true") {
						day = "29";
					} else {
						day = "28";
					}
				} else {
					day = "30";
				}
			} else {
				day = Integer.toString(Integer.parseInt(day) - 1);
				if (day.length() == 1) {
					day = "0" + day;
				}
			}

			pWorkDay = year + month + day;
		}

		try {
			extlogger.debug("-->BizDvCdMig::start");
			String sqlmapid = "STA150.Prc_Biz_Dv_Cd_Mig";
			HashMap map = new HashMap();
			map.put("I_WORK_DAY", pWorkDay);
			commonDao.selectString(sqlmapid, map);
			extlogger.debug(map.get("O_RESULT"));
			extlogger.debug("-->BizDvCdMig::end");
		} catch (SQLException se) {
			extlogger.debug("BizDvCdMig SQLEXCEPTION ::" + se.getMessage());
		} catch (IOException ie) {
			extlogger.debug("BizDvCdMig IOEXCEPTION ::" + ie.getMessage());
		} catch (Exception e) {
			extlogger.debug("BizDvCdMig EXCEPTION :: " + e.getMessage());
		}
	}

	/*
	 * 강제로 biz_dv_mig 작업 시킨다.
	 */
	public void ForceWork_BizDv(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		String date = request.getParameter("date");
		if (date == null || "".equals(date) || "null".equals(date)) {
			date = ComUtil.getCurDateTime("yyyyMMdd");
		}

		BizDvCdMig(date);
	}

	/*
	 * GSC분리보관 테이블에서 대상자를 ICCS_CSC DB로 수신한다
	 */
	public void Div_CustDw(String pWorkD) {

		extlogger.debug("## GSC WITHDRAW CUST INFO BATCH START !! ##");

		String withdraw_url = "";
		String withdraw_account = "";
		String withdraw_password = "";

		try {
			String serverNm = InetAddress.getLocalHost().getHostName().trim();

			//extlogger.debug("server name :: " + serverNm);
			//분리보관 DB 계정
			if ("CRMDEV".equals(serverNm)) {
				withdraw_url = Const.WITHDRAW_TEST_DB_URL;
				withdraw_account = Const.WITHDRAW_TEST_DB_ACCOUNT;
				withdraw_password = Const.WITHDRAW_TEST_DB_PASSWORD;
			} else if (Const.WAS1NAME.equals(serverNm)) {
				withdraw_url = Const.WITHDRAW_DB_URL;
				withdraw_account = Const.WITHDRAW_DB_ACCOUNT;
				withdraw_password = Const.WITHDRAW_DB_PASSWORD;
			}
		} catch (UnknownHostException e) {
			extlogger.debug("HOST EXCEPTION !! :: " + e.getMessage());
		}

		String WorkDay = "";

		if (!(pWorkD == null || pWorkD.equals(""))) {
			WorkDay = pWorkD;
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData md = null;
		StringBuffer sb = null;

		HashMap<String, String> map = new HashMap<String, String>();
		int roofCnt = 0;

		try {
			//mssql jdbc
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			DriverManager.setLoginTimeout(10);

			//mssql 연결
			conn = DriverManager.getConnection(withdraw_url, withdraw_account, withdraw_password);

			//쿼리 생성
			sb = withdrawMssqlQuery();

			//쿼리 실행
			ps = conn.prepareStatement(sb.toString());
			ps.setQueryTimeout(10);
			ps.setString(1, WorkDay);

			//결과값
			rs = ps.executeQuery();

			while (rs.next()) {
				if (rs.getRow() > 0) {
					if (roofCnt == 0) {
						md = rs.getMetaData();
					}

					if (md != null) {
						for (int i = 1; i <= md.getColumnCount(); i++) {
							map.put(md.getColumnName(i).toLowerCase(), rs.getString(i) == null ? "" : rs.getString(i));
						}
					}

					/*
					 * 탈회대상 값(map) 출력 for(String key : map.keySet()) { String
					 * value = map.get(key); extlogger.debug("key :: " + key +
					 * "\t||\t" + "value :: " + value); }
					 */

					try {
						//extlogger.debug("roofCnt :: " + roofCnt);
						extlogger.debug("## 탈회대상 DB Insert 작업 실행 !!");
						//탈회대상 고객 CSC DB insert
						commonDao.insert("ETC_STA.withDrawUserInfo_INSERT", map);
					} catch (SQLException se) {
						extlogger.debug("[ETC DAEMON STA ERROR : WITHDRAW CUST.  " + se.toString() + "][DATA : " + map + "]");
					}

					roofCnt++;
				}
			}

		} catch (SQLException se) {
			extlogger.debug("SQL EXCEPTION ERROR : " + se.toString());
		} catch (Exception e) {
			extlogger.debug("JDBC CONNECTION ERROR : " + e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e2) {
					extlogger.debug(e2.getMessage());
				} catch (Exception e) {
					extlogger.debug(e.getMessage());
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e2) {
					extlogger.debug(e2.getMessage());
				} catch (Exception e) {
					extlogger.debug(e.getMessage());
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e2) {
					extlogger.debug(e2.getMessage());
				} catch (Exception e) {
					extlogger.debug(e.getMessage());
				}
			}
			if (sb != null) {
				try {
					sb.delete(0, sb.length());
				} catch (ClassCastException e2) {
					extlogger.debug(e2.getMessage());
				} catch (Exception e) {
					extlogger.debug(e.getMessage());
				}
			}
		}

		/*
		 * 분리보관 DB 변경으로 인한 수정 처리 / 2024.04.11 | NP847 if(pWorkD==null ||
		 * "".equals(pWorkD) || "null".equals(pWorkD)) { //분리보관 -1Day 체크
		 * Calendar cal = new GregorianCalendar(); cal.add(Calendar.DATE,-1);
		 * Date date = cal.getTime();
		 * 
		 * SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");
		 * pWorkD = formatter.format(date);
		 * 
		 * System.out.println("::::::NowDay :"+pWorkDay+"- 1Day====>"+pWorkD);
		 * 
		 * } // pWorkDay = "20170807"; //테스트 try {
		 * extlogger.debug("======> 1) Div_CustDw::start==>"+pWorkD);
		 * System.out.println("-->Div_CustDw::start"+pWorkD); //테스트 //pWorkDay =
		 * "20170728";
		 * 
		 * String sqlmapid = "ETC_STA.DPXGZA2"; HashMap map = new HashMap();
		 * map.put("I_WORK_DT", pWorkD); commonDao.selectString(sqlmapid, map);
		 * 
		 * System.out.println("pWorkD Cnt :: "+pWorkD+"::"+map.get("O_RESULT"));
		 * extlogger.debug("======> 1) Div_CustDw::END"); }catch(SQLException
		 * sqe){ System.out.println("sqe.getMessage :: "+sqe.getMessage());
		 * extlogger.debug("sqe.getMessage :: "+sqe.getMessage()); } catch
		 * (Exception e) { extlogger.debug("e.getMessage :: "+e.getMessage());
		 * System.out.println("e.getMessage :: "+e.getMessage()); }finally { //
		 * 배치작업종료로그출력 System.out.println("Div_CustDw END"); }
		 */

	}

	//public void procBatch35(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	public void Div_CustFileDw(String pWorkD) {
		String strErrMsg = "";

		/*
		 * 수동 임시 테스트
		 * System.out.println("WorkDay ::"+request.getParameter("WorkDay"));
		 * pWorkD = request.getParameter("WorkDay");
		 */

		/*
		 * null이 올수 없다. if(pWorkD==null || "".equals(pWorkD) ||
		 * "null".equals(pWorkD)) { //분리보관 -1Day 체크 Calendar cal = new
		 * GregorianCalendar(); cal.add(Calendar.DATE,-1); Date date =
		 * cal.getTime();
		 * 
		 * SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMdd");
		 * pWorkD = formatter.format(date);
		 * 
		 * System.out.println("::::::NowDay :"+pWorkDay+"- 1Day====>"+pWorkD);
		 * 
		 * }
		 */
		// 
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

		extlogger.debug("======>2) Div_CustFileDw::start==>" + pWorkD);
		extlogger.debug("======>file name Day  bWorkDay:::::" + bWorkDay);

		try {
			// -------------------------------------------------------------
			//복원 삭제 대상자 
			// -------------------------------------------------------------			

			HashMap map = new HashMap();
			HashMap map1 = new HashMap();
			HashMap map2 = new HashMap();

			String sqlmapid = "ETC_STA.DPXGZA4";
			map.put("I_WORK_DT", pWorkD); //익일 전날짜 처리 
			commonDao.selectString(sqlmapid, map);

			//commonDao.selectString(sqlmapid, map);			
			//List list1 = (ArrayList)map.get("O_RESULT"); // 

			//01:휴면 02:파기 03:복원 04:탈회
			List listDeCust = (ArrayList) map.get("O_RESULT1"); // 삭제 대상01분리보관 02파기 04탈회 대상자를 가지고 온다
			List listReCust = (ArrayList) map.get("O_RESULT2"); // 복원 대상 03복원 대상자를 가지고 온다.

			extlogger.debug("strDeCust : " + listDeCust);
			extlogger.debug("strDeCust : " + listReCust);

			ArrayList list1 = (ArrayList) map1.get("O_RESULT1");
			ArrayList list2 = (ArrayList) map2.get("O_RESULT2");

			HashMap<String, String> mapRow1 = null;
			HashMap<String, String> mapRow2 = null;

			StringBuffer strSql1 = new StringBuffer();
			StringBuffer strSql2 = new StringBuffer();

			for (int i = 0; i < listDeCust.size(); i++) {
				mapRow1 = (HashMap<String, String>) listDeCust.get(i);
				strSql1.append(mapRow1.get("G_CUST_NO")); //통합 고객 번호
				strSql1.append(strWithDLmt); //구분   
				strSql1.append(mapRow1.get("K_CUST_NO")); //자사 고객번호
				strSql1.append(strWithCrLf); //개행
			}

			for (int i = 0; i < listReCust.size(); i++) {
				mapRow2 = (HashMap<String, String>) listReCust.get(i);
				strSql2.append(mapRow2.get("G_CUST_NO"));
				strSql2.append(strWithDLmt); //구분   
				strSql2.append(mapRow2.get("K_CUST_NO"));
				strSql2.append(strWithCrLf); //개행
			}

			String filePath = Const.WITH_GSIB_DIV_DOWN + pWorkD + File.separator; //파일 패스는 전날짜

			//파기
			String tbName1 = Const.WITH_GSIB_DE_CUST + "." + bWorkDay; //파일이름은 생성일로...20180515 정구식K 확인 
			//복원
			String tbName2 = Const.WITH_GSIB_RE_CUST + "." + bWorkDay; //파일이름은 생성일로...20180515 정구식K 확인 

			extlogger.debug("bWorkDay::::" + bWorkDay);
			extlogger.debug("tbName1===>" + tbName1);
			extlogger.debug("tbName1===>" + tbName2);

			createFile(strSql1.toString(), tbName1, pWorkD, filePath);
			createFile(strSql2.toString(), tbName2, pWorkD, filePath);

			extlogger.debug("======>2) Div_CustFileDw::END");
		} catch (SQLException sqe) {
			strErrMsg = "Exception Error!:" + sqe.getMessage();
			extlogger.debug(strErrMsg);

		} catch (Exception e) {
			strErrMsg = "Exception Error!:" + e.getMessage();
			extlogger.debug(strErrMsg);
		} finally {
			// 
			// 배치작업종료로그출력
			extlogger.debug("Div_CustFileDw END");

		}
	}

	//분리보관 대상자 파일 분리보관서버로 전송
	public void Div_CustFileSend(String pWorkD) throws IOException {

		String strErrMsg = "";
		boolean bDnload = false;

		if (pWorkD == null || "".equals(pWorkD) || "null".equals(pWorkD)) {
			//분리보관 -1Day 체크						
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DATE, -1);
			Date date = cal.getTime();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			pWorkD = formatter.format(date);

			extlogger.debug("::::::NowDay :" + pWorkDay + "- 1Day====>" + pWorkD);

		}

		//file name을 위해 현재 날짜
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

		String filePath1 = "";
		String filePath2 = "";
		String fileName1 = "";
		String fileName2 = "";
		extlogger.debug("======>3) Div_CustFileSend::STAR bWorkDay==>" + bWorkDay);
		extlogger.debug("======>3) Div_CustFileSend::STAR pWorkD::==>" + pWorkD);

		// ------------------------------------
		// 파일 cmsftp로 전송
		// ------------------------------------
		String FTP_IP;
		String FTP_ID;
		String FTP_PWD;
		String FTP_ROOT;
		int FTP_PORT = 0;
		try {
			// cmsftp접속계정
			if (ComUtil.isProd()) // 운영계
			{
				FTP_IP = Const.SFTP_IP;
				FTP_ID = Const.SFTP_ID;
				FTP_PWD = Const.SFTP_PWD;
				FTP_ROOT = Const.SFTP_SEND;
				FTP_PORT = Const.SFTP_PORT;
			} else // 개발계
			{
				FTP_IP = Const.TEST_SFTP_IP;
				FTP_ID = Const.TEST_SFTP_ID;
				FTP_PWD = Const.TEST_SFTP_PWD;
				FTP_ROOT = Const.TEST_SFTP_SEND;
				FTP_PORT = Const.TEST_SFTP_PORT;
			}

			//sftp 연결
			extlogger.debug("FTP . IP=>[" + FTP_IP + "], ID=>[" + FTP_ID + "], PWD=>[" + FTP_PWD + "], ROOT=>[" + FTP_ROOT + "], PORT=>[" + FTP_PORT + "]");
			//파기/복원 대상 파일 다운로드 
			filePath1 = Const.WITH_GSIB_DIV_DOWN + pWorkD + File.separator;

			//파일 이름은 생성일 기준으로 수정 해야 한다. pWorkD=>pWorkDay

			fileName1 = Const.WITH_GSIB_RE_CUST + "." + bWorkDay; //복원 대상 파일 name
			fileName2 = Const.WITH_GSIB_DE_CUST + "." + bWorkDay; //파기 대상 파일 name

			extlogger.debug("Div_CustFileSend filePath1:" + filePath1);
			extlogger.debug("Div_CustFileSend fileName1:" + fileName1);
			extlogger.debug("Div_CustFileSend fileName2:" + fileName2);

			SFTPUtil sftpUtil = new SFTPUtil();
			sftpUtil.init(FTP_IP, FTP_ID, FTP_PWD, FTP_PORT);

			boolean bUpload1 = sftpUtil.upload(FTP_ROOT, filePath1 + fileName1);
			boolean bUpload2 = sftpUtil.upload(FTP_ROOT, filePath1 + fileName2);

			sftpUtil.disconnection();

			System.out.println("===Div_CustFileSend END");

			extlogger.debug("======>3) Div_CustFileSend::STAR==>" + pWorkD);
			extlogger.debug("Send File result1=>>[" + bUpload1 + "]");
			extlogger.debug("Send File result2=>>[" + bUpload2 + "]");
		} catch (NullPointerException e) {
			extlogger.debug("Div_CustFileSend Exception :: " + e.getMessage());
		} finally {

		}
	}

	// DB 에 업로드된 고객번호를 기반으로 대상 정보를 추출 하여 생성하고, 데이터를 삭제 한다.
	// 파기(02) 고객도 분리보관 대상자 전송할때 같이 전송 한다.
	// ----------------------------------------------------------------------------		
	public void Div_CustDataSend(String pWorkD) {
		/* 수동 임시 테스트 */
		//pWorkD = request.getParameter("WorkDay");		

		String strErrMsg = "";
		boolean bDnload = false;

		//file name을 위해 현재 날짜
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

		//화면에서 수동으로 돌리기 위해 날짜를 받는다.			
		if (pWorkD == null || "".equals(pWorkD) || "null".equals(pWorkD)) {
			//분리보관 -1Day 체크						
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DATE, -1);
			Date date = cal.getTime();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			pWorkD = formatter.format(date);

			extlogger.debug("Div_CustDataSend::pWorkD----->" + pWorkD);

		}

		extlogger.debug("======> 0310:: Div_CustDataSend bWorkDay::==>" + bWorkDay);
		extlogger.debug("======> 0310:: Div_CustDataSend pWorkD::==>" + pWorkD);

		try {
			// 배치작업개시로그		

			// -------------------------------------------------------------
			// DB 개인정보 삭제 대상 테이블 정보 조회
			// -------------------------------------------------------------

			extlogger.debug("======>4) Div_CustDataSend::STAR==>" + pWorkD);
			//extlogger.debug("======> bWorkDay::==>"+bWorkDay);
			//extlogger.debug("======> pWorkD::==>"+pWorkD);

			String sqlmapid = "ETC_STA.DPXGZA0"; //테이블 목록 조회
			HashMap map = new HashMap();

			//대상 테이블 목록을 조회 한다.
			commonDao.selectString(sqlmapid, map);
			List list1 = (ArrayList) map.get("O_RESULT"); // 

			//i_tot_cnt = list1.size();
			//대상 테이블 별 SP 생성

			extlogger.debug("[DPXGZA0] Target Table Select = [" + list1.size() + "]");

			if (list1.size() > 0) {

				HashMap<String, String> mapRow1 = null;
				HashMap<String, String> mapRow2 = null;
				HashMap<String, String> mapRow3 = null;

				String tbName = "";
				String owner = "";
				for (int i = 0; i < list1.size(); i++) {

					mapRow1 = (HashMap<String, String>) list1.get(i);

					tbName = mapRow1.get("TABLE_NAME");
					owner = mapRow1.get("OWNER");

					//대상 테이블 컬럼 Name가지고 오기
					sqlmapid = "ETC_STA.DPXGZA1";
					//GSIB 인지 한번더 체크 
					if (owner.equals("GSIB")) {

						HashMap map2 = new HashMap();
						HashMap map3 = new HashMap();

						map2.put("I_OWNER", owner);
						map2.put("I_TABLE_NAME", tbName);

						commonDao.selectString(sqlmapid, map2);

						ArrayList list2 = (ArrayList) map2.get("O_RESULT"); //

						//extlogger.debug( "Table column select = [" +list2.size()+"]");

						StringBuffer strSql1 = new StringBuffer();
						StringBuffer strSql2 = new StringBuffer();

						strSql2.append(" SELECT ");
						for (int j = 0; j < list2.size(); j++) {

							mapRow2 = (HashMap<String, String>) list2.get(j);
							strSql1.append(mapRow2.get("COLUMN_NAME"));
							strSql1.append(strWithDLmt); // |^ 컬럼 파싱

							strSql2.append(mapRow2.get("COLUMN_NAME"));
							if (j < list2.size() - 1) {
								strSql2.append(" , ");
							}
						}

						strSql1.append(strWithCrLf); //|#$ 데이터 파싱

						strSql2.append(strDlmtCrLf);
						strSql2.append("FROM  ");
						strSql2.append(tbName + " A ");
						strSql2.append(strDlmtCrLf);
						strSql2.append(", TBL_WITHDRAW_CUST B ");
						strSql2.append(strDlmtCrLf);

						//컬럼이름이 다르다.					
						if (tbName.equals("TBL_PRIME_CNCL_HIST") || tbName.equals("TBL_SMT_CALLBACK")) {
							strSql2.append(" WHERE  (A.INTG_CUST_NO = B.G_CUST_NO OR A.INTG_CUST_NO = B.K_CUST_NO)");
						} else {
							strSql2.append(" WHERE (A.CUST_ID = B.G_CUST_NO OR A.CUST_ID = B.K_CUST_NO)");
						}
						strSql2.append(strDlmtCrLf);
						strSql2.append("   AND B.WORK_DT = '" + pWorkD + "'"); //전날짜를 조회해 온다.
						strSql2.append(strDlmtCrLf);
						//strSql2.append("   AND B.RT_DIV_CODE  in('01','02')"); // RT_DIV_CODE ( 분리보관 대상 :01)  (파기:02),  (복원:03)  (탈회:04)
						strSql2.append("   AND B.RT_DIV_CODE  in('01','02','04')"); // RT_DIV_CODE 분리보관 대상 :01  파기:02,   복원:03 탈회:04

						//writeLogFile(pBatchId, "strSql1 : " + strSql1.toString());
						//writeLogFile(pBatchId, "strSql2 : " + strSql2.toString());

						extlogger.debug("tbName========>" + tbName);
						//대상 테이블 데이터 조회 
						if (tbName.equals("TB_AGENT_MESSAGE_LOG")) {
							extlogger.debug(" strSql2.toString()=>" + strSql2.toString());
						}
						//

						sqlmapid = "ETC_STA.DPXGZA3_" + tbName;
						map2.put("I_SQL", strSql2.toString());
						commonDao.selectString(sqlmapid, map2);
						String rstCd = (String) map2.get("O_RTN_CD");

						extlogger.debug("ETC_STA.DPXGZA3 rstCd= [" + rstCd + "]");
						if (rstCd.equals("0")) {
							//대상자 정보
							ArrayList list3 = (ArrayList) map2.get("O_RESULT");
							/*
							 * 확인 log for(int k =0 ; k < list3.size() ; k++){
							 * extlogger.debug(list3.get(k)); }
							 */
							//출력 디렉터리 생성 
							String filePath = Const.WITH_GSIB_DE_DOWN + pWorkD + File.separator; //전날짜 디렉토리 생성

							extlogger.debug("======>4)filePath Create//  pWorkD ::" + pWorkD);
							extlogger.debug("========>4filePath" + filePath);

							File dir = new File(filePath);
							if (!dir.isDirectory()) {
								//디렉터리 생성 
								dir.mkdir();
							}

							//파일 생성
							extlogger.debug("======>4) Div_CustDataSend_createWithFile::STAR");
							createWithFile(list2, list3, strSql1.toString(), tbName, pWorkD, filePath);
						}
					}
				}
			}

		} catch (SQLException sqe) {
			strErrMsg = "Exception Error!:" + sqe.getMessage();
			extlogger.debug(strErrMsg);
		} catch (Exception e) {
			strErrMsg = "Exception Error!:" + e.getMessage();
			extlogger.debug(strErrMsg);
		} finally {
			// 배치작업종료로그출력
			extlogger.debug("==============Div_CustDataSend END=============");
		}
	}

	/*
	 * 분리보관 수동 처리 아래
	 

	// ----------------------------------------------------------------------------
	// 33.  GSC  분리보관 고객 대상정보 수신 수동배치
	// 최초 작성일 : 2017-05
	// 최종 수정일 : 2024-06
	public void procBatch33(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		extlogger.debug("resultCnt! ::" + resultCnt);
		
		String withdraw_url = "";
		String withdraw_account = "";
		String withdraw_password = "";

		String pramDay = request.getParameter("pWorkDay");

		if (pramDay == null || "".equals(pramDay) || "null".equals(pramDay)) {
			pramDay = ComUtil.getCurDateTime("yyyyMMdd");
		}

		try {
			String serverNm = InetAddress.getLocalHost().getHostName().trim();

			//extlogger.debug("server name :: " + serverNm);
			//분리보관 DB 계정
			if ("CRMDEV".equals(serverNm)) {
				withdraw_url = Const.WITHDRAW_TEST_DB_URL;
				withdraw_account = Const.WITHDRAW_TEST_DB_ACCOUNT;
				withdraw_password = Const.WITHDRAW_TEST_DB_PASSWORD;
			} else if (Const.WAS1NAME.equals(serverNm)) {
				withdraw_url = Const.WITHDRAW_DB_URL;
				withdraw_account = Const.WITHDRAW_DB_ACCOUNT;
				withdraw_password = Const.WITHDRAW_DB_PASSWORD;
			}
		} catch (UnknownHostException e) {
			extlogger.debug("HOST EXCEPTION !! :: " + e.getMessage());
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData md = null;
		StringBuffer sb = null;

		HashMap<String, String> map = new HashMap<String, String>();
		int roofCnt = 0;

		try {
			//mssql jdbc
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			DriverManager.setLoginTimeout(10);

			//mssql 연결
			conn = DriverManager.getConnection(withdraw_url, withdraw_account, withdraw_password);

			//쿼리 생성
			sb = withdrawMssqlQuery();

			//쿼리 실행
			ps = conn.prepareStatement(sb.toString());
			ps.setQueryTimeout(10);
			ps.setString(1, pramDay);

			//결과값
			rs = ps.executeQuery();

			while (rs.next()) {
				if (rs.getRow() > 0) {
					if (roofCnt == 0) {
						md = rs.getMetaData();
					}

					if (md != null) {
						for (int i = 1; i <= md.getColumnCount(); i++) {
							map.put(md.getColumnName(i).toLowerCase(), rs.getString(i) == null ? "" : rs.getString(i));
						}
					}


					try {
						//extlogger.debug("roofCnt :: " + roofCnt);
						extlogger.debug("## 탈회대상 DB Insert 수동 작업 실행 !!");
						//탈회대상 고객 CSC DB insert
						commonDao.insert("ETC_STA.withDrawUserInfo_INSERT", map);
					} catch (SQLException se) {
						extlogger.debug("[ETC DAEMON STA ERROR : procBatch33  " + se.toString() + "][DATA : " + map + "]");
					}

					roofCnt++;
				}
			}

		} catch (SQLException se) {
			extlogger.debug("SQL EXCEPTION ERROR : " + se.getMessage());
		} catch (Exception e) {
			extlogger.debug("JDBC CONNECTION ERROR : " + e.getMessage());
		} finally {
			if (conn != null) { try { conn.close(); } catch (SQLException e2) { extlogger.debug(e2.getMessage()); } catch (Exception e) { extlogger.debug(e.getMessage()); }}
			if (ps != null) {try {ps.close();} catch (SQLException e2) {extlogger.debug(e2.getMessage());} catch (Exception e) {extlogger.debug(e.getMessage());}}
			if (rs != null) {try {rs.close();} catch (SQLException e2) {extlogger.debug(e2.getMessage());} catch (Exception e) {extlogger.debug(e.getMessage());}}
			if (sb != null) {try {sb.delete(0, sb.length());} catch (ClassCastException e2) {extlogger.debug(e2.getMessage());} catch (Exception e) {extlogger.debug(e.getMessage());}}
		}
		
		resultCnt++;
	}
	*/

	/*
	// ----------------------------------------------------------------------------
	// 34.  탈회대상 테이블 및 대상자 조회, 파일생성
	// 최초 작성일 : 2017-05
	// 최종 수정일 : 2024-06
	public void procBatch34(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		extlogger.debug("procBatch34 START");

		String strErrMsg = "";
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

		String pramDay = request.getParameter("pWorkDay");

		if (pramDay == null || "".equals(pramDay) || "null".equals(pramDay)) {
			pramDay = ComUtil.getCurDateTime("yyyyMMdd");
		}

		extlogger.debug("======>2) SU_Div_CustFileDw::start==>" + pramDay);
		extlogger.debug("======>file name Day  bWorkDay:::::" + bWorkDay);

		try {
			// -------------------------------------------------------------
			//복원 삭제 대상자 
			// -------------------------------------------------------------			

			HashMap map = new HashMap();

			String sqlmapid = "ETC_STA.DPXGZA4";
			map.put("I_WORK_DT", pramDay); //익일 전날짜 처리 
			commonDao.selectString(sqlmapid, map);

			//01:휴면 02:파기 03:복원 04:탈회
			List listDeCust = (ArrayList) map.get("O_RESULT1"); // 삭제 대상01분리보관 02파기 04탈회 대상자를 가지고 온다
			List listReCust = (ArrayList) map.get("O_RESULT2"); // 복원 대상 03복원 대상자를 가지고 온다.

			extlogger.debug("SU_strDeCust : " + listDeCust);
			extlogger.debug("SU_strDeCust : " + listReCust);

			HashMap<String, String> mapRow1 = null;
			HashMap<String, String> mapRow2 = null;

			StringBuffer strSql1 = new StringBuffer();
			StringBuffer strSql2 = new StringBuffer();

			for (int i = 0; i < listDeCust.size(); i++) {
				mapRow1 = (HashMap<String, String>) listDeCust.get(i);
				strSql1.append(mapRow1.get("G_CUST_NO")); //통합 고객 번호
				strSql1.append(strWithDLmt); //구분   
				strSql1.append(mapRow1.get("K_CUST_NO")); //자사 고객번호
				strSql1.append(strWithCrLf); //개행
			}

			for (int i = 0; i < listReCust.size(); i++) {
				mapRow2 = (HashMap<String, String>) listReCust.get(i);
				strSql2.append(mapRow2.get("G_CUST_NO"));
				strSql2.append(strWithDLmt); //구분   
				strSql2.append(mapRow2.get("K_CUST_NO"));
				strSql2.append(strWithCrLf); //개행
			}

			String filePath = Const.WITH_GSIB_DIV_DOWN + pramDay + File.separator; //파일 패스는 전날짜

			//파기
			String fileName1 = Const.WITH_GSIB_DE_CUST + "." + bWorkDay; //파일이름은 현재날짜로 
			//복원
			String fileName2 = Const.WITH_GSIB_RE_CUST + "." + bWorkDay; //파일이름은 현재날짜로 

			extlogger.debug("bWorkDay::::" + bWorkDay);
			extlogger.debug("tbName1===>" + fileName1);
			extlogger.debug("tbName1===>" + fileName2);

			createFile(strSql1.toString(), fileName1, pramDay, filePath);
			createFile(strSql2.toString(), fileName2, pramDay, filePath);

			extlogger.debug("======>2) SU_Div_CustFileDw::END");
		} catch (SQLException sqe) {
			strErrMsg = "SU_Exception Error!:" + sqe.getMessage();
			extlogger.debug(strErrMsg);

		} catch (Exception e) {
			strErrMsg = "SU_Exception Error!:" + e.getMessage();
			extlogger.debug(strErrMsg);
		} finally {
			// 배치작업종료로그출력
			extlogger.debug("SU_Div_CustFileDw END");
		}
	}
	*/

	/*
	// ----------------------------------------------------------------------------
	// 35. GSC 탈회고객 분리보관 대상 cmsftp 전송 
	// 최초 작성일 : 2017-05
	// 최종 수정일 : 2024-06
	// ----------------------------------------------------------------------------
	// 분리보관서버-엠비즈 로 대상자 전송
	// ----------------------------------------------------------------------------
	public void procBatch35(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String strErrMsg = "";
		boolean bDnload = false;

		String pramDay = request.getParameter("pWorkDay");

		if (pramDay == null || "".equals(pramDay) || "null".equals(pramDay)) {
			pramDay = ComUtil.getCurDateTime("yyyyMMdd");
		}

		//file name을 위해 현재 날짜
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

		String filePath1 = "";
		String filePath2 = "";
		String fileName1 = "";
		String fileName2 = "";

		extlogger.debug("======>3) SU_Div_CustFileSend::STAR bWorkDay==>" + bWorkDay);
		extlogger.debug("======>3) SU_Div_CustFileSend::STAR pWorkD::==>" + pramDay);

		// ------------------------------------
		// 파일 cmsftp로 전송
		// ------------------------------------
		String FTP_IP;
		String FTP_ID;
		String FTP_PWD;
		String FTP_ROOT;
		int FTP_PORT = 0;
		try {
			// cmsftp접속계정
			if (ComUtil.isProd()) // 운영계
			{
				FTP_IP = Const.SFTP_IP;
				FTP_ID = Const.SFTP_ID;
				FTP_PWD = Const.SFTP_PWD;
				FTP_ROOT = Const.SFTP_SEND;
				FTP_PORT = Const.SFTP_PORT;
			} else // 개발계
			{
				FTP_IP = Const.TEST_SFTP_IP;
				FTP_ID = Const.TEST_SFTP_ID;
				FTP_PWD = Const.TEST_SFTP_PWD;
				FTP_ROOT = Const.TEST_SFTP_SEND;
				FTP_PORT = Const.TEST_SFTP_PORT;
			}

			//sftp 연결
			extlogger.debug("FTP . IP=>[" + FTP_IP + "], ID=>[" + FTP_ID + "], PWD=>[" + FTP_PWD + "], ROOT=>[" + FTP_ROOT + "], PORT=>[" + FTP_PORT + "]");
			//파기/복원 대상 파일 다운로드 
			filePath1 = Const.WITH_GSIB_DIV_DOWN + pramDay + File.separator;

			//파일 이름은 생성일 기준으로 수정 해야 한다. 
			fileName1 = Const.WITH_GSIB_RE_CUST + "." + bWorkDay; //복원 대상 파일 name
			fileName2 = Const.WITH_GSIB_DE_CUST + "." + bWorkDay; //파기 대상 파일 name

			extlogger.debug("Div_CustFileSend filePath1:" + filePath1);
			extlogger.debug("Div_CustFileSend fileName1:" + fileName1);
			extlogger.debug("Div_CustFileSend fileName2:" + fileName2);

			SFTPUtil sftpUtil = new SFTPUtil();
			sftpUtil.init(FTP_IP, FTP_ID, FTP_PWD, FTP_PORT);

			boolean bUpload1 = sftpUtil.upload(FTP_ROOT, filePath1 + fileName1);
			boolean bUpload2 = sftpUtil.upload(FTP_ROOT, filePath1 + fileName2);

			sftpUtil.disconnection();

			extlogger.debug("======>3) SU_Div_CustFileSend::STAR==>" + pWorkD);
			extlogger.debug("SU_Send File result1=>>[" + bUpload1 + "]");
			extlogger.debug("SU_Send File result2=>>[" + bUpload2 + "]");
		} catch (NullPointerException e) {
			extlogger.debug("SU_Div_CustFileSend Exception :: " + e.getMessage());
		} finally {
		}
	}
	*/

	/*
	// ----------------------------------------------------------------------------
	// 36. 대상자 정보 파일생성 및 삭제 후 파일전송 
	// 최초 작성일 : 2017-05
	// 최종 수정일 : 2024-06
	// ----------------------------------------------------------------------------
	public void procBatch36(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String strErrMsg = "";
		boolean bDnload = false;

		//file name을 위해 현재 날짜
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");
		String pramDay = request.getParameter("pWorkDay");
		if (pramDay == null || "".equals(pramDay) || "null".equals(pramDay)) {
			pramDay = ComUtil.getCurDateTime("yyyyMMdd");
		}

		extlogger.debug("======> SU_Div_CustDataSend bWorkDay::==>" + bWorkDay);
		extlogger.debug("======> SU_Div_CustDataSend pramDay::==>" + pramDay);

		try {
			// -------------------------------------------------------------
			// DB 개인정보 삭제 대상 테이블 정보 조회
			// -------------------------------------------------------------
			String sqlmapid = "ETC_STA.DPXGZA0"; //테이블 목록 조회
			HashMap map = new HashMap();

			//대상 테이블 목록을 조회 한다.
			commonDao.selectString(sqlmapid, map);
			List list1 = (ArrayList) map.get("O_RESULT"); // 

			//대상 테이블 별 SP 생성
			extlogger.debug("[DPXGZA0] Target Table Select = [" + list1.size() + "]");
			if (list1.size() > 0) {

				HashMap<String, String> mapRow1 = null;
				HashMap<String, String> mapRow2 = null;
				HashMap<String, String> mapRow3 = null;

				String tbName = "";
				String owner = "";
				for (int i = 0; i < list1.size(); i++) {

					mapRow1 = (HashMap<String, String>) list1.get(i);

					tbName = mapRow1.get("TABLE_NAME");
					owner = mapRow1.get("OWNER");

					//대상 테이블 컬럼 Name가지고 오기
					sqlmapid = "ETC_STA.DPXGZA1";
					//GSIB 인지 한번더 체크 
					if (owner.equals("GSIB")) {

						HashMap map2 = new HashMap();
						HashMap map3 = new HashMap();

						map2.put("I_OWNER", owner);
						map2.put("I_TABLE_NAME", tbName);

						commonDao.selectString(sqlmapid, map2);

						ArrayList list2 = (ArrayList) map2.get("O_RESULT"); //

						//extlogger.debug( "Table column select = [" +list2.size()+"]");

						StringBuffer strSql1 = new StringBuffer();
						StringBuffer strSql2 = new StringBuffer();

						strSql2.append(" SELECT ");
						for (int j = 0; j < list2.size(); j++) {

							mapRow2 = (HashMap<String, String>) list2.get(j);
							strSql1.append(mapRow2.get("COLUMN_NAME"));
							strSql1.append(strWithDLmt); // |^ 컬럼 파싱

							strSql2.append(mapRow2.get("COLUMN_NAME"));
							if (j < list2.size() - 1) {
								strSql2.append(" , ");
							}
						}

						strSql1.append(strWithCrLf); //|#$ 데이터 파싱

						strSql2.append(strDlmtCrLf);
						strSql2.append("FROM  ");
						strSql2.append(tbName + " A ");
						strSql2.append(strDlmtCrLf);
						strSql2.append(", TBL_WITHDRAW_CUST B ");
						strSql2.append(strDlmtCrLf);

						//컬럼이름이 다르다.					
						if (tbName.equals("TBL_PRIME_CNCL_HIST") || tbName.equals("TBL_SMT_CALLBACK")) {
							strSql2.append(" WHERE  (A.INTG_CUST_NO = B.G_CUST_NO OR A.INTG_CUST_NO = B.K_CUST_NO)");
						} else {
							strSql2.append(" WHERE (A.CUST_ID = B.G_CUST_NO OR A.CUST_ID = B.K_CUST_NO)");
						}
						strSql2.append(strDlmtCrLf);
						strSql2.append("   AND B.WORK_DT = '" + pWorkD + "'"); //전날짜를 조회해 온다.
						strSql2.append(strDlmtCrLf);
						strSql2.append("   AND B.RT_DIV_CODE  in('01','02','04')"); // RT_DIV_CODE 분리보관 대상 :01  파기:02,   복원:03 탈회:04

						extlogger.debug("tbName========>" + tbName);

						sqlmapid = "ETC_STA.DPXGZA3_" + tbName;
						map2.put("I_SQL", strSql2.toString());
						commonDao.selectString(sqlmapid, map2);
						String rstCd = (String) map2.get("O_RTN_CD");

						extlogger.debug("ETC_STA.DPXGZA3 rstCd= [" + rstCd + "]");
						if (rstCd.equals("0")) {
							//대상자 정보
							ArrayList list3 = (ArrayList) map2.get("O_RESULT");

							//출력 디렉터리 생성 
							String filePath = Const.WITH_GSIB_DE_DOWN + pramDay + File.separator;
							extlogger.debug("========>4filePath" + filePath);

							File dir = new File(filePath);
							if (!dir.isDirectory()) {
								//디렉터리 생성 
								dir.mkdir();
							}

							//파일 생성
							extlogger.debug("======>4) SU_Div_CustDataSend_createWithFile::STAR");
							createWithFileSU(list2, list3, strSql1.toString(), tbName, pramDay, filePath);
						}
					}
				}
			}

		} catch (SQLException sqe) {
			strErrMsg = "Exception Error!:" + sqe.getMessage();
			extlogger.debug(strErrMsg);
		} catch (Exception e) {
			strErrMsg = "Exception Error!:" + e.getMessage();
			extlogger.debug(strErrMsg);
		} finally {
			// 배치작업종료로그출력
			extlogger.debug("==============Div_CustDataSend END=============");
		}
	}
	*/

	/**
	 * SP CREATE FUNCTION
	 * 
	 * @param tblList
	 * @throws Exception
	 */
	private void createSPFile(ArrayList tblList, String Filename) throws Exception {

		StringBuffer sbSPfile = new StringBuffer();

		try {

			PrintStream pStrmSPFile = new PrintStream(new FileOutputStream(Const.GSC_SP_PATH + "SP_" + Filename + ".prc", false));

			HashMap<String, String> mapRow = null;

			sbSPfile.append(" CREATE OR REPLACE PROCEDURE SP_" + Filename + " (");
			sbSPfile.append(strDlmtCrLf);

			for (int i = 0; i < tblList.size(); i++) {

				mapRow = (HashMap<String, String>) tblList.get(i);
				sbSPfile.append(" I_" + mapRow.get("COLUMN_NAME") + " IN VARCHAR2 ,");
				sbSPfile.append(strDlmtCrLf);
			}

			sbSPfile.append(" O_RESULT OUT VARCHAR2");
			sbSPfile.append(strDlmtCrLf);
			sbSPfile.append(" )");
			sbSPfile.append(strDlmtCrLf);
			sbSPfile.append(" IS");
			sbSPfile.append(strDlmtCrLf);

			sbSPfile.append(" BEGIN");
			sbSPfile.append(strDlmtCrLf);

			sbSPfile.append(" INSERT INTO " + Filename + " (");
			sbSPfile.append(strDlmtCrLf);

			for (int i = 0; i < tblList.size(); i++) {
				mapRow = (HashMap<String, String>) tblList.get(i);

				sbSPfile.append("  " + mapRow.get("COLUMN_NAME"));

				if (i < tblList.size() - 1) {
					sbSPfile.append("," + strDlmtCrLf);
				} else {
					sbSPfile.append(strDlmtCrLf);
				}
			}

			sbSPfile.append(" ) VALUES  (");
			sbSPfile.append(strDlmtCrLf);

			for (int i = 0; i < tblList.size(); i++) {
				mapRow = (HashMap<String, String>) tblList.get(i);

				sbSPfile.append("I_" + mapRow.get("COLUMN_NAME"));

				if (i < tblList.size() - 1) {
					sbSPfile.append("," + strDlmtCrLf);
				} else {
					sbSPfile.append(strDlmtCrLf);
				}
			}
			sbSPfile.append(" ); ");
			sbSPfile.append(strDlmtCrLf);

			sbSPfile.append(" END;");
			sbSPfile.append(strDlmtCrLf);
			sbSPfile.append(" /");
			sbSPfile.append(strDlmtCrLf);

		} catch (NullPointerException ne) {
			extlogger.debug("createSPFile NullPointException ::" + ne.getMessage());
		} catch (StringIndexOutOfBoundsException se) {
			extlogger.debug("createSPFile StringIndexOutOfBoundsException ::" + se.getMessage());
		} catch (Exception e) {
			extlogger.debug("createSPFile Exception ::" + e.getMessage());
		}
	}

	/**
	 * 탈회대상 정보 파일 생성 및 전송Function
	 * 
	 * @param tblList
	 *            , Filename
	 * @throws Exception
	 */
	private void createWithFile(ArrayList tblList, ArrayList datalList, String strSql1, String filename, String pWorkD, String FilefilePath) throws Exception {

		//StringBuffer sbSPfile = new StringBuffer();
		String sbSPfile = "";
		extlogger.debug("createWithFile Start======" + pWorkD);

		//파일 전송 대상은 분리보관(01) , 파기(02) 고객을 같이 보내고 삭제 한다.
		//법적 개인 정보 보유 기간으로 일단 파기 고객도 분리 보관 서버로 전송 함 
		//법적 보유 기간 경과시 분리 보관 서버에서 파기 고객 삭제 
		//pWorkDay :현재 날짜
		//파일 이름은 생성일 기준으로.......20180515 정구식K 확인

		//File Name을 위해
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

		String Filename = "IN.SP_" + filename + "." + bWorkDay;

		extlogger.debug("bWorkDay::" + bWorkDay);
		extlogger.debug("====############FIleName #######::::::::" + Filename);

		try {
			//////
			//테이블별 파일 생성
			HashMap<String, String> mapRow = null;
			HashMap<String, String> mapRow1 = null;

			// 파일생성
			File dirFile = new File(FilefilePath + Filename);
			if (!dirFile.exists())
				dirFile.createNewFile();

			PrintWriter prtWtr = new PrintWriter(new FileWriter(dirFile));

			//헤더 파일출력(컬럼 이름)
			//컬럼 내용	
			sbSPfile = strSql1;
			//sbSPfile.append(strSql1);

			//분리보관 데이터
			for (int i = 0; i < datalList.size(); i++) {
				mapRow = (HashMap<String, String>) datalList.get(i);
				//System.out.println("mapRow"+mapRow.toString());		
				/*
				 * for(int j=0; j<tblList.size(); j++){ mapRow1 =
				 * (HashMap<String, String>) tblList.get(j);
				 * sbSPfile.append(String.valueOf((mapRow.get(mapRow1.get(
				 * "COLUMN_NAME"))))); sbSPfile.append(strWithDLmt); }
				 */
				for (int j = 0; j < tblList.size(); j++) {
					mapRow1 = (HashMap<String, String>) tblList.get(j);
					sbSPfile = sbSPfile + String.valueOf((mapRow.get(mapRow1.get("COLUMN_NAME"))));
					sbSPfile = sbSPfile + strWithDLmt;
				}

				//System.out.println("sbSPfile====>"+sbSPfile);
				//sbSPfile.append(strWithCrLf);			
				sbSPfile = sbSPfile + strWithCrLf;
			}

			String strBuf = new String(sbSPfile.getBytes("euc-kr"), "8859_1"); // 한글코드변환
			//null문자 변경 정구식K 요청
			strBuf = strBuf.replaceAll("null", "empty");
			//System.out.println("strBuf=============>"+strBuf);
			prtWtr.println(strBuf);
			//prtWtr.println(sbSPfile);				
			prtWtr.close();
			//생성 후 전송
			extlogger.debug("createWithFile END======");
			extlogger.debug("SendFtpFile Call======" + pWorkD);
			SendFtpFile(filename, Filename, pWorkD);

		} catch (IOException e) {
			extlogger.debug("createWithFile Error = [" + e.toString() + " ]");
		}
	}

	//수동 전송시 파일 생성
	private void createWithFileSU(ArrayList tblList, ArrayList datalList, String strSql1, String tbName, String WorkDay, String FilefilePath) throws Exception {

		String sbSPfile = "";
		String service = "";

		//file name을 위해 현재 날짜
		String toWorkDay = ComUtil.getCurDateTime("yyyyMMdd");
		extlogger.debug("==========createWithFileSU START======" + WorkDay);

		//파일 전송 대상은 분리보관(01) , 파기(02) 고객을 같이 보내고 삭제 한다.
		//법적 개인 정보 보유 기간으로 일단 파기 고객도 분리 보관 서버로 전송 함 
		//법적 보유 기간 경과시 분리 보관 서버에서 파기 고객 삭제 
		//pWorkDay :현재 날짜
		//파일 이름은 생성일 기준으로.......20180515 정구식K 확인
		if (WorkDay != null && !"".equals(WorkDay)) {
			WorkDay = WorkDay.replaceAll("/", "");
			WorkDay = WorkDay.replaceAll("\\", "");
			WorkDay = WorkDay.replaceAll("&", "");
			WorkDay = WorkDay.replaceAll(".", "");
		}

		//File Name을 위해
		String bWorkDay = WorkDay;
		String Filename = "IN.SP_" + tbName + "_" + toWorkDay;

		try {
			//테이블별 파일 생성
			HashMap<String, String> mapRow = null;
			HashMap<String, String> mapRow1 = null;

			// 파일생성 하고 싶은면 주석 해제
			File dirFile = new File(Const.WITH_GSIB_DE_DOWN + bWorkDay + "/" + Filename);
			if (!dirFile.exists()) {
				dirFile.createNewFile();
			} else {
				//dirFile.delete();
				fileDelete(dirFile);
			}
			PrintWriter prtWtr = new PrintWriter(new FileWriter(dirFile));

			//헤더 파일출력(컬럼 이름)
			//컬럼 내용	
			sbSPfile = strSql1;

			//분리보관 데이터
			for (int i = 0; i < datalList.size(); i++) {
				mapRow = (HashMap<String, String>) datalList.get(i);

				for (int j = 0; j < tblList.size(); j++) {
					mapRow1 = (HashMap<String, String>) tblList.get(j);
					sbSPfile = sbSPfile + String.valueOf((mapRow.get(mapRow1.get("COLUMN_NAME"))));
					sbSPfile = sbSPfile + strWithDLmt;
				}

				sbSPfile = sbSPfile + strWithCrLf;
			}

			String strBuf = new String(sbSPfile.getBytes("euc-kr"), "8859_1"); // 한글코드변환
			//null문자 변경 정구식K 요청
			strBuf = strBuf.replaceAll("null", "empty");

			prtWtr.println(strBuf);

			prtWtr.close();

			//생성 후 전송
			extlogger.debug("createWithFileSU END======");
			SendFtpFileSU(tbName, Filename, bWorkDay);

		} catch (IOException e) {
			extlogger.debug("createWithFileSU Error = [" + e.toString() + " ]");
		}
	}
	
	/**
	 * Sparrow 파일 삭제 처리(TOCTOU)
	 */
	private synchronized void fileDelete(File file) {
		file.delete();
	}

	private void SendFtpFile(String filename, String Filename, String bWorkDay) throws IOException {
		//extlogger.debug("SFTP Send START:: file name =>"+filename);
		extlogger.debug("SFTP Sned Start======" + bWorkDay);

		String strErrMsg = "";
		String filePath = "";

		// ------------------------------------
		// 파일 cmsftp로 전송
		// ------------------------------------
		String FTP_IP;
		String FTP_ID;
		String FTP_PWD;
		String FTP_ROOT;
		int FTP_PORT = 0;
		try {
			// cmsftp접속계정
			if (ComUtil.isProd()) // 운영계
			{
				FTP_IP = Const.SFTP_IP;
				FTP_ID = Const.SFTP_ID;
				FTP_PWD = Const.SFTP_PWD;
				FTP_ROOT = Const.SFTP_SEND;
				FTP_PORT = Const.SFTP_PORT;
			} else // 개발계
			{
				FTP_IP = Const.TEST_SFTP_IP;
				FTP_ID = Const.TEST_SFTP_ID;
				FTP_PWD = Const.TEST_SFTP_PWD;
				FTP_ROOT = Const.TEST_SFTP_SEND;
				FTP_PORT = Const.TEST_SFTP_PORT;
			}

			//sftp 연결
			extlogger.debug("FTP . IP=>[" + FTP_IP + "], ID=>[" + FTP_ID + "]" + " ROOT=>[" + FTP_ROOT + "]");
			//파기/복원 대상 파일 다운로드 
			filePath = Const.WITH_GSIB_DE_DOWN + bWorkDay + File.separator;

			extlogger.debug("Send filePath :: " + filePath);
			//extlogger.debug("re : "+FTP_ROOT+"<---->send:"+filePath +":::::"+ Filename);
			//System.out.println("FTP_ROOT : "+FTP_ROOT+" filePath:"+filePath +" fileName::"+ Filename);			
			extlogger.debug("FTP SEND Filename====>" + Filename);

			SFTPUtil sftpUtil = new SFTPUtil();
			sftpUtil.init(FTP_IP, FTP_ID, FTP_PWD, FTP_PORT);
			boolean bUpload = sftpUtil.upload(FTP_ROOT, filePath + Filename);
			//boolean bUpload = sftpUtil.download(FTP_ROOT, filePath, filePath);

			sftpUtil.disconnection();
			//System.out.println("===Filename::"+Filename);
			extlogger.debug("!=========SFTP Send End=========!");
			//System.out.println( "Send File result=>>[" + bUpload + "]");

			extlogger.debug("SFTP Sned :: " + bUpload);
			//전송 성공이면 데이터 삭제 
			if (bUpload == true) {
				extlogger.debug("SFTP SUCCES=> Delete Start::" + filename);
				//이력 때문에 나누어 삭제 처리 한다.
				//분리 보관 삭제		    	
				extlogger.debug("1)SE Delete call!!");
				DeCustDB(filename, "SE");

				/*
				 * //파기 대상 삭제 System.out.println("2)DE Delete call!!");
				 * extlogger.debug("2)DE Delete call!!");
				 * DeCustDB(filename,"DE");
				 * 
				 * //탈회 대상 삭제 System.out.println("3)TE Delete call!!");
				 * DeCustDB(filename,"TE");
				 */
			}
		} catch (NullPointerException e) {
			strErrMsg = "Exception Error!:" + e.getMessage();
			extlogger.debug("strErrMsg=====>" + strErrMsg);
		} finally {
		}
	}

	private void SendFtpFileSU(String tbName, String Filename, String bWorkDay) throws IOException {
		extlogger.debug("SFTP Send START:: file name =>" + tbName);
		extlogger.debug("SFTP Sned Start======" + bWorkDay);

		String strErrMsg = "";
		String filePath = "";

		// ------------------------------------
		// 파일 cmsftp로 전송
		// ------------------------------------
		String FTP_IP;
		String FTP_ID;
		String FTP_PWD;
		String FTP_ROOT;
		int FTP_PORT = 0;
		try {
			// cmsftp접속계정
			if (ComUtil.isProd()) // 운영계
			{
				FTP_IP = Const.SFTP_IP;
				FTP_ID = Const.SFTP_ID;
				FTP_PWD = Const.SFTP_PWD;
				FTP_ROOT = Const.SFTP_SEND;
				FTP_PORT = Const.SFTP_PORT;
			} else // 개발계
			{
				FTP_IP = Const.TEST_SFTP_IP;
				FTP_ID = Const.TEST_SFTP_ID;
				FTP_PWD = Const.TEST_SFTP_PWD;
				FTP_ROOT = Const.TEST_SFTP_SEND;
				FTP_PORT = Const.TEST_SFTP_PORT;
			}

			//파기/복원 대상 파일 다운로드 
			filePath = Const.WITH_GSIB_DE_DOWN + bWorkDay + File.separator;
			extlogger.debug("re : " + FTP_ROOT + "<---->send:" + filePath + ":::::" + Filename);

			SFTPUtil sftpUtil = new SFTPUtil();
			sftpUtil.init(FTP_IP, FTP_ID, FTP_PWD, FTP_PORT);
			boolean bUpload = sftpUtil.upload(FTP_ROOT, filePath + Filename);

			sftpUtil.disconnection();

			//전송 성공이면 데이터 삭제 
			if (bUpload == true) {
				extlogger.debug("SU_SFTP SUCCES=> Delete Start::" + tbName);
				//이력 때문에 나누어 삭제 처리 한다.
				//분리 보관 삭제(탈회)	    	
				extlogger.debug("3)SE Delete call!!");
				DeCustDBSU(tbName, "TE", bWorkDay);

			}
		} catch (NullPointerException e) {
			extlogger.debug("FILE SU EXCEPTION ::" + e.getMessage());
		} catch (Exception e) {
			extlogger.debug("FILE SU EXCEPTION ::" + e.getMessage());
		} finally {
		}
	}

	//분리 보관 대상자 삭제 처리 
	private void DeCustDB(String filename, String div) {
		extlogger.debug("====DB Delete Start====::" + div);

		String sqlmapid1 = "";

		try {
			HashMap map = new HashMap();
			HashMap map1 = new HashMap();
			HashMap<String, String> mapRow = null;

			extlogger.debug("DELETE DB pWorkDay==>" + pWorkDay);

			String sqlmapid = "ETC_STA.DPXGZA5"; //삭제 대상자 고객번호를 가지고 온다.
			map.put("I_WORK_DT", pWorkD); //전날짜로 삭제 대상자 정보를 가지고 와야 한다.
			map.put("I_DIV_CODE", ""); //나누지 말고 한번에 처리 하자 DB부하가 너무 많이 든다. 
			//extlogger.debug("Delete Cust Day ::"+pWorkD);

			/*
			 * if(div == "SE"){ map.put("I_DIV_CODE", "01"); //분리보관 대상자 }else
			 * if(div == "DE"){ map.put("I_DIV_CODE", "02"); //파기 대상자 }else{
			 * map.put("I_DIV_CODE", "04"); //탈회 대상자 }
			 */
			//System.out.println("map====>"+map);			
			commonDao.selectString(sqlmapid, map);
			//01:휴면대상자 삭제처리 (02:파기 03:복원)	
			List list1 = (ArrayList) map.get("O_RESULT"); //고객 번호와 구분값을 가지고 온다.  
			//System.out.println(list1.toString());
			extlogger.debug("+++++++++++++++++++++" + filename + "++++++++++++++++++++");
			//extlogger.debug(list1.size());

			for (int i = 0; i < list1.size(); i++) {

				mapRow = (HashMap<String, String>) list1.get(i);
				sqlmapid1 = "ETC_STA.SP_" + filename + "_DEL";

				// System.out.println("K>>>>>>>>>>>>>>"+mapRow.get("K_CUST_NO"));
				//System.out.println("G>>>>>>>>>>>>>>"+mapRow.get("G_CUST_NO"));

				map1.put("I_CUST_ID", mapRow.get("K_CUST_NO"));
				map1.put("I_CUST_NO", mapRow.get("G_CUST_NO"));
				map1.put("I_DIV_CODE", ""); //한번에 처리 하자

				/*
				 * if(div == "SE"){ map1.put("I_DIV_CODE", "01"); //분리보관 대상자
				 * }else if(div == "DE"){ map1.put("I_DIV_CODE", "02"); //파기 대상자
				 * }else{ map1.put("I_DIV_CODE", "04"); //탈회 대상자 }
				 */

				//System.out.println("sqlmapid1===>"+sqlmapid1+"/////map1===>"+map1);				
				commonDao.selectString(sqlmapid1, map1);

			}
			extlogger.debug("sqlmapid1::" + sqlmapid1 + "====DB Delete END");
		} catch (SQLException sql) {
			extlogger.debug("sql.getMessage :: " + sql.getMessage());
		} catch (Exception e) {
			extlogger.debug("sql.getMessage :: " + e.getMessage());
		}

	}

	//분리 보관 대상자 삭제 처리						 
	private void DeCustDBSU(String tbName, String div, String bWorkDay) {
		extlogger.debug("====DeCustDBSU Start====::" + div);

		String sqlmapid1 = "";

		try {
			HashMap map = new HashMap();
			HashMap map1 = new HashMap();
			HashMap<String, String> mapRow = null;

			String sqlmapid = "ETC_STA.DPXGZA5"; //삭제 대상자 고객번호를 가지고 온다.
			map.put("I_WORK_DT", bWorkDay); //전날짜로 삭제 대상자 정보를 가지고 와야 한다.
			map.put("I_DIV_CODE", ""); // SP에서 값을 넣어줘서 한번에 처리		

			commonDao.selectString(sqlmapid, map);
			//01:휴면대상자 삭제처리 (02:파기 03:복원)	
			List list1 = (ArrayList) map.get("O_RESULT"); //고객 번호와 구분값을 가지고 온다.  

			for (int i = 0; i < list1.size(); i++) {

				mapRow = (HashMap<String, String>) list1.get(i);
				sqlmapid1 = "ETC_STA.SP_" + tbName + "_DEL";

				map1.put("I_CUST_ID", mapRow.get("K_CUST_NO"));
				map1.put("I_CUST_NO", mapRow.get("G_CUST_NO"));
				map1.put("I_DIV_CODE", ""); //없어도 됨.

				commonDao.selectString(sqlmapid1, map1);

			}
			extlogger.debug("sqlmapid1::" + sqlmapid1 + "====DB Delete END");
		} catch (SQLException sql) {
			extlogger.debug("sql.getMessage :: " + sql.getMessage());
		} catch (Exception e) {
			extlogger.debug("sql.getMessage :: " + e.getMessage());
		}

	}

	private void createFile(String strSql1, String Filename, String pWorkD, String FilefilePath) throws Exception {

		StringBuffer sbSPfile = new StringBuffer();
		HashMap<String, String> mapRow = null;

		extlogger.debug("strSql1 ::" + strSql1);
		extlogger.debug("createfile Filename ::" + Filename);
		extlogger.debug("createfile FilefilePath ::" + FilefilePath);

		// 출력디렉토리설정						
		File dir = new File(FilefilePath);
		if (!dir.isDirectory())
			dir.mkdir();
		extlogger.debug("======>2) Div_CustFileDw  createFile ::start==>");

		extlogger.debug("FilefilePath+Filename::" + FilefilePath + Filename);
		//파일 생성
		File dirFile = new File(FilefilePath + Filename);

		if (!dirFile.exists()) {
			dirFile.createNewFile();
		}

		PrintWriter prtWtr = new PrintWriter(new FileWriter(dirFile));
		sbSPfile.append(strSql1);

		prtWtr.println(sbSPfile);
		prtWtr.close();
		extlogger.debug("======>2) Div_CustFileDw  createFile ::END==>");

	}

	//월말개인정보삭제 배치 테스트용
	public void procBatch38(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String WorkDay = request.getParameter("WorkDay");

		String strErrMsg = "";
		boolean bDnload = false;

		//file name을 위해 현재 날짜
		extlogger.debug("Div_CustDataSend::pWorkD----->" + WorkDay);

		//System.out.println("======> 0310:: Div_CustDataSend pWorkD::==>"+WorkDay);

		try {
			extlogger.debug("Prc_BT_PRIVACY_MTR_DEL::  START !!" + pWorkDay);
			String sqlmapid = "ETC_STA.Prc_BT_PRIVACY_MTR_DEL";

			HashMap map = new HashMap();
			map.put("I_WORK_DT", pWorkDay);
			;
			map.put("I_USER_ID", "SYSTEM");
			commonDao.selectString(sqlmapid, map);

			String listresult = (String) map.get("O_RESULT2");

			extlogger.debug("listresult :: " + listresult);

		} catch (SQLException sqe) {
			extlogger.debug("sqe.getMessage :: " + sqe.getMessage());
		} catch (Exception e) {
			extlogger.debug("e.getMessage :: " + e.getMessage());
		} finally {
			// 배치작업종료로그출력
			extlogger.debug("===============procBatch38 END=============");
		}
	}

	/**
	 * 분리보관DB 테이블 변경 oracle -> mssql 탈회대상자 조회 쿼리 작성 2024.04.04 NP847
	 * 
	 * @return
	 */
	private StringBuffer withdrawMssqlQuery() {
		extlogger.debug("## MSSQL QUERY CREATE !!");
		StringBuffer sb = new StringBuffer();

		sb.append("\n	select work_dt, cust_no, intg_cust_no, rt_dt_div_code");
		sb.append("\n from t_gc_rtcs_if_m");
		sb.append("\n	where work_dt = ?");
		sb.append("\n	and rt_dt_div_code = \'04\'");

		return sb;
	}
}
