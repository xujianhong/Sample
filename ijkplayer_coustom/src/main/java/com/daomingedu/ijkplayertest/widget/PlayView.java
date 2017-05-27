package com.daomingedu.ijkplayertest.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daomingedu.ijkplayertest.DensityUtil;
import com.daomingedu.ijkplayertest.R;
import com.daomingedu.ijkplayertest.StringUtils;
import com.daomingedu.ijkplayertest.ViewUtil;
import com.daomingedu.ijkplayertest.widget.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * Created by xjh on 2016/11/2.
 */

public class PlayView {
    /**
     * 打印日志的TAG
     */
    private static final String TAG = PlayView.class.getSimpleName();
    /**
     * 全局上下文
     */
    private Context mContext;
    /**
     * 依附的容器Activity
     */
    private Activity mActivity;

    /**
     * 原生的Ijkplayer
     */

    IjkVideoView videoView;

    String path = "";//播放路径


    RelativeLayout rl_controll;
    public RelativeLayout rl_videoview;

    LinearLayout ll_bottom;
    ImageButton pasue;
    TextView currenttime;
    SeekBar seekbar;
    TextView totaltime;
    ImageButton fill;
    ProgressBar pb_load;
    TextView tv_error;

    /**
     * 当前播放位置
     */
    private int currentPosition = -1;


    /**
     * 播放总时长
     */
    private long duration;

    boolean isShow;

    private boolean mIntoSeek = false;//是否 快进/快退

    private static final int HIDECONTEROL = 0x01;//隐藏底部控制器
    private static final int SHOWCONTEROL = 0x02;//显示底部控制器
    private static final int CURRONTTIME = 0x03;//当前时间
    private static final int SHOWPB = 0x04;//显示load
    private static final int HIDEPB = 0x05;//隐藏load
    private static final int SHOWERROR = 0x06;//显示错误
    private static final int HIDEERROR = 0x07;//隐藏错误
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDECONTEROL:
                    ll_bottom.setVisibility(View.GONE);
                    isShow = false;
                    break;
                case SHOWCONTEROL:
                    ll_bottom.setVisibility(View.VISIBLE);
                    isShow = true;
                    break;
                case CURRONTTIME:
                    long currentPosition = videoView.getCurrentPosition();
                    if (currentPosition <= duration) {
                        //更新时间显示
                        currenttime.setText(sec2time(currentPosition));
                        totaltime.setText(sec2time(duration));
                        //更新进度条
                        int progress = (int) ((currentPosition * 1.0 / duration) * 100);
                        seekbar.setProgress((int) currentPosition);
                        mHandler.sendEmptyMessage(CURRONTTIME);
                    }

