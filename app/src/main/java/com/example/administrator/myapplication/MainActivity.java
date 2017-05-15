package com.example.administrator.myapplication;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.myapplication.utils.ImageUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

     static final int UPLOAD_COMPLETE = 0x02;//压缩
    String path = "/storage/emulated/0/Pictures/1477896265583.jpg";
    ImageView iv, iv_small;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);

        iv_small = (ImageView)findViewById(R.id.iv_small);


        iv.setImageBitmap(ImageUtils.uploadDecodeFile(new File(path)));

        iv_small.setImageBitmap(centerSquareScaleBitmap(ImageUtils.getLoacalBitmap(path),100));
        iv_small.setScaleType(ImageView.ScaleType.FIT_XY);

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what== UPLOAD_COMPLETE){
                Bundle bundle = msg.getData();
//                iv.setImageBitmap(bundle.getb);
            }
        }
    };


    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength)
    {
        if(null == bitmap || edgeLength <= 0)
        {
            return  null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if(widthOrg > edgeLength && heightOrg > edgeLength)
        {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try{
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            }
            catch(Exception e){
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try{
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            }
            catch(Exception e){
                return null;
            }
        }

        return result;
    }
}
