<%@ page contentType = "application/xml; charset=utf-8" %><%@ page import = "
java.net.URL
,java.net.URLConnection
,java.net.URLEncoder
,java.net.HttpURLConnection
,java.io.OutputStreamWriter
,java.io.InputStreamReader
,java.io.InputStream
,java.io.BufferedReader
,javax.xml.parsers.DocumentBuilderFactory
,javax.xml.parsers.DocumentBuilder
,org.w3c.dom.Document" %><%!
	public static String isnull(String s){
        if(s == null){
        	s = "";
        }
        
        return s;
	}
%><%

String MODE 	= isnull(request.getParameter("MODE"));		//구분
String TRANCODE = isnull(request.getParameter("TRANCODE"));	//업무구분
String CNTRCD	= isnull(request.getParameter("CNTRCD"));	//지점코드1
String GRPCD	= isnull(request.getParameter("GRPCD"));	//그룹코드

String ROW_DATE     = isnull(request.getParameter("ROW_DATE"));	    //년월일시분초
String STARTTIME    = isnull(request.getParameter("STARTTIME"));	//현재시간
String SPLIT        = isnull(request.getParameter("SPLIT"));		//스킬코드
String INCALLS      = isnull(request.getParameter("INCALLS"));		//인입호수
String ACDCALLS     = isnull(request.getParameter("ACDCALLS"));	    //응답호수
String ACCEPTABLE   = isnull(request.getParameter("ACCEPTABLE"));	//20초이내응답
String WAITCALL     = isnull(request.getParameter("WAITCALL"));	    //대기호수
String ABNCALLS     = isnull(request.getParameter("ABNCALLS"));	    //포기호수
String DISCCALLS    = isnull(request.getParameter("DISCCALLS"));	//실패호수
String ANSTIME      = isnull(request.getParameter("ANSTIME"));		//응답시간
String BUSYCALLS    = isnull(request.getParameter("BUSYCALLS"));	//NONSVC
String CALLSOFFERED = isnull(request.getParameter("CALLSOFFERED"));	//총 콜수

String curdType     = "";

//TRANCODE = "VIEW1010.SELECT_BIBD_STTC_01";

if("I".equalsIgnoreCase(MODE)){
	curdType = "INSERT";
}else if("U".equalsIgnoreCase(MODE)){
	curdType = "UPDATE";
}else{
	curdType = "SELECT";
}

StringBuffer sb = new StringBuffer();

