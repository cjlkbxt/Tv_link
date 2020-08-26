package com.kobe.lib_base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    //申请运行时权限的Code
    private static final int PERMISSION_REQUEST_CODE = 1000;

    //申明所需权限
    private String[] mPermissions = {
            Manifest.permission.CAMERA,
    };

    //保存没有同意的权限
    private List<String> mPermissionList = new ArrayList<>();
    //保存没有同意的失败权限
    private List<String> mPermissionFailedList = new ArrayList<>();

    private OnPermissionsResultListener mOnPermissionsResultListener;


    /**
     * 一个方法请求权限
     */
    protected void requestAll(OnPermissionsResultListener onPermissionsResultListener) {
        if (!checkAllPermissions()) {
            requestAllPermissions(onPermissionsResultListener);
        }
    }

    /**
     * 判断单个权限
     */
    protected boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = checkSelfPermission(permission);
            return check == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    /**
     * 判断是否需要申请权限
     */
    protected boolean checkAllPermissions() {
        mPermissionList.clear();
        for (int i = 0; i < mPermissions.length; i++) {
            boolean check = checkPermission(mPermissions[i]);
            //如果不同意则请求
            if (!check) {
                mPermissionList.add(mPermissions[i]);
            }
        }
        return mPermissionList.size() > 0 ? false : true;
    }

    /**
     * 请求权限
     */
    protected void requestPermission(String[] permissions, OnPermissionsResultListener onPermissionsResultListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.mOnPermissionsResultListener = onPermissionsResultListener;
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 请求权限
     */
    protected void requestPermission(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 申请所有权限
     */
    protected void requestAllPermissions(OnPermissionsResultListener onPermissionsResultListener) {
        this.mOnPermissionsResultListener = onPermissionsResultListener;
        requestPermission(mPermissionList.toArray(new String[mPermissionList.size()]));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mPermissionFailedList.clear();
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        //你有失败的权限
                        mPermissionFailedList.add(permissions[i]);
                    }
                }
                if (mOnPermissionsResultListener != null) {
                    if (mPermissionFailedList.size() == 0) {
                        mOnPermissionsResultListener.OnSuccess();
                    } else {
                        mOnPermissionsResultListener.OnFail(mPermissionFailedList);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected interface OnPermissionsResultListener {
        void OnSuccess();

        void OnFail(List<String> failedPermissionList);
    }
}
