package com.lzy.imagepicker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.activity.results.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.util.CommonUtil;
import com.lzy.imagepicker.view.SuperCheckBox;

import java.util.ArrayList;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机

    private ImagePicker mImagePicker;
    private Activity mActivity;
    private ArrayList<ImageItem> mImageList;          //当前需要显示的所有的图片数据
    private ArrayList<ImageItem> mSelectedImageList;  //全局保存的已经选中的图片数据
    private boolean isShowCamera;                     //是否显示拍照按钮
    private int mImageSize;                           //每个条目的大小
    private LayoutInflater mInflater;
    private OnImageItemClickListener mListener;       //图片被点击的监听

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, ImageItem imageItem, int position);
    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images == null || images.size() == 0) {
            this.mImageList = new ArrayList<>();
        } else {
            this.mImageList = images;
        }
        notifyDataSetChanged();
    }

    public ImageRecyclerAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        if (images == null || images.size() == 0) {
            this.mImageList = new ArrayList<>();
        } else {
            this.mImageList = images;
        }

        mImageSize = CommonUtil.getImageItemWidth(mActivity);
        mImagePicker = ImagePicker.getInstance();
        isShowCamera = mImagePicker.isShowCamera();
        mSelectedImageList = mImagePicker.getSelectedImages();
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CAMERA) {
            return new CameraViewHolder(mInflater.inflate(R.layout.adapter_camera_item, parent, false));
        }
        return new ImageViewHolder(mInflater.inflate(R.layout.adapter_image_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) {
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return isShowCamera ? mImageList.size() + 1 : mImageList.size();
    }

    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) {
                return null;
            }
            return mImageList.get(position - 1);
        } else {
            return mImageList.get(position);
        }
    }

    private class ImageViewHolder extends ViewHolder {

        View rootView;
        ImageView ivThumb;
        View mask;
        SuperCheckBox cbCheck;

        ImageViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            ivThumb = itemView.findViewById(R.id.iv_thumb);
            mask = itemView.findViewById(R.id.mask);
            cbCheck = itemView.findViewById(R.id.cb_check);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
        }

        void bind(final int position) {
            final ImageItem imageItem = getItem(position);
            ivThumb.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onImageItemClick(rootView, imageItem, position);
                }
            });
            cbCheck.setOnClickListener(v -> {
                int selectLimit = mImagePicker.getSelectLimit();
                if (cbCheck.isChecked() && mSelectedImageList.size() >= selectLimit) {
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                    cbCheck.setChecked(false);
                    mask.setVisibility(View.GONE);
                } else {
                    mImagePicker.addSelectedImageItem(position, imageItem, cbCheck.isChecked());
                    mask.setVisibility(View.VISIBLE);
                }
            });
            //根据是否多选，显示或隐藏checkbox
            if (mImagePicker.isMultiMode()) {
                cbCheck.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImageList.contains(imageItem);
                if (checked) {
                    mask.setVisibility(View.VISIBLE);
                    cbCheck.setChecked(true);
                } else {
                    mask.setVisibility(View.GONE);
                    cbCheck.setChecked(false);
                }
            } else {
                cbCheck.setVisibility(View.GONE);
            }
            mImagePicker.getImageLoader().displayImage(mActivity, imageItem.path, ivThumb, mImageSize, mImageSize);
        }
    }

    private class CameraViewHolder extends ViewHolder {

        View mItemView;

        CameraViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        void bindCamera() {
            //让图片是个正方形
            mItemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize));
            mItemView.setTag(null);
            mItemView.setOnClickListener(v -> mImagePicker.takePicture(mActivity, ImagePicker.REQUEST_CODE_TAKE));
        }
    }
}
