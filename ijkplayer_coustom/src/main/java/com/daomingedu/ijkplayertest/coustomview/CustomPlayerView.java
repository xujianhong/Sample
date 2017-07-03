package com.daomingedu.ijkplayertest.coustomview;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Map;


/**
 * Created by jianhongxu on 2017/7/3.
 */

public class CustomPlayerView extends FrameLayout {

    MediaPlayer mediaPlayer;

    int mCurrentState = STATE_IDLE;//当前状态

    private boolean looping; //是否循环播放


    MyTextureView textureView;

    FrameLayout mContent;

    public static final int STATE_IDLE = 0x01;//闲置状态
    public static final int STATE_INITIALIZED = 0x02;//初始化状态

    public static final int STATE_ERROR = 0x03;//错误状态

    public static final int STATE_PREPARE = 0x04;//准备状态
    private Context mContext;


    private String mUrlData; //数据源
    private Map<String, String> mHeaders; //请求头
    private Surface surface;

    public CustomPlayerView(@NonNull Context context) {
        this(context, null);

    }

    public CustomPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        initView();

    }

    private void initView() {
        mContent = new FrameLayout(mContext);
        mContent.setBackgroundColor(Color.BLACK);
        FrameLayout.LayoutParams ps =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        this.addView(mContent,ps);
    }


    public void initPlayer() {
        if (mCurrentState == STATE_IDLE && mediaPlayer == null) {

            mediaPlayer = new MediaPlayer();


            mediaPlayer.setLooping(looping);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);

            mediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
            mediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mediaPlayer.setOnErrorListener(mOnErrorListener);
            mediaPlayer.setOnInfoListener(mOnInfoListener);
            mediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        } else {

            mediaPlayer.reset();

            mediaPlayer.setLooping(looping);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);

            mediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
            mediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mediaPlayer.setOnErrorListener(mOnErrorListener);
            mediaPlayer.setOnInfoListener(mOnInfoListener);
            mediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        }

        mCurrentState = STATE_INITIALIZED;

        initiailPlayer();

    }

    public void setUrlData(String urlData, Map<String, String> headers) {
        mHeaders = headers;
        mUrlData = urlData;

    }

    private void initiailPlayer() {
        if (mediaPlayer == null) return;
        try {
            mediaPlayer.setDataSource(mContext, Uri.parse(mUrlData), mHeaders);

            if (surface != null) {
                mediaPlayer.setSurface(surface);
            }
            mediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARE;

        } catch (IOException e) {
            e.printStackTrace();
            mCurrentState = STATE_ERROR;
        }
    }


    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer MediaPlayer) {

        }
    };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer MediaPlayer, int i) {

        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer MediaPlayer) {

        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer MediaPlayer, int i, int i1) {
            return false;
        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer MediaPlayer, int i, int i1) {
            return false;
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        }
    };


}
