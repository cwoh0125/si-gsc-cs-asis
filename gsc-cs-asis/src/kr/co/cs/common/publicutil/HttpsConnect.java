package kr.co.cs.common.publicutil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;


import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;



import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;




public class HttpsConnect {
		public  String  gethttps(String urlstring) throws IOException, NoSuchAlgorithmException,KeyManagementException {
			
			URL url = new URL(urlstring);
			
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(30000);
		    //conn.setRequestProperty("User-Agent","application/json");
		    //conn.setRequestProperty("Accept-Language", "euc-kr");
			
			System.out.println("respCode>>>"+conn.getResponseCode());
		
			conn.setHostnameVerifier(new HostnameVerifier() {
				
				public boolean verify( String hostname, SSLSession session) {
					 
					return true;
				}
			});
			
			
			SSLContext context = SSLContext.getInstance("TLS");
			
			//SSL Seting 
			context.init(null,null,null);
			/*
			context.init(null, new TrustManager[]{				
					
				 new javax.net.ssl.X509TrustManager(){			 

							public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
								// TODO Auto-generated method stub
								
							}

							public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
								// TODO Auto-generated method stub
								
							}

							public X509Certificate[] getAcceptedIssuers() {
								// TODO Auto-generated method stub
								return null;
							}
					
					}
			
			}, null);
			*/
			//no validateion for now end
			
			conn.setSSLSocketFactory(context.getSocketFactory());
			conn.connect();
			conn.setInstanceFollowRedirects(true);
			
			
			//print response from host
			InputStream inStream = conn.getInputStream();
			
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(inStream));
			
			String str;
			String result = "";		
			
			
			while ((str = bufReader.readLine()) != null ){
				System.out.printf("%s",str);
				System.out.println();
				result = result.concat(str);
			}
			return result;
		}		

}
