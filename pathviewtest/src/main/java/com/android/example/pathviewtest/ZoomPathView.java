package com.android.example.pathviewtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

public class ZoomPathView extends View {

    private static final int RADIUS = 98;
    private static final int SIZE = 200;

    private static final long DELAY_TIME = 250;

    private Rect srcRect;
    private Point dstPoint;


    private Bitmap magnifierBitmap;


    private Bitmap resBitmap;
    private Canvas canvas;
    private Bitmap bg;
    private PopupWindow popup;
    private Magnifier magnifier;

    public ZoomPathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.demo);


        BitmapDrawable magnifierDrawable = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.clcrle);
        magnifierBitmap = magnifierDrawable.getBitmap();
        magnifier = new Magnifier(context);
        popup = new PopupWindow(magnifier, SIZE, SIZE);
        popup.setAnimationStyle(android.R.style.Animation_Toast);

        srcRect = new Rect(0, 0, 2 * RADIUS, 2 * RADIUS);
        dstPoint = new Point(0, 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            srcRect.offsetTo((int)(x/scale)-RADIUS , (int)(y/scale)-RADIUS );//缩放后的x，y除以缩放比例得到原图的所在的x，y
            dstPoint.set(x - RADIUS, y - 3 * RADIUS / 2);
//            if (srcRect.left < 0) {
//                srcRect.offset(-srcRect.left, 0);
//            } else if (srcRect.right > bg.getWidth()) {
//                srcRect.offset(bg.getWidth() - srcRect.right, 0);
//            }
//            if (srcRect.top < 0) {
//                srcRect.offset(0, -srcRect.top);
//            } else if (srcRect.bottom > bg.getHeight()) {
//                srcRect.offset(0, bg.getHeight() - srcRect.bottom);
//            }
            if (y < 0||y>resBitmap.getHeight()) {
                // hide popup if out of bounds
                popup.dismiss();
                invalidate();
                return true;
            }
            if (action == MotionEvent.ACTION_DOWN) {
                removeCallbacks(showZoom);
                postDelayed(showZoom, DELAY_TIME);
            } else if (!popup.isShowing()) {
                showZoom.run();
            }
            popup.update(dstPoint.x, dstPoint.y, -1, -1);
            magnifier.invalidate();
        } else if (action == MotionEvent.ACTION_UP) {
            removeCallbacks(showZoom);
            //drawLayout();
            popup.dismiss();
        }
        invalidate();
        return true;
    }

    private void drawLayout() {

        canvas.drawBitmap(resBitmap, 0, 0, null);
    }

    Runnable showZoom = new Runnable() {
        public void run() {
            popup.showAtLocation(ZoomPathView.this,
                    Gravity.NO_GRAVITY,
                    getLeft() + dstPoint.x,
                    getTop() + dstPoint.y);
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        createBitmap();
        super.onMeasure(MyDevice.sWidth, resBitmap.getHeight());




    }

    float scale;


    private void createBitmap() {


        scale = (float) MyDevice.sWidth / bg.getWidth();

        resBitmap = scaleBitmap(bg, scale);


        canvas = new Canvas(resBitmap);
        drawLayout();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(resBitmap, 0, 0, null);


    }

    class Magnifier extends View {
        private Paint mPaint;
        private Rect rect;
        private Path clip;

        public Magnifier(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(0xff008000);
            mPaint.setStyle(Style.STROKE);
            rect = new Rect(0, 0, RADIUS * 2, RADIUS * 2);
            clip = new Path();
            clip.addCircle(2 + RADIUS, 2 + RADIUS, RADIUS, Direction.CW);
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


    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
//		origin.recycle();
        return newBM;
    }
}
