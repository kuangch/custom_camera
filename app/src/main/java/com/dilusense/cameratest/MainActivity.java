package com.dilusense.cameratest;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.badoo.mobile.util.WeakHandler;
import com.bumptech.glide.Glide;
import com.dilusense.cameratest.base.BaseTakePhotoActivity;
import com.dilusense.cameratest.utils.TakePhotoUtils;
import com.dilusense.custom_camera.CustomCameraActivity;
import com.dilusense.custom_camera.utils.CaptureImgUtils;
import com.jph.takephoto.model.TResult;

import java.io.File;

public class MainActivity extends BaseTakePhotoActivity {


    WeakHandler handler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void init() {
        super.init();

        handler = new WeakHandler();
        TakePhotoUtils.getTakePhoto(ctx).onPickFromCustomCapture(CaptureImgUtils.getCaptureImgFileUri(ctx),CustomCameraActivity.class);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setTitle("自定义相机");
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        final TResult res = result;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext()).load(
                        new File(res.getImages().get(0).getCompressPath())
                ).into((ImageView)findViewById(R.id.img));
            }
        });
    }
}
