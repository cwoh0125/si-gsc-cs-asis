package kr.co.cs.daemon;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;///
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

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
 */

//20171201 서비스레벨 통계수치 수정 정주섭K
public class CTIApp extends XbaseAction {

    /*
     * 생성자
     * was기동시에 시작되게..
     * */
    public CTIApp() {
        try {
            startCTIConstruct();
        } catch(UnknownHostException e) {
        	errlogger.debug("WAS UnknownHostException :: " + e.getMessage());
        } catch(Exception e) {
        	errlogger.debug("WAS EXCEPTION :: " + e.getMessage());
        }
    }



    private final static Logger errlogger = LogManager.getLogger("process.etc");
    private final static Logger extlogger = LogManager.getLogger("process.ext");

    private CommonDao commonDao = null;

    public void setCommonDao(CommonDao commonDao) {
        this.commonDao = commonDao;
    }

    public void StartCTIDaemon(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        startCTI();
    }

    public void StopCTIDaemon(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("val", "N");
        paramMap.put("cd", "DM_CTIDMRUN");
        commonDao.update("Common.Set_DaemonState", paramMap);
    }

    public void CTIState(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        Iterator<Thread> ir = map.keySet().iterator();

        String alive = "N";
        while(ir.hasNext()) {
            Thread th = ir.next();
            if(th.getName().indexOf("CTIDaemonThread")>=0) {
                //System.out.println("Alive");
                alive = "Y";
            }
        }


        HttpPlatformResponse platformResponse = new HttpPlatformResponse(response, PlatformType.CONTENT_TYPE_XML, "UTF-8");
        VariableList outvlist = new VariableList();
        outvlist.add("CTIStatusMsg", alive);
        PlatformData output = new PlatformData();
        output.setVariableList(outvlist);

        platformResponse.setData(output);
        platformResponse.sendData();
    }

    private void startCTI() throws Exception {

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("val", "Y");
        paramMap.put("cd", "DM_CTIDMRUN");
        commonDao.update("Common.Set_DaemonState", paramMap);

        startCTI(true);
    }


    private void startCTIConstruct() throws Exception {
        startCTI(true);
    }

    private void startCTI(Boolean a) {

        try {
            String server_name = InetAddress.getLocalHost().getHostName().trim();
            if(!("___WKH-BT-N".equals(server_name) ||
                    "___wkh-bt-n".equals(server_name) ||
                    "___CRMDEV".equals(server_name) ||  //개발계 배치
                    Const.WAS1NAME.equals(server_name) ||
                    Const.WAS2NAME.equals(server_name)))
            {
                //System.out.println(new Date() + " : CTI DAEMON STOP ::::: STOPING AS SERVER IS NOT PROD ........");
                extlogger.debug("[CTI DAEMON STOP ::::: STOPING AS SERVER IS NOT PROD ........]");
                System.out.println("CTI DAEMON STOP ::::: STOPING AS SERVER IS NOT PROD .......");

                //extlogger.debug("[CTI DAEMON TEMP GO GO ::::: 테스트하기위해 잠시살림.........]");
                return;  //★TODO 다시살려야함
            }
        } catch (UnknownHostException e1) {
            return;
        }

        //System.out.println(new Date() + " : CTI DAEMON GO ::::: GO GO GO........");
        extlogger.debug("[CTI DAEMON GO ::::: GO GO GO........]");
        System.out.println("[CTI DAEMON GO ::::: GO GO GO........]");

        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        Iterator<Thread> ir = map.keySet().iterator();

        while(ir.hasNext()) {
            Thread th = ir.next();
            if(th.getName().indexOf("CTIDaemonThread")>=0) {
                //th.interrupt();
                return;
            }
        }

        Thread CTIthread = new Thread() {
            public void run() {

                HashMap<String, String> runmap = new HashMap<String, String>();
                runmap.put("cd", "DM_CTIDMRUN");
                String bb = null;
                int tloop = 0;
                int execloop = 0;
                while(true) {
                    try {
                        Thread.sleep(60000); // 1 분
                        tloop++;
                        execloop++;
                    } catch(InterruptedException e) {
                    	errlogger.debug("Thread EXCEPTION :: " + e.getMessage());
                    }

                    try {
                        if(tloop==5) { //디비체크는 5분에 한번타게끔..
                            tloop = 0;
                            bb = commonDao.selectString("Common.Get_DaemonRunning", runmap);
                            //System.out.println("=====DB check::"+bb);
                            if("N".equals(bb)) {
                                //System.out.println(new Date() + " : CTI DAEMON STOP ::::: STOP STOP STOP........");
                                extlogger.debug("[CTI DAEMON STOP ::::: STOP STOP STOP........]");
                                execloop = 0; //loop 초기화 (아래 안타게..)
                                break;
                            }
                        }
                    } catch(SQLException e) {
                    	errlogger.debug("DB Check SQLException :: " + e.getMessage());
                    } catch(Exception e) {
                    	errlogger.debug("DB Check EXCEPTION :: " + e.getMessage());
                    }

                    try {
                        if(execloop==30) { //30분에 한번 호출..
                            execloop = 0;
                            System.out.println("====Batch Start======");
                            CTIHistroy();
                        }

                    } catch(InterruptedException e) {
                    	errlogger.debug("loop InterruptedException :: " + e.getMessage());
                    } catch(Exception e) {
                    	errlogger.debug("loop EXCEPTION :: " + e.getMessage());
                    }

                }
            }
        };

        CTIthread.setName("CTIDaemonThread");
        CTIthread.setDaemon(true);
        CTIthread.start();
    }

    /*
     * 강제로 cti작업 시킨다.
     * */
    public void ForceWork(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CTIHistroy();
        //CTIFiveM();
        //CTIDay();
        //CTIWeek();
        //CTIMonth();
    }
    /*
     * CTIserver connect
     * */

    private void CTIHistroy() throws Exception {
        CTIFiveM();
        CTIDay();
        CTIWeek();
        CTIMonth();
        CTIQuarter();
        CTIYear();

        CTIFiveM_Que();
        CTIDay_Que();
        CTIWeek_Que();
        CTIMonth_Que();
        CTIQuarter_Que();
        CTIYear_Que();

    }

    private void CTIFiveM() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);
            
            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
            	conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            //STEP 1. 인바운드 TM
		    StringBuffer sb = new StringBuffer();
		    sb.append("  DECLARE @min_key INT ;     \n");
		    sb.append("  DECLARE @max_key INT ;     \n");
		    sb.append("   SELECT @max_key = max(StatisticsSet) FROM IAgentQueueStats;      \n");
		    sb.append("       \n");
		    sb.append("   SELECT @min_key = agent_tm_serno FROM GSC_STATS_SERNO;    \n");
		    sb.append("      \n");
		    sb.append("   SELECT @max_key max_key, @min_key min_key, convert(varchar(8),dIntervalStart,112) + Replace(Convert(varchar(5),dIntervalStart,108),':','') + 'KST' i_time_key,        \n");
		    sb.append("         max( statisticsset ),        \n");
	           //★TODO 교체 필요           
//	           sb.append("   (case when cName = 'GSCUser901' then 'NP650' when cName = 'GSCUser902' then 'NP648' else substring(cName,1,6)  end) i_usr_id,  \n");
	        sb.append("         cmpny_emp_cd i_usr_id,        \n");
		    sb.append("          SUM( tTalkACD ) i_inbnd_drtm,         \n");
		    sb.append("          SUM( tAlertedAcd ) i_tel_ring_drtm,         \n");
		    sb.append("          SUM( tHoldAcd )  i_susp_drtm,         \n");
		    sb.append("          SUM( tInternToInternCalls  ) i_ext_cur_drtm,        \n");
		    sb.append("          SUM( nAnsweredACD  ) i_inbnd_house,        \n");
		    sb.append("          SUM( nAlertedAcd ) i_tel_ring_ncnt,         \n");
		    sb.append("          SUM( nHoldAcd ) i_susp_ncnt,         \n");
		    sb.append("          SUM( nInternToInternCalls ) i_ext_cur_ncnt,         \n");
		    sb.append("          SUM( nTransferedACD ) i_house_swt_try_ncnt,         \n");
		    sb.append("          SUM( nEnteredAcd ) i_req_ncnt,        --추가        \n");
		    sb.append("          SUM( nAnsweredAcdSvcLvl1 ) i_sec10_in_cnnt_ncnt,         \n");
		    sb.append("          SUM( nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2 )   i_sec20_in_cnnt_ncnt,        \n");
		    sb.append("          SUM( nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2 + nAnsweredAcdSvcLvl3 ) i_sec30_in_cnnt_ncnt,        \n");
		    sb.append("          SUM( nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2 + nAnsweredAcdSvcLvl3 + nAnsweredAcdSvcLvl4 ) i_sec40_in_cnnt_ncnt,        \n");
		    sb.append("          SUM( nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2 + nAnsweredAcdSvcLvl3 + nAnsweredAcdSvcLvl4 + nAnsweredAcdSvcLvl5 ) i_sec50_in_cnnt_ncnt,        \n");
		    sb.append("          SUM( nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2 + nAnsweredAcdSvcLvl3 + nAnsweredAcdSvcLvl4 + nAnsweredAcdSvcLvl5 + nAnsweredAcdSvcLvl6 ) i_sec60_in_cnnt_ncnt,        \n");
		    sb.append("          0 i_consult_cur_drtm,        \n");
		    sb.append("          0 i_unkn_cur_drtm,        \n");
		    sb.append("          0 i_consult_cur_ncnt,        \n");
		    sb.append("          0 i_unkn_call_ncnt,        \n");
		    sb.append("          0 i_house_swt_rcv_ncnt,        \n");
		    sb.append("          0 i_meti_cur_ncnt,        \n");
		    sb.append("          0 i_fwd_callcnt        \n");
		    sb.append("   FROM IAgentQueueStats a  with(NOLOCK),  cams.dbo.tb_emp b with(NOLOCK)       \n");
		    sb.append("   WHERE a.cReportGroup not in('*','-') AND a.cHKey3 <> N'*' AND a.cHKey4 = N'Call'        \n");
		    sb.append("   AND dintervalStart between convert(varchar(8),getdate(),112) +' 00:00:00' and convert(varchar(8),getdate(),112) +' 23:59:59'        \n");
		    sb.append("   AND StatisticsSet > @min_key and StatisticsSet <= @max_key     \n");
		    sb.append("   AND a.cname = b.phone_user_id    \n");
		    sb.append("   AND cReportGroup <> 'FAQGroup'  \n");
		    sb.append("   GROUP BY dIntervalStart, cmpny_emp_cd  \n");
			ps = conn.prepareStatement(sb.toString());
		    ps.setQueryTimeout(0);		    
			rs = ps.executeQuery();
			
			HashMap<String, String> map = new HashMap<String, String>();
		    ResultSetMetaData md = null;
		    int iCnt = 0;
		    
           String max_key = "";
           String cur_key = ""; 
           boolean bSuccess = true;		    
		    while(rs.next()) {
		    	if(iCnt==0) {
		    		md = rs.getMetaData();
		    		//iCnt = 1;
		    	}
		    	
		    	if(md != null) {
		    		for(int i=0; i<md.getColumnCount(); i++) {
			    		map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
			    	}
		    	}
		    	
                if(iCnt==0)  
                {
                    max_key = map.get("max_key");
                    cur_key = map.get("min_key");
                }
		    	
		    	try {
		    		commonDao.update("CTI_STA.FiveMInfo_UPDATE", map);
                    cur_key = map.get("statisticsset");    		    		
		    	} catch(SQLException se) {
                    bSuccess = false;
		    		errlogger.debug("[CTI DAEMON STA ERROR : CTIFiveM STEP1. " + se.toString() + "][DATA : " + map + "][key:"+cur_key+"]");
		    	}
		    	iCnt++;
			}
			
            if(bSuccess) cur_key = max_key;
            
            // 최종읽었던키 업데이트
            if(!cur_key.equals(""))
            {
                String key_update_sql = "UPDATE GSC_STATS_SERNO SET agent_tm_serno = '"+cur_key+"'";
                ps = conn.prepareStatement(key_update_sql);
                ps.executeUpdate();
            }		    
            
            //STEP 2. 아웃바운드 TM
            sb = new StringBuffer();
            sb.append("SELECT YYYYMMDDHHMM+'KST' i_time_key , \n");
            //★TODO 교체 필요           
