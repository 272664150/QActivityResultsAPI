package com.lzy.imagepicker.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.activity.results.R;
import com.activity.results.util.ScreenUtils;
import com.lzy.imagepicker.bean.ImageItem;

import java.util.List;

public class ImagePickerAdapter extends BaseQuickAdapter<ImageItem, ImagePickerAdapter.SelectedPicViewHolder> {

    private int mImgWidth;
    private float mCornerRadius;

    private Context mContext;

    public ImagePickerAdapter(Context mContext, List<ImageItem> data, int imgWidth) {
        super(R.layout.item_image_picker, data);
        this.mContext = mContext;
        this.mImgWidth = imgWidth;
    }

    public ImagePickerAdapter(Context mContext, List<ImageItem> data) {
        super(R.layout.item_image_picker, data);
        this.mContext = mContext;
    }

    @Override
    protected void convert(ImagePickerAdapter.SelectedPicViewHolder helper, ImageItem item) {
        // 无法使用此方法，处理局部刷新逻辑
    }

    @Override
    public void onBindViewHolder(ImagePickerAdapter.SelectedPicViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        ImageItem item = mData.get(position);
        if (mCornerRadius > 0) {
            Glide.with(mContext).load(item.path)
                    .transform(new CenterCrop(), new RoundedCorners((int) ScreenUtils.dip2px(mCornerRadius)))
                    .error(R.drawable.photo_blank)
                    .placeholder(R.drawable.photo_blank)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mItemIv);
        }
    }

    public void setCornerRadius(float cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    public class SelectedPicViewHolder extends BaseViewHolder {
        private ImageView mItemIv;

        public SelectedPicViewHolder(View itemView) {
            super(itemView);
            mItemIv = itemView.findViewById(R.id.iv_img);
            if (mImgWidth > 0) {
                ViewGroup.LayoutParams lp = mItemIv.getLayoutParams();
                lp.width = mImgWidth;
                lp.height = mImgWidth;
                mItemIv.setLayoutParams(lp);
            }
        }
    }
}