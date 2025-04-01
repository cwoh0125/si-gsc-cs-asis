package kr.co.cs.common.xdto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataSetList;
import com.tobesoft.xplatform.data.DataTypes;
import com.tobesoft.xplatform.data.Variable;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.tx.HttpPlatformRequest;
import com.tobesoft.xplatform.tx.PlatformType;

import kr.co.cs.common.config.Const;


public class XcommonDto {
	
	//private String default_encode_method = PlatformType.CONTENT_TYPE_XML;
		
	private String client_charset = "UTF-8";
	
	private VariableList vlist = null;
	
	private DataSetList  dslist = null;

	private VariableList outvlist = null;
	
	private DataSetList  outdslist = null;
	
	private DataSet globalDataSet = null;
	
	private HashMap globalDataSetMap = null;
	
	//일반 프로그램에서 개인이 각자 넣엏다 뺐다 하는 값.
	private HashMap<String, String> userTempMap = null;
	private List userTempList = null;
	
	/*public String getDefault_encode_method() {
		return default_encode_method;
	}*/
	
	public String getClient_charset() {
		return client_charset;
	}
	public DataSetList getDslist() {
		return dslist; 
	}
	public DataSet getGlobalDataSet() {
		return globalDataSet;
	}
	public DataSetList getOutdslist() {
		return outdslist;
	}
	public VariableList getVlist() {
		return vlist;
	}	
	public VariableList getOutvlist() {
		return outvlist;
	}
	public HashMap getGlobalDataSetMap() {
		return globalDataSetMap;
	}
	/*
	 * dto 초기화.
	 */
	public void setDsInit(HttpServletRequest request) throws Exception {
		
		/*
	   System.out.println("request:"+request);
		System.out.println("getProtocol:"+request.getProtocol());
		System.out.println("getQueryString:"+request.getQueryString());
		System.out.println("getRequestURI:"+request.getRequestURI());
		System.out.println("getRequestURL:"+request.getRequestURL());
		System.out.println("toString:"+request.toString());
		*/
		
		
		//2010.8월 인코딩 막음. 유진현
		HttpPlatformRequest platformRequest = new HttpPlatformRequest(request, "", Const.DEFAULT_CHARSET);
		platformRequest.setStreamLogEnabled(true);
		//platformRequest.setStreamLogDir("c:/logs/");
		platformRequest.receiveData();
		
		PlatformData pformdata = platformRequest.getData();
		
		//System.out.println("platformRequest:"+platformRequest.getData());
		//System.out.println("getContentType:"+platformRequest.getContentType());
		//System.out.println("toString:"+platformRequest.toString());		
		
		//System.out.println(in_data.toString());
		this.client_charset = platformRequest.getCharset();
		this.vlist = pformdata.getVariableList();
		this.dslist = pformdata.getDataSetList();
		this.outvlist = new VariableList();
		this.outdslist = new DataSetList();		
		this.globalDataSet  = dslist.get("GDS_USER"); //전역 데이터셑
				
		this.userTempMap = new HashMap(); //일반 전역변수값.
		this.userTempList = new ArrayList();

		setGlobalDataSetMap(globalDataSet); //전역데이터셋의 맵값.
 
		this.outvlist.add("ErrorCode", "0"); //2010.8 일단성공으로 만든다.
		this.outvlist.add("ErrorMsg", "");
						
	}

	/*
	 * 전역데이터셋의 맵 값을 가지고 있는다.
	 */
	private void setGlobalDataSetMap(DataSet gds) {		
		HashMap map = new HashMap();
		if(gds != null) {
			for ( int i = 0; i < gds.getColumnCount(); i++ ) {
				map.put((gds.getColumn(i).getName()).toLowerCase(), dsToString(gds.getObject(0, i)));	
			}
		}
		this.globalDataSetMap = map;
	}
	
