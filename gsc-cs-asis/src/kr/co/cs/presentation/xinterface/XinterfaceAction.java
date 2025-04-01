package kr.co.cs.presentation.xinterface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.ResponseWrapper;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.cs.business.xcommon.XcommonService;
import kr.co.cs.business.xinterface.XinterfaceSecondService;
import kr.co.cs.business.xinterface.XinterfaceService;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.presentation.xcommon.XbaseAction;


public class XinterfaceAction extends XbaseAction {
	
	private final static Logger logger = LogManager.getLogger("process.if");

	private XinterfaceService xinterfaceservice = null;
	public XinterfaceService getXinterfaceservice() {
		return xinterfaceservice;
	}

	public void setXinterfaceservice(XinterfaceService xinterfaceservice) {
		this.xinterfaceservice = xinterfaceservice;
	}
	
	private XinterfaceSecondService xinterfacesecondservice = null;	
	public XinterfaceSecondService getXinterfacesecondservice() {
		return xinterfacesecondservice;
	}

	public void setXinterfacesecondservice(XinterfaceSecondService xinterfacesecondservice) {
		this.xinterfacesecondservice = xinterfacesecondservice;
	}

	private XcommonService xcommonservice = null;	
	public XcommonService getXcommonservice() {
		return xcommonservice;
	}

	public void setXcommonservice(XcommonService xcommonservice) {
		this.xcommonservice = xcommonservice;
	}

	public void CommonInterface(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xinterfaceservice.CommonInterface(dto, xcommonservice);
			super.write(response, dto);
		}	
	public void MultiRowTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xinterfaceservice.MultiRowTransaction(dto, xcommonservice);
			super.write(response, dto);
		}
	
	public void NameVerification(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xinterfacesecondservice.NameVerification(dto, xcommonservice);
			super.write(response, dto);
		}
	

	public void IVRStaSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xinterfacesecondservice.IVRStaSearch(dto, xcommonservice);
			super.write(response, dto);
		}
	
	public void ListenContiue(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
	        HttpServletResponse response) throws Exception {		
	    XcommonDto dto = new XcommonDto();
	    dto.setDsInit(request); 
	    xinterfacesecondservice.ListenContiue(dto, xcommonservice);
	    super.write(response, dto);
	}

	public void CTIMiniBoard(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xinterfacesecondservice.CTIMiniBoard(dto, xcommonservice);
			super.write(response, dto);
		}

	public void CTIUserSyncSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xinterfacesecondservice.CTIUserSyncSave(dto, xcommonservice);
			super.write(response, dto);
		}
	
	public void CTIUserMonitoring(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xinterfacesecondservice.CTIUserMonitoring(dto, xcommonservice);
			super.write(response, dto);
		}
	
	public void CTIMiniBoard2(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			String str = xinterfacesecondservice.CTIMiniBoard2();
			
			//sparrow 처리
			if(str != null && !"".equals(str)) {
				str = str.replaceAll("<","&lt;");
				str = str.replaceAll(">","&gt;");
			}
			
			PrintWriter out = response.getWriter();
			try {
				out.print(str);
				out.close();
			} catch(RuntimeException se) {
				out.print("FAIL");
				out.close();
				throw se;
			} catch(Exception e) {
				out.print("FAIL");
				out.close();
				throw e;
			}
			
	}
	
		
	public void CAMSUserSyncSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
				
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xinterfacesecondservice.CAMSUserSyncSave(dto, xcommonservice); 
			
			super.write(response, dto);
		}
	
	public void MSGSUserSyncSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
				
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xinterfacesecondservice.MSGUserSyncSave(dto, xcommonservice); 
			
			super.write(response, dto);
		}
	
	/**
	 * TM 민원처리결과 전송
	 * 2022.11.30 NP847
	 */
	public void TMDsftAcpnInterface(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String result = null;
		
		logger.debug("######## TM DsftAcpn Interface START ########");
		PrintWriter out = null;
		
		try {
			
			//TM 불만처리결과 DB Update
			result = xinterfaceservice.TmDsftAcpnUpdate(request);
			
			// json 수신
			response.setContentType("application/json; charset=UTF-8");
			
			// send
			out = response.getWriter();
	        out.print(result);
	        out.close();
			
		} catch (NullPointerException e) {
			logger.debug("Exception ::" + e.getMessage());
		} catch (Exception e) {
			logger.debug("Exception ::" + e.getMessage());
		} finally {
			out.close();
		}
		
	}
	
	/**
	 * 분리보관(탈회대상자) 수동배치
	 */
	public void custWithdrawBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		XcommonDto dto = new XcommonDto();
		dto.setDsInit(request); 
		xinterfacesecondservice.custWithdrawBatch(dto, xcommonservice);
		super.write(response, dto);
	}
}

