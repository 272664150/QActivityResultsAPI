package com.lzy.imagepicker.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lzy.imagepicker.DataHolder;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.adapter.ImagePageAdapter;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.view.ViewPagerFixed;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 修订历史：图片预览的基类
 * ================================================
 */
public abstract class ImagePreviewBaseActivity extends ImageBaseActivity {

    protected ImagePicker mImagePicker;
    protected ArrayList<ImageItem> mImageItems;      //跳转进ImagePreviewFragment的图片文件夹
    protected int mCurrentPosition;                  //跳转进ImagePreviewFragment时的序号，第几个图片
    protected TextView mTitleCount;                  //显示当前图片的位置  例如  5/31
    protected ArrayList<ImageItem> mSelectedImages;  //所有已经选中的图片
    protected View mContent;
    protected View mTopBar;
    protected ViewPagerFixed mViewPager;
    protected ImagePageAdapter mAdapter;
    protected boolean isFromItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentPosition = getIntent().getIntExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        isFromItems = getIntent().getBooleanExtra(ImagePicker.EXTRA_FROM_ITEMS, false);
        if (isFromItems) {
            // 据说这样会导致大量图片崩溃
            mImageItems = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
        } else {
            // 下面采用弱引用会导致预览崩溃
            mImageItems = (ArrayList<ImageItem>) DataHolder.getInstance().retrieve(DataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS);
        }
        mImagePicker = ImagePicker.getInstance();
        mSelectedImages = mImagePicker.getSelectedImages();
    }

    /**
     * 单击时，隐藏头和尾
     */
    public abstract void onImageSingleTap();
}