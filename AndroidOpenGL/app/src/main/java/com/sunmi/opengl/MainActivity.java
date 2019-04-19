package com.sunmi.opengl;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//    // Example of a call to a native method
//    TextView tv = findViewById(R.id.sample_text);
//    tv.setText(stringFromJNI());
        setupViews();
    }

    private void setupViews() {
        mGLSurfaceView = new GLSurfaceView(this);
        setContentView(mGLSurfaceView);
        //设置版本
        mGLSurfaceView.setEGLContextClientVersion(3);
        //GLSurfaceView.Renderer renderer = new ColorRenderer(Color.GRAY);
        //GLSurfaceView.Renderer renderer = new NativeColorRenderer(Color.YELLOW);
        //GLSurfaceView.Renderer renderer = new PointRenderer();
        GLSurfaceView.Renderer renderer = new RectangleRenderer();
        mGLSurfaceView.setRenderer(renderer);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    // Used to load the 'native-lib' library on application startup.

}
