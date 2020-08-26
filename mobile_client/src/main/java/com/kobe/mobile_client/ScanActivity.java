package com.kobe.mobile_client;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding3.view.RxView;
import com.kobe.lib_base.BaseActivity;
import com.kobe.lib_zxing.zxing.activity.CaptureActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class ScanActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        TextView tvScan = findViewById(R.id.tv_scan);

        RxView.clicks(tvScan).throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        if (!checkPermission(Manifest.permission.CAMERA)) {
                            String[] permissions = new String[]{Manifest.permission.CAMERA};
                            requestPermission(permissions, new OnPermissionsResultListener() {
                                @Override
                                public void OnSuccess() {
                                    Intent intent = new Intent(ScanActivity.this, CaptureActivity.class);
                                    startActivityForResult(intent, 0);
                                }

                                @Override
                                public void OnFail(List<String> failedPermissionList) {
                                    Toast.makeText(ScanActivity.this, "扫描功能需要开启相机权限", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Intent intent = new Intent(ScanActivity.this, CaptureActivity.class);
                            startActivityForResult(intent, 0);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String result = bundle.getString("result");
                if (result.startsWith("tv_link")) {
                    Intent intent = new Intent(this, InputProjectAddressActivity.class);
                    intent.putExtra("ipAddress", result.replaceFirst("tv_link", ""));
                    startActivity(intent);
                } else {
                    Toast.makeText(ScanActivity.this, "请正确扫描电视端的二维码", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}
