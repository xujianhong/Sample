package com.daomingedu.ijkplayertest.coustomview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daomingedu.ijkplayertest.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jianhongxu on 2017/7/4.
 */

public class VideoController extends BaseController
        implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "BaseController";
    private Context mContext;
    CustomPlayer player;

    ImageButton
            ib_play,
            ib_screen;
    TextView
            tv_position_time,
            tv_end_time;
    LinearLayout ll_loading;
    AppCompatSeekBar seek;
    FrameLayout fl_main;

    Timer mUpdateTimer;
    TimerTask mUpdateTimerTask;
    public VideoController(@NonNull Context context) {
        super(context);
        init(context);
    }

    @Override
    public void setPlayer(CustomPlayer player) {
        this.player = player;
    }

    @Override
    public void setPlayerState(int currentState) {

        switch (currentState){
            case CustomPlayer.STATE_IDLE:
                fl_main.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                ll_loading.setVisibility(INVISIBLE);
                break;
            case CustomPlayer.STATE_INITIALIZED:
            case CustomPlayer.STATE_PREPARE:
                fl_main.setBackgroundColor(getResources().getColor(R.color.colorPlayerBg));
                ll_loading.setVisibility(VISIBLE);
                break;
            case CustomPlayer.STATE_PREPARE_END:
                fl_main.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                ll_loading.setVisibility(INVISIBLE);
                startUpdate();
                break;


        }
    }

    private void cancelUpdate(){
        if(mUpdateTimer!=null) {
            mUpdateTimer.cancel();
            mUpdateTimer =null;
        }
        if(mUpdateTimerTask!=null){
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }

    private void startUpdate() {
        cancelUpdate();
        if(mUpdateTimer == null){
            mUpdateTimer = new Timer();
        }
        if(mUpdateTimerTask == null){
            mUpdateTimerTask = new TimerTask() {
                @Override
                public void run() {

                    VideoController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateTime();
                        }
                    });
                }
            };
        }

        mUpdateTimer.schedule(mUpdateTimerTask,0,300);
    }

    private void updateTime() {
        long position = player.getCurrentPosition();
        long duration = player.getDuration();
        int bufferPercentage = player.getBufferPercentage(); //缓存百分比(0-100)
        seek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f*position/duration);
        seek.setProgress(progress);

        tv_position_time.setText(PlayerUtils.formatTime(position));
        tv_end_time.setText(PlayerUtils.formatTime(duration));

        Log.e(TAG, "updateTime: "+position);

    }


    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_controller,this);

        ib_play = (ImageButton)findViewById(R.id.ib_play);
        ib_screen = (ImageButton)findViewById(R.id.ib_screen);

        tv_position_time = (TextView)findViewById(R.id.tv_position_time);
        tv_end_time = (TextView)findViewById(R.id.tv_end_time);

        ll_loading =(LinearLayout)findViewById(R.id.ll_loading);
        seek = (AppCompatSeekBar)findViewById(R.id.seek);

        fl_main = (FrameLayout)findViewById(R.id.fl_main);


        ib_play.setOnClickListener(this);
        ib_screen.setOnClickListener(this);
        seek.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