//            sb.append("   (case when i_usr_id = 'GSCUser901' then 'NP650' when i_usr_id = 'GSCUser902' then 'NP648' else substring(i_usr_id,1,6)  end) i_usr_id,  \n");
            sb.append("  cmpny_emp_cd i_usr_id,        \n");
            sb.append("  CONVERT(NUMERIC(18,0), (ROUND( sum(ISNULL(tDialing,0))/1000,0))) i_dialing_drtm,       \n");
            sb.append("  CONVERT(NUMERIC(18,0), (ROUND(SUM(i_otbnd_drtm)/1000,0))) i_otbnd_drtm,    \n");
            sb.append("  count(case when tConnected > 0 then 1 end) i_otbnd_house,      \n");
            sb.append("  count(1) i_dialing_ncnt        \n");
            sb.append("FROM    \n");
            sb.append("(    \n");
            sb.append("     SELECT yyyymmdd + HH+    \n");
            sb.append("             (CASE WHEN MM >= 0 AND MM < 15 THEN '00'     \n");
            sb.append("             WHEN MM >= 15 AND MM < 30 THEN '15'    \n");
            sb.append("             WHEN MM >= 30 AND MM < 45 THEN '30'    \n");
            sb.append("             WHEN MM >=45 AND MM < 60 THEN '45'    \n");
            sb.append("             END ) YYYYMMDDHHMM,    \n");
            sb.append("             i_usr_id,    \n");
            sb.append("             tDialing,    \n");
            sb.append("             i_otbnd_drtm,    \n");
            sb.append("             tConnected    \n");
            sb.append("      FROM    \n");
            sb.append("     (    \n");
            sb.append("               SELECT yyyymmdd, HHMM, SUBSTRING(HHMM,1,2) HH,     \n");
            sb.append("               CONVERT(INT,SUBSTRING(HHMM,3,2)) MM,    \n");
            sb.append("               i_usr_id, tDialing, i_otbnd_drtm, tConnected    \n");
            sb.append("               FROM    \n");
            sb.append("               (    \n");
            sb.append("               SELECT     \n");
            sb.append("                     CONVERT(varchar(8), DATEADD(SECOND, +32400, InitiatedDateTimeUTC),112) AS yyyymmdd,    \n");
            sb.append("                     REPLACE(CONVERT(varchar(6), DATEADD(SECOND, +32400, InitiatedDateTimeUTC),108),':','')  AS HHMM,    \n");
            sb.append("                     LastLocalUserId i_usr_id,     \n");
            sb.append("                     tDialing,    \n");
            sb.append("                     iSNULL(tConnected,0)+ISNULL(tConference,0)+ISNULL(tHeld,0) AS i_otbnd_drtm,        \n");
            sb.append("                     tConnected    \n");
            sb.append("               FROM InteractionSummary  with(NOLOCK)      \n");
            sb.append("               WHERE direction = 2 AND MediaType = 0        \n");
            sb.append("                  AND InitiatedDateTimeUTC BETWEEN  DATEADD(SECOND, -32400, CONVERT(DATETIME, convert(varchar(8),getdate(),112)+' 00:00:00', 108))         \n");
            sb.append("                  AND DATEADD(SECOND, -32400, CONVERT(DATETIME, convert(varchar(8),getdate(),112)+' 23:59:59', 108))        \n");
            sb.append("                  AND connectiontype = '1'        \n");
            sb.append("                  AND LastLocalUserId IS NOT NULL       \n");
            sb.append("          ) AA    \n");
            sb.append("     ) AAA    \n");
            sb.append(") AAAA, cams.dbo.tb_emp b with(NOLOCK)    \n");
            sb.append("WHERE AAAA.i_usr_id = b.phone_user_id   \n");
            sb.append("GROUP BY YYYYMMDDHHMM, cmpny_emp_cd    \n");
            
            //System.out.println("CTIApp.CTIFiveM() :  sb\n"+sb.toString());
            ps = conn.prepareStatement(sb.toString());
            ps.setQueryTimeout(0);          
            rs = ps.executeQuery();            
            
            map = new HashMap<String, String>();
            md = null;
            iCnt = 0;
            
            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                }
                
                if(md != null) {
                	 for(int i=0; i<md.getColumnCount(); i++) {
                         map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                     }
                }
               
                try {
                    commonDao.update("CTI_STA.FiveMInfo_OUTCALL_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIFiveM STEP2.  " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }            
            
            //STEP 3. 수신대기, 후처리
            sb = new StringBuffer();      
            sb.append("  SELECT  YYYYMMDDHHMM +'KST' i_time_key,    \n");
            //★TODO 교체 필요           
//            sb.append("   (case when i_usr_id = 'GSCUser901' then 'NP650' when i_usr_id = 'GSCUser902' then 'NP648' else substring(i_usr_id,1,6)  end) i_usr_id,  \n");
            sb.append("  cmpny_emp_cd i_usr_id,        \n");     
            sb.append("        SUM(CASE WHEN Statuskey = 'available' then StateDuration else 0 end) as i_tot_lrgexst_drtm,    \n");
            sb.append("        SUM(CASE WHEN Statuskey = 'follow up' then StateDuration else 0 end) as i_af_proc_drtm,    \n");
            sb.append("        COUNT(CASE WHEN Statuskey = 'available' then StateDuration  end) as i_tot_lrgexst_tcnt,    \n");
            sb.append("        COUNT(CASE WHEN Statuskey = 'available' then StateDuration  end) as i_af_proc_tcnt    \n");
            sb.append("  from    \n");
            sb.append("  (    \n");
            sb.append("     SELECT yyyymmdd + HH+      \n");
            sb.append("         (CASE WHEN MM >= 0 AND MM < 15 THEN '00'      \n");
            sb.append("               WHEN MM >= 15 AND MM < 30 THEN '15'     \n");
            sb.append("               WHEN MM >= 30 AND MM < 45 THEN '30'     \n");
            sb.append("               WHEN MM >= 45 AND MM < 60 THEN '45'     \n");
            sb.append("           END ) YYYYMMDDHHMM,     \n");
            sb.append("           userid i_usr_id, StatusKey, statusdatetime,  StateDuration    \n");
            sb.append("     FROM    \n");
            sb.append("     (    \n");
            sb.append("         SELECT userid, StatusKey, statusdatetime,  StateDuration,    \n");
            sb.append("              CONVERT(varchar(8), statusdatetime,112) AS yyyymmdd,    \n");
            sb.append("             SUBSTRING( REPLACE(CONVERT(varchar(6), statusdatetime,108),':',''),1,2)  AS HH,    \n");
            sb.append("             SUBSTRING( REPLACE(CONVERT(varchar(6), statusdatetime,108),':',''),3,2)  AS MM    \n");
            sb.append("         FROM AgentActivityLog    \n");
            sb.append("         WHERE statusDatetime BETWEEN CONVERT(VARCHAR(8),GETDATE(),112) +' 00:00:00' AND CONVERT(VARCHAR(8),GETDATE(),112) +' 23:59:59'      \n");
            sb.append("             AND StatusKey IN ( 'available','follow up')    \n");
            sb.append("     ) a    \n");
            sb.append("  ) aa, cams.dbo.tb_emp b with(NOLOCK)     \n");
            sb.append("WHERE aa.i_usr_id = b.phone_user_id   \n");            
            sb.append("  GROUP BY YYYYMMDDHHMM, cmpny_emp_cd      \n");            
            
            ps = conn.prepareStatement(sb.toString());
            ps.setQueryTimeout(0);          
            rs = ps.executeQuery();            
            
            map = new HashMap<String, String>();
            md = null;
            iCnt = 0;
            
            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                }
                
                if(md!= null) {
                	for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }
                
                try {
                    commonDao.update("CTI_STA.FiveMInfo_AGENT_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIFiveM STEP3.  " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }               
            
            
            
			//System.out.println(new Date() + "CTIFiveM RS,RECORD COUNT " + iCnt);
		    extlogger.debug("[CTI STA SAVE ::::: CTIFiveM cti sta job good........]");
			
		} catch (SQLException e) {
			errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIFiveM " + e.getMessage() + "]");

		} catch (Exception e) {
			errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIFiveM " + e.getMessage() + "]");
		} finally {
			try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIFiveM rs EXCEPTION :: " + e.getMessage());}
			try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIFiveM ps EXCEPTION :: " + e.getMessage());}
			try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIFiveM conn EXCEPTION :: " + e.getMessage());}	
		}	
	} 


    // 오늘의 집계
    private String commonQuery(String gubun)
    {

        String i_time_key = "";
        if(gubun.equals("TM"))
        {
            //i_time_key = "convert(varchar(8),dIntervalStart,112) + Replace(Convert(varchar(5),dIntervalStart,108),':','') + 'KST' i_time_key, ";
        }
        else if(gubun.equals("DT"))
        {
            i_time_key = "A.i_time_key,  ";
        }
        else if(gubun.equals("YW"))
        {
            i_time_key = "SUBSTRING(A.i_time_key,1,4) +  CONVERT(VARCHAR(2),DATEPART(WEEK, A.i_time_key)) i_time_key,    ";
        }
        else if(gubun.equals("YM"))
        {
            i_time_key = "SUBSTRING(A.i_time_key,1,6) i_time_key,    ";
        }
        else if(gubun.equals("QT"))
        {
            i_time_key = "SUBSTRING(A.i_time_key,1,4) +'Q'+CONVERT(VARCHAR(1),DATEPART(QUARTER, A.i_time_key)) i_time_key, ";
        }
        else if(gubun.equals("YR"))
        {
            i_time_key = "SUBSTRING(A.i_time_key,1,4) i_time_key,    ";
        }


        StringBuffer sb = new StringBuffer();
        //★TODO 교체 필요
        //sb.append("  SELECT (case when A.i_usr_id = 'GSCUser901' then 'NP650' when A.i_usr_id = 'GSCUser902' then 'NP648' else substring(A.i_usr_id,1,6)  end) i_usr_id,  \n");
        sb.append("    SELECT cmpny_emp_cd i_usr_id,         \n");
        sb.append("       "+i_time_key+"           \n");
        sb.append("       isnull(i_inbnd_drtm,0) i_inbnd_drtm, isnull(i_tel_ring_drtm,0) i_tel_ring_drtm, isnull(i_susp_drtm,0) i_susp_drtm,            \n");
        sb.append("       isnull(i_ext_cur_drtm,0) i_ext_cur_drtm, isnull(i_inbnd_house,0) i_inbnd_house, isnull(i_tel_ring_ncnt,0) i_tel_ring_ncnt, isnull(i_susp_ncnt,0) i_susp_ncnt, isnull(i_ext_cur_ncnt,0) i_ext_cur_ncnt,        \n");
        sb.append("       isnull(i_house_swt_try_ncnt,0) i_house_swt_try_ncnt, isnull(i_req_ncnt,0) i_req_ncnt, isnull(i_sec10_in_cnnt_ncnt,0) i_sec10_in_cnnt_ncnt, isnull(i_sec20_in_cnnt_ncnt,0) i_sec20_in_cnnt_ncnt, isnull(i_sec30_in_cnnt_ncnt,0) i_sec30_in_cnnt_ncnt,        \n");
        sb.append("       isnull(i_sec40_in_cnnt_ncnt, 0) i_sec40_in_cnnt_ncnt, isnull(i_sec50_in_cnnt_ncnt,0) i_sec50_in_cnnt_ncnt, isnull(i_sec60_in_cnnt_ncnt,0) i_sec60_in_cnnt_ncnt, isnull(i_consult_cur_drtm,0) i_consult_cur_drtm,        \n");
        sb.append("       isnull(i_unkn_cur_drtm,0) i_unkn_cur_drtm, isnull(i_consult_cur_ncnt,0) i_consult_cur_ncnt, isnull(i_unkn_call_ncnt,0) i_unkn_call_ncnt, isnull(i_house_swt_rcv_ncnt,0) i_house_swt_rcv_ncnt, isnull(i_meti_cur_ncnt,0) i_meti_cur_ncnt,        \n");
        sb.append("       isnull(i_fwd_callcnt,0) i_fwd_callcnt,        \n");
        sb.append("       isnull(i_dialing_drtm,0) i_dialing_drtm, isnull(i_otbnd_drtm,0) i_otbnd_drtm, isnull(i_otbnd_house,0) i_otbnd_house, isnull(i_dialing_ncnt,0) i_dialing_ncnt,        \n");
        sb.append("       isnull(i_tot_lrgexst_tcnt,0) i_tot_lrgexst_tcnt, isnull(i_tot_lrgexst_drtm,0) i_tot_lrgexst_drtm, isnull(i_af_proc_tcnt,0) i_af_proc_tcnt, isnull(i_af_proc_drtm,0) i_af_proc_drtm, isnull(i_cle_seat_tcnt_meal,0) i_cle_seat_tcnt_meal,        \n");
        sb.append("       isnull(i_cle_seat_drtm_meal,0) i_cle_seat_drtm_meal, isnull(i_cle_seat_tcnt_edu,0) i_cle_seat_tcnt_edu, isnull(i_cle_seat_drtm_edu,0) i_cle_seat_drtm_edu, isnull(i_cle_seat_tcnt_eml,0) i_cle_seat_tcnt_eml, isnull(i_cle_seat_drtm_eml,0) i_cle_seat_drtm_eml,        \n");
        sb.append("       isnull(i_cle_seat_tcnt_dsft,0) i_cle_seat_tcnt_dsft, isnull(i_cle_seat_drtm_dsft,0) i_cle_seat_drtm_dsft, isnull(i_cle_seat_tcnt_coch,0) i_cle_seat_tcnt_coch, isnull(i_cle_seat_drtm_coch,0) i_cle_seat_drtm_coch, isnull(i_cle_seat_tcnt_rest,0) i_cle_seat_tcnt_rest,        \n");
        sb.append("       isnull(i_cle_seat_drtm_rest,0) i_cle_seat_drtm_rest, isnull(i_wkg_drtm,0) i_wkg_drtm, isnull(i_cle_seat_tcnt2_meal,0) i_cle_seat_tcnt2_meal, isnull(i_cle_seat_drtm2_meal,0) i_cle_seat_drtm2_meal, isnull(i_cle_seat_tcnt2_edu,0) i_cle_seat_tcnt2_edu,        \n");
        sb.append("       isnull(i_cle_seat_drtm2_edu,0) i_cle_seat_drtm2_edu,  isnull(i_cle_seat_tcnt2_eml,0) i_cle_seat_tcnt2_eml,  isnull(i_cle_seat_drtm2_eml,0) i_cle_seat_drtm2_eml,  isnull(i_cle_seat_tcnt2_dsft,0) i_cle_seat_tcnt2_dsft,  isnull(i_cle_seat_drtm2_dsft,0) i_cle_seat_drtm2_dsft,        \n");
        sb.append("       isnull(i_cle_seat_tcnt2_coch,0) i_cle_seat_tcnt2_coch, isnull(i_cle_seat_drtm2_coch,0) i_cle_seat_drtm2_coch, isnull(i_cle_seat_tcnt2_rest,0) i_cle_seat_tcnt2_rest, isnull(i_cle_seat_drtm2_rest,0) i_cle_seat_drtm2_rest,        \n");
        sb.append("       isnull(( i_inbnd_drtm + i_otbnd_drtm ),0) i_tot_cur_drtm,        \n");
        sb.append("       isnull(( i_inbnd_house + i_otbnd_house) ,0) i_tot_cur_ncnt,        \n");
        sb.append("       isnull(( i_cle_seat_drtm_rest + i_cle_seat_drtm_meal + i_cle_seat_drtm_edu + i_cle_seat_drtm_coch + i_cle_seat_drtm_dsft + i_cle_seat_drtm_eml ),0) i_cle_seat_drtm,        \n");
        sb.append("       isnull(( i_cle_seat_tcnt_rest + i_cle_seat_tcnt_meal + i_cle_seat_tcnt_edu + i_cle_seat_tcnt_coch + i_cle_seat_tcnt_dsft + i_cle_seat_tcnt_eml ),0) i_cle_seat_tcnt,    \n");
        sb.append("      0 i_cle_seat_drtm2,    \n");
        sb.append("      0 i_cle_seat_tcnt2,    \n");
        sb.append("      'CIC'  i_lst_corc_id,    \n");
        sb.append("      CONVERT(CHAR(8),GETDATE(),112)+REPLACE(CONVERT(CHAR(8),GETDATE(),108),':','')  i_lst_corc_dtm,         \n");
        sb.append("      'CIC' i_reg_id,    \n");
        sb.append("      CONVERT(CHAR(8),GETDATE(),112)+REPLACE(CONVERT(CHAR(8),GETDATE(),108),':','') I_reg_dtm    \n");
        sb.append("       FROM        \n");
        sb.append("       (        \n");
        sb.append("          SELECT DISTINCT userid i_usr_id, convert(varchar(8),getdate(),112) i_time_key        \n");
        sb.append("          FROM UserWorkgroups with(NOLOCK)        \n");
        sb.append("          WHERE userid not in ('mqadmin','Operator')        \n");
        sb.append("       ) A        \n");
        sb.append("       LEFT OUTER JOIN        \n");
        sb.append("       (        \n");
        sb.append("       -- 인콜        \n");
        sb.append("          SELECT convert(varchar(8),dintervalStart, 112) i_time_key,        \n");
        sb.append("                  cName i_usr_id,        \n");
        sb.append("                  sum(tTalkACD) i_inbnd_drtm,         \n");
        sb.append("                  sum(tAlertedAcd) i_tel_ring_drtm,         \n");
        sb.append("                  sum(tHoldAcd) i_susp_drtm,         \n");
        sb.append("                  sum(tInternToInternCalls) i_ext_cur_drtm,        \n");
        sb.append("                  sum(nAnsweredACD) i_inbnd_house,        \n");
        sb.append("                  sum(nAlertedAcd) i_tel_ring_ncnt,         \n");
        sb.append("                  sum(nHoldAcd) i_susp_ncnt,         \n");
        sb.append("                  sum(nInternToInternCalls) i_ext_cur_ncnt,         \n");
        sb.append("                  sum(nTransferedACD) i_house_swt_try_ncnt,         \n");
        sb.append("                  sum(nEnteredAcd) i_req_ncnt,        --추가        \n");
        sb.append("                  sum(nAnsweredAcdSvcLvl1) i_sec10_in_cnnt_ncnt,         \n");
        sb.append("                  sum(nAnsweredAcdSvcLvl1)  + sum(nAnsweredAcdSvcLvl2) i_sec20_in_cnnt_ncnt,        \n");
        sb.append("                  sum(nAnsweredAcdSvcLvl1)  + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) i_sec30_in_cnnt_ncnt,        \n");
        sb.append("                  sum(nAnsweredAcdSvcLvl1)  + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) + sum(nAnsweredAcdSvcLvl4) i_sec40_in_cnnt_ncnt,        \n");
        sb.append("                  sum(nAnsweredAcdSvcLvl1)  + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) + sum(nAnsweredAcdSvcLvl4) + sum(nAnsweredAcdSvcLvl5) i_sec50_in_cnnt_ncnt,        \n");
        sb.append("                  sum(nAnsweredAcdSvcLvl1)  + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) + sum(nAnsweredAcdSvcLvl4) + sum(nAnsweredAcdSvcLvl5) + sum(nAnsweredAcdSvcLvl6) i_sec60_in_cnnt_ncnt,        \n");
        sb.append("                  0 i_consult_cur_drtm,        \n");
        sb.append("                  0 i_unkn_cur_drtm,        \n");
        sb.append("                  0 i_consult_cur_ncnt,        \n");
        sb.append("                  0 i_unkn_call_ncnt,        \n");
        sb.append("                  0 i_house_swt_rcv_ncnt,        \n");
        sb.append("                  0 i_meti_cur_ncnt,        \n");
        sb.append("                  0 i_fwd_callcnt        \n");
        sb.append("          FROM IAgentQueueStats a  with(NOLOCK)        \n");
        sb.append("          WHERE a.cReportGroup not in('*','-') AND a.cHKey3 <> N'*' AND a.cHKey4 = N'Call'        \n");
        sb.append("              and dintervalStart between convert(varchar(8),getdate(),112) +' 00:00:00' and convert(varchar(8),getdate(),112) +' 23:59:59'        \n");
        sb.append("              and cReportGroup <> 'FAQGroup'      \n");
        sb.append("          GROUP BY convert(varchar(8),dintervalStart, 112), cName        \n");
        sb.append("       ) B on A.i_time_key = B.i_time_key  and A.i_usr_id = B.i_usr_id        \n");
        sb.append("       LEFT OUTER JOIN        \n");
        sb.append("       (        \n");
        sb.append("          -- 아웃콜        \n");
        sb.append("          SELECT CONVERT(varchar(8), DATEADD(SECOND, +32400, InitiatedDateTimeUTC),112) AS i_time_key,        \n");
        sb.append("              LastLocalUserId i_usr_id,        \n");
        sb.append("              CONVERT(NUMERIC(18,0), (ROUND( sum(ISNULL(tDialing,0))/1000,0))) i_dialing_drtm,        \n");
        sb.append("              CONVERT(NUMERIC(18,0), (ROUND(SUM(ISNULL(tConnected,0)+ISNULL(tConference,0)+ISNULL(tHeld,0))/1000,0))) AS i_otbnd_drtm,        \n");
        sb.append("              count(case when tConnected > 0 then 1 end) i_otbnd_house,        \n");
        sb.append("              count(1) i_dialing_ncnt        \n");
        sb.append("          FROM InteractionSummary  with(NOLOCK)        \n");
        sb.append("          WHERE direction = 2 AND MediaType = 0        \n");
        sb.append("              AND InitiatedDateTimeUTC BETWEEN  DATEADD(SECOND, -32400, CONVERT(DATETIME, convert(varchar(8),getdate(),112)+' 00:00:00', 108))         \n");
        sb.append("              AND DATEADD(SECOND, -32400, CONVERT(DATETIME, convert(varchar(8),getdate(),112)+' 23:59:59', 108))        \n");
        sb.append("              AND connectiontype = '1'        \n");
        sb.append("           GROUP BY CONVERT(VARCHAR(8), DATEADD(SECOND, +32400, InitiatedDateTimeUTC),112), LastLocalUserId        \n");
        sb.append("       ) C on A.i_time_key = C.i_time_key  and A.i_usr_id = C.i_usr_id        \n");
        sb.append("       LEFT OUTER JOIN        \n");
        sb.append("       (        \n");
        sb.append("          --상태        \n");
        sb.append("          SELECT        \n");
        sb.append("              convert( varchar(8), StatusDateTime, 112) i_time_key        \n");
        sb.append("              ,a.UserId i_usr_id        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Available' AND a.StateDuration > 0 THEN 1 ELSE 0 END) AS i_tot_lrgexst_tcnt        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Available' THEN a.StateDuration ELSE 0 END) AS i_tot_lrgexst_drtm        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Follow Up' AND a.StateDuration > 0 THEN 1 ELSE 0 END) AS i_af_proc_tcnt        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Follow Up' THEN a.StateDuration ELSE 0 END) AS i_af_proc_drtm        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'At Lunch' AND a.StateDuration > 0 THEN 1 ELSE 0 END) AS i_cle_seat_tcnt_meal        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'At Lunch' THEN a.StateDuration ELSE 0 END) AS i_cle_seat_drtm_meal        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'At a training session' AND a.StateDuration > 0 THEN 1 ELSE 0 END) AS i_cle_seat_tcnt_edu        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'At a training session' THEN a.StateDuration ELSE 0 END) AS i_cle_seat_drtm_edu        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Mail' AND a.StateDuration > 0 THEN 1 ELSE 0 END) AS i_cle_seat_tcnt_eml        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Mail' THEN a.StateDuration ELSE 0 END) AS i_cle_seat_drtm_eml        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Complaint' AND a.StateDuration > 0 THEN 1 ELSE 0 END) AS i_cle_seat_tcnt_dsft        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Complaint' THEN a.StateDuration ELSE 0 END) AS i_cle_seat_drtm_dsft        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Coach' AND a.StateDuration > 0 THEN 1 ELSE 0 END) AS i_cle_seat_tcnt_coch        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'Coach' THEN a.StateDuration ELSE 0 END) AS i_cle_seat_drtm_coch        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'At Rest' AND a.StateDuration > 0 THEN 1 ELSE 0 END) AS i_cle_seat_tcnt_rest        \n");
        sb.append("              ,SUM(CASE WHEN a.StatusKey = 'At Rest' THEN a.StateDuration ELSE 0 END) AS i_cle_seat_drtm_rest        \n");
        sb.append("              ,SUM(StateDuration) i_wkg_drtm        \n");
        sb.append("              , 0 i_cle_seat_tcnt2_meal        \n");
        sb.append("              , 0 i_cle_seat_drtm2_meal        \n");
        sb.append("              , 0 i_cle_seat_tcnt2_edu        \n");
        sb.append("              , 0 i_cle_seat_drtm2_edu        \n");
        sb.append("              , 0 i_cle_seat_tcnt2_eml        \n");
        sb.append("              , 0 i_cle_seat_drtm2_eml        \n");
        sb.append("              , 0 i_cle_seat_tcnt2_dsft        \n");
        sb.append("              , 0 i_cle_seat_drtm2_dsft        \n");
        sb.append("              , 0 i_cle_seat_tcnt2_coch        \n");
        sb.append("              , 0 i_cle_seat_drtm2_coch        \n");
        sb.append("              , 0 i_cle_seat_tcnt2_rest        \n");
        sb.append("              , 0 i_cle_seat_drtm2_rest        \n");
        sb.append("          FROM AgentActivityLog a  with(NOLOCK)        \n");
        sb.append("          WHERE StatusDateTime BETWEEN CONVERT(VARCHAR(8),GETDATE(),112) +' 00:00:00' AND CONVERT(VARCHAR(8),GETDATE(),112) +' 23:59:59'        \n");
        sb.append("          GROUP BY CONVERT( VARCHAR(8), StatusDateTime, 112),userid        \n");
        sb.append("       ) D on A.i_time_key = D.i_time_key  and A.i_usr_id = D.i_usr_id        \n");
        sb.append("       , cams.dbo.tb_emp bb with(NOLOCK)          \n");
        sb.append("       WHERE A.i_usr_id = bb.phone_user_id          \n");
        return sb.toString();
    }

    // 최초원본 또는 어제까지의 누적분을 가져옴
    // @param gubun 주기별
    // @param gubun2  최초원본(ORG) | 누적분(SUM)
    private String commonAddQuery(String gubun, String gubun2)
    {

        String where = "";
        if(gubun.equals("TM"))
        {
            //i_time_key = "convert(varchar(8),dIntervalStart,112) + Replace(Convert(varchar(5),dIntervalStart,108),':','') + 'KST' i_time_key, ";
        }
        else if(gubun.equals("YW"))
        {
            where = " kind = 'YW' AND time_key = CONVERT(VARCHAR(4),GETDATE(),112) +  CONVERT(VARCHAR(2),DATEPART(WEEK, CONVERT(VARCHAR(8),GETDATE(),112))) \n";
        }
        else if(gubun.equals("YM"))
        {
            where = " kind = 'YM' AND time_key = CONVERT(VARCHAR(6),GETDATE(),112)  \n";
        }
        else if(gubun.equals("QT"))
        {
            where = " kind = 'QT' AND time_key = CONVERT(VARCHAR(4),GETDATE(),112) +  'Q'+CONVERT(VARCHAR(1),DATEPART(QUARTER, GETDATE()))  \n";
        }
        else if(gubun.equals("YR"))
        {
            where = " kind = 'YR' AND time_key = CONVERT(VARCHAR(4),GETDATE(),112)  \n";
        }


        StringBuffer sb = new StringBuffer();
        //★TODO 교체 필요
//        sb.append("  SELECT (case when usr_id = 'GSCUser901' then 'NP650' when usr_id = 'GSCUser902' then 'NP648' else substring(usr_id,1,6)  end) i_usr_id,  \n");
        sb.append("  SELECT usr_id i_usr_id,     \n");
        sb.append("     time_key i_time_key,     \n");
        sb.append("     inbnd_drtm i_inbnd_drtm,    \n");
        sb.append("     tel_ring_drtm i_tel_ring_drtm,    \n");
        sb.append("     susp_drtm i_susp_drtm,         \n");
        sb.append("       ext_cur_drtm i_ext_cur_drtm, inbnd_house i_inbnd_house, tel_ring_ncnt i_tel_ring_ncnt, susp_ncnt i_susp_ncnt, ext_cur_ncnt i_ext_cur_ncnt,        \n");
        sb.append("       house_swt_try_ncnt i_house_swt_try_ncnt, req_ncnt i_req_ncnt, sec10_in_cnnt_ncnt i_sec10_in_cnnt_ncnt, sec20_in_cnnt_ncnt i_sec20_in_cnnt_ncnt, sec30_in_cnnt_ncnt i_sec30_in_cnnt_ncnt,        \n");
        sb.append("       sec40_in_cnnt_ncnt i_sec40_in_cnnt_ncnt, sec50_in_cnnt_ncnt i_sec50_in_cnnt_ncnt, sec60_in_cnnt_ncnt i_sec60_in_cnnt_ncnt, consult_cur_drtm i_consult_cur_drtm,        \n");
        sb.append("       unkn_cur_drtm i_unkn_cur_drtm, consult_cur_ncnt i_consult_cur_ncnt, unkn_call_ncnt i_unkn_call_ncnt, house_swt_rcv_ncnt i_house_swt_rcv_ncnt, meti_cur_ncnt i_meti_cur_ncnt,        \n");
        sb.append("       fwd_callcnt i_fwd_callcnt,        \n");
        sb.append("       dialing_drtm i_dialing_drtm, otbnd_drtm i_otbnd_drtm, otbnd_house i_otbnd_house, dialing_ncnt i_dialing_ncnt,        \n");
        sb.append("       tot_lrgexst_tcnt i_tot_lrgexst_tcnt, tot_lrgexst_drtm i_tot_lrgexst_drtm, af_proc_tcnt i_af_proc_tcnt, af_proc_drtm i_af_proc_drtm, cle_seat_tcnt_meal i_cle_seat_tcnt_meal,        \n");
        sb.append("       cle_seat_drtm_meal i_cle_seat_drtm_meal, cle_seat_tcnt_edu i_cle_seat_tcnt_edu, cle_seat_drtm_edu i_cle_seat_drtm_edu, cle_seat_tcnt_eml i_cle_seat_tcnt_eml, cle_seat_drtm_eml i_cle_seat_drtm_eml,        \n");
        sb.append("       cle_seat_tcnt_dsft i_cle_seat_tcnt_dsft, cle_seat_drtm_dsft i_cle_seat_drtm_dsft, cle_seat_tcnt_coch i_cle_seat_tcnt_coch, cle_seat_drtm_coch i_cle_seat_drtm_coch, cle_seat_tcnt_rest i_cle_seat_tcnt_rest,        \n");
        sb.append("       cle_seat_drtm_rest i_cle_seat_drtm_rest, wkg_drtm i_wkg_drtm, cle_seat_tcnt2_meal i_cle_seat_tcnt2_meal, cle_seat_drtm2_meal i_cle_seat_drtm2_meal, cle_seat_tcnt2_edu i_cle_seat_tcnt2_edu,        \n");
        sb.append("       cle_seat_drtm2_edu i_cle_seat_drtm2_edu,  cle_seat_tcnt2_eml i_cle_seat_tcnt2_eml,  cle_seat_drtm2_eml i_cle_seat_drtm2_eml,  cle_seat_tcnt2_dsft i_cle_seat_tcnt2_dsft, cle_seat_drtm2_dsft i_cle_seat_drtm2_dsft,        \n");
        sb.append("       cle_seat_tcnt2_coch i_cle_seat_tcnt2_coch, cle_seat_drtm2_coch i_cle_seat_drtm2_coch, cle_seat_tcnt2_rest i_cle_seat_tcnt2_rest, cle_seat_drtm2_rest i_cle_seat_drtm2_rest,        \n");
        sb.append("       tot_cur_drtm i_tot_cur_drtm,        \n");
        sb.append("       tot_cur_ncnt i_tot_cur_ncnt,        \n");
        sb.append("       cle_seat_drtm i_cle_seat_drtm,        \n");
        sb.append("       cle_seat_tcnt i_cle_seat_tcnt,    \n");
        sb.append("       cle_seat_drtm2 i_cle_seat_drtm2,    \n");
        sb.append("       cle_seat_tcnt2  i_cle_seat_tcnt2,    \n");
        sb.append("       lst_corc_id i_lst_corc_id,    \n");
        sb.append("       lst_corc_dtm i_lst_corc_dtm,          \n");
        sb.append("       reg_id i_reg_id,    \n");
        sb.append("       reg_dtm I_reg_dtm    \n");
        sb.append("  FROM GSC_BT_CTI_RPT_"+gubun2+"    \n");
        sb.append("  WHERE    \n");
        sb.append("        "+where);
        return sb.toString();
    }

    // 최종적으로 GROUP BY 할 최종 SELECT 절을 가져옴
    private String commonAddHeaderQuery()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("       i_usr_id,     \n");
        sb.append("       i_time_key,     \n");
        sb.append("       SUM(i_inbnd_drtm) i_inbnd_drtm,    \n");
        sb.append("       SUM(i_tel_ring_drtm) i_tel_ring_drtm,    \n");
        sb.append("       SUM(i_susp_drtm) i_susp_drtm,         \n");
        sb.append("       SUM(i_ext_cur_drtm) i_ext_cur_drtm, SUM(i_inbnd_house) i_inbnd_house, SUM(i_tel_ring_ncnt) i_tel_ring_ncnt, SUM(i_susp_ncnt) i_susp_ncnt, SUM(i_ext_cur_ncnt) i_ext_cur_ncnt,        \n");
        sb.append("       SUM(i_house_swt_try_ncnt) i_house_swt_try_ncnt, SUM(i_req_ncnt) i_req_ncnt, SUM(i_sec10_in_cnnt_ncnt) i_sec10_in_cnnt_ncnt, SUM(i_sec20_in_cnnt_ncnt) i_sec20_in_cnnt_ncnt, SUM(i_sec30_in_cnnt_ncnt) i_sec30_in_cnnt_ncnt,        \n");
        sb.append("       SUM(i_sec40_in_cnnt_ncnt) i_sec40_in_cnnt_ncnt, SUM(i_sec50_in_cnnt_ncnt) i_sec50_in_cnnt_ncnt, SUM(i_sec60_in_cnnt_ncnt) i_sec60_in_cnnt_ncnt, SUM(i_consult_cur_drtm) i_consult_cur_drtm,        \n");
        sb.append("       SUM(i_unkn_cur_drtm) i_unkn_cur_drtm, SUM(i_consult_cur_ncnt) i_consult_cur_ncnt, SUM(i_unkn_call_ncnt) i_unkn_call_ncnt, SUM(i_house_swt_rcv_ncnt) i_house_swt_rcv_ncnt, SUM(i_meti_cur_ncnt) i_meti_cur_ncnt,        \n");
        sb.append("       SUM(i_fwd_callcnt) i_fwd_callcnt,        \n");
        sb.append("       SUM(i_dialing_drtm) i_dialing_drtm, SUM(i_otbnd_drtm) i_otbnd_drtm, SUM(i_otbnd_house) i_otbnd_house, SUM(i_dialing_ncnt) i_dialing_ncnt,        \n");
        sb.append("       SUM(i_tot_lrgexst_tcnt) i_tot_lrgexst_tcnt, SUM(i_tot_lrgexst_drtm) i_tot_lrgexst_drtm, SUM(i_af_proc_tcnt) i_af_proc_tcnt, SUM(i_af_proc_drtm) i_af_proc_drtm, SUM(i_cle_seat_tcnt_meal) i_cle_seat_tcnt_meal,        \n");
        sb.append("       SUM(i_cle_seat_drtm_meal) i_cle_seat_drtm_meal, SUM(i_cle_seat_tcnt_edu) i_cle_seat_tcnt_edu, SUM(i_cle_seat_drtm_edu) i_cle_seat_drtm_edu, SUM(i_cle_seat_tcnt_eml) i_cle_seat_tcnt_eml, SUM(i_cle_seat_drtm_eml) i_cle_seat_drtm_eml,        \n");
        sb.append("       SUM(i_cle_seat_tcnt_dsft) i_cle_seat_tcnt_dsft, SUM(i_cle_seat_drtm_dsft) i_cle_seat_drtm_dsft, SUM(i_cle_seat_tcnt_coch) i_cle_seat_tcnt_coch, SUM(i_cle_seat_drtm_coch) i_cle_seat_drtm_coch, SUM(i_cle_seat_tcnt_rest) i_cle_seat_tcnt_rest,        \n");
        sb.append("       SUM(i_cle_seat_drtm_rest) i_cle_seat_drtm_rest, SUM(i_wkg_drtm) i_wkg_drtm, SUM(i_cle_seat_tcnt2_meal) i_cle_seat_tcnt2_meal, SUM(i_cle_seat_drtm2_meal) i_cle_seat_drtm2_meal, SUM(i_cle_seat_tcnt2_edu) i_cle_seat_tcnt2_edu,        \n");
        sb.append("       SUM(i_cle_seat_drtm2_edu) i_cle_seat_drtm2_edu,  SUM(i_cle_seat_tcnt2_eml) i_cle_seat_tcnt2_eml,  SUM(i_cle_seat_drtm2_eml) i_cle_seat_drtm2_eml,  SUM(i_cle_seat_tcnt2_dsft) i_cle_seat_tcnt2_dsft,     \n");
        sb.append("       SUM(i_cle_seat_tcnt2_coch) i_cle_seat_tcnt2_coch, SUM(i_cle_seat_drtm2_coch) i_cle_seat_drtm2_coch, SUM(i_cle_seat_tcnt2_rest) i_cle_seat_tcnt2_rest, SUM(i_cle_seat_drtm2_rest) i_cle_seat_drtm2_rest,     \n");
        sb.append("       SUM(i_tot_cur_drtm) i_tot_cur_drtm,        \n");
        sb.append("       SUM(i_tot_cur_ncnt) i_tot_cur_ncnt,        \n");
        sb.append("       SUM(i_cle_seat_drtm) i_cle_seat_drtm,        \n");
        sb.append("       SUM(i_cle_seat_tcnt) i_cle_seat_tcnt,    \n");
        sb.append("       SUM(i_cle_seat_drtm2) i_cle_seat_drtm2,    \n");
        sb.append("       SUM(i_cle_seat_tcnt2) i_cle_seat_tcnt2,    \n");
        sb.append("       MAX(i_lst_corc_id) i_lst_corc_id,    \n");
        sb.append("       MAX(i_lst_corc_dtm) i_lst_corc_dtm,           \n");
        sb.append("       MAX(i_reg_id) i_reg_id,    \n");
        sb.append("       MAX(I_reg_dtm) I_reg_dtm    \n");
        return sb.toString();
    }




    private void CTIDay() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String sql = commonQuery("DT");


            // 확인 System.out.println("CTIApp.CTIDay() :   sql \n"+sql );
            ps = conn.prepareStatement( sql );
            ps.setQueryTimeout(20);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if (md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }

                try {
                    commonDao.update("CTI_STA.DayInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIDay " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            //System.out.println(new Date() + "CTIDay RS,RECORD COUNT " + iCnt);
            extlogger.debug("[CTI STA SAVE ::::: CTIDay cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIDay " + e.getMessage() + "]");
        } catch (Exception e) {
        	errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIDay " + e.getMessage() + "]");
        } finally {
        	if (conn != null) { try { conn.close(); } catch (SQLException e2) { errlogger.debug(e2.getMessage()); } catch (Exception e) { errlogger.debug(e.getMessage()); }}
        	if (ps != null) {try {ps.close();} catch (SQLException e2) {errlogger.debug(e2.getMessage());} catch (Exception e) {errlogger.debug(e.getMessage());}}
        	if (rs != null) {try {rs.close();} catch (SQLException e2) {errlogger.debug(e2.getMessage());} catch (Exception e) {errlogger.debug(e.getMessage());}}
        }

    }

    private void CTIWeek() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String cur_sql = commonQuery("YW");
            String org_sql = commonAddQuery("YW","ORG");
            String sum_sql = commonAddQuery("YW","SUM");
            String header_sql = commonAddHeaderQuery();

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT \n");
            sb.append(       header_sql );
            sb.append("FROM ( \n" );
            sb.append(       cur_sql);
            sb.append("UNION ALL \n");
            sb.append(       org_sql);
            sb.append("UNION ALL \n");
            sb.append(       sum_sql);
            sb.append(") AA \n" );
            sb.append("GROUP BY i_time_key, i_usr_id \n" );

            //확인 System.out.println("CTIApp.CTIWeek() :  sql \n"+sb.toString() );

            ps = conn.prepareStatement( sb.toString() );
            ps.setQueryTimeout(20);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if (md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }

                try {
                    commonDao.update("CTI_STA.WeekInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIWeek " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            //System.out.println(new Date() + "CTIWeek RS,RECORD COUNT " + iCnt);

            extlogger.debug("[CTI STA SAVE ::::: CTIWeek cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIWeek " + e.getMessage() + "]");

        } catch (Exception e) {
            errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIWeek " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIWeek rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIWeek ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIWeek conn EXCEPTION ::" + e.getMessage());}
        }
    }

    private void CTIMonth() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String cur_sql = commonQuery("YM");
            String org_sql = commonAddQuery("YM","ORG");
            String sum_sql = commonAddQuery("YM","SUM");
            String header_sql = commonAddHeaderQuery();

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT \n");
            sb.append(       header_sql );
            sb.append("FROM ( \n" );
            sb.append(       cur_sql);
            sb.append("UNION ALL \n");
            sb.append(       org_sql);
            sb.append("UNION ALL \n");
            sb.append(       sum_sql);
            sb.append(") AA \n" );
            sb.append("GROUP BY i_time_key, i_usr_id \n" );

            //확인 System.out.println("CTIApp.CTIMonth() :  sql \n"+sb.toString() );

            ps = conn.prepareStatement( sb.toString() );
            ps.setQueryTimeout(20);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if (md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }

                try {
                    commonDao.update("CTI_STA.MonthInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIMonth " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            //System.out.println(new Date() + "CTIMonth RS,RECORD COUNT " + iCnt);

            extlogger.debug("[CTI STA SAVE ::::: CTIMonth cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIMonth " + e.getMessage() + "]");

        } catch (Exception e) {
        	errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIMonth " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIMonth rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIMonth ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIMonth conn EXCEPTION ::" + e.getMessage());}
        }
    }


    private void CTIQuarter() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {


            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String cur_sql = commonQuery("QT");
            String org_sql = commonAddQuery("QT","ORG");
            String sum_sql = commonAddQuery("QT","SUM");
            String header_sql = commonAddHeaderQuery();

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT \n");
            sb.append(       header_sql );
            sb.append("FROM ( \n" );
            sb.append(       cur_sql);
            sb.append("UNION ALL \n");
            sb.append(       org_sql);
            sb.append("UNION ALL \n");
            sb.append(       sum_sql);
            sb.append(") AA \n" );
            sb.append("GROUP BY i_time_key, i_usr_id \n" );

            //확인 System.out.println("CTIApp.CTIQuarter() :  sql \n"+sb.toString() );

            ps = conn.prepareStatement( sb.toString() );
            ps.setQueryTimeout(20);
            rs = ps.executeQuery();


            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if (md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }

                try {
                    commonDao.update("CTI_STA.QuarterInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIQuarter " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            //System.out.println(new Date() + "CTIQuarter RS,RECORD COUNT " + iCnt);

            extlogger.debug("[CTI STA SAVE ::::: CTIQuarter cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIQuarter " + e.getMessage() + "]");

        } catch (Exception e) {
        	errlogger.debug("[CTI DAEMON STA MAIN Exception : CTIQuarter " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIQuarter rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIQuarter ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIQuarter conn EXCEPTION ::" + e.getMessage());}
        }
    }


    private void CTIYear() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String cur_sql = commonQuery("YR");
            String org_sql = commonAddQuery("YR","ORG");
            String sum_sql = commonAddQuery("YR","SUM");
            String header_sql = commonAddHeaderQuery();

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT \n");
            sb.append(       header_sql );
            sb.append("FROM ( \n" );
            sb.append(       cur_sql);
            sb.append("UNION ALL \n");
            sb.append(       org_sql);
            sb.append("UNION ALL \n");
            sb.append(       sum_sql);
            sb.append(") AA \n" );
            sb.append("GROUP BY i_time_key, i_usr_id \n" );

            //확인 System.out.println("CTIApp.CTIYear() :  sql \n"+sb.toString() );

            ps = conn.prepareStatement( sb.toString() );
            ps.setQueryTimeout(20);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }
                if (md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }

                try {
                    commonDao.update("CTI_STA.YearInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIYear " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            //System.out.println(new Date() + "CTIYear RS,RECORD COUNT " + iCnt);

            extlogger.debug("[CTI STA SAVE ::::: CTIYear cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIYear " + e.getMessage() + "]");
        } catch (Exception e) {
            errlogger.debug("[CTI DAEMON STA MAIN Exception : CTIYear " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIYear rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIYear ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIYear conn EXCEPTION ::" + e.getMessage());}
        }
    }



    /*
     * QUE STA****************************************************************************
     * */


    //오늘것만 가져옴
    private String commonQueQuery(String gubun)
    {
        String i_time_key = "";
        if(gubun.equals("TM"))
        {
            i_time_key =  "CONVERT(VARCHAR(8),dIntervalStart,112) + REPLACE(CONVERT(VARCHAR(5),dIntervalStart,108),':','') + 'KST' i_time_key, \n";
        }
        if(gubun.equals("DT"))
        {
            i_time_key = "CONVERT(VARCHAR(8),dIntervalStart,112) i_time_key, ";
        }
        else if(gubun.equals("YW"))
        {
            i_time_key = "convert(varchar(4),dIntervalStart,112) +  convert(varchar(2),DATEPART(WEEK, convert(varchar(8),dIntervalStart,112))) i_time_key,    ";
        }
        else if(gubun.equals("YM"))
        {
            i_time_key = "convert(varchar(6),dIntervalStart,112) i_time_key,   ";
        }
        else if(gubun.equals("QT"))
        {
            i_time_key = "convert(varchar(4),dIntervalStart,112)+'Q'+convert(varchar(1),DATEPART(QUARTER, dIntervalStart)) i_time_key, ";
        }
        else if(gubun.equals("YR"))
        {
            i_time_key = "convert(varchar(4),dIntervalStart,112) i_time_key,   ";
        }

        StringBuffer sb = new StringBuffer();

        sb.append("SELECT i_time_key, i_skil_grp, SUM(i_incl_call) i_incl_call, SUM(i_rsps_call) i_rsps_call,SUM(i_nus_call) i_nus_call,    \n");
        sb.append(" SUM(i_sec10_in_cnnt_ncnt) i_sec10_in_cnnt_ncnt, SUM(i_sec20_in_cnnt_ncnt) i_sec20_in_cnnt_ncnt, SUM(i_sec30_in_cnnt_ncnt) i_sec30_in_cnnt_ncnt,  SUM(i_sec40_in_cnnt_ncnt) i_sec40_in_cnnt_ncnt,  SUM(i_sec50_in_cnnt_ncnt) i_sec50_in_cnnt_ncnt,    \n");
        sb.append(" SUM(i_sec60_in_cnnt_ncnt) i_sec60_in_cnnt_ncnt,    \n");
        sb.append("      'CIC'  i_lst_corc_id,    \n");
        sb.append("      CONVERT(CHAR(8),GETDATE(),112)+REPLACE(CONVERT(CHAR(8),GETDATE(),108),':','')  i_lst_corc_dtm,         ");
        sb.append("      'CIC' i_reg_id,    \n");
        sb.append("      CONVERT(CHAR(8),GETDATE(),112)+REPLACE(CONVERT(CHAR(8),GETDATE(),108),':','') I_reg_dtm    \n");
        sb.append(" FROM     \n");
        sb.append(" (     \n");
        sb.append("   SELECT    \n");
        sb.append("        "+i_time_key+"    \n");
        sb.append("      (case when cName = 'BonusGroup1' then 'VQG_상담원연결_보너스카드_레벨1'    \n");
        sb.append("        when cName = 'BonusGroup2' then 'VQG_상담원연결_보너스카드_레벨2'        \n");
        sb.append("        when cName = 'BonusGroup3' then 'VQG_상담원연결_보너스카드_레벨3'        \n");
        sb.append("        when cName = 'BonusGroup4' then 'VQG_상담원연결_보너스카드_레벨4'        \n");
        sb.append("        when cName = 'BonusGroup5' then 'VQG_상담원연결_보너스카드_레벨5'        \n");
        sb.append("        when cName = 'OrderGroup' then 'VQG_상담원연결_주문'     \n");
        sb.append("        when cName = 'FAQGroup' then 'VQG_상담원연결_주문_주유소개설문의'     \n");
        sb.append("        when cName = 'EvGroup1' then 'VQG_상담원연결_Ev_주간'     \n");
        sb.append("        when cName = 'EvGroup2' then 'VQG_상담원연결_Ev_야간'     \n");
        sb.append("        when cName = 'EvGroup3' then 'VQG_상담원연결_Ev_공휴일'     \n");
        sb.append("        else cName   \n");
        sb.append("          end) as i_skil_grp,    \n");
        sb.append("         nEnteredAcd as i_incl_call, (nAnsweredAcd + nFlowOutAcd) as i_rsps_call, nAbandonedAcd as i_nus_call,    \n");
        sb.append("         nAnsweredAcdSvcLvl1 as i_sec10_in_cnnt_ncnt,    \n");
        sb.append("         (nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2) as i_sec20_in_cnnt_ncnt,    \n");
        sb.append("         (nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2 + nAnsweredAcdSvcLvl3) as i_sec30_in_cnnt_ncnt,    \n");
        sb.append("         (nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2 + nAnsweredAcdSvcLvl3 + nAnsweredAcdSvcLvl4) as i_sec40_in_cnnt_ncnt,    \n");
        sb.append("         (nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2 + nAnsweredAcdSvcLvl3 + nAnsweredAcdSvcLvl4 + nAnsweredAcdSvcLvl5) as i_sec50_in_cnnt_ncnt,    \n");
        sb.append("         (nAnsweredAcdSvcLvl1 + nAnsweredAcdSvcLvl2 + nAnsweredAcdSvcLvl3 + nAnsweredAcdSvcLvl4 + nAnsweredAcdSvcLvl5 + nAnsweredAcdSvcLvl6) as i_sec60_in_cnnt_ncnt    \n");
        sb.append("   FROM IWrkgrpQueueStats   \n");
        sb.append("   WHERE dIntervalStart BETWEEN CONVERT(VARCHAR(8),GETDATE()-1,112) +' 00:00:00' AND CONVERT(VARCHAR(8),GETDATE(),112) +' 23:59:59'   \n");
        sb.append("   and cHKey3='Call' and cHKey4 = '*'   \n");

        sb.append("   UNION ALL   \n");

        sb.append("   SELECT  \n");
        sb.append("        "+i_time_key+"    \n");
        sb.append("      'VQG_상담원연결_보너스카드' as cName,    \n");
        sb.append("      sum(nEnteredAcd) nEnteredAcd, sum(nAnsweredAcd+nFlowOutAcd) nAnsweredAcd, sum(nAbandonedAcd) nAbandonedAcd,    \n");
        sb.append("      sum(nAnsweredAcdSvcLvl1) nAnsweredAcdSvcLvl1,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2)) as nAnsweredAcdSvcLvl2,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3)) as nAnsweredAcdSvcLvl3,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) + sum(nAnsweredAcdSvcLvl4)) as nAnsweredAcdSvcLvl4,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) + sum(nAnsweredAcdSvcLvl4) + sum(nAnsweredAcdSvcLvl5)) as nAnsweredAcdSvcLvl5,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) + sum(nAnsweredAcdSvcLvl4) + sum(nAnsweredAcdSvcLvl5) + sum(nAnsweredAcdSvcLvl6)) as nAnsweredAcdSvcLvl6    \n");
        sb.append("   FROM IWrkgrpQueueStats   \n");
        sb.append("   WHERE dIntervalStart BETWEEN CONVERT(VARCHAR(8),GETDATE()-1,112) +' 00:00:00' AND CONVERT(VARCHAR(8),GETDATE(),112) +' 23:59:59'   \n");
        sb.append("   AND cHKey3='Call' and cHKey4 = '*'   \n");
        sb.append("   AND cName in ('BonusGroup1','BonusGroup2','BonusGroup3','BonusGroup4','BonusGroup5', 'EvGroup1')   \n");
        sb.append("   GROUP BY dIntervalStart   \n");

        sb.append("   UNION ALL   \n");

        sb.append("   SELECT  \n");
        sb.append("        "+i_time_key+"    \n");
        sb.append("      'VQG_상담원연결_Ev' as cName,    \n");
        sb.append("      sum(nEnteredAcd) nEnteredAcd, sum(nAnsweredAcd+nFlowOutAcd) nAnsweredAcd, sum(nAbandonedAcd) nAbandonedAcd,    \n");
        sb.append("      sum(nAnsweredAcdSvcLvl1) nAnsweredAcdSvcLvl1,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2)) as nAnsweredAcdSvcLvl2,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3)) as nAnsweredAcdSvcLvl3,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) + sum(nAnsweredAcdSvcLvl4)) as nAnsweredAcdSvcLvl4,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) + sum(nAnsweredAcdSvcLvl4) + sum(nAnsweredAcdSvcLvl5)) as nAnsweredAcdSvcLvl5,    \n");
        sb.append("      (sum(nAnsweredAcdSvcLvl1) + sum(nAnsweredAcdSvcLvl2) + sum(nAnsweredAcdSvcLvl3) + sum(nAnsweredAcdSvcLvl4) + sum(nAnsweredAcdSvcLvl5) + sum(nAnsweredAcdSvcLvl6)) as nAnsweredAcdSvcLvl6    \n");
        sb.append("   FROM IWrkgrpQueueStats   \n");
        sb.append("   WHERE dIntervalStart BETWEEN CONVERT(VARCHAR(8),GETDATE()-1,112) +' 00:00:00' AND CONVERT(VARCHAR(8),GETDATE(),112) +' 23:59:59'   \n");
        sb.append("   AND cHKey3='Call' and cHKey4 = '*'   \n");
        sb.append("   AND cName in ('EvGroup1','EvGroup2','EvGroup3')   \n");
        sb.append("   GROUP BY dIntervalStart   \n");


        sb.append(") AA   \n");
        sb.append("GROUP BY i_time_key, i_skil_grp \n");


        return sb.toString();

    }


    // 최초원본 또는 어제까지의 누적분을 가져옴
    // @param gubun 주기별
    // @param gubun2  최초원본(ORG) | 누적분(SUM)
    private String commonQueAddQuery(String gubun, String gubun2)
    {

        String where = "";
        if(gubun.equals("YW"))
        {
            where = " kind = 'YW' AND time_key = CONVERT(VARCHAR(4),GETDATE(),112) +  CONVERT(VARCHAR(2),DATEPART(WEEK, CONVERT(VARCHAR(8),GETDATE(),112))) \n";
        }
        else if(gubun.equals("YM"))
        {
            where = " kind = 'YM' AND time_key = CONVERT(VARCHAR(6), DATEADD(M, -1, CONVERT(VARCHAR(8),GETDATE(),112)), 112)  \n";
        }
        else if(gubun.equals("QT"))
        {
            where = " kind = 'QT' AND time_key = CONVERT(VARCHAR(4), DATEADD(M, -1, CONVERT(VARCHAR(8),GETDATE(),112)), 112) +  'Q'+CONVERT(VARCHAR(1),DATEPART(QUARTER, DATEADD(M, -1, CONVERT(VARCHAR(8),GETDATE(),112))   ))  \n";
        }
        else if(gubun.equals("YR"))
        {
            where = " kind = 'YR' AND time_key = CONVERT(VARCHAR(4), DATEADD(M, -1, CONVERT(VARCHAR(8),GETDATE(),112)), 112)  \n";
        }


        StringBuffer sb = new StringBuffer();
        sb.append("  select time_key as i_time_key    \n");
        sb.append("  ,skil_grp as i_skil_grp    \n");
        sb.append("  ,incl_call as i_incl_call    \n");
        sb.append("  ,rsps_call as i_rsps_call    \n");
        sb.append("  ,nus_call as i_nus_call    \n");
        sb.append("  ,sec10_in_cnnt_ncnt as i_sec10_in_cnnt_ncnt    \n");
        sb.append("  ,sec20_in_cnnt_ncnt as i_sec20_in_cnnt_ncnt    \n");
        sb.append("  ,sec30_in_cnnt_ncnt as i_sec30_in_cnnt_ncnt    \n");
        sb.append("  ,sec40_in_cnnt_ncnt as i_sec40_in_cnnt_ncnt    \n");
        sb.append("  ,sec50_in_cnnt_ncnt as i_sec50_in_cnnt_ncnt    \n");
        sb.append("  ,sec60_in_cnnt_ncnt as i_sec60_in_cnnt_ncnt    \n");
        sb.append("  ,lst_corc_id i_lst_corc_id    \n");
        sb.append("  ,lst_corc_dtm i_lst_corc_dtm          \n");
        sb.append("  ,reg_id i_reg_id    \n");
        sb.append("  ,reg_dtm I_reg_dtm    \n");
        sb.append("  FROM GSC_BT_CTI_GRP_RPT_"+gubun2+"    \n");
        sb.append("  WHERE    \n");
        sb.append("        "+where);
        return sb.toString();
    }

    // 최종적으로 GROUP BY 할 최종 SELECT 절을 가져옴
    private String commonQueAddHeaderQuery()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(" i_time_key i_time_key    \n");
        sb.append(", i_skil_grp i_skil_grp    \n");
        sb.append(", SUM(i_incl_call) i_incl_call    \n");
        sb.append(", SUM(i_rsps_call) i_rsps_call    \n");
        sb.append(", SUM(i_nus_call) i_nus_call    \n");
        sb.append(", SUM(i_sec10_in_cnnt_ncnt) i_sec10_in_cnnt_ncnt    \n");
        sb.append(", SUM(i_sec20_in_cnnt_ncnt) i_sec20_in_cnnt_ncnt    \n");
        sb.append(", SUM(i_sec30_in_cnnt_ncnt) i_sec30_in_cnnt_ncnt    \n");
        sb.append(", SUM(i_sec40_in_cnnt_ncnt) i_sec40_in_cnnt_ncnt    \n");
        sb.append(", SUM(i_sec50_in_cnnt_ncnt) i_sec50_in_cnnt_ncnt    \n");
        sb.append(", SUM(i_sec60_in_cnnt_ncnt) i_sec60_in_cnnt_ncnt    \n");
        sb.append(", MAX(i_lst_corc_id) i_lst_corc_id    \n");
        sb.append(", MAX(i_lst_corc_dtm) i_lst_corc_dtm           \n");
        sb.append(", MAX(i_reg_id) i_reg_id    \n");
        sb.append(", MAX(I_reg_dtm) I_reg_dtm    \n");
        return sb.toString();
    }


    private void CTIFiveM_Que() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            System.out.println("==========CTIFiveM_Que START================");

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            //conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String sql = commonQueQuery("TM");

            //확인 System.out.println("CTIApp.CTIFiveM_Que() :  \n"+sql);

            ps = conn.prepareStatement(sql);
            ps.setQueryTimeout(10);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next())
            {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }
                for(int i=0; i<md.getColumnCount(); i++)
                {
                    map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                }


                try {
                    commonDao.update("CTI_STA_QUE.FiveMInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIFiveM_Que " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            extlogger.debug("[CTI STA SAVE ::::: CTIFiveM_Que cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIFiveM_Que " + e.getMessage() + "]");

        } catch (Exception e) {
        	errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIFiveM_Que " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIFiveM_Que rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIFiveM_Que ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIFiveM_Que conn EXCEPTION ::" + e.getMessage());}
        }
    }


    private void CTIDay_Que() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String sql = commonQueQuery("DT");
            //확인 System.out.println("CTIApp.CTIDay_Que() :  \n"+sql);

            ps = conn.prepareStatement(sql);
            ps.setQueryTimeout(10);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if (md != null) {
                    for(int i=0; i<md.getColumnCount(); i++)
                    {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }



                try {
                    commonDao.update("CTI_STA_QUE.DayInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIDay_Que " + se.toString() + "][DATA : " + map + "]");
                    break;
                }
                iCnt++;
            }

            extlogger.debug("[CTI STA SAVE ::::: CTIDay_Que cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIDay_Que " + e.getMessage() + "]");

        } catch (Exception e) {
        	errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIDay_Que " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIDay_Que rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIDay_Que ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIDay_Que conn EXCEPTION ::" + e.getMessage());}
        }

    }



    private void CTIWeek_Que() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String cur_sql = commonQueQuery("YW");
            String org_sql = commonQueAddQuery("YW","ORG");
            String sum_sql = commonQueAddQuery("YW","SUM");
            String header_sql = commonQueAddHeaderQuery();

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT \n");
            sb.append(       header_sql );
            sb.append("FROM ( \n" );
            sb.append(       cur_sql);
            sb.append("UNION ALL \n");
            sb.append(       org_sql);
            sb.append("UNION ALL \n");
            sb.append(       sum_sql);
            sb.append(") AA \n" );
            sb.append("GROUP BY i_time_key, i_skil_grp \n" );

            //확인 System.out.println("CTIApp.CTIWeek_Que() :  \n"+sb.toString());

            ps = conn.prepareStatement(sb.toString());
            ps.setQueryTimeout(10);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if (md != null) {
                    for(int i=0; i<md.getColumnCount(); i++)
                    {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }


                try {
                    commonDao.update("CTI_STA_QUE.WeekInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIWeek_Que " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            //System.out.println(new Date() + "CTIWeek_Que RS,RECORD COUNT " + iCnt);
            extlogger.debug("[CTI STA SAVE ::::: CTIWeek_Que cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIWeek_Que " + e.getMessage() + "]");
        } catch (Exception e) {
            errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIWeek_Que " + e.getMessage()+ "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIWeek_Que rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIWeek_Que ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIWeek_Que conn EXCEPTION ::" + e.getMessage());}
        }
    }

    private void CTIMonth_Que() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String cur_sql = commonQueQuery("YM");
            String org_sql = commonQueAddQuery("YM","ORG");
            String sum_sql = commonQueAddQuery("YM","SUM");
            String header_sql = commonQueAddHeaderQuery();

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT \n");
            sb.append(       header_sql );
            sb.append("FROM ( \n" );
//            sb.append(       cur_sql);
//            sb.append("UNION ALL \n");
//            sb.append(       org_sql);
//            sb.append("UNION ALL \n");
            sb.append(       sum_sql);
            sb.append(") AA \n" );
            sb.append("GROUP BY i_time_key, i_skil_grp \n" );

            //System.out.println("CTIApp.CTIMonth_Que() :  \n"+sb.toString());

            ps = conn.prepareStatement(sb.toString());
            ps.setQueryTimeout(10);
            rs = ps.executeQuery();


            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if (md != null) {
                    for(int i=0; i<md.getColumnCount(); i++)
                    {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }


                try {
                    commonDao.update("CTI_STA_QUE.MonthInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIMonth_Que " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            //System.out.println(new Date() + "CTIMonth_Que RS,RECORD COUNT " + iCnt);
            extlogger.debug("[CTI STA SAVE ::::: CTIMonth_Que cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIMonth_Que " + e.getMessage() + "]");

        } catch (Exception e) {
        	errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIMonth_Que " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIMonth_Que rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIMonth_Que ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIMonth_Que conn EXCEPTION ::" + e.getMessage());}
        }
    }


    private void CTIQuarter_Que() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String cur_sql = commonQueQuery("QT");
            String org_sql = commonQueAddQuery("QT","ORG");
            String sum_sql = commonQueAddQuery("QT","SUM");
            String header_sql = commonQueAddHeaderQuery();

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT \n");
            sb.append(       header_sql );
            sb.append("FROM ( \n" );
