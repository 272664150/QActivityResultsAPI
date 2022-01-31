package com.lzy.imagepicker.ui;

import android.os.Bundle;

import com.activity.results.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.adapter.ImagePageAdapter;

/**
 * description: 纯图片预览
 */
public class ImagePurePreviewActivity extends ImagePreviewBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pure_preview);
        initView();
    }

    public void initView() {
        mViewPager = findViewById(R.id.viewpager);
        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setPhotoViewClickListener((view, v, v1) -> onImageSingleTap());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);
    }

    @Override
    public void onImageSingleTap() {
        setResult(ImagePicker.RESULT_CODE_BACK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(ImagePicker.RESULT_CODE_BACK);
        finish();
        super.onBackPressed();
    }
}
