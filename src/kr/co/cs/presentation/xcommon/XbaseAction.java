package kr.co.cs.presentation.xcommon;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.cs.common.xdto.XcommonDto;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.tobesoft.xplatform.data.DataSetList;
import com.tobesoft.xplatform.data.PlatformData;
import com.tobesoft.xplatform.data.VariableList;
import com.tobesoft.xplatform.tx.HttpPlatformResponse;
import com.tobesoft.xplatform.tx.PlatformException;
import com.tobesoft.xplatform.tx.PlatformType;

/**
 * ***************************************************************************************** *
 * ***************************************************************************************** *
 */
public class XbaseAction extends DispatchAction {
	
	private final static Logger baselogger = LogManager.getLogger("process.etc");
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		try {
			ActionForward actionForward = super.execute(mapping, form, request, response);
			return actionForward;
		} catch (RuntimeException re) {			
						
			errorWrite(response, re.toString(), "RuntimeException");
			return null;
		} catch (SQLException se) {	
			
			errorWrite(response, se.toString(), "SQLException");
			return null;
		} catch(IOException ie) {				
			
			errorWrite(response, ie.toString(), "IOException");
			return null;			
		} catch (Exception ee) {
			
			errorWrite(response, ee.toString(), "Exception");
			return null;
		}
	}

	/*
	 * 클라이언트로 내려보낸다.
	 */
	public void write(HttpServletResponse response, XcommonDto dto) throws Exception {
		write(response, dto, "BIN");
		
		
	}
	
	public void write(HttpServletResponse response, XcommonDto dto, String TYPES) throws Exception {
		HttpPlatformResponse platformResponse;
		if("XML".equals(TYPES)) {
			platformResponse = new HttpPlatformResponse(response, PlatformType.CONTENT_TYPE_XML, dto.getClient_charset());
		} else {
			platformResponse = new HttpPlatformResponse(response, PlatformType.CONTENT_TYPE_BINARY, dto.getClient_charset());
		}

		PlatformData output = new PlatformData();
		
		output.setDataSetList(dto.getOutdslist());
		output.setVariableList(dto.getOutvlist());		
		platformResponse.setData(output);		
		platformResponse.sendData();
		
	}
	
	
	/*  
	 * 에러가 발생했을시 에러메시지만 내려보낸다.
	 */
	private void errorWrite(HttpServletResponse response, String msg, String msgtype) {
		try {
			HttpPlatformResponse platformResponse = new HttpPlatformResponse(response, PlatformType.CONTENT_TYPE_XML, "UTF-8"); //XML(압축방식)
			VariableList outvlist = new VariableList();
			DataSetList  outdslist = new DataSetList();
			outvlist.add("ErrorCode", "-2");
			outvlist.add("ErrorMsg", getErrorMsg(msg, msgtype));
			
			PlatformData output = new PlatformData();
			output.setDataSetList(outdslist);
			output.setVariableList(outvlist);
			
			platformResponse.setData(output);
			platformResponse.sendData();
			
		} catch(PlatformException e) { 
			baselogger.debug("Exception ::" + e.getMessage());
		} catch(Exception e) { 
			baselogger.debug("Exception ::" + e.getMessage());
		}
	} 
	
	private String getErrorMsg(String msg, String msgtype) {
 
		//System.out.println("["+ msgtype +"] " + msg);
		
//		if("RuntimeException".equals(msgtype)) {
//			msg = "[RuntimeException : 오류발생 "+ msg +"]";
//			
//		} else if("SQLException".equals(msgtype)) {
//			msg = "[SQLException : " + msg.substring(msg.lastIndexOf(":")+1,msg.length())+"]";
//			
//		} else if("RuntimeSQLException".equals(msgtype)) {
//			msg = "[RuntimeSQLException : "+ msg +"]";
//			
//		} else if("IOException".equals(msgtype)) {
//			msg = "[IOException : "+ msg +"]";
//
//		} else if("Exception".equals(msgtype)) {
//			msg = "[Exception : "+ msg +"]";
//		} else {
//			msg = "오류가 발생했습니다.";
//		}
		//baselogger.debug(msg);
		
		baselogger.debug("MSGTYPE:"+msgtype+":MSG:"+msg);
		
		return "시스템에러 : 업무처리를 하는 도중  오류가 발생했습니다. \n자세한내용을 알고싶으시면 아래내용을 관리자에게 보여주세요.\n\n" + msg;
	}
}