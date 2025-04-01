package kr.co.cs.common.publicutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtil {

	private Session session = null;
	private Channel channel = null;
	private ChannelSftp channelSftp = null;
	
	private final static Logger errlogger = LogManager.getLogger("process.etc");
	
	/**
	 * ���� ���ῡ �ʿ��� ������ ������ �ʱ�ȭ ��Ų��.
	 * @param host     �����ּ�
	 * @param userName ���ӿ� ���� ���̵�
	 * @param password ��й�ȣ
	 * @param port     ��Ʈ
	 */
	public void init(String host, String userName, String password, int port) {
		
		JSch jsch = new JSch();		
		try {
			session = jsch.getSession(userName, host, port);
			session.setPassword(password);
			
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			
			channel = session.openChannel("sftp");
			channel.connect();
			
		} catch (JSchException e) {
			errlogger.debug("JSchException :: " + e.getMessage());
		}
		
		channelSftp = (ChannelSftp)channel;
	}
	
	/**
	 * ���� ���� ���ε�
	 * @param dir       �����ų ���� ���(�����ּ�)
	 * @param localFile �������� ���(���ϰ�� + ���ϸ�)
	 * @param fileName  ���� ���� ��
	 * @return
	 */
	public boolean upload(String dir, String filePath) {
		boolean result = true;
		FileInputStream in = null;
		try {
			
			File file = new File(filePath);
			String fileName = file.getName();
			
			in = new FileInputStream(file);
						
			channelSftp.cd(dir);
			channelSftp.put(in, fileName);
		
		} catch ( IOException ie ) {
			errlogger.debug("SFTPUtil IOException :: " + ie.getMessage());
			result = false;
		} catch ( Exception e ) {
			errlogger.debug("SFTPUtil Exception :: " + e.getMessage());
			result = false;
			
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				errlogger.debug("SFTPUtil IOException :: " + e.getMessage());
			}
		}
		
		return result;
	}
	
	/**
	 * ���� ���� �ٿ�ε�
	 * @param dir                ������ ���(�������+���ϸ�)
	 * @param downloadFileName   �ٿ�ε��� ����(���ϸ�)
	 * @param path               ����� ��(���+���ϸ�)
	 */
	public boolean download(String dir, String downloadFileName, String path) {
		boolean result = true;
		InputStream in = null;
		FileOutputStream out = null;
		
//		try {
//			channelSftp.cd(dir);
//			in = channelSftp.get(downloadFileName);
//		} catch (SftpException e) {
//			result = false;
//			System.out.println("################## error 1");
//			e.printStackTrace();
//		}
		
		try {
			
			channelSftp.cd(dir);
			in = channelSftp.get(downloadFileName);
			
			File downloadFile = new File(path);
			out = new FileOutputStream(downloadFile);
			int i;
			
			while( (i = in.read()) != -1 ) {
				out.write(i);
			}
		} catch (SftpException e) {
			result = false;
			errlogger.debug("SFTPUtil SftpException :: " + e.getMessage());
		} catch (IOException e) {
			result = false;
			errlogger.debug("SFTPUtil IOException :: " + e.getMessage());
		} finally {
			try {
				if(in != null) {
					in.close();
				}
				if(out != null) {
					out.close();
				}
			} catch (IOException e) {
				errlogger.debug("SFTPUtil IOException :: " + e.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * �������� ������ ���´�.
	 */
	public void disconnection() {
		channelSftp.quit();
	}
	
	/**
	 * ���� ���� ��� ���ε�
	 * @param sftpHost        SFTP ���� �ּ�(host:IP)
	 * @param sftpUser        SFTP ���� USER
	 * @param sftpPass        SFTP ���� �н�����
	 * @param sftpPort        SFTP ���� ��Ʈ
	 * @param sftpWorkingDir  SFTP �۾� ���
	 * @param fileFullPath    ���ε� �� ���� ���
	 * @return
	 */
	public static boolean directUpload(
            String sftpHost, String sftpUser, String sftpPass, 
            int sftpPort, String sftpWorkingDir, String fileFullPath) throws IOException {
       
       boolean result = true;
       
       Session session = null;
       Channel channel = null;
       ChannelSftp channelSftp = null;
       
       try {
           JSch jsch = new JSch();
           session = jsch.getSession(sftpUser, sftpHost, sftpPort);
           session.setPassword(sftpPass);
           
           // Host ����.
           java.util.Properties config = new java.util.Properties();
           config.put("StrictHostKeyChecking", "no");
           session.setConfig(config);
           session.connect();
           
           // sftp ä�� ����.
           channel = session.openChannel("sftp");
           channel.connect();
           
           // ���� ���ε� ó��.
           channelSftp = (ChannelSftp) channel;
           channelSftp.cd(sftpWorkingDir);
           File f = new File(fileFullPath);
           String fileName = f.getName();
           //fileName = URLEncoder.encode(f.getName(),"UTF-8");
           channelSftp.put(new FileInputStream(f), fileName);
       } catch (IOException iex) {
    	   errlogger.debug("IOException ::" + iex.getMessage());
       } catch (Exception ex) {
    	   errlogger.debug("Exception ::" + ex.getMessage());
            System.out.println("Exception found while tranfer the response.");
            result = false;
       } finally {
           if(channelSftp !=null) channelSftp.exit();;
    	   if(channel !=null) channel.disconnect();;
    	   if(session !=null) session.disconnect();;
    	   
    	   /*
           channelSftp.quit();
           channelSftp.disconnect();
           channelSftp.isClosed();
           
           // ä�� ���� ����.
           channel.disconnect();
           channel.isClosed();
           
           // ȣ��Ʈ ���� ����.
           session.disconnect();
           */
       }
       
       return result;
   }
	
	public static void main(String args[]) {

        String host = "sftp�ּ�";
        int port = 22;
        String userName = "���̵�";
        String password = "��й�ȣ";
        String dir = "/�����/"; //������ ����� ��ġ�� ���
        String file = "f:\\test.txt(���ε��ų ����)";

        SFTPUtil util = new SFTPUtil();
        util.init(host, userName, password, port);
        util.upload(dir, new File(file).getPath());

        String fileName = "�ٿ�ε� ���� ���ϸ�"; //ex. "test.txt"
		String saveDir = "������ ��ġ"; //ex. "f:\\test3.txt"

        util.download(dir, fileName, saveDir);

        util.disconnection();
        System.exit(0);
    }
}
