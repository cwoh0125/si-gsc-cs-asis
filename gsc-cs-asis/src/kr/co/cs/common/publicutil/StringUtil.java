package kr.co.cs.common.publicutil;

/**
 * @(#)_StringUtil.java 1.0 2003/08/26
 *
 * Copyright Ji Hyun-Sang (Buttle Information System, inc.) All rights reserved.
 */
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import kr.co.cs.common.config.Const;

/**
 * 문자열 처리 기능 모음 Class.
 *
 * @author  Ji Hyun-Sang
 * @version 1.0 2003/08/26
 * @since   JDK 1.2
 */
public final class StringUtil {

	/** Don't let anyone instantiate this class */
	private StringUtil() {};
    
	/**
	 * 사용한 StringBuffer를 반납.
	 * 
	 * @param stringBuffer 반납할 StringBuffer
	 */
	public static void release(StringBuffer stringBuffer) {
		if(stringBuffer != null) {
			stringBuffer = null;
		}
	}
	
	/**
	 * 원본문자열이 NULL이면 defaultString 반환.
	 * 
	 * @param sourceString 원본문자열
	 * @param defaultString
	 * 
	 * @return sourceString == NULL이면 defaultString, 아니면 sourceString.
	 */
	public static String defaultString(String sourceString, String defaultString) {
		return sourceString != null ? sourceString : defaultString;
	}

	/**
	 * 원본문자열이 NULL이면 공백 문자열 반환.
	 * 
	 * @param sourceString 원본문자열
	 * 
	 * @return sourceString == NULL이면 "", 아니면 sourceString.
	 */
	public static String defaultString(String sourceString) {
		return defaultString(sourceString, Const.NULL_STRING);
	}
	
	/**
	 * 원본문자열 좌측에서 trimIndex까지를 제외한 trimString을 제거.
	 *  
	 * @param sourceString 원본문자열
	 * @param trimString 제거할 문자열
	 * @param trimIndex 최대로 제거할 위치. 0은 전체
	 * 
	 * @return 원본문자열에서 좌측 trimString이 제거된 문자열.
	 */
	public static String lTrim(String sourceString, String trimString, int trimIndex) {
		if (sourceString == null || sourceString.length() == 0 || trimIndex < 0)
			return sourceString;

		char[] charArray = sourceString.toCharArray();
		int trimPos = 0;
		int trimLen = charArray.length;
		if ((trimLen - trimIndex) > 0)
			trimLen = trimLen - trimIndex;

		if (trimString == null) {
			while ((trimPos < trimLen) && (
					(charArray[trimPos] <= Const.SPACE)
					|| (charArray[trimPos] == Const.FULL_SPACE)
					|| Character.isWhitespace(charArray[trimPos])
				)) {
				trimPos++;
			}
		} else {
			while ((trimPos < trimLen) && (trimString.indexOf(charArray[trimPos]) != -1)) {
				trimPos++;
			}
		}

		return ((trimPos > 0) || (trimPos < trimLen)) ? sourceString.substring(trimPos) : sourceString;
	}

	/**
	 * 원본문자열에서 좌측의 trimChar를 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param trimChar 제거할 문자
	 * @param trimIndex 최대로 제거할 위치. 0은 전체
	 * 
	 * @return 원본문자열에서 좌측 trimChar이 제거된 문자열
	 */
	public static String lTrim(String sourceString, char trimChar, int trimIndex) {
		return lTrim(sourceString, String.valueOf(trimChar), trimIndex);
	}
	
	/**
	 * 원본문자열 좌측의 trimString을 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param trimString 제거할 문자열
	 * 
	 * @return 원본문자열에서 좌측 trimString이 제거된 문자열.
	 */
	public static String lTrim(String sourceString, String trimString) {
		return lTrim(sourceString, trimString, 0);
	}
	
	/**
	 * 원본문자열 좌측의 trimChar를 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param trimChar 제거할 문자
	 * 
	 * @return 원본문자열에서 trimChar이 제거된 문자열
	 */
	public static String lTrim(String sourceString, char trimChar) {
		return lTrim(sourceString, trimChar, 0);
	}
	
	/**
	 * 원본문자열 좌측의 공백문자(전각포함) 제거.
	 * 
	 * @param sourceString 원본문자열
	 * 
	 * @return 원본문자열에서 좌측 공백문자가 제거된 문자열.
	 */
	public static String lTrim(String sourceString) {
		return lTrim(sourceString, null, 0);
	}

	/**
	 * 원본문자열 우측애서 trimIndex까지를 제외한 trimString을 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param trimString 제거할 문자열
	 * @param trimIndex 최대로 제거할 위치. 0은 전체
	 *  
	 * @return 원본문자열에서 우측 trimString이 제거된 문자열.
	 */
	public static String rTrim(String sourceString, String trimString, int trimIndex) {
		if (sourceString == null || sourceString.length() == 0 || trimIndex < 0)
			return sourceString;

		char[] charArray = sourceString.toCharArray();
		int trimLen = charArray.length;

		int trimPos = trimLen;

		if (trimString == null) {
			while ((trimPos > trimIndex) && (
					(charArray[trimPos - 1] <= Const.SPACE)
					|| (charArray[trimPos - 1] == Const.FULL_SPACE)
					|| Character.isWhitespace(charArray[trimPos - 1])
				)) {
				trimPos--;
			}
		} else {
			while ((trimPos > trimIndex) && (trimString.indexOf(charArray[trimPos - 1]) != -1)) {
				trimPos--;
			}
		}

		return ((trimPos > 0) || (trimPos < trimLen)) ? sourceString.substring(0, trimPos) : sourceString;
	}

