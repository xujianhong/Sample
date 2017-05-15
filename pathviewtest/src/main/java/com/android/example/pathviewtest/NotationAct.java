package com.android.example.pathviewtest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by jianhongxu on 2016/11/24.
 */

public class NotationAct  extends AppCompatActivity{

    NotationAmplificationImage image;
    ScrollView sro;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_notation);

        sro = (ScrollView)findViewById(R.id.sro);
        image = (NotationAmplificationImage)findViewById(R.id.image);

        image.setPath();
        image.setPerant(image);
        image.getScrollView(sro);
        image.setOnNotationAmplificationLisenter(new NotationAmplificationImage.OnNotationAmplificationLisenter() {
            @Override
            public void onNotationAmplificationLisenter(RelativeLayout layout, int x, int y) {
                if(relativeLayout ==null){
                    relativeLayout = layout;
                }
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DensityUtil.dip2px(NotationAct.this, 35),
                        DensityUtil.dip2px(NotationAct.this, 30));
                params.setMargins(x - 25, y - 30, 0, 0);
                ImageButton imageButton = new ImageButton(NotationAct.this);
                imageButton.setLayoutParams(params);
                imageButton.setBackgroundResource(R.mipmap.biaoji);
                relativeLayout.addView(imageButton);
            }

        });

    }
}
