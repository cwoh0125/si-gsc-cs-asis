package kr.co.cs.presentation.xtest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.cs.business.xtest.XtestService;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.presentation.xcommon.XbaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;



public class XtestAction extends XbaseAction {
	
	private XtestService xtestservice = null;
	
	
	public XtestService getXtestservice() {
		return xtestservice;
	}


	public void setXtestservice(XtestService xtestservice) {
		this.xtestservice = xtestservice;
	}


	public void XtestTran(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xtestservice.XtestTran(dto);
			super.write(response, dto);
		}
	
	public void XtestTran2(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xtestservice.XtestTran2(dto);
			super.write(response, dto,"XML");
		}
	
}
