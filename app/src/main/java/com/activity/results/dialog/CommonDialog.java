package com.activity.results.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;

import com.activity.results.R;
import com.activity.results.util.ScreenUtils;

public class CommonDialog extends Dialog implements View.OnClickListener {

    private ImageView mTitleImgIv;
    private TextView mDialogTitleTv;
    private TextView mDialogBodyTv;
    private LinearLayout mBtnLayout;
    private TextView mOkTv;
    private TextView mCancelTv;

    private Context mContext;
    private boolean isIgnoreBackPressed;
    private DialogClickListener mDialogClickListener;
    private DialogDismissListener mDialogDismissListener;

    public CommonDialog(Context context) {
        this(context, R.style.dialog_not_transparent);
    }

    public CommonDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        setContentView(R.layout.dialog_common);
        initWindow();
        initView();
    }

    private void initView() {
        mTitleImgIv = findViewById(R.id.img_title);
        mDialogTitleTv = findViewById(R.id.tv_dialog_title);
        mDialogBodyTv = findViewById(R.id.tv_dialog_body);
        mBtnLayout = findViewById(R.id.lay_btn);
        mCancelTv = findViewById(R.id.tv_cancel);
        mOkTv = findViewById(R.id.tv_ok);
        mCancelTv.setOnClickListener(this);
        mOkTv.setOnClickListener(this);
    }

    private void initWindow() {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.dimAmount = 0.40f;
        dialogWindow.setAttributes(params);
    }

    public void setTitle(String title) {
        mDialogTitleTv.setVisibility(View.VISIBLE);
        mDialogTitleTv.setText(title);
    }

    public void setTitleTextSize(int size) {
        mDialogTitleTv.setTextSize(size);
    }

    @Override
    public void setTitle(int titleId) {
        mDialogTitleTv.setVisibility(View.VISIBLE);
        mDialogTitleTv.setText(titleId);
        mDialogBodyTv.setTextColor(mContext.getResources().getColor(R.color.C2));
        mDialogBodyTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12);
    }

    public void setTitleTypeface(Typeface textStyle) {
        mDialogTitleTv.setTypeface(textStyle);
    }

    public void setMsgVisible(int visible) {
        mDialogBodyTv.setVisibility(visible);
    }

    public void setMsg(String msg) {
        mDialogBodyTv.setText(msg);
    }

    public void setMsg(CharSequence msg) {
        mDialogBodyTv.setText(msg);
    }

    public void setKeyText(String ok_msg, String cancel_msg) {
        mOkTv.setText(ok_msg);
        mCancelTv.setText(cancel_msg);
    }

    public void setKeyText(int ok_msg, int cancel_msg) {
        mOkTv.setText(ok_msg);
        mCancelTv.setText(cancel_msg);
    }

    public void setOkBtnText(int ok_msg) {
        mOkTv.setText(ok_msg);
        mOkTv.setClickable(true);
        mOkTv.setTextColor(getContext().getResources().getColor(R.color.default_black));
    }

    public void changeOKBtnColor() {
        mOkTv.setTextColor(getContext().getResources().getColor(R.color.C10));
    }

    public void setOkBtnColor(int colorid) {
        mOkTv.setTextColor(getContext().getResources().getColor(colorid));
    }

    /**
     * @param style Typeface.BOLD Typeface.NORMAL
     */
    public void setRightBtnTypeface(int style) {
        mOkTv.setTypeface(Typeface.defaultFromStyle(style));
    }

    /**
     * @param style Typeface.BOLD Typeface.NORMAL
     */
    public void setLeftBtnTypeface(int style) {
        mCancelTv.setTypeface(Typeface.defaultFromStyle(style));
    }

    public void setImageTitleResource(int drawId) {
        mTitleImgIv.setImageResource(drawId);
        mTitleImgIv.setVisibility(View.VISIBLE);
    }

    public void disableOkBtn() {
        mOkTv.setClickable(false);
        mOkTv.setTextColor(getContext().getResources().getColor(R.color.bottom_text_disable));
    }

    public void setMsgBodyGravity(int gravity) {
        mDialogBodyTv.setGravity(gravity);
    }

    public void setMsgTextSize(int size) {
        mDialogBodyTv.setTextSize(ScreenUtils.dip2px(size));
    }

    public void setOkBtnTextSize(int size) {
        mOkTv.setTextSize(ScreenUtils.dip2px(size));
    }

    public void setMsg(int msg_id) {
        mDialogBodyTv.setText(msg_id);
    }

    public void setMsgTextColor(@ColorRes int color) {
        mDialogBodyTv.setTextColor(mContext.getResources().getColor(color));
    }

    public void setCancelBtnColor(int colorId) {
        mCancelTv.setTextColor(getContext().getResources().getColor(colorId));
    }

    public void setCancelBtnTextSize(int size) {
        mCancelTv.setTextSize(ScreenUtils.dip2px(size));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void onClick(View v) {
        if (v == mCancelTv) {
            dismiss();
            if (mDialogClickListener != null) {
                mDialogClickListener.onCancelClick();
            }
        } else if (v == mOkTv) {
            if (mDialogClickListener != null) {
                mDialogClickListener.onConfirmClick();
            }
        }
    }

    /**
     * 只有一个确定按钮
     */
    public void hideCancelBtn() {
        mCancelTv.setVisibility(View.GONE);
        mOkTv.setBackgroundResource(R.drawable.corner_btn);
    }

    public void hideAllBtn() {
        mBtnLayout.setVisibility(View.GONE);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void onBackPressed() {
        if (isIgnoreBackPressed) {
            return;
        }
        if (mCancelTv.getVisibility() == View.GONE) {
            if (mDialogDismissListener != null) {
                mDialogDismissListener.dismiss();
            }
            dismiss();
        } else {
            return;
        }
    }

    /**
     * 忽略返回键
     */
    public void setIgnoreBackPressed(boolean ignoreBackPressed) {
        isIgnoreBackPressed = ignoreBackPressed;
    }

    /**
     * 外界需要的数据传递
     */
    private Object data;

    public void setData(Object object) {
        this.data = object;
    }

    public Object getData() {
        return this.data;
    }

    public void setDialogClickListener(DialogClickListener listener) {
        mDialogClickListener = listener;
    }

    public void setDialogDismissListener(DialogDismissListener dialogDismissListener) {
        mDialogDismissListener = dialogDismissListener;
    }

    public interface DialogClickListener {
        void onConfirmClick();

        void onCancelClick();
    }

    public interface DialogDismissListener {
        void dismiss();
    }
}
