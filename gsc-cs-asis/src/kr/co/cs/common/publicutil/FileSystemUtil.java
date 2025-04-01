package kr.co.cs.common.publicutil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import kr.co.cs.common.config.Const;

public final class FileSystemUtil {
	
	private final static Logger errlogger = LogManager.getLogger("process.etc");
	
	/**
	 * method name : getFileList
	 * desc        : 특정폴더의 파일리스트를 가져온다.
	 * return	   : String
	 * create date : 2009. 04. 15
	 * @author     : buttle
	 * @version    : 1.0.0
	 */
	
	public static List<String> getFileList(String path){
		
		List<String> flist = new ArrayList<String>();
		File f = new File(path);
		File[] fs = f.listFiles();
		
		try {
			if(fs != null) {
				for(int i=0; i<fs.length; i++){
					flist.add(i, fs[i].getName());			
		        }
			}
		}catch (ArrayIndexOutOfBoundsException e) {		
			errlogger.debug("EXCEPTION :: " + e.getMessage());
		}
		
		return flist;
	}
	
	/**
	 * method name : getSendTemplate
	 * desc        : 파일경로를 받으면 내용을 넘겨준다.
	 * return	   : String
	 * create date : 2009. 04. 15
	 * @author     : buttle
	 * @version    : 1.0.0
	 */
	public static String getFileContents(String file) throws Exception {		
		StringBuffer template = new StringBuffer(100);
		FileInputStream fis = null;

		try {				
			fis = new FileInputStream(file);			
			InputStreamReader isr = new InputStreamReader(fis, Const.DEFAULT_CHARSET);			
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null) {				
				template.append(line);
			}

		}catch (ClassCastException e) {
			errlogger.debug("EXCEPTION :: " + e.getMessage());
			throw new Exception(e);
		}finally{
			try {	
				if (fis != null) fis.close();
			} catch (IOException e) {			
				errlogger.debug("EXCEPTION :: " + e.getMessage());
			}
		}
		
		return template.toString();
	} 
	
	public static String getByteFileContents(String path, String file) throws Exception  {
        return getByteFileContents(path + "/" + file);
    }
	
	public static String getByteFileContents(String file) throws Exception  {
        int totlen = 0;
        int len = 0;
        String ret = "";
        FileInputStream fis = null;
        DataInputStream sin = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            sin = new DataInputStream(fis);
            bos = new ByteArrayOutputStream();
            
            byte[] packet = new byte[2048];//2048
            
            while (true) {
            	
            	len = sin.read(packet, 0, packet.length);
                if(len > 0)
                {
                	totlen = totlen + len;
                	bos.write(packet,0,len);
                }
                if(len <= 0) {
                	break;
                }
            }
            //ret = new String(bos.toByteArray(),0, totlen, Const.DEFAULT_CHARSET);
            ret = new String(bos.toByteArray(), Const.DEFAULT_CHARSET);
        }
        catch (ArrayIndexOutOfBoundsException e){
        	errlogger.debug("EXCEPTION :: " + e.getMessage());
        	throw new Exception(e);
        }
        finally {
        	try {if(fis!=null) fis.close();}catch(IOException e){errlogger.debug("EXCEPTION :: " + e.getMessage());}
        	try {if(sin!=null) sin.close();}catch(IOException e){errlogger.debug("EXCEPTION :: " + e.getMessage());}
        	try {if(bos!=null) bos.close();}catch(IOException e){errlogger.debug("EXCEPTION :: " + e.getMessage());}        	
        }        
        return ret;
    }
	
	
	/**
	 * method name : makeFileContents
	 * desc        : 양식을 파일로 만든다.
	 * return	   : String[]
	 * create date : 2009. 04. 15
	 * @author     : buttle
	 * @version    : 1.0.0
	 */
	public static String[] makeFileContents(String strVal)  throws Exception  {
		
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String yyyyMmDdSss = dateFormat.format(calendar.getTime());
		String yyyyMm = yyyyMmDdSss.toString().substring(0,6);
		
		String filePath = null;
		if("WIN".equals(ComUtil.getOsMinName())) {
			filePath = Const.SAVEDFILE_LOCALPATH+yyyyMm+"/";
		} else {
			filePath = Const.SAVEDFILE_UNIXPATH+yyyyMm+"/";
		}
		
		String []returnVal = new String[2];
		
		//폴더 확인및 폴더 만들기
		File  filePathCheck = new File(filePath);
		
		if (!filePathCheck.isDirectory()){
			filePathCheck.mkdirs();
		}
		
		//Templete 만들기
		//sparrow 부적절한 난수생성
		Random r = new Random();
		r = SecureRandom.getInstance("SHA1PRNG");
		r.setSeed(new Date().getTime());
		
		double sparrowNum = r.nextDouble();
		//String filename = yyyyMmDdSss+String.valueOf(Math.random());
		String filename = yyyyMmDdSss+sparrowNum;
		FileOutputStream	fout= new FileOutputStream(filePath + filename);
		try {
			fout.write(strVal.getBytes());
			fout.close();
			returnVal[0] = filePath;
			returnVal[1] = filename;
			
		} catch (IOException e) {
			errlogger.debug("Exception ::" + e.getMessage());
			fout.close();
		}
		
		return returnVal;	
	}
	
	public static String[] makeFileContents(String strVal, String charSet)  throws Exception  {
		
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String yyyyMmDdSss = dateFormat.format(calendar.getTime());
		String yyyyMm = yyyyMmDdSss.toString().substring(0,6);
		
		String filePath = null;
		if("WIN".equals(ComUtil.getOsMinName())) {
			filePath = Const.SAVEDFILE_LOCALPATH+yyyyMm+"/";
		} else {
			filePath = Const.SAVEDFILE_UNIXPATH+yyyyMm+"/";
		}
		
		String []returnVal = new String[2];
		
		//폴더 확인및 폴더 만들기
		File  filePathCheck = new File(filePath);
		
		if (!filePathCheck.isDirectory()){								
			filePathCheck.mkdirs();
		}
		
		//Templete 만들기
		//sparrow 부적절한 난수생성
		Random r = new Random();
		r = SecureRandom.getInstance("SHA1PRNG");
		r.setSeed(new Date().getTime());
		
		double sparrowNum = r.nextDouble();
		
		//String filename = yyyyMmDdSss+String.valueOf(Math.random());
		String filename = yyyyMmDdSss+sparrowNum;
		FileOutputStream	fout= new FileOutputStream(filePath + filename);
		try {
			fout.write(strVal.getBytes(charSet));
			fout.close();
			returnVal[0] = filePath;
			returnVal[1] = filename;
			
		} catch (IOException e) {
			errlogger.debug("EXCEPTION :: " + e.getMessage());
			fout.close();
		}
		
		return returnVal;	
	}
}
