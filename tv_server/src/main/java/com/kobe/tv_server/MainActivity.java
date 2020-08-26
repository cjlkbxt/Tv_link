package com.kobe.tv_server;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.kobe.lib_base.BaseActivity;
import com.kobe.lib_zxing.zxing.encoding.EncodingUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends BaseActivity {

    @SuppressLint("StaticFieldLeak")
    private static WebView mWebView;

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                Bundle bundle = msg.getData();
                if (bundle != null) {
                    if (!TextUtils.isEmpty(bundle.getString("msg"))) {
                        mWebView.setVisibility(View.VISIBLE);
                        mWebView.loadUrl(bundle.getString("msg"));
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ImageView mIvQrcode = findViewById(R.id.iv_qrcode);

        mWebView = findViewById(R.id.wb_project);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());

        Bitmap qrCodeBitmap = EncodingUtils.createQRCode(getIpAddress(), 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        // 设置图片
        mIvQrcode.setImageBitmap(qrCodeBitmap);
        new Thread() {
            public void run() {
                Bundle bundle = new Bundle();
                bundle.clear();
                try {
                    ServerSocket serverSocket = new ServerSocket(8888);
                    while (true) {
                        Message msg = new Message();
                        msg.what = 0x11;
                        try {
                            Socket socket = serverSocket.accept();
                            BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String line;
                            String buffer = "";
                            while ((line = bff.readLine()) != null) {
                                buffer = line + buffer;
                            }
                            bundle.putString("msg", buffer);
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                            bff.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();

    }

    //或取本机的ip地址
    private String getIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0) {
            return null;
        } else {
            return "tv_link" + ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                    + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
        }
    }

}
