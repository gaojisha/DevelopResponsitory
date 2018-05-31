package com.gjs.developresponsity.utils.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/05/25
 *     desc    : 必须说明该类的作用
 *     version : 1.0
 *
 *     博客地址 : https://blog.csdn.net/totcw/article/details/53409392
 * </pre>
 */

public class PermissionUtil {

    public static void checkPermission(Activity activity,String[] permissions, PermissionInterface permissionInterface){
        //版本小于23时，什么都不做
        if(Build.VERSION.SDK_INT < 23){
            return;
        }
        List<String> deniedPermission = findDeniedPermissions(activity, permissions);
        if(deniedPermission != null && deniedPermission.size() > 0){
//            requestContactPermissions(activity,view,deniedPermission.toArray(new String[deniedPermission.size()]),requestCode);
            permissionInterface.failed(deniedPermission);
        } else {
            //拥有权限
            permissionInterface.success();
        }
    }

    /**
     * 找到没有授权的权限
     * @param activity 检查是否授权需要用到
     * @param permission 需要检查是否授权的权限
     * @return
     */
    public static List<String> findDeniedPermissions(Activity activity,String... permission){
        //存储没有授权的权限
        List<String> denyPermissions = new ArrayList<>();
        for (String value:permission){
            if(ContextCompat.checkSelfPermission(activity,value) == PackageManager.PERMISSION_DENIED){
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    public static void requestContactPermissions(final Activity activity, final String[] permissions, final int requestCode){
        //默认是false，但是请求过一次权限就回为true，除非点了不再询问才会重新变成false
        if(shouldShowPermissions(activity,permissions)){
            //去掉虚拟按键
            activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | activity.getWindow().getDecorView().SYSTEM_UI_FLAG_HIDE_NAVIGATION //隐藏虚拟按键栏
                    | activity.getWindow().getDecorView().SYSTEM_UI_FLAG_IMMERSIVE //防止点击屏幕时,隐藏虚拟按键栏又弹了出来
            );
            Snackbar.make(activity.getWindow().getDecorView(), "需要一些权限",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(activity, permissions,
                                            requestCode);
                        }
                    })
                    .show();
        } else {
            //无需向用户界面提示，直接请求权限，如果用户点了不再询问，即使调用请求权限也不会出现请求权限的对话框
            ActivityCompat.requestPermissions(activity,permissions,requestCode);
        }
    }

    public static boolean shouldShowPermissions(Activity activity,String... permission){
        for(String value:permission) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断请求权限是否成功
     *
     * @param grantResults
     * @return
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public interface PermissionInterface {
        void success();
        void failed(List<String> permissions);
    }
}