                    break;
                case SHOWPB:
                    pb_load.setVisibility(View.VISIBLE);
                    break;
                case HIDEPB:
                    pb_load.setVisibility(View.GONE);
                    break;
                case SHOWERROR://显示错误
                    if (IjkVideoView.mCurrentState == IjkVideoView.STATE_ERROR)
                        tv_error.setVisibility(View.VISIBLE);
                    else
                        tv_error.setVisibility(View.INVISIBLE);
                    break;
                case HIDEERROR://隐藏错误
                    if (IjkVideoView.mCurrentState != IjkVideoView.STATE_ERROR)
                        tv_error.setVisibility(View.INVISIBLE);
                    break;


            }
        }
    };

    /**
     * 秒转化为常见格式
     *
     * @param time
     * @return
     */
    private String sec2time(long time) {
        String hms = StringUtils.generateTime(time);
        return hms;
    }

    public PlayView(Activity mActivity) {
        this(mActivity, null);
    }

    public PlayView(Activity mActivity, View root) {
        this.mActivity = mActivity;
        this.mContext = mActivity;
        if (root == null) {

            videoView = (IjkVideoView) mActivity.findViewById(R.id.ijk);
            rl_videoview = (RelativeLayout) mActivity.findViewById(R.id.rl_videoview);
            rl_controll = (RelativeLayout) mActivity.findViewById(R.id.rl_controll);
            ll_bottom = (LinearLayout) mActivity.findViewById(R.id.ll_bottom);
            pasue = (ImageButton) mActivity.findViewById(R.id.mediacontroller_play_pause);
            currenttime = (TextView) mActivity.findViewById(R.id.mediacontroller_time_current);
            seekbar = (SeekBar) mActivity.findViewById(R.id.mediacontroller_seekbar);
            totaltime = (TextView) mActivity.findViewById(R.id.mediacontroller_time_total);
            fill = (ImageButton) mActivity.findViewById(R.id.mediacontroller_fill);
            pb_load = (ProgressBar) mActivity.findViewById(R.id.pb_load);
            tv_error = (TextView) mActivity.findViewById(R.id.tv_error);
        } else {
            videoView = (IjkVideoView) root.findViewById(R.id.ijk);
            rl_videoview = (RelativeLayout) root.findViewById(R.id.rl_videoview);
            rl_controll = (RelativeLayout) root.findViewById(R.id.rl_controll);
            ll_bottom = (LinearLayout) root.findViewById(R.id.ll_bottom);
            pasue = (ImageButton) root.findViewById(R.id.mediacontroller_play_pause);
            currenttime = (TextView) root.findViewById(R.id.mediacontroller_time_current);
            seekbar = (SeekBar) root.findViewById(R.id.mediacontroller_seekbar);
            totaltime = (TextView) root.findViewById(R.id.mediacontroller_time_total);
            fill = (ImageButton) root.findViewById(R.id.mediacontroller_fill);
            pb_load = (ProgressBar) root.findViewById(R.id.pb_load);
            tv_error = (TextView) root.findViewById(R.id.tv_error);
        }

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        } catch (Throwable e) {
            Log.e(TAG, "loadLibraries error", e);
        }


        fill.setOnClickListener(mFillListener);

        pasue.setOnClickListener(pasueListener);

        rl_controll.setOnClickListener(mConytollListener);
        seekbar.setOnSeekBarChangeListener(seekBarChangeListener);

    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                mIntoSeek = false;
                return;
            }

            mIntoSeek = true;
            currenttime.setText(sec2time(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (videoView.isPlaying()) {
                videoView.pause();
                pasue.setImageResource(R.mipmap.pasue);


            }
            mHandler.removeMessages(CURRONTTIME);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessage(CURRONTTIME);
            videoView.seekTo(seekBar.getProgress());
//            pasue.setImageResource(R.mipmap.play);

        }
    };

    /**
     * 设置播放路径
     *
     * @param path
     * @param isplay
     * @return
     */
    public PlayView setPath(String path, boolean isplay) {
        pasue.setClickable(false);
        rl_controll.setClickable(false);
        this.path = path;
        if (TextUtils.isEmpty(path)) {
            mHandler.sendEmptyMessage(HIDECONTEROL);
            tv_error.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(HIDEPB);
            return this;
        }
        Log.e(TAG, "path:" + path);
        mHandler.sendEmptyMessage(SHOWPB);
        pasue.setClickable(false);
        rl_controll.setClickable(false);

        videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        videoView.toggleAspectRatio();

        videoView.setVideoPath(path);

        if (isplay) {
            videoView.start();

            pasue.setImageResource(R.mipmap.play);
            mHandler.sendEmptyMessage(HIDEERROR);
        }
        addVideoListener(videoView);
        return this;
    }


    long lastTime, curTime;
    /**
     * 双击播放和单击显示控制条
     */
    private View.OnClickListener mConytollListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            lastTime = curTime;
            curTime = System.currentTimeMillis();
            if (curTime - lastTime < 300) {//双击事件
                lastTime = Long.valueOf(0);
                curTime = Long.valueOf(0);

                if (videoView.isPlaying()) {
                    videoView.pause();
                    pasue.setImageResource(R.mipmap.pasue);

                } else {
                    videoView.start();
                    pasue.setImageResource(R.mipmap.play);

                }
                mHandler.sendEmptyMessage(HIDEERROR);

            } else {
                if (isShow) {
                    mHandler.sendEmptyMessage(HIDECONTEROL);
                } else {
                    mHandler.sendEmptyMessage(SHOWCONTEROL);
                }
            }
        }
    };

    private View.OnClickListener pasueListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mHandler.sendEmptyMessage(HIDEERROR);
            if (videoView.isPlaying()) {
                videoView.pause();
                pasue.setImageResource(R.mipmap.pasue);
            } else {
                videoView.start();
                pasue.setImageResource(R.mipmap.play);
            }
        }
    };
    /**
     * 全屏切换
     */
    private View.OnClickListener mFillListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
    };

    private void addVideoListener(final IjkVideoView videoView) {
        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {

                duration = iMediaPlayer.getDuration();
                seekbar.setMax((int) duration);
                mHandler.sendEmptyMessage(CURRONTTIME);
                mHandler.sendEmptyMessage(HIDEPB);
                pasue.setClickable(true);
                rl_controll.setClickable(true);

            }
        });
        videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (mHandler.hasMessages(CURRONTTIME))
                    mHandler.removeMessages(CURRONTTIME);
                mHandler.sendEmptyMessage(HIDECONTEROL);
                mHandler.sendEmptyMessage(SHOWERROR);
                mHandler.sendEmptyMessage(HIDEPB);
                return false;
            }
        });
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                Log.e(TAG, i + "  " + i1);
                switch (i) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
