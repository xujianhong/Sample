package com.example.administrator.myapplication.Tab;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.example.administrator.myapplication.MyDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/30.
 */
public class TabBottomView extends LinearLayout {
    private Context context;
    private LinearLayout mTabLayout = null;
    private MyViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private ArrayList<TabItemView> tabItemList = null;
    private ArrayList<Fragment> pagerItemList = null;
    private List<String> tabItemTextList = null;
    private List<Drawable> tabItemDrawableList = null;
    private int mSelectedTabIndex = 0;
    private CommonFragmentPagerAdapter mFragmentPagerAdapter = null;
    private int tabBackgroundResource = -1;
    private int tabTextSize = 30;
    private int tabTextColor = -16777216;
    private int tabSelectColor = -1;
    private int mDrawablesBoundsLeft;
    private int mDrawablesBoundsTop;
    private int mDrawablesBoundsRight;
    private int mDrawablesBoundsBottom;
    private TabSelectChangeListener tabSelectItemListener;
    private OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            TabItemView tabView = (TabItemView)view;
            TabBottomView.this.setCurrentItem(tabView.getIndex());
        }
    };
    private ImageView mTabImg;
    private int tabSlidingHeight = 0;
    private int tabSlidingColor;
    private int startX;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public TabBottomView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.tabSlidingColor = this.tabSelectColor;
        this.startX = 0;
        this.context = context;
        this.setOrientation(1);
        this.setBackgroundColor(Color.rgb(255, 255, 255));
        this.mTabLayout = new LinearLayout(context);
        this.mTabLayout.setOrientation(0);
        this.mTabLayout.setGravity(17);
        this.mViewPager = new MyViewPager(context);
        this.mViewPager.setId(View.generateViewId());
        this.pagerItemList = new ArrayList();
        this.addView(this.mViewPager, new LayoutParams(-1, 0, 1.0F));
        this.mTabImg = new ImageView(context);
        this.mTabImg.setBackgroundColor(this.tabSlidingColor);
        this.addView(this.mTabImg, new LayoutParams(-2, this.tabSlidingHeight));
        this.addView(this.mTabLayout, new LayoutParams(-1, -2));
        this.tabItemList = new ArrayList();
        this.tabItemTextList = new ArrayList();
        this.tabItemDrawableList = new ArrayList();
        if(!(this.context instanceof FragmentActivity)) {
            Log.e("TabBottomView","----构造SlidingTabView的参数context,必须是FragmentActivity的实例。");
        }
        FragmentManager mFragmentManager = ((FragmentActivity)this.context).getSupportFragmentManager();
        this.mFragmentPagerAdapter = new CommonFragmentPagerAdapter(mFragmentManager, this.pagerItemList);
        this.mViewPager.setAdapter(this.mFragmentPagerAdapter);
        this.mViewPager.setOnPageChangeListener(new TabBottomView.MyOnPageChangeListener());
        this.mViewPager.setOffscreenPageLimit(3);
    }

    public void setCurrentItem(int index) {
        if(this.mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        } else {
            this.mSelectedTabIndex = index;
            int tabCount = this.mTabLayout.getChildCount();
            int itemWidth;
            for(itemWidth = 0; itemWidth < tabCount; ++itemWidth) {
                TabItemView mParams = (TabItemView)this.mTabLayout.getChildAt(itemWidth);
                boolean toX = itemWidth == index;
                mParams.setSelected(toX);
                if(toX) {
                    if(this.tabBackgroundResource != -1) {
                        mParams.setTabBackgroundResource(this.tabBackgroundResource);
                    }

                    if(this.tabItemDrawableList.size() >= tabCount * 2) {
                        mParams.setTabCompoundDrawables(null, this.tabItemDrawableList.get(index * 2 + 1), null, null);
                    } else if(this.tabItemDrawableList.size() >= tabCount) {
                        mParams.setTabCompoundDrawables(null, this.tabItemDrawableList.get(index), null, null);
                    }

                    mParams.setTabTextColor(this.tabSelectColor);
                    this.mViewPager.setCurrentItem(index);
                } else {
                    if(this.tabBackgroundResource != -1) {
                        mParams.setTabBackgroundDrawable(null);
                    }

                    if(this.tabItemDrawableList.size() >= tabCount * 2) {
                        mParams.setTabCompoundDrawables(null, this.tabItemDrawableList.get(itemWidth * 2), null, null);
                    }

                    mParams.setTabTextColor(this.tabTextColor);
                }
            }
            itemWidth = MyDevice.sWidth / this.tabItemList.size();
            LayoutParams var6 = new LayoutParams(itemWidth, this.tabSlidingHeight);
            var6.topMargin = -this.tabSlidingHeight;
            this.mTabImg.setLayoutParams(var6);
            int var7 = itemWidth * index;
            this.imageSlide(this.mTabImg, this.startX, var7, 0, 0);
            this.startX = var7;
            this.mSelectedTabIndex = index;
        }
    }
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mListener = listener;
    }
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    public void setTabBackgroundResource(int resid) {
        this.tabBackgroundResource = resid;
    }
    public void setTabSelectBackgroundResource(int resid) {
        this.mTabLayout.setBackgroundResource(resid);
    }
    public int getTabTextSize() {
        return this.tabTextSize;
    }
    public void setTabTextSize(int tabTextSize) {
        this.tabTextSize = tabTextSize;
    }

    public void setTabTextColor(int tabColor) {
        this.tabTextColor = tabColor;
    }

    public void setTabSelectColor(int tabColor) {
        this.tabSelectColor = tabColor;
    }

    private void addTab(String text, int index) {
        this.addTab(text, index, null);
    }

    private void addTab(String text, int index, Drawable top) {
        TabItemView tabView = new TabItemView(this.context);
        if(top != null) {
            tabView.setTabCompoundDrawables(null, top, null, null);
            tabView.setTabCompoundDrawablesBounds(this.mDrawablesBoundsLeft, this.mDrawablesBoundsTop, this.mDrawablesBoundsRight, this.mDrawablesBoundsBottom);
        }

        tabView.setTabTextColor(this.tabTextColor);
        tabView.setTabTextSize(this.tabTextSize);
        tabView.init(index, text);
        this.tabItemList.add(tabView);
        tabView.setOnClickListener(this.mTabClickListener);
        this.mTabLayout.addView(tabView, new LayoutParams(0, -2, 1.0F));
    }
    public void updateText(int index, String text) {
        this.tabItemTextList.set(index, text);
        this.notifyTabDataSetChanged();
    }
    public void setTabCompoundDrawablesBounds(int left, int top, int right, int bottom) {
        this.mDrawablesBoundsLeft = left;
        this.mDrawablesBoundsTop = top;
        this.mDrawablesBoundsRight = right;
        this.mDrawablesBoundsBottom = bottom;
    }

    public void notifyTabDataSetChanged() {
        this.mTabLayout.removeAllViews();
        this.tabItemList.clear();
        int count = this.mFragmentPagerAdapter.getCount();

        for(int i = 0; i < count; ++i) {
            if(this.tabItemDrawableList.size() >= count * 2) {
                this.addTab(this.tabItemTextList.get(i), i, this.tabItemDrawableList.get(i * 2));
            } else if(this.tabItemDrawableList.size() >= count) {
                this.addTab(this.tabItemTextList.get(i), i, this.tabItemDrawableList.get(i));
            } else {
                this.addTab(this.tabItemTextList.get(i), i);
            }
        }

        if(this.mSelectedTabIndex > count) {
            this.mSelectedTabIndex = count - 1;
        }

        this.setCurrentItem(this.mSelectedTabIndex);
        this.requestLayout();
    }

    public void addItemViews(List<String> tabTexts, List<Fragment> fragments) {
        this.tabItemTextList.addAll(tabTexts);
        this.pagerItemList.addAll(fragments);
        this.mFragmentPagerAdapter.notifyDataSetChanged();
        this.notifyTabDataSetChanged();
    }

    public void addItemViews(List<String> tabTexts, List<Fragment> fragments, List<Drawable> drawables) {
        this.tabItemTextList.addAll(tabTexts);
        this.pagerItemList.addAll(fragments);
        this.tabItemDrawableList.addAll(drawables);
        this.mFragmentPagerAdapter.notifyDataSetChanged();
        this.notifyTabDataSetChanged();
    }

    public void addItemView(String tabText, Fragment fragment) {
        this.tabItemTextList.add(tabText);
        this.pagerItemList.add(fragment);
        this.mFragmentPagerAdapter.notifyDataSetChanged();
        this.notifyTabDataSetChanged();
    }

    public void addItemView(String tabText, Fragment fragment, Drawable drawableNormal, Drawable drawablePressed) {
        this.tabItemTextList.add(tabText);
        this.pagerItemList.add(fragment);
        this.tabItemDrawableList.add(drawableNormal);
        this.tabItemDrawableList.add(drawablePressed);
        this.mFragmentPagerAdapter.notifyDataSetChanged();
        this.notifyTabDataSetChanged();
    }

    public void removeItemView(int index) {
        this.mTabLayout.removeViewAt(index);
        this.pagerItemList.remove(index);
        this.tabItemList.remove(index);
        this.tabItemDrawableList.remove(index);
        this.mFragmentPagerAdapter.notifyDataSetChanged();
        this.notifyTabDataSetChanged();
    }

    public void removeAllItemViews() {
        this.mTabLayout.removeAllViews();
        this.pagerItemList.clear();
        this.tabItemList.clear();
        this.tabItemDrawableList.clear();
        this.mFragmentPagerAdapter.notifyDataSetChanged();
        this.notifyTabDataSetChanged();
    }

    public MyViewPager getViewPager() {
        return this.mViewPager;
    }

    public void setTabPadding(int left, int top, int right, int bottom) {
        for(int i = 0; i < this.tabItemList.size(); ++i) {
            TabItemView tabView = this.tabItemList.get(i);
            tabView.setPadding(left, top, right, bottom);
        }

    }

    public void setSlidingEnabled(boolean sliding) {
        this.mViewPager.setSlidingEnabled(sliding);
    }

    public int getTabSlidingColor() {
        return this.tabSlidingColor;
    }

    public void setTabSlidingColor(int tabSlidingColor) {
        this.tabSlidingColor = tabSlidingColor;
        this.mTabImg.setBackgroundColor(tabSlidingColor);
    }

    public void imageSlide(View v, int startX, int toX, int startY, int toY) {
        TranslateAnimation anim = new TranslateAnimation((float)startX, (float)toX, (float)startY, (float)toY);
        anim.setDuration(100L);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }

    public void setTabSelectChangeListener(TabSelectChangeListener listener) {
        this.tabSelectItemListener = listener;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        public MyOnPageChangeListener() {
        }

        public void onPageScrollStateChanged(int arg0) {
            if(TabBottomView.this.mListener != null) {
                TabBottomView.this.mListener.onPageScrollStateChanged(arg0);
            }

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if(TabBottomView.this.mListener != null) {
                TabBottomView.this.mListener.onPageScrolled(arg0, arg1, arg2);
            }

        }

        public void onPageSelected(int arg0) {
            TabBottomView.this.setCurrentItem(arg0);
            if(TabBottomView.this.mListener != null) {
                TabBottomView.this.mListener.onPageSelected(arg0);
            }

            if(TabBottomView.this.tabSelectItemListener != null) {
                TabBottomView.this.tabSelectItemListener.onSelectItem(arg0);
            }

        }
    }

}
