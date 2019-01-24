package com.sunmi.opensles;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class OpenSLES extends AppCompatActivity {

    private Button Init = null;
    private Button Start = null;
    private Button Pause = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensles);

    // Example of a call to a native method
        Init = (Button)findViewById(R.id.Init);
        Start = (Button)findViewById(R.id.Start);
        Pause = (Button)findViewById(R.id.Pause);

        Init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSlesInit();
            }
        });
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaySound();
            }
        });
        Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PauseSound();
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native void OpenSlesInit();
    public native void PlaySound();
    public native void PauseSound();
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native_opensles");
    }
}
