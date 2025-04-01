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

	BufferedReader _rd = null;
_rd = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

String _line = "";
String _buffer = "";
boolean isFirstLine = true;
while ((_line = _rd.readLine()) != null ){
	
	if(isFirstLine){
		isFirstLine = false;
		_buffer = _line;
	}else{
		_buffer = _buffer + "\r\n" + _line;
	}
}
System.out.println(_line);
%><%=_line%>
