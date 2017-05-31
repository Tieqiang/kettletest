package common.util;

/**
 * HIS的加密解密算法
 * Created by heren on 2015/11/2.
 */
public class EnscriptAndDenScript {

    /**
     * 加密算法
     * @param str
     * @return
     */
    public static String enScript(String str) {
        int k, l;
        String a = "";
        if (str == null) {
            return "";
        }
        str = str.trim();
        System.out.println(str);
        for (int i = 0; i < str.length(); i++) {
            l = i + 1;
            if (l % 2 == 0) {
                k = (int) str.charAt(i) + l - 32;
            } else {
                k = (int) str.charAt(i) - l + 8;
            }
            a = a + (char) k;
        }
        return a;
    }



    /**
     * 正确的解密算法
     * @param str
     * @return
     */
    public static String denscriptFromHis(String str){
        if("".equals(str)||str==null){
            return "" ;
        }

        StringBuffer stringBuffer = new StringBuffer() ;
        for(int i =1 ;i<=str.length();i++){
            char c = str.charAt(i - 1);
            char temp  ;
            if(i%2==0){
                temp = (char)((int)c -i +32) ;
            }else{
                temp = (char)((int)c+i -8) ;
            }
            stringBuffer.append(temp) ;
        }
        return stringBuffer.toString() ;
    }

    public static void main(String[] args) {
        System.out.println(enScript("000001") );

    }
}
