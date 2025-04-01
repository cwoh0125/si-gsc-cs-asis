package kr.co.cs.business.xinterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kr.co.cs.business.xcommon.XcommonService;
import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.common.publicutil.NiceNameAuthentication;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.persistence.dao.CommonDao;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataTypes;

import eit.com.data.PData;
import eit.com.data.PDataType;
import eit.main.ePGate;
 
public class XinterfaceSecondServiceImpl implements XinterfaceSecondService{
	 
	private final static Logger secondlogger = LogManager.getLogger("process.if");
	private final static Logger secondExtlogger = LogManager.getLogger("process.ext");
	
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
			    	for(int i=0; i<md.getColumnCount(); i++) {
			    		ds_out.addColumn(md.getColumnName(i+1), DataTypes.STRING, 2000);
			    	}
		    	}
		    	icnt=1;
		    	row = ds_out.newRow();
		    	for(int i=0; i<md.getColumnCount(); i++) {
		    	    
		    	    String value = rs.getString(i+1);
		    		ds_out.set(row, md.getColumnName(i+1), value);
		    	}			    	
		    }

		    
		    
	    	dto.getOutdslist().add(ds_out);

			
		} catch (Exception e) {
			throw new Exception("ivr 통계데이터 조회중 오류 " + e.toString());
	
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(Exception e){};
			try { if(ps !=null) ps.close(); ps=null; } catch(Exception e){};
			try { if(ivrconn !=null) ivrconn.close(); ivrconn=null;} catch(Exception e){};	
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
	    } catch (Exception e) {
	        throw new Exception("연속감청 세팅 오류 " + e.toString());
	        
	    } finally {
	        try { if(ps !=null) ps.close(); ps=null; } catch(Exception e){};
	        try { if(cicdb_conn !=null) cicdb_conn.close(); cicdb_conn=null;} catch(Exception e){};	
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
		    	for(int i=0; i<md.getColumnCount(); i++) {
		    		ds_out.addColumn(md.getColumnName(i+1), DataTypes.STRING, 2000);
		    	}
		    	ds_out.newRow();
		    	for(int i=0; i<md.getColumnCount(); i++) {
		    		ds_out.set(0, md.getColumnName(i+1), rs.getString(i+1));
		    	}
		    	dto.getOutdslist().add(ds_out);
		    }
		    
		} catch(Exception e) {
			//System.out.println("미니전광판 조회시 오류 " + e.toString());
			throw e;
			
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(Exception e){};
			try { if(ps !=null) ps.close(); ps=null; } catch(Exception e){};
			try { if(conn !=null) conn.close(); conn=null; } catch(Exception e){};			
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
		    	for(int i=0; i<md.getColumnCount(); i++) {
		    		sb1.append(md.getColumnName(i+1));
		    		sb1.append("^");
		    	}
		    	for(int i=0; i<md.getColumnCount(); i++) {
		    		sb2.append(rs.getString(i+1)==null?"0":rs.getString(i+1));
		    		sb2.append("^");
		    	}
		    }
		    ret = sb1.toString() + "|" + sb2.toString();
		    
		} catch(Exception e) {
			//System.out.println("미니전광판 조회시 오류 " + e.toString());
			throw e;
			
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(Exception e){};
			try { if(ps !=null) ps.close(); ps=null; } catch(Exception e){};
			try { if(conn !=null) conn.close(); conn=null; } catch(Exception e){};			
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
				} catch(Exception e) {}
				
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
			
		}catch (Exception e) {
			e.printStackTrace();
			secondExtlogger.debug(e);
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
			
		}catch (Exception e) {
			e.printStackTrace();
			secondExtlogger.debug(e);
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
						
		} catch (Exception e) {
			throw new Exception("오류=============================================" + e.toString());
	
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(Exception e){};
			try { if(ps !=null) ps.close(); ps=null; } catch(Exception e){};
			try { if(camsconn !=null) camsconn.close(); camsconn=null;} catch(Exception e){};	
		}
		
		secondExtlogger.debug("result==>" + result + "::::camsconn==>" + camsconn);
		
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
	public void MSGUserSyncSave(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		
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

						
		} catch (Exception e) {
			throw new Exception("오류=============================================" + e.toString());
	
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(Exception e){};
			try { if(ps !=null) ps.close(); ps=null; } catch(Exception e){};
			try { if(msgconn !=null) msgconn.close(); msgconn=null;} catch(Exception e){};	
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
			

	

}




