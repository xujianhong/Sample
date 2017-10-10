package com.daomingedu.ijkplayertest;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.daomingedu.ijkplayertest.coustomview.CustomPlayerView;

/**
 * Created by jianhongxu on 2017/7/3.
 */

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_SETTINGS = 200;
//    String urlData1 = "http://vfx.mtime.cn/Video/2017/03/31/mp4/170331093811717750.mp4";

    String urlData1 = "http://test1-manage.gz.bcebos.com/vocal/170720154217915.mp3";

    //    String urlData = "/storage/emulated/0/Movies/video_20170512_163255.mp4";
    String urlData = "http://vfx.mtime.cn/Video/2017/05/25/mp4/170525100752401900.mp4";
    CustomPlayerView cp_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cp_view = (CustomPlayerView) findViewById(R.id.cp_view);
    }

    public void onInit(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0以上要动态获取所以加一个判断
            if (Settings.System.canWrite(this)) {
                cp_view.setUrlData(urlData, null,false)
                        .start();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,REQUEST_WRITE_SETTINGS);
            }
        }
    }

    public void onPath(View view) {
        cp_view.setUrlData(urlData1, null,true)
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cp_view.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_WRITE_SETTINGS){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(Settings.System.canWrite(this)) {
                    cp_view.setUrlData(urlData, null,false)
                            .start();
                }
            }

        }
    }
}

