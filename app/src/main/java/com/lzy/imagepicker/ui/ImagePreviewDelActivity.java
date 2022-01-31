package com.lzy.imagepicker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.activity.results.R;
import com.activity.results.dialog.CommonDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.adapter.ImagePageAdapter;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧），ikkong （ikkong@163.com）
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 修订历史：预览已经选择的图片，并可以删除, 感谢 ikkong 的提交
 * ================================================
 */
public class ImagePreviewDelActivity extends ImagePreviewBaseActivity implements View.OnClickListener {

    private CommonDialog commonDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        initView();

        ImageView mBtnDel = findViewById(R.id.btn_del);
        mBtnDel.setOnClickListener(this);
        mBtnDel.setVisibility(View.VISIBLE);

        mTopBar.findViewById(R.id.btn_back).setOnClickListener(this);

        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
            }
        });
    }

    public void initView() {
        mContent = findViewById(R.id.content);
        mTitleCount = findViewById(R.id.tv_des);

        mTopBar = findViewById(R.id.top_bar);
        mTopBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        mTopBar.findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        mViewPager = findViewById(R.id.viewpager);
        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setPhotoViewClickListener((view, v, v1) -> onImageSingleTap());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_del) {
            showDeleteDialog();
        } else if (id == R.id.btn_back) {
            onBackPressed();
        }
    }

    /**
     * 是否删除此张图片
     */
    private void showDeleteDialog() {
        if (commonDialog == null) {
            commonDialog = new CommonDialog(this, R.style.dialog_not_transparent);
            commonDialog.setDialogClickListener(new CommonDialog.DialogClickListener() {
                @Override
                public void onConfirmClick() {
                    commonDialog.dismiss();
                    //移除当前图片刷新界面
                    if (mCurrentPosition == mImageItems.size()) {
                        return;
                    }
                    mImageItems.remove(mCurrentPosition);
                    if (mImageItems.size() > 0) {
                        mAdapter.setData(mImageItems);
                        mAdapter.notifyDataSetChanged();
                        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
                    } else {
                        mAdapter.notifyDataSetChanged();
                        onBackPressed();
                    }
                }

                @Override
                public void onCancelClick() {
                    commonDialog.dismiss();
                }
            });
            commonDialog.setKeyText(R.string.str_ok, R.string.str_cancel);
            commonDialog.setMsg(R.string.delete_pic_confirm);
        }
        commonDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //带回最新数据
        intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, mImageItems);
        setResult(ImagePicker.RESULT_CODE_BACK, intent);
        finish();
        super.onBackPressed();
    }

    /**
     * 单击时，隐藏头和尾
     */
    @Override
    public void onImageSingleTap() {
        if (mTopBar.getVisibility() == View.VISIBLE) {
            mTopBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            mTopBar.setVisibility(View.GONE);
            mTintManager.setStatusBarTintResource(R.color.transparent);
        } else {
            mTopBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            mTopBar.setVisibility(View.VISIBLE);
            mTintManager.setStatusBarTintResource(R.color.status_bar);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
    }
}