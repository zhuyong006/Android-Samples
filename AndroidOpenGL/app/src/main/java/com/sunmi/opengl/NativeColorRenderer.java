package com.sunmi.opengl;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class NativeColorRenderer implements GLSurfaceView.Renderer{
    static {
        System.loadLibrary("native-color");
    }

    public NativeColorRenderer(int color) {
        this.color = color;
    }

    public native void surfaceCreated(int color);

    public native void surfaceChanged(int width, int height);

    public native void onDrawFrame();

    private int color;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        surfaceCreated(color);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        surfaceChanged(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        onDrawFrame();
    }
}