	/**
	 * 원본문자열 앞쪽에서 trimIndex까지를 제외한 trimChar를 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param trimChar 제거할 문자
	 * @param trimIndex 최대로 제거할 위치. 0은 전체
	 * 
	 * @return 원본문자열에서 되쪽 trimChar이 제거된 문자열
	 */
	public static String rTrim(String sourceString, char trimChar, int trimIndex) {
		return rTrim(sourceString, String.valueOf(trimChar), trimIndex);
	}
	
	/**
	 * 원본문자열 우측의 trimString을 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param trimString 제거할 문자열
	 * 
	 * @return 원본문자열에서 우측 trimString이 제거된 문자열.
	 */
	public static String rTrim(String sourceString, String trimString) {
		return rTrim(sourceString, trimString, 0);
	}

	/**
	 * 원본문자열 우측의 trimChar를 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param trimChar 제거할 문자
	 * 
	 * @return 원본문자열에서 우측 trimChar이 제거된문자열
	 */
	public static String rTrim(String sourceString, char trimChar) {
		return rTrim(sourceString, trimChar, 0);
	}
	
	/**
	 * 원본문자열 우측의 공백문자(전각포함) 제거.
	 * 
	 * @param sourceString 원본문자열
	 * 
	 * @return 원본문자열에서 우측 공백문자가 제거된 문자열.
	 */
	public static String rTrim(String sourceString) {
		return rTrim(sourceString, null, 0);
	}

	/**
	 * 원본문자열 좌우의 trimString을 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param trimString 제거할 문자열
	 * 
	 * @return 원본문자열에서 좌우 trimString이 제거된 문자열.
	 */
	public static String Trim(String sourceString, String trimString) {
		return rTrim(lTrim(sourceString, trimString), trimString);
	}

	/**
	 * 원본문자열 좌우의 trimChar를 제거.
	 *  
	 * @param sourceString 원본문자열
	 * @param trimChar 제거할 문자
	 * 
	 * @return 원본문자열에서 좌우 trimChar이 제거된 문자열
	 */
	public static String Trim(String sourceString, char trimChar) {
		return Trim(sourceString, String.valueOf(trimChar));
	}
	
	/**
	 * 원본문자열 앞뒤의 공백문자(전각포함) 제거.
	 * 
	 * @param sourceString 원본문자열
	 * 
	 * @return 원본문자열에서 앞뒤 공백문자가 제거된 문자열.
	 */
	public static String Trim(String sourceString) {
		return Trim(sourceString, null);
	}

	/**
	 * 원본문자열의 길이가 length보다 작으면 좌측의 남는 공간에 padString으로 채움.
	 * 
	 * @param sourceString 원본문자열
	 * @param length 생성할 길이
	 * @param padString 채울 문자열 
	 * 
	 * @return sourceString.length() >= length 이면 sourceString, sourceString.length() < length 이면 좌측에 padString을 채운 문자열. 
	 */
	public static String lPad(String sourceString, int length, String padString) {
		if (length <= 0 || padString == null || padString.length() == 0)
			return sourceString;

		if (sourceString == null ) sourceString = "";
		
		String rtnString = sourceString;

		int remainSize = (length - rtnString.length()) / padString.length();

		if (remainSize > 0)
			rtnString = getPadString(padString, remainSize) + rtnString;

		return rtnString;
	}

	/**
	 * 원본문자열의 길이가 length보다 작으면 좌측의 남는 공간에 padChar로 채움.
	 * 
	 * @param sourceString 원본문자열
	 * @param length 생성할 길이
	 * @param padChar 채울 문자
	 * 
	 * @return sourceString.length() >= length 이면 sourceString, sourceString.length() < length 이면 좌측에 padChar를 채운 문자열. 
	 */
	public static String lPad(String sourceString, int length, char padChar) {
		return lPad(sourceString, length, String.valueOf(padChar));
	}
	
	/**
	 * 원본문자열의 길이가 length보다 작으면 좌측의 남는 공간에 공백을 채움.
	 * 
	 * @param sourceString 원본문자열
	 * @param length 생성할 길이
	 * 
	 * @return sourceString.length() >= length 이면 sourceString, sourceString.length() < length 이면 좌측에 공백을 채운 문자열. 
	 */
	public static String lPad(String sourceString, int length) {
		return lPad(sourceString, length, String.valueOf(Const.SPACE));
	}

//	/**
//	 * 숫자값의 길이가 length보다 작으면 좌측의 남는 공간에 0를 채움.
//	 * 
//	 * @param sourceValue 숫자값
//	 * @param length 생성할 길이
//	 * 
//	 * @return String.valueOf(sourceValue).length() >= length 이면 String.valueOf(sourceValue), String.valueOf(sourceValue).length() < length 이면 앞쪽에 0를 채운 문자열.
//	 */
//	public static String lPad(int sourceValue, int length) {
//		return lPad(Trim(String.valueOf(sourceValue)), length, String.valueOf(Const.ZERO));
//	}

