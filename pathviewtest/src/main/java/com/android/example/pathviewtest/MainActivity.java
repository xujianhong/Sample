package com.android.example.pathviewtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    Bitmap bg;


    private static final int RADIUS = 98;
    private static final int SIZE = 200;

    private static final long DELAY_TIME = 0;

    private Rect srcRect;
    private Point dstPoint;


    private Bitmap magnifierBitmap;


    private PopupWindow popup;
    private Magnifier magnifier;

    float scale;

    ScrollView sro;


    //是否移动了
    private boolean islong;//判断是否长按 true 长按  false 不是长按

    RelativeLayout rl_annotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        rl_annotation = (RelativeLayout) findViewById(R.id.rl_annotation);
        sro = (ScrollView) findViewById(R.id.sro);
        iv = (ImageView) findViewById(R.id.iv_music);
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.demo);
        double v = (double) MyDevice.sWidth / bg.getWidth() * bg.getHeight();
        iv.setLayoutParams(new RelativeLayout.LayoutParams(MyDevice.sWidth, (int) v));
        iv.setImageBitmap(bg);
        iv.setScaleType(ImageView.ScaleType.FIT_START);

        scale = (float) MyDevice.sWidth / bg.getWidth();


        BitmapDrawable magnifierDrawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.clcrle);
        magnifierBitmap = magnifierDrawable.getBitmap();
        magnifier = new Magnifier(this);
        popup = new PopupWindow(magnifier, SIZE, SIZE);
//        popup.setAnimationStyle(android.R.style.Animation_Toast);

        srcRect = new Rect(0, 0, 2 * RADIUS, 2 * RADIUS);
        dstPoint = new Point(0, 0);


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

                            int popy = y - sro.getScrollY();//滑动后的popy位置
                            Log.e("text", sro.getScrollY() + "");

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
                            popup.update(rl_annotation.getLeft() + dstPoint.x, rl_annotation.getTop() + dstPoint.y, -1, -1);
                            magnifier.invalidate();
                        } else if (action == MotionEvent.ACTION_UP) {
                            if (islong){
                                islong = false;
                            }else{
                                return false;
                            }
                            if(popup.isShowing()){
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DensityUtil.dip2px(MainActivity.this, 35),
                                        DensityUtil.dip2px(MainActivity.this, 30));
                                params.setMargins(x - 25, y - 30, 0, 0);
                                ImageButton imageButton = new ImageButton(MainActivity.this);
                                imageButton.setLayoutParams(params);
                                imageButton.setBackgroundResource(R.mipmap.biaoji);
                                rl_annotation.addView(imageButton);
                            }
                            iv.removeCallbacks(showZoom);
                            //drawLayout();
                            popup.dismiss();

                        }
                        iv.invalidate();
                        return false;
                    }
                });
                sro.requestDisallowInterceptTouchEvent(true);

                return true;
            }
        });


    }


    Runnable showZoom = new Runnable() {
        public void run() {
            popup.showAtLocation(iv,
                    Gravity.NO_GRAVITY,
                    rl_annotation.getLeft() + dstPoint.x,
                    rl_annotation.getTop() + dstPoint.y);
        }
    };


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
            clip.addCircle(2 + RADIUS, 2 + RADIUS, RADIUS, Path.Direction.CW);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            canvas.clipPath(clip);
            // draw popup
            mPaint.setAlpha(255);
            canvas.drawBitmap(bg, srcRect, rect, mPaint);
            canvas.restore();
            // draw popup frame
            mPaint.setAlpha(220);
            canvas.drawBitmap(magnifierBitmap, 0, 0, mPaint);

        }
    }


}


