package com.daomingedu.ijkplayertest.coustomview;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * 继承TextureView，使视频不拉伸
 * Created by qin on 2017/6/1.
 */

public class MyTextureView extends TextureView {
    private int mVideoWidth;
    private int mVideoHeight;
    private int mWidth;
    private int mHeight;
    public static final int CENTER_INSIDE = 0;
    public static final int CENTER_CROPED = 1;
    public static final int FIT_XY = 2;
    private int scaleType;
    private float rotation;

    public MyTextureView(Context context) {
        super(context);
    }

    public MyTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        updateTexture();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void updateTexture() {
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            switch (scaleType) {
                case CENTER_INSIDE:
                    updateTextureViewSizeCenter();
                    break;
                case CENTER_CROPED:
                    updateTextureViewSizeCenterCrop();
                    break;
                case FIT_XY:
                    updateTextureViewSizeFitXY();
                    break;
                default:
                    updateTextureViewSizeCenter();
                    break;
            }
        }
    }

    //重新计算video的显示位置，让其全部显示并居中
    private void updateTextureViewSizeCenter() {
        float sx;
        float sy;
        if (rotation == 90 || rotation == 270) {
            sx = (float) mWidth / (float) mVideoHeight;
            sy = (float) mHeight / (float) mVideoWidth;
        } else {
            sx = (float) mWidth / (float) mVideoWidth;
            sy = (float) mHeight / (float) mVideoHeight;
        }

        Matrix matrix = new Matrix();

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((mWidth - mVideoWidth) / 2, (mHeight - mVideoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) mWidth, mVideoHeight / (float) mHeight);

        //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        if (sx >= sy) {
            matrix.postScale(sy, sy, mWidth / 2, mHeight / 2);
        } else {
            matrix.postScale(sx, sx, mWidth / 2, mHeight / 2);
        }
        matrix.postRotate(rotation,mWidth / 2, mHeight / 2);
        setTransform(matrix);
        postInvalidate();
    }

    private void updateTextureViewSizeCenterCrop() {
        float sx;
        float sy;
        if (rotation == 90 || rotation == 270) {
            sx = (float) mWidth / (float) mVideoHeight;
            sy = (float) mHeight / (float) mVideoWidth;
        } else {
            sx = (float) mWidth / (float) mVideoWidth;
            sy = (float) mHeight / (float) mVideoHeight;
        }

        Matrix matrix = new Matrix();
        float maxScale = Math.max(sx, sy);

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((mWidth - mVideoWidth) / 2, (mHeight - mVideoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) mWidth, mVideoHeight / (float) mHeight);
        //第3步,等比例放大或缩小,直到视频区的一边超过View一边, 另一边与View的另一边相等. 因为超过的部分超出了View的范围,所以是不会显示的,相当于裁剪了.
        matrix.postScale(maxScale, maxScale, mWidth / 2, mHeight / 2);//后两个参数坐标是以整个View的坐标系以参考的
        matrix.postRotate(rotation, mWidth / 2, mHeight / 2);
        setTransform(matrix);
        postInvalidate();
    }

    private void updateTextureViewSizeFitXY() {
        if (rotation == 90 || rotation == 270) {
            float sx = (float) mWidth / (float) mVideoHeight;
            float sy = (float) mHeight / (float) mVideoWidth;
            Matrix matrix = new Matrix();
            //第1步:把视频区移动到View区,使两者中心点重合.
            matrix.preTranslate((mWidth - mVideoWidth) / 2, (mHeight - mVideoHeight) / 2);
            //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
            matrix.preScale(mVideoWidth / (float) mWidth, mVideoHeight / (float) mHeight);
            //第3步：旋转
            matrix.postRotate(rotation, mWidth / 2, mHeight / 2);
            //第4步：缩放为旋转后的大小
            matrix.postScale(sx,sy, mWidth / 2, mHeight / 2);
            setTransform(matrix);
            postInvalidate();
        }else {
            return;
        }

    }

    public void setVideoSize(int mVideoWidth, int mVideoHeight) {
        this.mVideoWidth = mVideoWidth;
        this.mVideoHeight = mVideoHeight;
        updateTexture();
    }

    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }


    public void setVideoRotation(float rotation){
        this.rotation = rotation;
        updateTexture();
    }
}
