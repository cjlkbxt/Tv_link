package com.kobe.mobile_client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kobe.lib_base.BaseActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class InputProjectAddressActivity extends BaseActivity {

    private TextInputEditText mEtInput;
    private String mIpAddress;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                Bundle bundle = msg.getData();
                Toast.makeText(InputProjectAddressActivity.this, bundle.getString("msg"), Toast.LENGTH_SHORT).show();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_ipaddress);
        mIpAddress = getIntent().getStringExtra("ipAddress");
        Button btnConfirm = findViewById(R.id.btn_confirm);
        mEtInput = findViewById(R.id.et_input);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动线程 向服务器发送信息
                new MyThread(mIpAddress, mEtInput.getText().toString().trim()).start();
            }
        });
    }

    class MyThread extends Thread {

        private final String ipAddress;
        private final String projectAddress;

        public MyThread(String ipAddress, String projectAddress) {
            this.ipAddress = ipAddress;
            this.projectAddress = projectAddress;
        }

        @Override
        public void run() {
            //定义消息
            Message msg = new Message();
            msg.what = 0x11;
            Bundle bundle = new Bundle();
            bundle.clear();
            try {
                //连接服务器 并设置连接超时为1秒
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ipAddress, 8888), 5000); //端口号为30000
                //获取输入输出流
                OutputStream ou = socket.getOutputStream();
                //向服务器发送信息
                ou.write(projectAddress.getBytes("gbk"));
                ou.flush();
                //关闭各种输入输出流
                ou.close();
                socket.close();
            } catch (SocketTimeoutException aa) {
                //连接超时 在UI界面显示消息
                bundle.putString("msg", "服务器连接失败！请检查网络是否打开");
                msg.setData(bundle);
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
