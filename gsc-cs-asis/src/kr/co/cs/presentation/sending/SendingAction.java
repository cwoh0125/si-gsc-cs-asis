package kr.co.cs.presentation.sending;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import kr.co.cs.business.sending.SendingService;
import kr.co.cs.business.xcommon.XcommonService;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.presentation.xcommon.XbaseAction;

public class SendingAction extends XbaseAction {
	private SendingService sendingservice = null;
	private XcommonService xcommonservice = null;

	public SendingService getSendingservice() {
		return sendingservice;
	}
	public void setSendingservice(SendingService sendingservice) {
		this.sendingservice = sendingservice;
	}
	public XcommonService getXcommonservice() {
		return xcommonservice;
	}
	public void setXcommonservice(XcommonService xcommonservice) {
		this.xcommonservice = xcommonservice;
	}
	
	public void CommonEmailTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			sendingservice.CommonEmailTransaction(dto, xcommonservice);
			super.write(response, dto);
		}
	
	public void BuyingEmailTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			sendingservice.BuyingEmailTransaction(dto, xcommonservice);
			super.write(response, dto);
		}
	
	public void CommonFaxTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			sendingservice.CommonFaxTransaction(dto, xcommonservice);
			super.write(response, dto);
		}
	
	public void FaxResendTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			sendingservice.FaxResendTransaction(dto, xcommonservice);
			super.write(response, dto);
		}
	

}
