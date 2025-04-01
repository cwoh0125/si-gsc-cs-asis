<%@ page language="java" contentType="text/html;charset=euc-kr" %>

<%
    Kisinfo.Check.CPClient kisCrypt = new  Kisinfo.Check.CPClient();

    String sEncodeData = request.getParameter("EncodeData");
    String sReserved1  = request.getParameter("param_r1");
    String sReserved2  = request.getParameter("param_r2");
    String sReserved3  = request.getParameter("param_r3");

    String sSiteCode 		= kr.co.cs.common.config.Const.NICE_SITE_CODE_CI;		// NICE�κ��� �ο����� ����Ʈ �ڵ�
    String sSitePassword 	= kr.co.cs.common.config.Const.NICE_SITE_PWD_CI;		// NICE�κ��� �ο����� ����Ʈ �н�����

    String sCipherTime = "";					// ��ȣȭ�� �ð�
    String sRequestNumber = "";				// ��û ��ȣ
    String sErrorCode = "";						// ���� ����ڵ�
    String sAuthType = "";						// ���� ����
    String sMessage = "";
    String sPlainData = "";
    
    int iReturn = kisCrypt.fnDecode(sSiteCode, sSitePassword, sEncodeData);

    if( iReturn == 0 )
    {
        sPlainData = kisCrypt.getPlainData();
        sCipherTime = kisCrypt.getCipherDateTime();
        
        // ����Ÿ�� �����մϴ�.
        java.util.HashMap mapresult = kisCrypt.fnParse(sPlainData);
        
        sRequestNumber 	= (String)mapresult.get("REQ_SEQ");
        sErrorCode 			= (String)mapresult.get("ERR_CODE");
        sAuthType 			= (String)mapresult.get("AUTH_TYPE");
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
    <title>NICE�ſ������� - CheckPlus �������� �׽�Ʈ</title>
</head>
<body>
    <center>
    <p><p><p><p>
    <b>NICE ���������� �����Ͽ����ϴ�.<br><font color=red>���� â�� �ݰ� �ٽ� �õ����ֽñ� �ٶ��ϴ�.</font><br><br></b>
    <table border=0>
        <tr>
            <td>��ȣȭ�� �ð�</td>
            <td><%= sCipherTime %> (YYMMDDHHMMSS)</td>
        </tr>
        <tr>
            <td>��û ��ȣ</td>
            <td><%= sRequestNumber %></td>
        </tr>            
        <tr>
            <td>�������� ���� �ڵ�</td>
            <td><%= sErrorCode %></td>
        </tr>            
        <tr>
            <td>��������</td>
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