//            sb.append(       cur_sql);
//            sb.append("UNION ALL \n");
//            sb.append(       org_sql);
//            sb.append("UNION ALL \n");
            sb.append(       sum_sql);
            sb.append(") AA \n" );
            sb.append("GROUP BY i_time_key, i_skil_grp \n" );

            //확인 System.out.println("CTIApp.CTIQuarter_Que() :  \n"+sb.toString());

            ps = conn.prepareStatement(sb.toString());
            ps.setQueryTimeout(10);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            String max_key = "";
            String cur_key = "";
            boolean bSuccess = true;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if(md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }


                try {
                    commonDao.update("CTI_STA_QUE.QuarterInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIQuarter_Que " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            extlogger.debug("[CTI STA SAVE ::::: CTIQuarter_Que cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIQuarter_Que " + e.getMessage() + "]");

        } catch (Exception e) {
            errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIQuarter_Que " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIQuarter_Que rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIQuarter_Que ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIQuarter_Que conn EXCEPTION ::" + e.getMessage());}
        }
    }


    private void CTIYear_Que() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            DriverManager.setLoginTimeout(10);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            String cur_sql = commonQueQuery("YR");
            String org_sql = commonQueAddQuery("YR","ORG");
            String sum_sql = commonQueAddQuery("YR","SUM");
            String header_sql = commonQueAddHeaderQuery();

            StringBuffer sb = new StringBuffer();
            sb.append("SELECT \n");
            sb.append(       header_sql );
            sb.append("FROM ( \n" );
