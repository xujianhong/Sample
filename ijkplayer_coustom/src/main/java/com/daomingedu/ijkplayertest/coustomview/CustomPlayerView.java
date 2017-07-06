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
import android.util.Log;
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

public class CustomPlayerView extends FrameLayout
        implements TextureView.SurfaceTextureListener, CustomPlayer {

    private static final String TAG = "CustomPlayerView";
    MediaPlayer mediaPlayer;

    int mCurrentState = STATE_IDLE;//当前状态


    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    private boolean looping; //是否循环播放


    MyTextureView textureView;

    FrameLayout mContainer;


    private Context mContext;


    private String mUrlData; //数据源
    private Map<String, String> mHeaders; //请求头

    private int mScaleType = MyTextureView.CENTER_CROPED;
    private SurfaceTexture mSurfaceTexture;

    private BaseController controller;
    private int mBufferPercentage;

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

        initController();

    }

    private void initController() {
        if (controller == null) {
            controller = new VideoController(mContext);

        }
        controller.setPlayer(this);
        mContainer.removeView(controller);

        LayoutParams ps =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(controller, ps);

    }

    private void initView() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        FrameLayout.LayoutParams ps =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        this.addView(mContainer, ps);
    }

    public void initPlayer() {
        if (mCurrentState == STATE_IDLE && mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {

            reset();
        }

        mediaPlayer.setLooping(looping);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //keep screen on
        ((AppCompatActivity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mediaPlayer.setOnErrorListener(mOnErrorListener);
        mediaPlayer.setOnInfoListener(mOnInfoListener);
        mediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mCurrentState = STATE_INITIALIZED;

        if (controller != null)
            controller.setPlayerState(mCurrentState);
        initTextureView();

//        perparePlayer(null);

    }

    /**
     * 初始化textureView
     */
    private void initTextureView() {
        if (textureView == null) {
            textureView = new MyTextureView(mContext);
            textureView.setSurfaceTextureListener(this);
        }

        textureView.setScaleType(mScaleType);
        mContainer.removeView(textureView);
        LayoutParams ps =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        mContainer.addView(textureView, 0, ps);

    }

    public void setUrlData(String urlData, Map<String, String> headers) {
        mHeaders = headers;
        mUrlData = urlData;

    }


    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mCurrentState != STATE_PREPARE) return;

            mp.start();

            mp.setVolume(1f, 1f);//设置音量
            if (mp.isPlaying()) {
                mCurrentState = STATE_PLAYING;
            }
            if (controller != null)
                controller.setPlayerState(mCurrentState);
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer MediaPlayer, int i) {
            mBufferPercentage = i;
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer MediaPlayer) {

            mCurrentState = STATE_COMPLETED;
            Log.d(TAG, "onCompletion: media play Completion");
            if (controller != null)
                controller.setPlayerState(mCurrentState);
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            switch (what) {
                /**
                 * 未指定的媒体播放器错误。
                 */
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.e(TAG, "onError-what: MEDIA_ERROR_UNKNOWN");
                    break;
                /**
                 * 媒体服务器死机。 在这种情况下，应用程序必须释放MediaPlayer对象并实例化一个新对象。
                 */
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    Log.e(TAG, "onError-what: MEDIA_ERROR_SERVER_DIED");
                    break;
            }

            switch (extra) {
                /**文件或网络相关的操作错误。*/
                case MediaPlayer.MEDIA_ERROR_IO:
                    Log.e(TAG, "onError-extra: MEDIA_ERROR_IO");
                    break;
                /**位流不符合相关编码标准或文件规格。*/
                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                    Log.e(TAG, "onError-extra: MEDIA_ERROR_MALFORMED");
                    break;
                /**位流符合相关编码标准或文件规格，但媒体框架不支持该功能。*/
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    Log.e(TAG, "onError-extra: MEDIA_ERROR_UNSUPPORTED");
                    break;
                /**一些操作需要太长时间才能完成，通常超过3-5秒。*/
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    Log.e(TAG, "onError-extra: MEDIA_ERROR_TIMED_OUT");
                    break;

            }
            return false;
        }
    };

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int what, int i1) {
            switch (what) {
                /**
                 * 未指定的媒体播放器信息
                 */
                case MediaPlayer.MEDIA_INFO_UNKNOWN:
                    Log.d(TAG, "onInfo: MEDIA_INFO_UNKNOWN");
                    break;
                /**
                 * 视频对于解码器来说太复杂了：它不能足够快地解码帧。 在这个阶段可能只有音频播放正常。
                 */
                case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Log.d(TAG, "onInfo: MEDIA_INFO_VIDEO_TRACK_LAGGING");
                    break;

                /**
                 * 播放器刚推出第一个视频帧进行渲染。
                 */
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.d(TAG, "onInfo: MEDIA_INFO_VIDEO_RENDERING_START");

                    break;
                /**
                 * MediaPlayer暂时暂停内部播放，以缓冲更多的数据。
                 */
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d(TAG, "onInfo: MEDIA_INFO_BUFFERING_START");
                    break;
                /**
                 * 填充缓冲区后MediaPlayer正在恢复播放。
                 */
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d(TAG, "onInfo: MEDIA_INFO_BUFFERING_END");
                    break;
                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Log.d(TAG, "onInfo: MEDIA_INFO_BAD_INTERLEAVING");
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.d(TAG, "onInfo: MEDIA_INFO_NOT_SEEKABLE");
                    break;
                case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    Log.d(TAG, "onInfo: MEDIA_INFO_METADATA_UPDATE");
                    break;
                case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    Log.d(TAG, "onInfo: MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                    break;
                case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    Log.d(TAG, "onInfo: MEDIA_INFO_SUBTITLE_TIMED_OUT");
                    break;


            }

            return false;
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (textureView != null) {
                textureView.setVideoSize(width, height);
            }
            Log.d(TAG, "onVideoSizeChanged: width:" + width + "  height:" + height);
        }
    };


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
        }
        perparePlayer(mSurfaceTexture);
    }

    private void perparePlayer(SurfaceTexture surface) {
        try {
            mediaPlayer.setDataSource(mContext, Uri.parse(mUrlData), mHeaders);
            if (surface != null) {
                mediaPlayer.setSurface(new Surface(surface));
            }
            mediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARE;


        } catch (IOException e) {
            e.printStackTrace();
            mCurrentState = STATE_ERROR;
        }
        if (controller != null)
            controller.setPlayerState(mCurrentState);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//        Log.d(TAG, "surface  onSurfaceTextureSizeChanged: ");

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        Log.d(TAG, "surface   onSurfaceTextureDestroyed: ");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        Log.d(TAG, "surface   onSurfaceTextureUpdated: ");
    }


    @Override
    public void start() {

    }

    @Override
    public int getCurrentState() {
        return mCurrentState;
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARE;
    }

    @Override
    public boolean isPrePared() {
        return mCurrentState == STATE_PREPARE_END;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPause() {
        return mCurrentState == STATE_PAUSE;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public void seekto(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mCurrentState = STATE_END;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
    }

    @Override
    public void reset() {
        mCurrentState = STATE_IDLE;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mBufferPercentage = 0;
        }
        if (controller != null) {
            controller.setPlayerState(mCurrentState);
        }
    }

    @Override
    public long getDuration() {

        return !isPlaying() ? -2 : mediaPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return !isPlaying() ? -2 : mediaPlayer.getCurrentPosition();
    }

    @Override
    public int getBufferPercentage() {
        return !isPlaying() ? -2 : mBufferPercentage;
    }
}
