package basic;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {
    private MessageDigest md5;

    public MD5Encoder() throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("MD5");
    }

    public String encode(String str) {
        byte[]bytes=null;
        try {
            bytes = md5.digest(str.getBytes("utf-8"));
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        String s=new String(bytes);
        return  s;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {

    }
}
