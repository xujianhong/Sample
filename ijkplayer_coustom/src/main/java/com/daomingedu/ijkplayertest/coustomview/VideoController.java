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

    private static final String TAG = "VideoController";
    private Context mContext;
    CustomPlayer player;

    ImageButton
            ib_play,
            ib_screen;
    TextView
            tv_buffing_Prepare,
            tv_position_time,
            tv_end_time;
    LinearLayout
            ll_loading,
            ll_error;
    AppCompatSeekBar seek;
    FrameLayout
            fl_main,
            fl_completed;

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

        if (currentState != CustomPlayer.STATE_ERROR && ll_error.isClickable()) {
            ll_error.setVisibility(INVISIBLE);
            ll_error.setClickable(false);
        }
        if(currentState!=CustomPlayer.STATE_COMPLETED&&fl_completed.isClickable()){
            fl_completed.setVisibility(INVISIBLE);
            fl_completed.setClickable(false);
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
                showBtn();
                ib_screen.setImageResource(R.mipmap.icon_full_screen);
                break;

            case CustomPlayer.STATE_BUFFING_START:
            case CustomPlayer.STATE_INITIALIZED:
            case CustomPlayer.STATE_PREPARE:
                fl_main.setBackgroundColor(getResources().getColor(R.color.colorPlayerBg));
                ll_loading.setVisibility(VISIBLE);
                break;

            case CustomPlayer.STATE_PREPARE_END:
                startUpdate(false);
            case CustomPlayer.STATE_BUFFING_END:
                fl_main.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                ll_loading.setVisibility(INVISIBLE);
                showBtn();
                break;
            case CustomPlayer.PLAYER_STATE_PAUSE:
                cancelUpdate();
                break;
            case CustomPlayer.PLAYER_STATE_PLAYING:
                startUpdate(false);
                break;

            case CustomPlayer.STATE_COMPLETED:
                showBtn();
                cancelUpdate();
                fl_completed.setVisibility(VISIBLE);
                fl_completed.setClickable(true);
                break;

            case CustomPlayer.STATE_ERROR:
                ll_loading.setVisibility(INVISIBLE);
                ll_error.setVisibility(VISIBLE);
                ll_error.setClickable(true);
                break;
        }
    }

    @Override
    public void setBufferingUpdate(int bufferingUpdate) {
        if(player.isPreparing()||player.isBuffing()){
            tv_buffing_Prepare.setText("已缓冲"+bufferingUpdate+"%");
        }
        seek.setSecondaryProgress(bufferingUpdate);
    }


    private void showBtn() {
        if (player.isPlaying()) {
            ib_play.setImageResource(R.mipmap.icon_pause);
        } else {
            ib_play.setImageResource(R.mipmap.icon_play);
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


    private void startUpdate(final boolean fromUser) {
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
                            updateTime(fromUser);
                        }
                    });
                }
            };
        }

        mUpdateTimer.schedule(mUpdateTimerTask, 0, 300);
    }

    private void updateTime(boolean fromUser) {
//        Log.d(TAG, "updateTime: " + player.getCurrentState());
        if (!fromUser) {
            long position = player.getCurrentPosition();
            long duration = player.getDuration();

            if (position == CustomPlayer.STATE_MEDIA_DATA_ERROR
                    || duration == CustomPlayer.STATE_MEDIA_DATA_ERROR) {
                cancelUpdate();
                return;
            }

            int progress = (int) (100f * position / duration);
            seek.setProgress(progress);

            tv_position_time.setText(PlayerUtils.formatTime(position));
            tv_end_time.setText(PlayerUtils.formatTime(duration));

//        Log.e(TAG, "updateTime: "+position);
        } else { //手动推拽

            int position = (int) (seek.getProgress() * player.getDuration() / seek.getMax());
            tv_position_time.setText(PlayerUtils.formatTime(position));

        }


    }


    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_controller, this);

        ib_play = (ImageButton) findViewById(R.id.ib_play);
        ib_screen = (ImageButton) findViewById(R.id.ib_screen);

        tv_buffing_Prepare = (TextView)findViewById(R.id.tv_buffing_Prepare);
        tv_position_time = (TextView) findViewById(R.id.tv_position_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        ll_error = (LinearLayout) findViewById(R.id.ll_error);


        seek = (AppCompatSeekBar) findViewById(R.id.seek);

        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        fl_completed = (FrameLayout)findViewById(R.id.fl_completed);


        ib_play.setOnClickListener(this);
        ib_screen.setOnClickListener(this);

        ll_error.setOnClickListener(this);
        seek.setOnSeekBarChangeListener(this);
        fl_completed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_completed:
            case R.id.ib_play:
                if(player.isPlaying()){
                    player.pause();
                }
                else{
                    player.resume();
                }
                showBtn();
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
        startUpdate(fromUser);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        if(fl_completed.isClickable()){
            fl_completed.setVisibility(INVISIBLE);
            fl_completed.setClickable(false);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int position = (int) (((double) seekBar.getProgress() / seekBar.getMax()) * player.getDuration());
        Log.e(TAG, "onStopTrackingTouch: " + position);
        seekTo(position);
        startUpdate(false);
    }


    private void seekTo(int posittion) {


        player.seekto(posittion);


    }


}
