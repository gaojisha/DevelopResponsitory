package com.gjs.developresponsity.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.gjs.developresponsity.video.tools.broadcast.DataBroadcast;

import java.io.File;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/07/04
 *     desc    : 必须说明该类的作用
 *     version : 1.0
 * </pre>
 */
public class Constants {
    public static final String PATH_DOWN_VIDEO = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            "FileDownloader";

    protected static Context context = null;

    private DataBroadcast mBroadcase = null;

    public HandlerThread handlerThread = null;

    public Handler workHandler = null;

    public Handler mHandler = null;

    private static Constants mInstance;

    private Constants(Context base){
        mBroadcase = new DataBroadcast(base);

        mHandler = new Handler();
        handlerThread = new HandlerThread("CoreThread");
        handlerThread.start();
        workHandler = new Handler(handlerThread.getLooper());

//		init();
    }

    public static Constants getInstance(){
        if(mInstance == null) {
            return new Constants(context);
        } else {
            return mInstance;
        }
    }

    public DataBroadcast getBroadcast() {
        return mBroadcase;
    }

    public static void init(Context base){
        context = base;
    }
}
