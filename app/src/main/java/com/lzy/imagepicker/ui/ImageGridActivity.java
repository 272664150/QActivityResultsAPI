package com.lzy.imagepicker.ui;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MyApplication;
import com.activity.results.R;
import com.activity.results.dialog.CommonDialog;
import com.activity.results.thread.QThreadPoolExecutor;
import com.lzy.imagepicker.AndroidRImageSource;
import com.lzy.imagepicker.DataHolder;
import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.adapter.ImageFolderAdapter;
import com.lzy.imagepicker.adapter.ImageRecyclerAdapter;
import com.lzy.imagepicker.adapter.ImageRecyclerAdapter.OnImageItemClickListener;
import com.lzy.imagepicker.bean.ImageFolder;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.view.FolderPopUpWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * ================================================
 */
public class ImageGridActivity extends ImageBaseActivity implements ImageDataSource.OnImagesLoadedListener, OnImageItemClickListener, ImagePicker.OnImageSelectedListener, View.OnClickListener, AndroidRImageSource.OnAndroidRImageListener {

    public static final String EXTRAS_TAKE_PICKERS = "TAKE";
    public static final String EXTRAS_IMAGES = "IMAGES";

    private ImagePicker mImagePicker;

    /**
     * 是否选中原图
     */
    private boolean isOrigin;
    /**
     * 图片展示控件
     */
    private GridView mGridView;
    /**
     * 底部栏
     */
    private View mFooterBar;
    /**
     * 确定按钮
     */
    private Button mBtnOk;
    /**
     * 文件夹切换按钮
     */
    private Button mBtnDir;
    /**
     * 预览按钮
     */
    private Button mBtnPre;
    /**
     * 图片文件夹的适配器
     */
    private ImageFolderAdapter mImageFolderAdapter;
    /**
     * ImageSet的PopupWindow
     */
    private FolderPopUpWindow mFolderPopupWindow;
    /**
     * 所有的图片文件夹
     */
    private List<ImageFolder> mImageFolders;
    /**
     * 默认不是直接调取相机
     */
    private boolean isDirectPhoto;
    private RecyclerView mRecyclerView;
    private ImageRecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isDirectPhoto = savedInstanceState.getBoolean(EXTRAS_TAKE_PICKERS, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRAS_TAKE_PICKERS, isDirectPhoto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        mImagePicker = ImagePicker.getInstance();
        mImagePicker.clear();
        mImagePicker.addOnImageSelectedListener(this);

        mRecyclerView = findViewById(R.id.recycler);

        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnDir = findViewById(R.id.btn_dir);
        mBtnDir.setOnClickListener(this);
        mBtnPre = findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mGridView = findViewById(R.id.gridview);
        mFooterBar = findViewById(R.id.footer_bar);
        if (mImagePicker.isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }

        mImageFolderAdapter = new ImageFolderAdapter(this, null);
        mRecyclerAdapter = new ImageRecyclerAdapter(this, null);

        onImageSelected(0, null, false);
        checkStoragePermission(R.string.str_permission_storage_hint_pic);

        Intent data = getIntent();
        // 新增可直接拍照
        if (data != null && data.getExtras() != null) {
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(EXTRAS_IMAGES);
            mImagePicker.setSelectedImages(images);
        }
    }

    /**
     * 已经获得了存储权限
     */
    @Override
    public void withStoragePermission() {
        super.withStoragePermission();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            new ImageDataSource(this, null, this);
        } else {
            new AndroidRImageSource(this, this, QThreadPoolExecutor.getsInstance()).loadImages();
        }

