package com.sunmi.openglcamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout previewLayout;
    private MyGLSurfaceView myGLSurfaceView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        previewLayout=(RelativeLayout)findViewById(R.id.previewLayout);
        myGLSurfaceView=new MyGLSurfaceView(this, new MyGLSurfaceView.GetUiHandlerInterface() {
            @Override
            public void getUiHandler(Message leftUpPt) {
                mHandler.sendMessage(leftUpPt);
            }
        });
        previewLayout.addView(myGLSurfaceView);

        textView = (TextView) findViewById(R.id.sample_text);
    }
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    textView.setText("Fps : " + msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };
    void requestPermission(){
        final int REQUEST_CODE = 1;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
    }
}