	/**
	 * 원본문자열의 길이가 length보다 작으면 우측의 남는 공간에 padString으로 채움.
	 * 
	 * @param sourceString 원본문자열
	 * @param length 생성할 길이
	 * @param padString 채울 문자열
	 * 
	 * @return sourceString.length() >= length 이면 sourceString, sourceString.length() < length 이면 우측에 padString을 채운 문자열. 
	 */
	public static String rPad(String sourceString, int length, String padString) {
		try {
			if (length <= 0 || padString == null || padString.length() == 0)
				return sourceString;
			
			if (sourceString == null ) sourceString = "";
			
			String rtnString = sourceString;
	
			int remainSize = (length - rtnString.getBytes(Const.KOREA_CHARSET).length) / padString.length();
	
			if (remainSize > 0)
				rtnString = rtnString + getPadString(padString, remainSize);

		return rtnString;
		} catch (Exception e) {
			return rPad("", length);
		}
	}

	/**
	 * 원본문자열의 길이가 length보다 작으면 우측의 남는 공간에 padChar으로 채움.
	 * 
	 * @param sourceString 원본문자열
	 * @param length 생성할 길이
	 * @param padChar 채울 문자
	 * 
	 * @return sourceString.length() >= length 이면 sourceString, sourceString.length() < length 이면 우측에 padChar을 채운 문자열. 
	 */
	public static String rPad(String sourceString, int length, char padChar) {
		return rPad(sourceString, length, String.valueOf(padChar));
	}
	
	/**
	 * 원본문자열의 길이가 length보다 작으면 우측의 남는 공간에 공백을 채움.
	 * 
	 * @param sourceString 원본문자열
	 * @param length 생성할 길이
	 * 
	 * @return sourceString.length() >= length 이면 sourceString, sourceString.length() < length 이면 우측에 공백을 채운 문자열. 
	 */
	public static String rPad(String sourceString, int length) {
		return rPad(sourceString, length, String.valueOf(Const.SPACE));
	}
	
	/**
	 * blank를 만들기 위해 loop을 도는 것을 줄이기 위해 10Bytes단위로 Blank문자열을 생성하여 반환.
	 * 
	 * @param fillLen   int    생성할 Blank문자열의 길이
	 * 
	 * @return Blank로 구성된 fillLen 길이의 문자열
	 */
	public static String makeBlankString(int fillLen) {
		String blankString;
		
		StringBuffer blankBuffer = new StringBuffer(100);
		int leftLen = fillLen;

		while (leftLen > 0) {
			if (leftLen < 10) {
				blankBuffer.append(" ");
				leftLen--;
			} else if (leftLen >= 10 && leftLen < 20) {
				//---------------0        1
				//---------------1234567890//
				blankBuffer.append("          ");
				leftLen = leftLen - 10;
			} else if (leftLen >= 20 && leftLen < 30) {
				//---------------0        1         2
				//---------------12345678901234567890//
				blankBuffer.append("                    ");
				leftLen = leftLen - 20;
			} else if (leftLen >= 30 && leftLen < 40) {
				//---------------0        1         2         3
				//---------------123456789012345678901234567890//
				blankBuffer.append("                              ");
				leftLen = leftLen - 30;
			} else if (leftLen >= 40 && leftLen < 50) {
				//---------------0        1         2         3         4
				//---------------1234567890123456789012345678901234567890//
				blankBuffer.append("                                        ");
				leftLen = leftLen - 40;
			} else if (leftLen >= 50 && leftLen < 60) {
				//---------------0        1         2         3         4         5
				//---------------12345678901234567890123456789012345678901234567890//
				blankBuffer.append("                                                  ");
				leftLen = leftLen - 50;
			} else if (leftLen >= 60 && leftLen < 70) {
				//---------------0        1         2         3         4         5         6
				//---------------123456789012345678901234567890123456789012345678901234567890//
				blankBuffer.append("                                                            ");
				leftLen = leftLen - 60;
			} else if (leftLen >= 70 && leftLen < 80) {
				//---------------0        1         2         3         4         5         6         7
				//---------------1234567890123456789012345678901234567890123456789012345678901234567890//
				blankBuffer.append("                                                                      ");
				leftLen = leftLen - 70;
			} else if (leftLen >= 80 && leftLen < 90) {
				//---------------0        1         2         3         4         5         6         7         8
				//---------------12345678901234567890123456789012345678901234567890123456789012345678901234567890//
				blankBuffer.append("                                                                                ");
				leftLen = leftLen - 80;
			} else if (leftLen >= 90 && leftLen < 100) {
				//---------------0        1         2         3         4         5         6         7         8         9
				//---------------123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890//
				blankBuffer.append("                                                                                          ");
				leftLen = leftLen - 90;
			} else if (leftLen >= 100) {
				while (leftLen >= 100) {
					//---------------0        1         2         3         4         5         6         7         8         9         10
					//---------------1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890//
					blankBuffer.append("                                                                                                    ");
					leftLen = leftLen - 100;
				}
			}
		} // end of While
		blankString = blankBuffer.toString();
		
		release(blankBuffer);

		return blankString;
	}

