package com.gjs.developresponsity;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/05/25
 *     desc    : 必须说明该类的作用
 *     version : 1.0
 * </pre>
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "2dacfad42e", false);
    }
}
