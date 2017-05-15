package com.daomingedu.ijkplayertest;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

public class DensityUtil {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
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