//            sb.append(       cur_sql);
//            sb.append("UNION ALL \n");
//            sb.append(       org_sql);
//            sb.append("UNION ALL \n");
            sb.append(       sum_sql);
            sb.append(") AA \n" );
            sb.append("GROUP BY i_time_key, i_skil_grp \n" );

            //확인 System.out.println("CTIApp.CTIYear_Que() :  \n"+sb.toString());

            ps = conn.prepareStatement(sb.toString());
            ps.setQueryTimeout(10);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if(md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }
                
                /*
                // 콜통계 값 확인용
                for(Entry<String, String> entrySet : map.entrySet()) {
                		System.out.println(entrySet.getKey() + " : " +  entrySet.getValue());
                }
				*/
                try {
                    commonDao.update("CTI_STA_QUE.YearInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIYear_Que " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            extlogger.debug("[CTI STA SAVE ::::: CTIYear_Que cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIYear_Que " + e.getMessage() + "]");
        } catch (Exception e) {
            errlogger.debug("[CTI DAEMON STA MAIN Exception : CTIYear_Que " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTIYear_Que rs EXCEPTION ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTIYear_Que ps EXCEPTION ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTIYear_Que conn EXCEPTION ::" + e.getMessage());}
        }
    }




    private String commonColumn() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(" SELECT ");
        sb.append("  A.TIME_KEY                       AS I_TIME_KEY                  ");
        sb.append(" ,REPLACE(SUBSTR(C.PRESENTATION_NAME,1,6),',','') AS I_USR_ID  ");
        sb.append(" ,VALUE(A.T_WORK, 0)            AS I_AF_PROC_DRTM              ");
        sb.append(" ,VALUE(A.T_INBOUND, 0)         AS I_INBND_DRTM                ");
        sb.append(" ,VALUE(A.T_OUTBOUND, 0)        AS I_OTBND_DRTM                ");
        sb.append(" ,VALUE(A.T_TALK, 0)            AS I_TOT_CUR_DRTM              ");
        sb.append(" ,VALUE(A.T_WAIT, 0)            AS I_TOT_LRGEXST_DRTM          ");
        sb.append(" ,VALUE(A.T_LOGIN, 0)           AS I_WKG_DRTM                  ");
        sb.append(" ,VALUE(A.T_RINGING, 0)         AS I_TEL_RING_DRTM             ");
        sb.append(" ,VALUE(A.T_DIALING, 0)         AS I_DIALING_DRTM              ");
        sb.append(" ,VALUE(A.T_CONSULT, 0)         AS I_CONSULT_CUR_DRTM          ");
        sb.append(" ,VALUE(A.T_HOLD, 0)            AS I_SUSP_DRTM                 ");
        sb.append(" ,VALUE(A.T_INTERNAL, 0)        AS I_EXT_CUR_DRTM              ");
        sb.append(" ,VALUE(A.T_UNKNOWN, 0)         AS I_UNKN_CUR_DRTM             ");
        sb.append(" ,VALUE(A.N_WORK, 0)            AS I_AF_PROC_TCNT              ");
        sb.append(" ,VALUE(A.N_INBOUND, 0)         AS I_INBND_HOUSE               ");
        sb.append(" ,VALUE(A.N_OUTBOUND, 0)        AS I_OTBND_HOUSE               ");
        sb.append(" ,VALUE(A.N_TALK, 0)            AS I_TOT_CUR_NCNT              ");
        sb.append(" ,VALUE(A.N_WAIT, 0)            AS I_TOT_LRGEXST_TCNT          ");
        sb.append(" ,VALUE(A.N_RINGING, 0)         AS I_TEL_RING_NCNT             ");
        sb.append(" ,VALUE(A.N_DIALING, 0)         AS I_DIALING_NCNT              ");
        sb.append(" ,VALUE(A.N_CONSULT, 0)         AS I_CONSULT_CUR_NCNT          ");
        sb.append(" ,VALUE(A.N_HOLD, 0)            AS I_SUSP_NCNT                 ");
        sb.append(" ,VALUE(A.N_INTERNAL, 0)        AS I_EXT_CUR_NCNT              ");
        sb.append(" ,VALUE(A.N_UNKNOWN, 0)         AS I_UNKN_CALL_NCNT            ");
        sb.append(" ,VALUE(A.N_TRANSFERS_TAKEN, 0) AS I_HOUSE_SWT_RCV_NCNT        ");
        sb.append(" ,VALUE(A.N_TRANSFERS_MADE, 0)  AS I_HOUSE_SWT_TRY_NCNT        ");
        sb.append(" ,VALUE(A.N_CONFERENCES, 0)     AS I_METI_CUR_NCNT             ");
        sb.append(" ,VALUE(A.VOICE_FRCD_OFF, 0)    AS I_FWD_CALLCNT               ");
        sb.append(" ,VALUE(B.T_NOT_READY_A,  0)    AS I_CLE_SEAT_DRTM             ");
        sb.append(" ,VALUE(B.T_NOT_READY_A1, 0)    AS I_CLE_SEAT_DRTM_REST        ");
        sb.append(" ,VALUE(B.T_NOT_READY_A2, 0)    AS I_CLE_SEAT_DRTM_MEAL        ");
        sb.append(" ,VALUE(B.T_NOT_READY_A3, 0)    AS I_CLE_SEAT_DRTM_EDU         ");
        sb.append(" ,VALUE(B.T_NOT_READY_A4, 0)    AS I_CLE_SEAT_DRTM_COCH        ");
        sb.append(" ,VALUE(B.T_NOT_READY_A5, 0)    AS I_CLE_SEAT_DRTM_DSFT        ");
        sb.append(" ,VALUE(B.T_NOT_READY_A6, 0)    AS I_CLE_SEAT_DRTM_EML         ");
        sb.append(" ,VALUE(B.T_NOT_READY_S,  0)    AS I_CLE_SEAT_DRTM2            ");
        sb.append(" ,VALUE(B.T_NOT_READY_S1, 0)    AS I_CLE_SEAT_DRTM2_REST       ");
        sb.append(" ,VALUE(B.T_NOT_READY_S2, 0)    AS I_CLE_SEAT_DRTM2_MEAL       ");
        sb.append(" ,VALUE(B.T_NOT_READY_S3, 0)    AS I_CLE_SEAT_DRTM2_EDU        ");
        sb.append(" ,VALUE(B.T_NOT_READY_S4, 0)    AS I_CLE_SEAT_DRTM2_COCH       ");
        sb.append(" ,VALUE(B.T_NOT_READY_S5, 0)    AS I_CLE_SEAT_DRTM2_DSFT       ");
        sb.append(" ,VALUE(B.T_NOT_READY_S6, 0)    AS I_CLE_SEAT_DRTM2_EML        ");
        sb.append(" ,VALUE(B.N_NOT_READY_A,  0)    AS I_CLE_SEAT_TCNT             ");
        sb.append(" ,VALUE(B.N_NOT_READY_A1, 0)    AS I_CLE_SEAT_TCNT_REST        ");
        sb.append(" ,VALUE(B.N_NOT_READY_A2, 0)    AS I_CLE_SEAT_TCNT_MEAL        ");
        sb.append(" ,VALUE(B.N_NOT_READY_A3, 0)    AS I_CLE_SEAT_TCNT_EDU         ");
        sb.append(" ,VALUE(B.N_NOT_READY_A4, 0)    AS I_CLE_SEAT_TCNT_COCH        ");
        sb.append(" ,VALUE(B.N_NOT_READY_A5, 0)    AS I_CLE_SEAT_TCNT_DSFT        ");
        sb.append(" ,VALUE(B.N_NOT_READY_A6, 0)    AS I_CLE_SEAT_TCNT_EML         ");
        sb.append(" ,VALUE(B.N_NOT_READY_S,  0)    AS I_CLE_SEAT_TCNT2            ");
        sb.append(" ,VALUE(B.N_NOT_READY_S1, 0)    AS I_CLE_SEAT_TCNT2_REST       ");
        sb.append(" ,VALUE(B.N_NOT_READY_S2, 0)    AS I_CLE_SEAT_TCNT2_MEAL       ");
        sb.append(" ,VALUE(B.N_NOT_READY_S3, 0)    AS I_CLE_SEAT_TCNT2_EDU        ");
        sb.append(" ,VALUE(B.N_NOT_READY_S4, 0)    AS I_CLE_SEAT_TCNT2_COCH       ");
        sb.append(" ,VALUE(B.N_NOT_READY_S5, 0)    AS I_CLE_SEAT_TCNT2_DSFT       ");
        sb.append(" ,VALUE(B.N_NOT_READY_S6, 0)    AS I_CLE_SEAT_TCNT2_EML        ");
        sb.append(" ,'CTI' AS I_REG_ID                                            ");
        sb.append(" ,REPLACE(CHAR(CURRENT DATE),'-','') AS I_REG_DTM              ");
        sb.append(" ,'CTI' AS I_LST_CORC_ID                                       ");
        sb.append(" ,REPLACE(CHAR(CURRENT DATE),'-','') AS I_LST_CORC_DTM         ");
        return sb.toString();
    }

    private String commonColumn_Que() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(" SELECT ");
        sb.append("  A.TIME_KEY                       AS I_TIME_KEY  ");
        sb.append(" ,C.PRESENTATION_NAME              AS I_SKIL_GRP  ");
        sb.append(" ,VALUE(A.N_ENTERED, 0)         AS I_INCL_CALL ");
        sb.append(" ,VALUE(A.N_ANSWERED, 0)        AS I_RSPS_CALL ");
        sb.append(" ,VALUE(A.N_ABANDONED, 0)       AS I_NUS_CALL  ");
        sb.append(" ,VALUE(B.N_ANSWERED_IN_10, 0)  AS I_SEC10_IN_CNNT_NCNT ");
        sb.append(" ,VALUE(B.N_ANSWERED_IN_20, 0)  AS I_SEC20_IN_CNNT_NCNT ");
        sb.append(" ,VALUE(B.N_ANSWERED_IN_30, 0)  AS I_SEC30_IN_CNNT_NCNT ");
        sb.append(" ,VALUE(B.N_ANSWERED_IN_40, 0)  AS I_SEC40_IN_CNNT_NCNT ");
        sb.append(" ,VALUE(B.N_ANSWERED_IN_50, 0)  AS I_SEC50_IN_CNNT_NCNT ");
        sb.append(" ,VALUE(B.N_ANSWERED_IN_60, 0)  AS I_SEC60_IN_CNNT_NCNT ");
        sb.append(" ,'CTI'                AS I_REG_ID               ");
        sb.append(" ,HEX(CURRENT DATE)    AS I_REG_DTM           ");
        sb.append(" ,'CTI'                AS I_LST_CORC_ID             ");
        sb.append(" ,HEX(CURRENT DATE)    AS I_LST_CORC_DTM      ");
        return sb.toString();
    }

    private void CTI_CALL(String Calendar) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
            DriverManager.setLoginTimeout(5);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);
            StringBuffer sb = new StringBuffer(100);

            sb.append(commonColumn_Que());

            sb.append("        FROM    V_GOQ_DAY A                                                     ");
            sb.append("                LEFT OUTER JOIN V_GOQ_SL_DAY B                                   ");
            sb.append("                     ON A.TIME_KEY = B.TIME_KEY AND A.OBJECT_ID = B.OBJECT_ID ");
            sb.append("                INNER JOIN OBJECT C                                             ");
            sb.append("                      ON A.OBJECT_ID = C.OBJECT_ID                               ");
            //sb.append("        WHERE   A.TIME_KEY  = '20120502'                               ");
            sb.append("        WHERE   A.TIME_KEY  IN (                             ");
            sb.append("'"+Calendar.toString()+"'");
            sb.append("      )          ");
            sb.append(" WITH UR ");

            extlogger.debug("CTI_CALL START====> 2"+sb.toString());
            ps = conn.prepareStatement(sb.toString());
            ps.setQueryTimeout(10);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }
                if(md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }

                try {
                    commonDao.update("CTI_STA_QUE.DayInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIDay_Que " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            //System.out.println(new Date() + "CTIDay_Que RS,RECORD COUNT " + iCnt);

            extlogger.debug("[CTI STA SAVE ::::: CTIDay_Que cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIDay_Que " + e.getMessage() + "]");
        } catch (Exception e) {
            errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIDay_Que " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTI_CALL rs Exception ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTI_CALL ps Exception ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTI_CALL conn Exception ::" + e.getMessage());}
        }

    }

    private void CTI_CALL2(String Calendar) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
            DriverManager.setLoginTimeout(5);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);
            StringBuffer sb = new StringBuffer(100);

            sb.append(commonColumn());

            sb.append("  FROM    V_AGENT_L_DAY A	                                              ");
            sb.append("          LEFT OUTER JOIN V_AGENT_NR_DAY B                                  ");
            sb.append("               ON A.TIME_KEY = B.TIME_KEY AND A.OBJECT_ID = B.OBJECT_ID    ");
            sb.append("          INNER JOIN OBJECT C                                              ");
            sb.append("                ON A.OBJECT_ID = C.OBJECT_ID                                ");
            //sb.append("  WHERE   A.TIME_KEY  = '20120502'                               ");
            sb.append("  WHERE   A.TIME_KEY  IN (                      ");
            sb.append("'"+Calendar.toString()+"'");
            sb.append(")");
            sb.append(" WITH UR ");



            ps = conn.prepareStatement(sb.toString());
            ps.setQueryTimeout(20);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }

                if(md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }

                try {
                    commonDao.update("CTI_STA.DayInfo_UPDATE", map);
                    //  errlogger.debug("CTI_DayInfo_UPDATE_20160909===> Start!!");
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIDay " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            // System.out.println(new Date() + "CTIDay RS,RECORD COUNT " + iCnt);
            extlogger.debug("[CTI STA SAVE ::::: CTIDay cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIDay " + e.getMessage() + "]");
        } catch (Exception e) {
            errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIDay " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTI_CALL2 rs Exception ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTI_CALL2 ps Exception ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTI_CALL2 conn Exception ::" + e.getMessage());}
        }

    }
    //시간대별 통계 전체 가지고 오기
    private void CTI_CALL3(String Calendar) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
            DriverManager.setLoginTimeout(5);

            if(ComUtil.isProd())
                conn = DriverManager.getConnection(Const.CTI_TOTSTADB_URL, Const.CTI_TOTSTADB_ACCOUNT, Const.CTI_TOTSTADB_PASSWORD);
            else
                conn = DriverManager.getConnection(Const.TEST_CTI_TOTSTADB_URL, Const.TEST_CTI_TOTSTADB_ACCOUNT, Const.TEST_CTI_TOTSTADB_PASSWORD);

            StringBuffer sb = new StringBuffer(100);

            sb.append(commonColumn());
            sb.append(" FROM    V_AGENT_L_NO_AGG A	");
            sb.append("         LEFT OUTER JOIN V_AGENT_NR_NO_AGG B	");
            sb.append("              ON A.TIME_KEY = B.TIME_KEY AND A.OBJECT_ID = B.OBJECT_ID	");
            sb.append("         INNER JOIN OBJECT C	 ");
            sb.append("               ON A.OBJECT_ID = C.OBJECT_ID	");
            sb.append(" WHERE   A.TIME_KEY LIKE       ");
            sb.append("'"+Calendar.toString()+"'%");


			/*
			sb.append("  CONCAT(SUBSTR(REPLACE(CHAR(CURRENT TIMESTAMP),'-',''),1,10), '00KST') ");
			sb.append(" ,CONCAT(SUBSTR(REPLACE(CHAR(CURRENT TIMESTAMP),'-',''),1,10), '15KST') ");
			sb.append(" ,CONCAT(SUBSTR(REPLACE(CHAR(CURRENT TIMESTAMP),'-',''),1,10), '30KST') ");
			sb.append(" ,CONCAT(SUBSTR(REPLACE(CHAR(CURRENT TIMESTAMP),'-',''),1,10), '45KST') ");
			sb.append(" ,CONCAT(SUBSTR(REPLACE(CHAR(CURRENT TIMESTAMP - 1 HOUR),'-',''),1,10), '00KST') ");
			sb.append(" ,CONCAT(SUBSTR(REPLACE(CHAR(CURRENT TIMESTAMP - 1 HOUR),'-',''),1,10), '15KST') ");
			sb.append(" ,CONCAT(SUBSTR(REPLACE(CHAR(CURRENT TIMESTAMP - 1 HOUR),'-',''),1,10), '30KST') ");
			sb.append(" ,CONCAT(SUBSTR(REPLACE(CHAR(CURRENT TIMESTAMP - 1 HOUR),'-',''),1,10), '45KST') ");

			sb.append(" ) ");
			*/
            sb.append(" WITH UR ");
            ps = conn.prepareStatement(sb.toString());
            ps.setQueryTimeout(0);
            rs = ps.executeQuery();

            HashMap<String, String> map = new HashMap<String, String>();
            ResultSetMetaData md = null;
            int iCnt = 0;

            while(rs.next()) {
                if(iCnt==0) {
                    md = rs.getMetaData();
                    //iCnt = 1;
                }
                if(md != null) {
                    for(int i=0; i<md.getColumnCount(); i++) {
                        map.put(md.getColumnName(i+1).toLowerCase(), rs.getString(i+1)==null?"":rs.getString(i+1));
                    }
                }
                try {
                    commonDao.update("CTI_STA.FiveMInfo_UPDATE", map);
                } catch(SQLException se) {
                    errlogger.debug("[CTI DAEMON STA ERROR : CTIFiveM " + se.toString() + "][DATA : " + map + "]");
                }
                iCnt++;
            }

            //System.out.println(new Date() + "CTIFiveM RS,RECORD COUNT " + iCnt);
            extlogger.debug("[CTI STA SAVE ::::: CTIFiveM cti sta job good........]");

        } catch (SQLException e) {
            errlogger.debug("[CTI DAEMON STA MAIN SQLException : CTIFiveM " + e.getMessage() + "]");
        } catch (Exception e) {
            errlogger.debug("[CTI DAEMON STA MAIN ERROR : CTIFiveM " + e.getMessage() + "]");
        } finally {
            try { if(rs !=null) rs.close(); rs=null; } catch(SQLException e){errlogger.debug("CTI_CALL3 rs Exception ::" + e.getMessage());}
            try { if(ps !=null) ps.close(); ps=null; } catch(SQLException e){errlogger.debug("CTI_CALL3 ps Exception ::" + e.getMessage());}
            try { if(conn !=null) conn.close(); conn=null; } catch(SQLException e){errlogger.debug("CTI_CALL3 conn Exception ::" + e.getMessage());}
        }

    }
    //수동 통계는 일일 통계만 가지고 오는걸로 얘기를 해야 할거 같다.
    //너무 수작업이 많다.
    public void CTIChange(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String Calendar = request.getParameter("Calendar");
        if(!(Calendar == null || Calendar.equals(""))) {
            extlogger.debug("CTI_CALL START!"+Calendar.toString());
            CTI_Change(Calendar);
        } else {
            extlogger.debug("Calendar is Null");
        }
    }

    private void CTI_Change(String Calendar) {
        extlogger.debug("CTI_CALL START====> 1");
        //일별 통계
        CTI_CALL(Calendar);

        //CSC일일 현황 보고 통계
        //CTI_CALL2(Calendar);

        //시간대별 통계
        //CTI_CALL3(Calendar);
    }

}



