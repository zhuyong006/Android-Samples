package com.sunmi.sdcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
/**
 * Created by Administrator on 2019/1/30 0030.
 */

public class EntryActivity extends AppCompatActivity {
    private Button object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.entry);

        Intent intent = getIntent();
        String TargetFile = intent.getStringExtra("target file");
        Log.e("EntryActivity","TargetFile : " + TargetFile);

        object = (Button) findViewById(R.id.button);
        if(TargetFile != null)
            object.setText(TargetFile);

        object.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(EntryActivity.this, SdcardActivity.class));
            }
        });
    }
}
