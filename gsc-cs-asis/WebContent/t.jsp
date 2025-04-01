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
System.out.println("77777");

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
                                                                        
System.out.println(sb.toString());

//String requestUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/cc/XcommonActionCS.do?method=XcommonUserTransaction";   //업무시스템 Agent경로 지정
String requestUrl = "http://192.168.7.71:7001/ARSinterfaceAction.do?method=XcommonUserTransaction";   //업무시스템 Agent경로 지정

URL url = new URL(requestUrl);
URLConnection conn = url.openConnection();

conn.setDoOutput(true);
//conn.setDoInput(true);	
conn.setUseCaches(false);	//캐시사용안함
conn.setConnectTimeout(60000); //응답 없을시 timeout 설정
conn.setRequestProperty("Content-Type","text/xml; charset=uft-8"); //xml 형태 데이터 요청 세팅

OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
String t = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Root> <Parameters>  <Parameter id=\"CallMethod\" type=\"STRING\">WASCall</Parameter> </Parameters> <Dataset id=\"INTERFACE_ENV\">  <ColumnInfo>   <Column id=\"IF_KIND\" type=\"STRING\" size=\"256\"/>   <Column id=\"REQ_SERVICE_METHOD\" type=\"STRING\" size=\"256\"/>   <Column id=\"REQ_SERVICE_ID\" type=\"STRING\" size=\"256\"/>   <Column id=\"RES_HEADER_SECTION\" type=\"STRING\" size=\"256\"/>   <Column id=\"RES_COLUMN_SECTION\" type=\"STRING\" size=\"256\"/>   <Column id=\"RES_RECORD_SECTION\" type=\"STRING\" size=\"256\"/>  </ColumnInfo>  <Rows>   <Row type=\"select\">    <Col id=\"IF_KIND\">WAS</Col>    <Col id=\"REQ_SERVICE_METHOD\"></Col>    <Col id=\"REQ_SERVICE_ID\"></Col>    <Col id=\"RES_HEADER_SECTION\"></Col>    <Col id=\"RES_COLUMN_SECTION\"></Col>    <Col id=\"RES_RECORD_SECTION\"></Col>   </Row>  </Rows> </Dataset> <Dataset id=\"INTERFACE_DATA\">  <ColumnInfo>   <Column id=\"IF_ID\" type=\"STRING\" size=\"256\"/>   <Column id=\"req_chnl_code\" type=\"STRING\" size=\"256\"/>   <Column id=\"input_user_id\" type=\"STRING\" size=\"256\"/>   <Column id=\"input_user_nm\" type=\"STRING\" size=\"256\"/>   <Column id=\"input_user_ip\" type=\"STRING\" size=\"256\"/>   <Column id=\"in_search_gbn\" type=\"STRING\" size=\"256\"/>   <Column id=\"in_bday\" type=\"STRING\" size=\"256\"/>   <Column id=\"in_cphn_head_tphn_no\" type=\"STRING\" size=\"256\"/>   <Column id=\"in_cphn_mid_tphn_no\" type=\"STRING\" size=\"256\"/>   <Column id=\"in_cphn_fnl_tphn_no\" type=\"STRING\" size=\"256\"/>  </ColumnInfo>  <Rows>   <Row type=\"select\">    <Col id=\"IF_ID\">IF033</Col>    <Col id=\"req_chnl_code\">610060</Col>    <Col id=\"input_user_id\">online</Col>    <Col id=\"input_user_nm\">ARS</Col>    <Col id=\"input_user_ip\">192.168.16.101</Col>    <Col id=\"in_search_gbn\">1</Col>    <Col id=\"in_bday\">19771230</Col>    <Col id=\"in_cphn_head_tphn_no\">010</Col>    <Col id=\"in_cphn_mid_tphn_no\">2014</Col>    <Col id=\"in_cphn_fnl_tphn_no\">1215</Col>   </Row>  </Rows> </Dataset></Root>";
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
