<%@ page contentType="text/html; charset=euc-kr" %>
<%@ page language="java"%>

<%@ page import="com.tobesoft.xplatform.tx.*" %>

<%@ page import="com.tobesoft.xplatform.data.*" %>
<%@ page import="com.tobesoft.xplatform.data.datatype.*" %>

<%
    //System.out.println("SESSION ID = " + session.getId());

    String strCharset = "euc-kr";

    /*********************************************************
     * request로 들어온 내용을 parsing하여
     * input variable list, input dataset list에 저장한다.
     * (MiPlatform 에서 보내온 데이터를 parsing한다.)
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
     * response로 보낼 내용을 생성한다.
     * output variable list, output dataset list에 저장한다.
     * (MiPlatform 이 받을 수 있는 데이터 형태로 가공)
     *********************************************************/
     out.clear();
     out=pageContext.pushBody();
    //PlatformResponse platformResponse = new PlatformResponse(response.getOutputStream(), PlatformType.CONTENT_TYPE_BINARY, strCharset);
    PlatformResponse platformResponse = new PlatformResponse(response.getOutputStream(), PlatformType.CONTENT_TYPE_XML, strCharset);
   // PlatformResponse platformResponse = new PlatformResponse(response.getOutputStream(), PlatformType.CONTENT_TYPE_SSV, strCharset); //SSV 형태로 설정을 합니다.
  	PlatformData outPD = platformRequest.getData();
    VariableList    outVariableList  = new VariableList();
    DataSetList     outDataSetList   = new DataSetList();
	
    //platformResponse.addProtocolType(PlatformType.PROTOCOL_TYPE_ZLIB);
    

    try {
    	System.out.println("5초 대기 시작");
        Thread.sleep(3000);
        System.out.println("5초 대기 끝");
        
    
		
        // Output Dataset 을 Output Dataset List 에 담는다.

        // Output Vairable 을 세팅한다.
        outVariableList.add("ErrorCode", 0);
        outVariableList.add("ErrorMsg",  "조회 성공");
        outVariableList.add("strOutputData", "※ Output Vairable을 받으려면, 화면의 전역변수로 선언하면 됩니다.");

    } catch(Exception e) {

        // Output Vairable 을 세팅한다.
        outVariableList.add("ErrorCode", -1);
        outVariableList.add("ErrorMsg",  e.toString());

    } finally {

        // 조회 결과(Output Dataset List, Output Variable List)를 MiPlatform 으로 전송
       outPD.setDataSetList(outDataSetList);
       outPD.setVariableList(outVariableList);
       platformResponse.setData(outPD);
       platformResponse.sendData();
    }
%>
