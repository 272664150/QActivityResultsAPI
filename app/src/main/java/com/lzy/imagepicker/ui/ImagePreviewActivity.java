package com.lzy.imagepicker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.activity.results.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.adapter.ImagePageAdapter;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.view.SuperCheckBox;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * ================================================
 */
public class ImagePreviewActivity extends ImagePreviewBaseActivity implements ImagePicker.OnImageSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String IS_ORIGIN = "isOrigin";

    private boolean isOrigin;                      //是否选中原图
    private SuperCheckBox mCbCheck;                //是否选中当前图片的CheckBox
    private SuperCheckBox mCbOrigin;               //原图
    private Button mBtnOk;                         //确认图片的选择
    private View mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
        isOrigin = getIntent().getBooleanExtra(IS_ORIGIN, false);
        mImagePicker.addOnImageSelectedListener(this);

        mBtnOk = mTopBar.findViewById(R.id.btn_ok);
        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setOnClickListener(this);

        mBottomBar = findViewById(R.id.bottom_bar);
        mBottomBar.setVisibility(View.VISIBLE);

        mCbCheck = findViewById(R.id.cb_check);
        mCbOrigin = findViewById(R.id.cb_origin);
        mCbOrigin.setText(getString(R.string.origin));
        mCbOrigin.setOnCheckedChangeListener(this);
        mCbOrigin.setChecked(isOrigin);

        //初始化当前页面的状态
        onImageSelected(0, null, false);
        ImageItem item = mImageItems.get(mCurrentPosition);
        boolean isSelected = mImagePicker.isSelect(item);
        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
        mCbCheck.setChecked(isSelected);
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                ImageItem item = mImageItems.get(mCurrentPosition);
                boolean isSelected = mImagePicker.isSelect(item);
                mCbCheck.setChecked(isSelected);
                mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
            }
        });
        //当点击当前选中按钮的时候，需要根据当前的选中状态添加和移除图片
        mCbCheck.setOnClickListener(v -> {
            ImageItem imageItem = mImageItems.get(mCurrentPosition);
            int selectLimit = mImagePicker.getSelectLimit();
            if (mCbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                Toast.makeText(ImagePreviewActivity.this, ImagePreviewActivity.this.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                mCbCheck.setChecked(false);
            } else {
                mImagePicker.addSelectedImageItem(mCurrentPosition, imageItem, mCbCheck.isChecked());
            }
        });
    }

    public void initView() {
        mContent = findViewById(R.id.content);

        mTopBar = findViewById(R.id.top_bar);
        mTopBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        mTopBar.findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        mTitleCount = findViewById(R.id.tv_des);

        mViewPager = findViewById(R.id.viewpager);
        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setPhotoViewClickListener((view, v, v1) -> onImageSingleTap());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems == null ? 0 : mImageItems.size()));
    }

    /**
     * 图片添加成功后，修改当前图片的选中数量
     * 当调用 addSelectedImageItem 或 deleteSelectedImageItem 都会触发当前回调
     */
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (mImagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.select_complete, mImagePicker.getSelectImageCount(), mImagePicker.getSelectLimit()));
            mBtnOk.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
        }

        if (mCbOrigin.isChecked()) {
            long size = 0;
            for (ImageItem imageItem : mSelectedImages) {
                size += imageItem.size;
            }

            String fileSize = Formatter.formatFileSize(this, size);
            mCbOrigin.setText(getString(R.string.origin_size, fileSize));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mImagePicker.getSelectedImages());
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
            finish();
        } else if (id == R.id.btn_back) {
            Intent intent = new Intent();
            intent.putExtra(ImagePreviewActivity.IS_ORIGIN, isOrigin);
            setResult(ImagePicker.RESULT_CODE_BACK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ImagePreviewActivity.IS_ORIGIN, isOrigin);
        setResult(ImagePicker.RESULT_CODE_BACK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_origin) {
            if (isChecked) {
                long size = 0;
                for (ImageItem item : mSelectedImages) {
                    size += item.size;
                }

                String fileSize = Formatter.formatFileSize(this, size);
                isOrigin = true;
                mCbOrigin.setText(getString(R.string.origin_size, fileSize));
            } else {
                isOrigin = false;
                mCbOrigin.setText(getString(R.string.origin));
            }
        }
    }

    @Override
    protected void onDestroy() {
        mImagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    /**
     * 单击时，隐藏头和尾
     */
    @Override
    public void onImageSingleTap() {
        if (mTopBar.getVisibility() == View.VISIBLE) {
            mTopBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            mTopBar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
            mTintManager.setStatusBarTintResource(R.color.transparent);
        } else {
            mTopBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            mTopBar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
            mTintManager.setStatusBarTintResource(R.color.status_bar);
        }
    }
}
