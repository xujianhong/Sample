package com.daomingedu.ijkplayertest.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.MediaController;

import java.util.ArrayList;

/**
 * Created by xjh on 2016/11/2.
 */

public class CostomMediaController extends MediaController implements IMediaController {
    public CostomMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CostomMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public CostomMediaController(Context context) {
        super(context);
    }

    private ArrayList<View> mShowOnceArray = new ArrayList<View>();
    @Override
    public void showOnce(View view) {
        mShowOnceArray.add(view);
        view.setVisibility(View.VISIBLE);
        show();
    }
}
