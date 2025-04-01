/*
 * @(#)AES256Cipher.java	1.0  2004/03/15
 *
 * Copyright GS ITM. All rights reserved.
 * This software is the proprietary information of Bestech, Inc.
 * Use is subject to license terms.
 */

package kr.co.cs.common.publicutil;

import java.io.UnsupportedEncodingException;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import kr.co.cs.common.config.Base64Encoder;


/**
 * AES 처리 클래스.<p>
 */
public class AES256Cipher {

	private static String key = "gsM639Crm!@#$%^&";
    
    public static byte ivBytes[] = new byte[16];
    
    public static String AES_Encode(String str)
        throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        byte textBytes[] = str.getBytes("UTF-8");
        java.security.spec.AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return new String(Base64Encoder.encode(cipher.doFinal(textBytes))); //Base64.encodeBase64String(cipher.doFinal(textBytes));
    }

    public static String AES_Decode(String str)
        throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, 
        NoSuchProviderException, Exception 
    {
    	byte textBytes[] = Base64Encoder.decode(str);//Base64.decodeBase64(str);
        java.security.spec.AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        byte[] keyBytes = key.getBytes("UTF-8");
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
       
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return new String(cipher.doFinal(textBytes), "UTF-8");
    }
    
    public static void main(String args[]) throws Exception {
    	//String key ="1951747";
    	//System.out.println( key + ":" + AES256Cipher.AES_Encode(key));
    	
    	// 암호화 ...
    	String str1 = "jdbc:oracle:thin:@192.168.19.10:1521:GSNCRM";
    	String str2 = "TBL_OB_PRD_CD_TEST";
    	String str3 = "crmuser";
    	String str4 = "TBL_OB_PRD_CD";
    	
    	//blic static final String TEST_CMS_DB_TABLE_NM  = "TBL_OB_PRD_CD_TEST";
    	
    	String enc_str1 = AES256Cipher.AES_Encode(str1);
    	String enc_str2 = AES256Cipher.AES_Encode(str2);
    	String enc_str3 = AES256Cipher.AES_Encode(str3);
    	String enc_str4 = AES256Cipher.AES_Encode(str4);
    	
    	System.out.println( "enc_str1 (encode):" + enc_str1 );
    	System.out.println( "enc_str2 (encode):" + enc_str2 );
    	System.out.println( "enc_str3 (encode):" + enc_str3 );
    	System.out.println( "enc_str4 (encode):" + enc_str4 );
    	
    	String dec_str1 = AES256Cipher.AES_Decode(enc_str1);
    	String dec_str2 = AES256Cipher.AES_Decode(enc_str2);
    	String dec_str3 = AES256Cipher.AES_Decode(enc_str3);
    	String dec_str4 = AES256Cipher.AES_Decode(enc_str4);
    	
    	System.out.println("dec_str1 (decode):" + dec_str1);
    	System.out.println("dec_str2 (decode):" + dec_str2);
    	System.out.println("dec_str2 (decode):" + dec_str3);
    	System.out.println("dec_str2 (decode):" + dec_str4);
    	
    }
    
 
}
