package com.lzy.imagepicker;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import com.activity.results.R;
import com.lzy.imagepicker.bean.ImageFolder;
import com.lzy.imagepicker.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class AndroidRImageSource {
    private final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
            MediaStore.Images.Media.MIME_TYPE,      //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED};    //图片被添加的时间，long型  1450518608

    private Context mContext;
    private OnAndroidRImageListener mListener;
    private Executor mExecutor;
    private Handler mHandler;

    private AndroidRImageSource() {
    }

    public AndroidRImageSource(Context context, OnAndroidRImageListener listener, Executor executor) {
        mContext = context.getApplicationContext();
        mListener = listener;
        mExecutor = executor;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void loadImages() {
        if (mContext == null) {
            throw new RuntimeException("Context must set");
        }
        if (mExecutor == null) {
            throw new RuntimeException("Executors must set,run the image load need is non-main thread");
        }
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                loadAllImages();
            }
        });
    }

    /**
     * 扫描全部文件夹
     */
    private synchronized void loadAllImages() {
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        if (cursor != null) {
            try {
                //所有的图片文件夹,指定默认大小16，避免添加第一个数据时就扩容
                ArrayList<ImageFolder> imageFolders = new ArrayList<>(16);
                ArrayList<ImageItem> allImages = new ArrayList<>(16);
                while (cursor.moveToNext()) {
                    String imageName = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    File file = new File(imagePath);
                    if (!file.exists() || file.length() <= 0) {
                        continue;
                    }

                    long imageSize = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                    int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                    String imageMimeType = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                    long imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));

                    ImageItem imageItem = new ImageItem();
                    imageItem.name = imageName;
                    imageItem.path = imagePath;
                    imageItem.size = imageSize;
                    imageItem.width = imageWidth;
                    imageItem.height = imageHeight;
                    imageItem.mimeType = imageMimeType;
                    imageItem.addTime = imageAddTime;
                    allImages.add(imageItem);
                    //根据父路径分类存放图片
                    File imageFile = new File(imagePath);
                    File imageParentFile = imageFile.getParentFile();
                    ImageFolder imageFolder = new ImageFolder();
                    if (imageParentFile != null) {
                        imageFolder.name = imageParentFile.getName();
                        imageFolder.path = imageParentFile.getAbsolutePath();
                    }

                    if (!imageFolders.contains(imageFolder)) {
                        ArrayList<ImageItem> images = new ArrayList<>();
                        images.add(imageItem);
                        imageFolder.cover = imageItem;
                        imageFolder.images = images;
                        imageFolders.add(imageFolder);
                    } else {
                        imageFolders.get(imageFolders.indexOf(imageFolder)).images.add(imageItem);
                    }
                }

                //防止没有图片报异常
                if (cursor.getCount() > 0 && allImages.size() > 0) {
                    //构造所有图片的集合
                    ImageFolder allImagesFolder = new ImageFolder();
                    allImagesFolder.name = mContext.getResources().getString(R.string.all_images);
                    allImagesFolder.path = "/";
                    allImagesFolder.cover = allImages.get(0);
                    allImagesFolder.images = allImages;
                    //确保第一条是所有图片
                    imageFolders.add(0, allImagesFolder);
                    //回调接口，通知图片数据准备完成
                    ImagePicker.getInstance().setImageFolders(imageFolders);
                    if (mHandler != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mListener != null) {
                                    //子线程调用的
                                    mListener.onImagesLoaded(imageFolders);
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
    }

    public interface OnAndroidRImageListener {
        void onImagesLoaded(List<ImageFolder> imageFolders);
    }
}