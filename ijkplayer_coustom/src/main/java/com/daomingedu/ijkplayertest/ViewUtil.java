package com.daomingedu.ijkplayertest;


import android.app.Activity;

import android.view.WindowManager;



/**
 * Created by Administrator on 2016/6/30.
 */
public class ViewUtil {
    public static final int INVALID = -2147483648;

    public ViewUtil() {
    }

    public static  void full(boolean enable,Activity activity) {
        if (enable) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attr);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
