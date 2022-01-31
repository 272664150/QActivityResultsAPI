package com.activity.results.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

public class ResUtil {

    /**
     * 资源文件id 转为 uri
     * R.drawable.pic --> android.resource://{package name}/drawable/pic
     * R.layout.login --> android.resource://{package name}/layout/login
     *
     * @return
     */
    public static String drawableToUri(Context context, int resId) {
        Resources r = context.getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(resId) + "/"
                + r.getResourceTypeName(resId) + "/"
                + r.getResourceEntryName(resId));
        return uri.toString();
    }

    /**
     * 根据资源文件的uri,获取资源id
     * android.resource://{package name}/drawable/pic --> R.drawable.pic
     *
     * @param context
     * @param resUri
     * @return
     */
    public static int getIdentifier(Context context, String resUri) {
        int resId = 0;
        Uri uri = Uri.parse(resUri);
        String packageName = uri.getHost();
        String path = uri.getPath();
        if (path != null) {
            String[] data = path.split("/");
            resId = context.getResources().getIdentifier(data[2], data[1], packageName);
        }
        return resId;
    }

    public static boolean isResPath(String path) {
        return path.startsWith(ContentResolver.SCHEME_ANDROID_RESOURCE + "://");
    }
}
