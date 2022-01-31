/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.activity.results.util;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 文件路径、操作类
 */
public class FileAccessor {

    private static final String TAG = FileAccessor.class.getSimpleName();

    public static final String APPS_FILE_PATH = "/QPath";
    public static final String APPS_ROOT_DIR = getExternalStorePath() + APPS_FILE_PATH;

    /**
     * 此处新增文件夹需要在初始换方法内创建，并在清空缓存的地方增加清空处理。
     **/
    public static final String IMESSAGE_VOICE = APPS_ROOT_DIR + "/voice";
    public static final String IMESSAGE_IMAGE = APPS_ROOT_DIR + "/image";
    public static final String IMESSAGE_AVATAR = APPS_ROOT_DIR + "/avatar";
    public static final String IMESSAGE_FILE = APPS_ROOT_DIR + "/file";
    public static final String PATH_TEMP = getInnerFilesPath() + "/.temp"; //保存临时图片的地址
    public static final String PATH_DOWNLOAD = APPS_ROOT_DIR + "/download"; //保存下载文件的地址
    public static final String PATH_LOG = APPS_ROOT_DIR + "/log"; //存储性能监测日志与文件日志

    /**
     * 内部路径初始化
     */
    public static void initInnerPath() {
        File tempPic = new File(PATH_TEMP);
        if (!tempPic.exists()) {
            tempPic.mkdir();
        }
    }

    /**
     * 初始化应用文件夹目录
     */
    public static void initFileAccess() {
        File rootDir = new File(APPS_ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }

        File imessageDir = new File(IMESSAGE_VOICE);
        if (!imessageDir.exists()) {
            imessageDir.mkdir();
        }

        File imageDir = new File(IMESSAGE_IMAGE);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }

        File fileDir = new File(IMESSAGE_FILE);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        File avatarDir = new File(IMESSAGE_AVATAR);
        if (!avatarDir.exists()) {
            avatarDir.mkdir();
        }

        File pic = new File(APPS_ROOT_DIR);
        if (!pic.exists()) {
            pic.mkdir();
        }

        File video = new File(PATH_DOWNLOAD);
        if (!video.exists()) {
            video.mkdir();
        }

        File log = new File(PATH_LOG);
        if (!log.exists()) {
            log.mkdir();
        }
    }

    /**
     * 清空缓存,删除二级目录下的文件夹和文件
     */
    public static void clearCacheFile() {
        FileUtil.deleteFolderFile(APPS_ROOT_DIR, true);
        initFileAccess();
    }

    public static String readContentByFile(String path) {
        BufferedReader reader = null;
        String line = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
                    sb.append(line.trim());
                }
                return sb.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String pathName) {
        int start = pathName.lastIndexOf("/");
        if (start != -1) {
            return pathName.substring(start + 1);
        }
        return pathName;
    }

    /**
     * 外置存储卡的路径
     */
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            //解决Android11 文件下载功能失效的问题
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (MyApplication.mApplication.getExternalFilesDir(null) != null) {
                    return MyApplication.mApplication.getExternalFilesDir(null).getAbsolutePath();
                } else {
                    return null;
                }
            }
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * APP内部存储路径（不用读写权限）
     */
    public static String getInnerFilesPath() {
        if (MyApplication.mApplication.getFilesDir() != null) {
            return MyApplication.mApplication.getFilesDir().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 是否有外存卡
     */
    public static boolean isExistExternalStore() {
        return true;
    }

    public static void delFiles(ArrayList<String> filePaths) {
        for (String url : filePaths) {
            if (!TextUtils.isEmpty(url)) {
                delFile(url);
            }
        }
    }

    public static boolean delFile(String filePath) {
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return true;
        }
        return file.delete();
    }
}
