package kr.co.cs.common.publicutil;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;

/**
 * file name   : FileFtp.java
 * desc        : 
 * create date : 2009. 04. 15
 * @author     : buttle
 * @version    : 1.0.0
 */
public class FileFtp{

	private final static Logger errlogger = LogManager.getLogger("process.etc");
	static FTPClient ftpClient = null;
	
	/**
	 * method name : ftpFileSend
	 * desc        : 파일전송을 한다.
	 * return	   : void
	 * create date : 2009. 04. 15
	 * @author     : buttle
	 * @version    : 1.0.0
	 */
	public void ftpFileSend(String fileName, String filePath, String host, int port, String user, String pass) throws Exception{
  
		try{
			ftpClient = new FTPClient();
			ftpClient.setControlEncoding("UTF-8");
			
			ftpClient.login(user, pass);
			
		}catch (IOException e) {
			System.out.println("login failed");
		}
		
		try{
			System.out.println("FTP server connection OK");			
			//ftpClient.binary();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			putCommand(fileName, filePath);
			
		}catch (IOException e) {
			System.out.println("ftpclient failed");
		}
		
		try{
			//ftpClient.closeServer();
			ftpClient.logout();
		}catch (IOException e) {
			System.out.println("logout failed");
		}
				
	}

	public static void putCommand(String fileName, String filePath) throws Exception{
		if ("".equals(fileName) || fileName == null){
			System.out.println("fileName error");
		}

		FileInputStream f = new FileInputStream(filePath + fileName);
		//TelnetOutputStream t = ftpClient.put("./" + fileName);
		TelnetOutputStream t = (TelnetOutputStream) ftpClient.appendFileStream("./" + fileName);

		try{
			byte[] buffer = new byte[4096];
			int bytes_read;
			while ((bytes_read = f.read(buffer))!= -1){
				t.write(buffer,0,bytes_read);
			}
		}catch (ArrayIndexOutOfBoundsException e) {
			errlogger.debug("Exception :: " + e.getMessage());
		}finally{
			f.close();
			t.close();
		}
	}	

}
