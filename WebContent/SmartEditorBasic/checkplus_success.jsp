<%@ page language="java" contentType="text/html;charset=euc-kr" %>
<%@ page import ="java.util.HashMap"%> 

<%
    Kisinfo.Check.CPClient kisCrypt = new  Kisinfo.Check.CPClient();

    String sEncodeData = request.getParameter("EncodeData");
    String sReserved1  = request.getParameter("param_r1");
    String sReserved2  = request.getParameter("param_r2");
    String sReserved3  = request.getParameter("param_r3");

    String sSiteCode 		= kr.co.cs.common.config.Const.NICE_SITE_CODE_CI;		// NICE�κ��� �ο����� ����Ʈ �ڵ�
    String sSitePassword 	= kr.co.cs.common.config.Const.NICE_SITE_PWD_CI;		// NICE�κ��� �ο����� ����Ʈ �н�����

    String sCipherTime = "";				 // ��ȣȭ�� �ð�
    String sRequestNumber = "";			 // ��û ��ȣ
    String sResponseNumber = "";		 // ���� ������ȣ
    String sAuthType = "";				   // ���� ����
    String sName = "";							 // ����
    String sDupInfo = "";						 // �ߺ����� Ȯ�ΰ� (DI_64 byte)
    String sConnInfo = "";					 // �������� Ȯ�ΰ� (CI_88 byte)
    String sBirthDate = "";					 // ����
    String sGender = "";						 // ����
    String sNationalInfo = "";       // ��/�ܱ������� (���߰��̵� ����)
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
        
        // ����Ÿ�� �����մϴ�.
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
            sMessage = "���ǰ��� �ٸ��ϴ�. �ùٸ� ��η� �����Ͻñ� �ٶ��ϴ�.";
            sResponseNumber = "";
            sAuthType = "";
        }
    }
    else if( iReturn == -1)
    {
        sMessage = "��ȣȭ �ý��� �����Դϴ�.";
    }    
    else if( iReturn == -4)
    {
        sMessage = "��ȣȭ ó�������Դϴ�.";
    }    
    else if( iReturn == -5)
    {
        sMessage = "��ȣȭ �ؽ� �����Դϴ�.";
    }    
    else if( iReturn == -6)
    {
        sMessage = "��ȣȭ ������ �����Դϴ�.";
    }    
    else if( iReturn == -9)
    {
        sMessage = "�Է� ������ �����Դϴ�.";
    }    
    else if( iReturn == -12)
    {
        sMessage = "����Ʈ �н����� �����Դϴ�.";
    }    
    else
    {
        sMessage = "�˼� ���� ���� �Դϴ�. iReturn : " + iReturn;
    }
    
    //XSS ó��
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
    <title>NICE�ſ������� - CheckPlus �������� �׽�Ʈ</title>
    <script language=javascript>
   		function onload() {
   			document.title = "receive"; 
    	}
    </script>
</head>
<body onload=onload();>
    <center>
    <p><p><p><p>
    <b>NICE ���������� �Ϸ� �Ǿ����ϴ�.</b><br>
    <input type=hidden name=ciData value="<%= sConnInfo %>">  
    <%= sMessage %><br>
    </center>
</body>
</html>