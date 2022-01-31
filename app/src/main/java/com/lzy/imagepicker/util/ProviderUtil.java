package com.lzy.imagepicker.util;

import android.content.Context;

/**
 * 用于解决provider冲突
 */
public class ProviderUtil {

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".provider";
    }
}
