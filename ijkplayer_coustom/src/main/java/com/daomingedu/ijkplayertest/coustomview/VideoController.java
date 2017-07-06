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
    LinearLayout
            ll_loading,
            ll_error;
    AppCompatSeekBar seek;
    FrameLayout fl_main;

    Timer mUpdateTimer;
    TimerTask mUpdateTimerTask;

    private long lastPosition;

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

        if(currentState != CustomPlayer.STATE_ERROR&&ll_error.isClickable()){
            ll_error.setVisibility(INVISIBLE);
            ll_error.setClickable(false);
        }

        switch (currentState) {

            case CustomPlayer.STATE_IDLE:
                cancelUpdate();
                fl_main.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                ll_loading.setVisibility(INVISIBLE);
                tv_position_time.setText(PlayerUtils.formatTime(0));
                tv_end_time.setText(PlayerUtils.formatTime(0));
                seek.setProgress(0);
                seek.setSecondaryProgress(0);
                ib_play.setImageResource(R.mipmap.icon_play);
                ib_screen.setImageResource(R.mipmap.icon_full_screen);
                break;
            case CustomPlayer.STATE_INITIALIZED:
            case CustomPlayer.STATE_PREPARE:
                fl_main.setBackgroundColor(getResources().getColor(R.color.colorPlayerBg));
                ll_loading.setVisibility(VISIBLE);
                break;
            case CustomPlayer.STATE_PLAYING:
                fl_main.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                ll_loading.setVisibility(INVISIBLE);
                ib_play.setImageResource(R.mipmap.icon_pause);
                startUpdate();
                break;

            case CustomPlayer.STATE_COMPLETED:
                cancelUpdate();
                break;

            case CustomPlayer.STATE_ERROR:
                ll_loading.setVisibility(INVISIBLE);
                ll_error.setVisibility(VISIBLE);
                ll_error.setClickable(true);
                break;




        }
    }

    private void cancelUpdate() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }

    private void startUpdate() {
        cancelUpdate();
        if (mUpdateTimer == null) {
            mUpdateTimer = new Timer();
        }
        if (mUpdateTimerTask == null) {
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

        mUpdateTimer.schedule(mUpdateTimerTask, 0, 300);
    }

    private void updateTime() {
        Log.d(TAG, "updateTime: " + player.getCurrentState());
        long position = player.getCurrentPosition();
        long duration = player.getDuration();
        int bufferPercentage = player.getBufferPercentage(); //缓存百分比(0-100)

        if (position == CustomPlayer.STATE_MEDIA_DATA_ERROR
                || duration == CustomPlayer.STATE_MEDIA_DATA_ERROR
                || bufferPercentage == CustomPlayer.STATE_MEDIA_DATA_ERROR) {
            cancelUpdate();
            return;
        }
        seek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        seek.setProgress(progress);

        tv_position_time.setText(PlayerUtils.formatTime(position));
        tv_end_time.setText(PlayerUtils.formatTime(duration));

//        Log.e(TAG, "updateTime: "+position);

        lastPosition = position;

    }


    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_controller, this);

        ib_play = (ImageButton) findViewById(R.id.ib_play);
        ib_screen = (ImageButton) findViewById(R.id.ib_screen);

        tv_position_time = (TextView) findViewById(R.id.tv_position_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        ll_error = (LinearLayout)findViewById(R.id.ll_error);


        seek = (AppCompatSeekBar) findViewById(R.id.seek);

        fl_main = (FrameLayout) findViewById(R.id.fl_main);


        ib_play.setOnClickListener(this);
        ib_screen.setOnClickListener(this);

        ll_error.setOnClickListener(this);
        seek.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_play:
                break;
            case R.id.ib_screen:
                break;
            case R.id.ll_error://发生错误
                player.start();
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) { //用户操作

            int position = (int) (((double) progress / seekBar.getMax()) * player.getDuration());
            Log.e(TAG, "onProgressChanged: " + position);
            seekTo(position);

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private void seekTo(int posittion) {


        player.seekto(posittion);

        lastPosition = posittion;
    }


}
