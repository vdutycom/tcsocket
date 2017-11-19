package com.tc.tcsocket.utils;

import java.security.MessageDigest;

public class MD5Utils {
	
	 /*** 
     * MD5���ܣ�����32λMD5�룩
     */  
    public static String encrypt(String oriStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = oriStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    } 
    
    /**
     * �����MD5�����㷨
     * @param inStr
     * @return
     */
    public static String encryptRever(String oriStr) {   
    	  // String s = new String(inStr);   
    	  char[] a = oriStr.toCharArray();   
    	  for (int i = 0; i < a.length; i++) {   
    	   a[i] = (char) (a[i] ^ 't');   
    	  }   
    	  String resultStr = new String(a);   
    	  return resultStr;   
    }
    
    /**
     * MD5�����㷨
     * @param inStr
     * @return
     */
    public static String parse(String enStr) {   
     char[] a = enStr.toCharArray();   
     for (int i = 0; i < a.length; i++) {   
      a[i] = (char) (a[i] ^ 't');   
     }   
     String oriStr = new String(a);   
     return oriStr;   
    } 

}
