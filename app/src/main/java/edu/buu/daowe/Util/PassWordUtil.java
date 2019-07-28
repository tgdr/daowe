package edu.buu.daowe.Util;

/**
 * @author: lty
 * @time: 2019/7/11   ------   10:56
 */
public class PassWordUtil {
    public static String encode(String data) {
    //把字符串转为字节数组
        byte[] b = data.getBytes();
        //遍历
        for(int i=0;i<b.length;i++) {
            b[i] += 31;//在原有的基础上+1
        }
        return new String(b);
    }
    /**
     * 解密：把一个加密后的字符串在原有基础上-1
     * @param data 加密后的字符串
     * @return 返回解密后的新字符串
     */
    public static String decode(String data) {
        //把字符串转为字节数组
        byte[] b = data.getBytes();
        //遍历
        for(int i=0;i<b.length;i++) {
            b[i] -= 31;//在原有的基础上-1
        }
        return new String(b);
    }
}
