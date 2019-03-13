package com.sunmi.mmleak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private int index = 0;
    private TextView tv = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                do {
//                    NativeMmLeak();
//                    index++;
//                    Log.e("Jon","Leak Mem");
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException io) {
//
//                    }
//                }while (true);
//            }
//        }).start();
        NativeMmLeak();

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String NativeMmLeak();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
