package kr.co.cs.common.publicutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil {

	public String ftpPut(String host, int port, String user, String pass, String localPath, String remotePath, String fname, String viewname, String mode) {
		
		FTPClient ftp = null;
		String msg = null;
		FileInputStream fis = null;
		try {
			
			ftp = new FTPClient();
			ftp.connect(host, port);
			
			if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				return "FAIL -> Host에 연결하지 못했습니다.";
			}
			
			if(!ftp.login(user, pass)) {
				ftp.logout();
				ftp.disconnect();
				return "FAIL -> Host에 로그인을 못했습니다.";
			}
			
			ftp.setFileType("BINARY".equals(mode)? FTP.BINARY_FILE_TYPE : FTP.ASCII_FILE_TYPE);  //BINARY, ASCII
			ftp.enterLocalPassiveMode();
			if(!"".equals(remotePath))
				ftp.changeWorkingDirectory(remotePath);
			
			File file = new File(localPath+fname);
			fis = new FileInputStream(file);
			
			if(ftp.storeFile(file.getName(), fis)) {
				ftp.rename(fname, viewname);
				msg = "OK -> 파일전송에 성공했습니다.";
			} else {
				msg = "FAIL -> 파일전송에 실패했습니다.";
			}
		} catch (FileNotFoundException fe) {			
			msg = "FAIL -> 파일전송에 실패했습니다.";
		} catch (Exception e) {			
			msg = "FAIL -> 파일전송에 실패했습니다.";
			
		} finally {
			if(fis!=null)
				try {fis.close(); }  catch(IOException e) {};
			if(ftp!=null && ftp.isConnected()) {
				try {ftp.logout(); } catch(FileNotFoundException e) {} catch (Exception e2) {}
				try {ftp.disconnect(); } catch(FileNotFoundException e) {} catch (Exception e2) {}
			}
		}
		return msg;		
	}
	
	public String ftpGet(String host, int port, String user, String pass, String localPath, String serverPath, String fname, String mode) {
		
		FTPClient ftp = null;
		String msg = null;
		FileOutputStream fos = null;
		try {
			
			ftp = new FTPClient();
			ftp.connect(host, port);
			
			if(!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				ftp.disconnect();
				return "FAIL -> Host에 연결하지 못했습니다.";
			}
			
			if(!ftp.login(user, pass)) {
				ftp.logout();
				ftp.disconnect();
				return "FAIL -> Host에 로그인을 못했습니다.";
			}
			
			ftp.setFileType("BINARY".equals(mode)? FTP.BINARY_FILE_TYPE : FTP.ASCII_FILE_TYPE);  //BINARY, ASCII
			ftp.enterLocalPassiveMode();
			if(!"".equals(serverPath))
				ftp.changeWorkingDirectory(serverPath);
			
			File file = new File(localPath, fname);
			fos = new FileOutputStream(file);
			
			if(ftp.retrieveFile(fname, fos)) {
				msg = "OK -> 파일받기에 성공했습니다.";
			} else {
				msg = "FAIL -> 파일받기에 실패했습니다.";
			}
			
		} catch (FileNotFoundException fe) {			
			msg = "FAIL -> 파일전송에 실패했습니다.";
		} catch (Exception e) {			
			msg = "FAIL -> 파일전송에 실패했습니다.";
			
		} finally {
			if(fos!=null)
				try {fos.close(); }  catch(IOException e) {};
			if(ftp!=null && ftp.isConnected()) {
				try {ftp.logout(); } catch(FileNotFoundException e) {} catch (Exception e2) {}
				try {ftp.disconnect(); } catch(FileNotFoundException e) {} catch (Exception e2) {}
			}
		}
		return msg;		
	}
	
}
