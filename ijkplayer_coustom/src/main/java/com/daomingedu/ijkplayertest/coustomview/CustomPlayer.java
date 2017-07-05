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


    void start();

    int getCurrentState();

    boolean isIdle();

    boolean isPreparing();

    boolean isPrePared();

    void seekto(int position);


    long getDuration();
    long getCurrentPosition();
    int getBufferPercentage();


}
