package com.gjs.developresponsity.utils;

import com.gjs.developresponsity.utils.image.ImageTools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/4/11.
 */
public class MD5Util {
    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符
     */
    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    protected static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String getFileMD5String(File file) {
        try {
            InputStream fis;
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                messagedigest.update(buffer, 0, numRead);
            }
            fis.close();
        } catch (Exception e) {
        }
        return bufferToHex(messagedigest.digest());
    }

    public static String getStringMD5(String str) {
        byte[] buffer = str.getBytes();
        messagedigest.update(buffer);
        return bufferToHex(messagedigest.digest());
    }

    public static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static String getMD5(String URLName) {
        String name = "";
        try {
            URL url = new URL(URLName);
            InputStream inputStream = new BufferedInputStream(url.openStream());
            byte[] bytes = new byte[1024];
            int len = 0;
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            while ((len = inputStream.read(bytes)) > 0) {
                messagedigest.update(bytes, 0, len);
            }
            name = MD5Util.bufferToHex(messagedigest.digest());
            inputStream.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return name;
    }

    /**
     * 对byte类型的数组进行MD5加密
     *
     * @author
     */
    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    public static String getImageMD5(String ImagePath){
        if(!new File(ImagePath).exists()){
            return null;
        }
        String MD5 = ImageTools.getMD5Exif(ImagePath);
        if(MD5 == null || "".equals(MD5) || !MD5.startsWith("MD5:")) {
            File imageFile = new File(ImagePath);
            if (imageFile.exists()) {
                MD5 = getFileMD5String(imageFile);
            }
            if(MD5 == null || MD5.length() < 16){
                return null;
            }
            MD5 = MD5.substring(0,16);
//            ImageTools.saveMD5Exif(ImagePath,MD5);//原图Exif不做处理（只有jpeg有Exif信息）
        } else {
            MD5 = MD5.substring(4,MD5.length());
        }
        return MD5;
    }
}