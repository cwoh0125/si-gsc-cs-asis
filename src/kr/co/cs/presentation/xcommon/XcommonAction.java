package kr.co.cs.presentation.xcommon;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
  




import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.business.xcommon.XcommonService;  

public class XcommonAction extends XbaseAction {

	private XcommonService xcommonservice = null;
	
	public XcommonService getXcommonservice() {
		return xcommonservice;
	}

	public void setXcommonservice(XcommonService xcommonservice) {
		this.xcommonservice = xcommonservice;
	}
	
	
	
	

	/*
	 * 데이터셋의 row state 상태를 가지고 트랜잭션을 일으킬 경우
	 */
	public void XcommonTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
		HttpServletResponse response) throws Exception {		
		
		Cookie[] cookies = request.getCookies();
		String jsession = "";
		
		
		if( cookies == null){
			jsession = "null";		
		}else{
			for(int i=0; i<cookies.length ; i++){
				Cookie thisCookies = cookies[i];				
				
				if(thisCookies.getName().equals("gsnt2_SESSION")){
				
					jsession = thisCookies.getValue();				
					
		
					  HttpSession session = request.getSession();	
					  String jsession1 = session.getId();
					//  System.out.println("jsession::"+jsession);
					//  System.out.println("jsession1:"+jsession1);
					  
					jsession1 = jsession1.substring(0,jsession.length());
					
						if(!jsession1.equals( jsession) ){							
							return;  //에러 처리
						}		
					}
				}
			}		
		
		
		XcommonDto dto = new XcommonDto();
		dto.setDsInit(request); 
		xcommonservice.XcommonTransaction(dto);
		super.write(response, dto);
	}

	/*
	 *데이터셋의 속성이 아니라 사용자가 트랜잭션을 지정하는 경우
	 */
	public void XcommonUserTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
		HttpServletResponse response) throws Exception {	
		
		Cookie[] cookies = request.getCookies();
		String jsession = "";
		
		
		if( cookies == null){
			jsession = "null";		
		}else{
			for(int i=0; i<cookies.length ; i++){
				Cookie thisCookies = cookies[i];				
				
				if(thisCookies.getName().equals("gsnt2_SESSION")){
				
					jsession = thisCookies.getValue();				
					
					HttpSession session = request.getSession();	
					  String jsession1 = session.getId();
					//  System.out.println("jsession::"+jsession);
					 // System.out.println("jsession1:"+jsession1);
					  
					jsession1 = jsession1.substring(0,jsession.length());
					
						if(!jsession1.equals( jsession) ){							
							return;  //에러 처리
						}		
				}
			}
		}		
		
		XcommonDto dto = new XcommonDto();
		dto.setDsInit(request); 
		xcommonservice.XcommonUserTransaction(dto);
		super.write(response, dto);
	}
	
	/*
	 * 롤백이 없는 트랜잭션
	 * */	
	public void XcommonNonTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		Cookie[] cookies = request.getCookies();
		String jsession = "";
		
		
		if( cookies == null){
			jsession = "null";		
		}else{
			for(int i=0; i<cookies.length ; i++){
				Cookie thisCookies = cookies[i];				
				
				if(thisCookies.getName().equals("gsnt2_SESSION")){
				
					jsession = thisCookies.getValue();				
					
					HttpSession session = request.getSession();	
					  String jsession1 = session.getId();
					 // System.out.println("jsession::"+jsession);
					//  System.out.println("jsession1:"+jsession1);
					  
					jsession1 = jsession1.substring(0,jsession.length());
					
						if(!jsession1.equals( jsession) ){							
							return;  //에러 처리
						}		
				}
			}
		}		
		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xcommonservice.XcommonNonTransaction(dto);
			super.write(response, dto, "XML");
		}
	
	public void XcommonServerEnv(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {		
		
		Cookie[] cookies = request.getCookies();
		String jsession = "";
		
		
		if( cookies == null){
			jsession = "null";		
		}else{
			for(int i=0; i<cookies.length ; i++){
				Cookie thisCookies = cookies[i];				
				
				if(thisCookies.getName().equals("gsnt2_SESSION")){
				
					jsession = thisCookies.getValue();				
					
					HttpSession session = request.getSession();	
					  String jsession1 = session.getId();
					  //System.out.println("jsession::"+jsession);
					  //System.out.println("jsession1:"+jsession1);
					  
					jsession1 = jsession1.substring(0,jsession.length());
					
						if(!jsession1.equals( jsession) ){							
							return;  //에러 처리
						}		
				}
			}
		}		
			XcommonDto dto = new XcommonDto();
			dto.setDsInit(request); 
			xcommonservice.XcommonServerEnv(dto);
			super.write(response, dto);
		}
	
}