//                        if(videoView.isPlaying()){
//                            videoView.pause();
//                        }
                        mHandler.sendEmptyMessage(SHOWPB);
                        pasue.setImageResource(R.mipmap.pasue);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mHandler.sendEmptyMessage(HIDEPB);
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        if (!mHandler.hasMessages(CURRONTTIME)) {
                            mHandler.sendEmptyMessage(CURRONTTIME);
                        }
                        Log.e(TAG, "onInfo");

                        if (!videoView.isPlaying())
                            videoView.start();
                        pasue.setImageResource(R.mipmap.play);
                        break;
                }
                return false;
            }
        });

        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                Log.e(TAG, "视频播放完成");

                seekbar.setProgress(0);
                iMediaPlayer.pause();

                currenttime.setText(sec2time(0));
                pasue.setImageResource(R.mipmap.pasue);
                mHandler.removeMessages(CURRONTTIME);
                mHandler.removeMessages(HIDECONTEROL);
            }
        });
        videoView.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                Log.e(TAG, "缓存的进度" + i + " 当前的进度" + ((int) ((iMediaPlayer.getCurrentPosition() * 1.0 / iMediaPlayer.getDuration()) * 100)) + "总的进度" + iMediaPlayer.getDuration());
                if (mIntoSeek) {
                    mHandler.sendEmptyMessage(SHOWPB);
                }
//                mHandler.removeMessages(CURRONTTIME);
                pasue.setImageResource(R.mipmap.play);


                updateSecondaryProgress(i);
            }
        });

    }

    private void updateSecondaryProgress(int i) {
//        int secondaryProgress = (int) (videoView.getDuration() * i / 100f);
        seekbar.setSecondaryProgress(i);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        int mCurrentOrientation = mActivity.getResources().getConfiguration().orientation;
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏的时候
            ViewUtil.full(false, mActivity);
            ViewGroup.LayoutParams params = rl_videoview.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = DensityUtil.dip2px(mActivity, 200);
            rl_videoview.setLayoutParams(params);
            rl_controll.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//底部虚拟按键
        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏的时候
            ViewUtil.full(true, mActivity);
            ViewGroup.LayoutParams layoutParams = rl_videoview.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            rl_videoview.setLayoutParams(layoutParams);

            rl_controll.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    String currentPath = "";

    public void onPause() {
        if (seekbar.getProgress() != 0)
            currentPosition = videoView.getCurrentPosition();
        else
            currentPosition = 0;
        if (videoView.isPlaying()) {
            videoView.pause();
            pasue.setImageResource(R.mipmap.pasue);
            currentPath = path;
        }
    }

    public void onStart() {
        if (currentPosition != -1) {
            if (!TextUtils.isEmpty(currentPath)) {
                videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
                videoView.setVideoPath(currentPath);
                if (currentPosition != 0)
                    videoView.seekTo(currentPosition);
                if(videoView.isPlaying())
                    pasue.setImageResource(R.mipmap.play);
                else
                    pasue.setImageResource(R.mipmap.pasue);
            }
        }
    }

    public void onDestroy() {
        videoView.stopPlayback();
        IjkMediaPlayer.native_profileEnd();
    }
}
