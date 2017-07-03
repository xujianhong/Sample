package com.daomingedu.ijkplayertest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.daomingedu.ijkplayertest.coustomview.CustomPlayerView;

/**
 * Created by jianhongxu on 2017/7/3.
 */

public  class MainActivity extends AppCompatActivity {
    String urlData = "http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4";
    CustomPlayerView cp_view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cp_view = (CustomPlayerView)findViewById(R.id.cp_view);
    }

    public void onInit(View view) {
        cp_view.setUrlData(urlData,null);
        cp_view.initPlayer();
    }
}

