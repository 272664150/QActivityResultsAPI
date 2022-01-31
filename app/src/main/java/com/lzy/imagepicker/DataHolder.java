package com.lzy.imagepicker;

import com.lzy.imagepicker.bean.ImageItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 新的DataHolder，使用单例和弱引用解决崩溃问题
 */
public class DataHolder {
    public static final String DH_CURRENT_IMAGE_FOLDER_ITEMS = "dh_current_image_folder_items";

    private static DataHolder mInstance;
    private Map<String, List<ImageItem>> mDataMap;

    public static DataHolder getInstance() {
        if (mInstance == null) {
            synchronized (DataHolder.class) {
                if (mInstance == null) {
                    mInstance = new DataHolder();
                }
            }
        }
        return mInstance;
    }

    private DataHolder() {
        mDataMap = new HashMap<>();
    }

    public void save(String id, List<ImageItem> object) {
        if (mDataMap != null) {
            mDataMap.put(id, object);
        }
    }

    public Object retrieve(String id) {
        if (mDataMap == null || mInstance == null) {
            throw new RuntimeException("init before");
        }
        return mDataMap.get(id);
    }
}
