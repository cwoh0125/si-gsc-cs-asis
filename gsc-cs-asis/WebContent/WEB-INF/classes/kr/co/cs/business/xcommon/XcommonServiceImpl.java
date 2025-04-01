package kr.co.cs.business.xcommon;
 
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataTypes;

import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.persistence.dao.CommonDao;


 
public class XcommonServiceImpl implements XcommonService {
	private CommonDao commonDao = null;
 
	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;  
	}  
 
	private static String ROWTYPE_INSERT = "inserted";
	private static String ROWTYPE_UPDATE = "updated" ;  
	//private static String ROWTYPE_DELETE = "delete" ;
 
	/*fffff
	 * 공통 비즈니스 로직을 가진 함수이다.
	 * COMSAVE_0_ID	 
	 * COMSAVE_10_ID          
		COMSRCH_11_ID
		COMSAVE_10_IDATA   
		COMSRCH_11_IDATA  
		COMSRCH_11_ODATA 
	 */   
	public void XcommonTransaction(XcommonDto dto)  throws Exception  {				
 
		DataSet Ds = null;	   
		String sqlid = null;
		StringBuffer vid = new StringBuffer(10);
		
		//업무처리 저장
		if(dto.getDataSet("BIZSAVE_IDATA")!=null) {
			this.setBizSave(dto);
		}
		
		//시퀀스 채번이 있으면..
		if(dto.getDataSet("SEQSRCH_IDATA")!=null) {
			this.setSeqNumber(dto);
		}		
		
		for(int i=0; i<dto.getVlistCount(); i++) { 
//			select
			vid.delete(0, vid.length());
			vid.append("COMSRCH_"); vid.append(String.valueOf(i)); vid.append("_ID");
			
			if((sqlid = dto.getVariableValue(vid.toString())) != null) { 
				Ds = dto.getDataSet("COMSRCH_" + String.valueOf(i) + "_IDATA");
				List list = commonDao.selectList(sqlid, dto.getRowMap(Ds, 0));
				
				if(list.size()>0) {	
					dto.getOutdslist().add(dto.setDataSet(list, "COMSRCH_" + String.valueOf(i) + "_ODATA"));					
				}
				continue;
			}    
  
 
			//save
			vid.delete(0, vid.length());
			vid.append("COMSAVE_"); vid.append(String.valueOf(i)); vid.append("_ID");
			
			if((sqlid = dto.getVariableValue(vid.toString())) != null) {
				Ds = dto.getDataSet("COMSAVE_" + String.valueOf(i) + "_IDATA");

				//delete 먼저하고
				for(int m=0; m < Ds.getRemovedRowCount(); m++) {					
					commonDao.delete(sqlid+"_DELETE", dto.getRowDeleteMap(Ds, m));
				}

				//인서트 업데이트 한다.
				for(int k=0; k < Ds.getRowCount(); k++) {
					
					if(ROWTYPE_INSERT.equals(Ds.getRowTypeName(k).toLowerCase())) {
						commonDao.insert(sqlid+"_INSERT", dto.getRowMap(Ds, k));
					} else if(ROWTYPE_UPDATE.equals(Ds.getRowTypeName(k).toLowerCase())) {
						commonDao.update(sqlid+"_UPDATE", dto.getRowUpadteMap(Ds, k));
					}
				}
				continue;
			}
			
			//insert (단독)
			vid.delete(0, vid.length());
			vid.append("INSERT_"); vid.append(String.valueOf(i)); vid.append("_ID");
			if((sqlid = dto.getVariableValue(vid.toString())) != null) {
				Ds = dto.getDataSet("INSERT_" + String.valueOf(i) + "_IDATA");
				int k=0;
				for(k=0; k < Ds.getRowCount(); k++) {
					commonDao.insert(sqlid, dto.getRowMap(Ds, k));
				}
				continue;
			} 

			//update (단독)
			vid.delete(0, vid.length());
			vid.append("UPDATE_"); vid.append(String.valueOf(i)); vid.append("_ID");
			if((sqlid = dto.getVariableValue(vid.toString())) != null) { 
				Ds = dto.getDataSet("UPDATE_" + String.valueOf(i) + "_IDATA");
				for(int k=0; k < Ds.getRowCount(); k++) {
					commonDao.update(sqlid, dto.getRowMap(Ds, k));
				}
				continue;
			}
			
			//delete (단독)
			vid.delete(0, vid.length());
			vid.append("DELETE_"); vid.append(String.valueOf(i)); vid.append("_ID");
			if((sqlid = dto.getVariableValue(vid.toString())) != null) { 
				Ds = dto.getDataSet("DELETE_" + String.valueOf(i) + "_IDATA");
				for(int k=0; k < Ds.getRowCount(); k++) {			
					commonDao.delete(sqlid, dto.getRowMap(Ds, k));
				}				
			}
		}		
	} 
	
	/*
	 * (non-Javadoc)
	 *
	 */
	public void XcommonUserTransaction(XcommonDto dto)  throws Exception  {				
		DataSet Ds = null;	 
		String sqlid = null;
		StringBuffer vid = new StringBuffer(10);
		
		//업무처리 저장
		if(dto.getDataSet("BIZSAVE_IDATA")!=null) {
			this.setBizSave(dto);
		}
		
		//시퀀스 채번이 있으면..
		if(dto.getDataSet("SEQSRCH_IDATA")!=null) {
			this.setSeqNumber(dto);
		}		
		
		for(int i=0; i<dto.getVlistCount(); i++) {

			// select
			vid.delete(0, vid.length());
			vid.append("SELECT_"); vid.append(String.valueOf(i)); vid.append("_ID");
			if((sqlid = dto.getVariableValue(vid.toString())) != null) { 
				Ds = dto.getDataSet("SELECT_" + String.valueOf(i) + "_IDATA");
				List list = commonDao.selectList(sqlid, dto.getRowMap(Ds, 0));
				
				if(list.size()>0) {					
					dto.getOutdslist().add(dto.setDataSet(list, "SELECT_" + String.valueOf(i) + "_ODATA"));
				}
				continue;
			}

			//insert
			vid.delete(0, vid.length());
			vid.append("INSERT_"); vid.append(String.valueOf(i)); vid.append("_ID");
			if((sqlid = dto.getVariableValue(vid.toString())) != null) {
				Ds = dto.getDataSet("INSERT_" + String.valueOf(i) + "_IDATA");
				int k=0;
				for(k=0; k < Ds.getRowCount(); k++) {
					//System.out.println("시작시간 [" + new Date() +"]");
					commonDao.insert(sqlid, dto.getRowMap(Ds, k));
					//System.out.println("종료시간 [" + new Date() +"]");
				}
				continue;
			} 

			//update
			vid.delete(0, vid.length());
			vid.append("UPDATE_"); vid.append(String.valueOf(i)); vid.append("_ID");
			if((sqlid = dto.getVariableValue(vid.toString())) != null) { 
				Ds = dto.getDataSet("UPDATE_" + String.valueOf(i) + "_IDATA");
				for(int k=0; k < Ds.getRowCount(); k++) {
					commonDao.update(sqlid, dto.getRowMap(Ds, k));
				}
				continue;
			}
			
			//delete
			vid.delete(0, vid.length());
			vid.append("DELETE_"); vid.append(String.valueOf(i)); vid.append("_ID");
			if((sqlid = dto.getVariableValue(vid.toString())) != null) { 
				Ds = dto.getDataSet("DELETE_" + String.valueOf(i) + "_IDATA");
				for(int k=0; k < Ds.getRowCount(); k++) {			
					commonDao.delete(sqlid, dto.getRowMap(Ds, k));
				}				
				continue;
			}
			
			//procudre
			vid.delete(0, vid.length());
			vid.append("PROCEDURE_"); vid.append(String.valueOf(i)); vid.append("_ID");
			if((sqlid = dto.getVariableValue(vid.toString())) != null) { 
				Ds = dto.getDataSet("PROCEDURE_" + String.valueOf(i) + "_IDATA");
				for(int k=0; k < Ds.getRowCount(); k++) {
					commonDao.selectString(sqlid, dto.getRowMap(Ds, k));
				}				
				continue;
			}
			
			//procudre_OUT
			vid.delete(0, vid.length());
			vid.append("PROCEDUREOUT_"); vid.append(String.valueOf(i)); vid.append("_ID");
			if((sqlid = dto.getVariableValue(vid.toString())) != null) { 
				Ds = dto.getDataSet("PROCEDUREOUT_" + String.valueOf(i) + "_IDATA");
				
				System.out.println("######## sqlid::" + sqlid);
				
				List list = commonDao.selectList(sqlid, dto.getRowMap(Ds, 0));
				
				System.out.println("######## list.size::" + list.size());
				
				if(list.size()>0) {//조회일때는 한건만 한다는 전제조건으로 하자. 두건이상이면 레코드가 뒤0섞일 가능성이 있으므로 
					dto.getOutdslist().add(dto.setDataSet(list, "PROCEDUREOUT_" + String.valueOf(i) + "_ODATA"));
				}
				continue;
			}		
		} 
	}
	

	
	/*
	 * (non-transaction)./stop.sh
	 *이노믹스 메일 수신
	 */
	public void XcommonNonTransaction(XcommonDto dto)  throws Exception  {				
		DataSet Ds = null;	 
		String sqlid = null;
		HashMap imap = new HashMap(); 
		HashMap dbmap = new HashMap();
		List olist = new ArrayList();
		
		
		//insert
		if((sqlid = dto.getVariableValue("QUERY_ID")) != null) {
			Ds = dto.getDataSet("REQUEST_DATA");
			int k=0;
			for(k=0; k < Ds.getRowCount(); k++) {
				imap = dto.getDefaultRowMap(Ds, k);
				dbmap = dto.getRowMap(Ds, k);				
				/*
				Ds.addColumn("ctt1", DataTypes.STRING, 20000);
				Ds.addColumn("ctt2", DataTypes.STRING, 20000);				
				
				//이노믹스 메일 수신 부이면? 
				if(sqlid == "Common.Set_InmxEmail_INSERT"){
					System.out.println("IF==============");					
					System.out.println("CTT======>"+Ds.getColumn("ctt").toString());
					System.out.println("CTT.length========>"+Ds.getColumn("ctt").toString().length()+Ds.getColumn("ctt1").toString().length()+Ds.getColumn("ctt2").toString().length());						
					int len = Ds.getColumn("ctt").toString().length()+Ds.getColumn("ctt1").toString().length()+Ds.getColumn("ctt2").toString().length();
					dbmap.put("ctt_len",len);
				

				}else{
					System.out.println("ELSE==============");
					System.out.println("CTT======>"+Ds.getColumn("ctt").toString());
					System.out.println("CTT.length========>"+Ds.getColumn("ctt").toString().length());
					System.out.println("CTT.length========>"+Ds.getColumn("ctt").toString().length()+Ds.getColumn("ctt1").toString().length()+Ds.getColumn("ctt2").toString().length());						
					int len = Ds.getColumn("ctt").toString().length()+Ds.getColumn("ctt1").toString().length()+Ds.getColumn("ctt2").toString().length();
					dbmap.put("ctt_len",len);
				}
				
				System.out.println("dbmap===>"+dbmap);
				*/
				try {
					commonDao.insert(sqlid, dbmap);
					imap.put("ERR_CD", "0");
					imap.put("ERR_MSG", "");
				} catch(Exception se) {
					imap.put("ERR_CD", "-2");
					imap.put("ERR_MSG", se.getMessage());
				}
				olist.add(k, imap);
			}
			dto.getOutdslist().add(dto.setDataSet(olist, "RESULT_DATA"));
		} 
	}
	

	
	
	
	
	/*
	 * 업무처리정보
	 * */
	private void setBizSave(XcommonDto dto) throws Exception {
		DataSet Ds = dto.getDataSet("BIZSAVE_IDATA");
		if(Ds.getRowCount()>0)
			commonDao.insert("Common.Set_BizProcInfo_INSERT", dto.getRowMap(Ds, 0));

	}
	
	
	/*
	 * 시퀀스번호를 세팅한다.
	 *	
	 * 시퀀스값을 필요로 하는 관련 DataSet이 2개이상 3개일경우에만 쓴다.
	 * ds name : SEQSRCH_IDATA
	 * column name : QUERY_ID, COLUMN_ID, DS1, DS2, DS3, SEQ_TITLE
	 * column data example : "COMSEQ.NoticeSEQ", "NOT_NO", "COMSAVE_0_IDATA", "COMSAVE_1_IDATA"
	 * column data example : "COMSEQ.NoticeSEQ", "NOT_NO", "INSERT_0_IDATA", "INSERT_1_IDATA"
	 * 주의사항 : 반드시 마스터 테이블은 하나의 레코드만 존재해야 하며  "인서트" 해야 한다. 다른 레코드 업데이트 이런건 없다.
	 *           COMSAVE로 만들어서 호출할경우는 저장DataSet을 따로 만들어서 전송하길 바란다.
	 * */
	private void setSeqNumber(XcommonDto dto) throws Exception {
		
		DataSet ds_seq = dto.getDataSet("SEQSRCH_IDATA");
		DataSet temp = null;
		HashMap map = null;
		String seq = null;
		String column_id = null;
		for(int i=0; i<ds_seq.getRowCount(); i++) {
			map = dto.getRowMap(ds_seq, i);
			column_id = (String)map.get("column_id");
			seq = commonDao.selectString((String)map.get("query_id") , map);
			temp = dto.getDataSet((String)map.get("ds1"));
			if(temp != null){
				for(int k=0; k<temp.getRowCount(); k++) {
					temp.set(k, column_id, seq);
				}
			}
			temp = dto.getDataSet((String)map.get("ds2"));
			if(temp != null){
				for(int k=0; k<temp.getRowCount(); k++) {
					temp.set(k, column_id, seq);
				}
			}
			temp = dto.getDataSet((String)map.get("ds3"));
			if(temp != null){
				for(int k=0; k<temp.getRowCount(); k++) {
					temp.set(k, column_id, seq);
				}
			}
		}
	}
	
	public void XcommonServerEnv(XcommonDto dto)  throws Exception  {				
		DataSet ds_senv = new DataSet("DS_SERVERENV");
		ds_senv.addColumn("UNIX", DataTypes.STRING, 255); //UNIX:Y, N
		ds_senv.addColumn("PROD", DataTypes.STRING, 255); //운영:Y, N
		
		ds_senv.newRow();
		System.out.println("DS_SERVERENV");
		if("WIN".equals(ComUtil.getOsMinName().toUpperCase())) {
			ds_senv.set(0, "UNIX", "N");
			ds_senv.set(0, "PROD", "N");
		} else {
			if(ComUtil.isProd()) {
				ds_senv.set(0, "UNIX", "Y");
				ds_senv.set(0, "PROD", "Y");
			} else {
				ds_senv.set(0, "UNIX", "Y");
				ds_senv.set(0, "PROD", "N");
			}
		}
		dto.getOutdslist().add(ds_senv);
		
	}
}

