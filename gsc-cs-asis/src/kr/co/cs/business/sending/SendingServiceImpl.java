package kr.co.cs.business.sending;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.tobesoft.xplatform.data.DataSet;
import com.tobesoft.xplatform.data.DataTypes;

import kr.co.cs.business.xcommon.XcommonService;
import kr.co.cs.common.config.Const;
import kr.co.cs.common.publicutil.ComUtil;
import kr.co.cs.common.publicutil.FileSystemUtil;
import kr.co.cs.common.publicutil.FtpUtil;
import kr.co.cs.common.publicutil.MailUtil;
import kr.co.cs.common.xdto.XcommonDto;
import kr.co.cs.persistence.dao.CommonDao;

public class SendingServiceImpl implements SendingService {
		
	private CommonDao commonDao = null;
	
	private final static Logger extlogger = LogManager.getLogger("process.ext");
	private final static Logger errlogger = LogManager.getLogger("process.etc");
	
	public void setCommonDao(CommonDao commonDao) {
		this.commonDao = commonDao;  
	} 
	
	private static String fileASCII = ".txt,.text,.htm,.html,.js,.java,.jsp,.asp,.log"; 
	
	/*   
	 * 공통
	 * */
	public void CommonEmailTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		 /* 
		  * 1 : CONTENTS (SUBJECT, BODY, TOMAILADDRESS, CCMAILADDRESS, BCCMAILADDRESS, user defined columns)
		  * 2 : FILELIST (LOCAL_FILENAME, SERVER_FILENAME, FILE_PATH) ->첨부할려고 업로드한 파일리스트
		  * 3 : TEMPLATELIST (VIEW_FILENAME, TEMPLATE_FILENAME) -> 서버에 저장되어있는 파일을 첨부로 보낼때
		  * 4 : MAKINGFILELIST (TEMPLATE_FILENAME, MAKING_FILENAME, INPUT_DATA)
		  * 5 : MAKINGBODY (TEMPLATE_FILENAME, INPUT_DATA)
		  */

		//메일 바디에 들어갈 내용
		this.setMakeBody(dto);

		// FILELIST 가 없으면 만든다..
		DataSet ds_filelist = dto.getDataSet("FILELIST");
		if(ds_filelist==null) {
			ds_filelist = new DataSet("FILELIST");
			ds_filelist.addColumn("SEQ_NO", DataTypes.STRING, 255);
			ds_filelist.addColumn("FILE_SEQ", DataTypes.STRING, 255);
			ds_filelist.addColumn("FILE_KIND_CD", DataTypes.STRING, 255);
			ds_filelist.addColumn("LOCAL_FILENAME", DataTypes.STRING, 5000);
			ds_filelist.addColumn("SERVER_FILENAME", DataTypes.STRING, 5000);
			ds_filelist.addColumn("FILE_PATH", DataTypes.STRING, 5000);

			//만든 데이터셋을 dto에 추가한다.
			dto.setAddExternalDataset(ds_filelist);
		}
		
 
		//파일에 내용을 넣어서 파일을 만들어서 첨부하는것.
		List fileInfoList = this.makeFile(dto);
		if(fileInfoList.size()>0) {			
			for(int i=0; i<fileInfoList.size(); i++) {
				String[] files = (String[])fileInfoList.get(i);
				int row = ds_filelist.newRow();
				ds_filelist.set(row, "FILE_SEQ", row+1);
				ds_filelist.set(row, "FILE_KIND_CD", "3"); //
				ds_filelist.set(row, "LOCAL_FILENAME", files[0]);
				ds_filelist.set(row, "FILE_PATH", files[1]);
				ds_filelist.set(row, "SERVER_FILENAME", files[2]);
			}
		}
		
		//템플릿
		DataSet ds_templatelist = dto.getDataSet("TEMPLATELIST");
		
		if(ds_templatelist!=null && ds_templatelist.getRowCount()>0) {
			
			String templateFilePath = null;
			if("WIN".equals(ComUtil.getOsMinName())) {
				templateFilePath = Const.FIXEDFILE_LOCALPATH;
			} else {
				templateFilePath = Const.FIXEDFILE_UNIXPATH;
			}
			
			for(int i=0; i<ds_templatelist.getRowCount(); i++) {
				int row = ds_filelist.newRow();
				ds_filelist.set(row, "FILE_SEQ", row+1);
				ds_filelist.set(row, "FILE_KIND_CD", "2"); 
				ds_filelist.set(row, "LOCAL_FILENAME", dto.dsToString(ds_templatelist.getObject(i, "VIEW_FILENAME")));
				ds_filelist.set(row, "FILE_PATH", templateFilePath);
				ds_filelist.set(row, "SERVER_FILENAME", dto.dsToString(ds_templatelist.getObject(i, "TEMPLATE_FILENAME")));
			}
		}
		
		//최종적으로 파일을 만든다...
		this.SendingEmail(dto);			

