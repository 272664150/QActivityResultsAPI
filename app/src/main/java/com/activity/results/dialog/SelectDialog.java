package com.activity.results.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.activity.results.R;
import com.activity.results.util.ScreenUtils;

import java.util.List;

public class SelectDialog extends Dialog implements OnClickListener, OnItemClickListener {

    private TextView mTitleTv;
    private Button mCancelBtn;

    private Context mContext;
    private boolean mUseCustomColor;
    private int mFirstItemColor;
    private int mOtherItemColor;
    private int mCancelBtnColor;
    private int delBtnColor;
    private String mTitle;
    private List<String> mName;
    private SelectDialogListener mListener;
    private SelectDialogCancelListener mCancelListener;

    public interface SelectDialogCancelListener {
        void onCancelClick(View v);
    }

    public interface SelectDialogListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    public void setDialogListener(SelectDialogListener mListener) {
        this.mListener = mListener;
    }

    public SelectDialog(Context context, int theme, SelectDialogListener listener, List<String> names) {
        super(context, theme);
        mContext = context;
        mListener = listener;
        mName = names;

        setCanceledOnTouchOutside(true);
    }

    public SelectDialog(Context context, int theme, SelectDialogListener listener, SelectDialogCancelListener cancelListener, List<String> names) {
        super(context, theme);
        mContext = context;
        mListener = listener;
        mCancelListener = cancelListener;
        mName = names;

        setCanceledOnTouchOutside(false);
    }

    public SelectDialog(Context context, int theme, SelectDialogListener listener, List<String> names, String title) {
        super(context, theme);
        mContext = context;
        mListener = listener;
        mName = names;
        mTitle = title;

        setCanceledOnTouchOutside(true);
    }

    public SelectDialog(Context context, int theme, SelectDialogListener listener, SelectDialogCancelListener cancelListener, List<String> names, String title) {
        super(context, theme);
        mContext = context;
        mListener = listener;
        mCancelListener = cancelListener;
        mName = names;
        mTitle = title;

        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.view_dialog_select, null);
        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Window window = getWindow();
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = ScreenUtils.getScreenHeight(mContext);
        wl.width = LayoutParams.MATCH_PARENT;
        wl.height = LayoutParams.WRAP_CONTENT;
        onWindowAttributesChanged(wl);

        initViews();
    }

    private void initViews() {
        DialogAdapter dialogAdapter = new DialogAdapter(mName);
        ListView dialogList = findViewById(R.id.dialog_list);
        dialogList.setOnItemClickListener(this);
        dialogList.setAdapter(dialogAdapter);
        mCancelBtn = findViewById(R.id.mBtn_Cancel);
        if (mUseCustomColor) {
            mCancelBtn.setTextColor(mCancelBtnColor);
        }
        mCancelBtn.setOnClickListener(v -> {
            if (mCancelListener != null) {
                mCancelListener.onCancelClick(v);
            }
            dismiss();
        });

        mTitleTv = findViewById(R.id.mTv_Title);
        if (!TextUtils.isEmpty(mTitle) && mTitleTv != null) {
            mTitleTv.setVisibility(View.VISIBLE);
            mTitleTv.setText(mTitle);
        } else {
            mTitleTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onItemClick(parent, view, position, id);
        dismiss();
    }

    private class DialogAdapter extends BaseAdapter {
        private List<String> mStrings;
        private ViewHolder mViewHolder;

        public DialogAdapter(List<String> strings) {
            this.mStrings = strings;
        }

        @Override
        public int getCount() {
            return mStrings.size();
        }

        @Override
        public Object getItem(int position) {
            return mStrings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                mViewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.view_dialog_item, null);
                mViewHolder.dialogItemButton = convertView.findViewById(R.id.dialog_item_bt);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            mViewHolder.dialogItemButton.setText(mStrings.get(position));
            if (!mUseCustomColor) {
                mFirstItemColor = mContext.getResources().getColor(R.color.color_txt_common);
                mOtherItemColor = mContext.getResources().getColor(R.color.color_txt_common);
            }
            if (1 == mStrings.size()) {
                mViewHolder.dialogItemButton.setTextColor(mFirstItemColor);
                mViewHolder.dialogItemButton.setBackgroundResource(R.drawable.dialog_item_bg_one);
            } else if (position == 0) {
                mViewHolder.dialogItemButton.setTextColor(mFirstItemColor);
                mViewHolder.dialogItemButton.setBackgroundResource(R.drawable.dialog_item_bg_top);
            } else if (position == mStrings.size() - 1) {
                mViewHolder.dialogItemButton.setTextColor(mOtherItemColor);
                mViewHolder.dialogItemButton.setBackgroundResource(R.drawable.dialog_item_bg_buttom);
            } else {
                mViewHolder.dialogItemButton.setTextColor(mOtherItemColor);
                mViewHolder.dialogItemButton.setBackgroundResource(R.drawable.dialog_item_bg_center);
            }
            if (mContext.getString(R.string.str_delete).equals(mViewHolder.dialogItemButton.getText().toString())) {
                if (delBtnColor != 0) {
                    mViewHolder.dialogItemButton.setTextColor(delBtnColor);
                } else {
                    mViewHolder.dialogItemButton.setTextColor(mContext.getResources().getColor(R.color.color_txt_common));
                }
            }
            return convertView;
        }

        private class ViewHolder {
            public TextView dialogItemButton;
        }
    }

    /**
     * 设置列表项的文本颜色
     */
    public void setItemColor(int firstItemColor, int otherItemColor) {
        mFirstItemColor = firstItemColor;
        mOtherItemColor = otherItemColor;
        mUseCustomColor = true;
    }

    /**
     * 设置所有选项的颜色一样
     */
    public void setPureColor(int color) {
        mFirstItemColor = color;
        mOtherItemColor = color;
        mCancelBtnColor = color;
        mUseCustomColor = true;
    }

    /**
     * 设置所有选项的颜色一样
     */
    public void setPureCancelColor(int pureColor, int cancelColor) {
        mFirstItemColor = pureColor;
        mOtherItemColor = pureColor;
        mCancelBtnColor = cancelColor;
        mUseCustomColor = true;
    }

    public void setDelBtnColor(int delBtnColor) {
        this.delBtnColor = delBtnColor;
    }

    /**
     * 用来携带数据
     * 匿名内部类中不要使用局部变量
     */
    private Object data;

    public void setData(Object object) {
        this.data = object;
    }

    public Object getData() {
        return this.data;
    }
}
