package com.lzy.imagepicker.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.github.chrisbanes.photoview.PhotoView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.util.CommonUtil;

import java.util.ArrayList;


/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * ================================================
 */
public class ImagePageAdapter extends PagerAdapter {

    private int mScreenWidth;
    private int mScreenHeight;
    private ImagePicker mImagePicker;
    private ArrayList<ImageItem> mImageList;
    private Activity mActivity;
    private PhotoViewClickListener mListener;

    public ImagePageAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        this.mImageList = images;

        DisplayMetrics dm = CommonUtil.getScreenPix(activity);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mImagePicker = ImagePicker.getInstance();
    }

    public void setData(ArrayList<ImageItem> images) {
        this.mImageList = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mActivity);
        ImageItem imageItem = mImageList.get(position);
        mImagePicker.getImageLoader().displayImage(mActivity, imageItem.path, photoView, mScreenWidth, mScreenHeight);
        photoView.setOnPhotoTapListener((view, x, y) -> {
            if (mListener != null) {
                mListener.OnPhotoTapListener(view, x, y);
            }
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public int getCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        // 强迫viewpager重绘所有item
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View view, float v, float v1);
    }
}
