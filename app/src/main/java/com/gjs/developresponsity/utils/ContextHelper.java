package com.gjs.developresponsity.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by xxhx on 2016/12/2.
 */

public class ContextHelper {
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        if(sContext == null) {
            throw new RuntimeException("Context is null");
        }
        return sContext;
    }

    public static String getString(int resId) {
        return getContext().getString(resId);
    }

    public static String getString(int resId, Object... formatArgs) {
        return getContext().getString(resId, formatArgs);
    }

    public static ContentResolver getContentResolver() {
        return getContext().getContentResolver();
    }

    public static SharedPreferences getSharedPreferences(String name, int mode) {
        return getContext().getSharedPreferences(name, mode);
    }

    public static Object getSystemService(@NonNull String name) {
        return getContext().getSystemService(name);
    }
}
