<%@ page language="java" contentType="text/html;charset=euc-kr" %>
<%
    Kisinfo.Check.CPClient kisCrypt = new  Kisinfo.Check.CPClient();
    
    String sSiteCode 		= kr.co.cs.common.config.Const.NICE_SITE_CODE_CI;		// NICE로부터 부여받은 사이트 코드
    String sSitePassword 	= kr.co.cs.common.config.Const.NICE_SITE_PWD_CI;		// NICE로부터 부여받은 사이트 패스워드
    
    String sRequestNumber = "REQ0000000001";        	// 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로 
                                                    	// 업체에서 적절하게 변경하여 쓰거나, 아래와 같이 생성한다.
    sRequestNumber = kisCrypt.getRequestNO(sSiteCode);
  	session.setAttribute("REQ_SEQ" , sRequestNumber);	// 해킹등의 방지를 위하여 세션을 쓴다면, 세션에 요청번호를 넣는다.
  	
   	String sAuthType = "";      	// 없으면 기본 선택화면, X: 공인인증서, M: 핸드폰, C: 신용카드
   	String endPoint = "";
   	
   	if(kr.co.cs.common.publicutil.ComUtil.isProd()){
		endPoint = java.net.InetAddress.getLocalHost().getHostAddress();
	}else{
		endPoint = java.net.InetAddress.getLocalHost().getHostAddress();
	}
    // CheckPlus(본인인증) 처리 후, 결과 데이타를 리턴 받기위해 다음예제와 같이 http부터 입력합니다.
    String sReturnUrl 	= "http://"+endPoint+":7001/SmartEditorBasic/checkplus_success.jsp";      	// 성공시 이동될 URL
    String sErrorUrl 	= "http://"+endPoint+":7001/SmartEditorBasic/checkplus_fail.jsp";         	// 실패시 이동될 URL

    // 입력될 plain 데이타를 만든다.
    String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
                        "8:SITECODE" + sSiteCode.getBytes().length + ":" + sSiteCode +
                        "9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
                        "7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
                        "7:ERR_URL" + sErrorUrl.getBytes().length + ":" + sErrorUrl;
    
    String sMessage = "";
    String sEncData = "";
    
    int iReturn = kisCrypt.fnEncode(sSiteCode, sSitePassword, sPlainData);
    if( iReturn == 0 )
    {
        sEncData = kisCrypt.getCipherData();
    }
    else if( iReturn == -1)
    {
        sMessage = "암호화 시스템 에러입니다.";
    }    
    else if( iReturn == -2)
    {
        sMessage = "암호화 처리오류입니다.";
    }    
    else if( iReturn == -3)
    {
        sMessage = "암호화 데이터 오류입니다.";
    }    
    else if( iReturn == -9)
    {
        sMessage = "입력 데이터 오류입니다.";
    }    
    else
    {
        sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
    }
%>

<html>
<head>
	<title>NICE신용평가정보 - CheckPlus 본인인증 테스트</title>
	
	<script language='javascript'>
	window.name ="Parent_window";
	
	function fnPopup(){
		//window.open('', 'popupChk', 'width=500, height=461, top=100, left=100, fullscreen=no, menubar=no, status=no, toolbar=no, titlebar=yes, location=no, scrollbar=no');
		document.form_chk.action = "https://check.namecheck.co.kr/checkplus_new_model4/checkplus.cb";
//		document.form_chk.target = _self;
		document.form_chk.submit();
	}
	</script>
</head>
<body onload="fnPopup();" scroll=no>
<!-- 
	<br><%=sReturnUrl %><br>
	sMessage : <%= sMessage %><br><br>
	iReturn : <%= iReturn%><br><br>
	 업체정보 암호화 데이타 : [<%= sEncData %>]<br><br>
 	  -->
	<!-- 본인인증 서비스 팝업을 호출하기 위해서는 다음과 같은 form이 필요합니다. -->
	<form name="form_chk" method="post">
		<input type="hidden" name="m" value="checkplusSerivce">						<!-- 필수 데이타로, 누락하시면 안됩니다. -->
		<input type="hidden" name="EncodeData" value="<%= sEncData %>">		<!-- 위에서 업체정보를 암호화 한 데이타입니다. -->
	    
	    <!-- 업체에서 응답받기 원하는 데이타를 설정하기 위해 사용할 수 있으며, 인증결과 응답시 해당 값을 그대로 송신합니다.
	    	 해당 파라미터는 추가하실 수 없습니다. -->
		<input type="hidden" name="param_r1" value="">
		<input type="hidden" name="param_r2" value="">
		<input type="hidden" name="param_r3" value="">
	</form>
</body>
</html>