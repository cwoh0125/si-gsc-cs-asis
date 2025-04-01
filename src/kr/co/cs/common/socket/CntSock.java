package kr.co.cs.common.socket;

import java.io.*;
import java.net.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import kr.co.cs.common.config.Const;

public class CntSock
{
	private final static Logger errlogger = LogManager.getLogger("process.etc");
	private String m_Host;
	private int m_Port;
	private int m_WaitTime;
    private Socket sock;
    private DataOutputStream sout;
    private DataInputStream sin;
	private int SOCK_TRANS_BUF_SIZE = 2048;
    
    public String TransData(String host, int port, String sendmsg, int timeout) {
        m_Host = host;
        m_Port = port;
        m_WaitTime = timeout;
        //System.out.println("host : " + host + " port : " + port);
        if(!Connect())
        	return "F Gateway Connect Error";
        if(!SendB(sendmsg))
        	return "F Was -> Gateway Send Error"; // Send Error
        
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
        return RecvB(); 
        
    }
    
    
    private boolean Connect() {
        try {
            if (m_Host == "" || m_Port == 0) return false;

            sock = new Socket(m_Host, m_Port);            
            sock.setSoTimeout(m_WaitTime);

            BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(sock.getOutputStream());
            sout = new DataOutputStream(bufferedoutputstream);
            BufferedInputStream bufferedinputstream = new BufferedInputStream(sock.getInputStream());
            sin = new DataInputStream(bufferedinputstream);
            
            return true;
        }
		catch (IOException e) {
			Close();
		    return false; 
		}
    }
  
    private boolean SendB(String Sstr) {
    	byte[] str = null;
    	try{
    		str = Sstr.getBytes(Const.KOREA_CHARSET);
            sout.write(str, 0, str.length);
            sout.flush();
            return true;
    	}
    	catch (ArrayIndexOutOfBoundsException e){
    		errlogger.debug("Exception ::" + e.getMessage());
    		Close();
    		return false;
    	}
    	catch (Exception e){
    		errlogger.debug("Exception ::" + e.getMessage());
    		Close();
    		return false;
    	}
    }
  
    private String RecvB() {
        try {
            int totlen = 0;
            int len = 0;
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] packet = new byte[SOCK_TRANS_BUF_SIZE];//2048
            
            while (true) {
            	
            	len = sin.read(packet, 0, packet.length);
                if(len > 0)
                {
                	totlen = totlen + len;
                	bos.write(packet,0,len);
                }
                if(len <= 0 || len < SOCK_TRANS_BUF_SIZE) {
                	break;
                }
            }
            return new String(bos.toByteArray(),Const.KOREA_CHARSET);
        }
        catch (ArrayIndexOutOfBoundsException e){
        	return "E Was <- Gateway Recv Error [" + e.toString() + " " + e.getMessage() + "]"; // Recv Error
        }
        catch (Exception e){
        	return "E Was <- Gateway Recv Error [" + e.toString() + " " + e.getMessage() + "]"; // Recv Error
        }
        finally {
        	Close();
        }
    }
    

	private void Close() {
		try {if (sock != null) sock.close();	} catch (IOException e) {errlogger.debug("Exception ::" + e.getMessage());} 
		try {if (sout != null) sout.close();	} catch (IOException e) {errlogger.debug("Exception ::" + e.getMessage());} 
		try {if (sin != null) sin.close();		} catch (IOException e) {errlogger.debug("Exception ::" + e.getMessage());} 
	}
}

