package com.gjs.developresponsity.utils.base;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/07/09
 *     desc    : 必须说明该类的作用
 *     version : 1.0
 * </pre>
 */
public class FileUtils {

    public static final int BUFSIZE = 2048;
    private static final String TAG = FileUtils.class.getSimpleName();
    private static String aU;
    public static String tmpImagePath;
    public static String tmpDefaultImagePath;
    public static String tmpDefaultImageReceivePath;
    public static String tmpImageReceivePath;
    public static String tmpImageChangeMd5;

    public FileUtils() {
    }

    public static boolean prepareDir(String dirFile) {
        if(!dirFile.endsWith(File.separator)){
            return false;
        } else {
            File file = new File(dirFile);
            if(!file.exists() && !file.mkdirs()){
                return false;
            } else {
                return true;
            }
        }
    }

    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            prepareDir(parentFile.getAbsolutePath());
        }

        try {
            boolean result = file.createNewFile();
            Log.i("createFile", "create folder:" + filePath + ",result:" + result);
            return result;
        } catch (IOException var4) {
            var4.printStackTrace();
            Log.i("createFile", "create folder:" + filePath + ",result:false");
            return false;
        }
    }

    public static String getFileSize(long lvalue) {
        float ftmp = (float)lvalue;
        DecimalFormat df;
        if (lvalue >= 1048576L) {
            df = new DecimalFormat("#.00 ");
            return df.format((double)(ftmp / 1048576.0F)) + "MB";
        } else if (lvalue < 1048576L && lvalue > 1024L) {
            df = new DecimalFormat("#.00 ");
            return df.format((double)(ftmp / 1024.0F)) + "KB";
        } else if (lvalue > 0L && lvalue <= 1024L) {
            df = new DecimalFormat("0.00 ");
            return df.format((double)(ftmp / 1024.0F)) + "KB";
        } else {
            return "0.00KB";
        }
    }

    public static String formatPath(String path) {
        return path.indexOf("/sd") > -1 ? path.substring(path.indexOf("/sd")) : path;
    }

    public static byte[] fileToByteArray(Context context, String path) {
        InputStream is = null;
        byte[] data = null;

        try {
            File file = null;
            if ("content".equals(Uri.parse(path).getScheme())) {
                ContentResolver cr = context.getContentResolver();
                Uri imageUri = Uri.parse(path);
                String[] projection = new String[]{"_data"};
                Cursor cursor = cr.query(imageUri, projection, (String)null, (String[])null, (String)null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                cursor.moveToFirst();
                file = new File(cursor.getString(column_index));
            } else if ("file".equals(Uri.parse(path).getScheme())) {
                file = new File(Uri.parse(path).getPath());
            } else {
                file = new File(path);
            }

            is = new FileInputStream(file);
            data = new byte[is.available()];
            int i = 0;

            int temp;
            for(boolean var21 = false; (temp = is.read()) != -1; ++i) {
                data[i] = (byte)temp;
            }
        } catch (Exception var18) {
            var18.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException var17) {
                var17.printStackTrace();
            }

        }

        return data;
    }

    public static Uri getUri(File file) {
        return file != null ? Uri.fromFile(file) : null;
    }

    public static File getFile(Uri uri) {
        if (uri != null) {
            String filepath = uri.getPath();
            if (filepath != null) {
                return new File(filepath);
            }
        }

        return null;
    }

    public static File getFile(String curdir, String file) {
        String separator = "/";
        if (curdir.endsWith("/")) {
            separator = "";
        }

        File clickedFile = new File(curdir + separator + file);
        return clickedFile;
    }

    public static File contentUriToFile(Context context, Uri uri) {
        File file = null;
        if (uri != null) {
            if (uri.toString().startsWith("file://")) {
                String path = uri.getPath();
                file = new File(path);
            } else {
                String[] proj = new String[]{"_data"};
                Cursor actualimagecursor = ((Activity)context).managedQuery(uri, proj, (String)null, (String[])null, (String)null);
                if (actualimagecursor != null) {
                    int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow("_data");
                    actualimagecursor.moveToFirst();
                    String img_path = actualimagecursor.getString(actual_image_column_index);
                    if (img_path != null) {
                        file = new File(img_path);
                    }

                    if (!SDKVersionUtil.hasICS() && actualimagecursor != null) {
                        actualimagecursor.close();
                    }
                }
            }
        }

        return file;
    }

    public static InputStream getFileInputStream(File file) {
        FileInputStream is = null;

        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        }

        return is;
    }

    public static boolean deleteFile(File file) {
        boolean delete = false;

        try {
            delete = file.delete();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return delete;
    }

    public static boolean deleteFile(String path) {
        boolean delete = false;

        try {
            File file = new File(path);
            if (file.exists()) {
                delete = file.delete();
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return delete;
    }

    public static void delete(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    File[] var3 = files;
                    int var4 = files.length;

                    for(int var5 = 0; var5 < var4; ++var5) {
                        File f = var3[var5];
                        if (f.isDirectory()) {
                            delete(f.getAbsolutePath());
                        } else {
                            f.delete();
                        }
                    }
                } else {
                    file.delete();
                }
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public static void deleteFile(File file, boolean includedir) {
        if (file != null && file.exists()) {
            if (!file.isDirectory()) {
                file.delete();
            } else {
                File[] allfiles = file.listFiles();
                if (allfiles.length == 0 && includedir) {
                    file.delete();
                }

                File[] var3 = allfiles;
                int var4 = allfiles.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    File file2 = var3[var5];
                    deleteFile(file2, true);
                }

                if (includedir) {
                    file.delete();
                }
            }
        }

    }

    public static boolean deleteFileArrays(String[] delFilePaths) {
        if (delFilePaths != null) {
            File delFile = null;
            String[] var2 = delFilePaths;
            int var3 = delFilePaths.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String delFilePath = var2[var4];
                delFile = new File(delFilePath);
                if (delFile.exists()) {
                    return delFile.delete();
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static File getFile(File curdir, String file) {
        return getFile(curdir.getAbsolutePath(), file);
    }

    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();
                String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }

                return new File(pathwithoutname);
            }
        } else {
            return null;
        }
    }

    public static byte[] getByteArrayByFile(File file) {
        BufferedInputStream stream = null;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outsteam = null;

        try {
            FileInputStream in = new FileInputStream(file);
            stream = new BufferedInputStream(in);
            outsteam = new ByteArrayOutputStream();

            while(stream.read(buffer) != -1) {
                outsteam.write(buffer);
            }
        } catch (OutOfMemoryError var15) {
            var15.printStackTrace();
        } catch (Exception var16) {
            ;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                    outsteam.close();
                } catch (Exception var14) {
                    var14.printStackTrace();
                }
            }

        }

        return outsteam.toByteArray();
    }

    public static void saveByteToFile(File f, byte[] buff) {
        FileOutputStream fOut = null;

        try {
            if (buff != null && buff.length != 0) {
                if (f.exists()) {
                    f.delete();
                }

                f.getParentFile().mkdirs();
                f.createNewFile();
                fOut = new FileOutputStream(f);
                fOut.write(buff);
                fOut.flush();
            }
        } catch (FileNotFoundException var16) {
            var16.printStackTrace();
        } catch (IOException var17) {
            var17.printStackTrace();
        } catch (Exception var18) {
            var18.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException var15) {
                var15.printStackTrace();
            }

        }

    }

    public static void saveByteToPath(String path, ArrayList<byte[]> buffs) {
        FileOutputStream fOut = null;
        File f = new File(path);

        try {
            fOut = new FileOutputStream(f);

            for(int i = 0; i < buffs.size(); ++i) {
                fOut.write((byte[])buffs.get(i));
            }

            fOut.flush();
        } catch (FileNotFoundException var15) {
            var15.printStackTrace();
        } catch (IOException var16) {
            var16.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }

    }

    public static void saveByteToPath(String path, byte[] buffs) {
        FileOutputStream fOut = null;
        File f = new File(path);

        try {
            fOut = new FileOutputStream(f);
            fOut.write(buffs);
            fOut.flush();
        } catch (FileNotFoundException var15) {
            var15.printStackTrace();
        } catch (IOException var16) {
            var16.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }

    }

    public static void saveByteToFile(File f, ArrayList<byte[]> buffs) {
        FileOutputStream fOut = null;

        try {
            if (f.exists()) {
                f.delete();
            }

            f.createNewFile();
            fOut = new FileOutputStream(f);

            for(int i = 0; i < buffs.size(); ++i) {
                fOut.write((byte[])buffs.get(i));
            }

            fOut.flush();
        } catch (FileNotFoundException var14) {
            var14.printStackTrace();
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

    }

    public static void saveByteToSDCard(Context context, File f, byte[] buff) throws IOException {
        FileOutputStream fOut = null;

        try {
            fOut = new FileOutputStream(f);
            if (buff != null) {
                fOut.write(buff);
            }

            fOut.flush();
            refreshAlbum(context, f);
        } catch (FileNotFoundException var13) {
            var13.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            }

        }

    }

    public static String saveByteToData(Context context, Bitmap bitmap, String filePath) throws IOException {
        try {
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, fos);
            fos.flush();
            fos.close();
            return filePath;
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return null;
    }

    public static void refreshAlbum(Context context, File imageFile) {
        if (context != null) {
            Uri localUri = Uri.fromFile(imageFile);
            Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", localUri);
            context.sendBroadcast(localIntent);
        }

    }

    public static void refreshAlbum(Context context, Uri localUri) {
        if (context != null) {
            Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", localUri);
            context.sendBroadcast(localIntent);
        }

    }

    public static void copyFile(String fileFromPath, String fileToPath) throws IOException {
        if (!fileFromPath.equals(fileToPath)) {
            InputStream in = null;
            FileOutputStream out = null;

            try {
                in = new FileInputStream(fileFromPath);
                out = new FileOutputStream(fileToPath);
                int length = in.available();
                int len = length % 2048 == 0 ? length / 2048 : length / 2048 + 1;
                byte[] temp = new byte[2048];

                for(int i = 0; i < len; ++i) {
                    if (i == len - 1) {
                        temp = new byte[length % 2048];
                    }

                    in.read(temp);
                    out.write(temp);
                }
            } finally {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.close();
                }

            }
        }

    }

    public static boolean copyFile(InputStream inputStream, String destFilePath) {
        int bufferSize = 8192;
        FileOutputStream out = null;

        boolean var5;
        try {
            out = new FileOutputStream(destFilePath);
            byte[] buffer = new byte[bufferSize];
            var5 = false;

            int reacCount;
            while((reacCount = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, reacCount);
            }

            out.flush();
            boolean var6 = true;
            return var6;
        } catch (Exception var16) {
            var16.printStackTrace();
            var5 = false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

        return var5;
    }

    public static String readFile(File file) {
        String data = "";

        try {
            FileInputStream stream = new FileInputStream(file);
            StringBuffer sb = new StringBuffer();

            int c;
            while((c = stream.read()) != -1) {
                sb.append((char)c);
            }

            stream.close();
            data = sb.toString();
        } catch (FileNotFoundException var5) {
            ;
        } catch (IOException var6) {
            ;
        }

        return data;
    }

    public static String ReadTxtFile(String strFilePath) {
        String content = "";
        File file = new File(strFilePath);
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);

                    String line;
                    for(BufferedReader buffreader = new BufferedReader(inputreader); (line = buffreader.readLine()) != null; content = content + line + "\n") {
                        ;
                    }

                    instream.close();
                }
            } catch (FileNotFoundException var8) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException var9) {
                Log.d("TestFile", var9.getMessage());
            }
        }

        return content;
    }

    public static ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
        ArrayList<byte[]> array = new ArrayList();
        if (spsize > 0 && length > 0 && buffer != null && buffer.length >= length) {
            int size = 0;

            while(size < length) {
                int left = length - size;
                byte[] sdata;
                if (spsize < left) {
                    sdata = new byte[spsize];
                    System.arraycopy(buffer, size, sdata, 0, spsize);
                    array.add(sdata);
                    size += spsize;
                } else {
                    sdata = new byte[left];
                    System.arraycopy(buffer, size, sdata, 0, left);
                    array.add(sdata);
                    size += left;
                }
            }

            return array;
        } else {
            return array;
        }
    }

    public static String getFilePath(Context context, Uri imageUri) {
        String imagePath = null;
        Cursor cursor = context.getContentResolver().query(imageUri, (String[])null, (String)null, (String[])null, (String)null);
        if (cursor != null) {
            cursor.moveToFirst();
            imagePath = cursor.getString(1);
            cursor.close();
        } else {
            int start = imageUri.toString().indexOf("file://");
            if (start > -1) {
                imagePath = imageUri.toString().substring(start + "file://".length());
            } else {
                imagePath = imageUri.toString();
            }
        }

        return imagePath;
    }

    public static int getImageRotateDegrees(String path) {
        int degrees = 0;
        if (!TextUtils.isEmpty(path)) {
            try {
                ExifInterface exifInterface = new ExifInterface(path);
                int tag = exifInterface.getAttributeInt("Orientation", -1);
                if (tag == 6) {
                    degrees = 90;
                } else if (tag == 3) {
                    degrees = 180;
                } else if (tag == 8) {
                    degrees = 270;
                }
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        return degrees;
    }

    public static String getFileName(String path, String name) {
        File parentFile = new File(path);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        File originFile = new File(path + name);
        if (!originFile.exists()) {
            return name;
        } else {
            String[] nameSplit = name.split("\\.");
            String head = null;
            String end = null;
            int len = nameSplit.length;
            if (len == 1) {
                head = name;
                end = "";
            } else {
                StringBuilder sb = new StringBuilder();

                for(int k = 0; k < len - 1; ++k) {
                    sb.append(nameSplit[k]);
                }

                head = sb.toString();
                end = nameSplit[len - 1];
            }

            int i = 1;
            String newName = null;

            while(true) {
                newName = head + "(" + i + ")." + end;
                File fl = new File(path + newName);
                if (!fl.exists()) {
                    return newName;
                }

                ++i;
            }
        }
    }

    public static File getFile(Context context, Uri uri) {
        String filePath = null;
        Cursor cursor = context.getContentResolver().query(uri, (String[])null, (String)null, (String[])null, (String)null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(1);
            cursor.close();
        } else {
            int start = uri.toString().indexOf("file://");
            if (start > -1) {
                filePath = uri.toString().substring(start + "file://".length());
            } else {
                filePath = uri.toString();
            }
        }

        return TextUtils.isEmpty(filePath) ? null : new File(filePath);
    }

    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;

        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];

            int n;
            while((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException var7) {
            var7.printStackTrace();
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        return buffer;
    }

    static {
        aU = Environment.getExternalStorageDirectory() + File.separator + "icbcim" + File.separator + "icbcim" + File.separator + "urapportclient";
        tmpImagePath = aU + File.separator + "tmp_image";
        tmpDefaultImagePath = aU + File.separator + "tmp_default_image";
        tmpDefaultImageReceivePath = aU + File.separator + "tmp_default_receive_image";
        tmpImageReceivePath = aU + File.separator + "tmp_image_receive";
        tmpImageChangeMd5 = aU + File.separator + "tmp_image_Md5";
    }

}
