package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.example.administrator.myapplication.Tab.CommonFragmentPagerAdapter;
import com.example.administrator.myapplication.Tab.MyViewPager;
import com.example.administrator.myapplication.fragment.MainFragment;
import java.util.ArrayList;


/**
 * Created by jianhongxu on 2016/12/1.
 */

public class BottomAct extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    MyViewPager mp_main;

    RadioButton rb_menu_main, rb_menu_me;
    ImageButton ib_classmangement;
    CommonFragmentPagerAdapter pagerAdapter;

    Button btn_check,btn_class,btn_homework;
    RelativeLayout rl_menu;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bottom);

        initViews();

    }

    private void initViews() {
        btn_check = (Button)findViewById(R.id.btn_check);
        btn_check.setOnClickListener(this);
        btn_class = (Button)findViewById(R.id.btn_class);
        btn_class.setOnClickListener(this);
        btn_homework = (Button)findViewById(R.id.btn_homework);
        btn_homework.setOnClickListener(this);
        rl_menu =(RelativeLayout)findViewById(R.id.rl_menu);

        mp_main = (MyViewPager)findViewById(R.id.mp_main);
        rb_menu_main  =(RadioButton)findViewById(R.id.rb_menu_main);
        rb_menu_me  =(RadioButton)findViewById(R.id.rb_menu_me);
        ib_classmangement = (ImageButton)findViewById(R.id.ib_classmangement);


        closeMenu();
        ib_classmangement.setOnClickListener(this);

        rb_menu_main.setChecked(true);

        rb_menu_main.setOnClickListener(this);
        rb_menu_me.setOnClickListener(this);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainFragment());
        fragments.add(new MainFragment());
        FragmentManager mFragmentManager = getSupportFragmentManager();
        pagerAdapter = new CommonFragmentPagerAdapter(mFragmentManager,fragments);
        mp_main.setAdapter(pagerAdapter);
        mp_main.setOffscreenPageLimit(2);
        mp_main.addOnPageChangeListener(this);


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (mp_main.getCurrentItem()) {
                case 0:
                    if(!rb_menu_main.isChecked()){
                        rb_menu_main.setChecked(true);

                    }
                    rb_menu_me.setChecked(false);
                    break;
                case 1:
                    if(!rb_menu_me.isChecked()){
                        rb_menu_me.setChecked(true);

                    }
                    rb_menu_main.setChecked(false);

                    break;


               default:
                    break;
            }
        }
        else if(state == 1){
            if(isshow)
                closeMenu();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rb_menu_main:
                if(!rb_menu_main.isChecked()){
                    rb_menu_main.setChecked(true);

                }
                rb_menu_me.setChecked(false);

                mp_main.setCurrentItem(0);
                if(isshow)
                    closeMenu();
                break;
            case R.id.rb_menu_me:
                if(!rb_menu_me.isChecked()){
                    rb_menu_me.setChecked(true);

                }
                rb_menu_main.setChecked(false);
                mp_main.setCurrentItem(1);
                if(isshow)
                    closeMenu();
                break;
            case R.id.ib_classmangement:
                if(isshow){
                    closeMenu();
                }else
                    openMenu();
                break;
            case R.id.btn_check:
                Log.e("BottomAct","btn_check");
                break;
            case R.id.btn_class:
                Log.e("BottomAct","btn_class");
                break;
            case R.id.btn_homework:
                Log.e("BottomAct","btn_homework");
                break;
        }
    }



    boolean isshow = false;

    /**
     * 缩放
     */
    public void closeMenu() {
        isshow = false;
        rl_menu.animate().scaleX(0.1f).scaleY(0.1f).translationY(dip2px(40)).setDuration(300).start();


    }

    /**
     * 放大
     */
    public void openMenu() {
        isshow = true;
        rl_menu.animate().scaleX(1f).scaleY(1f).translationY(0).setDuration(300).start();
    }

    public int dip2px(float dipValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
