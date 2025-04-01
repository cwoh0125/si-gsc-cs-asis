package kr.co.cs.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Const {
	
	/*
	 * 환경
	 */
	public static final String DEFAULT_CHARSET = "UTF-8"; 
	public static final String KOREA_CHARSET = "KSC5601";
	//public static final String KOREA_CHARSET = "EUC-KR";
 
	/** upload file directory
	 * 
	*/ 
	
	//분리 보관 테스트 
	public static final String DOWN_PATH = "/APPDATA/SMILE2/SFTP/DOWN/";
	//test
	
	//SFTP
	public static final String SFTP_IP = "192.168.7.21";
	public static final String SFTP_ID = "sep_csc";
	public static final String SFTP_PWD = "QWert!@345";
   //public static final String TEST_SFTP_SEND = "D:/SFTP/GSM_Separation/Org/IS2002/";
	public static final String SFTP_SEND = "/";
	public static final int SFTP_PORT = 22;  		
	

	
	
	public static final String TEST_SFTP_IP = "192.168.230.33";
	public static final String TEST_SFTP_ID = "sep_csc";
	public static final String TEST_SFTP_PWD = "QWert!@345";
   //public static final String TEST_SFTP_SEND = "D:/SFTP/GSM_Separation/Org/IS2002/";
	public static final String TEST_SFTP_SEND = "/";
	public static final int TEST_SFTP_PORT = 22;  	
	public static final String GSC_SP_PATH  = "/APPDATA/SMILE2/GSC/SP/";	
	//분리 보관 대상 데이터 다운
	public static final String WITH_GSIB_DE_DOWN  = "/APPDATA/SMILE2/SFTP/DOWN/";
	
	//삭제 복원 대상자 다운 
	public static final String WITH_GSIB_DIV_DOWN  = "/APPDATA/SMILE2/SFTP/DIV/";
	public static final String WITH_GSIB_RE_CUST  = "RE.IS2002";
	public static final String WITH_GSIB_DE_CUST  = "DE.IS2002";	
	//분리보관 설정 END
		
	public static final String SAVEDFILE_LOCALPATH = "C:/GS_TESTFILE/SAVED/";
	public static final String SAVEDFILE_UNIXPATH 	= "/APPDATA/SMILE2/FILE_BOX/SAVED/";
 
	public static final String FIXEDFILE_LOCALPATH = "C:/GS_TESTFILE/FIXED/";
	public static final String FIXEDFILE_UNIXPATH 	= "/APPDATA/SMILE2/FILE_BOX/FIXED/";

	/*
	 * SMTP 환경 (진짜 메일 주소 오면 고치시오)
	 * 
	 */	
	public static final String SMTP_HOST	= "192.168.7.9";
	//20190429      25->10025변경
	//20191204   10025->25변경
	public static final String SMTP_PORT 	= "25";
	public static final String SMTP_ACCOUNT 	= "";
	public static final String SMTP_PASSWORD 	= "";
	//public static final String SMTP_FROM_MAIL 	= "help@gscaltex.co.kr";
	public static final String SMTP_FROM_MAIL 	= "help@gscaltex.com";

	
	/* 
	 * fax FTP 환경 (진짜 팩스오면 고치시오)
	 * 새로운 FAX 유비커스
	 * */
	public static final String FAX_FTP_HOST = "192.168.16.20";
	public static final int FAX_FTP_PORT = 21;
	public static final String FAX_FTP_ACCOUNT = "wiseftp";
	public static final String FAX_FTP_PASSWORD = "wiseftp";


	public static final String TEST_FAX_FTP_HOST = "192.168.16.20";
	public static final int TEST_FAX_FTP_PORT = 21;
	public static final String TEST_FAX_FTP_ACCOUNT = "wiseftp";
	public static final String TEST_FAX_FTP_PASSWORD = "wiseftp";
	/* 
	 * fax db 환경 (진짜 팩스오면 고치시오) 안쓰임
	 * */
	//더이상 사용 하지 않는 예전 FAX정보
	public static final String FAX_DB_ACCOUNT = "XXXdb2admin";
	public static final String FAX_DB_PASSWORD = "XXXdb2admin";
	public static final String FAX_DB_URL = "XXXjdbc:sqlserver://192.168.16.113:1433;DatabaseName=WISEImage4";
	public static final String TEST_FAX_DB_ACCOUNT 	= "XXXdb2admin";
	public static final String TEST_FAX_DB_PASSWORD = "XXXdb2admin";
	public static final String TEST_FAX_DB_URL = "XXXjdbc:sqlserver://192.168.16.113:1433;DatabaseName=WISEImage4";
//사용 하지 않는 END
	
	/* 
	 * ivr db 환경 (진짜 ivr오면 고치시오)
	 * */
	public static final String IVR_DB_ACCOUNT 	= "sa"; 
	public static final String IVR_DB_PASSWORD = "gs!call2010";
	public static final String IVR_DB_URL = "jdbc:sqlserver://192.168.16.20:51433;DatabaseName=I3_IC";

	public static final String TEST_IVR_DB_ACCOUNT 	= "sa";
	public static final String TEST_IVR_DB_PASSWORD = "gs!call2010";
	public static final String TEST_IVR_DB_URL = "jdbc:sqlserver://192.168.16.20:51433;DatabaseName=I3_IC";
	
	// 운영관리 DB 환경	CAMS
	public static final String CAMS_DB_ACCOUNT 	= "sa"; 
	public static final String CAMS_DB_PASSWORD = "gs!call2010";
	public static final String CAMS_DB_URL = "jdbc:sqlserver://192.168.16.20:51433;DatabaseName=CAMS";
	
	
	// 운영관리 DB 환경	MSG
	public static final String MSG_DB_ACCOUNT 	= "sa"; 
	public static final String MSG_DB_PASSWORD = "gs!call2010";
	public static final String MSG_DB_URL = "jdbc:sqlserver://192.168.16.20:51433;DatabaseName=MSG";
		
		
	/* 
	 * CTI 미니전광판 통계db 환경 (진짜 CTI오면 고치시오) 안쓰임
	 * */
	public static final String CTI_STADB_ACCOUNT = "cscetl";
	public static final String CTI_STADB_PASSWORD = "cscetl";
	public static final String CTI_STADB_URL = "jdbc:db2://192.168.16.142:60000/CCADB";
	
	
	public static final String TEST_CTI_STADB_ACCOUNT 	= "cscetl";
	public static final String TEST_CTI_STADB_PASSWORD = "cscetl";
	public static final String TEST_CTI_STADB_URL = "jdbc:db2://192.168.16.110:60000/CCADB";


	//15분 통계
	public static final String CTI_TOTSTADB_ACCOUNT = "sa";
	public static final String CTI_TOTSTADB_PASSWORD = "gs!call2010";
	public static final String CTI_TOTSTADB_URL = "jdbc:sqlserver://192.168.16.20:51433;DatabaseName=I3_IC";
	
	
	public static final String TEST_CTI_TOTSTADB_ACCOUNT 	= "sa";
	public static final String TEST_CTI_TOTSTADB_PASSWORD = "gs!call2010";
	public static final String TEST_CTI_TOTSTADB_URL = "jdbc:sqlserver://192.168.16.20:51433;DatabaseName=I3_IC";

	
	/*
	 * INTERFACE URL
	 * */
	public static final String SAP_END_POINT = "http://203.245.89.15:48899/WSCSC.asmx";  //SAP
	public static final String TEST_SAP_END_POINT = "http://203.245.65.176:48899/WSCSC.asmx";	//GCP 리허설 클라이언트 EAI
	
	public static final String SAP_46C_END_POINT = "http://203.245.89.15:48899/WSCSC_46C.asmx";  //SAP
	public static final String TEST_SAP_46C_END_POINT = "http://203.245.65.176:48899/WSCSC_46C.asmx";	//GCP 리허설 클라이언트 EAI
	//public static final String SAP_END_POINT = "http://203.245.89.15:48899/WSCSC.asmx";  //SAP ERP 재구축 이전 REAL
	
	//public static final String WAS_END_POINT = "http://203.245.82.207:8030/gscwas/was/"; //CAPTIVE
	//SSL적용
	public static final String WAS_END_POINT = "https://gscwas.gscaltex.co.kr:8030/gscwas/was/"; //CAPTIVE
	
	public static final String GSWAS_END_POINT = "http://203.245.82.204:8030/gswas/was/"; //

	//20201126 GSC URL변경 요청자 김재숙주임
	//public static final String GSSMS_END_POINT = "http://203.245.82.120/";
	public static final String GSSMS_END_POINT = "http://203.245.82.118/gssms/";
	
	public static final String KIXX_END_POINT = "http://203.245.82.209:20098/KixxToCRM.asmx/"; //KIXX
	//public static final String ERMS_END_POINT = "http://203.245.82.136:8080/enomix/csc/"; //이노믹스
	public static final String OILCP_END_POINT = "http://gsapi.m2i.kr/";					//쿠폰마케팅
	public static final String MPP_END_POINT = "http://control.gsmpp.com/";
	  
	//public static final String GSCP_END_POINT = "http://gsmcoupon.gsmpp.com/services/mppWas/"; //GS쿠폰(운영)
	
	
	public static final String GSCP_END_POINT = "https://managerapi.gsmcoupon.gsmpp.com:32443/services/mppWas/"; //GS쿠폰(운영)	
	//public static final String GSCP_END_POINT = "http://managerapi.gsmcoupon.gsmpp.com/services/mppWas/"; //GS쿠폰(운영)
	
	public static final String ERMS_END_POINT = "http://203.245.82.142:9090/enomix/"; //이노믹스(New 운영)
	
	
	
	//public static final String TEST_SAP_END_POINT = "http://10.200.201.181:48899/WSCSC.asmx";	//SAP_EAI 개발 ERP_DEV
	//public static final String TEST_SAP_46C_END_POINT = "http://10.200.201.181:48899/WSCSC_46C.asmx";	//SAP_EAI_46C 개발 DEV
	//public static final String TEST_SAP_END_POINT = "http://203.245.65.176:48899/WSCSC.asmx";	//SAP ERP 재구축 이전 DEV 
	
	
	
	
	
	//SSL적용으로 테스트 대상 
	//public static final String TEST_WAS_END_POINT = "http://203.245.82.121:8088/gscwas/was/";
	//public static final String TEST_WAS_END_POINT = "https://captive.gscaltex.co.kr:9088/gscwas/was/";	
	
	//20210825 9088에서 8088로 변경
	//public static final String TEST_WAS_END_POINT = "https://devloyalty.gscaltex.co.kr:9088/gscwas/was/";
	public static final String TEST_WAS_END_POINT = "https://devloyalty.gscaltex.co.kr:8088/gscwas/was/";
	
	//public static final String TEST_WAS_END_POINT = "http://172.24.32.121:8088/gscwas/was/";
	
	
	public static final String TEST_P7_WAS_END_POINT = "http://203.245.82.121:8088/p7_gscwas/was/";	//P7 전용 개발 was
	
	//SSL적용 
	//public static final String TEST_GSWAS_END_POINT = "http://203.245.82.121:8088/gswas/was/";  //<- 'c' 만 없어짐..
	public static final String TEST_GSWAS_END_POINT = "https://devloyalty.gscaltex.co.kr:8088/gswas/was/";  //<- 'c' 만 없어짐..
	//public static final String TEST_GSWAS_END_POINT = "http://172.24.32.121:8088/gswas/was/";  //<- 'c' 만 없어짐..
	
	public static final String TEST_GSSMS_END_POINT = "http://203.245.82.121:8088/gssms/";
	//public static final String TEST_GSSMS_END_POINT = "http://172.24.32.121:8088/gssms/";
	
	
	
	public static final String TEST_KIXX_END_POINT = "http://203.245.82.102:20098/KixxToCRM.asmx/";
	public static final String TEST_OILCP_END_POINT = "http://gsapitest.m2i.kr:9999/";
	public static final String TEST_MPP_END_POINT = "http://controltest.gsmpp.com/";
	
	public static final String GSN_MPP_SMS_END_POINT3 = "http://192.168.7.21:8082/SMSService.asmx";
	public static final String TEST_GSN_MPP_SMS_END_POINT3 = "http://192.168.7.21:8882/SMSService.asmx";
	
	
	//쿠폰 프로젝트 테스트 20211006	
	//public static final String TEST_GSCP_END_POINT = "http://t-inapi.gsmcoupon.gsmpp.com/services/mppWas/"; //GS쿠폰(TEST)
	public static final String TEST_GSCP_END_POINT = "https://t-managerapi.gsncoupon.com:32443/services/mppWas/"; //GS쿠폰(TEST)
	                                                
	public static final String TEST_ERMS_END_POINT = "http://203.245.82.142:9090/enomix/";   // 이노믹스(New)
	
	//JSONTOXML   앱푸시
	public static final String GSCAPP_END_POINT = "http://203.245.82.172:10080/api/crm/";   // 앱push	
	public static final String TEST_GSCAPP_END_POINT = "http://172.24.31.23:10080/api/crm/";;   // 앱push
	
	
	//public static final String TEST_AO_END_POINT = "http://192.168.7.21:8081/aoPOSServer.asmx"; //개발
	public static final String TEST_AO_END_POINT = "http://192.168.7.21:8881/aoPOSServer.asmx"; //개발
	public static final String AO_END_POINT = "http://192.168.7.21:8081/aoPOSServer.asmx";	   //운영
	
	public static final String ORDER_CANCEL_END_POINT= "http://m.gsmpp.com/";
	public static final String TEST_ORDER_CANCEL_END_POINT_1 = "http://121.140.161.41/SPoSAServiceGSN/";
	public static final String TEST_ORDER_CANCEL_END_POINT_2 = "http://192.168.7.41/SPoSAServiceGSN/";
	
	//fax 전송 주소
	public static final String parcel_dest_url="http://192.168.16.20:80/mngr/action/faxConvertMgr.jspx?cmd=parcel_application";
	public static final String gas_fillup_url="http://192.168.16.20:80/mngr/action/faxConvertMgr.jspx?cmd=gas_fillup_history";
	public static final String app_fixed_forms_url="http://192.168.16.20:80/mngr/action/faxConvertMgr.jspx?cmd=appFixedForms";
	public static final String fax_resend_url="http://192.168.16.20:80/mngr/action/faxConvertMgr.jspx?cmd=resendFax";
	public static final String bonus_ivr_url="http://192.168.16.20:80/mngr/action/faxConvertMgr.jspx?cmd=gas_fillup_history_ivr";
	public static final String fileMod_url="http://192.168.16.20:80/mngr/action/faxConvertMgr.jspx?cmd=fileMod";
	public static final String order_ivr_url="http://192.168.16.20:80/mngr/action/faxConvertMgr.jspx?cmd=order_history";
	public static final String tran_ivr_url="http://192.168.16.20:80/mngr/action/faxConvertMgr.jspx?cmd=tran_status";
	
	//GSC자사앱 	
	public static final String WASAPP_END_POINT="http://203.245.82.212:9000/api/extern/csc/";
	public static final String WASAPPTEST_END_POINT="http://172.24.31.22:9000/api/extern/csc/";
	
	
	//EV APP
	public static final String WAS_EVAPP_END_POINT="http://203.245.82.212:9010/api/extern/csc/";
	public static final String WAS_EVAPPTEST_END_POINT="http://172.24.31.22:9010/api/extern/csc/";
	
	//쿠폰 암복호화 키 값 
    public static  String  PK = "000320131128ABCDEFGHIJKLMNOPQRST"; // 운영
    public static String   PK_TEST = "1234567890ABCDEFGHIJKLMNOPQRSTUV"; // 개발
    public static String   Alg = "AES/CBC/PKCS5Padding";
	
	/*
	 * SAP ERP 재구축 데이터 이관 날짜
	 * */
	public static final String SAP_ERP_ESTABLISH_DATE = "20130701"; 
	
	/*
	 * 한신평 실명인증
	 * */
	public static final String NICE_SITE_CODE = "K229";
	public static final String NICE_SITE_PWD = "71339058";	
	
	/*
	 * NICE CI값 가져오기 위함
	 * */
	public static final String NICE_SITE_CODE_CI 	= "G2261";				// 2013-01-09 이광호 추가
	public static final String NICE_SITE_PWD_CI 	= "ZR5YS19XWOEX";		// 2013-01-09 이광호 추가

	
	/*
	 * real was 환경
	 */	
	public static final String WAS1NAME = "apserver";
	public static final String WAS2NAME = "apserver";

	
	 /* String 관련
	 */
	public static final char CR                       = 0x0D;
	public static final char LF                       = 0x0A;

	public static final char BEL                      = 0x07;	// 
	public static final char BS                       = 0x08;	// 
	public static final char DEL                      = 0x7F;	// 
	public static final char BLANK                    = 0x80; //
	public static final char SUB                      = 0x1A; // 
	public static final char ESC                      = 0x1B; // 

	public static final char RETURN                   = '\r';
	public static final char ENTER                    = '\n';
	public static final char TAB                      = '\t';

	public static final char DOUBLE_QUOT              = 0x22;	// "
	public static final char SINGLE_QUOT              = 0x27;	// '
	public static final char BACK_QUOT                = 0x60;	// `
	public static final char DOT                      = 0x2E;	// .
	public static final char PIPE                     = 0x7C; // |
	public static final char EXCLAM_POINT             = 0x21; // !
	public static final char TILDE                    = 0x7E; // ~
	public static final char SLASH                    = 0x2F; // /
	public static final char CIRCUMFLEX               = 0x5E; // ^

	public static final char PLUS                     = 0x2B; // +
	public static final char MINUS                    = 0x2D; // -
	public static final char ASTERISK                 = 0x2A; // *
	public static final char DIVIDE                   = SLASH;

	public static final char ZERO                     = 0x30; // 0
	public static final char EQUAL                    = 0x3D; // =
	public static final char COMMA                    = 0x2C; // ,
	public static final char QUESTION_MARK            = 0x3F; // ?
	public static final char UNDERLINE                = 0x5F; // _
	public static final char COLON                    = 0x3A; // :
	public static final char SEMICOLON                = 0x3B; // ;
	public static final char BACK_SLASH               = 0x5C; // \
	public static final char DASH                     = MINUS;

	public static final char LEFT_SQUARE_BRACKET      = 0x5B; // [
	public static final char RIGHT_SQUARE_BRACKET     = 0x5D; // ]

	public static final char LEFT_BRACE               = 0x7B; // {
	public static final char RIGHT_BRACE              = 0x7D; // }

	public static final char LEFT_ROUND_BRACKET       = 0x28; // (
	public static final char RIGHT_ROUND_BRACKET      = 0x29; // )

	public static final char LEFT_ANGLE_BRACKET       = 0x3C; // <
	public static final char RIGHT_ANGLE_BRACKET      = 0x3E; // >

	public static final char SPACE                    = 0x20;		// ' '
	public static final char FULL_SPACE               = 0x3000;	// '　'

	public static final char PATH_SEPARATOR           = File.separatorChar;
	public static final String LINE_SEPARATOR         = System.getProperty("line.separator");

	public static final String EOL                    = "\r\n";

	public static final String NULL_STRING            = new String();
	public static final byte[] NULL_BYTE              = new byte[0];
	public static final String[] NULL_STRING_ARRAY    = new String[0];
	public static final List NULL_LIST				  = new ArrayList(0);                    
	


	
	//한신평 결과
	public static String NICE_RET_MSG(int code) {
		String x;
		switch(code){
		case 1 : x = "본인 맞음"; break;
		case 2 : x = "본인 아님"; break; 
		case 3 : x = "한신평에 자료 없음 "; break;
		case 4 : x = "시스템 장애(네트워크)."; break;
		case 5 : x = "주민번호 오류 "; break;
		case 6 : x = "성인인증시 만19세 이하인 경우 실명인증 거치지 않고 바로 리턴코드 출력됩니다."; break;
		case 9 : x = "주민번호, 사이트패스워드, 사이트아이디, 성명 중 한개의 데이타라도 빠지고 온 경우의 에러"; break;
		case 10 : x = "사이트 코드 오류"; break;
		case 11 : x = "정지된 고객사"; break;
		case 12 : x = "해당고객사 비밀번호 오류"; break;
		case 13 : x = "사이트 인증 시스템 장애"; break;
		case 15 : x = "Decoding 오류(Data) "; break;
		case 16 : x = "Decoding 시스템장애"; break;
		case 21 : x = "암호화 데이타 이상 ( 주민번호(13), 비밀번호(8) 자릿수를 확인해 주세요.)"; break;
		case 24 : x = "암호화 연산중 에러 (올바르지 않은 주민번호인지 확인해 주세요.)"; break;
		case 50 : x = "정보도용 차단 요청 주민번호임"; break;
		case 55 :
		case 56 :	
		case 57 : x = "외국인 번호 확인 오류"; break;
		case 58 : x = "출입국 관리소 통신 오류"; break;
		case 61 : x = "연결 장애 (NoRouteToHostException 일 경우 라우터에서 연결 host를 못찾을때)"; break;
		case 62 : x = "MalformedURLException 일 경우.(실명확인 서버를 못찾을때)"; break;
		case 63 : x = "소켓통신 오류"; break;
		default : x = ""; break;			
		}
		
		return x;
	}
}
