package com.sunmi.camera;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,Camera.PreviewCallback{

    SurfaceHolder surfaceholder;
    SurfaceView surfaceview;
    SurfaceTexture surfacetexture;
    Canvas canvas;
    Camera camera;
    int counter=0;
    int width,height,sum;
    Bitmap img1;
    private long current_cost = 0;
    private long last_cost = 0;
    static final String Tag = "Jon";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        surfaceview = (SurfaceView)findViewById(R.id.cameraView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //开启相机
        camera=Camera.open(0);
        try
        {
            camera.setPreviewDisplay(surfaceholder);
            //camera.setPreviewCallback(this);
            Log.e(Tag,"开启相机成功");
        }
        catch(Exception e)
        {
            Log.e(Tag,"开启相机失败");
            camera.release();
            camera=null;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Camera.Parameters parameters=camera.getParameters();
        parameters.setPictureSize(640, 480);
        parameters.setPreviewSize(640, 480);

        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);

        camera.startPreview();
        Log.e(Tag,"开启相机成功22323");
        counter++;
        Log.e(Tag,Integer.toString(counter));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e(Tag,"surfaceDestroyed");
        camera.stopPreview();
        camera.release();
        camera=null;
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        current_cost = System.currentTimeMillis();
        Log.e(Tag,"onPreviewFrame,time = " + (int)(current_cost-last_cost));
        last_cost = current_cost;
    }
}
