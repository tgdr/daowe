package edu.buu.daowe.Util;

public class CheckInArgument {
    public static long stu_late_time;             //学生离开时间
    public static long stu_leaveearly_time;         //学生早退时间 （现在时间-搜索到最后一次ibacon时间开始>设定的早退时间 认定为早退）
    public static long ZAOTUI =15*6000;

    public static boolean isEarlyLate(long stu_late_time,long ibeacontime){
        if(ibeacontime-stu_late_time <= ZAOTUI){
            return false;
        }
        else{
            return  true;
        }
    }


    /**
     * 加密，把一个字符串在原有的基础上+1
     * @param data 需要解密的原字符串
     * @return 返回解密后的新字符串
     */
    public static String encode(String data) {
    //把字符串转为字节数组
        byte[] b = data.getBytes();
        //遍历
        for(int i=0;i<b.length;i++) {
            b[i] += 24;//在原有的基础上+1
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
            b[i] -= 24;//在原有的基础上-1
        }
        return new String(b);
    }
}
