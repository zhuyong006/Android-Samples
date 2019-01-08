package com.sunmi.looper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btnSendToUI1, btnSendToUI2,btnSendToSubThread;
    private TextView tvSendMes1;
    private TextView LooperId;
    private static MyHandler handler;
    private Handler SubThreadHandler;
    private int SubThreadTid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvSendMes1 = (TextView) findViewById(R.id.tvSendMes1);
        LooperId = (TextView) findViewById(R.id.looper_id);
        btnSendToUI1 = (Button) findViewById(R.id.btnSendToUI1);
        btnSendToUI2 = (Button) findViewById(R.id.btnSendToUI2);
        btnSendToSubThread = (Button) findViewById(R.id.btnSendToSubThread);
        btnSendToUI1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 使用Activity内部的Looper对象
                handler=new MyHandler();
                Message msg=Message.obtain();
                msg.what=1;
                msg.obj="默认Looper";
                handler.sendMessage(msg);
            }
        });

        btnSendToUI2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前线程的Looper
                Looper looper=Looper.myLooper();
                handler=new MyHandler(looper);

                Message msg=Message.obtain();
                msg.what=2;
                msg.obj="使用当前线程的Looper";
                handler.sendMessage(msg);
            }
        });

        btnSendToSubThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg=Message.obtain();
                msg.what=3;
                msg.obj="使用子线程的Looper";
                SubThreadHandler.sendMessage(msg);
            }
        });
        // 在UI线程中开启一个子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 在子线程中初始化一个Looper对象
                Looper.prepare();
                SubThreadHandler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        // 把UI线程发送来的消息显示到屏幕上。
                        Message submsg=Message.obtain();
                        submsg.what=msg.what;
                        submsg.obj=msg.obj;
                        SubThreadTid = android.os.Process.myTid();
                        handler=new MyHandler(Looper.getMainLooper());
                        handler.sendMessage(submsg);
                    }
                };
                // 把刚才初始化的Looper对象运行起来，循环消息队列的消息
                Looper.loop();
            }
        }).start();
    }


    public class MyHandler extends Handler {

        public MyHandler() {
        }

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int tid = android.os.Process.myTid();
            tvSendMes1.setText("what=" + msg.what + "," + msg.obj);
            if(SubThreadTid != 0) {
                LooperId.setText("Looper Tid = " + SubThreadTid);
                SubThreadTid = 0;
            }
            else
                LooperId.setText("Looper Tid = " + tid);
        }
    }

}
