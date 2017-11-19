package com.tc.tcsocket.utils;

/**
 * Created by Administrator on 2016/12/13.
 */

public class DataChangeUtils {

    /**
     * 将两个ASCII字符合成一个字节；
     * 如："EF"--> 0xEF
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte)(_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src){
        int length =(int)(src.length()/2);
        byte[] ret = new byte[length];
        byte[] tmp = src.getBytes();
        for(int i=0; i<length; i++){
            ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
        }
        return ret;
    }

    /**
     * byte数组连接
     * @param buf1
     * @param buf2
     * @return
     */
   public static byte[] arraycat(byte[] buf1,byte[] buf2)
    {
        byte[] bufret=null;
        int len1=0;
        int len2=0;
        if(buf1!=null)
            len1=buf1.length;
        if(buf2!=null)
            len2=buf2.length;
        if(len1+len2>0)
            bufret=new byte[len1+len2];
        if(len1>0)
            System.arraycopy(buf1,0,bufret,0,len1);
        if(len2>0)
            System.arraycopy(buf2,0,bufret,len1,len2);
        return bufret;
    }
    /**
     *打印字节数组，以16进制方式
     * @param b
     * @return
     */
    public static String  printHexString( byte[] b) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);//与运算,转二进制
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            stringBuilder.append(hex);
            stringBuilder.append(" ");
        }

        return  stringBuilder.toString();

    }

    /**
     *获取字节数组的16进制字符串
     * @param b
     * @param Separator 分隔符
     * @return
     */
    public static String  getHexStringFromByteArray( byte[] b, String Separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for(byte byteChar : b) {
                   /* X 表示以十六进制形式输出
                    02 表示不足两位，前面补0输出；*/
            stringBuilder.append(String.format("%02X"+Separator, byteChar));
        }

        return  stringBuilder.toString();

    }

    /**
     * 生成字节数组连续异或校验码
     *
     * @command 头部有一位为起始位，不参加计算
     */
    public static byte aXOR(byte[] command) {
        byte cur = command[1];
        for (int i = 1; i < command.length - 1; i++) {
            cur = DataChangeUtils.XOR(cur, command[i + 1]);
        }
        return cur;
    }


    /**
    异或运算
     */
    public static byte XOR(byte a, byte b )
    {
        return (byte)(a^b);
    }
    /**
     * 当对方getBytesFromLong用asc true时，这边用asc false 
     * byte to  int 
     * 
     * */  
    public final static long getLongFromBytes(byte[] buf, boolean asc, int len) {  
        if (buf == null) {  
          throw new IllegalArgumentException("byte array is null!");  
        }  
        
        int r = 0;  
        if (asc)  
          for (int i = len - 1; i >= 0; i--) {  
            r <<= 8;  
            r |= (buf[i] & 0x000000ff);  
          }  
        else  
          for (int i = 0; i < len; i++) {  
            r <<= 8;  
            r |= (buf[i] & 0x000000ff);  
          }  
        return r;  
    } 
    
    /**
     * 获取整数的十六进制字节数组
     * @param s
     * @param byteArraylen 生成的字节长度，不足将会补0
     * @param asc 是否正顺序组合
     * @return 
     * ylx 20170315
     */
    public final static byte[] getBytesFromLong(long s,int byteArraylen, boolean asc) {  
        byte[] buf = new byte[byteArraylen];  
        if (asc)  
          for (int i = buf.length - 1; i >= 0; i--) {  
            buf[i] = (byte) (s & 0x000000ff);  
            s >>= 8;  
          }  
        else  
          for (int i = 0; i < buf.length; i++) {  
            buf[i] = (byte) (s & 0x000000ff);  
            s >>= 8;  
          }  
        return buf;  
      }
    
    public static byte[] getByteFromString(String str)
    {
    	byte[] ru = str.getBytes();
    	return ru;
    	  	
    	
    }
}
