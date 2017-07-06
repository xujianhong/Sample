package com.daomingedu.ijkplayertest.coustomview;

/**
 * Created by jianhongxu on 2017/7/4.
 */

public interface CustomPlayer {

    int STATE_IDLE = 0x01;//闲置状态
    int STATE_INITIALIZED = 0x02;//初始化状态

    int STATE_ERROR = 0x03;//错误状态

    int STATE_PREPARE = 0x04;//准备状态
    int STATE_PREPARE_END = 0x05;//准备结束结束状态


    int STATE_COMPLETED = 0x06;//播放完成
    int STATE_PLAYING = 0x07;//正在播放
    int STATE_PAUSE = 0x08;//暂停播放

    int STATE_END = 0x09;//停止状态


    int STATE_MEDIA_DATA_ERROR = -2;//媒体播放器无效时 调用getDuration getCurrentPosition getBufferPercentage 返回的数值

    void start();

    int getCurrentState();

    boolean isIdle();

    boolean isPreparing();

    boolean isPrePared();

    boolean isPlaying();

    boolean isPause();

    boolean isCompleted();

    void seekto(int position);

    void release();

    void reset();

    long getDuration();
    long getCurrentPosition();
    int getBufferPercentage();



}
