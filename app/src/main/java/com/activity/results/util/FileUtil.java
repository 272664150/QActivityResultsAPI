package com.activity.results.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.MyApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtil {

    public static String SDCardRoot;
    public static File updateFile;

    static {
        // 获取SD卡路径
        SDCardRoot = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !isExternalStorageRemovable()
                ? getExternalCacheDir(MyApplication.mApplication).getPath()
                : MyApplication.mApplication.getCacheDir().getPath();
    }

    @SuppressLint("NewApi")
    public static File getExternalCacheDir(Context context) {
        File dir = null;
        if (SysUtils.hasFroyo()) {
            dir = context.getExternalCacheDir();
        }
        if (dir != null) {
            return dir;
        }

        // Before Froyo we need to construct the external cache dir ourselves
        String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (SysUtils.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * 创建文件夹
     *
     * @throws IOException
     */
    public static File createFileInSDCard(String fileName, String dir) {
        File file_dir = new File(SDCardRoot + File.separator + dir);
        File file = new File(SDCardRoot + File.separator + dir + File.separator + fileName);

        try {
            if (!file_dir.exists()) {
                file_dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateFile = file;
        return file;
    }

    /**
     * 创建目录
     *
     * @param dir
     * @return
     */
    public static File creatSDDir(String dir) {
        File dirFile = new File(SDCardRoot + dir + File.separator);
        dirFile.mkdirs();
        return dirFile;
    }

    /**
     * 检测文件是否存在
     */
    public static boolean isFileExist(String fileName, String path) {
        File file = new File(SDCardRoot + path + File.separator + fileName);
        return file.exists();
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 通过流往文件里写东西
     */
    public static File writeToSDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            file = createFileInSDCard(fileName, path);
            output = new FileOutputStream(file, false);
            byte buffer[] = new byte[4 * 1024];
            int temp;
            while ((temp = input.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 把字符串写入文件
     */
    public static File writeToSDFromInput(String path, String fileName, String data) {
        File file = null;
        OutputStreamWriter outputWriter = null;
        OutputStream outputStream = null;
        try {
            creatSDDir(path);
            file = createFileInSDCard(fileName, path);
            outputStream = new FileOutputStream(file, false);
            outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(data);
            outputWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String getFromCipherConnection(String actionUrl, String content, String path) {
        try {
            File[] files = new File[1];
            files[0] = new File(path);
            String BOUNDARY = java.util.UUID.randomUUID().toString();
            String PREFIX = "--", LINEND = "\r\n";
            String MULTIPART_FROM_DATA = "multipart/form-data";
            String CHARSET = "UTF-8";
            URL uri = new URL(actionUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(5 * 1000); // 缓存的最长时间
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
            // 首先组拼文本类型的参数
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"userName\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
            sb.append(content);
            sb.append(LINEND);

            DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(sb.toString().getBytes());
            // 发送文件数据
            if (files != null) {
                for (File file : files) {
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"" + file.getName() + "\"; filename=\"" + file.getName() + "\"" + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                    sb1.append(LINEND);
                    outStream.write(sb1.toString().getBytes());
                    try {
                        InputStream is = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) != -1) {
                            outStream.write(buffer, 0, len);
                        }
                        is.close();
                        outStream.write(LINEND.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            outStream.close();
            // 得到响应码
            int res = conn.getResponseCode();
            if (res == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                StringBuilder result = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();
                conn.disconnect();// 断开连接
                return "true";
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] getFileContent(String fileName) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] bytes = new byte[length];
            fin.read(bytes);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void delete(String file) {
        delete(new File(file));
    }

    /**
     * @param file
     * @Description 删除文件或文件夹
     */
    public static void delete(File file) {
        if (!file.exists()) {
            return; // 不存在直接返回
        }

        if (file.isFile()) {
            file.delete(); // 若是文件则删除后返回
            return;
        }

        // 若是目录递归删除后,并最后删除目录后返回
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete(); // 如果是空目录，直接删除
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]); // 递归删除子文件或子文件夹
            }
            file.delete(); // 删除最后的空目录
        }
        return;
    }

    public static void clearFileDir(String dirPath) {
        clearFileDir(new File(dirPath));
    }

    /**
     * 删除文件夹下面的文件 文件夹保留
     */
    public static void clearFileDir(File file) {
        if (!file.exists()) {
            return; // 不存在直接返回
        }
        // 若是目录递归删除后,并最后删除目录后返回
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]); // 递归删除子文件或子文件夹
            }
        }
    }

    /**
     * @param dirFile  目录文件
     * @param timeLine 时间分隔线
     * @Description 删除目录下过旧的文件
     */
    public static void deleteHistoryFiles(File dirFile, long timeLine) {
        // 不存在或是文件直接返回
        if (!dirFile.exists() || dirFile.isFile()) {
            return;
        }

        try {
            // 如果是目录则删除过旧的文件
            if (dirFile.isDirectory()) {
                File[] childFiles = dirFile.listFiles();
                if (childFiles == null || childFiles.length == 0) {
                    return; // 如果是空目录就直接返回
                }
                // 遍历文件，删除过旧的文件
                Long fileTime;
                for (int i = 0; i < childFiles.length; i++) {
                    fileTime = childFiles[i].lastModified();
                    if (fileTime < timeLine) {
                        delete(childFiles[i]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File save2File(String savePath, String saveName, String crashReport) {
        try {
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, saveName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(crashReport.getBytes());
            fos.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unused")
    private static Bitmap getBitmap(InputStream fs) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        Bitmap imgBitmap = BitmapFactory.decodeStream(fs, null, opts);
        if (imgBitmap != null) {
            int width = imgBitmap.getWidth();
            int height = imgBitmap.getHeight();
            imgBitmap = Bitmap.createScaledBitmap(imgBitmap, width, height,
                    true);
        }
        return imgBitmap;
    }

    public static long getFileLen(File file) {
        long total = 0;
        try {
            InputStream is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                total += len;
            }
            is.close();
        } catch (Exception e) {

        }
        return total;
    }

    /**
     * 文件夹中文件个数
     */
    public static long getFileDirCount(String path) {
        File f = new File(path);
        long size = 0;
        try {
            if (f.exists()) {
                if (f.isDirectory()) {
                    if (f.list() != null) {
                        size = f.list().length;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取sdcard可用空间的大小
     */
    @SuppressWarnings("deprecation")
    public static long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

    public static byte[] file2Byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            buffer = bos.toByteArray();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 文件后缀名，例如doc、pdf，不包含.
     *
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * @param filePath
     * @return
     */
    public static boolean checkFile(String filePath) {
        if (TextUtils.isEmpty(filePath) || !(new File(filePath).exists())) {
            return false;
        }
        return true;
    }

    public static String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start >= end) {
            return "";
        }
        if (start != -1) {
            if (end != -1) {
                return pathandname.substring(start + 1, end);
            } else {    //防止文件名不带后缀
                return pathandname.substring(start + 1, pathandname.length());
            }
        } else {
            return null;
        }
    }

    public static String getMimeType(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        String type = null;
        String extension = getExtensionName(filePath.toLowerCase());
        if (!TextUtils.isEmpty(extension)) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }

        if (TextUtils.isEmpty(type) && filePath.endsWith("aac")) {
            type = "audio/aac";
        }
        return type;
    }

    /**
     * 根据文件名判断是否是图片
     *
     * @param fileName 图片全称
     * @return
     */
    public static boolean isImageByFileName(String fileName) {
        return isImageBySuffix(getExtensionName(fileName));
    }

    /**
     * 判断是否是图片
     *
     * @param suffix 扩展名
     * @return
     */
    public static boolean isImageBySuffix(String suffix) {
        if (TextUtils.isEmpty(suffix)) {
            return false;
        }
        if (suffix.equalsIgnoreCase("png")
                || suffix.equalsIgnoreCase("jpg")
                || suffix.equalsIgnoreCase("gif")
                || suffix.equalsIgnoreCase("bmp")
                || suffix.equalsIgnoreCase("jpeg")) {
            return true;
        }
        return false;
    }

    /**
     * 获取文件夹大小(递归)
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取当前文件夹大小，不递归子文件夹
     *
     * @param file
     * @return
     */
    public static long getCurrentFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    //跳过子文件夹
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param filePath
     * @return
     */
    public static boolean deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 删除指定目录下文件
     *
     * @param filePath
     * @return
     */
    public static void deleteFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    if (!fileList[i].isDirectory()) {
                        fileList[i].delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(long size) {
        long kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }

        long megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(String.valueOf(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        long gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(String.valueOf(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        long teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(String.valueOf(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * byte[] 转 file
     *
     * @param bytes
     * @param path
     * @return
     */
    public static void byteToFile(byte[] bytes, String path) {
        try {
            File localFile = new File(path);
            if (!localFile.exists()) {
                localFile.createNewFile();
            }

            OutputStream os = new FileOutputStream(localFile);
            os.write(bytes);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * inputStream 转 file
     *
     * @param is
     * @param path
     * @return
     */
    public static void inputStreamToFile(InputStream is, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024 * 8];
            int len;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}