		//디비작업(조회용)
		xcommonservice.XcommonUserTransaction(dto); 
	}
	
	/*
	 * 구매이력이메일
	 * */
	public void BuyingEmailTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		/*
		 * DataSet Name and Fields
		 * 1 : CONTENTS (SUBJECT, BODY, TOMAILADDRESS, CCMAILADDRESS, BCCMAILADDRESS user defined column...)
		 * 2 : MAKINGFILELIST (user defined column...)
		 * 5 : MAKINGBODY (TEMPLATE_FILENAME, INPUT_DATA)
		 * */
 
		//메일 바디에 들어갈 내용
		this.setMakeBody(dto);
		
		//구매이력파일
		String[] files = this.buyListMakeFile(dto);
 
		DataSet ds_filelist = dto.getDataSet("FILELIST");
		if(ds_filelist==null) {
			ds_filelist = new DataSet("FILELIST");
			ds_filelist.addColumn("SEQ_NO", DataTypes.STRING, 255);
			ds_filelist.addColumn("FILE_SEQ", DataTypes.STRING, 255);
			ds_filelist.addColumn("FILE_KIND_CD", DataTypes.STRING, 255);
			ds_filelist.addColumn("LOCAL_FILENAME", DataTypes.STRING, 5000);
			ds_filelist.addColumn("SERVER_FILENAME", DataTypes.STRING, 5000);
			ds_filelist.addColumn("FILE_PATH", DataTypes.STRING, 5000);

			//만든 데이터셋을 dto에 추가한다.
			dto.setAddExternalDataset(ds_filelist);
		}
		
		int row = ds_filelist.newRow();
		ds_filelist.set(row, "FILE_SEQ", row+1);
		ds_filelist.set(row, "FILE_KIND_CD", "4"); //<--구매이력
		ds_filelist.set(row, "LOCAL_FILENAME", files[0]);
		ds_filelist.set(row, "FILE_PATH", files[1]);
		ds_filelist.set(row, "SERVER_FILENAME", files[2]);
		
		//메일을 보낸다..
		this.SendingEmail(dto);	

		//디비작업(조회용)
		xcommonservice.XcommonUserTransaction(dto);
	}
	
	/*   
	 * 공통fax
	 * */
	public void CommonFaxTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		
		this.SendingFax(dto);
		
		//디비작업(조회용)
		xcommonservice.XcommonUserTransaction(dto);
		
		
	}

	
	/*   
	 * 공통fax
	 * */
	public void FaxResendTransaction(XcommonDto dto, XcommonService xcommonservice) throws Exception {
		/*
		 * FAXREND(SEQ_NO, FAX_ID, FAX_NUMBER, FORM_TYPE)
		 * */
		String seq_no = "";
		String faxno = "";
		
		//재발송 카운드 업데이트
		DataSet ds_resend = dto.getDataSet("FAXREND");		
		if(ds_resend != null) {
			commonDao.update("Common.Set_FaxResend", dto.getRowMap(ds_resend, 0));
			
			seq_no = dto.dsToString(ds_resend.getObject(0, "SEQ_NO"));
			faxno = dto.dsToString(ds_resend.getObject(0, "FAX_NUMBER"));
		}

		String dest_url = Const.fax_resend_url;
		System.out.println(seq_no+"▦"+faxno);
		sendXml(seq_no+"▦"+faxno, dest_url);
		
		
		//팩스디비 재발송 저장
//		this.faxDBResendSave(dto);

		//디비작업(조회용)
		xcommonservice.XcommonUserTransaction(dto);
	}

	
	
	/*
	 * 최종메일 보내기...
	 * */
	private void SendingEmail(XcommonDto dto) throws Exception {		
		/*
		 * DataSet Name and Fields
		 * 1 : CONTENTS (SUBJECT, BODY, TOMAILADDRESS, CCMAILADDRESS, BCCMAILADDRESS, user defined columns)
		 * 2 : FILELIST (LOCAL_FILENAME, SERVER_FILENAME, FILE_PATH) ->첨부할려고 업로드한 파일리스트
		 * 3 : TEMPLATELIST (VIEW_FILENAME, TEMPLATE_FILENAME) -> 서버에 저장되어있는 파일을 첨부로 보낼때
		 * 5 : MAKINGBODY
		 * */

		DataSet ds_contents = dto.getDataSet("CONTENTS");
		DataSet ds_filelist = dto.getDataSet("FILELIST");
		//DataSet ds_templatelist = dto.getDataSet("TEMPLATELIST");
		DataSet ds_mailbody = dto.getDataSet("MAKINGBODY");
		
		//디비먼저 타고..
		String seq_qid = "Common.Get_SEQ_SELECT";  //==>시퀀스채번
		String contents_qid = "Common.Set_TblEmlSndgList_INSERT";
		String filelist_qid = "Common.Set_TblApndFileList_INSERT";
		//String templatelist_qid = "Common.Set_TblEmlTmplApndList_INSERT";
		String mailbody_qid = "Common.Set_TblEmlBodyApndList_INSERT";
		
		String email_seq = commonDao.selectString(seq_qid, dto.getRowMap(null, 0));		

		this.dataInsert(dto, ds_contents, contents_qid, email_seq);
		if(ds_filelist!=null)
			this.dataInsert(dto, ds_filelist, filelist_qid, email_seq);
		//if(ds_templatelist!=null)
		//	this.dataInsert(dto, ds_templatelist, templatelist_qid, email_seq);
		if(ds_mailbody!=null)
			this.dataInsert(dto, ds_mailbody, mailbody_qid, email_seq);
		
		
		String subject = dto.dsToString(ds_contents.getObject(0, "SUBJECT"));
		String body = dto.dsToString(ds_contents.getObject(0, "BODY"));
		String toMailAddress = dto.dsToString(ds_contents.getObject(0, "TOMAILADDRESS"));
		String ccMailAddress = dto.dsToString(ds_contents.getObject(0, "CCMAILADDRESS"));
		String bccMailAddress = dto.dsToString(ds_contents.getObject(0, "BCCMAILADDRESS"));	
		
		List flist = new ArrayList();
		String[] fname = null;		
		
		if(ds_filelist!=null) {
			for(int i=0; i<ds_filelist.getRowCount(); i++) {
				fname = new String[3];
				fname[0] = dto.dsToString(ds_filelist.getObject(i, "LOCAL_FILENAME"));
				fname[1] = dto.dsToString(ds_filelist.getObject(i, "FILE_PATH"));
				fname[2] = dto.dsToString(ds_filelist.getObject(i, "SERVER_FILENAME"));
				flist.add(fname);
			}
		}
		
		MailUtil mu = new MailUtil();
		mu.sendMail(subject, body, dto.dsToString(toMailAddress).split(";"), dto.dsToString(ccMailAddress).split(";"), dto.dsToString(bccMailAddress).split(";"), flist);
	}
	
	/*
	 * 최종FAX 보내기...
	 * */
	private void SendingFax(XcommonDto dto) throws Exception {		
	 /* 
		  * 1 : FAXCONTENTS (SEQ_NO, USER_ID, INPUT_COVER, INPUT_DATA, FORM_ID, FORM_TYPE, FAX_NUMBER, RES_FAX_ID, RES_MSG_CD, RES_MSG_VALUE, user defined columns) fax서버에 저장되어있는 TIF 파일을  보낼때 FORM_TYPE=1, fod형식으로 보낼때 FORM_TYPE=2
		  * 2 : FILELIST (LOCAL_FILENAME, SERVER_FILENAME, FILE_PATH) ->첨부할려고 업로드한 파일리스트
	  */
		extlogger.debug("========> FAX 전송 시작");

		DataSet ds_contents = dto.getDataSet("FAXCONTENTS");
		DataSet ds_filelist = dto.getDataSet("FILELIST");
		
//		String fileJoin = ""; //첨부파일 묶은것 abc.txt | ggg.txt
//		//디비먼저 타고.. fax로 고쳐야 한다.
		
		String seq_qid = "Common.Get_SEQ_SELECT";  //==>시퀀스채번
		String contents_qid = "Common.Set_TblFaxlogList_INSERT";
		String fax_seq = commonDao.selectString(seq_qid, dto.getRowMap(null, 0));		
		String fax_number =  dto.dsToString(ds_contents.getObject(0, "FAX_NUMBER"));
		this.dataInsert(dto, ds_contents, contents_qid, fax_seq);

		/// ds_contents의 팩스 종류 확인 후 if문으로 converter 정의
		String form_id = dto.dsToString(ds_contents.getObject(0, "FORM_ID"));
		String result = "";
		String dest_url = "";
		if("3".equals(form_id)){
			result = xmlConverterParcel(dto.dsToString(ds_contents.getObject(0, "INPUT_DATA")), fax_seq, fax_number);
			dest_url = Const.parcel_dest_url;
			sendXml(result, dest_url);
		}else if("4".equals(form_id)){
			result = xmlConverter(dto.dsToString(ds_contents.getObject(0, "INPUT_DATA")), fax_seq, fax_number, dto.dsToString(ds_contents.getObject(0, "CUST_NM")));
			dest_url = Const.gas_fillup_url;
			sendXml(result, dest_url);
			
		}else if(("10".equals(form_id))||("11".equals(form_id))||("12".equals(form_id))||("15".equals(form_id))){
			
			result = "<result><data><form_id>"+form_id+"</form_id><fax_number>"+fax_number+"</fax_number><fax_seq>"+fax_seq+"</fax_seq><cust_nm>"+dto.dsToString(ds_contents.getObject(0, "CUST_NM"))+"</cust_nm></data></result>";
			dest_url = Const.app_fixed_forms_url;
			sendXml(result, dest_url);
		}else{
			FtpUtil ftp = new FtpUtil();
			String ret = null;
			
			String fileformat = "";
			String sendmode = "BINARY";
			
			String localName = null;
			String filePath = null;
			String remoteName = null;
//			파일이 있으면 ftp로 먼저 전송		
			if(ds_filelist!=null && ds_filelist.getRowCount()>0) {
				
				
				for(int i=0; i<ds_filelist.getRowCount(); i++) {
					localName = dto.dsToString(ds_filelist.getObject(i, "LOCAL_FILENAME"));
					filePath = dto.dsToString(ds_filelist.getObject(i, "FILE_PATH"));
					remoteName = dto.dsToString(ds_filelist.getObject(i, "SERVER_FILENAME"));
					System.out.println(localName);
					System.out.println(filePath);
					System.out.println(remoteName);
					
					
					try {
						fileformat = localName.substring(localName.lastIndexOf("."), localName.length());
						sendmode = fileASCII.indexOf(fileformat)>-1 ? "ASCII" : "BINARY";
					} catch(Exception e) {
						e.printStackTrace();
						errlogger.debug("File Format Exception !!");
					}
					
//					fileJoin += remoteName + fileformat + "|";
					
					if(ComUtil.isProd())
						ret = ftp.ftpPut(Const.FAX_FTP_HOST, Const.FAX_FTP_PORT, Const.FAX_FTP_ACCOUNT, Const.FAX_FTP_PASSWORD, filePath, "", remoteName, remoteName+fileformat, sendmode);
					else
						ret = ftp.ftpPut(Const.TEST_FAX_FTP_HOST, Const.TEST_FAX_FTP_PORT, Const.TEST_FAX_FTP_ACCOUNT, Const.TEST_FAX_FTP_PASSWORD, filePath, "", remoteName, remoteName+fileformat, sendmode);
					if(ret.indexOf("FAIL")>-1) 
						errlogger.debug("FAX 서버로 파일전송을 실패했습니다. ["+ret+"]");
						throw new RuntimeException("FAX 서버로 파일전송을 실패했습니다. ["+ret+"]");
				}
			}
			result = "<result><data><form_id>"+form_id+"</form_id><file_name>"+remoteName+fileformat+"</file_name><fax_number>"+fax_number+"</fax_number><fax_seq>"+fax_seq+"</fax_seq><cust_nm>"+dto.dsToString(ds_contents.getObject(0, "CUST_NM"))+"</cust_nm></data></result>";
			dest_url = Const.fileMod_url;
			sendXml(result, dest_url);
			
		}
		
		
//		
		//파일이 있으면 ftp로 먼저 전송		
//		if(ds_filelist!=null && ds_filelist.getRowCount()>0) {
//			
//			FtpUtil ftp = new FtpUtil();
//			String ret = null;
//			
//			String fileformat = "";
//			String sendmode = "BINARY";
//			
//			String localName = null;
//			String filePath = null;
//			String remoteName = null;
//			
//			for(int i=0; i<ds_filelist.getRowCount(); i++) {
//				localName = dto.dsToString(ds_filelist.getObject(i, "LOCAL_FILENAME"));
//				filePath = dto.dsToString(ds_filelist.getObject(i, "FILE_PATH"));
//				remoteName = dto.dsToString(ds_filelist.getObject(i, "SERVER_FILENAME"));
//				
//				try {
//					fileformat = localName.substring(localName.lastIndexOf("."), localName.length());
//					sendmode = fileASCII.indexOf(fileformat)>-1 ? "ASCII" : "BINARY";
//				} catch(Exception e) {}
//				
//				fileJoin += remoteName + fileformat + "|";
//				
//				if(ComUtil.isProd())
//					ret = ftp.ftpPut(Const.FAX_FTP_HOST, Const.FAX_FTP_PORT, Const.FAX_FTP_ACCOUNT, Const.FAX_FTP_PASSWORD, filePath, "", remoteName, remoteName+fileformat, sendmode);
//				else
//					ret = ftp.ftpPut(Const.TEST_FAX_FTP_HOST, Const.TEST_FAX_FTP_PORT, Const.TEST_FAX_FTP_ACCOUNT, Const.TEST_FAX_FTP_PASSWORD, filePath, "", remoteName, remoteName+fileformat, sendmode);
//				
//				if(ret.indexOf("FAIL")>-1) 
//					throw new RuntimeException("FAX 서버로 파일전송을 실패했습니다. ["+ret+"]");
//			}
//		}
//		
  
		//fax서버로 디비 저장한다.		
//		this.faxDBSave(dto, fax_seq, fileJoin);
		
	}
	
	
	/*
	 * 템플릿을 변경시켜서 만들기 위한 파일들...
	 * */
	private List makeFile(XcommonDto dto) throws Exception {
		 // 4 : MAKINGFILELIST (TEMPLATE_FILENAME, MAKING_FILENAME, INPUT_DATA)
		DataSet mds = dto.getDataSet("MAKINGFILELIST");
		
		List fileList = new ArrayList();

		if(mds!=null && mds.getRowCount()>0) {
			
			String template_file = null;
			String making_file = null;
			String templateCode = "";
			String[] inputdata = null;
			String[] retFileInfo = null;
			String[] makeFileInfo = null;
			
			String templateFilePath = null;
			if("WIN".equals(ComUtil.getOsMinName())) {
				templateFilePath = Const.FIXEDFILE_LOCALPATH;
			} else {
				templateFilePath = Const.FIXEDFILE_UNIXPATH;
			}
		
			StringBuffer msb = new StringBuffer(100);
			for(int i=0; i<mds.getRowCount(); i++) {
				template_file = dto.dsToString(mds.getObject(i, "TEMPLATE_FILENAME"));
				making_file = dto.dsToString(mds.getObject(i, "MAKING_FILENAME"));
				inputdata = dto.dsToString(mds.getObject(i, "INPUT_DATA")).split("[|]");
				
				templateCode = FileSystemUtil.getByteFileContents(templateFilePath + template_file);
				
				for(int k=0; k<inputdata.length; k++) {
					msb.delete(0, msb.length());
					msb.append("$");
					msb.append(String.valueOf(k+1));
					msb.append("$");
					templateCode = templateCode.replace(msb.toString(), inputdata[k]);
				}
				for(int k=inputdata.length;k<10000;k++) {
					msb.delete(0, msb.length());
					msb.append("$");
					msb.append(String.valueOf(k+1));
					msb.append("$");
					if(templateCode.indexOf(msb.toString())==-1)
						break;
					templateCode = templateCode.replace(msb.toString(), " ");
				}
	
				retFileInfo = FileSystemUtil.makeFileContents(templateCode, Const.KOREA_CHARSET);
				makeFileInfo = new String[3];
				makeFileInfo[0] = making_file; //LOCAL_FILENAME
				makeFileInfo[1] = retFileInfo[0]; //FILE_PATH
				makeFileInfo[2] = retFileInfo[1]; //SERVER_FILENAME
				fileList.add(makeFileInfo);
			}
		}
		
		return fileList;
	}
	
	/*
	 * 템플릿을 변경시켜서 만들기 바디에 담을 내용
	 * */
	private void setMakeBody(XcommonDto dto) throws Exception {
		 // 5 : MAKINGBODY (TEMPLATE_FILENAME, INPUT_DATA)
		
		DataSet mds = dto.getDataSet("MAKINGBODY");

		if(mds!=null && mds.getRowCount()>0) {
		
			String template_file = null;
			String templateCode = "";
			String[] inputdata = null;
			
			String savedFilePath = null;
			String templateFilePath = null;
			if("WIN".equals(ComUtil.getOsMinName())) {
				templateFilePath = Const.FIXEDFILE_LOCALPATH;
			} else {
				templateFilePath = Const.FIXEDFILE_UNIXPATH;
			}
		
			template_file = dto.dsToString(mds.getObject(0, "TEMPLATE_FILENAME"));
			
			templateCode = FileSystemUtil.getByteFileContents(templateFilePath + template_file);
			//templateCode = FileSystemUtil.getFileContents(templateFilePath + template_file);
			inputdata = dto.dsToString(mds.getObject(0, "INPUT_DATA")).split("[|]");
			
			StringBuffer msb = new StringBuffer(100);
			for(int k=0; k<inputdata.length; k++) {
				msb.delete(0, msb.length());
				msb.append("$");
				msb.append(String.valueOf(k+1));
				msb.append("$");
				templateCode = templateCode.replace(msb.toString(), inputdata[k]);
			}
			for(int k=inputdata.length;k<10000;k++) {
				msb.delete(0, msb.length());
				msb.append("$");
				msb.append(String.valueOf(k+1));
				msb.append("$");
				if(templateCode.indexOf(msb.toString())==-1)
					break;
				templateCode = templateCode.replace(msb.toString(), " ");
			}
			dto.getDataSet("CONTENTS").set(0, "BODY", templateCode);
			dto.getDataSet("CONTENTS").set(0, "BODY_CTT", templateCode); 
		} 
	}
	
	/*
	 * 구매이력 첨부파일을 보내기 위한 파일만들기..
	 * 컬럼은 정의되면 다시 수정하기 바람..
	 * */
	private String[] buyListMakeFile(XcommonDto dto) throws Exception {
		 // 4 : MAKINGFILELIST (TEMPLATE_FILENAME, MAKING_FILENAME, user defined column...)

		DataSet ds_contents = dto.getDataSet("CONTENTS");
		DataSet mds = dto.getDataSet("MAKINGFILELIST");
		HashMap map = null;
		
		StringBuffer sb = new StringBuffer(100);
		//★TODO 여기를 수정하면됨 
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title></title>");
        sb.append("<meta http-equiv='content-type' content='text/html; charset=euc-kr' />");
        sb.append("<style type='text/css'>");
        sb.append("#Tempbox {");
        sb.append(" width: 1090px;");
        sb.append(" height: 647px;");
        sb.append("}");
        sb.append("");
        sb.append("table.sHeadListTbl {");
        sb.append(" background: #ffffff;");
        sb.append(" border-collapse: collapse;");
        sb.append(" font-family: dotum, gulim;");
        sb.append(" font-size: 11px;");
        sb.append("}");
        sb.append("");
        sb.append("table.sHeadListTbl th {");
        sb.append(" background: #f3f8fd;");
        sb.append(" padding: 0 3;");
        sb.append(" font-weight:;");
        sb.append(" color: #467ebe;");
        sb.append(" height: 25px;");
        sb.append(" font-size: 12px;");
        sb.append("}");
        sb.append("");
        sb.append("table.sHeadListTbl td {");
        sb.append(" padding: 3 5;");
        sb.append(" text-align: center;");
        sb.append(" height: 25px;");
        sb.append("}");
        sb.append("");
        sb.append(".txtBblue {");
        sb.append(" color: #1d70a0;");
        sb.append(" font-weight: bold;");
        sb.append(" font-size: 12px;");
        sb.append(" height: 18px;");
        sb.append(" font-family: dotum, gulim;");
        sb.append("}");
        sb.append("");
        sb.append("div {");
        sb.append(" font-size: 12px;");
        sb.append(" color: #333333;");
        sb.append(" font-family: dotum, gulim;");
        sb.append("}");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body scroll='yes' style='margin: 3px;'>");
        sb.append(" <div>");
        sb.append("     <div align='center'>");
        sb.append("         <img align='middle' alt='칼텍스 이미지'");
        sb.append("             src='http://kmug.co.kr/board/data/illust/gs_%c4%ae%c5%d8%bd%ba.jpg'");
        sb.append("             height='100'>");
        sb.append("     </div>");
        sb.append("     <hr>");
        sb.append("     <h1 align='center'>");
        sb.append("         <b>Mail Service</b>");
        sb.append("         </h1>");
        sb.append("         <hr>");
        sb.append("         <table align='center' width='900'>");
        sb.append("             <colgroup>");
        sb.append("                 <col width='105px'>");
        sb.append("                 </col>");
        sb.append("                 <col></col>");
        sb.append("             </colgroup>");
        sb.append("             <tr>");
        sb.append("                 <td style='letter-spacing: 15px; display: inline-block;height:60px'>제 목:</td>");
        sb.append("                 <td>보너스카드 주유내역서(구매이력)</td>");
        sb.append("             </tr>");
        sb.append("             <tr>");
        sb.append("                 <td style='letter-spacing: 15px; display: inline-block;height:60px'>수 신:</td>");
        sb.append("     <td>"+dto.dsToString(ds_contents.getObject(0, "CUST_NM"))+"&nbsp; 귀사(하)</td>");
        sb.append("             </tr>");
        sb.append("             <tr>");
        sb.append("                 <td style='letter-spacing: 15px; display: inline-block;height:60px'>발 신:</td>");
        sb.append("                 <td>GS칼텍스 고객서비스센터</td>");
        sb.append("             </tr>");
        sb.append("         </table>");
        sb.append("         <hr style='border: 0; border-top: 3px double #8c8c8c;;height:20px'>");
        sb.append("         <table align='center' width='900'>");
        sb.append("             <tr>");
        sb.append("                 <td>귀사(하)의 무궁한 발전을 축원 하오며, 평소 저희 GS칼텍스에 대한 따뜻한 관심과 협조에 깊이");
        sb.append("                     감사드립니다.</td>");
        sb.append("             </tr>");
        sb.append("             <tr>");
        sb.append("                 <td><br>귀사(하)께서 요청하신 문서를 다음과 같이 발송하오니 문의사항이 있으시면 당");
        sb.append("                     고객서비스센터로 연락 주시기 바랍니다.</td>");
        sb.append("             </tr>");
        sb.append("         </table>");
        sb.append("         <table><tr><td style='width:10px;height:600px'></td></tr></table>");
        sb.append("         <br>");
        sb.append("         <table width='100%' style='text-align:right'>");
        sb.append("             <tr>");
        sb.append("                 <td><table align='right' style='width: 400px;'>");
        sb.append("                         <colgroup>");
        sb.append("                             <col style='width: 5%'>");
        sb.append("                             <col style='width: 95%'>");
        sb.append("                         </colgroup>");
        sb.append("                         <tr>");
        sb.append("                             <td colspan='2'>&lt;GS칼텍스 고객서비스센터&gt;</td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td colspan='2'>08589</td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td colspan='2'>");
        sb.append("                                 서울시 금천구 디지털로 130 (가산동)");
        sb.append("                             </td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td colspan='2'>");
        sb.append("                                 남성프라자(에이스9차) 2층             ");
        sb.append("                             </td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td valign='top' style='width: 5%'>TEL</td>");
        sb.append("                             <td style='width: 95%'>1544-5151 (보너스 카드) </td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td valign='top' style='width: 5%'></td>");
        sb.append("                             <td style='width: 95%'>1544-5100 (주문)");
        sb.append("                             </td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td valign='top' style='width: 5%'></td>");
        sb.append("                             <td style='width: 95%'>02-6714-2081 (법인 보너스 카드)");
        sb.append("                             </td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td valign='top'>FAX</td>");
        sb.append("                             <td>02-6714-2090(보너스카드)</td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td valign='top'></td>");
        sb.append("                             <td> 02-2105-8969(주문)</td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td valign='top'></td>");
        sb.append("                             <td>02-6714-2091(법인 보너스 카드)</td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td colspan='2'>http://www.gscaltex.co.kr </td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td colspan='2'>http://www.kixx.co.kr</td>");
        sb.append("                         </tr>");
        sb.append("                         <tr>");
        sb.append("                             <td colspan='2'>http://www.gsnpoint.com</td>");
        sb.append("                         </tr>");
        sb.append("                     </table></td>");
        sb.append("             </tr>");
        sb.append("         </table>");
        sb.append(" </div>");
        sb.append(" <table><tr><td style='width:10px;height:280px'></td></tr></table>");
        
        sb.append("<div style='height: 1px;width:100%;border: dotted 1px gray;'></div>");		
		
		sb.append("    <div id='Tempbox' style='margin: 0px auto;'>");
		sb.append("        <div class='box_m' style='display: inline-block;'>");
		sb.append("            <div style='width:1050px;margin-left:5px;'>");
		sb.append("                <table border='0' cellspacing='10' cellpadding='10' width='100%'>");
		sb.append("					<tr><td align=center style='font-size:24px;font-family:dotum,gulim;'>보너스카드 주유 내역서</td></tr>");
		sb.append("				</table>");
		sb.append("			</div>");
		sb.append("        	<div style='position:absolute;margin-left:900px'>Total 적립포인트 : <span>"+dto.dsToString(ds_contents.getObject(0, "TOT_PT"))+"</span></div>");
		sb.append("            <div class='txtBblue' style='width:1050px;margin-left:5px;'>고객명 : <span>"+ dto.dsToString(ds_contents.getObject(0, "CUST_NM")) +"</span></div>");
		sb.append("            <div style='background-color:#ffffff;border-top:3px solid #5e99d3;border-bottom:1px solid #5e99d3;width:1050px;margin:0px;'>");
		sb.append("                <table border='1' cellspacing='0' cellpadding='0' bordercolor='#b6cde4' class='sHeadListTbl' width='100%'>");
		sb.append("                  <colgroup>");
		sb.append("                    <col width='120px'>");
		sb.append("                    <col width='130px'>");
		sb.append("                    <col width='130px'>");
		sb.append("                    <col width='80px'>");
		sb.append("                    <col width='130px'>");
		sb.append("                    <col width='70px'>");
		sb.append("                    <col width='75px'>");
		sb.append("                    <col width='65px'>");
		sb.append("                    <col width='50px'>");
		sb.append("                    <col width='45px'>");
		sb.append("                    <col width=''>");
		sb.append("                  </colgroup>");
		sb.append("                   <tbody>");
		sb.append("                    <tr>");
		sb.append("                      <th>처리일시</th>");
		sb.append("                      <th>카드번호</th>");
		sb.append("                      <th>채널</th>");
		sb.append("                      <th>상세구분</th>");
		sb.append("                      <th>가맹점</th>");
		sb.append("                      <th>유종</th>");
		sb.append("                      <th>금액</th>");
		sb.append("                      <th>단가</th>");
		sb.append("                      <th>주유량</th>");
		sb.append("                      <th>포인트</th>");
		sb.append("                      <th>결제유형</th>");
		sb.append("                    </tr>");

		for(int i=0; i<mds.getRowCount(); i++) {			
			sb.append("<tr>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "SALE_DTIME_UI")));     sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "CRD_NO")));            sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "CHNL_DIV_NM")));       sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "RSV_USE_DIV_NM")));     sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "FRCH_NM")));           sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "PROD_CD_NM")));        sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "AMT")));               sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "UPRC")));              sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "QTY")));               sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "BAL_PT")));           sb.append("</td>");
			sb.append("<td>");   sb.append(dto.dsToString(mds.getObject(i, "PAYM_TP_NM")));        sb.append("</td>");
			sb.append("</tr>");
		}

		sb.append("                  </tbody>");
		sb.append("              </table>"); 
		sb.append("            </div>");
		sb.append("        </div>");
		sb.append("        <div class='box_b'></div>");
		sb.append("    </div>");
		sb.append("  </body>");
		sb.append("</html>");
		
		String[] retFileInfo = FileSystemUtil.makeFileContents(sb.toString(), Const.KOREA_CHARSET);
		String[] makeFileInfo = new String[3];
		makeFileInfo[0] = "보너스카드주유내역서.html"; //LOCAL_FILENAME
		makeFileInfo[1] = retFileInfo[0]; //FILE_PATH
		makeFileInfo[2] = retFileInfo[1]; //SERVER_FILENAME
		
		return makeFileInfo;
	}
	
	/*
	 * faxserver connect
	 * */
	private void faxDBSave(XcommonDto dto, String fax_seq, String fileJoin) throws Exception {
		 /* 
		  * 1 : FAXCONTENTS (SEQ_NO, USER_ID, INPUT_COVER, INPUT_DATA, FORM_ID, FORM_TYPE, FAX_NUMBER, RES_FAX_ID, RES_MSG_CD, RES_MSG_VALUE, user defined columns) fax서버에 저장되어있는 TIF 파일을  보낼때 FORM_TYPE=1, fod형식으로 보낼때 FORM_TYPE=2
		  * 2 : FILELIST (LOCAL_FILENAME, SERVER_FILENAME, FILE_PATH) ->첨부할려고 업로드한 파일리스트
		  */ 
		DataSet ds_contents = dto.getDataSet("FAXCONTENTS");
		String ft = dto.dsToString(ds_contents.getObject(0, "FORM_TYPE"));
	
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		Connection faxconn = null;
		PreparedStatement ps = null;
		
		try {
			if(ComUtil.isProd())
				faxconn = DriverManager.getConnection(Const.FAX_DB_URL, Const.FAX_DB_ACCOUNT, Const.FAX_DB_PASSWORD);
			else
				faxconn = DriverManager.getConnection(Const.TEST_FAX_DB_URL, Const.TEST_FAX_DB_ACCOUNT, Const.TEST_FAX_DB_PASSWORD);
			
			StringBuffer sb = new StringBuffer(100);	
			String formId = (String)ds_contents.getObject(0, "FORM_ID");
			
			if("1".equals(ft)) { //tif용
				sb.append(" INSERT INTO REQUEST_DATA (");
				sb.append("  user_id         ");
				sb.append(" ,filepage       ");
				sb.append(" ,filename       ");
				sb.append(" ,faxnumber_count ");
				sb.append(" ,request_date    ");
				sb.append(" ,body      ");
				sb.append(" ,cover_value       ");
				sb.append(" ,flag            ");
				sb.append(" ,priority        ");
				sb.append(" ,form_id         ");
				sb.append(" ,customer_id     ");
				sb.append(" ,fod_id     ");
			    sb.append(" )VALUES(");
			    sb.append(" ? ");
			    sb.append(" ,'1' ");
			    sb.append(" ,? ");
			    sb.append(" ,1 ");
			    sb.append(" ,convert(varchar(8), getdate(), 112)    ");
			    sb.append(" ,? ");
			    sb.append(" ,? ");
			    sb.append(" ,'00'  ");
			    sb.append(" ,0 ");
			    sb.append(" ,? ");
			    sb.append(" ,? ");
			    
			    if (formId == null || formId.equals("")) {
			    	sb.append(" ,'8' ");
			    }
			    else {
			    	sb.append(" ,'1' ");
			    }
			    
			    sb.append(" )");
			    
			    ps = faxconn.prepareStatement(sb.toString());
			    ps.setString(1, dto.dsToString(ds_contents.getObject(0, "USER_ID")));
			    ps.setString(2, fileJoin);
			    ps.setString(3, dto.dsToString(ds_contents.getObject(0, "FAX_NUMBER"))+"/" + fax_seq + "/1|");
			    ps.setString(4, dto.dsToString(ds_contents.getObject(0, "INPUT_COVER")));
			    ps.setString(5, dto.dsToString(formId));
			    ps.setString(6, fax_seq); 
			} else {
				sb.append(" INSERT INTO FOD_ROW_Data (");
				sb.append("  site_code     ");
				sb.append(" ,fax_number    ");
				sb.append(" ,user_id       ");
				sb.append(" ,FOD_ID        ");
				sb.append(" ,customer_id   ");
				sb.append(" ,request_date  ");
				sb.append(" ,request_time  ");
				sb.append(" ,cover_id      ");
				sb.append(" ,process_tag   ");
				sb.append(" ,fax_value     ");
			    sb.append(" )VALUES(");
			    sb.append("  1     ");
			    sb.append(" ,?    "); //fax_number
			    sb.append(" ,?    "); //user_id
			    sb.append(" ,?    "); //fod_id
			    sb.append(" ,?    "); //customer_id
			    sb.append(" ,convert(varchar(8), getdate(), 112)  ");
			    sb.append(" ,replace(convert(varchar(8), getdate(), 108),':','')  ");
			    sb.append(" ,'1'   ");
			    sb.append(" ,0   ");
			    sb.append(" ,?   ");  //fax_value
			    sb.append(" )");
			    
			    ps = faxconn.prepareStatement(sb.toString());
			    ps.setString(1, dto.dsToString(ds_contents.getObject(0, "FAX_NUMBER")));
			    ps.setString(2, dto.dsToString(ds_contents.getObject(0, "USER_ID")));
			    ps.setString(3, dto.dsToString(ds_contents.getObject(0, "FORM_ID")));
			    ps.setString(4, fax_seq);
			    ps.setString(5, dto.dsToString(ds_contents.getObject(0, "INPUT_COVER")) + "#" + dto.dsToString(ds_contents.getObject(0, "INPUT_DATA")));
			}
			
			ps.executeUpdate();
		    ps.close();
		} catch (Exception e) {
			//System.out.println("팩스디비 저장중 오류 " + e.toString());
			throw new RuntimeException("팩스디비 저장중 오류  : " + e);
  
		} finally {
			try { if(ps !=null) ps.close(); ps=null; } catch(Exception e){};
			try { if(faxconn !=null) faxconn.close(); faxconn=null; } catch(Exception e){};	
		}
	}
	
	//팩스디비 재발송 업데이트
	private void faxDBResendSave(XcommonDto dto) throws Exception {

		/*
		 * FAXREND(SEQ_NO, FAX_ID, FAX_NUMBER, FORM_TYPE)
		 * */
		
		//팩스디비 업데이트
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		Connection faxconn = null;
		PreparedStatement ps = null;
		
		try {
			if(ComUtil.isProd())
				faxconn = DriverManager.getConnection(Const.FAX_DB_URL, Const.FAX_DB_ACCOUNT, Const.FAX_DB_PASSWORD);
			else
				faxconn = DriverManager.getConnection(Const.TEST_FAX_DB_URL, Const.TEST_FAX_DB_ACCOUNT, Const.TEST_FAX_DB_PASSWORD);
			
			StringBuffer sb = new StringBuffer(100);

			DataSet ds_resend = dto.getDataSet("FAXREND");		
			String ft = dto.dsToString(ds_resend.getObject(0, "FORM_TYPE"));
		
			if("1".equals(ft)) { //tif용
				sb.append(" UPDATE REQUEST_DATA SET ");
				sb.append(" body = ?  ");
				sb.append(" ,fax_id = ?  ");
				sb.append(" ,flag = '00'  ");
				sb.append(" where customer_id = ?    ");
			    
			    ps = faxconn.prepareStatement(sb.toString());
			    ps.setString(1, dto.dsToString(ds_resend.getObject(0, "FAX_NUMBER"))+"/"+ dto.dsToString(ds_resend.getObject(0, "SEQ_NO")) +"/1|");
			    ps.setString(2, dto.dsToString(ds_resend.getObject(0, "FAX_ID")));
			    ps.setString(3, dto.dsToString(ds_resend.getObject(0, "SEQ_NO")));
			    
			} else {
				sb.append(" UPDATE FOD_ROW_Data SET ");
				sb.append("  fax_number = ? ");
				sb.append(" ,fax_id = ? ");
				sb.append(" ,process_tag = 0  ");
				sb.append(" where customer_id = ? ");
				
			    ps = faxconn.prepareStatement(sb.toString());
			    ps.setString(1, dto.dsToString(ds_resend.getObject(0, "FAX_NUMBER")));
			    ps.setString(2, dto.dsToString(ds_resend.getObject(0, "FAX_ID")));
			    ps.setString(3, dto.dsToString(ds_resend.getObject(0, "SEQ_NO")));
			}		    
		    ps.executeUpdate();
		    ps.close();
		    
		    //기존보낸거 삭제시킨다. 
		    //sb.delete(0, sb.length());
//		    sb = new StringBuffer(100);
//			sb.append(" UPDATE Fax_Last_Call_Detail_New SET ");
//			sb.append("  customer_fax_id1 = '' ");
//			sb.append(" where customer_fax_id1 = ? ");
//		    ps = faxconn.prepareStatement(sb.toString());
//		    ps.setString(1, dto.dsToString(ds_resend.getObject(0, "SEQ_NO")));
//		    
//		    ps.executeUpdate();		    
//		    ps.close();
		    
		    faxconn.close();
			
		} catch (Exception e) {
			//System.out.println("팩스디비 저장중 오류 " + e.toString());
			throw new RuntimeException("팩스재발송 저장중 오류  : " + e);

		} finally {
			try { if(ps !=null) ps.close(); ps=null; } catch(Exception e){};
			try { if(faxconn !=null) faxconn.close(); faxconn=null; } catch(Exception e){};	
		}
	}
	
	
	// 데이터 인서트
	private void dataInsert(XcommonDto dto, DataSet Ds, String sqlid, String seq) throws Exception {
		if(Ds != null) {
			for(int k=0; k < Ds.getRowCount(); k++) {
				HashMap map = dto.getRowMap(Ds, k);
				map.put("seq_no", seq);
				commonDao.insert(sqlid, map);
			}
		}
	}

	public void aaa()
	{
	    StringBuffer sb = new StringBuffer();
	    sb.append("aaaaaaaaa");
        try
        {
            String[] retFileInfo = FileSystemUtil.makeFileContents(sb.toString(), Const.KOREA_CHARSET);
            System.out.println("SendingServiceImpl.aaa() :  retFileInfo[0]"+retFileInfo[0]);
            System.out.println("SendingServiceImpl.aaa() :  retFileInfo[0]"+retFileInfo[1]);
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args)
    {
        SendingServiceImpl send = new SendingServiceImpl();
        send.aaa();
    }
	
	public String xmlConverter(String input_data, String fax_seq, String fax_number, String cust_nm){
		String[] blocks = input_data.split("\\$");
		String tot_pt = blocks[0].split("\\|")[2];
		StringBuffer sb = new StringBuffer("");
		sb.append("<result><data><header>");
		
		sb.append("<cust_nm>"+cust_nm+"</cust_nm>");
		sb.append("<tot_pt>"+tot_pt+"</tot_pt>");
		sb.append("<fax_seq>"+fax_seq+"</fax_seq>");
		sb.append("<fax_number>"+fax_number+"</fax_number></header><body>");
	
		for(int i=0;i<blocks.length;i++){
			String[] pageData = blocks[i].split("\\▦");
			
			for(int j=0;j<pageData.length;j++){
				String[] rowdata = pageData[j].split("\\|");
				System.out.println(rowdata.length);
				if(j==0&&rowdata.length==14){
					sb.append("<info><exec_dt>"+rowdata[3]+"</exec_dt>");
					sb.append("<card_no>"+rowdata[4]+"</card_no>");
					sb.append("<channel>"+rowdata[5]+"</channel>");
					sb.append("<type>"+rowdata[6]+"</type>");
					sb.append("<branch>"+rowdata[7]+"</branch>");
					sb.append("<oil_type>"+rowdata[8]+"</oil_type>");
					sb.append("<price>"+rowdata[9]+"</price>");
					sb.append("<rate>"+rowdata[10]+"</rate>");
					sb.append("<volume>"+rowdata[11]+"</volume>");
					sb.append("<point>"+rowdata[12]+"</point>");
					sb.append("<payment_type>"+rowdata[13]+"</payment_type></info>");
				}else{
					if(rowdata.length==12){
					sb.append("<info><exec_dt>"+rowdata[1]+"</exec_dt>");
						sb.append("<card_no>"+rowdata[2]+"</card_no>");
						sb.append("<channel>"+rowdata[3]+"</channel>");
						sb.append("<type>"+rowdata[4]+"</type>");
						sb.append("<branch>"+rowdata[5]+"</branch>");
						sb.append("<oil_type>"+rowdata[6]+"</oil_type>");
						sb.append("<price>"+rowdata[7]+"</price>");
						sb.append("<rate>"+rowdata[8]+"</rate>");
						sb.append("<volume>"+rowdata[9]+"</volume>");
						sb.append("<point>"+rowdata[10]+"</point>");
						sb.append("<payment_type>"+rowdata[11]+"</payment_type></info>");
					}
				}
				
			}
		}
		sb.append("</body></data></result>");
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	public String xmlConverterParcel(String input_data, String fax_seq, String fax_number){
		String[] rowdata = input_data.split("\\|");
		StringBuffer sb = new StringBuffer("");
		sb.append("<result><data>");
		sb.append("<cust_nm>"+rowdata[2]+"</cust_nm>");
		sb.append("<fax_seq>"+fax_seq+"</fax_seq>");
		sb.append("<fax_number>"+fax_number+"</fax_number>");
		sb.append("<parcel_applied_dt>"+rowdata[0]+"</parcel_applied_dt>");
		sb.append("<reg_no>"+rowdata[1]+"</reg_no>");
		sb.append("<parcel_cmpny>"+rowdata[2]+"</parcel_cmpny>");
		sb.append("<cmpny_tel_no>"+rowdata[3]+"</cmpny_tel_no>");
		sb.append("<fax_no>"+rowdata[4]+"</fax_no>");
		sb.append("<email>"+rowdata[5]+"</email>");
		sb.append("<applicant>"+rowdata[6]+"</applicant>");
		sb.append("<branch_nm>"+rowdata[7]+"</branch_nm>");
		sb.append("<applicant_tel_no>"+rowdata[8]+"</applicant_tel_no>");
		sb.append("<MC_nm>"+rowdata[9]+"</MC_nm>");
		sb.append("<lab_nm>"+rowdata[10]+"</lab_nm>");
		sb.append("<MC_tel_no>"+rowdata[11]+"</MC_tel_no>");
		sb.append("<lab_tel_no>"+rowdata[12]+"</lab_tel_no>");
		sb.append("<MC_cell_no>"+rowdata[13]+"</MC_cell_no>");
		sb.append("<lab_receipient>"+rowdata[14]+"</lab_receipient>");
		sb.append("<container>"+rowdata[15]+"</container>");
		sb.append("<sample_no>"+rowdata[16]+"</sample_no>");
		sb.append("</data></result>");
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	
	public void sendXml(String send_data, String url) throws Exception{
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try{
			String xmlString = send_data;
			HttpPost httpRequest = new HttpPost(url);
			httpRequest.setHeader("Content-Type", "application/xml");
			StringEntity xmlEntity = new StringEntity(xmlString,"UTF-8");
			httpRequest.setEntity(xmlEntity);
			HttpResponse httpresponse = httpClient.execute(httpRequest);
			System.out.println("status_code = "+httpresponse.getStatusLine().getStatusCode());
		}catch(Exception e){
			errlogger.debug("Send Exception !!");
			errlogger.debug(e.toString());
			e.printStackTrace();
			throw e;
		}finally {
            httpClient.getConnectionManager().shutdown();
        }
		
	}
	

}
