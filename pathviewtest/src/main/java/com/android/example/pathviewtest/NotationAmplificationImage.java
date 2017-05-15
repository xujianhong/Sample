package com.android.example.pathviewtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by jianhongxu on 2016/11/24.
 */

public class NotationAmplificationImage extends RelativeLayout {

    ImageView iv;
    Bitmap bg;


    private static  int RADIUS = 100;
    private static final int SIZE = 400;

    private static final long DELAY_TIME = 0;

    private Rect srcRect;
    private Point dstPoint;


    private Bitmap magnifierBitmap;


    private PopupWindow popup;
    private Magnifier magnifier;

    float scale;


    //是否移动了
    private boolean islong;//判断是否长按 true 长按  false 不是长按

    RelativeLayout rl_annotation;

    OnNotationAmplificationLisenter onNotationAmplificationLisenter;//得到批注坐标监听

    RelativeLayout rl_view;
    Context context;
    public NotationAmplificationImage(Context context) {
        super(context);
        this.context=context;
        RADIUS=DensityUtil.dip2px(context,50f);
        View.inflate(context, R.layout.notationamplificationimage, this);
    }

    public NotationAmplificationImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        RADIUS=DensityUtil.dip2px(context,50f);
        View.inflate(context, R.layout.notationamplificationimage, this);
        iv = (ImageView) findViewById(R.id.iv_music);
        rl_annotation = (RelativeLayout)findViewById(R.id.rl_annotation);
        BitmapDrawable magnifierDrawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.clcrle);
        magnifierBitmap = magnifierDrawable.getBitmap();
        magnifier = new Magnifier(context);
        popup = new PopupWindow(magnifier, DensityUtil.dip2px(context,100f), DensityUtil.dip2px(context,100f));

        popup.setAnimationStyle(android.R.style.Animation_Toast);//puop弹出的动画

        srcRect = new Rect(0, 0, DensityUtil.dip2px(context,50f), DensityUtil.dip2px(context,50f));
        dstPoint = new Point(0, 0);


    }

    public void getScrollView(final ScrollView view){

        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                islong = true;
                iv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                            if (!islong)
                                return false;

                            int popy = y - view.getScrollY();//滑动后的popy位置
                            Log.e("text", view.getScrollY() + "");

                            srcRect.offsetTo((int) (x / scale) - RADIUS, (int) (y / scale) - RADIUS);//缩放后的x，y除以缩放比例得到原图的所在的x，y
                            dstPoint.set(x - RADIUS, popy - 3 * RADIUS / 2);//puop的x，y位置
                            if (y < popy || popy > iv.getHeight()) {
                                // hide popup if out of bounds
                                popup.dismiss();
                                iv.invalidate();
                                return false;
                            }
                            if (action == MotionEvent.ACTION_DOWN) {
                                iv.removeCallbacks(showZoom);
//                                showZoom.run();

                                iv.postDelayed(showZoom, DELAY_TIME);

                            } else if (!popup.isShowing()) {

                                showZoom.run();


                            }
                            popup.update(rl_view.getLeft() + dstPoint.x, rl_view.getTop()+dstPoint.y, -1, -1);
                            magnifier.invalidate();
                        } else if (action == MotionEvent.ACTION_UP) {
                            if (islong){
                                islong = false;
                            }else{
                                return false;
                            }
                            if(popup.isShowing()){
                                onNotationAmplificationLisenter.onNotationAmplificationLisenter(rl_annotation,x,y);
                            }
                            iv.removeCallbacks(showZoom);
                            //drawLayout();
                            popup.dismiss();

                        }
                        iv.invalidate();
                        return false;
                    }
                });
                view.requestDisallowInterceptTouchEvent(true);

                return true;
            }
        });
    }

    public interface OnNotationAmplificationLisenter{
        void onNotationAmplificationLisenter(RelativeLayout layout,int x,int y);
    }

    /**
     * 设置 触摸后的到perantview和x，y坐标
     * @param onNotationAmplificationLisenter
     */
    public void setOnNotationAmplificationLisenter(OnNotationAmplificationLisenter onNotationAmplificationLisenter){
        this.onNotationAmplificationLisenter = onNotationAmplificationLisenter;
    }
    public void setPath() {
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.demo);
        double v = (double) MyDevice.sWidth / bg.getWidth() * bg.getHeight();
        iv.setLayoutParams(new RelativeLayout.LayoutParams(MyDevice.sWidth, (int) v));
        iv.setImageBitmap(bg);
        iv.setScaleType(ImageView.ScaleType.FIT_START);

        scale = (float) MyDevice.sWidth / bg.getWidth();
    }

    /**
     * 设置puop x getleft，y getTop的view
     * @param perant
     */
    public void setPerant(RelativeLayout perant){
        this.rl_view = perant;
    }

    /**
     * 圆形的放大镜
     */
    class Magnifier extends View {
        private Paint mPaint;
        private Rect rect;
        private Path clip;

        public Magnifier(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(0xff008000);
            mPaint.setStyle(Paint.Style.STROKE);
            rect = new Rect(0, 0, RADIUS * 2, RADIUS * 2);
            clip = new Path();
            clip.addCircle(RADIUS, RADIUS, RADIUS, Path.Direction.CW);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            canvas.clipPath(clip);
            // draw popup
            mPaint.setAlpha(255);
            canvas.drawBitmap(bg, srcRect, rect, mPaint);
            canvas.restore();

//            // draw popup frame
            mPaint.setAlpha(220);
            canvas.drawBitmap(magnifierBitmap, 0, 0,null);


        }
    }


    Runnable showZoom = new Runnable() {
        public void run() {
            popup.showAtLocation(iv,
                    Gravity.NO_GRAVITY,
                    rl_view.getLeft() + dstPoint.x,
                    rl_view.getTop()+dstPoint.y);
        }
    };
}
