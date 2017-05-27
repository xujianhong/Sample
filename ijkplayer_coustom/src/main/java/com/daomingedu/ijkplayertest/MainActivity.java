package com.daomingedu.ijkplayertest;

import android.content.res.Configuration;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.daomingedu.ijkplayertest.widget.PlayView;

import com.daomingedu.ijkplayertest.widget.media.IjkVideoView;


import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    IjkVideoView ijk;



    String path ="rtmp://live.hkstv.hk.lxdns.com/live/hks";


    Button btn_path1;

    Button btn_path2;


    private boolean mBackPressed;

    PlayView playView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        findViewById(R.id.btn_path1).setOnClickListener(this);

        findViewById(R.id.btn_path2).setOnClickListener(this);

//        path = "http://baobab.wdjcdn.com/14564977406580.mp4";
        path ="/storage/emulated/0/Movies/video_20170516_115603.mp4";
        playView = new PlayView(this).setPath(path,false);


    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        playView.onConfigurationChanged(newConfig);

        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        playView.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(mBackPressed||!ijk.isBackgroundPlayEnabled()){
//            ijk.stopPlayback();
//            ijk.release(true);
//            ijk.stopBackgroundPlay();//关闭后台播放
//        }else{
//            ijk.enterBackground();//后台播放
//        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_path1){
            path = "/storage/emulated/0/Movies/video_20170516_115603.mp4";

        }else if(v.getId() == R.id.btn_path2){
            path ="/storage/emulated/0/Movies/video_20170418_102425.mp4";
        }
        playView.setPath(path,false);
    }
}
