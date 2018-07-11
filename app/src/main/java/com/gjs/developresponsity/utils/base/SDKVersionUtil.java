package com.gjs.developresponsity.utils.base;

import android.content.Context;
import android.os.Build.VERSION;

public class SDKVersionUtil {
    public static final int ECLAIR_MR1 = 7;
    public static final int FROYO = 8;
    public static final int GINGERBREAD = 9;
    public static final int GINGERBREAD_MR1 = 10;
    public static final int HONEYCOMB = 11;
    public static final int HONEYCOMB_MR1 = 12;
    public static final int HONEYCOMB_MR2 = 13;
    public static final int ICE_CREAM_SANDWICH = 14;
    public static final int ICE_CREAM_SANDWICH_MR1 = 15;
    public static final int JELLY_BEAN = 16;

    public SDKVersionUtil() {
    }

    public static boolean hasECLAIR_MR1() {
        return VERSION.SDK_INT >= 7;
    }

    public static boolean hasFroyo() {
        return VERSION.SDK_INT >= 8;
    }

    public static boolean hasGingerbread() {
        return VERSION.SDK_INT >= 9;
    }

    public static boolean hasHoneycomb() {
        return VERSION.SDK_INT >= 11;
    }

    public static boolean hasHoneycombMR1() {
        return VERSION.SDK_INT >= 12;
    }

    public static boolean hasHoneycombMR2() {
        return VERSION.SDK_INT >= 13;
    }

    public static boolean hasICS() {
        return VERSION.SDK_INT >= 14;
    }

    public static boolean hasJellyBean() {
        return VERSION.SDK_INT >= 16;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static boolean isHoneycombTablet(Context context) {
        return hasHoneycomb() && isTablet(context);
    }
}