	/**
	 * zero를 만들기 위해 loop을 도는 것을 줄이기 위해 10Bytes단위로 Zero문자열을 생성하여 반환.
	 * 
	 * @param fillLen	생성할 문자열의 길이
	 * 
	 * @return Zreo로 구성된 fillLen길이의 문자열
	 */
	public static String makeZeroString(int fillLen) {
		String zeroString;
		
		StringBuffer zeroBuffer = new StringBuffer(100);
		int leftLen = fillLen;

		while (leftLen > 0) {
			if (leftLen < 10) {
				zeroBuffer.append("0");
				leftLen--;
			} else if (leftLen >= 10 && leftLen < 20) {
				//--------------0        1
			  //--------------1234567890//
				zeroBuffer.append("0000000000");
				leftLen = leftLen - 10;
			} else if (leftLen >= 20 && leftLen < 30) {
				//--------------0        1         2                        	
				//--------------12345678901234567890//
				zeroBuffer.append("00000000000000000000");
				leftLen = leftLen - 20;
			} else if (leftLen >= 30 && leftLen < 40) {
				//--------------0        1         2         3                        	
				//--------------123456789012345678901234567890//
				zeroBuffer.append("000000000000000000000000000000");
				leftLen = leftLen - 30;
			} else if (leftLen >= 40 && leftLen < 50) {
				//--------------0        1         2         3         4                        	
				//--------------1234567890123456789012345678901234567890//
				zeroBuffer.append("0000000000000000000000000000000000000000");
				leftLen = leftLen - 40;
			} else if (leftLen >= 50 && leftLen < 60) {
				//--------------0        1         2         3         4         5
			  //--------------12345678901234567890123456789012345678901234567890//
				zeroBuffer.append("00000000000000000000000000000000000000000000000000");
				leftLen = leftLen - 50;
			} else if (leftLen >= 60 && leftLen < 70) {
				//--------------0        1         2         3         4         5         6                        	
				//--------------123456789012345678901234567890123456789012345678901234567890//
				zeroBuffer.append("000000000000000000000000000000000000000000000000000000000000");
				leftLen = leftLen - 60;
			} else if (leftLen >= 70 && leftLen < 80) {
				//--------------0        1         2         3         4         5         6         7
			  //--------------1234567890123456789012345678901234567890123456789012345678901234567890//
				zeroBuffer.append("0000000000000000000000000000000000000000000000000000000000000000000000");
				leftLen = leftLen - 70;

			} else if (leftLen >= 80 && leftLen < 90) {
				//--------------0        1         2         3         4         5         6         7         8
				//--------------12345678901234567890123456789012345678901234567890123456789012345678901234567890//
				zeroBuffer.append("00000000000000000000000000000000000000000000000000000000000000000000000000000000");

				leftLen = leftLen - 80;
			} else if (leftLen >= 90 && leftLen < 100) {
				//--------------0        1         2         3         4         5         6         7         8         9
				//--------------123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890//
				zeroBuffer.append("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

				leftLen = leftLen - 90;
			} else if (leftLen >= 100) {
				while (leftLen >= 100) {
					//--------------0        1         2         3         4         5         6         7         8         9         10
					//--------------1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890//
					zeroBuffer.append("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
					leftLen = leftLen - 100;
				}
			}
		} // end of While
		zeroString = zeroBuffer.toString();
		
		release(zeroBuffer);
		
		return zeroString;
	}
	
	/**
	 * 원본문자열이 fillSiz만큼 반복해서 채워진 문자열.
	 * 
	 * @param fillString 반복할 문자열(되도록 문자하나로된 문자열 사용)
	 * @param fillSize 채울 길이
	 * 
	 * @return 반복해서 채워진 문자열 
	 */
	public static String getPadString(String fillString, int fillSize) {
		String padString = Const.NULL_STRING;
		
		if(fillString != null && fillString.length() > 0 && fillSize > 0) {
			if(fillString.equals(String.valueOf(Const.SPACE))) {
				padString = makeBlankString(fillSize);
			} else if(fillString.equals(String.valueOf(Const.ZERO))) {
				padString = makeZeroString(fillSize);
			} else {
				StringBuffer fillBuffer = new StringBuffer(fillSize * fillString.length());
				for (int i = 0; i < fillSize; i++) {
					fillBuffer.append(fillString);
				}
				padString = fillBuffer.toString();
				
				release(fillBuffer);
			}
		}
		
		return padString;
	}

	/**
	 * 원본문자가 fillSiz만큼 반복해서 채워진 문자열.
	 * 
	 * @param fillChar 반복할 문자
	 * @param fillSize 채울 길이
	 * 
	 * @return 반복해서 채워진 문자열 
	 */
	public static String getPadString(char fillChar, int fillSize) {
		String padString = Const.NULL_STRING;
		
		if(fillSize > 0) {
			if(fillChar == Const.SPACE) {
				padString = makeBlankString(fillSize);
			} else if(fillChar == Const.ZERO) {
				padString = makeZeroString(fillSize);
			} else {
				StringBuffer fillBuffer = new StringBuffer();
				for(int i = 0; i < fillSize; i++) {
					fillBuffer.append(String.valueOf(fillChar));
				}
				padString = fillBuffer.toString();
				
				release(fillBuffer);
			}
		}

		return padString;
	}
	
	/**
	 * 문자열이 문자로만 이루어졌는지 Check.
	 * 
	 * @param testString	테스트할 문자열
	 * 
	 * @return 테스트 문자열이 문자로만 이루어졌을 경우 true, 그 외 false
	 */
	public static boolean isAlpha(String testString) {
		if (testString == null || testString.length() == 0)
			return false;

		int len = testString.length();
		for (int i = 0; i < len; i++)
			if (!Character.isLetter(testString.charAt(i)))
				return false;

		return true;
	}

	/**
	 * 문자열이 문자와 공백으로만 이루어졌는지 Check.
	 * 
	 * @param testString	테스트할 문자열
	 * 
	 * @return 테스트 문자열이 문자와 공백으로만 이루어진 경우 true, 그 외 false 
	 */
	public static boolean isAlphaSpace(String testString) {
		if (testString == null || testString.length() == 0)
			return false;

		int len = testString.length();
		for (int i = 0; i < len; i++)
			if (!Character.isLetter(testString.charAt(i)) && !Character.isWhitespace(testString.charAt(i)))
				return false;

		return true;
	}

	/**
	 * 문자열이 문자와 숫자로만 이루어졌는지 Check.
	 * 
	 * @param testString	테스트할 문자열
	 * 
	 * @return 테스트 문자열이 문자와 숫자로만 이루어진 경우 true, 그 외 false
	 */
	public static boolean isAlphanumeric(String testString) {
		if (testString == null || testString.length() == 0)
			return false;

		int len = testString.length();
		for (int i = 0; i < len; i++)
			if (!Character.isLetterOrDigit(testString.charAt(i)))
				return false;

		return true;
	}

	/**
	 * 문자열이 문자와 숫자, 공백으로만 이루어졌는지 Check.
	 * 
	 * @param testString	테스트할 문자열
	 * 
	 * @return 테스트 문자열이 문자와 숫자, 공백으로만 이루어진 경우 true, 그 외 false
	 */
	public static boolean isAlphanumericSpace(String testString) {
		if (testString == null || testString.length() == 0)
			return false;

		int len = testString.length();
		for (int i = 0; i < len; i++)
			if (!Character.isLetterOrDigit(testString.charAt(i)) && !Character.isWhitespace(testString.charAt(i)))
				return false;

		return true;
	}

	/**
	 * 문자열이 숫자로만 이루어졌는지 Check.
	 * 
	 * @param testString	테스트할 문자열
	 * 
	 * @return 테스트 문자열이 숫자로만 이루어진 경우 true, 그 외 false
	 */
	public static boolean isNumeric(String testString) {
		if (testString == null || testString.length() == 0)
			return false;

		int len = testString.length();
		int chkStart = 0;
		if(testString.charAt(0) == Const.PLUS || testString.charAt(0) == Const.MINUS)	chkStart++;
		for (int i = chkStart; i < len; i++)
			if (!Character.isDigit(testString.charAt(i)))
				return false;

		return true;
	}

	/**
	 * 문자열이 숫자와 공백으로만 이루어졌는지 Check.
	 * 
	 * @param testString	테스트할 문자열
	 * 
	 * @return 테스트 문자열이 숫자와 공백으로만 이루어진 경우 true, 그 외 false
	 */
	public static boolean isNumericSpace(String testString) {
		if (testString == null || testString.length() == 0)
			return false;

		int len = testString.length();
		for (int i = 0; i < len; i++)
			if (!Character.isDigit(testString.charAt(i)) && !Character.isWhitespace(testString.charAt(i)))
				return false;

		return true;
	}

	/**
	 * 문자열이 공백으로만 이루어졌는지 Check.
	 * 
	 * @param testString 테스트할 문자열
	 * 
	 * @return 테스트 문자열이 공백으로만 이루어진 경우 true, 그 외 false
	 */
	public static boolean isWhiteSpace(String testString) {
		if (testString == null || testString.length() == 0)
			return false;

		int len = testString.length();
		for (int i = 0; i < len; i++)
			if (!Character.isWhitespace(testString.charAt(i)))
				return false;

		return true;
	}

	/**
	 * 원본문자열에서 replaceFrom문자열을 replaceTo문자열로 변환.
	 * 
	 * @param sourceString 원본문자열
	 * @param replaceFrom	원본문자열의 변환대상 문자열
	 * @param replaceTo 변화할 문자열
	 * 
	 * @return 원본문자열에 replaceFrom문자열이 있는 경우 replaceTo문자열로 변환된 문자열
	 */
	public static String replace(String sourceString, String replaceFrom, String replaceTo) {
		if(sourceString == null || sourceString.length() == 0 || replaceFrom == null || replaceTo == null) return sourceString;
		
		String replaceString;
		StringBuffer replaceBuffer = new StringBuffer(sourceString);

		int index = 0;
		while (index > -1) {
			index = replaceBuffer.toString().indexOf(replaceFrom, index);
			if (index > -1) {
				replaceBuffer.delete(index, index + replaceFrom.length());
				replaceBuffer.insert(index, replaceTo);
				index += replaceTo.length();
			}
		}
		
		replaceString = replaceBuffer.toString();
		release(replaceBuffer);
		
		return replaceString;
	}

	/**
	 * 원본문자열에서 replaceFrom문자를 replaceTo문자로 변환.
	 * 
	 * @param sourceString 원본문자열
	 * @param replaceFrom 원본문자열의 변환대상 문자
	 * @param replaceTo 변환할 문자
	 * 
	 * @return 원본문자열에서 oldChar이 있는 경우 newChar으로 변환된 문자열
	 */
	public static String replace(String sourceString, char replaceFrom, char replaceTo) {
		if (sourceString == null || sourceString.length() == 0)
			return sourceString;

		char[] charArray = sourceString.toCharArray();
		int srcLen = charArray.length;

		for (int i = 0; i < srcLen; i++) {
			if (charArray[i] == replaceFrom) {
				charArray[i] = replaceTo;
			}
		}

		return String.valueOf(charArray);
	}
	
	/**
	 * 원본문자열에서 remove문자열을 모두 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param remove 제거할 문자열
	 * 
	 * @return 원본문자열에서 remove문자열이 모두 제거된 문자열
	 */
	public static String remove(String sourceString, String remove) {
		String removeString = sourceString;
		
		if(sourceString != null && sourceString.length() > 0) {
			StringBuffer removeBuffer = new StringBuffer(sourceString);
			int index = 0;
			int removeLength = remove.length();
			while(index > -1) {
				index = removeBuffer.toString().indexOf(remove, index);
				if(index > -1) {
					removeBuffer.delete(index, index + removeLength);
				}
			}
			removeString = removeBuffer.toString();
			
			release(removeBuffer);
		}
		
		return removeString;
	}
	
	/**
	 * 원본문자열에서 remove문자를 모두 제거.
	 * 
	 * @param sourceString 원본문자열
	 * @param remove 제거할 문자
	 * 
	 * @return 원본문자열에서 remove문자가 모두 제거된 문자열.
	 */
	public static String remove(String sourceString, char remove) {
		String removeString = sourceString;
		
		if(sourceString != null && sourceString.length() > 0) {
			StringBuffer removeBuffer = new StringBuffer(sourceString);
			for (int i = removeBuffer.length() - 1; i >= 0; i--) { // deleteCharAt()를 사용하면 index가 변경되므로 뒤에서부터 삭제처리해야 함
				if (removeBuffer.charAt(i) == remove)
					removeBuffer.deleteCharAt(i);
			}
			removeString = removeBuffer.toString();
			
			release(removeBuffer);
		}
		
		return removeString;
	}

	public static int contains(String sourceString, String contain) {
		int count = 0;
		
		if(sourceString != null && sourceString.length() > 0) {
			String countString = sourceString;
			int index = 0;
			while(index >= 0) {
				index = countString.indexOf(contain);
				if(index > -1) {
					count++;
					countString = countString.substring(index + contain.length());
				}
			}
		}
		
		return count;
	}
	
	public static int contains(String sourceString, char contain) {
		return contains(sourceString, String.valueOf(contain));
	}
	
	/**
	 * 원본문자열을 구분자로 분할.
	 * 
	 * @param sourceString 원본문자열
	 * @param separator 문자열을 분리할 구분자
	 * 
	 * @return 원본문자열이 separator로 분할 된 List
	 */
	public static List split2List(String sourceString, String separator) {
		if (sourceString == null || sourceString.length() == 0)
			return Const.NULL_LIST;

		List rtnList = new ArrayList();

		String splitString = sourceString;
		int index = 0;
		while (index >= 0) {
			index = splitString.indexOf(separator);
			if (index > -1) {
				rtnList.add(splitString.substring(0, index));
				splitString = splitString.substring(index + separator.length());
			} else {
				if(splitString.length() > 0) {
					rtnList.add(splitString);
				}
			}
		}

		return rtnList;
	}
	
	/**
	 * 원본문자열을 구분자로 분할.
	 * 
	 * @param sourceString 원본문자열
	 * @param separator 문자열을 분리할 구분자
	 * 
	 * @return 원본문자열이 separator로 분할 된 List
	 */
	public static List split2List(String sourceString, char separator) {
		return split2List(sourceString, String.valueOf(separator));
	}

	/**
	 * 원본문자열을 구분자로 분할.
	 * 
	 * @param sourceString 원본문자열
	 * @param separator 문자열을 분리할 구분자
	 * 
	 * @return 원본문자열이 separator로 분할 된 String[]
	 */
	public static String[] split2Array(String sourceString, String separator) {
		if (sourceString == null || sourceString.length() == 0)
			return Const.NULL_STRING_ARRAY;

		return (String[]) split2List(sourceString, separator).toArray(new String[0]);
	}
	
	/**
	 * 원본문자열을 구분자로 분할.
	 * 
	 * @param sourceString 원본문자열
	 * @param separator 문자열을 분리할 구분자
	 * 
	 * @return 원본문자열이 separator로 분할 된 String[]
	 */
	public static String[] split2Array(String sourceString, char separator) {
		return split2Array(sourceString, String.valueOf(separator));
	}
	
	/**
	 * 배열의 값을 구분자로 구분된 문자열로 변환하여 반환.
	 * 
	 * @param array 배열
	 * @param separator 구분자
	 * 
	 * @return 구분자로 구분된 문자열
	 */
	public static String merge(Object[] array, String separator) {
		String rtnString = null;
		
		if(array != null && array.length > 0) {
			StringBuffer stringBuffer = new StringBuffer();
			for(int index = 0; index < array.length; index++) {
				if(index != 0) {
					stringBuffer.append(separator);
				}
				stringBuffer.append(array[index]);
			}
			rtnString = stringBuffer.toString();
			
			release(stringBuffer);			
		}
		
		return rtnString;
	}
	
	/**
	 * 배열의 값을 구분자로 구분된 문자열로 변환하여 반환.
	 * 
	 * @param array 배열
	 * @param separator 구분자
	 * 
	 * @return 구분자로 구분된 문자열
	 */
	public static String merge(Object[] array, char separator) {
		return merge(array, String.valueOf(separator));
	}
	
	/**
	 * 리스트의 값을 구분자로 구분된 문자열로 변환하여 반환.
	 * 
	 * @param list 리스트
	 * @param separator 구분자
	 * 
	 * @return 구분자로 구분된 문자열
	 */
	public static String merge(List list, String separator) {
		return merge(list.toArray(), separator);
	}
	
	/**
	 * 리스트의 값을 구분자로 구분된 문자열로 변환하여 반환.
	 * 
	 * @param list 리스트
	 * @param separator 구분자
	 * 
	 * @return 구분자로 구분된 문자열
	 */
	public static String merge(List list, char separator) {
		return merge(list, String.valueOf(separator));
	}
	
	/**
	 * 원본문자열을 int형 값으로 변환.
	 *  
	 * @param sourceString 원본문자열
	 * 
	 * @return 원본문자열이 int형 값을 가진 경우 int 값, 변환이 안될경우 0
	 */
	public static int getInt(String sourceString) {
		int rtnValue = 0;

		if(sourceString != null && sourceString.length() > 0) {
			rtnValue = Integer.parseInt(Trim(sourceString));
		}

		return rtnValue;
	}

	/**
	 * 원본문자열을 int형 값으로 변환.
	 *  
	 * @param sourceString 원본문자열
	 * @param radix 밑수
	 * 
	 * @return 원본문자열이 int형 값을 가진 경우 int 값, 변환이 안될경우 0
	 */
	public static int getInt(String sourceString, int radix) {
		int rtnValue = 0;

		if(sourceString != null && sourceString.length() > 0) {
			rtnValue = Integer.valueOf(Trim(sourceString), radix).intValue();
		}

		return rtnValue;
	}

	/**
	 * 원본문자열을 long형 값으로 변환.
	 * 
	 * @param sourceString 원본문자열
	 * 
	 * @return 원본문자열이 long형 값을 가진 경우 long 값, 변환이 안될경우 0L
	 */
	public static long getLong(String sourceString) {
		long rtnValue = 0L;

		if(sourceString != null && sourceString.length() > 0) {
			rtnValue = Long.parseLong(Trim(sourceString));
		}

		return rtnValue;
	}

	/**
	 * 원본문자열을 float형 값으로 변환.
	 * 
	 * @param sourceString 원본문자열
	 * 
	 * @return 원본문자열이 float형 값을 가진 경우 float 값, 변환이 안될경우 0.0f
	 */
	public static float getFloat(String sourceString) {
		float rtnValue = 0.0f;

		if(sourceString != null && sourceString.length() > 0) {
			rtnValue = Float.parseFloat(Trim(sourceString));
		}

		return rtnValue;
	}

	/**
	 * 원본문자열을 double형 값으로 변환.
	 * 
	 * @param sourceString 원본문자열
	 * 
	 * @return 원본문자열이 double형 값을 가진 경우 double 값, 변환이 안될경우 0.0D
	 */
	public static double getDouble(String sourceString) {
		double rtnValue = 0.0D;

		if(sourceString != null && sourceString.length() > 0) {
			rtnValue = Double.parseDouble(Trim(sourceString));
		}

		return rtnValue;
	}

	/**
	 * 원본문자열을 boolean형 값으로 변환.
	 * 
	 * @param sourceString 원본문자열
	 * 
	 * @return 원본문자열이 boolean형 값을 가진 경우 boolean 값, 변환이 안될경우 false
	 */
	public static boolean getBoolean(String sourceString) {
		return new Boolean(Trim(sourceString)).booleanValue();
	}
	
	/**
	 * 원본문자열에서 countChar의 개수를 반환.
	 * 
	 * @param sourceString 원본문자열
	 * @param countChar 카운트할 문자
	 * 
	 * @return 문자 수
	 */
	public static int getCharCount(String sourceString, char countChar) {
		int count = 0;
		
		char[] chars = sourceString.toCharArray();
		for(int index = 0; index < chars.length; index++) {
			if(chars[index] == countChar) {
				count++;
			}
		}
		
		return count;
	}
	
	public static ArrayList getTokenList(String sourceString, String startDelimeter, String endDelimeter) {
		ArrayList tokenList = new ArrayList();
		
		if ((sourceString != null && sourceString.length() > 0) && (startDelimeter != null && startDelimeter.length() > 0) && (endDelimeter != null && endDelimeter.length() > 0)) {
			String splitString = sourceString;
		
			int startDelimLength = startDelimeter.length();
			int endDelimLength = endDelimeter.length();
		
			int subStartIndex = 0;
			int subEndIndex = 0;

			while (subStartIndex >= 0 && subEndIndex >= 0) {
				subStartIndex = splitString.indexOf(startDelimeter);
				if (subStartIndex > -1) {
					splitString = splitString.substring(subStartIndex + startDelimLength);
		
					subEndIndex = splitString.indexOf(endDelimeter);
					if (subEndIndex > -1) {
						tokenList.add(splitString.substring(0, subEndIndex));
						splitString = splitString.substring(subEndIndex + endDelimLength);
					}
				}
			}
		}
		
		return tokenList;
	}

	public static ArrayList getTokenList(String sourceString, char startDelimeter, char endDelimeter) {
		return getTokenList(sourceString, String.valueOf(startDelimeter), String.valueOf(endDelimeter));
	}
	
	public static String replaceToken(String sourceString, String startDelimeter, String endDelimeter, String replaceString) {
		String string = null;

		if(sourceString != null && (startDelimeter != null && startDelimeter.length() > 0) && (endDelimeter != null && endDelimeter.length() > 0) && (replaceString != null && replaceString.length() > 0)) {
			StringBuffer stringBuffer = new StringBuffer();
			String splitString = sourceString;
			
			int startDelimLength = startDelimeter.length();
			int endDelimLength = endDelimeter.length();
			
			int subStartIndex = 0;
			int subEndIndex = 0;
			
			while (subStartIndex >= 0 && subEndIndex >= 0) {
				subStartIndex = splitString.indexOf(startDelimeter);
				if(subStartIndex > -1) {
					stringBuffer.append(splitString.substring(0, subStartIndex));
					splitString = splitString.substring(subStartIndex + startDelimLength);
					subEndIndex = splitString.indexOf(endDelimeter);
					
					if(subEndIndex > -1) {
						stringBuffer.append(replaceString);
						splitString = splitString.substring(subEndIndex + endDelimLength);
					}
				} else {
					stringBuffer.append(splitString);
				}
			}
			
			string = stringBuffer.toString();
			
			release(stringBuffer);
		}

		return string;
	}
	
	public static String replaceToken(String sourceString, char startDelimeter, char endDelimeter, char replaceChar) {
		return replaceToken(sourceString, String.valueOf(startDelimeter), String.valueOf(endDelimeter), String.valueOf(replaceChar));
	}
	
	
	public static String formatNumber(String ss) {
		NumberFormat nf = NumberFormat.getInstance();
		try {
			return nf.format(Double.parseDouble(ss));
		} catch(Exception e){
			return "0";
		}
	}
	public static String formatNumber(int ss) {
		NumberFormat nf = NumberFormat.getInstance();
		try {
			return nf.format(ss);
		} catch(Exception e){
			return "0";
		}
	}
	public static String formatNumber(double ss) {
		NumberFormat nf = NumberFormat.getInstance();
		try {
			return nf.format(ss);
		} catch(Exception e){
			return "0";
		}
	}
	public static String formatNumber(float ss) {
		DecimalFormat df = new DecimalFormat("###,###,###,###.0");		
		try {
			return df.format(ss);
		} catch(Exception e){
			return "0";
		}
	}
	
	public static String formatDate(String ss) {
		try {
			ss = ss.replaceAll("-", "");
			if(ss.length()==8) {
				return ss.substring(0, 4) +"-"+ss.substring(4, 6) +"-"+ss.substring(6, 8); 
			} else if(ss.length()==6) {
				return ss.substring(0, 4) +"-"+ss.substring(4, 6); 
			} else {
				return "";
			}
		} catch(Exception e){
			return "";
		}
	}
}
