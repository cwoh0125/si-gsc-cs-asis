package kr.co.cs.common.publicutil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.co.cs.persistence.dao.CommonDao;

public class javaTestUtil {
	 private static String   PK = "1234567890ABCDEFGHIJKLMNOPQRSTUV"; // 개발
	 private static String IV = PK.substring(0,16); // 16byte
	 private static String Alg = "AES/CBC/PKCS5Padding";
	 
	 private static CommonDao commonDao = null; 
		public void setCommonDao(CommonDao commonDao) {
			this.commonDao = commonDao;
		}
	 
	 public static void TmDsftAcpnUpdate(String param) throws Exception {
	    	//return set
	    	String resultRtn = null;
	    	JSONObject setJsObj = new JSONObject();
			
			BufferedReader br = null;
			InputStream is = null;
			HashMap<String, String> map = new HashMap<String, String>();
			
			//DB Update 결과값
			int result = 0;
			
			//요청값을 jsonObject parsing
			JSONParser jp = new JSONParser();
			JSONObject parmJsObj = new JSONObject();
			
			try {
				parmJsObj =  (JSONObject) jp.parse(param);
				Iterator it = parmJsObj.entrySet().iterator();
				
				while (it.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
					map.put(entry.getKey(), entry.getValue());
				}
				
				for(String key : map.keySet()) {
					System.out.println("key :" + key + " | val : " + map.get(key) );
				}
				
				//String sqlmapid = "CMP180.Set_TM_DSFT_ACPN_PROC_UPDATE";
				//result = commonDao.update(sqlmapid, map);
				
			} catch (Exception e) {
				e.printStackTrace();
				setJsObj.put("RETURN", "시스템 오류입니다. 관리자에게 문의주세요.");
				setJsObj.put("RETURN_CODE", "99");
			} finally {
				if(is != null) {is.close();}
				if(br != null) {br.close();}
				
				System.out.println(setJsObj.toString());
				//return 암호화
				resultRtn = encrypt(setJsObj.toString());
			}
	    	
	    }
	
	public static String encrypt(String sdata) throws Exception {   	

        Cipher cipher = Cipher.getInstance(Alg);
        
        SecretKeySpec keySpec = new SecretKeySpec(PK.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);	        

        byte[] encrypted = cipher.doFinal(sdata.getBytes("UTF-8"));
        String encodeBytes = new String(Base64.encodeBase64(encrypted));        
        
        return encodeBytes;
    }

    

    public static String decrypt(String cipherText) throws Exception {	    	
    	cipherText = "XNM8qpACZkp+COPz4IyJDmQ71IjrViR2c8a18QUcLVv8gcCSz47/6WlS33RuqMjVjU+Z2OJ3eH5jJjyacwTkkaSqYPx/otQ4O6N3RfCnrUB2JUXvEsKUrfReUlIuaonq9weP9sF0/MO2q7U3klKyI9J5zLvZaY0Ht5sUU5wO17ivA8MuVxZXNS8+Qv6s6UpjhZ7Li8CM0ihwisYUbH0ZtXMwv42JwGCbWfl2wF2K53smFmRO/K3qh34//l/FypPG/TdLiOa7iRnCiDlvl8FJuQ==";
    	if(cipherText == null) {
    		return "";
    	}
        
        Cipher cipher = Cipher.getInstance(Alg);
        SecretKeySpec keySpec = new SecretKeySpec(PK.getBytes(), "AES");
        System.out.println();
        IvParameterSpec ivParamSpec = new IvParameterSpec(IV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);	       
        
        byte[] decodeBytes = Base64.decodeBase64(cipherText.getBytes());
        return new String(cipher.doFinal(decodeBytes), "UTF-8");              
    }
    
    public static void main(String[] agrs) throws Exception {
    	/* 복호화 테스트
    	String result = decrypt("test");
    	
    	System.out.println(result);
    	*/
    	
    	/* DB Update 테스트
    	String param = "{\"PROC_RSLT_CTT\":\"처리완료\",\"DSPSR_ID\":\"NP984\",\"PROC_DTM\":\"202211291600\",\"PROC_RSLT_INPUT_DTM\":\"202211291600\",\"OB_CODE1\":\"1\",\"OB_CODE2\":\"1\",\"DSFT_ACPN_SEQ_NO\":\"CMP010202211291551479897NP847\"}";
    	TmDsftAcpnUpdate(param);
    	*/
    	
    	/* 유비커스 쿼리 확인 
    	ubiDbSel();
    	*/
    	
    	/* java 연산테스트 */
    	javaTest();
    }

