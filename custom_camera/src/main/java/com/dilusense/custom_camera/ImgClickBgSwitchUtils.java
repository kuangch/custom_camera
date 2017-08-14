package com.dilusense.custom_camera;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.dilusense.custom_camera.utils.BitmapUtils;

/**
 * Created by Thinkpad on 2017/3/17.
 */
public class ImgClickBgSwitchUtils {
    public static void  onClick(final Context ctx,ImageView img, final int normalImgID, final int pressedImgID){
        img.setImageBitmap(BitmapUtils.readBitMap(ctx, normalImgID));
        img.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //重新设置按下时的背景图片
                    ((ImageView)v).setImageBitmap(BitmapUtils.readBitMap(ctx, pressedImgID));
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    //再修改为抬起时的正常图片
                    ((ImageView)v).setImageBitmap(BitmapUtils.readBitMap(ctx, normalImgID));
                }
                return false;
            }
        });
    }
}
