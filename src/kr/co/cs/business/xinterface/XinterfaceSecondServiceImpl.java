package kr.co.cs.business.xinterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.co.cs.business.xcommon.XcommonService;
import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.common.publicutil.NiceNameAuthentication;
import kr.co.cs.common.publicutil.SFTPUtil;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.persistence.dao.CommonDao;
import sun.jdbc.odbc.JdbcOdbcBatchUpdateException;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.httpclient.ConnectionPoolTimeoutException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jcraft.jsch.SftpException;
import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataTypes;

import eit.com.data.PData;
import eit.com.data.PDataType;
import eit.main.ePGate;
 
public class XinterfaceSecondServiceImpl implements XinterfaceSecondService{
	 
	private final static Logger secondlogger = LogManager.getLogger("process.if");
	private final static Logger secondExtlogger = LogManager.getLogger("process.ext");
	
	// parsing 구분 및 계행	
	private String strDlmtCrLf = "\n";
	private String strWithDLmt = "|^";
	private String strWithCrLf = "|#$";
	
	private CommonDao commonDao = null;
	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;
	}    
 
	
	
	/*
	 * 한신평 실명인증..
	 * */
	public void NameVerification(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		
		DataSet dsin = dto.getDataSet("DS_IN_VF");
		String name = dto.dsToString(dsin.getObject(0, "NAME"));
		String ssn = dto.dsToString(dsin.getObject(0, "SSN"));
		
		DataSet dsout = new DataSet("DS_OUT_VF");
		dsout.addColumn("YN", DataTypes.STRING, 255);
		dsout.addColumn("MSG", DataTypes.STRING, 255);
		dsout.newRow();
				
		NiceNameAuthentication nice = new NiceNameAuthentication();
		nice.setChkName(name);
		String ret = nice.setJumin(ssn + Const.NICE_SITE_PWD);
		if("0".equals(ret)) {
			nice.setSiteCode(Const.NICE_SITE_CODE);
			nice.setTimeOut(7000);
			ret = nice.getRtn().trim();
			if("1".equals(ret)) {
				dsout.set(0, "YN", "Y");
				dsout.set(0, "MSG", "실명확인이 되었습니다.");
			} else {
				dsout.set(0, "YN", "N");
				dsout.set(0, "MSG", Const.NICE_RET_MSG(Integer.parseInt(ret)));
			}			
		} else {
			dsout.set(0, "YN", "N");
			dsout.set(0, "MSG", "주민등록번호가 정확하지 않습니다.");
		}
		dto.getOutdslist().add(dsout);
		
		//try {
			HashMap map = dto.getRowMap(dto.getDataSet("DS_IN_VF"),0);
			map.put("ssn", "");
			secondlogger.debug("[NICE NameVerification]" + map + dsout.getObject(0, "MSG"));
		//} catch(Exception e){
		//}

	}
	
	
	public StringBuffer getIVRQueryASIS()
	{
        StringBuffer sb = new StringBuffer(100);
        
        sb.append(" SELECT");
        sb.append("  A.SERVICE_CODE,");
        sb.append("  A.SERVICE_NAME, ");
        sb.append("  ISNULL(B.N_ENTERED,0) AS N_ENTERED,");
        sb.append("  ISNULL(B.N_HOUR00,0) AS N_HOUR00,");
        sb.append("  ISNULL(B.N_HOUR01,0) AS N_HOUR01,");
        sb.append("  ISNULL(B.N_HOUR02,0) AS N_HOUR02,");
        sb.append("  ISNULL(B.N_HOUR03,0) AS N_HOUR03,");
        sb.append("  ISNULL(B.N_HOUR04,0) AS N_HOUR04,");
        sb.append("  ISNULL(B.N_HOUR05,0) AS N_HOUR05,");
        sb.append("  ISNULL(B.N_HOUR06,0) AS N_HOUR06,");
        sb.append("  ISNULL(B.N_HOUR07,0) AS N_HOUR07,");
        sb.append("  ISNULL(B.N_HOUR08,0) AS N_HOUR08,");
        sb.append("  ISNULL(B.N_HOUR09,0) AS N_HOUR09,");
        sb.append("  ISNULL(B.N_HOUR10,0) AS N_HOUR10,");
        sb.append("  ISNULL(B.N_HOUR11,0) AS N_HOUR11,");
        sb.append("  ISNULL(B.N_HOUR12,0) AS N_HOUR12,");
        sb.append("  ISNULL(B.N_HOUR13,0) AS N_HOUR13,");
        sb.append("  ISNULL(B.N_HOUR14,0) AS N_HOUR14,");
        sb.append("  ISNULL(B.N_HOUR15,0) AS N_HOUR15,");
        sb.append("  ISNULL(B.N_HOUR16,0) AS N_HOUR16,");
        sb.append("  ISNULL(B.N_HOUR17,0) AS N_HOUR17,");
        sb.append("  ISNULL(B.N_HOUR18,0) AS N_HOUR18,");
        sb.append("  ISNULL(B.N_HOUR19,0) AS N_HOUR19,");
        sb.append("  ISNULL(B.N_HOUR20,0) AS N_HOUR20,");
        sb.append("  ISNULL(B.N_HOUR21,0) AS N_HOUR21,");
        sb.append("  ISNULL(B.N_HOUR22,0) AS N_HOUR22,");
        sb.append("  ISNULL(B.N_HOUR23,0) AS N_HOUR23");
        sb.append("  FROM");
        sb.append("  Service_Code_Master_BEFORE A");
        sb.append("  LEFT JOIN");
        sb.append("  (");
        sb.append("  SELECT");
    //  sb.append("  audit_date,");
        sb.append("  service_code AS SERVICE_CODE,");
        sb.append("  sum(ISNULL(n_service_use,0)) AS N_ENTERED,");
        sb.append("  sum(case audit_hour when '00' then n_service_use else 0 end) as N_HOUR00,");
        sb.append("  sum(case audit_hour when '01' then n_service_use else 0 end) as N_HOUR01,");
        sb.append("  sum(case audit_hour when '02' then n_service_use else 0 end) as N_HOUR02,");
        sb.append("  sum(case audit_hour when '03' then n_service_use else 0 end) as N_HOUR03,");
        sb.append("  sum(case audit_hour when '04' then n_service_use else 0 end) as N_HOUR04,");
        sb.append("  sum(case audit_hour when '05' then n_service_use else 0 end) as N_HOUR05,");
        sb.append("  sum(case audit_hour when '06' then n_service_use else 0 end) as N_HOUR06,");
        sb.append("  sum(case audit_hour when '07' then n_service_use else 0 end) as N_HOUR07,");
        sb.append("  sum(case audit_hour when '08' then n_service_use else 0 end) as N_HOUR08,");
        sb.append("  sum(case audit_hour when '09' then n_service_use else 0 end) as N_HOUR09,");
        sb.append("  sum(case audit_hour when '10' then n_service_use else 0 end) as N_HOUR10,");
        sb.append("  sum(case audit_hour when '11' then n_service_use else 0 end) as N_HOUR11,");
        sb.append("  sum(case audit_hour when '12' then n_service_use else 0 end) as N_HOUR12,");
        sb.append("  sum(case audit_hour when '13' then n_service_use else 0 end) as N_HOUR13,");
        sb.append("  sum(case audit_hour when '14' then n_service_use else 0 end) as N_HOUR14,");
        sb.append("  sum(case audit_hour when '15' then n_service_use else 0 end) as N_HOUR15,");
        sb.append("  sum(case audit_hour when '16' then n_service_use else 0 end) as N_HOUR16,");
        sb.append("  sum(case audit_hour when '17' then n_service_use else 0 end) as N_HOUR17,");
        sb.append("  sum(case audit_hour when '18' then n_service_use else 0 end) as N_HOUR18,");
        sb.append("  sum(case audit_hour when '19' then n_service_use else 0 end) as N_HOUR19,");
        sb.append("  sum(case audit_hour when '20' then n_service_use else 0 end) as N_HOUR20,");
        sb.append("  sum(case audit_hour when '21' then n_service_use else 0 end) as N_HOUR21,");
        sb.append("  sum(case audit_hour when '22' then n_service_use else 0 end) as N_HOUR22,");
        sb.append("  sum(case audit_hour when '23' then n_service_use else 0 end) as N_HOUR23");
        sb.append("  FROM");
        sb.append("   Hour_Service_Result_BEFORE ");
        sb.append("  WHERE audit_date between ?");
        sb.append("  AND ?");
        sb.append("  GROUP BY SERVICE_CODE");
        sb.append("  )B");
        sb.append("  ON A.SERVICE_CODE = B.SERVICE_CODE");
        sb.append("  ORDER BY A.SERVICE_CODE ASC");
        return sb;
	}

	
	public StringBuffer getIVRCodeQuery()
	{
	    StringBuffer sb = new StringBuffer();
	    sb.append(" select a.SERVICE_CODE, a.SERVICE_NAME");
	    sb.append(" from service_code_master a");
	    sb.append(" where call_kind = ? and depth = '1'");
	    sb.append(" order by service_code");
        return sb;
	}
	
	public StringBuffer getIVRQuery(String service_code)
	{
	    service_code = service_code != null ? service_code : "";
        StringBuffer sb = new StringBuffer();

        String addQuery = "";
        if(!service_code.equals("TOP") && !service_code.equals(""))
        {
            addQuery = " AND service_code = '"+service_code+"'";
        }
        
        sb.append("\n         with cte ");
        sb.append("\n         as ");
        sb.append("\n         ( ");
        sb.append("\n         select a.service_code, a.depth, convert(varchar(100), a.service_name) as path,sort_no ");
        sb.append("\n         from service_code_master a ");
        sb.append("\n         where call_kind = ?  and stat_use_yn = 'Y' ");
        sb.append("\n         and depth = 1 "+addQuery);
        if(!service_code.equals("TOP"))
        {
        sb.append("\n         union all ");
        sb.append("\n         select b.service_code, b.depth, convert(varchar(100), path + '|'+ b.service_name),b.sort_no ");
        sb.append("\n         from service_code_master b, cte ");
        sb.append("\n         where call_kind = ? ");
        sb.append("\n         and cte.service_code = b.p_service_code ");
        }
        else 
        {
        sb.append("\n         AND call_kind = ? ");
        }
        sb.append("\n         ) ");
        sb.append("\n         SELECT ");
        sb.append("\n          A.SERVICE_CODE, ");
        sb.append("\n          A.B_SERVICE_NAME SERVICE_NAME, A.B_SERVICE_NAME, A.M_SERVICE_NAME, A.S_SERVICE_NAME, ");
        sb.append("\n          ISNULL(B.N_ENTERED,0) AS N_ENTERED, ");
        sb.append("\n          ISNULL(B.N_HOUR00,0) AS N_HOUR00, ");
        sb.append("\n          ISNULL(B.N_HOUR01,0) AS N_HOUR01, ");
        sb.append("\n          ISNULL(B.N_HOUR02,0) AS N_HOUR02, ");
        sb.append("\n          ISNULL(B.N_HOUR03,0) AS N_HOUR03, ");
        sb.append("\n          ISNULL(B.N_HOUR04,0) AS N_HOUR04, ");
        sb.append("\n          ISNULL(B.N_HOUR05,0) AS N_HOUR05, ");
        sb.append("\n          ISNULL(B.N_HOUR06,0) AS N_HOUR06, ");
        sb.append("\n          ISNULL(B.N_HOUR07,0) AS N_HOUR07, ");
        sb.append("\n          ISNULL(B.N_HOUR08,0) AS N_HOUR08, ");
        sb.append("\n          ISNULL(B.N_HOUR09,0) AS N_HOUR09, ");
        sb.append("\n          ISNULL(B.N_HOUR10,0) AS N_HOUR10, ");
        sb.append("\n          ISNULL(B.N_HOUR11,0) AS N_HOUR11, ");
        sb.append("\n          ISNULL(B.N_HOUR12,0) AS N_HOUR12, ");
        sb.append("\n          ISNULL(B.N_HOUR13,0) AS N_HOUR13, ");
        sb.append("\n          ISNULL(B.N_HOUR14,0) AS N_HOUR14, ");
        sb.append("\n          ISNULL(B.N_HOUR15,0) AS N_HOUR15, ");
        sb.append("\n          ISNULL(B.N_HOUR16,0) AS N_HOUR16, ");
        sb.append("\n          ISNULL(B.N_HOUR17,0) AS N_HOUR17, ");
        sb.append("\n          ISNULL(B.N_HOUR18,0) AS N_HOUR18, ");
        sb.append("\n          ISNULL(B.N_HOUR19,0) AS N_HOUR19, ");
        sb.append("\n          ISNULL(B.N_HOUR20,0) AS N_HOUR20, ");
        sb.append("\n          ISNULL(B.N_HOUR21,0) AS N_HOUR21, ");
        sb.append("\n          ISNULL(B.N_HOUR22,0) AS N_HOUR22, ");
        sb.append("\n          ISNULL(B.N_HOUR23,0) AS N_HOUR23 ");
        sb.append("\n          FROM ");
        sb.append("\n          ( ");
        sb.append("\n             select row_number() over(order by sort_no) rnum,   service_code, dbo.arr_split(path,'|',1) b_service_name, dbo.arr_split(path,'|',2) m_service_name, dbo.arr_split(path,'|',3) s_service_name ");
        sb.append("\n             from cte ");
        sb.append("\n         ) ");
        sb.append("\n           A ");
        sb.append("\n          LEFT JOIN ");
        sb.append("\n          ( ");
        sb.append("\n          SELECT ");
        sb.append("\n          service_code AS SERVICE_CODE, ");
        sb.append("\n          sum(ISNULL(n_service_use,0)) AS N_ENTERED, ");
        sb.append("\n          sum(case audit_hour when '00' then n_service_use else 0 end) as N_HOUR00, ");
        sb.append("\n          sum(case audit_hour when '01' then n_service_use else 0 end) as N_HOUR01, ");
        sb.append("\n          sum(case audit_hour when '02' then n_service_use else 0 end) as N_HOUR02, ");
        sb.append("\n          sum(case audit_hour when '03' then n_service_use else 0 end) as N_HOUR03, ");
        sb.append("\n          sum(case audit_hour when '04' then n_service_use else 0 end) as N_HOUR04, ");
        sb.append("\n          sum(case audit_hour when '05' then n_service_use else 0 end) as N_HOUR05, ");
        sb.append("\n          sum(case audit_hour when '06' then n_service_use else 0 end) as N_HOUR06, ");
        sb.append("\n          sum(case audit_hour when '07' then n_service_use else 0 end) as N_HOUR07, ");
        sb.append("\n          sum(case audit_hour when '08' then n_service_use else 0 end) as N_HOUR08, ");
        sb.append("\n          sum(case audit_hour when '09' then n_service_use else 0 end) as N_HOUR09, ");
        sb.append("\n          sum(case audit_hour when '10' then n_service_use else 0 end) as N_HOUR10, ");
        sb.append("\n          sum(case audit_hour when '11' then n_service_use else 0 end) as N_HOUR11, ");
        sb.append("\n          sum(case audit_hour when '12' then n_service_use else 0 end) as N_HOUR12, ");
        sb.append("\n          sum(case audit_hour when '13' then n_service_use else 0 end) as N_HOUR13, ");
        sb.append("\n          sum(case audit_hour when '14' then n_service_use else 0 end) as N_HOUR14, ");
        sb.append("\n          sum(case audit_hour when '15' then n_service_use else 0 end) as N_HOUR15, ");
        sb.append("\n          sum(case audit_hour when '16' then n_service_use else 0 end) as N_HOUR16, ");
        sb.append("\n          sum(case audit_hour when '17' then n_service_use else 0 end) as N_HOUR17, ");
        sb.append("\n          sum(case audit_hour when '18' then n_service_use else 0 end) as N_HOUR18, ");
        sb.append("\n          sum(case audit_hour when '19' then n_service_use else 0 end) as N_HOUR19, ");
        sb.append("\n          sum(case audit_hour when '20' then n_service_use else 0 end) as N_HOUR20, ");
        sb.append("\n          sum(case audit_hour when '21' then n_service_use else 0 end) as N_HOUR21, ");
        sb.append("\n          sum(case audit_hour when '22' then n_service_use else 0 end) as N_HOUR22, ");
        sb.append("\n          sum(case audit_hour when '23' then n_service_use else 0 end) as N_HOUR23 ");
        sb.append("\n          FROM ");
        sb.append("\n           Hour_Service_Result  with (nolock) "); // 20201104 1년치 조회 수정
        sb.append("\n          WHERE audit_date between ? AND ? ");
        sb.append("\n           ");
        sb.append("\n          GROUP BY SERVICE_CODE ");
        sb.append("\n          )B ");
        sb.append("\n          ON A.SERVICE_CODE = B.SERVICE_CODE ");
        sb.append("\n          order by rnum ");
        
        return sb;
	}
	
	
	/*
	 * ivr 통계 데이터 읽어오기...
	 * */
	public void IVRStaSearch(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		
		DataSet ds_in = dto.getDataSet("INTERFACE_DATA");
		DataSet ds_out = new DataSet("RESULT_DATA");
		
        String cic_gubun = dto.dsToString(ds_in.getObject(0, "CIC_GUBUN"));
        cic_gubun = cic_gubun == null ? "" : cic_gubun;		
		
		Connection ivrconn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			DriverManager.setLoginTimeout(10);

			if(ComUtil.isProd())
				ivrconn = DriverManager.getConnection(Const.IVR_DB_URL, Const.IVR_DB_ACCOUNT, Const.IVR_DB_PASSWORD);
			else
				ivrconn = DriverManager.getConnection(Const.TEST_IVR_DB_URL, Const.TEST_IVR_DB_ACCOUNT, Const.TEST_IVR_DB_PASSWORD);
			
			
			if(cic_gubun.equals("DATA"))
			{
			    String service_code = dto.dsToString(ds_in.getObject(0, "SRCH_SVC_NM"));
    			StringBuffer sb = getIVRQuery(service_code);
    			
    			System.out.println("XinterfaceSecondServiceImpl.IVRStaSearch() :  sb "+sb.toString());
    		    ps = ivrconn.prepareStatement(sb.toString());
    		    ps.setQueryTimeout(10);
    		    
                ps.setString(1, dto.dsToString(ds_in.getObject(0, "SRCH_GUBUN")));
                ps.setString(2, dto.dsToString(ds_in.getObject(0, "SRCH_GUBUN")));
                ps.setString(3, dto.dsToString(ds_in.getObject(0, "SRCH_DAY")));
                ps.setString(4, dto.dsToString(ds_in.getObject(0, "SRCH_DAY2")));    		    
    			rs = ps.executeQuery();
			}
			else if(cic_gubun.equals("CODE"))
			{
                StringBuffer sb = getIVRCodeQuery();
                ps = ivrconn.prepareStatement(sb.toString());
                ps.setQueryTimeout(10);			    
                ps.setString(1, dto.dsToString(ds_in.getObject(0, "SRCH_GUBUN")));                
                rs = ps.executeQuery();                
			}
			else
			{
                StringBuffer sb = getIVRQueryASIS();
                ps = ivrconn.prepareStatement(sb.toString());
                ps.setQueryTimeout(10);
                ps.setString(1, dto.dsToString(ds_in.getObject(0, "SRCH_DAY")));
                ps.setString(2, dto.dsToString(ds_in.getObject(0, "SRCH_DAY2")));
                rs = ps.executeQuery();			    
			}
			
			int row = 0;
			int icnt = 0;
		    ResultSetMetaData md = null;
		    while(rs.next()) {		    	
		    	if(icnt==0) {
			    	md = rs.getMetaData();
			    	if(md != null) {
			    		for(int i=0; i<md.getColumnCount(); i++) {
				    		ds_out.addColumn(md.getColumnName(i+1), DataTypes.STRING, 2000);
				    	}
			    	}
		    	}
		    	icnt=1;
		    	row = ds_out.newRow();
		    	if(md != null) {
		    		for(int i=0; i<md.getColumnCount(); i++) {
			    	    
			    	    String value = rs.getString(i+1);
			    		ds_out.set(row, md.getColumnName(i+1), value);
			    	}
		    	}
		    }

		    
		    
	    	dto.getOutdslist().add(ds_out);

			
		} catch (JdbcOdbcBatchUpdateException je) {
			secondExtlogger.debug("IVRStaSearch JdbcOdbcBatchUpdateException==>" + je.getMessage());
		} catch (StringIndexOutOfBoundsException se) {
			secondExtlogger.debug("IVRStaSearch StringIndexOutOfBoundsException==>" + se.getMessage());
		} catch (Exception e) {
			secondExtlogger.debug("IVRStaSearch Exception==>" + e.getMessage());
		} finally {
			if (ivrconn != null) { try { ivrconn.close(); } catch (SQLException e2) { secondExtlogger.debug(e2.getMessage()); } catch (Exception e) { secondExtlogger.debug(e.getMessage()); }}
			if (ps != null) {try {ps.close();} catch (SQLException e2) {secondExtlogger.debug(e2.getMessage());} catch (Exception e) {secondExtlogger.debug(e.getMessage());}}
			if (rs != null) {try {rs.close();} catch (SQLException e2) {secondExtlogger.debug(e2.getMessage());} catch (Exception e) {secondExtlogger.debug(e.getMessage());}}
		}
	} 
	
	// 연속 감청
	public void ListenContiue(XcommonDto dto, XcommonService xcommonservice) throws Exception {
	    
	    DataSet ds_in = dto.getDataSet("INTERFACE_DATA");
	    DataSet ds_out = new DataSet("RESULT_DATA");
	    
	    String gubun = dto.dsToString(ds_in.getObject(0, "GUBUN"));
	    
	    Connection cicdb_conn = null;
	    PreparedStatement ps = null;
	    try {
	        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
	        DriverManager.setLoginTimeout(10);
	        
	        if(ComUtil.isProd())
	            cicdb_conn = DriverManager.getConnection(Const.IVR_DB_URL, Const.IVR_DB_ACCOUNT, Const.IVR_DB_PASSWORD);
	        else
	            cicdb_conn = DriverManager.getConnection(Const.TEST_IVR_DB_URL, Const.TEST_IVR_DB_ACCOUNT, Const.TEST_IVR_DB_PASSWORD);
	        
	        
	        if(gubun.equals("LISTEN"))
	        {
	            String service_code = dto.dsToString(ds_in.getObject(0, "SRCH_SVC_NM"));
	            StringBuffer sb = new StringBuffer();
	            sb.append("                     UPDATE GSC_TB_LISTEN SET  use_yn = 'N'  where  admin_station = ? ;\n");
	            sb.append("                     MERGE INTO GSC_TB_LISTEN a   \n");
                sb.append("                     USING (SELECT ? admin_station, ? agent_id) b   \n");
                sb.append("                     ON (a.admin_station = b.admin_station and a.agent_id = b.agent_id)   \n");
                sb.append("                     WHEN MATCHED THEN   \n");
                sb.append("                         UPDATE SET         \n");
                sb.append("                            a.use_yn = 'Y'    \n");
                sb.append("                     WHEN NOT MATCHED THEN   \n");
                sb.append("                         INSERT   \n");
                sb.append("                         (admin_station, agent_id, use_yn)   \n");
                sb.append("                        VALUES   \n");
                sb.append("                         (?, ?, 'Y');   \n");
                System.out.println("XinterfaceSecondServiceImpl.ListenContiue() :  sb "+sb.toString());
	            ps = cicdb_conn.prepareStatement(sb.toString());
	            ps.setQueryTimeout(10);
	            ps.setString(1, dto.dsToString(ds_in.getObject(0, "ADMIN_STATION")));
	            ps.setString(2, dto.dsToString(ds_in.getObject(0, "ADMIN_STATION")));
	            ps.setString(3, dto.dsToString(ds_in.getObject(0, "LISTEN_AGENT_ID")));
	            ps.setString(4, dto.dsToString(ds_in.getObject(0, "ADMIN_STATION")));
	            ps.setString(5, dto.dsToString(ds_in.getObject(0, "LISTEN_AGENT_ID")));
	            ps.executeUpdate();
	        }
	        else 
	        {
                StringBuffer sb = new StringBuffer();
                
                sb.append("                    UPDATE GSC_TB_LISTEN SET         \n");
                sb.append("                            use_yn = 'N'    \n");
                sb.append("                     WHERE admin_station =?   \n");
                System.out                    .println("XinterfaceSecondServiceImpl.ListenContiue() :  sb "+sb.toString());                
                ps = cicdb_conn.prepareStatement(sb.toString());
                ps.setQueryTimeout(10);
                ps.setString(1, dto.dsToString(ds_in.getObject(0, "ADMIN_STATION")));
                ps.executeUpdate();         
	        }
	    } catch (JdbcOdbcBatchUpdateException je) {
			secondExtlogger.debug("CTIMiniBoard JdbcOdbcBatchUpdateException==>" + je.getMessage());
		} catch (StringIndexOutOfBoundsException se) {
			secondExtlogger.debug("CTIMiniBoard StringIndexOutOfBoundsException==>" + se.getMessage());
		} catch (Exception e) {
			secondExtlogger.debug("ListenContiue Exception==>" + e.getMessage());
	    } finally {
	    	if (cicdb_conn != null) { try { cicdb_conn.close(); } catch (SQLException e2) { secondExtlogger.debug(e2.getMessage()); } catch (Exception e) { secondExtlogger.debug(e.getMessage()); }}
	    	if (ps != null) {try {ps.close();} catch (SQLException e2) {secondExtlogger.debug(e2.getMessage());} catch (Exception e) {secondExtlogger.debug(e.getMessage());}}
	    }
	} 
	  
	
	/*
	 * cti 미니전광판 사용안함 2017.07.06
	 * */
	public void CTIMiniBoard(XcommonDto dto, XcommonService xcommonservice) throws Exception {

		DataSet ds_in = dto.getDataSet("INTERFACE_DATA");
		DataSet ds_out = new DataSet("RESULT_DATA");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
 
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
			DriverManager.setLoginTimeout(2);

			if(ComUtil.isProd())
				conn = DriverManager.getConnection(Const.CTI_STADB_URL, Const.CTI_STADB_ACCOUNT, Const.CTI_STADB_PASSWORD);
			else
				conn = DriverManager.getConnection(Const.TEST_CTI_STADB_URL, Const.TEST_CTI_STADB_ACCOUNT, Const.TEST_CTI_STADB_PASSWORD);
		    StringBuffer sb = new StringBuffer(100);
		    
		    sb.append(" select * ");
		    sb.append(" from GIS_STAT");
		    sb.append(" fetch first 1 rows only");
		    sb.append(" for read only with ur");
		    
		    ps = conn.prepareStatement(sb.toString());
		    ps.setQueryTimeout(2);
		    rs = ps.executeQuery();
		    
		    ResultSetMetaData md = null;
		    if(rs.next()) {
		    	md = rs.getMetaData();
		    	if(md != null) {
		    		for(int i=0; i<md.getColumnCount(); i++) {
			    		ds_out.addColumn(md.getColumnName(i+1), DataTypes.STRING, 2000);
			    	}
			    	ds_out.newRow();
			    	for(int i=0; i<md.getColumnCount(); i++) {
			    		ds_out.set(0, md.getColumnName(i+1), rs.getString(i+1));
			    	}
			    	dto.getOutdslist().add(ds_out);
		    	}
		    }
		    
		} catch (JdbcOdbcBatchUpdateException je) {
			secondExtlogger.debug("CTIMiniBoard JdbcOdbcBatchUpdateException==>" + je.getMessage());
		} catch (StringIndexOutOfBoundsException se) {
			secondExtlogger.debug("CTIMiniBoard StringIndexOutOfBoundsException==>" + se.getMessage());
		} catch (Exception e) {
			secondExtlogger.debug("CTIMiniBoard Exception==>" + e.getMessage());
			throw e;
			
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(SQLException se){secondExtlogger.debug("rs SQLException==>" + se.getMessage());} catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
			try { if(ps !=null) ps.close(); ps=null; } catch(SQLException se){secondExtlogger.debug("ps SQLException==>" + se.getMessage());}catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
			try { if(conn !=null) conn.close(); conn=null;} catch(SQLException se){secondExtlogger.debug("conn SQLException==>" + se.getMessage());}catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
		}
	}
	
	/*
	 * cti 미니전광판
	 * */
	public String CTIMiniBoard2() throws Exception {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String ret = "";
 
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
			DriverManager.setLoginTimeout(2);
			if(ComUtil.isProd())
				conn = DriverManager.getConnection(Const.CTI_STADB_URL, Const.CTI_STADB_ACCOUNT, Const.CTI_STADB_PASSWORD);
			else
				conn = DriverManager.getConnection(Const.TEST_CTI_STADB_URL, Const.TEST_CTI_STADB_ACCOUNT, Const.TEST_CTI_STADB_PASSWORD);
		    StringBuffer sb = new StringBuffer(100);
		    
		    sb.append(" select * ");
		    sb.append(" from GIS_STAT");
		    sb.append(" fetch first 1 rows only");
		    sb.append(" for read only with ur");
		    
		    ps = conn.prepareStatement(sb.toString());
		    ps.setQueryTimeout(2);
		    rs = ps.executeQuery();
		    
		    ResultSetMetaData md = null;
		    
		    StringBuffer sb1 = new StringBuffer(100);
		    StringBuffer sb2 = new StringBuffer(100);
		    
		    if(rs.next()) {
		    	md = rs.getMetaData();
		    	if(md != null) {
		    		for(int i=0; i<md.getColumnCount(); i++) {
			    		sb1.append(md.getColumnName(i+1));
			    		sb1.append("^");
			    	}
			    	for(int i=0; i<md.getColumnCount(); i++) {
			    		sb2.append(rs.getString(i+1)==null?"0":rs.getString(i+1));
			    		sb2.append("^");
			    	}
		    	}
		    }
		    ret = sb1.toString() + "|" + sb2.toString();
		    
		} catch (JdbcOdbcBatchUpdateException je) {
			secondExtlogger.debug("CTIMiniBoard2 JdbcOdbcBatchUpdateException==>" + je.getMessage());
		} catch (StringIndexOutOfBoundsException se) {
			secondExtlogger.debug("CTIMiniBoard2 StringIndexOutOfBoundsException==>" + se.getMessage());
		} catch (Exception e) {
			secondExtlogger.debug("CTIMiniBoard2 Exception==>" + e.getMessage());
			throw e;
			
		} finally {
			if (conn != null) { try { conn.close(); } catch (SQLException e2) { secondExtlogger.debug(e2.getMessage()); } catch (Exception e) { secondExtlogger.debug(e.getMessage()); }}
			if (ps != null) {try {ps.close();} catch (SQLException e2) {secondExtlogger.debug(e2.getMessage());} catch (Exception e) {secondExtlogger.debug(e.getMessage());}}
			if (rs != null) {try {rs.close();} catch (SQLException e2) {secondExtlogger.debug(e2.getMessage());} catch (Exception e) {secondExtlogger.debug(e.getMessage());}}
		}
		
		return ret;
	}
	
	
	/*
	 * cti 상담원 등록 : CTI AGENT SKILL GROUP 등록시 ;를 두어서 여러건을 삭제 및 등록한다.
	 * AGENT 스킬그릅운 CTI담당자 :  안현민 대리님이 주시면 최종 DB에 UPDATE를 하고 사용자 화면에서 관리할예정
	 * */
	public void CTIUserSyncSave(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		
		secondExtlogger.debug("==CTIUserSyncSave==");
		
		DataSet ds_env 			= dto.getDataSet("INTERFACE_ENV_DATA");
		DataSet ds_in 			= dto.getDataSet("INTERFACE_0DATA");  //삭제스킬그룹
		DataSet ds_in1 			= dto.getDataSet("INTERFACE_1DATA");  //등록스킬그룹
		DataSet ds_out 			= new DataSet("RESULT_DATA");
		
		String type 			= dto.dsToString(ds_env.getObject(0, "TYPE"));  //1:상담사 등록, 2:상담사 상태변경
		String cti_id 			= dto.dsToString(ds_env.getObject(0, "CTI_ID"));  //CTI ID
		String usr_id 			= dto.dsToString(ds_env.getObject(0, "USR_ID"));  //Smile2 ID
		String usr_nm 			= dto.dsToString(ds_env.getObject(0, "USR_NM"));  //상담사명
		
		usr_nm = new String(usr_nm.getBytes(Const.KOREA_CHARSET));
		
		secondExtlogger.debug("type ==>" + type);
		secondExtlogger.debug("cti_id ==>" + cti_id);
		secondExtlogger.debug("usr_id ==>" + usr_id);
		secondExtlogger.debug("usr_nm ==>" + usr_nm);
		
		int rtn = 0; //성공여부
		
		ePGate eGate = new ePGate();
	
		try{
			
			secondExtlogger.debug("==connect== start");
			eGate.connect();
			
			/*
			 * 상담사등록 및 스킬그룹 등록	
			 */			
			if ("1".equals(type)){
				
				secondExtlogger.debug("==상담사등록==");
				rtn = eGate.getPersonCreate(cti_id,usr_id,usr_nm,usr_id,cti_id);  //상담사등록
				secondExtlogger.debug("rtn==>" + rtn);
				
				secondExtlogger.debug("==상담사 그룹등록==");
				int k=0;
				String skillName="";
				for(k=0; k < ds_in1.getRowCount(); k++) {
					skillName = dto.dsToString(ds_in1.getObject(k, "SKIL_CDNM"));
					//skillName = new String(skillName.getBytes("iso8859-1"), "UTF-8");
					skillName = new String(skillName.getBytes(Const.KOREA_CHARSET));
					secondExtlogger.debug("==상담사 skillName==" + skillName);
					rtn = eGate.getAgtAddAgentGroup(usr_id, skillName);  //상담사그룹등록
					secondExtlogger.debug("rtn==>" + rtn);
				}
							
			/*
			 * 상담사이름변경및 스킬그룹 삭제및 등록	
			 */
			}else if ("2".equals(type)){
				
				secondExtlogger.debug("==상담사변경==");
				secondExtlogger.debug("==usr id==" + usr_id + " ::ctiid  "+cti_id + "name " + usr_nm);
				rtn = eGate.getPersonUpDate(usr_id,usr_id ,usr_nm ,usr_id,cti_id);
				secondExtlogger.debug("rtn==>" + rtn);
				
				if(rtn != 0){
					secondExtlogger.debug("==변경 실패 후 상담사 신규등록 ==");
					rtn = eGate.getPersonCreate(cti_id,usr_id,usr_nm,usr_id,cti_id);  //상담사등록
					secondExtlogger.debug("rtn==>" + rtn);
				}
				
				secondExtlogger.debug("==상담사 그룹삭제==");
				int l=0;
				String skillName="";
				for(l=0; l < ds_in.getRowCount(); l++) {
					skillName = dto.dsToString(ds_in.getObject(l, "SKIL_CDNM"));
					//skillName = new String(skillName.getBytes("iso8859-1"), "UTF-8");
					skillName = new String(skillName.getBytes(Const.KOREA_CHARSET));
					secondExtlogger.debug("==상담사 skillName==" + skillName);
					rtn = eGate.getAgtDelAgentGroup(usr_id, skillName);
					secondExtlogger.debug("rtn==>" + rtn);
				}				
				//퇴사자그룹 삭제.
				try {
					eGate.getAgtDelAgentGroup(usr_id, new String("퇴사자그룹".getBytes(Const.KOREA_CHARSET)));
				} catch(NullPointerException ne) {secondExtlogger.debug(" NullPointerException==>" + ne.getMessage());
				} catch(StringIndexOutOfBoundsException se) {secondExtlogger.debug(" StringIndexOutOfBoundsException==>" + se.getMessage());
				} catch(Exception e) {secondExtlogger.debug("Exception==>" + e.getMessage());}
				
				secondExtlogger.debug("==상담사 그룹등록==");
				int k=0;
				skillName="";
				for(k=0; k < ds_in1.getRowCount(); k++) {
					skillName = dto.dsToString(ds_in1.getObject(k, "SKIL_CDNM"));
					//skillName = new String(skillName.getBytes("iso8859-1"), "UTF-8");
					skillName = new String(skillName.getBytes(Const.KOREA_CHARSET));
					secondExtlogger.debug("==상담사 skillName==" + skillName);
					rtn = eGate.getAgtAddAgentGroup(usr_id, skillName);  //상담사그룹등록
					secondExtlogger.debug("rtn==>" + rtn);
				}	 
				
			}else{
				secondExtlogger.debug("==상담사 퇴사자==");
				
				secondExtlogger.debug("==상담사 그룹삭제==");
				int l=0;
				String skillName="";
				for(l=0; l < ds_in.getRowCount(); l++) {
					skillName = dto.dsToString(ds_in.getObject(l, "SKIL_CDNM"));
					//skillName = new String(skillName.getBytes("iso8859-1"), "UTF-8");
					skillName = new String(skillName.getBytes(Const.KOREA_CHARSET));
					secondExtlogger.debug("==상담사 skillName==" + skillName);
					rtn = eGate.getAgtDelAgentGroup(usr_id, skillName);
					secondExtlogger.debug("rtn==>" + rtn);
				}
				
				//퇴사그룹등록
				eGate.getAgtAddAgentGroup(usr_id, new String("퇴사자그룹".getBytes(Const.KOREA_CHARSET)));					
			}
			
			eGate.disconnect();	
		}catch (ConnectException ce) {
			secondExtlogger.debug("CTIUserSyncSave ConnectException==>" + ce.getMessage());
		}catch (Exception e) {
			secondExtlogger.debug("CTIUserSyncSave Exception==>" + e.getMessage());
		}
		
		secondExtlogger.debug("rtn==>" + rtn);
		
		//성공메세지 전달
		ds_out.addColumn("result", DataTypes.STRING, 2000);
    	int row = ds_out.newRow();
		ds_out.set(row, "result", rtn);
		dto.getOutdslist().add(ds_out);	
	}
	
	/*
	 * cti 상담원 등록 : CTI AGENT SKILL GROUP 등록시 ;를 두어서 여러건을 삭제 및 등록한다.
	 * AGENT 스킬그릅운 CTI담당자 :  안현민 대리님이 주시면 최종 DB에 UPDATE를 하고 사용자 화면에서 관리할예정
	 * */
	public void CTIUserMonitoring(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		
		/*
		70005 || 23 || TestAgent5 || 김개똥 ||
		70003 || 23 || TestAgent3 || 상담원3 || Place7530
		70002 || 8 || TestAgent2 || 테스트 || Place4002
		70001 || 4 || TestAgent1 || 상담원1 || Place4001
		*/
		
		secondExtlogger.debug("CTIUserMonitoring==>");

		DataSet ds_in 			= dto.getDataSet("INTERFACE_DATA");
		DataSet ds_out 			= new DataSet("RESULT_DATA");
		
		String skill_group		= dto.dsToString(ds_in.getObject(0, "SKILL_GROUP"));  //스킬그룹
		skill_group = new String(skill_group.getBytes(Const.KOREA_CHARSET));
		
		secondExtlogger.debug("skill_group==>[" + skill_group + "]");
		
		 
		try{
			ePGate eGate = new ePGate();
			eGate.connect();
			//PData data = eGate.getPersonStat_Group("AG");
			secondExtlogger.debug("==1");
			PData data = eGate.getPersonStat_Group(skill_group);
			
			if (data != null){ 
				
				secondExtlogger.debug("==2");
				secondExtlogger.debug("==data ==>" + data.toString());
				HashMap<String, PDataType> hm = data.getHash();
				secondExtlogger.debug("==3");
				Set s = hm.entrySet();
				Iterator it = s.iterator();
				
				secondExtlogger.debug("it.hasNext()==>" + it.hasNext());
				int row = 0;
				int icnt = 0;			
				while (it.hasNext()) {
					Map.Entry m = (Map.Entry) it.next();
					String key = (String) m.getKey();
					PDataType value = (PDataType) m.getValue();
	       
			    	if(icnt==0) {				    	
				    	ds_out.addColumn("USERID", DataTypes.STRING, 2000);
				    	ds_out.addColumn("USERST", DataTypes.STRING, 2000);	
				    	ds_out.addColumn("USERNO", DataTypes.STRING, 2000);	
				    	ds_out.addColumn("USERNM", DataTypes.STRING, 2000);	
				    	ds_out.addColumn("USERDN", DataTypes.STRING, 2000);
			    	}  
			    	icnt=1;
			    	
			    	row = ds_out.newRow();
			    	
			    	ds_out.set(row, "USERID" , value.getUserID());
			    	ds_out.set(row, "USERST" , value.getUserST());
			    	ds_out.set(row, "USERNO" , value.getUserNO() );
			    	//ds_out.set(row, "USERNM" , value.getUserNM());
			    	//ds_out.set(row, "USERNM" , new String(value.getUserNM().getBytes(Const.DEFAULT_CHARSET)));			    	 
			    	ds_out.set(row, "USERNM" , new String(new String(value.getUserNM().getBytes(Const.KOREA_CHARSET)).getBytes("UTF-8")));
			    	ds_out.set(row, "USERDN" , value.getUserDN());
					
					secondExtlogger.debug(value.getUserID() 
							 + " || " 
							 + value.getUserST() 
							 + " || " 
							 + value.getUserNO() 
							 + " || " + value.getUserNM()
							 + " || " + value.getUserDN()
					); // AGTID			
				}
				
				eGate.disconnect();
			}
			
			dto.getOutdslist().add(ds_out);
			
		} catch (NullPointerException ne) {
			secondExtlogger.debug("CTIUserMonitoring NullPointerException==>" + ne.getMessage());
		} catch (Exception e) {
			secondExtlogger.debug("CTIUserMonitoring Exception ==>" + e.getMessage());
		}
	}
	
	
		
	/*
	 * 사용자관리 저장 -> 운영관리 CAMS 사용자 정보 수정(사용자명, CTI ID, 내선번호) 2017.06.29
	* */
	public void CAMSUserSyncSave(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		
		secondExtlogger.debug("========================= CAMSUserSyncSave =========================");
		
		DataSet ds_in 				= dto.getDataSet("INTERFACE_DATA");	
		DataSet ds_out 				= new DataSet("RESULT_DATA");
		
		String gubun = dto.dsToString(ds_in.getObject(0, "GUBUN"));
        gubun = gubun == null ? "" : gubun;		
		
		String cmpny_emp_cd			= dto.dsToString(ds_in.getObject(0, "CMPNY_EMP_CD"));  			// 사용자ID
		String emp_nm 				= dto.dsToString(ds_in.getObject(0, "EMP_NM"));  				// 사용자명
		String phone_user_id 		= dto.dsToString(ds_in.getObject(0, "PHONE_USER_ID"));  		// CTI ID
		String phone_extension_no	= dto.dsToString(ds_in.getObject(0, "PHONE_EXTENSION_NO"));	    // 내선번호
		String group_cd				= dto.dsToString(ds_in.getObject(0, "GROUP_CD"));				// 그룹번호
		String rtrmnt_dd			= dto.dsToString(ds_in.getObject(0, "RTRMNT_DD"));				// 퇴사일
		String rtrmnt_yn			= dto.dsToString(ds_in.getObject(0, "RTRMNT_YN"));				// 퇴사여부

		
		//emp_nm = new String(emp_nm.getBytes(Const.KOREA_CHARSET));
		
		secondExtlogger.debug("cmpny_emp_cd 		==>" + cmpny_emp_cd);
		secondExtlogger.debug("emp_nm 				==>" + emp_nm);
		secondExtlogger.debug("phone_user_id 		==>" + phone_user_id);
		secondExtlogger.debug("phone_extension_no 	==>" + phone_extension_no);
		secondExtlogger.debug("gubun 				==>" + gubun);	
		secondExtlogger.debug("group_cd 			==>" + group_cd);
		secondExtlogger.debug("rtrmnt_dd 			==>" + rtrmnt_dd);
		secondExtlogger.debug("rtrmnt_yn 			==>" + rtrmnt_yn);
		
		Connection camsconn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = 0;
		
		StringBuffer sb = null;
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			DriverManager.setLoginTimeout(10);
		
			camsconn = DriverManager.getConnection(Const.CAMS_DB_URL, Const.CAMS_DB_ACCOUNT, Const.CAMS_DB_PASSWORD);
					
			//기존고객여부 체크
			 sb = getCAMSEmpSelectQuery();
			 ps = camsconn.prepareStatement(sb.toString());
			 ps.setQueryTimeout(10);
			 ps.setString(1, cmpny_emp_cd);
			 rs = ps.executeQuery();

			 gubun = "I";
			 while ( rs.next() ) {
				gubun = "U";												
			 }
		 
			if(gubun.equals("I"))
			{
			    secondExtlogger.debug("==INSERT==");
			
			    sb = getCAMSEmpCreateQuery();
   			
			    ps = camsconn.prepareStatement(sb.toString());
			    ps.setQueryTimeout(10);
		
                ps.setString(1, cmpny_emp_cd);
                ps.setString(2, emp_nm);
                ps.setString(3, phone_user_id);
                ps.setString(4, phone_extension_no);    		    
                result = ps.executeUpdate();
   				
				if(result > 0)
				{				
					sb = getCAMSLoginCreateQuery();

					ps = camsconn.prepareStatement(sb.toString());
					ps.setQueryTimeout(10);
			
					ps.setString(1, cmpny_emp_cd);					
					result = ps.executeUpdate();	
				}
				
				if(result > 0)
				{								
					sb = getCAMSGroupUserMapCreateQuery();
					
					ps = camsconn.prepareStatement(sb.toString());
					ps.setQueryTimeout(10);
			
					ps.setString(1, group_cd);		
					ps.setString(2, cmpny_emp_cd);
					result = ps.executeUpdate();					
				}
			}
			else if(gubun.equals("U"))  //사용자 정보 수정 
			{			
				secondExtlogger.debug("==UPDATE==");
				
				sb = getCAMSEmpUpdateQuery();
				
				ps = camsconn.prepareStatement(sb.toString());
				ps.setQueryTimeout(10);			    
					
			    ps.setString(1, emp_nm);  
			    ps.setString(2, phone_user_id);  
			    ps.setString(3, phone_extension_no);
			    ps.setString(4, rtrmnt_yn);
			    ps.setString(5, rtrmnt_dd);			    
			    ps.setString(6, cmpny_emp_cd);  
			    			    
				result = ps.executeUpdate();
			
				if(result > 0)
				{
					sb = getCAMSGroupUpdateQuery();
					
					ps = camsconn.prepareStatement(sb.toString());
					ps.setQueryTimeout(10);			    
						
				    ps.setString(1, group_cd);  
				    ps.setString(2, cmpny_emp_cd);  
				    ps.setString(3, group_cd);  //콜인프라 요청 201902
				    
					result = ps.executeUpdate();					
				}								
			}
			secondExtlogger.debug("result==>" + result + "::::camsconn==>" + camsconn);			
		} catch (JdbcOdbcBatchUpdateException je) {
			secondExtlogger.debug("CAMSUserSyncSave JdbcOdbcBatchUpdateException==>" + je.getMessage());
		} catch (StringIndexOutOfBoundsException se) {
			secondExtlogger.debug("CAMSUserSyncSave StringIndexOutOfBoundsException==>" + se.getMessage());
		} catch (Exception e) {
			secondExtlogger.debug("CAMSUserSyncSave Exception==>" + e.getMessage());
	
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(SQLException se){secondExtlogger.debug("rs SQLException==>" + se.getMessage());} catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
			try { if(ps !=null) ps.close(); ps=null; } catch(SQLException se){secondExtlogger.debug("ps SQLException==>" + se.getMessage());}catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
			try { if(camsconn !=null) camsconn.close(); camsconn=null;} catch(SQLException se){secondExtlogger.debug("conn SQLException==>" + se.getMessage());}catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
		}
	
		
		//성공메세지 전달
		ds_out.addColumn("result", DataTypes.STRING, 2000);
    	int row = ds_out.newRow();
		ds_out.set(row, "result", result);
		dto.getOutdslist().add(ds_out);	
	} 
	
	
	public StringBuffer getCAMSEmpSelectQuery()
	{
		StringBuffer sb = new StringBuffer();
       
		sb.append("\n	select cmpny_emp_cd from tb_emp ");		
		sb.append("\n	WHERE cmpny_emp_cd = ?	");
		
		return sb;
	}
		
	public StringBuffer getCAMSEmpUpdateQuery()
	{
		StringBuffer sb = new StringBuffer();
       
		sb.append("\n	update tb_emp set		");
		sb.append("\n	emp_nm = ?,				");
		sb.append("\n	phone_user_id = ?,		");
		sb.append("\n	phone_extension_no = ?,	");		
		sb.append("\n	rtrmnt_yn = ?,	");
		sb.append("\n	rtrmnt_dd = ?	");
		sb.append("\n	WHERE cmpny_emp_cd = ?	");
		
		return sb;
	}
		
	public StringBuffer getCAMSEmpCreateQuery()
	{
		StringBuffer sb = new StringBuffer();
       
		sb.append("\n	insert into tb_emp (cmpny_emp_cd, emp_nm, phone_user_id, phone_extension_no, rtrmnt_yn, cmpny_no)	");
		sb.append("\n	values (?, ?, ?, ?, 'N','1')				");
						
		return sb;
	}
	
	public StringBuffer getCAMSLoginCreateQuery()
	{
		StringBuffer sb = new StringBuffer();

        sb.append("\n	insert into tb_login	");
		sb.append("\n	(user_id, emp_no, psswd, psswd_err_num)	");
	    sb.append("\n	select ");
		sb.append("\n	cmpny_emp_cd, emp_no, 'AE82AF783B03EA82173BDAB1E98BFF38', '0'	"); //gs!call2010
		sb.append("\n	from tb_emp where cmpny_emp_cd = ?	");
	
		return sb;
	}
	
	public StringBuffer getCAMSGroupUserMapCreateQuery()
	{
		StringBuffer sb = new StringBuffer();
	
		sb.append("\n	insert into tb_group_user_map	");
		sb.append("\n	(grp_no, user_no, reg_dt, reg_user_no)	");
	    sb.append("\n	select	");
		sb.append("\n	?, emp_no, getdate(), '999'	");
		sb.append("\n	from tb_emp	");
		sb.append("\n	where cmpny_emp_cd = ?");
	
		return sb;
	}
		
	public StringBuffer getCAMSGroupUpdateQuery()
	{
		StringBuffer sb = new StringBuffer();
    			
		sb.append("\n	update tb_group_user_map set ");
		sb.append("\n	grp_no = ?	");
		sb.append("\n	where user_no = (select emp_no from tb_emp where cmpny_emp_cd = ?)	");
		sb.append("\n	and grp_no = ?	"); //콜인프라 수정 요청으로 20190207
		return sb;
	}

		
	/*
	 * 사용자관리 저장 -> 운영관리 MSG 사용자 정보 수정 2017.07.12
	* */
	public void MSGUserSyncSave(XcommonDto dto, XcommonService xcommonservice) throws IOException {
		
		secondExtlogger.debug("========================= MSGUserSyncSave =========================");
		
		DataSet ds_in 				= dto.getDataSet("INTERFACE_DATA");	
		DataSet ds_out 				= new DataSet("RESULT_DATA");
		
		String gubun = dto.dsToString(ds_in.getObject(0, "GUBUN"));
        gubun = gubun == null ? "" : gubun;		
		        
        
		String cmpny_emp_cd			= dto.dsToString(ds_in.getObject(0, "CMPNY_EMP_CD"));  			// 사용자ID
		String emp_nm 				= dto.dsToString(ds_in.getObject(0, "EMP_NM"));  				// 사용자명
		String phone_user_id 		= dto.dsToString(ds_in.getObject(0, "PHONE_USER_ID"));  		// CTI ID
		String phone_extension_no	= dto.dsToString(ds_in.getObject(0, "PHONE_EXTENSION_NO"));	    // 내선번호
		String usr_part_cd			= dto.dsToString(ds_in.getObject(0, "USR_PART_CD"));	    	// 사용자 파트코드	 
		String usr_part_nm			= dto.dsToString(ds_in.getObject(0, "USR_PART_NM"));	    	// 사용자 파트명
		String part_cd				= dto.dsToString(ds_in.getObject(0, "PART_CD"));	    		// 상담사가 속해있는 파트코드들 ,로 구분되어 옴	 
		String part_nm				= dto.dsToString(ds_in.getObject(0, "PART_NM"));	    		// 상담사가 속해있는 파트명들 ,로 구분되어 옴	 
				
		//emp_nm = new String(emp_nm.getBytes(Const.KOREA_CHARSET));
		
		if(part_nm.indexOf(usr_part_nm) == -1){
			part_cd += ","+usr_part_cd;
		   	part_nm += ","+usr_part_nm;
		}
		 
		secondExtlogger.debug("cmpny_emp_cd 		==>" + cmpny_emp_cd);
		secondExtlogger.debug("emp_nm 				==>" + emp_nm);
		secondExtlogger.debug("phone_user_id 		==>" + phone_user_id);
		secondExtlogger.debug("phone_extension_no 	==>" + phone_extension_no);
		secondExtlogger.debug("gubun 				==>" + gubun);	
		secondExtlogger.debug("usr_part_cd 			==>" + usr_part_cd);
		secondExtlogger.debug("usr_part_nm 			==>" + usr_part_nm);
		secondExtlogger.debug("part_cd 				==>" + part_cd);
		secondExtlogger.debug("part_nm 				==>" + part_nm);
		
		
		Connection msgconn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int result = 0;
		
		StringBuffer sb = null;
		
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
			DriverManager.setLoginTimeout(10);
		
			msgconn = DriverManager.getConnection(Const.MSG_DB_URL, Const.MSG_DB_ACCOUNT, Const.MSG_DB_PASSWORD);
					
			if(gubun.equals("I"))
			{
			    secondExtlogger.debug("==INSERT==");
			  	
			    sb = getMSGEmpCreateQuery();
			    
			    ps = msgconn.prepareStatement(sb.toString());
			    ps.setQueryTimeout(10);
		
                ps.setString(1, cmpny_emp_cd);
                ps.setString(2, emp_nm);
                ps.setString(3, phone_extension_no);
                ps.setString(4, phone_user_id);
                ps.setString(5, phone_extension_no);
                ps.setString(6, phone_user_id);
                ps.setString(7, phone_extension_no);
                ps.setString(8, usr_part_cd);
                ps.setString(9, usr_part_nm);
                      		
                result = ps.executeUpdate();
			    
            	if(result > 0)
				{
            		sb = getMSGUserMasterCreateQuery();
					ps = msgconn.prepareStatement(sb.toString());
					ps.setQueryTimeout(10);
			
					ps.setString(1, cmpny_emp_cd);		
					ps.setString(2, emp_nm);
					result = ps.executeUpdate();	            		
				}

            	if(result > 0)
				{								
					sb = getMSGUserInfoCreateQuery();
					ps = msgconn.prepareStatement(sb.toString());
					ps.setQueryTimeout(10);
					
					ps.setString(1, cmpny_emp_cd);
					ps.setString(2, emp_nm);
					result = ps.executeUpdate();					
				}
            	
            	if(result > 0)
				{								
					sb = getMSGBuddyGroupCreateQuery(part_nm); 					
					ps = msgconn.prepareStatement(sb.toString());
					ps.setQueryTimeout(10);
					
					ps.setString(1, cmpny_emp_cd);					
					result = ps.executeUpdate();		
				}
            	
            	if(result > 0)
				{								
					sb = getMSGBuddyListCreateQuery(); 					
					ps = msgconn.prepareStatement(sb.toString());
					ps.setQueryTimeout(10);
					
					ps.setString(1, cmpny_emp_cd);					
					result = ps.executeUpdate();		
				}            	  			  
			}
			else if(gubun.equals("U"))  //사용자 정보 수정 
			{			
				secondExtlogger.debug("==UPDATE==");
											
				sb = getMSGEmpUpdateQuery(); 					
				ps = msgconn.prepareStatement(sb.toString());
				ps.setQueryTimeout(10);
				
				ps.setString(1, emp_nm);
                ps.setString(2, phone_extension_no);
                ps.setString(3, phone_user_id);
                ps.setString(4, phone_extension_no);
                ps.setString(5, phone_user_id);
                ps.setString(6, phone_extension_no);
                ps.setString(7, usr_part_cd);
                ps.setString(8, usr_part_nm);
                ps.setString(9, cmpny_emp_cd);      						
				result = ps.executeUpdate();		
				
				if(result > 0)
				{								
					sb = getMSGUserInfoUpdateQuery(); 
					
					ps = msgconn.prepareStatement(sb.toString());
					ps.setQueryTimeout(10);
					
					ps.setString(1, emp_nm);
					ps.setString(2, cmpny_emp_cd);					
					result = ps.executeUpdate();		
				}
				
				if(result > 0)
				{								
					sb = getMSGUserMasterUpdateQuery(); 					
					ps = msgconn.prepareStatement(sb.toString());
					ps.setQueryTimeout(10);
					
					ps.setString(1, emp_nm);
					ps.setString(2, cmpny_emp_cd);					
					result = ps.executeUpdate();		
				}
								
			}
			
			if(result > 0)
			{							
				sb = getMSGBuddyGroupAddCreateQuery();

				ps = msgconn.prepareStatement(sb.toString());
				ps.setQueryTimeout(10);
			
				ps.setString(1, usr_part_nm);					
				ps.setString(2, cmpny_emp_cd);
				ps.setString(3, usr_part_nm);
				result = ps.executeUpdate();

				sb = getMSGBuddyListAddCreateQuery();

				ps = msgconn.prepareStatement(sb.toString());
				ps.setQueryTimeout(10);
			
				ps.setString(1, cmpny_emp_cd);					
				ps.setString(2, cmpny_emp_cd);
				ps.setString(3, usr_part_nm);
				ps.setString(4, cmpny_emp_cd);	
				ps.setString(5, usr_part_nm);

				result = ps.executeUpdate();
			}
			
			try { if(rs !=null) rs.close(); rs=null; } catch(SQLException se){secondExtlogger.debug("rs SQLException==>" + se.getMessage());} catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
			try { if(ps !=null) ps.close(); ps=null; } catch(SQLException se){secondExtlogger.debug("ps SQLException==>" + se.getMessage());}catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
			try { if(msgconn !=null) msgconn.close(); msgconn=null;} catch(SQLException se){secondExtlogger.debug("conn SQLException==>" + se.getMessage());}catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}	
		} catch (SQLException se) {
			secondExtlogger.debug("MSGUserSyncSave SQLException :: " + se.getMessage());
		} catch (NullPointerException ne) {
			secondExtlogger.debug("MSGUserSyncSave NullPointerException :: " + ne.getMessage());
		} catch (StringIndexOutOfBoundsException se) {
			secondExtlogger.debug("MSGUserSyncSave StringIndexOutOfBoundsException :: " + se.getMessage());
		} catch (Exception e) {
			secondExtlogger.debug("MSGUserSyncSave Exception==>" + e.getMessage());
	
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(SQLException se){secondExtlogger.debug("rs SQLException==>" + se.getMessage());} catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
			try { if(ps !=null) ps.close(); ps=null; } catch(SQLException se){secondExtlogger.debug("ps SQLException==>" + se.getMessage());}catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}
			try { if(msgconn !=null) msgconn.close(); msgconn=null;} catch(SQLException se){secondExtlogger.debug("conn SQLException==>" + se.getMessage());}catch(Exception e){secondExtlogger.debug("rs Exception==>" + e.getMessage());}	
		}
		
		secondExtlogger.debug("result==>" + result + "::::msgconn==>" + msgconn);
		
		//성공메세지 전달
		ds_out.addColumn("result", DataTypes.STRING, 2000);
    	int row = ds_out.newRow();
		ds_out.set(row, "result", result);
		dto.getOutdslist().add(ds_out);	
		
	} 
		
	public StringBuffer getMSGEmpUpdateQuery()
	{
		StringBuffer sb = new StringBuffer();
       
		sb.append("\n	update tb_emp set		");
		sb.append("\n	emp_nm = ?,				");
		sb.append("\n	ext_tel_no = ?,			");
		sb.append("\n	cti_agent_id = ?,		");
		sb.append("\n	cti_phone_no = ?,		");
		sb.append("\n	phone_user_id = ?,		");
		sb.append("\n	phone_extension_no = ?,	");		
		sb.append("\n	part_cd = ?,			");
		sb.append("\n	part_nm = ?				");		
		sb.append("\n	WHERE cmpny_emp_cd = ?	");
		
		return sb;
	}
	
	public StringBuffer getMSGUserInfoUpdateQuery()
	{
		StringBuffer sb = new StringBuffer();
       
		sb.append("\n	update msg_user_info set		");
		sb.append("\n	displayname = ?					");		
		sb.append("\n	WHERE user_id = ?				");
		
		return sb;
	}
	
	public StringBuffer getMSGUserMasterUpdateQuery()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("\n	update msg_user_master set									");
		sb.append("\n	user_nm = ?,												");		
		sb.append("\n	passwd = 'J/AIxiMgPWH3TfWQqb64eQR+6kS0B8it9YfVA4FiSho='		");
		sb.append("\n	WHERE user_id = ?											");
	
		return sb;
	}
	
	public StringBuffer getMSGEmpCreateQuery()
	{
		StringBuffer sb = new StringBuffer();
	
		sb.append("\n	insert into tb_emp	");
		sb.append("\n	(cmpny_emp_cd, emp_nm, ext_tel_no, rtrmnt_yn, cmpny_no, phone_user_id, phone_extension_no, cti_agent_id, cti_phone_no ,part_cd, part_nm)	");
		sb.append("\n	values");
		sb.append("\n	(?, ?, ?, 'N', '1', ?, ?, ?, ?, ?, ?)");

		return sb;
	}
	
	public StringBuffer getMSGUserMasterCreateQuery()
	{
		StringBuffer sb = new StringBuffer();
       
		sb.append("\n	insert into msg_user_master 	");
		sb.append("\n	select ?, 'kyobobook.co.kr', ?, null, 'J/AIxiMgPWH3TfWQqb64eQR+6kS0B8it9YfVA4FiSho=', '','','','Y',null,'','1','' 	");
						
		return sb;
	}
		
	public StringBuffer getMSGUserInfoCreateQuery()
	{
		StringBuffer sb = new StringBuffer();
       
		sb.append("\n	insert into msg_user_info 	");
		sb.append("\n	select ?, 'kyobobook.co.kr',-1,'','','','','',?, 0, '' 	");
						
		return sb;
	}
	
	public StringBuffer getMSGBuddyGroupCreateQuery(String part)
	{
		StringBuffer sb = new StringBuffer();
	
		sb.append("\n	insert into msg_buddy_group ");
		sb.append("\n	select a11, a22, a33, user_id, a55, a66 ");
		sb.append("\n	from ( ");
		sb.append("\n		select user_id ");
		sb.append("\n		from msg_user_info ");
		sb.append("\n		) a, ");
		sb.append("\n	( ");		
		
		String[] p = part.split(",");
		for(int i=0; i< p.length; i++) {
			if(i == 0) sb.append("\n	select '"+p[i]+"' as a11 , 'N' as a22, 'Y' as a33, 'kyobobook.co.kr' as a55, null as a66");
			else  sb.append("\n	union all select '"+p[i]+"', 'N', 'Y', 'kyobobook.co.kr', null ");
		}
		sb.append("\n	) b ");
		sb.append("\n	where a.user_id = ? ");

		return sb;
	}

	
	public StringBuffer getMSGBuddyGroupCreateQuery2()
	{
		StringBuffer sb = new StringBuffer();
	
		sb.append("\n	insert into msg_buddy_group ");
		sb.append("\n	(group_name, iseditable, isdefault, user_id, domain, etc)");
		sb.append("\n	values");
		sb.append("\n	(?, 'N', 'Y', ?, 'kyobobook.co.kr', null)");

		return sb;
	}

	
	public StringBuffer getMSGBuddyListCreateQuery()
	{
		StringBuffer sb = new StringBuffer();
	
		sb.append("\n	insert into msg_buddy_list ");
		sb.append("\n	select a.GROUP_IDX, b.buddy_id, 'kyobobook.co.kr', a.owner_id, 'kyobobook.co.kr', 'N', null, '3' ");
		sb.append("\n	from ( ");
		sb.append("\n			select a.user_id AS owner_id, b.GROUP_IDX, b.GROUP_NAME ");
		sb.append("\n			from MSG_USER_INFO a, MSG_BUDDY_GROUP b ");
		sb.append("\n			where a.USER_ID = b.USER_ID ");
		sb.append("\n			and a.user_id = ? ");
		sb.append("\n		) a, ");
		sb.append("\n		( ");
		sb.append("\n			select cmpny_emp_cd AS buddy_id, part_nm AS GROUP_NAME from tb_emp ");
		sb.append("\n		) b ");
		sb.append("\n	where a.owner_id <> buddy_id ");
		sb.append("\n	and a.GROUP_NAME = b.GROUP_NAME ");

		return sb;
	}
	
			
	//기존 메신저 사용자에게 신규사용자 정보 추가 
	public StringBuffer getMSGBuddyGroupAddCreateQuery()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n	insert into msg_buddy_group ");
		sb.append("\n	select ?, 'N', 'Y', user_id, 'kyobobook.co.kr', null ");
		sb.append("\n	from ( ");
		sb.append("\n		select user_id ");
		sb.append("\n		from msg_user_info ");
		sb.append("\n		where user_id NOT IN ( ");
		sb.append("\n			select ? "); //새로 추가될 직원ID
		sb.append("\n			UNION ALL ");
		sb.append("\n			select user_id ");
		sb.append("\n			from msg_buddy_group ");
		sb.append("\n			where group_name = ? "); //'추가그룹'
		sb.append("\n			group by user_id) ");		
		sb.append("\n	) a ");
		
		return sb;
	}

	
	//기존 메신저 사용자에게 신규사용자 정보 추가 
	public StringBuffer getMSGBuddyListAddCreateQuery()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n	insert into msg_buddy_list ");
		sb.append("\n	select b.GROUP_IDX, b.buddy_id, 'kyobobook.co.kr', a.owner_id, 'kyobobook.co.kr', 'N', null, '3' ");
		sb.append("\n	from ( ");
		sb.append("\n		select a.user_id AS owner_id ");
		sb.append("\n		from msg_user_info a ");
		sb.append("\n		where a.user_id NOT IN ( ");
		sb.append("\n			select ? ");
		sb.append("\n			UNION ALL ");
		sb.append("\n			select owner_id ");
		sb.append("\n			FROM msg_buddy_list ");
		sb.append("\n			where buddy_id = ? ");
		sb.append("\n			and GROUP_IDX = ( ");
		sb.append("\n			select top(1) GROUP_IDX ");
		sb.append("\n			FROM msg_buddy_group ");
		sb.append("\n			Where GROUP_NAME = ? ");
		sb.append("\n			order by GROUP_IDX desc) ");		
		sb.append("\n		) ");
		sb.append("\n	) a, ");
		sb.append("\n	( ");
		sb.append("\n		select top(1) ? AS buddy_id, GROUP_IDX, GROUP_NAME ");  //--새로 추가될 직원ID와 파트정보 
		sb.append("\n		from msg_buddy_group ");
		sb.append("\n		where GROUP_NAME = ? ");
		sb.append("\n		order by GROUP_IDX desc ");
		sb.append("\n	) b ");
				  
		return sb;			
	}				
			

	//분리보관(탈회대상자) 수동배치 시작-----------------------------------------
	/**
	 * 분리보관 수동배치 분기처리
	 * 2024-06 | np847
	 */
	public void custWithdrawBatch(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		DataSet ds_out = new DataSet("RESULT_DATA");
		DataSet ds_env = dto.getDataSet("INTERFACE_ENV");
		
		String batchGbn = "";
		int rtn = 0; //성공여부
		
		try {
			if(ds_env != null) {
				batchGbn = dto.dsToString(ds_env.getObject(0, "BATCH_KIND"));
			}
			
			secondExtlogger.debug("batchGbn :: " + batchGbn);
			
			//1. GSC 대상자 수신
			if("SU_BATCH01".equals(batchGbn)) {
				procBatch01(dto);
			}
			//2. 탈회대상 테이블 및 대상자 조회, 파일생성
			if("SU_BATCH02".equals(batchGbn)) {
				procBatch02(dto);
			}
			//3. 분리보관서버-CMSFTP로 대상자 전송
			if("SU_BATCH03".equals(batchGbn)) {
				procBatch03(dto);
			}
			//4. 탈회대상자 정보 CSC SERVER 파일생성 및 CSC DB DATA삭제 후 파일전송
			if("SU_BATCH04".equals(batchGbn)) {
				procBatch04(dto);
			}
			rtn = 1;
		} catch (NullPointerException ne) {
			secondExtlogger.debug("SU_BATCH NullPointerException :: " + ne.getMessage());
			rtn = 2;
		} catch (StringIndexOutOfBoundsException se) {
			secondExtlogger.debug("SU_BATCH StringIndexOutOfBoundsException :: " + se.getMessage());
			rtn = 2;
		} finally {
			if(rtn == 1) {
				//성공메세지 전달
				ds_out.addColumn("result", DataTypes.STRING, 2000);
		    	int row = ds_out.newRow();
				ds_out.set(row, "result", "SUCCESS");
				dto.getOutdslist().add(ds_out);	
			} else {
				//실패메세지 전달
				ds_out.addColumn("result", DataTypes.STRING, 2000);
		    	int row = ds_out.newRow();
				ds_out.set(row, "result", "FAIL");
				dto.getOutdslist().add(ds_out);	
			}
		}
		
	}

	/**
	 * GSC  분리보관 고객 대상정보 수신 수동배치
	 * 2024-06 | np847
	 */
	private void procBatch01(XcommonDto dto)throws Exception {
		secondExtlogger.debug("## 분리보관 수동배치 procBatch01 Start!! ##");
		
		
		String withdraw_url = "";
		String withdraw_account = "";
		String withdraw_password = "";
		
		DataSet ds_in = dto.getDataSet("INTERFACE_DATA");
		String pramDay = dto.dsToString(ds_in.getObject(0, "WORK_DAY")); 
		List<String> paramList = new ArrayList<String>();
		
		secondExtlogger.debug("## 분리보관 수동배치 요청 일자 ::" + pramDay);
		
		if (pramDay == null || "".equals(pramDay) || "null".equals(pramDay)) {
			pramDay = ComUtil.getCurDateTime("yyyyMMdd");
		} else {
			paramList = new ArrayList<String>(Arrays.asList(pramDay.split(",")));
		}
		
		
		try {
			String serverNm = InetAddress.getLocalHost().getHostName().trim();

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
			secondExtlogger.debug("HOST EXCEPTION !! :: " + e.getMessage());
		}
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData md = null;
		StringBuffer sb = null;
		
		HashMap<String, String> map = new HashMap<String, String>();
		int roofCnt = 0;
		
		for(int k = 0; k < paramList.size(); k++) {
			try {
				//mssql jdbc
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
				DriverManager.setLoginTimeout(10);

				//mssql 연결
				conn = DriverManager.getConnection(withdraw_url, withdraw_account, withdraw_password);

				//쿼리 생성
				sb = withdrawQuery();

				//쿼리 실행
				ps = conn.prepareStatement(sb.toString());
				ps.setQueryTimeout(10);
				ps.setString(1, paramList.get(k));

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
							if(map.keySet() != null) {
								secondExtlogger.debug("## 탈회대상 DB Insert 수동 작업 실행 !!");
								//탈회대상 고객 CSC DB insert
								commonDao.insert("ETC_STA.withDrawUserInfo_INSERT", map);
							}
						} catch (ConnectionPoolTimeoutException cpe) {
							secondExtlogger.debug("[ConnectionPoolTimeoutException : procBatch01  " + cpe.getMessage() + "][DATA : " + map + "]");
							break;
						} catch (SQLException se) {
							secondExtlogger.debug("[SQLException : procBatch01  " + se.getMessage() + "][DATA : " + map + "]");
							break;
						}
						roofCnt++;
					}
				}
			} catch (ConnectionPoolTimeoutException cpe) {
				secondExtlogger.debug("ConnectionPoolTimeoutException ERROR : " + cpe.getMessage());
				break;
			} catch (SQLException se) {
				secondExtlogger.debug("SQL EXCEPTION ERROR : " + se.getMessage());
				break;
			} catch (Exception e) {
				secondExtlogger.debug("JDBC CONNECTION ERROR : " + e.getMessage());
				break;
			} finally {
				if (conn != null) { try { conn.close(); } catch (SQLException e2) { secondExtlogger.debug(e2.getMessage()); } catch (Exception e) { secondExtlogger.debug(e.getMessage()); }}
				if (ps != null) {try {ps.close();} catch (SQLException e2) {secondExtlogger.debug(e2.getMessage());} catch (Exception e) {secondExtlogger.debug(e.getMessage());}}
				if (rs != null) {try {rs.close();} catch (SQLException e2) {secondExtlogger.debug(e2.getMessage());} catch (Exception e) {secondExtlogger.debug(e.getMessage());}}
				if (sb != null) {try {sb.delete(0, sb.length());} catch (ClassCastException e2) {secondExtlogger.debug(e2.getMessage());} catch (Exception e) {secondExtlogger.debug(e.getMessage());}}
			}
		}
		
	}

	/**
	 * 탈회대상 테이블 및 대상자 조회, 파일생성
	 * 2024-06 | np847
	 * @param dto
	 */
	private void procBatch02(XcommonDto dto) {
		secondExtlogger.debug("## 분리보관 수동배치 procBatch02 Start!! ##");

		String strErrMsg = "";
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");
		
		DataSet ds_in = dto.getDataSet("INTERFACE_DATA");
		String pramDay = dto.dsToString(ds_in.getObject(0, "WORK_DAY")); 
		List<String> paramList = new ArrayList<String>();
		
		if (pramDay == null || "".equals(pramDay) || "null".equals(pramDay)) {
			pramDay = ComUtil.getCurDateTime("yyyyMMdd");
		} else {
			paramList = new ArrayList<String>(Arrays.asList(pramDay.split(",")));
		}
		
		secondExtlogger.debug("======>2) SU_Div_CustFileDw::start==>" + pramDay);
		secondExtlogger.debug("======>file name Day  bWorkDay:::::" + bWorkDay);
		HashMap map = new HashMap();
		
		StringBuffer strSql1 = new StringBuffer();
		StringBuffer strSql2 = new StringBuffer();

		for(int k = 0; k < paramList.size(); k++) {
			try {
				// -------------------------------------------------------------
				//복원 삭제 대상자 
				// -------------------------------------------------------------

				String sqlmapid = "ETC_STA.DPXGZA4";
				map.put("I_WORK_DT", paramList.get(k)); //익일 전날짜 처리 
				commonDao.selectString(sqlmapid, map);

				//01:휴면 02:파기 03:복원 04:탈회
				List listDeCust = (ArrayList) map.get("O_RESULT1"); // 삭제 대상01분리보관 02파기 04탈회 대상자를 가지고 온다
				List listReCust = (ArrayList) map.get("O_RESULT2"); // 복원 대상 03복원 대상자를 가지고 온다.

				secondExtlogger.debug("SU_strDeCust : " + listDeCust);
				secondExtlogger.debug("SU_strDeCust : " + listReCust);

				HashMap<String, String> mapRow1 = null;
				HashMap<String, String> mapRow2 = null;

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

				String filePath = Const.WITH_GSIB_DIV_DOWN + paramList.get(k) + File.separator; //파일 패스는 전날짜

				//탈회
				String tbName1 = Const.WITH_GSIB_DE_CUST + "." + bWorkDay; //파일이름은 현재날짜로 
				//복원
				String tbName2 = Const.WITH_GSIB_RE_CUST + "." + bWorkDay; //파일이름은 현재날짜로 

				secondExtlogger.debug("tbName1===>" + tbName1);
				secondExtlogger.debug("tbName1===>" + tbName2);

				createFile(strSql1.toString(), tbName1, paramList.get(k), filePath);
				createFile(strSql2.toString(), tbName2, paramList.get(k), filePath);

				//자원 초기화
				strSql1.delete(0, strSql1.length());
				strSql2.delete(0, strSql2.length());
				
				secondExtlogger.debug("======>2) SU_Div_CustFileDw::END");
			} catch (FileUploadException fe) {
				strErrMsg = "SU_FileUploadException Error!:" + fe.getMessage();
				secondExtlogger.debug(strErrMsg);
				break;
			} catch (SQLException sqe) {
				strErrMsg = "SU_SQLException Error!:" + sqe.getMessage();
				secondExtlogger.debug(strErrMsg);
				break;
			} catch (Exception e) {
				strErrMsg = "SU_Exception Error!:" + e.getMessage();
				secondExtlogger.debug(strErrMsg);
				break;
			} finally {
				// 배치작업종료로그출력
				secondExtlogger.debug("SU_Div_CustFileDw END");
			}
		}
	}

	/**
	 * CMSFTP로 대상자 전송
	 * 2024-06 | np847
	 * @param dto
	 */
	private void procBatch03(XcommonDto dto) throws IOException, SftpException {

		String strErrMsg = "";
		boolean bDnload = false;
		SFTPUtil sftpUtil = new SFTPUtil();

		DataSet ds_in = dto.getDataSet("INTERFACE_DATA");
		String pramDay = dto.dsToString(ds_in.getObject(0, "WORK_DAY")); 
		List<String> paramList = new ArrayList<String>();
		
		if (pramDay == null || "".equals(pramDay) || "null".equals(pramDay)) {
			pramDay = ComUtil.getCurDateTime("yyyyMMdd");
		} else {
			paramList = new ArrayList<String>(Arrays.asList(pramDay.split(",")));
		}

		//file name을 위해 현재 날짜
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");

		String filePath1 = "";
		String filePath2 = "";
		String fileName1 = "";
		String fileName2 = "";

		secondExtlogger.debug("======>3) SU_Div_CustFileSend::STAR bWorkDay==>" + bWorkDay);
		secondExtlogger.debug("======>3) SU_Div_CustFileSend::STAR pWorkD::==>" + pramDay);

		// ------------------------------------
		// 파일 cmsftp로 전송
		// ------------------------------------
		String FTP_IP;
		String FTP_ID;
		String FTP_PWD;
		String FTP_ROOT;
		int FTP_PORT = 0;
		
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
		secondExtlogger.debug("FTP . IP=>[" + FTP_IP + "], ID=>[" + FTP_ID + "], PWD=>[" + FTP_PWD + "], ROOT=>[" + FTP_ROOT + "], PORT=>[" + FTP_PORT + "]");

		//파일 이름은 생성일 기준으로 수정 해야 한다. 
		fileName1 = Const.WITH_GSIB_RE_CUST + "." + bWorkDay; //복원 대상 파일 name
		fileName2 = Const.WITH_GSIB_DE_CUST + "." + bWorkDay; //파기 대상 파일 name
		
		secondExtlogger.debug("Div_CustFileSend fileName1:" + fileName1);
		secondExtlogger.debug("Div_CustFileSend fileName2:" + fileName2);
		
		for(int k = 0; k < paramList.size(); k++) {
			try {
				//파기/복원 대상 파일 다운로드 
				filePath1 = Const.WITH_GSIB_DIV_DOWN + paramList.get(k) + File.separator;

				secondExtlogger.debug("Div_CustFileSend filePath1:" + filePath1);
				
				sftpUtil.init(FTP_IP, FTP_ID, FTP_PWD, FTP_PORT);

				boolean bUpload1 = sftpUtil.upload(FTP_ROOT, filePath1 + fileName1);
				boolean bUpload2 = sftpUtil.upload(FTP_ROOT, filePath1 + fileName2);

				sftpUtil.disconnection();

				secondExtlogger.debug("SU_Send File result1=>>[" + bUpload1 + "]");
				secondExtlogger.debug("SU_Send File result2=>>[" + bUpload2 + "]");
			} catch (StringIndexOutOfBoundsException se) {
				secondExtlogger.debug("SU_Div_CustFileSend StringIndexOutOfBoundsException :: " + se.getMessage());
				break;
			} catch (Exception e) {
				secondExtlogger.debug("SU_Div_CustFileSend Exception :: " + e.getMessage());
				break;
			} finally {
				sftpUtil.disconnection();
			}
		}
	}
	
	/**
	 * 탈회대상자 정보 파일생성 및 삭제 후 파일전송
	 * 2024-06 | np847
	 * @param dto
	 */
	private void procBatch04(XcommonDto dto) {
		String strErrMsg = "";
		boolean bDnload = false;

		//file name을 위해 현재 날짜
		String bWorkDay = ComUtil.getCurDateTime("yyyyMMdd");
		
		DataSet ds_in = dto.getDataSet("INTERFACE_DATA");
		String pramDay = dto.dsToString(ds_in.getObject(0, "WORK_DAY")); 
		List<String> paramList = new ArrayList<String>();
		
		if (pramDay == null || "".equals(pramDay) || "null".equals(pramDay)) {
			pramDay = ComUtil.getCurDateTime("yyyyMMdd");
		} else {
			paramList = new ArrayList<String>(Arrays.asList(pramDay.split(",")));
		}

		secondExtlogger.debug("======> SU_Div_CustDataSend bWorkDay::==>" + bWorkDay);
		secondExtlogger.debug("======> SU_Div_CustDataSend pramDay::==>" + pramDay);

		
		StringBuffer strSql1 = new StringBuffer();
		StringBuffer strSql2 = new StringBuffer();
		
		for(int k = 0; k < paramList.size(); k++) {
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
				secondExtlogger.debug("[DPXGZA0] Target Table Select = [" + list1.size() + "]");
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
							//자원 초기화
							strSql1.delete(0, strSql1.length());
							strSql2.delete(0, strSql2.length());
							
							HashMap map2 = new HashMap();
							
							map2.put("I_OWNER", owner);
							map2.put("I_TABLE_NAME", tbName);

							commonDao.selectString(sqlmapid, map2);

							ArrayList list2 = (ArrayList) map2.get("O_RESULT"); //

							//extlogger.debug( "Table column select = [" +list2.size()+"]");

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
							strSql2.append("   AND B.WORK_DT = '" + paramList.get(k) + "'"); //전날짜를 조회해 온다.
							strSql2.append(strDlmtCrLf);
							strSql2.append("   AND B.RT_DIV_CODE  in('01','02','04')"); // RT_DIV_CODE 분리보관 대상 :01  파기:02,   복원:03 탈회:04

							secondExtlogger.debug("tbName========>" + tbName);

							sqlmapid = "ETC_STA.DPXGZA3_" + tbName;
							map2.put("I_SQL", strSql2.toString());
							commonDao.selectString(sqlmapid, map2);
							String rstCd = (String) map2.get("O_RTN_CD");

							secondExtlogger.debug("ETC_STA.DPXGZA3 rstCd= [" + rstCd + "]");
							if (rstCd.equals("0")) {
								//대상자 정보
								ArrayList list3 = (ArrayList) map2.get("O_RESULT");

								//출력 디렉터리 생성 
								String filePath = Const.WITH_GSIB_DE_DOWN + paramList.get(k) + File.separator;
								secondExtlogger.debug("========>4filePath" + filePath);

								File dir = new File(filePath);
								if (!dir.isDirectory()) {
									//디렉터리 생성 
									dir.mkdir();
								}

								//파일 생성
								secondExtlogger.debug("======>4) SU_Div_CustDataSend_createWithFile::STAR");
								createWithFileSU(list2, list3, strSql1.toString(), tbName, paramList.get(k), filePath);
							}
						}
					}
				}
			} catch (FileUploadException fe) {
				strErrMsg = "FileUploadException Error!:" + fe.getMessage();
				secondExtlogger.debug(strErrMsg);
				break;
			} catch (SQLException sqe) {
				strErrMsg = "SQLException Error!:" + sqe.getMessage();
				secondExtlogger.debug(strErrMsg);
				break;
			} catch (Exception e) {
				strErrMsg = "Exception Error!:" + e.getMessage();
				secondExtlogger.debug(strErrMsg);
				break;
			} finally {
				// 배치작업종료로그출력
				strSql1.delete(0, strSql1.length());
				strSql2.delete(0, strSql2.length());
				secondExtlogger.debug("==============Div_CustDataSend END=============");
			}
		}
	}
	
	/**
	 * 분리보관 탈회대상자 MSSQL 쿼리
	 * @return Stringbuffer
	 */
	private StringBuffer withdrawQuery() {
		StringBuffer sb = new StringBuffer();

		sb.append("\n	select work_dt, cust_no, intg_cust_no, rt_dt_div_code");
		sb.append("\n from t_gc_rtcs_if_m");
		sb.append("\n	where work_dt = ?");
		sb.append("\n	and rt_dt_div_code = \'04\'");

		return sb;
	}
	
	/**
	 * 탈회대상자 CSC SERVER 파일 생성
	 * 파일명 : RE.IS2002 | DE.IS2002 => /APPDATA/SMILE2/SFTP/DIV/
	 * @param strSql1
	 * @param Filename
	 * @param pWorkD
	 * @param FilefilePath
	 * @throws Exception
	 */
	private void createFile(String strSql1, String Filename, String pWorkD, String FilefilePath) throws Exception {
		StringBuffer sbSPfile = new StringBuffer();

		secondExtlogger.debug("strSql1 ::" + strSql1);
		secondExtlogger.debug("createfile Filename ::" + Filename);
		secondExtlogger.debug("createfile FilefilePath ::" + FilefilePath);

		// 출력디렉토리설정						
		File dir = new File(FilefilePath);
		if (!dir.isDirectory())
			dir.mkdir();
		secondExtlogger.debug("======>2) SU createFile ::start==>");
		//파일 생성
		File dirFile = new File(FilefilePath + Filename);

		if (!dirFile.exists()) {
			dirFile.createNewFile();
		}

		PrintWriter prtWtr = new PrintWriter(new FileWriter(dirFile));
		sbSPfile.append(strSql1);

		prtWtr.println(sbSPfile);
		prtWtr.close();
		secondExtlogger.debug("======>2) SU createFile ::END==>");
	}
	
	/**
	 * 수동배치 탈회대상자 파일 생성
	 * 파일명 : IN.SP_테이블명_날짜 => /APPDATA/SMILE2/SFTP/DOWN/
	 * 2024-06 | np847
	 * @param list2
	 * @param list3
	 * @param string
	 * @param tbName
	 * @param pramDay
	 * @param filePath
	 */
	private void createWithFileSU(ArrayList tblList, ArrayList datalList, String strSql1, String tbName, String WorkDay, String FilefilePath) {
		String sbSPfile = "";

		secondExtlogger.debug("==========createWithFileSU START======" + WorkDay);
		String bWorkDay = WorkDay;
		//file name을 위해 현재 날짜
		String toWorkDay = ComUtil.getCurDateTime("yyyyMMdd");
		String Filename = "IN.SP_" + tbName + "." + toWorkDay;

		try {
			secondExtlogger.debug("==========createWithFileSU Filename ==>" + Filename);
			//테이블별 파일 생성
			HashMap<String, String> mapRow = null;
			HashMap<String, String> mapRow1 = null;

			// 파일생성 하고 싶은면 주석 해제
			File dirFile = new File(FilefilePath+ Filename);
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
			secondExtlogger.debug("==========createWithFileSU sbSPfile ::" + sbSPfile);

			prtWtr.println(strBuf);

			prtWtr.close();

			//생성 후 전송
			secondExtlogger.debug("createWithFileSU END======");
			SendFtpFileSU(tbName, Filename, bWorkDay);
		} catch (FileNotFoundException fe) {
			secondExtlogger.debug("createWithFileSU FileNotFoundException = [" + fe.getMessage() + " ]");
		} catch (IOException ie) {
			secondExtlogger.debug("createWithFileSU IOException = [" + ie.getMessage() + " ]");
		} catch (Exception e) {
			secondExtlogger.debug("createWithFileSU Exception = [" + e.getMessage() + " ]");
		}
		
	}
	
	/**
	 * Sparrow 파일 삭제 처리(TOCTOU)
	 */
	private synchronized void fileDelete(File file) {
		file.delete();
	}
	
	/**
	 * 수동배치 탈회대상자 CSC DB데이터 cmsftp 전송
	 * 2024-06 | np847
	 * @param tbName
	 * @param filename
	 * @param bWorkDay
	 */
	private void SendFtpFileSU(String tbName, String Filename, String bWorkDay)throws IOException, SftpException {
		secondExtlogger.debug("SU SFTP Send START:: file name =>" + tbName);
		secondExtlogger.debug("SU SFTP Sned Start======" + bWorkDay);

		String filePath = "";
		// ------------------------------------
		// 파일 cmsftp로 전송
		// ------------------------------------
		String FTP_IP;
		String FTP_ID;
		String FTP_PWD;
		String FTP_ROOT;
		int FTP_PORT = 0;
		
		SFTPUtil sftpUtil = new SFTPUtil();
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
			secondExtlogger.debug("re : " + FTP_ROOT + "<---->send:" + filePath + ":::::" + Filename);

			sftpUtil.init(FTP_IP, FTP_ID, FTP_PWD, FTP_PORT);
			boolean bUpload = sftpUtil.upload(FTP_ROOT, filePath + Filename);

			sftpUtil.disconnection();

			//전송 성공이면 데이터 삭제 
			if (bUpload == true) {
				secondExtlogger.debug("SU_SFTP SUCCES=> Delete Start::" + tbName);
				//이력 때문에 나누어 삭제 처리 한다.
				//분리 보관 삭제(탈회)	    	
				secondExtlogger.debug("3)SE Delete call!!");
				DeCustDBSU(tbName, "TE", bWorkDay);

			}
		} catch (StringIndexOutOfBoundsException se) {
			secondExtlogger.debug("FILE SU StringIndexOutOfBoundsException :: " + se.getMessage());
		} catch (Exception e) {
			secondExtlogger.debug("FILE SU EXCEPTION ::" + e.getMessage());
		} finally {
			sftpUtil.disconnection();
		}
		
	}



	/**
	 * 수동배치 탈회대상자 cmsftp 전송 후 CSC DB 테이블 데이터 정보 삭제
	 * @param tbName
	 * @param string
	 * @param bWorkDay
	 */
	private void DeCustDBSU(String tbName, String div, String bWorkDay) {
		secondExtlogger.debug("====DeCustDBSU Start====::" + div);

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
			secondExtlogger.debug("sqlmapid1::" + sqlmapid1 + "====DB Delete END");
		} catch (SQLException sql) {
			secondExtlogger.debug("sql.getMessage :: " + sql.getMessage());
		} catch (Exception e) {
			secondExtlogger.debug("sql.getMessage :: " + e.getMessage());
		}
	}
	
	//분리보관(탈회대상자) 수동배치 종료-----------------------------------------

}

