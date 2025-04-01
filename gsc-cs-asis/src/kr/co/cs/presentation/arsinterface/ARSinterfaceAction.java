package kr.co.cs.presentation.arsinterface;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.cs.business.arsinterface.ARSinterfaceService;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.presentation.xcommon.XbaseAction;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;



public class ARSinterfaceAction extends XbaseAction {
	
	private final static Logger logger = LogManager.getLogger("process.if");  
	
	private ARSinterfaceService ARSinterfaceservice = null;
	private Logger Log = Logger.getLogger(this.getClass());

	public ARSinterfaceService getARSinterfacecommonservice() { 
		return ARSinterfaceservice;
	}

	public void setARSinterfaceservice(ARSinterfaceService ARSinterfaceservice) {
		this.ARSinterfaceservice = ARSinterfaceservice;
	}
	
	/*
	 *데이터셋의 속성이 아니라 사용자가 트랜잭션을 지정하는 경우
	 */
	public void XcommonUserTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
		HttpServletResponse response) throws Exception {	
		
		/*
		//20150121 받는값 로그에 그대로 찍는것.
		BufferedReader _rd = null;
		String _line = "none";
		String _buffer = "";
		try{
			
			_rd = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

			boolean isFirstLine = true;
			while ((_line = _rd.readLine()) != null ){
				
				if(isFirstLine){
					isFirstLine = false;
					_buffer = _line;
				}else{
					_buffer = _buffer + "\r\n" + _line;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			_rd.close();
			_rd = null;
		}
		logger.debug("===================request S=================");
		logger.debug(_buffer);
		logger.debug("===================request E=================");
		*/
		
		XcommonDto dto = new XcommonDto();
		
		dto.setDsInit(request);
		logger.debug("===================request================="+request);
		ARSinterfaceservice.ARSUserTransaction(dto);		
		logger.debug("===================dto================="+dto);
		super.write(response, dto, "XML");		
		logger.debug("===================RESPONSE=================");
		for(int i = 0 ; i < dto.getOutvlist().size() ; i++){
			logger.debug("Parameter : " + dto.getOutvlist().get(i).getName() + " \t");
			logger.debug("P Value : " + dto.getOutvlist().getString(i));
		}
		for(int i = 0 ; i < dto.getOutdslist().size() ; i++){
			logger.debug("Dataset : " + dto.getOutdslist().get(i).getName());
			for(int  j = 0 ; j < dto.getOutdslist().get(i).getColumnCount() ; j++){
				logger.debug("Column ID : " + dto.getOutdslist().get(i).getColumn(j).getName() + "\t");
				for(int k = 0 ; k < dto.getOutdslist().get(i).getRowCount() ; k++){					
					logger.debug("Value : " + dto.getOutdslist().get(i).getString(k, j));
				}	
			}
		}
		logger.debug("===========================================");
	}
	
}
