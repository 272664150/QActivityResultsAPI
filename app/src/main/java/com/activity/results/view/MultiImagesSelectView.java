package com.activity.results.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activity.results.R;
import com.activity.results.dialog.SelectDialog;
import com.activity.results.manager.ActivityStackManager;
import com.activity.results.util.ResUtil;
import com.activity.results.widget.GridSpacingItemDecoration;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.adapter.ImagePickerAdapter;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;

import java.util.ArrayList;
import java.util.List;

public class MultiImagesSelectView extends RelativeLayout implements IComponentView {

    private ImageView mRequiredIv;
    private TextView mLeftTitleTv;
    private ImageView mLeftIconIv;
    private TextView mRightTitleTv;
    private RecyclerView mImagesRv;

    private SelectDialog mSelectDialog;
    private ImagePickerAdapter mAdapter;

    private boolean isRequired;
    private String mLeftTitleStr;
    private Drawable mLeftIcon;
    private int mLeftIconVisibility;
    private String mRightTitleStr;
    private int mRightTitleVisibility;
    private boolean isDisplayMode;
    private int mSpanCount;
    private int mMaxSelectCount;
    private ArrayList<ImageItem> mImageBeanList;

    //You need to registerForActivityResult before onStart of the Activity.
    private ActivityResultLauncher mOpenCameraResultLauncher;
    private ActivityResultLauncher mOpenAlbumResultLauncher;
    private ActivityResultLauncher mPurePreviewResultLauncher;
    private ActivityResultLauncher mPreviewDeleteResultLauncher;

    public MultiImagesSelectView(Context context) {
        this(context, null);
    }

