package com.example.administrator.myapplication;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import com.example.administrator.myapplication.Tab.TabBottomView;
import com.example.administrator.myapplication.fragment.MainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjh on 2016/6/30.
 */
public class TabAct extends AppCompatActivity {
    private TabBottomView tbv_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tab);

        initView();
    }

    private void initView() {

        tbv_main = (TabBottomView) findViewById(R.id.tbv_main);
        tbv_main.getViewPager().setOffscreenPageLimit(2);// 缓存数量
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainFragment());
        fragments.add(new MainFragment());


        List<String> tabTexts = new ArrayList<>();
        tabTexts.add("首页");
        tabTexts.add("我的");

        tbv_main.setTabTextSize(32);// 设置底部文字大小
//        tbv_main.setTabTextColor(R.color.gray_8e); // 设置底部普通时候文字颜色
//        tbv_main.setTabSelectColor(Color.rgb(248,252,255)); // 设置底部选中时候文字颜色
//        tbv_main.setTabBackgroundResource(R.mipmap.tab_bg_select); // 设置底部普通时候tab的背景图片
//        tbv_main.setTabSelectBackgroundResource(R.mipmap.tab_bg); // 设置底部选中时候tab的背景图片

        ArrayList<Drawable> tabDrawables = new ArrayList<>();
        tabDrawables.add(getResources().getDrawable(R.mipmap.ic_launcher));
        tabDrawables.add(this.getResources().getDrawable(R.mipmap.ic_launcher));
        tabDrawables.add(this.getResources().getDrawable(R.mipmap.ic_launcher));
        tabDrawables.add(this.getResources().getDrawable(R.mipmap.ic_launcher));

        tbv_main.setTabCompoundDrawablesBounds(0, 0, 70, 70); // 设置图片的大小和底部的高度，在addItemViews之前设置.
        tbv_main.addItemViews(tabTexts, fragments, tabDrawables); // 增加一组
//        tbv_main.setTabPadding(0, 10, 0, 10); // 设置padding


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//为子fragment能获取到onActivityResult；
    }



}