sb.append("<?xml version='1.0' encoding='UTF-8'?>                         ").append("\r\n");
sb.append("<Root>                                                         ").append("\r\n");
sb.append("<Parameters>                                                   ").append("\r\n");
sb.append("<Parameter id='CallMethod' type='STRING'>WASCall</Parameter>   ").append("\r\n");
sb.append("</Parameters>                                                  ").append("\r\n");
sb.append("<Dataset id='INTERFACE_ENV'>                                   ").append("\r\n");
sb.append("<ColumnInfo>                                                   ").append("\r\n");
sb.append("<Column id='IF_KIND' type='STRING' size='256'/>                ").append("\r\n");
sb.append("<Column id='REQ_SERVICE_METHOD' type='STRING' size='256'/>     ").append("\r\n");
sb.append("<Column id='REQ_SERVICE_ID' type='STRING' size='256'/>         ").append("\r\n");
sb.append("<Column id='RES_HEADER_SECTION' type='STRING' size='256'/>     ").append("\r\n");
sb.append("<Column id='RES_COLUMN_SECTION' type='STRING' size='256'/>     ").append("\r\n");
sb.append("<Column id='RES_RECORD_SECTION' type='STRING' size='256'/>     ").append("\r\n");
sb.append("</ColumnInfo>                                                  ").append("\r\n");
sb.append("<Rows>                                                         ").append("\r\n");
sb.append("<Row type='select'>                                            ").append("\r\n");
sb.append("<Col id='IF_KIND'>WAS</Col>                                    ").append("\r\n");
sb.append("<Col id='REQ_SERVICE_METHOD'></Col>                            ").append("\r\n");
sb.append("<Col id='REQ_SERVICE_ID'></Col>                                ").append("\r\n");
sb.append("<Col id='RES_HEADER_SECTION'></Col>                            ").append("\r\n");
sb.append("<Col id='RES_COLUMN_SECTION'></Col>                            ").append("\r\n");
sb.append("<Col id='RES_RECORD_SECTION'></Col>                            ").append("\r\n");
sb.append("</Row>                                                         ").append("\r\n");
sb.append("</Rows>                                                        ").append("\r\n");
sb.append("</Dataset>                                                     ").append("\r\n");
sb.append("<Dataset id='INTERFACE_DATA'>                                  ").append("\r\n");
sb.append("<ColumnInfo>                                                   ").append("\r\n");
sb.append("<Column id='IF_ID' type='STRING' size='256'/>                  ").append("\r\n");
sb.append("<Column id='req_chnl_code' type='STRING' size='256'/>          ").append("\r\n");
sb.append("<Column id='input_user_id' type='STRING' size='256'/>          ").append("\r\n");
sb.append("<Column id='input_user_nm' type='STRING' size='256'/>          ").append("\r\n");
sb.append("<Column id='input_user_ip' type='STRING' size='256'/>          ").append("\r\n");
sb.append("<Column id='in_cphn_head_tphn_no' type='STRING' size='256'/>   ").append("\r\n");
sb.append("<Column id='in_cphn_mid_tphn_no' type='STRING' size='256'/>    ").append("\r\n");
sb.append("<Column id='in_cphn_fnl_tphn_no' type='STRING' size='256'/>    ").append("\r\n");
sb.append("<Column id='in_authn_sbjt_cd' type='STRING' size='256'/>       ").append("\r\n");
sb.append("</ColumnInfo>                                                  ").append("\r\n");
sb.append("<Rows>                                                         ").append("\r\n");
sb.append("<Row type='select'>                                            ").append("\r\n");
sb.append("<Col id='IF_ID'>IF037</Col>                                    ").append("\r\n");
sb.append("<Col id='req_chnl_code'>610060</Col>                           ").append("\r\n");
sb.append("<Col id='input_user_id'>online</Col>                           ").append("\r\n");
sb.append("<Col id='input_user_nm'>ARS</Col>                              ").append("\r\n");
sb.append("<Col id='input_user_ip'>192.168.16.101</Col>                   ").append("\r\n");
sb.append("<Col id='in_cphn_head_tphn_no'>010</Col>                       ").append("\r\n");
sb.append("<Col id='in_cphn_mid_tphn_no'>4664</Col>                       ").append("\r\n");
sb.append("<Col id='in_cphn_fnl_tphn_no'>7587</Col>                       ").append("\r\n");
sb.append("<Col id='in_authn_sbjt_cd'>02</Col>                            ").append("\r\n");
sb.append("</Row>                                                         ").append("\r\n");
sb.append("</Rows>                                                        ").append("\r\n");
sb.append("</Dataset>                                                     ").append("\r\n");
sb.append("</Root>                                                        ").append("\r\n");
                                                                        


//String requestUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/cc/XcommonActionCS.do?method=XcommonUserTransaction";   //업무시스템 Agent경로 지정
String requestUrl = "/http://192.168.7.71:7001/ARSinterfaceAction.do?method=XcommonUserTransaction";   //업무시스템 Agent경로 지정

URL url = new URL(requestUrl);
URLConnection conn = url.openConnection();

conn.setDoOutput(true);
//conn.setDoInput(true);	
conn.setUseCaches(false);	//캐시사용안함
conn.setConnectTimeout(60000); //응답 없을시 timeout 설정
conn.setRequestProperty("Content-Type","text/xml; charset=uft-8"); //xml 형태 데이터 요청 세팅

OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

wr.write(sb.toString());
wr.flush();

BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

String line; 
while ((line = rd.readLine()) != null) {
	if(!"".equals(line.replaceAll(" ", ""))){
		out.println(line);
	}
}


%>
