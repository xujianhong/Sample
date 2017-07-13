package com.daomingedu.ijkplayertest.coustomview;


import android.content.ContentResolver;
import android.content.Context;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
        SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    private static final String TAG = "VideoController";
    private static final int SLIDE_RIGHT_UP_DOWN = 6; //右上下滑动
    private static final int SLIDE_LEFT_UP_DOWN = 1;//左上下滑动
    private static final int SWIPE_LEFT_RIGHT = 2; //左右滑动
    private static final int SCROLLER_NORMAL = 0; //无滑动
    private static final int FROM_SEEKER_USER = 4;//seekBar拖拽
    private static final int FROM_PLAYER = 3;//player自得的
    private static final int FROM_LEFT_RIGHT_SCROLLER = 5;//左右滑动拖拽
    private static final int BRIGHTNESS_MEDIT = 7;//左上下滑动拖拽
    private static final int FROM_UP_DOWN_SCROLLER_RIGHT = 8;//右上下滑动拖拽


    private Context mContext;
    CustomPlayer player;

    ImageButton
            ib_play,
            ib_screen;
    TextView
            tv_buffing_Prepare,
            tv_position_time,
            tv_end_time,
            tv_scroller;
    LinearLayout
            ll_loading,
            ll_error,
            ll_scroller;
    AppCompatSeekBar seek;
    FrameLayout
            fl_main,
            fl_completed,
            fl_scroller;
    ImageView iv_scroller;

    ProgressBar pb_scroller;
    Timer mUpdateTimer;
    TimerTask mUpdateTimerTask;

    boolean isFull;//是否全屏


    private float downX;
    private float downY;
    private float differenceX; //滑动X的差值
    private float differenceY; //滑动Y的差值
    private int scroller = SCROLLER_NORMAL;
    private long scrollerCurrentPosition = 0; //滑动当前的位置
    private float mCurrentBrightness = CustomPlayer.STATE_MEDIA_DATA_ERROR;//当前系统的亮度
    private boolean isModifyBrightnessMode ;//是否修改系统亮度模式

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

        displayBtn();
        if (currentState != CustomPlayer.STATE_ERROR && ll_error.isClickable()) {
            ll_error.setVisibility(INVISIBLE);
            ll_error.setClickable(false);
        }
        if (currentState != CustomPlayer.STATE_COMPLETED && fl_completed.isClickable()) {
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
            case CustomPlayer.STATE_INITIALIZED:
                setCurrentBrightness();
            case CustomPlayer.STATE_BUFFING_START:
            case CustomPlayer.STATE_PREPARE:
                fl_main.setBackgroundColor(getResources().getColor(R.color.colorPlayerBg));
                ll_loading.setVisibility(VISIBLE);
                break;

            case CustomPlayer.STATE_PREPARE_END:
                startUpdate(FROM_PLAYER);
            case CustomPlayer.STATE_BUFFING_END:
                fl_main.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                ll_loading.setVisibility(INVISIBLE);
                showBtn();
                break;
            case CustomPlayer.PLAYER_STATE_PAUSE:
                cancelUpdate();
                break;
            case CustomPlayer.PLAYER_STATE_PLAYING:
                startUpdate(FROM_PLAYER);
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
            case CustomPlayer.STATE_END:
                restoreBrightnessMode();
                break;
        }
    }

    /**
     * 得到当前系统亮度
     */
    private void setCurrentBrightness() {
        if (mCurrentBrightness == CustomPlayer.STATE_MEDIA_DATA_ERROR) {
            setScreenBrightnessMode();
            ContentResolver contentResolver = mContext.getContentResolver();
            try {
                mCurrentBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);

            } catch (Settings.SettingNotFoundException e) {
                try {
                    mCurrentBrightness = Settings.System.getFloat(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
                } catch (Settings.SettingNotFoundException e1) {
                    try {
                        mCurrentBrightness = Settings.System.getLong(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
                    } catch (Settings.SettingNotFoundException e2) {
                        e2.printStackTrace();
                    }

                }

            }

        }

    }

    /**
     * 修改当前系统亮度调节模式 如果是自动改为手动
     */
    public void setScreenBrightnessMode() {
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                isModifyBrightnessMode = true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复之前亮度调节模式
     */
    public void restoreBrightnessMode(){
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL&&isModifyBrightnessMode) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                isModifyBrightnessMode = false;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setBufferingUpdate(int bufferingUpdate) {
        if (player.isPreparing() || player.isBuffing()) {
            tv_buffing_Prepare.setText("已缓冲" + bufferingUpdate + "%");
        }
        seek.setSecondaryProgress(bufferingUpdate);
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


    private void startUpdate(final int fromUser) {
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

    private void updateTime(int from) {
//        Log.d(TAG, "updateTime: " + player.getCurrentState());
        if (from == FROM_PLAYER) {
            //导航栏的时间显示跟着播放器走
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
        } else if (from == FROM_SEEKER_USER) { //seekBar推拽
            int position = (seek.getProgress() * player.getDuration() / seek.getMax());
            tv_position_time.setText(PlayerUtils.formatTime(position));

        } else if (from == FROM_LEFT_RIGHT_SCROLLER) {//滑动推拽

            //导航栏的时间显示跟着播放器走
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


            if (scrollerCurrentPosition == 0) {
                scrollerCurrentPosition = position;
            }

            long scrollerPosition = scrollerCurrentPosition + (long) (differenceX * duration / getWidth());
            if (scrollerPosition >= duration)
                scrollerPosition = duration;
            int scrollerProgress = (int) (100f * scrollerPosition / duration);
            pb_scroller.setProgress(scrollerProgress);
            tv_scroller.setText(PlayerUtils.formatTime(scrollerPosition) + "/" + PlayerUtils.formatTime(duration));
        }


    }


    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.layout_controller, this);

        ib_play = (ImageButton) findViewById(R.id.ib_play);
        ib_screen = (ImageButton) findViewById(R.id.ib_screen);

        tv_buffing_Prepare = (TextView) findViewById(R.id.tv_buffing_Prepare);
        tv_position_time = (TextView) findViewById(R.id.tv_position_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_scroller = (TextView) findViewById(R.id.tv_scroller);

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        ll_error = (LinearLayout) findViewById(R.id.ll_error);
        ll_scroller = (LinearLayout) findViewById(R.id.ll_scroller);

        iv_scroller = (ImageView) findViewById(R.id.iv_scroller);

        seek = (AppCompatSeekBar) findViewById(R.id.seek);

        pb_scroller = (ProgressBar) findViewById(R.id.pb_scroller);

        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        fl_completed = (FrameLayout) findViewById(R.id.fl_completed);
        fl_scroller = (FrameLayout) findViewById(R.id.fl_scroller);


        ib_play.setOnClickListener(this);
        ib_screen.setOnClickListener(this);

        ll_error.setOnClickListener(this);
        seek.setOnSeekBarChangeListener(this);
        fl_completed.setOnClickListener(this);
        fl_scroller.setOnTouchListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_completed:
                Log.d(TAG, "onClick: fl_completed");
            case R.id.ib_play:
                Log.d(TAG, "onClick: ib_play");
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.resume();
                }
                showBtn();
                break;
            case R.id.ib_screen:
                Log.d(TAG, "onClick: ib_screen");
                if (player.getDisplayState() == CustomPlayer.DISPLAY_SMALL) {
                    player.fullScreen();
                } else if (player.getDisplayState() == CustomPlayer.DISPLAY_FULL) {
                    player.smallScreen();
                }
                displayBtn();
                break;
            case R.id.ll_error://发生错误
                Log.d(TAG, "onClick: ll_error");

                player.start();
                break;

        }
    }

    private void displayBtn() {
        if (player.getDisplayState() == CustomPlayer.DISPLAY_SMALL) {
            ib_screen.setImageResource(R.mipmap.icon_full_screen);
            isFull = false;
        } else if (player.getDisplayState() == CustomPlayer.DISPLAY_FULL) {
            ib_screen.setImageResource(R.mipmap.icon_crop_screen);
            isFull = true;
        }
    }

    private void showBtn() {
        if (player.isPlaying()) {
            ib_play.setImageResource(R.mipmap.icon_pause);
        } else {
            ib_play.setImageResource(R.mipmap.icon_play);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            startUpdate(FROM_SEEKER_USER);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        if (fl_completed.isClickable()) {
            fl_completed.setVisibility(INVISIBLE);
            fl_completed.setClickable(false);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int position = (int) (((double) seekBar.getProgress() / seekBar.getMax()) * player.getDuration());
        Log.e(TAG, "onStopTrackingTouch: " + position);
        seekTo(position);

        startUpdate(FROM_PLAYER);
    }


    private void seekTo(int position) {
        if (!player.isPlaying()) {//如果没有播放 设置当前的滑动的位置
            player.setCurrentPosition(position);
        }
        player.seekTo(position);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isFull) return false;

        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
//                Log.w(TAG, "onTouch: ACTION_DOWN ");
                scroller = SCROLLER_NORMAL;
                break;
            case MotionEvent.ACTION_MOVE:
                differenceX = event.getX() - downX;//获得滑动差值
                differenceY = event.getY() - downY;
                if (event.getEventTime() - event.getDownTime() < 100) {//防止误操作，down 和move间隔小于100不会触发下面的滑动操作

                    break;
                }
                showScroller();
                if (Math.abs(differenceX) > Math.abs(differenceY)
                        && scroller == SCROLLER_NORMAL) {//左右滑动
                    scroller = SWIPE_LEFT_RIGHT;

                } else if (Math.abs(differenceX) < Math.abs(differenceY)
                        && scroller == SCROLLER_NORMAL) {
                    if (downX <= (getWidth() / 2)) {//左上下滑动
                        scroller = SLIDE_LEFT_UP_DOWN;
                    } else {
                        scroller = SLIDE_RIGHT_UP_DOWN;//右上下滑动
                    }
                }
                if (scroller == SWIPE_LEFT_RIGHT) {//左右滑动
                    showScrollerProgressBar();
                    if (differenceX < 0 && !"CRewind".equals(iv_scroller.getTag())) {//后退
                        iv_scroller.setImageResource(R.mipmap.ic_fast_rewind);
                        iv_scroller.setTag("CRewind");

                    } else if (differenceX >= 0 && !"CForward".equals(iv_scroller.getTag())) {//快进
                        iv_scroller.setImageResource(R.mipmap.ic_fast_forward);
                        iv_scroller.setTag("CForward");

                    }
                    startUpdate(FROM_LEFT_RIGHT_SCROLLER);

                    Log.w(TAG, "onTouch: Swipe left and right " + differenceX);
                } else if (scroller == SLIDE_LEFT_UP_DOWN) { //左上下滑动
                    hideScrollerProgressBar();
                    if (mCurrentBrightness > 170 && !"CLightHigh".equals(iv_scroller.getTag())) {
                        iv_scroller.setImageResource(R.mipmap.ic_brightness_high);
                        iv_scroller.setTag("CLightHigh");
                    } else if (mCurrentBrightness < 170 && mCurrentBrightness > 85 && !"CLightMedic".equals(iv_scroller.getTag())) {
                        iv_scroller.setImageResource(R.mipmap.ic_brightness_medit);
                        iv_scroller.setTag("CLightMedic");
                    } else if (mCurrentBrightness < 85 && !"CLightLow".equals(iv_scroller.getTag())) {
                        iv_scroller.setImageResource(R.mipmap.ic_brightness_low);
                        iv_scroller.setTag("CLightLow");
                    }
//                    Log.w(TAG, "onTouch: Slide up and down    LEFT " + differenceY);

                    setScreenBrightness(differenceY);

                } else if (scroller == SLIDE_RIGHT_UP_DOWN) { //右上下滑动
                    hideScrollerProgressBar();
                    if (!"CVolume".equals(iv_scroller.getTag())) {
                        iv_scroller.setImageResource(R.mipmap.ic_volume_mute);
                        iv_scroller.setTag("CVolume");
                    }
                    Log.w(TAG, "onTouch: Slide up and down    RIGHT " + differenceY);

                }

                break;
            case MotionEvent.ACTION_UP:
                if (scroller == SWIPE_LEFT_RIGHT) {//左右滑动
                    int position = (int) (((double) pb_scroller.getProgress() / pb_scroller.getMax()) * player.getDuration());
                    Log.e(TAG, "swipe left right: " + position);
                    seekTo(position);
                    scrollerCurrentPosition = 0;
                    pb_scroller.setProgress(0);
                    startUpdate(FROM_PLAYER);
                }

                scroller = SCROLLER_NORMAL;
                hideScroller();

                break;
        }
        return true;
    }

    private void hideScrollerProgressBar() {
        if (pb_scroller.getTag(R.id.progressbar_visibility) == null || (boolean) pb_scroller.getTag(R.id.progressbar_visibility)) {
            pb_scroller.setVisibility(GONE);
            pb_scroller.setTag(R.id.progressbar_visibility, false);
        }
    }

    private void showScrollerProgressBar() {
        if (pb_scroller.getTag(R.id.progressbar_visibility) == null || !(boolean) pb_scroller.getTag(R.id.progressbar_visibility)) {
            pb_scroller.setVisibility(VISIBLE);
            pb_scroller.setTag(R.id.progressbar_visibility, true);
        }
    }

    private void hideScroller() {
        if (ll_scroller.getTag(R.id.scroller_visibility) == null || (boolean) ll_scroller.getTag(R.id.scroller_visibility)) {
            ll_scroller.setVisibility(INVISIBLE);
            ll_scroller.setTag(R.id.scroller_visibility, false);
            fl_main.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            Log.e(TAG, "hideScroller: ");
        }

    }


    private void showScroller() {
        if (ll_scroller.getTag(R.id.scroller_visibility) == null || !(boolean) ll_scroller.getTag(R.id.scroller_visibility)) {
            ll_scroller.setVisibility(VISIBLE);
            ll_scroller.setTag(R.id.scroller_visibility, true);
            fl_main.setBackgroundColor(getResources().getColor(R.color.colorPlayerBg));
            Log.d(TAG, "showScroller: ");
        }

    }

    /**
     * 设置当前窗口屏幕亮度
     *
     * @param screenBrightness
     */
    public void setScreenBrightness(float screenBrightness) {
        Window window = ((AppCompatActivity) mContext).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        Log.e(TAG, "setScreenBrightness: "+(screenBrightness * 255.0f / getHeight()) +"\n  mCurrentBrightness "+mCurrentBrightness
        +"\n  total"+(mCurrentBrightness + (screenBrightness * 255.0f / getHeight())));
//       mCurrentBrightness + (screenBrightness * 255.0f / getHeight());

        lp.screenBrightness = mCurrentBrightness;
        tv_scroller.setText((mCurrentBrightness/255.0f*100)+"");
        window.setAttributes(lp);
        //TODO 调节亮度
    }


}
