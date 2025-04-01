<%@ page contentType="text/html; charset=euc-kr" %>
<%@ page language="java"%>

<%@ page import="com.tobesoft.xplatform.tx.*" %>

<%@ page import="com.tobesoft.xplatform.data.*" %>
<%@ page import="com.tobesoft.xplatform.data.datatype.*" %>

<%
    //System.out.println("SESSION ID = " + session.getId());

    String strCharset = "euc-kr";

    /*********************************************************
     * request�� ���� ������ parsing�Ͽ�
     * input variable list, input dataset list�� �����Ѵ�.
     * (MiPlatform ���� ������ �����͸� parsing�Ѵ�.)
     *********************************************************/
    PlatformRequest platformRequest = new PlatformRequest(request.getInputStream(), PlatformType.CONTENT_TYPE_BINARY);
    platformRequest.addProtocolType(PlatformType.PROTOCOL_TYPE_ZLIB); 
    
    
    platformRequest.receiveData();
    
    PlatformData inPD = platformRequest.getData();
    
	System.out.println("=====================================>test");
   // System.out.println(inPD.saveXml());  
    
    VariableList    inVariableList  = inPD.getVariableList();
    DataSetList     inDataSetList   = inPD.getDataSetList();


   // String strUserID   = inVariableList.getString("userid");
    //String strUserName = inVariableList.getString("username");
    
    
    
    //System.out.println("input userid   = " + strUserID);
    //System.out.println("input username = " + strUserName);

    /*********************************************************
     * response�� ���� ������ �����Ѵ�.
     * output variable list, output dataset list�� �����Ѵ�.
     * (MiPlatform �� ���� �� �ִ� ������ ���·� ����)
     *********************************************************/
     out.clear();
     out=pageContext.pushBody();
    //PlatformResponse platformResponse = new PlatformResponse(response.getOutputStream(), PlatformType.CONTENT_TYPE_BINARY, strCharset);
    PlatformResponse platformResponse = new PlatformResponse(response.getOutputStream(), PlatformType.CONTENT_TYPE_XML, strCharset);
   // PlatformResponse platformResponse = new PlatformResponse(response.getOutputStream(), PlatformType.CONTENT_TYPE_SSV, strCharset); //SSV ���·� ������ �մϴ�.
  	PlatformData outPD = platformRequest.getData();
    VariableList    outVariableList  = new VariableList();
    DataSetList     outDataSetList   = new DataSetList();
	
    //platformResponse.addProtocolType(PlatformType.PROTOCOL_TYPE_ZLIB);
    

    try {
    	System.out.println("5�� ��� ����");
        Thread.sleep(3000);
        System.out.println("5�� ��� ��");
        
    
		
        // Output Dataset �� Output Dataset List �� ��´�.

        // Output Vairable �� �����Ѵ�.
        outVariableList.add("ErrorCode", 0);
        outVariableList.add("ErrorMsg",  "��ȸ ����");
        outVariableList.add("strOutputData", "�� Output Vairable�� ��������, ȭ���� ���������� �����ϸ� �˴ϴ�.");

    } catch(Exception e) {

        // Output Vairable �� �����Ѵ�.
        outVariableList.add("ErrorCode", -1);
        outVariableList.add("ErrorMsg",  e.toString());

    } finally {

        // ��ȸ ���(Output Dataset List, Output Variable List)�� MiPlatform ���� ����
       outPD.setDataSetList(outDataSetList);
       outPD.setVariableList(outVariableList);
       platformResponse.setData(outPD);
       platformResponse.sendData();
    }
%>
