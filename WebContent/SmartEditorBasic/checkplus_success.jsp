<%@ page language="java" contentType="text/html;charset=euc-kr" %>
<%@ page import ="java.util.HashMap"%> 

<%
    Kisinfo.Check.CPClient kisCrypt = new  Kisinfo.Check.CPClient();

    String sEncodeData = request.getParameter("EncodeData");
    String sReserved1  = request.getParameter("param_r1");
    String sReserved2  = request.getParameter("param_r2");
    String sReserved3  = request.getParameter("param_r3");

    String sSiteCode 		= kr.co.cs.common.config.Const.NICE_SITE_CODE_CI;		// NICE로부터 부여받은 사이트 코드
    String sSitePassword 	= kr.co.cs.common.config.Const.NICE_SITE_PWD_CI;		// NICE로부터 부여받은 사이트 패스워드

    String sCipherTime = "";				 // 복호화한 시간
    String sRequestNumber = "";			 // 요청 번호
    String sResponseNumber = "";		 // 인증 고유번호
    String sAuthType = "";				   // 인증 수단
    String sName = "";							 // 성명
    String sDupInfo = "";						 // 중복가입 확인값 (DI_64 byte)
    String sConnInfo = "";					 // 연계정보 확인값 (CI_88 byte)
    String sBirthDate = "";					 // 생일
    String sGender = "";						 // 성별
    String sNationalInfo = "";       // 내/외국인정보 (개발가이드 참조)
    String sMessage = "";
    String sPlainData = "";
        
    int iReturn = 0;
    try{
    	iReturn = kisCrypt.fnDecode(sSiteCode, sSitePassword, sEncodeData);
    }catch (NumberFormatException e){
    }catch (Exception e){
    }
    if( iReturn == 0 )
    {
       	sPlainData = kisCrypt.getPlainData();

        //sCipherTime = kisCrypt.getCipherDateTime();
        
        // 데이타를 추출합니다.
        HashMap mapresult = kisCrypt.fnParse(sPlainData);
                
        sRequestNumber  = (String)mapresult.get("REQ_SEQ");
        sResponseNumber = (String)mapresult.get("RES_SEQ");
        sAuthType 		= (String)mapresult.get("AUTH_TYPE");
        sName 			= (String)mapresult.get("NAME");
        sBirthDate 		= (String)mapresult.get("BIRTHDATE");
        sGender 		= (String)mapresult.get("GENDER");
        sNationalInfo  	= (String)mapresult.get("NATIONALINFO");
        sDupInfo 		= (String)mapresult.get("DI");
        sConnInfo 		= (String)mapresult.get("CI");
                
        String session_sRequestNumber = (String)session.getAttribute("REQ_SEQ");
        if(!sRequestNumber.equals(session_sRequestNumber))
        {
            sMessage = "세션값이 다릅니다. 올바른 경로로 접근하시기 바랍니다.";
            sResponseNumber = "";
            sAuthType = "";
        }
    }
    else if( iReturn == -1)
    {
        sMessage = "복호화 시스템 에러입니다.";
    }    
    else if( iReturn == -4)
    {
        sMessage = "복호화 처리오류입니다.";
    }    
    else if( iReturn == -5)
    {
        sMessage = "복호화 해쉬 오류입니다.";
    }    
    else if( iReturn == -6)
    {
        sMessage = "복호화 데이터 오류입니다.";
    }    
    else if( iReturn == -9)
    {
        sMessage = "입력 데이터 오류입니다.";
    }    
    else if( iReturn == -12)
    {
        sMessage = "사이트 패스워드 오류입니다.";
    }    
    else
    {
        sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
    }
    
    //XSS 처리
    if(sMessage != null) {
    	sMessage = sMessage.replaceAll("<","&lt;");
    	sMessage = sMessage.replaceAll(">","&gt;");
    }
    if(sConnInfo != null) {
    	sConnInfo = sConnInfo.replaceAll("<","&lt;");
    	sConnInfo = sConnInfo.replaceAll(">","&gt;");
    }

%>

<html>
<head>
    <title>NICE신용평가정보 - CheckPlus 본인인증 테스트</title>
    <script language=javascript>
   		function onload() {
   			document.title = "receive"; 
    	}
    </script>
</head>
<body onload=onload();>
    <center>
    <p><p><p><p>
    <b>NICE 본인인증이 완료 되었습니다.</b><br>
    <input type=hidden name=ciData value="<%= sConnInfo %>">  
    <%= sMessage %><br>
    </center>
</body>
</html>