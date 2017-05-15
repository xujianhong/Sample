package com.example.administrator.myapplication.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.myapplication.MyApp;
import com.example.administrator.myapplication.MyDevice;


/**
 * Created by Administrator on 2016/6/30.
 */
public class ViewUtil {
    public static final int INVALID = -2147483648;

    public ViewUtil() {
    }

    public static void removeSelfFromParent(View v) {
        ViewParent parent = v.getParent();
        if(parent != null && parent instanceof ViewGroup) {
            ((ViewGroup)parent).removeView(v);
        }

    }

    public static int scaleValue(float value) {
        if(MyDevice.sScaledDensity > (float) MyApp.UI_DENSITY) {
            if(MyDevice.sWidth > MyApp.UI_WIDTH) {
                value *= 1.3F - 1.0F / MyDevice.sScaledDensity;
            } else if(MyDevice.sWidth < MyApp.UI_WIDTH) {
                value *= 1.0F - 1.0F / MyDevice.sScaledDensity;
            }
        }

        return scale(MyDevice.sWidth, MyDevice.sHeight, value);
    }

    public static int scaleTextValue(float value) {
        return scale(MyDevice.sWidth, MyDevice.sHeight, value);
    }

    public static int scale(int displayWidth, int displayHeight, float pxValue) {
        if(pxValue == 0.0F) {
            return 0;
        } else {
            float scale = 1.0F;

            try {
                float e = (float)displayWidth / (float) MyApp.UI_WIDTH;
                float scaleHeight = (float)displayHeight / (float) MyApp.UI_HEIGHT;
                scale = Math.min(e, scaleHeight);
            } catch (Exception var6) {
            }

            return Math.round(pxValue * scale + 0.5F);
        }
    }

//    public static void scaleContentView(ViewGroup contentView) {
//        scaleView(contentView);
//        if(contentView.getChildCount() > 0) {
//            for(int i = 0; i < contentView.getChildCount(); ++i) {
//                View view = contentView.getChildAt(i);
//                if(view instanceof ViewGroup && !(view instanceof PullToRefreshLayout) && !(view instanceof LoadMoreListView) && !(view instanceof PullToRefreshView)) {
//                    scaleContentView((ViewGroup)((ViewGroup)view));
//                } else {
//                    scaleView(contentView.getChildAt(i));
//                }
//            }
//        }
//
//    }

//    public static void scaleContentView(View parent, int id) {
//        ViewGroup contentView = null;
//        View view = parent.findViewById(id);
//        if(view instanceof ViewGroup) {
//            contentView = (ViewGroup)view;
//            scaleContentView(contentView);
//        }
//
//    }
//
//    public static void scaleContentView(Context context, int id) {
//        ViewGroup contentView = null;
//        View view = ((Activity)context).findViewById(id);
//        if(view instanceof ViewGroup) {
//            contentView = (ViewGroup)view;
//            scaleContentView(contentView);
//        }
//
//    }

    @SuppressLint({"NewApi"})
    public static void scaleView(View view) {
        if(view instanceof TextView) {
            TextView params = (TextView)view;
            setTextSize(params, params.getTextSize());
        }

        ViewGroup.LayoutParams params1 = view.getLayoutParams();
        int minWidth;
        int minHeight;
        if(null != params1) {
            minWidth = -2147483648;
            minHeight = -2147483648;
            if(params1.width != -2 && params1.width != -1) {
                minWidth = params1.width;
            }

            if(params1.height != -2 && params1.height != -1) {
                minHeight = params1.height;
            }

            setViewSize(view, minWidth, minHeight);
            setPadding(view, view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }

        if(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams minWidth1 = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
            if(minWidth1 != null) {
                setMargin(view, minWidth1.leftMargin, minWidth1.topMargin, minWidth1.rightMargin, minWidth1.bottomMargin);
            }
        }

        if(Build.VERSION.SDK_INT >= 16) {
            minWidth = scaleValue((float)view.getMinimumWidth());
            minHeight = scaleValue((float)view.getMinimumHeight());
            view.setMinimumWidth(minWidth);
            view.setMinimumHeight(minHeight);
        }

    }

    public static void setSPTextSize(TextView textView, float size) {
        float scaledSize = (float)scaleTextValue(size);
        textView.setTextSize(scaledSize);
    }

    public static void setTextSize(TextView textView, float sizePixels) {
        float scaledSize = (float)scaleTextValue(sizePixels);
        textView.setTextSize(0, scaledSize);
    }

    public static void setTextSize(Context context, TextPaint textPaint, float sizePixels) {
        float scaledSize = (float)scaleTextValue(sizePixels);
        textPaint.setTextSize(scaledSize);
    }

    public static void setTextSize(Context context, Paint paint, float sizePixels) {
        float scaledSize = (float)scaleTextValue(sizePixels);
        paint.setTextSize(scaledSize);
    }

    public static void setViewSize(View view, int widthPixels, int heightPixels) {
        int scaledWidth = scaleValue((float)widthPixels);
        int scaledHeight = scaleValue((float)heightPixels);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(params == null) {
            Log.e("ViewUtil","setViewSize出错,如果是代码new出来的View，需要设置一个适合的LayoutParams");
        } else {
            if(widthPixels != -2147483648) {
                params.width = scaledWidth;
            }

            if(heightPixels != -2147483648 && heightPixels != 1) {
                params.height = scaledHeight;
            }

            view.setLayoutParams(params);
        }
    }

    public static void setPadding(View view, int left, int top, int right, int bottom) {
        int scaledLeft = scaleValue((float)left);
        int scaledTop = scaleValue((float)top);
        int scaledRight = scaleValue((float)right);
        int scaledBottom = scaleValue((float)bottom);
        view.setPadding(scaledLeft, scaledTop, scaledRight, scaledBottom);
    }

    public static void setMargin(View view, int left, int top, int right, int bottom) {
        int scaledLeft = scaleValue((float)left);
        int scaledTop = scaleValue((float)top);
        int scaledRight = scaleValue((float)right);
        int scaledBottom = scaleValue((float)bottom);
        if(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mMarginLayoutParams = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
            if(mMarginLayoutParams != null) {
                if(left != -2147483648) {
                    mMarginLayoutParams.leftMargin = scaledLeft;
                }

                if(right != -2147483648) {
                    mMarginLayoutParams.rightMargin = scaledRight;
                }

                if(top != -2147483648) {
                    mMarginLayoutParams.topMargin = scaledTop;
                }

                if(bottom != -2147483648) {
                    mMarginLayoutParams.bottomMargin = scaledBottom;
                }

                view.setLayoutParams(mMarginLayoutParams);
            }
        }

    }

    public static void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if(p == null) {
            p = new ViewGroup.LayoutParams(-1, -2);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if(lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, 1073741824);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }

        view.measure(childWidthSpec, childHeightSpec);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter != null) {
            int totalHeight = 0;

            for(int params = 0; params < listAdapter.getCount(); ++params) {
                View listItem = listAdapter.getView(params, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams var5 = listView.getLayoutParams();
            var5.height = totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1);
            listView.setLayoutParams(var5);
        }
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