    public MultiImagesSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiImagesSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        parseXmlAttributes(context, attrs);
        bindData();
    }

    @Override
    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_multi_images_select, this);

        mRequiredIv = findViewById(R.id.iv_required);
        mLeftTitleTv = findViewById(R.id.tv_left_title);
        mLeftIconIv = findViewById(R.id.iv_left_Icon);
        mRightTitleTv = findViewById(R.id.tv_right_title);
        mImagesRv = findViewById(R.id.rv_images);

        AppCompatActivity currentActivity = (AppCompatActivity) ActivityStackManager.getInstance().getCurrentActivity();
        if (currentActivity == null) {
            return;
        }

        mOpenCameraResultLauncher = currentActivity.registerForActivityResult(
                new OpenCameraResultContract(),
                new ActivityResultCallback<ArrayList<ImageItem>>() {
                    @Override
                    public void onActivityResult(ArrayList<ImageItem> result) {
                        addImages(result);
                    }
                });
        mOpenAlbumResultLauncher = currentActivity.registerForActivityResult(
                new OpenAlbumResultContract(),
                new ActivityResultCallback<ArrayList<ImageItem>>() {
                    @Override
                    public void onActivityResult(ArrayList<ImageItem> result) {
                        addImages(result);
                    }
                });
        mPurePreviewResultLauncher = currentActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                    }
                });
        mPreviewDeleteResultLauncher = currentActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == ImagePicker.RESULT_CODE_BACK && result.getData() != null) {
                            mImageBeanList.clear();
                            ArrayList<ImageItem> resultList = (ArrayList<ImageItem>) result.getData().getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                            addImages(resultList);
                        }
                    }
                });
    }

    @Override
    public void parseXmlAttributes(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MultiImagesSelectView);
        if (typedArray == null) {
            return;
        }

        isRequired = typedArray.getBoolean(R.styleable.MultiImagesSelectView_required, false);
        mLeftTitleStr = typedArray.getString(R.styleable.MultiImagesSelectView_left_title_text);
        mLeftIcon = typedArray.getDrawable(R.styleable.MultiImagesSelectView_left_icon);
        mLeftIconVisibility = typedArray.getInteger(R.styleable.MultiImagesSelectView_left_icon_visibility, GONE);
        mRightTitleStr = typedArray.getString(R.styleable.MultiImagesSelectView_right_title_text);
        mRightTitleVisibility = typedArray.getInteger(R.styleable.MultiImagesSelectView_right_title_visibility, VISIBLE);
        isDisplayMode = typedArray.getBoolean(R.styleable.MultiImagesSelectView_display_mode, false);
        mSpanCount = typedArray.getInteger(R.styleable.MultiImagesSelectView_span_count, 3);
        mMaxSelectCount = typedArray.getInteger(R.styleable.MultiImagesSelectView_max_select_count, 6);
        typedArray.recycle();

        bindAttributes();
    }

    private void bindAttributes() {
        setRequired(isRequired);
        if (!TextUtils.isEmpty(mLeftTitleStr)) {
            setLeftTitle(mLeftTitleStr);
        }
        if (mLeftIcon != null) {
            setLeftIcon(mLeftIcon);
        }
        setLeftIconVisibility(mLeftIconVisibility);
        if (!TextUtils.isEmpty(mRightTitleStr)) {
            setRightTitle(mRightTitleStr);
        }
        setRightTitleVisibility(mRightTitleVisibility);
        setDisplayMode(isDisplayMode);
        setSpanCount(mSpanCount);
        setMaxSelectCount(mMaxSelectCount);
    }

    public void setRequired(boolean required) {
        if (required) {
            mRequiredIv.setVisibility(VISIBLE);
        } else {
            mRequiredIv.setVisibility(GONE);
        }
    }

    public void setLeftTitle(int titleId) {
        mLeftTitleTv.setText(titleId);
    }

    public void setLeftTitle(String title) {
        mLeftTitleTv.setText(title);
    }

    public void setLeftTitle(CharSequence title) {
        mLeftTitleTv.setText(title);
    }

    public CharSequence getLeftTitleText() {
        return mLeftTitleTv.getText();
    }

    public void setLeftIcon(Drawable drawable) {
        mLeftIconIv.setBackground(drawable);
    }

    public void setLeftIconVisibility(int visibility) {
        mLeftIconIv.setVisibility(visibility);
    }

    public void setRightTitle(int titleId) {
        mRightTitleTv.setText(titleId);
        setRightTitleVisibility(VISIBLE);
    }

    public void setRightTitle(String title) {
        mRightTitleTv.setText(title);
        setRightTitleVisibility(VISIBLE);
    }

    public void setRightTitle(CharSequence title) {
        mRightTitleTv.setText(title);
        setRightTitleVisibility(VISIBLE);
    }

    public void setRightTitleVisibility(int visibility) {
        mRightTitleTv.setVisibility(visibility);
    }

    public void setDisplayMode(boolean displayMode) {
        isDisplayMode = displayMode;
        if (displayMode) {
            setRightTitleVisibility(INVISIBLE);
        }
    }

    public void setSpanCount(int spanCount) {
        mSpanCount = spanCount;
    }

    public void setMaxSelectCount(int maxSelectCount) {
        mMaxSelectCount = maxSelectCount;
        // 最多一张图片时，不显示"x/y张"
        if (mMaxSelectCount <= 1) {
            setRightTitleVisibility(INVISIBLE);
        }
        mMaxSelectCount = maxSelectCount;
    }

    @Override
    public void bindData() {
        if (mImageBeanList == null) {
            mImageBeanList = new ArrayList<>();
        }
        if (!isDisplayMode) {
            ImageItem addItem = new ImageItem();
            addItem.path = ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_default);
            mImageBeanList.add(addItem);
        }
        setSelectedImagesInfo(mImageBeanList);

        // 根据UI稿的比例进行换算，先保证间距和xml的换算基准一致，图片宽高除法运算可丢失部分精度
        int margin = mImagesRv.getWidth() * 15 / 345;
        int imgRealWidth = (mImagesRv.getWidth() - margin * (mSpanCount - 1)) / mSpanCount;
        mAdapter = new ImagePickerAdapter(getContext(), mImageBeanList, imgRealWidth);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (isAddPicture(position)) {
                createPictureDialog();
            } else {
                watchBigPic(position);
            }
        });
        mAdapter.setCornerRadius(8);

        mImagesRv.setHasFixedSize(true);
        mImagesRv.setLayoutManager(new GridLayoutManager(getContext(), mSpanCount));
        mImagesRv.addItemDecoration(new GridSpacingItemDecoration(mSpanCount, margin, false));
        mImagesRv.setAdapter(mAdapter);
    }

    private boolean isAddPicture(int position) {
        return (isDisplayMode || hasFull()) ? false : position == mImageBeanList.size() - 1;
    }

    private boolean hasFull() {
        return (mImageBeanList != null &&
                mImageBeanList.size() == mMaxSelectCount &&
                !mImageBeanList.get(mImageBeanList.size() - 1).path.equals(ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_default)) &&
                !mImageBeanList.get(mImageBeanList.size() - 1).path.equals(ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_alarm)))
                ||
                (mMaxSelectCount == 1 &&
                        mImageBeanList != null &&
                        !mImageBeanList.isEmpty() &&
                        !mImageBeanList.get(0).path.equals(ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_default)) &&
                        !mImageBeanList.get(0).path.equals(ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_alarm)));
    }

    private void createPictureDialog() {
        if (getSelectLimit() <= 0) {
            return;
        }

        if (mSelectDialog == null) {
            List<String> names = new ArrayList<>();
            names.add(getResources().getString(R.string.str_take_picture));
            names.add(getResources().getString(R.string.str_obtain_from_album));

            SelectDialog.SelectDialogListener listener = (parent, view, position, id) -> {
                if (0 == position) {
                    // 直接调起相机
                    mOpenCameraResultLauncher.launch(getSelectLimit());
                } else if (1 == position) {
                    // 直接调起相册
                    mOpenAlbumResultLauncher.launch(getSelectLimit());
                }
            };
            mSelectDialog = new SelectDialog(getContext(), R.style.transparentFrameWindowStyle, listener, names);
            mSelectDialog.setPureColor(ContextCompat.getColor(getContext(), R.color.black));
        }

        if (!mSelectDialog.isShowing()) {
            mSelectDialog.show();
        }
    }

    private int getSelectLimit() {
        return mMaxSelectCount - getRealCount();
    }

    private int getRealCount() {
        return (isDisplayMode || hasFull()) ? mImageBeanList.size() : mImageBeanList.size() - 1;
    }

    private void watchBigPic(int pos) {
        if (isDisplayMode) {
            Intent intent = new Intent(getContext(), ImagePreviewActivity.class);
            ArrayList<String> pics = new ArrayList<>();
            pics.add(mImageBeanList.get(pos).path);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, pos);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, pics);
            intent.putExtra(ImagePreviewActivity.IS_ORIGIN, true);
            intent.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            mPurePreviewResultLauncher.launch(intent);
        } else {
            Intent intent = new Intent(getContext(), ImagePreviewDelActivity.class);
            intent.putParcelableArrayListExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (isDisplayMode || hasFull()) ? mImageBeanList : new ArrayList<>(mImageBeanList.subList(0, mImageBeanList.size() - 1)));
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, pos);
            intent.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            mPreviewDeleteResultLauncher.launch(intent);
        }
    }

    public void showNormalStatus() {
        if (isDisplayMode) {
            return;
        }
        ImageItem addItem = mImageBeanList.get(mAdapter.getItemCount() - 1);
        addItem.path = ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_default);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    public void showErrorStatus() {
        if (isDisplayMode) {
            return;
        }
        ImageItem addItem = mImageBeanList.get(mAdapter.getItemCount() - 1);
        addItem.path = ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_alarm);
        mAdapter.notifyItemChanged(mAdapter.getItemCount() - 1);
    }

    private void addImages(ArrayList<ImageItem> imageBeanList) {
        if (mImageBeanList == null) {
            mImageBeanList = new ArrayList<>();
        }
        if (!mImageBeanList.isEmpty() &&
                (mImageBeanList.get(mImageBeanList.size() - 1).path.equals(ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_default))
                        || (mImageBeanList.get(mImageBeanList.size() - 1).path.equals(ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_alarm))))) {
            mImageBeanList.remove(mImageBeanList.size() - 1);
        }

        if (imageBeanList != null && !imageBeanList.isEmpty()) {
            mImageBeanList.addAll(imageBeanList);
        }
        if (!isDisplayMode && !hasFull()) {
            ImageItem addItem = new ImageItem();
            addItem.path = ResUtil.drawableToUri(getContext(), R.drawable.icons_add_pictures_default);
            mImageBeanList.add(addItem);
        }
        setSelectedImagesInfo(mImageBeanList);
        mAdapter.notifyDataSetChanged();
    }

    private void setSelectedImagesInfo(ArrayList<ImageItem> selectedImageList) {
        selectedImageList = (isDisplayMode || hasFull()) ? selectedImageList : new ArrayList<>(selectedImageList.subList(0, selectedImageList.size() - 1));
        mRightTitleTv.setText((selectedImageList == null ? 0 : selectedImageList.size()) + "/" + mMaxSelectCount);
    }

    public void setDefaultData(ArrayList<ImageItem> imageBeanList) {
        if (imageBeanList == null || imageBeanList.isEmpty()) {
            return;
        }

        if (mImageBeanList != null) {
            mImageBeanList.clear();
        }
        addImages(imageBeanList);
    }

    private class OpenCameraResultContract extends ActivityResultContract<Integer, ArrayList<ImageItem>> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Integer selectLimit) {
            //打开选择,本次允许选择的数量
            ImagePicker.getInstance().setSelectLimit(selectLimit);
            Intent intent = new Intent(getContext(), ImageGridActivity.class);
            //是否是直接打开相机
            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true);
            return intent;
        }

        @Override
        public ArrayList<ImageItem> parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == ImagePicker.RESULT_CODE_ITEMS && intent != null) {
                return (ArrayList<ImageItem>) intent.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            }
            return null;
        }
    }

    private class OpenAlbumResultContract extends ActivityResultContract<Integer, ArrayList<ImageItem>> {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Integer selectLimit) {
            //打开选择,本次允许选择的数量  不用考虑 相减 小于 0 的情况，不会执行
            ImagePicker.getInstance().setSelectLimit(selectLimit);
            return new Intent(getContext(), ImageGridActivity.class);
        }

        @Override
        public ArrayList<ImageItem> parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == ImagePicker.RESULT_CODE_ITEMS && intent != null) {
                return (ArrayList<ImageItem>) intent.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            }
            return null;
        }
    }
}
