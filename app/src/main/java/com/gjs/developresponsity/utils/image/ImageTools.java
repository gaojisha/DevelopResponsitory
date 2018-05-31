package com.gjs.developresponsity.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import com.gjs.developresponsity.utils.MD5Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 操作图片的工具类
 *
 * @author young
 */
public class ImageTools {
    // 临时文件夹
    private final static String temp = "/sdcard/icbc/temp/";

    /**
     * 图片质量
     *
     * @author young
     */
    public enum Quality {
        BIG(1), THUM(2), PORTRAIT(3), SMALL(4);

        private int q;

        private Quality(int q) {
            this.q = q;
        }

        public int getQuality() {
            return q;
        }
    }

    /**
     * 压缩图片获取BitMap数据
     *
     * @param oldimage 原图
     * @param q        图片质量枚举
     * @return
     */
    public static Bitmap getCommpressImage(String inputFilepath, Quality q) {
        File dirFile = new File(temp);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File jpegTrueFile = new File(dirFile, new java.util.Date().getTime() + ".jpg");
        ImageNativeUtil.compressBitmap(inputFilepath, jpegTrueFile.getAbsolutePath(), true, q);
        Bitmap nImage = BitmapFactory.decodeFile(jpegTrueFile.getAbsolutePath());
        jpegTrueFile.delete();
        return nImage;
    }

    /**
     * 压缩图片获取字节流
     *
     * @param oldimage
     * @param q
     * @return
     */
    public static byte[] getCommpressImage2Byte(String inputFilepath, Quality q) {
        try {
            File dirFile = new File(temp);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            String MD5 = MD5Util.getImageMD5(inputFilepath);
            File jpegTrueFile = new File(dirFile, new java.util.Date().getTime() + ".jpg");
            ImageNativeUtil.compressBitmap(inputFilepath, jpegTrueFile.getAbsolutePath(), true, q);
            //增加方向exif
            setExif(inputFilepath, jpegTrueFile.getAbsolutePath());
            saveMD5Exif(jpegTrueFile.getAbsolutePath(), MD5);
            byte[] newf = getBytesFromFile(jpegTrueFile);
            jpegTrueFile.delete();
            return newf;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 存储裁剪图片
     *
     * @param q 图片质量枚举
     * @return
     */
    public static void saveCutCompressImage(String inputFilepath, Quality q, String outputFilepath, int s_x, int s_y, int e_x, int e_y) {
        ImageNativeUtil.cutCompressBitmap(inputFilepath, outputFilepath, true, q, s_x, s_y, e_x, e_y);
    }

    /**
     * 存储压缩图片
     *
     * @param oldimage 原图
     * @param q        图片质量枚举
     * @param filename 存储文件名
     * @return
     */
    public static void saveCommpressImage(String inputFilepath, Quality q, String outputFilepath) {
        ImageNativeUtil.compressBitmap(inputFilepath, outputFilepath, true, q);
        //增加方向exif
        setExif(inputFilepath, outputFilepath);
    }

    /**
     * 文件转化为字节数组
     *
     * @param file
     * @return
     */
    private static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 加入方向exif
     *
     * @param input
     * @param output
     */
    private static void setExif(String input, String output) {
        try {
            ExifInterface inexif = new ExifInterface(input);
            ExifInterface outexif = new ExifInterface(output);
            String smodel = inexif.getAttribute(ExifInterface.TAG_ORIENTATION);
            outexif.setAttribute(ExifInterface.TAG_ORIENTATION, smodel);
            outexif.saveAttributes();
        } catch (IOException e) {
        }
    }

    public static void saveMD5Exif(String ImagePath, String MD5) {
        try {
            ExifInterface imagexif = new ExifInterface(ImagePath);
            imagexif.setAttribute(ExifInterface.TAG_MODEL, "MD5:" + MD5);
            imagexif.saveAttributes();
        } catch (IOException e) {
        }
    }

    public static String getMD5Exif(String imagePath) {
        String MD5 = "";
        try {
            ExifInterface imagexif = new ExifInterface(imagePath);
            MD5 = imagexif.getAttribute(ExifInterface.TAG_MODEL);
        } catch (IOException e) {
        }
        return MD5;
    }

    /**
     * 判断文件是否为图片文件(GIF,PNG,JPG)
     *
     * @param srcFileName
     * @return
     */
    public static String isImage(String srcFileName) {
        FileInputStream imgFile = null;
        byte[] b = new byte[10];
        int l = -1;
        try {
            imgFile = new FileInputStream(srcFileName);
            l = imgFile.read(b);
            imgFile.close();
        } catch (Exception e) {
            return "";
        }
        if (l == 10) {
            byte b0 = b[0];
            byte b1 = b[1];
            byte b2 = b[2];

            if ((b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') || (b0 == (byte) 'g' && b1 == (byte) 'i' && b2 == (byte) 'f')) {
                return ".gif";
            } else {
                return ".jpg";
            }
        } else {
            return ".jpg";
        }
    }

    /**
     * 判断文件是否为图片文件(GIF,PNG,JPG)
     *
     * @param srcFileName
     * @return
     */
    public static String isImage(byte[] b) {
        byte b0 = b[0];
        byte b1 = b[1];
        byte b2 = b[2];
        if ((b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') || (b0 == (byte) 'g' && b1 == (byte) 'i' && b2 == (byte) 'f')) {
            return ".gif";
        } else {
            return ".jpg";
        }
    }
}
