package com.lzy.imagepicker.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.activity.results.R;
import com.activity.results.dialog.CommonDialog;
import com.activity.results.dialog.PermissionDialog;
import com.activity.results.util.FileAccessor;
import com.activity.results.util.PermissionUtils;
import com.jaeger.library.StatusBarUtil;
import com.lzy.imagepicker.view.SystemBarTintManager;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * ================================================
 */
public class ImageBaseActivity extends AppCompatActivity {

    protected static final int PERMISSION_CODE_STORAGE = 0x12;
    protected static final int REQUEST_CODE_PERMISSION_CAMERA = 0x15;

    protected CommonDialog mPermissionStorageHintDialog;
    protected CommonDialog mPermissionCameraHintDialog;
    protected PermissionDialog mStoragePermissionDialog;
    protected PermissionDialog mCameraPermissionDialog;

    protected SystemBarTintManager mTintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintResource(R.color.status_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.C1));
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void showToast(String toastText) {
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    /**
     * 申请存储权限
     */
    public void checkCameraPermission(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //写存储权限
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            if (!PermissionUtils.hasSelfPermissions(this, permissions)) {
                // 弹出申请权限提示框
                showCameraPermissionHint(id);
            } else {
                withCameraPermission();
            }
        } else {
            withCameraPermission();
        }
    }

    /**
     * 已经获得了存储权限
     */
    public void withCameraPermission() {
    }

    /**
     * 告知用户获取存储权限的目的
     */
    public void showCameraPermissionHint(int id) {
        mPermissionCameraHintDialog = new CommonDialog(this, R.style.dialog_not_transparent);
        mPermissionCameraHintDialog.setDialogClickListener(new CommonDialog.DialogClickListener() {
            @Override
            public void onConfirmClick() {
                mPermissionCameraHintDialog.dismiss();
                String[] permissions = new String[]{Manifest.permission.CAMERA};
                ActivityCompat.requestPermissions(ImageBaseActivity.this, permissions, REQUEST_CODE_PERMISSION_CAMERA);
            }

            @Override
            public void onCancelClick() {
            }
        });
        mPermissionCameraHintDialog.setTitle(R.string.str_permission_camera_title);
        mPermissionCameraHintDialog.setOkBtnText(R.string.str_nextstep);
        mPermissionCameraHintDialog.setMsg(id);
        mPermissionCameraHintDialog.hideCancelBtn();
        mPermissionCameraHintDialog.setIgnoreBackPressed(true);
        mPermissionCameraHintDialog.show();
    }

    /**
     * 申请存储权限
     */
    public void checkStoragePermission(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //写存储权限
            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!PermissionUtils.hasSelfPermissions(this, permissions)) {
                // 弹出申请权限提示框
                showStoragePermissionHint(id);
            } else {
                withStoragePermission();
            }
        } else {
            withStoragePermission();
        }
    }

    /**
     * 告知用户获取存储权限的目的
     */
    public void showStoragePermissionHint(int id) {
        mPermissionStorageHintDialog = new CommonDialog(this, R.style.dialog_not_transparent);
        mPermissionStorageHintDialog.setDialogClickListener(new CommonDialog.DialogClickListener() {
            @Override
            public void onConfirmClick() {
                mPermissionStorageHintDialog.dismiss();
                String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(ImageBaseActivity.this, permissions, PERMISSION_CODE_STORAGE);
            }

            @Override
            public void onCancelClick() {
            }
        });
        mPermissionStorageHintDialog.setTitle(R.string.str_permission_storage_title);
        mPermissionStorageHintDialog.setOkBtnText(R.string.str_nextstep);
        mPermissionStorageHintDialog.setMsg(id);
        mPermissionStorageHintDialog.hideCancelBtn();
        mPermissionStorageHintDialog.setIgnoreBackPressed(true);
        mPermissionStorageHintDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                // 用户点的拒绝，仍未拥有权限
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == PERMISSION_CODE_STORAGE) {
                        onStoragePermissionDenied();
                    }

                    if (requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
                        onCameraPermissionDenied();
                    }
                } else if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == PERMISSION_CODE_STORAGE) {
                        withStoragePermission();
                    }

                    if (requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
                        withCameraPermission();
                    }
                }
            }
        }
    }

    /**
     * 已经获得了存储权限
     */
    public void withStoragePermission() {
        FileAccessor.initFileAccess();
    }

    /**
     * 存储权限被拒绝时的处理
     */
    public void onStoragePermissionDenied() {
        if (mStoragePermissionDialog == null) {
            mStoragePermissionDialog = new PermissionDialog(this);
            mStoragePermissionDialog.setMsg(R.string.permission_storage);
        }

        if (mStoragePermissionDialog.isShowing()) {
            mStoragePermissionDialog.dismiss();
        }
        if (!isFinishing()) {
            mStoragePermissionDialog.show();
        }
    }

    /**
     * 相机权限被拒绝时的处理
     */
    public void onCameraPermissionDenied() {
        if (mCameraPermissionDialog == null) {
            mCameraPermissionDialog = new PermissionDialog(this);
            mCameraPermissionDialog.setMsg(R.string.permission_camera);
        }

        if (mCameraPermissionDialog.isShowing()) {
            mCameraPermissionDialog.dismiss();
        }
        if (!isFinishing()) {
            mCameraPermissionDialog.show();
        }
    }
}
