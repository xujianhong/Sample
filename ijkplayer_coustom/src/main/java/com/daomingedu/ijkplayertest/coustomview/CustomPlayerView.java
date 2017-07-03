package com.daomingedu.ijkplayertest.coustomview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Map;


/**
 * Created by jianhongxu on 2017/7/3.
 */

public class CustomPlayerView extends FrameLayout implements TextureView.SurfaceTextureListener {

    MediaPlayer mediaPlayer;

    int mCurrentState = STATE_IDLE;//当前状态

    private boolean looping; //是否循环播放


    MyTextureView textureView;

    FrameLayout mContainer;

    public static final int STATE_IDLE = 0x01;//闲置状态
    public static final int STATE_INITIALIZED = 0x02;//初始化状态

    public static final int STATE_ERROR = 0x03;//错误状态

    public static final int STATE_PREPARE = 0x04;//准备状态
    private Context mContext;


    private String mUrlData; //数据源
    private Map<String, String> mHeaders; //请求头
    private Surface surface;
    private int mScaleType = MyTextureView.CENTER_CROPED;
    private SurfaceTexture mSufaceTexture;

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
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        FrameLayout.LayoutParams ps =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        this.addView(mContainer,ps);
    }

    public void initPlayer() {
        if (mCurrentState == STATE_IDLE && mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        mediaPlayer.setLooping(looping);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setScreenOnWhilePlaying(true); ineffective
        //keep screen on
        ((AppCompatActivity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mediaPlayer.setOnErrorListener(mOnErrorListener);
        mediaPlayer.setOnInfoListener(mOnInfoListener);
        mediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mCurrentState = STATE_INITIALIZED;

        initTextureView();

//        perparePlayer(null);

    }

    /**
     * 初始化textureView
     */
    private void initTextureView() {
        if(textureView ==null){
            textureView = new MyTextureView(mContext);
        }
        textureView.setSurfaceTextureListener(this);
        textureView.setScaleType(mScaleType);
        mContainer.removeView(textureView);
        LayoutParams ps =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        mContainer.addView(textureView,0,ps);

    }

    public void setUrlData(String urlData, Map<String, String> headers) {
        mHeaders = headers;
        mUrlData = urlData;

    }




    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if(mCurrentState !=STATE_PREPARE)return;

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


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if(mSufaceTexture ==null){
            mSufaceTexture = surface;
        }
        perparePlayer(mSufaceTexture);
    }

    private void perparePlayer(SurfaceTexture surface) {
        try {
            mediaPlayer.setDataSource(mContext,Uri.parse(mUrlData),mHeaders);
            if(surface !=null){
                mediaPlayer.setSurface(new Surface(surface));
            }
            mediaPlayer.prepareAsync();
            mCurrentState =STATE_PREPARE;

        } catch (IOException e) {
            e.printStackTrace();
            mCurrentState =STATE_ERROR;
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


}