        Intent data = getIntent();
        // 新增可直接拍照
        if (data != null && data.getExtras() != null) {
            // 默认不是直接打开相机
            isDirectPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false);
            if (isDirectPhoto) {
                checkCameraPermission(R.string.str_permission_camera);
            }
        }
    }

    /**
     * 已经获得了存储权限
     */
    @Override
    public void withCameraPermission() {
        mImagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
    }

    @Override
    public void onStoragePermissionDenied() {
        super.onStoragePermissionDenied();
        mStoragePermissionDialog.setDialogClickListener(new CommonDialog.DialogClickListener() {
            @Override
            public void onConfirmClick() {
                finish();
            }

            @Override
            public void onCancelClick() {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mImagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mImagePicker.getSelectedImages());
            //多选不允许裁剪裁剪，返回数据
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
            finish();
        } else if (id == R.id.btn_dir) {
            if (mImageFolders == null) {
                return;
            }
            //点击文件夹按钮
            createPopupFolderList();
            //刷新数据
            mImageFolderAdapter.refreshData(mImageFolders);
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, mImagePicker.getSelectedImages());
            intent.putExtra(ImagePreviewActivity.IS_ORIGIN, isOrigin);
            intent.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
        } else if (id == R.id.btn_back) {
            finish();
        }
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener((adapterView, view, position, l) -> {
            mImageFolderAdapter.setSelectIndex(position);
            mImagePicker.setCurrentImageFolderPosition(position);
            mFolderPopupWindow.dismiss();
            ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
            if (null != imageFolder) {
                mRecyclerAdapter.refreshData(imageFolder.images);
                mBtnDir.setText(imageFolder.name);
            }
            //滑动到顶部
            mGridView.smoothScrollToPosition(0);
        });
        mFolderPopupWindow.setMargin(mFooterBar.getHeight());
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        if (isFinishing()) {
            return;
        }

        this.mImageFolders = imageFolders;
        mImagePicker.setImageFolders(imageFolders);
        if (imageFolders.size() == 0) {
            mRecyclerAdapter.refreshData(null);
        } else {
            mRecyclerAdapter.refreshData(imageFolders.get(0).images);
        }
        mRecyclerAdapter.setOnImageItemClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mImageFolderAdapter.refreshData(imageFolders);
    }

    @Override
    public void onImageItemClick(View view, ImageItem imageItem, int position) {
        //根据是否有相机按钮确定位置
        position = mImagePicker.isShowCamera() ? position - 1 : position;
        if (mImagePicker.isMultiMode()) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);

            // 但采用弱引用会导致预览弱引用直接返回空指针
            DataHolder.getInstance().save(DataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS, mImagePicker.getCurrentImageFolderItems());
            intent.putExtra(ImagePreviewActivity.IS_ORIGIN, isOrigin);
            //如果是多选，点击图片进入预览界面
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
        } else {
            mImagePicker.clearSelectedImages();
            mImagePicker.addSelectedImageItem(position, mImagePicker.getCurrentImageFolderItems().get(position), true);
            if (mImagePicker.isCrop()) {
                Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                //单选需要裁剪，进入裁剪界面
                startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);
            } else {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mImagePicker.getSelectedImages());
                //单选不需要裁剪，返回数据
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                finish();
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (mImagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.select_complete, mImagePicker.getSelectImageCount(), mImagePicker.getSelectLimit()));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
        }
        mBtnPre.setText(getResources().getString(R.string.preview_count, mImagePicker.getSelectImageCount()));
        for (int i = mImagePicker.isShowCamera() ? 1 : 0; i < mRecyclerAdapter.getItemCount(); i++) {
            if (mRecyclerAdapter.getItem(i).path != null && mRecyclerAdapter.getItem(i).path.equals(item.path)) {
                mRecyclerAdapter.notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // android R拍照方式不同，返回的data不为空
        if (data != null && data.getExtras() != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (resultCode == ImagePicker.RESULT_CODE_BACK) {
                isOrigin = data.getBooleanExtra(ImagePreviewActivity.IS_ORIGIN, false);
            } else {
                //从拍照界面返回, 点击X, 没有选择照片
                if (data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) == null) {
                    //什么都不做 直接调起相机
                } else {
                    //说明是从裁剪页面过来的数据，直接返回就可以
                    setResult(ImagePicker.RESULT_CODE_ITEMS, data);
                }
                finish();
            }
        } else {
            //如果是裁剪，因为裁剪指定了存储的Uri，所以返回的data一定为null
            if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_TAKE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    final Uri uri = mImagePicker.getTakeImageUri();
                    if (uri == null) {
                        return;
                    }
                    QThreadPoolExecutor.getsInstance().execute(() -> {
                        Cursor cursor = null;
                        File targetFile = null;
                        try {
                            ContentResolver resolver = MyApplication.mApplication.getContentResolver();
                            cursor = resolver.query(uri, null, null, null);
                            if (cursor != null && cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                                targetFile = new File(path);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                            mImagePicker.setTakeImageFile(targetFile);
                            if (targetFile != null) {
                                runOnUiThread(() -> notifyGalleryAndBack());
                            }
                        }
                    });
                } else {
                    notifyGalleryAndBack();
                }
            } else if (isDirectPhoto) {
                finish();
            }
        }
    }

    private void notifyGalleryAndBack() {
        //发送广播通知图片增加了
        ImagePicker.galleryAddPic(this, mImagePicker.getTakeImageFile());

        /**
         * 对机型做旋转处理
         */
        String path = mImagePicker.getTakeImageFile().getAbsolutePath();

        ImageItem imageItem = new ImageItem();
        imageItem.path = path;
        mImagePicker.clearSelectedImages();
        mImagePicker.addSelectedImageItem(0, imageItem, true);
        if (mImagePicker.isCrop()) {
            Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
            //单选需要裁剪，进入裁剪界面
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);
        } else {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mImagePicker.getSelectedImages());
            //单选不需要裁剪，返回数据
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
            finish();
        }
    }
}