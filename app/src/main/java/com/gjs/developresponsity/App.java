package com.gjs.developresponsity;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.gjs.developresponsity.database.DaoMaster;
import com.gjs.developresponsity.database.DaoSession;
import com.gjs.developresponsity.database.UserDao;
import com.gjs.developresponsity.database.VideoInfoDao;
import com.gjs.developresponsity.utils.Constants;
import com.gjs.developresponsity.utils.ContextHelper;
import com.gjs.developresponsity.video.controller.SdcardImageController;
import com.tencent.bugly.crashreport.CrashReport;

import org.wlf.filedownloader.FileDownloadConfiguration;
import org.wlf.filedownloader.FileDownloader;

import java.io.File;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/05/25
 *     desc    : application
 *     version : 1.0
 * </pre>
 */

public class App extends Application {

    public static VideoInfoDao videoInfoDao;
    public static UserDao userDao;
    public static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(getApplicationContext());//Fresco初始化

        SdcardImageController.init(getApplicationContext());

        Constants.init(getApplicationContext());

        ContextHelper.init(getApplicationContext());

        //bugly异常捕获配置初始化
        CrashReport.initCrashReport(getApplicationContext(), "2dacfad42e", true);

        //greendao初始化
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "zpf.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        videoInfoDao = daoSession.getVideoInfoDao();
        userDao = daoSession.getUserDao();

        FileDownloadConfiguration.Builder builder = new FileDownloadConfiguration.Builder(this);

        // 2.配置Builder
        // 配置下载文件保存的文件夹
        builder.configFileDownloadDir(Constants.PATH_DOWN_VIDEO);
        // 配置同时下载任务数量，如果不配置默认为2
        builder.configDownloadTaskSize(3);
        // 配置失败时尝试重试的次数，如果不配置默认为0不尝试
        builder.configRetryDownloadTimes(5);
        // 开启调试模式，方便查看日志等调试相关，如果不配置默认不开启
        builder.configDebugMode(true);
        // 配置连接网络超时时间，如果不配置默认为15秒
        builder.configConnectTimeout(25000);// 25秒

        // 3、使用配置文件初始化FileDownloader
        FileDownloadConfiguration configuration = builder.build();
        FileDownloader.init(configuration);
    }

}