	/*
	 * 전역데이터에서 각 필드별로 값을 뽑아온다.
	 */
	public String getGlobalData(String id) {
		try {
			return globalDataSet.getObject(0, id).toString();
		} catch(Exception e) {
			return null;
		}				
	}
	
	/*
	 * 마이플랫폼 변수명 리턴.
	 */
	public String getVariableId(int index) {
		try {
			return vlist.get(index).getName() ;
		} catch(Exception e){
			return null;
		}
	}
	
	/*
	 * 마이플랫폼 변수값 리턴.
	 */
	public String getVariableValue(int index) {
		try {
			return vlist.get(index).getString();
		} catch(Exception e){
			return null;
		}
	}

	/*
	 * 마이플랫폼 변수값 리턴.
	 */
	public String getVariableValue(String id) {
		try {
			return vlist.get(id).getString();
		} catch(Exception e){
			return null;
		}
	}
	
	/*
	 * 데이터셋을 데이터셋 변수명으로 가져온다
	 */
	public DataSet getDataSet(String dsname) {
		
		try {			
			return dslist.get(dsname);
		 
		} catch(Exception e){
			return null;
		}
	}
	
	/*
	 * 변수리스트 크기
	 */
	public int getVlistCount() {
		return vlist.size();
	}
	
	/*
	 * 결과 갯수를 담을 RESULT_COUNT DATASET을 리턴한다.
	 */
	public DataSet getResultDataSet() {		
		return outdslist.get("RESULT_COUNT");
	}
	
	
	public String dsToString(Object value){
		if( value == null )
			return "";
		else
			return value.toString();		
	}
	
	/*
	 * 데이터셋의 로우단위를 디폴트 값으로 해쉬맵으로 가져온다.
	 */
	public HashMap<String, Object> getDefaultRowMap(DataSet Ds, int rowcnt){
		HashMap<String, Object> map = new HashMap<String, Object>();
		if(Ds != null) {
			for ( int i = 0; i < Ds.getColumnCount(); i++ ) {
				map.put(Ds.getColumn(i).getName(), dsToString(Ds.getObject(rowcnt, i)));
			}
		}
		return map;
	}
	
	/*
	 * 데이터셋의 로우단위를 해쉬맵으로 가져온다.
	 */
	public HashMap<String, Object> getRowMap(DataSet Ds, int rowcnt){
		HashMap<String, Object> map = new HashMap<String, Object>();
		String columnID = null;
		if(Ds != null) {
			for ( int i = 0; i < Ds.getColumnCount(); i++ ) {
				columnID = (Ds.getColumn(i).getName()).toLowerCase(); 
				if(columnID.indexOf("iterate_")==-1) {
					map.put(columnID, dsToString(Ds.getObject(rowcnt, i)));
				} else {
					map.put(columnID, getColumnArray(dsToString(Ds.getObject(rowcnt, i))));
				}
			}
		}
		map.putAll(this.globalDataSetMap);
		return map;
	}
	
	/*
	 * 데이터셋의 row state가 업데이트인거를 업데이트 로우단위를 해쉬맵으로 가져온다.
	 */
	public HashMap<String, Object> getRowUpadteMap(DataSet Ds, int rowcnt){
		HashMap<String, Object> map = new HashMap<String, Object>();
		String columnID = null;
		for ( int i = 0; i < Ds.getColumnCount(); i++ ) {
			columnID = (Ds.getColumn(i).getName()).toLowerCase(); 
			if(columnID.indexOf("iterate_")==-1) {
				map.put(columnID, dsToString(Ds.getObject(rowcnt, i)));
			} else {
				map.put(columnID, getColumnArray(dsToString(Ds.getObject(rowcnt, i))));
			}
			map.put("org_" + columnID, dsToString(Ds.getSavedData(rowcnt, Ds.getColumn(i).getName())));
		}
		map.putAll(this.globalDataSetMap);
		return map;
	}

	/*
	 * 데이터셋의 row state가 딜리트인거를  해쉬맵으로 가져온다.
	 */