	private static void javaTest() {
		System.out.println("##JAVA Practice##");
		
		//사칙연산
		int i = 1;
		int j = 2;
		
		int sum = i+j;
		
		System.out.println(sum);
	}

	private static void ubiDbSel() {
		StringBuffer sb = new StringBuffer();
        sb.append("SELECT YYYYMMDDHHMM+'KST' i_time_key , \n");
        //★TODO 교체 필요
//        sb.append("   (case when i_usr_id = 'GSCUser901' then 'NP650' when i_usr_id = 'GSCUser902' then 'NP648' else substring(i_usr_id,1,6)  end) i_usr_id,  \n");
        sb.append("  cmpny_emp_cd i_usr_id,        \n");
        sb.append("  CONVERT(NUMERIC(18,0), (ROUND( sum(ISNULL(tDialing,0))/1000,0))) i_dialing_drtm,       \n");
        sb.append("  CONVERT(NUMERIC(18,0), (ROUND(SUM(i_otbnd_drtm)/1000,0))) i_otbnd_drtm,    \n");
        sb.append("  count(case when tConnected > 0 then 1 end) i_otbnd_house,      \n");
        sb.append("  count(1) i_dialing_ncnt        \n");
        sb.append("FROM    \n");
        sb.append("(    \n");
        sb.append("     SELECT yyyymmdd + HH+    \n");
        sb.append("             (CASE WHEN MM >= 0 AND MM < 15 THEN '00'     \n");
        sb.append("             WHEN MM >= 15 AND MM < 30 THEN '15'    \n");
        sb.append("             WHEN MM >= 30 AND MM < 45 THEN '30'    \n");
        sb.append("             WHEN MM >=45 AND MM < 60 THEN '45'    \n");
        sb.append("             END ) YYYYMMDDHHMM,    \n");
        sb.append("             i_usr_id,    \n");
        sb.append("             tDialing,    \n");
        sb.append("             i_otbnd_drtm,    \n");
        sb.append("             tConnected    \n");
        sb.append("      FROM    \n");
        sb.append("     (    \n");
        sb.append("               SELECT yyyymmdd, HHMM, SUBSTRING(HHMM,1,2) HH,     \n");
        sb.append("               CONVERT(INT,SUBSTRING(HHMM,3,2)) MM,    \n");
        sb.append("               i_usr_id, tDialing, i_otbnd_drtm, tConnected    \n");
        sb.append("               FROM    \n");
        sb.append("               (    \n");
        sb.append("               SELECT     \n");
        sb.append("                     CONVERT(varchar(8), DATEADD(SECOND, +32400, InitiatedDateTimeUTC),112) AS yyyymmdd,    \n");
        sb.append("                     REPLACE(CONVERT(varchar(6), DATEADD(SECOND, +32400, InitiatedDateTimeUTC),108),':','')  AS HHMM,    \n");
        sb.append("                     LastLocalUserId i_usr_id,     \n");
        sb.append("                     tDialing,    \n");
        sb.append("                     iSNULL(tConnected,0)+ISNULL(tConference,0)+ISNULL(tHeld,0) AS i_otbnd_drtm,        \n");
        sb.append("                     tConnected    \n");
        sb.append("               FROM InteractionSummary  with(NOLOCK)      \n");
        sb.append("               WHERE direction = 2 AND MediaType = 0        \n");
        sb.append("                  AND InitiatedDateTimeUTC BETWEEN  DATEADD(SECOND, -32400, CONVERT(DATETIME, convert(varchar(8),getdate(),112)+' 00:00:00', 108))         \n");
        sb.append("                  AND DATEADD(SECOND, -32400, CONVERT(DATETIME, convert(varchar(8),getdate(),112)+' 23:59:59', 108))        \n");
        sb.append("                  AND connectiontype = '1'        \n");
        sb.append("                  AND LastLocalUserId IS NOT NULL       \n");
        sb.append("          ) AA    \n");
        sb.append("     ) AAA    \n");
        sb.append(") AAAA, cams.dbo.tb_emp b with(NOLOCK)    \n");
        sb.append("WHERE AAAA.i_usr_id = b.phone_user_id   \n");
        sb.append("GROUP BY YYYYMMDDHHMM, cmpny_emp_cd    \n");
        
        System.out.println(sb.toString());
		
	}
    
}
