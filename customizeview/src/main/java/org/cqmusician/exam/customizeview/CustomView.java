package org.cqmusician.exam.customizeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by jianhongxu on 2017/2/17.
 */

public class CustomView extends View {


    Paint mPaint;

    int mWidth, mHeight;

    Path path;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CustomView(Context context) {
        super(context);

        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    private void init(Context context) {

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

        path =new Path();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth/2,mHeight/2);
        path.lineTo(0,-200);
        path.lineTo(-200,-200);
        path.lineTo(-200,0);
        path.lineTo(0,0);

        RectF rect = new RectF();
        boolean is = path.isRect(rect);

        Log.d("==========", "isRect:"+is+"| left:"+rect.left+"| top:"+rect.top+"| right:"+rect.right+"| bottom:"+rect.bottom);

        Log.d("==========", "onDraw: "+path.isEmpty());

        canvas.drawPath(path,mPaint);

    }
}
