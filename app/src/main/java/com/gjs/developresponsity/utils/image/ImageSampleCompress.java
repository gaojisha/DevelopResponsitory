package com.gjs.developresponsity.utils.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/05/24
 *     desc    : 必须说明该类的作用
 *     version : 1.0
 * </pre>
 */

public class ImageSampleCompress {



    /**
     * 设置bitmap option属性(0-100)，降低图片质量，像素不会减少
     *
     * @param bitmap 压缩的图片
     * @param file 压缩后保存的文件位置
     */
    public static void compressImageToFile(Bitmap bitmap, File file){
        int option = 100;//0-100，100为不压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //压缩后数据保存到
        bitmap.compress(Bitmap.CompressFormat.JPEG, option, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过减少图片像素来减少图片所占内存空间
     *
     * @param bitmap 需要压缩的图片
     * @param file 保存图片到文件
     */
    public static void compressBitmapToFile(Bitmap bitmap,File file){
        //图片压缩倍数，值越大，图片越小
        int ratio = 2;
        //压缩图片到对应尺寸
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth()/ratio,bitmap.getHeight()/ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bitmap.getWidth()/ratio, bitmap.getHeight()/ratio);
        canvas.drawBitmap(bitmap, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //把压缩的图片存放到baos
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置图片采样率，降低图片像素
     *
     * @param filepath 需要压缩的图片
     * @param file 压缩后图片保存的文件
     */
    public static void compressBitmap(String filepath,File file){
        //数值越高，像素越低
        int sampleOptions = 2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleOptions;
        Bitmap bitmap = BitmapFactory.decodeFile(filepath,options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
