package com.gjs.developresponsity.utils;

import android.os.Environment;

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
}
