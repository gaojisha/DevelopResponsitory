package com.gjs.developresponsity;

import android.app.Application;

import com.gjs.developresponsity.database.DaoMaster;
import com.gjs.developresponsity.database.DaoSession;
import com.gjs.developresponsity.database.UserDao;
import com.gjs.developresponsity.database.VideoInfoDao;
import com.tencent.bugly.crashreport.CrashReport;

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

    @Override
    public void onCreate() {
        super.onCreate();

        //bugly异常捕获配置初始化
        CrashReport.initCrashReport(getApplicationContext(), "2dacfad42e", true);

        //greendao初始化
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "zpf.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        videoInfoDao = daoSession.getVideoInfoDao();
        userDao = daoSession.getUserDao();
    }
}
