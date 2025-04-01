package kr.co.cs.common.publicutil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.co.cs.common.config.Const;

public final class ComUtil {

	private ComUtil() {};
	/*
	 *  다양한 pattern으로 날짜와 시간을 String으로 반환한다.
	 */
	public static String getCurDateTime(String pattern) {
		SimpleDateFormat curDate = new SimpleDateFormat(pattern);
		Date currentDate = new Date();
		return new String(curDate.format(currentDate));
	}
 
	/*
	 * 운영계일때 TRUE 리턴
	 */
	public static boolean isProd() {
		String server_name = "";
		try {
			server_name = InetAddress.getLocalHost().getHostName();
		} catch(UnknownHostException e) {}
		
		if(server_name.trim().equals(Const.WAS1NAME) || server_name.trim().equals(Const.WAS2NAME)){
			return true;	//운영
		} else {
			return false;
		}
	}
	
	/*
	 * OS가 윈도운인지 유닉스인지 리턴
	 */
	public static String getOsName() {
		return System.getProperty("os.name").toUpperCase();
	}
	
	public static String getOsMinName() {
		return System.getProperty("os.name").toUpperCase().substring(0, 3);
	}
}
