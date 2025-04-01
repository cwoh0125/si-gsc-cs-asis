package kr.co.cs.common.publicutil;

import java.net.ConnectException;
import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;



public class HttpsClient {
	
	private final static Logger errlogger = LogManager.getLogger("process.etc");
	
	
	public HttpClient getNewHttpClient() {
	    try {
	    	
	    	System.out.println("========================httpclient==========================");
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        
	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	        
	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
	        return new DefaultHttpClient(ccm, params);
	    } catch (ConnectException ce) {
	    	errlogger.debug("HttpsClient Exception ::" + ce.getMessage());
	        return new DefaultHttpClient();
	    } catch (Exception e) {
	    	errlogger.debug("HttpsClient Exception ::" + e.getMessage());
	        return new DefaultHttpClient();
	    }
	}

}
