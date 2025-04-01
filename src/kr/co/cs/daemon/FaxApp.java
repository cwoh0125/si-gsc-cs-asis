package kr.co.cs.daemon;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.tobesoft.xplatform.data.DataSetList;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.tx.HttpPlatformResponse;
import com.tobesoft.xplatform.tx.PlatformType;

import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.persistence.dao.CommonDao;
import kr.co.cs.presentation.xcommon.XbaseAction;

/**
 * @author Administrator
 * 
 * 
 * 
 * 팩스 코드테이블 키값 잡을것.
 * customer_cd 인덱스 잡을것.
 * 
 * 
 */
public class FaxApp extends XbaseAction {

	/*
	 * 생성자
	 * was기동시에 시작되게..
	 * */
	public FaxApp() {
		startFax(true);
	}
	
	private final static Logger extlogger = LogManager.getLogger("process.ext");
	
	private CommonDao commonDao = null;

	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;  
	}
	
	public void StartFaxDaemon(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {		
		startFax();
	}
	
	public void StopFaxDaemon(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("val", "N");
		paramMap.put("cd", "DM_FAXDRUN");
		commonDao.update("Common.Set_DaemonState", paramMap);
	}
		
	public void FaxState(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		Iterator<Thread> ir = map.keySet().iterator();

		String alive = "N";
		while(ir.hasNext()) {
			Thread th = ir.next();
			if(th.getName().indexOf("faxDaemonThread")>=0) {
				////System.out.println("Alive");
				alive = "Y";
			}
		}
		
		HttpPlatformResponse platformResponse = new HttpPlatformResponse(response, PlatformType.CONTENT_TYPE_XML, "UTF-8");
		VariableList outvlist = new VariableList();
		outvlist.add("faxStatusMsg", alive);		
		PlatformData output = new PlatformData();
		output.setVariableList(outvlist);
		
		platformResponse.setData(output);
		platformResponse.sendData();
	}
		
	private void startFax() throws Exception {
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("val", "Y");
		paramMap.put("cd", "DM_FAXDRUN");
		commonDao.update("Common.Set_DaemonState", paramMap);
		
		startFax(true);
	}
 
	private void startFax(Boolean a) {		
		
		try {
			String server_name = InetAddress.getLocalHost().getHostName().trim();
			if(!("___WKH-BT-N".equals(server_name) ||
					"___wkh-bt-n".equals(server_name) ||
			     "___CRMDEV".equals(server_name) ||
				 Const.WAS1NAME.equals(server_name) ||
				 Const.WAS2NAME.equals(server_name))) 
			{
				////System.out.println(new Date() + " : FAX DAEMON STOP ::::: STOPING AS SERVER IS NOT PROD ........");
				extlogger.debug("[FAX DAEMON STOP ::::: STOPING AS SERVER IS NOT PROD ........]");
				return;
			}
		} catch (UnknownHostException e1) {			
			return;
		}
		////System.out.println(new Date() + " : FAX DAEMON GO ::::: GO GO GO........");
		extlogger.debug("[FAX DAEMON GO ::::: GO GO GO ........]");

		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		Iterator<Thread> ir = map.keySet().iterator();

		while(ir.hasNext()) {
			Thread th = ir.next();
			if(th.getName().indexOf("faxDaemonThread")>=0) { 
				//th.interrupt();
				return;
			}
		}
 
		Thread faxthread = new Thread() {
			public void run() {
				
				HashMap<String, String> runmap = new HashMap<String, String>();
				runmap.put("cd", "DM_FAXDRUN");
				String bb = null;
				int tloop = 0;
				int execloop = 0; 
				while(true) {
					try {
						Thread.sleep(1000); // 1 second
						tloop++;
						execloop++;
					} catch(InterruptedException e) {}

					try {
						if(tloop==10) { //디비체크는 10초에 한번타게끔..
							tloop = 0;
							bb = commonDao.selectString("Common.Get_DaemonRunning", runmap);
							if("N".equals(bb)) {
								execloop = 0; //loop 초기화 (아래 안타게..)
								////System.out.println(new Date() + " : FAX DAEMON STOP ::::: STOP STOP STOP........");
								extlogger.debug("[FAX DAEMON STOP ::::: STOP STOP STOP ........]");
								break;
							}
						}
					} catch(InterruptedException e) {
					} catch(Exception e) {
					}

					try {
						if(execloop==300) { //5분에 한번 호출..
							execloop = 0;
							faxRetUpdate();
						}
						
					} catch(NullPointerException e) {
					} catch(Exception e) {
					}
				}
			}
		};
		
		faxthread.setName("faxDaemonThread");		
		faxthread.setDaemon(true);
		faxthread.start();
	}
	
	/*
	 * 강제로 팩스작업 시킨다.
	 * */
	public void ForceWork(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		faxRetUpdate();
	}
	/*
	 * faxserver connect
	 * */
	private void faxRetUpdate() {
		try {
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("fax_id", "0");
			List list = commonDao.selectList("Common.Get_FaxKey", paramMap);
			if (list.size() > 0) {
				faxRetUpdate(list);
			}
		} catch (SQLException e) {
		} catch (Exception e) {
		}
	}
	
	private void faxRetUpdate(List<HashMap<String, String>> list) {
		
		Connection faxconn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			DriverManager.setLoginTimeout(5);
			
			if(ComUtil.isProd())
				faxconn = DriverManager.getConnection(Const.FAX_DB_URL, Const.FAX_DB_ACCOUNT, Const.FAX_DB_PASSWORD);
			else
				faxconn = DriverManager.getConnection(Const.TEST_FAX_DB_URL, Const.TEST_FAX_DB_ACCOUNT, Const.TEST_FAX_DB_PASSWORD);
			
			StringBuffer sb = new StringBuffer(100);
			
			sb.append(" select");
			sb.append("	seq_no");
			sb.append("	,fax_id");
			sb.append("	,ret_cd");
			sb.append("	,ret_msg");
			sb.append("  from");
			sb.append(" ( select");
			sb.append("    a.customer_fax_id1 as seq_no");
			sb.append(" 	,rank() over(PARTITION BY a.customer_fax_id1 ORDER BY rtrim(ltrim(a.fax_id)) DESC)  as rk");
			sb.append(" 	,rtrim(ltrim(a.fax_id)) as fax_id");
			sb.append(" 	,a.fax_result as ret_cd");
			sb.append(" 	,case a.fax_result");
			sb.append(" 	  when 1 then '발신성공'");
			sb.append(" 	  when 3 then '발신중'");
			sb.append(" 	  when 4 then isnull(b.result_desc,'')");
			sb.append(" 	  else '발신대기' end as ret_msg");
			sb.append("   from Fax_Last_Call_Detail_New a left join Dial_Result_Master b");
			sb.append("     on a.failed_result = b.result_code");
			sb.append("  where a.customer_fax_id1 IN ( ");
			for(int i=0; i<list.size(); i++) {
				sb.append("?,");
			}
			sb.append("'0' ) ");
			sb.append("   ) A ");			
			sb.append(" where A.rk = 1");
			
		    ps = faxconn.prepareStatement(sb.toString());
		    ps.setQueryTimeout(10);
		    
			HashMap<String, String> map = null;
		    for(int i=0; i<list.size(); i++) {
		    	map = new HashMap<String, String>();
				map = list.get(i);
			    ps.setString(i+1, map.get("SEQ_NO"));
			}
			rs = ps.executeQuery();
			
			while(rs.next()) {
				map = new HashMap<String, String>();
				map.put("fax_id", rs.getString("fax_id"));
				map.put("ret_cd", rs.getString("ret_cd"));
				map.put("ret_msg", rs.getString("ret_msg"));
				map.put("seq_no", rs.getString("seq_no"));
				commonDao.update("Common.Set_FaxResult", map);
			}
			
			////System.out.println(new Date() + " : FAX SAVE ::::: fax job good........");
			extlogger.debug("[FAX SAVE ::::: fax job good ........]");
			
		} catch (SQLException e) {
			////System.out.println(new Date() + " : FAX SAVE ::::: fax saving fail : " + e.toString());
			extlogger.debug("[FAX SAVE ::::: fax saving fail : " + e.toString()+"]");
		} catch (Exception e) {
			////System.out.println(new Date() + " : FAX SAVE ::::: fax saving fail : " + e.toString());
			extlogger.debug("[FAX SAVE ::::: fax saving fail : " + e.toString()+"]");
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){};
			try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){};
			try { if(faxconn !=null) faxconn.close(); faxconn=null; } catch(SQLException e){};	
		}
	}
}


