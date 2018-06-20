package com.gjs.developresponsity.utils.base;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/06/13
 *     desc    : 获取手机内存大小
 *     version : 1.0
 * </pre>
 */
public class PhoneStateUtil {

    private static final String TAG = PhoneStateUtil.class.getName();
    private static final String MEM_INFO_PATH = "/proc/meminfo";
    public static final String MEMTOTAL = "MemTotal";
    public static final String MEMFREE = "MemFree";

    /**
     * 得到内存大小
     *
     * @param context
     * @param memtotal
     * @return
     */
    public static String getTotalMemory(Context context) {
        return getMemInfoIype(context, MEMTOTAL);
    }

    /**
     * 得到可用内存大小
     *
     * @param context
     * @param memfree
     * @return
     */
    public static String getMemoryFree(Context context) {
        return getMemInfoIype(context, MEMFREE);
    }

    /**
     * 得到type info
     *
     * @param context
     * @param type
     * @return
     */
    public static String getMemInfoIype(Context context, String type) {
        try {
            FileReader fileReader = new FileReader(MEM_INFO_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 4 * 1024);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains(type)) {
                    break;
                }
            }
            bufferedReader.close();
            /* \\s表示   空格,回车,换行等空白符,
            +号表示一个或多个的意思     */
            String[] array = str.split("\\s+");
            // 获得系统总内存，单位是KB，乘以1024转换为Byte
            int length = Integer.valueOf(array[1]).intValue() * 1024;
            return android.text.format.Formatter.formatFileSize(context, length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    //字节大小，K,M,G
//    public static final long KB = 1024;
//    public static final long MB = KB * 1024;
//    public static final long GB = MB * 1024;
//
//    /**
//     * 文件字节大小显示成M,G和K
//     * @param size
//     * @return
//     */
//    public static String displayFileSize(long size) {
//        if (size >= GB) {
//            return String.format("%.1f GB", (float) size / GB);
//        } else if (size >= MB) {
//            float value = (float) size / MB;
//            return String.format(value > 100 ? "%.0f MB" : "%.1f MB", value);
//        } else if (size >= KB) {
//            float value = (float) size / KB;
//            return String.format(value > 100 ? "%.0f KB" : "%.1f KB", value);
//        } else {
//            return String.format("%d B", size);
//        }
//    }


}
