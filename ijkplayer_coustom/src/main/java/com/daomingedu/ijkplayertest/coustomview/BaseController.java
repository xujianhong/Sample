package com.daomingedu.ijkplayertest.coustomview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.io.IOException;

/**
 * Created by jianhongxu on 2017/7/4.
 */

public abstract class BaseController extends FrameLayout{
    public BaseController(@NonNull Context context) {
        super(context);
    }

    public BaseController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void setPlayer(CustomPlayer player);


    public abstract void setPlayerState(int currentState);


    public abstract void setBufferingUpdate(int bufferingUpdate);

}