	public HashMap<String, Object> getRowDeleteMap(DataSet Ds, int rowcnt){
		HashMap<String, Object> map = new HashMap<String, Object>();
		String columnID = null;
		for ( int i = 0; i < Ds.getColumnCount(); i++ ) {
			columnID = (Ds.getColumn(i).getName()).toLowerCase(); 
			if(columnID.indexOf("iterate_")==-1) {
				map.put(columnID, dsToString(Ds.getRemovedData(rowcnt, Ds.getColumn(i).getName())));
			} else {
				map.put(columnID, getColumnArray(dsToString(Ds.getRemovedData(rowcnt, Ds.getColumn(i).getName()))));
			}
		}
		map.putAll(this.globalDataSetMap);
		return map;
	}
	private String[] getColumnArray(String val) {
		return val.replace(" ", "").split(",");
	}
	
	/*
	 * 리스트 형태로 넘겨받은 데이터를 DataSet 에 옮긴다. 
	 */	
	public DataSet setDataSet(List list, String outds) throws Exception  {
		
		System.out.println("outds:"+outds);
		System.out.println("toString:"+list.toString());
		//System.out.println("list:"+list);
		//System.out.println("toArray:"+list.toArray());
				
		//DataSet Ds = new DataSet(outds, client_charset);
		DataSet Ds = new DataSet(outds);
		String key = null;
		HashMap map = null;
		int row = 0; 
		 
		for(int i=0; i<list.size(); i++) {
			map = (HashMap)list.get(i);			
			if(i==0) {
				Iterator cir = map.keySet().iterator();
				while(cir.hasNext()){
					Ds.addColumn(cir.next().toString(), DataTypes.STRING, 1000);
				}
			}
			Iterator ir = map.keySet().iterator();
			row = Ds.newRow();
			while(ir.hasNext()) {
				key = ir.next().toString();
				Ds.set(row, key, dsToString(map.get(key)));
			}
		}
		return Ds;
	}
	

	/*
	 * map 형태로 넘겨받은 데이터를 DataSet 에 옮긴다. 
	 */	
	public DataSet setDataSet(HashMap map, DataSet Ds)  {		
		
        String key = null;
		try {
			if(Ds.getRowCount()==0) {
				Iterator cir = map.keySet().iterator();
				while(cir.hasNext()){
					key = cir.next().toString();
					if(key.indexOf("gbl_")==-1)
						//Ds.addColumn(key.toUpperCase(), DataTypes.STRING, 2000);
						//이노믹스
						Ds.addColumn(key.toUpperCase(), DataTypes.STRING, 4000);
				}
			}
			Iterator ir = map.keySet().iterator();
			int row = Ds.newRow();
			while(ir.hasNext()) {
				key = ir.next().toString();
				if(key.indexOf("gbl_")==-1)
					Ds.set(row, key.toUpperCase(), dsToString(map.get(key)));
			}
		} catch(Exception e) {			
		}
		
		return Ds;
	}
	
	/*
	 * 임시 값들 저장하는 변수
	 * */

	public HashMap<String, String> getUserTempMap() {
		return userTempMap;
	}
	
	public List getUserTempList() {
		return userTempList;
	}


	/*--------------------------------------------------------------------------------------
	 * subject : 외부에서 request로 넘어오지 않은 값들을 강제로 세팅하는 함수들
	 * */
	public void setAddExternalVariable(Variable v) {
		if(vlist==null) {
			vlist = new VariableList();
		}
		this.vlist.add(v);
	}
	public void setAddExternalDataset(DataSet ds) {
		if(dslist==null) {
			dslist = new DataSetList();
		}
		this.dslist.add(ds);
	}
	public void setExternalGblDataset(DataSet ds) {
		this.globalDataSet = ds;
		setGlobalDataSetMap(this.globalDataSet); //전역데이터셋의 맵값.
	}
}
