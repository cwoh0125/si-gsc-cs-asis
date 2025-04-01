<%@ page language="java" contentType="text/html;charset=euc-kr" %>

<%
    Kisinfo.Check.CPClient kisCrypt = new  Kisinfo.Check.CPClient();

    String sEncodeData = request.getParameter("EncodeData");
    String sReserved1  = request.getParameter("param_r1");
    String sReserved2  = request.getParameter("param_r2");
    String sReserved3  = request.getParameter("param_r3");

    String sSiteCode 		= kr.co.cs.common.config.Const.NICE_SITE_CODE_CI;		// NICE로부터 부여받은 사이트 코드
    String sSitePassword 	= kr.co.cs.common.config.Const.NICE_SITE_PWD_CI;		// NICE로부터 부여받은 사이트 패스워드

    String sCipherTime = "";					// 복호화한 시간
    String sRequestNumber = "";				// 요청 번호
    String sErrorCode = "";						// 인증 결과코드
    String sAuthType = "";						// 인증 수단
    String sMessage = "";
    String sPlainData = "";
    
    int iReturn = kisCrypt.fnDecode(sSiteCode, sSitePassword, sEncodeData);

    if( iReturn == 0 )
    {
        sPlainData = kisCrypt.getPlainData();
        sCipherTime = kisCrypt.getCipherDateTime();
        
        // 데이타를 추출합니다.
        java.util.HashMap mapresult = kisCrypt.fnParse(sPlainData);
        
        sRequestNumber 	= (String)mapresult.get("REQ_SEQ");
        sErrorCode 			= (String)mapresult.get("ERR_CODE");
        sAuthType 			= (String)mapresult.get("AUTH_TYPE");
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
    
    if(sReserved1 != null) {
    	sReserved1 = sReserved1.replaceAll("<","&lt;");
    	sReserved1 = sReserved1.replaceAll(">","&gt;");
    	sReserved1 = sReserved1.replaceAll("&", "&amp;");
    	sReserved1 = sReserved1.replaceAll("\"", "&quot;");
    }
    if(sReserved2 != null) {
    	sReserved2 = sReserved2.replaceAll("<","&lt;");
    	sReserved2 = sReserved2.replaceAll(">","&gt;");
    	sReserved2 = sReserved2.replaceAll("&", "&amp;");
    	sReserved2 = sReserved2.replaceAll("\"", "&quot;");
    }
    if(sReserved3 != null) {
    	sReserved3 = sReserved3.replaceAll("<","&lt;");
    	sReserved3 = sReserved3.replaceAll(">","&gt;");
    	sReserved3 = sReserved3.replaceAll("&", "&amp;");
    	sReserved3 = sReserved3.replaceAll("\"", "&quot;");
    }
    if(sMessage != null) {
    	sMessage = sMessage.replaceAll("<","&lt;");
    	sMessage = sMessage.replaceAll(">","&gt;");
    }
    if(sAuthType != null) {
    	sAuthType = sAuthType.replaceAll("<","&lt;");
    	sAuthType = sAuthType.replaceAll(">","&gt;");
    }
    if(sRequestNumber != null) {
    	sRequestNumber = sRequestNumber.replaceAll("<","&lt;");
    	sRequestNumber = sRequestNumber.replaceAll(">","&gt;");
    }
    if(sErrorCode != null) {
    	sErrorCode = sErrorCode.replaceAll("<","&lt;");
    	sErrorCode = sErrorCode.replaceAll(">","&gt;");
    }
    if(sCipherTime != null) {
    	sCipherTime = sCipherTime.replaceAll("<","&lt;");
    	sCipherTime = sCipherTime.replaceAll(">","&gt;");
    }
%>

<html>
<head>
    <title>NICE신용평가정보 - CheckPlus 본인인증 테스트</title>
</head>
<body>
    <center>
    <p><p><p><p>
    <b>NICE 본인인증이 실패하였습니다.<br><font color=red>인증 창을 닫고 다시 시도해주시기 바랍니다.</font><br><br></b>
    <table border=0>
        <tr>
            <td>복호화한 시간</td>
            <td><%= sCipherTime %> (YYMMDDHHMMSS)</td>
        </tr>
        <tr>
            <td>요청 번호</td>
            <td><%= sRequestNumber %></td>
        </tr>            
        <tr>
            <td>본인인증 실패 코드</td>
            <td><%= sErrorCode %></td>
        </tr>            
        <tr>
            <td>인증수단</td>
            <td><%= sAuthType %></td>
        </tr>
        <tr>
            <td>RESERVED1</td>
            <td><%= sReserved1 %></td>
        </tr>
        <tr>
            <td>RESERVED2</td>
            <td><%= sReserved2 %></td>
        </tr>
        <tr>
            <td>RESERVED3</td>
            <td><%= sReserved3 %></td>
        </tr>
    </table><br><br>        
    <%= sMessage %><br>
    </center>
</body>
</html>