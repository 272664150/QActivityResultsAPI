package com.activity.results.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import java.util.List;

public final class ActivityUtils {

    private ActivityUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * @param context
     * @param activityName
     * @return
     * @Description 判断是否是顶部activity
     */
    public static boolean isTopActivity(Context context, String activityName) {
        if (context == null) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cName = am.getRunningTasks(1).size() > 0 ? am.getRunningTasks(1).get(0).topActivity : null;

        if (null == cName) {
            return false;
        }
        return cName.getClassName().equals(activityName);
    }

    /**
     * 去设置页面
     * 1.优先跳转到该应用设置页
     * 2.如果1的条件不满意，找不到应用设置页，则跳转到应用管理列表页
     */
    public static void goToSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.getPackageName(), null));
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断是否安装了支付宝
     *
     * @return true 为已经安装
     */
    public static boolean hasAliPayApp(Context context) {
        PackageManager manager = context.getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse("alipays://"));
        List list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }

    /**
     * 校验传给Glide的上下文是否合法
     */
    public static boolean isValidContext(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
}
