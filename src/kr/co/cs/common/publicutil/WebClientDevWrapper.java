package kr.co.cs.common.publicutil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.text.DefaultCaret;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class WebClientDevWrapper {
	
	private final static Logger logger = LogManager.getLogger("process.if");
	private final static Logger errlogger = LogManager.getLogger("process.etc");

//public static String wrapClient() {
	public static String[] wrapClient(String sendurl,List<BasicNameValuePair> sdata, String inCharSet, String outCharSet) throws NoSuchAlgorithmException, KeyManagementException, IOException {
	
		String startTime = ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss");	
		
		 logger.debug("=============[httpcall]===============");
		 System.out.println("=================httpscall===========================");
		
		 
		
		 HttpClient httpclient = new DefaultHttpClient();
		 
	    UrlEncodedFormEntity reqEntity = null;
	    HttpEntity   resEntity = null;
	    String[] ret = new String[3];
	    
	    InputStream iis = null;
	    ByteArrayOutputStream bos = null;
	    try {		
	   	
	    System.out.println("================================X509TrustManager=======================================");	
	   
        X509TrustManager easyTrustManager = new X509TrustManager() {        		       
        	
          public X509Certificate[] getAcceptedIssuers() {
        	  System.out.println("getAcceptedIssuers");
                  return null;
              }
  
			public void checkClientTrusted(X509Certificate[] arg0, java.lang.String arg1) throws CertificateException {
				// TODO Auto-generated method stub
				System.out.println("checkClientTrusted");
				
			}
			public void checkServerTrusted(X509Certificate[] arg0, java.lang.String arg1) throws CertificateException {
				// TODO Auto-generated method stub
				System.out.println("checkServerTrusted");
			} 
        };       	

		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");        	 //SSLContext지정된 시큐어 소켓 프로토콜
		
        	//SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  //이게 안됨..
			
        	sslContext.init(null, new TrustManager[]{easyTrustManager}, new java.security.SecureRandom()); 
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext);	 
	        HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
	        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);	      
	        
	        //Scheme sch = new Scheme("https",socketFactory,443);
	        
	        //base.getConnectionManager().getSchemeRegistry().register(sch);  한줄요
	        
	         
	        @SuppressWarnings("deprecation")
	        //SingleClientConnManager ccm = new SingleClientConnManager(httpclient.getParams(),Registry)
			ClientConnectionManager ccm =  httpclient.getConnectionManager();
	        
	       
	   
	        SchemeRegistry sr = ccm.getSchemeRegistry();	        
	   
	        sr.register( new Scheme("https",socketFactory,4431));
	        
	        
	        HttpsURLConnection.setDefaultHostnameVerifier( hostnameVerifier);
	        
	       HttpPost httppost = new HttpPost(sendurl);       
	        
	      //  PostMethod httppost = new PostMethod(sendurl);	    	
	      //  RequestEntity reqEntity  = new StringRequestEntity((String) sdata, inCharSet, "UTF-8");		
	        
	        reqEntity = new UrlEncodedFormEntity(sdata, inCharSet);	        
	      
	         httppost.setEntity(reqEntity);	         
	         System.out.println("3333333333333333333333333");
	         
	         HttpResponse response =  httpclient.execute(httppost);
	        
	      
	       System.out.println(response.getProtocolVersion()+"//////////////"+response.getStatusLine().toString());
	        
	        //httpclient timeout 셋팅
	       // HttpParams params = httpclient.getParams();
	    	//HttpConnectionParams.setConnectionTimeout(params, 30000);
	    	//HttpConnectionParams.setSoTimeout(params, 30000);
	        
	        
	    	ret[0] = String.valueOf(response.getStatusLine().getStatusCode());
	        ret[1] = response.getStatusLine().getReasonPhrase();            
	        
	     
	        System.out.println("====================="+ret[0]+"/////////////////////"+ret[1]);
	        
	        if("200".equals(ret[0])) {
	            int totlen = 0;
	            int len = 0;
	            resEntity = response.getEntity();
//	            ret[2] = EntityUtils.toString(resEntity); ==> 이게 되면 좋은데... 이상하게 exception 떨어진다.. 짜증....
	            iis = resEntity.getContent();
	            bos = new ByteArrayOutputStream();
	            byte[] packet = new byte[2048];
	            
	            if(iis!=null) {
		            while (true) {	            
		            	len = iis.read(packet, 0, packet.length);
		                if(len > 0) {
		                	totlen = totlen + len;
		                	bos.write(packet,0,len);
		                }
		                if(len <= 0) {
		                	break;
		                }
		            }
		            
		          //  logger.debug("[outCharSet]["+outCharSet+"]");		            
		            ret[2] = new String(bos.toByteArray(), outCharSet);
		         //   logger.debug("[outCharSet_1]["+outCharSet+"]");
	            } else {
	                ret[0] = "201";
	                ret[1] = "응답받은 데이터가 없습니다.";
	            	ret[2] = "No return data";
	            }

	        } else {
	            ret[2] = "No return data";
	        }
	       // logger.debug("[resEntity]["+resEntity+"]");
	        logger.debug("[httpcall]["+ret[2]+"]");            
	    } catch (IOException ex) {
	        throw ex;
	    } catch(KeyManagementException e) {
	    	errlogger.debug("KeyManagementException :: " + e.getMessage());
	    	throw e;
	    }catch(NoSuchAlgorithmException e) {
	    	errlogger.debug("NoSuchAlgorithmException :: " + e.getMessage());
	    	throw e;
	    }
	    finally {
	    	
	    	try {if(iis!=null) iis.close();}catch(FileNotFoundException e){	errlogger.debug("iis Exception :: " + e.getMessage());}
	    	try {if(bos!=null) bos.close();}catch(FileNotFoundException e){errlogger.debug("bos Exception :: " + e.getMessage());}    	
	        httpclient.getConnectionManager().shutdown();
	        try {
	        	StringBuffer sb = new StringBuffer(100);
	            sb.append("[URL][");
	            sb.append(sendurl);
	            sb.append("][SENDTIME][");
	            sb.append(startTime);
	            sb.append("][RECVTIME][");
	            sb.append(ComUtil.getCurDateTime("yyyy-MM-dd HH:mm:ss"));
	            sb.append("][SENDDATA][");
	            sb.append(sdata.toString());
	            sb.append("][RECVDATA][");
	            sb.append(ret[0]);
	            sb.append(" : ");
	            sb.append(ret[2]);
	            sb.append("]");
	    		logger.info(sb.toString());
	    		//System.out.println("======>"+sb.toString());
	        } catch(ClassCastException ce) {errlogger.debug("ClassCastException ::" + ce.getMessage());}

	    }
	    return ret;        
	}
}
