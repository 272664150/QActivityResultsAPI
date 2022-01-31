package com.lzy.imagepicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：图片信息
 * ================================================
 */
public class ImageItem implements Serializable, Parcelable {

    public String name;       //图片的名字
    public String path;       //图片的路径
    public long size;         //图片的大小
    public int width;         //图片的宽度
    public int height;        //图片的高度
    public String mimeType;   //图片的类型
    public long addTime;      //图片的创建时间

    /**
     * 图片的路径和创建时间相同就认为是同一张图片
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageItem) {
            ImageItem item = (ImageItem) o;
            return this.path.equalsIgnoreCase(item.path) && this.addTime == item.addTime;
        }

        return super.equals(o);
    }

    public ImageItem() {
    }

    protected ImageItem(Parcel in) {
        name = in.readString();
        path = in.readString();
        size = in.readLong();
        width = in.readInt();
        height = in.readInt();
        mimeType = in.readString();
        addTime = in.readLong();
    }

    public void readFromParcel(Parcel in) {
        name = in.readString();
        path = in.readString();
        size = in.readLong();
        width = in.readInt();
        height = in.readInt();
        mimeType = in.readString();
        addTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeLong(size);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(mimeType);
        dest.writeLong(addTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };
}
