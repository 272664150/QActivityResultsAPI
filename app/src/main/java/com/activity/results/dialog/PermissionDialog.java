package com.activity.results.dialog;

import android.content.Context;
import android.os.Bundle;

import com.activity.results.R;
import com.activity.results.util.ActivityUtils;
import com.activity.results.util.PackageUtils;

public class PermissionDialog extends CommonDialog {

    private Context mContext;
    private DialogClickListener mListener;

    public PermissionDialog(Context context) {
        this(context, R.style.dialog_not_transparent);
    }

    public PermissionDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    public void setDialogClickListener(DialogClickListener l) {
        mListener = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.str_permission_title);
        setKeyText(R.string.str_settings, R.string.str_cancel);
        super.setDialogClickListener(new DialogClickListener() {
            @Override
            public void onConfirmClick() {
                dismiss();
                goToSetting();
                if (mListener != null) {
                    mListener.onConfirmClick();
                }
            }

            @Override
            public void onCancelClick() {
                dismiss();
                if (mListener != null) {
                    mListener.onCancelClick();
                }
            }
        });
    }

    /**
     * 只需要传入权限类型
     */
    @Override
    public void setMsg(int msgId) {
        super.setMsg(mContext.getString(R.string.str_allow_settings, getAppName(), mContext.getString(msgId)));
    }

    /**
     * 只需要传入权限类型
     */
    @Override
    public void setMsg(String msg) {
        super.setMsg(mContext.getString(R.string.str_allow_settings, getAppName(), msg));
    }

    private String getAppName() {
        return PackageUtils.getAppName(mContext);
    }

    /**
     * 去设置页面
     */
    private void goToSetting() {
        ActivityUtils.goToSetting(mContext);
    }
}
