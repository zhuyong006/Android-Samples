package com.sunmi.audiotrack;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    private static final int BUFFER_SIZE = 1024 * 2;
    private ReadThread mReadThread;
    private AudioTrack audioRtack;
    private FileInputStream dis;
    private byte[] mBuffer;
    private File recordingFile;

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            audioRtack.play();
            try {
                int cnt = 0;
                while((cnt = dis.read(mBuffer)) > 0){
                    audioRtack.write(mBuffer, 0, cnt);
                    Log.e("AudioTrack","cnt = " + cnt);
                }
            } catch (RuntimeException | IOException e) {
                e.printStackTrace();
            }

            try {
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordingFile = new File("/data/xusong-shzj2.wav");

        try {
            dis = new FileInputStream(recordingFile);
        }catch (FileNotFoundException e){
            e.printStackTrace();
            onDestroy();
        }


        int streamType = AudioManager.STREAM_MUSIC;
        int simpleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int mode = AudioTrack.MODE_STREAM;
        mBuffer = new byte[BUFFER_SIZE];
        int minBufferSize = AudioTrack.getMinBufferSize(simpleRate , channelConfig , audioFormat);
        audioRtack = new AudioTrack(streamType , simpleRate , channelConfig , audioFormat ,
                Math.max(minBufferSize , BUFFER_SIZE) , mode);

        mReadThread = new ReadThread();
        mReadThread.start();
    }
}
