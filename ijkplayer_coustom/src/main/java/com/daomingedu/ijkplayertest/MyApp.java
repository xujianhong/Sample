package com.daomingedu.ijkplayertest;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/11/11.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        displayMetrics(this);

    }

    /**
     * 获取手机屏幕的宽高
     * @param ctx
     */
    public static void displayMetrics(Context ctx) {
        WindowManager manager = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        MyDevice.sDensity = dm.density;
        MyDevice.sScaledDensity = dm.scaledDensity;
        MyDevice.sWidth = w;
        MyDevice.sHeight = h;
        if(w > h) {
            MyDevice.sWidth = h;
            MyDevice.sHeight = w;
        }
    }
}
