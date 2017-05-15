package com.example.administrator.myapplication.Tab;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.myapplication.utils.ViewUtil;


/**
 * Created by xjh on 2016/6/30.
 */
public class TabItemView extends LinearLayout {

    private int mIndex;
    private TextView mTextView;
    private Drawable mLeftDrawable;
    private Drawable mTopDrawable;
    private Drawable mRightDrawable;
    private Drawable mBottomDrawable;
    private int leftBounds;
    private int topBounds;
    private int rightBounds;
    private int bottomBounds;
    public TabItemView(Context context) {
        this(context, null);
    }
    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOrientation(0);
        this.setGravity(17);
        this.setPadding(10, 10, 10, 10);
        this.mTextView = new TextView(context);
        this.mTextView.setGravity(17);
        this.mTextView.setLayoutParams(new LayoutParams(-1, -2));
        this.mTextView.setFocusable(true);
        this.mTextView.setPadding(0, 0, 0, 0);
        this.mTextView.setCompoundDrawablePadding(10);
        this.mTextView.setSingleLine();
        this.addView(this.mTextView);
    }

    public void init(int index, String text) {
        this.mIndex = index;
        this.mTextView.setText(text);
    }
    public int getIndex() {
        return this.mIndex;
    }

    public TextView getTextView() {
        return this.mTextView;
    }
    public void setTabTextSize(int tabTextSize) {
        float scaledSize = (float) ViewUtil.scaleTextValue((float)tabTextSize);
       this. mTextView.setTextSize(0, scaledSize);
    }
    public void setTabTextColor(int tabColor) {
        this.mTextView.setTextColor(tabColor);
    }

    public void setTabCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        this.mLeftDrawable = left;
        this.mTopDrawable = top;
        this.mRightDrawable = right;
        this.mBottomDrawable = bottom;
        if(this.mLeftDrawable != null) {
            this.mLeftDrawable.setBounds(this.leftBounds, this.topBounds, this.rightBounds, this.bottomBounds);
        }

        if(this.mTopDrawable != null) {
            this.mTopDrawable.setBounds(this.leftBounds, this.topBounds, this.rightBounds, this.bottomBounds);
        }

        if(this.mRightDrawable != null) {
            this.mRightDrawable.setBounds(this.leftBounds, this.topBounds, this.rightBounds, this.bottomBounds);
        }

        if(this.mBottomDrawable != null) {
            this.mBottomDrawable.setBounds(this.leftBounds, this.topBounds, this.rightBounds, this.bottomBounds);
        }

        this.mTextView.setCompoundDrawables(this.mLeftDrawable, this.mTopDrawable, this.mRightDrawable, this.mBottomDrawable);
    }
    public void setTabCompoundDrawablesBounds(int left, int top, int right, int bottom) {
        this.leftBounds = ViewUtil.scaleValue((float)left);
        this.topBounds = ViewUtil.scaleValue((float)top);
        this.rightBounds = ViewUtil.scaleValue((float)right);
        this.bottomBounds = ViewUtil.scaleValue((float)bottom);
    }

    public void setTabBackgroundResource(int resid) {
        this.setBackgroundResource(resid);
    }

    public void setTabBackgroundDrawable(Drawable d) {
        this.setBackgroundDrawable(d);
    }
}
