<%@ page language="java" contentType="text/html;charset=euc-kr" %>
<%
    Kisinfo.Check.CPClient kisCrypt = new  Kisinfo.Check.CPClient();
    
    String sSiteCode 		= kr.co.cs.common.config.Const.NICE_SITE_CODE_CI;		// NICE�κ��� �ο����� ����Ʈ �ڵ�
    String sSitePassword 	= kr.co.cs.common.config.Const.NICE_SITE_PWD_CI;		// NICE�κ��� �ο����� ����Ʈ �н�����
    
    String sRequestNumber = "REQ0000000001";        	// ��û ��ȣ, �̴� ����/�����Ŀ� ���� ������ �ǵ����ְ� �ǹǷ� 
                                                    	// ��ü���� �����ϰ� �����Ͽ� ���ų�, �Ʒ��� ���� �����Ѵ�.
    sRequestNumber = kisCrypt.getRequestNO(sSiteCode);
  	session.setAttribute("REQ_SEQ" , sRequestNumber);	// ��ŷ���� ������ ���Ͽ� ������ ���ٸ�, ���ǿ� ��û��ȣ�� �ִ´�.
  	
   	String sAuthType = "";      	// ������ �⺻ ����ȭ��, X: ����������, M: �ڵ���, C: �ſ�ī��
   	String endPoint = "";
   	
   	if(kr.co.cs.common.publicutil.ComUtil.isProd()){
		endPoint = java.net.InetAddress.getLocalHost().getHostAddress();
	}else{
		endPoint = java.net.InetAddress.getLocalHost().getHostAddress();
	}
    // CheckPlus(��������) ó�� ��, ��� ����Ÿ�� ���� �ޱ����� ���������� ���� http���� �Է��մϴ�.
    String sReturnUrl 	= "http://"+endPoint+":7001/SmartEditorBasic/checkplus_success.jsp";      	// ������ �̵��� URL
    String sErrorUrl 	= "http://"+endPoint+":7001/SmartEditorBasic/checkplus_fail.jsp";         	// ���н� �̵��� URL

    // �Էµ� plain ����Ÿ�� �����.
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
        sMessage = "��ȣȭ �ý��� �����Դϴ�.";
    }    
    else if( iReturn == -2)
    {
        sMessage = "��ȣȭ ó�������Դϴ�.";
    }    
    else if( iReturn == -3)
    {
        sMessage = "��ȣȭ ������ �����Դϴ�.";
    }    
    else if( iReturn == -9)
    {
        sMessage = "�Է� ������ �����Դϴ�.";
    }    
    else
    {
        sMessage = "�˼� ���� ���� �Դϴ�. iReturn : " + iReturn;
    }
%>

<html>
<head>
	<title>NICE�ſ������� - CheckPlus �������� �׽�Ʈ</title>
	
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
	 ��ü���� ��ȣȭ ����Ÿ : [<%= sEncData %>]<br><br>
 	  -->
	<!-- �������� ���� �˾��� ȣ���ϱ� ���ؼ��� ������ ���� form�� �ʿ��մϴ�. -->
	<form name="form_chk" method="post">
		<input type="hidden" name="m" value="checkplusSerivce">						<!-- �ʼ� ����Ÿ��, �����Ͻø� �ȵ˴ϴ�. -->
		<input type="hidden" name="EncodeData" value="<%= sEncData %>">		<!-- ������ ��ü������ ��ȣȭ �� ����Ÿ�Դϴ�. -->
	    
	    <!-- ��ü���� ����ޱ� ���ϴ� ����Ÿ�� �����ϱ� ���� ����� �� ������, ������� ����� �ش� ���� �״�� �۽��մϴ�.
	    	 �ش� �Ķ���ʹ� �߰��Ͻ� �� �����ϴ�. -->
		<input type="hidden" name="param_r1" value="">
		<input type="hidden" name="param_r2" value="">
		<input type="hidden" name="param_r3" value="">
	</form>
</body>
</html>