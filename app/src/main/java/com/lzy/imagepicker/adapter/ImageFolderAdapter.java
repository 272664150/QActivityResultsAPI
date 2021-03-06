package com.lzy.imagepicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.activity.results.R;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageFolder;
import com.lzy.imagepicker.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * ================================================
 */
public class ImageFolderAdapter extends BaseAdapter {

    private ImagePicker mImagePicker;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private int mImageSize;
    private List<ImageFolder> mImageFolders;
    private int mLastSelected;

    public ImageFolderAdapter(Activity activity, List<ImageFolder> folders) {
        mActivity = activity;
        if (folders != null && folders.size() > 0) {
            mImageFolders = folders;
        } else {
            mImageFolders = new ArrayList<>();
        }

        mImagePicker = ImagePicker.getInstance();
        mImageSize = CommonUtil.getImageItemWidth(mActivity);
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshData(List<ImageFolder> folders) {
        if (folders != null && folders.size() > 0) {
            mImageFolders = folders;
        } else {
            mImageFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImageFolders.size();
    }

    @Override
    public ImageFolder getItem(int position) {
        return mImageFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_folder_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageFolder folder = getItem(position);
        holder.folderName.setText(folder.name);
        holder.imageCount.setText(mActivity.getString(R.string.folder_image_count, folder.images.size()));
        mImagePicker.getImageLoader().displayImage(mActivity, folder.cover.path, holder.cover, mImageSize, mImageSize);

        if (mLastSelected == position) {
            holder.folderCheck.setVisibility(View.VISIBLE);
        } else {
            holder.folderCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setSelectIndex(int i) {
        if (mLastSelected == i) {
            return;
        }
        mLastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return mLastSelected;
    }

    private class ViewHolder {
        ImageView cover;
        TextView folderName;
        TextView imageCount;
        ImageView folderCheck;

        public ViewHolder(View view) {
            cover = view.findViewById(R.id.iv_cover);
            folderName = view.findViewById(R.id.tv_folder_name);
            imageCount = view.findViewById(R.id.tv_image_count);
            folderCheck = view.findViewById(R.id.iv_folder_check);
            view.setTag(this);
        }
    }
}
