package com.gjs.developresponsity.utils.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gjs.developresponsity.activity.PermissionActivity;

import java.util.List;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/05/25
 *     desc    : 必须说明该类的作用
 *     version : 1.0
 * </pre>
 */

public class Permission {
    public static PermissionResult mPermissionResult;
    public static void checkPermission(final Context context, final String[] permission, PermissionResult permissionResult){
        mPermissionResult = permissionResult;
        PermissionUtil.checkPermission((Activity) context, permission, new PermissionUtil.PermissionInterface() {
            @Override
            public void success() {
                if(mPermissionResult != null){
                    mPermissionResult.success();
                }
            }

            @Override
            public void failed(List<String> permissions) {
                Intent intent = new Intent(context, PermissionActivity.class);
                intent.putExtra("permission", permissions.toArray(new String[permissions.size()]));
                context.startActivity(intent);
            }
        });
    }
}
